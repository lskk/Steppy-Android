/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lskk.shesop.steppy.algorithms;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.lskk.shesop.steppy.DashboardFragment;
import org.lskk.shesop.steppy.MainActivity;
import org.lskk.shesop.steppy.receiver.GameAlarmReceiver;
import org.lskk.shesop.steppy.receiver.SendAlarmReceiver;
import org.lskk.shesop.steppy.utils.Record;
import org.lskk.shesop.steppy.utils.Tools;
import org.lskk.shesop.steppy.utils.Utils;
import org.lskk.teamshesop.steppy.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;



/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application.  The {@link StepServiceController}
 * and {@link StepServiceBinding} classes show how to interact with the
 * service.
 *
 * <p>Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */
public class StepService extends Service {
	private static final String TAG = "StepService";
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private SharedPreferences mState;
    private SharedPreferences.Editor mStateEditor;
    private Utils mUtils;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private StepDetector mStepDetector;
    // private StepBuzzer mStepBuzzer; // used for debugging
    private StepDisplayer mStepDisplayer;
    private WeeklyDisplayer mWeeklyDisplayer;
    private PaceNotifier mPaceNotifier;
    private DistanceNotifier mDistanceNotifier;
    private SpeedNotifier mSpeedNotifier;
    private CaloriesNotifier mCaloriesNotifier;
    private SpeakingTimer mSpeakingTimer;
    private SensorDisplayer mSensorDisplayer;
    
    private PowerManager.WakeLock wakeLock;
    private NotificationManager mNM;

    private int mSteps;
    private int mLastStep;
    private float mLastDistance;
    private float mLastCalories = 0;
    private int mSetCal;
    private int mCalculateStep;
    private float mCalculateDistance;
    private float mCalculateCalories;
//    private int mMinuteStep;
    private int mWeeklyStep;
    private int mPace;
    private float mDistance;
    private float mSpeed;
    private float mCalories;
    
    private int stepBeforeDestroy = 0;
    private float caloriesBeforeDestroy = 0.0f;
    private float distanceBeforeDestroy = 0.0f;
    private String startTime;
    
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class StepBinder extends Binder {
        public StepService getService() {
            return StepService.this;
        }
    }
    
    @Override
    public void onCreate() {
        Log.i(TAG, "[SERVICE] onCreate");
        super.onCreate();
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
        
        SendAlarmReceiver sAlarm = new SendAlarmReceiver();
        Tools.updateCountingState(getApplicationContext(), "1");
        sAlarm.setAlarm(this);
        
        GameAlarmReceiver gAlarm = new GameAlarmReceiver();
        gAlarm.setAlarm(this);
        // set starttime
        SimpleDateFormat start = new SimpleDateFormat("HH:mm");
        startTime = start.format(new Date());
        Tools.updateStartTimeCalculate(getApplicationContext(), startTime);
        // Load settings
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);
        mState = getSharedPreferences("state", 0);

        mUtils = Utils.getInstance();
        mUtils.setService(this);
        mUtils.initTTS();

        acquireWakeLock();
        
        // Start detecting
        mStepDetector = new StepDetector();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        registerDetector();

