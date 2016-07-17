package org.lskk.shesop.steppy.data;

import java.util.ArrayList;
import java.util.Calendar;

import org.lskk.shesop.steppy.utils.Tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TBMeasurement {
	
	public static final String TAG					= "Table Measurement";
	
	public static final String TABLE_MEASUREMENT 	= "measurement";
	
	public static final String COL_ID 				= "_id";
	public static final String COL_VALUE			= "value";
	public static final String COL_DISTANCE			= "distance";
	public static final String COL_CALORIE			= "calorie";
	public static final String COL_DATE				= "date";
	public static final String COL_MEASUREMENT_TYPE	= "type";
	public static final String COL_LAST_MODIFIED	= "l_modified";
	public static final String COL_IS_SYNC			= "l_sync";
	
	private SQLiteDatabase db;
	private DBLocalHelper dbHelper;
	private String lastStep ="0";
	
	// Database creation sql statement
	public static final String INITIAL_CREATE = "create table "
			+ TABLE_MEASUREMENT + "(" + COL_ID + " integer primary key autoincrement, "
			+ COL_VALUE + " varchar(30) not null, "
			+ COL_DISTANCE + " varchar(30) not null, "
			+ COL_CALORIE + " varchar(30) not null, "
			+ COL_DATE +  " varchar(50) not null, "
			+ COL_MEASUREMENT_TYPE +  " varchar(3) not null, "
			+ COL_LAST_MODIFIED +  " varchar(50) not null, "
			+ COL_IS_SYNC +  " varchar(1) not null); ";
	
	public TBMeasurement(Context context) {
		this.dbHelper = new DBLocalHelper(context);
	}
	
	public void open() {
		Log.w(TAG, "Open database connection...");
		this.db = dbHelper.getWritableDatabase();
		this.db = dbHelper.getReadableDatabase();
	}
	
	public void close() {
		Log.w(TAG, "Close database connection...");
		this.dbHelper.close();
	}
	
	public long insert(ContentValues values) {
		Log.w(TAG, "inserting to table measurement");
		
		Long result = db.insert(TABLE_MEASUREMENT, null, values);
		
		if(result != -1) {
			Log.i(TAG, "inserting value "+ values.getAsString(TBMeasurement.COL_VALUE) +" to table measurement succeed");
			lastStep = values.getAsString(TBMeasurement.COL_VALUE);
		} else {
			Log.e(TAG, "inserting to table measurement failed");
		}
		
		return result;
	}
	
	public String getLastSteps(){
		String mlastStep;
		mlastStep = lastStep;
        return mlastStep;
    }
	
	public int update(String tID, ContentValues values) {
		Log.w(TAG, "updating to table measurement");
		
		if(tID == null) {
			Log.e(TAG, "updating to table measurement failed, no ID");
			return 0;
		}
		
		int result = db.update(TABLE_MEASUREMENT, values, COL_ID + "=?", new String[] {tID});
		
		if(result > 0) {
			Log.i(TAG, "updating to table measurement succeed");
		} else {
			Log.e(TAG, "updating to table measurement failed");
		}
		
		return result;
	}
	
	public int updateAfterSync(ArrayList<String> pID) {
		Log.w(TAG, "updating to table measurement after sync");
		
		pID.add(0, "0");
		
		String[] where = pID.toArray(new String[pID.size()]);
		
		ContentValues values = new ContentValues();
		values.put(COL_IS_SYNC, "1");
		
		int result = db.update(TABLE_MEASUREMENT, values, COL_IS_SYNC + "=? and "
				+ COL_ID +" IN(" + makePlaceholders(pID.size()) +")", where);
		
		if(result > 0) {
			Log.i(TAG, "updating to table measurement after sync succeed");
		} else {
			Log.e(TAG, "updating to table measurement after sync failed");
		}
		
		return result;
	}
	
	public Cursor getAllData() {
		Log.i(TAG, "Get all data measurement");
		String[] projection = new String[] {COL_ID, COL_DATE, COL_VALUE, COL_DISTANCE, COL_CALORIE,
				COL_LAST_MODIFIED, COL_IS_SYNC, COL_MEASUREMENT_TYPE};
		
		Cursor c = db.query(TABLE_MEASUREMENT, projection, null, null, null, null, null);
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got all data");
			return c;
		}
		
		Log.i(TAG, "No data found");
		return null;
	}
	
	public Cursor getStepsData() {
		Log.i(TAG, "Get steps data for graphic");
		String[] projection = new String[] {COL_VALUE};
		
		Cursor c = db.query(TABLE_MEASUREMENT, projection, null, null, null, null, null);
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got step data");
			return c;
		}
		
		Log.i(TAG, "No data found");
		return null;
	}
	
	public Cursor getLastInsert() {
		Log.i(TAG, "Get last date insert");
		String[] projection = new String[] {COL_ID, COL_DATE, COL_VALUE, COL_DISTANCE, COL_CALORIE,
				COL_LAST_MODIFIED, COL_IS_SYNC, COL_MEASUREMENT_TYPE};
		
		Cursor c = db.query(TABLE_MEASUREMENT, projection, null, null, null, null, COL_DATE + " DESC");
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got last insert");
			return c;
		}
		
		Log.i(TAG, "No data found");
		return null;
	}
	
	public Cursor getDataToSync() {
		Log.i(TAG, "Getting not-sync data");
		String[] projection = new String[] {COL_ID, COL_DATE, COL_VALUE, COL_DISTANCE, COL_CALORIE,
				COL_LAST_MODIFIED, COL_IS_SYNC, COL_MEASUREMENT_TYPE};
		
		Cursor c = db.query(TABLE_MEASUREMENT, projection, COL_IS_SYNC + "=?", new String[] {"0"}, null, null, null, "100");
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got " + c.getCount() +" not-sync data");
			return c;
		}
		
		Log.i(TAG, "No data found");
		return null;
	}
	
	public Cursor getLastWeekStep() {
		Log.i(TAG, "Getting weekly step data");
		
		int dayOfWeek 	= Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		int sunday 		= (dayOfWeek < 2) ? dayOfWeek+7-2: dayOfWeek-2; 
		
		// Start weekly step from monday
		String startDate = Tools.getTime(Calendar.DATE, -sunday, "yyyy-MM-dd 00:00:00.000");
		String finishDate = Tools.getTime(Calendar.MINUTE, 1, "yyyy-MM-dd HH:mm:ss.SSS");
		
		String[] projection = new String[] {"SUM("+COL_VALUE+")"};
		
		Cursor c = db.query(TABLE_MEASUREMENT, projection, COL_DATE + " >= ? and " 
				+ COL_DATE +" <= ? and "+ COL_MEASUREMENT_TYPE + " =? ", 
				new String[] {startDate, finishDate, TBMeasurementType.TYPE_STEP_MINUTES},
				null, null, null);
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got "+c.getString(0)+" weekly step value");
			return c;
		}
		
		Log.i(TAG, "No data found");
		return null;
	}
	
	public Cursor getTodayStep() {
		Log.i(TAG, "Getting today step data");
		
		String startDate = Tools.getCurrentTime("yyyy-MM-dd") + " 00:00:00.000";
		
		String[] projection = new String[] {"SUM("+COL_VALUE+")"};
		
		Cursor c = db.query(TABLE_MEASUREMENT, projection, COL_DATE + " >= ? and "
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
		
		String[] projection = new String[] {"SUM("+COL_VALUE+")"};
		
		Cursor c = db.query(TABLE_MEASUREMENT, projection, COL_DATE + " >= ? and " 
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
		
		Cursor c = db.query(TABLE_MEASUREMENT, projection, COL_DATE + " >= ? and "
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
		
		Cursor c = db.query(TABLE_MEASUREMENT, projection, COL_DATE + " >= ? and " 
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
		
		Cursor c = db.query(TABLE_MEASUREMENT, projection, COL_DATE + " >= ? and "
				+ COL_MEASUREMENT_TYPE + " =? ",new String[] {startDate, 
				TBMeasurementType.TYPE_STEP_MINUTES},null, null, null);
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got "+c.getString(0)+" today calorie value");
			return c;
		}
		
		Log.i(TAG, "No data found");
		return null;
	}
	
	
	String makePlaceholders(int len) {
	    if (len < 1) {
	        // It will lead to an invalid query anyway ..
	        throw new RuntimeException("No placeholders");
	    } else {
	        StringBuilder sb = new StringBuilder(len * 2 - 1);
	        sb.append("?");
	        for (int i = 1; i < len; i++) {
	            sb.append(",?");
	        }
	        return sb.toString();
	    }
	}
	
}
