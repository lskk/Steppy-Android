package org.lskk.shesop.steppy.utils;

import org.lskk.shesop.steppy.StepDashboard;
import org.lskk.shesop.steppy.HeartRateDashboard;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new StepDashboard();
	/*	case 1:
			// Games fragment activity
			return new HeartRateDashboard(); */
		} 

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 1;
	}

}