        // Register our receiver for the ACTION_SCREEN_OFF action. This will make our receiver
        // code be called whenever the phone enters standby mode.
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);

        mStepDisplayer = new StepDisplayer(mPedometerSettings, mUtils);
        mStepDisplayer.setSteps(mSteps = Tools.getTodayStep(this));//mState.getInt("steps", 0));
        mStepDisplayer.addListener(mStepListener);
        mStepDetector.addStepListener(mStepDisplayer);
        
        mSensorDisplayer = new SensorDisplayer();
        mSensorDisplayer.addListener(mSensorListener);
        mStepDetector.addSensorListener(mSensorDisplayer);
        
        mWeeklyDisplayer = new WeeklyDisplayer(mPedometerSettings, mUtils);
        mWeeklyDisplayer.setSteps(mWeeklyStep = Tools.getLastWeekStep(this));
        mWeeklyDisplayer.addListener(mWeeklyListener);
        mStepDetector.addStepListener(mWeeklyDisplayer);

        mPaceNotifier     = new PaceNotifier(mPedometerSettings, mUtils);
        mPaceNotifier.setPace(mPace = mState.getInt("pace", 0));
        mPaceNotifier.addListener(mPaceListener); 
        mStepDetector.addStepListener(mPaceNotifier);

        mDistanceNotifier = new DistanceNotifier(mDistanceListener, mPedometerSettings, mUtils);
        mDistanceNotifier.setDistance(mDistance = Tools.getTodayDistance(this));
        mStepDetector.addStepListener(mDistanceNotifier);
        
        mSpeedNotifier    = new SpeedNotifier(mSpeedListener,    mPedometerSettings, mUtils);
        mSpeedNotifier.setSpeed(mSpeed = mState.getFloat("speed", 0));
        mPaceNotifier.addListener(mSpeedNotifier);
        
        mCaloriesNotifier = new CaloriesNotifier(mCaloriesListener, mPedometerSettings, mUtils);
        mCaloriesNotifier.setCalories(mCalories = Tools.getTodayCalorie(this));
        mStepDetector.addStepListener(mCaloriesNotifier);
        
