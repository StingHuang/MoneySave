package com.sting.moneysave;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ManageAccountActivity extends AppCompatActivity {
	
	private EditText maEdtName, maEdtCash;
	private Button maBtnAdd;
	private ListView maAccList;
	private AccountAdapter maAdapter;
	
	private List<Accounts> acc_list = new ArrayList<Accounts>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_accounts);
		setView();
		setClickedEvent();
	}
	
	private void setView(){
		maEdtName = (EditText)findViewById(R.id.edtAccName);
		maEdtCash = (EditText)findViewById(R.id.edtAccCash);
		maBtnAdd = (Button)findViewById(R.id.btn_acc_add);
		maAccList = (ListView)findViewById(R.id.account_list);
		acc_list = AccountsHelper.listAccounts(getContentResolver());
		maAdapter = new AccountAdapter(this, acc_list);
		maAccList.setAdapter(maAdapter);
	}
	
	private void setClickedEvent(){
		maBtnAdd.setOnClickListener(btnclicked);
		maAccList.setOnItemLongClickListener(editAccount);
	}
	
	private View.OnClickListener btnclicked = new View.OnClickListener() {
		
		String aName, aCashS;
		int aCash;
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			aName = maEdtName.getText().toString();
			aCashS = maEdtCash.getText().toString();
			if( !aName.matches("")&&!aCashS.matches("") ){
				aCash = Integer.parseInt(maEdtCash.getText().toString());
				Accounts acc = new Accounts();
				acc.setAccountName(aName);
				acc.setAccountMoney(aCash);
				AccountsHelper.addAccounts(getContentResolver(), acc);				
				acc_list = AccountsHelper.listAccounts(getContentResolver());
				maAdapter = new AccountAdapter(ManageAccountActivity.this, acc_list);
				maAccList.setAdapter(maAdapter);
				maAdapter.notifyDataSetChanged();
				resetForm();
			}else{
				if(aName.matches(""))
					Toast.makeText(ManageAccountActivity.this, "Account Name is required", Toast.LENGTH_LONG).show();
				if(aCashS.matches(""))
					Toast.makeText(ManageAccountActivity.this, "Cash is required", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	private AdapterView.OnItemLongClickListener editAccount = new AdapterView.OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			Accounts acc = maAdapter.getItem(position);
			showAccountDialog(acc, position);
			return true;
		}
	};
	
	private void showAccountDialog(Accounts acc, final int position){
		final int aId = acc.getId();   
		AlertDialog.Builder accDialog = new AlertDialog.Builder(ManageAccountActivity.this);
		LayoutInflater inflater = getLayoutInflater();
		final View accDlg = inflater.inflate(R.layout.dialog_account_edit, null);
		accDialog.setView(accDlg);
		accDialog.setTitle(R.string.ma_dlg_title);
		final EditText edtDlgName = (EditText)accDlg.findViewById(R.id.dlg_ma_name);
		edtDlgName.setText(acc.getAccountName());
		final EditText edtDlgCash = (EditText)accDlg.findViewById(R.id.dlg_ma_cash);
		edtDlgCash.setText(Integer.toString(acc.getAccountMoney()));
		
		accDialog.setPositiveButton(R.string.ma_dlg_btn_ok, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Accounts upacc = new Accounts();
				upacc.setId(aId);
				upacc.setAccountName(edtDlgName.getText().toString());
				upacc.setAccountMoney(Integer.parseInt(edtDlgCash.getText().toString()));
				AccountsHelper.updateAccounts(getContentResolver(), upacc);
				acc_list = AccountsHelper.listAccounts(getContentResolver());
				maAdapter = new AccountAdapter(ManageAccountActivity.this, acc_list);
				maAccList.setAdapter(maAdapter);
			}
		});
		accDialog.setNegativeButton(R.string.ma_dlg_btn_delete, new DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				AccountsHelper.deleteAccounts(getContentResolver(), aId);
				acc_list = AccountsHelper.listAccounts(getContentResolver());
				maAdapter = new AccountAdapter(ManageAccountActivity.this, acc_list);
				maAccList.setAdapter(maAdapter);
			}
		});
		accDialog.show();
	}
	
	private void resetForm() {
		maEdtName = (EditText)findViewById(R.id.edtAccName);
		maEdtCash = (EditText)findViewById(R.id.edtAccCash);
		maEdtName.setText("");
		maEdtCash.setText("");
    }
}
