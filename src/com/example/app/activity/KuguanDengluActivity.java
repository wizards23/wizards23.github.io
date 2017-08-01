package com.example.app.activity;

import java.net.SocketTimeoutException;

import hdjc.rfid.operator.RFID_Device;
import android.annotation.SuppressLint;
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
import com.service.FixationValue;

/**
 * 库管员验证指纹页面
 * @author Administrator
 */
public class KuguanDengluActivity extends Activity {
//	private ImageView back_bank; // 返回 btn
	private TextView name_left, name_right, bottomtext, texttop;
	private ImageView img_left, img_right;
	private User result_user;
	private String fname_left, fname_right;
	Scan scan;
	public static String userid1; // 角色ID
	public static String userid2; // 角色ID
	public static boolean firstSuccess = false; // 第一位是否已成功验证指纹
	String f1 = null; // 第一个按手指的人
	String f2 = null; // 第二个按手指的人
	int one = 0;// 统计第一位验证指纹人的次数
	int two = 0;// 统计第二位验证指纹人的次数
	String planNum; // 计划编号
	String type = null; // 交接类型
	String admin;
	View vtoast;
	private ManagerClass manager;
	OnClickListener OnClick;
	/*
	 * (跳转页面 演示而用 实际开发请删除)
	 */
	// private LinearLayout yanshi;
	private FingerCheckBiz fingerCheck;

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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_kuguandenglu);
		load();
		manager = new ManagerClass();
		Shijianchuli shijianchuli = new Shijianchuli();
	//	back_bank.setOnClickListener(shijianchuli);
		// yanshi.setOnClickListener(shijianchuli);
		getRfid().addNotifly(new GetFingerValue());
	//	getRfid().fingerOpen();	
		new Thread(new Runnable() {
			@Override
			public void run() {
				getRfid().fingerOpen();			
			}
		}).start();
		
		/*GetFingerValue.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle;
				if (msg.what == 1) {
					bundle = msg.getData();

					if (bundle != null
							&& bundle.getString("finger").equals("正在获取指纹特征值！")) {
						texttop.setText("正在验证指纹...");
					} else if (bundle != null
							&& bundle.getString("finger").equals("获取指纹特征值成功！")) {
						yanzhengFinger();
						// 第一位验证

					}
				}
			}

		};*/

		OnClick = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				yanzhengFinger();
			}
		};
	}
	
	/**
	 * 控件初始化
	 */
	private void load() {
	//	back_bank = (ImageView) findViewById(R.id.back_bank);
		// yanshi = (LinearLayout) findViewById(R.id.yanshi);
		img_left = (ImageView) findViewById(R.id.finger_left);
		img_right = (ImageView) findViewById(R.id.finger_right);
		name_left = (TextView) findViewById(R.id.login_name1);
		name_right = (TextView) findViewById(R.id.login_name2);
		bottomtext = (TextView) findViewById(R.id.login_bottom_show);
		texttop = (TextView) findViewById(R.id.resultmsg);
	}

	/**
	 * 事件处理 
	 * @author yuyunheng
	 */
	private class Shijianchuli implements OnClickListener {

		@SuppressLint("ShowToast")
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			/*switch (arg0.getId()) {
			case R.id.back_bank:
				// 返回
				firstSuccess = false;
				f1 = null;
				f2 = null;
				if(ShareUtil.zhiwenid_left!=null){
					ShareUtil.zhiwenid_left=null;
				}
				if(ShareUtil.zhiwenid_right!=null){
					ShareUtil.zhiwenid_right=null;
				}
				if(GApplication.getApplication().kuguan1!=null){
					GApplication.getApplication().kuguan1=null;
				}
				if(GApplication.getApplication().kuguan2!=null){
					GApplication.getApplication().kuguan2=null;
				}
				KuguanDengluActivity.this.finish();
				break;
			}*/
		}
	}

	// 根据交接类型获取返回角色ID
	public String getUserID(String type) {
		// 空钞箱交接
		if (type.equals("01")) {
			return FixationValue.clearer + "";
			// 未清回收钞箱交接
		} else if (type.equals("03")) {
			return FixationValue.clearer + "";
			// 网点人员加钞钞箱交接
		} else if (type.equals("04")) {
			return FixationValue.webuser + "";
		}
		return null;

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
					result_user = yz.yanzhengfinger(GApplication.loginJidouId,
							"4", ShareUtil.ivalBack);
					if (result_user != null) {
						handler.sendEmptyMessage(1);
					} else {
						handler.sendEmptyMessage(2);
					}
				} catch (SocketTimeoutException e) {
					handler.sendEmptyMessage(3);
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(4);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(5);
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
				// OneAsyncTask
				manager.getAbnormal().timeout(KuguanDengluActivity.this,
						"获取数据失败", OnClick);
				break;
			case 1:
				if (!firstSuccess && !"1".equals(f1)) {
					img_left.setImageBitmap(ShareUtil.finger_bitmap_left);
					fname_left = result_user.getUsername();
					ShareUtil.zhiwenid_left = result_user.getUserzhanghu();
					name_left.setText(fname_left);
					f1 = "1";
					// //System.out.println("左边指纹特征值："+ShareUtil.ivalBack);
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < ShareUtil.ivalBack.length; i++) {
						sb.append(ShareUtil.ivalBack[i]);
					}
					firstSuccess = true;
					bottomtext.setText("请第二位库管员按压手指...");
					texttop.setText("第一位验证成功");
					// 第二位验证
				} else {
					fname_right = result_user.getUsername();
					if (fname_left != null && fname_left.equals(fname_right)) {
						texttop.setText("验证失败");
						handler.sendEmptyMessage(2);
					} else {
						texttop.setText("");
						img_right.setImageBitmap(ShareUtil.finger_bitmap_right);
						ShareUtil.zhiwenid_right = result_user.getUserzhanghu();
						// //System.out.println("右边指纹特征值："+ShareUtil.finger_bitmap_right);
						name_right.setText(fname_right);
						f2 = "2";
						bottomtext.setText("验证成功！");
						if (GApplication.kuguan1 == null) {
							GApplication.kuguan1 = new User();
						}
						if (GApplication.kuguan2 == null) {
							GApplication.kuguan2 = new User();
						}
						fname_left = null;
						fname_right = null;
						firstSuccess = false;
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("走起:222222"+"f1是什么"+f1+"f2是什么"+f2);
						if(null!=f1&&null!=f2&&f1.equals("1")&&f2.equals("2")){
							manager.getRuning().runding(KuguanDengluActivity.this,"验证完成，数据加载中...");
							Skip.skip(KuguanDengluActivity.this,KuanxiangChuruActivity.class, null, 0);
						}
						
					}
				}
				break;
			case 2:
				if (firstSuccess == false) {// 失败就累加次数
					one++;
					texttop.setText("验证失败" + one + "次");
				} else {
					two++;
					texttop.setText("验证失败" + two + "次");
				}
				if (one >= 3) {
					Intent intent = new Intent();
					Bundle bundle2 = new Bundle();
					bundle2.putString("FLAG", "kuguanone");
					ShareUtil.ivalBack = null;
					ShareUtil.finger_bitmap_left = null;
					intent.putExtras(bundle2);
					texttop.setText("");
					Skip.skip(KuguanDengluActivity.this,
							KuguanCheckFingerActivity.class, bundle2, 0);
				} else if (two >= 3) {
					Intent intent = new Intent();
					Bundle bundle2 = new Bundle();
					bundle2.putString("FLAG", "kuguantwo");
					bundle2.putString("name1", name_left.getText().toString());
					System.out.println("您是什么账号?:"+name_left.getText().toString());
					if (ShareUtil.finger_bitmap_left != null)
						GApplication.map = ShareUtil.finger_bitmap_left;
					intent.putExtras(bundle2);
					texttop.setText("");
					f1="1";
					Skip.skip(KuguanDengluActivity.this,
							KuguanCheckFingerActivity.class, bundle2, 0);
				}
				break;
			case 3:
				texttop.setText("验证超时");
