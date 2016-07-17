package org.lskk.shesop.steppy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lskk.teamshesop.steppy.R;
import org.lskk.shesop.steppy.DashboardFragment.ChangeProfpictDialog;
import org.lskk.shesop.steppy.DashboardFragment.StartActionDialog;
import org.lskk.shesop.steppy.algorithms.PedometerSettings;
import org.lskk.shesop.steppy.algorithms.StepService;
import org.lskk.shesop.steppy.connection.AccountHandler;
import org.lskk.shesop.steppy.connection.IConnectionResponseHandler;
import org.lskk.shesop.steppy.data.Account;
import org.lskk.shesop.steppy.data.TBAccount;
import org.lskk.shesop.steppy.msband.MSBandActivity;
import org.lskk.shesop.steppy.utils.Tools;

import android.R.array;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Shader;

import com.androidplot.xy.*;



import java.security.PublicKey;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;


public class StepDashboard extends Fragment {

	private ImageButton mStartPhone;
	private ImageButton mStartBand;
	private XYPlot plot;
	private ImageView mProfpict;
	final String[] mWeek = new String[] {
	        "Sun","Mon", "Tue","Wed", "Thu","Fri",
	        "Sat"
	    };
	final String[] mMonth = new String[] {
	        "Week 1","Week 2", "Week 3","Week 4", "Week 5","Week 6"};
	
	final String[] mYear = new String[] {
	        "Jan","Feb", "Mar","Apr", "Mei","Jun",
	        "Jul", "Aug", "Sep", "Oct", "Nov", "Des"
	    };
	
	private static final int REQUEST_CAMERA = 1;
	private static final int REQUEST_GALLERY = 2;
	private TextView mAccName;
	private TextView mAge;
	private TextView mHeight;
	private TextView mWeight;
	private TextView mTest;
	private TextView mTextState;
	int[] weeklyData = {0, 0, 0, 0, 0, 0, 0};
	int[] monthlyData = {0, 0, 0, 0, 0, 0};
	int[] yearData = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
	TBAccount tbAccount;
	ProgressDialog pDialog2;
	private Button bWeekly;
	private Button bMonthly;
	private Button bYears;
	private int typeState = 0;
	private String bmiState;
	private float bb, tb, bmi;
	private boolean isDataWeek = false;
	private boolean isDataMonth = false;
	private boolean isDataYear = false;
	private Button startStop;
	SharedPreferences mSettings;
	
	private int mPageNumber;
	public static final String ARG_PAGE = "page";
	
	public static StepDashboard create(int pageNumber) {
		StepDashboard fragment = new StepDashboard();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("Step Dashboard", "[FRAGMENT] onCreate");
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
	}
	
