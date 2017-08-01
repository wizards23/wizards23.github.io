package com.out.admin.pda;

import java.net.SocketTimeoutException;

import com.application.GApplication;
import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.loginsystem.biz.SystemLoginBiz;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.Abnormal;
import com.messagebox.MenuShow;
import com.messagebox.Runing;
import com.moneyboxadmin.pda.BoxDoDetail;
import com.moneyboxadmin.pda.ClearerUser;
import com.moneyboxadmin.pda.SupercargoJoin;
import com.out.biz.AssignBiz;
import com.out.biz.CashboxNumByCorpIdBiz;
import com.out.biz.CorpListByPlanNumBiz;
import com.out.service.AssignService;

import android.app.Activity;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TimeFormatException;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OrderWork extends Activity implements OnTouchListener {

	LinearLayout orderwork; // 派工单
	TextView webadd; // 网点加钞钞箱交接
	TextView webback; // 网点回收钞箱交接
	TextView about; // 交接浏览
	TextView plan; // 路线编号
	TextView webaddnum; // 网点所有待交接加钞钞箱数量
	TextView webbacknum; // 网点所有待交接加收钞箱数量
	TextView way; // 路线
	TextView tastdata; // 没有任务
	Bundle bundle;
	
	public static String type;   //交接类型

	View.OnClickListener clcik;  
	private ManagerClass managerClass;
	ImageView refresh;
	ImageView back;
	public static int backCount = 0; // 标识是否为第一次进入

	AssignBiz assign;

	AssignBiz getAssign() {
		return assign = assign == null ? new AssignBiz() : assign;
	}

	SystemLoginBiz systemLogin;

	SystemLoginBiz getSystemLogin() {
		return systemLogin = systemLogin == null ? new SystemLoginBiz()
				: systemLogin;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.y_goout_join);

		// 全局异常处理
		// CrashHandler.getInstance().init(this);

		managerClass = new ManagerClass();
		orderwork = (LinearLayout) findViewById(R.id.orderwork);
		webadd = (TextView) findViewById(R.id.webadd);
		webback = (TextView) findViewById(R.id.webback);
		webaddnum = (TextView) findViewById(R.id.webaddnum);
		webbacknum = (TextView) findViewById(R.id.webbacknum);
		about = (TextView) findViewById(R.id.aboutjoin);
		plan = (TextView) findViewById(R.id.webplan);
		way = (TextView) findViewById(R.id.webway);
		tastdata = (TextView) findViewById(R.id.tast_out);
		refresh = (ImageView) findViewById(R.id.order_refresh);
		back = (ImageView) findViewById(R.id.order_back);

		if ("离行式".equals(type)) {
			System.out.println("111111111");
			webadd.setText("离行加钞钞箱交接");
			webback.setText("离行回收钞箱交接");
		} else {
			System.out.println("22222222222222");
			webadd.setText("网点加钞钞箱交接");
			webback.setText("网点回收钞箱交接");
			
		}

		orderwork.setOnTouchListener(this);
		webadd.setOnTouchListener(this);
		webback.setOnTouchListener(this);
		about.setOnTouchListener(this);
		refresh.setOnTouchListener(this);
		back.setOnTouchListener(this);

		// 重试单击事件
		clcik = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				managerClass.getRuning()
						.runding(OrderWork.this, "正在获取派工单信息...");
				// 重新请求数据
				Thread thread=new Thread(new ChaxunJihuadanLeixing(GApplication.user.getYonghuZhanghao()));
				thread.start();
			}
		};

		// handler 通知(获取计划任务)
		getAssign().handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				managerClass.getRuning().remove();

				switch (msg.what) {
				case 1:
					if (getAssign().order.getCarBrand().equals("没有做派工任务")) {
						orderwork.setVisibility(View.GONE);
						tastdata.setVisibility(View.VISIBLE);
						tastdata.setText("您没有做派工任务");
					} else {
						orderwork.setVisibility(View.VISIBLE);
						tastdata.setVisibility(View.GONE);
						webaddnum.setText(getAssign().order.getAddMoneyJoin());
						webbacknum.setText(getAssign().order.getBackNum());
						plan.setText(getAssign().order.getPlanNum());
						way.setText(getAssign().order.getWay());
					}
					break;
				case -1:
					managerClass.getAbnormal().timeout(OrderWork.this,
							"连接异常，重新连接？", clcik);
					break;
				case 0:
					orderwork.setVisibility(View.GONE);
					tastdata.setVisibility(View.VISIBLE);
					break;
				case -4:
					managerClass.getAbnormal().timeout(OrderWork.this,
							"连接超时，重新连接？", clcik);
					break;
				}

			}
		};

	}

	@Override
	protected void onStart() {
		super.onStart();
		managerClass.getRuning().runding(this, "正在获取派工单信息...");
		Thread thread=new Thread(new ChaxunJihuadanLeixing(GApplication.user.getYonghuZhanghao()));
		thread.start();
	}

	// 触摸事件
	@Override
	public boolean onTouch(View view, MotionEvent even) {
		// 按下的时候
		if (MotionEvent.ACTION_DOWN == even.getAction()) {
			switch (view.getId()) {
			// 网点加钞钞箱交接
			case R.id.webadd:
				webadd.setBackgroundResource(R.color.gray_background);
				break;
			// 网点回收钞箱交接
			case R.id.webback:
				webback.setBackgroundResource(R.color.gray_background);
				break;
			// 交接情况
			case R.id.aboutjoin:
				about.setBackgroundResource(R.color.gray_background);
				break;
			// 派工单
			case R.id.orderwork:
				orderwork.setBackgroundResource(R.color.gray_background);
				break;
			// 刷新
			case R.id.order_refresh:
				refresh.setImageResource(R.drawable.refplace_cirle_press);
				break;

			// 返回
			case R.id.order_back:
				back.setImageResource(R.drawable.back_cirle_press);
				break;

			}

		}

		// 手指松开的时候
		if (MotionEvent.ACTION_UP == even.getAction()) {
			bundle = new Bundle();
			switch (view.getId()) {
			// 网点加钞钞箱交接
			case R.id.webadd:
				webadd.setBackgroundResource(R.color.transparency);
				if (!webaddnum.getText().equals("0")) {
					if ("在行式".equals(type)) {
						bundle.putString("clear", "网点加钞钞箱交接");
					} else {
						bundle.putString("clear", "离行加钞钞箱交接");
					}
					bundle.putString("number", plan.getText().toString());
					managerClass.getGolbalutil().gotoActivity(OrderWork.this,
							WebSiteJoin.class, bundle,
							managerClass.getGolbalutil().ismover);
				} else {
					managerClass.getGolbalView().toastShow(OrderWork.this,
							"没有任务");
				}

				break;
			// 网点回收钞箱交接
			case R.id.webback:
				webback.setBackgroundResource(R.color.transparency);
				if (!webbacknum.getText().equals("0")) {
					if ("在行式".equals(type)) {
						bundle.putString("clear", "网点回收钞箱交接");
					} else {
						bundle.putString("clear", "离行回收钞箱交接");
					}

					bundle.putString("number", plan.getText().toString());
					if (backCount == 0) {
						backCount++;
						try {
							CorpListByPlanNumBiz.list.clear();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					managerClass.getGolbalutil().gotoActivity(OrderWork.this,
							WebSiteJoin.class, bundle,
							managerClass.getGolbalutil().ismover);
				} else {
					managerClass.getGolbalView().toastShow(OrderWork.this,
							"没有任务");
				}

				break;
			// 交接情况
			case R.id.aboutjoin:
				about.setBackgroundResource(R.color.transparency);
				if (AssignBiz.order == null) {
					Toast.makeText(OrderWork.this, "暂时没有情况可查看",
							Toast.LENGTH_SHORT).show();
				} else {
					managerClass.getGolbalutil().gotoActivity(OrderWork.this,
							AboutJoin.class, bundle,
							managerClass.getGolbalutil().ismover);
				}

				break;
			// 派工单
			case R.id.orderwork:
				orderwork.setBackgroundResource(R.color.transparency);
				managerClass.getGolbalutil().gotoActivity(OrderWork.this,
						OrderWorkInformation.class, null,
						managerClass.getGolbalutil().ismover);
				break;
			// 刷新
			case R.id.order_refresh:
				refresh.setImageResource(R.drawable.refplace_cirle);
				managerClass.getRuning().runding(this, "正在获取派工单信息...");
				// 获取派工单列表，参数：用户ID
				if ("在行式".equals(type)) {
					getAssign().getAssig(GApplication.user.getYonghuZhanghao(),
							"0");
				} else {
					getAssign().getAssig(GApplication.user.getYonghuZhanghao(),
							"1");
				}
				break;

			// 返回
			case R.id.order_back:
				back.setImageResource(R.drawable.back_cirle);
				OrderWork.this.finish();
				break;

			}

		}

		managerClass.getGolbalutil().ismover = 0;

		// 手指移动的时候
		if (MotionEvent.ACTION_MOVE == even.getAction()) {
			managerClass.getGolbalutil().ismover++;
		}
		// 意外中断事件取消
		if (MotionEvent.ACTION_CANCEL == even.getAction()) {
			managerClass.getGolbalutil().ismover = 0;
			switch (view.getId()) {
			// 网点加钞钞箱交接
			case R.id.webadd:
				webadd.setBackgroundResource(R.color.transparency);
				break;
			// 网点回收钞箱交接
			case R.id.webback:
				webback.setBackgroundResource(R.color.transparency);
				break;
			// 交接情况
			case R.id.aboutjoin:
				about.setBackgroundResource(R.color.transparency);
				break;
			// 派工单
			case R.id.orderwork:
				orderwork.setBackgroundResource(R.color.transparency);
				break;
			// 刷新
			case R.id.order_refresh:
				refresh.setImageResource(R.drawable.refplace_cirle);
				break;

			// 返回
			case R.id.order_back:
				back.setImageResource(R.drawable.back_cirle);
				break;

			}

		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("a");
		return true;
	}

	// 拦截Menu
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		new MenuShow().menu(this);
		MenuShow.pw.showAtLocation(findViewById(R.id.orderwork_box),
				Gravity.BOTTOM, 0, 0);
		return false;
	}

	/**
	 * 
	 * @author Administrator
	 * 
	 */
	@SuppressWarnings("unused")
	private class ChaxunJihuadanLeixing implements Runnable {

		private String userId;  //押运员ID
		Message msg;
		
		public ChaxunJihuadanLeixing(String userId){
			this.userId=userId;
		}
		
		@Override
		public void run() {
			msg=leixingHandler.obtainMessage();
			// TODO Auto-generated method stub
			try {
				System.out.println("开始查询计划单类别——参数[userId]："+userId);
				String typeString=new AssignService().getJihuadanLeibie(userId);
				if(typeString!=null){
					msg.what=1;
					msg.obj=typeString;
					System.out.println("计划单类别为："+typeString);
				}else{
					msg.what=-1;
					System.out.println("没有查询出计划单编号类别");
				}
			}catch(SocketTimeoutException e1){
				msg.what=3;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg.what=-1;
				System.out.println("查询计划单类别失败");
			}finally{
				leixingHandler.sendMessage(msg);
			}
		}

	}

	/**
	 * ChaxunJihuadanLeixing Handelr
	 */
	private Handler leixingHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				type=msg.obj.toString();   //将计划单模式赋值给type
				// 获取派工单列表，参数：用户ID
				Log.i("login_username",
						GApplication.getApplication().app_hash.get("login_username")
								.toString());
				if ("在行式".equals(type)) { 
					getAssign().getAssig(GApplication.user.getYonghuZhanghao(), "0");
					webadd.setText("网点加钞钞箱交接");
					webback.setText("网点回收钞箱交接");
				} else {
					getAssign().getAssig(GApplication.user.getYonghuZhanghao(), "1");
					webadd.setText("离行加钞钞箱交接");
					webback.setText("离行回收钞箱交接");
				};
				break;

			case -1:
				managerClass.getAbnormal().timeout(OrderWork.this,
						"查询异常，重新查询？", clcik);
				break;
			case 3:
				managerClass.getAbnormal().timeout(OrderWork.this,
						"查询超时，重新查询？", clcik);
				break;
			
			}
		}
	};

}
