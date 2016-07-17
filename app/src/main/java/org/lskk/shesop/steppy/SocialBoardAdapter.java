package org.lskk.shesop.steppy;

import java.util.List;

import org.lskk.shesop.steppy.data.Friend;
import org.lskk.teamshesop.steppy.R;

import android.content.Context;
import android.widget.ArrayAdapter;

public abstract class SocialBoardAdapter extends ArrayAdapter<Friend> {
	
	protected Context mContext;
	protected List<Friend> dataObject;

	public SocialBoardAdapter(Context context, List<Friend> pObject) {
		super(context, R.layout.fragment_social_board, pObject);
		
		this.mContext = context;
		this.dataObject = pObject;
	}
	
	public void swapObject(List<Friend> pObject) {
	    this.dataObject = pObject;
	    notifyDataSetChanged();
	}
}
