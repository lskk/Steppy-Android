package org.lskk.shesop.steppy;

import org.json.JSONArray;
import org.lskk.shesop.steppy.connection.AccountHandler;
import org.lskk.shesop.steppy.connection.IConnectionResponseHandler;
import org.lskk.shesop.steppy.utils.Tools;
import org.lskk.teamshesop.steppy.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends Activity{

	private EditText mEmail;
	private EditText mNumber;
	private EditText mDisplayName;
	private EditText mPassword;
	private EditText mRepeatPassword;
	private Button mSignupButton;
	private TextView mErrorResponse;
	private Spinner mGenderSpinner;
	private TextView mGenderText;
	private EditText mAge;
	private EditText mHeight;
	private String[] mGender;
	private EditText mWeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		if(Tools.isLogin(SignUpActivity.this)) {
        	toLoginView();
        }
		
		// initialization get view control from xml resource
		mEmail = (EditText) findViewById(R.id.signup_email);
		mNumber = (EditText)findViewById(R.id.signup_telp);
		mDisplayName = (EditText) findViewById(R.id.signup_display_name);
		mPassword = (EditText) findViewById(R.id.signup_password);
		mRepeatPassword = (EditText)findViewById(R.id.repeat_password);
		mSignupButton = (Button) findViewById(R.id.signup_signup);
		mErrorResponse = (TextView) findViewById(R.id.signup_error_response);
		mGenderSpinner = (Spinner) findViewById(R.id.gender_spinner);
		mGender = getResources().getStringArray(R.array.gender_array);
		mAge = (EditText)findViewById(R.id.age);
		mHeight = (EditText)findViewById(R.id.height);
		mWeight = (EditText)findViewById(R.id.weight);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, R.id.spintext, mGender);
		mGenderSpinner.setAdapter(adapter);
		
		mSignupButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if((mEmail.getText().toString().equals("")) || 
						(mPassword.getText().toString().equals("")) ||
						(mRepeatPassword.getText().toString().equals("")) ||
						(mDisplayName.getText().toString().equals("")) || 
						(mNumber.getText().toString().equals("")) ||
						(mAge.getText().toString().equals("")) || 
						(mHeight.getText().toString().equals("")) ||
						(mWeight.getText().toString().equals("")))
				{
					Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_LONG).show();
					return;
				}
				
				if(mGenderSpinner.getSelectedItem().toString().contains("Gender")){
					Toast.makeText(SignUpActivity.this, "Please select your gender", Toast.LENGTH_LONG).show();
					return;
				}
				
				if(!mPassword.getText().toString().equals(mRepeatPassword.getText().toString())){
					Toast.makeText(SignUpActivity.this, "Please repeat your password correctly", Toast.LENGTH_LONG).show();
					return;
				}
				
				// Check network connection
				if(!Tools.isNetworkConnected(SignUpActivity.this)) {
					Toast.makeText(SignUpActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
					return;
				}
				
				AccountHandler account = new AccountHandler(SignUpActivity.this,
						new IConnectionResponseHandler() {
					
							@Override
							public void OnSuccessArray(JSONArray pResult){
								
							}
							
							@Override
							public void onSuccessJSONObject(String pResult) {
								Toast.makeText(SignUpActivity.this, "Register Success", Toast.LENGTH_SHORT).show();
								toLoginView();
							}
							@Override
							public void onFailure(String e) {
								mErrorResponse.setText(String.format(getResources()
										.getString(R.string.login_error_response), e));
								mErrorResponse.setVisibility(View.VISIBLE);
							}
							@Override
							public void onSuccessJSONArray(String pResult) {
								// Ignore and do nothing
							}
						});
				
				account.signup(mEmail.getText().toString()
						, mPassword.getText().toString()
						, mDisplayName.getText().toString()
						,mNumber.getText().toString()
						,String.valueOf(mGenderSpinner.getSelectedItem())
						, mAge.getText().toString()
						, mHeight.getText().toString()
						, mWeight.getText().toString()
						);
			}
		});	
	}	
	private void toLoginView() {
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(intent);
		
		finish();
	}
	
	
	public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
		
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback

    }
	
	
	
}
