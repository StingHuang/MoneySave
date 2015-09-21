package com.sting.moneysave;

import java.text.ParseException;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class AddNewItemActivity extends AppCompatActivity {

	private final int iTypeOUT = 1, iTypeIN = 2, iTypeEXC = 3;
	private final int iTagNO = 0;
	
	private LinearLayout mLView_tag, mLView_item, mLView_to, mLView_from;
	private EditText mEdtDate, mEdtCash, mEdtItem;
	private Button mBtnOK;
	private Spinner mSpnType, mSpnTag, mSpnFrom, mSpnTo;
	private ArrayAdapter<String> mCFromList, mCToList;
	
	private String[] accounts;
	private long mDStamp;
	private int mDYear, mDMonth, mDDay, mType, mCash, mITag;
	private String mItem, mFrom, mTo; 
	private Calendar mCalendar;
	private Items newItem;
	private final String KEY_IsADD = "isADD";
	
	private Thread exeDataBase;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mCalendar = Calendar.getInstance();
		newItem = new Items();
		setView();
        	setSpinList();
        	setClickedEvent();
	}

	private void setView(){
    		mEdtDate = (EditText)findViewById(R.id.edt_date);
    		mEdtDate.setText(StingFunction.toDateString(mCalendar.getTimeInMillis()));
    		mEdtCash = (EditText)findViewById(R.id.edt_cash);
    		mEdtItem = (EditText)findViewById(R.id.edt_item);
    		mSpnType = (Spinner)findViewById(R.id.spn_type);
    		mSpnTag = (Spinner)findViewById(R.id.spn_tag);
    		mSpnFrom = (Spinner)findViewById(R.id.spn_cashFROM);
    		mSpnTo = (Spinner)findViewById(R.id.spn_cashTO);
    		mBtnOK = (Button)findViewById(R.id.btn_ok);
    		mBtnOK.setVisibility(View.VISIBLE);
    		mLView_tag = (LinearLayout)findViewById(R.id.view_tag);
    		mLView_item = (LinearLayout)findViewById(R.id.view_item);
    		mLView_from = (LinearLayout)findViewById(R.id.view_cash_from);
    		mLView_to = (LinearLayout)findViewById(R.id.view_cash_to);
	}
	
	private void setSpinList(){
		accounts = (String[]) AccountsHelper.getAllAccName(getContentResolver()).toArray(new String[0]);
    		mCFromList = new ArrayAdapter<String>(AddNewItemActivity.this, R.layout.my_simple_spinner, accounts);
    		mSpnFrom.setAdapter(mCFromList);
    		mCToList = new ArrayAdapter<String>(AddNewItemActivity.this, R.layout.my_simple_spinner, accounts);
    		mSpnTo.setAdapter(mCToList);
	}
	
	private void setClickedEvent(){
		mEdtDate.setOnClickListener(edt_clicked);
    		mSpnType.setOnItemSelectedListener(spn_ItemSelected);
    		mBtnOK.setOnClickListener(btn_clicked);
    	}
	
	class exeDataBase extends Thread{
		public void run() {
			super.run();
			updateAccount(mType ,mCash, mFrom, mTo);
		}
	}
	
	private void updateAccount(int type, int money, String money_from, String money_to){
		Accounts acc_a ;
		int money_a ;
		switch(type){
		case iTypeOUT:	//支出
			acc_a = AccountsHelper.getAccountsByName(getContentResolver(), money_from);
			money_a = acc_a.getAccountMoney();
			acc_a.setAccountMoney(money_a - money);
			AccountsHelper.updateAccounts(getContentResolver(), acc_a);
			break;
			
		case iTypeIN:	//收入
			acc_a = AccountsHelper.getAccountsByName(getContentResolver(), money_to);
			money_a = acc_a.getAccountMoney();
			acc_a.setAccountMoney(money_a + money);
			AccountsHelper.updateAccounts(getContentResolver(), acc_a);
			break;
			
		case iTypeEXC:	//轉移
			acc_a = AccountsHelper.getAccountsByName(getContentResolver(), money_from);
			money_a = acc_a.getAccountMoney();
			acc_a.setAccountMoney(money_a - money);
			AccountsHelper.updateAccounts(getContentResolver(), acc_a);
			Accounts acc_b = AccountsHelper.getAccountsByName(getContentResolver(), money_to);
			int money_b = acc_b.getAccountMoney() + money;
			acc_b.setAccountMoney(money_b);
			AccountsHelper.updateAccounts(getContentResolver(), acc_b);
			break;
		}
	}
	
	private View.OnClickListener btn_clicked = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		if(v.getId()==R.id.btn_ok)
		{
			try {
				mDStamp = StingFunction.toDateStamp(mEdtDate.getText().toString());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] d = mEdtDate.getText().toString().split("/");	// yyyy/MM/dd
			mDYear = Integer.parseInt(d[0]);
			mDMonth = Integer.parseInt(d[1]);
			mDDay = Integer.parseInt(d[2]);
			mCash = Integer.parseInt(mEdtCash.getText().toString());
			
			switch(mType){
			case iTypeOUT:
				mITag = mSpnTag.getSelectedItemPosition()+1;
				mItem = mEdtItem.getText().toString();
				mFrom = (String)mSpnFrom.getSelectedItem();
				mTo = "";
				break;
			case iTypeIN:
				mITag = iTagNO;
				mItem = mEdtItem.getText().toString();
				mFrom = "";
				mTo = (String)mSpnTo.getSelectedItem();
				break;
			case iTypeEXC:
				mITag = iTagNO;
				mFrom = (String)mSpnFrom.getSelectedItem();
				mTo = (String)mSpnTo.getSelectedItem();
				mItem = mFrom + " > " + mTo;
				break;
			}
			
			newItem.setDateStamp(mDStamp);
			newItem.setDateYear(mDYear);
			newItem.setDateMonth(mDMonth);
			newItem.setDateDay(mDDay);
			newItem.setType(mType);
			newItem.setCash(mCash);
			newItem.setItemTag(mITag);
			newItem.setItem(mItem);
			newItem.setFrom(mFrom);
			newItem.setTo(mTo);
			ItemsHelper.addItems(getContentResolver(), newItem);
			
			exeDataBase = new exeDataBase();
			exeDataBase.start();			
			
			Intent intent = getIntent();
			Bundle bundle = new Bundle();
			bundle.putBoolean(KEY_IsADD,true);  
			intent.putExtras(bundle); 
			setResult(Activity.RESULT_OK, intent); 
			AddNewItemActivity.this.finish();
		}
	}};
	
	private View.OnClickListener edt_clicked = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.edt_date:
				DatePickerDialog dpd = new DatePickerDialog(
						AddNewItemActivity.this, 
						datePickerDlgOnDateSet, 
						mCalendar.get(Calendar.YEAR), 
						mCalendar.get(Calendar.MONTH), 
						mCalendar.get(Calendar.DAY_OF_MONTH) );
		    	  	dpd.setTitle(R.string.dlg_datepick);
		    	  	dpd.setMessage("");
		    	  	dpd.setCancelable(false);
		    	  	dpd.show(); 
				break;
			}
		}
	};
	private DatePickerDialog.OnDateSetListener datePickerDlgOnDateSet = new DatePickerDialog.OnDateSetListener(){

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			// TODO Auto-generated method stub
			mCalendar.set(Calendar.YEAR, year);
			mCalendar.set(Calendar.MONTH, monthOfYear);
			mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			String tDate = StingFunction.toDateString(mCalendar.getTimeInMillis());
			mEdtDate.setText(tDate);
			try {
				mDStamp = StingFunction.toDateStamp(tDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mDYear = year;
			mDMonth = monthOfYear+1;
			mDDay = dayOfMonth;
		}	
    	};

    private AdapterView.OnItemSelectedListener spn_ItemSelected = 
    		new AdapterView.OnItemSelectedListener(){

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			int pid = parent.getId();
			switch(pid){
			case R.id.spn_type:
				if( parent.getSelectedItemPosition()==(iTypeEXC-1) ){	//轉移2
					mLView_tag.setVisibility(View.GONE);
					mLView_item.setVisibility(View.GONE);
					mLView_from.setVisibility(View.VISIBLE);
					mLView_to.setVisibility(View.VISIBLE);
					mType = iTypeEXC;
				}else if(parent.getSelectedItemPosition()==(iTypeOUT-1)){	//支出0
					mLView_tag.setVisibility(View.VISIBLE);
					mLView_from.setVisibility(View.VISIBLE);
					mLView_to.setVisibility(View.INVISIBLE);
					mType = iTypeOUT;
				}else{											//收入1
					mLView_tag.setVisibility(View.GONE);
					mLView_from.setVisibility(View.GONE);
					mLView_to.setVisibility(View.VISIBLE);
					mType = iTypeIN;
				}
				break;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	};
}
