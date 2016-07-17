package org.lskk.shesop.steppy.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TBGameRecorcd {

	
	public static String TAG					= "Table Game Record";
	
	public static String TABLE_RECORD			= "game_record";
	
	public static final String COL_ID 			= "id";
	public static final String COL_ID_USER		= "id_user_shesop";
	public static final String COL_DATE			= "date";
	public static final String COL_MISSION_1	= "mission_1";
	public static final String COL_MISSION_2	= "mission_2";
	public static final String COL_MISSION_3	= "mission_3";
	public static final String COL_MISSION_4	= "mission_4";
	public static final String COL_LEVEL		= "level";
	public static final String COL_POINT		= "point";
	
	private SQLiteDatabase db;
	private DBLocalHelper dbHelper;
	
	// Database creation sql statement
		public static final String INITIAL_CREATE = "create table "
				+ TABLE_RECORD + "(" + COL_ID + " integer primary key autoincrement, "
				+ COL_ID_USER +  " varchar(30) not null, "
				+ COL_DATE +  " varchar(30) not null, "
				+ COL_MISSION_1 +  " varchar(30) not null, "
				+ COL_MISSION_2 +  " varchar(30) not null, "
				+ COL_MISSION_3 +  " varchar(30) not null, "
				+ COL_MISSION_4 +  " varchar(30) not null, "
				+ COL_LEVEL +  " varchar(30) not null, "
				+ COL_POINT +  " varchar(30) not null); ";
		
		public TBGameRecorcd(Context context) {
			this.dbHelper = new DBLocalHelper(context);
		}
		
		public void open() {
			// Log.w(TAG, "Open database connection...");
			this.db = dbHelper.getWritableDatabase();
			this.db = dbHelper.getReadableDatabase();
		}
		
		public void close() {
			// Log.w(TAG, "Close database connection...");
			this.dbHelper.close();
		}
		
		
		public long insert(ContentValues values) {
			//Log.w(TAG, "inserting to table mission");		
			Long result = db.insert(TABLE_RECORD, null, values);
			if(result != -1) {
				Log.i(TAG, "inserting val mission 1 :  "+ values.getAsString(TBGameRecorcd.COL_MISSION_1) +" succeed");
				Log.i(TAG, "inserting val mission 2 :  "+ values.getAsString(TBGameRecorcd.COL_MISSION_2) +" succeed");
				Log.i(TAG, "inserting val mission 3 :  "+ values.getAsString(TBGameRecorcd.COL_MISSION_3) +" succeed");
				Log.i(TAG, "inserting val mission 4 :  "+ values.getAsString(TBGameRecorcd.COL_MISSION_4) +" succeed");
				Log.i(TAG, "inserting point :  "+ values.getAsString(TBGameRecorcd.COL_POINT) +" to table succeed");
				Log.i(TAG, "inserting Level :  "+ values.getAsString(TBGameRecorcd.COL_LEVEL) +" to table succeed");
			} else {
				Log.e(TAG, "inserting to table failed");
			}
			return result;
		}
		
		
		public int update(String tID, ContentValues values) {
			// Log.w(TAG, "updating to table calculate");
			
			if(tID == null) {
				Log.e(TAG, "updating to table calculate failed, no ID");
				return 0;
			}
			
			int result = db.update(TABLE_RECORD, values, COL_ID + "=?", new String[] {tID});
			
			if(result > 0) {
				Log.i(TAG, "updating to table record succeed");
			} else {
				Log.e(TAG, "updating to table record failed");
			}
			
			return result;
		}
		
		
		public Cursor getIdRecordPosition(int rowId) {

			  String[] allColumns = new String[] { TBGameRecorcd.COL_ID, TBGameRecorcd.COL_ID_USER, TBGameRecorcd.COL_DATE, 
					  TBGameRecorcd.COL_MISSION_1, TBGameRecorcd.COL_MISSION_2, TBGameRecorcd.COL_MISSION_3,
					  TBGameRecorcd.COL_MISSION_4, TBGameRecorcd.COL_LEVEL, TBGameRecorcd.COL_POINT};

			  Cursor c = db.query(TBGameRecorcd.TABLE_RECORD, allColumns, null, null, null,
			    null, null);
			  if (c != null) {
				   c.moveToPosition(rowId);
				  }
			  return c;
			 }
		
		
		
		
		
		 public Cursor getIdRecordFirst() {

			  String[] allColumns = new String[] { TBGameRecorcd.COL_ID, TBGameRecorcd.COL_ID_USER, 
					  TBGameRecorcd.COL_MISSION_1, TBGameRecorcd.COL_MISSION_2, TBGameRecorcd.COL_MISSION_3,
					  TBGameRecorcd.COL_MISSION_4, TBGameRecorcd.COL_LEVEL, TBGameRecorcd.COL_POINT};

			  Cursor c = db.query(TBGameRecorcd.TABLE_RECORD, allColumns, null, null, null,
			    null, null);
			  if (c != null) {
				   c.moveToFirst();
				  }
			  return c;
			 }
		 
		 
		 public Cursor getIdRecordLast() {

			  String[] allColumns = new String[] { TBGameRecorcd.COL_ID, TBGameRecorcd.COL_ID_USER, 
					  TBGameRecorcd.COL_MISSION_1, TBGameRecorcd.COL_MISSION_2, TBGameRecorcd.COL_MISSION_3,
					  TBGameRecorcd.COL_MISSION_4, TBGameRecorcd.COL_LEVEL, TBGameRecorcd.COL_POINT};

			  Cursor c = db.query(TBGameRecorcd.TABLE_RECORD, allColumns, null, null, null,
			    null, null);
			  if (c != null) {
				   c.moveToLast();
				  }
			  return c;
			 }
		 
			public int delete(String id) {
				// Log.w(TAG, "Deleting account id" + id);
				String tID = id;
				int result = db.delete(TABLE_RECORD, COL_ID + "=?",
						new String[] { tID });

				if (result > 0) {
					Log.i(TAG, "deleting id record no " + tID + " succeed");
				} else {
					Log.e(TAG, "deleting id record no " + tID + " failed");
				}

				return result;
			}
}
