package com.moneyboxadmin.pda;

import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

import com.application.GApplication;
import com.entity.Finger;
import com.entity.SystemUser;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.imple.getnumber.GetFingerValue;
import com.imple.getnumber.Getnumber;
import com.main.pda.Scan;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.biz.ATMOutAndJoinBiz;
import com.moneyboxadmin.biz.FingerCheckBiz;
import com.moneyboxadmin.biz.GetBoxDetailListBiz;
import com.moneyboxadmin.biz.SaveAuthLogBiz;

import com.moneyboxadmin.service.SaveAuthLogService;
import com.out.admin.pda.OrderWork;
import com.out.biz.CashboxNumByCorpIdBiz;
import com.poka.device.ShareUtil;
import com.service.FixationValue;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

//押运员交接
public class SupercargoJoin extends Activity implements OnTouchListener {

	TextView supertext; // 提示信息
	TextView supername; // 押运员姓名
	TextView msgtext; // 验证结果信息
	ImageView msgimg; // 验证结果信息
	ImageView back; // 返回
	Bundle bundle;
	ImageView finger_img;
	Timer timer;
	Bundle bundleIntent;
	String plan; // 计划编号
	String busin; // 业务名称
	String type; // 交接类型

	private boolean isscuess = false; // 是否已交接成功

	View vtoast;
	private ManagerClass managerClass;
	Scan scan;
	
	private FingerCheckBiz fingerCheck;

	FingerCheckBiz getFingerCheck() {
		return fingerCheck = fingerCheck == null ? new FingerCheckBiz()
				: fingerCheck;
	}
	// 钞箱交接业务
	private ATMOutAndJoinBiz aTMOutAndJoinBiz;

	ATMOutAndJoinBiz getaTMOutAndJoinBiz() {
		return aTMOutAndJoinBiz = aTMOutAndJoinBiz == null ? new ATMOutAndJoinBiz()
				: aTMOutAndJoinBiz;
	}

	private SaveAuthLogBiz saveAuthLogBiz;

	SaveAuthLogBiz getSaveAuthLogBiz() {
		if (saveAuthLogBiz == null) {
			saveAuthLogBiz = new SaveAuthLogBiz();
		}
		return saveAuthLogBiz;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.y_supercargo_join);

		// 全局异常处理
		// CrashHandler.getInstance().init(this);

		managerClass = new ManagerClass();
		back = (ImageView) findViewById(R.id.back_super);
		msgtext = (TextView) findViewById(R.id.super_msgtext);
		msgimg = (ImageView) findViewById(R.id.super_msgimsg);
		supername = (TextView) findViewById(R.id.super_name);
		supertext = (TextView) findViewById(R.id.supertextjoin);
		finger_img = (ImageView) findViewById(R.id.finger_img);

		supertext.setText("请按压指纹...");
		bundleIntent = getIntent().getExtras();
		plan = bundleIntent.getString("number");
		Log.i("plan", plan);
		busin = bundleIntent.getString("businName");
		type = bundleIntent.getString("type");

