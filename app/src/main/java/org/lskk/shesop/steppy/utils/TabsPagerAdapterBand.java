package org.lskk.shesop.steppy.utils;

import org.lskk.shesop.steppy.MissionDashboard;
import org.lskk.shesop.steppy.StepDashboard;
import org.lskk.shesop.steppy.HeartRateDashboard;
import org.lskk.shesop.steppy.msband.HeartRateDashboardBand;
import org.lskk.shesop.steppy.msband.StepDashboardBand;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapterBand extends FragmentPagerAdapter {

	public TabsPagerAdapterBand(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new StepDashboardBand();
		case 1:
			// Games fragment activity
			return new HeartRateDashboardBand();
		case 2:
			return new MissionDashboard();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}
