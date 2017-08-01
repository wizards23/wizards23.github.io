package com.example.app.activity;

import org.ksoap2.SoapFault;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.entity.User;
import com.example.app.util.Skip;
import com.example.pda.R;
import com.o.service.LoginService;

/**
 * 系统登录
 * 
 * @author yuyunheng
 * 
 */
public class XitongDengluActivity extends Activity {
	private EditText name, pwd;
	private String loginName, loginPwd;
	private Button queding, quxiao; // 确定按钮 取消
	private User logininfo;
	private String jigouid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_systemlogin);
		load();
		ShijianChuli shijianchuli = new ShijianChuli();
		queding.setOnClickListener(shijianchuli);
	}

	/**
	 * 控件初始化
	 */
	private void load() {
		queding = (Button) findViewById(R.id.system_login_btn);
		quxiao = (Button) findViewById(R.id.system_login_cancel);
		name = (EditText) findViewById(R.id.login_name);
		pwd = (EditText) findViewById(R.id.login_pwd);
	}

	/**
	 * 事件处理类
	 * 
	 * @author yuyunheng
	 * 
	 */
	private class ShijianChuli implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.system_login_btn:
				// Skip.skip(XitongDengluActivity.this,XianchaoGuanliActivity.class,
				// null, 0);
				// 登录验证
				loginName = name.getText().toString();
				loginPwd = pwd.getText().toString();
				if ((TextUtils.isEmpty(loginName)) || (TextUtils.isEmpty(loginPwd))) {
					Toast.makeText(XitongDengluActivity.this, "帐号密码不能为空", 4).show();
				} else {
					new Thread() {
						@Override
						public void run() {
							super.run();
							LoginService loginService = new LoginService();
							try {
								logininfo = loginService.Login(loginName,
										loginPwd);
								//System.out.println(logininfo);
								jigouid = logininfo.getJigouid();
							} catch (SoapFault e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.start();
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (logininfo != null) {
						Bundle bundle = new Bundle();
						bundle.putString("jigouid", jigouid);
						//System.out.println("登录页面的机构id"+jigouid);
						Skip.skip(XitongDengluActivity.this,XianchaoGuanliActivity.class, bundle, 0);
					} else {
						Toast.makeText(XitongDengluActivity.this, "帐号密码错误", 4).show();
					}

				}
				break;

			case R.id.system_login_cancel:
				break;
			}

		}
	}

}
