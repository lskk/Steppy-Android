package org.lskk.shesop.steppy;

import java.io.File;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.textservice.SpellCheckerService.Session;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import org.lskk.shesop.steppy.receiver.BackupAlarmReceiver;
import org.lskk.shesop.steppy.receiver.SyncAlarmReceiver;
import org.lskk.shesop.steppy.utils.TabsPagerAdapter;
import org.lskk.shesop.steppy.utils.TabsPagerAdapterStep;
import org.lskk.shesop.steppy.utils.Tools;

import org.lskk.teamshesop.steppy.R;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	
	private ViewPager viewPager;
	private TabsPagerAdapterStep mAdapter;
	private ActionBar actionBar;
	// Tab titles
	// private String[] tabs = { "Step", "Heart Rate"};
	private String[] tabs = { "Step", "Mission"};
	private ShareActionProvider mShareActionProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_dashboard);
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapterStep(getSupportFragmentManager());

	       SyncAlarmReceiver alarm = new SyncAlarmReceiver();
	        BackupAlarmReceiver sAlarm = new BackupAlarmReceiver();
	       // SendAlarmReceiver nAlrm = new SendAlarmReceiver();
	       // nAlrm.setAlarm(this);
	        alarm.setAlarm(this);
	        sAlarm.setAlarm(this);
	        
		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}
		
		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
	
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	
	    	DashboardFragment dahsboard = new DashboardFragment();
	        dahsboard.onActivityResult(requestCode, resultCode, data);
    	
    
    } 
    
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}    
}