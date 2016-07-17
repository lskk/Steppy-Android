package org.lskk.shesop.steppy;

import org.lskk.shesop.steppy.heartratemonitor.HeartRateMonitor;
import org.lskk.teamshesop.steppy.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends Activity implements OnClickListener{
	Button satu, dua, tiga, empat, lima;
	Intent i;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		satu = (Button)findViewById(R.id.satu);
		satu.setOnClickListener(this);
		dua = (Button)findViewById(R.id.dua);
		dua.setOnClickListener(this);
		tiga = (Button)findViewById(R.id.tiga);
		tiga.setOnClickListener(this);
		empat = (Button)findViewById(R.id.empat);
		empat.setOnClickListener(this);
		lima = (Button)findViewById(R.id.lima);
		lima.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.satu:
			i = new Intent (MenuActivity.this, MainActivity.class);
			startActivity(i);
			break;
		case R.id.dua:
			i = new Intent (MenuActivity.this, HeartRateMonitor.class);
			startActivity(i);
			break;
		case R.id.tiga:
			i = new Intent (MenuActivity.this, HistoryActivity.class);
			startActivity(i);
			break;
		case R.id.empat:
			i = new Intent (MenuActivity.this, SensorActivity.class);
			startActivity(i);
			break;
		case R.id.lima:
			i = new Intent (MenuActivity.this, AboutActivity.class);
			startActivity(i);
			break;
				}
			}
		
		
		}