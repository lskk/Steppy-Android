package org.lskk.shesop.steppy.receiver;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.lskk.shesop.steppy.data.TBAccount;
import org.lskk.shesop.steppy.connection.AccountHandler;
import org.lskk.shesop.steppy.connection.IConnectionResponseHandler;
import org.lskk.shesop.steppy.data.Account;
import org.lskk.shesop.steppy.utils.Record;
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
public class BackupService extends IntentService {
    public BackupService() {
        super("SchedulingService");
    }
    
    public static final String TAG = "Sending Backup Step Data ";
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
    
    private int recordCount;
    
    private int rowPos;
    private int idTodelete;
    private String id; 
    
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
	    	        	// sending step backup
	    	        	dataCount = Tools.getSyncStepIdCount(getApplicationContext());
	    	    		Log.i(TAG, "data Count is : " +Integer.toString(dataCount));
	    	    		if(dataCount > 0){
	    	    				localData = Tools.getLastIDSyncStep(getApplicationContext());
	    	    				Log.i(TAG, "Sending backup no : " +Integer.toString(localData));
	    	    				sendStepBackup();
	    	    			}
	    	    		else
	    	    			Log.i(TAG, "Step Backup not found!");
	    	    		
	    	    		// sending record backup
	    	    		recordCount = Record.getRecordCount(getApplicationContext());
	    	    		if(recordCount > 1){
	    	    			rowPos = 1;
	    	    		}
	    	    		if(recordCount <= 1)
	    	    			rowPos = 0;
	    	    		Log.i("Send Record", "data Count is : " +Integer.toString(recordCount));
	    	    		Log.i("Send Record", "row positin : " +Integer.toString(rowPos));
	    	    		
	    	    		if(recordCount > 1){
	    	    			sendRecordBackup();
	    	    		}
	    	    		else
	    	    			Log.i("Send Record", "No Record found");
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
						Log.i(TAG, "sending step success");
						
						Tools.deleteSyncStep(getApplicationContext(), tId);
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
		int step = Tools.getLastDataIntSyncStep(getApplicationContext(), 1);
		String steps = Integer.toString(step);
		int cal = Math.round(Float.parseFloat(Tools.getLastDataStringSyncStep(getApplicationContext(), 2)));
		String cals = Integer.toString(cal);
		String startTime = Tools.getLastDataStringSyncStep(getApplicationContext(), 3);
		String endTime = Tools.getLastDataStringSyncStep(getApplicationContext(), 4);
		String sensor = Tools.getLastDataStringSyncStep(getApplicationContext(), 5);
		String date = Tools.getLastDataStringSyncStep(getApplicationContext(), 6);
		String distance = Tools.getLastDataStringSyncStep(getApplicationContext(), 7);
		
		Log.d("Backup Service", "id : "+Account.getIdSteppy(getApplicationContext()));
		Log.d("Backup Service", "ids : "+Account.getIdShesop(getApplicationContext()));
		Log.d("Backup Service", "starttime : "+startTime);
		Log.d("Backup Service", "Endtime : "+endTime);
		Log.d("Backup Service", "Step : "+steps);
		Log.d("Backup Service", "Cal : "+cal);
		Log.d("Backup Service", "Dis : "+distance);
		Log.d("Backup Service", "Sensor : "+sensor);
		
		account.send(Account.getIdShesop(getApplicationContext()),
				Account.getIdShesop(getApplicationContext()), 
				date,startTime,endTime,steps, cals, sensor, distance); 
		
	/*	account.send(Account.getSingleton(getApplicationContext()).getmGlobalID(),
				Account.getSingleton(getApplicationContext()).getmShesopID(), 
		date,startTime,endTime,steps, cals, sensor, distance); */
	
	}
    
    
    
    private void sendRecordBackup(){
    	final String tId = Integer.toString(localData);
		AccountHandler account = new AccountHandler(this,
				new IConnectionResponseHandler() {
			
			
			
				@Override
				public void OnSuccessArray(JSONArray pResult){
					
				}
			
					@Override
					public void onSuccessJSONObject(String pResult) {
						Log.i("Game Record Backup", "sending record success");
						if(recordCount > 1)
							Record.deleteRecord(getApplicationContext(), String.valueOf(idTodelete));
					}
					@Override
					public void onFailure(String e) {
						if(e != null){
	    					Log.e("Game Record Backup", e);
	    					Log.i("Game Record Backup", "Error sending db no : "+id);
						}
						
					}
					@Override
					public void onSuccessJSONArray(String pResult) {
						// Ignore and do nothing
					}
				});
		String id = Record.getDetailRecord(getApplicationContext(), rowPos, 0);
		idTodelete = Integer.parseInt(id);
		idTodelete -= 1;
		
		String idShesop = String.valueOf(Account.getIdShesop(getApplicationContext()));
		String date = Record.getDetailRecord(getApplicationContext(), rowPos, 2);
		String mission1 = Record.getDetailRecord(getApplicationContext(), rowPos, 3);
		String mission2 = Record.getDetailRecord(getApplicationContext(), rowPos, 4);
		String mission3 = Record.getDetailRecord(getApplicationContext(), rowPos, 5);
		String mission4 = Record.getDetailRecord(getApplicationContext(), rowPos, 6);
		String level = Record.getDetailRecord(getApplicationContext(), rowPos, 7);
		String point = Record.getDetailRecord(getApplicationContext(), rowPos, 8);
		
		Log.d("Send Record", "id : "+id);
		Log.i("Send Record", "id to delete : "+idTodelete);
		Log.d("Send Record", "date : "+date);
		Log.d("Send Record", "Mission 1 : "+mission1);
		Log.d("Send Record", "Mission 2 : "+mission2);
		Log.d("Send Record", "Mission 3 : "+mission3);
		Log.d("Send Record", "Mission 4 : "+mission4);
		Log.d("Send Record", "Level : "+level);
		Log.d("Send Record", "Point : "+point);
		
		account.sendGameRecord(idShesop, 
				date, 
				mission1, 
				mission2, 
				mission3, 
				mission4, 
				level, 
				point);
	
    }
}
