package com.sting.moneysave.providers;

import android.net.Uri;
import android.provider.BaseColumns;

public class MoneySaveInfo {

	/** content provider 的 unique key，會定義在 manifest 裡，也是對外開放的 uri */
    public static final String AUTHORITY = "com.sting.moneysave.providers.MoneySaveContentProvider";
    /** 用 db 實做 content provider */
    public static final String DB_NAME = "moneysave.db";
    /** 只要版本一改，舊資料會被清空 */
    public static final int DB_VERSION = 3;
    
    private MoneySaveInfo(){};
    
    
    /** 定義 items 表格的所有資訊，一個 content provider 可以有多個 table */
    public static final class MoneyItemsTableMetaData implements BaseColumns{
    	
    	private MoneyItemsTableMetaData(){};
    	
    	 public static final String TABLE_NAME = "money_items";
    	 /** money_items 專用的 uri */
         public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
         
         /** 複數與單數的 MIME 型別*/
         public static final String CONTENT_DIR_TYPE = 
        		 					"vnd.android.cursor.dir/vnd.com.sting.moneysave."+TABLE_NAME;
         public static final String CONTENT_ITEM_TYPE = 
        		 					"vnd.android.cursor.item/vnd.com.sting.moneysave."+TABLE_NAME;
         
         /** 預設的排序 (asc/desc) */
         public static final String DEFAULT_SORT_ORDER = BaseColumns._ID + " desc";	
         
         /** 定義所有欄位 */
         // 不用定義 _id，因為 BaseColumns 已經定義了
         public static final String MItems_DATE_stamp = "date_stamp";
         public static final String MItems_DATE_year = "date_year";
         public static final String MItems_DATE_month = "date_month";
         public static final String MItems_DATE_day = "date_day";
         public static final String MItems_TYPE = "type";
         public static final String MItems_CASH = "cash";
         public static final String MItems_ITAG = "item_tag";
         public static final String MItems_ITEM = "item";
         public static final String MItems_FROM = "cash_from";
         public static final String MItems_TO = "cash_to"; 
    }
    
    public static final class MoneyAccountsTableMetaData implements BaseColumns{
    	
    	private MoneyAccountsTableMetaData(){};
    	
    	public static final String TABLE_NAME = "money_accounts";
    	 /** money_items 專用的 uri */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
         
         /** 複數與單數的 MIME 型別*/
        public static final String CONTENT_DIR_TYPE = 
        		 					"vnd.android.cursor.dir/vnd.com.sting.moneysave."+TABLE_NAME;
        public static final String CONTENT_ITEM_TYPE = 
        		 					"vnd.android.cursor.item/vnd.com.sting.moneysave."+TABLE_NAME;
         
         /** 預設的排序 (asc/desc) */
        public static final String DEFAULT_SORT_ORDER = BaseColumns._ID + " asc";	
         
         /** 定義所有欄位 */
         // 不用定義 _id，因為 BaseColumns 已經定義了
        public static final String MAccounts_ACNAME = "account_name";
        public static final String MAccounts_MONEY = "account_cash";
    }
}