package org.lskk.shesop.steppy;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.lskk.shesop.steppy.algorithms.PedometerSettings;
import org.lskk.shesop.steppy.algorithms.StepService;
import org.lskk.shesop.steppy.connection.AccountHandler;
import org.lskk.shesop.steppy.connection.IConnectionResponseHandler;
import org.lskk.shesop.steppy.data.Account;
import org.lskk.shesop.steppy.data.TBAccount;
import org.lskk.shesop.steppy.preferences.AccountPreference;
import org.lskk.shesop.steppy.receiver.SendService;
import org.lskk.shesop.steppy.utils.Tools;
import org.lskk.shesop.steppy.utils.Utils;
import org.lskk.shesop.steppy.utils.Tools;
import org.lskk.teamshesop.steppy.R;

import android.R.bool;
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
import android.renderscript.Sampler.Value;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardFragment extends Fragment {

	/**
	 * Tag string info for this fragment
	 */
	private static final String TAG = "DashboardFragment";
	int step_limit = 50;
	int level;

	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";

	/**
	 * The fragment's page number, which is set to the argument value for
	 * {@link #ARG_PAGE}.
	 */
	private int mPageNumber;

	/**
	 * True, when service is running.
	 */
	private boolean mIsServiceRunning;

	/**
	 * Set when user selected Quit from menu, can be used by onPause, onStop,
	 * onDestroy
	 */
	public boolean mQuitting = false;

	private String id_user;
	private int mStepValue;
	private int mWeeklyValue;
	private float mDiffValue;
	// private int mPaceValue;
	private float mDistanceValue;
	private float mSpeedValue;
	private int mCaloriesValue;
	private static TextView mStepValueView;
//	private TextView mStepWeeklyValueView;
	// private TextView mPaceValueView;
	private TextView mDistanceValueView;
	private static TextView mSpeedValueView;
	private static TextView mCaloriesValueView;
	private TextView mAccName;
	private TextView mStartBtn;
//	private TextView mLevelView;
//	private ProgressBar pg;
//	private TextView pgt;

	private ImageView mProfpict;
//	private Button tDailyMission;
	private ImageButton tStartActionBtn;
//	private ImageButton tSendAction;

	private SharedPreferences mSettings;
	private PedometerSettings mPedometerSettings;
	private Utils mUtils;

	private static final int REQUEST_CAMERA = 1;
	private static final int REQUEST_GALLERY = 2;
	private TextView mAge;
	private TextView mHeight;
	private TextView mWeight;
	TBAccount tbAccount;
	
	private TextView mTextState;
	private String bmiState;
	private float bb, tb, bmi;
	private SharedPreferences mState;
	private String sensString;
	private TextView sensNameDisplay;
	
	/**
	 * Factory method for this fragment class. Constructs a new fragment for the
	 * given page number.
	 */
	public static DashboardFragment create(int pageNumber) {
		DashboardFragment fragment = new DashboardFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "[FRAGMENT] onCreate");
		super.onCreate(savedInstanceState);
				
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
					//	int limit = step_limit * level;
				//		final int prog = (int) (level * limit) / 100;
			//			pg.post(new Runnable() {
				//			@Override
					//		public void run() {
						//		pgt.setText(Integer.toString(prog) + "%");
							//	pg.setProgress(prog);
						//	}
					//	});
						// pgt.setText(Integer.toString(prog) + "%");
						// pg.setProgress(prog);
						level++;
			//			mLevelView.setText("Your Current Score: "
				//				+ Integer.toString(mStepValue) + ", Level "
					//			+ level);
						step_limit = step_limit * level;
						updateDBAccount(mStepValue, level);
					}
					mStartBtn.setEnabled(true);
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
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_dashboard, container, false);

	//	pg = (ProgressBar) rootView.findViewById(R.id.dashboard_progressBar);
	//	pgt = (TextView) rootView
		//		.findViewById(R.id.dashboard_progress_percentage);

		mStepValueView 		= (TextView) rootView.findViewById(R.id.dashboard_step_count);
