package com.sting.moneysave;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.sting.moneysave.R.color;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
	private final int iTypeOUT = 1, iTypeIN = 2, iTypeEXC = 3;
	private final int iTagNO = 0;
	private final String OutString = "支出", InString = "收入", ExcString = "轉移"; 
	private final int dlgBtnUpdate = 1, dlgBtnDelete = 2;
	private String mainPref = "moneySavePref";
	private SharedPreferences sharedPref;
	private SharedPreferences.Editor editor;
	private String KEY_isFirstIn = "FIRST";
	
	private Spinner mSpnYear, mSpnMonth;
	private Button mBtnList, mBtnAnalysis, mBtnAdd;
	private ItemAdapter2 itemadapter;
	private ListView mListView;
	private List<Items> money_items = new ArrayList<Items>();
	private String[] accounts;
	private ArrayAdapter<String> idCFromList, idCToList;
	
	private String[] years;
	private Calendar mCalendar;
	private int mYear, mMonth;
	private int sYear, sMonth;
	private final String initAccName = "現金";
	private final int initAccMoney = 0;
	private final int addNew_requestCode = 200;
	private final String KEY_IsADD = "isADD";
	
	private Handler mUI_handler = new Handler();
	private int dlg_MODE, dlg_ItemId;
	private Items dlg_ItemOld, dlg_ItemNew;
	private Thread exeUpdateDataBase;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
  	  	mMonth = mCalendar.get(Calendar.MONTH); 	  	
        setView();
        setClickedEvent();
    }
    
    private void setView(){
    	mSpnYear = (Spinner)findViewById(R.id.spn_year);
    	setSpinList();
    	mSpnYear.setSelection(mSpnYear.getCount()-1);
    	mSpnMonth = (Spinner)findViewById(R.id.spn_month);
    	mSpnMonth.setSelection(mMonth);
    	mBtnList = (Button)findViewById(R.id.btn_list);
    	mBtnAnalysis = (Button)findViewById(R.id.btn_analysis);
    	mBtnAdd = (Button)findViewById(R.id.btn_add);
    	
    	sharedPref = getSharedPreferences(mainPref, 0);
  	  	editor = sharedPref.edit();
  	  	if(sharedPref.getBoolean(KEY_isFirstIn, true)){
  	  		Accounts def_a = new Accounts();
	  		def_a.setAccountName(initAccName);
	  		def_a.setAccountMoney(initAccMoney);
	  		AccountsHelper.addAccounts(getContentResolver(), def_a);
	  		editor.putBoolean(KEY_isFirstIn, false).commit();
  	  	}
  	  	mListView = (ListView)findViewById(R.id.mainListView);
  	  	setListView(mYear, mMonth+1);
    }
    
    private void setListView(int yy, int mm){
    	Log.d(TAG, "setListView...... "+yy+"/"+mm);
    	money_items = ItemsHelper.listItems(MainActivity.this, Integer.toString(mm), yy);
  	  	itemadapter = new ItemAdapter2(this, money_items);
  	  	mListView.setAdapter(itemadapter);
    }
    
    private Runnable updateListView = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			sYear = Integer.parseInt(mSpnYear.getSelectedItem().toString());
			sMonth = Integer.parseInt(mSpnMonth.getSelectedItem().toString());
			setListView(sYear, sMonth);
		}
	};
    
    private void setSpinList(){
    	years = ItemsHelper.getAllYear(getContentResolver());
    	mSpnYear.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.my_simple_spinner, years));
    }
    
    private void setClickedEvent(){
    	mBtnAdd.setOnClickListener(btn_clicked);
    	mBtnList.setOnClickListener(btn_clicked);
    	mBtnAnalysis.setOnClickListener(btn_clicked);
    	mListView.setOnItemLongClickListener(showItemDialog);
    }
    
    private AdapterView.OnItemLongClickListener showItemDialog = new AdapterView.OnItemLongClickListener(){

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			Items item = itemadapter.getItem(position);
			showItemDialog(item, position);
			return true;
		}
    	
    };
    
    private void showItemDialog(Items item, final int adpPos){
    	dlg_ItemId = item.getId(); 
    	dlg_ItemOld = item;
    	dlg_ItemNew = new Items();
    	final int iType = item.getType();
    	AlertDialog.Builder itemDialog = new AlertDialog.Builder(MainActivity.this);
    	LayoutInflater inflater = getLayoutInflater();
    	itemDialog.setTitle(R.string.idlg_title);
    	final View itemDlg = inflater.inflate(R.layout.activity_add_new, null);
    	itemDialog.setView(itemDlg);
    	
    	final TextView idTxtType = (TextView)itemDlg.findViewById(R.id.txt_idlg_type);
    	idTxtType.setVisibility(View.VISIBLE);
    	final EditText idEdtDate = (EditText)itemDlg.findViewById(R.id.edt_date);
    	idEdtDate.setText(StingFunction.toDateString(item.getDateStamp()));
    	final LinearLayout idLView_type = (LinearLayout)itemDlg.findViewById(R.id.view_type);
    	idLView_type.setVisibility(View.GONE);
    	final EditText idEdtCash = (EditText)itemDlg.findViewById(R.id.edt_cash);
    	idEdtCash.setText(Integer.toString(item.getCash()));
    	
    	final LinearLayout idLView_tag = (LinearLayout)itemDlg.findViewById(R.id.view_tag);
    	final Spinner idSpnTag = (Spinner)itemDlg.findViewById(R.id.spn_tag);
    	final LinearLayout idLView_item = (LinearLayout)itemDlg.findViewById(R.id.view_item);
    	final EditText idEdtItem = (EditText)itemDlg.findViewById(R.id.edt_item);
    	final LinearLayout idLView_from = (LinearLayout)itemDlg.findViewById(R.id.view_cash_from);
    	final Spinner idSpnFrom = (Spinner)itemDlg.findViewById(R.id.spn_cashFROM);
    	final LinearLayout idLView_to = (LinearLayout)itemDlg.findViewById(R.id.view_cash_to);
    	final Spinner idSpnTo = (Spinner)itemDlg.findViewById(R.id.spn_cashTO);
    	ArrayList<String> alAccName = AccountsHelper.getAllAccName(getContentResolver());
    	accounts = (String[]) alAccName.toArray(new String[0]);
    	idCFromList = new ArrayAdapter<String>(MainActivity.this, R.layout.my_simple_spinner, accounts);
    	idSpnFrom.setAdapter(idCFromList);
    	idCToList = new ArrayAdapter<String>(MainActivity.this, R.layout.my_simple_spinner, accounts);
    	idSpnTo.setAdapter(idCToList);
    	
    	switch(iType){
    	case iTypeOUT:
    		idTxtType.setTextColor(getResources().getColor(color.darkslategray));
    		idTxtType.setText(OutString);
    		idLView_tag.setVisibility(View.VISIBLE);
    		idSpnTag.setSelection(item.getItemTag()-1);
    		idEdtItem.setText(item.getItem());
			idLView_from.setVisibility(View.VISIBLE);
			idSpnFrom.setSelection(alAccName.indexOf(item.getFrom()));
			idLView_to.setVisibility(View.GONE);
    		break;
    	case iTypeIN:
    		idTxtType.setTextColor(getResources().getColor(color.limegreen));
    		idTxtType.setText(InString);
    		idLView_tag.setVisibility(View.GONE);
    		idEdtItem.setText(item.getItem());
			idLView_from.setVisibility(View.GONE);
			idLView_to.setVisibility(View.VISIBLE);
		    idSpnTo.setSelection(alAccName.indexOf(item.getTo()));
    		break;
    	case iTypeEXC:
    		idTxtType.setTextColor(getResources().getColor(color.cadetblue));
    		idTxtType.setText(ExcString);
    		idLView_tag.setVisibility(View.GONE);
			idLView_item.setVisibility(View.GONE);
			idLView_from.setVisibility(View.VISIBLE);
			idSpnFrom.setSelection(alAccName.indexOf(item.getFrom()));
			idLView_to.setVisibility(View.VISIBLE);
			idSpnTo.setSelection(alAccName.indexOf(item.getTo()));
    		break;
    	}
    	
    	itemDialog.setPositiveButton(R.string.idlg_btn_update, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dlg_MODE = dlgBtnUpdate;
				try {
					dlg_ItemNew.setId(dlg_ItemId);
					dlg_ItemNew.setDateStamp(StingFunction.toDateStamp(idEdtDate.getText().toString()));
					String[] d = idEdtDate.getText().toString().split("/");
					dlg_ItemNew.setDateYear(Integer.parseInt(d[0]));
					dlg_ItemNew.setDateMonth(Integer.parseInt(d[1]));
					dlg_ItemNew.setDateDay(Integer.parseInt(d[2]));
					dlg_ItemNew.setType(iType);
					dlg_ItemNew.setCash(Integer.parseInt(idEdtCash.getText().toString()));
					
					int idITag = 0;
					String idItem="", idFrom="", idTo="";
					switch(iType){
					case iTypeOUT:
						idITag = idSpnTag.getSelectedItemPosition()+1;
						idItem = idEdtItem.getText().toString();
						idFrom = (String)idSpnFrom.getSelectedItem();
						idTo = "";
						break;
					case iTypeIN:
						idITag = iTagNO;
						idItem = idEdtItem.getText().toString();
						idFrom = "";
						idTo = (String)idSpnTo.getSelectedItem();
						break;
					case iTypeEXC:
						idITag = iTagNO;
						idFrom = (String)idSpnFrom.getSelectedItem();
						idTo = (String)idSpnTo.getSelectedItem();
						idItem = idFrom + " > " + idTo;
						break;
					}
					dlg_ItemNew.setItemTag(idITag);
					dlg_ItemNew.setItem(idItem);
					dlg_ItemNew.setFrom(idFrom);
					dlg_ItemNew.setTo(idTo);
					
					exeUpdateDataBase = new exeUpdateDataBase();
					exeUpdateDataBase.start();
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    	
    	itemDialog.setNegativeButton(R.string.idlg_btn_delete, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dlg_MODE = dlgBtnDelete;
				exeUpdateDataBase = new exeUpdateDataBase();
				exeUpdateDataBase.start();
			}
		});
    	itemDialog.show();
    }
    
    class exeUpdateDataBase extends Thread{
		public void run() {
			super.run();
			mUI_handler.post(updateListView);
			updateDataBase(dlg_MODE, dlg_ItemId, dlg_ItemOld, dlg_ItemNew);
		}
	}
    
    private void updateDataBase(int mode, int iId, Items itemOld, Items itemNew){
    	Accounts acc_a, acc_b;
    	if(mode==dlgBtnUpdate){
    		switch(itemNew.getType()){
    		case iTypeOUT:
    			ItemsHelper.updateItems(getContentResolver(), itemNew);
    			acc_a = AccountsHelper.getAccountsByName(getContentResolver(), itemOld.getFrom());
    			acc_a.setAccountMoney(acc_a.getAccountMoney() + itemOld.getCash());
    			AccountsHelper.updateAccounts(getContentResolver(), acc_a);
    			acc_b = AccountsHelper.getAccountsByName(getContentResolver(), itemNew.getFrom());
    			acc_b.setAccountMoney(acc_b.getAccountMoney() - itemNew.getCash());
    			AccountsHelper.updateAccounts(getContentResolver(), acc_b);
    			break;
    		case iTypeIN:
    			ItemsHelper.updateItems(getContentResolver(), itemNew);
    			acc_a = AccountsHelper.getAccountsByName(getContentResolver(), itemOld.getTo());
    			acc_a.setAccountMoney(acc_a.getAccountMoney() - itemOld.getCash());
    			AccountsHelper.updateAccounts(getContentResolver(), acc_a);
    			acc_b = AccountsHelper.getAccountsByName(getContentResolver(), itemNew.getTo());
    			acc_b.setAccountMoney(acc_b.getAccountMoney() + itemNew.getCash());
    			AccountsHelper.updateAccounts(getContentResolver(), acc_b);
    			break;
    		case iTypeEXC:
    			ItemsHelper.updateItems(getContentResolver(), itemNew);
    			acc_a = AccountsHelper.getAccountsByName(getContentResolver(), itemOld.getFrom());
    			acc_a.setAccountMoney(acc_a.getAccountMoney() + itemOld.getCash());
    			AccountsHelper.updateAccounts(getContentResolver(), acc_a);
    			acc_b = AccountsHelper.getAccountsByName(getContentResolver(), itemOld.getTo());
    			acc_b.setAccountMoney(acc_b.getAccountMoney() - itemOld.getCash());
    			AccountsHelper.updateAccounts(getContentResolver(), acc_b);
    			
    			acc_a = AccountsHelper.getAccountsByName(getContentResolver(), itemNew.getFrom());
    			acc_a.setAccountMoney(acc_a.getAccountMoney() - itemNew.getCash());
    			AccountsHelper.updateAccounts(getContentResolver(), acc_a);
    			acc_b = AccountsHelper.getAccountsByName(getContentResolver(), itemNew.getTo());
    			acc_b.setAccountMoney(acc_b.getAccountMoney() + itemNew.getCash());
    			AccountsHelper.updateAccounts(getContentResolver(), acc_b);
    			break;
    		}
    	}else if(mode==dlgBtnDelete){
    		switch(itemOld.getType()){
    		case iTypeOUT:
    			ItemsHelper.deleteItems(getContentResolver(), iId);
    			acc_a = AccountsHelper.getAccountsByName(getContentResolver(), itemOld.getFrom());
    			acc_a.setAccountMoney(acc_a.getAccountMoney() + itemOld.getCash());
    			AccountsHelper.updateAccounts(getContentResolver(), acc_a);
    			break;
    		case iTypeIN:
    			ItemsHelper.deleteItems(getContentResolver(), iId);
    			acc_b = AccountsHelper.getAccountsByName(getContentResolver(), itemOld.getTo());
    			acc_b.setAccountMoney(acc_b.getAccountMoney() - itemOld.getCash());
    			AccountsHelper.updateAccounts(getContentResolver(), acc_b);
    			break;
    		case iTypeEXC:
    			ItemsHelper.deleteItems(getContentResolver(), iId);
    			acc_a = AccountsHelper.getAccountsByName(getContentResolver(), itemOld.getFrom());
    			acc_b = AccountsHelper.getAccountsByName(getContentResolver(), itemOld.getTo());
    			acc_a.setAccountMoney(acc_a.getAccountMoney() + itemOld.getCash());
    			AccountsHelper.updateAccounts(getContentResolver(), acc_a);
    			acc_b.setAccountMoney(acc_b.getAccountMoney() - itemOld.getCash());
    			AccountsHelper.updateAccounts(getContentResolver(), acc_b);
    			break;
    		}
    	}
    }

    private View.OnClickListener btn_clicked = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.btn_list:
				sYear = Integer.parseInt(mSpnYear.getSelectedItem().toString());
				sMonth = Integer.parseInt(mSpnMonth.getSelectedItem().toString());
				setListView(sYear, sMonth);
				break;
			case R.id.btn_add:
				Intent iadd = new Intent(MainActivity.this, AddNewItemActivity.class);
				startActivityForResult(iadd,addNew_requestCode);
				break;
			case R.id.btn_analysis:
				sYear = Integer.parseInt(mSpnYear.getSelectedItem().toString());
				sMonth = Integer.parseInt(mSpnMonth.getSelectedItem().toString());
				Intent intent_analysis = new Intent(MainActivity.this, AnalysisActivity.class);
				Bundle bundle_analysis = new Bundle();
				bundle_analysis.putIntArray("MONTH_YEAR", new int[]{sMonth, sYear});
				intent_analysis.putExtras(bundle_analysis);
				startActivity(intent_analysis);
				break;
			}
			
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult......");
		Bundle bundle = data.getExtras();
		if (requestCode == addNew_requestCode && resultCode == Activity.RESULT_OK) {
			if(bundle.getBoolean(KEY_IsADD)){
				setSpinList();
		  	  	mSpnYear.setSelection(mSpnYear.getCount()-1);
		  	  	mSpnMonth.setSelection(mMonth);
		  	  	setListView(mYear, mMonth+1);
			}
		}
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	switch(item.getItemId()){
        case R.id.action_settings:
        	Intent sit = new Intent();
        	sit.setClass(MainActivity.this, MoneyPreference.class);
        	startActivity(sit);
        	break;
        case R.id.action_about:
        	new AlertDialog.Builder(MainActivity.this)
        		.setTitle(R.string.aboutDlg_title)
        		.setMessage(R.string.aboutDlg_message)
        		.setCancelable(false)
        		.setPositiveButton(R.string.aboutDlg_btn, 
        			new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					})
				.show();
        	break;
        case R.id.action_manage_account:
        	Intent mit = new Intent(MainActivity.this, ManageAccountActivity.class);
        	startActivity(mit);
        	break;
        }
        return super.onOptionsItemSelected(item);
    }
}
