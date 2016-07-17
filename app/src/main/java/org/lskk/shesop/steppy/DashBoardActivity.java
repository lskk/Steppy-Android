package org.lskk.shesop.steppy;
import java.io.File;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;

import android.app.ActionBar.Tab;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import org.lskk.shesop.steppy.receiver.BackupAlarmBandReceiver;
import org.lskk.shesop.steppy.receiver.BackupAlarmReceiver;
import org.lskk.shesop.steppy.utils.TabsPagerAdapter;
import org.lskk.shesop.steppy.utils.Tools;

import org.lskk.teamshesop.steppy.R;




public class DashBoardActivity extends FragmentActivity {
	
	/**
	 * Tag string info for this fragment
	 */
	private static final String TAG = "MainActivity";
	
	/**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 1;

    /**
     * The pager widget, which handles animation and allows swiping horizontally 
     * to access previous and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private ShareActionProvider mShareActionProvider;
	
	 private AlarmManager alarmMgr;
	 private PendingIntent alarmIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pager);
        
        
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
	}

	 @Override
	    protected void onResume() {
	    	Log.i(TAG, "[ACTIVITY] onResume");
	    	super.onResume();
	    	
	    }
	    
	    @Override
	    protected void onPause() {
	    	Log.i(TAG, "[ACTIVITY] onPause");
	    	super.onPause();
	    }
	    
	    @Override
	    protected void onStop() {
	    	Log.i(TAG, "[ACTIVITY] onStop");
	    	super.onStop();
	    }
	    
	    @Override
	    protected void onDestroy() {
	    	Log.i(TAG, "[ACTIVITY] onDestroy");
	    	super.onDestroy();
	    }
	
	
	    
	    
	    
	    
	    
	    
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	// Inflate the menu 
    	// this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

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
	        case R.id.settings:
	            startActivity(new Intent(this,Preferences.class));
	            finish();
	        	//startActivity(new Intent(this,ShowLocalStep.class));
	            break;
	        case R.id.calibrate:
	        	if(Tools.getIsCountingState(getApplicationContext())==0 || Tools.getIsCountingState(getApplicationContext())==2 )
	        		startActivity(new Intent(this,CalibrateActivity.class));
	        	else if(Tools.getIsCountingState(getApplicationContext())==1)
	        		Toast.makeText(DashBoardActivity.this, "Steppy is counting, stop counting to start Calibrate", Toast.LENGTH_SHORT).show();
	    	    break;
	        case R.id.about:
	        	 startActivity(new Intent(this,AboutActivity.class));
	    	    break;
	        case R.id.logout:
	        	if(Tools.getIsCountingState(getApplicationContext())==0){
		        	Intent myIntent = new Intent(DashBoardActivity.this, Splash.class);
		            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back stack
		        	clearApplicationData(DashBoardActivity.this);
		        	startActivity(myIntent);
		        	Toast.makeText(DashBoardActivity.this, "Logout Success", Toast.LENGTH_SHORT).show();
		        	
		        	alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		            Intent intent = new Intent(this, BackupAlarmBandReceiver.class);
		            alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		        	alarmMgr.cancel(alarmIntent);
		        	  
		            finish();
	        	} else
	        		Toast.makeText(DashBoardActivity.this, "Stop Counting Step first to Signout", Toast.LENGTH_LONG).show();
	    	    break;
	        case R.id.action_share:
	        	String steps = String.valueOf(Tools.getTodayStep(DashBoardActivity.this));
	        	String calories = String.valueOf(Tools.getTodayCalorie(DashBoardActivity.this));
	        	String distances = String.format("%.2f", Tools.getTodayDistance(DashBoardActivity.this));
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
	
    
    
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(mPager.getCurrentItem() <= 1) {
	    	StepDashboard dahsboard = (StepDashboard) mPagerAdapter.instantiateItem(mPager, 0);
	        dahsboard.onActivityResult(requestCode, resultCode, data);
    	}    	
    }
    
    
    /**
     * A simple pager adapter that represents 3 {@link ScreenSlidePageFragment} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return StepDashboard.create(position);
				default:
					return null;
				}
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
        
    }
    
    
    
    
    
    
    
    
	
	
}