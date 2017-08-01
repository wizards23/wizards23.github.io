package com.ljsw.tjbankpda.yy.activity;

import hdjc.rfid.operator.RFID_Device;

import java.net.SocketTimeoutException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.GApplication;
import com.example.app.activity.YayunJiaojieActivity;
import com.example.app.entity.User;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.imple.getnumber.GetFingerValue;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.yy.application.S_application;
import com.ljsw.tjbankpda.yy.service.IPdaOfBoxOperateService;
import com.manager.classs.pad.ManagerClass;
import com.poka.device.ShareUtil;

/**
 * 押运员单人登录页面
 * 
 * @author Administrator
 */
public class YayunLoginSAcitivity extends Activity implements OnClickListener {
	private TextView top, fname, bottom;// 顶部提示 指纹对应人员姓名 底部提示
	private ImageView finger;// 指纹图片
	private ManagerClass managerClass;
	public User result_user;
	public Handler handler;
	private Intent intent;
	private int fingerCount;// 验证指纹失败的次数
	private boolean isFlag = true;

	private RFID_Device rfid;

	RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}


	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yayun_login_s);
		managerClass = new ManagerClass();
		intent = new Intent();
		initView();
		managerClass.getRfid().addNotifly(new GetFingerValue()); // 添加通知
		managerClass.getRfid().fingerOpen(); // 打开指纹

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				isFlag = true;
				switch (msg.what) {
				case 1:// 验证成功跳转
					/*
					 * revised by zhangxuewei
					 * 2016-8-25	
					 * add frame confirm
					 */
					finger.setImageBitmap(ShareUtil.finger_gather);
					fname.setText(result_user.getUsername());
					managerClass.getSureCancel().makeSuerCancel2(
							YayunLoginSAcitivity.this, "押运员："+result_user.getUsername(),
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getRuning().runding(YayunLoginSAcitivity.this,
											"即将请稍后...");
									// 跳转下一个页面
									Skip.skip(YayunLoginSAcitivity.this,
											YayunRwLbSActivity.class, null, 0);
								}
							}, false);
					break;
				case -1:
					// top.setText("验证异常，请重按");

					fingerCount++;
					top.setText("验证失败" + fingerCount + "次，请重按");
					if (fingerCount >= ShareUtil.three) {
						// 跳用户登录
						intent.setClass(YayunLoginSAcitivity.this,
								YayunDenglu.class);
						YayunLoginSAcitivity.this.startActivityForResult(
								intent, 1);
						top.setText("");
						fingerCount = 0;
					}
					break;
				case -4:

					top.setText("验证超时，请重按");
					break;
				case 0:

					fingerCount++;
					top.setText("验证失败" + fingerCount + "次，请重按");
					if (fingerCount >= ShareUtil.three) {
						// 跳用户登录
						intent.setClass(YayunLoginSAcitivity.this,
								YayunDenglu.class);
						YayunLoginSAcitivity.this.startActivityForResult(
								intent, 1);
						top.setText("");
						fingerCount = 0;
					}
					break;
				case 10:
					Skip.skip(YayunLoginSAcitivity.this, YayunRwLbSActivity.class,null, 0);
					break;
				}
			}
		};

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		isFlag = true;
		// 获得指纹通知
		GetFingerValue.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				Bundle bundle;

				if (msg.what == 1) {
					bundle = msg.getData();
					if (bundle != null
							&& bundle.getString("finger").equals("正在获取指纹特征值！")) {

					} else if (bundle != null
							&& bundle.getString("finger").equals("获取指纹特征值成功！")) {
						if (isFlag) {
							top.setText("正在验证指纹...");
							isFlag = false;
							// 开始调用服务器验证指纹
							CheckFingerThread cf = new CheckFingerThread();
							cf.start();
						}
					}
				}
			}
		};
	}

	public void initView() {
		fname = (TextView) findViewById(R.id.yy_fname);
		finger = (ImageView) this.findViewById(R.id.yy_image);
		top = (TextView) this.findViewById(R.id.yy_top);
		bottom = (TextView) this.findViewById(R.id.ll_yayun_bottom);
		// bottom.setOnClickListener(this);
	}

	/**
	 * 指纹验证线程
	 * 
	 * @author Administrator
	 */
	class CheckFingerThread extends Thread {
		Message m;

		public CheckFingerThread() {
			super();
			m = handler.obtainMessage();
		}

		@Override
		public void run() {
			super.run();
			IPdaOfBoxOperateService yz = new IPdaOfBoxOperateService();
			try {
				// 押运员的机构id默认 等于最开始登录用户的机构id
				S_application.getApplication().s_yayunJigouId = GApplication.user
						.getOrganizationId(); 
				System.out.println("GApplication.user.getLoginUserId():" + 9);
				result_user = yz.checkFingerprint(
						S_application.getApplication().s_yayunJigouId,
						GApplication.user.getLoginUserId(), ShareUtil.ivalBack);
				System.out.println("yyl============"
						+ GApplication.user.getLoginUserId());
				System.out.println("yyl============"
						+ GApplication.user.getOrganizationId());
				if (result_user != null) {// 验证成功
					GApplication.use = result_user;
					S_application.getApplication().s_userYayun = result_user
							.getUserzhanghu();
					m.what = 1;
				} else {
					m.what = 0;
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				m.what = -4;// 超时验证
			} catch (Exception e) {
				e.printStackTrace();
				m.what = -1;// 验证异常
			} finally {
				handler.sendMessage(m);
				GolbalUtil.onclicks = true;
			}
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getRfid().close_a20();
		isFlag = false;
		managerClass.getRuning().remove();
	}

	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ll_yayun_bottom:

			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			Bundle bundle = data.getExtras();
			String isOk = bundle.getString("isOk");
			if (isOk.equals("success")) {
				fname.setText(GApplication.use.getUsername());
				finger.setImageResource(R.drawable.result_isok);
				bottom.setText("验证成功!");
				if (bundle.getString("name") != null
						&& !bundle.getString("name").equals("")) {
					S_application.getApplication().s_userYayun = bundle
							.getString("name");
				}
				// 跳转下一个页面
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(1000);
							handler.sendEmptyMessage(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			YayunLoginSAcitivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
