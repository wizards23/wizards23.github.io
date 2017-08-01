package com.out.admin.pda;

import com.entity.JoinInfo;
import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.loginsystem.biz.SystemLoginBiz;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.Abnormal;
import com.messagebox.Runing;
import com.out.admin.pda.ATMInformation.Ad;
import com.out.biz.AssignBiz;
import com.out.biz.CashboxHandoverInfoBiz;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class AboutJoin extends Activity {
	
	Ad ad;
	ListView listview;
	ImageView back;
	
	private ManagerClass managerClass;
	CashboxHandoverInfoBiz cashboxHandoverInfo;
	CashboxHandoverInfoBiz getCashboxHandoverInfo() {
		return cashboxHandoverInfo=cashboxHandoverInfo==null?new CashboxHandoverInfoBiz():cashboxHandoverInfo;
	}

	
	private SystemLoginBiz systemLogin;
	SystemLoginBiz getSystemLogin() {
		return systemLogin=systemLogin==null?new SystemLoginBiz():systemLogin;
	}				
	private AssignBiz assign;	
	AssignBiz getAssign() {
		return assign =assign==null?new AssignBiz():assign;
	}

	View.OnClickListener click;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutjoin_information);
		
		 //全局异常处理
		// CrashHandler.getInstance().init(this);
		
		listview = (ListView) findViewById(R.id.listaboutjoin);	
		back = (ImageView) findViewById(R.id.aboutjoin_back);
		managerClass = new ManagerClass();
		//重试单击事件
		click = new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				//提示
				managerClass.getRuning().runding(AboutJoin.this,"正在获取交接情况...");
				//开始获取数据，参数：计划编号
				getCashboxHandoverInfo().getJionInfo(getAssign().order.getPlanNum());					
			}
		};
				
		//hand通知
		getCashboxHandoverInfo().handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				managerClass.getRuning().remove();
				super.handleMessage(msg);
				
				switch(msg.what){
				case 1:
					if(ad==null){
						   ad = new Ad();	
						  }				
					listview.setAdapter(ad);
					break;
				case -1:
					managerClass.getAbnormal().timeout(AboutJoin.this,"连接异常,重试请确定！", click);
					break;
					
				case 0:
					managerClass.getSureCancel().makeSuerCancel(AboutJoin.this, "没有数据！", new View.OnClickListener() {					
						@Override
						public void onClick(View arg0) {
						managerClass.getSureCancel().remove();	
						}
					},true);
					break;					
				case -4:
					managerClass.getAbnormal().timeout(AboutJoin.this,"连接异常,重试请确定！", click);
					break;
					
					
				}
				
			}
			
		};	
		
		
		back.setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View view, MotionEvent even) {
			if(MotionEvent.ACTION_DOWN==even.getAction()){
				view.setBackgroundResource(R.drawable.back_cirle_press);
			}
			
			if(MotionEvent.ACTION_UP==even.getAction()){
				view.setBackgroundResource(R.drawable.back_cirle);
				AboutJoin.this.finish();
			}
			
			if(MotionEvent.ACTION_CANCEL==even.getAction()){
				view.setBackgroundResource(R.drawable.back_cirle);
			}
			
				return true;
			}
		});
		
	}
	

						
	@Override
	protected void onStart() {
		super.onStart();
		
			managerClass.getRuning().runding(this,"正在获取交接情况...");
			//开始获取数据，参数：计划编号
			getCashboxHandoverInfo().getJionInfo(getAssign().order.getPlanNum());		
		
	}


	//适配器
	class Ad extends BaseAdapter{

		@Override
		public int getCount() {
			return getCashboxHandoverInfo().list.size();
		}
		@Override
		public Object getItem(int arg0) {
			return getCashboxHandoverInfo().list.get(arg0);
		}
		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			View v = view;
			ViewHoler holer = null;
			if(v==null){
				v=GolbalView.getLF(AboutJoin.this).inflate(R.layout.aboutjoin_item, null);	
				holer = new ViewHoler();
				holer.address =(TextView) v.findViewById(R.id.join_address);
				holer.addnum =(TextView) v.findViewById(R.id.join_addnum);
				holer.addjoin =(TextView) v.findViewById(R.id.join_addstate);
				holer.backjoin =(TextView) v.findViewById(R.id.join_backjoin);
				holer.backnum =(TextView) v.findViewById(R.id.join_backnum);
				v.setTag(holer);
			}else{
				holer = (ViewHoler) v.getTag();
			}
			
				JoinInfo join = (JoinInfo) getItem(arg0);
				holer.address.setText(join.getAddress());
				holer.addnum.setText(join.getAddnum());
				holer.addjoin.setText(join.getAddstate());
				holer.backjoin.setText(join.getBackstate());
				holer.backnum.setText(join.getBacknum());
						
			return v;
		}				
	}
	
	static class ViewHoler{
		TextView address;   //地址
		TextView addnum;  //加钞钞箱数量
		TextView addjoin;  //加钞交接情况
		TextView backnum;  //回收钞箱数量
		TextView backjoin; //回收钞箱交接情况
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}
	
	
}
