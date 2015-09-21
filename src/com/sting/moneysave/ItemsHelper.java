package com.sting.moneysave;

import java.util.ArrayList;
import java.util.List;

import com.sting.moneysave.providers.MoneySaveInfo.MoneyItemsTableMetaData;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class ItemsHelper {

	private static final String TAG = "ItemsHelper";
	private static final String sortorder_ASC = " ASC";
	private static final String sortorder_DESC = " DESC";
	private static final int iTypeOUT = 1;
	
	public static List<Items> listAllItems(ContentResolver cr){
		Log.d(TAG, "listAllItems...");
		Cursor cs = cr.query(MoneyItemsTableMetaData.CONTENT_URI, null, null, null, MoneyItemsTableMetaData._ID + sortorder_ASC);
		int idIdx = cs.getColumnIndex(MoneyItemsTableMetaData._ID);
		int datestampIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_DATE_stamp);
		int yearIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_DATE_year);
		int monthIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_DATE_month);
		int dayIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_DATE_day);
		int typeIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_TYPE);
		int cashIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_CASH);
		int tagIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_ITAG);
		int itemIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_ITEM);
		int fromIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_FROM);
		int toIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_TO);
		List<Items> ilist = new ArrayList<Items>();
		Items im;
		for (cs.moveToFirst(); !cs.isAfterLast(); cs.moveToNext()) {
			im = new Items();
        		im.setId(cs.getInt(idIdx));
        		im.setDateStamp(cs.getLong(datestampIdx));
        		im.setDateYear(cs.getInt(yearIdx));
        		im.setDateMonth(cs.getInt(monthIdx));
        		im.setDateDay(cs.getInt(dayIdx));
        		im.setType(cs.getInt(typeIdx));
        		im.setCash(cs.getInt(cashIdx));
        		im.setItemTag(cs.getInt(tagIdx));
        		im.setItem(cs.getString(itemIdx));
        		im.setFrom(cs.getString(fromIdx));
        		im.setTo(cs.getString(toIdx));   	
            		ilist.add(im);
            		Log.d(TAG, "listAllItems - " + im);
		}
		Log.d(TAG, "listAllItems - " + cs.getCount());
		cs.close();
		return ilist;
	}
	
	public static List<Items> listItems(AppCompatActivity act, String month, int year) {
		Log.d(TAG, "listItems...");
		String[] sa = {month};        
		Cursor cs = act.getContentResolver().query(MoneyItemsTableMetaData.CONTENT_URI, 
        						null, 
        						MoneyItemsTableMetaData.MItems_DATE_month + " = ?", 
        						sa, 
        						MoneyItemsTableMetaData.MItems_DATE_stamp + sortorder_DESC);
		int idIdx = cs.getColumnIndex(MoneyItemsTableMetaData._ID);
		int datestampIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_DATE_stamp);
		int yearIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_DATE_year);
		int monthIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_DATE_month);
		int dayIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_DATE_day);
		int typeIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_TYPE);
		int cashIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_CASH);
		int tagIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_ITAG);
		int itemIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_ITEM);
		int fromIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_FROM);
		int toIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_TO);
                
		List<Items> ilist = new ArrayList<Items>();
		Items im;
        	for (cs.moveToFirst(); !cs.isAfterLast(); cs.moveToNext()) {
        		if(cs.getInt(yearIdx)==year){
        			im = new Items();
            			im.setId(cs.getInt(idIdx));
            			im.setDateStamp(cs.getLong(datestampIdx));
            			im.setDateYear(cs.getInt(yearIdx));
            			im.setDateMonth(cs.getInt(monthIdx));
            			im.setDateDay(cs.getInt(dayIdx));
            			im.setType(cs.getInt(typeIdx));
            			im.setCash(cs.getInt(cashIdx));
            			im.setItemTag(cs.getInt(tagIdx));
            			im.setItem(cs.getString(itemIdx));
            			im.setFrom(cs.getString(fromIdx));
            			im.setTo(cs.getString(toIdx));   	
                		ilist.add(im);
                		Log.d(TAG, "listItems - " + im);
			}
        	}
        	Log.d(TAG, "listItems - " + cs.getCount());
        	cs.close();
        	return ilist;
    	}
	
	public static String[] getAllYear(ContentResolver cr){
		Log.d(TAG, "getAllYear...");
		ArrayList<String> year = new ArrayList<String>();
		Cursor cs = cr.query(MoneyItemsTableMetaData.CONTENT_URI, 
					null, 
					null, 
					null, 
					MoneyItemsTableMetaData.MItems_DATE_year + sortorder_ASC);
		int yearIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_DATE_year);
		for (cs.moveToFirst(); !cs.isAfterLast(); cs.moveToNext()) {
			String ys = Integer.toString(cs.getInt(yearIdx));
			if(!year.contains(ys))
				year.add(ys);
		}
		String years[] = (String[]) year.toArray(new String[0]);
		return years;
	}
	
	public static int getOnedayCost(ContentResolver cr, long day_stamp){
		int total_cost = 0;
		Log.d(TAG, "getOnedayCost..." + day_stamp);
		String[] days = {String.valueOf(day_stamp)};
		Cursor cs = cr.query(MoneyItemsTableMetaData.CONTENT_URI, 
					null, 
					MoneyItemsTableMetaData.MItems_DATE_stamp + " = ?", 
					days,
					null);
		int typeIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_TYPE);
		int cashIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_CASH);
		if(cs.getCount()>0){
			for (cs.moveToFirst(); !cs.isAfterLast(); cs.moveToNext()){
				if(cs.getInt(typeIdx)==iTypeOUT)
					total_cost = total_cost + cs.getInt(cashIdx);
			}
		}
		return total_cost;
	}
	
	public static Items getItems(ContentResolver cr, int id) {
		Log.d(TAG, "getItems..." + id);
		Uri getUri = Uri.withAppendedPath(MoneyItemsTableMetaData.CONTENT_URI, String.valueOf(id));
		Cursor cs = cr.query(getUri, null, null, null, null);
		int idIdx = cs.getColumnIndex(MoneyItemsTableMetaData._ID);
		int datestampIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_DATE_stamp);
		int yearIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_DATE_year);
		int monthIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_DATE_month);
		int dayIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_DATE_day);
		int typeIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_TYPE);
		int cashIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_CASH);
		int tagIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_ITAG);
		int itemIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_ITEM);
		int fromIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_FROM);
		int toIdx = cs.getColumnIndex(MoneyItemsTableMetaData.MItems_TO);
		Items im;
		if (cs.moveToNext()) {
			im = new Items();
			im.setId(cs.getInt(idIdx));
			im.setDateStamp(cs.getLong(datestampIdx));
			im.setDateYear(cs.getInt(yearIdx));
			im.setDateMonth(cs.getInt(monthIdx));
			im.setDateDay(cs.getInt(dayIdx));
			im.setType(cs.getInt(typeIdx));
			im.setCash(cs.getInt(cashIdx));
			im.setItemTag(cs.getInt(tagIdx));
			im.setItem(cs.getString(itemIdx));
			im.setFrom(cs.getString(fromIdx));
			im.setTo(cs.getString(toIdx));
		}
		else {
			throw new IllegalArgumentException("Unknown id - " + id);
		}
		cs.close();
		Log.d(TAG, "getItems - " + im);
		return im;
	}
	
	public static void addItems(ContentResolver cr, Items im) {
		Log.d(TAG, "addItems..." + im);
		ContentValues cv = new ContentValues();
		cv.put(MoneyItemsTableMetaData.MItems_DATE_stamp, im.getDateStamp());
		cv.put(MoneyItemsTableMetaData.MItems_DATE_year, im.getDateYear());
		cv.put(MoneyItemsTableMetaData.MItems_DATE_month, im.getDateMonth());
		cv.put(MoneyItemsTableMetaData.MItems_DATE_day, im.getDateDay());
		cv.put(MoneyItemsTableMetaData.MItems_TYPE, im.getType());
		cv.put(MoneyItemsTableMetaData.MItems_CASH, im.getCash());
		cv.put(MoneyItemsTableMetaData.MItems_ITAG, im.getItemTag());
		cv.put(MoneyItemsTableMetaData.MItems_ITEM, im.getItem());
		cv.put(MoneyItemsTableMetaData.MItems_FROM, im.getFrom());
		cv.put(MoneyItemsTableMetaData.MItems_TO, im.getTo());
		Uri addedUri = cr.insert(MoneyItemsTableMetaData.CONTENT_URI, cv);
		Log.d(TAG, "addItems - " + addedUri);
	}

	public static void updateItems(ContentResolver cr, Items im) {
        	Log.d(TAG, "updateItems..." + im);
        	ContentValues cv = new ContentValues();
        	cv.put(MoneyItemsTableMetaData.MItems_DATE_stamp, im.getDateStamp());
        	cv.put(MoneyItemsTableMetaData.MItems_DATE_year, im.getDateYear());
        	cv.put(MoneyItemsTableMetaData.MItems_DATE_month, im.getDateMonth());
		cv.put(MoneyItemsTableMetaData.MItems_DATE_day, im.getDateDay());
        	cv.put(MoneyItemsTableMetaData.MItems_TYPE, im.getType());
        	cv.put(MoneyItemsTableMetaData.MItems_CASH, im.getCash());
        	cv.put(MoneyItemsTableMetaData.MItems_ITAG, im.getItemTag());
        	cv.put(MoneyItemsTableMetaData.MItems_ITEM, im.getItem());
        	cv.put(MoneyItemsTableMetaData.MItems_FROM, im.getFrom());
        	cv.put(MoneyItemsTableMetaData.MItems_TO, im.getTo());
        	Uri updatedUri = Uri.withAppendedPath(
        		MoneyItemsTableMetaData.CONTENT_URI, String.valueOf(im.getId()));
        	int cnt = cr.update(updatedUri, cv, null, null);
        	Log.d(TAG, "updateItems - " + cnt);
    	}

    	public static void deleteAllItems(ContentResolver cr) {
        	Log.d(TAG, "deleteAllItems...");
        	int cnt = cr.delete(MoneyItemsTableMetaData.CONTENT_URI, null, null);
        	Log.d(TAG, "deleteAllItems - " + cnt);
    	}

    	public static void deleteItems(ContentResolver cr, int id) {
        	Log.d(TAG, "deleteItems...");
        	Uri deletedUri = Uri.withAppendedPath(MoneyItemsTableMetaData.CONTENT_URI, String.valueOf(id));
        	int cnt = cr.delete(deletedUri, null, null);
        	Log.d(TAG, "deleteItems - " + cnt);
    	}
	
}
