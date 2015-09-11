package com.sting.moneysave;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Accounts implements Serializable {
	
	private int id;
	private String account_name;
	private int account_money;
	
	public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public String getAccountName() {
        return this.account_name;
    }
    public void setAccountName(String account_name) {
        this.account_name = account_name;
    }

    public int getAccountMoney() {
        return this.account_money;
    }
    public void setAccountMoney(int account_money) {
        this.account_money = account_money;
    }
    
    @Override
    public String toString() {
        return "MoneyAccount [id=" + this.id 
        			+ ", account_name=" + this.account_name
        			+ ", account_cash=" + this.account_money
        		 + "]";
    }
    
    public String writeToFile(){
    	return "" + this.id
    			+ "," + this.account_name
    			+ "," + this.account_money + "," ;
    }
}
