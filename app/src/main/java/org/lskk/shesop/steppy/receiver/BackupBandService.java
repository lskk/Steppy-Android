package org.lskk.shesop.steppy.receiver;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.lskk.shesop.steppy.data.TBAccount;
import org.lskk.shesop.steppy.connection.AccountHandler;
import org.lskk.shesop.steppy.connection.IConnectionResponseHandler;
import org.lskk.shesop.steppy.data.Account;
import org.lskk.shesop.steppy.utils.Tools;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class BackupBandService extends IntentService {
    public BackupBandService() {
        super("SchedulingService");
    }
    
    public static final String TAG = "Sending Backup Step Band Data ";
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
	    	        	dataCount = Tools.getSyncStepBandIdCount(getApplicationContext());
	    	    		Log.i(TAG, "data Count is : " +Integer.toString(dataCount));
	    	    		if(dataCount > 0){
	    	    				localData = Tools.getLastIDSyncStepBand(getApplicationContext());
	    	    				Log.i(TAG, "Sending backup no : " +Integer.toString(localData));
	    	    				sendStepBackup();
	    	    			}
	    	    		else
	    	    			Log.i(TAG, "Step Backup not found!");
	    	        }
	    	    });
    	//	}
    		
    	}
    	if(!Tools.isNetworkConnected(this)) {
    		if(Tools.getIsCountingState(getApplicationContext())==1);

        		
        	}
        // Release the wake lock provided by the BroadcastReceiver.
        BackupAlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }
    
    private void sendStepBackup() {
		// TODO Auto-generated method stub
    	final String tId = Integer.toString(localData);
		AccountHandler account = new AccountHandler(this,
				new IConnectionResponseHandler() {
			
			
			
				@Override
				public void OnSuccessArray(JSONArray pResult){
					
				}
			
					@Override
					public void onSuccessJSONObject(String pResult) {
						Log.i(TAG, "sending step band success");
						
						Tools.deleteSyncStepBand(getApplicationContext(), tId);
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
		int step = Tools.getLastDataIntSyncStepBand(getApplicationContext(), 1);
		String steps = Integer.toString(step);
		String cals = Tools.getLastDataStringSyncStepBand(getApplicationContext(), 2);
		String distance = Tools.getLastDataStringSyncStepBand(getApplicationContext(), 5);
		
		String startTime = Tools.getLastDataStringSyncStepBand(getApplicationContext(), 3);
		String endTime = Tools.getLastDataStringSyncStepBand(getApplicationContext(), 4);
		String date = Tools.getLastDataStringSyncStepBand(getApplicationContext(), 6);
		Log.d("SEND BAND STEP BACKUP", "id : "+Account.getIdSteppy(getApplicationContext()));
		Log.d("SEND BAND STEP BACKUP", "idshesop : "+Account.getIdShesop(getApplicationContext()));
		Log.d("SEND BAND STEP BACKUP", "step : "+steps);
		Log.d("SEND BAND STEP BACKUP", "CAL : "+cals);
		Log.d("SEND BAND STEP BACKUP", "distance : "+distance);
		Log.d("SEND BAND STEP BACKUP", "sTime: "+startTime);
		Log.d("SEND BAND STEP BACKUP", "sTime: "+endTime);
		Log.d("SEND BAND STEP BACKUP", "date: "+date);
		
		account.sendBand(Account.getIdSteppy(getApplicationContext()),
				Account.getIdShesop(getApplicationContext()), 
				date,startTime,endTime,steps, cals, distance);
	
	}
}
