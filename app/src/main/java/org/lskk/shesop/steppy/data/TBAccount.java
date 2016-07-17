package org.lskk.shesop.steppy.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TBAccount {
	public static final String TAG = "Table Account";

	public static final String TABLE_ACCOUNT = "account";

	public static final String COL_ID = "_id";
	public static final String COL_ID_SHESOP = "id_shesop";
	public static final String COL_ID_ACCOUNT = "id_user";
	public static final String COL_TELP_NUMBER = "telp_number";
	public static final String COL_AGE = "age";
	public static final String COL_GENDER = "gender";
	public static final String COL_HEIGHT = "height";
	public static final String COL_WEIGHT = "weight";
	public static final String COL_NAME = "disp_name";
	public static final String COL_PROFPICT = "photo";

	public static final String COL_SCORE = "score";
	public static final String COL_LEVEL = "level";
	public static final String COL_EMAIL = "email";
	public static final String COL_LAST_MODIFIED = "l_modified";
	public static final String COL_LAST_SYNC = "l_sync";
	public static final String COL_IS_LOGIN = "islogin";
	public static final String COL_TOKEN = "token";

	private SQLiteDatabase db;
	private DBLocalHelper dbHelper;

	public static final String INITIAL_CREATE = "create table " + TABLE_ACCOUNT
			+ "(" + COL_ID + " integer primary key autoincrement, "
			+ COL_ID_SHESOP + " integer unique not null, " 
			+ COL_ID_ACCOUNT + " integer unique not null, " + COL_TELP_NUMBER
			+ " varchar(30) unique not null, " + COL_NAME
			+ " varchar(30) unique not null, " + COL_GENDER
			+ " varchar(30) unique not null, " + COL_AGE
			+ " varchar(30) unique not null, " + COL_HEIGHT
			+ " varchar(30) unique not null, " + COL_WEIGHT
			+ " varchar(50) not null, " + COL_SCORE + " integer, " + COL_LEVEL
			+ " integer, " + COL_PROFPICT + " blob, " + COL_LAST_MODIFIED
			+ " varchar(50), " + COL_LAST_SYNC + " varchar(50), " + COL_TOKEN
			+ " varchar(50), " + COL_IS_LOGIN + " integer, "+ COL_EMAIL + " varchar(50) not null);";

	public TBAccount(Context context) {
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
		Log.w(TAG, "inserting to table account");
		Long result = db.insert(TABLE_ACCOUNT, null, values);
		if (result != -1) {
			Log.i(TAG, "inserting to table account succeed");
		} else {
			Log.e(TAG, "inserting to table account failed");
		}

		return result;
	}

	public int delete(String uname) {
		return db.delete(TABLE_ACCOUNT, COL_TELP_NUMBER + "=?",
				new String[] { uname });
	}

	public int update(String tID, ContentValues values) {
		Log.w(TAG, "updating to table account");
		if (tID == null) {
			Log.e(TAG, "updating to table account failed, no ID");
			return 0;
		}
		int result = db.update(TABLE_ACCOUNT, values, COL_ID + "=?",
				new String[] { tID });
		if (result > 0) {
			Log.i(TAG, "updating to table account succeed");
		} else {
			Log.e(TAG, "updating to table account failed");
		}
		return result;
	}

	public int delete(long id) {
		Log.w(TAG, "Deleting account id" + id);

		String tID = Long.toString(id);
		int result = db.delete(TABLE_ACCOUNT, COL_ID + "=?",
				new String[] { tID });

		if (result > 0) {
			Log.i(TAG, "deleting account " + tID + " succeed");
		} else {
			Log.e(TAG, "deleting account " + tID + " failed");
		}

		return result;
	}

	public Cursor getAccountLogin() {
		Log.i(TAG, "Get account login");
		String[] projection = new String[] { TBAccount.COL_ID, TBAccount.COL_ID_SHESOP, TBAccount.COL_ID_ACCOUNT, 
				TBAccount.COL_TELP_NUMBER, TBAccount.COL_NAME, TBAccount.COL_GENDER,
				TBAccount.COL_AGE, TBAccount.COL_HEIGHT, TBAccount.COL_WEIGHT,
				TBAccount.COL_SCORE, TBAccount.COL_TOKEN, TBAccount.COL_LEVEL, TBAccount.COL_IS_LOGIN };

		Cursor c = db.query(TABLE_ACCOUNT, projection, COL_IS_LOGIN + "=?",
				new String[] { "1" }, null, null, null);

		if (c.moveToFirst()) {
			Log.i(TAG, "Already sign in");
			return c;
		}
		Log.i(TAG, "Not sign in yet");
		return null;
	}

	public Cursor getAccountProfpict(String pID) {
		Log.i(TAG, "Get account profpict");
		String[] projection = new String[] { COL_PROFPICT };

		Cursor c = db.query(TABLE_ACCOUNT, projection, COL_ID + "=?",
				new String[] { pID }, null, null, null);

		if (c.moveToFirst()) {
			Log.i(TAG, "profpict got");
			return c;
		}

		Log.i(TAG, "no profpict");
		return null;
	}
	
	
	
	public Cursor getAcountEntry() {

		String[] allColumns = new String[] { TBAccount.COL_ID, TBAccount.COL_ID_SHESOP, TBAccount.COL_ID_ACCOUNT, 
				TBAccount.COL_TELP_NUMBER, TBAccount.COL_NAME, TBAccount.COL_GENDER,
				TBAccount.COL_AGE, TBAccount.COL_HEIGHT, TBAccount.COL_WEIGHT,
				TBAccount.COL_SCORE, TBAccount.COL_TOKEN, TBAccount.COL_LEVEL, TBAccount.COL_IS_LOGIN, TBAccount.COL_EMAIL };

		  Cursor c = db.query(TBAccount.TABLE_ACCOUNT, allColumns, null, null, null,
		    null, null);
		  if (c != null) {
			   c.moveToFirst();
			  }
		  return c;
		 }
	
	
	
}
