package org.lskk.shesop.steppy.msband;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.lskk.shesop.steppy.AboutActivity;
import org.lskk.shesop.steppy.CalibrateActivity;
import org.lskk.shesop.steppy.DashBoardActivity;
import org.lskk.shesop.steppy.Preferences;
import org.lskk.teamshesop.steppy.R;
import org.lskk.shesop.steppy.Splash;
import org.lskk.shesop.steppy.data.Account;
import org.lskk.shesop.steppy.receiver.BackupAlarmBandReceiver;
import org.lskk.shesop.steppy.receiver.BackupBandService;
import org.lskk.shesop.steppy.receiver.BackupHeartRateAlarmReceiver;
import org.lskk.shesop.steppy.receiver.GameAlarmReceiver;
import org.lskk.shesop.steppy.receiver.SyncAlarmReceiver;
import org.lskk.shesop.steppy.utils.Tools;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.BandPendingResult;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;
import com.microsoft.band.sensors.BandHeartRateEvent;
import com.microsoft.band.sensors.BandPedometerEvent;
import com.microsoft.band.sensors.BandPedometerEventListener;
import com.microsoft.band.sensors.BandHeartRateEventListener;
import com.microsoft.band.sensors.BandUVEventListener;
import com.microsoft.band.sensors.HeartRateConsentListener;
import com.microsoft.band.sensors.BandDistanceEvent;
import com.microsoft.band.sensors.BandDistanceEventListener;
import com.microsoft.band.sensors.BandCaloriesEvent;
import com.microsoft.band.sensors.BandCaloriesEventListener;
import com.microsoft.band.sensors.BandSkinTemperatureEvent;
import com.microsoft.band.sensors.BandSkinTemperatureEventListener;
import com.microsoft.band.sensors.BandUVEvent;
import com.microsoft.band.sensors.BandCaloriesEvent;

import android.R.string;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;





public class MSBandActivity extends Activity {
	private BandClient client = null;
    BandClient bandClient;
    private static final int REQUEST_CAMERA = 1;
	private static final int REQUEST_GALLERY = 2;
	private TextView mAccName;
	private TextView mAge;
	private TextView mHeight;
	private TextView mWeight;
	private TextView mTest;
	private TextView mTextState;
	private String bmiState;
	private float bb, tb, bmi;
	private ImageView mProfpict;
	private static TextView mStepValueView;
	private static TextView mDistanceValueView;
	private static TextView mPaceValueView;
	private static TextView mTemperatureValueView;
	private static TextView mUvValueView;
	private static TextView mSpeedValueView;
	private static TextView mCaloriesValueView;
	private static TextView mHeartRateValueView;
	//private static TextView m
	private ImageButton buttonOff;
	private Button startStop;
	private Boolean isRunning = false;
	private NotificationManager mNM;
	private Timer heartRateTimer;
	private Timer measurementTimer;
	private String startTime;
	private String endTime;
	private String currDate;
	private SimpleDateFormat start;
	private SimpleDateFormat end;
	private long heartRateValue;
	private long stepValue;
	private long distanceValue;
	private long caloriesValue;
	private float speedValue;
	private float paceValue;
	private float temperatureValue;
	private String uvLevelValue = "NONE";
	private String toastText;
	private IntentFilter requestBluetoothDisconect;
	private IntentFilter bluetoothDisconect;
	private appTask msBandTask;
	private sendHeartRate myTask;
	private saveMeasurement measurementTask;
	private Boolean isBlueTooth = true;
	private Boolean enableHeartrate = true;
	
	private int mLastStep;
	private int mLastCalories;
	private int mLastDistance;
	
