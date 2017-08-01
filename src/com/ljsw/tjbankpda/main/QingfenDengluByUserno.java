package com.ljsw.tjbankpda.main;

import java.net.SocketTimeoutException;

import com.application.GApplication;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.ljsw.tjbankpda.db.application.o_Application;
import com.ljsw.tjbankpda.db.service.SecondLogin;
import com.ljsw.tjbankpda.qf.application.Mapplication;
import com.ljsw.tjbankpda.yy.application.S_application;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.MenuShow;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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

/**
 * 指纹验证失败3次后，跳转账号登陆界面。
 * @作者 殴昀
 * @描述 使用回传值的方式，把信息传回指纹验证界面
 */
public class QingfenDengluByUserno extends Activity implements OnTouchListener {
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
				managerClass.getRuning().runding(QingfenDengluByUserno.this,
						"返回中...");
				login.setBackgroundResource(R.drawable.buttom_select_press);
				intent.putExtra("isOk", "error");
				QingfenDengluByUserno.this.setResult(
						QingfenDengluByUserno.this.RESULT_OK, intent);
				QingfenDengluByUserno.this.finish();
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
		managerClass.getRuning().runding(QingfenDengluByUserno.this, "登录中...");
		new Thread() {
			@Override
			public void run() {
				super.run();
				SecondLogin login = new SecondLogin();
				try {
					o_Application.qingfen = login.login(name, pwd);
					if (o_Application.qingfen != null) {
						Mapplication.getApplication().UserId = name;
						System.out.println("赋值2："+Mapplication.getApplication().UserId);
						String fristLoginUserId = o_Application.qingfen.getLoginUserId();
						if ("7".equals(fristLoginUserId) || "17".equals(fristLoginUserId)) {
							handler.sendEmptyMessage(3);   //是清分员身份
						} else {
							handler.sendEmptyMessage(2); // 身份不复合
						}
					} else {
						handler.sendEmptyMessage(-1);
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
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				managerClass.getRuning().remove();		
				managerClass.getResultmsg().resultmsg(
							QingfenDengluByUserno.this, S_application.wrong, false);					
				break;
			case 0:
				managerClass.getRuning().remove();
				managerClass.getAbnormal().timeout(QingfenDengluByUserno.this,
						"登录超时,重试?", onclickreplace);
				break;
			case 1:
				managerClass.getRuning().remove();
				managerClass.getAbnormal().timeout(QingfenDengluByUserno.this,
						"网络连接失败,重试?", onclickreplace);
				break;
			case 2:
				managerClass.getRuning().remove();
				managerClass.getAbnormal().timeout(QingfenDengluByUserno.this,
						"请使用清分员或清分管理员身份!", new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								editname.setText("");
								editpwd.setText("");
								managerClass.getAbnormal().remove();
							}
						});

				break;
			case 3:
				managerClass.getRuning().remove();
				if (o_Application.left_user != null) {
					System.out.println("原登录名:"+o_Application.qingfen.getLoginUserName());
					System.out.println("现登录名:"+o_Application.left_user.getLoginUserName());
					System.out.println("现111登录名:"+name);
					if (o_Application.qingfen.getLoginUserName().equals(o_Application.left_user.getLoginUserName())) {
						managerClass.getAbnormal().timeout(
								QingfenDengluByUserno.this, "重复验证!",
								new View.OnClickListener() {
									@Override
									public void onClick(View arg0) {
										managerClass.getAbnormal().remove();
									}
								});
					} else {
						intent.putExtra("isOk", "success");
						o_Application.FingerQinfenLoginNum.add(name);
						QingfenDengluByUserno.this.setResult(
								QingfenDengluByUserno.this.RESULT_OK, intent);
						QingfenDengluByUserno.this.finish();
					}
				} else {
					intent.putExtra("isOk", "success");
					o_Application.FingerQinfenLoginNum.add(name);
					QingfenDengluByUserno.this.setResult(
							QingfenDengluByUserno.this.RESULT_OK, intent);
					QingfenDengluByUserno.this.finish();
				}

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
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			managerClass.getRuning().runding(QingfenDengluByUserno.this,
					"返回中...");
			login.setBackgroundResource(R.drawable.buttom_select_press);
			intent.putExtra("isOk", "error");
			QingfenDengluByUserno.this.setResult(
					QingfenDengluByUserno.this.RESULT_OK, intent);
			QingfenDengluByUserno.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		managerClass.getRuning().remove();
	}
}
