package org.lskk.shesop.steppy.receiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.lskk.shesop.steppy.data.TBAccount;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lskk.shesop.steppy.DashboardFragment;
import org.lskk.shesop.steppy.algorithms.StepService;
import org.lskk.shesop.steppy.connection.AccountHandler;
import org.lskk.shesop.steppy.connection.IConnectionResponseHandler;
import org.lskk.shesop.steppy.data.Account;
import org.lskk.shesop.steppy.data.TBAccount;
import org.lskk.shesop.steppy.data.TBMeasurement;
import org.lskk.shesop.steppy.utils.Tools;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SendService extends IntentService {
    public SendService() {
        super("SchedulingService");
    }
    
    public static final String TAG = "Sending Step Data ";
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
    private int mStepValue;
    private int decStepValue;
    SharedPreferences mSettings;

    
    @Override
    protected void onHandleIntent(Intent intent) {
        // BEGIN_INCLUDE(service_onhandle)
    		
    	mSettings = PreferenceManager.getDefaultSharedPreferences(this);
    	// Synchronizing measurement data to server
    	if(Tools.isNetworkConnected(this)) {
    		if(Tools.getIsCountingState(getApplicationContext())==1){
	    		Handler mHandler = new Handler(getMainLooper());
	    	    mHandler.post(new Runnable() {
	    	        @Override
	    	        public void run() {
	    	    	//	sendStep();
	    	    		saveToLocal();
	    	        }
	    	    });
    		}
    		
    	}
    	if(!Tools.isNetworkConnected(this)) {
    		if(Tools.getIsCountingState(getApplicationContext())==1)
        		saveToLocal();
        		
        	}
        // Release the wake lock provided by the BroadcastReceiver.
        SendAlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }
        
    
    
    @SuppressLint({ "DefaultLocale", "SimpleDateFormat" })
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
}
