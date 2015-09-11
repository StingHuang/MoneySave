package com.sting.moneysave;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class StingFunction {
	
	public static long toDateStamp(String date) throws ParseException{
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);
    	Date date1 = formatter.parse(date);
    	long dateInLong = date1.getTime();
    	return dateInLong;
    }
	
	public static String toDateString(long dstamp){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);
		String datestring = formatter.format(dstamp);
		return datestring;
	}
	
	public static String toDecimalFormat(int money){
		DecimalFormat fmt = new DecimalFormat("##,###,###,###,##0"); 
		String outStr = fmt.format(money);
		return outStr;
	}

}
