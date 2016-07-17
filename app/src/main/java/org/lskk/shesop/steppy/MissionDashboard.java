package org.lskk.shesop.steppy;

import java.util.Timer;
import java.util.TimerTask;

import org.lskk.teamshesop.steppy.R;
import org.lskk.shesop.steppy.utils.Mission;
import org.lskk.shesop.steppy.utils.Record;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MissionDashboard extends Fragment {
	
	private TextView mission1Text;
	private TextView mission1Value;
	private TextView mission1State;
	
	private TextView mission2Text;
	private TextView mission2Value;
	private TextView mission2State;
	
	private TextView mission3Text;
	private TextView mission3Value;
	private TextView mission3State;
	
	private TextView mission4Text;
	private TextView mission4Value;
	private TextView mission4State;
	
	private TextView pointT;
	private TextView levelText;
	
	private int levelValue;
	private int rowPos;
	
	private int mission1Score;
	private int mission2Score;
	private int mission3Score;
	private int mission4Score;
	private int maxMissionScore;
	
	
	private ImageView icMission1;
	private ImageView icMission2;
	private ImageView icMission3;
	private ImageView icMission4;
	
	private checkTask myTask;
	private Timer checkTimer;
	
	private String completeString = "Complete";
	private String notCompleteString = "Not Complete";

	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.i("Mission Dashboard", "Call from destroy");
		myTask.cancel();
		this.checkTimer.cancel();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_mission, container, false);
		
		mission1Text = (TextView)rootView.findViewById(R.id.nameMission1);
		mission2Text = (TextView)rootView.findViewById(R.id.nameMission2);
		mission3Text = (TextView)rootView.findViewById(R.id.nameMission3);
		mission4Text = (TextView)rootView.findViewById(R.id.nameMission4);
		
		mission1Value = (TextView)rootView.findViewById(R.id.valueMission1);
		mission2Value = (TextView)rootView.findViewById(R.id.valueMission2);
		mission3Value = (TextView)rootView.findViewById(R.id.valueMission3);
		mission4Value = (TextView)rootView.findViewById(R.id.valueMission4);
		
		mission1State = (TextView)rootView.findViewById(R.id.stateMission1);
		mission2State = (TextView)rootView.findViewById(R.id.stateMission2);
		mission3State = (TextView)rootView.findViewById(R.id.stateMission3);
		mission4State = (TextView)rootView.findViewById(R.id.stateMission4);
		
		pointT = (TextView)rootView.findViewById(R.id.point);
		
		icMission1 = (ImageView)rootView.findViewById(R.id.iconmission1);
		icMission2 = (ImageView)rootView.findViewById(R.id.iconmission2);
		icMission3 = (ImageView)rootView.findViewById(R.id.iconmission3);
		icMission4 = (ImageView)rootView.findViewById(R.id.iconmission4);
		
		levelText = (TextView)rootView.findViewById(R.id.level);
		checkMissionStatus();
		myTask = new checkTask();
        
        checkTimer = new Timer();
        checkTimer.schedule(myTask, 30000, 30000);
		
		return rootView;
	}
	
	class checkTask extends TimerTask {
	    public void run() {
	    	android.os.Process
			.setThreadPriority(android.os.Process.THREAD_PRIORITY_DEFAULT);
	    	checkMissionStatus();
	    }
	}
	
	private void checkMissionStatus(){
		int cekLevel = Integer.parseInt(Record.getLastValueLevel(getActivity()));
		int cekMissionLevel = Integer.parseInt(Mission.getLastValueLevel(getActivity()));
		Log.i("Mission Dashboard", "cek current level "+cekLevel);
		Log.i("Mission Dashboard", "cek mission last data level "+cekMissionLevel);
		if(cekLevel != cekMissionLevel)
			setView();
		else if(cekLevel >= cekMissionLevel)
			setViewCompleteAllLevel();
	}
	
	
	private void setView() {
		
		getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	levelValue = Integer.parseInt(Record.getLastValueLevel(getActivity()));
        		
        		mission1Score = Integer.parseInt(Record.getLastValueMission1(getActivity()));
        		mission2Score = Integer.parseInt(Record.getLastValueMission2(getActivity()));
        		mission3Score = Integer.parseInt(Record.getLastValueMission3(getActivity()));
        		mission4Score = Integer.parseInt(Record.getLastValueMission4(getActivity()));
        		
        		maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
        		if(maxMissionScore == 4){
        			mission1Score = 0;
        			mission2Score = 0;
        			mission3Score = 0;
        			mission4Score = 0;
        		}
        		
        		rowPos = levelValue;
        		
        		levelText.setText("Level : "+levelValue);
        		pointT.setText(" | Point : "+Record.getLastValuePoint(getActivity()));
        		
        		mission1Value.setText(Mission.getDetailMission(getActivity(), rowPos, 2));
        		mission2Value.setText(Mission.getDetailMission(getActivity(), rowPos, 3));
        		mission3Value.setText(Mission.getDetailMission(getActivity(), rowPos, 4));
        		mission4Value.setText(Mission.getDetailMission(getActivity(), rowPos, 5));
        		
        		if(mission1Score == 1){
        			mission1State.setText(completeString);
        			mission1State.setBackgroundColor(Color.parseColor("#00a65a"));
        			icMission1.setBackgroundResource(R.drawable.step_2hrs_complete);
        		}
        		else{
        			mission1State.setText(notCompleteString);
        			icMission1.setBackgroundResource(R.drawable.step_2hrs_incomplete);
        			mission1State.setBackgroundColor(Color.parseColor("#d04545"));
        		}
        		if(mission2Score == 1){
        			mission2State.setText(completeString);
        			mission2State.setBackgroundColor(Color.parseColor("#00a65a"));
        			icMission2.setBackgroundResource(R.drawable.distance_2hrs_complete);
        		}
        		else{
        			mission2State.setText(notCompleteString);
        			icMission2.setBackgroundResource(R.drawable.distance_2hrs_incomplete);
        			mission2State.setBackgroundColor(Color.parseColor("#d04545"));
        		}
        		if(mission3Score == 1){
        			mission3State.setText(completeString);
        			mission3State.setBackgroundColor(Color.parseColor("#00a65a"));
        			icMission3.setBackgroundResource(R.drawable.step_day_complete);
        		}
        		else{
        			mission3State.setText(notCompleteString);
        			icMission3.setBackgroundResource(R.drawable.step_day_incomplete);
        			mission3State.setBackgroundColor(Color.parseColor("#d04545"));
        		}
        		if(mission4Score == 1){
        			mission4State.setText(completeString);
        			mission4State.setBackgroundColor(Color.parseColor("#00a65a"));
        			icMission4.setBackgroundResource(R.drawable.distance_day_complete);
        		}
        		else{
        			mission4State.setText(notCompleteString);
        			icMission4.setBackgroundResource(R.drawable.distance_day_incomplete);
        			mission4State.setBackgroundColor(Color.parseColor("#d04545"));
        		}
            }
        });
	}
	
	
	
