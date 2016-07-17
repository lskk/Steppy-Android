package org.lskk.shesop.steppy.data;

import java.util.ArrayList;
import java.util.Calendar;

import org.lskk.shesop.steppy.utils.Tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TBMeasurementBand {
	
	public static final String TAG					= "Table Measurement band";
	
	public static final String TABLE_MEASUREMENT_BAND 	= "measurement_band";
	
	public static final String COL_ID 				= "_id";
	public static final String COL_STEP			= "value";
	public static final String COL_DISTANCE			= "distance";
	public static final String COL_CALORIE			= "calorie";
	public static final String COL_DATE				= "date";
	public static final String COL_MEASUREMENT_TYPE	= "type";
	
	private SQLiteDatabase db;
	private DBLocalHelper dbHelper;
	private String lastStep ="0";
	
	// Database creation sql statement
	public static final String INITIAL_CREATE = "create table "
			+ TABLE_MEASUREMENT_BAND + "(" + COL_ID + " integer primary key autoincrement, "
			+ COL_STEP + " varchar(30) not null, "
			+ COL_DISTANCE + " varchar(30) not null, "
			+ COL_CALORIE + " varchar(30) not null, "
			+ COL_MEASUREMENT_TYPE +  " varchar(3) not null, "
			+ COL_DATE +  " varchar(50) not null); ";
	
	public TBMeasurementBand(Context context) {
		this.dbHelper = new DBLocalHelper(context);
	}
	
	public void open() {
		this.db = dbHelper.getWritableDatabase();
		this.db = dbHelper.getReadableDatabase();
	}
	
	public void close() {
		this.dbHelper.close();
	}
	
	public long insert(ContentValues values) {
		Log.w(TAG, "inserting to table measurement");
		
		Long result = db.insert(TABLE_MEASUREMENT_BAND, null, values);
		
		if(result != -1) {
			Log.i(TAG, "inserting value "+ values.getAsString(TBMeasurementBand.COL_STEP) +" to table measurementband succeed");
			lastStep = values.getAsString(TBMeasurementBand.COL_STEP);
		} else {
			Log.e(TAG, "inserting to table measurementband failed");
		}
		
		return result;
	}
	
	public String getLastSteps(){
		String mlastStep;
		mlastStep = lastStep;
        return mlastStep;
    }
	
	public int update(String tID, ContentValues values) {		
		if(tID == null) {
			Log.e(TAG, "updating to table measurement failed, no ID");
			return 0;
		}
		
		int result = db.update(TABLE_MEASUREMENT_BAND, values, COL_ID + "=?", new String[] {tID});
		
		if(result > 0) {
			Log.i(TAG, "updating to table measurement succeed");
		} else {
			Log.e(TAG, "updating to table measurement failed");
		}
		
		return result;
	}
	
	
	public Cursor getTodayStep() {
		Log.i(TAG, "Getting today step data");
		
		String startDate = Tools.getCurrentTime("yyyy-MM-dd") + " 00:00:00.000";
		
		String[] projection = new String[] {"SUM("+COL_STEP+")"};
		
		Cursor c = db.query(TABLE_MEASUREMENT_BAND, projection, COL_DATE + " >= ? and "
				+ COL_MEASUREMENT_TYPE + " =? ",new String[] {startDate, 
				TBMeasurementType.TYPE_STEP_MINUTES},null, null, null);
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got "+c.getString(0)+" today step value");
			return c;
		}
		
		Log.i(TAG, "No data found");
		return null;
	}
	
	public Cursor getTwoHoursSteps() {
		Log.i(TAG, "Getting two hours earlier step data");
		
		String endTime = 
				Tools.getCurrentTime("yyyy-MM-dd HH:mm:ss.SSS");
		String startTime = Tools.getTime(Calendar.HOUR, -2, "yyyy-MM-dd HH:mm:ss.SSS");
		
		String[] projection = new String[] {"SUM("+COL_STEP+")"};
		
		Cursor c = db.query(TABLE_MEASUREMENT_BAND, projection, COL_DATE + " >= ? and " 
				+ COL_DATE +" <= ? and "+ COL_MEASUREMENT_TYPE + " =? ", 
				new String[] {startTime, endTime, TBMeasurementType.TYPE_STEP_MINUTES},
				null, null, null);
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got "+c.getString(0)+" two hours earlier step value");
			return c;
		}
		
		Log.i(TAG, "No data found");
		return null;
	}
	
	
	public Cursor getTodayDistance() {
		Log.i(TAG, "Getting today distance data");
		
		String startDate = Tools.getCurrentTime("yyyy-MM-dd") + " 00:00:00.000";
		
		String[] projection = new String[] {"SUM("+COL_DISTANCE+")"};
		
		Cursor c = db.query(TABLE_MEASUREMENT_BAND, projection, COL_DATE + " >= ? and "
				+ COL_MEASUREMENT_TYPE + " =? ",new String[] {startDate, 
				TBMeasurementType.TYPE_STEP_MINUTES},null, null, null);
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got "+c.getString(0)+" today distance value");
			return c;
		}
		
		Log.i(TAG, "No data found");
		return null;
	}
	
	public Cursor getTwoHoursDistance() {
		Log.i(TAG, "Getting two hours earlier distance data");
		
		String endTime = Tools.getCurrentTime("yyyy-MM-dd HH:mm:ss.SSS");
		String startTime = Tools.getTime(Calendar.HOUR, -2, "yyyy-MM-dd HH:mm:ss.SSS");
		
		String[] projection = new String[] {"SUM("+COL_DISTANCE+")"};
		
		Cursor c = db.query(TABLE_MEASUREMENT_BAND, projection, COL_DATE + " >= ? and " 
				+ COL_DATE +" <= ? and "+ COL_MEASUREMENT_TYPE + " =? ", 
				new String[] {startTime, endTime, TBMeasurementType.TYPE_STEP_MINUTES},
				null, null, null);
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got "+c.getString(0)+" two hours earlier step value");
			return c;
		}
		
		Log.i(TAG, "No data found");
		return null;
	}
	
	
	public Cursor getTodayCalorie() {
		Log.i(TAG, "Getting today calorie data");
		
		String startDate = Tools.getCurrentTime("yyyy-MM-dd") + " 00:00:00.000";
		
		String[] projection = new String[] {"SUM("+COL_CALORIE+")"};
		
		Cursor c = db.query(TABLE_MEASUREMENT_BAND, projection, COL_DATE + " >= ? and "
				+ COL_MEASUREMENT_TYPE + " =? ",new String[] {startDate, 
				TBMeasurementType.TYPE_STEP_MINUTES},null, null, null);
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got "+c.getString(0)+" today calorie value");
			return c;
		}
		
		Log.i(TAG, "No data found");
		return null;
	}
	
	
	
	public void delete() {
		// Log.w(TAG, "Deleting account id" + id);

		int result = db.delete(TABLE_MEASUREMENT_BAND, null, null);
		if (result > 0) {
			Log.i(TAG, "deleting all Game Measurement Success");
		} else {
			Log.i(TAG, "deleting all Game Measurement Failed");
		}
	}
	
	
	
	
	
}
