package com.ljsw.tjbankpda.yy.activity;

import java.net.SocketTimeoutException;

import hdjc.rfid.operator.RFID_Device;

import com.application.GApplication;
import com.entity.SystemUser;
import com.example.pda.R;
import com.imple.getnumber.GetFingerValue;
import com.ljsw.tjbankpda.db.service.YanZhengZhiWenService;
import com.ljsw.tjbankpda.util.BianyiType;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.yy.application.S_application;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.biz.FingerCheckBiz;
import com.poka.device.ShareUtil;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 押运任务交接,网点人员(库管员)验证指纹页面
 * 
 * @author 石锚
 * 
 */
public class YyrwJiaojieActivity extends FragmentActivity implements
		OnClickListener {

	// 左右姓名提示和底部提示
	private TextView setName_left, setName_right, prompt, yy_fingerTop, top;
	private ImageView img_left, img_right;
	private SystemUser result_user;// 指纹验证
	public static boolean firstSuccess = false; // 第一位是否已成功验证指纹
	String f1 = null; // 第一个按手指的人
	String f2 = null; // 第二个按手指的人
	StringBuffer f1AndF2 = new StringBuffer();
	private FingerCheckBiz fingerCheck;
	private ManagerClass manager;
	private OnClickListener OnClick, OnClick2;
	private String left_name, right_name;
	private int wrongleftNum, wrongrightNum;// 指纹验证失败次数统计
	private Intent intent;
	private Handler handler;
	private TextView userTxt1;
	private TextView userTxt2;
	private boolean isFlag = true;

	private String jiaosheId;

	FingerCheckBiz getFingerCheck() {
		return fingerCheck = fingerCheck == null ? new FingerCheckBiz()
				: fingerCheck;
	}

	private RFID_Device rfid;

	RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}

	/**
	 * 080114  080107   98223
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yy_jiaojie_s);
		manager = new ManagerClass();
		intent = new Intent();

		userTxt1 = (TextView) this.findViewById(R.id.username11);
		userTxt2 = (TextView) this.findViewById(R.id.username22);
		prompt = (TextView) this.findViewById(R.id.yanshi);
		initView();
		OnClick = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				manager.getAbnormal().remove();
				if (isFlag) {
					isFlag = false;
					YanZhenFinger yf = new YanZhenFinger();
					yf.start();
				}

			}
		};

		OnClick2 = new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				manager.getAbnormal().remove();
			}
		};

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				isFlag = true;
				switch (msg.what) {
				case 1:
					// 第一位验证
					if (!firstSuccess && !"1".equals(f1)) {
						img_left.setImageBitmap(ShareUtil.finger_wangdian_left);
						left_name = result_user.getLoginUserName();
						ShareUtil.zhiwenid_left = result_user.getLoginUserId();

						setName_left.setText(left_name);
						f1 = "1";

						StringBuffer sb = new StringBuffer();
						for (int i = 0; i < ShareUtil.ivalBack.length; i++) {
							sb.append(ShareUtil.ivalBack[i]);
						}
						firstSuccess = true;
						yy_fingerTop.setText("第一位验证成功");
						if (S_application.getApplication().jiaojieType == 1) {
							prompt.setText("请第二位库管员按手指...");
						} else {
							System.out.println("2015-11-09:hander");
							prompt.setText("请第二位网点人员按手指...");
						}
						// 第二位验证
					} else if (firstSuccess && !"2".equals(f2)) {
						right_name = result_user.getLoginUserName();
						if (right_name != null && right_name.equals(left_name)) {
							System.out.println("重复验证了吗？");
							yy_fingerTop.setText("重复验证!");
						} else {
							yy_fingerTop.setText("");
							img_right
									.setImageBitmap(ShareUtil.finger_wangdian_right);
							ShareUtil.zhiwenid_right = result_user
									.getLoginUserId();
							// System.out.println("右边指纹特征值："+ShareUtil.finger_bitmap_right);
							setName_right.setText(right_name);
							f2 = "2";
							f1AndF2.append(ShareUtil.zhiwenid_left
									+ BianyiType.xiahuaxian
									+ ShareUtil.zhiwenid_right);
							S_application.getApplication().s_userWangdian = f1AndF2
									.toString();
							prompt.setText("验证成功！");
							top.setText("");
							firstSuccess = false;
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
					break;

				case -1:
					manager.getAbnormal().timeout(YyrwJiaojieActivity.this,
							"网络连接失败,重试?", OnClick);
					break;
				case -4:
					manager.getAbnormal().timeout(YyrwJiaojieActivity.this,
							"验证超时,重试?", OnClick);
					break;
				case 0:
					// 验证3次失败跳登录页面 未实现
					if (f1 == null) {
						wrongleftNum++;
						yy_fingerTop.setText("验证失败:" + wrongleftNum + "次");
					} else if (f2 == null) {
						wrongrightNum++;
						yy_fingerTop.setText("验证失败:" + wrongrightNum + "次");
					} else {
						manager.getAbnormal().timeout(YyrwJiaojieActivity.this,
								"验证已完成!", new View.OnClickListener() {
									@Override
									public void onClick(View arg0) {
										manager.getAbnormal().remove();
										yy_fingerTop.setText("");
										return;
									}
								});
					}
					if (wrongleftNum >= ShareUtil.three) {
						// 左侧跳用户登录
						firstSuccess = false;
						intent.setClass(YyrwJiaojieActivity.this,
								YayunDoubleLogin.class);
						YyrwJiaojieActivity.this.startActivityForResult(intent,
								1);
						yy_fingerTop.setText("");
						wrongleftNum = 0;
					} else if (wrongrightNum >= ShareUtil.three) {
						// 右侧跳用户登录
						firstSuccess = true;
						intent.setClass(YyrwJiaojieActivity.this,
								YayunDoubleLogin.class);
						YyrwJiaojieActivity.this.startActivityForResult(intent,
								1);
						wrongrightNum = 0;
						yy_fingerTop.setText("");
					}
					break;
				case 4:
					if (f1 != null && f2 != null) {
						Skip.skip(YyrwJiaojieActivity.this,
								YyrwJjYanzhengActivity.class, null, 0);
					}
					break;
				}
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();

		getRfid().addNotifly(new GetFingerValue());
		getRfid().fingerOpen();
		isFlag = true;
		GetFingerValue.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
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
							YanZhenFinger yf = new YanZhenFinger();
							yf.start();
						}
					}
				}
			}
		};
	}

	public void initView() {
		if (S_application.getApplication().jiaojieType == 1) {
			userTxt1.setText("库管员");
			userTxt2.setText("库管员");
			prompt.setText("请第一位库管员按手指...");
			jiaosheId = "4";
		} else {
			userTxt1.setText("网点人员");
			userTxt2.setText("网点人员");
			prompt.setText("请第一位网点人员按手指...");
			jiaosheId = ShareUtil.WdId;
		}

		img_left = (ImageView) this.findViewById(R.id.yyjj_left);
		img_right = (ImageView) this.findViewById(R.id.yyjj_right);
		yy_fingerTop = (TextView) this.findViewById(R.id.yanshi);
		top = (TextView) findViewById(R.id.textViewtop);
		setName_left = (TextView) this.findViewById(R.id.yy_leftName);
		setName_right = (TextView) this.findViewById(R.id.yy_rightName);
		findViewById(R.id.yayun_backS5).setOnClickListener(this);
		findViewById(R.id.yanshi).setOnClickListener(this);
	}

	/**
	 * 指纹验证线程
	 * 
	 * @author Administrator
	 */
	class YanZhenFinger extends Thread {
		Message m;

		public YanZhenFinger() {
			super();
			m = handler.obtainMessage();
		}

		@Override
		public void run() {
			super.run();
			YanZhengZhiWenService yanzheng = new YanZhengZhiWenService();
			System.out.println("网点机构:"
					+ S_application.getApplication().wangdianJigouId);
			System.out.println("网点角色ID" + jiaosheId);
			System.out.println("网点指纹特征值:" + ShareUtil.ivalBack);
			try {
				result_user = yanzheng.checkFingerprint(
						S_application.getApplication().wangdianJigouId,
						jiaosheId,// 网点人员角色Id
						ShareUtil.ivalBack);

				if (result_user != null) {
					System.out.println("我是帐号吗?" + result_user.getLoginUserId());
					m.what = 1;
				} else {
					m.what = 0;
				}
			} catch (SocketTimeoutException e) {
				// TODO: handle exception
				e.printStackTrace();
				m.what = -4;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				m.what = -1;
			} finally {
				handler.sendMessage(m);
			}
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		top.setText("");
		if (null == f1) {
			firstSuccess = false;
			if (S_application.getApplication().jiaojieType == 1) {
				prompt.setText("请第一位库管员按手指...");
			} else {
				prompt.setText("请第一位网点人员按手指...");
			}
		} else {
			if (S_application.getApplication().jiaojieType == 1) {
				prompt.setText("请第二位库管员按手指...");
			} else {
				prompt.setText("请第二位网点人员按手指...");
			}
		}
		if (arg2 != null) {
			Bundle bundle = arg2.getExtras();
			String isOk = bundle.getString("isOk");
			String name = bundle.getString("name");
			if (isOk.equals("success") && firstSuccess == false) {
				f1 = "1";
				left_name = GApplication.user.getLoginUserName();
				setName_left.setText(left_name);
				img_left.setImageResource(R.drawable.result_isok);
				firstSuccess = true;
				top.setText("第一位验证成功");
				ShareUtil.zhiwenid_left = name;
				if (S_application.getApplication().jiaojieType == 1) {
					prompt.setText("请第二位库管员按手指...");
				} else {
					System.out.println("2015-11-09:hander2");
					prompt.setText("请第二位网点人员按手指...");
				}
				S_application.getApplication().left_user = GApplication.user;
			} else if (isOk.equals("success") && firstSuccess == true) {
				f2 = "2";
				right_name = GApplication.user.getLoginUserName();
				if (right_name != null && right_name.equals(left_name)) {
					yy_fingerTop.setText("重复验证!");
				} else {
					setName_right.setText(right_name);
					img_right.setImageResource(R.drawable.result_isok);
					firstSuccess = false;
					yy_fingerTop.setText("");
					ShareUtil.zhiwenid_right = name;
					prompt.setText("验证成功！");
					S_application.getApplication().right_user = GApplication.user;
					f1AndF2.append(ShareUtil.zhiwenid_left
							+ BianyiType.xiahuaxian + ShareUtil.zhiwenid_right);
					S_application.getApplication().s_userWangdian = f1AndF2
							.toString();
					if (f1 != null && f2 != null) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									Thread.sleep(2000);
									handler.sendEmptyMessage(4);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).start();
					} else {
						manager.getAbnormal().timeout(this, "身份验证未通过,请完成验证!",
								OnClick2);
					}
				}
			}
		}

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.yayun_backS5:// 返回
			YyrwJiaojieActivity.this.finish();
			break;

		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		isFlag = false;
		manager.getRuning().remove();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		S_application.getApplication().left_user = null;
		S_application.getApplication().right_user = null;
		f2=null;
		f1=null;
		firstSuccess=false;
	}

}
