package org.lskk.shesop.steppy.receiver;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.lskk.shesop.steppy.data.TBAccount;
import org.lskk.shesop.steppy.connection.AccountHandler;
import org.lskk.shesop.steppy.connection.IConnectionResponseHandler;
import org.lskk.shesop.steppy.data.Account;
import org.lskk.shesop.steppy.utils.Mission;
import org.lskk.shesop.steppy.utils.Record;
import org.lskk.shesop.steppy.utils.Tools;

import android.R.integer;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class GameService extends IntentService {
    public GameService() {
        super("SchedulingService");
    }
    
    int twoHoursStep;
	float twoHoursDistance;
	int todayStep;
	float todayDistance;
	
	int level;
	int rowPos;
	
	int twoHoursStepToComplete;
	float twoHoursDistanceToComplete;
	int todayStepToComplete;
	float todayDistanceToComplete;
	
	
	int mission1Score;
	int mission2Score;
	int mission3Score;
	int mission4Score;
	
	int maxMissionScore;
	
	float point;
	int cekLevel = 0;
    int recordCount;
    public static final String TAG = "Sending Backup Step Data ";
    // An ID used to post the notification.
    public static final int NOTIFICATION_ID = 1;
    // The string the app searches for in the Google home page content. If the app finds 
    // the string, it indicates the presence of a doodle.  
    public static final String SEARCH_STRING = "doodle";
    // The Google home page URL from which the app fetches content.
    // You can find a list of other Google domains with possible doodles here:
    // http://en.wikipedia.org/wiki/List_of_Google_domains
    public static final String URL = "http://www.google.com";
//    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    TBAccount tbAccount;
    SharedPreferences mSettings;
    
    @Override
    protected void onHandleIntent(Intent intent) {
    		
    	mSettings = PreferenceManager.getDefaultSharedPreferences(this);
	    Handler mHandler = new Handler(getMainLooper());
	    mHandler.post(new Runnable() {
	    	@Override 
	    	 public void run() {
	    		recordCount = Record.getRecordCount(getApplicationContext());
	    		if(recordCount > 0){
		    		cekLevel = Integer.parseInt(Record.getLastValueLevel(getApplicationContext()));
		    		Log.i("Game Service", "cek current level "+cekLevel);
		    		int cekMissionLevel = Integer.parseInt(Mission.getLastValueLevel(getApplicationContext()));
		    		Log.i("Game Service", "cek mission last data level "+cekMissionLevel);
		    		
		    		if(cekLevel != cekMissionLevel)
		    			checkMissionComplete();
		    		else if(cekLevel >= cekMissionLevel)
		    			Log.d("Game Service", "All Level Complete!");
	    		} else 
	    			Log.i("Game Service", "Record count is 0, You are loggingout");
	    	 }
	    });
        GameAlarmReceiver.completeWakefulIntent(intent);
    }
    
    
    private void checkMissionComplete(){
    	
    	level = Integer.parseInt(Record.getLastValueLevel(getApplicationContext()));
    	rowPos = level;
    	
    	twoHoursStep = Record.getTwoHoursStep(getApplicationContext());
    	twoHoursStepToComplete = Integer.parseInt(Mission.getDetailMission(getApplicationContext(), rowPos, 2));
    	
    	mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
		mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
		mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
		mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
		
		maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
		if(maxMissionScore == 4){
			mission1Score = 0;
			mission2Score = 0;
			mission3Score = 0;
			mission4Score = 0;
			maxMissionScore = 0;

		}
		point = Float.parseFloat(Record.getLastValuePoint(getApplicationContext()));
		
		SimpleDateFormat curr = new SimpleDateFormat("dd/MM/yyyy");
		String currentDate = curr.format(new Date());
		
		// check for mission 1
		if(Tools.getGameCheckState(getApplicationContext()) ==  1){
			if(mission1Score == 0){
				if(twoHoursStep >= twoHoursStepToComplete){
					Log.i("Game Service", "Your Mission 1 in level "+level+" Complete");
					Log.i("Game Service", "Step in 2 hrs must : "+twoHoursStepToComplete+", Your 2 hrs step : "+Record.getTwoHoursStep(getApplicationContext()));
					point += 0.25f;
					int levelToSend = level;
					if(maxMissionScore == 3){
						levelToSend += 1;
					}
					Record.insertRecord(getApplicationContext(), 
							Account.getIdShesop(getApplicationContext()), 
							currentDate, 
							"1", 
							String.valueOf(mission2Score), 
							String.valueOf(mission3Score), 
							String.valueOf(mission4Score), 
							String.valueOf(point), 
							String.valueOf(levelToSend));
					
					// cek : delete db if 4 mission complete
					mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
					mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
					mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
					mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
					
					maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
					if(maxMissionScore == 4){
						Record.deleteGameMeasurement(getApplicationContext());
					}
					
					
				} else {
					Log.d("Game Service", "Your Mission 1 in level "+level+" not Complete");
					Log.d("Game Service", "Step in 2 hrs must : "+twoHoursStepToComplete+", Your 2 hrs step only : "+twoHoursStep);
				}
			} else {
				Log.d("Game Service", "Mission 1 has ben complete in level "+level);
			}
			Tools.updateGameCheckState(getApplicationContext(), "2");
		}
		
		// check for mission 2
		level = Integer.parseInt(Record.getLastValueLevel(getApplicationContext()));
    	rowPos = level;
    	
    	twoHoursDistance = Record.getTwoHoursDistance(getApplicationContext())* 1000;
    	twoHoursDistanceToComplete = Float.parseFloat(Mission.getDetailMission(getApplicationContext(), rowPos, 3));	
    	
    	mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
		mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
		mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
		mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
		
		maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
		if(maxMissionScore == 4){
			mission1Score = 0;
			mission2Score = 0;
			mission3Score = 0;
			mission4Score = 0;
			maxMissionScore = 0;
		}
		
		point = Float.parseFloat(Record.getLastValuePoint(getApplicationContext()));
		
		
		 
		if(Tools.getGameCheckState(getApplicationContext()) ==  2){
			if(mission2Score == 0){
				if(twoHoursDistance >= twoHoursDistanceToComplete){
					
					Log.i("Game Service", "Your Mission 2 in level "+level+" Complete");
					Log.i("Game Service", "Distance in 2 hrs must : "+twoHoursDistanceToComplete+", Your 2 hrs distance : "+twoHoursDistance);
					
					point += 0.25f;
					int levelToSend = level;
					
					if(maxMissionScore == 3){
						levelToSend += 1;
					}
						
					Record.insertRecord(getApplicationContext(), 
							Account.getIdShesop(getApplicationContext()), 
							currentDate, 
							String.valueOf(mission1Score), 
							"1", 
							String.valueOf(mission3Score), 
							String.valueOf(mission4Score), 
							String.valueOf(point), 
							String.valueOf(levelToSend));
					
					// cek : delete db if 4 mission complete
					mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
					mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
					mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
					mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
					
					maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
					if(maxMissionScore == 4){
						Record.deleteGameMeasurement(getApplicationContext());
					}
					
					
					
				}else{
					Log.d("Game Service", "Your Mission 2 in level "+level+" not Complete");
					Log.d("Game Service", "Distance in 2 hrs must : "+twoHoursDistanceToComplete+", Your 2 distance step only : "+twoHoursDistance);
				}
			} else {
				Log.d("Game Service", "Mission 2 has ben complete in level "+level);
			}
			Tools.updateGameCheckState(getApplicationContext(), "3");
		}
		
		
		
		// check for mission 3
		
		level = Integer.parseInt(Record.getLastValueLevel(getApplicationContext()));
    	rowPos = level;
    	
		todayStep = Record.getTodayStep(getApplicationContext());
    	todayStepToComplete = Integer.parseInt(Mission.getDetailMission(getApplicationContext(), rowPos, 4));
    	
    	mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
		mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
		mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
		mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
		
		maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
		if(maxMissionScore == 4){
			mission1Score = 0;
			mission2Score = 0;
			mission3Score = 0;
			mission4Score = 0;
			maxMissionScore = 0;
		}
		
		point = Float.parseFloat(Record.getLastValuePoint(getApplicationContext()));
		
		
		if(Tools.getGameCheckState(getApplicationContext()) ==  3){
			if(mission3Score == 0){
				if(todayStep >= todayStepToComplete){
					
					Log.i("Game Service", "Your Mission 3 in level "+level+" Complete");
					Log.i("Game Service", "Today step must : "+todayStepToComplete+", Your today step : "+todayStep);
					
					point += 0.25f;
					int levelToSend = level;
					if(maxMissionScore == 3){
						levelToSend += 1;
					}
					
					
					Record.insertRecord(getApplicationContext(), 
							Account.getIdShesop(getApplicationContext()), 
							currentDate, 
							String.valueOf(mission1Score), 
							String.valueOf(mission2Score), 
							"1", 
							String.valueOf(mission4Score), 
							String.valueOf(point), 
							String.valueOf(levelToSend));
					
					// cek : delete db if 4 mission complete
					mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
					mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
					mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
					mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
					
					maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
					if(maxMissionScore == 4){
						Record.deleteGameMeasurement(getApplicationContext());
					}
					
					
				}else{
					Log.d("Game Service", "Your Mission 3 in level "+level+" not Complete");
					Log.d("Game Service", "Step today must : "+todayStepToComplete+", Your today step only : "+todayStep);
				}
			} else {
				Log.d("Game Service", "Mission 3 has ben complete in level : "+level);
			}
			Tools.updateGameCheckState(getApplicationContext(), "4");
		}
		
		
		// check for mission 4
		
		level = Integer.parseInt(Record.getLastValueLevel(getApplicationContext()));
    	rowPos = level;
		
		todayDistance = Record.getTodayDistance(getApplicationContext())* 1000;
    	todayDistanceToComplete = Float.parseFloat(Mission.getDetailMission(getApplicationContext(), rowPos, 5));
    	 	
    	// cek apakah sebelumnya level sudah complit
    	
    	mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
		mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
		mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
		mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
		
		maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
		if(maxMissionScore == 4){
			mission1Score = 0;
			mission2Score = 0;
			mission3Score = 0;
			mission4Score = 0;
			maxMissionScore = 0;
		}
		
		point = Float.parseFloat(Record.getLastValuePoint(getApplicationContext()));
		
		
		if(Tools.getGameCheckState(getApplicationContext()) ==  4){
			if(mission4Score == 0){
				if(todayDistance >= todayDistanceToComplete){
					
					Log.i("Game Service", "Your Mission 4 in level "+level+" Complete");
					Log.i("Game Service", "Distance today must : "+todayDistanceToComplete+", Your today distance : "+todayDistance);
					
					point += 0.25f;
					int levelToSend = level;
					if(maxMissionScore == 3){
						levelToSend += 1;
					}
					
					
					Record.insertRecord(getApplicationContext(), 
							Account.getIdShesop(getApplicationContext()), 
							currentDate, 
							String.valueOf(mission1Score), 
							String.valueOf(mission2Score), 
							String.valueOf(mission3Score), 
							"1", 
							String.valueOf(point), 
							String.valueOf(levelToSend));
					// cek : delete db if 4 mission complete
					mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
					mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
					mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
					mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
					
					maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
					if(maxMissionScore == 4){
						Record.deleteGameMeasurement(getApplicationContext());
					}
				}else{
					Log.d("Game Service", "Your Mission 4 in level "+level+" not Complete");
					Log.d("Game Service", "Distance today must : "+todayDistanceToComplete+", Your today distance only : "+todayDistance);
				}
			} else {
				Log.d("Game Service", "Mission 4 has ben complete in level "+level);
			}
			Tools.updateGameCheckState(getApplicationContext(), "5");
		}
		
		
		
		
		
		// check for mission 1 from band
		level = Integer.parseInt(Record.getLastValueLevel(getApplicationContext()));
    	rowPos = level;
    	
    	twoHoursStep = Tools.getTwoHoursStepBand(getApplicationContext());
    	twoHoursStepToComplete = Integer.parseInt(Mission.getDetailMission(getApplicationContext(), rowPos, 2));	
		    	
		    	mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
				mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
				mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
				mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
				
				maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
				if(maxMissionScore == 4){
					mission1Score = 0;
					mission2Score = 0;
					mission3Score = 0;
					mission4Score = 0;
					maxMissionScore = 0;
				}
				
				point = Float.parseFloat(Record.getLastValuePoint(getApplicationContext()));
				
				if(Tools.getGameCheckState(getApplicationContext()) ==  5){
					if(mission1Score == 0){
						if(twoHoursStep >= twoHoursStepToComplete){
							Log.i("Game Service Band", "Your Mission 1 in level "+level+" Complete");
							Log.i("Game Service Band", "Step in 2 hrs must : "+twoHoursStepToComplete+", Your 2 hrs step : "+Record.getTwoHoursStep(getApplicationContext()));
							point += 0.25f;
							int levelToSend = level;
							if(maxMissionScore == 3){
								levelToSend += 1;
							}
							Record.insertRecord(getApplicationContext(), 
									Account.getIdShesop(getApplicationContext()), 
									currentDate, 
									"1", 
									String.valueOf(mission2Score), 
									String.valueOf(mission3Score), 
									String.valueOf(mission4Score), 
									String.valueOf(point), 
									String.valueOf(levelToSend));
							
							// cek : delete db if 4 mission complete
							mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
							mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
							mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
							mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
							
							maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
							if(maxMissionScore == 4){
								Tools.deleteGameMeasurementBand(getApplicationContext());
							}
							
							
						} else {
							Log.d("Game Service Band", "Your Mission 1 in level "+level+" not Complete");
							Log.d("Game Service Band", "Step in 2 hrs must : "+twoHoursStepToComplete+", Your 2 hrs step only : "+twoHoursStep);
						}
					} else {
						Log.d("Game Service Band", "Mission 1 has ben complete in level "+level);
					}
					Tools.updateGameCheckState(getApplicationContext(), "6");
				}
		
		
				// check for mission 2 from band
				level = Integer.parseInt(Record.getLastValueLevel(getApplicationContext()));
		    	rowPos = level;
		    	
		    	twoHoursDistance = Tools.getTwoHoursDistanceBand(getApplicationContext());
		    	twoHoursDistanceToComplete = Float.parseFloat(Mission.getDetailMission(getApplicationContext(), rowPos, 3));	
		    	
		    	mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
				mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
				mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
				mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
				
				maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
				if(maxMissionScore == 4){
					mission1Score = 0;
					mission2Score = 0;
					mission3Score = 0;
					mission4Score = 0;
					maxMissionScore = 0;
				}
				
				point = Float.parseFloat(Record.getLastValuePoint(getApplicationContext()));
				
				
				 
				if(Tools.getGameCheckState(getApplicationContext()) ==  6){
					if(mission2Score == 0){
						Log.i("Game Service Band", "mission 2 score : "+mission2Score);
						if(twoHoursDistance >= twoHoursDistanceToComplete){
							
							Log.i("Game Service Band", "Your Mission 2 in level "+level+" Complete");
							Log.i("Game Service Band", "Distance in 2 hrs must : "+twoHoursDistanceToComplete+", Your 2 hrs distance : "+twoHoursDistance);
							
							point += 0.25f;
							int levelToSend = level;
							
							if(maxMissionScore == 3){
								levelToSend += 1;
							}
								
							Record.insertRecord(getApplicationContext(), 
									Account.getIdShesop(getApplicationContext()), 
									currentDate, 
									String.valueOf(mission1Score), 
									"1", 
									String.valueOf(mission3Score), 
									String.valueOf(mission4Score), 
									String.valueOf(point), 
									String.valueOf(levelToSend));
							
							// cek : delete db if 4 mission complete
							mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
							mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
							mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
							mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
							
							maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
							if(maxMissionScore == 4){
							Tools.deleteGameMeasurementBand(getApplicationContext());
							}
							
							
							
						}else{
							Log.d("Game Service Band", "Your Mission 2 in level "+level+" not Complete");
							Log.d("Game Service Band", "Distance in 2 hrs must : "+twoHoursDistanceToComplete+", Your 2 distance step only : "+twoHoursDistance);
						}
					} else {
						Log.d("Game Service Band", "Mission 2 has ben complete in level "+level);
					}
					Tools.updateGameCheckState(getApplicationContext(), "7");
				}
		
		
				
				// check for mission 3 from band
				
				level = Integer.parseInt(Record.getLastValueLevel(getApplicationContext()));
		    	rowPos = level;
		    	
				todayStep = Tools.getTodayStepBand(getApplicationContext());
		    	todayStepToComplete = Integer.parseInt(Mission.getDetailMission(getApplicationContext(), rowPos, 4));
		    	
		    	mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
				mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
				mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
				mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
				
				maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
				if(maxMissionScore == 4){
					mission1Score = 0;
					mission2Score = 0;
					mission3Score = 0;
					mission4Score = 0;
					maxMissionScore = 0;
				}
				
				point = Float.parseFloat(Record.getLastValuePoint(getApplicationContext()));
				
				
				if(Tools.getGameCheckState(getApplicationContext()) ==  7){
					if(mission3Score == 0){
						if(todayStep >= todayStepToComplete){
							
							Log.i("Game Service Band", "Your Mission 3 in level "+level+" Complete");
							Log.i("Game Service Band", "Today step must : "+todayStepToComplete+", Your today step : "+todayStep);
							
							point += 0.25f;
							int levelToSend = level;
							if(maxMissionScore == 3){
								levelToSend += 1;
							}
							
							
							Record.insertRecord(getApplicationContext(), 
									Account.getIdShesop(getApplicationContext()), 
									currentDate, 
									String.valueOf(mission1Score), 
									String.valueOf(mission2Score), 
									"1", 
									String.valueOf(mission4Score), 
									String.valueOf(point), 
									String.valueOf(levelToSend));
							
							// cek : delete db if 4 mission complete
							mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
							mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
							mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
							mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
							
							maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
							if(maxMissionScore == 4){
								Tools.deleteGameMeasurementBand(getApplicationContext());
							}
							
							
						}else{
							Log.d("Game Service Band", "Your Mission 3 in level "+level+" not Complete");
							Log.d("Game Service Band", "Step today must : "+todayStepToComplete+", Your today step only : "+todayStep);
						}
					} else {
						Log.d("Game Service Band", "Mission 3 has ben complete in level : "+level);
					}
					Tools.updateGameCheckState(getApplicationContext(), "8");
				}
				
				
				// check for mission 4 from band
				level = Integer.parseInt(Record.getLastValueLevel(getApplicationContext()));
		    	rowPos = level;
				
				todayDistance = Tools.getTodayDistanceBand(getApplicationContext());
		    	todayDistanceToComplete = Float.parseFloat(Mission.getDetailMission(getApplicationContext(), rowPos, 5));
		    	 	
		    	// cek apakah sebelumnya level sudah complit
		    	
		    	mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
				mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
				mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
				mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
				
				maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
				if(maxMissionScore == 4){
					mission1Score = 0;
					mission2Score = 0;
					mission3Score = 0;
					mission4Score = 0;
					maxMissionScore = 0;
				}
				
				point = Float.parseFloat(Record.getLastValuePoint(getApplicationContext()));
				
				
				if(Tools.getGameCheckState(getApplicationContext()) ==  8){
					if(mission4Score == 0){
						if(todayDistance >= todayDistanceToComplete){
							
							Log.i("Game Service Band", "Your Mission 4 in level "+level+" Complete");
							Log.i("Game Service Band", "Distance today must : "+todayDistanceToComplete+", Your today distance : "+todayDistance);
							
							point += 0.25f;
							int levelToSend = level;
							if(maxMissionScore == 3){
								levelToSend += 1;
							}
							
							
							Record.insertRecord(getApplicationContext(), 
									Account.getIdShesop(getApplicationContext()), 
									currentDate, 
									String.valueOf(mission1Score), 
									String.valueOf(mission2Score), 
									String.valueOf(mission3Score), 
									"1", 
									String.valueOf(point), 
									String.valueOf(levelToSend));
							// cek : delete db if 4 mission complete
							mission1Score = Integer.parseInt(Record.getLastValueMission1(getApplicationContext()));
							mission2Score = Integer.parseInt(Record.getLastValueMission2(getApplicationContext()));
							mission3Score = Integer.parseInt(Record.getLastValueMission3(getApplicationContext()));
							mission4Score = Integer.parseInt(Record.getLastValueMission4(getApplicationContext()));
							
							maxMissionScore = mission1Score + mission2Score + mission3Score + mission4Score;
							if(maxMissionScore == 4){
								Tools.deleteGameMeasurementBand(getApplicationContext());
							}
						}else{
							Log.d("Game Service Band", "Your Mission 4 in level "+level+" not Complete");
							Log.d("Game Service Band", "Distance today must : "+todayDistanceToComplete+", Your today distance only : "+todayDistance);
						}
					} else {
						Log.d("Game Service Band", "Mission 4 has ben complete in level "+level);
					}
					Tools.updateGameCheckState(getApplicationContext(), "1");
				}
		
		
    }
}
