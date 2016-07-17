package org.lskk.shesop.steppy.utils;

import org.lskk.shesop.steppy.data.TBGameRecorcd;
import org.lskk.shesop.steppy.data.TBMission;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Mission {

	
	public static void InsertMission(Context context, String idlevel, String mission1, String mission2, 
			String mission3, String mission4){
		
		TBMission tbMission = new TBMission(context);
		tbMission.open();
		ContentValues values = new ContentValues();
		values.put(TBMission.COL_IDLEVEL, idlevel);
		values.put(TBMission.COL_MISSION_1, mission1);
		values.put(TBMission.COL_MISSION_2, mission2);
		values.put(TBMission.COL_MISSION_3, mission3);
		values.put(TBMission.COL_MISSION_4, mission4);
		tbMission.insert(values);
		tbMission.close();
	}
	
	
	public static String getDetailMission(Context context, int rowPos, int dataPos){
		String data = "";
		
		TBMission tbMission = new TBMission(context);
		tbMission.open();
		Cursor tCursor = tbMission.getIdMission(rowPos);
		if (tCursor != null) {
			data = tCursor.getString(dataPos);
			tCursor.close();
		}
		tbMission.close();
		return data;
	}
	
	
	
	public static String getLastValueLevel(Context context){
		String lastValueLevel = "0";
		
		TBMission tbMission = new TBMission(context);
		tbMission.open();
		Cursor cursor = tbMission.getIdRecordLast();
		if(cursor != null){
			lastValueLevel = cursor.getString(1);
			cursor.close();
		} 
		tbMission.close();
		return lastValueLevel;
	}
	
	
	public static void updateMissionDetail(Context context, String idLevel,
			String mission1, String mission2, String mission3, String mission4, String id){
		
		TBMission tbMission = new TBMission(context);
		ContentValues values = new ContentValues();
		tbMission.open();
		values.put(TBMission.COL_IDLEVEL, idLevel);
		values.put(TBMission.COL_MISSION_1, mission1);
		values.put(TBMission.COL_MISSION_2, mission2);
		values.put(TBMission.COL_MISSION_3, mission3);
		values.put(TBMission.COL_MISSION_4, mission4);
		tbMission.update(id, values);
		tbMission.close();
	}
	
}