	private int mLastStepRecord;
	private int mLastCaloriesRecord;
	private int mLastDistanceRecord;
	private ShareActionProvider mShareActionProvider;
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		saveToCalculate();
		saveMeasurement();
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msband_layout);
        
        BackupHeartRateAlarmReceiver alarm = new BackupHeartRateAlarmReceiver();
        alarm.setAlarm(this);
        GameAlarmReceiver gAlarm = new GameAlarmReceiver();
        gAlarm.setAlarm(this);
        
        
        Log.i("MSBAND Act", "Today distance :"+Tools.getTodayDistanceBand(MSBandActivity.this));
        Log.i("MSBAND Act", "Today Step :"+Tools.getTodayStepBand(MSBandActivity.this));
        Log.i("MSBAND Act", "Today Cal :"+Tools.getTodayCalorieBand(MSBandActivity.this));
        
        BackupAlarmBandReceiver bandAlarm = new BackupAlarmBandReceiver();
        bandAlarm.setAlarm(this);
        
        mLastStep = Tools.getLastCalculateStepBand(getApplicationContext());
        mLastCalories = Integer.parseInt(Tools.getLastCalculateCalBand(getApplicationContext()));
        mLastDistance = Integer.parseInt(Tools.getLastCalculateDistanceBand(getApplicationContext()));
        
        mLastStepRecord = Tools.getLastCalculateStepBandRecord(getApplicationContext());
        mLastCaloriesRecord = Integer.parseInt(Tools.getLastCalculateDistanceBandRecord(getApplicationContext()));
        mLastDistanceRecord = Integer.parseInt(Tools.getLastCalculateDistanceBandRecord(getApplicationContext()));
        
        SimpleDateFormat start = new SimpleDateFormat("HH:mm");
        String startTime = start.format(new Date());
        Tools.updateStartTimeCalculateBand(getApplicationContext(), startTime);
        
        
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
        
        bb = Float.parseFloat(Account.getAccountWeight(MSBandActivity.this)) ;
		tb = Float.parseFloat(Account.getAccountHeight(MSBandActivity.this)) / 100 ;
		bmi = bb / (tb * tb);
		if(bmi < 18.5)
			bmiState = "Underweight";
		if(bmi >= 18.5 && bmi <= 24)
			bmiState = "Normal Weight";
		if(bmi >= 25 && bmi <= 29)
			bmiState = "Overweight";
		if(bmi > 30)
			bmiState = "Obese";
		
		mTextState = (TextView)findViewById(R.id.text_state);
		mTextState.setText(bmiState);
		mAccName = (TextView)findViewById(R.id.dashboard_name);
		mAccName.setText(Account.getAccountName(MSBandActivity.this));
		mAge = (TextView)findViewById(R.id.text_age);
		mAge.setText(Account.getAccountAge(MSBandActivity.this)+" Years");
		mHeight = (TextView)findViewById(R.id.text_height);
		mHeight.setText(Account.getAccountHeight(MSBandActivity.this)+ " cm, ");
		mWeight = (TextView)findViewById(R.id.text_weight);
		mWeight.setText(Account.getAccountWeight(MSBandActivity.this)+" Kg");
		mTest = (TextView)findViewById(R.id.test_array);
		
		mProfpict = (ImageView)findViewById(R.id.dashboard_profpict);
		byte[] bmpArray = Tools.getAccountProfpict(MSBandActivity.this);
		if (bmpArray != null)
			mProfpict.setImageBitmap(BitmapFactory.decodeByteArray(bmpArray, 0,
					bmpArray.length));
		else
			mProfpict.setImageResource(R.drawable.profpict_blank);
		
		
		msBandTask = new appTask();
		msBandTask.execute();
		
		isRunning = true;

		Toast.makeText(MSBandActivity.this, "Counting Started", Toast.LENGTH_SHORT).show();
		// set start time
        start = new SimpleDateFormat("HH:mm");
        startTime = start.format(new Date());
        
        myTask = new sendHeartRate();
        measurementTask = new saveMeasurement();
        
        heartRateTimer = new Timer();
        measurementTimer = new Timer();
        
        heartRateTimer.schedule(myTask, 60000 * 60, 60000 * 60);
        measurementTimer.schedule(measurementTask, 60000, 60000);
		
		startStop = (Button)findViewById(R.id.stopandstart);
		startStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					if(!isRunning){
						startCount();
					}
					
					else if(isRunning){
						stopCount();
						
					}
			}
		});
		
		mStepValueView 		= (TextView) findViewById(R.id.dashboard_step_count);
		mSpeedValueView 	= (TextView) findViewById(R.id.speed_value);
		mCaloriesValueView 	= (TextView) findViewById(R.id.calories_value);
		mHeartRateValueView 	= (TextView) findViewById(R.id.dashboard_hr_count);
		mDistanceValueView = (TextView)findViewById(R.id.distance_value);
		mPaceValueView = (TextView)findViewById(R.id.pace_value);
		mTemperatureValueView = (TextView)findViewById(R.id.temerature_value);
		mUvValueView = (TextView)findViewById(R.id.uv_value);
		
		System.out.println("State : "+Integer.toString(Tools.getIsCountingState(MSBandActivity.this)));
	//	if(Tools.getIsCountingState(MSBandActivity.this) == 0){
		
	//		Tools.updateCountingState(getApplicationContext(), "1");
	//	}
		
		
		requestBluetoothDisconect = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		bluetoothDisconect  = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		this.registerReceiver(mReceiver, requestBluetoothDisconect);
	    this.registerReceiver(mReceiver, bluetoothDisconect);
	    
		
    }
    
    
    
    private void startCount(){
    	//msBandTask.execute();
    	enableHeartrate = true;
    	start = new SimpleDateFormat("HH:mm");
        startTime = start.format(new Date());
    	requestBluetoothDisconect = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		bluetoothDisconect  = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		this.registerReceiver(mReceiver, requestBluetoothDisconect);
	    this.registerReceiver(mReceiver, bluetoothDisconect);
	    
    	Toast.makeText(MSBandActivity.this, "Counting Started", Toast.LENGTH_LONG).show();
		startStop.setBackgroundColor(Color.parseColor("#d04545"));
		startStop.setText("Stop Counting");
		showNotification();
		isRunning = true;
		myTask = new sendHeartRate();
		this.heartRateTimer = new Timer();
		this.heartRateTimer.schedule(myTask, 60000 * 60, 60000 * 60);
		msBandTask = new appTask();
		msBandTask.execute();
    }
    
    
    private void stopCount(){
    	enableHeartrate = false;
    	unRegisterListeners("Counting Stopped");
		resetValues();
		myTask.cancel();
		measurementTask.cancel();
		System.out.println("Stop MS Band");
		this.heartRateTimer.cancel();
		// heartRateTimer.purge();
		msBandTask.cancel(true);
		mNM.cancel(R.string.app_name);
		startStop.setBackgroundColor(Color.parseColor("#00a65a"));
		startStop.setText("Start Counting");
		isRunning = false;
		
    }
    
    private void blueToothDown(){
    	enableHeartrate = false;
    	unRegisterListeners("Device Disconnected, Counting Stopped");
		resetValues();
		msBandTask.cancel(true);
		myTask.cancel();
		System.out.println("Stop MS Band");
		this.heartRateTimer.cancel();
		mNM.cancel(R.string.app_name);
		isRunning = false;
		startStop.setVisibility(View.GONE);
		appendToUI("Device Disconnected!\nPlease make sure bluetooth is on and the band is in range.", 1);
		mTest.setTextColor(Color.parseColor("#d04545"));
		finish();
    }
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

    //        if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
       //        //Device is about to disconnect
       //     	isBlueTooth = false;
        //    	blueToothDown();
            	
       //     }
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
               //Device has disconnected
            	isBlueTooth = false;
            	blueToothDown();
            } 
        }
    };
    
    
    
    
    
    
    
    private void resetValues(){
    	mStepValueView.setText("0");
    	mSpeedValueView.setText("0");
    	mCaloriesValueView.setText("0");
    	mHeartRateValueView.setText("0");
    	mDistanceValueView.setText("0");
    	mPaceValueView.setText("0");
    	mTemperatureValueView.setText("0");
    	mUvValueView.setText("0");
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {
         if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
         {
        	 if(isRunning){
        		 this.moveTaskToBack(true);
        	 }
        	 else if(!isRunning){
        		 finish();
        	 }
            return true;
         }
        return super.onKeyDown(keyCode, event);
    }
    
    
	@Override
	protected void onResume() {
		super.onResume();
		// txtStatus.setText("");
	}
	
	
	private class appTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (getConnectedBandClient()) {
					appendToUI("Band is connected.", 1);
					mTest.setTextColor(Color.parseColor("#2c3e50"));
					if(client.getSensorManager().getCurrentHeartRateConsent() !=
							UserConsent.GRANTED) {
						client.getSensorManager().requestHeartRateConsent(MSBandActivity.this, heartRateConsentListener);
					}
						if(enableHeartrate)
							client.getSensorManager().registerHeartRateEventListener(heartRateListener);
						client.getSensorManager().registerPedometerEventListener(pedometerListener);
						client.getSensorManager().registerDistanceEventListener(distanceListener);
						client.getSensorManager().registerCaloriesEventListener(caloriesListener);
						client.getSensorManager().registerSkinTemperatureEventListener(temperaturListener);
						client.getSensorManager().registerUVEventListener(uvListener);
						
						
					
				} else {
				//	appendToUI("Band isn't connected. Please make sure bluetooth is on and the band is in range.", 1);
					blueToothDown();
				}
			} catch (BandException e) {
				String exceptionMessage="";
				switch (e.getErrorType()) {
				case UNSUPPORTED_SDK_VERSION_ERROR:
					exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.";
					break;
				case SERVICE_ERROR:
					exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.";
					break;
				default:
					exceptionMessage = e.getMessage();
					break;
				}
				appendToUI(e.getMessage() + "\nAccept permision of Microsoft Health Service, then restart counting", 1);
				mTest.setTextColor(Color.parseColor("#d04545"));

			} catch (Exception e) {
				appendToUI(e.getMessage(), 1);
				mTest.setTextColor(Color.parseColor("#d04545"));
			}
			
			return null;
		}
	}
	
	
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
        pedometerIntent.setComponent(new ComponentName(this, Splash.class));
        pedometerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                pedometerIntent, 0);
        notification.setLatestEventInfo(this, text,
                "Counting Your Steps", contentIntent);

        mNM.notify(R.string.app_name, notification);
    }
	
	
	private void appendToUI(final String string, final int code) {
		// code : 1 = state, 2 = Step, 3 = heartrate
		// 4 = speed, 5 = calories, 6 = distance
		// 7 = pace, 8 = temp, 9 = uv
		this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	if(code == 1)
            		mTest.setText(string);
            	else if(code == 2)
            		mStepValueView .setText(string);
            	else if(code == 3)
            		mHeartRateValueView.setText(string);
            	else if(code == 4)
            		mSpeedValueView.setText(string);
            	else if(code == 5)
            		mCaloriesValueView.setText(string);
            	else if(code == 6)
            		mDistanceValueView.setText(string);
            	else if(code == 7)
            		mPaceValueView.setText(string);
            	else if(code == 8)
            		mTemperatureValueView.setText(string);
            	else if(code == 9)
            		mUvValueView.setText(string);
            }
        });
	}
	
	
	

    
    
    private HeartRateConsentListener heartRateConsentListener = new HeartRateConsentListener() {
		@Override
		public void userAccepted(boolean b) {
			
		}
	};
	
    
    private BandHeartRateEventListener heartRateListener = new BandHeartRateEventListener() {
        @Override
        public void onBandHeartRateChanged(final BandHeartRateEvent event) {
            if (event != null) {
            	appendToUI(Integer.toString(event.getHeartRate()), 3);
            	heartRateValue = event.getHeartRate();
            	 saveHeartRate();
            	// System.out.println(event.getHeartRate());
            }
        }
    };

    private BandCaloriesEventListener caloriesListener = new BandCaloriesEventListener() {
		
		@Override
		public void onBandCaloriesChanged(BandCaloriesEvent event) {
			// TODO Auto-generated method stub
        	appendToUI(String.valueOf(event.getCalories()), 5);
			// System.out.println(event.getCalories());
        	caloriesValue = event.getCalories();
		}
	};
	
	
	private BandUVEventListener uvListener = new BandUVEventListener() {
		
		@Override
		public void onBandUVChanged(BandUVEvent event) {
			// TODO Auto-generated method stub
			appendToUI(String.valueOf(event.getUVIndexLevel()), 9);
			uvLevelValue = String.valueOf(event.getUVIndexLevel());
		}
	};
    
	
	
    private BandPedometerEventListener pedometerListener = new BandPedometerEventListener() {
		
		@Override
		public void onBandPedometerChanged(BandPedometerEvent event) {
			// TODO Auto-generated method stub
			appendToUI(String.valueOf(event.getTotalSteps()), 2);
			stepValue = event.getTotalSteps();
        	// System.out.println(event.getTotalSteps());
		}
	};
	
	private BandSkinTemperatureEventListener temperaturListener = new BandSkinTemperatureEventListener() {
		
		@Override
		public void onBandSkinTemperatureChanged(BandSkinTemperatureEvent event) {
			// TODO Auto-generated method stub
			appendToUI(String.valueOf(event.getTemperature()), 8);
			temperatureValue = event.getTemperature();
		}
	};
	
	private BandDistanceEventListener distanceListener = new BandDistanceEventListener() {
		
		@Override
		public void onBandDistanceChanged(BandDistanceEvent event) {
			// TODO Auto-generated method stub
			
			appendToUI(String.valueOf(event.getSpeed()), 4);
			speedValue = event.getSpeed();
			appendToUI(String.valueOf(event.getTotalDistance()/100000), 6);
			// convert distance cm to km
			distanceValue = event.getTotalDistance()/100;
			appendToUI(String.valueOf(event.getPace()), 7);
			paceValue = event.getPace();
			// System.out.println(event.getSpeed());
			
		}
	};

