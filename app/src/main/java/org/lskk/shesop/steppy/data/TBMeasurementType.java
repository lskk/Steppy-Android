package org.lskk.shesop.steppy.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TBMeasurementType {
	
	public static final String TAG					= "Table Measurement Type";
	
	public static final String TABLE_MEASUREMENT_TYPE 	= "measurement_type";
	
	public static final String COL_ID 				= "_id";
	public static final String COL_TYPE_NAME		= "name";
	public static final String COL_TYPE_UNIT		= "type";
	
	public static final String TYPE_STEP_MINUTES	= "5";
	public static final String TYPE_HEARTBEAT		= "7";
	public static final String TYPE_SPEED			= "8";
	public static final String TYPE_PUSHUP			= "11";
	
	private SQLiteDatabase db;
	private DBLocalHelper dbHelper;
	
	// Database creation sql statement
	public static final String INITIAL_CREATE = "create table "
			+ TABLE_MEASUREMENT_TYPE + "(" + COL_ID + " integer primary key, "
			+ COL_TYPE_NAME + " varchar(30) unique not null, "
			+ COL_TYPE_UNIT + " varchar(30) not null);";
	
	public static final String INITIAL_INSERT = ""
			+ "insert into " + TABLE_MEASUREMENT_TYPE + " values(1, 'ACCX', 'm/(s^2)');"
			+ "insert into " + TABLE_MEASUREMENT_TYPE + " values(2, 'ACCY', 'm/(s^2)');"
			+ "insert into " + TABLE_MEASUREMENT_TYPE + " values(3, 'ACCZ', 'm/(s^2)');"
			+ "insert into " + TABLE_MEASUREMENT_TYPE + " values(4, 'STEP', 'steps');"
			+ "insert into " + TABLE_MEASUREMENT_TYPE + " values(5, 'STEPMINUTES', 'step/minutes');"
			+ "insert into " + TABLE_MEASUREMENT_TYPE + " values(6, 'STEPHOUR', 'step/hour');"
			+ "insert into " + TABLE_MEASUREMENT_TYPE + " values(7, 'HEARTBEAT', 'beat/minute');"
			+ "insert into " + TABLE_MEASUREMENT_TYPE + " values(8, 'SPEED', 'm/s');"
			+ "insert into " + TABLE_MEASUREMENT_TYPE + " values(9, 'LOCLONG', 'degree');"
			+ "insert into " + TABLE_MEASUREMENT_TYPE + " values(10, 'LOCLAC', 'beat/minute');"
			+ "insert into " + TABLE_MEASUREMENT_TYPE + " values(11, 'PUSHUP', 'times');";
	
	public TBMeasurementType(Context context) {
		this.dbHelper = new DBLocalHelper(context);
	}
	
	public void open() {
		Log.w(TAG, "Open database connection...");
		this.db = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		Log.w(TAG, "Close database connection...");
		this.dbHelper.close();
	}
	
	public Cursor getAllMeasurementType() {
		Log.i(TAG, "Get account login");
		String[] projection = new String[] {COL_ID, COL_TYPE_NAME, COL_TYPE_UNIT};
		
		Cursor c = db.query(TABLE_MEASUREMENT_TYPE, projection, null, null, null, null, null);
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Succeed get all measurement type");
			return c;
		}
		
		Log.i(TAG, "table measurement type is empty");
		return null;
	}
	
}
