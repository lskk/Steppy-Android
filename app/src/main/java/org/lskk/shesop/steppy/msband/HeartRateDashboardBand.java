package org.lskk.shesop.steppy.msband;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.lskk.teamshesop.steppy.R;
import org.lskk.shesop.steppy.connection.AccountHandler;
import org.lskk.shesop.steppy.connection.IConnectionResponseHandler;
import org.lskk.shesop.steppy.data.Account;
import org.lskk.shesop.steppy.data.TBAccount;
import org.lskk.shesop.steppy.msband.StepDashboardBand.ChangeProfpictDialog;
import org.lskk.shesop.steppy.utils.DatePickerFragment;
import org.lskk.shesop.steppy.utils.Tools;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HeartRateDashboardBand extends Fragment {
	
	private static final int REQUEST_CAMERA = 1;
	private static final int REQUEST_GALLERY = 2;
	
	private XYPlot plot;
	private ImageView mProfpict;
	
	
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
	int dummyData[];
	Number series1Numbers[] = {};
	String mNum[] = {};
	private int seriesCount;
	private Button refreshButton;
	
	private TextView Output;
    private Button changeDate;
 
    private int year;
    private int month;
    private int day;
    
    private String dateString;
 
    static final int DATE_PICKER_ID = 1111; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_heartrate_dashboard, container, false);
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
		
		
		bb = Float.parseFloat(Account.getAccountWeight(getActivity())) ;
		tb = Float.parseFloat(Account.getAccountHeight(getActivity())) ;
		bmi = bb / (tb * tb);
		if(bmi < 18.5)
			bmiState = "Underweight";
		if(bmi >= 18.5 && bmi <= 24)
			bmiState = "Normal Weight";
		if(bmi >= 25 && bmi <= 29)
			bmiState = "Overweight";
		if(bmi > 30)
			bmiState = "Obese";
		
		SimpleDateFormat curr = new SimpleDateFormat("dd/MM/yyyy");
		String currentDate = curr.format(new Date());
		dateString = currentDate;
		
		
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
		
        changeDate = (Button) rootView.findViewById(R.id.setdate);
		changeDate.setOnClickListener(new View.OnClickListener() {

			   @Override
			   public void onClick(View v) {
			    showDatePicker();
			   }
		});
        
		refreshButton = (Button)rootView.findViewById(R.id.refresh);
		refreshButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getDailyGraph();
			}
		});
		
	//	showDailyGraph();
		getDailyGraph();
		
		return rootView;
	}
	
	private void getDailyGraph(){
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
							
							dummyData = new int[ja.length()];
							series1Numbers = new Number[ja.length()];
							mNum = new String[ja.length()];
							
							for(int i = 0; i < ja.length(); i++){
								series1Numbers[i] = Math.abs(ja.getInt(i));
								mNum[i] = String.valueOf(i+1);
							}
							Log.d("DATA", "DATA VER IS : "+Arrays.toString(series1Numbers));
							Log.d("DATA", "DATA HOR IS : "+Arrays.toString(mNum));
							seriesCount = ja.length();
							Log.d("DATA", "size is : "+seriesCount);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
						}
						
						showDailyGraph();
						isDataWeek = true;
						
						
					}
				});
		Log.d("Graphic Heartrate", "date : "+dateString);
		account.getGrafikHeartRateBandValue(Account.getIdShesop(getActivity()),  dateString);
		
	}
	
	
	
	void showDailyGraph(){
	//	RelativeLayout view = (RelativeLayout) LayoutInflater.from(getActivity())
	//            .inflate(R.layout.fragment_step_dashboard, null);
		
	//	XYPlot plot = (XYPlot) view.findViewById(R.id.mySimpleXYPlot);
		plot.clear();
		plot.setDrawingCacheEnabled(true);
	//	Number[] series1Numbers = {weeklyData[0], weeklyData[1], weeklyData[2], weeklyData[3], weeklyData[4], weeklyData[5], weeklyData[6]};
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
                R.xml.line_point_formatter_with_plf2);
        
        plot.getLegendWidget().setVisible(false);
        plot.setDomainValueFormat(new Format() {
        	 
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                return new StringBuffer( mNum[ ( (Number)obj).intValue() ]  );
            }
 
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
        
        if(seriesCount >= 1000 && seriesCount <= 1500)
        	plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 300);
        
        plot.getGraphWidget().setRangeLabelWidth(50);
        
        // Reduce the number of range labels
        plot.setTicksPerRangeLabel(2);
 
        // Reduce the number of domain labels
        plot.setTicksPerDomainLabel(2);
        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        plot.setDomainLabel("Numbers Record");
        Paint lineFill = new Paint();
        lineFill.setAlpha(253);
        lineFill.setShader(new LinearGradient(0, 0, 0, 400, Color.parseColor("#00a65a"), Color.parseColor("#2c3e50"),Shader.TileMode.CLAMP));
        series1Format.setFillPaint(lineFill);
        series1Format.getVertexPaint().setColor(Color.parseColor("#0000a65a"));
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
		mTest.setText("Graph : Heartrate Records ");
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
				Uri selectedImageUri = data.getData();
				String tempPath = Tools.getGalleryPath(selectedImageUri,
						getActivity());

				BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
				btmapOptions.inSampleSize = 8;
				Bitmap bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
				byteBmp = Tools.bitmapToByteArray(bm);

				// mProfpict.setImageBitmap(Bitmap.createScaledBitmap(bm,
				// mProfpict.getWidth(),
				// mProfpict.getHeight(), false));
				mProfpict.setImageBitmap(BitmapFactory.decodeByteArray(byteBmp,
						0, byteBmp.length));
			}

			tbAccount.open();

			ContentValues values = new ContentValues();
			values.put(TBAccount.COL_PROFPICT, byteBmp);
			tbAccount.update(Account.getIdSteppy(getActivity()),
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
						intentCamera.putExtra("crop", "true");
						intentCamera.putExtra("outputX", 200);
						intentCamera.putExtra("outputY", 200);
						intentCamera.putExtra("aspectX", 1);
						intentCamera.putExtra("aspectY", 1);
						intentCamera.putExtra("scale", true);
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
						startActivityForResult(
						Intent.createChooser(intentGallery,"Select File"), REQUEST_GALLERY);
						break;
					default:
						break;
					}
				}
			});

			return builder.create();
		}
	}
	
	
	 private void showDatePicker() {
		  DatePickerFragment date = new DatePickerFragment();
		  /**
		   * Set Up Current Date Into dialog
		   */
		  Calendar calender = Calendar.getInstance();
		  Bundle args = new Bundle();
		  args.putInt("year", calender.get(Calendar.YEAR));
		  args.putInt("month", calender.get(Calendar.MONTH));
		  args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
		  date.setArguments(args);
		  /**
		   * Set Call back to capture selected date
		   */
		  date.setCallBack(ondate);
		  date.show(getChildFragmentManager(), "Date Picker");
		 }

		 OnDateSetListener ondate = new OnDateSetListener() {
		  @Override
		  public void onDateSet(DatePicker view, int year, int monthOfYear,
		    int dayOfMonth) {
			  
			  monthOfYear += 1;
			  
			  dateString = String.valueOf((dayOfMonth<10?("0"+dayOfMonth):(dayOfMonth)) ) + "/" 
					     + String.valueOf((monthOfYear<10?("0"+monthOfYear):(monthOfYear)) )
					       + "/" + String.valueOf(year);
			  
		/*   Toast.makeText(
		     getActivity(),
		     String.valueOf((dayOfMonth<10?("0"+dayOfMonth):(dayOfMonth)) ) + "/" 
		     + String.valueOf((monthOfYear<10?("0"+monthOfYear):(monthOfYear)) )
		       + "/" + String.valueOf(year),
		     Toast.LENGTH_SHORT).show(); */
		  }
		 };
}