	public int getPageNumber() {
		return mPageNumber;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Tools.updateCountingState(getActivity(), "0");
		mPageNumber = getArguments().getInt(ARG_PAGE);
		
		
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_step_dashboard, container, false);
		mProfpict = (ImageView) rootView.findViewById(R.id.dashboard_profpict);
		byte[] bmpArray = Tools.getAccountProfpict(getActivity());
		if (bmpArray != null)
			mProfpict.setImageBitmap(BitmapFactory.decodeByteArray(bmpArray, 0,
					bmpArray.length));
		else
			mProfpict.setImageResource(R.drawable.profpict_blank);
		mProfpict.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ChangeProfpictDialog dialog = new ChangeProfpictDialog();
				dialog.show(getFragmentManager(), getTag());
			}
		});
		
		startStop = (Button)rootView.findViewById(R.id.stopandstart);
		
		if(Tools.getIsCountingState(getActivity())==1)
			startStop.setText("View Counting");
		
		startStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Tools.getIsCountingState(getActivity())==0 || Tools.getIsCountingState(getActivity())== 1)
					startActivity(new Intent(getActivity(),MainActivity.class));
				else if(Tools.getIsCountingState(getActivity())==2)
					Toast.makeText(getActivity(), "Calibrate is running, stop Calibrate to start Counting", Toast.LENGTH_SHORT).show();
					
				// getActivity().finish();
			}
		});
		
		
		
		bb = Float.parseFloat(Account.getAccountWeight(getActivity()));
		tb = Float.parseFloat(Account.getAccountHeight(getActivity())) / 100 ;
		bmi = bb / (tb * tb);
		Log.d("Step Dashboard", "bb :"+bb);
		Log.d("Step Dashboard", "tb :"+tb);
		Log.d("Step Dashboard", "bmi :"+bmi);
		if(bmi < 18.5)
			bmiState = "Underweight";
		if(bmi >= 18.5 && bmi <= 24)
			bmiState = "Normal Weight";
		if(bmi >= 25 && bmi <= 29)
			bmiState = "Overweight";
		if(bmi > 30)
			bmiState = "Obese";
		 
		mTextState = (TextView)rootView.findViewById(R.id.text_state);
		mTextState.setText(bmiState);
		mAccName = (TextView)rootView.findViewById(R.id.text_name);
		mAccName.setText(Account.getAccountName(getActivity()));
		mAge = (TextView)rootView.findViewById(R.id.text_age);
		mAge.setText(Account.getAccountAge(getActivity())+" Years");
		mHeight = (TextView)rootView.findViewById(R.id.text_height);
		mHeight.setText(Account.getAccountHeight(getActivity())+ " cm, ");
		mWeight = (TextView)rootView.findViewById(R.id.text_weight);
		mWeight.setText(Account.getAccountWeight(getActivity())+" Kg");
		mTest = (TextView)rootView.findViewById(R.id.test_array);
		plot = (XYPlot) rootView.findViewById(R.id.mySimpleXYPlot);
		bWeekly = (Button)rootView.findViewById(R.id.time_0);
		bMonthly = (Button)rootView.findViewById(R.id.time_1);
		bYears = (Button)rootView.findViewById(R.id.time_2);
		
		bWeekly.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(typeState != 1){
					if(isDataWeek == false)
						getWeeklyGraph();
					else
						showWeeklyGraph();
				}
			}
		});
		
		
		bMonthly.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(typeState != 2){
					if(isDataMonth == false)
						getMonthlyGraph();
					else
						showMonthlyGraph();
				}
			}
		});
		
		bYears.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(typeState != 3){
					if(isDataYear == false)
						getYearsGraph();
					else
						showYearGraph();
				}
			}
		});
		
		getWeeklyGraph();
		showWeeklyGraph();
		return rootView;
	}
	
    @Override
	public void onResume() {
        Log.i("ONRESUME", "[ACTIVITY] onResume");
        super.onResume();
        if(Tools.getIsCountingState(getActivity())==1)
			startStop.setText("View Counting");
        else
        	startStop.setText("Start Counting");

        
    }
    
    
  
	private void getWeeklyGraph(){
		AccountHandler account = new AccountHandler(getActivity(),
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
						
						//-------------------------------- nyampe sini
						Toast.makeText(getActivity(), "Connection error", Toast.LENGTH_SHORT).show();
						mTest.setText("Graph : Connection error.");
						mTest.setTextColor(Color.parseColor("#d04545"));
					}

					@Override
					public void onSuccessJSONArray(String pResult) {
					//	Toast.makeText(getActivity(), "Data : "+pResult.toString(), Toast.LENGTH_LONG).show();
						
						try {
							JSONArray ja = new JSONArray(pResult);
							for(int i = 0; i < ja.length(); i++){
								weeklyData[i] = ja.getInt(i);
								// mTest.append(Integer.toString(weeklyData[i]));
							}
							
							// JSONObject jo = ja.getJSONObject(0);
							// Toast.makeText(getActivity(), foo, Toast.LENGTH_LONG).show();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
						}
						
						showWeeklyGraph();
						isDataWeek = true;
						
						
					}
				});
		SimpleDateFormat curr = new SimpleDateFormat("dd/MM/yyyy");
		String currentDate = curr.format(new Date());
		account.getGrafikValue(currentDate, "1", Account.getIdShesop(getActivity()));
		
	}
	void showWeeklyGraph(){
	//	RelativeLayout view = (RelativeLayout) LayoutInflater.from(getActivity())
	//            .inflate(R.layout.fragment_step_dashboard, null);
		
	//	XYPlot plot = (XYPlot) view.findViewById(R.id.mySimpleXYPlot);
		plot.clear();
		plot.setDrawingCacheEnabled(true);
		Number[] series1Numbers = {weeklyData[0], weeklyData[1], weeklyData[2], weeklyData[3], weeklyData[4], weeklyData[5], weeklyData[6]};
        // Turn the above arrays into XYSeries':
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                "Series1");                             // Set the display title of the series
        // same as above

 
        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        series1Format.setPointLabelFormatter(new PointLabelFormatter());
        series1Format.configure(getActivity(),
                R.xml.line_point_formatter_with_plf1);
        
        plot.getLegendWidget().setVisible(false);
        plot.setDomainValueFormat(new Format() {
        	 
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                return new StringBuffer( mWeek[ ( (Number)obj).intValue() ]  );
            }
 
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
        plot.getGraphWidget().setRangeLabelWidth(50);
        
        // Reduce the number of range labels
        plot.setTicksPerRangeLabel(2);
 
        // Reduce the number of domain labels
        plot.setTicksPerDomainLabel(2);
        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        plot.setDomainLabel("Days");
        Paint lineFill = new Paint();
        lineFill.setAlpha(253);
        lineFill.setShader(new LinearGradient(0, 0, 0, 400, Color.parseColor("#00a65a"), Color.parseColor("#2c3e50"),Shader.TileMode.CLAMP));
        series1Format.setFillPaint(lineFill);
        plot.addSeries(series1, series1Format);
        plot.getGraphWidget().getDomainLabelPaint().setColor(Color.parseColor("#00a65a"));
        plot.getGraphWidget().getRangeLabelPaint().setColor(Color.parseColor("#00a65a"));
        plot.getBackgroundPaint().setColor(Color.parseColor("#2c3e50")); 
        plot.setBorderStyle(XYPlot.BorderStyle.SQUARE, 14.0f, 14.0f);
        plot.getGraphWidget().getBackgroundPaint().setColor(Color.parseColor("#2c3e50"));
        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.parseColor("#2c3e50"));
        SimpleDateFormat curr = new SimpleDateFormat("dd/MM/yyyy");
		String currentDate = curr.format(new Date());
		plot.setTitle(currentDate);
		plot.redraw();
		typeState = 1;
		if(typeState == 1){
			bWeekly.setBackgroundColor(Color.parseColor("#2c3e50"));
			bMonthly.setBackgroundColor(Color.parseColor("#ffffff"));
			bYears.setBackgroundColor(Color.parseColor("#ffffff"));
		}
		mTest.setText("Graph : Steps in week ");
		mTest.setTextColor(Color.parseColor("#2c3e50"));
	}
	
	
	
	
	private void getMonthlyGraph(){
		AccountHandler account = new AccountHandler(getActivity(),
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
						
						//-------------------------------- nyampe sini
						Toast.makeText(getActivity(), "Connection error", Toast.LENGTH_SHORT).show();
						mTest.setText("Graph : Connection error.");
						mTest.setTextColor(Color.parseColor("#d04545"));
					}

					@Override
					public void onSuccessJSONArray(String pResult) {
					//	Toast.makeText(getActivity(), "Data : "+pResult.toString(), Toast.LENGTH_LONG).show();
						
						try {
							JSONArray ja = new JSONArray(pResult);
							for(int i = 0; i < ja.length(); i++){
								monthlyData[i] = ja.getInt(i);
								// mTest.append(Integer.toString(weeklyData[i]));
							}
							// Toast.makeText(getActivity(), foo, Toast.LENGTH_LONG).show();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
						}
						
						showMonthlyGraph();
						isDataMonth = true;
						
					}
				});
		SimpleDateFormat curr = new SimpleDateFormat("dd/MM/yyyy");
		String currentDate = curr.format(new Date());
		account.getGrafikValue(currentDate, "2", Account.getIdShesop(getActivity()));
		
	}
	void showMonthlyGraph(){
		//	RelativeLayout view = (RelativeLayout) LayoutInflater.from(getActivity())
		//            .inflate(R.layout.fragment_step_dashboard, null);
			
		//	XYPlot plot = (XYPlot) view.findViewById(R.id.mySimpleXYPlot);
			plot.clear();
			plot.setDrawingCacheEnabled(true);
			Number[] series1Numbers = {monthlyData[0], monthlyData[1], monthlyData[2], monthlyData[3], monthlyData[4], monthlyData[5]};
	        // Turn the above arrays into XYSeries':
	        XYSeries series1 = new SimpleXYSeries(
	                Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
	                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
	                "Series1");                             // Set the display title of the series
	        // same as above

	 
	        // Create a formatter to use for drawing a series using LineAndPointRenderer
	        // and configure it from xml:
	        LineAndPointFormatter series1Format = new LineAndPointFormatter();
	        series1Format.setPointLabelFormatter(new PointLabelFormatter());
	        series1Format.configure(getActivity(),
	                R.xml.line_point_formatter_with_plf1);
	        plot.addSeries(series1, series1Format);
	        plot.getLegendWidget().setVisible(false);
	        plot.setDomainValueFormat(new Format() {
	        	 
	            /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
	            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
	                return new StringBuffer( mMonth[ ( (Number)obj).intValue() ]  );
	            }
	 
	            @Override
	            public Object parseObject(String source, ParsePosition pos) {
	                return null;
	            }
	        });
	        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
	        plot.getGraphWidget().setRangeLabelWidth(50);
	        
	        // Reduce the number of range labels
	        plot.setTicksPerRangeLabel(2);
	 
	        // Reduce the number of domain labels
	        plot.setTicksPerDomainLabel(2);
	        // reduce the number of range labels
	        plot.setTicksPerRangeLabel(3);
	        plot.setDomainLabel("Weeks");
	        Paint lineFill = new Paint();
	        lineFill.setAlpha(253);
	        lineFill.setShader(new LinearGradient(0, 0, 0, 400, Color.parseColor("#00a65a"), Color.parseColor("#2c3e50"),Shader.TileMode.CLAMP));
	        series1Format.setFillPaint(lineFill);
	        plot.addSeries(series1, series1Format);
	        plot.getGraphWidget().getDomainLabelPaint().setColor(Color.parseColor("#00a65a"));
	        plot.getGraphWidget().getRangeLabelPaint().setColor(Color.parseColor("#00a65a"));
	        plot.getBackgroundPaint().setColor(Color.parseColor("#2c3e50")); 
	        plot.setBorderStyle(XYPlot.BorderStyle.SQUARE, 14.0f, 14.0f);
	        plot.getGraphWidget().getBackgroundPaint().setColor(Color.parseColor("#2c3e50"));
	        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.parseColor("#2c3e50"));
	        SimpleDateFormat curr = new SimpleDateFormat("MM/yyyy");
			String currentDate = curr.format(new Date());
			plot.setTitle(currentDate);
			plot.redraw();
			typeState = 2;
			if(typeState == 2){
				bWeekly.setBackgroundColor(Color.parseColor("#ffffff"));
				bMonthly.setBackgroundColor(Color.parseColor("#2c3e50"));
				bYears.setBackgroundColor(Color.parseColor("#ffffff"));
			}
			mTest.setText("Graph : Steps in Month ");
			mTest.setTextColor(Color.parseColor("#2c3e50"));
		}
	
	
	private void getYearsGraph(){
		AccountHandler account = new AccountHandler(getActivity(),
				new IConnectionResponseHandler() {
					@Override
					public void onSuccessJSONObject(String pResult) {
		

					}
					
					@Override
					public void OnSuccessArray(JSONArray pResult){
					//	Toast.makeText(getActivity(), "Data : "+pResult.toString(), Toast.LENGTH_LONG).show();
						
						 
					}
					
					@Override
					public void onFailure(String e) {
						
						//-------------------------------- nyampe sini
						Toast.makeText(getActivity(), "Connection error", Toast.LENGTH_SHORT).show();
						mTest.setText("Graph : Connection error.");
						mTest.setTextColor(Color.parseColor("#d04545"));
					}

					@Override
					public void onSuccessJSONArray(String pResult) {
						// Toast.makeText(getActivity(), pResult.toString(), Toast.LENGTH_LONG).show();
						
						try {
							JSONArray ja = new JSONArray(pResult);
							for(int i = 0; i < ja.length(); i++){
								yearData[i] = ja.getInt(i);
								// mTest.append(Integer.toString(weeklyData[i]));
							}
							// Toast.makeText(getActivity(), foo, Toast.LENGTH_LONG).show();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
						}
						
						showYearGraph();
						isDataYear = true;
						
					}
				});
		SimpleDateFormat curr = new SimpleDateFormat("dd/MM/yyyy");
		String currentDate = curr.format(new Date());
		account.getGrafikValue(currentDate, "3", Account.getIdShesop(getActivity()));
		
	}
	
	
	void showYearGraph(){
		//	RelativeLayout view = (RelativeLayout) LayoutInflater.from(getActivity())
		//            .inflate(R.layout.fragment_step_dashboard, null);
			
		//	XYPlot plot = (XYPlot) view.findViewById(R.id.mySimpleXYPlot);
			plot.clear();
			plot.setDrawingCacheEnabled(true);
			Number[] series1Numbers = {yearData[0], yearData[1], yearData[2], yearData[3], yearData[4], yearData[5], yearData[6], yearData[7], yearData[8], yearData[9], yearData[10], yearData[11]};
	        // Turn the above arrays into XYSeries':
	        XYSeries series1 = new SimpleXYSeries(
	                Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
	                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
	                "Series1");                             // Set the display title of the series
	        // same as above

	 
	        // Create a formatter to use for drawing a series using LineAndPointRenderer
	        // and configure it from xml:
	        LineAndPointFormatter series1Format = new LineAndPointFormatter();
	        series1Format.setPointLabelFormatter(new PointLabelFormatter());
	        series1Format.configure(getActivity(),
	                R.xml.line_point_formatter_with_plf1);
	        plot.addSeries(series1, series1Format);
	        plot.getLegendWidget().setVisible(false);
	        plot.setDomainValueFormat(new Format() {
	        	 
	            /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
	            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
	                return new StringBuffer( mYear[ ( (Number)obj).intValue() ]  );
	            }
	 
	            @Override
	            public Object parseObject(String source, ParsePosition pos) {
	                return null;
	            }
	        });
	        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
	        plot.getGraphWidget().setRangeLabelWidth(50);
	        
	        // Reduce the number of range labels
	        plot.setTicksPerRangeLabel(2);
	 
	        // Reduce the number of domain labels
	        plot.setTicksPerDomainLabel(2);
	        // reduce the number of range labels
	        plot.setDomainLabel("Months");
	        Paint lineFill = new Paint();
	        lineFill.setAlpha(253);
	        lineFill.setShader(new LinearGradient(0, 0, 0, 400, Color.parseColor("#00a65a"), Color.parseColor("#2c3e50"),Shader.TileMode.CLAMP));
	        series1Format.setFillPaint(lineFill);
	        plot.addSeries(series1, series1Format);
	        plot.getGraphWidget().getDomainLabelPaint().setColor(Color.parseColor("#00a65a"));
	        plot.getGraphWidget().getRangeLabelPaint().setColor(Color.parseColor("#00a65a"));
	        plot.setTicksPerRangeLabel(3);
	        plot.getBackgroundPaint().setColor(Color.parseColor("#2c3e50")); 
	        plot.setBorderStyle(XYPlot.BorderStyle.SQUARE, 14.0f, 14.0f);
	        plot.getGraphWidget().getBackgroundPaint().setColor(Color.parseColor("#2c3e50"));
	        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.parseColor("#2c3e50"));
	        SimpleDateFormat curr = new SimpleDateFormat("yyyy");
			String currentDate = curr.format(new Date());
			plot.setTitle(currentDate);
			plot.redraw();
			typeState = 3;
			if(typeState == 3){
				bWeekly.setBackgroundColor(Color.parseColor("#ffffff"));
				bMonthly.setBackgroundColor(Color.parseColor("#ffffff"));
				bYears.setBackgroundColor(Color.parseColor("#2c3e50"));
			}
			mTest.setText("Graph : Steps in year ");
			mTest.setTextColor(Color.parseColor("#2c3e50"));
	        
		}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			byte[] byteBmp = null;

			if (requestCode == REQUEST_CAMERA) {
				
				Bitmap photo = (Bitmap) data.getExtras().get("data");

				// mProfpict.setImageBitmap(Bitmap.createScaledBitmap(photo,
				// mProfpict.getWidth(),
				// mProfpict.getHeight(), false));

				byteBmp = Tools.bitmapToByteArray(photo);

				mProfpict.setImageBitmap(BitmapFactory.decodeByteArray(byteBmp,
						0, byteBmp.length));
			} else if (requestCode == REQUEST_GALLERY) {
				
				Bundle extras = data.getExtras(); 
				Bitmap photo = extras.getParcelable("data");
				
			//	Uri selectedImageUri = data.getData();
				
			//	String tempPath = Tools.getGalleryPath(selectedImageUri,
			//			getActivity());

		//		BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
			//	btmapOptions.inSampleSize = 8;
			//	Bitmap bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
				byteBmp = Tools.bitmapToByteArray(photo);

				// mProfpict.setImageBitmap(Bitmap.createScaledBitmap(bm,
				// mProfpict.getWidth(),
				// mProfpict.getHeight(), false));
				mProfpict.setImageBitmap(BitmapFactory.decodeByteArray(byteBmp,
						0, byteBmp.length));
			}
			tbAccount = new TBAccount(this.getActivity());
			tbAccount.open();

			ContentValues values = new ContentValues();
			values.put(TBAccount.COL_PROFPICT, byteBmp);
			tbAccount.update(Account.getSingleton(getActivity()).getmLocalID(),
					values);

			tbAccount.close();
		}
	}
	
	public static class ChangeProfpictDialog extends DialogFragment {
		final CharSequence[] items = { "Take Photo", "Choose from Gallery" };

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Change Profile Picture");

			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dismiss();
						}
					});
			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int position) {
					switch (position) {
					case 0:
						Intent intentCamera = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						// File f = new File(android.os.Environment
						// .getExternalStorageDirectory(),
						// Tools.creteNewPictName());
						// intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,
						// Uri.fromFile(f));
						getActivity().startActivityForResult(intentCamera,
								REQUEST_CAMERA);
						break;
					case 1:
						Intent intentGallery = new Intent(
								Intent.ACTION_PICK,
								android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						intentGallery.setType("image/*");
						intentGallery.putExtra("crop", "true");
						intentGallery.putExtra("outputX", 200);
						intentGallery.putExtra("outputY", 200);
						intentGallery.putExtra("aspectX", 1);
						intentGallery.putExtra("aspectY", 1);
						intentGallery.putExtra("scale", true);
						intentGallery.setType("image/*");
						getActivity().startActivityForResult(
								Intent.createChooser(intentGallery,
										"Select File"), REQUEST_GALLERY);
						break;
					default:
						break;
					}
				}
			});

			return builder.create();
		}
	}
}


