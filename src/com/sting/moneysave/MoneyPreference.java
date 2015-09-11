package com.sting.moneysave;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

public class MoneyPreference extends AppCompatActivity {

	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	private final String KEY_EdtBudget = "SET_BUDGET";
	private final String KEY_CboxAlarm = "CBOX_ALARM";
	private final String KEY_PrefSetAlarm = "SET_ALARM";
	private final String KEY_CboxNotify = "CBOX_NOTIFY";
	private final String KEY_MSListNotifyTime = "MSList_NOTIFY_TIME";
	private final String KEY_CboxBackup = "CBOX_BACKUP";
	private final String KEY_RecoverData = "RECOVER_DATA";
	
	private final String KEY_Receiver = "MAR_key";
	private final String Receiver_Alarm = "type2";
	private final String Receiver_Notify = "type3";
	private final String Receiver_Backup = "type4";
	private final long oneday = 86400000;
	
	private final String fileName_item = "MoneySave_ItemData.txt";
	private final String fileName_account = "MoneySave_AccountData.txt";
	private String budget_sum = "Everyday budget is NT$";
	private String alarm_sum = "Set the alarm clock for ";
	private String clockTime = "12:00";
	private String backupTime = "23:59";
	private int alarm_h, alarm_m, mBudget;
	private String selectTime_meg = "You have to select time";
	
