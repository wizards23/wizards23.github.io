package com.ljsw.tjbankpda.main;

import java.net.SocketTimeoutException;

import hdjc.rfid.operator.RFID_Device;

import com.application.GApplication;
import com.entity.SystemUser;
import com.example.pda.R;
import com.imple.getnumber.GetFingerValue;
import com.ljsw.tjbankpda.db.activity.KuGuanLogin_db;
import com.ljsw.tjbankpda.db.activity.KuGuanYuanDengLu;
import com.ljsw.tjbankpda.db.activity.RenWuLieBiao_db;
import com.ljsw.tjbankpda.db.application.o_Application;
import com.ljsw.tjbankpda.db.service.YanZhengZhiWenService;
import com.ljsw.tjbankpda.qf.application.Mapplication;
import com.ljsw.tjbankpda.qf.entity.User;
import com.ljsw.tjbankpda.qf.service.QingfenRenwuService;
import com.ljsw.tjbankpda.util.Skip;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.biz.FingerCheckBiz;
import com.poka.device.ShareUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QingfenDengLuAcyivity extends Activity implements OnClickListener {
	// 指纹验证 左右姓名提示和底部提示
	private ImageView back, img_left, img_right;
	private TextView setName_left, setName_right, top_tishi, bottom_tishi;
	private SystemUser result_user;// 指纹验证
	public static boolean firstSuccess = false; // 第一位是否已成功验证指纹
	String f1 = ""; // 第一个按手指的人
	String f2 = ""; // 第二个按手指的人
	private String left_name, right_name;
	private OnClickListener OnClick1;
	private int wrongleftNum, wrongrightNum;// 指纹验证失败次数统计
	private Intent intent;
	private FingerCheckBiz fingerCheck;
	private ManagerClass manager;
	QingfenRenwuService qs = new QingfenRenwuService();
	private String xiaoleft = "",// 清分小组 左边
			xiaoright = "";// 清分小组 右边
	private boolean isFlag = true;

	FingerCheckBiz getFingerCheck() {
		return fingerCheck = fingerCheck == null ? new FingerCheckBiz()
				: fingerCheck;
	}

	private RFID_Device rfid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qingfendenglu);
		manager = new ManagerClass();
		intent = new Intent();
		// GApplication app = (GApplication) this.getApplicationContext();
		OnClick1 = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				manager.getAbnormal().remove();
				if (isFlag)
					yanzhengFinger();
			}
		};

	}

	@Override
	protected void onResume() {
		super.onResume();
		isFlag = true;
		load();
		getRfid().addNotifly(new GetFingerValue());
		new Thread(new Runnable() {
			@Override
			public void run() {
				getRfid().fingerOpen();
			}
		}).start();

		GetFingerValue.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle;
				if (msg.what == 1) {

					bundle = msg.getData();
					if (bundle != null
							&& bundle.getString("finger").equals("正在获取指纹特征值！")) {
						top_tishi.setText("正在验证指纹...");
					} else if (bundle != null
							&& bundle.getString("finger").equals("获取指纹特征值成功！")) {
						if (isFlag) {
							isFlag = false;
							yanzhengFinger();
						}

					}
				}
			}

		};
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				isFlag = true;
				manager.getRuning().remove();
				manager.getAbnormal().timeout(QingfenDengLuAcyivity.this,
						"验证超时,重试?", OnClick1);

				break;
			case 1:
				isFlag = true;
				manager.getRuning().remove();
				manager.getAbnormal().timeout(QingfenDengLuAcyivity.this,
						"网络连接失败,重试?", OnClick1);
				break;
			case 2:
				isFlag = true;
				// 第一位验证
				if (!firstSuccess && !"1".equals(f1)) {
					Mapplication.getApplication().user1 = result_user;
					xiaoleft = result_user.getXiaozu(); // 将左边用户的小组id 记录下来
					System.out
							.println("=============Mapplication.getApplication().user1=="
									+ Mapplication.getApplication().user1
											.getLoginUserName());
					img_left.setImageBitmap(ShareUtil.finger_qingfen_denglu_left);
					left_name = result_user.getLoginUserName();
					o_Application.left_user = result_user;
					o_Application.FingerQinfenLoginNum.add(result_user
							.getLoginUserId());
					setName_left.setText(left_name);
					f1 = "1";
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < ShareUtil.ivalBack.length; i++) {
						sb.append(ShareUtil.ivalBack[i]);
					}
					firstSuccess = true;
					bottom_tishi.setText("请第二位清分员按手指");
					top_tishi.setText("第一位验证成功");
					Mapplication.getApplication().UserId = Mapplication
							.getApplication().user1.getYonghuZhanghao();
					System.out.println("赋值1："+Mapplication.getApplication().UserId);
					// 第二位验证
				} else if (firstSuccess && !"2".equals(f2)) {
					Mapplication.getApplication().user2 = result_user;
					xiaoright = result_user.getXiaozu(); // 记录下右边用户的所属小组
					right_name = result_user.getLoginUserName();
					if (right_name != null && right_name.equals(left_name)) {
						top_tishi.setText("重复验证!");
					} else {
						top_tishi.setText("");
						o_Application.right_user = result_user;
						o_Application.FingerQinfenLoginNum.add(result_user
								.getLoginUserId());
						img_right
								.setImageBitmap(ShareUtil.finger_qingfen_denglu_right);
						setName_right.setText(right_name);
						f2 = "2";
						bottom_tishi.setText("验证成功！");
						firstSuccess = false;

						handler.sendEmptyMessage(4);
					}
				}
				break;
			case 3:
				isFlag = true;
				// 验证3次失败跳登录页面 未实现
				if (!"1".equals(f1)) {
					wrongleftNum++;
					top_tishi.setText("验证失败:" + wrongleftNum + "次");
				} else {
					wrongrightNum++;
					top_tishi.setText("验证失败:" + wrongrightNum + "次");
				}
				if (wrongleftNum >= ShareUtil.three) {
					// 左侧跳用户登录
					firstSuccess = false;
					intent.setClass(QingfenDengLuAcyivity.this,
							QingfenDengluByUserno.class);
					QingfenDengLuAcyivity.this
							.startActivityForResult(intent, 1);
					top_tishi.setText("");
					wrongleftNum = 0;
				} else if (wrongrightNum >= ShareUtil.three) {
					// 右侧跳用户登录
					firstSuccess = true;
					intent.setClass(QingfenDengLuAcyivity.this,
							QingfenDengluByUserno.class);
					QingfenDengLuAcyivity.this
							.startActivityForResult(intent, 1);
					wrongrightNum = 0;
					top_tishi.setText("");
				}
				break;
			case 4:
				isFlag = true;
				if (xiaoleft.equals(xiaoright)) {// 如果2者的小组id相同则 跳转
					Skip.skip(QingfenDengLuAcyivity.this,
							QingfenMenuActivity.class, null, 0);
					QingfenDengLuAcyivity.this.finish();
				} else {
					manager.getAbnormal().timeout(QingfenDengLuAcyivity.this,
							"两清分员不在同一组不能进行下一步的业务", new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									manager.getAbnormal().remove();
								}
							});

				}

				break;
			case 5:
				break;
			}
		}

	};

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		Bundle bundle = arg2.getExtras();
		String isOk = bundle.getString("isOk");
		if (isOk.equals("success")) {
			if (!f1.equals("1")) {
				Mapplication.getApplication().user1 = new SystemUser();
				f1 = "1";
				left_name = o_Application.qingfen.getLoginUserName();
				setName_left.setText(left_name);
				img_left.setImageResource(R.drawable.result_isok);
				firstSuccess = true;
				top_tishi.setText("第一位验证成功");
				bottom_tishi.setText("请第二位清分员按手指");
				o_Application.left_user = o_Application.qingfen;
				Mapplication.getApplication().user1 = o_Application.qingfen;
				System.out.println("Mapplication.getApplication().user1="
						+ Mapplication.getApplication().user1.getLoginUserId());
				System.out.println("左边登录完成 o_Application.left_user："
						+ o_Application.left_user.getLoginUserName());

				// 取出左边用户登录的所属小组
				xiaoleft = o_Application.qingfen.getXiaozu() == null ? ""
						: o_Application.qingfen.getXiaozu();
			} else {
				// 判断验证 用户是否重复
				right_name = o_Application.qingfen.getLoginUserName();
				if (right_name != null && right_name.equals(left_name)) {
					top_tishi.setText("重复验证!");
				} else {
					f2 = "2";
					f1 = "1";
					Mapplication.getApplication().user2 = new SystemUser();
					setName_right.setText(right_name);
					img_right.setImageResource(R.drawable.result_isok);
					firstSuccess = false;
					top_tishi.setText("");
					bottom_tishi.setText("验证成功！");
					o_Application.right_user = o_Application.qingfen;
					Mapplication.getApplication().user2 = o_Application.qingfen;
					System.out.println("Mapplication.getApplication().user2="
							+ Mapplication.getApplication().user2
									.getLoginUserName());

					// 取出右边登录用的所属小组
					xiaoright = o_Application.qingfen.getXiaozu() == null ? ""
							: o_Application.qingfen.getXiaozu();
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								Thread.sleep(1000);
								handler.sendEmptyMessage(4);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}).start();

				}
			}
		}
	}

	public void load() {
		setName_left = (TextView) findViewById(R.id.tv_qingfen_login_name1);
		setName_right = (TextView) findViewById(R.id.tv_qingfen_login__name2);
		bottom_tishi = (TextView) findViewById(R.id.tv_qingfen_login_message);
		back = (ImageView) findViewById(R.id.iv_qingfen_login_back);
		back.setOnClickListener(this);
		top_tishi = (TextView) findViewById(R.id.tv_qingfen_login_resultmsg);
		img_left = (ImageView) findViewById(R.id.iv_qingfen_login_finger_left);
		img_right = (ImageView) findViewById(R.id.iv_qingfen_login_finger_right);
	}

	/**
	 * 指纹验证
	 */
	public void yanzhengFinger() {
		new Thread() {
			@Override
			public void run() {
				super.run();
				YanZhengZhiWenService yanzheng = new YanZhengZhiWenService();
				System.out
						.println("QingfenDengLuAcyivity:" + GApplication.user);
				try {
					System.out.println("GApplication.user.getOrganizationId()"
							+ GApplication.user.getOrganizationId() + "/id="
							+ GApplication.user.getLoginUserId());
					result_user = yanzheng.checkFingerprint2(
							GApplication.user.getOrganizationId(),
							GApplication.user.getLoginUserId(),
							ShareUtil.ivalBack);
					// Thread.sleep(1000);
					if (result_user != null) {
						handler.sendEmptyMessage(2);
					} else {
						System.out.println("aaaaaaaaa");
						handler.sendEmptyMessage(3);
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(0);
				} catch (Exception e1) {
					e1.printStackTrace();
					handler.sendEmptyMessage(1);
				}
			}

		}.start();
	}

	String params;

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_qingfen_login_back:
			QingfenDengLuAcyivity.this.finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isFlag = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		o_Application.left_user = null;
		o_Application.right_user = null;
		f1 = "";
		f2 = "";
		firstSuccess = false;
		for (int i = 0; i < o_Application.FingerQinfenLoginNum.size(); i++) {
			// System.out.println(o_Application.FingerQinfenLoginNum.get(i));
		}
		// getRfid().fingerClose();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			QingfenDengLuAcyivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}

}
