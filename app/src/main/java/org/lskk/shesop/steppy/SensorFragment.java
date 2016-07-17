package org.lskk.shesop.steppy;

import org.lskk.shesop.steppy.algorithms.PedometerSettings;
import org.lskk.shesop.steppy.algorithms.StepService;
import org.lskk.shesop.steppy.data.Account;
import org.lskk.shesop.steppy.data.TBAccount;
import org.lskk.shesop.steppy.preferences.AccountPreference;
import org.lskk.shesop.steppy.utils.Tools;
import org.lskk.shesop.steppy.utils.Utils;
import org.lskk.teamshesop.steppy.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SensorFragment extends Fragment {

	/**
	 * Tag string info for this fragment
	 */
	private static final String TAG = "SensorFragment";
	int step_limit = 500;
	private int level;
	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";

	/**
	 * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
	 */
	private int mPageNumber;

	/**
	 * True, when service is running.
	 */
	private boolean mIsServiceRunning;
	
	/**
	 * Set when user selected Quit from menu, can be used by onPause, onStop, onDestroy
	 */
	public boolean mQuitting = false;

	private String id_user;
	private int mStepValue;
	private int mWeeklyValue;
	private float mDiffValue;
	private int mCaloriesValue;
	private TextView mStepValueView;
	private TextView mStepWeeklyValueView;
	//    private TextView mPaceValueView;
	//    private TextView mDistanceValueView;
	private TextView mSpeedValueView;
	private TextView mCaloriesValueView;
	private TextView mAccName;
	private TextView mStartBtn;
	private TextView mLevelView;
	private ProgressBar pg;
	private TextView pgt;

	private ImageView mProfpict;
	private SharedPreferences mSettings;
	private PedometerSettings mPedometerSettings;
	private Utils mUtils;
	
	private static final int REQUEST_CAMERA = 1;
	private static final int REQUEST_GALLERY = 2;

	TBAccount tbAccount;
	/**
	 * Factory method for this fragment class. Constructs a new fragment for the given page number.
	 */
	public static SensorFragment create(int pageNumber) {
		SensorFragment fragment = new SensorFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "[FRAGMENT] onCreate");
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);

