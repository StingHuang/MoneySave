package com.sting.moneysave;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BackupService extends Service {

    	private static final String TAG = "BackupService";
	private final String fileName_item = "MoneySave_ItemData.txt";
	private final String fileName_account = "MoneySave_AccountData.txt";
	private FileOutputStream outputStream;
	private BufferedOutputStream bufferedOutputStream;
	private boolean writeDONE = false;

	private Handler sHandler = new Handler();
	
	private Runnable backupFile = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			writeDONE = writeFile();
			if(writeDONE==true)	stopSelf();
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		writeDONE = false;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy....");
		sHandler.removeCallbacks(backupFile);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		sHandler.post(backupFile);
		return super.onStartCommand(intent, flags, startId);
	}
	
	private boolean writeFile(){
		boolean done = false;
		try {
			Log.d(TAG, "writeFile.... ing");
			
			outputStream = openFileOutput(fileName_item, MODE_PRIVATE);
			bufferedOutputStream = new BufferedOutputStream(outputStream);
			List<Items> allItems = ItemsHelper.listAllItems(getContentResolver());
			for(Items i : allItems){
				String line = i.writeToFile() + "\n";
				Log.d(TAG, line);
				bufferedOutputStream.write(line.getBytes());
			}
			bufferedOutputStream.close();
			
			outputStream = openFileOutput(fileName_account, MODE_PRIVATE);
			bufferedOutputStream = new BufferedOutputStream(outputStream);
			List<Accounts> allAccounts = AccountsHelper.listAccounts(getContentResolver());
			for(Accounts a : allAccounts){
				String line = a.writeToFile() + "\n";
				Log.d(TAG, line);
				bufferedOutputStream.write(line.getBytes());
			}
			bufferedOutputStream.close();
			
			Log.d(TAG, "writeFile.... done");
			done = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return done;
	}

}