private void setViewCompleteAllLevel() {
		
		getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	levelValue = Integer.parseInt(Record.getLastValueLevel(getActivity()));
            	levelText.setText("Level : "+levelValue);
        		mission1Score = Integer.parseInt(Record.getLastValueMission1(getActivity()));
        		mission2Score = Integer.parseInt(Record.getLastValueMission2(getActivity()));
        		mission3Score = Integer.parseInt(Record.getLastValueMission3(getActivity()));
        		mission4Score = Integer.parseInt(Record.getLastValueMission4(getActivity()));
        		
        		maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
        		if(maxMissionScore == 4){
        			
        		}
        		
        		rowPos = levelValue-1;
        		pointT.setText(" | Point : "+Record.getLastValuePoint(getActivity()));
        		mission1Value.setText(Mission.getDetailMission(getActivity(), rowPos, 2));
        		mission2Value.setText(Mission.getDetailMission(getActivity(), rowPos, 3));
        		mission3Value.setText(Mission.getDetailMission(getActivity(), rowPos, 4));
        		mission4Value.setText(Mission.getDetailMission(getActivity(), rowPos, 5));
        		
        		if(mission1Score == 1){
        			mission1State.setText(completeString);
        			mission1State.setBackgroundColor(Color.parseColor("#00a65a"));
        			icMission1.setBackgroundResource(R.drawable.step_2hrs_complete);
        		}
        		else{
        			mission1State.setText(notCompleteString);
        			icMission1.setBackgroundResource(R.drawable.step_2hrs_incomplete);
        			mission1State.setBackgroundColor(Color.parseColor("#d04545"));
        		}
        		if(mission2Score == 1){
        			mission2State.setText(completeString);
        			mission2State.setBackgroundColor(Color.parseColor("#00a65a"));
        			icMission2.setBackgroundResource(R.drawable.distance_2hrs_complete);
        		}
        		else{
        			mission2State.setText(notCompleteString);
        			icMission2.setBackgroundResource(R.drawable.distance_2hrs_incomplete);
        			mission2State.setBackgroundColor(Color.parseColor("#d04545"));
        		}
        		if(mission3Score == 1){
        			mission3State.setText(completeString);
        			mission3State.setBackgroundColor(Color.parseColor("#00a65a"));
        			icMission3.setBackgroundResource(R.drawable.step_day_complete);
        		}
        		else{
        			mission3State.setText(notCompleteString);
        			icMission3.setBackgroundResource(R.drawable.step_day_incomplete);
        			mission3State.setBackgroundColor(Color.parseColor("#d04545"));
        		}
        		if(mission4Score == 1){
        			mission4State.setText(completeString);
        			mission4State.setBackgroundColor(Color.parseColor("#00a65a"));
        			icMission4.setBackgroundResource(R.drawable.distance_day_complete);
        		}
        		else{
        			mission4State.setText(notCompleteString);
        			icMission4.setBackgroundResource(R.drawable.distance_day_incomplete);
        			mission4State.setBackgroundColor(Color.parseColor("#d04545"));
        		}
            }
        });
	}
	
	
	
	
}