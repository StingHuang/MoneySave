package com.sting.moneysave;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Items implements Serializable {

	private int id;
	private long date_stamp;
	private int date_year, date_month, date_day, type, cash, item_tag;
    private String item, from, to;    
    
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public long getDateStamp() {
        return this.date_stamp;
    }
    public void setDateStamp(long date_stamp) {
        this.date_stamp = date_stamp;
    }
    
    public int getDateYear() {
        return this.date_year;
    }
    public void setDateYear(int date_year) {
        this.date_year = date_year;
    }
    
    public int getDateMonth() {
        return this.date_month;
    }
    public void setDateMonth(int date_month) {
        this.date_month = date_month;
    }
    
    public int getDateDay() {
        return this.date_day;
    }
    public void setDateDay(int date_day) {
        this.date_day = date_day;
    }
    
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    
    public int getCash() {
        return this.cash;
    }
    public void setCash(int cash) {
        this.cash = cash;
    }
    
    public int getItemTag() {
        return this.item_tag;
    }
    public void setItemTag(int item_tag) {
        this.item_tag = item_tag;
    }
    
    public String getItem() {
        return this.item;
    }
    public void setItem(String item) {
    	if(item.matches(""))	item = "--";
        this.item = item;
    }
    
    public String getFrom() {
        return this.from;
    }
    public void setFrom(String from) {
    	if(from.matches(""))	from = "--";
        this.from = from;
    }
    
    public String getTo() {
        return this.to;
    }
    public void setTo(String to) {
    	if(to.matches(""))	to = "--";
        this.to = to;
    }
	
    @Override
    public String toString() {
        return "MoneyItem [ id=" + this.id 
        			+ ", date_stamp=" + this.date_stamp
        			+ ", date_year=" + this.date_year
        			+ ", date_month=" + this.date_month
        			+ ", date_day=" + this.date_day
        			+ ", type=" + this.type
        			+ ", cash=" + this.cash
        			+ ", item_tag=" + this.item_tag
        			+ ", item=" + this.item
        			+ ", from=" + this.from
        			+ ", to=" + this.to
        		 + " ]";
    }
    
    public String writeToFile(){
    	return "" + this.id 
    			+ "," + this.date_stamp
    			+ "," + this.date_year
    			+ "," + this.date_month
    			+ "," + this.date_day
    			+ "," + this.type
    			+ "," + this.cash
    			+ "," + this.item_tag
    			+ "," + this.item
    			+ "," + this.from
    			+ "," + this.to + "," ;
    }    
}
