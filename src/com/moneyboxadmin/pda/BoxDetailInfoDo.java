package com.moneyboxadmin.pda;

import com.clearadmin.biz.CashboxAddMoneyDetailBiz;
import com.clearadmin.pda.BackMoneyBoxCount;
import com.clearadmin.pda.ClearAddMoneyOutDo;
import com.example.pda.R;
import com.fragment.pda.BackMoneyBoxIn_fragment;
import com.fragment.pda.ClearBackMoneyBoxIn_fragment;
import com.fragment.pda.EmptyBoxOut_fragment;
import com.golbal.pda.GolbalView;
import com.imple.getnumber.Getnumber;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.biz.GetBoxDetailListBiz;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ATM加钞出库明细
 * 
 * @author Administrator
 * 
 */
public class BoxDetailInfoDo extends Activity implements OnTouchListener {
	// 钞箱出库明细,主要负责中转

	Button box_detail; // 钞箱明细
	Button box_do; // 出库操作
	String planNum; // 计划编号
	String way; // 路线
	Bundle bundleBussin; // 接收业务
	String busin; // 业务名称
	TextView bizName; // 业务名称显示控件

	public static int isfirst = 0; // 回收钞箱入库是否是首次入库
	LinearLayout fragment_layout; // 装载fragment

	Fragment atmf; // ATM加钞出库
	Fragment backf; // 回收钞库入库
	Fragment clearbackf; // 已清回收钞库入库
	Fragment emptyf; // 空钞箱出库
	Fragment putinf; // 钞箱装钞入库
	ImageView back_pre; // 返回上一级

	private GetBoxDetailListBiz boxDetailList;

	GetBoxDetailListBiz getBoxDetailList() {
		return boxDetailList = boxDetailList == null ? new GetBoxDetailListBiz()
				: boxDetailList;
	}

	private CashboxAddMoneyDetailBiz cashboxAddMoneyDetail;

	CashboxAddMoneyDetailBiz getCashboxAddMoneyDetail() {
		return cashboxAddMoneyDetail = cashboxAddMoneyDetail == null ? new CashboxAddMoneyDetailBiz()
				: cashboxAddMoneyDetail;
	}

