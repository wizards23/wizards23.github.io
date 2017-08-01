package com.moneyboxadmin.pda;

import hdjc.rfid.operator.IRFID_Device;
import hdjc.rfid.operator.RFID_Device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.application.GApplication;
import com.clearadmin.pda.ClearManager;
import com.entity.BoxDetail;
import com.entity.Finger;
import com.example.app.activity.LookStorageTaskListActivity;
import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.imple.getnumber.GetFingerValue;
import com.imple.getnumber.Getnumber;
import com.imple.getnumber.StopNewClearBox;
import com.imple.getnumber.WebJoin;
import com.loginsystem.biz.SystemLoginBiz;
import com.main.pda.FingerGather;
import com.main.pda.Scan;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.biz.FingerCheckBiz;
import com.moneyboxadmin.biz.GetBoxDetailListBiz;
import com.moneyboxadmin.biz.MoneyBoxOutDoBiz;
import com.moneyboxadmin.biz.SaveAuthLogBiz;
import com.out.admin.pda.ClearMachineIing;
import com.out.admin.pda.OrderWork;
import com.out.admin.pda.WebSiteJoin;
import com.poka.device.ShareUtil;
import com.service.FixationValue;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 双人指纹交接
 * 
 * @author Administrator
 * 
 */
public class BankDoublePersonLogin extends Activity implements OnTouchListener {

	ImageView backimg;
	TextView name1; // 名字1
	TextView name2; // 名字2
	TextView show; // 提示信息
	TextView resulttext; // 登陆结果
	ImageView resultimg; // 登陆结果
	TextView resultmsg; // 中间提示
	Timer timer;
	TextView loginTitle; // 标题
	String busin;// 业务
	TextView username1; // 库管员1
	TextView username2; // 库管员2
	ImageView finger_left; // 左手指纹图
	ImageView finger_right; // 左手指纹图
	String clearjoin;
	String bizNum;
	StringBuffer sb1 = new StringBuffer();
	StringBuffer sb2 = new StringBuffer();
	View vtoast;
	// int count; //按压手指次数
	String admin;
	Bundle bundleIntent;
	Bundle bundle;
	String where; // 标识是从清分管理进入
	public static String userid1; // 角色ID
	public static String userid2; // 角色ID
	public static boolean firstSuccess = false; // 第一位是否已成功验证指纹
	private boolean isscuess = false; // 是否已交接成功
	Scan scan;
	String f1; // 第一个按手指的人
	String f2; // 第二个按手指的人
	String type = null; // 交接类型
	String planNum; // 计划编号

	String user = null;
	String userid = null;

	public static String textname1; // 姓名1
	public static String textname2; // 姓名2

	String bizName;
	/**
	 * 钞箱编号字符串（ZH0001|ZH0002）
	 */
	private String cashBoxNums;
	
	boolean first = true; // 第一次进入的
	// IRFID_Device irfid;
	OnClickListener clickreplace;
	private FingerCheckBiz fingerCheck;

	FingerCheckBiz getFingerCheck() {
		return fingerCheck = fingerCheck == null ? new FingerCheckBiz()
				: fingerCheck;
	}
	private MoneyBoxOutDoBiz moneyBoxOutDo;
	MoneyBoxOutDoBiz getMoneyBoxOutDo() {
		return moneyBoxOutDo = moneyBoxOutDo == null ? new MoneyBoxOutDoBiz()
				: moneyBoxOutDo;
	}
	SaveAuthLogBiz saveAuthLogBiz;

	SaveAuthLogBiz getSaveAuthLogBiz() {
		return saveAuthLogBiz = saveAuthLogBiz == null ? new SaveAuthLogBiz()
				: saveAuthLogBiz;
	}
	
