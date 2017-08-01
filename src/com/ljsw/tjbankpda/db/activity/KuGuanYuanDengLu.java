package com.ljsw.tjbankpda.db.activity;

import java.net.SocketTimeoutException;

import com.application.GApplication;
import com.entity.SystemUser;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.ljsw.tjbankpda.db.application.o_Application;
import com.ljsw.tjbankpda.db.service.SecondLogin;
import com.ljsw.tjbankpda.yy.application.S_application;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.MenuShow;
import com.service.FixationValue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class KuGuanYuanDengLu extends Activity implements OnTouchListener {
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
	private Intent intent;
	String space; // 空间
	String webservice; // webservice地址
	int error = 3;
	boolean network = true; // 是否有网络
	public static boolean current; // 当前界面
	public static SystemUser lingshiUser;//临时库管员信息存放 
	// 记住用户名
	SharedPreferences share;
	Editor editor;
	private ManagerClass managerClass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c_login_system);
		managerClass = new ManagerClass();
		intent = new Intent();
		login = (Button) findViewById(R.id.system_login_btn);
		cancel = (Button) findViewById(R.id.system_login_cancel);
		editname = (EditText) findViewById(R.id.name);
		editpwd = (EditText) findViewById(R.id.pwd);
		textlogin = (TextView) findViewById(R.id.netmsgtext);
		login.setOnTouchListener(this);
		cancel.setOnTouchListener(this);

		// 重试单击事件
		onclickreplace = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				login();
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

	}

	@SuppressWarnings("static-access")
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
				// 调用登录方法
				if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
					login();
				} else {
					managerClass.getAbnormal().timeout(this, "帐号密码不允许为空!",
							new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									managerClass.getAbnormal().remove();
								}
							});
				}
				break;
			// 取消
			case R.id.system_login_cancel:
				login.setBackgroundResource(R.drawable.buttom_select_press);
				intent.putExtra("isOk", "error");
				KuGuanYuanDengLu.this.setResult(
						KuGuanYuanDengLu.this.RESULT_OK, intent);
				KuGuanYuanDengLu.this.finish();
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

	public void login() {
		managerClass.getRuning().runding(KuGuanYuanDengLu.this, "登录中...");
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					lingshiUser = new SecondLogin().login(name, pwd);
					if (lingshiUser!=null) {
						handler.sendEmptyMessage(2);
					} else {
						handler.sendEmptyMessage(3);
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(1);
				}
			}

		}.start();
	}

	private Handler handler = new Handler() {

		@SuppressWarnings("static-access")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				managerClass.getRuning().remove();
				managerClass.getAbnormal().timeout(KuGuanYuanDengLu.this,
						"登录超时,重试?", onclickreplace);
				break;
			case 1:
				managerClass.getRuning().remove();
				managerClass.getAbnormal().timeout(KuGuanYuanDengLu.this,
						"网络连接失败,重试?", onclickreplace);
				break;

			case 2:
				managerClass.getRuning().remove();
				if (!lingshiUser.getLoginUserId().equals("4")&&!lingshiUser.getLoginUserId().equals(FixationValue.waibaoQingfenString)) {
					managerClass.getAbnormal().timeout(KuGuanYuanDengLu.this,
							"请使用库管员或清分管理员身份!", new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									editname.setText("");
									editpwd.setText("");
									managerClass.getAbnormal().remove();
								}
							});
				} else {
					 if(!GApplication.loginJidouId.equals(lingshiUser.getOrganizationId())){
							managerClass.getAbnormal().timeout(KuGuanYuanDengLu.this,
									"请使用本机构人员身份!", new View.OnClickListener() {
										@Override
										public void onClick(View arg0) {
											editname.setText("");
											editpwd.setText("");
											managerClass.getAbnormal().remove();
										}
									});
						}else {
							if (o_Application.left_user != null) {
								if (lingshiUser.getLoginUserName().equals(
										o_Application.left_user.getLoginUserName())) {
									managerClass.getAbnormal().timeout(
											KuGuanYuanDengLu.this, "重复验证!",
											new View.OnClickListener() {
												@Override
												public void onClick(View arg0) {
													managerClass.getAbnormal().remove();
												}
											});
								} else {
									intent.putExtra("isOk", "success");
									o_Application.FingerLoginNum.add(name);
									KuGuanYuanDengLu.this.setResult(
											KuGuanYuanDengLu.this.RESULT_OK, intent);
									KuGuanYuanDengLu.this.finish();
								}
							} else {
								intent.putExtra("isOk", "success");
								o_Application.FingerLoginNum.add(name);
								KuGuanYuanDengLu.this.setResult(
										KuGuanYuanDengLu.this.RESULT_OK, intent);
								KuGuanYuanDengLu.this.finish();
							}
						}
				}
					
				
				break;
			case 3:
				managerClass.getRuning().remove();
				managerClass.getAbnormal().timeout(KuGuanYuanDengLu.this,
						S_application.wrong, new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								editname.setText("");
								editpwd.setText("");
								managerClass.getAbnormal().remove();
								editname.setFocusable(true);   
								editname.setFocusableInTouchMode(true);   
								editname.requestFocus();
							}
						});
				
				break;
			default:
				break;
			}
		}

	};

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

}
