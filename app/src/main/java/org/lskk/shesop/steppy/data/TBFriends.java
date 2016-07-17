package org.lskk.shesop.steppy.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class represents table contacts contain user friend's phone number
 * that has been registered as Steppy user
 * 
 * @author Steppy
 *
 */
public class TBFriends {
	public static final String TAG				= "Table Friends";
	
	public static final String TABLE_FRIENDS 	= "friends";
	
	public static final String COL_ID 			= "_id";
	public static final String COL_TELP_NUMBER	= "telp_number";
	public static final String COL_NAME			= "name";
	public static final String COL_LEVEL		= "level";
	public static final String COL_HIGH_SCORE	= "high_score";
	public static final String COL_TODAY_STEP	= "today";
	public static final String COL_WEEKLY_STEP	= "weekly";
	public static final String COL_PROFPICT		= "photo";
	
	public static final String COL_LAST_MODIFIED= "l_modified";
	public static final String COL_LAST_SYNC	= "l_sync";
	
	private SQLiteDatabase db;
	private DBLocalHelper dbHelper;
	
	// Database creation sql statement
	public static final String INITIAL_CREATE = "create table "
			+ TABLE_FRIENDS + "(" + COL_ID + " integer primary key autoincrement, "
			+ COL_TELP_NUMBER + " varchar(30) unique not null, "
			+ COL_NAME +  " varchar(100), "
			+ COL_LEVEL +  " integer, "
			+ COL_HIGH_SCORE +  " integer, "
			+ COL_TODAY_STEP +  " integer, "
			+ COL_WEEKLY_STEP +  " integer, "
			+ COL_PROFPICT +  " blob, "
			+ COL_LAST_MODIFIED +  " varchar(50), "
			+ COL_LAST_SYNC +  " varchar(50)); ";
	
	public TBFriends(Context context) {
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
	
	public long insert(ContentValues values) {
		Log.w(TAG, "inserting to table friends");
		
		Long result = db.insert(TABLE_FRIENDS, null, values); 
		
		if(result != -1) {
			Log.i(TAG, "inserting to table friends succeed");
		} else {
			Log.e(TAG, "inserting to table friends failed");
		}
		
		return result;
	}
	
	public int update(String tPhone, ContentValues values) {
		Log.w(TAG, "updating to table friend");
		
		if(tPhone == null) {
			Log.e(TAG, "updating to table friend failed, no phone");
			return 0;
		}
		
		int result = db.update(TABLE_FRIENDS, values, COL_TELP_NUMBER + "=?", new String[] {tPhone});
		
		if(result > 0) {
			Log.i(TAG, "updating to table friend succeed");
		} else {
			Log.e(TAG, "updating to table friend failed");
		}
		
		return result;
	}
	
	public Cursor getAllFriendsList() {
		Log.i(TAG, "Get friends list");
		String[] projection = new String[] {COL_WEEKLY_STEP, COL_ID, COL_TELP_NUMBER, COL_NAME, 
				COL_LEVEL, COL_HIGH_SCORE, COL_PROFPICT, COL_TODAY_STEP};
		
		Cursor c = db.query(TABLE_FRIENDS, projection, null, null, null, null, null);
		
		if (c.moveToFirst()) {
			Log.i(TAG, "Got friends all list");
			return c;
		}
		
		Log.i(TAG, "No friends found");
		return null;
	}
}
