package com.main.pda;

import com.application.GApplication;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.ljsw.tjbankpda.db.application.o_Application;
import com.loginsystem.biz.SystemLoginBiz;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.MenuShow;
import com.service.FixationValue;
import com.service.NetService;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SystemLogin extends Activity implements OnTouchListener {
	// 系统登陆界面

	Button login; // 登陆按钮
	Button cancel; // 取消按钮
	EditText editname; // 用户名输入框
	EditText editpwd; // 密码输入框

	TextView textlogin; // 网络状态提示
	String name = null; // 用户名
	String pwd = null; // 密码
	OnClickListener onclickreplace;
	SharedPreferences sharepre;
	String space; // 空间
	String webservice; // webservice地址
	int error = 3;
	//public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
	boolean network = true; // 是否有网络
	public static boolean current; // 当前界面

	// 记住用户名
	SharedPreferences share;
	Editor editor;
	private ManagerClass managerClass;

	private SystemLoginBiz systemLogin;

	public SystemLoginBiz getSystemLogin() {
		if (systemLogin == null) {
			systemLogin = new SystemLoginBiz();
		}
		return systemLogin;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
		setContentView(R.layout.c_login_system);

		// 全局异常处理
		// CrashHandler.getInstance().init(this);
		managerClass = new ManagerClass();
		login = (Button) findViewById(R.id.system_login_btn);
		cancel = (Button) findViewById(R.id.system_login_cancel);
		editname = (EditText) findViewById(R.id.name);
		editpwd = (EditText) findViewById(R.id.pwd);
		textlogin = (TextView) findViewById(R.id.netmsgtext);

		login.setOnTouchListener(this);
		cancel.setOnTouchListener(this);
		
			
		// 接收网络状态广播后，发出handler通知
		NetService.handnet = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 1) {
					textlogin.setVisibility(View.GONE);
					network = true;
				} else {
					textlogin.setVisibility(View.VISIBLE);
					network = false;
				}
			}
		};

		// 手动判断是否有网络
		if (NetService.info != null
				&& !NetService.info.isConnectedOrConnecting()) {
			textlogin.setVisibility(View.VISIBLE);
			network = false;
		} else if (NetService.info == null) {
			textlogin.setVisibility(View.VISIBLE);
			network = false;
		}

		// 重试单击事件
		onclickreplace = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				if (network) {
					managerClass.getRuning().runding(SystemLogin.this,
							"正在登录...");
					getSystemLogin().login(name, pwd);
				} else {
					// Toast.makeText(SystemLogin.this,"网络没有连通，无法登录",
					// Toast.LENGTH_LONG).show();
					managerClass.getGolbalView().toastShow(SystemLogin.this,
							"网络没有连通，无法登录");
				}
			}
		};

		// Hand通知操作
		getSystemLogin().handler_login = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				managerClass.getRuning().remove();
				super.handleMessage(msg);

				switch (msg.what) {
				case 1:
					error = 3;
					GApplication.getApplication().app_hash.put(
							"login_username", editname.getText());
					GApplication.loginUsername = GApplication.user.getLoginUserName();
					GApplication.organizationName = GApplication.user.getOrganizationName();
				//	System.out.println(GApplication.user.getLoginUserName());	
					GApplication.loginname=name;
					GApplication.loginpwd=pwd; //修改密码用
					o_Application.kuguan_db=GApplication.user;
					GApplication.loginJidouId=GApplication.user.getOrganizationId();
					GApplication.getApplication().loginjueseid=GApplication.user.getLoginUserId();
					System.out.println("GApplication.loginJidouId :" +GApplication.user.getOrganizationId());
			//		GApplication.user.setLoginUserName(name);
					managerClass.getGolbalutil().gotoActivity(SystemLogin.this,
							HomeMenu.class, null, GolbalUtil.ismover);
					break;
				case 0:
//					error--;
//					if (error <= 0) {
//						managerClass.getGolbalView().toastShow(
//								SystemLogin.this, "连续错误3次以上！帐号已被锁定");
//						
//					} else {
//						managerClass.getGolbalView()
//								.toastShow(SystemLogin.this,
//										"用户或密码不正确！还有" + error + "次机会");
//					}
					if(msg.obj!=null){
						managerClass.getGolbalView().toastShow(
								SystemLogin.this, msg.obj.toString());
					}else{
						managerClass.getGolbalView().toastShow(
								SystemLogin.this, "");
					}
					break;
				case -4:
					managerClass.getAbnormal().timeout(SystemLogin.this,
							"登录超时，重新链接？", onclickreplace);
					break;
				case -1:
					managerClass.getAbnormal().timeout(SystemLogin.this,
							"登录出现异常", onclickreplace);
					break;
				case -3:
					managerClass.getGolbalView().toastShow(SystemLogin.this,
							"用户或密码为空！");
					break;

				}

			}

		};

		share = this.getPreferences(0);
		editor = share.edit();

		// 把当前activity放进集合
		GApplication.addActivity(this, "1system");
	}

	@Override
	protected void onStart() {
		super.onStart();
		current = true;
		Log.i("1111", "11111");

//		//获取空间和web服务地址
//		sharepre = getSharedPreferences("web", Context.MODE_PRIVATE);
//
//		space = sharepre.getString("space", "");
//		webservice = sharepre.getString("webservice", "");
//
//		Log.i("space", space + "a");
//		Log.i("webservice", webservice + "a");
//
//		if (!"".equals(space) && !"".equals(webservice)) {
//			FixationValue.URL = webservice;
//			FixationValue.NAMESPACE = space;
//
//			Log.i("FixationValue.URL", FixationValue.URL + "");
//			Log.i("FixationValue.NAMESPACE", FixationValue.NAMESPACE + "");
//		} else {
//			System.out.println("默认地址");
//			FixationValue.NAMESPACE = "http://service.timer.cashman.poka.cn/";
//			FixationValue.URL = "http://192.168.1.111:8080/CashWebServices/webservice/cash_pda";
//
//			// FixationValue.NAMESPACE ="http://service.timer.cashman.poka.cn";
//			// FixationValue.URL
//			// ="http://10.16.1.120:9080/cash/webservice/cash_pda";
//		}
//		Log.i("2222", "2222");
//
//		Log.i("FixationValue.URL", FixationValue.URL + "");
//		Log.i("FixationValue.NAMESPACE", FixationValue.NAMESPACE + "");

	}

	@Override
	public boolean onTouch(View view, MotionEvent even) {
		// 按下的时候
		if (MotionEvent.ACTION_DOWN == even.getAction()) {
			switch (view.getId()) {
			// 登陆
			case R.id.system_login_btn:
				login.setBackgroundResource(R.drawable.buttom_select_press);
				break;
			// 取消
			case R.id.system_login_cancel:
				cancel.setBackgroundResource(R.drawable.buttom_select_press);
				break;
			}
		}

		// 手指松开的时候
		if (MotionEvent.ACTION_UP == even.getAction()) {
			switch (view.getId()) {
			// 登陆
			case R.id.system_login_btn:
				name = editname.getText().toString();
				pwd = editpwd.getText().toString();
				login.setBackgroundResource(R.drawable.buttom_selector_bg);
				// 非空验证
				if (isnull(name, pwd)) {
					// 有网络才可以执行登录操作
					Log.i("network", network + "");
					if (network) {
						// 提示
						try {
							managerClass.getRuning().runding(SystemLogin.this,
									"正在登录...");
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println(e.getMessage());
						}

						// 登陆方法
						getSystemLogin().login(name, pwd);
					} else {
						managerClass.getGolbalView().toastShow(this,
								"网络没有连通，无法登录");
						// Toast.makeText(this,"网络没有连通，无法登录",
						// Toast.LENGTH_LONG).show();
					}
				}

				break;
			// 取消
			case R.id.system_login_cancel:
				login.setBackgroundResource(R.drawable.buttom_select_press);
				SystemLogin.this.finish();
				break;

			}
			GolbalUtil.ismover = 0;
		}
		// 手指移动的时候
		if (MotionEvent.ACTION_MOVE == even.getAction()) {
			GolbalUtil.ismover++;
		}

		// 意外中断事件取消
		if (MotionEvent.ACTION_CANCEL == even.getAction()) {
			GolbalUtil.ismover = 0;
			switch (view.getId()) {
			// 登陆
			case R.id.system_login_btn:
				login.setBackgroundResource(R.drawable.buttom_selector_bg);
				break;
			// 取消
			case R.id.system_login_cancel:
				cancel.setBackgroundResource(R.drawable.buttom_selector_bg);
				break;
			}
		}

		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (name != null) {
			editor.putString("uid", name);
			editor.commit();
		}
		current = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*if(!"".equals(GApplication.user.getOrganizationId())){
			GApplication.user=null;
		}*/
		editpwd.setFocusable(false);
		editpwd.setFocusableInTouchMode(true);
		GApplication.loginJidouId="";
		GApplication.jigouid="";
	//	SharedPreferences preferences = this.getPreferences(0);
	//	String nametext = preferences.getString("uid", "");
	//	editname.setText(nametext);
		editname.setText("");
		editpwd.setText("");
	}

	// 非空验证
	public boolean isnull(String name, String pwd) {
		if (name == null || "".equals(name)) {
			managerClass.getGolbalView().toastShow(this, "用户名不能为空");
			return false;

		} else if (pwd == null || "".equals(pwd)) {
			managerClass.getGolbalView().toastShow(this, "密码不能为空");
			return false;
		}
		return true;
	}

	// 拦截Menu
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		new MenuShow().menu(this);
		MenuShow.pw.showAtLocation(findViewById(R.id.loginparent),
				Gravity.BOTTOM, 0, 0);
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("a");
		return true;
	}

	/*@Override
	  public void onAttachedToWindow() {
	      System.out.println("Page01 -->onAttachedToWindow");
	      this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
	      super.onAttachedToWindow();
	  }*/
	
	/* @Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
	      System.out.println("Page01 -->onKeyDown: keyCode: " + keyCode);
	      if (KeyEvent.KEYCODE_HOME == keyCode) {
	          System.out.println("HOME has been pressed yet ...");
	          // android.os.Process.killProcess(android.os.Process.myPid());
	          Toast.makeText(getApplicationContext(), "HOME 键已被禁用...",
	                  Toast.LENGTH_LONG).show();
	         
	      }
	      return super.onKeyDown(keyCode, event); // 不会回到 home 页面
	  }*/
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}
