package com.moneyboxadmin.pda;

import hdjc.rfid.operator.RFID_Device;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.application.GApplication;
import com.entity.Box;
import com.entity.BoxDetail;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.imple.getnumber.StopNewClearBox;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.biz.BrandNameByCboxNumBiz;
import com.moneyboxadmin.biz.EmptyOutCheck;
import com.moneyboxadmin.biz.GetBoxDetailListBiz;
import com.moneyboxadmin.biz.MoneyBoxOutDoBiz;
import com.moneyboxadmin.service.GetBrandNameByCboxNumService;
/*
 * 出库扫描活动
 */
public class BoxAddStop extends Activity {

	// 不用交接
	LinearLayout lbiz;
	Bundle bundle_biz;// 接收务业标识
	String bundle_biz_value;
	TextView title; // 标题
	TextView boxs; // 扫描的箱子数量
	TextView biztitle;// 业务单号
	TextView bizvalue; // 业务单号值
	String busin;// 业务
	Fragment stopf; // 停用钞箱出库
	Fragment notclearf; // 未清回收钞箱出库
	Fragment addf; // 新增钞箱入库
	ListView listview;
	ImageView back_noway; // 返回
	Button btncheck; // 出入库前检测
	Button out;
	Button cancel;
	String planNum;
	String bizNum; // 业务单号
	OnClickListener clickreplace;
	OnClickListener clickreplace2;
	EmptyOutCheck ch;
	Timer timer1;
	private RFID_Device rfid;

	RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}

	Ad ad;
	boolean del = true;
	private MoneyBoxOutDoBiz moneyBoxOutDo;

	MoneyBoxOutDoBiz getMoneyBoxOutDo() {
		return moneyBoxOutDo = moneyBoxOutDo == null ? new MoneyBoxOutDoBiz()
				: moneyBoxOutDo;
	}

	private GetBrandNameByCboxNumService boxService;

	GetBrandNameByCboxNumService getBoxService() {
		return boxService = boxService == null ? new GetBrandNameByCboxNumService()
				: boxService;
	}

	private BrandNameByCboxNumBiz brandNameByCboxNumBiz;

	BrandNameByCboxNumBiz getBrandNameByCboxNumBiz() {
		if (brandNameByCboxNumBiz == null) {
			brandNameByCboxNumBiz = new BrandNameByCboxNumBiz();
		}
		return brandNameByCboxNumBiz;
	}

	// 钞箱明细类
	private GetBoxDetailListBiz BoxDetailListBiz;

	GetBoxDetailListBiz getBoxDetailListBiz() {
		if (BoxDetailListBiz == null) {
			BoxDetailListBiz = new GetBoxDetailListBiz();
		}
		return BoxDetailListBiz;
	}

	private ManagerClass managerClass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.noway_notjoin);
		new GolbalView().Init(this);
		// 全局异常
		// CrashHandler.getInstance().init(this);

		managerClass = new ManagerClass();
		title = (TextView) findViewById(R.id.title_noway);
		bizvalue = (TextView) findViewById(R.id.biznumbervalue);
		listview = (ListView) findViewById(R.id.listview_noway);
		back_noway = (ImageView) findViewById(R.id.backnoway);
		out = (Button) findViewById(R.id.noway_out);
		cancel = (Button) findViewById(R.id.cancel_do);
		boxs = (TextView) findViewById(R.id.addstopnew_boxcount);
		btncheck = (Button) findViewById(R.id.outberforcheck);
		lbiz = (LinearLayout) findViewById(R.id.lbiz_name);

		btncheck.setOnTouchListener(new Toucth());
		back_noway.setOnTouchListener(new Toucth());
		cancel.setOnTouchListener(new Toucth());

		ch = new EmptyOutCheck();

		// 产生业务单号
		bizNum = createBizNumber();
		Log.i("bizNum", bizNum);

		timer1 = new Timer();
		// 接收参数
		bundle_biz = getIntent().getExtras();
		if (bundle_biz != null) {
			busin = bundle_biz.getString("business");
			planNum = bundle_biz.getString("number");
		}

		back_noway.setOnTouchListener(new Toucth());
		out.setOnTouchListener(new Toucth());

		// 未清回收钞箱handler明细
		getBoxDetailListBiz().hand_detail = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				managerClass.getRuning().remove();
				super.handleMessage(msg);

				switch (msg.what) {
				case 1:
					// 信息提示
					Log.i("sssssss", "ssssssssss");
					managerClass.getGolbalView().toastShow(BoxAddStop.this,
							"获取成功，开启扫描");
					// 打开扫描
					getRfid().open_a20();

					break;
				case -1:
					managerClass.getAbnormal().timeout(BoxAddStop.this,
							"获取异常！重试请确定", new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getAbnormal().remove();
									managerClass.getRuning().runding(
											BoxAddStop.this, "正在获取未清钞箱明细");
									getBoxDetailListBiz().getBoxDetailList(
											GApplication.user
													.getOrganizationId(),
											"未清回收钞箱出库");
								}
							});
					break;
				case 0:
					managerClass.getSureCancel().makeSuerCancel(
							BoxAddStop.this, "获取失败,找不到数据",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getSureCancel().remove();
								}
							}, true);
					break;
				case -4:
					managerClass.getAbnormal().timeout(BoxAddStop.this,
							"获取超时！重试请确定", new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getAbnormal().remove();
									managerClass.getRuning().runding(
											BoxAddStop.this, "正在获取未清钞箱明细");
									getBoxDetailListBiz().getBoxDetailList(
											GApplication.user
													.getOrganizationId(),
											"未清回收钞箱出库");
								}
							});
					break;

				}

			}

		};

		if ("停用钞箱出库".equals(busin)) {
			title.setText("停用钞箱出库");
			bizvalue.setVisibility(View.GONE);
			out.setText("出库");
			lbiz.setVisibility(View.GONE);

			StopNewClearBox.type = 0;

		} else if ("未清回收钞箱出库".equals(busin)) {
			title.setText("未清回收钞箱出库");
			bizvalue.setVisibility(View.VISIBLE);
			// 设置业务单号
			bizvalue.setText(bizNum);
			out.setText("出库");
			lbiz.setVisibility(View.VISIBLE);
			managerClass.getRuning().runding(this, "正在获取清钞箱明细");
			getBoxDetailListBiz().getBoxDetailList(
					GApplication.user.getOrganizationId(), "未清回收钞箱出库");

			StopNewClearBox.type = 4;

		} else if ("新增钞箱入库".equals(busin)) {
			title.setText("新增钞箱入库");
			bizvalue.setVisibility(View.GONE);
			out.setText("入库");
			lbiz.setVisibility(View.GONE);

			StopNewClearBox.type = 0;

		}

		// listview.setAdapter(new Ad());
		// 重试单击事件
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

				managerClass.getRuning().runding(BoxAddStop.this,
						"操作正在执行,请等待...");
			}
		};

		clickreplace2 = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getRfid().close_a20();
				managerClass.getRuning()
						.runding(BoxAddStop.this, "正在核实钞箱信息...");
				StringBuffer sb = new StringBuffer();
				for (BoxDetail box : StopNewClearBox.list) {
					sb.append(box.getNum() + ";");
				}

				String boxString = sb.toString();
				if (!"".equals(boxString)) {
					boxString = boxString.substring(0, boxString.length() - 1);
				}

				Thread thread = new Thread(new boxList(boxString));
				thread.start();
			}
		};

		// 根据出库操作返回的结果通知进行下一步操作
		getMoneyBoxOutDo().hand_out = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				managerClass.getRuning().remove();
				super.handleMessage(msg);

				switch (msg.what) {
				case 1:
//					if ("未清回收钞箱出库".equals(busin)) {
//
//						bundle_biz.putString("type", "03");
//						bundle_biz.putString("user", "清分员");
//						bundle_biz.putString("clearjoin", "清分员交接");
//						bundle_biz.putString("number", bizNum);
//
//						managerClass.getGolbalutil().gotoActivity(
//								BoxAddStop.this, BankDoublePersonLogin.class,
//								bundle_biz, 0);
//					} else {
						managerClass.getResultmsg().resultmsg(BoxAddStop.this,
								busin + "成功");
						OutDo();
//					}

					break;
				case 0:
					managerClass.getSureCancel().makeSuerCancel(
							BoxAddStop.this, busin + "失败",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getSureCancel().remove();
								}
							}, true);
					break;
				case -1:
					managerClass.getAbnormal().timeout(BoxAddStop.this,
							"连接异常，要重试吗？", clickreplace);
					break;
				case -4:
					managerClass.getAbnormal().timeout(BoxAddStop.this,
							"连接超时，要重试吗？", clickreplace);
					break;
				}

			}
		};

		// 钞箱扫描后
		StopNewClearBox.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 1) {
					boxs.setText(StopNewClearBox.list.size() + "");
					del = true;
					if (ad == null) {
						ad = new Ad();
						listview.setAdapter(ad);
					} else {
						ad.notifyDataSetChanged();
					}
					out.setEnabled(false);
					out.setBackgroundResource(R.drawable.button_gray);
				}
			}

		};

		getBrandNameByCboxNumBiz().handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 1) {
					if (ad == null) {
						ad = new Ad();
						listview.setAdapter(ad);
					} else {
						ad.notifyDataSetChanged();
					}
				}
			}

		};

		// 把当前activity放进集合
		// GApplication.addActivity(this,"0boxa");

	}

	// 触摸事件

	class Toucth implements OnTouchListener {
		@Override
		public boolean onTouch(View view, MotionEvent even) {
			// 按下的时候
			if (MotionEvent.ACTION_DOWN == even.getAction()) {

				switch (view.getId()) {
				// 返回
				case R.id.backnoway:
					back_noway.setImageResource(R.drawable.back_cirle_press);
					break;
				// 出库
				case R.id.noway_out:
					out.setBackgroundResource(R.drawable.buttom_select_press);

					break;
				// 取消
				case R.id.cancel_do:
					cancel.setBackgroundResource(R.drawable.buttom_select_press);
					break;
				// 检测
				case R.id.outberforcheck:
					btncheck.setBackgroundResource(R.drawable.buttom_select_press);
					break;
				}

			}

			// 手指松开的时候
			if (MotionEvent.ACTION_UP == even.getAction()) {
				switch (view.getId()) {

				// 返回
				case R.id.backnoway:

					StopNewClearBox.liststr.clear();
					BoxAddStop.this.finish();

					break;
				// 出库
				case R.id.noway_out:
					out.setBackgroundResource(R.drawable.buttom_selector_bg);
					
					/*
					 * revised by zhangxuewei,此处如果未清出库，需要不向后台传
					 */
					if ("未清回收钞箱出库".equals(busin)){
						bundle_biz.putString("type", "03");
						bundle_biz.putString("user", "清分员");
						bundle_biz.putString("clearjoin", "清分员交接");
						bundle_biz.putString("bizNum", bizNum);
						bundle_biz.putString("busin", busin);
						System.out.println("busin"+busin);
						bundle_biz.putString("planNum", planNum);
						StringBuffer sb = new StringBuffer();
						for (BoxDetail box : StopNewClearBox.list) {
							sb.append(box.getNum() + ";");
						}
						bundle_biz.putString("cashBoxNums",sb.toString());
						managerClass.getGolbalutil().gotoActivity(
								BoxAddStop.this, BankDoublePersonLogin.class,
								bundle_biz, 0);
					}else{
						managerClass.getRuning().runding(BoxAddStop.this,
								"操作正在执行...");
						getMoneyBoxOutDo().getemptyMoneyBoxoutdo(busin,
								StopNewClearBox.list, planNum,
								BankDoublePersonLogin.userid1,
								BankDoublePersonLogin.userid2,
								GApplication.user.getOrganizationId(), bizNum,
								BoxDetailInfoDo.isfirst);
					}
					break;
				// 取消
				case R.id.cancel_do:
					cancel.setBackgroundResource(R.drawable.buttom_selector_bg);
					StopNewClearBox.list.clear();
					StopNewClearBox.liststr.clear();
					boxs.setText("0");
					out.setEnabled(false);
					out.setBackgroundResource(R.drawable.button_gray);
					getRfid().start_a20();
					try {
						ad.notifyDataSetChanged();
					} catch (Exception e) {
					}

					break;
				// 出入库前检测
				case R.id.outberforcheck:
					btncheck.setBackgroundResource(R.drawable.buttom_selector_bg);
					getRfid().close_a20();
					// 检测是否所有钞箱都已经获取到了品牌
					if (ch.checkBox(StopNewClearBox.list)) {
						// 检查钞箱是否都是按以ATM为组的单位在出库
						managerClass.getRuning().runding(BoxAddStop.this,
								"正在核实钞箱信息...");
						StringBuffer sb = new StringBuffer();
						for (BoxDetail box : StopNewClearBox.list) {
							sb.append(box.getNum() + ";");
						}

						String boxString = sb.toString();
						if (!"".equals(boxString)) {
							boxString = boxString.substring(0,
									boxString.length() - 1);
						}

						Thread thread = new Thread(new boxList(boxString));
						thread.start();

					} else {
						GolbalView.toastShow(BoxAddStop.this, "存在无效钞箱");
					}

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
				back_noway.setImageResource(R.drawable.back_cirle_press);
				out.setBackgroundResource(R.drawable.buttom_selector_bg);
				cancel.setBackgroundResource(R.drawable.buttom_selector_bg);
				btncheck.setBackgroundResource(R.drawable.buttom_selector_bg);

			}
			return true;
		}

	}

	// 适配器
	class Ad extends BaseAdapter {

		@Override
		public int getCount() {
			return StopNewClearBox.list.size();

		}

		@Override
		public Object getItem(int arg0) {
			return StopNewClearBox.list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {

			View v = view;
			final ViewHolder holder;
			if (v == null) {
				v = GolbalView.getLF(BoxAddStop.this).inflate(
						R.layout.boxinformation_item, null);
				holder = new ViewHolder();
				holder.brand = (TextView) v.findViewById(R.id.brand);
				holder.Num = (TextView) v.findViewById(R.id.box_num);
				holder.delText = (TextView) v
						.findViewById(R.id.newstop_item_del);
				holder.cancelText = (TextView) v
						.findViewById(R.id.newstop_item_cancel);
				holder.llayout = (LinearLayout) v
						.findViewById(R.id.lboxdodetail_item);
				holder.menu = (LinearLayout) v
						.findViewById(R.id.newstop_item_menu);

				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			BoxDetail box = (BoxDetail) getItem(arg0);

			// 隔行变色
			if (arg0 % 2 == 0) {
				holder.llayout.setBackgroundColor(Color.parseColor("#dce8ef"));
			} else {
				holder.llayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
			}

			holder.brand.setText(box.getBrand());
			holder.Num.setText(box.getNum());

			// 删除触摸事件
			holder.delText.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent even) {
					// 按下的时候
					if (MotionEvent.ACTION_DOWN == even.getAction()) {
						view.setBackgroundResource(R.color.blue_title);
					}
					// 手指松开的时候
					if (MotionEvent.ACTION_UP == even.getAction()) {
						view.setBackgroundResource(R.color.transparency);
						for (int i = 0; i < StopNewClearBox.list.size(); i++) {
							String num = StopNewClearBox.list.get(i).getNum();
							if (holder.Num.getText().equals(num)) {
								StopNewClearBox.list.remove(i);
								boxs.setText(StopNewClearBox.list.size() + "");
								StopNewClearBox.liststr.remove(num);
								out.setEnabled(false);
								out.setBackgroundResource(R.drawable.button_gray);
								if (del) {
									getRfid().start_a20();
									del = false;
								}

								break;
							}
						}
						ad.notifyDataSetChanged();
						holder.menu.setVisibility(view.GONE);
					}

					// 意外中断事件取消
					if (MotionEvent.ACTION_CANCEL == even.getAction()) {
						view.setBackgroundResource(R.color.transparency);
					}
					return true;
				}
			});

			// 取消触摸事件
			holder.cancelText.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent even) {
					// 按下的时候
					if (MotionEvent.ACTION_DOWN == even.getAction()) {
						view.setBackgroundResource(R.color.blue_title);
					}
					// 手指松开的时候
					if (MotionEvent.ACTION_UP == even.getAction()) {
						view.setBackgroundResource(R.color.transparency);
						holder.menu.setVisibility(view.GONE);
					}

					// 意外中断事件取消
					if (MotionEvent.ACTION_CANCEL == even.getAction()) {
						view.setBackgroundResource(R.color.transparency);
					}
					return true;
				}
			});

			// 提示触摸事件
			holder.llayout.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent even) {
					// 按下的时候
					if (MotionEvent.ACTION_DOWN == even.getAction()) {
						holder.llayout
								.setBackgroundResource(R.color.gray_msg_bg);
					}
					// 手指松开的时候
					if (MotionEvent.ACTION_UP == even.getAction()) {
						holder.llayout
								.setBackgroundResource(R.color.transparency);
					}

					// 意外中断事件取消
					if (MotionEvent.ACTION_CANCEL == even.getAction()) {
						holder.llayout
								.setBackgroundResource(R.color.transparency);
					}
					return false;
				}
			});

			// 长按事件删除出现
			holder.llayout.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View arg0) {
					holder.menu.setVisibility(View.VISIBLE);
					return true;
				}
			});

			holder.brand.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View vi, MotionEvent even) {
					// 按下的时候
					if (MotionEvent.ACTION_DOWN == even.getAction()) {
						vi.setBackgroundResource(R.color.gray_msg_bg);
					}

					// 手指松开的时候
					if (MotionEvent.ACTION_UP == even.getAction()) {
						vi.setBackgroundResource(R.color.transparency);
						if (GolbalUtil.ismover <= 5
								&& holder.brand.getText().equals("点击获取品牌")) {
							holder.brand.setText("正在获取...");
							// 通过编号获取品牌
							getBrandNameByCboxNumBiz().getBrnadNamebyNum(
									holder.Num.getText().toString(), "");
						}
						GolbalUtil.ismover = 0;
					}
					// 手指移动的时候
					if (MotionEvent.ACTION_MOVE == even.getAction()) {
						GolbalUtil.ismover++;
					}
					// 意外中断事件取消
					if (MotionEvent.ACTION_CANCEL == even.getAction()) {
						vi.setBackgroundResource(R.color.transparency);
						GolbalUtil.ismover = 0;
					}
					return true;
				}
			});

			return v;
		}
	}

	class ViewHolder {
		TextView brand; // 品牌
		TextView Num; // 编号
		TextView delText; // 删除
		TextView cancelText; // 取消
		LinearLayout llayout;
		LinearLayout menu;
	}

	// 出库成功后执行的操作
	public void OutDo() {

		timer1.schedule(new TimerTask() {
			@Override
			public void run() {
				time1500();
				managerClass.getGolbalutil().gotoActivity(BoxAddStop.this,
						MoneyBoxManager.class, bundle_biz, GolbalUtil.ismover);
				StopNewClearBox.liststr.clear();
			}
		}, 2000);
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("onPause");
		getRfid().close_a20();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("onResume");
		getRfid().addNotifly(new StopNewClearBox());
		if (!"未清回收钞箱出库".equals(busin)) {
			getRfid().open_a20();
		}
	}

	public void time1500() {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				managerClass.getResultmsg().remove();
			}
		}, 1500);
	}

	// 产生业务单号
	public String createBizNumber() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddmmSS");
		Date date = new Date();
		String time = GApplication.user.getOrganizationId() + sdf.format(date);
		return time.substring(0, time.length() - 1);

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		StopNewClearBox.liststr.clear();
	}

	/**
	 * 根据钞箱编号查询组
	 * 
	 * @author Administrator
	 * 
	 */
	private class boxList implements Runnable {

		private String boxNum;
		private Message msg;

		public boxList(String boxNum) {
			this.boxNum = boxNum;
			msg = boxListHandelr.obtainMessage();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				List<BoxDetail> list = getBoxService()
						.getBoxDetailInATM(boxNum);
				msg.what = 1;
				msg.obj = list;
			} catch (SocketTimeoutException e1) {
				msg.what = 3;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg.what = -1;
			}

			boxListHandelr.sendMessage(msg);
		}

	}

	/**
	 * 查询钞箱handelr
	 */
	private Handler boxListHandelr = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			managerClass.getRuning().remove();
			
			switch (msg.what) {
			case 1:
				List<BoxDetail> list = (List<BoxDetail>) msg.obj;
				if (list.size() > 0) {
					// 查询到了需要补全的钞箱 弹出提示框
					BoxDialog dialog = new BoxDialog(BoxAddStop.this, list,rfid);
					dialog.show();
				} else {
					// 没有需要补全的钞箱
					out.setEnabled(true);
					out.setBackgroundResource(R.drawable.buttom_selector_bg);
					getRfid().stop_a20();
				}
				break;

			case 3:
				managerClass.getAbnormal().timeout(BoxAddStop.this,
						"连接超时，要重试吗？", clickreplace2);
				break;
			case -1:
				managerClass.getAbnormal().timeout(BoxAddStop.this,
						"连接异常，要重试吗？", clickreplace2);
				break;
			}
		}

	};
}
