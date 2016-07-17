package org.lskk.shesop.steppy;

import org.lskk.shesop.steppy.data.DBLocalHelper;
import org.lskk.shesop.steppy.data.TBAccount;
import org.lskk.shesop.steppy.data.TBCalculate;
import org.lskk.shesop.steppy.data.TBGameRecorcd;
import org.lskk.shesop.steppy.data.TBHeartRateSync;
import org.lskk.shesop.steppy.data.TBStepSync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SQLController {

// private TBStepSync dbhelper;
 private DBLocalHelper dbLocalHelper;
 private Context ourcontext;
 private SQLiteDatabase database;
 
 public SQLController(Context c) {
  ourcontext = c;
 }

 public SQLController open() throws SQLException {
	 dbLocalHelper = new DBLocalHelper(ourcontext);
	 database = dbLocalHelper.getReadableDatabase();
  return this;

 }

 public void close() {
	 dbLocalHelper.close();
 }



/* public Cursor readEntry() {

  String[] allColumns = new String[] { TBStepSync.COL_ID, TBStepSync.COL_STEP,
    TBStepSync.COL_CAL, TBStepSync.COL_TIME_START, TBStepSync.COL_TIME_END, TBStepSync.COL_SENSOR,
    TBStepSync.COL_DATE};

  Cursor c = database.query(TBStepSync.TABLE_STEP, allColumns, null, null, null,
    null, TBStepSync.COL_ID + " DESC");
  if (c.moveToLast()) {
		return c;
	}
  
  return null;

 } */
 
 
 public Cursor readEntry() {

	  String[] allColumns = new String[] { TBStepSync.COL_ID, TBStepSync.COL_STEP,
	    TBStepSync.COL_CAL, TBStepSync.COL_TIME_START, TBStepSync.COL_TIME_END, TBStepSync.COL_SENSOR,
	    TBStepSync.COL_DATE};

	  Cursor c = database.query(TBStepSync.TABLE_STEP, allColumns, null, null, null,
	    null, null);
	  if (c != null) {
		   c.moveToFirst();
		  }
	  return c;

	 }
 
 
 
 public Cursor readEntry2(){
	 String[] allColumns = new String[] { TBStepSync.COL_ID, TBStepSync.COL_STEP,
			    TBStepSync.COL_CAL, TBStepSync.COL_TIME_START, TBStepSync.COL_TIME_END};
	 Cursor c = database.query(TBCalculate.TABLE_CALCULATE, allColumns, null, null, null,
			    null, TBStepSync.COL_ID + " DESC");
	 if (c.moveToFirst()) {
			return c;
		}
	 return null;
 }
 
 
 public Cursor readEntry3(){
	 String[] allColumns = new String[] { TBHeartRateSync.COL_ID, TBHeartRateSync.COL_HR,
			 TBHeartRateSync.COL_DATE, TBHeartRateSync.COL_TIME_START, TBHeartRateSync.COL_TIME_END,
			 TBHeartRateSync.COL_SPEED, TBHeartRateSync.COL_PACE, TBHeartRateSync.COL_TEMPERATURE,
			 TBHeartRateSync.COL_UV};
	 Cursor c = database.query(TBHeartRateSync.TABLE_HR, allColumns, null, null, null,
			    null, TBHeartRateSync.COL_ID + " DESC");
	 if (c.moveToFirst()) {
			return c;
		}
	 return null;
 }
 
 
 public Cursor readEntry4(){
	 String[] allColumns = new String[] { TBGameRecorcd.COL_ID, TBGameRecorcd.COL_MISSION_1,
			 TBGameRecorcd.COL_MISSION_2, TBGameRecorcd.COL_MISSION_3, TBGameRecorcd.COL_MISSION_4,
			 TBGameRecorcd.COL_LEVEL, TBGameRecorcd.COL_POINT};
	 Cursor c = database.query(TBGameRecorcd.TABLE_RECORD, allColumns, null, null, null,
			    null, TBGameRecorcd.COL_ID + " DESC");
	 if (c.moveToFirst()) {
			return c;
		}
	 return null;
 }
 
 
 public Cursor readEntry5(){
	 String[] allColumns = new String[] { TBAccount.COL_ID, TBAccount.COL_ID_SHESOP, TBAccount.COL_ID_ACCOUNT, 
			 TBAccount.COL_TELP_NUMBER, TBAccount.COL_NAME, TBAccount.COL_GENDER,
			 TBAccount.COL_AGE, TBAccount.COL_HEIGHT, TBAccount.COL_WEIGHT,
			 TBAccount.COL_SCORE, TBAccount.COL_LEVEL, TBAccount.COL_IS_LOGIN };
	 Cursor c = database.query(TBAccount.TABLE_ACCOUNT, allColumns, null, null, null,
			    null, TBAccount.COL_ID + " DESC");
	 if (c.moveToFirst()) {
			return c;
		}
	 return null;
 }
 
 
 
 public Cursor getLast() {

	  String[] allColumns = new String[] { TBStepSync.COL_ID, TBStepSync.COL_STEP,
	    TBStepSync.COL_CAL, TBStepSync.COL_TIME_START, TBStepSync.COL_TIME_END, TBStepSync.COL_SENSOR,
	    TBStepSync.COL_DATE};

	  Cursor c = database.query(TBStepSync.TABLE_STEP, allColumns, null, null, null,
	    null, TBStepSync.COL_ID + " DESC");
	  if (c.moveToFirst()) {
			return c;
		}
	  
	  return null;

	 }
 
 
 

}