package com.sting.moneysave.providers;

import java.util.HashMap;
import java.util.Map;

import com.sting.moneysave.providers.MoneySaveInfo.MoneyItemsTableMetaData;
import com.sting.moneysave.providers.MoneySaveInfo.MoneyAccountsTableMetaData;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MoneySaveContentProvider extends ContentProvider {

private static final String TAG = "MoneySaveContentProvider";
	
	// 有時候 db 查詢因為使用 join 導致欄位名稱衝突或因為使用 table alias 讓欄位名稱變複雜
	// 所以定義一份對應關係，將 user 使用的欄位名稱對應到db使用的欄位名稱
	// 所有 user 會用到的欄位名稱都要定義，或者說 user 只能使用這個 map 裡定義的欄位名稱
	private static Map<String, String> MItems_projectionMap;
	static {
		MItems_projectionMap = new HashMap<String, String>();
		MItems_projectionMap.put(MoneyItemsTableMetaData._ID, MoneyItemsTableMetaData._ID);
		MItems_projectionMap.put(MoneyItemsTableMetaData.MItems_DATE_stamp, MoneyItemsTableMetaData.MItems_DATE_stamp);
		MItems_projectionMap.put(MoneyItemsTableMetaData.MItems_DATE_year, MoneyItemsTableMetaData.MItems_DATE_year);
		MItems_projectionMap.put(MoneyItemsTableMetaData.MItems_DATE_month, MoneyItemsTableMetaData.MItems_DATE_month);
		MItems_projectionMap.put(MoneyItemsTableMetaData.MItems_DATE_day, MoneyItemsTableMetaData.MItems_DATE_day);
		MItems_projectionMap.put(MoneyItemsTableMetaData.MItems_TYPE, MoneyItemsTableMetaData.MItems_TYPE);
		MItems_projectionMap.put(MoneyItemsTableMetaData.MItems_CASH, MoneyItemsTableMetaData.MItems_CASH);
		MItems_projectionMap.put(MoneyItemsTableMetaData.MItems_ITAG, MoneyItemsTableMetaData.MItems_ITAG);
		MItems_projectionMap.put(MoneyItemsTableMetaData.MItems_ITEM, MoneyItemsTableMetaData.MItems_ITEM);
		MItems_projectionMap.put(MoneyItemsTableMetaData.MItems_FROM, MoneyItemsTableMetaData.MItems_FROM);
		MItems_projectionMap.put(MoneyItemsTableMetaData.MItems_TO, MoneyItemsTableMetaData.MItems_TO);
	}
	private static Map<String, String> MAccounts_projectionMap;
	static{
		MAccounts_projectionMap = new HashMap<String, String>();
		MAccounts_projectionMap.put(MoneyAccountsTableMetaData._ID, MoneyAccountsTableMetaData._ID);
		MAccounts_projectionMap.put(MoneyAccountsTableMetaData.MAccounts_ACNAME, MoneyAccountsTableMetaData.MAccounts_ACNAME);
		MAccounts_projectionMap.put(MoneyAccountsTableMetaData.MAccounts_MONEY, MoneyAccountsTableMetaData.MAccounts_MONEY);
	}
	
	// 因為一個 ContentProvider 可以處理多種 uri，例如取得一堆資料或者只取得一筆資料
	// Android 提供 UriMatcher 來簡化 uri 的識別工作
	private static final UriMatcher uriMatcher;
	private static final int MItems_DIR_URI_INDICATOR = 10;
	private static final int MItems_ITEM_URI_INDICATOR = 20;
	private static final int MAccounts_DIR_URI_INDICATOR = 30;
	private static final int MAccounts_ITEM_URI_INDICATOR = 40;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		// 當 uri 符合前兩個參數組成的語法時，回傳第三個參數
		uriMatcher.addURI(MoneySaveInfo.AUTHORITY, "money_items", MItems_DIR_URI_INDICATOR);
		uriMatcher.addURI(MoneySaveInfo.AUTHORITY, "money_items/#", MItems_ITEM_URI_INDICATOR);
		uriMatcher.addURI(MoneySaveInfo.AUTHORITY, "money_accounts", MAccounts_DIR_URI_INDICATOR);
		uriMatcher.addURI(MoneySaveInfo.AUTHORITY, "money_accounts/#", MAccounts_ITEM_URI_INDICATOR);
	}
	
	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, MoneySaveInfo.DB_NAME, null, MoneySaveInfo.DB_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			Log.d(TAG, "create db...");
			// 建立 table 的語法
			db.execSQL("create table " + MoneyItemsTableMetaData.TABLE_NAME + " ( "
							+ MoneyItemsTableMetaData._ID + " integer primary key, "
							+ MoneyItemsTableMetaData.MItems_DATE_stamp + " long not null, "
							+ MoneyItemsTableMetaData.MItems_DATE_year + " integer not null, "
							+ MoneyItemsTableMetaData.MItems_DATE_month + " integer not null, "
							+ MoneyItemsTableMetaData.MItems_DATE_day + " integer not null, "
							+ MoneyItemsTableMetaData.MItems_TYPE + " integer not null, "
							+ MoneyItemsTableMetaData.MItems_CASH + " integer not null, "
							+ MoneyItemsTableMetaData.MItems_ITAG + " integer not null, "
							+ MoneyItemsTableMetaData.MItems_ITEM + " text , "
							+ MoneyItemsTableMetaData.MItems_FROM + " text not null, "
							+ MoneyItemsTableMetaData.MItems_TO + " text "
							+ "); ");
			Log.d(TAG, "create tb1...done");
			db.execSQL("create table " + MoneyAccountsTableMetaData.TABLE_NAME + " ( "
							+ MoneyAccountsTableMetaData._ID + " integer primary key, "
							+ MoneyAccountsTableMetaData.MAccounts_ACNAME + " text not null, "
							+ MoneyAccountsTableMetaData.MAccounts_MONEY + " integer not null "
							+ "); ");
			Log.d(TAG, "create tb2...done");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.d(TAG, "modify db from version " + oldVersion + " to version " + newVersion + " ...");
			// 當 version 改變時，一般會 drop 掉舊 table，然後重新建立 table，所以要小心舊資料會被清掉
			db.execSQL("drop table if exists " + MoneyItemsTableMetaData.TABLE_NAME);
			db.execSQL("drop table if exists " + MoneyAccountsTableMetaData.TABLE_NAME);
			this.onCreate(db);
		}
	}
	
	private DBHelper dbHelper;

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onCreate");
		// 新建或重建 db
		this.dbHelper = new DBHelper(this.getContext());
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		// 這兩個一定要
//		qb.setTables(MoneyItemsTableMetaData.TABLE_NAME);
//		qb.setProjectionMap(MoneySaveContentProvider.projectionMap);
		
		// 因 uri 不同而導致查詢語法的不同
		Log.d(TAG, "query  " + uri);
		switch( uriMatcher.match(uri) ) {
			case MItems_DIR_URI_INDICATOR :
				qb.setTables(MoneyItemsTableMetaData.TABLE_NAME);
				qb.setProjectionMap(MoneySaveContentProvider.MItems_projectionMap);
				break;
			case MItems_ITEM_URI_INDICATOR :
				qb.setTables(MoneyItemsTableMetaData.TABLE_NAME);
				qb.setProjectionMap(MoneySaveContentProvider.MItems_projectionMap);
				qb.appendWhere(MoneyItemsTableMetaData._ID + " = " + uri.getPathSegments().get(1));
				break;
			case MAccounts_DIR_URI_INDICATOR :
				qb.setTables(MoneyAccountsTableMetaData.TABLE_NAME);
				qb.setProjectionMap(MoneySaveContentProvider.MAccounts_projectionMap);
				break;
			case MAccounts_ITEM_URI_INDICATOR :
				qb.setTables(MoneyAccountsTableMetaData.TABLE_NAME);
				qb.setProjectionMap(MoneySaveContentProvider.MAccounts_projectionMap);
				qb.appendWhere(MoneyAccountsTableMetaData._ID + " = " + uri.getPathSegments().get(1));
				break;
			default:
				throw new IllegalArgumentException("Unknown Uri - " + uri);
		}
		
		// 資料排序方式
		String orderBy = MoneyItemsTableMetaData.DEFAULT_SORT_ORDER;
		if (!TextUtils.isEmpty(sortOrder)) {
			orderBy = sortOrder;
		}

		// 取得 cursor
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		Cursor cs = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

		// 因為是取得資料，所以當資料有改變時，需要被通知
		cs.setNotificationUri(this.getContext().getContentResolver(), uri);
		return cs;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		int cnt;
		switch (uriMatcher.match(uri)) {
		case MItems_DIR_URI_INDICATOR :
			Log.d(TAG, "MoneyItemsTable - Delete all data...");
			// 如果 selection 是空的，可是會將整個 table 清空，這邊就是用來作為清空 table 的功能
			cnt = db.delete(MoneyItemsTableMetaData.TABLE_NAME, selection, selectionArgs);
			break;
		case MItems_ITEM_URI_INDICATOR :
			// 刪除單筆資料
			String rowId1 = uri.getPathSegments().get(1);
			Log.d(TAG, "MoneyItemsTable - Delete data [" + rowId1 + "]...");
			cnt = db.delete(MoneyItemsTableMetaData.TABLE_NAME,
							MoneyItemsTableMetaData._ID
							+ " = "
							+ rowId1
							+ (TextUtils.isEmpty(selection) ? "" : " and ("
									+ selection + ") "), selectionArgs);			
			break;
		case MAccounts_DIR_URI_INDICATOR :
			Log.d(TAG, "MoneyAccountsTable - Delete all data...");
			cnt = db.delete(MoneyAccountsTableMetaData.TABLE_NAME, selection, selectionArgs);
			break;
		case MAccounts_ITEM_URI_INDICATOR :
			// 刪除單筆資料
			String rowId2 = uri.getPathSegments().get(1);
			Log.d(TAG, "MoneyAccountsTable - Delete data [" + rowId2 + "]...");
			cnt = db.delete(MoneyAccountsTableMetaData.TABLE_NAME,
							MoneyAccountsTableMetaData._ID
							+ " = "
							+ rowId2
							+ (TextUtils.isEmpty(selection) ? "" : " and ("
									+ selection + ") "), selectionArgs);						
			break;
			
		default:
			throw new IllegalArgumentException("Unknown Uri - " + uri);
		}
		// 資料改變了，發出通知
		this.getContext().getContentResolver().notifyChange(uri, null);
		return cnt;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (uriMatcher.match(uri)) {
		case MItems_DIR_URI_INDICATOR:
			return MoneyItemsTableMetaData.CONTENT_DIR_TYPE;
		case MItems_ITEM_URI_INDICATOR:
			return MoneyItemsTableMetaData.CONTENT_ITEM_TYPE;
		case MAccounts_DIR_URI_INDICATOR :
			return MoneyAccountsTableMetaData.CONTENT_DIR_TYPE;
		case MAccounts_ITEM_URI_INDICATOR :
			return MoneyAccountsTableMetaData.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown Uri - " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		// 只能是複數的 uri
		if ( (uriMatcher.match(uri)!=MItems_DIR_URI_INDICATOR) 
				&& (uriMatcher.match(uri)!=MAccounts_DIR_URI_INDICATOR) ) {
			throw new IllegalArgumentException("Unknown Uri - " + uri);
		}
		
		ContentValues cv;
		if (values == null) {
			cv = new ContentValues();
		}
		else {
			// 包一層的原因是待會可能會修改
			cv = new ContentValues(values);
		}
		
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
		case MItems_DIR_URI_INDICATOR:
			// 檢查必要的欄位
			if (!cv.containsKey(MoneyItemsTableMetaData.MItems_DATE_stamp)) {
				throw new IllegalArgumentException("The 'date' is required!");
			}
			if (!cv.containsKey(MoneyItemsTableMetaData.MItems_CASH)) {
				throw new IllegalArgumentException("The 'cash' is required!");
			}
			long rowId1 = db.insert(MoneyItemsTableMetaData.TABLE_NAME, null, cv);
			if (rowId1 > 0) {
				Log.d(TAG, "Insert " + rowId1 + " data");
				Uri addedUri = ContentUris.withAppendedId(MoneyItemsTableMetaData.CONTENT_URI, rowId1);
				// 資料改變了，發出通知
				this.getContext().getContentResolver().notifyChange(addedUri, null);
				return addedUri;
			}
		case MAccounts_DIR_URI_INDICATOR :
			// 檢查必要的欄位
			if (!cv.containsKey(MoneyAccountsTableMetaData.MAccounts_ACNAME)) {
				throw new IllegalArgumentException("The 'account_name' is required!");
			}
			if (!cv.containsKey(MoneyAccountsTableMetaData.MAccounts_MONEY)) {
				throw new IllegalArgumentException("The 'account_money' is required!");
			}
			long rowId2 = db.insert(MoneyAccountsTableMetaData.TABLE_NAME, null, cv);
			if (rowId2 > 0) {
				Log.d(TAG, "Insert " + rowId2 + " data");
				Uri addedUri = ContentUris.withAppendedId(MoneyAccountsTableMetaData.CONTENT_URI, rowId2);
				// 資料改變了，發出通知
				this.getContext().getContentResolver().notifyChange(addedUri, null);
				return addedUri;
			}
		}
		throw new IllegalArgumentException("Failed to insert data to uri - " + uri);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		int cnt;
		switch (uriMatcher.match(uri)) {
		case MItems_DIR_URI_INDICATOR:
			// 更新多筆資料
			cnt = db.update(MoneyItemsTableMetaData.TABLE_NAME, values, selection, selectionArgs);
			break;
			
		case MItems_ITEM_URI_INDICATOR:
			// 更新單筆資料
			String rowId1 = uri.getPathSegments().get(1);
			cnt = db.update(MoneyItemsTableMetaData.TABLE_NAME,
							values,
							MoneyItemsTableMetaData._ID
							+ " = "
							+ rowId1
							+ (TextUtils.isEmpty(selection) ? "" : " and ("
									+ selection + ") "), selectionArgs);
			break;
			
		case MAccounts_DIR_URI_INDICATOR :
			// 更新多筆資料
			cnt = db.update(MoneyAccountsTableMetaData.TABLE_NAME, values, selection, selectionArgs);
			break;
			
		case MAccounts_ITEM_URI_INDICATOR :
			// 更新單筆資料
			String rowId2 = uri.getPathSegments().get(1);
			cnt = db.update(MoneyAccountsTableMetaData.TABLE_NAME,
							values,
							MoneyAccountsTableMetaData._ID
							+ " = "
							+ rowId2
							+ (TextUtils.isEmpty(selection) ? "" : " and ("
									+ selection + ") "), selectionArgs);
			break;
			
		default:
			throw new IllegalArgumentException("Unknown Uri - " + uri);
		}
		// 資料改變了，發出通知
		this.getContext().getContentResolver().notifyChange(uri, null);
		return cnt;
	}
	
}
