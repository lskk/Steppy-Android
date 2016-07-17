package org.lskk.shesop.steppy.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.lskk.shesop.steppy.connection.AccountHandler;
import org.lskk.shesop.steppy.connection.IConnectionResponseHandler;
import org.lskk.shesop.steppy.utils.Tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

public class Account {
	
	public static final String TAG_SYNC_FRIENDS = "[Account] Sync friends";
	
	public static Account sAccount;
	
	private Context mContext;
	private String mGlobalID;
	private String mLocalID;
	private String mShesopID;
	private String mTelpNumber;
	private String mName;
	private String mGender;
	private String mAge;
	private String mHeight;
	private String mWeight;
	private String mScore;
	private String mLevel;
	private String mToken;
	
	/**
	 * Get Singleton object of Account class
	 * @return
	 */
	public static Account getSingleton(Context context) {
        if (sAccount == null) {
        	sAccount = new Account(context);           
        } 
        
        return sAccount;        
    }
	
	public static void logout() {
		sAccount = null;
	}
	
	public Account(Context context) {
		this.mContext = context;
		
		Cursor cursor = getActiveAccount(context);
		
		if(cursor != null) {
			this.setmLocalID(cursor.getString(cursor.getColumnIndex(TBAccount.COL_ID)));
			this.setmGlobalID(cursor.getString(cursor.getColumnIndex(TBAccount.COL_ID_ACCOUNT)));
			this.setmShesopID(cursor.getString(cursor.getColumnIndex(TBAccount.COL_ID_SHESOP)));
			this.mTelpNumber= cursor.getString(cursor.getColumnIndex(TBAccount.COL_TELP_NUMBER));
			this.mName 		= cursor.getString(cursor.getColumnIndex(TBAccount.COL_NAME));
			this.mScore 	= cursor.getString(cursor.getColumnIndex(TBAccount.COL_SCORE));
			this.mLevel 	= cursor.getString(cursor.getColumnIndex(TBAccount.COL_LEVEL));
			this.mToken		= cursor.getString(cursor.getColumnIndex(TBAccount.COL_TOKEN));
			this.mGender	= cursor.getString(cursor.getColumnIndex(TBAccount.COL_GENDER));
			this.mAge 		= cursor.getString(cursor.getColumnIndex(TBAccount.COL_AGE));
			this.mHeight 	= cursor.getString(cursor.getColumnIndex(TBAccount.COL_HEIGHT));
			this.mWeight 	= cursor.getString(cursor.getColumnIndex(TBAccount.COL_WEIGHT));
			cursor.close();
		}
	}
	
	private Cursor getActiveAccount(Context context) {
		TBAccount tbAccount = new TBAccount(context);
		tbAccount.open();
		Cursor result = tbAccount.getAccountLogin();
		tbAccount.close();
		
		return result;
	}
	
	/**
	 * @return the mUsername
	 */
/*	public String getTelpNumber() {
		return mTelpNumber;
	}

	/**
	 * @param mTelpNumber the mUsername to set
	 */
	public void setTelpNumber(String mTelpNumber) {
		this.mTelpNumber = mTelpNumber;
	}
	
	
	/**
	 * @return the mName
	 */
	public String getName() {
		return mName;
	}
	
	
	public String getGender(){
		return mGender;
	}
	
	public String getAge(){
		return mAge;
	}

	public String getHeight(){
		return mHeight;
	}
	
	public String getWeight(){
		return mWeight;
	}

	public void setName(String mName) {
		this.mName = mName;
	}


	public String getScore() {
		return mScore;
	}


	public void setScore(String mScore) {
		this.mScore = mScore;
	}


	public String getLevel() {
		return mLevel;
	}


	public void setLevel(String mLevel) {
		this.mLevel = mLevel;
	}


	public String getToken() {
		return mToken;
	}


	public void setToken(String mToken) {
		this.mToken = mToken;
	}

	public String getmGlobalID() {
		return mGlobalID;
	}

	public String getmLocalID() {
		return mLocalID;
	}


	public void setmLocalID(String mLocalID) {
		this.mLocalID = mLocalID;
	}
	

	public void setmGlobalID(String mGlobalID) {
		this.mGlobalID = mGlobalID;
	}
	

	public void setmShesopID(String mShesopID) {
		this.mShesopID = mShesopID;
	}
	
	
	public String getmShesopID() {
		return mShesopID;
	} 
	
