package org.lskk.shesop.steppy.utils;

import org.lskk.shesop.steppy.data.TBGameMeasurement;
import org.lskk.shesop.steppy.data.TBGameRecorcd;
import org.lskk.shesop.steppy.data.TBMeasurement;
import org.lskk.shesop.steppy.data.TBMeasurementType;
import org.lskk.shesop.steppy.data.TBMission;
import org.lskk.shesop.steppy.data.TBStepSync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Record {

	
	public static void insertRecord(Context context, String idUserShesop, String date, String valueMission1,
			String valueMission2, String valueMission3, String valueMission4, String valuePoint, String valueLevel){
		TBGameRecorcd tbRecord =  new TBGameRecorcd(context);
		tbRecord.open();
		ContentValues values = new ContentValues();
		values.put(TBGameRecorcd.COL_ID_USER, idUserShesop);
		values.put(TBGameRecorcd.COL_DATE, date);
		values.put(TBGameRecorcd.COL_MISSION_1, valueMission1);
		values.put(TBGameRecorcd.COL_MISSION_2, valueMission2);
		values.put(TBGameRecorcd.COL_MISSION_3, valueMission3);
		values.put(TBGameRecorcd.COL_MISSION_4, valueMission4);
		values.put(TBGameRecorcd.COL_LEVEL, valueLevel);
		values.put(TBGameRecorcd.COL_POINT, valuePoint);
		tbRecord.insert(values);
		tbRecord.close();
	}
	
	public static String getLastValueMission1(Context context){
		String lastValueMission1 = "0";
		
		TBGameRecorcd tbRecord = new TBGameRecorcd(context);
		tbRecord.open();
		Cursor cursor = tbRecord.getIdRecordLast();
		if(cursor != null){
			lastValueMission1 = cursor.getString(2);
			cursor.close();
		} 
		tbRecord.close();
		
		return lastValueMission1;
	}
	
	
	public static String getLastValueMission2(Context context){
		String lastValueMission2 = "0";
		
		TBGameRecorcd tbRecord = new TBGameRecorcd(context);
		tbRecord.open();
		Cursor cursor = tbRecord.getIdRecordLast();
		if(cursor != null){
			lastValueMission2 = cursor.getString(3);
			cursor.close();
		} 
		tbRecord.close();
		
		return lastValueMission2;
	}
	
	
	public static String getLastValueMission3(Context context){
		String lastValueMission3 = "0";
		
		TBGameRecorcd tbRecord = new TBGameRecorcd(context);
		tbRecord.open();
		Cursor cursor = tbRecord.getIdRecordLast();
		if(cursor != null){
			lastValueMission3 = cursor.getString(4);
			cursor.close();
		} 
		tbRecord.close();
		
		return lastValueMission3;
	}
	
	
	public static String getLastValueMission4(Context context){
		String lastValueMission4 = "0";
		
		TBGameRecorcd tbRecord = new TBGameRecorcd(context);
		tbRecord.open();
		Cursor cursor = tbRecord.getIdRecordLast();
		if(cursor != null){
			lastValueMission4 = cursor.getString(5);
			cursor.close();
		} 
		tbRecord.close();
		
		return lastValueMission4;
	}
	
	
	public static String getLastValueLevel(Context context){
		String lastValueLevel = "0";
		
		TBGameRecorcd tbRecord = new TBGameRecorcd(context);
		tbRecord.open();
		Cursor cursor = tbRecord.getIdRecordLast();
		if(cursor != null){
			lastValueLevel = cursor.getString(6);
			cursor.close();
		} 
		tbRecord.close();
		return lastValueLevel;
	}
	
	
	public static String getLastValuePoint(Context context){
		String lastValuePoint = "0";
		
		TBGameRecorcd tbRecord = new TBGameRecorcd(context);
		tbRecord.open();
		Cursor cursor = tbRecord.getIdRecordLast();
		if(cursor != null){
			lastValuePoint = cursor.getString(7);
			cursor.close();
		} 
		tbRecord.close();
		return lastValuePoint;
	}
	
	
	public static void deleteRecord(Context context, String id){
		TBGameRecorcd tbRecord = new TBGameRecorcd(context);
		tbRecord.open();
		tbRecord.delete(id);
		tbRecord.close();
	}
	
	public static int getFirstId(Context context){
		int firstId = 0;
		
		TBGameRecorcd tbRecord = new TBGameRecorcd(context);
		tbRecord.open();
		Cursor cursor = tbRecord.getIdRecordFirst();
		if(cursor != null){
			firstId = cursor.getInt(0);
			cursor.close();
		}
		tbRecord.close();
		
		return firstId;
	}
	
	
	public static int getRecordCount(Context context){
		int recordCount = 0;
		
		TBGameRecorcd tbRecord = new TBGameRecorcd(context);
		tbRecord.open();
		Cursor cursor = tbRecord.getIdRecordFirst();
		if(cursor != null){
			recordCount = cursor.getCount();
			cursor.close();
		}
		
		tbRecord.close();
		return recordCount;
		
	}
	
	
	
	public static String getDetailRecord(Context context, int rowPos, int dataPos){
		String data = "";
		
		TBGameRecorcd tbRecord = new TBGameRecorcd(context);
		tbRecord.open();
		Cursor tCursor = tbRecord.getIdRecordPosition(rowPos);
		if (tCursor != null) {
			data = tCursor.getString(dataPos);
			tCursor.close();
		}
		tbRecord.close();
		return data;
	}
	
	
	
	
	
	public static String saveGameMeasurement(Context context, String pStep,
			String pSpeed, String distance, String calorie) {
		String tCurrTime = Tools.getCurrentTime("yyyy-MM-dd HH:mm:ss.SSS");

		TBGameMeasurement tbMeasurement = new TBGameMeasurement(context);
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
	
	
	public static int getTodayStep(Context context) {
		int tTodayStep = 0;

		TBGameMeasurement tbMeasurement = new TBGameMeasurement(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getTodayStep();
		if (tCursor != null) {
			tTodayStep = tCursor.getInt(0);

			tCursor.close();
		}

		tbMeasurement.close();

		return tTodayStep;
	}
	
	
	public static float getTodayDistance(Context context) {
		float tTodayDistance = 0;

		TBGameMeasurement tbMeasurement = new TBGameMeasurement(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getTodayDistance();
		if (tCursor != null) {
			tTodayDistance = tCursor.getFloat(0);

			tCursor.close();
		}

		tbMeasurement.close();

		return tTodayDistance;
	}
	
	
	public static int getTwoHoursStep(Context context) {
		int tStep = 0;

		TBGameMeasurement tbMeasurement = new TBGameMeasurement(context);
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

		TBGameMeasurement tbMeasurement = new TBGameMeasurement(context);
		tbMeasurement.open();
		Cursor tCursor = tbMeasurement.getTwoHoursDistance();
		if (tCursor != null) {
			tDistance = tCursor.getFloat(0);
			tCursor.close();
		}

		tbMeasurement.close();

		return tDistance;
	}
	
	public static void deleteGameMeasurement(Context context){
		TBGameMeasurement tbGameM = new TBGameMeasurement(context);
		tbGameM.open();
		tbGameM.delete();
		tbGameM.close();
	}
}
