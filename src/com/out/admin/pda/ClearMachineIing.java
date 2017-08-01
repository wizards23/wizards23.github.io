package com.out.admin.pda;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.application.GApplication;
import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.imple.getnumber.WebJoin;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.pda.BankDoublePersonLogin;
import com.out.biz.ClearMachineIngBiz;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ClearMachineIing extends Activity implements OnTouchListener {
	//正在进行清机加钞
	
	Button btn;
	TextView msgs;
	ImageView fail;
	ImageView sccuss;
	ImageView back;
	List<String> list_result = new ArrayList<String>(); 
	ListView lv;
	String head="";
	Button suer;
	
	ClearMachineIngBiz clearMachineIngBiz;
	ClearMachineIngBiz getClearMachineIngBiz(){
		if(clearMachineIngBiz==null){
			clearMachineIngBiz = new ClearMachineIngBiz();
		}
		return clearMachineIngBiz;
	  }
	
	private ManagerClass managerClass;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//禁止休睡眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cleaning);
		managerClass = new ManagerClass();
		
		btn = (Button) findViewById(R.id.clear_btn);
		msgs = (TextView) findViewById(R.id.clear_msg);
		fail = (ImageView) findViewById(R.id.clear_fail);
		back = (ImageView) findViewById(R.id.cleaning_back);
		sccuss = (ImageView) findViewById(R.id.clear_sccuss);
		lv = (ListView) findViewById(R.id.listview_clearing);
		suer = (Button) findViewById(R.id.clearing_sure);
		
		 //全局异常处理
		// CrashHandler.getInstance().init(this);
		 back.setOnTouchListener(this);
		 suer.setOnTouchListener(this);
		 
		btn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
			//revised By zhangxuewei for CorpId Question
			//getClearMachineIngBiz().cleanAtmAddMoney(getBoxNumList(), GApplication.getApplication().user.getOrganizationId(), BankDoublePersonLogin.userid1, BankDoublePersonLogin.userid2);
			getClearMachineIngBiz().cleanAtmAddMoney(getBoxNumList(), GApplication.getApplication().taskCorpId, BankDoublePersonLogin.userid1, BankDoublePersonLogin.userid2);
			msgs.setText("正在进行清机加钞操作...");
			fail.setVisibility(View.GONE);
			sccuss.setVisibility(View.GONE);
			btn.setVisibility(View.GONE);
			/** Modify begin by zhangxuewei at 05-dec-2016
			 * 重新加钞时，清空listview显示内容*/
			list_result.clear();
			/** Modify end */
			}
		});
		
		//清机加钞
		getClearMachineIngBiz().handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				switch(msg.what){
				case 1:
					 msgs.setText("");
						Log.i("成功", "成功");
						show();
						msgs.setText(head);
						lv.setAdapter(new Ad());
						//清机加钞成功后，清空数据
						//WebJoin.list.clear();
					break;
				case -1:
					msgs.setText("操作处理异常，请重新操作");
					fail.setVisibility(View.VISIBLE);
					sccuss.setVisibility(View.GONE);
					btn.setVisibility(View.VISIBLE);
					break;
				case 0:
					msgs.setText(ClearMachineIngBiz.msg);
					fail.setVisibility(View.VISIBLE);
					sccuss.setVisibility(View.GONE);
					btn.setVisibility(View.VISIBLE);
					break;
				case -4:
					msgs.setText("操作处理超时，请重新操作");
					fail.setVisibility(View.VISIBLE);
					sccuss.setVisibility(View.GONE);
					btn.setVisibility(View.VISIBLE);
					break;
				
				}
				
			}			
		};
		// GApplication.getApplication().addActivity(this,"0clearing");	
		
	}
	
	/**
	 * 钞箱列表
	 */
	public String getBoxNumList(){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < WebJoin.list.size(); i++) {
			String boxNum =WebJoin.list.get(i).getNum();
			sb.append(boxNum);
			sb.append("|");
		}
		return sb.toString().substring(0,sb.length()-1);
	}	

	@Override
	protected void onStart() {
		super.onStart();
		//清机加钞
		//getClearMachineIngBiz().cleanAtmAddMoney(getBoxNumList(), GApplication.getApplication().user.getOrganizationId(), BankDoublePersonLogin.userid1, BankDoublePersonLogin.userid2);		
		//revised By zhangxuewei for CorpId Question
		getClearMachineIngBiz().cleanAtmAddMoney(getBoxNumList(), GApplication.getApplication().taskCorpId, BankDoublePersonLogin.userid1, BankDoublePersonLogin.userid2);
	}

	@Override
	public boolean onTouch(View view, MotionEvent even) {
		//按下的时候
		if(MotionEvent.ACTION_DOWN==even.getAction()){

			switch(view.getId()){			
			case R.id.clearing_sure:
				suer.setBackgroundResource(R.drawable.buttom_select_press);				
				break;
			case R.id.cleaning_back:
			   back.setBackgroundResource(R.drawable.back_cirle_press);
			   ClearMachineIing.this.finish();
			   managerClass.getGolbalutil().ismover=0;
				break;
			
			}
		}
		
		//手指松开的时候
		if(MotionEvent.ACTION_UP==even.getAction()){
			switch(view.getId()){
			
			case R.id.clearing_sure:
				suer.setBackgroundResource(R.drawable.buttom_selector_bg);
				ClearMachineIing.this.finish();
				managerClass.getGolbalutil().gotoActivity(ClearMachineIing.this,ClearMachineResult.class, null, 0);		
				break;
			case R.id.cleaning_back:
			   back.setBackgroundResource(R.drawable.back_cirle);
			   ClearMachineIing.this.finish();
			   managerClass.getGolbalutil().ismover=0;
				break;
			
			}
				
		}
		//手指移动的时候
		if(MotionEvent.ACTION_MOVE==even.getAction()){
			back.setBackgroundResource(R.drawable.back_cirle);
			managerClass.getGolbalutil().ismover++;
		}
		//意外中断事件取消
		if(MotionEvent.ACTION_CANCEL==even.getAction()){
			switch(view.getId()){			
			case R.id.clearing_sure:
				suer.setBackgroundResource(R.drawable.buttom_selector_bg);				
				break;
			case R.id.cleaning_back:
			   back.setBackgroundResource(R.drawable.back_cirle);
			   ClearMachineIing.this.finish();
			   managerClass.getGolbalutil().ismover=0;
				break;
			
			}
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}
	
	/**
	 * 处理清机加钞结果
	 */
	public void show(){
	   if(ClearMachineIngBiz.msg!=null){
		 if(ClearMachineIngBiz.msg.indexOf("ATM")>0){
			 /** MODIFY BEGIN BY ZHANGXUEWEI 2016-12-01 因为只要返回00就显示正确，这样是不正确的 */
		   if(ClearMachineIngBiz.msg.contains("失败")){
			   fail.setVisibility(View.VISIBLE);
				sccuss.setVisibility(View.GONE);
				btn.setVisibility(View.VISIBLE);
		   }
		   /**Modify End*/
		   head =ClearMachineIngBiz.msg.substring(0, ClearMachineIngBiz.msg.indexOf("ATM"));
		   String body = ClearMachineIngBiz.msg.substring(ClearMachineIngBiz.msg.indexOf("ATM"),ClearMachineIngBiz.msg.length());
		   String[] array = body.split(";");
		   for (int i = 0; i < array.length; i++) {
			   list_result.add(array[i]);
		   	}
		 }  
		   
	   }
				
	}
	
	
	class Ad extends BaseAdapter{

		@Override
		public int getCount() {
			
			return list_result.size();
		}

		@Override
		public Object getItem(int arg0) {			
			return list_result.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {			
			return 0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			View v = view;			
			if(v==null){
				v = GolbalView.getLF(ClearMachineIing.this).inflate(R.layout.clearing_item, null);	
				Log.i("v", "vvvvvvvvvvvv");
			}
			
			TextView item = (TextView) v.findViewById(R.id.clear_lv_item);
			
			item.setText(getItem(arg0).toString());
			return v;
		}
		
	}
	
	
	
}
