package org.lskk.shesop.steppy;

import java.util.List;

import org.lskk.shesop.steppy.data.Friend;
import org.lskk.teamshesop.steppy.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SocialBoardLocalAdapter extends SocialBoardAdapter {

	public SocialBoardLocalAdapter(Context context, List<Friend> pData) {
		super(context, pData);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
		View rowView = inflater.inflate(R.layout.fragment_social_board, parent, false);
		
//		ImageView tProfpict = (ImageView) rowView.findViewById(R.id.sb_profpict);
		TextView tName		= (TextView) rowView.findViewById(R.id.sb_name);
		TextView tTodayInfo	= (TextView) rowView.findViewById(R.id.sb_today_step_info);
		TextView tHighScore	= (TextView) rowView.findViewById(R.id.sb_high_score_info);
		
		tName.setText(dataObject.get(position).getName());
		
		tTodayInfo.setText(String.format(mContext.getResources().getString(
				R.string.today_step_info), String.valueOf(dataObject.get(position).getTodayStep()),
				String.valueOf(dataObject.get(position).getWeeklyStep())));
		
		tHighScore.setText(String.format(mContext.getResources().getString(
				R.string.sb_high_score), String.valueOf(dataObject.get(position).getHighScore()),
				String.valueOf(dataObject.get(position).getLevel())));
		    
		return rowView;
	}
	
}
