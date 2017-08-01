package com.main.pda;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.application.GApplication;
import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.loginsystem.biz.SystemLoginBiz;
import com.loginsystem.biz.SystemUpdatePwdBiz;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.Abnormal;
import com.messagebox.MenuShow;
import com.messagebox.ResultMsg;
import com.messagebox.Runing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.AlteredCharSequence;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UpdatePwd extends Activity implements OnTouchListener {
	 
	TextView uid;  //用户名
	TextView pwd;   //原密码
	TextView newpwd; //新密码
	Button btnlogin;  //登陆
	Button btncancel;   //取消
	View.OnClickListener clickupdate;
	private ManagerClass managerClass;
	
	private SystemLoginBiz systemLogin;		
	public SystemLoginBiz getSystemLogin() {
		return systemLogin= systemLogin==null?new SystemLoginBiz():systemLogin;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)  {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c_updatepwd);
		
		 //全局异常处理
		// CrashHandler.getInstance().init(this);
		
		managerClass = new ManagerClass();
		uid = (TextView) findViewById(R.id.uid);
		pwd = (TextView) findViewById(R.id.pwd);
		newpwd = (TextView) findViewById(R.id.newpwd);
		btnlogin = (Button) findViewById(R.id.btnlgoin);
		btncancel = (Button) findViewById(R.id.btncancel);
		
		btnlogin.setOnTouchListener(this);
		btncancel.setOnTouchListener(this);
		
		
		//重试单击事件
		clickupdate = new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				//修改密码
				SystemUpdatePwdBiz.updatePwd(uid.getText().toString(),pwd.getText().toString(), newpwd.getText().toString());
				managerClass.getRuning().runding(UpdatePwd.this,"正在修改密码...");
			}
		};
		//hand通知操作
		SystemUpdatePwdBiz.handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				managerClass.getRuning().remove();
				super.handleMessage(msg);
				if(msg.what==1){
					managerClass.getGoBack().back(UpdatePwd.this, "密码修改成功", new OnClickListener() {		
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							managerClass.getGolbalutil().gotoActivity(UpdatePwd.this, SystemManager.class, null, 0);
							UpdatePwd.this.finish();
						}
					});
				}else if(msg.what==0){
					Toast.makeText(UpdatePwd.this, "原始密码错误！", Toast.LENGTH_SHORT).show();	
				}else if(msg.what==-1){
					managerClass.getAbnormal().timeout(UpdatePwd.this, "连接超时!", clickupdate);
				}
			}						
		};
		
		 //把当前activity放进集合	
		// GApplication.addActivity(this,"0updatepwd");
	}

	@Override
	public boolean onTouch(View view, MotionEvent even) {
		//按下的时候
		if(MotionEvent.ACTION_DOWN==even.getAction()){
			switch(view.getId()){
			case R.id.btnlgoin:
				btnlogin.setBackgroundResource(R.drawable.buttom_select_press);
				break;
			case R.id.btncancel:
				btncancel.setBackgroundResource(R.drawable.buttom_select_press);
				break;
				
			}
		}
		
		//手指松开的时候
		if(MotionEvent.ACTION_UP==even.getAction()){
			switch(view.getId()){
			case R.id.btnlgoin:
				btnlogin.setBackgroundResource(R.drawable.buttom_selector_bg);
				//如果密码强度满足要求
				if(regCheck(newpwd.getText().toString())){
					//提示
					 managerClass.getRuning().runding(UpdatePwd.this,"正在修改密码...");
					//修改密码
					SystemUpdatePwdBiz.updatePwd(GApplication.loginname,pwd.getText().toString(), newpwd.getText().toString());		
				}else{
					managerClass.getSureCancel().makeSuerCancel(UpdatePwd.this, "密码必须由数字和英文字母组成", new View.OnClickListener() {					
						@Override
						public void onClick(View arg0) {
						managerClass.getSureCancel().remove();	
						}
					}, true);	
				}
							
				break;
			case R.id.btncancel:
				btncancel.setBackgroundResource(R.drawable.buttom_selector_bg);
				managerClass.getGolbalutil().gotoActivity(UpdatePwd.this, SystemManager.class, null, 0);
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
			case R.id.btnlgoin:
				btnlogin.setBackgroundResource(R.drawable.buttom_select_press);
				break;
			case R.id.btncancel:
				btncancel.setBackgroundResource(R.drawable.buttom_select_press);
				break;
				
			}
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
			MenuShow.pw.showAtLocation(findViewById(R.id.updatepwd_box), Gravity.BOTTOM, 0, 0);
			return false;
		}
		
	/**
	 * 正则表达式	由字母和数字组成
	 * @param str
	 * @return
	 */
	public boolean regCheck(String str){
		Pattern p = Pattern.compile("[a-zA-Z]");
		Pattern p2 = Pattern.compile("[0-9]");		
		String reg = "^[a-zA-Z0-9]{6,}$";
		boolean is = false;
		if(str.matches(reg)){ 
			Matcher m = p.matcher(str);
			Matcher m2 = p2.matcher(str);
		  if(m.find() && m2.find()){
			is = true;
		  }else{
			is = false;
		  }
		}
		return is;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}
		
}
