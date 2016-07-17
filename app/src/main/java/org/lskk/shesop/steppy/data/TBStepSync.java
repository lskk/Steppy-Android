package org.lskk.shesop.steppy.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TBStepSync {
	public static final String TAG					= "Table Step Sync";
	
	public static final String TABLE_STEP 			= "sync_step";
	
	public static final String COL_ID 				= "id";
	public static final String COL_STEP				= "step";
	public static final String COL_CAL				= "calories";
	public static final String COL_DIS				= "distance";
	public static final String COL_TIME_START		= "start_time";
	public static final String COL_TIME_END			= "end_time";
	public static final String COL_SENSOR			= "sensor_type";
	public static final String COL_DATE				= "date";
	
	
	private SQLiteDatabase db;
	private DBLocalHelper dbHelper;
	
	// Database creation sql statement
	public static final String INITIAL_CREATE = "create table "
			+ TABLE_STEP + "(" + COL_ID + " integer primary key autoincrement, "
			+ COL_STEP + " varchar(6) not null, "
			+ COL_CAL +  " varchar(6) not null, "
			+ COL_TIME_START +  " varchar(30) not null, "
			+ COL_TIME_END +  " varchar(30) not null, "
			+ COL_SENSOR +  " varchar(30) not null, "
			+ COL_DATE +  " varchar(30) not null, "
			+ COL_DIS + " varchar(6) not null);";
	
	public TBStepSync(Context context) {
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
		//Log.w(TAG, "inserting to table measurement");
		
		Long result = db.insert(TABLE_STEP, null, values);
		
		if(result != -1) {
			Log.i(TAG, "inserting step :  "+ values.getAsString(TBStepSync.COL_STEP) +" succeed");
			Log.i(TAG, "inserting Cal :  "+ values.getAsString(TBStepSync.COL_CAL) +" succeed");
			Log.i(TAG, "inserting Dis :  "+ values.getAsString(TBStepSync.COL_DIS) +" succeed");
		} else {
			Log.e(TAG, "inserting to table step failed");
		}
		
		return result;
	}
	
	
	public int update(String tID, ContentValues values) {
		//Log.w(TAG, "updating to table measurement");
		
		if(tID == null) {
			Log.e(TAG, "updating to table step sync failed, no ID");
			return 0;
		}
		
		int result = db.update(TABLE_STEP, values, COL_ID + "=?", new String[] {tID});
		
		if(result > 0) {
			Log.i(TAG, "updating to table step succeed");
		} else {
			Log.e(TAG, "updating to table step failed");
		}
		
		return result;
	}
	
	public int delete(String id) {
		// Log.w(TAG, "Deleting account id" + id);

		String tID = id;
		int result = db.delete(TABLE_STEP, COL_ID + "=?",
				new String[] { tID });

		if (result > 0) {
			Log.i(TAG, "deleting id step no " + tID + " succeed");
		} else {
			Log.e(TAG, "deleting id step no  " + tID + " failed");
		}

		return result;
	}
	
	public Cursor getStepsData() {
		Log.i(TAG, "Get steps data");
		String[] projection = new String[] {COL_STEP};
		
		Cursor c = db.query(TABLE_STEP, projection, null, null, null, null, null);
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got step data");
			return c;
		}
		
		Log.i(TAG, "No data found");
		return null;
	}
	
	
	
	public Cursor getLastInsert() {
		Log.i(TAG, "Get last date insert");
		String[] projection = new String[] {COL_ID, COL_STEP, COL_CAL, COL_DIS,
				COL_TIME_START, COL_TIME_END, COL_SENSOR, COL_DATE};
		
		Cursor c = db.query(TABLE_STEP, projection, null, null, null, null, COL_DATE + " DESC");
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got last insert");
			return c;
		}
		
		Log.i(TAG, "No data found");
		return null;
	}
	
	
	 public Cursor getIdSteps() {

		  String[] allColumns = new String[] { TBStepSync.COL_ID, TBStepSync.COL_STEP,
		    TBStepSync.COL_CAL, TBStepSync.COL_TIME_START, TBStepSync.COL_TIME_END, TBStepSync.COL_SENSOR,
		    TBStepSync.COL_DATE, TBStepSync.COL_DIS};

		  Cursor c = db.query(TBStepSync.TABLE_STEP, allColumns, null, null, null,
		    null, null);
		  if (c != null) {
			   c.moveToFirst();
			  }
		  
		  return c;

		 }
	
	
	
}
