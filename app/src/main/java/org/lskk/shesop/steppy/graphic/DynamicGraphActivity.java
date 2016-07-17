package org.lskk.shesop.steppy.graphic;

import org.achartengine.GraphicalView;
import org.lskk.teamshesop.steppy.R;

import android.app.Activity;
import android.os.Bundle;

public class DynamicGraphActivity extends Activity {

	private static GraphicalView view;
	private LineGraph line = new LineGraph();
	private static Thread thread;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.box_steps_weekly);
		thread = new Thread() {
			public void run()
			{
				for (int i = 0; i < 9999; i++) 
				{
					try {
						Thread.sleep(500);
					} 
					catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Point p = StepsData.getDataFromReceiver(i, getBaseContext()); // We got new data!
					line.addNewPoints(p); // Add it to our graph
					view.repaint();
				}
				
			}
		};
		thread.start();

	}

	@Override
	protected void onStart() {
		super.onStart();
		view = line.getView(this);
		setContentView(view);

	}

}