//				manager.getAbnormal().timeout(KuguanDengluActivity.this,
//						"连接超时,重试？", new View.OnClickListener() {
//
//							@Override
//							public void onClick(View arg0) {
//								manager.getRuning().remove();
//								manager.getAbnormal().remove();
//								yanzhengFinger();
//							}
//						});
				
				if (firstSuccess == false) {// 失败就累加次数
					one++;
					texttop.setText("验证失败" + one + "次");
				} else {
					two++;
					texttop.setText("验证失败" + two + "次");
				}
				if (one >= 3) {
					Intent intent = new Intent();
					Bundle bundle2 = new Bundle();
					bundle2.putString("FLAG", "kuguanone");
					ShareUtil.ivalBack = null;
					ShareUtil.finger_bitmap_left = null;
					intent.putExtras(bundle2);
					texttop.setText("");
					Skip.skip(KuguanDengluActivity.this,
							KuguanCheckFingerActivity.class, bundle2, 0);
				} else if (two >= 3) {
					Intent intent = new Intent();
					Bundle bundle2 = new Bundle();
					bundle2.putString("FLAG", "kuguantwo");
					bundle2.putString("name1", name_left.getText().toString());
					System.out.println("您是什么账号?:"+name_left.getText().toString());
					if (ShareUtil.finger_bitmap_left != null)
						GApplication.map = ShareUtil.finger_bitmap_left;
					intent.putExtras(bundle2);
					texttop.setText("");
					f1="1";
					Skip.skip(KuguanDengluActivity.this,
							KuguanCheckFingerActivity.class, bundle2, 0);
				}

				break;
			case 4:
				texttop.setText("验证失败");
