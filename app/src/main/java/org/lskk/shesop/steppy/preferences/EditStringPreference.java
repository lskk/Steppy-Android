package org.lskk.shesop.steppy.preferences;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


abstract public class EditStringPreference extends EditTextPreference {
	boolean mIsMetric;
	
	protected int mTitleResource;
	protected int mMetricUnitsResource;
	protected int mImperialUnitsResource;
	
	public EditStringPreference(Context context) {
		super(context);
		initPreferenceDetails();
	}
	public EditStringPreference(Context context, AttributeSet attr) {
		super(context, attr);
		initPreferenceDetails();
	}
	public EditStringPreference(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		initPreferenceDetails();
	}
	
	abstract protected void initPreferenceDetails();
	
	protected void showDialog(Bundle state) {
		mIsMetric = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("units", "imperial").equals("metric");
		setDialogTitle(
				getContext().getString(mTitleResource) + 
				" (" + 
						getContext().getString(
								mIsMetric
								? mMetricUnitsResource 
								: mImperialUnitsResource) + 
				")"
		);
		
		try {
			getText();
		}
		catch (Exception e) {
			setText(" ");
		}
		
		super.showDialog(state);
	}
	protected void onAddEditTextToDialogView (View dialogView, EditText editText) {
		editText.setRawInputType(
				InputType.TYPE_CLASS_TEXT);
		super.onAddEditTextToDialogView(dialogView, editText);
	}
	
	public void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			try {
				((CharSequence)(getEditText().getText())).toString();
				Log.d("Preferencess", ((CharSequence)(getEditText().getText())).toString());
				
			}
			catch (NumberFormatException e) {
				this.showDialog(null);
				return;
			}
		}
		super.onDialogClosed(positiveResult);
	} 
}