package com.sting.moneysave;

import java.text.ParseException;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.view.WindowManager;

public class MoneyAlarmReceiver extends BroadcastReceiver {

	private final String KEY_Receiver = "MAR_key";
	private final String Receiver_Alarm = "type2";
	private final String Receiver_Notify = "type3";
	private final String Receiver_Backup = "type4";
	private final String KEY_SetBudget = "SET_BUDGET";
	private String dlg_megLine1 = "Today's cost: NT$";
	private String dlg_megLine2_over = "\n\nOverspend by NT$";
	private String dlg_megLine2_blc = "\n\nToday's balance is NT$";
	private String dlg_btn_p = "OK";
	private String dlg_btn_n = "Cancel";
	private String ntf_ticker = "MoneySave Notice";
	private String ntf_ctitle = "MoneySave";
	private String ntf_ctext = "Time to record";
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle bData = intent.getExtras();
		if(bData.get(KEY_Receiver).equals(Receiver_Alarm)){
			Calendar mCalendar = Calendar.getInstance();
			long day_stamp = 0;
			try {
				day_stamp = StingFunction.toDateStamp(StingFunction.toDateString(mCalendar.getTimeInMillis()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			int mbudget = Integer.parseInt(sharedPref.getString(KEY_SetBudget, "0"));
			int onedaycost = ItemsHelper.getOnedayCost(context.getContentResolver(), day_stamp);
						
			AlertDialog.Builder alarmDlgB = new AlertDialog.Builder(context);
			alarmDlgB.setTitle(R.string.alarmDlg_title);

			String ml1 = dlg_megLine1+onedaycost ,ml2;
			if(mbudget<=onedaycost){
				ml2 = dlg_megLine2_over + Integer.toString(onedaycost-mbudget);
			}else{
				ml2 = dlg_megLine2_blc + Integer.toString(mbudget-onedaycost);
			}
			alarmDlgB.setMessage(ml1+ml2);
			
			alarmDlgB.setPositiveButton(dlg_btn_p, 
					new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent it = new Intent();
							it.setClass(context, MainActivity.class);
							it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(it);
						}});
			alarmDlgB.setNegativeButton(dlg_btn_n, 
					new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}});
			AlertDialog dialog = alarmDlgB.create();
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialog.show();
		}
		else if(bData.get(KEY_Receiver).equals(Receiver_Notify)){			
			int noId = 1;
			NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);
			nBuilder.setSmallIcon(R.drawable.ic_launcher)
					.setWhen(System.currentTimeMillis())
					.setTicker(ntf_ticker)
			        .setContentTitle(ntf_ctitle)
			        .setContentText(ntf_ctext)
					.setAutoCancel(true);

			Intent call = new Intent(context, MainActivity.class);
			PendingIntent pIntent = PendingIntent.getActivity(context, 200, call, 0);
			nBuilder.setContentIntent(pIntent);

			NotificationManager mNotificationManager =
				    (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(noId, nBuilder.build());
		}
		else if(bData.get(KEY_Receiver).equals(Receiver_Backup)){
			Intent rbIntent = new Intent(context, BackupService.class);
			context.startService(rbIntent);
		}
		
	}
}
