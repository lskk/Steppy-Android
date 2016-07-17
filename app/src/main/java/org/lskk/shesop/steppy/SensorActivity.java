package org.lskk.shesop.steppy;

import org.lskk.shesop.steppy.graphic.DynamicGraphActivity;
import org.lskk.shesop.steppy.receiver.SyncAlarmReceiver;
import org.lskk.shesop.steppy.utils.Tools;
import org.lskk.teamshesop.steppy.R;

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

public class SensorActivity extends FragmentActivity {
	
	/**
	 * Tag string info for this fragment
	 */
	private static final String TAG = "SensorActivity";
	
	/**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 2;

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

    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pager);
        
        if(!Tools.isLogin(SensorActivity.this)) {
        	Tools.toActivity(getApplicationContext(), LoginActivity.class, true);
        }
        
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        
//        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//			@Override
//            public void onPageSelected(int position) {
//                // When changing pages, reset the action bar actions since they are dependent
//                // on which page is currently active. An alternative approach is to have each
//                // fragment expose actions itself (rather than the activity exposing actions),
//                // but for simplicity, the activity provides the actions in this sample.
//                invalidateOptionsMenu();
//            }
//        });
        
        SyncAlarmReceiver alarm = new SyncAlarmReceiver();
        alarm.setAlarm(this);
    }
    
    @Override
    protected void onResume() {
    	Log.i(TAG, "[ACTIVITY] onResume");
    	super.onResume();
    	
    	if(!Tools.isLogin(SensorActivity.this)) {
    		Tools.toActivity(SensorActivity.this, LoginActivity.class, true);
        }
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
		getMenuInflater().inflate(R.menu.dashboard, menu);

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
	            break;
	        case R.id.action_main:
	        	startActivity(new Intent(this,MainActivity.class));
	    	    break;
	        case R.id.action_graph:
	        	startActivity(new Intent(this,DynamicGraphActivity.class));
	    	    break;
	        case R.id.action_maps:
	        	startActivity(new Intent(this,MapsActivity.class));
	    	    break;
	        case R.id.action_share:
	        	Intent sendIntent = new Intent();
	    	    sendIntent.setAction(Intent.ACTION_SEND);
	    	    sendIntent.putExtra(Intent.EXTRA_TEXT, "http://shesop.lskk.ee.itb.ac.id");
	    	    sendIntent.setType("text/plain");
	    	    startActivity(sendIntent);
	            return true;
	    }
    	return super.onMenuItemSelected(featureId, item);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(mPager.getCurrentItem() <= 1) {
	    	SensorFragment dahsboard = (SensorFragment) mPagerAdapter.instantiateItem(mPager, 0);
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
					return SensorFragment.create(position);
				case 1:
					return SocialBoardFragment.create(position);
				case 2:
					return SocialBoardFragment.create(position);
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