package org.lskk.shesop.steppy;

import org.lskk.shesop.steppy.algorithms.CalibrateService;
import org.lskk.shesop.steppy.algorithms.PedometerSettings;
import org.lskk.shesop.steppy.heartratemonitor.HeartRateMonitor;
import org.lskk.shesop.steppy.msband.MSBandActivity;
import org.lskk.shesop.steppy.utils.Tools;
import org.lskk.shesop.steppy.utils.Utils;
import org.lskk.teamshesop.steppy.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



public class CalibrateActivity extends Activity {
	private static final String TAG = "Pedometer";
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private Utils mUtils;
    
    private TextView mStepValueView;
    TextView mDesiredPaceView;
    private int mStepValue;
    private boolean mQuitting = false; // Set when user selected Quit from menu, can be used by onPause, onStop, onDestroy
    private String[] sensitivitas;
    private String[] sensitivitasName;
    private Spinner sensitivitasSpinner;
    private Button setButton;
    private Button startandstopButton;
    private Button stopService;
    private String type;
    private TextView sensNameDisplay;
    private SharedPreferences mState;
    private SharedPreferences.Editor mStateEditor;
    private String sensString;
    private Boolean isRunning = false;
    /**
     * True, when service is running.
     */
    private boolean mIsRunning;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "[ACTIVITY] onCreate");
        super.onCreate(savedInstanceState);
        
        mStepValue = 0;
        
        setContentView(R.layout.calibrate_layout);
        startandstopButton = (Button)findViewById(R.id.stopandstart);
        
        sensNameDisplay = (TextView)findViewById(R.id.sens_name);
        mState = PreferenceManager.getDefaultSharedPreferences(this);
        mStateEditor = PreferenceManager.getDefaultSharedPreferences(CalibrateActivity.this).edit();
        sensString = mState.getString("sensitivity", "10");
        if(sensString.contains("1.9753"))
        	sensNameDisplay.setText("Extra High");
        else if(sensString.contains("2.9630"))
        	sensNameDisplay.setText("Very High");
        else if(sensString.contains("4.4444"))
        	sensNameDisplay.setText("High");
        else if(sensString.contains("6.6667"))
        	sensNameDisplay.setText("Higher");
        else if(sensString.contains("10"))
        	sensNameDisplay.setText("Medium");
        else if(sensString.contains("15"))
        	sensNameDisplay.setText("Lower");
        else if(sensString.contains("22.5"))
        	sensNameDisplay.setText("Low");
        else if(sensString.contains("33.75"))
        	sensNameDisplay.setText("Very Low");
        else if(sensString.contains("50.625"))
        	sensNameDisplay.setText("Extra Low");
        mUtils = Utils.getInstance();
        setButton = (Button)findViewById(R.id.buttonset);
        sensitivitasSpinner = (Spinner) findViewById(R.id.sens_spinner);
        sensitivitas = getResources().getStringArray(R.array.sensitivity_preference_values_calibrate);
        sensitivitasName = getResources().getStringArray(R.array.sensitivity_preference_calibrate);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, R.id.spintext, sensitivitasName);
        sensitivitasSpinner.setAdapter(adapter);
        sensitivitasSpinner.setContentDescription("Choose Type");
        int selectedPosition = sensitivitasSpinner.getSelectedItemPosition();
        type = String.valueOf(sensitivitas[selectedPosition]);
        
        setButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mStateEditor = mState.edit();
				String setValue = String.valueOf(sensitivitasSpinner.getSelectedItem());
				if(setValue.contains("extra high"))
					mStateEditor.putString("sensitivity", "1.9753");
				else if(setValue.contains("very high"))
					mStateEditor.putString("sensitivity", "2.9630");
				else if(setValue.contains("higher"))
					mStateEditor.putString("sensitivity", "6.6667");
				else if(setValue.contains("high"))
					mStateEditor.putString("sensitivity", "4.4444");
				else if(setValue.contains("medium"))
					mStateEditor.putString("sensitivity", "10");
				else if(setValue.contains("lower"))
					mStateEditor.putString("sensitivity", "15");
				else if(setValue.contains("very low"))
					mStateEditor.putString("sensitivity", "33.75");
				else if(setValue.contains("extra low"))
					mStateEditor.putString("sensitivity", "50.625");
				else if(setValue.contains("low"))
					mStateEditor.putString("sensitivity", "22.5");
				mStateEditor.commit();
				if(mService != null){
					resetValues(false);
					unbindStepService();
	                stopStepService();
	                mQuitting = true;
				}
				finish();
				startActivity(getIntent());
				
			}
		});
        if(Tools.getIsCountingState(CalibrateActivity.this)== 2)
        	startandstopButton.setText("Stop Calibrate");
        startandstopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 mIsRunning = mPedometerSettings.isServiceRunning();
			        isRunning = true;
			        // Start the service if this is considered to be an application start (last onPause was long ago)
			        if (!mPedometerSettings.isServiceRunning()) {
			            startStepService();
			            bindStepService();
			            startandstopButton.setText("Stop Calibrate");
			        }
			        if(mService != null){
			        	resetValues(false);
		                unbindStepService();
		                stopStepService();
		                mQuitting = true;
			            startandstopButton.setText("Start Calibrate");
			            finish();
			        }
				
			}
		});
        

    }
    
    
    
    
	@Override
	public void onBackPressed() {		
		super.onBackPressed();
		if(!isRunning)
			this.finish();
		else
			//Toast.makeText(CalibrateActivity.this, "Press Stop Calibrate to Exit", Toast.LENGTH_SHORT).show();
			moveTaskToBack(true);
	}
    
    @Override
    public void onStart() {
        Log.i(TAG, "[ACTIVITY] onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "[ACTIVITY] onResume");
        super.onResume();
        
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);        
        mUtils.setSpeak(mSettings.getBoolean("speak", false));
        
        // Read from preferences if the service was running on the last onPause
        mIsRunning = mPedometerSettings.isServiceRunning();
        
        // Start the service if this is considered to be an application start (last onPause was long ago)
     //   if (!mIsRunning && mPedometerSettings.isNewStart()) {
      //      startStepService();
      //      bindStepService();
     //   }
        if (mIsRunning) {
            bindStepService();
        }
        
        mPedometerSettings.clearServiceRunning();

        mStepValueView     = (TextView) findViewById(R.id.step_value);
        
    }
    
    
    @Override
    public void onPause() {
        Log.i(TAG, "[ACTIVITY] onPause");
        
        if (mIsRunning) {
            unbindStepService();
        }
        if (mQuitting) {
            mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
        }
        else {
            mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
        }
        moveTaskToBack(true);
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "[ACTIVITY] onStop");
        super.onStop();
        moveTaskToBack(true);
    }

    public void onDestroy() {
        Log.i(TAG, "[ACTIVITY] onDestroy");
        super.onDestroy();
        moveTaskToBack(true);
    }
    
    public void onRestart() {
        Log.i(TAG, "[ACTIVITY] onRestart");
        super.onDestroy();
    }

    

    private CalibrateService mService;
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((CalibrateService.StepBinder)service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();
            
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    

    private void startStepService() {
        if (! mIsRunning) {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            startService(new Intent(CalibrateActivity.this,
            		CalibrateService.class));
        }
    }
    
    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        bindService(new Intent(CalibrateActivity.this, 
        		CalibrateService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        unbindService(mConnection);
    }
    
    private void stopStepService() {
        Log.i(TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(TAG, "[SERVICE] stopService");
            stopService(new Intent(CalibrateActivity.this,
            		CalibrateService.class));
        }
        mIsRunning = false;
    }
    
    private void resetValues(boolean updateDisplay) {
        if (mService != null && mIsRunning) {
            mService.resetValues();                    
        }
        else {
            mStepValueView.setText("0");
            SharedPreferences state = getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);
                stateEditor.putInt("pace", 0);
                stateEditor.putFloat("distance", 0);
                stateEditor.putFloat("speed", 0);
                stateEditor.putFloat("calories", 0);
                stateEditor.commit();
            }
        }
    }

 
    // TODO: unite all into 1 type of message
    private CalibrateService.ICallback mCallback = new CalibrateService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void paceChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }
        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), 0));
        }
        public void speedChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
        }
    };
    
    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int)msg.arg1;
                    mStepValueView.setText("" + mStepValue);
                    break;
                case PACE_MSG:
                    break;
                case DISTANCE_MSG:
                    break;
                case SPEED_MSG:
                    break;
                case CALORIES_MSG:
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
        
    };
}