private boolean getConnectedBandClient() throws InterruptedException, BandException {
	if (client == null) {
		BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
		if (devices.length == 0) {
			appendToUI("Band isn't paired with your phone.", 1);
			mTest.setTextColor(Color.parseColor("#d04545"));
			return false;
		}
		client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
	} else if (ConnectionState.CONNECTED == client.getConnectionState()) {
		return true;
	} else if(ConnectionState.UNBOUND == client.getConnectionState())
		return false;
	
	appendToUI("Band is connecting...", 1);
	return ConnectionState.CONNECTED == client.connect().await();
}    


class sendHeartRate extends TimerTask {
    public void run() {
    	android.os.Process
		.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
    	// saveHeartRate();
    	 saveToCalculate();

    }
}



class saveMeasurement extends TimerTask {
    public void run() {
    	android.os.Process
		.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
    	saveMeasurement();

    }
}


private void saveHeartRate(){
	SimpleDateFormat curr = new SimpleDateFormat("dd/MM/yyyy");
	currDate = curr.format(new Date());
	end = new SimpleDateFormat("HH:mm");
	endTime = end.format(new Date());
	
/*	Log.d("MS BAND ACTIVITY", "Heartrate : "+String.valueOf(heartRateValue));
	Log.d("MS BAND ACTIVITY", "Start Time : "+startTime);
	Log.d("MS BAND ACTIVITY", "End Time : "+endTime);
	Log.d("MS BAND ACTIVITY", "Date : "+currDate);
	Log.d("MS BAND ACTIVITY", "Speed : "+String.valueOf(speedValue));
	Log.d("MS BAND ACTIVITY", "Pace : "+String.valueOf(paceValue));
	Log.d("MS BAND ACTIVITY", "Temperature : "+String.valueOf(temperatureValue));
	Log.d("MS BAND ACTIVITY", "UV : "+uvLevelValue); */
	
	Tools.saveHeartRateLocal(getApplicationContext(), String.valueOf(heartRateValue), startTime, 
			endTime, currDate, String.valueOf(speedValue), String.valueOf(paceValue), String.valueOf(temperatureValue), uvLevelValue);
	startTime = endTime;
}