//		mStepWeeklyValueView= (TextView) rootView.findViewById(R.id.step_weekly_value);
		mSpeedValueView 	= (TextView) rootView.findViewById(R.id.speed_value);
		mCaloriesValueView 	= (TextView) rootView.findViewById(R.id.calories_value);
		mDistanceValueView = (TextView) rootView.findViewById(R.id.dashboard_distance_count);
	//	mLevelView = (TextView) rootView.findViewById(R.id.dashboard_level);
	//	mLevelView.setText("Your Current Score: "
	//			+ Integer.toString(step_limit) + ", Level " + mStepValue);

		mProfpict = (ImageView) rootView.findViewById(R.id.dashboard_profpict);
		byte[] bmpArray = Tools.getAccountProfpict(getActivity());
		if (bmpArray != null)
			mProfpict.setImageBitmap(BitmapFactory.decodeByteArray(bmpArray, 0,
					bmpArray.length));
		else
			mProfpict.setImageResource(R.drawable.profpict_blank);
	/*	mProfpict.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ChangeProfpictDialog dialog = new ChangeProfpictDialog();
				dialog.show(getFragmentManager(), getTag());
			}
		}); */

	/*	tDailyMission = (Button) rootView
				.findViewById(R.id.dashboard_daily_mission_what);
		tDailyMission.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DailyMissionDialogFragment a = new DailyMissionDialogFragment();
				a.show(getFragmentManager(), getTag());
				resetValues(true);
				tDailyMission.setEnabled(false);
			}
		}); */

		tStartActionBtn = (ImageButton) rootView
				.findViewById(R.id.dashboard_btn_start_action);
		tStartActionBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				StartActionDialog a = new StartActionDialog();
				a.setDashboardFragment(DashboardFragment.this);
				a.show(getFragmentManager(), getTag());
			}
		});
				
		mAccName = (TextView)rootView.findViewById(R.id.dashboard_name);
		mAccName.setText(Account.getAccountName(getActivity()));
		mAge = (TextView)rootView.findViewById(R.id.text_age);
		mAge.setText(Account.getAccountAge(getActivity())+" Years");
		mHeight = (TextView)rootView.findViewById(R.id.text_height);
		mHeight.setText(Account.getAccountHeight(getActivity())+ " cm, ");
		mWeight = (TextView)rootView.findViewById(R.id.text_weight);
		mWeight.setText(Account.getAccountWeight(getActivity())+" Kg");
		
		bb = Float.parseFloat(Account.getAccountWeight(getActivity())) ;
		tb = Float.parseFloat(Account.getAccountHeight(getActivity()))/ 100 ;
		bmi = bb / (tb * tb);
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
		
		mStartBtn = (TextView) rootView.findViewById(R.id.dashboard_start_btn);
		mStartBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mIsServiceRunning) {
					 pauseService();
					count();
				} else {
					 resumeService();
				}
			}
		});
		
		
		
		
		sensNameDisplay = (TextView)rootView.findViewById(R.id.sens_name);
        mState = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sensString = mState.getString("sensitivity", "10");
        if(sensString.contains("1.9753"))
        	sensNameDisplay.setText("Sensor : Extra High");
        else if(sensString.contains("2.9630"))
        	sensNameDisplay.setText("Sensor : Very High");
        else if(sensString.contains("4.4444"))
        	sensNameDisplay.setText("Sensor : High");
        else if(sensString.contains("6.6667"))
        	sensNameDisplay.setText("Sensor : Higher");
        else if(sensString.contains("10"))
        	sensNameDisplay.setText("Sensor : Medium");
        else if(sensString.contains("15"))
        	sensNameDisplay.setText("Sensor : Lower");
        else if(sensString.contains("22.5"))
        	sensNameDisplay.setText("Sensor : Low");
        else if(sensString.contains("33.75"))
        	sensNameDisplay.setText("Sensor : Very Low");
        else if(sensString.contains("50.625"))
        	sensNameDisplay.setText("Sensor : Extra Low");
		
		
		
		
		
		
		

		return rootView;
	}

	/**
	 * Returns the page number represented by this fragment object.
	 */
//	public int getPageNumber() {
	//	return mPageNumber;
