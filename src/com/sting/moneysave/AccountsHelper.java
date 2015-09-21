package com.sting.moneysave;

import java.util.ArrayList;
import java.util.List;

import com.sting.moneysave.providers.MoneySaveInfo.MoneyAccountsTableMetaData;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class AccountsHelper {
	
	private static final String TAG = "AccountsHelper";
	private static final String sortorder_ASC = " ASC";
	
	public static List<Accounts> listAccounts(ContentResolver cr) {
		Log.d(TAG, "listAccounts...");
		
		Cursor cs = cr.query(MoneyAccountsTableMetaData.CONTENT_URI, null, null, null, MoneyAccountsTableMetaData._ID + sortorder_ASC);
		int iidIdx = cs.getColumnIndex(MoneyAccountsTableMetaData._ID);
		int nameIdx = cs.getColumnIndex(MoneyAccountsTableMetaData.MAccounts_ACNAME);
        	int moneyIdx = cs.getColumnIndex(MoneyAccountsTableMetaData.MAccounts_MONEY);
        	List<Accounts> alist = new ArrayList<Accounts>();
        	Accounts acc;
        	for (cs.moveToFirst(); !cs.isAfterLast(); cs.moveToNext()) {
        		acc = new Accounts();
        		acc.setId(cs.getInt(iidIdx));
        		acc.setAccountName(cs.getString(nameIdx));
        		acc.setAccountMoney(cs.getInt(moneyIdx));
        		alist.add(acc);
        		Log.d(TAG, "listAccounts - " + acc);
        	}
        	Log.d(TAG, "listAccounts - " + cs.getCount());
        	cs.close();
        	return alist;
	}
	
	public static ArrayList<String> getAllAccName(ContentResolver cr){
		Log.d(TAG, "getAllAccName...");
		ArrayList<String> all = new ArrayList<String>();
		Cursor cs = cr.query(MoneyAccountsTableMetaData.CONTENT_URI, 
						null, null, null, MoneyAccountsTableMetaData._ID + sortorder_ASC);
		int nameIdx = cs.getColumnIndex(MoneyAccountsTableMetaData.MAccounts_ACNAME);
		for (cs.moveToFirst(); !cs.isAfterLast(); cs.moveToNext()) {
			all.add(cs.getString(nameIdx));
		}
		Log.d(TAG, "getAllAccName...  DONE");
		return all;
	}
	
	public static int getCount(ContentResolver cr){
		Log.d(TAG, "getCount...");
		int count = 0;
		Cursor cs = cr.query(MoneyAccountsTableMetaData.CONTENT_URI, null, null, null, null);
		for (cs.moveToFirst(); !cs.isAfterLast(); cs.moveToNext()) {
			count++;
		}
		return count;
	}

	public static Accounts getAccounts(ContentResolver cr, int id) {
		Log.d(TAG, "getAccounts..." + id);
		
		Uri getUri = Uri.withAppendedPath(MoneyAccountsTableMetaData.CONTENT_URI, String.valueOf(id));
		Cursor cs = cr.query(getUri, null, null, null, null);
		int idIdx = cs.getColumnIndex(MoneyAccountsTableMetaData._ID);
        	int nameIdx = cs.getColumnIndex(MoneyAccountsTableMetaData.MAccounts_ACNAME);
        	int moneyIdx = cs.getColumnIndex(MoneyAccountsTableMetaData.MAccounts_MONEY);
        	Accounts acc;
        	if (cs.moveToNext()) {
        		acc = new Accounts();
        		acc.setId(cs.getInt(idIdx));
        		acc.setAccountName(cs.getString(nameIdx));
        		acc.setAccountMoney(cs.getInt(moneyIdx));
        	}else {
        		 throw new IllegalArgumentException("Unknown id - " + id);
        	}
	 	cs.close();
        	Log.d(TAG, "getAccounts - " + acc);
        	return acc;
	}
	
	public static Accounts getAccountsByName(ContentResolver cr, String name){
		Log.d(TAG, "getAccountsByName..." + name);
		Accounts acc;
		String[] name_arg = {name};
		Cursor cs = cr.query(MoneyAccountsTableMetaData.CONTENT_URI, 
								null, 
								MoneyAccountsTableMetaData.MAccounts_ACNAME + " = ?", 
								name_arg, 
								null);
		int idIdx = cs.getColumnIndex(MoneyAccountsTableMetaData._ID);
        	int nameIdx = cs.getColumnIndex(MoneyAccountsTableMetaData.MAccounts_ACNAME);
        	int moneyIdx = cs.getColumnIndex(MoneyAccountsTableMetaData.MAccounts_MONEY);
		if (cs.moveToNext()) {
        		acc = new Accounts();
        		acc.setId(cs.getInt(idIdx));
        		acc.setAccountName(cs.getString(nameIdx));
        		acc.setAccountMoney(cs.getInt(moneyIdx));
        	}else {
            		throw new IllegalArgumentException("Unknown name - " + name);
        	}
		cs.close();		
		Log.d(TAG, "getAccountsByName - " + acc);
		return acc;
	}
	
	public static void addAccounts(ContentResolver cr, Accounts acc) {
		Log.d(TAG, "addAccounts..." + acc);
		ContentValues cv = new ContentValues();
		cv.put(MoneyAccountsTableMetaData.MAccounts_ACNAME, acc.getAccountName());
		cv.put(MoneyAccountsTableMetaData.MAccounts_MONEY, acc.getAccountMoney());
		Uri addedUri = cr.insert(MoneyAccountsTableMetaData.CONTENT_URI, cv);
        	Log.d(TAG, "addAccounts - " + addedUri);
	}
	
	public static void updateAccounts(ContentResolver cr, Accounts acc) {
		Log.d(TAG, "updateAccounts..." + acc);
        	ContentValues cv = new ContentValues();
        	cv.put(MoneyAccountsTableMetaData.MAccounts_ACNAME, acc.getAccountName());
		cv.put(MoneyAccountsTableMetaData.MAccounts_MONEY, acc.getAccountMoney());
		Uri updatedUri = Uri.withAppendedPath(MoneyAccountsTableMetaData.CONTENT_URI, String.valueOf(acc.getId()));
        	int cnt = cr.update(updatedUri, cv, null, null);
        	Log.d(TAG, "updateAccounts - " + cnt);
	}
	
	public static void deleteAllAccounts(ContentResolver cr) {
        	Log.d(TAG, "deleteAllAccounts...");
        	int cnt = cr.delete(MoneyAccountsTableMetaData.CONTENT_URI, null, null);
        	Log.d(TAG, "deleteAllAccounts - " + cnt);
    	}
	
	public static void deleteAccounts(ContentResolver cr, int id) {
        	Log.d(TAG, "deleteAccounts...");
        	Uri deletedUri = Uri.withAppendedPath(MoneyAccountsTableMetaData.CONTENT_URI, String.valueOf(id));
        	int cnt = cr.delete(deletedUri, null, null);
        	Log.d(TAG, "deleteAccounts - " + cnt);
    	}
	
}
