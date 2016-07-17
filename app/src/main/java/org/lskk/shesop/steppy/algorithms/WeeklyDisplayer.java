package org.lskk.shesop.steppy.algorithms;

import java.util.ArrayList;

import org.lskk.shesop.steppy.utils.Utils;

public class WeeklyDisplayer implements StepListener, SpeakingTimer.Listener {
	
	private int mWeekly = 0;
    PedometerSettings mSettings;
    Utils mUtils;
    
    public WeeklyDisplayer(PedometerSettings settings, Utils utils) {
    	mUtils = utils;
        mSettings = settings;
        notifyListener();
    }
    
    public void setUtils(Utils utils) {
        mUtils = utils;
    }

    public void setSteps(int pWeekly) {
        mWeekly = pWeekly;
        notifyListener();
    }

	@Override
	public void onStep() {
		mWeekly++;
        notifyListener();
	}

	@Override
	public void passValue() {
		// TODO Auto-generated method stub
		
	}
	
	public void reloadSettings() {
        notifyListener();
    }
	
	//-----------------------------------------------------
    // Listener
    
    public interface Listener {
        public void stepsChanged(int value);
        public void passValue();
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }
    public void notifyListener() {
        for (Listener listener : mListeners) {
            listener.stepsChanged((int)mWeekly);
        }
    }
    
    //-----------------------------------------------------
    // Speaking
    
    public void speak() {
    	// Do nothing
    }

}