//		mStepValue = 0;
//		mWeeklyValue = 0;
//		mPaceValue = 0;
		tbAccount = new TBAccount(this.getActivity());
		if (tbAccount != null) {
			tbAccount.open();
			Cursor c = tbAccount.getAccountLogin();
			mStepValue = 0;
			level = 0;
			if (c != null) {
				id_user = c.getString(0);
				mStepValue = c.getInt(5);
				level = c.getInt(6);
			}
			tbAccount.close();
		} else {
			Toast.makeText(this.getActivity().getApplicationContext(),
					"Db Kosong", Toast.LENGTH_LONG).show();
		}
		mUtils = Utils.getInstance();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
	            mBroadcastReceiver,
	            new IntentFilter(AccountPreference.ACTION_LOGOUT_INTENT));
	}

	private void updateDBAccount(int step, int levels) {
		tbAccount.open();
		Cursor c = tbAccount.getAccountLogin();
		ContentValues values = new ContentValues();
		values.put(TBAccount.COL_SCORE, step);
		values.put(TBAccount.COL_LEVEL, levels);
		tbAccount.update(id_user, values);
		tbAccount.close();
	}

	private void count() {
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				try {
					android.os.Process
							.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
					mStartBtn.setEnabled(false);
					//Thread.sleep(10000);
					if (mStepValue >= step_limit) {
						int limit = step_limit * level;
						final int prog = (int) (mStepValue * limit) / 100;
						pg.post(new Runnable() {
							@Override
							public void run() {
								pgt.setText(Integer.toString(prog) + "%");
								pg.setProgress(prog);
							}
						});
						level++;
						mLevelView.setText("Your Current Level: "
								+ Integer.toString(mStepValue) + ", Score "
								+ level);
						step_limit = step_limit * level;
						updateDBAccount(mStepValue, level);
					}
					this.run();
				} catch (Exception ex) {
				}
			}
		};
		try {
			Thread t = new Thread(runnable);
			t.run();
		} catch (Exception ex) {

		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "[FRAGMENT] onCreateView");

		// Inflate the layout containing a title and body text.
		ViewGroup rootView = (ViewGroup) inflater
				.inflate(R.layout.fragment_sensor, container, false);

		pg = (ProgressBar) rootView.findViewById(R.id.dashboard_progressBar);
		pgt = (TextView) rootView
				.findViewById(R.id.dashboard_progress_percentage);
		
		mStepValueView     	= (TextView) rootView.findViewById(R.id.dashboard_step_count);
		mStepWeeklyValueView= (TextView) rootView.findViewById(R.id.step_weekly_value);
		mSpeedValueView    	= (TextView) rootView.findViewById(R.id.speed_value);
		mCaloriesValueView 	= (TextView) rootView.findViewById(R.id.calories_value);
		
		mLevelView = (TextView) rootView.findViewById(R.id.dashboard_level);
		mLevelView.setText("Your Current Score: "
				+ Integer.toString(step_limit) + ", Level " + mStepValue);
		
		mProfpict = (ImageView) rootView.findViewById(R.id.dashboard_profpict);
		byte[] bmpArray = Tools.getAccountProfpict(getActivity());
		if (bmpArray != null)
			mProfpict.setImageBitmap(BitmapFactory.decodeByteArray(bmpArray, 0, bmpArray.length));
		else
			mProfpict.setImageResource(R.drawable.profpict_blank);
		
		mProfpict.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ChangeProfpictDialog dialog = new ChangeProfpictDialog();
				dialog.show(getFragmentManager(), getTag());
			}
		});
		
		mAccName = (TextView) rootView.findViewById(R.id.dashboard_name);
		mAccName.setText(Account.getAccountName(getActivity()));

		return rootView;
		}
	

	/**
	 * Returns the page number represented by this fragment object.
	 */
	public int getPageNumber() {
		return mPageNumber;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "[FRAGMENT] onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		Log.i(TAG, "[FRAGMENT] onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		Log.i(TAG, "[FRAGMENT] onResume");
		super.onResume();

		mSettings = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mPedometerSettings = new PedometerSettings(mSettings);

		mUtils.setSpeak(mSettings.getBoolean("speak", false));

		// Read from preferences if the service was running on the last onPause
		mIsServiceRunning = mPedometerSettings.isServiceRunning();

		// Start the service if this is considered to be an application start (last onPause was long ago)
		if (!mIsServiceRunning && mPedometerSettings.isNewStart()) {
			startStepService();
			bindStepService();
		} else if (mIsServiceRunning) {
			bindStepService();
		} else if (!mIsServiceRunning) {
			mStartBtn.setText(getResources().getString(R.string.resume));
		}

		mPedometerSettings.clearServiceRunning();
	}

	@Override
	public void onPause() {
		Log.i(TAG, "[FRAGMENT] onPause");
		if (mIsServiceRunning) {
			unbindStepService();
		}
		if (mQuitting) {
			mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsServiceRunning);
		}
		else {
			mPedometerSettings.saveServiceRunningWithTimestamp(mIsServiceRunning);
		}

		super.onPause();
		//        savePaceSetting();
	}

	@Override
	public void onStop() {
		Log.i(TAG, "[FRAGMENT] onStop");
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		Log.i(TAG, "[FRAGMENT] onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "[FRAGMENT] onDestroy");
		super.onDestroy();
	}

	public void startStepService() {
		if (! mIsServiceRunning) {
			Log.i(TAG, "[FRAGMENT] Starting Service");
			mIsServiceRunning = true;
			getActivity().startService(new Intent(getActivity(),
					StepService.class));
		}
	}

	public void bindStepService() {
		Log.i(TAG, "[FRAGMENT] Binding Service");
		getActivity().bindService(new Intent(getActivity(), 
				StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	}

	public void unbindStepService() {
		Log.i(TAG, "[FRAGMENT] Unbinding Service");
		getActivity().unbindService(mConnection);
		
//		Tools.saveMeasurement(getActivity(), String.valueOf(mMinuteValue), 
//				String.valueOf(mSpeedValue));
	}

	public void stopStepService() {
		if (mService != null) {
			Log.i(TAG, "[FRAGMENT] Stoping Service");
			getActivity().stopService(new Intent(getActivity(),
					StepService.class));
		}
		mIsServiceRunning = false;
	}

	public void resetValues(boolean updateDisplay) {
		pgt.setText("0%");
		pg.setProgress(0);
		mStepValue = 0;
//		updateDBAccount(0, level);
		if (mService != null && mIsServiceRunning) {
			mService.resetValues();
		} else {
			mStepValueView.setText("0");
			// mPaceValueView.setText("0");
			// mDistanceValueView.setText("0");
			mSpeedValueView.setText("0");
			mCaloriesValueView.setText("0");
			SharedPreferences state = getActivity().getSharedPreferences(
					"state", 0);
			SharedPreferences.Editor stateEditor = state.edit();
			if (updateDisplay) {
				stateEditor.putInt("steps", 0);
				stateEditor.putInt("pace", 0);
				stateEditor.putFloat("distance", 0);
				stateEditor.putFloat("speed", 0);
				stateEditor.putFloat("calories", 0);
				stateEditor.commit();
			}
		}
		// Toast.makeText(DashboardFragment.this, "sd" ,
		// Toast.LENGTH_SHORT).show();

	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			byte[] byteBmp = null;
			
			if (requestCode == REQUEST_CAMERA) {
				Bitmap photo = (Bitmap) data.getExtras().get("data"); 
				
//				mProfpict.setImageBitmap(Bitmap.createScaledBitmap(photo, mProfpict.getWidth(),
//						mProfpict.getHeight(), false));
				
				byteBmp = Tools.bitmapToByteArray(photo);				
				
				mProfpict.setImageBitmap(BitmapFactory.decodeByteArray(byteBmp, 0, byteBmp.length));
			} else if(requestCode == REQUEST_GALLERY) {
				Uri selectedImageUri = data.getData();
				String tempPath = Tools.getGalleryPath(selectedImageUri, getActivity());
				
	            BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
	            btmapOptions.inSampleSize = 8;
	            Bitmap bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
	            byteBmp = Tools.bitmapToByteArray(bm);
	            
//	            mProfpict.setImageBitmap(Bitmap.createScaledBitmap(bm, mProfpict.getWidth(),
//	            		mProfpict.getHeight(), false));
	            mProfpict.setImageBitmap(BitmapFactory.decodeByteArray(byteBmp, 0, byteBmp.length));
			}

//			TBAccount tbAccount = new TBAccount(getActivity());
            tbAccount.open();
            
            ContentValues values = new ContentValues();
			values.put(TBAccount.COL_PROFPICT, byteBmp);
            tbAccount.update(Account.getIdSteppy(getActivity()),
            		values);
            
            tbAccount.close();
		}
	}
	

	private StepService mService;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((StepService.StepBinder)service).getService();

			mService.registerCallback(mCallback);
			mService.reloadSettings();

		}

		public void onServiceDisconnected(ComponentName className) {
			Log.i(TAG, "Service Disconnected");
			mService = null;
		}
	};

	// TODO: unite all into 1 type of message
	private StepService.ICallback mCallback = new StepService.ICallback() {
		public void stepsChanged(int value, int stepPerMin) {
			mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, stepPerMin));
		}
		public void weeklyChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(STEP_WEEKLY_MSG, value, 0));
		}
		public void paceChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
		}
		public void distanceChanged(float value, float distancePermin) {
			mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), Math.round(distancePermin)));
		}
		public void speedChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
		}
		public void caloriesChanged(float value, float calPermin) {
			mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int)(value), Math.round(calPermin)));
		}
		public void sensorChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(SENSOR_MSG, (int)(value*1000), 0));
		}
	};

	private static final int STEPS_MSG = 1;
	private static final int PACE_MSG = 2;
	private static final int DISTANCE_MSG = 3;
	private static final int SPEED_MSG = 4;
	private static final int CALORIES_MSG = 5;
	private static final int STEP_WEEKLY_MSG = 6;
	private static final int SENSOR_MSG = 7;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override public void handleMessage(Message msg) {
			switch (msg.what) {
			case SENSOR_MSG:
				mDiffValue = ((int)msg.arg1)/1000f;
				if (mDiffValue <= 0) { 
					mSpeedValueView.setText("0");
				} else {
					mSpeedValueView.setText(
							("" + (mDiffValue + 0.000001f)).substring(0, 4)
							);
				}
				break;
			case STEP_WEEKLY_MSG:
				mWeeklyValue = (int)msg.arg1;
				mStepWeeklyValueView.setText("" + mWeeklyValue);
				break;
//			case SPEED_MSG:
//				mSpeedValue = ((int)msg.arg1)/1000f;
//				if (mSpeedValue <= 0) { 
//					mSpeedValueView.setText("0");
//				}
//				else {
//					mSpeedValueView.setText(
//							("" + (mSpeedValue + 0.000001f)).substring(0, 4)
//							);
//				}
//				break;
			case CALORIES_MSG:
				mCaloriesValue = msg.arg1;
				mCaloriesValueView.setText("" + (int)mCaloriesValue);
				break;
			default:
				super.handleMessage(msg);
			}
		}

	};

	@SuppressLint("ValidFragment")
	public class StartActionDialog extends DialogFragment {
		private SensorFragment df;
		
		public void setSensorFragment(SensorFragment df) {
			this.df = df;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();
			
			View view = inflater.inflate(R.layout.dialog_start_action, null);
			
			Button startBtn = (Button) view.findViewById(R.id.dsa_resume_btn);
			startBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					resumeService();
					dismiss();
				}
			});
			if(mIsServiceRunning) startBtn.setVisibility(View.GONE);
			
			Button pauseBtn = (Button) view.findViewById(R.id.dsa_pause_btn);
			pauseBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					pauseService();
					dismiss();
				}
			});
			if(!mIsServiceRunning) pauseBtn.setVisibility(View.GONE);
			
			Button resetBtn = (Button) view.findViewById(R.id.dsa_reset_btn);
			resetBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					df.resetValues(true);
					dismiss();
				}
			});
			
			Button quitBtn = (Button) view.findViewById(R.id.dsa_quit_btn);
			quitBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					quit();
				}
			});
			
			builder.setView(view);

			return builder.create();
		}
	}
	
	public static class ChangeProfpictDialog extends DialogFragment {
		final CharSequence[] items = { "Take Photo", "Choose from Gallery"};
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Change Profile Picture");
			
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
						Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//	                    File f = new File(android.os.Environment
//	                            .getExternalStorageDirectory(), Tools.creteNewPictName());
//	                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
	                    getActivity().startActivityForResult(intentCamera, REQUEST_CAMERA);
						break;
					case 1:
						Intent intentGallery = new Intent(
	                            Intent.ACTION_PICK,
	                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						intentGallery.setType("image/*");
						getActivity().startActivityForResult(
	                            Intent.createChooser(intentGallery, "Select File"),
	                            REQUEST_GALLERY);
						break;
					default:
						break;
					}
				}
			});
			
			return builder.create();
		}
	}
	
	private void pauseService() {
		unbindStepService();
    	stopStepService();
    	mStartBtn.setText(getResources().getString(R.string.resume));
	}
	
	private void resumeService() {
		startStepService();
    	bindStepService();
		mStartBtn.setText(getResources().getString(R.string.pause));
	}
	
	private void quit() {
		resetValues(false);
		if (mIsServiceRunning) unbindStepService();
		stopStepService();
		mQuitting = true;
		getActivity().finish();
	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	String action = intent.getAction();

	        if(action.equals(AccountPreference.ACTION_LOGOUT_INTENT)){
	        	Log.w(TAG, "Loging out");
	        	
//	        	TBAccount tbAccount = new TBAccount(context);
				tbAccount.open();
				Cursor tCursor = tbAccount.getAccountLogin();
				if(tCursor != null) {
					tbAccount.delete(tCursor.getLong(0));
					tCursor.close();
				}
				tbAccount.close();

				Account.logout();
				
				try {
					Thread.sleep(1500);
				} catch(Exception e) {
					e.printStackTrace();
				}

				stopStepService();
				mQuitting = true;
				getActivity().finish();
	        }
	    }
	};
	
	
}
