package com.ljsw.tjbankpda.db.activity;

import java.net.SocketTimeoutException;

import hdjc.rfid.operator.RFID_Device;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.GApplication;
import com.entity.SystemUser;
import com.example.pda.R;
import com.imple.getnumber.GetFingerValue;
import com.ljsw.tjbankpda.db.application.o_Application;
import com.ljsw.tjbankpda.db.service.YanZhengZhiWenService;
import com.ljsw.tjbankpda.util.Skip;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.biz.FingerCheckBiz;
import com.poka.device.ShareUtil;
import com.service.FixationValue;

/**
 * 库管或清分管理员指纹双人登录
 * @author yuyunheng
 */
@SuppressLint("HandlerLeak")
public class KuGuanLogin_db extends FragmentActivity implements OnClickListener {
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
	private TextView textView3; 
	private TextView textView4;
	private TextView textView1;
	private String jiaoseId;  //登陆人角色Id
	/**
	 * 演示跳转用 功能实现请删除
	 */
	private LinearLayout yanshi;
	private FingerCheckBiz fingerCheck;
	private ManagerClass manager;
	private boolean isFlag = true;

	FingerCheckBiz getFingerCheck() {
		return fingerCheck = fingerCheck == null ? new FingerCheckBiz()
				: fingerCheck;
	}

	private RFID_Device rfid;

