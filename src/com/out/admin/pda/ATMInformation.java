package com.out.admin.pda;

import com.entity.ATM;
import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalView;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.Abnormal;
import com.messagebox.Runing;
import com.out.biz.AtmDetailBiz;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ATMInformation extends Activity {

	
	ListView listview;
	Bundle bundle;
	String plan;
	View.OnClickListener click;
	ImageView back;
	private ManagerClass managerClass;
	
	AtmDetailBiz atmDetail;		
	AtmDetailBiz getAtmDetail() {
		return atmDetail=atmDetail==null?new AtmDetailBiz():atmDetail;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.atm_information);
		
		 //全局异常处理
		// CrashHandler.getInstance().init(this);
		
		managerClass = new ManagerClass();
		listview = (ListView) findViewById(R.id.listviewatminfo);
		back = (ImageView) findViewById(R.id.atm_back);
		bundle = getIntent().getExtras();
		plan=bundle.getString("plan");
		
		
		back.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent even) {
				if(MotionEvent.ACTION_DOWN==even.getAction()){
					back.setImageResource(R.drawable.back_cirle_press);
				}
				if(MotionEvent.ACTION_UP==even.getAction()){
					back.setImageResource(R.drawable.back_cirle);
					ATMInformation.this.finish();
				}
				if(MotionEvent.ACTION_CANCEL==even.getAction()){
					back.setImageResource(R.drawable.back_cirle);
				}
				return true;
			}
		});
		
		click = new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				 managerClass.getAbnormal().remove();
				 managerClass.getRuning().runding(ATMInformation.this,"正在获取ATM明细...");
				  //获取ATM明细信息
				 getAtmDetail().getAtmDetail(plan);	
			}
		};
		
		
		//ATM明细信息
		getAtmDetail().handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				managerClass.getRuning().remove();
				
				switch(msg.what){
				case 1:
					listview.setAdapter(new Ad());	
					break;
				case -1:
					managerClass.getAbnormal().timeout(ATMInformation.this, "连接异常！", click);
					break;
				case 0:
	
					break;
				case -4:
					managerClass.getAbnormal().timeout(ATMInformation.this, "连接超时！", click);
					break;
				}
				
				
			}			
		};						
	}
	
	
	
	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if(getAtmDetail().list==null){
		  managerClass.getRuning().runding(this,"正在获取ATM明细...");
		  Log.i("ATM明细", "1");
		  //获取ATM明细信息
		  getAtmDetail().getAtmDetail(plan);
		  Log.i("ATM明细", "2");
		}else{
		  listview.setAdapter(new Ad());	
		}
	}

	



	//适配器
	class Ad extends BaseAdapter{
		@Override
		public int getCount() {
			return getAtmDetail().list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return getAtmDetail().list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			
			ViewHolder holder=null;
			View v=view;
			if(v==null){
			   v = GolbalView.getLF(ATMInformation.this).inflate(R.layout.atm_detail_item, null);			   
			   holder = new ViewHolder();
			   holder.atmNum = (TextView) v.findViewById(R.id.atmNum);
			   holder.address = (TextView) v.findViewById(R.id.atmaddress);
			   holder.boxNum = (TextView) v.findViewById(R.id.atmboxnum);
			   v.setTag(holder);
			}else{
			   holder = (ViewHolder) v.getTag();	
			}
			   ATM atm = (ATM) getItem(arg0);
			 
			   holder.atmNum.setText(atm.getAtmNum());
			   holder.address.setText(atm.getAddress());
			   holder.boxNum.setText(atm.getBoxNum());
			return v;
		}
				
	}
	
	static class ViewHolder{
		TextView atmNum;
		TextView boxNum;
		TextView address;
		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	} 
	
	
}
