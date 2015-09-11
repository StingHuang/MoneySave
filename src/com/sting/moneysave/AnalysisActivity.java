package com.sting.moneysave;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnalysisActivity extends AppCompatActivity {

	private final String Key_Bundle = "MONTH_YEAR";
	private LinearLayout mLinearLayout, LLcostlist;
	
	private final int Total = 0, iTagEat = 1, iTagCth = 2, iTagHos = 3,
						iTagTff = 4, iTagFun = 5, iTagEdu = 6;

	private String[] costS = {"食: NT$", "衣: NT$", "住: NT$", "行: NT$", "樂: NT$", "育: NT$"};
	private float[] data_cost = new float[7];
	private float[] data_values = new float[6];
	private int[] pie_color = {R.color.orange, R.color.hotpink, R.color.seagreen,
			R.color.steelblue, R.color.darkviolet, R.color.gray};
	
	private List<Items> money_items = new ArrayList<Items>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pie_chart);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle bundle = getIntent().getExtras();  
		int[] my = bundle.getIntArray(Key_Bundle);
		money_items = ItemsHelper.listItems(AnalysisActivity.this, Integer.toString(my[0]), my[1]);
		
		for(Items aItem: money_items){
			if(aItem.getItemTag()!=Total){
				data_cost[Total] = data_cost[Total] + aItem.getCash();
				switch(aItem.getItemTag()){
				case iTagEat:
					data_cost[iTagEat] = data_cost[iTagEat] + aItem.getCash();
					break;
				case iTagCth:
					data_cost[iTagCth] = data_cost[iTagCth] + aItem.getCash();
					break;
				case iTagHos:
					data_cost[iTagHos] = data_cost[iTagHos] + aItem.getCash();
					break;
				case iTagTff:
					data_cost[iTagTff] = data_cost[iTagTff] + aItem.getCash();
					break;
				case iTagFun:
					data_cost[iTagFun] = data_cost[iTagFun] + aItem.getCash();
					break;
				case iTagEdu:
					data_cost[iTagEdu] = data_cost[iTagEdu] + aItem.getCash();
					break;
				}
			}	
		}
		
		for(int i=1; i<data_cost.length; i++){
			data_values[i-1] = 360 * ( data_cost[i]/data_cost[Total] );
		}
		setView();
	}
	
	private void setView(){
		mLinearLayout = (LinearLayout)findViewById(R.id.LinearLayout_piechart);
		MyGraphview graphview = new MyGraphview(this);  
		mLinearLayout.addView(graphview);
		
		LLcostlist = (LinearLayout)findViewById(R.id.LinearLayout_costlist);
		
		for(int i=0; i<data_values.length; i++){
			TextView tv = new TextView(this);
			tv.setText( costS[i] + Integer.toString( (int)data_cost[i+1] ) );
			tv.setTextColor(getResources().getColor(pie_color[i]));
			tv.setTextSize(24);
			tv.setLayoutParams(new LayoutParams(
		            LayoutParams.WRAP_CONTENT,
		            LayoutParams.WRAP_CONTENT));
			LLcostlist.addView(tv);
		}
	}
	
	public class MyGraphview extends View { 
		
		public MyGraphview(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);   
		RectF rectf = new RectF(150, 150, 550, 550);  
		float temp = 0;  

		@Override
		protected void onDraw(Canvas canvas) {
			// TODO Auto-generated method stub
			super.onDraw(canvas);
			for (int i = 0; i < data_values.length; i++) {  
				if (i == 0) {  
					paint.setColor(getResources().getColor(pie_color[i]));  
					canvas.drawArc(rectf, 0, data_values[i], true, paint); 
					temp = temp + data_values[i];
				} else {      
					paint.setColor(getResources().getColor(pie_color[i]));  
					canvas.drawArc(rectf, temp, data_values[i], true, paint);
					temp = temp + data_values[i];
				}
			}
		}
		
	}

}
