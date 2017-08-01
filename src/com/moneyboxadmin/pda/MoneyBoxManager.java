package com.moneyboxadmin.pda;

import java.text.ParseException;

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
import com.messagebox.SureCancelButton;
import com.moneyboxadmin.biz.AddNewStopCheck;
import com.moneyboxadmin.biz.GetPlanWayListBiz;
import com.moneyboxadmin.biz.GetBoxNumberBiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MoneyBoxManager extends Activity implements OnTouchListener {
	// 钞箱管理

	TextView emptyboxout; // 空钞箱出库
	TextView atmaddmoneyout; // ATM加钞出库
	TextView notcleanboxout; // 未清回收钞箱出库
	TextView stopmoneyboxout; // 停用钞箱出库
	TextView moneyboxin; // 钞箱装钞入库
	TextView backmoneyboxin; // 回收钞箱入库
	TextView cleanmoneyboxin; // 已清回收钞箱入库
	TextView addmoneyboxin; // 新增钞箱入库
	Bundle bundle; // 把当前选择的业务传到下一个页面
	ImageView back; // 返回上一级
	ImageView refresh; // 刷新
	boolean isup; // 弹起事件是否有执行

	TextView empty; // 待处理业务数量
	TextView atm; // 待处理业务数量
	TextView notclear; // 待处理业务数量
	TextView backbox; // 待处理业务数量
	TextView clear; // 待处理业务数量
	TextView putin; // 待处理业务数量

	OnClickListener clcikReplace;
	private ManagerClass magagerClass;

	private GetBoxNumberBiz boxNumber;

	public GetBoxNumberBiz getBoxNumber() {
		return boxNumber = boxNumber == null ? new GetBoxNumberBiz()
				: boxNumber;
	}

	private SystemLoginBiz systemLogin;

	public SystemLoginBiz getSystemLoginss() {
		return systemLogin = systemLogin == null ? new SystemLoginBiz()
				: systemLogin;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 禁止休睡眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.y_money_box_manage);

		// 全局异常处理
		// CrashHandler.getInstance().init(this);

		magagerClass = new ManagerClass();
		emptyboxout = (TextView) findViewById(R.id.emptyboxout);
		atmaddmoneyout = (TextView) findViewById(R.id.atmaddout);
		notcleanboxout = (TextView) findViewById(R.id.notclearout);
		stopmoneyboxout = (TextView) findViewById(R.id.stopout);
		moneyboxin = (TextView) findViewById(R.id.moneyboxin);
		backmoneyboxin = (TextView) findViewById(R.id.backmoneyboxin);
		cleanmoneyboxin = (TextView) findViewById(R.id.cleanmoneyboxin);
		addmoneyboxin = (TextView) findViewById(R.id.addmoneyboxin);
		back = (ImageView) findViewById(R.id.back);
		refresh = (ImageView) findViewById(R.id.refresh);

		empty = (TextView) findViewById(R.id.emptybox); // 空钞箱出库
		atm = (TextView) findViewById(R.id.atmbox); // ATM加钞
		notclear = (TextView) findViewById(R.id.notclearbox); // 未清回收钞箱出库
		backbox = (TextView) findViewById(R.id.backbox); // 回收钞箱入库
		clear = (TextView) findViewById(R.id.clearbox); // 已清回收钞箱入库
		putin = (TextView) findViewById(R.id.putinbox); // 钞箱装钞入库

		emptyboxout.setOnTouchListener(this);
		atmaddmoneyout.setOnTouchListener(this);
		notcleanboxout.setOnTouchListener(this);
		stopmoneyboxout.setOnTouchListener(this);
		moneyboxin.setOnTouchListener(this);
		backmoneyboxin.setOnTouchListener(this);
		cleanmoneyboxin.setOnTouchListener(this);
		addmoneyboxin.setOnTouchListener(this);
		back.setOnTouchListener(this);
		refresh.setOnTouchListener(this);

		try {
			magagerClass.getGolbalutil().gettime();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		// 重试单击事件
		clcikReplace = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 获取未处理的业务数量
				magagerClass.getRuning().runding(MoneyBoxManager.this,
						"正在获取未处理业务数量...");
				getBoxNumber().getBoxNum(GApplication.user.getOrganizationId());
				magagerClass.getAbnormal().remove();
			}

		};

		// ATM钞箱管理获取钞箱出库数量和钞箱入库数量通知
		getBoxNumber().hand_boxNum = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				magagerClass.getRuning().remove();
				super.handleMessage(msg);

				switch (msg.what) {
				case 1:
					if (getBoxNumber().box.getState().equals("成功")) {
						update();
					}
					;

					break;
				case -4:
					// 连接超时提示
					magagerClass.getAbnormal().timeout(MoneyBoxManager.this,
							"连接超时！要重试吗？", clcikReplace);
					break;
				case -1:
					// 连接超时提示
					magagerClass.getAbnormal().timeout(MoneyBoxManager.this,
							"连接异常！要重试吗？", clcikReplace);
					break;

				}

			}
		};

		// 把当前activity放进集合
		// GApplication.addActivity(this,"1monbyboxmanager");

	}

	// 测试注释
	@Override
	protected void onStart() {
		super.onStart();
		// 获取未处理的业务数量
		magagerClass.getRuning().runding(this, "正在获取未处理业务数量...");
		// 参数为机构ID
		try {
			getBoxNumber().getBoxNum(GApplication.user.getOrganizationId());
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	// 触摸事件
	@Override
	public boolean onTouch(View view, MotionEvent even) {

		// 当按下的时候
		if (MotionEvent.ACTION_DOWN == even.getAction()) {
			switch (view.getId()) {
			// 空钞箱出库
			case R.id.emptyboxout:
				emptyboxout.setBackgroundResource(R.color.bleu_pressdown);
				break;
			// ATM加钞出库
			case R.id.atmaddout:
				atmaddmoneyout.setBackgroundResource(R.color.bleu_pressdown);
				break;
			// 未清回收钞箱出库
			case R.id.notclearout:
				notcleanboxout.setBackgroundResource(R.color.bleu_pressdown);
				break;
			// 停用钞箱出库
			case R.id.stopout:
				stopmoneyboxout.setBackgroundResource(R.color.bleu_pressdown);
				break;
			// 钞箱装钞入库
			case R.id.moneyboxin:
				moneyboxin.setBackgroundResource(R.color.bleu_pressdown);
				break;
			// 回收钞箱入库
			case R.id.backmoneyboxin:
				backmoneyboxin.setBackgroundResource(R.color.bleu_pressdown);
				break;
			// 已清钞箱回收入库
			case R.id.cleanmoneyboxin:
				cleanmoneyboxin.setBackgroundResource(R.color.bleu_pressdown);
				break;
			// 新增钞箱入库
			case R.id.addmoneyboxin:
				addmoneyboxin.setBackgroundResource(R.color.bleu_pressdown);
				break;
			// 返回上一级
			case R.id.back:
				back.setImageResource(R.drawable.back_cirle_press);
				break;
			// 刷新
			case R.id.refresh:
				refresh.setImageResource(R.drawable.refplace_cirle_press);

				break;
			}

		}

		// 当移动的时候
		if (MotionEvent.ACTION_MOVE == even.getAction()) {
			magagerClass.getGolbalutil().ismover++;
		}

		// 当松手的的时候
		if (MotionEvent.ACTION_UP == even.getAction()) {
			bundle = new Bundle();
			isup = true;
			switch (view.getId()) {
			// 空钞箱出库
			case R.id.emptyboxout:
				emptyboxout.setBackgroundResource(R.color.transparency);
				if (empty.getText().equals("0")) {
					GolbalView.toastShow(MoneyBoxManager.this, "没有要执行的任务");
				} else {
					bundle.putString("business", "空钞箱出库");
					magagerClass.getGolbalutil().gotoActivity(
							MoneyBoxManager.this, PlanWay.class, bundle,
							magagerClass.getGolbalutil().ismover);
				}

				break;
			// ATM加钞出库
			case R.id.atmaddout:
				atmaddmoneyout.setBackgroundResource(R.color.transparency);
				if (atm.getText().equals("0")) {
					GolbalView.toastShow(MoneyBoxManager.this, "没有要执行的任务");
				} else {
					bundle.putString("business", "ATM加钞出库");
					magagerClass.getGolbalutil().gotoActivity(
							MoneyBoxManager.this, PlanWay.class, bundle,
							magagerClass.getGolbalutil().ismover);
				}

				break;
			// 未清回收钞箱出库
			case R.id.notclearout:
				// 重置清空上次的数据
				new AddNewStopCheck().clearNull();
				notcleanboxout.setBackgroundResource(R.color.transparency);
				if (notclear.getText().equals("0")) {
					GolbalView.toastShow(MoneyBoxManager.this, "没有要执行的任务");
				} else {
					bundle.putString("business", "未清回收钞箱出库");
					magagerClass.getGolbalutil().gotoActivity(
							MoneyBoxManager.this, BoxAddStop.class, bundle,
							magagerClass.getGolbalutil().ismover);
				}

				break;
			// 停用钞箱出库
			case R.id.stopout:
				// 重置清空上次的数据
				new AddNewStopCheck().clearNull();

				stopmoneyboxout.setBackgroundResource(R.color.transparency);
				bundle.putString("business", "停用钞箱出库");

				magagerClass.getGolbalutil().gotoActivity(MoneyBoxManager.this,
						BoxAddStop.class, bundle,
						magagerClass.getGolbalutil().ismover);
				break;
			// 钞箱装钞入库
			case R.id.moneyboxin:
				moneyboxin.setBackgroundResource(R.color.transparency);
				if (putin.getText().equals("0")) {
					GolbalView.toastShow(MoneyBoxManager.this, "没有要执行的任务");
				} else {
					bundle.putString("business", "钞箱装钞入库");
					magagerClass.getGolbalutil().gotoActivity(
							MoneyBoxManager.this, PlanWay.class, bundle,
							magagerClass.getGolbalutil().ismover);
				}

				break;
			// 回收钞箱入库
			case R.id.backmoneyboxin:
				backmoneyboxin.setBackgroundResource(R.color.transparency);
				if (backbox.getText().equals("0")) {
					GolbalView.toastShow(MoneyBoxManager.this, "没有要执行的任务");
				} else {
					bundle.putString("business", "回收钞箱入库");
					magagerClass.getGolbalutil().gotoActivity(
							MoneyBoxManager.this, PlanWay.class, bundle,
							magagerClass.getGolbalutil().ismover);
				}

				break;
			// 已清回收钞箱收入库
			case R.id.cleanmoneyboxin:
				cleanmoneyboxin.setBackgroundResource(R.color.transparency);
				if (clear.getText().equals("0")) {
					GolbalView.toastShow(MoneyBoxManager.this, "没有要执行的任务");
				} else {
					bundle.putString("business", "已清回收钞箱入库");
					magagerClass.getGolbalutil().gotoActivity(
							MoneyBoxManager.this, PlanWay.class, bundle,
							magagerClass.getGolbalutil().ismover);
				}

				break;
			// 新增钞箱入库
			case R.id.addmoneyboxin:
				// 重置清空上次的数据
				new AddNewStopCheck().clearNull();

				addmoneyboxin.setBackgroundResource(R.color.transparency);
				bundle.putString("business", "新增钞箱入库");
				magagerClass.getGolbalutil().gotoActivity(MoneyBoxManager.this,
						BoxAddStop.class, bundle,
						magagerClass.getGolbalutil().ismover);
				break;
			// 返回上一级
			case R.id.back:
				back.setImageResource(R.drawable.back_cirle);
				new SureCancelButton().makeSuerCancel(MoneyBoxManager.this,
						"您确认要出退钞箱业务操作？", new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								MoneyBoxManager.this.finish();

							}
						}, true);

				break;
			// 刷新
			case R.id.refresh:
				refresh.setImageResource(R.drawable.refplace_cirle);
				magagerClass.getRuning().runding(this, "正在刷新数据...");
				// 参数为机构ID
				getBoxNumber().getBoxNum(GApplication.user.getOrganizationId());
				break;
			}

			magagerClass.getGolbalutil().ismover = 0;
		}

		// 当弹起因意外打断的时候
		if (MotionEvent.ACTION_CANCEL == even.getAction()) {
			magagerClass.getGolbalutil().ismover = 0;
			if (!isup) {
				emptyboxout.setBackgroundResource(R.color.transparency);
				atmaddmoneyout.setBackgroundResource(R.color.transparency);
				notcleanboxout.setBackgroundResource(R.color.transparency);
				stopmoneyboxout.setBackgroundResource(R.color.transparency);
				moneyboxin.setBackgroundResource(R.color.transparency);
				backmoneyboxin.setBackgroundResource(R.color.transparency);
				cleanmoneyboxin.setBackgroundResource(R.color.transparency);
				addmoneyboxin.setBackgroundResource(R.color.transparency);
				back.setImageResource(R.drawable.back_cirle_press);
				refresh.setImageResource(R.drawable.refplace_cirle);
			}
		}
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * 更新未处理烽业务数据
	 */
	public void update() {
		empty.setText(getBoxNumber().box.getEmpty());
		atm.setText(getBoxNumber().box.getAtm());
		notclear.setText(getBoxNumber().box.getNotclear());
		backbox.setText(getBoxNumber().box.getBack());
		putin.setText(getBoxNumber().box.getPutin());
		clear.setText(getBoxNumber().box.getClear());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// menu.add("a");
		return true;
	}

	// //拦截Menu
	// @Override
	// public boolean onMenuOpened(int featureId, Menu menu) {
	// new MenuShow().menu(this);
	// MenuShow.pw.showAtLocation(findViewById(R.id.managerparent),
	// Gravity.BOTTOM, 0, 0);
	// return false;
	// }

}
