package com.moneyboxadmin.pda;

import java.util.Timer;
import java.util.TimerTask;

import com.application.GApplication;
import com.clearadmin.pda.ClearManager;
import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.manager.classs.pad.ManagerClass;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ClearerUser extends Activity implements OnTouchListener{
	//清分员交接
	
	Bundle bundle_clear;   //接收主界面传过来的参数
	ImageView back;
	String islogin; 	//标识是清分交接还是清分管理登录
	Bundle joinresult;    //标识是何种交接完成
	TextView clearuser;   //交接者类型
	String title=null;
	private ManagerClass managerClass;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.y_clearingmember_join);
		managerClass = new ManagerClass();
		back = (ImageView) findViewById(R.id.back);
		clearuser = (TextView) findViewById(R.id.clear_user);
		 //全局异常处理
		// CrashHandler.getInstance().init(this);
		 
		bundle_clear = getIntent().getExtras();
		
		clearuser.setText("清分员交接");
		if(bundle_clear!=null){
		islogin = bundle_clear.getString("clear");
		title = bundle_clear.getString("webjoin");
			if(title!=null){
				clearuser.setText(title);	
			}else if("清分管理".equals(islogin)){
				clearuser.setText("清分员登录");
			}
		}			
		Timer t = new Timer();
		
		t.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
			joinresult = new Bundle();
				
			if("清分管理".equals(islogin)){
			//跳到清分管理
			managerClass.getGolbalutil().gotoActivity(ClearerUser.this,ClearManager.class, null, 0);	
			}else if("网点加钞钞箱交接".equals(islogin)){
				//网点加钞钞箱交接结果
				joinresult.putString("joinresult","网点加钞钞箱交接");				
				managerClass.getGolbalutil().gotoActivity(ClearerUser.this,JoinResult.class, null, 0);	
			}else{
			//交接结果
			joinresult.putString("joinresult","空钞箱交接完成");
			managerClass.getGolbalutil().gotoActivity(ClearerUser.this,JoinResult.class, null, 0);		
			}
						
			}
		}, 2000);
		
		 //把当前activity放进集合	
		// GApplication.addActivity(this,"0clearuser");
	}
	
	
	//触摸事件
	@Override
	public boolean onTouch(View view, MotionEvent even) {
		//按下的时候
		if(MotionEvent.ACTION_DOWN==even.getAction()){
			switch(view.getId()){
			case R.id.back_clear:
				back.setImageResource(R.drawable.back_cirle_press);
				ClearerUser.this.finish();
				break;
				
			}
		}
		
		//手指松开的时候
		if(MotionEvent.ACTION_UP==even.getAction()){
			switch(view.getId()){
			case R.id.back_clear:
				back.setImageResource(R.drawable.back_cirle);
				ClearerUser.this.finish();
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
			back.setImageResource(R.drawable.back_cirle_press);
		}

		return true;
	}
	
}
