package org.lskk.shesop.steppy.connection;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lskk.teamshesop.steppy.R;

import android.R.string;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AccountHandler extends ConnectionHandler {
	
	private String TAG_SIGN_IN 				= "[AccountHandler] Sign in";
	private String TAG_SIGN_UP 				= "[AccountHandler] Sign up";
	private String TAG_SEND_STEP			= "[AccountHandler] Sending Step";
	private String TAG_SYNC_CONTACT_LIST 	= "[AccountHandler] Sync contact";
	private String TAG_SYNC_MEASUREMENT 	= "[AccountHandler] Sync measurement";
	private String TAG_SYNC_FRIEND_BOARD	= "[AccountHandler] Sync friends";

	public AccountHandler(Context context, IConnectionResponseHandler handler) {
		this.mContext = context;
		this.responseHandler = handler;
	}
	
	@Override
	public String getAbsoluteUrl(String relativeUrl) {
		return mContext.getResources().getString(R.string.http_address) + relativeUrl;
	}
	
	public void send(String userid, String shesopid, String date, String startTime, String endTime, String step, String calori, String sensor, String distance){
		RequestParams params = new RequestParams();
		params.put("UserId", userid);
		params.put("IdUserShesop", shesopid);
		params.put("Date", date);
		params.put("StartTime", startTime);
		params.put("EndTime", endTime);
		params.put("Step", step);
		params.put("Distance", distance);
		params.put("Calori", calori);
		params.put("Sensor", sensor);
		System.setProperty("http.keepAlive", "false");
		post("step", params, new JsonHttpResponseHandler(){
		//	ProgressDialog dialog;
			@Override
			public void onStart(){
				super.onStart();
				Log.i(TAG_SEND_STEP, "Sending step");
			//	dialog = ProgressDialog.show(mContext, "Connecting", "Sending Step...", true);
			}
			
			@Override
			public void onSuccess(JSONObject response) {
				super.onSuccess(response);
				Log.i(TAG_SEND_STEP, "Success");
				responseHandler.onSuccessJSONObject(response.toString());
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable e) {
				super.onFailure(statusCode, headers, responseBody, e);
				Log.e(TAG_SEND_STEP, "Failed");
				responseHandler.onFailure("Send step failed");//e.getMessage());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				Log.i(TAG_SEND_STEP, "Disconnected");
			//	dialog.dismiss();
			}
			
		});
	}
	
	
	
	public void sendBand(String userid, String shesopid, String date, String startTime, String endTime, String step, String calori, String distance){
		RequestParams params = new RequestParams();
		params.put("UserId", userid);
		params.put("Date", date);
		params.put("StartTime", startTime);
		params.put("EndTime", endTime);
		params.put("Step", step);
		params.put("Calorie", calori);
		params.put("IdUserShesop", shesopid);
		params.put("Distance", distance);

		System.setProperty("http.keepAlive", "false");
		post("stepband", params, new JsonHttpResponseHandler(){
		//	ProgressDialog dialog;
			@Override
			public void onStart(){
				super.onStart();
				Log.i(TAG_SEND_STEP, "Sending step band");
			//	dialog = ProgressDialog.show(mContext, "Connecting", "Sending Step...", true);
			}
			
			@Override
			public void onSuccess(JSONObject response) {
				super.onSuccess(response);
				Log.i(TAG_SEND_STEP, "Success");
				responseHandler.onSuccessJSONObject(response.toString());
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable e) {
				super.onFailure(statusCode, headers, responseBody, e);
				Log.e(TAG_SEND_STEP, "Failed");
				responseHandler.onFailure("Send step band failed");//e.getMessage());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				Log.i(TAG_SEND_STEP, "Disconnected");
			//	dialog.dismiss();
			}
			
		});
	}
	
	
	
	
	
	public void sendHeartrate(String id, String date, String sTime, String eTime, String hRate,
			String idShesop, String pace, String speed, String temp, String uvlevel){
		RequestParams params = new RequestParams();
		params.put("UserId", id);
		params.put("Date", date);
		params.put("StartTime", sTime);
		params.put("EndTime", eTime);
		params.put("HeartRate", hRate);
		params.put("Pace", pace);
		params.put("IdUserShesop", idShesop);
		params.put("Speed", speed);
		params.put("Temperature", temp);
		params.put("UVlevel", uvlevel);
	//	System.setProperty("http.keepAlive", "false");
		
		Log.d("TAG", "HR :"+hRate);
		Log.d("TAG", "Speed :"+speed);
		Log.d("TAG", "Pace :"+pace);
		Log.d("TAG", "Temperature :"+temp);
		Log.d("TAG", "UVLevel :"+uvlevel);
		Log.d("TAG", "sTime :"+sTime);
		Log.d("TAG", "eTime :"+eTime);
		Log.d("TAG", "Date :"+date);
		Log.d("TAG", "id :"+id);
		Log.d("TAG", "idshesop :"+idShesop); 
		
		post("heartrate", params, new JsonHttpResponseHandler(){
			@Override
			public void onStart() {
				super.onStart();
				Log.i("TAG_SEND_HEARTRATE", "Connecting...");
			}
			
			@Override
			public void onSuccess(JSONObject response) {
				super.onSuccess(response);
				Log.i("TAG_SEND_HEARTRATE", "Success");
				responseHandler.onSuccessJSONObject(response.toString());
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable e) {
				super.onFailure(statusCode, headers, responseBody, e);
				Log.e("TAG_SEND_HEARTRATE", "Failed");
				responseHandler.onFailure(e.getMessage());//e.getMessage());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				Log.i("TAG_SEND_HEARTRATE", "Disconnected");
			}
		});
	}
	
	public void login(String uname, String pwd) {
		RequestParams params = new RequestParams();
		params.put("Email", uname);
		params.put("Password", pwd);
		
		post("authentication", params, new JsonHttpResponseHandler(){

			ProgressDialog dialog;
			
			@Override
			public void onStart() {
				super.onStart();
				Log.i(TAG_SIGN_IN, "Connecting...");
				dialog = ProgressDialog.show(mContext, "Connecting", "Signing in...", true);
			}
			
//			@Override
//			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
//				Log.i(TAG_SIGN_IN, "Success stream");
//				String s = new String(arg2);
//				System.out.println(s);
//			}
			
			@Override
			public void onSuccess(JSONObject response) {
				super.onSuccess(response);
				Log.i(TAG_SIGN_IN, "Success");
				responseHandler.onSuccessJSONObject(response.toString());
			}
			
//			@Override
//			public void onFailure(Throwable e, JSONObject errorResponse) {
//				super.onFailure(e, errorResponse);
//				Log.e(TAG_SIGN_IN, "Failed");
//				responseHandler.onFailure("Invalid username or password");//e.getMessage());
//			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable e) {
				super.onFailure(statusCode, headers, responseBody, e);
				Log.e(TAG_SIGN_IN, "Failed");
				responseHandler.onFailure("Invalid username or password");//e.getMessage());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				Log.i(TAG_SIGN_IN, "Disconnected");
				dialog.dismiss();
			}
			
		});
		
	}
	
	public void signup(String uname, String pwd, String displayName, String telp, String gender, String age, String height, String weight) {
		RequestParams params = new RequestParams();
		params.put("Email", uname);
		params.put("Password", pwd);
		params.put("DisplayName", displayName);
		params.put("TelpNumber", telp);
		params.put("Gender", gender);
		params.put("Age", age);
		params.put("Height", height);
		params.put("Weight", weight);
		post("registration", params, new JsonHttpResponseHandler(){

			ProgressDialog dialog;
			
			@Override
			public void onStart() {
				super.onStart();
				Log.i(TAG_SIGN_UP, "Connecting...");
				dialog = ProgressDialog.show(mContext, "Connecting", "Signing up...", true);
			}
			

			@Override
			public void onSuccess(JSONObject response) {
				super.onSuccess(response);
				Log.i(TAG_SIGN_UP, "Success");
				responseHandler.onSuccessJSONObject(response.toString());
			}
			
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				Log.e(TAG_SIGN_UP, "Failed");
				responseHandler.onFailure(e.getMessage());
			}		
			
			@Override
			public void onFinish() {
				super.onFinish();
				Log.i(TAG_SIGN_IN, "Disconnected");
				dialog.dismiss();
			}
			
		});
		
	}
	
	public void syncContacts(String pToken, String pContacts) {
		RequestParams params = new RequestParams();
		params.put("TelpNumbers", pContacts);
		
		post("friend/"+ pToken, params, new JsonHttpResponseHandler(){

			ProgressDialog dialog;
			
			@Override
			public void onStart() {
				Log.i(TAG_SYNC_CONTACT_LIST, "Sync contacts...");
				dialog = ProgressDialog.show(mContext, "Connecting", 
						"Sync contacts with server...", true);
				
				super.onStart();
			}
			
			@Override
			public void onSuccess(JSONArray response) {
				super.onSuccess(response);
				Log.i(TAG_SYNC_CONTACT_LIST, "Success");
				responseHandler.onSuccessJSONArray(response.toString());
			}
			
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				Log.e(TAG_SYNC_CONTACT_LIST, "Failed");
				responseHandler.onFailure(e.getMessage());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				Log.i(TAG_SYNC_CONTACT_LIST, "Disconnected");
				dialog.dismiss();
			}
		});
	}
	
	
	public void syncMeasurement(String pToken, JSONArray pData) {
		postJArray(mContext, "measurement/"+ pToken, pData, new JsonHttpResponseHandler(){
			
			@Override
			public void onStart() {
				Log.i(TAG_SYNC_MEASUREMENT, "Syncronizing measurement data...");
				
				super.onStart();
			}
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				super.onSuccess(arg0, arg1, arg2);
				Log.i(TAG_SYNC_MEASUREMENT, "Success");
				responseHandler.onSuccessJSONObject("");
			}
			
//			@Override
//			public void onFailure(Throwable e, JSONObject errorResponse) {
//				super.onFailure(e, errorResponse);
//				Log.e(TAG_SYNC_MEASUREMENT, "Failed");
//				responseHandler.onFailure(e.getMessage());
//			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable e) {
				super.onFailure(statusCode, headers, responseBody, e);
				Log.e(TAG_SYNC_MEASUREMENT, "Failed status code: "+statusCode);
				responseHandler.onFailure("Failed status code: "+statusCode);
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				Log.i(TAG_SYNC_MEASUREMENT, "Disconnected");
			}
		});
	}
	
	
	public void getMission(String nextLevel){
		get("gamemision?idLevel="+nextLevel, null, new JsonHttpResponseHandler(){
			ProgressDialog dialog;
			public void onStart() {
				dialog = ProgressDialog.show(mContext, "Connecting", "Get Mission...", true);
				Log.i("GET Mission", "Getting data...");
				
				super.onStart();
			}
			
			@Override
			public void onSuccess(JSONArray response) {
				super.onSuccess(response);
				Log.i("GET Mission", "Success receive data");
				responseHandler.onSuccessJSONArray(response.toString());
				responseHandler.OnSuccessArray(response);
			}
			
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				Log.e("Get Mission", "Failed");
				responseHandler.onFailure(e.getMessage());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dialog.dismiss();
				Log.i("Get Mission", "Disconnected");
			}
		});
		
	}
	
	
	
	public void getGrafikValue(String date, String type, String id){
		get("step?date="+date+"&periode="+type+"&idShesop="+id, null, new JsonHttpResponseHandler(){
			ProgressDialog dialog;
			public void onStart() {
				dialog = ProgressDialog.show(mContext, "Connecting", "Get data...", true);
				Log.i("GET GRAFIK VALUES", "Getting data...");
				
				super.onStart();
			}
			
			@Override
			public void onSuccess(JSONArray response) {
				super.onSuccess(response);
				Log.i("GET GRAFIK VALUES", "Success receive data");
				responseHandler.onSuccessJSONArray(response.toString());
				responseHandler.OnSuccessArray(response);
			}
			
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				Log.e(TAG_SYNC_FRIEND_BOARD, "Failed");
				responseHandler.onFailure(e.getMessage());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dialog.dismiss();
				Log.i(TAG_SYNC_FRIEND_BOARD, "Disconnected");
			}
		});
		
	}
	
	
	
	
	public void getGrafikBandValue(String date, String type, String id){
		get("stepband?date="+date+"&periode="+type+"&idShesop="+id, null, new JsonHttpResponseHandler(){
			ProgressDialog dialog;
			public void onStart() {
				dialog = ProgressDialog.show(mContext, "Connecting", "Get data...", true);
				Log.i("GET GRAFIK VALUES", "Getting data...");
				
				super.onStart();
			}
			
			@Override
			public void onSuccess(JSONArray response) {
				super.onSuccess(response);
				Log.i("GET GRAFIK VALUES", "Success receive data");
				responseHandler.onSuccessJSONArray(response.toString());
				responseHandler.OnSuccessArray(response);
			}
			
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				Log.e(TAG_SYNC_FRIEND_BOARD, "Failed");
				responseHandler.onFailure(e.getMessage());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dialog.dismiss();
				Log.i(TAG_SYNC_FRIEND_BOARD, "Disconnected");
			}
		});
		
	}
	
	
	
	
	public void getGrafikHeartRateBandValue( String id, String date){
		get("heartrate?idShesop="+id+"&date="+date, null, new JsonHttpResponseHandler(){
			ProgressDialog dialog;
			public void onStart() {
				dialog = ProgressDialog.show(mContext, "Connecting", "Get data...", true);
				Log.i("GET GRAFIK VALUES HR", "Getting data...");
				
				super.onStart();
			}
			
			@Override
			public void onSuccess(JSONArray response) {
				super.onSuccess(response);
				Log.i("GET GRAFIK VALUES HR", "Success receive data");
				responseHandler.onSuccessJSONArray(response.toString());
				responseHandler.OnSuccessArray(response);
			}
			
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				Log.e(TAG_SYNC_FRIEND_BOARD, "Failed");
				responseHandler.onFailure(e.getMessage());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				dialog.dismiss();
				Log.i(TAG_SYNC_FRIEND_BOARD, "Disconnected");
			}
		});
		
	}
	
	
	public void getDetailMission(){
		get("gamemission", null, new JsonHttpResponseHandler(){
			ProgressDialog dialog;
			public void onStart() {
				dialog = ProgressDialog.show(mContext, "Connecting", "Get Mission data...", true);
				super.onStart();
			}
			
			@Override
			public void onSuccess(JSONArray response) {
				super.onSuccess(response);
				Log.i("GET Mission Detail", "Success receive data");
				responseHandler.onSuccessJSONArray(response.toString());
				responseHandler.OnSuccessArray(response);
			}
			
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				Log.e("Get Mission", "Failed");
				responseHandler.onFailure(e.getMessage());
			}		
			@Override
			public void onFinish() {
				super.onFinish();
				dialog.dismiss();
			}
		});
	}
	
	
	public void syncFriends(String pToken) {
		get("friend/"+pToken, null, new JsonHttpResponseHandler(){
			public void onStart() {
				Log.i(TAG_SYNC_FRIEND_BOARD, "Sync friend board...");
				
				super.onStart();
			}
			
			@Override
			public void onSuccess(JSONArray response) {
				super.onSuccess(response);
				Log.i(TAG_SYNC_FRIEND_BOARD, "Success");
				responseHandler.onSuccessJSONArray(response.toString());
			}
			
			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				super.onFailure(e, errorResponse);
				Log.e(TAG_SYNC_FRIEND_BOARD, "Failed");
				responseHandler.onFailure(e.getMessage());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				Log.i(TAG_SYNC_FRIEND_BOARD, "Disconnected");
			}
		});
	}
	
	
	
	
	public void sendGameRecord(String IdUserShesop, String date, String valMission1, String valMission2,
			String valMission3, String valMission4, String valLevel, String valPoint){
		RequestParams params = new RequestParams();
		params.put("IdUserShesop", IdUserShesop);
		params.put("Date", date);
		params.put("Mission1", valMission1);
		params.put("Mission2", valMission2);
		params.put("Mission3", valMission3);
		params.put("Mission4", valMission4);
		params.put("IdLevel", valLevel);
		params.put("Point", valPoint);
		post("gamerecord", params, new JsonHttpResponseHandler(){
		//	ProgressDialog dialog;
			@Override
			public void onStart(){
				super.onStart();
				Log.i("Game Record", "Sending record");
			//	dialog = ProgressDialog.show(mContext, "Connecting", "Sending Step...", true);
			}
			
			@Override
			public void onSuccess(JSONObject response) {
				super.onSuccess(response);
				Log.i("Game Record", "Success");
				responseHandler.onSuccessJSONObject(response.toString());
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable e) {
				super.onFailure(statusCode, headers, responseBody, e);
				Log.e("Game Record", "Failed");
				responseHandler.onFailure("Send record failed");//e.getMessage());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				Log.i("Game Record", "Disconnected");
			//	dialog.dismiss();
			}
			
		});
	}
	
	
	
	
	public void updateAccount(String IdUserShesop, String displayname, String age,
			String height, String weight, String gender, String telpNumber){
		RequestParams params = new RequestParams();
		params.put("DisplayName", displayname);
		params.put("Age", age);
		params.put("Height", height);
		params.put("Weight", weight);
		params.put("Gender", gender);
		params.put("TelpNumber", telpNumber);
		
		post("profile/1/"+IdUserShesop, params, new JsonHttpResponseHandler(){
			ProgressDialog dialog;
			@Override
			public void onStart(){
				super.onStart();
				Log.i("Update Account", "Sending edit");
				dialog = ProgressDialog.show(mContext, "Connecting", "Updating Account", true);
			}
			
			@Override
			public void onSuccess(JSONObject response) {
				super.onSuccess(response);
				Log.i("Update Account", "Success");
				responseHandler.onSuccessJSONObject(response.toString());
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable e) {
				super.onFailure(statusCode, headers, responseBody, e);
				Log.e("Update Account", "Failed");
				responseHandler.onFailure("Send record failed");//e.getMessage());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				Log.i("Update Account", "Disconnected");
				dialog.dismiss();
			}
			
		});
	}
	
	
	
	
}