	private RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_kuguandenglu_db);
		load();
		jiaoseId=GApplication.user.getLoginUserId();   //赋值角色id
		
		if(jiaoseId.equals(FixationValue.waibaoQingfenString)){
			textView3.setText("清分管理员");
			textView4.setText("清分管理员");
			bottom_tishi.setText("请第一位清分管理员按手指...");
		}
		manager = new ManagerClass();
		intent = new Intent();
		if (o_Application.FingerLoginNum.size() > 0) {
			o_Application.FingerLoginNum.clear();
		}
		OnClick1 = new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				manager.getAbnormal().remove();
				if (isFlag) {
					isFlag = false;
					yanzhengFinger();
				}

			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		isFlag = true;
		
		getRfid().addNotifly(new GetFingerValue());
		new Thread() {
			@Override
			public void run() {
				super.run();
				getRfid().fingerOpen();
			}
		}.start();
		GetFingerValue.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle;
				if (msg.what == 1) {
					
						bundle = msg.getData();

						if (bundle != null
								&& bundle.getString("finger").equals(
										"正在获取指纹特征值！")) {
							top_tishi.setText("正在验证指纹...");
						} else if (bundle != null
								&& bundle.getString("finger").equals(
										"获取指纹特征值成功！")) {
							if (isFlag) {
							isFlag = false;
							yanzhengFinger();
						}
					}
				}
			}

		};
	}

	/**
	 * 指纹验证
	 */
	public void yanzhengFinger() {
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					result_user = new YanZhengZhiWenService().checkFingerprint(
							o_Application.kuguan_db.getOrganizationId(), GApplication.user.getLoginUserId(),
							ShareUtil.ivalBack);
					if (result_user != null) {
						System.out.println("返回指纹用户："
								+ result_user.getLoginUserName());
						handler.sendEmptyMessage(2);
					} else {
						System.out.println("返回指纹用户失败");
						handler.sendEmptyMessage(3);
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(0);
				} catch (NullPointerException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(3);
				} catch (Exception e1) {
					e1.printStackTrace();
					handler.sendEmptyMessage(1);
				}
			}

		}.start();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isFlag = true;
			switch (msg.what) {
			case 0:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(KuGuanLogin_db.this, "验证超时,重试?",
						OnClick1);
				break;
			case 1:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(KuGuanLogin_db.this,
						"网络连接失败,重试?", OnClick1);
				break;
			case 2:
				// 第一位验证
				if (!firstSuccess && !"1".equals(f1)) {
					img_left.setImageBitmap(ShareUtil.finger_kuguandenglu_left);
					left_name = result_user.getLoginUserName();
					o_Application.fragmentleft = left_name;
					o_Application.left_user = result_user;
					o_Application.FingerLoginNum.add(result_user// 存放指纹登陆人的账号信息,
							.getLoginUserId());
					System.out.println("左侧验证完毕  保存帐号集合长度:"
							+ o_Application.FingerLoginNum.size());
					setName_left.setText(left_name);
					f1 = "1";
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < ShareUtil.ivalBack.length; i++) {
						sb.append(ShareUtil.ivalBack[i]);
					}
					firstSuccess = true;
					bottom_tishi.setText("请第二位库管员按手指...");
					top_tishi.setText("第一位验证成功");
					// 第二位验证
				} else if (firstSuccess && !"2".equals(f2)) {
					right_name = result_user.getLoginUserName();
					o_Application.fragmentright = right_name;
					if (left_name != null && right_name.equals(left_name)) {
						top_tishi.setText("重复验证!");
						// handler.sendEmptyMessage(3);
					} else {
						top_tishi.setText("");
						o_Application.right_user = result_user;
						o_Application.FingerLoginNum.add(result_user
								.getLoginUserId());
						System.out.println("右侧验证完毕  保存帐号集合长度:"
								+ o_Application.FingerLoginNum.size());
						img_right
								.setImageBitmap(ShareUtil.finger_kuguandenglu_right);
						setName_right.setText(right_name);
						f2 = "2";
						bottom_tishi.setText("验证成功！");
						firstSuccess = false;
						Skip.skip(KuGuanLogin_db.this, RenWuLieBiao_db.class,
								null, 0);
						KuGuanLogin_db.this.finish();
					}
				}
				break;
			case 3:
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
					intent.setClass(KuGuanLogin_db.this, KuGuanYuanDengLu.class);
					KuGuanLogin_db.this.startActivityForResult(intent, 1);
					top_tishi.setText("");
					wrongleftNum = 0;
				} else if (wrongrightNum >= ShareUtil.three) {
					// 右侧跳用户登录
					firstSuccess = true;
					intent.setClass(KuGuanLogin_db.this, KuGuanYuanDengLu.class);
					KuGuanLogin_db.this.startActivityForResult(intent, 1);
					wrongrightNum = 0;
					top_tishi.setText("");
				}
				break;
			case 4:
				Skip.skip(KuGuanLogin_db.this, RenWuLieBiao_db.class, null, 0);
				KuGuanLogin_db.this.finish();
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg2 != null) {
			Bundle bundle = arg2.getExtras();
			String isOk = bundle.getString("isOk");
			if (isOk.equals("success")) {
				if (!f1.equals("1")) {
					f1 = "1";
					o_Application.left_user = KuGuanYuanDengLu.lingshiUser;
					left_name = o_Application.left_user.getLoginUserName();
					o_Application.fragmentleft = left_name;
					setName_left.setText(left_name);
					img_left.setImageResource(R.drawable.result_isok);
					firstSuccess = true;
					System.out.println("左侧验证完毕  保存帐号集合长度:"
							+ o_Application.FingerLoginNum.size());
					top_tishi.setText("第一位验证成功");
					if(jiaoseId.equals(FixationValue.waibaoQingfenString)){
						bottom_tishi.setText("请第二位清分管理员按手指...");
					}else{
						bottom_tishi.setText("请第二位库管员按手指...");
					}
					System.out.println("左边登录完成 o_Application.left_user："
							+ o_Application.left_user.getLoginUserName());
				} else {
					f2 = "2";
					o_Application.right_user = KuGuanYuanDengLu.lingshiUser;
					right_name = o_Application.right_user.getLoginUserName();
					o_Application.fragmentright = right_name;
					setName_right.setText(right_name);
					img_right.setImageResource(R.drawable.result_isok);
					firstSuccess = false;
					top_tishi.setText("");
					System.out.println("右侧验证完毕  保存帐号集合长度:"
							+ o_Application.FingerLoginNum.size());
					bottom_tishi.setText("验证成功！");
					Thread thread=new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								Thread.sleep(1000);
								Message msg=handler.obtainMessage();
								msg.what=4;
								handler.sendMessage(msg);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					
					thread.start();
				}
			}
		}

	}
	
	Handler h=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
		}
	};

	public void load() {
		yanshi = (LinearLayout) findViewById(R.id.kuguandenglu_yanshi);
		yanshi.setOnClickListener(this);
		setName_left = (TextView) findViewById(R.id.kuguanlogin_setname_left);
		setName_right = (TextView) findViewById(R.id.kuguanlogin_setname_right);
		bottom_tishi = (TextView) findViewById(R.id.kuguanlogin_buttom_prompt);
		back = (ImageView) findViewById(R.id.kuguandenglu_back);
		back.setOnClickListener(this);
		top_tishi = (TextView) findViewById(R.id.kuguandenglu_top_tishi);
		img_left = (ImageView) findViewById(R.id.kuguandenglu_img_left);
		img_right = (ImageView) findViewById(R.id.kuguandenglu_img_right);
		textView1=(TextView) findViewById(R.id.textView1);
		textView3=(TextView) findViewById(R.id.textView3);
		textView4=(TextView) findViewById(R.id.textView4);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		
		case R.id.kuguandenglu_back:
			KuGuanLogin_db.this.finish();
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
		System.out.println("isFlag:--->"+isFlag);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		f1 = "";
		f2 = "";
		firstSuccess = false;
		getRfid().close_a20();
		o_Application.left_user = null;
		o_Application.right_user = null;
		System.gc();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			KuGuanLogin_db.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