	public static void contactSync(final Context context) {
		AccountHandler account = new AccountHandler(context,
				new IConnectionResponseHandler() {
					
			
			
				@Override
				public void OnSuccessArray(JSONArray pResult){
					
				}
			
					@Override
					public void onSuccessJSONObject(String pResult) {
						Toast.makeText(context, "Sync Succeed", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onSuccessJSONArray(String pResult) {
						try {
							JSONArray jArray = new JSONArray(pResult);
							Tools.contactSyncTools(context, jArray);
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						Toast.makeText(context, "Sync Succeed", Toast.LENGTH_SHORT).show();
					}
					
					@Override
					public void onFailure(String e) {
						Toast.makeText(context, "Sync Failed", Toast.LENGTH_SHORT).show();
					}
				});
		
		account.syncContacts(Account.getSingleton(context).getToken(),
				Tools.contactArraytoString(Tools.getAllContacts(context).toString()));
	}

	public List<Friend> getFriends() {
		List<Friend> tFriends 	= new ArrayList<Friend>();
    	TBFriends tbFriends 	= new TBFriends(mContext);
    	tbFriends.open();
    	
    	Cursor tCursor = tbFriends.getAllFriendsList();    	
    	
    	if (tCursor != null) {
	    	while(tCursor.moveToNext()) {
	    		int tWeeklyStep	= tCursor.getInt(tCursor.getColumnIndex(TBFriends.COL_WEEKLY_STEP));
	    		String tName 		= tCursor.getString(tCursor.getColumnIndex(TBFriends.COL_NAME));
	    		String tTelp 		= tCursor.getString(tCursor.getColumnIndex(TBFriends.COL_TELP_NUMBER));
	    		int tLevel 		= tCursor.getInt(tCursor.getColumnIndex(TBFriends.COL_LEVEL));
	    		int tHighScore 	= tCursor.getInt(tCursor.getColumnIndex(TBFriends.COL_HIGH_SCORE));
	    		int tTodayStep 	= tCursor.getInt(tCursor.getColumnIndex(TBFriends.COL_TODAY_STEP));
	    		tFriends.add(new Friend(tName, tTelp, tLevel, tHighScore, tTodayStep, tWeeklyStep));
	    	}
    		tCursor.close();
    	}
    	
    	if(tFriends.size() == 0)
    		return null;
    	
    	tbFriends.close();

    	return tFriends;
	}
	
	
	public static String getIdShesop(Context context){
		String id = "0";
		
		TBAccount tbAcount = new TBAccount(context);
		tbAcount.open();
		Cursor cursor = tbAcount.getAcountEntry();
		if(cursor != null){
			id = cursor.getString(1);
			cursor.close();
		} 
		tbAcount.close();
		
		return id;
	}
	
	
	public static String getIdSteppy(Context context){
		String id = "0";
		
		TBAccount tbAcount = new TBAccount(context);
		tbAcount.open();
		Cursor cursor = tbAcount.getAcountEntry();
		if(cursor != null){
			id = cursor.getString(2);
			cursor.close();
		} 
		tbAcount.close();
		
		return id;
	} 
	
	
	
	public static String getAccountName(Context context){
		String name = "0";
		
		TBAccount tbAcount = new TBAccount(context);
		tbAcount.open();
		Cursor cursor = tbAcount.getAcountEntry();
		if(cursor != null){
			name = cursor.getString(4);
			cursor.close();
		} 
		tbAcount.close();
		
		return name;
	}
	
	
	
	public static String getAccountAge(Context context){
		String age = "0";
		
		TBAccount tbAcount = new TBAccount(context);
		tbAcount.open();
		Cursor cursor = tbAcount.getAcountEntry();
		if(cursor != null){
			age = cursor.getString(6);
			cursor.close();
		} 
		tbAcount.close();
		
		return age;
	}
	
	
	public static String getAccountHeight(Context context){
		String height = "0";
		
		TBAccount tbAcount = new TBAccount(context);
		tbAcount.open();
		Cursor cursor = tbAcount.getAcountEntry();
		if(cursor != null){
			height = cursor.getString(7);
			cursor.close();
		} 
		tbAcount.close();
		
		return height;
	}
	
	
	public static String getAccountWeight(Context context){
		String weight = "0";
		
		TBAccount tbAcount = new TBAccount(context);
		tbAcount.open();
		Cursor cursor = tbAcount.getAcountEntry();
		if(cursor != null){
			weight = cursor.getString(8);
			cursor.close();
		} 
		tbAcount.close();
		
		return weight;
	}
	
	
	public static String getAccountGender(Context context){
		String gender = "0";
		
		TBAccount tbAcount = new TBAccount(context);
		tbAcount.open();
		Cursor cursor = tbAcount.getAcountEntry();
		if(cursor != null){
			gender = cursor.getString(5);
			cursor.close();
		} 
		tbAcount.close();
		
		return gender;
	}
	
	public static String getAccountTelpNumber(Context context){
		String number = "0";
		
		TBAccount tbAcount = new TBAccount(context);
		tbAcount.open();
		Cursor cursor = tbAcount.getAcountEntry();
		if(cursor != null){
			number = cursor.getString(3);
			cursor.close();
		} 
		tbAcount.close();
		
		return number;
	}
	
	
	public static String getAccountEmail(Context context){
		String email = "0";
		
		TBAccount tbAcount = new TBAccount(context);
		tbAcount.open();
		Cursor cursor = tbAcount.getAcountEntry();
		if(cursor != null){
			email = cursor.getString(13);
			cursor.close();
		} 
		tbAcount.close();
		
		return email;
	}
	
	
	
	
	public static void updateAccount(Context context, String id, String name, String Age, String Height, String Weight) {
		TBAccount tbAccount = new TBAccount(context);
		tbAccount.open();

		ContentValues values = new ContentValues();
		values.put(TBAccount.COL_NAME, name);
		values.put(TBAccount.COL_AGE, Age);
		values.put(TBAccount.COL_HEIGHT, Height);
		values.put(TBAccount.COL_WEIGHT, Weight);
		tbAccount.update(id, values);
		tbAccount.close();
	}
	

}
