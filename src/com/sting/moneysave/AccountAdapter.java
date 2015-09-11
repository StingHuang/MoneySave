package com.sting.moneysave;

import java.util.List;

import com.sting.moneysave.R.color;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AccountAdapter extends BaseAdapter {

	private LayoutInflater myInflater;
	private List<Accounts> accounts;
	
	public AccountAdapter(Context context,List<Accounts> accounts){
		myInflater = LayoutInflater.from(context);
        this.accounts = accounts;
	}
	
	 private class ViewHolder {
	        TextView txtAName, txtACash;
	        public ViewHolder(TextView txtAName, TextView txtACash){
	                this.txtAName = txtAName;
	                this.txtACash = txtACash;
	        }
	    }
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return accounts.size();
	}

	@Override
	public Accounts getItem(int position) {
		// TODO Auto-generated method stub
		return accounts.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return accounts.indexOf(getItem(position));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView==null){
		        convertView = myInflater.inflate(R.layout.one_account_adapter, null);
		        holder = new ViewHolder(
		                (TextView) convertView.findViewById(R.id.adp_acc_name),
		                (TextView) convertView.findViewById(R.id.adp_acc_cash)
		                );
		        convertView.setTag(holder);
		}else{
		        holder = (ViewHolder) convertView.getTag();
		}
		
		Accounts acc = (Accounts)getItem(position);
		holder.txtAName.setText(acc.getAccountName());
		if( acc.getAccountMoney() < 0 ){
			holder.txtACash.setTextColor(convertView.getResources().getColor(color.red));
		}else{
			holder.txtACash.setTextColor(convertView.getResources().getColor(color.teal));
		}
		holder.txtACash.setText(StingFunction.toDecimalFormat(acc.getAccountMoney()));
		
		return convertView;
	}

}
