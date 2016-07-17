package org.lskk.shesop.steppy.receiver;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lskk.shesop.steppy.connection.AccountHandler;
import org.lskk.shesop.steppy.connection.IConnectionResponseHandler;
import org.lskk.shesop.steppy.data.Account;
import org.lskk.shesop.steppy.data.TBMeasurement;
import org.lskk.shesop.steppy.utils.Tools;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SyncService extends IntentService {
    public SyncService() {
        super("SchedulingService");
    }
    
    public static final String TAG = "Synchronizing Data";
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

    @Override
    protected void onHandleIntent(Intent intent) {
        // BEGIN_INCLUDE(service_onhandle)
    	
    	// Synchronizing measurement data to server
    	if(Tools.isNetworkConnected(this)) {
    	//	syncMeasurementData();
    	}
    	
    	
    	
    	
    	
    	
    	
    	
    	
        // Release the wake lock provided by the BroadcastReceiver.
        SyncAlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }
    
//    // Post a notification indicating whether a doodle was found.
//    private void sendNotification(String msg) {
//        mNotificationManager = (NotificationManager)
//               this.getSystemService(Context.NOTIFICATION_SERVICE);
//    
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//            new Intent(this, MainActivity.class), 0);
//
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//        .setSmallIcon(R.drawable.ic_launcher)
//        .setContentTitle(getString(R.string.app_name))
//        .setStyle(new NotificationCompat.BigTextStyle()
//        .bigText(msg))
//        .setContentText(msg);
//
//        mBuilder.setContentIntent(contentIntent);
//        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
//    }
    
    private void syncMeasurementData() {
    	final ArrayList<String> tID = new ArrayList<String>();
    	
    	JSONArray jArray = new JSONArray();
    	
    	final TBMeasurement tbMeasurement = new TBMeasurement(this);
    	tbMeasurement.open();
    	Cursor tCursor = tbMeasurement.getDataToSync();
    	
    	
    	if (tCursor != null) {
    		do {
        		try{
    	    		JSONObject jObject = new JSONObject();
    	    		jObject.put("Value", tCursor.getString(
    	    				tCursor.getColumnIndex(TBMeasurement.COL_VALUE)));
    	    		jObject.put("TimeStamp", Tools.dateToMillis(
    	    				tCursor.getString(tCursor.getColumnIndex(TBMeasurement.COL_DATE)),
    	    				"yyyy-MM-dd HH:mm:ss.SSS"));
    	    		jObject.put("MeasurementType", tCursor.getString(
    	    				tCursor.getColumnIndex(TBMeasurement.COL_MEASUREMENT_TYPE)));
    	    		
    	    		jArray.put(jObject);
    	    		tID.add(tCursor.getString(
    	    				tCursor.getColumnIndex(TBMeasurement.COL_ID)));
        		} catch (JSONException e) {
        			e.printStackTrace();
        		}
        	} while (tCursor.moveToNext());
    		tCursor.close();
    		
    		AccountHandler account = new AccountHandler(this, 
        			new IConnectionResponseHandler() {
    			
    			
    			@Override
				public void OnSuccessArray(JSONArray pResult){
					
				}
    			
    			@Override
    			public void onSuccessJSONObject(String pResult) {
    				tbMeasurement.updateAfterSync(tID);
    			}
    			
    			@Override
    			public void onSuccessJSONArray(String pResult) {
//    				tbMeasurement.updateAfterSync(tID);
    			}
    			
    			@Override
    			public void onFailure(String e) {
    				if(e != null)
    					Log.e(TAG, e);
    			}
    		});
    		
        //	account.syncMeasurement(Account.getSingleton(this).getToken(), 
        	//		jArray);                	
    		
    	}
		
		tbMeasurement.close();
    }
 
}
