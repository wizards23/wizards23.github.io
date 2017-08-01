package com.ljsw.tjbankpda.yy.activity;

import java.net.SocketTimeoutException;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.application.GApplication;
import com.entity.SystemUser;
import com.example.app.entity.User;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.ljsw.tjbankpda.db.service.SecondLogin;
import com.ljsw.tjbankpda.main.ZhouzhuanxiangMenu;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.yy.application.S_application;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.MenuShow;
import com.service.NetService;

/**
 * 押运员帐号密码登录界面
 * @author yuyunheng
 */
public class YayunDenglu extends Activity implements OnTouchListener {

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
	Handler handler_login; // 登录handels;
	private SystemUser loginUser = null; // 押运用户登录信息
	boolean network = true; // 是否有网络
	public static boolean current; // 当前界面
//	public boolean flag = true;

	// 记住用户名
	SharedPreferences share;
	Editor editor;
	private ManagerClass managerClass;

	private SecondLogin systemLogin;

	public SecondLogin getSystemLogin() {
		if (systemLogin == null) {
			systemLogin = new SecondLogin();
		}
		return systemLogin;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
					managerClass.getRuning().runding(YayunDenglu.this,
							"正在登录...");
					try {
						// 登陆方法
						Thread thread=new Thread(new YayunLogin(name, pwd));
						thread.start();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					// Toast.makeText(SystemLogin.this,"网络没有连通，无法登录",
					// Toast.LENGTH_LONG).show();
					managerClass.getGolbalView().toastShow(YayunDenglu.this,
							"网络没有连通，无法登录");
				}
			}
		};

		// Hand通知操作
		handler_login = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				managerClass.getRuning().remove();
				super.handleMessage(msg);

				switch (msg.what) {
				case 1:
					error = 3;
					if (GApplication.user != null
							&& !GApplication.user.getLoginUserId().equals("9")) {
						if (S_application.getApplication().s_userYayun == null) {
							managerClass.getAbnormal().timeout(
									YayunDenglu.this, "请使用押运员身份!",
									new View.OnClickListener() {
										@Override
										public void onClick(View arg0) {
											editname.setText("");
											editpwd.setText("");
											managerClass.getAbnormal().remove();
										}
									});
						}
					} else {
						if (S_application.getApplication().s_userYayun == null) {
							// 修改
							Intent intent = new Intent();
							intent.putExtra("isOk", "success");
							intent.putExtra("name", name);
							GApplication.use = new User();
							GApplication.use.setUserzhanghu(name);
							GApplication.use.setUsername(GApplication.user
									.getLoginUserName());
							YayunDenglu.this.setResult(
									YayunDenglu.this.RESULT_OK, intent);
							YayunDenglu.this.finish();
						} else if (S_application.getApplication().s_userYayun != null
								&& S_application.getApplication().s_userYayun.equals(name)) {
							// 修改
							Intent intent = new Intent();
							intent.putExtra("isOk", "success");
							intent.putExtra("name", name);
							GApplication.use = new User();
							GApplication.use.setUserzhanghu(name);
							GApplication.use.setUsername(GApplication.user
									.getLoginUserName());
							YayunDenglu.this.setResult(
									YayunDenglu.this.RESULT_OK, intent);
							YayunDenglu.this.finish();
						} else {
							System.out.println("s_userYayun:"+S_application.getApplication().s_userYayun);
							System.out.println("name:"+name);
							managerClass.getAbnormal().timeout(
									YayunDenglu.this, "请使用押运员本人登录!",
									new View.OnClickListener() {
										@Override
										public void onClick(View arg0) {
											editname.setText("");
											editpwd.setText("");
											managerClass.getAbnormal().remove();
										}
									});
						}

					}

					break;
				case 0:
					managerClass.getGolbalView().toastShow(YayunDenglu.this,S_application.wrong);
					break;
				case -4:
					managerClass.getAbnormal().timeout(YayunDenglu.this,
							"登陆超时，重新链接？", onclickreplace);
					break;
				case -1:
					managerClass.getAbnormal().timeout(YayunDenglu.this,
							"登录出现异常", onclickreplace);
					break;
				case -3:
					managerClass.getGolbalView().toastShow(YayunDenglu.this,
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
								managerClass.getRuning().runding(YayunDenglu.this,
										"正在登录...");
							} catch (Exception e) {
								e.printStackTrace();
								System.out.println(e.getMessage());
							}
							
							// 登陆方法
							Thread thread=new Thread(new YayunLogin(name, pwd));
							thread.start();
						} else {
							managerClass.getGolbalView().toastShow(this,
									"网络没有连通，无法登录");
						}
					}

					break;
				// 取消
				case R.id.system_login_cancel:
					login.setBackgroundResource(R.drawable.buttom_select_press);
					Intent intent = new Intent();
					intent.putExtra("isOk", "error");
					YayunDenglu.this.setResult(YayunDenglu.this.RESULT_OK, intent);				
					YayunDenglu.this.finish();
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
		S_application.wrong="";
		current = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences preferences = this.getPreferences(0);
		String nametext = preferences.getString("uid", "");
		editname.setText("");
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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == event.KEYCODE_BACK) {
			if(null!=S_application.wrong && S_application.wrong.equals("jiaojie")){
				YayunDenglu.this.finish();
			}else{
				Skip.skip(YayunDenglu.this, ZhouzhuanxiangMenu.class, null, 0);
				YayunDenglu.this.finish();
			}	
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 押运登录
	 * @author yuyunheng
	 * 
	 */
	private class YayunLogin implements Runnable {
		private String name;
		private String pwd;

		public YayunLogin(String name, String pwd) {
			this.name = name;
			this.pwd = pwd;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = handler_login.obtainMessage();
			try {
				if (("").equals(name) || ("").equals(pwd)) {
					msg.what = -3;   // 帐号或密码为空
				} else {
					loginUser = getSystemLogin().login(name, pwd);
					if (loginUser != null) { // 成功获取
						GApplication.user = loginUser;
						S_application.getApplication().s_yayunJigouId = GApplication.user
								.getOrganizationId();
						S_application.getApplication().s_userYayunName=GApplication.user.getLoginUserName();
						msg.what = 1;
					} else { // 未成功获取
						msg.what = 0;
					}
				}
			} catch (SocketTimeoutException ee) {
				// TODO: handle exception
				msg.what = -4;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg.what = -1;
			}
			
			handler_login.sendMessage(msg);
		}

	}
}
