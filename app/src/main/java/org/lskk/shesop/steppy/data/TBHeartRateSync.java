package org.lskk.shesop.steppy.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TBHeartRateSync {
	public static final String TAG					= "Table Heart Rate Sync";
	
	public static final String TABLE_HR				= "sync_heartrate";
	public static final String COL_SPEED			= "speed";
	public static final String COL_PACE				= "pace";
	public static final String COL_TEMPERATURE		= "temperature";
	public static final String COL_UV				= "uv";
	public static final String COL_ID 				= "id";
	public static final String COL_HR				= "Heartrate";
	public static final String COL_TIME_START		= "start_time";
	public static final String COL_TIME_END			= "end_time";
	public static final String COL_DATE				= "date";
	
	
	private SQLiteDatabase db;
	private DBLocalHelper dbHelper;
	
	// Database creation sql statement
	public static final String INITIAL_CREATE = "create table "
			+ TABLE_HR + "(" + COL_ID + " integer primary key autoincrement, "
			+ COL_HR + " varchar(6) not null, "
			+ COL_SPEED +  " varchar(30) not null, "
			+ COL_PACE +  " varchar(30) not null, "
			+ COL_TEMPERATURE +  " varchar(30) not null, "
			+ COL_UV +  " varchar(30) not null, "
			+ COL_TIME_START +  " varchar(30) not null, "
			+ COL_TIME_END +  " varchar(30) not null, "
			+ COL_DATE +  " varchar(30) not null); ";
	
	
	public TBHeartRateSync(Context context) {
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
	//	Log.w(TAG, "inserting to table Heart Rate");
		
		Long result = db.insert(TABLE_HR, null, values);
		
		if(result != -1) {
			Log.i(TAG, "inserting Heart Rate :  "+ values.getAsString(TBHeartRateSync.COL_HR) +" to table succeed");
		} else {
			Log.e(TAG, "inserting to table heartrate failed");
		}
		
		return result;
	}
	
	
	public int update(String tID, ContentValues values) {
		Log.w(TAG, "updating to table heartrate");
		
		if(tID == null) {
			Log.e(TAG, "updating to table heartrate failed, no ID");
			return 0;
		}
		
		int result = db.update(TABLE_HR, values, COL_ID + "=?", new String[] {tID});
		
		if(result > 0) {
			Log.i(TAG, "updating to table step succeed");
		} else {
			Log.e(TAG, "updating to table step failed");
		}
		
		return result;
	}
	
	public int delete(String id) {
		//Log.w(TAG, "Deleting account id" + id);

		String tID = id;
		int result = db.delete(TABLE_HR, COL_ID + "=?",
				new String[] { tID });

		if (result > 0) {
			Log.i(TAG, "deleting id no " + tID + " succeed");
		} else {
			Log.e(TAG, "deleting account " + tID + " failed");
		}

		return result;
	}
	
	
	
	
	public Cursor getHRData() {
		//Log.i(TAG, "Get steps data");
		String[] projection = new String[] {COL_HR};
		
		Cursor c = db.query(TABLE_HR, projection, null, null, null, null, null);
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got hr data");
			return c;
		}
		
		Log.i(TAG, "No data found");
		return null;
	}
	
	
	
	public Cursor getLastInsert() {
		Log.i(TAG, "Get last date insert");
		String[] projection = new String[] {COL_ID, COL_HR, 
				COL_TIME_START, COL_TIME_END, COL_DATE};
		
		Cursor c = db.query(TABLE_HR, projection, null, null, null, null, COL_DATE + " DESC");
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got last insert");
			return c;
		}
		
		Log.i(TAG, "No data found");
		return null;
	}
	
	
	 public Cursor getIdHR() {

		  String[] allColumns = new String[] { TBHeartRateSync.COL_ID, TBHeartRateSync.COL_HR,
			TBHeartRateSync.COL_TIME_START, TBHeartRateSync.COL_TIME_END, TBHeartRateSync.COL_DATE
			,TBHeartRateSync.COL_SPEED, TBHeartRateSync.COL_PACE, TBHeartRateSync.COL_TEMPERATURE,
			TBHeartRateSync.COL_UV};

		  Cursor c = db.query(TBHeartRateSync.TABLE_HR, allColumns, null, null, null,
		    null, null);
		  if (c != null) {
			   c.moveToFirst();
			  }
		  return c;

		 }
	
	
	
}