		scan = new Scan();
		scan.scan();
		scan.handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				managerClass.getRfid().addNotifly(new GetFingerValue()); // 添加通知
				managerClass.getRfid().fingerOpen(); // 打开指纹

			}
		};

		// 获得指纹通知
		GetFingerValue.handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle;
				if (msg.what == 1) {
					bundle = msg.getData();
					vtoast = GolbalView.getLF(SupercargoJoin.this).inflate(
							R.layout.toast, null);

					if (bundle != null
							&& bundle.getString("finger").equals("获取指纹特征值成功！")) {

						finger_img.setImageBitmap(ShareUtil.finger_bitmap_left);

						// 如果交接成功后，不再执行
						if (isscuess) {
							return;
						}
						/*
						 * revised by zhangXueWei @2016/8/15
						 */
						supertext.setText("正在验证..");
						Finger finger = new Finger();
						finger.setCorpId(GApplication.user
								.getOrganizationId()); // 机构ID
						finger.setRoleId("9"); // 押运员角色ID=9
						finger.setcValue(ShareUtil.ivalBack); // 特征值
						getFingerCheck().fingerLoginCheck(finger);
						

					}

				}
			}
		};
		/*
		 * ATM加钞出库交接
		 * Revised by zhangXueWei 
		 */
		getFingerCheck().hand_finger=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				managerClass.getRuning().remove();
				switch(msg.what){
				case 1://验证通过
					
						System.out.println("通过验证,开始查看押运是否有任务");
						supertext.setText("指纹通过验证");
						Bundle bundle=msg.getData();
						final String name=bundle.getString("username");
						String userId=bundle.getString("userid");
						final SystemUser result_user=new SystemUser();
						result_user.setLoginUserId(userId);
						result_user.setLoginUserName(name);
						managerClass.getSureCancel().makeSuerCancel2(
								SupercargoJoin.this, "押运员："+name,
								new View.OnClickListener() {
									@Override
									public void onClick(View arg0) {
										managerClass.getSureCancel().remove();
										// 交接
										if(busin.equals("ATM加钞出库")){
											Thread thread = new Thread(new IsKongxian(result_user));
											thread.start();
										}else{//回收空钞箱交接
											supertext.setText("开始交接...");
											supername.setText(name);
											join(busin);
										}
										
									}
								}, false);
						
					break;
				case 0:
					supertext.setText("验证失败，请重按");
					break;
				case -1:
					supertext.setText("验证异常，请重按");
					break;
				case -4:
					supertext.setText("验证超时，请重按");
					break;
					
				}
			}
		}; 
		// ATM加钞出库交接
		getaTMOutAndJoinBiz().handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				managerClass.getRuning().remove();

				switch (msg.what) {
				case 1:
					// 交接成功标识
					isscuess = true;
					supertext.setText("出库交接成功..");
					supername.setText(ATMOutAndJoinBiz.user.getName());
					managerClass.getSureCancel().makeSuerCancel(
							SupercargoJoin.this, "交接成功",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getSureCancel().remove();//revised by zhangxuewei
									managerClass.getGolbalutil().gotoActivity(
											SupercargoJoin.this,
											JoinResult.class, bundleIntent, 0);
									// 重置交接状态
									isscuess = false;
									// 关闭当前页面
									SupercargoJoin.this.finish();
								}
							}, true);
					break;
				case -1:
					supertext.setText("出库交接异常..");
					managerClass.getAbnormal().timeout(SupercargoJoin.this,
							"出库交接异常,重试请确定！", new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getAbnormal().remove();
									// 交接
									supertext.setText("正在出库交接...");
									Log.i("我又来交接了", "123");
									// 拼参数
									String cbinfo = getaTMOutAndJoinBiz()
											.getCbInfo(
													plan,
													Getnumber.list_boxdeatil,
													BankDoublePersonLogin.userid1,
													BankDoublePersonLogin.userid2,
													GApplication
															.getApplication().user
															.getOrganizationId());
									// 请求数据
									getaTMOutAndJoinBiz().atmOutAndJoin(
											cbinfo,
											GApplication.getApplication().user
													.getOrganizationId(),
											FixationValue.supercargo + "",
											ShareUtil.ivalBack, "", "");

								}
							});

					break;
				case 0:
					supertext.setText("出库交接失败...");
					managerClass.getSureCancel().makeSuerCancel(
							SupercargoJoin.this, "钞箱交接失败",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getSureCancel().remove();
								}
							}, true);
					break;
				case -4:
					supertext.setText("出库交接超时..");
					managerClass.getAbnormal().timeout(SupercargoJoin.this,
							"出库交接超时,重试请确定！", new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getAbnormal().remove();
									// 交接
									supertext.setText("正在出库交接...");
									Log.i("我又来交接了", "123");
									String cbinfo = getaTMOutAndJoinBiz()
											.getCbInfo(
													plan,
													Getnumber.list_boxdeatil,
													BankDoublePersonLogin.userid1,
													BankDoublePersonLogin.userid2,
													GApplication
															.getApplication().user
															.getOrganizationId());
									getaTMOutAndJoinBiz().atmOutAndJoin(
											cbinfo,
											GApplication.getApplication().user
													.getOrganizationId(),
											FixationValue.supercargo + "",
											ShareUtil.ivalBack, "", "");

								}
							});

					break;

				}

			}

		};

		// 回收钞箱交接
		getSaveAuthLogBiz().handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				managerClass.getRuning().remove();
				super.handleMessage(msg);

				switch (msg.what) {
				case 1:
					// 交接成功标识
					isscuess = true;
					supertext.setText("交接成功");
					try {
						supername.setText(SaveAuthLogBiz.user.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
					managerClass.getSureCancel().makeSuerCancel(
							SupercargoJoin.this, "交接成功",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getGolbalutil().gotoActivity(
											SupercargoJoin.this,
											JoinResult.class, bundleIntent, 0);
									// 重置交接状态
									isscuess = false;
									// 关闭当前页面
									SupercargoJoin.this.finish();
								}
							}, true);

					break;
				case -1:
					supertext.setText("交接超时..");
					managerClass.getAbnormal().timeout(SupercargoJoin.this,
							"连接异常，重试请确定！", new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									supertext.setText("正在交接...");
									managerClass.getRuning().runding(
											SupercargoJoin.this, "正在交接...");
									getSaveAuthLogBiz().getSaveAuthLog(
											plan,
											GetBoxDetailListBiz.list,
											GApplication.getApplication().user
													.getOrganizationId(),
											FixationValue.supercargo + "",
											ShareUtil.ivalBack, "04", "1", "1");

								}
							});
					break;
				case 0:
					supertext.setText("交接失败，请重按");
					break;
				case -4:
					supertext.setText("交接超时..");
					managerClass.getAbnormal().timeout(SupercargoJoin.this,
							"连接超时，重试请确定！", new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									supertext.setText("正在交接...");
									managerClass.getRuning().runding(
											SupercargoJoin.this, "正在交接...");
									getSaveAuthLogBiz().getSaveAuthLog(
											plan,
											GetBoxDetailListBiz.list,
											GApplication.getApplication().user
													.getOrganizationId(),
											FixationValue.supercargo + "",
											ShareUtil.ivalBack, "04", "1", "1");

								}
							});
					break;

				}

			}

		};

		back.setOnTouchListener(this);

		// 定时器
		timer = new Timer();
		// 把当前activity放进集合
		// GApplication.addActivity(this,"0super");

	}

	@Override
	public boolean onTouch(View view, MotionEvent even) {
		// 按下的时候
		if (MotionEvent.ACTION_DOWN == even.getAction()) {
			switch (view.getId()) {
			case R.id.back_super:
				back.setImageResource(R.drawable.back_cirle_press);

				break;
			}
		}

		// 手指松开的时候
		if (MotionEvent.ACTION_UP == even.getAction()) {
			switch (view.getId()) {
			case R.id.back_super:
				back.setImageResource(R.drawable.back_cirle);
				break;
			}
			managerClass.getGolbalutil().ismover = 0;
		}
		// 手指移动的时候
		if (MotionEvent.ACTION_MOVE == even.getAction()) {
			managerClass.getGolbalutil().ismover++;
		}

		// 意外中断事件取消
		if (MotionEvent.ACTION_CANCEL == even.getAction()) {
			back.setImageResource(R.drawable.back_cirle_press);
			managerClass.getGolbalutil().ismover = 0;
		}
		return true;
	}

	/**
	 * 2秒后跳转
	 */
	public void towseconds() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ShareUtil.finger_bitmap_left = null;
				bundle.putString("businName", busin);
				managerClass.getGolbalutil().gotoActivity(SupercargoJoin.this,
						JoinResult.class, bundleIntent, 0);
				// 关闭当前页面
				SupercargoJoin.this.finish();
			}
		}, 2000);
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	// 钞箱交接
	public void join(String biz) {
		if (biz.equals("ATM加钞出库")) {
			supertext.setText("正在出库交接...");
			Log.i("ATM加钞出库", biz + "");
			String cbinfo = getaTMOutAndJoinBiz().getCbInfo(plan,
					Getnumber.list_boxdeatil, BankDoublePersonLogin.userid1,
					BankDoublePersonLogin.userid2,
					GApplication.getApplication().user.getOrganizationId());
			getaTMOutAndJoinBiz().atmOutAndJoin(cbinfo,
					GApplication.getApplication().user.getOrganizationId(),
					FixationValue.supercargo + "", ShareUtil.ivalBack, "", "");
		} else if (biz.equals("网点回收钞箱交接")) {
			// 网点人员交接
			Log.i("网点回收钞箱交接", biz + "");
			supertext.setText("正在交接...");
			managerClass.getRuning().runding(SupercargoJoin.this, "正在交接...");
			if ("在行式".equals(OrderWork.type)) {
				// 在行式交接 type传"04" 离行式交接传"C1"
				getSaveAuthLogBiz().getSaveAuthLog(plan,
						CashboxNumByCorpIdBiz.list,
						GApplication.getApplication().user.getOrganizationId(),
						FixationValue.supercargo + "", ShareUtil.ivalBack,
						type, "1", "1");
			} else {
				getSaveAuthLogBiz().getSaveAuthLog(plan,
						CashboxNumByCorpIdBiz.list,
						GApplication.getApplication().user.getOrganizationId(),
						FixationValue.supercargo + "", ShareUtil.ivalBack,
						"C1", "1", "1");
			}
		}
	}
	
	/**
	 * 判断加钞员是否处于空闲状态线程函数
	 * 
	 * @author Administrator
	 * 
	 */
	private class IsKongxian implements Runnable {
		private SystemUser user;

		public IsKongxian(SystemUser user) {
			this.user = user;
		}

		@Override
		public void run() {
			Message msg = kongxianHandelr.obtainMessage();
			try {
				System.out
						.println("钓调用空闲查询——参数userId:" + user.getLoginUserId());
				String code = new SaveAuthLogService().checkEscort1State(user
						.getLoginUserId());
				System.out
				.println("钓调用空闲查询——结果code:" + code);
				if ("00".equals(code)) {
					msg.what = 1; // 空闲
					msg.obj = user;
				} else if ("01".equals(code)) {
					msg.what = 2; // 被占用
				}else{
					msg.what=4;  //验证异常
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				msg.what = 3;
			} catch (Exception e) {
				e.printStackTrace();
				msg.what = 4;
			} finally {
				kongxianHandelr.sendMessage(msg);
			}
		}
	}

	/*
	 * 空闲验证handler
	 */
	private Handler kongxianHandelr = new Handler() {
		public void handleMessage(Message msg) {
			SystemUser user = (SystemUser) msg.obj;
			switch (msg.what) {
			case 1: // 空闲(可用)
				supertext.setText("开始交接...");
				join(busin);
				break;
			case 2: // 占用(不可用)
				supertext.setText("押运员已有任务！请核查");
				break;
			case 3:// 验证超时
				supertext.setText("验证超时,请重按");
				System.out.println("验证空闲超时");
				break;
			case 4: // 验证异常
				supertext.setText("验证异常,请重按");
				System.out.println("验证空闲异常");
				break;
			}

			GolbalUtil.onclicks = true;
		};
	};

}


