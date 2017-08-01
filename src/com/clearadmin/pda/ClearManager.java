package com.clearadmin.pda;

import com.application.GApplication;
import com.clearadmin.biz.GetCashboxNumClearCheckBiz;
import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.loginsystem.biz.SystemLoginBiz;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.Abnormal;
import com.messagebox.MenuShow;
import com.messagebox.ResultMsg;
import com.messagebox.Runing;
import com.moneyboxadmin.pda.PlanWay;
import com.moneyboxadmin.pda.MoneyBoxManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


//清分管理
public class ClearManager extends Activity implements OnTouchListener {

	ImageView back_clearmanager;   //返回
	Bundle bundle_biz;  //标识选了那种业务
	
	TextView boxputin;   //钞箱加钞
	TextView boxback;   //回收钞箱清点
	OnClickListener click;
	TextView addboxcount;  //加钞数量
	TextView backboxcount;  //回收数量
	int count;   //首次进入时提示加载状态
	private ManagerClass managerClass;
	View.OnClickListener sureclick;
	
	
	private GetCashboxNumClearCheckBiz getCashboxNumClearCheck;	
	GetCashboxNumClearCheckBiz getGetCashboxNumClearCheck() {return getCashboxNumClearCheck=getCashboxNumClearCheck==null?new GetCashboxNumClearCheckBiz():getCashboxNumClearCheck;}
				
	private SystemLoginBiz systemLogin;			
	SystemLoginBiz getSystemLogin() {
		return systemLogin=systemLogin==null?new SystemLoginBiz():systemLogin;
	}	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//禁止休睡眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.clear_manager);
		
		 //全局异常处理
		// CrashHandler.getInstance().init(this);
		
		managerClass = new ManagerClass();
		managerClass.getGolbalView().Init(this);
		back_clearmanager = (ImageView) findViewById(R.id.back_clearadmin);
		boxputin = (TextView) findViewById(R.id.addmoney);
		boxback = (TextView) findViewById(R.id.countbox);
		
		addboxcount = (TextView) findViewById(R.id.addboxcount);
		backboxcount = (TextView) findViewById(R.id.backboxcount);
		
		boxputin.setOnTouchListener(this);
		boxback.setOnTouchListener(this);
		back_clearmanager.setOnTouchListener(this);
		
		bundle_biz = new Bundle();
		
		//重试单击事件
		click =new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				managerClass.getRuning().runding(ClearManager.this,"正在获取钞箱数量...");
				//重新获取加钞箱数量，回收钞箱数量，传入参数：机构ID
				getGetCashboxNumClearCheck().getClearNumBox(GApplication.user.getOrganizationId());
			}
		};
		
		
		
		//Handler 通知操作
		getGetCashboxNumClearCheck().handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				managerClass.getRuning().remove();
				super.handleMessage(msg);
				
				switch(msg.what){
				case 1:
					count++;  
					Bundle bundle = msg.getData();
					addboxcount.setText(bundle.getString("addmoney"));
					backboxcount.setText(bundle.getString("backmoney"));
					break;
				case -1:
					 managerClass.getAbnormal().timeout(ClearManager.this, "连接异常，重新连接？", click);
					break;
				case -4:
					 managerClass.getAbnormal().timeout(ClearManager.this, "连接超时，重新连接？", click);
					break;
				case 0:
					Toast.makeText(ClearManager.this,"暂时无业务",Toast.LENGTH_SHORT).show();
					break;
				}
				
				
			}
			
		};
		
		
		//是否退出业务事件
		sureclick = new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				managerClass.getSureCancel().remove();
				ClearManager.this.finish();
			}
		};
		
	}

	@Override
	public boolean onTouch(View view, MotionEvent even) {
		//按下的时候
		if(MotionEvent.ACTION_DOWN==even.getAction()){
			switch(view.getId()){			
			//返回
			case R.id.back_clearadmin:
				back_clearmanager.setImageResource(R.drawable.back_cirle_press);
				break;	
			//加钞
			case R.id.addmoney:
				boxputin.setBackgroundResource(R.color.gray_msg_bg);
				break;
			//回收
			case R.id.countbox:
				boxback.setBackgroundResource(R.color.gray_msg_bg);
				break;
			}
		}
		
		//手指松开的时候
		if(MotionEvent.ACTION_UP==even.getAction()){
			switch(view.getId()){
			//返回
			case R.id.back_clearadmin:
				back_clearmanager.setImageResource(R.drawable.back_cirle);				
			    managerClass.getSureCancel().makeSuerCancel(this, "您确认要出退清分业务操作？", sureclick, false);								
				break;
			//钞箱加钞
			case R.id.addmoney:
				bundle_biz.putString("business", "钞箱加钞");
				boxputin.setBackgroundResource(R.color.transparency);
				if(addboxcount.getText().equals("0")){
				Toast.makeText(ClearManager.this,"没有业务", Toast.LENGTH_SHORT).show();	
				}else{			
				managerClass.getGolbalutil().gotoActivity(ClearManager.this, PlanWay.class, bundle_biz, managerClass.getGolbalutil().ismover);
				}
				break;
			//回收钞箱清点
			case R.id.countbox:
				bundle_biz.putString("business", "回收钞箱清点");
				boxback.setBackgroundResource(R.color.transparency);
				if(backboxcount.getText().equals("0")){
					Toast.makeText(ClearManager.this,"没有业务", Toast.LENGTH_SHORT).show();
				}else{
					managerClass.getGolbalutil().gotoActivity(ClearManager.this, PlanWay.class, bundle_biz, managerClass.getGolbalutil().ismover);
				 }
				break;
			}
			managerClass.getGolbalutil().ismover=0;
		}
		//手指移动的时候
		if(MotionEvent.ACTION_MOVE==even.getAction()){
			managerClass.getGolbalutil().ismover++;
		}
		//意外中断事件取消
		if(MotionEvent.ACTION_CANCEL==even.getAction()){
			managerClass.getGolbalutil().ismover=0;
			switch(view.getId()){
			//返回
			case R.id.back_clearadmin:
				back_clearmanager.setImageResource(R.drawable.back_cirle_press);
				boxputin.setBackgroundResource(R.color.transparency);
				boxback.setBackgroundResource(R.color.transparency);
				break;
			
			}
		}

		return true;
	}


	@Override
	protected void onStart() {
		super.onStart();			
		 try {
			 managerClass.getRuning().runding(this,"正在获取钞箱数量...");
		} catch (Exception e) {
			System.out.println("正在获取钞箱数量...");
			System.out.println(e.getMessage());
		}	
		//开始获取加钞箱数量，回收钞箱数量，传入参数：机构ID
		 try {			 
			 Log.i("GApplication.user.getOrganizationId()",GApplication.user.getOrganizationId());
			 getGetCashboxNumClearCheck().getClearNumBox(GApplication.user.getOrganizationId());
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {	
		menu.add("a");
		return true;
	}
	
	//拦截Menu
		@Override
		public boolean onMenuOpened(int featureId, Menu menu) {
			new MenuShow().menu(this);
			MenuShow.pw.showAtLocation(findViewById(R.id.clearmanager_box), Gravity.BOTTOM, 0, 0);
			return false;
		}
	
	
	
}
