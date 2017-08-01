package com.example.app.activity;

import java.net.SocketTimeoutException;

import hdjc.rfid.operator.RFID_Device;
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
import android.widget.Toast;

import com.application.GApplication;
import com.example.app.entity.User;
import com.example.app.util.Skip;
import com.example.pda.R;
import com.imple.getnumber.GetFingerValue;
import com.main.pda.Scan;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.biz.FingerCheckBiz;
import com.moneyboxadmin.biz.SaveAuthLogBiz;
import com.o.service.YanZhengZhiWen;
import com.poka.device.ShareUtil;

public class KuanXiangJiaoJieActivity extends Activity {
//	private ImageView back_bank; // 返回 btn
	private TextView name_left, name_right, bottomtext, texttop;
	private ImageView img_left, img_right;
	private User result_user;
	private String fname_left, fname_right;
	private int shibaiCount = 0;
	Scan scan;
	public static String userid1; // 角色ID
	public static String userid2; // 角色ID
	public static boolean firstSuccess = false; // 第一位是否已成功验证指纹
	String f1 = null; // 第一个按手指的人
	String f2 = null; // 第二个按手指的人
	int one = 0;// 统计第一个验证指纹失败的次数
	int two = 0;// 统计第二个验证指纹失败的次数
	String planNum; // 计划编号
	String type = null; // 交接类型
	String admin;
	View vtoast;
	private FingerCheckBiz fingerCheck;
	private ManagerClass manager;
	FingerCheckBiz getFingerCheck() {
		return fingerCheck = fingerCheck == null ? new FingerCheckBiz()
				: fingerCheck;
	}

	SaveAuthLogBiz saveAuthLogBiz;

	SaveAuthLogBiz getSaveAuthLogBiz() {
		return saveAuthLogBiz = saveAuthLogBiz == null ? new SaveAuthLogBiz()
				: saveAuthLogBiz;
	}

	private RFID_Device rfid;

	RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_kuguanjiaojie);
		load();
		manager = new ManagerClass();

		
		getRfid().addNotifly(new GetFingerValue());
		new Thread(new Runnable() {
			@Override
			public void run() {
				getRfid().fingerOpen();			
			}
		}).start();

		Shijianchuli shijianchuli = new Shijianchuli();
	//	back_bank.setOnClickListener(shijianchuli);
	}

	/**
	 * 控件初始化
	 */
	private void load() {
		img_left = (ImageView) findViewById(R.id.jj_finger_left);
		img_right = (ImageView) findViewById(R.id.jj_finger_right);
	//	back_bank = (ImageView) findViewById(R.id.back_bank);
		name_left = (TextView) findViewById(R.id.jj_login_name_left);
		name_right = (TextView) findViewById(R.id.jj_login_name_right);
		bottomtext = (TextView) findViewById(R.id.jj_show);
		texttop = (TextView) findViewById(R.id.jj_resultmsg);
	}

	/**
	 * 事件处理
	 * 
	 * @author yuyunheng
	 */
	private class Shijianchuli implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			/*switch (arg0.getId()) {
			case R.id.back_bank:// 返回
				Skip.skip(KuanXiangJiaoJieActivity.this, JiaoJieActivity.class, null, 0);	
				KuanXiangJiaoJieActivity.this.finish();
				break;
			}*/
		}
	}

	// 验证指纹
	public void yanzhengFinger() {
		Thread t1;
		t1 = new Thread() {
			@Override
			public void run() {
				super.run();
				YanZhengZhiWen yz = new YanZhengZhiWen();
				try {
				//	texttop.setText("正在进行指纹验证.....");
					result_user = yz.yanzhengfinger(GApplication.sk.getNetId(),
							ShareUtil.WdId, ShareUtil.ivalBack);
					if(result_user!=null){
						handler.sendEmptyMessage(0);
					}else{
						handler.sendEmptyMessage(1);
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(2);
				} catch (NullPointerException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(3);
				}
			}
		};
		t1.start();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				manager.getRuning().remove();
				if (!firstSuccess && !"1".equals(f1)) {
					img_left.setImageBitmap(ShareUtil.w_finger_bitmap_left);
					fname_left = result_user.getUsername();
					ShareUtil.zhiwenid_left = result_user.getUserzhanghu();
					name_left.setText(fname_left);
					f1 = "1";
					firstSuccess = true;
					bottomtext.setText("请第二位网点人员员按压手指...");
					texttop.setText("第一位验证成功");
					// 第二位验证
				} else {
					fname_right = result_user.getUsername();
					if (null != fname_left && fname_left.equals(fname_right)) {
					//	two++;
						texttop.setText("请更换人员验证");
					} else {
						img_right
								.setImageBitmap(ShareUtil.w_finger_bitmap_right);
						fname_right = result_user.getUsername();
						ShareUtil.zhiwenid_right = result_user.getUserzhanghu();
						name_right.setText(fname_right);
						f2 = "2";
						texttop.setText("第二位验证成功");
						bottomtext.setText("验证成功！");
						firstSuccess = false;
						if (GApplication.wd_user1 == null) {
							GApplication.wd_user1 = new User();
						}
						if (GApplication.wd_user2 == null) {
							GApplication.wd_user2 = new User();
						}

						manager.getRuning().runding(
								KuanXiangJiaoJieActivity.this, "即将自动跳转");
						try {
							Thread.sleep(2000);
						} catch (Exception e) {
							e.printStackTrace();
						}
						fname_left = null;
						fname_right = null;					
						if(null!=f1&&null!=f2&&f1.equals("1")&&f2.equals("2")){
							Skip.skip(KuanXiangJiaoJieActivity.this,KuanXiangJiaoJieYaYunActivity.class, null, 0);
							KuanXiangJiaoJieActivity.this.finish();
						}						
					}
				}
				break;
			case 1:
				manager.getRuning().remove();
				
				// 累计验证失败的次数
				texttop.setText("验证失败");
				if (firstSuccess == false) {// 失败就累加次数
					one++;
					// System.out.println("one :+++++++++++:"+one);
					Toast.makeText(KuanXiangJiaoJieActivity.this,
							"验证失败，您还有" + (3 - one) + "次机会", 4).show();
				} else {
					two++;
					// System.out.println("two :+++++++++++:"+two);
					Toast.makeText(KuanXiangJiaoJieActivity.this,
							"验证失败，您还有" + (3 - two) + "次机会", 4).show();
				}
				// 指纹验证3次失败时跳转的登录页面
				if (one >= 3) {
					Intent intent = new Intent();
					Bundle bundle2 = new Bundle();
					bundle2.putString("FLAG", "wangdianone");
					ShareUtil.ivalBack = null;
					ShareUtil.w_finger_bitmap_left = null;
					intent.putExtras(bundle2);
					Skip.skip(KuanXiangJiaoJieActivity.this,
							WangdianCheckFingerActivity.class, bundle2, 0);
				} else if (two >= 3) {
					Intent intent = new Intent();
					Bundle bundle2 = new Bundle();
					bundle2.putString("FLAG", "wangdiantwo");
					// ShareUtil.ivalBack=null;
					if (ShareUtil.w_finger_bitmap_left != null)
						GApplication.map = ShareUtil.w_finger_bitmap_left;
					bundle2.putString("left", fname_left);
					intent.putExtras(bundle2);
					Skip.skip(KuanXiangJiaoJieActivity.this,
							WangdianCheckFingerActivity.class, bundle2, 0);
				}
				break;
			case 2:
				
				manager.getRuning().remove();
//				texttop.setText("验证超时");
//				manager.getAbnormal().timeout(KuanXiangJiaoJieActivity.this,
//						"验证超时,重试?", new View.OnClickListener() {
//
//							@Override
//							public void onClick(View v) {
//								manager.getAbnormal().remove();
//								texttop.setText("正在进行指纹验证.....");
//								yanzhengFinger();
//							}
//						});
				// 累计验证失败的次数
				texttop.setText("验证失败");
				if (firstSuccess == false) {// 失败就累加次数
					one++;
					// System.out.println("one :+++++++++++:"+one);
					Toast.makeText(KuanXiangJiaoJieActivity.this,
							"验证失败，您还有" + (3 - one) + "次机会", 4).show();
				} else {
					two++;
					// System.out.println("two :+++++++++++:"+two);
					Toast.makeText(KuanXiangJiaoJieActivity.this,
							"验证失败，您还有" + (3 - two) + "次机会", 4).show();
				}
				// 指纹验证3次失败时跳转的登录页面
				if (one >= 3) {
					Intent intent = new Intent();
					Bundle bundle2 = new Bundle();
					bundle2.putString("FLAG", "wangdianone");
					ShareUtil.ivalBack = null;
					ShareUtil.w_finger_bitmap_left = null;
					intent.putExtras(bundle2);
					Skip.skip(KuanXiangJiaoJieActivity.this,
							WangdianCheckFingerActivity.class, bundle2, 0);
				} else if (two >= 3) {
					Intent intent = new Intent();
					Bundle bundle2 = new Bundle();
					bundle2.putString("FLAG", "wangdiantwo");
					// ShareUtil.ivalBack=null;
					if (ShareUtil.w_finger_bitmap_left != null)
						GApplication.map = ShareUtil.w_finger_bitmap_left;
					bundle2.putString("left", fname_left);
					intent.putExtras(bundle2);
					Skip.skip(KuanXiangJiaoJieActivity.this,
							WangdianCheckFingerActivity.class, bundle2, 0);
				}

				break;
			case 3:
				
				manager.getRuning().remove();
//				texttop.setText("网络连接失败");
//				manager.getAbnormal().timeout(KuanXiangJiaoJieActivity.this,
//						"网络连接失败,重试?", new View.OnClickListener() {
//
//							@Override
//							public void onClick(View v) {
//								manager.getAbnormal().remove();
//								texttop.setText("正在进行指纹验证.....");
//								yanzhengFinger();
//							}
//						});
				// 累计验证失败的次数
				texttop.setText("验证失败");
				if (firstSuccess == false) {// 失败就累加次数
					one++;
					// System.out.println("one :+++++++++++:"+one);
					Toast.makeText(KuanXiangJiaoJieActivity.this,
							"验证失败，您还有" + (3 - one) + "次机会", 4).show();
				} else {
					two++;
					// System.out.println("two :+++++++++++:"+two);
					Toast.makeText(KuanXiangJiaoJieActivity.this,
							"验证失败，您还有" + (3 - two) + "次机会", 4).show();
				}
				// 指纹验证3次失败时跳转的登录页面
				if (one >= 3) {
					Intent intent = new Intent();
					Bundle bundle2 = new Bundle();
					bundle2.putString("FLAG", "wangdianone");
					ShareUtil.ivalBack = null;
					ShareUtil.w_finger_bitmap_left = null;
					intent.putExtras(bundle2);
					Skip.skip(KuanXiangJiaoJieActivity.this,
							WangdianCheckFingerActivity.class, bundle2, 0);
				} else if (two >= 3) {
					Intent intent = new Intent();
					Bundle bundle2 = new Bundle();
					bundle2.putString("FLAG", "wangdiantwo");
					// ShareUtil.ivalBack=null;
					if (ShareUtil.w_finger_bitmap_left != null)
						GApplication.map = ShareUtil.w_finger_bitmap_left;
					bundle2.putString("left", fname_left);
					intent.putExtras(bundle2);
					Skip.skip(KuanXiangJiaoJieActivity.this,
							WangdianCheckFingerActivity.class, bundle2, 0);
				}
				break;
			default:
				break;
			}
		}

	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			firstSuccess = false;
			f1 = null;
			f2 = null;
			if(ShareUtil.zhiwenid_left!=null){
				ShareUtil.zhiwenid_left=null;
			}
			if(ShareUtil.zhiwenid_right!=null){
				ShareUtil.zhiwenid_right=null;
			}
			if(GApplication.wd_user1!=null){
				GApplication.wd_user1=null;
			}
			if(GApplication.wd_user2!=null){
				GApplication.wd_user2=null;
			}
			Skip.skip(KuanXiangJiaoJieActivity.this, JiaoJieActivity.class, null, 0);
			KuanXiangJiaoJieActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(fname_left==null){
			firstSuccess=false;
		}	
		GetFingerValue.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle;
				if (msg.what == 1) {
					bundle = msg.getData();
					if (bundle != null
							&& bundle.getString("finger").equals("正在获取指纹特征值！")) {
						texttop.setText("正在进行指纹验证.....");
					} else if (bundle != null
							&& bundle.getString("finger").equals("获取指纹特征值成功！")) {
						yanzhengFinger();

					}
				}
			}

		};
		
		/**
		 * SM修改部分
		 */
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			String flag = bundle.getString("FLAG");

			if (flag.equals("wangdianone")) {
				img_left.setImageResource(R.drawable.sccuss);
				// System.out.println("R.drawable.sccuss  ： "+R.drawable.sccuss);
				fname_left = GApplication.wd_user1.getUsername();
				name_left.setText(fname_left);
				f1 = "1";
				one = 0;
				firstSuccess = true;
				bottomtext.setText("请第二位网点人员按压手指...");
				texttop.setText("第一位验证成功");
				// System.out.println("我是标识符！"+"===="+flag+"======");
			}
			if (flag.equals("wangdiantwo")) {
				fname_right = GApplication.wd_user2.getUsername();
				fname_left = bundle.getString("left");
				img_right.setImageResource(R.drawable.sccuss);
				// System.out.println("one  is :"+one);
				if (GApplication.wd_user1==null) {
					GApplication.wd_user1 = new User();
				}
				if (GApplication.wd_user1.getUsername() != null
						&& GApplication.map != null) {
					img_left.setImageBitmap(GApplication.map);
				} else {
					img_left.setImageResource(R.drawable.sccuss);
				}
				name_left.setText(fname_left);
				name_right.setText(fname_right);
				f1 = "1";
				f2 = "2";
				texttop.setText("第二位验证成功");
				bottomtext.setText("验证成功！");

				if (fname_left.equals(fname_right)) {
					Toast.makeText(KuanXiangJiaoJieActivity.this,
							"请两位网点人员进行验证！", Toast.LENGTH_SHORT).show();
				} else {
					firstSuccess = false;
					fname_left = null;
					fname_right = null;
					
					if(null!=f1 && null!=f2 && f1.equals("1") && f2.equals("2")){
						Skip.skip(KuanXiangJiaoJieActivity.this,KuanXiangJiaoJieYaYunActivity.class, null, 0);
						KuanXiangJiaoJieActivity.this.finish();
					}
					
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		manager.getRuning().remove();
	}

}
