package org.lskk.shesop.steppy.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class TBMission {

	public static final String TAG				= "Table Mision";
	
	public static final String TABLE_MISSION	= "mission_table";
	
	public static final String COL_ID 			= "id";
	public static final String COL_IDLEVEL		= "id_level";
	public static final String COL_MISSION_1	= "mission_1";
	public static final String COL_MISSION_2	= "mission_2";
	public static final String COL_MISSION_3	= "mission_3";
	public static final String COL_MISSION_4	= "mission_4";
	
	private SQLiteDatabase db;
	private DBLocalHelper dbHelper;
	
	// Database creation sql statement
	public static final String INITIAL_CREATE = "create table "
			+ TABLE_MISSION + "(" + COL_ID + " integer primary key autoincrement, "
			+ COL_IDLEVEL +  " varchar(30) not null, "
			+ COL_MISSION_1 +  " varchar(30) not null, "
			+ COL_MISSION_2 +  " varchar(30) not null, "
			+ COL_MISSION_3 +  " varchar(30) not null, "
			+ COL_MISSION_4 +  " varchar(30) not null); ";
	
	public TBMission(Context context) {
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
		//Log.w(TAG, "inserting to table mission");		
		Long result = db.insert(TABLE_MISSION, null, values);
		if(result != -1) {
			Log.i(TAG, "inserting val Level:  "+ values.getAsString(TBMission.COL_IDLEVEL) +" succeed");
			Log.i(TAG, "inserting val mission 1 :  "+ values.getAsString(TBMission.COL_MISSION_1) +" succeed");
			Log.i(TAG, "inserting val mission 2 :  "+ values.getAsString(TBMission.COL_MISSION_2) +" succeed");
			Log.i(TAG, "inserting val mission 3 :  "+ values.getAsString(TBMission.COL_MISSION_3) +" succeed");
			Log.i(TAG, "inserting val mission 4 :  "+ values.getAsString(TBMission.COL_MISSION_4) +" succeed");
		} else {
			Log.e(TAG, "inserting to table failed");
		}
		return result;
	}
	
	
	
	public int update(String tID, ContentValues values) {
		// Log.w(TAG, "updating to table calculate");
		
		if(tID == null) {
			Log.e(TAG, "updating to table mission failed, no ID");
			return 0;
		}
		
		int result = db.update(TABLE_MISSION, values, COL_ID + "=?", new String[] {tID});
		
		if(result > 0) {
			Log.i(TAG, "updating to table mission succeed");
		} else {
			Log.e(TAG, "updating to table mission failed");
		}
		
		return result;
	}
	
	public Cursor getIdRecordLast() {

		String[] allColumns = new String[] { TBMission.COL_ID, TBMission.COL_IDLEVEL, 
				  TBMission.COL_MISSION_1, TBMission.COL_MISSION_2, TBMission.COL_MISSION_3,
				  TBMission.COL_MISSION_4};

		  Cursor c = db.query(TBMission.TABLE_MISSION, allColumns, null, null, null,
		    null, null);
		  if (c != null) {
			   c.moveToLast();
			  }
		  return c;
		 }
	
	
	 public Cursor getIdMission(int rowId) {

		  String[] allColumns = new String[] { TBMission.COL_ID, TBMission.COL_IDLEVEL, 
				  TBMission.COL_MISSION_1, TBMission.COL_MISSION_2, TBMission.COL_MISSION_3,
				  TBMission.COL_MISSION_4};

		  Cursor c = db.query(TBMission.TABLE_MISSION, allColumns, null, null, null,
		    null, null);
		  if (c != null) {
			   // c.moveToFirst();
			  c.moveToPosition(rowId);
			  }
		  return c;

		 }
}
