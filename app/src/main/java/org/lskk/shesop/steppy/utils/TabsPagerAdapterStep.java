package org.lskk.shesop.steppy.utils;

import org.lskk.shesop.steppy.DashboardFragment;
import org.lskk.shesop.steppy.HeartRateDashboard;
import org.lskk.shesop.steppy.MissionDashboard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapterStep extends FragmentPagerAdapter {

	public TabsPagerAdapterStep(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new DashboardFragment();
		case 1:
			// Games fragment activity
			return new MissionDashboard(); 
		} 

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 2;
	}

}