@Override
public boolean onCreateOptionsMenu(Menu menu) {
	// Inflate the menu 
	// this adds items to the action bar if it is present.
	getMenuInflater().inflate(R.menu.mainband, menu);

    // Locate MenuItem with ShareActionProvider
    MenuItem item = menu.findItem(R.id.action_share);
    
    // Fetch and store ShareActionProvider
    mShareActionProvider = (ShareActionProvider) item.getActionProvider();
	return true;
}
// Call to update the share intent
@SuppressWarnings("unused")
private void setShareIntent(Intent shareIntent) {
    if (mShareActionProvider != null) {
        mShareActionProvider.setShareIntent(shareIntent);
    }
}


@Override
public boolean onMenuItemSelected(int featureId, MenuItem item) {
	
	switch(item.getItemId()) {
	case R.id.dashboard:
    		startActivity(new Intent(this,DashBoardActivityBand.class));
    		break;
        case R.id.about:
        	 startActivity(new Intent(this,AboutActivity.class));
    	    break;
        case R.id.logout:
        	if(Tools.getIsCountingState(getApplicationContext())==0){
        		stopCount();
	        	Intent myIntent = new Intent(MSBandActivity.this, Splash.class);
	            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
	        	clearApplicationData(MSBandActivity.this);
	        	startActivity(myIntent);
	        	Toast.makeText(MSBandActivity.this, "Logout Success", Toast.LENGTH_SHORT).show();
	        	
	        	  
	            finish();
        	} else
        		Toast.makeText(MSBandActivity.this, "Stop Counting Step first to Signout", Toast.LENGTH_LONG).show();
    	    break;
        case R.id.action_share:
        	String steps = String.valueOf(Tools.getTodayStep(MSBandActivity.this));
        	String calories = String.valueOf(Tools.getTodayCalorie(MSBandActivity.this));
        	String distances = String.format("%.2f", Tools.getTodayDistance(MSBandActivity.this));
        	String textToShare = "http://shesop.lskk.ee.itb.ac.id\nHai, hari ini Saya sudah melangkah "+steps+" langkah sejauh "+distances+" Km dan membakar "+calories+" kalori, bagaimana dengan Anda ?";
        	Intent sendIntent = new Intent();
    	    sendIntent.setAction(Intent.ACTION_SEND);
    	    sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
    	    sendIntent.setType("text/plain");
    	    startActivity(sendIntent);
            return true;
    }
	return super.onMenuItemSelected(featureId, item);
}



