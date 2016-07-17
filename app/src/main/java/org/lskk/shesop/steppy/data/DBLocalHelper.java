package org.lskk.shesop.steppy.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/***
 * 
 * For debug purpose use this command to get database from device <br>
 * <code> adb -d shell 'run-as org.shesop.steppy cat /data/data/org.shesop.steppy/databases/steppylocal.db > /sdcard/steppylocal.db'</code></br>
 * <code> adb pull /sdcard/steppylocal.db ~/</code>
 *  
 * @author Shesop Team
 *  
 */
public class DBLocalHelper extends SQLiteOpenHelper {

	private String TAG = "Local DB";
	
	private static final String DATABASE_NAME = "steppylocal.db";
	private static final int DATABASE_VERSION = 1;
	
	private static DBLocalHelper mInstance;

	public DBLocalHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public static DBLocalHelper getInstance(Context context) {
		if(mInstance == null) {
			mInstance = new DBLocalHelper(context);
		}
		
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "Creating database " +DATABASE_NAME);
		
		Log.i(TAG, "Creating table " + TBAccount.TABLE_ACCOUNT);
		db.execSQL(TBAccount.INITIAL_CREATE);
		
		Log.i(TAG, "Creating table " + TBFriends.TABLE_FRIENDS);
		db.execSQL(TBFriends.INITIAL_CREATE);
		
		Log.i(TAG, "Creating table " + TBMeasurementType.TABLE_MEASUREMENT_TYPE);
		db.execSQL(TBMeasurementType.INITIAL_CREATE);
		
		Log.i(TAG, "Inserting to table " + TBMeasurementType.TABLE_MEASUREMENT_TYPE);
		db.execSQL(TBMeasurementType.INITIAL_INSERT);
		
		Log.i(TAG, "Creating table " + TBGameMeasurement.TABLE_GAME_MEASUREMENT);
		db.execSQL(TBGameMeasurement.INITIAL_CREATE);
		
		Log.i(TAG, "Creating table " + TBMeasurement.TABLE_MEASUREMENT);
		db.execSQL(TBMeasurement.INITIAL_CREATE);
		
		Log.i(TAG, "Creating table " + TBMeasurementBand.TABLE_MEASUREMENT_BAND);
		db.execSQL(TBMeasurementBand.INITIAL_CREATE);
		
		Log.i(TAG, "Creating table " + TBStepSync.TABLE_STEP);
		db.execSQL(TBStepSync.INITIAL_CREATE);
		
		Log.i(TAG, "Creating table " + TBStepSyncBand.TABLE_STEP_BAND);
		db.execSQL(TBStepSyncBand.INITIAL_CREATE);
		
		Log.i(TAG, "Creating table " + TBCalculate.TABLE_CALCULATE);
		db.execSQL(TBCalculate.INITIAL_CREATE);
		
		Log.i(TAG, "Creating table " + TBHRCalculate.TABLE_HR_CALCULATE);
		db.execSQL(TBHRCalculate.INITIAL_CREATE);
		
		Log.i(TAG, "Creating table " + TBHRCalculateRecord.TABLE_HR_CALCULATE_RECORD);
		db.execSQL(TBHRCalculateRecord.INITIAL_CREATE);
		
		Log.i(TAG, "Creating table " + TBHeartRateSync.TABLE_HR);
		db.execSQL(TBHeartRateSync.INITIAL_CREATE);
		
		Log.i(TAG, "Creating table " + TBMission.TABLE_MISSION);
		db.execSQL(TBMission.INITIAL_CREATE);
		
		Log.i(TAG, "Creating table " + TBGameRecorcd.TABLE_RECORD);
		db.execSQL(TBGameRecorcd.INITIAL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TBAccount.TABLE_ACCOUNT);
		onCreate(db);
	}

}
