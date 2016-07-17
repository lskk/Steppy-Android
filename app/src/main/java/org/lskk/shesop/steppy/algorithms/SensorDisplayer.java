package org.lskk.shesop.steppy.algorithms;

import java.util.ArrayList;


public class SensorDisplayer implements SensorListener {
	
	@Override
	public void onSensorChanged(float value) {
		notifyListener(value);		
	}
	
	@Override
	public void passValue() {
		// TODO Auto-generated method stub
		
	}
	
	//-----------------------------------------------------
    // Listener
    
    public interface Listener {
        public void sensorChanged(float value);
        public void passValue();
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }
    
    public void notifyListener(float value) {
        for (Listener listener : mListeners) {
            listener.sensorChanged(value);
        }
    }

}
