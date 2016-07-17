package org.lskk.shesop.steppy.receiver;

import org.json.JSONArray;
import org.lskk.shesop.steppy.Splash;
import org.lskk.shesop.steppy.connection.AccountHandler;
import org.lskk.shesop.steppy.connection.IConnectionResponseHandler;
import org.lskk.shesop.steppy.data.Account;
import org.lskk.shesop.steppy.data.TBAccount;
import org.lskk.shesop.steppy.utils.Tools;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class BackupHeartRateService extends IntentService {
    public BackupHeartRateService() {
        super("SchedulingService");
    }
    
    public static final String TAG = "Sending Backup HeartRate Data ";
    // An ID used to post the notification.
    public static final int NOTIFICATION_ID = 1;
    // The string the app searches for in the Google home page content. If the app finds 
    // the string, it indicates the presence of a doodle.  
    public static final String SEARCH_STRING = "doodle";
    // The Google home page URL from which the app fetches content.
    // You can find a list of other Google domains with possible doodles here:
    // http://en.wikipedia.org/wiki/List_of_Google_domains
    public static final String URL = "http://www.google.com";
//    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    TBAccount tbAccount;
    SharedPreferences mSettings;
    private int localData;
    private int dataCount;
    private String id;
    private String idsheSop;
    private String date;
    private String startTime;
    private String endTime;
    private String heartRate;
    private String temperature;
    private String speed;
    private String pace;
    private String uvlevel;
    
    @Override
    protected void onHandleIntent(Intent intent) {
        // BEGIN_INCLUDE(service_onhandle)
    		
    	mSettings = PreferenceManager.getDefaultSharedPreferences(this);
    	// Synchronizing measurement data to server
    	if(Tools.isNetworkConnected(this)) {
    	//	if(Tools.getIsCountingState(getApplicationContext())==1){
	    		Handler mHandler = new Handler(getMainLooper());
	    	    mHandler.post(new Runnable() {
	    	        @Override
	    	        public void run() {
	    	        	dataCount = Tools.getSyncHeartRateIdCount(getApplicationContext());
	    	    		Log.i(TAG, "data Count is : " +Integer.toString(dataCount));
	    	    		if(dataCount > 0){
	    	    				localData = Tools.getLastIDSyncHeartRate(getApplicationContext());
	    	    				Log.i(TAG, "Sending backup no : " +Integer.toString(localData));
	    	    				sendHeartRateBackup();
	    	    			}
	    	    		else
	    	    			Log.i(TAG, "Heartrate Backup not found!");
	    	        }
	    	    });
    //		}
    		
    	}
    	if(!Tools.isNetworkConnected(this)) {
    		if(Tools.getIsCountingState(getApplicationContext())==1);

        		
        	}
        // Release the wake lock provided by the BroadcastReceiver.
        BackupAlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }
        
    private void sendHeartRateBackup() {
		// TODO Auto-generated method stub
    	final String tId = Integer.toString(localData);
		AccountHandler account = new AccountHandler(this,
				new IConnectionResponseHandler() {
			
			
			
				@Override
				public void OnSuccessArray(JSONArray pResult){
					
				}
			
					@Override
					public void onSuccessJSONObject(String pResult) {
						Log.i(TAG, "sending HeartRate success");
						
						Tools.deleteSyncHeartRate(getApplicationContext(), tId);
						Log.i(TAG, "Delete db no : "+tId);
					}
					@Override
					public void onFailure(String e) {
						if(e != null){
	    					Log.e(TAG, e);
	    					Log.i(TAG, "Error sending db no : "+tId);
						}
						
					}
					@Override
					public void onSuccessJSONArray(String pResult) {
						// Ignore and do nothing
					}
				});
		heartRate = String.valueOf(Tools.getLastDataIntSyncHeartRate(getApplicationContext(), 1));
		speed = Tools.getLastDataStringSyncHeartRate(getApplicationContext(), 5);
		pace = Tools.getLastDataStringSyncHeartRate(getApplicationContext(), 6);
		temperature = Tools.getLastDataStringSyncHeartRate(getApplicationContext(), 7);
		uvlevel = Tools.getLastDataStringSyncHeartRate(getApplicationContext(), 8);
		startTime = Tools.getLastDataStringSyncHeartRate(getApplicationContext(), 2);
		endTime = Tools.getLastDataStringSyncHeartRate(getApplicationContext(), 3);
		date = Tools.getLastDataStringSyncHeartRate(getApplicationContext(), 4);
	/*	Log.d("TAG", "HR :"+heartRate);
		Log.d("TAG", "Speed :"+speed);
		Log.d("TAG", "Pace :"+pace);
		Log.d("TAG", "Temperature :"+temperature);
		Log.d("TAG", "UVLevel :"+uvlevel);
		Log.d("TAG", "sTime :"+startTime);
		Log.d("TAG", "eTime :"+endTime);
		Log.d("TAG", "Date :"+date);
		Log.d("TAG", "id :"+Account.getSingleton(this).getmGlobalID());
		Log.d("TAG", "idshesop :"+Account.getSingleton(this).getmShesopID()); */
		
		account.sendHeartrate(Account.getIdSteppy(getApplicationContext()), date, 
				startTime, endTime, heartRate, Account.getIdShesop(getApplicationContext()), pace,
				speed, temperature, uvlevel);
	
	}
}