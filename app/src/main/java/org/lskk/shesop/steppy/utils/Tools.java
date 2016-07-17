package org.lskk.shesop.steppy.utils;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lskk.shesop.steppy.data.Account;
import org.lskk.shesop.steppy.data.Friend;
import org.lskk.shesop.steppy.data.TBAccount;
import org.lskk.shesop.steppy.data.TBCalculate;
import org.lskk.shesop.steppy.data.TBFriends;
import org.lskk.shesop.steppy.data.TBGameMeasurement;
import org.lskk.shesop.steppy.data.TBHRCalculate;
import org.lskk.shesop.steppy.data.TBHRCalculateRecord;
import org.lskk.shesop.steppy.data.TBHeartRateSync;
import org.lskk.shesop.steppy.data.TBMeasurement;
import org.lskk.shesop.steppy.data.TBMeasurementBand;
import org.lskk.shesop.steppy.data.TBMeasurementType;
import org.lskk.shesop.steppy.data.TBStepSync;
import org.lskk.shesop.steppy.data.TBStepSyncBand;

import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class Tools {

	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	public static boolean isLogin(Context context) {
		TBAccount tbAccount = new TBAccount(context);
		tbAccount.open();

		Cursor cursor = tbAccount.getAccountLogin();
		boolean isLogin = (cursor != null);

		if (isLogin) {
			cursor.close();
		}

		tbAccount.close();

		return isLogin;
	}

	public static void toActivity(Context context, Class<?> cls,
			boolean isFinish) {
		Intent intent = new Intent(context, cls);
		context.startActivity(intent);

		if (isFinish) {
			((Activity) context).finish();
		}
	}

	public static String creteNewPictName() {
		return "steppy_profile_" + getCurrentTime("yyyyMMddHHmmss") + ".jpg";
	}

	public static String getGalleryPath(Uri uri, Activity activity) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = activity.getContentResolver().query(uri, projection,
				null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	public static byte[] bitmapToByteArray(Bitmap bitmap) {
		// //calculate how many bytes our image consists of.
		// //int bytes = bitmap.getByteCount();
		// //or we can calculate bytes this way. Use a different value than 4 if
		// you don't use 32bit images.
		// int bytes = bitmap.getWidth()*bitmap.getHeight()*4;
		//
		// ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
		// bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer
		//
		// byte[] array = buffer.array(); //Get the underlying array containing
		// the data.
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, stream);

		return stream.toByteArray();
	}

	public static byte[] getAccountProfpict(Context context) {
		byte[] bitmapArray = null;

		TBAccount tbAccount = new TBAccount(context);
		tbAccount.open();

		Cursor tCursor = tbAccount.getAccountProfpict(Account.getSingleton(context).getmLocalID());

		if (tCursor != null) {
			bitmapArray = tCursor.getBlob(tCursor
					.getColumnIndex(TBAccount.COL_PROFPICT));
			System.out.println("bitmapnya: " + bitmapArray);

			tCursor.close();
		}
		tbAccount.close();

		return bitmapArray;
	}

	@SuppressLint("InlinedApi")
	public static void logout(Context context) {

		// SharedPreferences state = context.getSharedPreferences("state", 0);
		// SharedPreferences.Editor stateEditor = state.edit();
		// stateEditor.putInt("steps", 0);
		// stateEditor.putInt("pace", 0);
		// stateEditor.putFloat("distance", 0);
		// stateEditor.putFloat("speed", 0);
		// stateEditor.putFloat("calories", 0);
		// stateEditor.commit();
	}

	public static ArrayList<String> getAllContacts(Context context) {
		ArrayList<String> tContactList = new ArrayList<String>();

		Cursor tCursor = context.getContentResolver().query(Phone.CONTENT_URI,
				new String[] { Phone._ID, Phone.NUMBER }, null, null, null);
		while (tCursor.moveToNext()) {
			tContactList.add(cleaningContacts(tCursor.getString(tCursor
					.getColumnIndex(Phone.NUMBER))));
		}
		if (tCursor != null)
			tCursor.close();

		return tContactList;
	}

	public static String cleaningContacts(String pContact) {

		pContact = pContact.replace("-", "");
		// if(pContact.charAt(0) == ('0')) {
		// pContact = pContact.replaceFirst("0", "+62");
		// }

		return pContact;
	}

	public static void contactSyncTools(Context context, JSONArray jArray) {
		try {

			TBFriends tbFriends = new TBFriends(context);
			tbFriends.open();

			for (int i = 0; i < jArray.length(); i++) {
				JSONObject jObject = jArray.getJSONObject(i);
				String tCurrTime = Tools.getCurrentTime("yyyy-MM-dd HH:mm:ss");

				ContentValues value = new ContentValues();
				value.put(TBFriends.COL_TELP_NUMBER,
						jObject.getString("TelpNumber"));
				value.put(TBFriends.COL_NAME, jObject.getString("DisplayName"));
				value.put(TBFriends.COL_LEVEL, jObject.getString("Level"));
				value.put(TBFriends.COL_HIGH_SCORE,
						jObject.getString("HiScore"));
				value.put(TBFriends.COL_LAST_MODIFIED, tCurrTime);
				value.put(TBFriends.COL_LAST_SYNC, tCurrTime);

				tbFriends.insert(value);
			}

			tbFriends.close();

		} catch (JSONException e) {
			Log.e(Account.TAG_SYNC_FRIENDS, e.getMessage());
		}
	}

	public static void friendSync(Context context, JSONArray jArray,
			Friend.Listener listener) {
		try {
			TBFriends tbFriends = new TBFriends(context);
			tbFriends.open();

			for (int i = 0; i < jArray.length(); i++) {
				JSONObject jObject = jArray.getJSONObject(i);
				String tCurrTime = Tools.getCurrentTime("yyyy-MM-dd HH:mm:ss");

				ContentValues value = new ContentValues();
				// value.put(TBFriends.COL_TELP_NUMBER,
				// jObject.getString("TelpNumber"));
				value.put(TBFriends.COL_TODAY_STEP,
						jObject.getString("TodayStep"));
				value.put(TBFriends.COL_WEEKLY_STEP,
						jObject.getString("WeeklyStep"));
				value.put(TBFriends.COL_HIGH_SCORE,
						jObject.getString("HiScore"));
				value.put(TBFriends.COL_LEVEL, jObject.getString("Level"));
				value.put(TBFriends.COL_LAST_MODIFIED, tCurrTime);
				value.put(TBFriends.COL_LAST_SYNC, tCurrTime);

				tbFriends.update(jObject.getString("TelpNumber"), value);
			}

			tbFriends.close();
			listener.dataChanged();
		} catch (JSONException e) {
			Log.e(Account.TAG_SYNC_FRIENDS, e.getMessage());
		}
	}

	public static String contactArraytoString(String pContacts) {
		pContacts = pContacts.replace(" ", "");
		pContacts = pContacts.replace("[", "");
		pContacts = pContacts.replace("]", "");

		return pContacts;
	}

	public static String getCurrentTime(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	public static String getTime(int field, int value, String format) {
		Calendar cal = Calendar.getInstance();
		cal.add(field, value);

		return new SimpleDateFormat(format).format(cal.getTime());
	}

	public static String convertTimeFormat(String oldFormat, String pDate,
			String newFormat) {
		try {
			Date date = new SimpleDateFormat(oldFormat).parse(pDate);

			return new SimpleDateFormat(newFormat).format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static long dateToMillis(String pDate, String pFormat) {
		SimpleDateFormat f = new SimpleDateFormat(pFormat);
		Date d = null;

		try {
			d = f.parse(pDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d.getTime();
	}

	public static String saveMeasurement(Context context, String pStep,
			String pSpeed, String distance, String calorie) {
		String tCurrTime = getCurrentTime("yyyy-MM-dd HH:mm:ss.SSS");

		TBMeasurement tbMeasurement = new TBMeasurement(context);
		tbMeasurement.open();

		ContentValues values = new ContentValues();
			values.clear();
			values.put(TBMeasurement.COL_DATE, tCurrTime);
			values.put(TBMeasurement.COL_VALUE, pStep);
			values.put(TBMeasurement.COL_CALORIE, calorie);
			values.put(TBMeasurement.COL_DISTANCE, distance);
			values.put(TBMeasurement.COL_MEASUREMENT_TYPE,
					TBMeasurementType.TYPE_STEP_MINUTES);
			values.put(TBMeasurement.COL_LAST_MODIFIED, tCurrTime);
			values.put(TBMeasurement.COL_IS_SYNC, "0");

			tbMeasurement.insert(values);
	
		tbMeasurement.close();

		return "";
	}
	
	
	
	
	
	public static String saveStepLocal(Context context, String pStep, String dis,
			String pCal, String timeStart, String timeEnd, String sensor, String date) {

		TBStepSync tbStepSync = new TBStepSync(context);
		tbStepSync.open();

		ContentValues values = new ContentValues();
			values.clear();
			values.put(TBStepSync.COL_STEP, pStep);
			values.put(TBStepSync.COL_CAL, pCal);
			values.put(TBStepSync.COL_DIS, dis);
			values.put(TBStepSync.COL_TIME_START, timeStart);
			values.put(TBStepSync.COL_TIME_END, timeEnd);
			values.put(TBStepSync.COL_SENSOR, sensor);
			values.put(TBStepSync.COL_DATE, date);
			tbStepSync.insert(values);
			tbStepSync.close();
		return "";
	}
	
	
	
	public static String saveStepBandLocal(Context context, String pStep,
			String pCal, String timeStart, String timeEnd, String distance, String date) {

		TBStepSyncBand tbStepSyncBand = new TBStepSyncBand(context);
		tbStepSyncBand.open();

		ContentValues values = new ContentValues();
			values.clear();
			values.put(TBStepSyncBand.COL_STEP, pStep);
			values.put(TBStepSyncBand.COL_CAL, pCal);
			values.put(TBStepSyncBand.COL_TIME_START, timeStart);
			values.put(TBStepSyncBand.COL_TIME_END, timeEnd);
			values.put(TBStepSyncBand.COL_DISTANCE, distance);
			values.put(TBStepSyncBand.COL_DATE, date);
			tbStepSyncBand.insert(values);
			tbStepSyncBand.close();
		return "";
	}
	
	
	
	
	public static String saveHeartRateLocal(Context context, String pHR,
			String timeStart, String timeEnd, String date, String speed, String pace, 
			String temperature, String uvlevel) {

		TBHeartRateSync tbHrSync = new TBHeartRateSync(context);
		tbHrSync.open();

		ContentValues values = new ContentValues();
			values.clear();
			values.put(TBHeartRateSync.COL_HR, pHR);
			values.put(TBHeartRateSync.COL_TIME_START, timeStart);
			values.put(TBHeartRateSync.COL_TIME_END, timeEnd);
			values.put(TBHeartRateSync.COL_SPEED, speed);
			values.put(TBHeartRateSync.COL_PACE, pace);
			values.put(TBHeartRateSync.COL_TEMPERATURE, temperature);
			values.put(TBHeartRateSync.COL_UV, uvlevel);
			values.put(TBHeartRateSync.COL_DATE, date);
			tbHrSync.insert(values);
			tbHrSync.close();
		return "";
	}
	
	
	
	
	

	public static void updateAccount(Context context, String id, int score, int level) {
		String tCurrTime = getCurrentTime("yyyy-MM-dd HH:mm:ss.SSS");
		TBAccount tbAccount = new TBAccount(context);
		tbAccount.open();

		ContentValues values = new ContentValues();

		values.clear();
		values.put(TBAccount.COL_ID_ACCOUNT, id);
		values.put(TBAccount.COL_SCORE, score);
		values.put(TBAccount.COL_LEVEL, level);
		tbAccount.insert(values);
		tbAccount.close();
	}

	public static String saveHeartRate(Context context, String hRate) {
		String tCurrTime = getCurrentTime("yyyy-MM-dd HH:mm:ss.SSS");

		TBMeasurement tbMeasurement = new TBMeasurement(context);
		tbMeasurement.open();

		ContentValues values = new ContentValues();

		if (hRate != null) {
			values.clear();
			values.put(TBMeasurement.COL_DATE, tCurrTime);
			values.put(TBMeasurement.COL_VALUE, hRate);
			values.put(TBMeasurement.COL_MEASUREMENT_TYPE,
					TBMeasurementType.TYPE_HEARTBEAT);
			values.put(TBMeasurement.COL_LAST_MODIFIED, tCurrTime);
			values.put(TBMeasurement.COL_IS_SYNC, "0");

			tbMeasurement.insert(values);
		}

		tbMeasurement.close();

		return "";
	}

	// TODO
	public static void getAllDataStep(Context context) {
		TBMeasurement tbMeasurement = new TBMeasurement(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getAllData();
		if (tCursor != null) {
			do {
				System.out.println("ID: "
						+ tCursor.getString(tCursor
								.getColumnIndex(TBMeasurement.COL_ID)));
				System.out.println("DATE: "
						+ tCursor.getString(tCursor
								.getColumnIndex(TBMeasurement.COL_DATE)));
				System.out.println("VALUE: "
						+ tCursor.getString(tCursor
								.getColumnIndex(TBMeasurement.COL_VALUE)));
			} while (tCursor.moveToNext());

			tCursor.close();
		}
		tbMeasurement.close();
	}

	// public static int getLastStep(Context context) {
	// boolean isNewDay = true;
	// int tLastStep = 0;
	//
	// TBMeasurement tbMeasurement = new TBMeasurement(context);
	// tbMeasurement.open();
	// Cursor tCursor = tbMeasurement.getLastInsert();
	// if(tCursor != null) {
	// String lastDate = convertTimeFormat("yyyy-MM-dd HH:mm:ss.SSS",
	// tCursor.getString(tCursor.getColumnIndex(TBMeasurement.COL_DATE)),
	// "yyyy-MM-dd");
	// String today = Tools.getCurrentTime("yyyy-MM-dd");
	// tLastStep =
	// tCursor.getInt(tCursor.getColumnIndex(TBMeasurement.COL_VALUE));
	//
	// isNewDay = !lastDate.equals(today);
	// }
	// if (tCursor != null) tCursor.close();
	//
	// tbMeasurement.close();
	//
	// if(isNewDay) {
	// return 0;
	// }
	//
	// return tLastStep;
	// }

	public static int getTodayStep(Context context) {
		int tTodayStep = 0;

		TBMeasurement tbMeasurement = new TBMeasurement(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getTodayStep();
		if (tCursor != null) {
			tTodayStep = tCursor.getInt(0);

			tCursor.close();
		}

		tbMeasurement.close();

		return tTodayStep;
	}
	
	public static int getTwoHoursStep(Context context) {
		int tStep = 0;

		TBMeasurement tbMeasurement = new TBMeasurement(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getTwoHoursSteps();
		if (tCursor != null) {
			tStep = tCursor.getInt(0);

			tCursor.close();
		}

		tbMeasurement.close();

		return tStep;
	}
	
	public static float getTwoHoursDistance(Context context) {
		float tDistance = 0;

		TBMeasurement tbMeasurement = new TBMeasurement(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getTwoHoursDistance();
		if (tCursor != null) {
			tDistance = tCursor.getFloat(0);
			tCursor.close();
		}

		tbMeasurement.close();

		return tDistance;
	}
	
	
	public static int getTodayCalorie(Context context) {
		int tTodayCalorie = 0;

		TBMeasurement tbMeasurement = new TBMeasurement(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getTodayCalorie();
		if (tCursor != null) {
			tTodayCalorie = tCursor.getInt(0);

			tCursor.close();
		}

		tbMeasurement.close();

		return tTodayCalorie;
	}
	
	
	public static float getTodayDistance(Context context) {
		float tTodayDistance = 0;

		TBMeasurement tbMeasurement = new TBMeasurement(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getTodayDistance();
		if (tCursor != null) {
			tTodayDistance = tCursor.getFloat(0);

			tCursor.close();
		}

		tbMeasurement.close();

		return tTodayDistance;
	}
	
	
	public static int getLastStep(Context context) {
		int tLastStep = 0;

		TBMeasurement tbMeasurement = new TBMeasurement(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getLastInsert();
		if (tCursor != null) {
			tLastStep = tCursor.getInt(0);

			tCursor.close();
		}

		tbMeasurement.close();

		return tLastStep;
	}
	
	
	
	
	
	
	
	
	

	public static int getLastWeekStep(Context context) {
		int tResult = 0;

		TBMeasurement tbMeasurement = new TBMeasurement(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getLastWeekStep();

		if (tCursor != null) {
			tResult = tCursor.getInt(0);

			tCursor.close();
		}
		tbMeasurement.close();

		return tResult;
	}
	
	
	public static String updateStepCalculate(Context context, String step, String cal, String Distance){
		TBCalculate tbCalculate = new TBCalculate(context);
		ContentValues values = new ContentValues();
		tbCalculate.open();
		values.put(TBCalculate.COL_CAL, cal);
		values.put(TBCalculate.COL_STEP, step);
		values.put(TBCalculate.COL_DIS, Distance);
		tbCalculate.update("1", values);
		tbCalculate.close();
		Log.i("TOOLS", "Updating step and Cal to DB");
		return "";
	}
	
	
	public static String updateStepCalculateBand(Context context, String step, String cal, String distance){
		TBHRCalculate tbCalculateBand = new TBHRCalculate(context);
		ContentValues values = new ContentValues();
		tbCalculateBand.open();
		values.put(TBHRCalculate.COL_CAL, cal);
		values.put(TBHRCalculate.COL_STEP, step);
		values.put(TBHRCalculate.COL_DISTANCE, distance);
		tbCalculateBand.update("1", values);
		tbCalculateBand.close();
		Log.d("Calculate HR", "Updating step and Cal to DB Band");
		return "";
	}
	
	public static String updateStartTimeCalculate(Context context,String startTime){
		TBCalculate tbCalculate = new TBCalculate(context);
		tbCalculate.open();
		ContentValues values = new ContentValues();
		values.put(TBCalculate.COL_TIME_START, startTime);
		tbCalculate.update("1", values);
		tbCalculate.close();
		Log.i("TOOLS", "Updating start Time to DB : "+startTime);
		return "";
	}
	
	
	public static String updateStartTimeCalculateBand(Context context,String startTime){
		TBHRCalculate tbCalculateBand = new TBHRCalculate(context);
		tbCalculateBand.open();
		ContentValues values = new ContentValues();
		values.put(TBHRCalculate.COL_TIME_START, startTime);
		tbCalculateBand.update("1", values);
		tbCalculateBand.close();
		Log.d("CALCULATE HR", "Updating start Time to DB");
		return "";
	}
	
	
	public static String updateCountingState(Context context, String state){
		TBCalculate tbCalculate = new TBCalculate(context);
		ContentValues values = new ContentValues();
		tbCalculate.open();
		values.put(TBCalculate.COL_IS_COUNTING, state);
		tbCalculate.update("1", values);
		tbCalculate.close();
		return "";
	}
	
	public static void updateGameCheckState(Context context, String state){
		TBCalculate tbCalculate = new TBCalculate(context);
		ContentValues values = new ContentValues();
		tbCalculate.open();
		values.put(TBCalculate.COL_STATE_GAME_CHECK, state);
		tbCalculate.update("1", values);
		tbCalculate.close();
	}
	
	
	public static int getLastCalculateStep(Context context) {
		int tLastStep = 0;

		TBCalculate tbCalculate = new TBCalculate(context);
		tbCalculate.open();
		Cursor tCursor = tbCalculate.getSteps();
		if (tCursor != null) {
			tLastStep = tCursor.getInt(1);

			tCursor.close();
		}

		tbCalculate.close();

		return tLastStep;
	}
	
	
	public static int getLastCalculateStepBand(Context context) {
		int tLastStep = 0;

		TBHRCalculate tbHRCalculate = new TBHRCalculate(context);
		tbHRCalculate.open();
		Cursor tCursor = tbHRCalculate.getSteps();
		if (tCursor != null) {
			tLastStep = tCursor.getInt(1);

			tCursor.close();
		}

		tbHRCalculate.close();

		return tLastStep;
	}
	
	
	
	public static String getLastCalculateCalBand(Context context) {
		String tLastCal = "";

		TBHRCalculate tbHRCalculate = new TBHRCalculate(context);
		tbHRCalculate.open();
		Cursor tCursor = tbHRCalculate.getSteps();
		if (tCursor != null) {
			tLastCal = tCursor.getString(3);

			tCursor.close();
		}

		tbHRCalculate.close();

		return tLastCal;
	}
	
	
	public static String getLastCalculateDistanceBand(Context context) {
		String tLastDistance = "";

		TBHRCalculate tbHRCalculate = new TBHRCalculate(context);
		tbHRCalculate.open();
		Cursor tCursor = tbHRCalculate.getSteps();
		if (tCursor != null) {
			tLastDistance = tCursor.getString(2);

			tCursor.close();
		}

		tbHRCalculate.close();

		return tLastDistance;
	}
	
	
	public static String getStartTimeBand(Context context) {
		String startTime = "";

		TBHRCalculate tbHRCalculate = new TBHRCalculate(context);
		tbHRCalculate.open();
		Cursor tCursor = tbHRCalculate.getSteps();
		if (tCursor != null) {
			startTime = tCursor.getString(5);

			tCursor.close();
		}

		tbHRCalculate.close();

		return startTime;
	}
	

	
	
	public static float getLastCalculateCal(Context context) {
		String tLastCal = "0";

		TBCalculate tbCalculate = new TBCalculate(context);
		tbCalculate.open();
		Cursor tCursor = tbCalculate.getSteps();
		if (tCursor != null) {
			tLastCal = tCursor.getString(2);

			tCursor.close();
		}

		tbCalculate.close();

		return Float.parseFloat(tLastCal);
	}
	
	
	public static float getLastCalculateDis(Context context) {
		String tLastDist = "";

		TBCalculate tbCalculate = new TBCalculate(context);
		tbCalculate.open();
		Cursor tCursor = tbCalculate.getSteps();
		if (tCursor != null) {
			tLastDist = tCursor.getString(6);

			tCursor.close();
		}

		tbCalculate.close();

		return Float.parseFloat(tLastDist);
	}
	
	
	
	
	
	public static int getIsCountingState(Context context) {
		int isCounting = 0;

		TBCalculate tbCalculate = new TBCalculate(context);
		tbCalculate.open();
		Cursor tCursor = tbCalculate.getSteps();
		if (tCursor != null) {
			isCounting = tCursor.getInt(5);

			tCursor.close();
		}

		tbCalculate.close();

		return isCounting;
	}
	
	
	public static int getGameCheckState(Context context) {
		int isGameCheck = 0;

		TBCalculate tbCalculate = new TBCalculate(context);
		tbCalculate.open();
		Cursor tCursor = tbCalculate.getSteps();
		if (tCursor != null) {
			isGameCheck = tCursor.getInt(7);

			tCursor.close();
		}

		tbCalculate.close();

		return isGameCheck;
	}
	
	
	public static String getStartTime(Context context) {
		String startTime = "";

		TBCalculate tbCalculate = new TBCalculate(context);
		tbCalculate.open();
		Cursor tCursor = tbCalculate.getSteps();
		if (tCursor != null) {
			startTime = tCursor.getString(3);

			tCursor.close();
		}

		tbCalculate.close();

		return startTime;
	}
	
	
	public static void deleteSyncStep(Context context, String id){
		TBStepSync tbStepSync = new TBStepSync(context);
		tbStepSync.open();
		tbStepSync.delete(id);
		tbStepSync.close();
	}
	
	
	public static int getLastIDSyncStep(Context context) {
		int lastID = 0;

		TBStepSync tbSyncStep = new TBStepSync(context);
		tbSyncStep.open();
		Cursor tCursor = tbSyncStep.getIdSteps();
		if (tCursor != null && tCursor.moveToFirst()) {
			lastID = tCursor.getInt(0);
		}
		else
			lastID = 0;
		tCursor.close();
		tbSyncStep.close();

		return lastID;
	}
	
	
	
	public static void deleteSyncStepBand(Context context, String id){
		TBStepSyncBand tbStepSyncBand = new TBStepSyncBand(context);
		tbStepSyncBand.open();
		tbStepSyncBand.delete(id);
		tbStepSyncBand.close();
	}
	
	
	public static int getLastIDSyncStepBand(Context context) {
		int lastID = 0;

		TBStepSyncBand tbSyncStep = new TBStepSyncBand(context);
		tbSyncStep.open();
		Cursor tCursor = tbSyncStep.getIdSteps();
		if (tCursor != null && tCursor.moveToFirst()) {
			lastID = tCursor.getInt(0);
		}
		else
			lastID = 0;
		tCursor.close();
		tbSyncStep.close();

		return lastID;
	}
	
	
	
	
	
	public static int getSyncStepIdCount(Context context) {
		int lastID = 0;

		TBStepSync tbSyncStep = new TBStepSync(context);
		tbSyncStep.open();
		Cursor tCursor = tbSyncStep.getIdSteps();
		if (tCursor != null && tCursor.moveToFirst()) {
			lastID = tCursor.getCount();
		}
		else
			lastID = 0;
		tCursor.close();
		tbSyncStep.close();

		return lastID;
	}
	
	
	
	public static int getSyncStepBandIdCount(Context context) {
		int lastID = 0;

		TBStepSyncBand tbSyncStep = new TBStepSyncBand(context);
		tbSyncStep.open();
		Cursor tCursor = tbSyncStep.getIdSteps();
		if (tCursor != null && tCursor.moveToFirst()) {
			lastID = tCursor.getCount();
		}
		else
			lastID = 0;
		tCursor.close();
		tbSyncStep.close();

		return lastID;
	}
	
	
	
	
	
	
	public static String getLastDataStringSyncStep(Context context, int pos) {
		String lastData = "";

		TBStepSync tbSyncStep = new TBStepSync(context);
		tbSyncStep.open();
		Cursor tCursor = tbSyncStep.getIdSteps();
		if (tCursor != null) {
			lastData = tCursor.getString(pos);

			tCursor.close();
		}

		tbSyncStep.close();

		return lastData;
	}
	
	
	
	
	
	public static String getLastDataStringSyncStepBand(Context context, int pos) {
		String lastData = "";

		TBStepSyncBand tbSyncStep = new TBStepSyncBand(context);
		tbSyncStep.open();
		Cursor tCursor = tbSyncStep.getIdSteps();
		if (tCursor != null) {
			lastData = tCursor.getString(pos);

			tCursor.close();
		}

		tbSyncStep.close();

		return lastData;
	}
	
	
	
	
	
	
	public static int getLastDataIntSyncStep(Context context, int pos) {
		int lastData = 0;

		TBStepSync tbSyncStep = new TBStepSync(context);
		tbSyncStep.open();
		Cursor tCursor = tbSyncStep.getIdSteps();
		if (tCursor != null) {
			lastData = tCursor.getInt(pos);

			tCursor.close();
		}

		tbSyncStep.close();

		return lastData;
	}

	
	
	
	public static int getLastDataIntSyncStepBand(Context context, int pos) {
		int lastData = 0;

		TBStepSyncBand tbSyncStep = new TBStepSyncBand (context);
		tbSyncStep.open();
		Cursor tCursor = tbSyncStep.getIdSteps();
		if (tCursor != null) {
			lastData = tCursor.getInt(pos);

			tCursor.close();
		}

		tbSyncStep.close();

		return lastData;
	}
	
	
	
	
	
	
	
	
	public static int getSyncHeartRateIdCount(Context context) {
		int lastID = 0;

		TBHeartRateSync tbHR = new TBHeartRateSync(context);
		tbHR.open();
		Cursor tCursor = tbHR.getIdHR();
		if (tCursor != null && tCursor.moveToFirst()) {
			lastID = tCursor.getCount();
		}
		else
			lastID = 0;
		tCursor.close();
		tbHR.close();

		return lastID;
	}
	
	
	
	public static void deleteSyncHeartRate(Context context, String id){
		TBHeartRateSync tbHR = new TBHeartRateSync(context);
		tbHR.open();
		tbHR.delete(id);
		tbHR.close();
	}
	
	
	public static int getLastIDSyncHeartRate(Context context) {
		int lastID = 0;

		TBHeartRateSync tbHR = new TBHeartRateSync(context);
		tbHR.open();
		Cursor tCursor = tbHR.getIdHR();
		if (tCursor != null && tCursor.moveToFirst()) {
			lastID = tCursor.getInt(0);
		}
		else
			lastID = 0;
		tbHR.close();
		tbHR.close();

		return lastID;
	}
	
	
	public static String getLastDataStringSyncHeartRate(Context context, int pos) {
		String lastData = "";

		TBHeartRateSync tbHR = new TBHeartRateSync(context);
		tbHR.open();
		Cursor tCursor = tbHR.getIdHR();
		if (tCursor != null) {
			lastData = tCursor.getString(pos);

			tCursor.close();
		}

		tbHR.close();

		return lastData;
	}
	
	
	public static int getLastDataIntSyncHeartRate(Context context, int pos) {
		int lastData = 0;

		TBHeartRateSync tbHR = new TBHeartRateSync(context);
		tbHR.open();
		Cursor tCursor = tbHR.getIdHR();
		if (tCursor != null) {
			lastData = tCursor.getInt(pos);

			tCursor.close();
		}

		tbHR.close();

		return lastData;
	}
	
	
	
	
	public static int getTodayStepBand(Context context) {
		int tTodayStep = 0;

		TBMeasurementBand tbMeasurement = new TBMeasurementBand(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getTodayStep();
		if (tCursor != null) {
			tTodayStep = tCursor.getInt(0);

			tCursor.close();
		}

		tbMeasurement.close();

		return tTodayStep;
	}
	
	public static int getTwoHoursStepBand(Context context) {
		int tStep = 0;

		TBMeasurementBand tbMeasurement = new TBMeasurementBand(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getTwoHoursSteps();
		if (tCursor != null) {
			tStep = tCursor.getInt(0);

			tCursor.close();
		}

		tbMeasurement.close();

		return tStep;
	}
	
	public static float getTwoHoursDistanceBand(Context context) {
		float tDistance = 0;

		TBMeasurementBand tbMeasurement = new TBMeasurementBand(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getTwoHoursDistance();
		if (tCursor != null) {
			tDistance = tCursor.getFloat(0);
			tCursor.close();
		}

		tbMeasurement.close();

		return tDistance;
	}
		
	public static float getTodayDistanceBand(Context context) {
		float tTodayDistance = 0;

		TBMeasurementBand tbMeasurement = new TBMeasurementBand(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getTodayDistance();
		if (tCursor != null) {
			tTodayDistance = tCursor.getFloat(0);

			tCursor.close();
		}

		tbMeasurement.close();

		return tTodayDistance;
	}
	
	public static int getTodayCalorieBand(Context context) {
		int tTodayCalorie = 0;

		TBMeasurementBand tbMeasurement = new TBMeasurementBand(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getTodayCalorie();
		if (tCursor != null) {
			tTodayCalorie = tCursor.getInt(0);

			tCursor.close();
		}

		tbMeasurement.close();

		return tTodayCalorie;
	}
	
	
	public static String saveMeasurementBand(Context context, String pStep, String distance, String calorie) {
		String tCurrTime = getCurrentTime("yyyy-MM-dd HH:mm:ss.SSS");

		TBMeasurementBand tbMeasurement = new TBMeasurementBand(context);
		tbMeasurement.open();

		ContentValues values = new ContentValues();
			values.clear();
			values.put(TBMeasurementBand.COL_DATE, tCurrTime);
			values.put(TBMeasurementBand.COL_STEP, pStep);
			values.put(TBMeasurementBand.COL_CALORIE, calorie);
			values.put(TBMeasurementBand.COL_DISTANCE, distance);
			values.put(TBMeasurementBand.COL_MEASUREMENT_TYPE,
					TBMeasurementType.TYPE_STEP_MINUTES);
			tbMeasurement.insert(values);
	
		tbMeasurement.close();

		return "";
	}
	
	
	
	public static int getLastCalculateStepBandRecord(Context context) {
		int tLastStep = 0;

		TBHRCalculateRecord tbHRCalculate = new TBHRCalculateRecord(context);
		tbHRCalculate.open();
		Cursor tCursor = tbHRCalculate.getSteps();
		if (tCursor != null) {
			tLastStep = tCursor.getInt(1);

			tCursor.close();
		}

		tbHRCalculate.close();

		return tLastStep;
	}
	
	
	
	public static String getLastCalculateCalBandRecord(Context context) {
		String tLastCal = "";
		TBHRCalculateRecord tbHRCalculate = new TBHRCalculateRecord(context);
		tbHRCalculate.open();
		Cursor tCursor = tbHRCalculate.getSteps();
		if (tCursor != null) {
			tLastCal = tCursor.getString(3);
			tCursor.close();
		}
		tbHRCalculate.close();
		return tLastCal;
	}
	
	
	public static String getLastCalculateDistanceBandRecord(Context context) {
		String tLastDistance = "";
		TBHRCalculateRecord tbHRCalculate = new TBHRCalculateRecord(context);
		tbHRCalculate.open();
		Cursor tCursor = tbHRCalculate.getSteps();
		if (tCursor != null) {
			tLastDistance = tCursor.getString(2);
			tCursor.close();
		}
		tbHRCalculate.close();
		return tLastDistance;
	}
	
	
	public static String updateStepCalculateBandRecord(Context context, String step, String cal, String distance){
		TBHRCalculateRecord tbCalculateBand = new TBHRCalculateRecord(context);
		ContentValues values = new ContentValues();
		tbCalculateBand.open();
		values.put(TBHRCalculateRecord.COL_CAL, cal);
		values.put(TBHRCalculateRecord.COL_STEP, step);
		values.put(TBHRCalculateRecord.COL_DISTANCE, distance);
		tbCalculateBand.update("1", values);
		tbCalculateBand.close();
		return "";
	}
	
	public static void deleteGameMeasurementBand(Context context){
		TBMeasurementBand tbGameM = new TBMeasurementBand(context);
		tbGameM.open();
		tbGameM.delete();
		tbGameM.close();
	}
}
