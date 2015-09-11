package com.sting.moneysave;

import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class BootReceiver extends BroadcastReceiver {
	
	private final String KEY_CboxAlarm = "CBOX_ALARM";
	private final String KEY_PrefSetAlarm = "SET_ALARM";
	private final String KEY_CboxNotify = "CBOX_NOTIFY";
	private final String KEY_CboxBackup = "CBOX_BACKUP";
	private final String KEY_MSListNotifyTime = "MSList_NOTIFY_TIME";
	private final String Boot_Action = "android.intent.action.BOOT_COMPLETED";
	private final String KEY_Receiver = "MAR_key";
	private final String Receiver_Alarm = "type2";
	private final String Receiver_Notify = "type3";
	private final String Receiver_Backup = "type4";
	private final String backupTime = "23:59";
	private final long oneday = 86400000;
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(Boot_Action)) {
            // Set the alarm here.
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			if(sharedPref.getBoolean(KEY_CboxAlarm, false)){
				String clockTime = sharedPref.getString(KEY_PrefSetAlarm,"");
				int alarm_h = Integer.parseInt(clockTime.split(":")[0]);
	        	int alarm_m = Integer.parseInt(clockTime.split(":")[1]);
	        	Calendar c_alarm = Calendar.getInstance();
	    		c_alarm.set(Calendar.HOUR_OF_DAY, alarm_h);
	    		c_alarm.set(Calendar.MINUTE, alarm_m);
	    		Intent ait = new Intent(context, MoneyAlarmReceiver.class);
	    		ait.putExtra(KEY_Receiver, Receiver_Alarm);
	    		int requestCode = 10;
	    		PendingIntent pi = PendingIntent.getBroadcast(
	    							context, requestCode, ait, 
	    							PendingIntent.FLAG_UPDATE_CURRENT);
	    		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	    		if((c_alarm.getTimeInMillis()-System.currentTimeMillis())>0){
					am.setRepeating(AlarmManager.RTC_WAKEUP, c_alarm.getTimeInMillis(),
		                    		AlarmManager.INTERVAL_DAY, pi);
				}
				else{
					am.setRepeating(AlarmManager.RTC_WAKEUP, c_alarm.getTimeInMillis()+oneday,
	                				AlarmManager.INTERVAL_DAY, pi);
				}
			}
			
			if(sharedPref.getBoolean(KEY_CboxNotify, false)){
				Set<String> selected = sharedPref.getStringSet(KEY_MSListNotifyTime, null);
				AlarmManager notifyAlarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				Intent ni = new Intent(context, MoneyAlarmReceiver.class);
				ni.putExtra(KEY_Receiver, Receiver_Notify);
				TreeSet<Integer> time_list = new TreeSet<Integer>();
				for(String at: selected)	time_list.add(Integer.parseInt(at));				
				Calendar c_alarm = Calendar.getInstance();
				int i = 1;
				for(int time: time_list)
				{
					PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, ni, 0);
					c_alarm.set(Calendar.HOUR_OF_DAY, time);
					c_alarm.set(Calendar.MINUTE, 0);
					c_alarm.set(Calendar.SECOND, 0);
					
					if((c_alarm.getTimeInMillis()-System.currentTimeMillis())>0){
						notifyAlarm.setRepeating(AlarmManager.RTC_WAKEUP, c_alarm.getTimeInMillis(),
    												AlarmManager.INTERVAL_DAY, pendingIntent);
					}else{
						notifyAlarm.setRepeating(AlarmManager.RTC_WAKEUP, c_alarm.getTimeInMillis()+oneday,
								AlarmManager.INTERVAL_DAY, pendingIntent);
					}
					i++;
				}	
			}
			
			if(sharedPref.getBoolean(KEY_CboxBackup, false)){
				Calendar c_alarm = Calendar.getInstance();
				c_alarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(backupTime.split(":")[0]));
				c_alarm.set(Calendar.MINUTE, Integer.parseInt(backupTime.split(":")[1]));
				AlarmManager backupAlarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				Intent backupIntent = new Intent(context, MoneyAlarmReceiver.class);
				backupIntent.putExtra(KEY_Receiver, Receiver_Backup);
				int requestCode = 200;
				PendingIntent backupPI = PendingIntent.getBroadcast(context, requestCode, backupIntent, 
																		PendingIntent.FLAG_UPDATE_CURRENT);
				Intent rbIntent = new Intent(context, BackupService.class);
				context.startService(rbIntent);
				backupAlarm.setRepeating(AlarmManager.RTC_WAKEUP, c_alarm.getTimeInMillis(), 
											AlarmManager.INTERVAL_DAY, backupPI);
			}
        }
	}

}
