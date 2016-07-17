package org.lskk.shesop.steppy.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.preference.DialogPreference;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;

public class AccountPreference extends DialogPreference {
	
	private Context mContext;
	public static final String ACTION_LOGOUT_INTENT = "shesop.steppy.action.LOGOUT";

	public AccountPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.mContext = context;
	}
	
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		
		// If user want to logout
		if(positiveResult)
//			Tools.logout(mContext);
			((Activity)mContext).finish();
			LocalBroadcastManager.getInstance(mContext).sendBroadcast(
		            new Intent(ACTION_LOGOUT_INTENT));
			
			
			
//			setSummary(String.format(mContext.getResources()
//					.getString(R.string.pref_account_summary), "Logged out"));
//		else
//			setSummary(String.format(mContext.getResources()
//					.getString(R.string.pref_account_summary), "nothing"));
		
		persistBoolean(positiveResult);
	}
	
}
