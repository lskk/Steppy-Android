package org.lskk.shesop.steppy;

import java.util.List;

import org.lskk.shesop.steppy.data.Friend;
import org.lskk.teamshesop.steppy.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SocialBoardGlobalAdapter extends SocialBoardAdapter {

	public SocialBoardGlobalAdapter(Context context, List<Friend> pData) {
		super(context, pData);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
		View rowView = inflater.inflate(R.layout.fragment_social_board, parent, false);
		
//		ImageView tProfpict = (ImageView) rowView.findViewById(R.id.sb_profpict);
		TextView tName		= (TextView) rowView.findViewById(R.id.sb_name);
		
		tName.setText(dataObject.get(position).getName());
		    
		return rowView;
	}

}
