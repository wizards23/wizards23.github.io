package com.main.pda;

import com.application.GApplication;
import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.messagebox.MenuShow;
import com.service.FixationValue;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SystemManager extends Activity implements OnTouchListener {
	//系统管理
	
	ImageView finger;  //指纹采集
	ImageView pdaset;  //pda设置
	ImageView updatepwd;  //修改密码
	
	LinearLayout lfinger;  //指纹采集
	LinearLayout lpdaset;  //pda设置
	LinearLayout lupdatepwd;  //修改密码
	
	
	private GolbalUtil golbalUtil;		
	public GolbalUtil getGolbalUtil() {
		if(golbalUtil==null){
			golbalUtil = new GolbalUtil();	
		}
		return golbalUtil;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.y_systemmanage);
		
		 //全局异常处理
		// CrashHandler.getInstance().init(this);
		
		finger = (ImageView) findViewById(R.id.finger);
		pdaset = (ImageView) findViewById(R.id.pda);
		updatepwd = (ImageView) findViewById(R.id.updatepwd);
		
		lfinger = (LinearLayout) findViewById(R.id.lfinger);
		lpdaset = (LinearLayout) findViewById(R.id.lpda);
		lupdatepwd = (LinearLayout)findViewById(R.id.lupdatepwd);
		
		finger.setOnTouchListener(this);
		pdaset.setOnTouchListener(this);
		updatepwd.setOnTouchListener(this);
		
		 //把当前activity放进集合	
		// GApplication.addActivity(this,"0systemmanager");
		//权限判断
		 show(Integer.parseInt(GApplication.user.getLoginUserId()));
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public boolean onTouch(View view, MotionEvent even) {
		//当按下的时候
				if(MotionEvent.ACTION_DOWN==even.getAction()){
					
				}
				
				//当移动的时候
				if(MotionEvent.ACTION_MOVE==even.getAction()){
					switch(view.getId()){
					//指纹采集
					case R.id.finger:
						finger.setImageResource(R.drawable.fingergather_down);
						break;
					//pda设置
					case R.id.pda:
						pdaset.setImageResource(R.drawable.systemadmin_item_down);
						break;
					//修改密码
					case R.id.updatepwd:
						updatepwd.setImageResource(R.drawable.updatepwd_down);	
						break;
					}
					getGolbalUtil().ismover++;		
				}
						
				//当松手的的时候
				if(MotionEvent.ACTION_UP==even.getAction()){
					switch(view.getId()){
					//指纹采集
					case R.id.finger:
						finger.setImageResource(R.drawable.fingergather);
						getGolbalUtil().gotoActivity(SystemManager.this, FingerGather.class, null, getGolbalUtil().ismover);
						break;
					//pda设置
					case R.id.pda:
						pdaset.setImageResource(R.drawable.systemadmin_item);
						//getGolbalUtil().gotoActivity(SystemManager.this, FingerGather.class, null, getGolbalUtil().ismover);
						break;
					//修改密码
					case R.id.updatepwd:
						updatepwd.setImageResource(R.drawable.updatepwd);	
						getGolbalUtil().gotoActivity(SystemManager.this, UpdatePwd.class, null, getGolbalUtil().ismover);
						break;
					}
					
					
					getGolbalUtil().ismover=0;
				}
				
				//当弹起因意外打断的时候	
				if(MotionEvent.ACTION_CANCEL==even.getAction()){
					
					switch(view.getId()){
					//指纹采集
					case R.id.finger:
						finger.setImageResource(R.drawable.fingergather);
						break;
					//pda设置
					case R.id.pda:
						pdaset.setImageResource(R.drawable.systemadmin_item);
						break;
					//修改密码
					case R.id.updatepwd:
						updatepwd.setImageResource(R.drawable.updatepwd);	
						break;
					}
					getGolbalUtil().ismover=0;				
						
				}
		return true;
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
			MenuShow.pw.showAtLocation(findViewById(R.id.system_box), Gravity.BOTTOM, 0, 0);
			return false;
		}
		
		public void show(int permission){
			switch(permission){
			case 3:
				 lfinger.setVisibility(View.VISIBLE);  //指纹采集
				 lpdaset.setVisibility(View.VISIBLE);  //pda设置
				 lupdatepwd.setVisibility(View.GONE);  //修改密码	
				break;
			default:
				 lfinger.setVisibility(View.GONE);  //指纹采集
				 lpdaset.setVisibility(View.GONE);  //pda设置
				 lupdatepwd.setVisibility(View.VISIBLE);  //修改密码	
			break;
			}
		}
		@Override
		public void onBackPressed() {
			super.onBackPressed();
			this.finish();
		}
		
}
