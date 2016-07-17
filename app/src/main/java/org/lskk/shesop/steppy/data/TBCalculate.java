package org.lskk.shesop.steppy.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TBCalculate {
	public static final String TAG					= "Table Step Calculate";
	
	public static final String TABLE_CALCULATE	 	= "calculate_step";
	
	public static final String COL_ID 				= "id";
	public static final String COL_STEP				= "step";
	public static final String COL_CAL				= "calories";
	public static final String COL_DIS				= "distance";
	public static final String COL_TIME_START		= "start_time";
	public static final String COL_TIME_END			= "end_time";
	public static final String COL_IS_COUNTING		= "is_counting";
	public static final String COL_STATE_GAME_CHECK	= "is_game_state";
		
	private SQLiteDatabase db;
	private DBLocalHelper dbHelper;
	
	// Database creation sql statement
	public static final String INITIAL_CREATE = "create table "
			+ TABLE_CALCULATE + "(" + COL_ID + " integer primary key autoincrement, "
			+ COL_STEP + " integer unique not null, "
			+ COL_CAL +  " varchar(30) not null, "
			+ COL_DIS +  " varchar(30) not null, "
			+ COL_TIME_START +  " varchar(30) not null, "
			+ COL_TIME_END +  " varchar(30) not null, "
			+ COL_STATE_GAME_CHECK +  " integer unique not null, "
			+ COL_IS_COUNTING +  " integer unique not null); ";

	public TBCalculate(Context context) {
		this.dbHelper = new DBLocalHelper(context);
	}
	
	
	public void open() {
	//	Log.w(TAG, "Open database connection...");
		this.db = dbHelper.getWritableDatabase();
		this.db = dbHelper.getReadableDatabase();
	}
	
	public void close() {
	//	Log.w(TAG, "Close database connection...");
		this.dbHelper.close();
	}
	
	public long insert(ContentValues values) {
	//	Log.w(TAG, "inserting to table measurement");
		
		Long result = db.insert(TABLE_CALCULATE, null, values);
		
		if(result != -1) {
			Log.i(TAG, "inserting dumy data at first : to table calculate succeed");
		} else {
			Log.e(TAG, "inserting to table failed");
		}
		
		return result;
	}
	
	
	
	public int update(String tID, ContentValues values) {
		//Log.w(TAG, "updating to table calculate");
		
		if(tID == null) {
			Log.e(TAG, "updating to table calculate failed, no ID");
			return 0;
		}
		
		int result = db.update(TABLE_CALCULATE, values, COL_ID + "=?", new String[] {tID});
		
		if(result > 0) {
			Log.i(TAG, "updating to table calculate succeed");
		} else {
			Log.e(TAG, "updating to table calculate failed");
		}
		
		return result;
	}
	
	
	 public Cursor getSteps() {

		  String[] allColumns = new String[] { TBCalculate.COL_ID, TBCalculate.COL_STEP,
		    TBCalculate.COL_CAL, TBCalculate.COL_TIME_START, TBCalculate.COL_TIME_END, 
		    TBCalculate.COL_IS_COUNTING, TBCalculate.COL_DIS, TBCalculate.COL_STATE_GAME_CHECK};

		  Cursor c = db.query(TBCalculate.TABLE_CALCULATE, allColumns, null, null, null,
		    null, TBCalculate.COL_ID + " DESC");
		  if (c.moveToFirst()) {
				return c;
			}
		  return null;
		 }
}