	private ManagerClass managerClass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.y_emptvan_goout_detail);
		managerClass = new ManagerClass();
		managerClass.getRuning().remove();

		// 全局异常处理
		// CrashHandler.getInstance().init(this);

		Log.i("11", "111");
		box_detail = (Button) findViewById(R.id.money_box_detail);
		box_do = (Button) findViewById(R.id.box_do);
		back_pre = (ImageView) findViewById(R.id.back_empty);
		bizName = (TextView) findViewById(R.id.bizname);

		box_detail.setOnTouchListener(this);
		box_do.setOnTouchListener(this);
		back_pre.setOnTouchListener(this);

		// 接收传进来的bundle
		bundleBussin = getIntent().getExtras();
		planNum = bundleBussin.getString("number");
		way = bundleBussin.getString("way");
		try {
			isfirst = Integer.parseInt(bundleBussin.getString("isfirst"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		busin = bundleBussin.getString("businName");
		bizName.setText(busin + "明细");

		Log.i("busin", busin);

		// 判断是何种操作
		if ("空钞箱出库".equals(busin) || "ATM加钞出库".equals(busin)
				|| "未清回收钞箱出库".equals(busin)) {
			box_do.setText("出库操作");

		} else if ("钞箱装钞入库".equals(busin) || "回收钞箱入库".equals(busin)
				|| "已清回收钞箱入库".equals(busin) || "新增钞箱入库".equals(busin)) {
			box_do.setText("入库操作");

		} else if ("钞箱加钞".equals(busin)) {
			box_do.setText("加钞操作");
		}
		Log.i("22", "22");

		// 判断加载那个fragment
		if ("空钞箱出库".equals(busin) || "钞箱加钞".equals(busin)
				|| "ATM加钞出库".equals(busin) || "钞箱装钞入库".equals(busin)) {
			if (emptyf == null) {
				emptyf = new EmptyBoxOut_fragment();
			}
			managerClass.getGolbalView().replaceFragment(this,
					R.id.fragment_address, emptyf, bundleBussin);
		} else if ("回收钞箱入库".equals(busin)) {
			if (backf == null) {
				backf = new BackMoneyBoxIn_fragment();
			}
			managerClass.getGolbalView().replaceFragment(this,
					R.id.fragment_address, backf, bundleBussin);

		} else if ("已清回收钞箱入库".equals(busin)) {

			if (clearbackf == null) {
				clearbackf = new ClearBackMoneyBoxIn_fragment();
			}
			managerClass.getGolbalView().replaceFragment(this,
					R.id.fragment_address, clearbackf, bundleBussin);

		} else if ("回收钞箱清点".equals(busin)) { // 界面特殊，这次跳到别的activity
			this.finish();
			managerClass.getGolbalutil().gotoActivity(BoxDetailInfoDo.this,
					BackMoneyBoxCount.class, bundleBussin,
					managerClass.getGolbalutil().ismover);
		}

		// 把当前activity放进集合
		// GApplication.addActivity(this,"0boxd");

	}

	// 触摸事件
	@Override
	public boolean onTouch(View view, MotionEvent even) {
		// 按下的时候
		if (MotionEvent.ACTION_DOWN == even.getAction()) {
			switch (view.getId()) {
			// 返回
			case R.id.back_empty:
				back_pre.setImageResource(R.drawable.back_cirle_press);
				break;
			// 钞箱明细
			case R.id.money_box_detail:
				box_detail
						.setBackgroundResource(R.drawable.buttom_select_press);
				break;
			case R.id.box_do:
				// 出库
				box_do.setBackgroundResource(R.drawable.buttom_select_press);
				break;
			}
		}

		// 手指松开的时候
		if (MotionEvent.ACTION_UP == even.getAction()) {
			switch (view.getId()) {
			// 钞箱明细
			case R.id.money_box_detail:
				box_detail.setBackgroundResource(R.drawable.buttom_selector_bg);
				if (isfirst > 0) {
					managerClass.getSureCancel().makeSuerCancel(
							BoxDetailInfoDo.this, "首次回收钞箱入库不用获取明细",
							new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									managerClass.getSureCancel().remove();

								}
							}, true);
				} else {
					managerClass.getGolbalutil().gotoActivity(
							BoxDetailInfoDo.this, MoneyBoxDetial.class,
							bundleBussin, managerClass.getGolbalutil().ismover);
				}
				break;
			// 各种操作，出库
			case R.id.box_do:
				// //出库前清空前一次的数据
				if (Getnumber.list_boxdeatil.size() > 0
						|| Getnumber.map.size() > 0) {
					try {
						Getnumber.list_boxdeatil.clear();
						Getnumber.map.clear();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				Log.i("map", Getnumber.map.size() + "");
				Log.i("list_boxdeatil", Getnumber.list_boxdeatil.size() + "");
				box_do.setBackgroundResource(R.drawable.buttom_selector_bg);

				// 如果是首次回收钞箱入库
				if (isfirst > 0) {
					managerClass.getGolbalutil().gotoActivity(
							BoxDetailInfoDo.this, BoxDoDetail.class,
							bundleBussin, managerClass.getGolbalutil().ismover);
				} else {

					if ("钞箱加钞".equals(busin)) {
						if (getCashboxAddMoneyDetail().list == null
								|| getCashboxAddMoneyDetail().list.size() <= 0) {
							GolbalView.toastShow(BoxDetailInfoDo.this,
									"请先获取钞箱明细，再进行出入库。");
						} else {

							managerClass.getGolbalutil().gotoActivity(
									BoxDetailInfoDo.this,
									ClearAddMoneyOutDo.class, bundleBussin,
									managerClass.getGolbalutil().ismover);
						}

					} else {
						if (getBoxDetailList().list == null
								|| getBoxDetailList().list.size() <= 0) {
							GolbalView.toastShow(BoxDetailInfoDo.this,
									"请先获取钞箱明细，再进行出入库。");
						} else {

							managerClass.getGolbalutil().gotoActivity(
									BoxDetailInfoDo.this, BoxDoDetail.class,
									bundleBussin,
									managerClass.getGolbalutil().ismover);
						}
					}

				}

				break;
			// 返回
			case R.id.back_empty:
				back_pre.setImageResource(R.drawable.back_cirle);
				BoxDetailInfoDo.this.finish();
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
			back_pre.setImageResource(R.drawable.back_cirle_press);

			switch (view.getId()) {
			// 返回
			case R.id.back_empty:
				back_pre.setImageResource(R.drawable.back_cirle);
				break;
			// 钞箱明细
			case R.id.money_box_detail:
				box_detail.setBackgroundResource(R.drawable.buttom_selector_bg);
				break;
			case R.id.box_do:
				// 出库
				box_do.setBackgroundResource(R.drawable.buttom_selector_bg);
				break;
			}
			managerClass.getGolbalutil().ismover = 0;
		}
		return true;
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

}
