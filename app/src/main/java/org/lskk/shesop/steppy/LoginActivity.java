package org.lskk.shesop.steppy;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lskk.shesop.steppy.connection.AccountHandler;
import org.lskk.shesop.steppy.connection.IConnectionResponseHandler;
import org.lskk.shesop.steppy.data.TBAccount;
import org.lskk.shesop.steppy.data.TBCalculate;
import org.lskk.shesop.steppy.data.TBHRCalculate;
import org.lskk.shesop.steppy.data.TBHRCalculateRecord;
import org.lskk.shesop.steppy.msband.MSBandActivity;
import org.lskk.shesop.steppy.utils.Mission;
import org.lskk.shesop.steppy.utils.Record;
import org.lskk.shesop.steppy.utils.Tools;
import org.lskk.teamshesop.steppy.R;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.UserConsent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class LoginActivity extends Activity {

	private EditText mEmail;
	private EditText mPassword;
	private Button mLoginStart;
	private Button mSignIn;
	private Button mSignUp;
	private TextView mErrorResponse, mSignupText;
	private LinearLayout mLoginLinearLayout, mBtnLoginLinearLayout;
	private ImageView steppyLogo;
	private BandClient client = null;
	private SharedPreferences mState;
    private SharedPreferences.Editor mStateEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);

		if (Tools.isLogin(LoginActivity.this)) {
			// toDashboardView();
			new appTask().execute();
		}

		this.mEmail = (EditText) findViewById(R.id.login_email);
		this.mPassword = (EditText) findViewById(R.id.login_password);
		this.mLoginStart = (Button) findViewById(R.id.login_start);
		this.mSignIn = (Button) findViewById(R.id.login_signin);
		this.mSignUp = (Button) findViewById(R.id.login_signup);
		this.mErrorResponse = (TextView) findViewById(R.id.login_error_response);
		this.mLoginLinearLayout = (LinearLayout)findViewById(R.id.login_linear1);
		this.mBtnLoginLinearLayout = (LinearLayout)findViewById(R.id.login_btn_wrap);
		this.mSignupText = (TextView)findViewById(R.id.signupText);
		this.steppyLogo = (ImageView)findViewById(R.id.login_steppy_logo);

		
		mState = PreferenceManager.getDefaultSharedPreferences(this);
        mStateEditor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
        
		mLoginStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// set button login start invisible
				mLoginStart.setVisibility(View.GONE);
				mSignUp.setVisibility(View.GONE);
				mSignIn.setVisibility(View.VISIBLE);
				mLoginLinearLayout.setVisibility(View.VISIBLE);
				mBtnLoginLinearLayout.setVisibility(View.VISIBLE);
				mPassword.setVisibility(View.VISIBLE);
				mSignupText.setVisibility(View.VISIBLE);
				
			}
		});
		
		
		mSignupText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toSignUpView();
			}
		});
		
		
		mSignIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Make sure username and password are filled
				if ((mEmail.getText().toString().equals(""))
						&& (mPassword.getText().toString().equals(""))) {
					Toast.makeText(LoginActivity.this,
							"Please fill all the field", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				// Check network connection
				if (!Tools.isNetworkConnected(LoginActivity.this)) {
					Toast.makeText(LoginActivity.this, "Connection failed",
							Toast.LENGTH_SHORT).show();
					return;
				}

				AccountHandler account = new AccountHandler(LoginActivity.this,
						new IConnectionResponseHandler() {
					
						@Override
						public void OnSuccessArray(JSONArray pResult){
							
						}
					
					
							@Override
							public void onSuccessJSONObject(String pResult) {
								try {
									JSONObject jObject = new JSONObject(pResult);

									updateDBAccount_login(jObject);
									
									initCalculateTB();
									Tools.contactSyncTools(
											LoginActivity.this,
											new JSONArray(
													jObject.getString("FriendProfiles")));
								} catch (JSONException e) {
									e.printStackTrace();
								}

								// toDashboardView();
								// new appTask().execute();
								getMissionDetail();
							}

							@Override
							public void onFailure(String e) {
								mErrorResponse.setText(String.format(
										getResources().getString(
												R.string.login_error_response),
										e));
								mErrorResponse.setVisibility(View.VISIBLE);
							}

							@Override
							public void onSuccessJSONArray(String pResult) {
								// Ignore and do nothing
							}
						});

				account.login(mEmail.getText().toString(), mPassword
						.getText().toString());
		/*		Toast.makeText(LoginActivity.this,
						mPassword
						.getText().toString(), Toast.LENGTH_SHORT)
						.show(); */
				
			}
		});

		mSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toSignUpView();
			}
		});
	}
	
	
	private void getMissionDetail(){
		AccountHandler account = new AccountHandler(this,
				new IConnectionResponseHandler() {
				
					@Override
					public void onSuccessJSONObject(String pResult) {
					}
					@Override
					public void OnSuccessArray(JSONArray pResult){
						// Toast.makeText(getActivity(), pResult.toString(), Toast.LENGTH_LONG).show();	
					}
					@Override
					public void onFailure(String e) {
					}
					@Override
					public void onSuccessJSONArray(String pResult) {
						try {
							int levelCount = 0;
							JSONArray ja = new JSONArray(pResult);
							levelCount = ja.length();
							Log.i("Login", "Level Found : "+levelCount);
							for(int i=0; i < levelCount;i++){
								JSONObject jo = ja.getJSONObject(i);
								Mission.InsertMission(LoginActivity.this, 
										jo.getString("IdLevel"), 
										jo.getString("Mission1"), 
										jo.getString("Mission2"), 
										jo.getString("Mission3"), 
										jo.getString("Mission4"));
							}
							new appTask().execute();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		account.getDetailMission();
	}
	
	
	private void updateDBAccount_login(JSONObject pJSONObject)
			throws JSONException {
		ContentValues values = new ContentValues();
		values.put(TBAccount.COL_ID_SHESOP, pJSONObject.getString("IdUserShesop"));
		values.put(TBAccount.COL_ID_ACCOUNT, pJSONObject.getString("IdUser"));
		values.put(TBAccount.COL_TELP_NUMBER,pJSONObject.getString("TelpNumber"));
		values.put(TBAccount.COL_NAME, pJSONObject.getString("DisplayName"));
		values.put(TBAccount.COL_GENDER, pJSONObject.getString("Gender"));
		values.put(TBAccount.COL_AGE, Integer.parseInt(pJSONObject.getString("Age")));
		values.put(TBAccount.COL_HEIGHT, Integer.parseInt(pJSONObject.getString("Height")));
		values.put(TBAccount.COL_WEIGHT, Integer.parseInt(pJSONObject.getString("Weight")));
		values.put(TBAccount.COL_TOKEN, pJSONObject.getString("Token"));
		values.put(TBAccount.COL_IS_LOGIN, "1");
		values.put(TBAccount.COL_EMAIL, pJSONObject.getString("Email"));
		TBAccount tbAccount = new TBAccount(LoginActivity.this);
		tbAccount.open();
		tbAccount.insert(values);
		tbAccount.close();
		
		JSONObject userGameData = pJSONObject.getJSONObject("UserGameData");
		SimpleDateFormat curr = new SimpleDateFormat("dd/MM/yyyy");
		String currentDate = curr.format(new Date());
		Record.insertRecord(LoginActivity.this, 
				pJSONObject.getString("IdUserShesop"), 
				currentDate, 
				userGameData.getString("Mission1"), 
				userGameData.getString("Mission2"), 
				userGameData.getString("Mission3"), 
				userGameData.getString("Mission4"), 
				userGameData.getString("Point"), 
				userGameData.getString("IdLevel"));
		
		mStateEditor = mState.edit();
		mStateEditor.putString("display_name", pJSONObject.getString("DisplayName"));
		mStateEditor.putString("age", pJSONObject.getString("Age"));
		mStateEditor.putString("body_height", pJSONObject.getString("Height"));
		mStateEditor.putString("body_weight", pJSONObject.getString("Weight"));
		mStateEditor.commit();
		
		
	}
	
	private void initCalculateTB(){
		SimpleDateFormat start = new SimpleDateFormat("HH:mm");
		String startTime = start.format(new Date());
		ContentValues values = new ContentValues();
		values.put(TBCalculate.COL_STEP, "0");
		values.put(TBCalculate.COL_CAL, "0");
		values.put(TBCalculate.COL_DIS, "0");
		values.put(TBCalculate.COL_TIME_START, startTime);
		values.put(TBCalculate.COL_TIME_END, "0");
		values.put(TBCalculate.COL_IS_COUNTING, "0");
		values.put(TBCalculate.COL_STATE_GAME_CHECK, "1");
		
		TBCalculate tbCalculate = new TBCalculate(LoginActivity.this);
		tbCalculate.open();
		tbCalculate.insert(values);
		tbCalculate.close();
		
		ContentValues values2 = new ContentValues();
		values2.put(TBHRCalculate.COL_STEP, 0);
		values2.put(TBHRCalculate.COL_CAL, "0");
		values2.put(TBHRCalculate.COL_TIME_START, startTime);
		values2.put(TBHRCalculate.COL_TIME_END, "0");
		values2.put(TBHRCalculate.COL_DISTANCE, "0");
		
		TBHRCalculate tbHRCalculate = new TBHRCalculate(LoginActivity.this);
		tbHRCalculate.open();
		tbHRCalculate.insert(values2);
		tbHRCalculate.close();
		
		
		ContentValues values3 = new ContentValues();
		values3.put(TBHRCalculateRecord.COL_STEP, 0);
		values3.put(TBHRCalculateRecord.COL_CAL, "0");
		values3.put(TBHRCalculateRecord.COL_DISTANCE, "0");
		
		TBHRCalculateRecord tbHRCalculateRecord = new TBHRCalculateRecord(LoginActivity.this);
		tbHRCalculateRecord.open();
		tbHRCalculateRecord.insert(values3);
		tbHRCalculateRecord.close();
	}

	private void toDashboardView() {
		Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
		startActivity(intent);
		finish();
	}
	
	
	private void toMsBandView(){
		Intent intent = new Intent(getApplicationContext(), MSBandActivity.class);
		startActivity(intent);
		finish();
	}

	private void toSignUpView() {
		Intent intent = new Intent(getApplicationContext(),
				SignUpActivity.class);
		startActivity(intent);
	}
	
	
	
	private class appTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			try {
				if (getConnectedBandClient()) {
					System.out.println("Band is connected.");
					toMsBandView();
					
				} else {
					System.out.println("Band isn't connected. Please make sure bluetooth is on and the band is in range.");
					toDashboardView();
					
					// isRunning = false;
				}
			} catch (BandException e) {
				String exceptionMessage="";
				switch (e.getErrorType()) {
				case UNSUPPORTED_SDK_VERSION_ERROR:
					exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.";
					break;
				case SERVICE_ERROR:
					exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.";
					break;
				default:
					exceptionMessage = e.getMessage();
					break;
				}
				System.out.println(e.getMessage() + "\nAccept permision of Microsoft Health Service, then restart counting");

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			return null;
		}
	}
	
	
	
	
	private boolean getConnectedBandClient() throws InterruptedException, BandException {
		if (client == null) {
			BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
			if (devices.length == 0) {
				System.out.println("Band isn't paired with your phone.");
				return false;
			}
			client = BandClientManager.getInstance().create(getBaseContext(), devices[0]);
		} else if (ConnectionState.CONNECTED == client.getConnectionState()) {
			return true;
		}
		
		System.out.println("Band is connecting...");
		return ConnectionState.CONNECTED == client.connect().await();
	} 
}
