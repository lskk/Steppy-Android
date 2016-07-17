package org.lskk.shesop.steppy.preferences;

import org.lskk.teamshesop.steppy.R;
import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;


public class NamePreference extends EditStringPreference {

	public NamePreference(Context context) {
		super(context);
	}
	public NamePreference(Context context, AttributeSet attr) {
		super(context, attr);
	}
	public NamePreference(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
	}

	protected void initPreferenceDetails() {
		mTitleResource = R.string.display_name_title;
		mMetricUnitsResource = R.string.name;
		mImperialUnitsResource = R.string.pounds;
	}
}

