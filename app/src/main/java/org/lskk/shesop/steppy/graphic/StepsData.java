package org.lskk.shesop.steppy.graphic;

import org.lskk.shesop.steppy.data.TBMeasurement;

import android.content.Context;
import android.database.Cursor;

public class StepsData {
	
	public static Point getDataFromReceiver(int x, Context Context)
	{
		return new Point(x, generateSteppyData(Context));
		
	}
	
	private static int generateSteppyData(Context context) {
		
		int tResult = 0;
		TBMeasurement tbMeasurement = new TBMeasurement(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getStepsData();

		if (tCursor != null) {
			tResult = tCursor.getInt(0);

			tCursor.close();
		}
		tbMeasurement.close();

		return tResult;

	}

	public static Point getDataFromReceiver(int i, Point tResult) {
		// TODO Auto-generated method stub
		return tResult;
	}
	}