//				manager.getAbnormal().timeout(KuguanDengluActivity.this,
//						"验证失败,重试？", new View.OnClickListener() {
//
//							@Override
//							public void onClick(View arg0) {
//								manager.getRuning().remove();
//								manager.getAbnormal().remove();
//								yanzhengFinger();
//							}
//						});
				if (firstSuccess == false) {// 失败就累加次数
					one++;
					texttop.setText("验证失败" + one + "次");
				} else {
					two++;
					texttop.setText("验证失败" + two + "次");
				}
				if (one >= 3) {
					Intent intent = new Intent();
					Bundle bundle2 = new Bundle();
					bundle2.putString("FLAG", "kuguanone");
					ShareUtil.ivalBack = null;
					ShareUtil.finger_bitmap_left = null;
					intent.putExtras(bundle2);
					texttop.setText("");
					Skip.skip(KuguanDengluActivity.this,
							KuguanCheckFingerActivity.class, bundle2, 0);
				} else if (two >= 3) {
					Intent intent = new Intent();
					Bundle bundle2 = new Bundle();
					bundle2.putString("FLAG", "kuguantwo");
					bundle2.putString("name1", name_left.getText().toString());
					System.out.println("您是什么账号?:"+name_left.getText().toString());
					if (ShareUtil.finger_bitmap_left != null)
						GApplication.map = ShareUtil.finger_bitmap_left;
					intent.putExtras(bundle2);
					texttop.setText("");
					f1="1";
					Skip.skip(KuguanDengluActivity.this,
							KuguanCheckFingerActivity.class, bundle2, 0);
				}
				break;
			case 5:
				texttop.setText("验证异常");
//				manager.getAbnormal().timeout(KuguanDengluActivity.this,
//						"网络连接失败,重试？", new View.OnClickListener() {
//
//							@Override
//							public void onClick(View arg0) {
//								manager.getRuning().remove();
//								manager.getAbnormal().remove();
//								yanzhengFinger();
//							}
//						});
				if (firstSuccess == false) {// 失败就累加次数
					one++;
					texttop.setText("验证失败" + one + "次");
				} else {
					two++;
					texttop.setText("验证失败" + two + "次");
				}
				if (one >= 3) {
					Intent intent = new Intent();
					Bundle bundle2 = new Bundle();
					bundle2.putString("FLAG", "kuguanone");
					ShareUtil.ivalBack = null;
					ShareUtil.finger_bitmap_left = null;
					intent.putExtras(bundle2);
					texttop.setText("");
					Skip.skip(KuguanDengluActivity.this,
							KuguanCheckFingerActivity.class, bundle2, 0);
				} else if (two >= 3) {
					Intent intent = new Intent();
					Bundle bundle2 = new Bundle();
					bundle2.putString("FLAG", "kuguantwo");
					bundle2.putString("name1", name_left.getText().toString());
					System.out.println("您是什么账号?:"+name_left.getText().toString());
					if (ShareUtil.finger_bitmap_left != null)
						GApplication.map = ShareUtil.finger_bitmap_left;
					intent.putExtras(bundle2);
					texttop.setText("");
					f1="1";
					Skip.skip(KuguanDengluActivity.this,
							KuguanCheckFingerActivity.class, bundle2, 0);
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
			if(GApplication.getApplication().kuguan1!=null){
				GApplication.getApplication().kuguan1=null;
			}
			if(GApplication.getApplication().kuguan2!=null){
				GApplication.getApplication().kuguan2=null;
			}
			Skip.skip(KuguanDengluActivity.this, KuanxiangCaidanActivity.class, null, 0);
			KuguanDengluActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStop() {
		super.onStop();
		manager.getRuning().remove();
		getRfid().close_a20();
		if (fname_left == null || fname_right == null) {
			KuguanDengluActivity.this.finish();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("fname_left"+fname_left);
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
						texttop.setText("正在验证指纹...");
					} else if (bundle != null
							&& bundle.getString("finger").equals("获取指纹特征值成功！")) {
						yanzhengFinger();
						// 第一位验证

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

			if (flag.equals("kuguanone")) {
				img_left.setImageResource(R.drawable.sccuss);
				// System.out.println("R.drawable.sccuss  ： "+R.drawable.sccuss);
				// System.out.println("GApplication.use.getUsername()   :"+GApplication.use.getUsername());
				fname_left = GApplication.use.getUsername();
				// System.out.println(GApplication.use.getUsername());
				name_left.setText(fname_left);
				f1 = "1";
				one = 0;
				firstSuccess = true;
				bottomtext.setText("请第二位库管员按压手指...");
				texttop.setText("第一位验证成功");
				// System.out.println("我是标识符！"+"===="+flag+"======");
			}
			if (flag.equals("kuguantwo")) {
				img_right.setImageResource(R.drawable.sccuss);
				fname_right = GApplication.use.getUsername();
				fname_left = bundle.getString("name1");
				if (fname_left != null && GApplication.map != null) {
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
					Toast.makeText(KuguanDengluActivity.this, "请两个库管员进行验证！",
							Toast.LENGTH_SHORT).show();
				} else {
					fname_right = null;
					fname_left = null;
					firstSuccess = false;
					if(null!=f1 && null!=f2 && f1.equals("1") && f2.equals("2")){
						System.out.println("走起:111111");
						manager.getRuning().runding(this, "验证完成,数据加载中...");
						Skip.skip(KuguanDengluActivity.this,KuanxiangChuruActivity.class, null, 0);
					}					
				}
			}
		}
	}
	
}