//	}

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

		mIsServiceRunning = mPedometerSettings.isServiceRunning();

		if (!mIsServiceRunning && mPedometerSettings.isNewStart()) {
			startStepService();
			bindStepService();
		} else if (mIsServiceRunning) {
			bindStepService();
			mStartBtn.setText("Pause");
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
			mPedometerSettings
					.saveServiceRunningWithNullTimestamp(mIsServiceRunning);
		} else {
			mPedometerSettings
					.saveServiceRunningWithTimestamp(mIsServiceRunning);
		}

		super.onPause();
		// savePaceSetting();
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
		tbAccount.close();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "[FRAGMENT] onDestroy");
		super.onDestroy();
	}

	public void startStepService() {
		if (!mIsServiceRunning) {
			Log.i(TAG, "[FRAGMENT] Starting Service");
			mIsServiceRunning = true;
			getActivity().startService(
					new Intent(getActivity(), StepService.class));
		//	getActivity().startService(
		//			new Intent(getActivity(), SendService.class));
		}
	}

	public void bindStepService() {
		Log.i(TAG, "[FRAGMENT] Binding Service");
		getActivity().bindService(new Intent(getActivity(), StepService.class),
				mConnection,
				Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	}

	public void unbindStepService() {
		Log.i(TAG, "[FRAGMENT] Unbinding Service");
		getActivity().unbindService(mConnection);

		// Tools.saveMeasurement(getActivity(), String.valueOf(mMinuteValue),
		// String.valueOf(mSpeedValue));
	}

	public void stopStepService() {
		if (mService != null) {
			Log.i(TAG, "[FRAGMENT] Stoping Service");
			getActivity().stopService(
					new Intent(getActivity(), StepService.class));
		}
		mIsServiceRunning = false;
	}

	public void resetValues(boolean updateDisplay) {
//		pgt.setText("0%");
//		pg.setProgress(0);
		mStepValue = 0;
//		updateDBAccount(0, level);
		if (mService != null && mIsServiceRunning) {
			mService.resetValues();
		} else {
			mStepValueView.setText("0");
			// mPaceValueView.setText("0");
			mDistanceValueView.setText("0");
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


	private static StepService mService;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((StepService.StepBinder) service).getService();

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
			mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value,
					stepPerMin));
		}

		public void weeklyChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(STEP_WEEKLY_MSG, value,
					0));
		}

		public void paceChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
		}

		public void distanceChanged(float value, float disPermin) {
			mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG,
					(int) (value * 1000), Math.round(disPermin)));
		}

		public void speedChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG,
					(int) (value * 1000), 0));
		}

		public void caloriesChanged(float value, float calPermin) {
			mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG,
					(int) (value), Math.round(calPermin)));
		}

		public void sensorChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(SENSOR_MSG,
					(int) (value * 1000), 0));
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
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STEPS_MSG:
				mStepValue = (int) msg.arg1;
				mStepValueView.setText("" + mStepValue);
				break;
			case SENSOR_MSG:
				// mDiffValue = ((int)msg.arg1)/1000f;
				// if (mDiffValue <= 0) {
				// mSpeedValueView.setText("0");
				// } else {
				// mSpeedValueView.setText(
				// ("" + (mDiffValue + 0.000001f)).substring(0, 4)
				// );
				// }
				break;
			case STEP_WEEKLY_MSG:
				mWeeklyValue = (int) msg.arg1;
			//	mStepWeeklyValueView.setText("" + mWeeklyValue);
				break;
			case SPEED_MSG:
				mSpeedValue = ((int) msg.arg1) / 1000f;
				if (mSpeedValue <= 0) {
					mSpeedValueView.setText("0");
				} else {
					mSpeedValueView.setText(("" + (mSpeedValue + 0.000001f))
							.substring(0, 4));
				}
				break;
			case DISTANCE_MSG:
                mDistanceValue = ((int)msg.arg1)/1000f;
                if (mDistanceValue <= 0) { 
                    mDistanceValueView.setText("0");
                }
                else {
                    mDistanceValueView.setText(
                    		("" + (mDistanceValue + 0.000001f)).substring(0, 5)
                    	//	String.valueOf(mDistanceValue)
                    );
                }
                break;
			case CALORIES_MSG:
				mCaloriesValue = msg.arg1;
				if (mCaloriesValue <= 0) {
					mCaloriesValueView.setText("0");
				} else {
					mCaloriesValueView.setText("" + (int) mCaloriesValue);
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}

	};

	public static class DailyMissionDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.dialog_daily_mission_title);
			builder.setMessage(
					"You have to walk or running until 500 steps to complete this challenges!!!")
					.setPositiveButton(R.string.dialog_daily_mission_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if (mService != null &&
									// mIsServiceRunning) {
									// mService.resetValues();
									// }
									// else {
									// mStepValueView.setText("0");
									// // mPaceValueView.setText("0");
									// // mDistanceValueView.setText("0");
									// mSpeedValueView.setText("0");
									// mCaloriesValueView.setText("0");
									// SharedPreferences state =
									// getActivity().getSharedPreferences("state",
									// 0);
									// SharedPreferences.Editor stateEditor =
									// state.edit();
									//
									// stateEditor.putInt("steps", 0);
									// stateEditor.putInt("pace", 0);
									// stateEditor.putFloat("distance", 0);
									// stateEditor.putFloat("speed", 0);
									// stateEditor.putFloat("calories", 0);
									// stateEditor.commit();
									//
									// }

									dismiss();

								}
							});

			// Create the AlertDialog object and return it
			return builder.create();
		}
	}

	@SuppressLint("ValidFragment")
	public class StartActionDialog extends DialogFragment {
		private DashboardFragment df;

		public void setDashboardFragment(DashboardFragment df) {
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
			if (mIsServiceRunning)
				startBtn.setVisibility(View.GONE);

			Button pauseBtn = (Button) view.findViewById(R.id.dsa_pause_btn);
			pauseBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					pauseService();
					dismiss();
				}
			});
			if (!mIsServiceRunning)
				pauseBtn.setVisibility(View.GONE);

			Button resetBtn = (Button) view.findViewById(R.id.dsa_reset_btn);
			resetBtn.setVisibility(View.GONE);
	/*		resetBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					df.resetValues(true);
					dismiss();
				}
			}); */

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
			tbAccount.update(Account.getSingleton(getActivity()).getmLocalID(),
					values);

			tbAccount.close();
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
		if (mIsServiceRunning)
			unbindStepService();
		stopStepService();
		mQuitting = true;
		getActivity().finish();
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(AccountPreference.ACTION_LOGOUT_INTENT)) {
				Log.w(TAG, "Loging out");

				tbAccount.open();
				Cursor tCursor = tbAccount.getAccountLogin();
				if (tCursor != null) {
					tbAccount.delete(tCursor.getLong(0));
					tCursor.close();
				}
				tbAccount.close();

				Account.logout();

				try {
					Thread.sleep(1500);
				} catch (Exception e) {
					e.printStackTrace();
				}

				stopStepService();
				mQuitting = true;
				getActivity().finish();
			}
		}
	};

}
