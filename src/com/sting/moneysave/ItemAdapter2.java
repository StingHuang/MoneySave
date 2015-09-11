package com.sting.moneysave;

import java.util.List;

import com.sting.moneysave.R.color;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItemAdapter2 extends BaseAdapter {

	private final int iTypeOUT = 1, iTypeIN = 2, iTypeEXC = 3;
	private LayoutInflater myInflater;
    private List<Items> items;
    
    public ItemAdapter2(Context context,List<Items> item){
        myInflater = LayoutInflater.from(context);
        this.items = item;
    }
    
    private class ViewHolder {
        TextView txtDate, txtItem, txtCost;
        public ViewHolder(TextView txtDate, TextView txtItem, TextView txtCost){
                this.txtDate = txtDate;
                this.txtItem = txtItem;
                this.txtCost = txtCost;
        }
    }
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Items getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return items.indexOf(getItem(position));
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView==null){
		        convertView = myInflater.inflate(R.layout.one_item_adapter, null);
		        holder = new ViewHolder(
		                (TextView) convertView.findViewById(R.id.txt_date),
		                (TextView) convertView.findViewById(R.id.txt_item),
		                (TextView) convertView.findViewById(R.id.txt_cost)
		                );
		        convertView.setTag(holder);
		}else{
		        holder = (ViewHolder) convertView.getTag();
		}
		
		Items item = (Items)getItem(position);
		String date = StingFunction.toDateString(item.getDateStamp());
		holder.txtDate.setText(date);
		
		switch(item.getType()){
		case iTypeOUT:
			holder.txtItem.setTextColor(convertView.getResources().getColor(color.maroon));
			holder.txtCost.setTextColor(convertView.getResources().getColor(color.darkslategray));
			break;
		case iTypeIN:
			holder.txtItem.setTextColor(convertView.getResources().getColor(color.maroon));
			holder.txtCost.setTextColor(convertView.getResources().getColor(color.limegreen));
			break;
		case iTypeEXC:
			holder.txtItem.setTextColor(convertView.getResources().getColor(color.cadetblue));
			holder.txtCost.setTextColor(convertView.getResources().getColor(color.cadetblue));
			break;
		}
		
		holder.txtItem.setText(item.getItem());
		holder.txtCost.setText(StingFunction.toDecimalFormat(item.getCash()));
		
		return convertView;
	}

}
