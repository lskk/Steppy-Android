package org.lskk.shesop.steppy.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TBHRCalculate {
	public static final String TAG					= "Table HR Calculate";
	
	public static final String TABLE_HR_CALCULATE	= "calculate_heartrate";
	
	public static final String COL_ID 				= "id";
	public static final String COL_STEP				= "step";
	public static final String COL_CAL				= "cal";
	public static final String COL_DISTANCE			= "dist";
	public static final String COL_TIME_START		= "start_time";
	public static final String COL_TIME_END			= "end_time";
		
	private SQLiteDatabase db;
	private DBLocalHelper dbHelper;
	
	// Database creation sql statement
	public static final String INITIAL_CREATE = "create table "
			+ TABLE_HR_CALCULATE + "(" + COL_ID + " integer primary key autoincrement, "
			+ COL_STEP + " integer unique not null, "
			+ COL_CAL + " varchar(30) not null, "
			+ COL_DISTANCE + " varchar(30) not null, "
			+ COL_TIME_START +  " varchar(30) not null, "
			+ COL_TIME_END +  " varchar(30) not null); ";

	public TBHRCalculate(Context context) {
		this.dbHelper = new DBLocalHelper(context);
	}
	
	
	public void open() {
		//Log.w(TAG, "Open database connection...");
		this.db = dbHelper.getWritableDatabase();
		this.db = dbHelper.getReadableDatabase();
	}
	
	public void close() {
		//Log.w(TAG, "Close database connection...");
		this.dbHelper.close();
	}
	
	public long insert(ContentValues values) {
		Log.w(TAG, "inserting to table Hr Calculate");
		
		Long result = db.insert(TABLE_HR_CALCULATE, null, values);
		
		if(result != -1) {
			Log.i(TAG, "inserting dumy data at first : to table HR calculate succeed");
		} else {
			Log.e(TAG, "inserting to table failed");
		}
		
		return result;
	}
	
	
	
	public int update(String tID, ContentValues values) {
		//Log.w(TAG, "updating to table HR calculate");
		
		if(tID == null) {
			Log.e(TAG, "updating to table HR calculate failed, no ID");
			return 0;
		}
		
		int result = db.update(TABLE_HR_CALCULATE, values, COL_ID + "=?", new String[] {tID});
		
		if(result > 0) {
			Log.i(TAG, "updating to table HR calculate succeed");
		} else {
			Log.e(TAG, "updating to table HR calculate failed");
		}
		
		return result;
	}
	
	
	 public Cursor getSteps() {

		  String[] allColumns = new String[] { TBHRCalculate.COL_ID, TBHRCalculate.COL_STEP,
				  TBHRCalculate.COL_DISTANCE, TBHRCalculate.COL_CAL, TBHRCalculate.COL_DISTANCE, 
				  TBHRCalculate.COL_TIME_START, TBHRCalculate.COL_TIME_END};

		  Cursor c = db.query(TBHRCalculate.TABLE_HR_CALCULATE, allColumns, null, null, null,
		    null, TBHRCalculate.COL_ID + " DESC");
		  if (c.moveToFirst()) {
				return c;
			}
		  
		  return null;
		 }	
}