//        mMinuteStep = 0;
        mLastStep = mSteps;
        mLastDistance = mDistance;
        mLastCalories = mCalories;
        
        mSpeakingTimer = new SpeakingTimer(mPedometerSettings, mUtils);
        mSpeakingTimer.addListener(mStepDisplayer);
        mSpeakingTimer.addListener(mWeeklyDisplayer);
        mSpeakingTimer.addListener(mPaceNotifier);
        mSpeakingTimer.addListener(mDistanceNotifier);
        mSpeakingTimer.addListener(mSpeedNotifier);
        mSpeakingTimer.addListener(mCaloriesNotifier);
        mStepDetector.addStepListener(mSpeakingTimer);
        
        // Used when debugging:
        // mStepBuzzer = new StepBuzzer(this);
        // mStepDetector.addStepListener(mStepBuzzer);

        // Start voice
        reloadSettings();
        
        // Start service for inserting measurement to DB
        // every minutes
        handlerInsertDB.postDelayed(taskInsertDB, 60000);
        // Tell the user we started.
        Toast.makeText(this, getText(R.string.started), Toast.LENGTH_SHORT).show();
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "[SERVICE] onStart");
        mLastStep = mSteps;
        mLastCalories = mCalories;
        mLastDistance = mDistance;
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "[SERVICE] onDestroy");
        mUtils.shutdownTTS();
        handlerInsertDB.removeCallbacks(taskInsertDB);
        saveStepBeforeDestroy();
        saveStepToCalculate();
        saveToLocal();
        // Unregister our receiver.
        unregisterReceiver(mReceiver);
        unregisterDetector();
        
        mStateEditor = mState.edit();
        mStateEditor.putInt("steps", mSteps);
        mStateEditor.putInt("weekly", mWeeklyStep);
        mStateEditor.putInt("pace", mPace);
        mStateEditor.putFloat("distance", mDistance);
        mStateEditor.putFloat("speed", mSpeed);
        mStateEditor.putFloat("calories", mCalories);
        mStateEditor.commit();
        
        mNM.cancel(R.string.app_name);

        wakeLock.release();
        Tools.updateCountingState(getApplicationContext(), "0");
        super.onDestroy();
        Log.d(TAG, "Value before destroy : "+mSteps);
        // Stop detecting
        mSensorManager.unregisterListener(mStepDetector);
        // Tell the user we stopped.
        Toast.makeText(this, getText(R.string.stopped), Toast.LENGTH_SHORT).show();
    }

    private void registerDetector() {
        mSensor = mSensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER /*| 
            Sensor.TYPE_MAGNETIC_FIELD | 
            Sensor.TYPE_ORIENTATION*/);
        mSensorManager.registerListener(mStepDetector,
            mSensor,
            SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void unregisterDetector() {
        mSensorManager.unregisterListener(mStepDetector);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "[SERVICE] onBind");
        return mBinder;
    }

    /**
     * Receives messages from activity.
     */
    private final IBinder mBinder = new StepBinder();

    public interface ICallback {
        public void stepsChanged(int value, int minutes);
        public void sensorChanged(float value);
        public void weeklyChanged(int value);
        public void paceChanged(int value);
        public void distanceChanged(float value, float minutes);
        public void speedChanged(float value);
        public void caloriesChanged(float value, float minutes);
    }
    
    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
        //mStepDisplayer.passValue();
        //mPaceListener.passValue();
    }
    
    private int mDesiredPace;
    private float mDesiredSpeed;
    
    /**
     * Called by activity to pass the desired pace value, 
     * whenever it is modified by the user.
     * @param desiredPace
     */
    public void setDesiredPace(int desiredPace) {
        mDesiredPace = desiredPace;
        if (mPaceNotifier != null) {
            mPaceNotifier.setDesiredPace(mDesiredPace);
        }
    }
    /**
     * Called by activity to pass the desired speed value, 
     * whenever it is modified by the user.
     * @param desiredSpeed
     */
    public void setDesiredSpeed(float desiredSpeed) {
        mDesiredSpeed = desiredSpeed;
        if (mSpeedNotifier != null) {
            mSpeedNotifier.setDesiredSpeed(mDesiredSpeed);
        }
    }
    
    public void reloadSettings() {
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        
        if (mStepDetector != null) { 
            mStepDetector.setSensitivity(
                Float.valueOf(mSettings.getString("sensitivity", "10"))
            );
        }
        
        if (mStepDisplayer    != null) mStepDisplayer.reloadSettings();
        if (mWeeklyDisplayer  != null) mWeeklyDisplayer.reloadSettings();
        if (mPaceNotifier     != null) mPaceNotifier.reloadSettings();
        if (mDistanceNotifier != null) mDistanceNotifier.reloadSettings();
        if (mSpeedNotifier    != null) mSpeedNotifier.reloadSettings();
        if (mCaloriesNotifier != null) mCaloriesNotifier.reloadSettings();
        if (mSpeakingTimer    != null) mSpeakingTimer.reloadSettings();
    }
    
    public void resetValues() {
        mStepDisplayer.setSteps(0);
        mPaceNotifier.setPace(0);
        mDistanceNotifier.setDistance(0);
        
        mSpeedNotifier.setSpeed(0);
        mCaloriesNotifier.setCalories(0);
    }
    
    /**
     * Forwards pace values from PaceNotifier to the activity. 
     */
    private StepDisplayer.Listener mStepListener = new StepDisplayer.Listener() {
        public void stepsChanged(int value) {
            mSteps = value;
            if(mSteps > 0)
            	stepBeforeDestroy = mSteps;
         //   Log.d(TAG, "Step before destroy : "+stepBeforeDestroy);
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.stepsChanged(mSteps, mSteps-mLastStep);
            //    Log.d(TAG, "Value after pass value : "+mSteps);
            }
        }
    };
    /**
     * Forwards pace values from PaceNotifier to the activity. 
     */
    private SensorDisplayer.Listener mSensorListener = new SensorDisplayer.Listener() {
		@Override
		public void sensorChanged(float value) {
			if (mCallback != null) {
				mCallback.sensorChanged(value);
			}			
		}
		@Override
		public void passValue() {
			// TODO Auto-generated method stub
			
		}    
    };
    
    /**
     * Forwards pace values from PaceNotifier to the activity. 
     */
    private WeeklyDisplayer.Listener mWeeklyListener = new WeeklyDisplayer.Listener() {
        public void stepsChanged(int value) {
            mWeeklyStep = value;
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.weeklyChanged(mWeeklyStep);
            }
        }
    };
    
    /**
     * Forwards pace values from PaceNotifier to the activity. 
     */
    private PaceNotifier.Listener mPaceListener = new PaceNotifier.Listener() {
        public void paceChanged(int value) {
            mPace = value;
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.paceChanged(mPace);
            }
        }
    };
    /**
     * Forwards distance values from DistanceNotifier to the activity. 
     */
    private DistanceNotifier.Listener mDistanceListener = new DistanceNotifier.Listener() {
        public void valueChanged(float value) {
            mDistance = value;
            if(mDistance > 0)
            	distanceBeforeDestroy = mDistance;
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.distanceChanged(mDistance, mDistance-mLastDistance);
            }
        }
    };
    /**
     * Forwards speed values from SpeedNotifier to the activity. 
     */
    private SpeedNotifier.Listener mSpeedListener = new SpeedNotifier.Listener() {
        public void valueChanged(float value) {
            mSpeed = value;
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.speedChanged(mSpeed);
            }
        }
    };
    /**
     * Forwards calories values from CaloriesNotifier to the activity. 
     */
    private CaloriesNotifier.Listener mCaloriesListener = new CaloriesNotifier.Listener() {
        public void valueChanged(float value) {
            mCalories = value;
            if(mCalories > 0)
            	caloriesBeforeDestroy = mCalories;
          //  Log.d(TAG, "Cal before destroy : "+caloriesBeforeDestroy);
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.caloriesChanged(mCalories, mCalories-mLastCalories);
            }
        }
    };
    
    /**
     * Show a notification while this service is running.
     */
    @SuppressWarnings("deprecation")
	private void showNotification() {
        CharSequence text = getText(R.string.app_name);
        Notification notification = new Notification(R.drawable.ic_notification, null,
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        Intent pedometerIntent = new Intent();
        pedometerIntent.setComponent(new ComponentName(this, MainActivity.class));
        pedometerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                pedometerIntent, 0);
        notification.setLatestEventInfo(this, text, 
                getText(R.string.notification_subtitle), contentIntent);

        mNM.notify(R.string.app_name, notification);
    }


    // BroadcastReceiver for handling ACTION_SCREEN_OFF.
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check action just to be on the safe side.
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // Unregisters the listener and registers it again.
                StepService.this.unregisterDetector();
                StepService.this.registerDetector();
                if (mPedometerSettings.wakeAggressively()) {
                    wakeLock.release();
                    acquireWakeLock();
                }
            }
        }
    };

    @SuppressWarnings("deprecation")
	private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        int wakeFlags;
        if (mPedometerSettings.wakeAggressively()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
        }
        else if (mPedometerSettings.keepScreenOn()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK;
        }
        else {
            wakeFlags = PowerManager.PARTIAL_WAKE_LOCK;
        }
        wakeLock = pm.newWakeLock(wakeFlags, TAG);
        wakeLock.acquire();
    }
    
    private Handler handlerInsertDB = new Handler();	
	
	private Runnable taskInsertDB = new Runnable() {
		   @Override
		   public void run() {
		      /* do what you need to do */
		      saveStep();
		      saveStepToCalculate();
		      /* and here comes the "trick" */
		      handlerInsertDB.postDelayed(this, 60000);
		   }
	};
	
	private void saveStep() {
		Log.d(TAG, "Save Step");
		int tPerMinute = ((mSteps-mLastStep) > 0) ? mSteps-mLastStep:0;
		
		float dPerminuteFloat = ((mDistance-mLastDistance) > 0)? mDistance-mLastDistance:0;
		
		
		float cPerminuteFloat = ((mCalories-mLastCalories) > 0)? mCalories-mLastCalories:0;
		int cPerminute = Math.round(cPerminuteFloat);
		
		Log.d(TAG, "Value before save : "+mSteps);
		Log.d(TAG, "Step to save : "+tPerMinute);
		/*Log.d(TAG, "Cal to Csave : "+cPerminute);
		Log.d(TAG, "Distance to Save : "+dPerminuteFloat);
		Log.i(TAG, "Last Distance Value : "+mLastDistance);
		Log.i(TAG, "Last Step Value : "+mLastStep);
		Log.i(TAG, "Last Calorie Value : "+mLastCalories); */
		if(tPerMinute < 100){
			Tools.saveMeasurement(getApplicationContext(), String.valueOf(tPerMinute),
	    		  String.valueOf(mSpeed), String.valueOf(dPerminuteFloat), String.valueOf(cPerminute));
			Record.saveGameMeasurement(getApplicationContext(), String.valueOf(tPerMinute),
		    		  String.valueOf(mSpeed), String.valueOf(dPerminuteFloat), String.valueOf(cPerminute));
			
			mCalculateStep = tPerMinute;
			mCalculateDistance = dPerminuteFloat;
			mCalculateCalories = cPerminuteFloat;
			
			mLastDistance = mDistance;
			mLastCalories = mCalories;
			mLastStep = mSteps;
		
		}
		
		else if(tPerMinute >= 100){
			Toast.makeText(this, "Steppy : Your step count is invalid", Toast.LENGTH_SHORT).show();
			resetDisplayer();
			Log.d(TAG, "Your step > 100");
		}
		
	}
	
	
	
	private void saveStepBeforeDestroy() {
		Log.d(TAG, "Save Step Before Destroy");
		int tPerMinute = ((stepBeforeDestroy-mLastStep) > 0) ? stepBeforeDestroy-mLastStep:0;
		
		float dPerminuteFloat = ((distanceBeforeDestroy-mLastDistance) > 0)? distanceBeforeDestroy-mLastDistance:0;
		
		
		float cPerminuteFloat = ((caloriesBeforeDestroy-mLastCalories) > 0)? caloriesBeforeDestroy-mLastCalories:0;
		int cPerminute = Math.round(cPerminuteFloat);
		
		if(tPerMinute < 100){
			Tools.saveMeasurement(getApplicationContext(), String.valueOf(tPerMinute),
		    		  String.valueOf(mSpeed), String.valueOf(dPerminuteFloat), String.valueOf(cPerminute));
			
			Record.saveGameMeasurement(getApplicationContext(), String.valueOf(tPerMinute),
		    		  String.valueOf(mSpeed), String.valueOf(dPerminuteFloat), String.valueOf(cPerminute));
		}
		else if(tPerMinute >= 100){
			Toast.makeText(this, "Steppy : Your step count is invalid", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "Your step > 100");
		}
	}
	
	
	
	
	
	
	private void saveStepToCalculate(){
		int tPerMinute = mCalculateStep + Tools.getLastCalculateStep(getApplicationContext());
		
		float cPerminuteFloat = mCalculateCalories + Tools.getLastCalculateCal(getApplicationContext());
		//int cPerminute = Math.round(cPerminuteFloat);
		
		float dPerminute = mCalculateDistance + Tools.getLastCalculateDis(getApplicationContext());
		
		Log.i(TAG, "Last Calculate Step : "+Tools.getLastCalculateStep(getApplicationContext()));
		Log.i(TAG, "Last Calculate Cal : "+Tools.getLastCalculateCal(getApplicationContext()));
		Log.i(TAG, "Last Calculate Dis : "+Tools.getLastCalculateDis(getApplicationContext()));
		
		Log.d(TAG, "Step to Calculate : "+tPerMinute);
		Log.d(TAG, "Cal to Calculate : "+cPerminuteFloat);
		Log.d(TAG, "Distance to Calculate : "+dPerminute);
		
		Tools.updateStepCalculate(getApplicationContext(), String.valueOf(tPerMinute), String.valueOf(cPerminuteFloat)
				, String.valueOf(dPerminute));
	}
	
	
	
	
	
    @SuppressLint("DefaultLocale")
	private void saveToLocal(){
    	Log.i(TAG, "Save To Local");
    	SimpleDateFormat curr = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat start = new SimpleDateFormat("HH:mm");
		SimpleDateFormat end = new SimpleDateFormat("HH:mm");
		String currentDate = curr.format(new Date());
		String startTime = Tools.getStartTime(getApplicationContext());
		String endTime = end.format(new Date());
		String calories =  String.format("%.4f", Tools.getLastCalculateCal(getApplicationContext()));
		Log.i(TAG, "Sending Calories to local : "+calories);
		String steps = Integer.toString(Tools.getLastCalculateStep(getApplicationContext()));
		Log.i(TAG, "Sending step to local : "+steps);
	//	String distances = Float.toString(Tools.getLastCalculateDis(getApplicationContext()));
		String distances = String.format("%.4f", Tools.getLastCalculateDis(getApplicationContext()));
		Log.i(TAG, "Sending distance to local : "+distances);
		String sensor = getSensivityName(mSettings.getString("sensitivity", "Very Low"));
		Tools.saveStepLocal(getApplicationContext(), steps, distances, calories, startTime, endTime, sensor, currentDate);
		Tools.updateStartTimeCalculate(getApplicationContext(), endTime);
		Tools.updateStepCalculate(getApplicationContext(), "0","0","0");
		Log.i(TAG, "Reset calculate step after sync");
    }
    
    
    
    private String getSensivityName(String val){
    	String sensitivityName = "";
    	if(val.equals("1.9753")) sensitivityName = "Extra High";
    	else if(val.equals("2.9630")) sensitivityName = "Very High";
    	else if(val.equals("4.4444")) sensitivityName = "High";
    	else if(val.equals("6.6667")) sensitivityName = "Higher";
    	else if(val.equals("10")) sensitivityName = "Medium";
    	else if(val.equals("15")) sensitivityName = "Lower";
    	else if(val.equals("22.5")) sensitivityName = "Low";
    	else if(val.equals("33.75")) sensitivityName = "Very Low";
    	else if(val.equals("50.625")) sensitivityName = "Extra Low";
    	else if(val.equals("50")) sensitivityName = "Default";
    	else sensitivityName = "Low";
    	return sensitivityName;
    }
	
	
	private void resetDisplayer(){
        mStepDisplayer.setSteps(mSteps = Tools.getTodayStep(this));//mState.getInt("steps", 0));
        mStepDisplayer.addListener(mStepListener);
        mStepDetector.addStepListener(mStepDisplayer);
        
        mSensorDisplayer.addListener(mSensorListener);
        mStepDetector.addSensorListener(mSensorDisplayer);
        
        mWeeklyDisplayer.setSteps(mWeeklyStep = Tools.getLastWeekStep(this));
        mWeeklyDisplayer.addListener(mWeeklyListener);
        mStepDetector.addStepListener(mWeeklyDisplayer);

        mPaceNotifier.setPace(mPace = mState.getInt("pace", 0));
        mPaceNotifier.addListener(mPaceListener); 
        mStepDetector.addStepListener(mPaceNotifier);

        mDistanceNotifier.setDistance(mDistance = Tools.getTodayDistance(this));
        mStepDetector.addStepListener(mDistanceNotifier);
        

        mSpeedNotifier.setSpeed(mSpeed = mState.getFloat("speed", 0));
        mPaceNotifier.addListener(mSpeedNotifier);
        
        mCaloriesNotifier.setCalories(mCalories = Tools.getTodayCalorie(this));
        mStepDetector.addStepListener(mCaloriesNotifier);
	}
	
	
	
}

