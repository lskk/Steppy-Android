package org.lskk.shesop.steppy.preferences;

import org.lskk.teamshesop.steppy.R;
import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;


public class AgePreference extends EditMeasurementPreference {

	public AgePreference(Context context) {
		super(context);
	}
	public AgePreference(Context context, AttributeSet attr) {
		super(context, attr);
	}
	public AgePreference(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
	}

	protected void initPreferenceDetails() {
		mTitleResource = R.string.age_title;
		mMetricUnitsResource = R.string.year;
		mImperialUnitsResource = R.string.pounds;
	}
}

