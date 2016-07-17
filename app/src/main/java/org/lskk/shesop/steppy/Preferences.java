package org.lskk.shesop.steppy;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lskk.shesop.steppy.connection.AccountHandler;
import org.lskk.shesop.steppy.connection.IConnectionResponseHandler;
import org.lskk.shesop.steppy.data.Account;
import org.lskk.shesop.steppy.preferences.AccountPreference;
import org.lskk.shesop.steppy.receiver.BackupAlarmBandReceiver;
import org.lskk.shesop.steppy.utils.Mission;
import org.lskk.shesop.steppy.utils.Tools;
import org.lskk.teamshesop.steppy.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Preferences extends PreferenceActivity {
	
	
	private AccountPreference mAccountPref;

	private Preference mLogout;
	private Preference mUpdate;
	private PreferenceCategory stepCategory;
	private PreferenceCategory speakCategory;
	private PreferenceCategory syncCategory;
	SharedPreferences mSettings;
	
	private String mName;
	private String mAge;
	private String mHeight;
	private String mWeight;
	
	
	private SharedPreferences mState;
    private SharedPreferences.Editor mStateEditor;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		
		mState = PreferenceManager.getDefaultSharedPreferences(this);
        mStateEditor = PreferenceManager.getDefaultSharedPreferences(Preferences.this).edit();
		
		String uname = Account.getAccountEmail(getApplicationContext());
		mSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		mName = mSettings.getString("display_name", "Steppy");
		mAge =	mSettings.getString("age", "17").trim(); 
		mHeight = mSettings.getString("body_height", "167").trim();
		mWeight = mSettings.getString("body_weight", "50").trim();
		
		mAccountPref = (AccountPreference) getPreferenceScreen().
				findPreference(getResources().getString(R.string.pref_account_key));
		
		mAccountPref.setSummary(String.format(getResources().
				getString(R.string.pref_account_summary), uname));
		
		mLogout = (Preference) getPreferenceScreen().
				findPreference("logout");
		
		mUpdate = (Preference) getPreferenceScreen().
				findPreference("update_pref");
		
		mUpdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				
				
				return false;
			};
		});
		
		mLogout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if(Tools.getIsCountingState(getApplicationContext())==0){
		        	Intent myIntent = new Intent(Preferences.this, Splash.class);
		            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
		        	clearApplicationData(Preferences.this);
		        	startActivity(myIntent);
		        	Toast.makeText(Preferences.this, "Logout Success", Toast.LENGTH_SHORT).show();  
		            finish();
	        	} else
	        		Toast.makeText(Preferences.this, "Stop Counting Step first to Signout", Toast.LENGTH_LONG).show();
				return false;
			}
		});
		
		stepCategory = (PreferenceCategory) findPreference("stepPref");
		speakCategory = (PreferenceCategory) findPreference("speakPref");
		syncCategory = (PreferenceCategory) findPreference("syncPref");
		
		PreferenceScreen preferenceScreen = getPreferenceScreen();
		preferenceScreen.removePreference(stepCategory);
		preferenceScreen.removePreference(speakCategory);
		preferenceScreen.removePreference(syncCategory);
		
	//	PreferenceScreen preferenceSpeak = (PreferenceScreen) findPreference(getResources().getString(R.string.tell_what));
	//	preferenceSpeak.removeAll();
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
	
	
	
    @Override
    public void onBackPressed() {
       Log.d("CDA", "onBackPressed Called");
       super.onBackPressed();
       
       mStateEditor = mState.edit();
		mStateEditor.putString("display_name", mName);
		mStateEditor.putString("age", mAge);
		mStateEditor.putString("body_height", mHeight);
		mStateEditor.putString("body_weight", mWeight);
		mStateEditor.commit();
       startActivity(new Intent(this,DashBoardActivity.class));
       finish();
    }
	
    @Override
    public void onDestroy(){
    	super.onDestroy();
    }

    public void updatePrefs(View v){
    	Handler mHandler = new Handler(getMainLooper());
	    mHandler.post(new Runnable() {
	        @Override
	        public void run() {
	        	sendUpdate();
	        }
	    });
	        
    }
    
    
    private void sendUpdate(){
    	AccountHandler account = new AccountHandler(this,
				new IConnectionResponseHandler() {
				
					@Override
					public void onSuccessJSONObject(String pResult) {
						
						Account.updateAccount(getApplicationContext(), "1", 
								mSettings.getString("display_name", "Steppy"),
								mSettings.getString("age", "17").trim(), 
								mSettings.getString("body_height", "167").trim(), 
								mSettings.getString("body_weight", "50").trim());
						Toast.makeText(Preferences.this, "Updating account Success", Toast.LENGTH_SHORT).show();
						Intent myIntent = new Intent(Preferences.this, DashBoardActivity.class);
						startActivity(myIntent);
					       finish();
				    	
						
					}
					@Override
					public void OnSuccessArray(JSONArray pResult){
						// Toast.makeText(getActivity(), pResult.toString(), Toast.LENGTH_LONG).show();	
						
					}
					@Override
					public void onFailure(String e) {
						Toast.makeText(Preferences.this, "Error updating account. Check your connection", Toast.LENGTH_LONG).show();	
					}
					@Override
					public void onSuccessJSONArray(String pResult) {
						
					}
				});
   /* 	Log.d("Preferences", "ids : "+Account.getIdShesop(Preferences.this));
    	Log.d("Preferences", "display : "+mSettings.getString("display_name", "Steppy"));
    	Log.d("Preferences", "age : "+mSettings.getString("age", "17").trim());
    	Log.d("Preferences", "height : "+mSettings.getString("body_height", "167"));
    	Log.d("Preferences", "weight : "+mSettings.getString("body_weight", "50").trim());
    	Log.d("Preferences", "gender : "+Account.getAccountGender(Preferences.this));
    	Log.d("Preferences", "telp : "+Account.getAccountTelpNumber(Preferences.this)); */
    	
		account.updateAccount(Account.getIdShesop(Preferences.this), 
				mSettings.getString("display_name", "Steppy"), 
				mSettings.getString("age", "17").trim(), 
				mSettings.getString("body_height", "167"), 
				mSettings.getString("body_weight", "50").trim(), 
				Account.getAccountGender(Preferences.this), 
				Account.getAccountTelpNumber(Preferences.this));
    }
}