	private GolbalUtil golbalUtil;		
	 public GolbalUtil getGolbalUtil() {
	 return golbalUtil=golbalUtil==null?new GolbalUtil():golbalUtil;
	 }
	private ManagerClass managerClass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.y_bank_double_person_login);

		managerClass = new ManagerClass();
		// 全局异常处理

		backimg = (ImageView) findViewById(R.id.back_bank);
		resultimg = (ImageView) findViewById(R.id.resultimg);
		username1 = (TextView) findViewById(R.id.name1);
		username2 = (TextView) findViewById(R.id.name2);
		name1 = (TextView) findViewById(R.id.username1);
		name2 = (TextView) findViewById(R.id.username2);

		show = (TextView) findViewById(R.id.show);
		resultmsg = (TextView) findViewById(R.id.resultmsg);
		resulttext = (TextView) findViewById(R.id.resulttext);
		loginTitle = (TextView) findViewById(R.id.login_title);
		finger_left = (ImageView) findViewById(R.id.finger_left);
		finger_right = (ImageView) findViewById(R.id.finger_right);
		clickreplace = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 开始出库操作
				getMoneyBoxOutDo().getemptyMoneyBoxoutdo(busin,
						StopNewClearBox.list, planNum,
						BankDoublePersonLogin.userid1,
						BankDoublePersonLogin.userid2,
						GApplication.user.getOrganizationId(), bizNum,
						BoxDetailInfoDo.isfirst);
				managerClass.getAbnormal().remove();

				managerClass.getRuning().runding(BankDoublePersonLogin.this,
						"操作正在执行,请等待...");
			}
		};
		
		// 获得指纹通知
		GetFingerValue.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle;

				if (msg.what == 1) {
					Log.i("finger", ShareUtil.fingerImg + "");
					Log.i("fingerval", ShareUtil.ivalBack + "");
					// managerClass.getGolbalutil().gotoActivity(BankDoublePersonLogin.this,MoneyBoxManager.class,
					// bundleIntent, GolbalUtil.ismover);
					bundle = msg.getData();
					vtoast = GolbalView.getLF(BankDoublePersonLogin.this)
							.inflate(R.layout.toast, null);
					TextView text = (TextView) vtoast.findViewById(R.id.toast);

					if (bundle != null
							&& bundle.getString("finger").equals("正在获取指纹特征值！")) {

					} else if (bundle != null
							&& bundle.getString("finger").equals("获取指纹特征值成功！")) {

						// 1.第一位交接人员验证指纹
						if (!firstSuccess) {
							finger_left
									.setImageBitmap(ShareUtil.finger_bitmap_left);
							f1 = "1";

							resultmsg.setText(String.valueOf("正在验证..."));
							Finger finger = new Finger();
							finger.setCorpId(GApplication.user
									.getOrganizationId()); // 机构ID
							finger.setRoleId(GApplication.user.getLoginUserId()); // 角色ID
							finger.setcValue(ShareUtil.ivalBack); // 特征值

							if (type == null) { // 登录
								// 登录
								getFingerCheck().fingerLoginCheck(finger);
							} else { // 交接 WebSiteJoin.webJoinID
								if (admin.equals("加钞人员")) {
									//
									Log.i("开始交接", "开始交接");
									if ("在行式".equals(OrderWork.type)) {
										getSaveAuthLogBiz().getSaveAuthLog(
												planNum, WebJoin.list,
												WebSiteJoin.webJoinID,
												getUserID(type),
												ShareUtil.ivalBack, type, f1,
												"2");
									} else {
										getSaveAuthLogBiz().getSaveAuthLog(
												planNum,
												WebJoin.list,
												GApplication.user
														.getOrganizationId(),
												"5", ShareUtil.ivalBack, "C0",
												f1, "2");
									}

								} else {
									// 如果是空钞箱交接
									if ("01".equals(type)) {
									 	getSaveAuthLogBiz().getSaveAuthLog(
												planNum,
												Getnumber.list_boxdeatil,
												GApplication.user
														.getOrganizationId(),
												getUserID(type),
												ShareUtil.ivalBack, type, f1,
												"2");
									} else {//当前判断分支是：未清回收钞箱出库
										//modify begin 2017-4-25 by liuchang  清分员指纹验证通过后交接的钞箱数量有原来通过sql查询，
										//改为通过页面中“钞箱出入库前检测”按钮检测后确认的数量
										String[] cashBoxArray = cashBoxNums.split(";");
										ArrayList<BoxDetail> cashBoxList = new ArrayList<BoxDetail>();
										for(int i=0;i<cashBoxArray.length;i++){
											BoxDetail boxDetail = new BoxDetail();
											boxDetail.setNum(cashBoxArray[i]);
											cashBoxList.add(boxDetail);
										}
										//modify end
										getSaveAuthLogBiz().getSaveAuthLog(
												planNum,
												cashBoxList,
												GApplication.user
														.getOrganizationId(),
												getUserID(type),	
												ShareUtil.ivalBack, type, f1,
												"2");
									}
									Log.i("aaaaaaa", "sss");
								}
							}

						// 2.第二位交接人验证指纹
						} else {
							finger_right
									.setImageBitmap(ShareUtil.finger_bitmap_right);
							f2 = "2";
							resultmsg.setText(String.valueOf("正在验证..."));

							if (type == null) { // 登录
								Finger finger = new Finger();
								finger.setCorpId(GApplication.user
										.getOrganizationId()); // 机构ID
								finger.setRoleId(GApplication.user
										.getLoginUserId()); // 角色ID
								finger.setcValue(ShareUtil.ivalBack); // 特征值
								getFingerCheck().fingerLoginCheck(finger);
							} else { // 交接 WebSiteJoin
								if (isscuess) {
									return;
								}
								if (admin.equals("加钞人员")) {
									if ("在行式".equals(OrderWork.type)) {
										getSaveAuthLogBiz().getSaveAuthLog(
												planNum, WebJoin.list,
												WebSiteJoin.webJoinID,
												getUserID(type),
												ShareUtil.ivalBack, type, f2,
												"2");
									} else {
										getSaveAuthLogBiz().getSaveAuthLog(
												planNum,
												WebJoin.list,
												GApplication.user
														.getOrganizationId(),
												"5", ShareUtil.ivalBack, "C0",
												f2, "2");
									}

								} else {
									// 如果是空钞箱交接
									if ("01".equals(type)) {
										getSaveAuthLogBiz().getSaveAuthLog(
												planNum,
												Getnumber.list_boxdeatil,
												GApplication.user
														.getOrganizationId(),
												getUserID(type),
												ShareUtil.ivalBack, type, f2,
												"2");
									} else {
										// 未清回收钞箱出库交接
										//modify begin 2017-4-25 by liuchang  清分员指纹验证通过后交接的钞箱数量有原来通过sql查询，
										//改为通过页面中“钞箱出入库前检测”按钮检测后确认的数量
										String[] cashBoxArray = cashBoxNums.split(";");
										ArrayList<BoxDetail> cashBoxList = new ArrayList<BoxDetail>();
										for(int i=0;i<cashBoxArray.length;i++){
											BoxDetail boxDetail = new BoxDetail();
											boxDetail.setNum(cashBoxArray[i]);
											cashBoxList.add(boxDetail);
										}
										//modify end
										getSaveAuthLogBiz().getSaveAuthLog(
												planNum,
												cashBoxList,
												GApplication.user
														.getOrganizationId(),
												getUserID(type),
												ShareUtil.ivalBack, type, f2,
												"2");
									}
								}

							}
						}
					}
				}
			}

		};
		
		getMoneyBoxOutDo().hand_out = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				managerClass.getRuning().remove();
				super.handleMessage(msg);

				switch (msg.what) {
				case 1:

					break;
				case 0:
					managerClass.getSureCancel().makeSuerCancel(
							BankDoublePersonLogin.this, busin + "失败",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getSureCancel().remove();
								}
							}, true);
					break;
				case -1:
					managerClass.getAbnormal().timeout(BankDoublePersonLogin.this,
							"连接异常，要重试吗？", clickreplace);
					break;
				case -4:
					managerClass.getAbnormal().timeout(BankDoublePersonLogin.this,
							"连接超时，要重试吗？", clickreplace);
					break;
				}

			}
		};

		
		// 钞箱交接
		getSaveAuthLogBiz().handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				bundle = msg.getData();

				switch (msg.what) {
				case 1:
					System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaa");
					firstSuccess = true;
					fingerDo();
					break;
				case -1:
					resultmsg.setText("验证异常，请重按");
					break;
				case -4:
					resultmsg.setText("网络超时，请重按");
					break;
				case 0:
					resultmsg.setText("验证失败，请重按");
					break;
				case 2:
					resultmsg.setText("验证身份无效，请重按");
					break;
				}

			}

		};

		bundleIntent = getIntent().getExtras();

		if (bundleIntent != null) {
			admin = bundleIntent.getString("user");
			where = bundleIntent.getString("where");
			type = bundleIntent.getString("type");
			planNum = bundleIntent.getString("planNum");
			Log.i("planNum", planNum + "");
			bizName = bundleIntent.getString("busin");
			clearjoin = bundleIntent.getString("clearjoin");
			bizNum = bundleIntent.getString("bizNum");
			cashBoxNums = bundleIntent.getString("cashBoxNums");
		}

		name1.setText(admin);
		name2.setText(admin);

		if ("库管员".equals(admin)) {
			loginTitle.setText(admin + "双人登陆");
			show.setText("请第一位库管员按压手指...");
		} else if ("清分员".equals(admin) && !"清分管理".equals(where)) {
			loginTitle.setText(admin + "交接");
			show.setText("请第一位清分员按压手指...");
		} else if ("清分员".equals(admin) && "清分管理".equals(where)) {
			show.setText("请第一位清分员按压手指...");
			loginTitle.setText("清分管理登录");
		} else if ("加钞人员".equals(admin)) {
			show.setText("请第一位加钞人员按压手指...");
			loginTitle.setText("加钞人员交接");
		} else if ("清分员交接".equals(clearjoin)) {
			show.setText("请第一位清分人员按压手指...");
			loginTitle.setText("清分员交接");
		}

		backimg.setOnTouchListener(this);

		// 登录验证
		getFingerCheck().hand_finger = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				bundle = msg.getData();
				int num = bundle.getInt("num");
				super.handleMessage(msg);

				switch (msg.what) {
				case 1:
					// 指纹登录操作及提示
					firstSuccess = true;
					fingerDo();
					break;
				case -1:
					resultmsg.setText("验证异常，请重按");
					break;
				case -4:
					resultmsg.setText("验证超时，请重按");
					break;
				case 0:
					resultmsg.setText("验证失败，请重按");
					break;
				}

			}
		};

		// 0.6秒后开启指纹
		scan = new Scan();
		scan.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				managerClass.getRfid().addNotifly(new GetFingerValue()); // 添加通知
				managerClass.getRfid().fingerOpen(); // 打开指纹
			}
		};

		scan.scan();
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	public boolean onTouch(View view, MotionEvent even) {
		// 按下的时候
		if (MotionEvent.ACTION_DOWN == even.getAction()) {
			switch (view.getId()) {
			case R.id.back_bank:
				backimg.setImageResource(R.drawable.back_cirle_press);
				break;
			}
		}

		// 手指松开的时候
		if (MotionEvent.ACTION_UP == even.getAction()) {
			switch (view.getId()) {
			case R.id.back_bank:
				backimg.setImageResource(R.drawable.back_cirle);
				BankDoublePersonLogin.this.finish();
				firstSuccess = false;
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
			case R.id.back_bank:
				backimg.setImageResource(R.drawable.back_cirle);
				break;
			}
		}
		return true;
	}

	/**
	 * 2秒后自动跳下一个页面
	 */
	public void towseconds() {

		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				type = null;
				firstSuccess = false;
				// 库管员
				if (GApplication.user.getLoginUserId().equals("4")) {
					String taskType = BankDoublePersonLogin.this.bundleIntent.getString("taskType");
					/*
					 * 判断是否为查库服务
					 * @author zhouKai
					 */
					if (taskType != null && taskType.equals("lookStorageService")) {
						gotoLookStorageTaskListActivity();
					} else {
						managerClass.getGolbalutil().gotoActivity(BankDoublePersonLogin.this,
								MoneyBoxManager.class, bundleIntent, GolbalUtil.ismover);
					}
					BankDoublePersonLogin.this.finish();
					// 清分员
				} else if (GApplication.user.getLoginUserId().equals("7")
						&& !"清分管理".equals(where)) {
					managerClass.getGolbalutil().gotoActivity(
							BankDoublePersonLogin.this, JoinResult.class,
							bundleIntent, GolbalUtil.ismover);
					BankDoublePersonLogin.this.finish();
					// 清分管理
				} else if ("清分管理".equals(where)
						&& GApplication.user.getLoginUserId().equals("7")) {
					managerClass.getGolbalutil().gotoActivity(
							BankDoublePersonLogin.this, ClearManager.class,
							bundleIntent, GolbalUtil.ismover);
					BankDoublePersonLogin.this.finish();
					// 网点加钞
				} else if ("加钞人员".equals(admin)) {
					managerClass.getGolbalutil().gotoActivity(
							BankDoublePersonLogin.this, ClearMachineIing.class,
							bundleIntent, GolbalUtil.ismover);
					BankDoublePersonLogin.this.finish();
					// 未清回收钞箱
				} else if (type.equals("03")) {
					managerClass.getGolbalutil().gotoActivity(
							BankDoublePersonLogin.this, MoneyBoxManager.class,
							bundleIntent, GolbalUtil.ismover);
					BankDoublePersonLogin.this.finish();
				}
				// 重置交接状态
				isscuess = false;
				ShareUtil.finger_bitmap_left = null;
				ShareUtil.finger_bitmap_right = null;
				// 关闭当前页面
				BankDoublePersonLogin.this.finish();

			}
		}, 2000);

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	// 接钞箱编号
	String getBoxNum() {
		StringBuffer boxNum = null; // 钞箱编号
		for (int i = 0; i < GetBoxDetailListBiz.list.size(); i++) {
			if (GetBoxDetailListBiz.list.size() == 1) {
				return boxNum.toString();
			} else {
				boxNum.append(GetBoxDetailListBiz.list.get(i).getNum());
				boxNum.append("|");
			}
		}
		return boxNum.toString().substring(0, boxNum.lastIndexOf("|"));
	}

	// 指纹登录操作及提示
	void fingerDo() {

		int num = bundle.getInt("num");
		// 如果是同一个人
		if (username1.getText() != null
				&& !(username1.getText() + "").equals("")) {
			num = 2;
		}

		switch (num) {
		case 1:
			System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
			user = bundle.getString("username"); // 用户名
			userid = bundle.getString("userid"); // 用户ID
			username1.setText("姓名:" + user);
			textname1 = admin + ": " + user; // 头部显示用的用户名
			userid1 = userid;
			resultmsg.setText("成功1位");
			finger_left.setVisibility(View.VISIBLE);
			show.setText("请第二位" + admin + "按压手指..");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;

		case 2:
			System.out.println("cccccccccccccccccccccccc");
			getFingerCheck().num = 0;
			
			user = bundle.getString("username"); // 用户名
			userid = bundle.getString("userid"); // 用户ID
			userid2 = userid;
			Log.i("userid1", userid1);
			Log.i("userid2", userid2);

			// 如果是同一个人
			if (userid1.equals(userid2)) {
				resultmsg.setText("不可重复登录");
				return;
			}

			username2.setText("姓名:" + user);
			textname2 = admin + ": " + user; // 头部显示用的用户名
			resultmsg.setText("成功2位");
			finger_right.setVisibility(View.VISIBLE);
			show.setVisibility(View.GONE);
			resulttext.setVisibility(View.VISIBLE);
			resultimg.setVisibility(View.VISIBLE);
			// 交接成功2位后，不再交接
			isscuess = true;
			/*
			 * revised by zhangxuewei
			 */	
			
			if(bizName!=null && bizName.equals("未清回收钞箱出库")){
				System.out.println(StopNewClearBox.list.size());
				System.out.println(bizName);
				System.out.println("bizNum"+bizNum);
				getGolbalUtil().onclicks=true;
				getMoneyBoxOutDo().getemptyMoneyBoxoutdo(bizName,
						StopNewClearBox.list, planNum,
						BankDoublePersonLogin.userid1,
						BankDoublePersonLogin.userid2,
						GApplication.user.getOrganizationId(), bizNum,
						BoxDetailInfoDo.isfirst);
			}
			// 2秒后跳转
			towseconds();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
		firstSuccess = false;
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
	
	/**
	 * 跳转到查库服务任务单列表界面
	 * @author zhouKai
	 */
	private void gotoLookStorageTaskListActivity() {
		Bundle bundle = new Bundle();
		bundle.putString("nameOne", this.username1.getText().toString().replaceAll("姓名:", ""));
		bundle.putString("nameTwo", this.username2.getText().toString().replaceAll("姓名:", ""));
		bundle.putString("codeOne", BankDoublePersonLogin.userid1);
		bundle.putString("codeTwo", BankDoublePersonLogin.userid2);
		managerClass.getGolbalutil().gotoActivity(BankDoublePersonLogin.this,
												  LookStorageTaskListActivity.class,
												  bundle, GolbalUtil.ismover);
	}
	

}