public static void clearApplicationData(Context context) {
    File cache = context.getCacheDir();
    File appDir = new File(cache.getParent());
    if (appDir.exists()) {
        String[] children = appDir.list();
        for (String s : children) {
            File f = new File(appDir, s);
            if(deleteDir(f))
                Log.i("TAG", String.format("**************** DELETED -> (%s) *******************", f.getAbsolutePath()));
        }
    }
}
private static boolean deleteDir(File dir) {
    if (dir != null && dir.isDirectory()) {
        String[] children = dir.list();
        for (int i = 0; i < children.length; i++) {
            boolean success = deleteDir(new File(dir, children[i]));
            if (!success) {
                return false;
            }
        }
    }
    return dir.delete();
}


    private void unRegisterListeners(String toastText){
	    if(isBlueTooth == true){
    		if (client != null) {
		    	try {
		    		client.getSensorManager().unregisterAllListeners();
				} catch (BandIOException e) {
					appendToUI(e.getMessage(), 1);
				}
	    	}
	    	Toast.makeText(MSBandActivity.this, toastText, Toast.LENGTH_LONG).show();
			this.unregisterReceiver(mReceiver);
	    }	
		// finish();
    }
    
 /*   @Override
    protected void onDestroy(){
    	super.onDestroy();
    	if(!isBlueTooth){
    		if (client != null) {
		    	try {
					client.getSensorManager().unregisterPedometerEventListeners();
					client.getSensorManager().unregisterHeartRateEventListeners();
					client.getSensorManager().unregisterDistanceEventListeners();
					client.getSensorManager().unregisterCaloriesEventListeners();
					client.getSensorManager().unregisterSkinTemperatureEventListeners();
					client.getSensorManager().unregisterUVEventListeners();
				} catch (BandIOException e) {
					appendToUI(e.getMessage(), 1);
				}
	    	}
    		this.unregisterReceiver(mReceiver);
    	}
    } */
    
    private void saveToCalculate(){
    	int countStep = (int)stepValue;
    	int countCal = (int)caloriesValue;
    	int countDistance = (int)distanceValue;
    	
    	// data yang dikirim 
    	int currentStep = countStep -  mLastStep;
    	int currentCal = countCal - mLastCalories;
    	int currentDist = countDistance - mLastDistance;
    	String sTime = Tools.getStartTimeBand(getApplicationContext());
    	SimpleDateFormat end = new SimpleDateFormat("HH:mm");
        String endTime = end.format(new Date());
        SimpleDateFormat curr = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = curr.format(new Date());
        
    	Tools.saveStepBandLocal(getApplicationContext(), 
    			String.valueOf(currentStep), String.valueOf(currentCal), sTime, endTime, String.valueOf(currentDist), currentDate);
    	Log.d("MSBAND Sending", "Step : "+String.valueOf(currentStep));
    	Log.d("MSBAND Sending", "Cal : "+String.valueOf(currentCal));
    	Log.d("MSBAND Sending", "Dist : "+String.valueOf(currentDist));
    	Log.d("MSBAND Sending", "sTime : "+sTime);
    	Log.d("MSBAND Sending", "eTime : "+endTime);
    	Log.d("MSBAND Sending", "date : "+currentDate);
    	
    	// ngupdate ke db
    	mLastStep = countStep;
    	mLastCalories = countCal;
    	mLastDistance = countDistance;
    	Log.i("MSBAND Sending", "Last Step : "+String.valueOf(mLastStep));
    	Log.i("MSBAND Sending", "Last Cal : "+String.valueOf(mLastCalories));
    	Log.i("MSBAND Sending", "Dist : "+String.valueOf(mLastDistance));
    	Tools.updateStartTimeCalculateBand(getApplicationContext(), endTime);
    	Tools.updateStepCalculateBand(getApplicationContext(), 
    			String.valueOf(mLastStep), String.valueOf(mLastCalories) , 
    			String.valueOf(mLastDistance));
    }
    
    
    
    
    
    private void saveMeasurement(){
    	int countStep = (int)stepValue;
    	int countCal = (int)caloriesValue;
    	int countDistance = (int)distanceValue;
    	
    	// data yang dikirim 
    	int currentStep = countStep -  mLastStepRecord;
    	int currentCal = countCal - mLastCaloriesRecord;
    	int currentDist = countDistance - mLastDistanceRecord;
    	
    	Log.d("MSBAND Sending", "Step M : "+String.valueOf(currentStep));
    	Log.d("MSBAND Sending", "Cal M : "+String.valueOf(currentCal));
    	Log.d("MSBAND Sending", "Dist M : "+String.valueOf(currentDist));
    	
    	Tools.saveMeasurementBand(getApplicationContext(), 
    			String.valueOf(currentStep), 
    			String.valueOf(currentDist), 
    			String.valueOf(currentCal));
    	
    	// ngupdate ke db
    	mLastStepRecord = countStep;
    	mLastCaloriesRecord = countCal;
    	mLastDistanceRecord = countDistance;
    	
    	Tools.updateStepCalculateBandRecord(getApplicationContext(), 
    			String.valueOf(mLastStep), String.valueOf(mLastCalories) , 
    			String.valueOf(mLastDistance));
    }
}