	private CheckBoxPreference pCboxAlarm, pCboxNotify, pCboxBackup;
	private EditTextPreference pEdtBudget;
	private Preference pSetAlarm, pRecover;
	private MultiSelectListPreference pSetNotifyTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getFragmentManager().beginTransaction()
							.replace(android.R.id.content, new PrefsFragement()).commit();  
	}
	
	public class PrefsFragement extends PreferenceFragment {  
        
		@Override  
        public void onCreate(Bundle savedInstanceState) {  
            // TODO Auto-generated method stub  
            super.onCreate(savedInstanceState);  
            addPreferencesFromResource(R.xml.m_preference);  
            
            sharedPref = PreferenceManager.getDefaultSharedPreferences(MoneyPreference.this);
            editor = sharedPref.edit();
            
            pEdtBudget = (EditTextPreference)findPreference(KEY_EdtBudget);
            if(Integer.parseInt(sharedPref.getString(KEY_EdtBudget, "0"))!=0){
        		mBudget = Integer.parseInt(sharedPref.getString(KEY_EdtBudget, "0"));
        	 	pEdtBudget.setSummary(budget_sum + mBudget);
        	}else{
        		mBudget = 0;
        		pEdtBudget.setSummary("");
        	}
            pCboxAlarm = (CheckBoxPreference)findPreference(KEY_CboxAlarm);
            pSetAlarm = (Preference)findPreference(KEY_PrefSetAlarm);
            if(!sharedPref.getString(KEY_PrefSetAlarm,"").matches(""))
            	clockTime = sharedPref.getString(KEY_PrefSetAlarm,"");
            pSetAlarm.setSummary(alarm_sum + clockTime);
        	alarm_h = Integer.parseInt(clockTime.split(":")[0]);
        	alarm_m = Integer.parseInt(clockTime.split(":")[1]);
            
            pCboxNotify = (CheckBoxPreference)findPreference(KEY_CboxNotify);
            pSetNotifyTime = (MultiSelectListPreference)findPreference(KEY_MSListNotifyTime);
            
            pCboxBackup = (CheckBoxPreference)findPreference(KEY_CboxBackup);
            pRecover = (Preference)findPreference(KEY_RecoverData);
            
            pEdtBudget.setOnPreferenceChangeListener(setbudget);
            pCboxAlarm.setOnPreferenceChangeListener(cbox_change);
            pSetAlarm.setOnPreferenceClickListener(setalarm);
            pCboxNotify.setOnPreferenceChangeListener(cbox_change);
            pSetNotifyTime.setOnPreferenceChangeListener(select_time);
            pCboxBackup.setOnPreferenceChangeListener(cbox_change);
            pRecover.setOnPreferenceClickListener(showDlg);
        }
		
		private Preference.OnPreferenceClickListener showDlg = new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				AlertDialog.Builder recoverDlg = new AlertDialog.Builder(MoneyPreference.this);
				recoverDlg.setTitle(R.string.recover_dlg_title);
				recoverDlg.setMessage(R.string.recover_dlg_message);
				recoverDlg.setPositiveButton(R.string.recover_dlg_btn_p, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						final ProgressDialog PDialog = ProgressDialog.show(MoneyPreference.this, "Waiting", "Just a few minutes", true);
						new Thread(){
						    public void run(){
						    	try{
						    		InputStream inputStream1 = openFileInput(fileName_item);
						    		if(inputStream1!=null){
						    			ItemsHelper.deleteAllItems(getContentResolver());
						    			InputStreamReader inputStreamReader = new InputStreamReader(inputStream1);
						    			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
						    			String receiveString = "";
						                while ( (receiveString = bufferedReader.readLine()) != null ) {
						                	Items i = makeItem(receiveString);
						                	ItemsHelper.addItems(getContentResolver(), i);
						                }
						                inputStream1.close();
						    		}
						    		sleep(1000);
						    		InputStream inputStream2 = openFileInput(fileName_account);
						    		if(inputStream2!=null){
						    			AccountsHelper.deleteAllAccounts(getContentResolver());
						    			InputStreamReader inputStreamReader = new InputStreamReader(inputStream2);
						    			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
						    			String receiveString = "";
						                while ( (receiveString = bufferedReader.readLine()) != null ) {
						                	Accounts a = makeAccount(receiveString);
						                	AccountsHelper.addAccounts(getContentResolver(), a);
						                }
						                inputStream2.close();
						    		}
						    		sleep(3000);
						    	}catch(Exception e){
						    		e.printStackTrace();
						    	}
						    	finally{
						    		PDialog.dismiss();
						     }
						    }
						}.start();
					}
				});
				recoverDlg.setNegativeButton(R.string.recover_dlg_btn_n, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				recoverDlg.show();				
				return true;
			}
		};
		
		private Preference.OnPreferenceChangeListener setbudget = 
				new Preference.OnPreferenceChangeListener(){

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				// TODO Auto-generated method stub
				mBudget = Integer.parseInt(newValue.toString());
				editor.putString(KEY_EdtBudget, newValue.toString());
				pEdtBudget.setSummary(budget_sum + mBudget);
				return true;
			}
		};
        
        private Preference.OnPreferenceChangeListener cbox_change = 
        		new Preference.OnPreferenceChangeListener(){

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub
						if(preference.getKey().equals(KEY_CboxAlarm)){
							setAlarm(Boolean.parseBoolean(newValue.toString()));
						}
						else if(preference.getKey().equals(KEY_CboxNotify)){
							setNotify(Boolean.parseBoolean(newValue.toString()),
									pSetNotifyTime.getValues());
						}else if(preference.getKey().equals(KEY_CboxBackup)){
							setBackup(Boolean.parseBoolean(newValue.toString()));
						}
						return true;
					}
		};
        
		private Preference.OnPreferenceClickListener setalarm = 
				new Preference.OnPreferenceClickListener(){
			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				TimePickerDialog tpDlg = new TimePickerDialog(
						MoneyPreference.this, new TimePickerDialog.OnTimeSetListener() {
							
							@Override
							public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
								// TODO Auto-generated method stub
								alarm_h = hourOfDay;
								alarm_m = minute;
								clockTime = String.format("%02d", alarm_h) + ":" + String.format("%02d", alarm_m);
								editor.putString(KEY_PrefSetAlarm, clockTime);
								editor.commit();
								pSetAlarm.setSummary(alarm_sum + clockTime);
								setAlarm(true);
							}
						},
						alarm_h,
						alarm_m, true);
				tpDlg.show();
				return true;
			}
		};
	
		private Preference.OnPreferenceChangeListener select_time = 
				new Preference.OnPreferenceChangeListener(){

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				// TODO Auto-generated method stub
				@SuppressWarnings("unchecked")
				Set<String> selected = (Set<String>) newValue;
				if(selected.size()<=0){
					Toast.makeText(MoneyPreference.this, selectTime_meg, Toast.LENGTH_LONG).show();
				}else{
					setNotify(false, selected);
					setNotify(true, selected);
				}
				return true;
			}
		};
    }
	
	public void setAlarm(boolean turnOn){
		Calendar c_alarm = Calendar.getInstance();
		c_alarm.set(Calendar.HOUR_OF_DAY, alarm_h);
		c_alarm.set(Calendar.MINUTE, alarm_m);
		c_alarm.set(Calendar.SECOND, 0);
		Intent intent = new Intent(MoneyPreference.this, MoneyAlarmReceiver.class);
		intent.putExtra(KEY_Receiver, Receiver_Alarm);
		int requestCode = 100;
		PendingIntent pi = PendingIntent.getBroadcast(
							MoneyPreference.this, requestCode, intent, 
							PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		if(turnOn)
		{
			if((c_alarm.getTimeInMillis()-System.currentTimeMillis())>0){
				Log.d("MoneyPreference", "Alarm turn ON for "+alarm_h+":"+alarm_m);
				am.setRepeating(AlarmManager.RTC_WAKEUP, c_alarm.getTimeInMillis(),
	                    		AlarmManager.INTERVAL_DAY, pi);
			}
			else{
				Log.d("MoneyPreference", "NEXTDAY Alarm turn ON for "+alarm_h+":"+alarm_m);
				am.setRepeating(AlarmManager.RTC_WAKEUP, c_alarm.getTimeInMillis()+oneday,
                				AlarmManager.INTERVAL_DAY, pi);
			}
		}
		else{
			Log.d("MoneyPreference", "Alarm turn OFF ");
			am.cancel(pi);
		}
	}
	
	private void setNotify(boolean turnOn, Set<String> selected_time){
		AlarmManager notifyAlarm = (AlarmManager)getSystemService(ALARM_SERVICE);
		Intent ni = new Intent(MoneyPreference.this, MoneyAlarmReceiver.class);
		ni.putExtra(KEY_Receiver, Receiver_Notify);
		if(turnOn){	
			if(selected_time.size()>0){
				TreeSet<Integer> time_list = new TreeSet<Integer>();
				for(String at: selected_time)	time_list.add(Integer.parseInt(at));			
				Calendar c_alarm = Calendar.getInstance();
				int i = 1;
				for(int time: time_list)
				{
					PendingIntent pendingIntent = PendingIntent.getBroadcast(MoneyPreference.this, i, ni, 0);
					c_alarm.set(Calendar.HOUR_OF_DAY, time);
					c_alarm.set(Calendar.MINUTE, 0);
					c_alarm.set(Calendar.SECOND, 0);
					
					if((c_alarm.getTimeInMillis()-System.currentTimeMillis())>0){
						notifyAlarm.setRepeating(AlarmManager.RTC_WAKEUP, c_alarm.getTimeInMillis(),
    												AlarmManager.INTERVAL_DAY, pendingIntent);
						Log.d("MoneyPreference", "setNotify "+ time+":00"+" TODAY");
					}else{
						notifyAlarm.setRepeating(AlarmManager.RTC_WAKEUP, c_alarm.getTimeInMillis()+oneday,
								AlarmManager.INTERVAL_DAY, pendingIntent);
						Log.d("MoneyPreference", "setNotify "+ time+":00"+" NEXTDAY");
					}
					i++;
				}	
			}else{
				Toast.makeText(MoneyPreference.this, selectTime_meg, Toast.LENGTH_LONG).show();
			}
		}else{
			for(int i=1; i<=16; i++){
				PendingIntent pendingIntent = PendingIntent.getBroadcast(MoneyPreference.this, i, ni, 0);
				notifyAlarm.cancel(pendingIntent);
			}
		}
	}
	
	private void setBackup(boolean turnOn){
		Calendar c_alarm = Calendar.getInstance();
		c_alarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(backupTime.split(":")[0]));
		c_alarm.set(Calendar.MINUTE, Integer.parseInt(backupTime.split(":")[1]));
		AlarmManager backupAlarm = (AlarmManager)getSystemService(ALARM_SERVICE);
		Intent backupIntent = new Intent(MoneyPreference.this, MoneyAlarmReceiver.class);
		backupIntent.putExtra(KEY_Receiver, Receiver_Backup);
		int requestCode = 200;
		PendingIntent backupPI = PendingIntent.getBroadcast(MoneyPreference.this, requestCode, backupIntent, 
																PendingIntent.FLAG_UPDATE_CURRENT);
		if(turnOn){
			Intent rbIntent = new Intent(MoneyPreference.this, BackupService.class);
			startService(rbIntent);
			backupAlarm.setRepeating(AlarmManager.RTC_WAKEUP, c_alarm.getTimeInMillis(), 
										AlarmManager.INTERVAL_DAY, backupPI);
		}else{
			backupAlarm.cancel(backupPI);
		}
	}
	
	private Items makeItem(String oneLine){
    	String[] itemAttr = oneLine.split(",");
    	Items i = new Items();
    	i.setDateStamp(Long.parseLong(itemAttr[1]));
    	i.setDateYear(Integer.parseInt(itemAttr[2]));
    	i.setDateMonth(Integer.parseInt(itemAttr[3]));
    	i.setDateDay(Integer.parseInt(itemAttr[4]));
    	i.setType(Integer.parseInt(itemAttr[5]));
    	i.setCash(Integer.parseInt(itemAttr[6]));
    	i.setItemTag(Integer.parseInt(itemAttr[7]));
    	i.setItem(itemAttr[8]);
    	i.setFrom(itemAttr[9]);
    	i.setTo(itemAttr[10]);
    	return i;
    }
	private Accounts makeAccount(String oneLine){
		String[] accAttr = oneLine.split(",");
		Accounts a = new Accounts();
		a.setAccountName(accAttr[1]);
		a.setAccountMoney(Integer.parseInt(accAttr[2]));
		return a;
	}

}
