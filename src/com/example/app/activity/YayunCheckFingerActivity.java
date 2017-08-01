package com.example.app.activity;

import com.application.GApplication;
import com.example.app.entity.User;
import com.example.app.entity.UserInfo;
import com.example.app.util.Skip;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.loginsystem.biz.SystemLoginBiz;
import com.main.pda.SystemLogin;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.MenuShow;
import com.poka.device.ShareUtil;
import com.service.NetService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class YayunCheckFingerActivity extends Activity implements
		OnTouchListener {
	Button login; // 登陆按钮
	Button cancel; // 取消按钮
	EditText editname; // 用户名输入框 帐号
	EditText editpwd; // 密码输入框 密码

	TextView textlogin; // 网络状态提示
	String name = null; // 用户名
	String pwd = null; // 密码
	OnClickListener onclickreplace;
	SharedPreferences sharepre;
	String space; // 空间
	String webservice; // webservice地址
	int error = 3;

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
		setContentView(R.layout.activity_yayun_checkfinger);
		// 全局异常处理
		managerClass = new ManagerClass();
		login = (Button) findViewById(R.id.yayun_login_btn1);
		cancel = (Button) findViewById(R.id.yayun_login_cancel1);
		editname = (EditText) findViewById(R.id.yayun_name1);
		editpwd = (EditText) findViewById(R.id.yayun_pwd1);
		textlogin = (TextView) findViewById(R.id.netmsgtext1);
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
					managerClass.getRuning().runding(
							YayunCheckFingerActivity.this, "正在登录...");
					// System.out.println("账号"+name +"密码"+pwd);
					getSystemLogin().login(name, pwd);
				} else {
					// Toast.makeText(SystemLogin.this,"网络没有连通，无法登录",
					// Toast.LENGTH_LONG).show();
					managerClass.getGolbalView().toastShow(
							YayunCheckFingerActivity.this, "网络没有连通，无法登录");
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

					Intent intent = getIntent();
					Bundle bundle = intent.getExtras();
					if (bundle != null) {
						UserInfo u = new UserInfo(name, pwd);
						GApplication.userInfo = u;// 保存押运员的账号和密码
						// 把登陆后的押运员的姓名传过去；
						bundle.putString("name",
								GApplication.user.getLoginUserName());
						intent.putExtras(bundle);
						String flag = bundle.getString("FLAG");
						if (flag.equals("chuku")) {// 跳到出入库押运员处的验证
							managerClass.getRuning().runding(
									YayunCheckFingerActivity.this,
									"正在验证用户名和密码...");
							Skip.skip(YayunCheckFingerActivity.this,
									YayunJiaojieActivity.class, bundle, 0);
						} else if (flag.equals("jiaojie")) {// 跳到款箱交接押运员处的验证
							managerClass.getRuning().runding(
									YayunCheckFingerActivity.this,
									"正在验证用户名和密码...");
							Skip.skip(YayunCheckFingerActivity.this,
									KuanXiangJiaoJieYaYunActivity.class,
									bundle, 0);
						}
					}

					break;
				case 0:
					// error--;
					// if (error <= 0) {
					// managerClass.getGolbalView().toastShow(
					// YayunCheckFingerActivity.this, "连续错误3次以上！帐号已被锁定");
					// } else {
					// managerClass.getGolbalView()
					// .toastShow(YayunCheckFingerActivity.this,
					// "用户或密码不正确！还有" + error + "次机会");
					// }
					if (msg.obj != null) {
						managerClass.getGolbalView().toastShow(
								YayunCheckFingerActivity.this,
								msg.obj.toString());
					} else {
						managerClass.getGolbalView().toastShow(
								YayunCheckFingerActivity.this, "");
					}
					break;
				case -4:
					managerClass.getAbnormal().timeout(
							YayunCheckFingerActivity.this, "登陆超时，重新链接？",
							onclickreplace);
					break;
				case -1:
					managerClass.getAbnormal().timeout(
							YayunCheckFingerActivity.this, "登录出现异常",
							onclickreplace);
					break;
				case -3:
					managerClass.getGolbalView().toastShow(
							YayunCheckFingerActivity.this, "用户或密码为空！");
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
			case R.id.yayun_login_btn1:
				login.setBackgroundResource(R.drawable.buttom_select_press);
				break;
			// 取消
			case R.id.yayun_login_cancel1:
				cancel.setBackgroundResource(R.drawable.buttom_select_press);
				break;
			}
		}

		// 手指松开的时候
		if (MotionEvent.ACTION_UP == even.getAction()) {
			switch (view.getId()) {
			// 登陆
			case R.id.yayun_login_btn1:
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
							managerClass.getRuning().runding(
									YayunCheckFingerActivity.this, "正在登录...");
						} catch (Exception e) {
							e.printStackTrace();
							// System.out.println(e.getMessage());
						}
						// //System.out.println("开始调用登陆的方法========"+"checkFingerprint");
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
			case R.id.yayun_login_cancel1:
				login.setBackgroundResource(R.drawable.buttom_select_press);
				// YayunCheckFingerActivity.this.finish();
				editname.setText("");
				editpwd.setText("");
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
			case R.id.yayun_login_btn1:
				login.setBackgroundResource(R.drawable.buttom_selector_bg);
				break;
			// 取消
			case R.id.yayun_login_cancel1:
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
		SharedPreferences preferences = this.getPreferences(0);
		String nametext = preferences.getString("uid", "");
		editpwd.setFocusable(false);
		editpwd.setFocusableInTouchMode(true);
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
	protected void onStop() {
		super.onStop();
		managerClass.getRuning().remove();
		YayunCheckFingerActivity.this.finish();
	}

	// login.setOnClickListener(this);
}
