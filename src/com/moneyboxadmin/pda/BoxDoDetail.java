package com.moneyboxadmin.pda;

import java.util.Timer;
import java.util.TimerTask;
import com.application.GApplication;
import com.entity.BoxDetail;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.imple.getnumber.GetFingerValue;
import com.imple.getnumber.Getnumber;
import com.imple.getnumber.StopNewClearBox;
import com.loginsystem.biz.SystemLoginBiz;
import com.main.pda.Scan;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.Abnormal;
import com.messagebox.ResultMsg;
import com.messagebox.Runing;
import com.moneyboxadmin.biz.BrandNameByCboxNumBiz;
import com.moneyboxadmin.biz.EmptyOutCheck;
import com.moneyboxadmin.biz.GetBoxDetailListBiz;
import com.moneyboxadmin.biz.GetPlanWayListBiz;
import com.moneyboxadmin.biz.MoneyBoxOutDoBiz;
import com.moneyboxadmin.service.EmptyMoneyBoxOutDoService;
import com.strings.tocase.CaseString;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.AlteredCharSequence;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 出入库操作
 * 
 * @author Administrator
 * 
 */
public class BoxDoDetail extends Activity {

	Button out; // 出库
	Button cancel; // 取消
	Timer t2; // 2秒后跳转
	Timer t1; // 1.5秒后移除出库结果提示
	ListView listview;

	public static String bussin; // 业务名称
	public static String type; // 业务模式
	String planNum; // 计划编号
	String bizNum; // 业务单号
	Button check;

	Bundle bundleBussin;
	OnClickListener clickreplace; // 重试单击事件

	ImageView outdo_back;
	TextView boxcount;
	TextView whatdoing; // 标题
	View v;

	Ad ad;
	Scan scan;

	private MoneyBoxOutDoBiz moneyBoxOutDo;

	MoneyBoxOutDoBiz getMoneyBoxOutDo() {
		return moneyBoxOutDo = moneyBoxOutDo == null ? new MoneyBoxOutDoBiz()
				: moneyBoxOutDo;
	}

	private Getnumber getnumber;

	Getnumber getGetnumber() {
		return getnumber = getnumber == null ? new Getnumber() : getnumber;
	}

	private ManagerClass managerClass;
	private SystemLoginBiz systemLogin;

	SystemLoginBiz getSystemLogin() {
		return systemLogin = systemLogin == null ? new SystemLoginBiz()
				: systemLogin;
	}

	private BrandNameByCboxNumBiz brandNameByCboxNumBiz;

	BrandNameByCboxNumBiz getBrandNameByCboxNumBiz() {
		if (brandNameByCboxNumBiz == null) {
			brandNameByCboxNumBiz = new BrandNameByCboxNumBiz();
		}
		return brandNameByCboxNumBiz;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 禁止休睡眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.c_boxdo_detail);
		managerClass = new ManagerClass();
		// 全局异常处理
		// CrashHandler.getInstance().init(this);
		out = (Button) findViewById(R.id.out);
		cancel = (Button) findViewById(R.id.cancel_do);
		listview = (ListView) findViewById(R.id.listview);
		outdo_back = (ImageView) findViewById(R.id.outdo_back);
		boxcount = (TextView) findViewById(R.id.boxdo_boxcount);
		whatdoing = (TextView) findViewById(R.id.whatdoing);
		check = (Button) findViewById(R.id.btn_check);

		// 接收参数
		bundleBussin = getIntent().getExtras();
		if (bundleBussin != null) {
			bussin = bundleBussin.getString("businName");
			planNum = bundleBussin.getString("number");
			bizNum = bundleBussin.getString("number");
			type = bundleBussin.getString("type");
			// 修改标题
			whatdoing.setText(bussin);
			// 出库或入库
			outText(bussin);
		}

		// 钞箱出入库前检测事件
		check.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EmptyOutCheck ch = new EmptyOutCheck();

				// 空钞箱入库检测
				if (bussin.equals("空钞箱出库")) {
					if (Getnumber.list_boxdeatil.size() == GetBoxDetailListBiz.boxCount) {
						if (ch.check()) {
							out.setEnabled(true);
							out.setBackgroundResource(R.drawable.buttom_selector_bg);
							// 暂停扫描
							managerClass.getRfid().stop_a20();
						} else {
							ch.remove();
							ad.notifyDataSetChanged();
							GolbalView.toastShow(BoxDoDetail.this,
									"请核对每种品牌的箱子的数量");
						}
					} else {
						GolbalView.toastShow(BoxDoDetail.this,
								"当前钞箱的数量与出库的数量不相等("
										+ GetBoxDetailListBiz.boxCount + ")");
					}

					// 其他业务钞箱检测（ATM加钞出库，钞箱装钞入库，回收钞箱入库钞箱,已清回收钞箱）
				} else {
					if (ch.checkBox(Getnumber.list_boxdeatil)) {
						out.setEnabled(true);
						out.setBackgroundResource(R.drawable.buttom_selector_bg);
						// 暂停扫描
						managerClass.getRfid().stop_a20();
					} else {
						GolbalView.toastShow(BoxDoDetail.this, "存在无效钞箱，无法执行操作");
					}

				}

			}
		});

		// 触摸事件
		out.setOnTouchListener(new Touch());
		cancel.setOnTouchListener(new Touch());
		outdo_back.setOnTouchListener(new Touch());

		listview.requestFocusFromTouch();

		t2 = new Timer();
		t1 = new Timer();

		// 连接超时重试单击事件
		clickreplace = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 开始出库操作 参数：bussin, list, planNum,"审核员", "复审核员","机构ID","业务单号"
				if (bussin.equals("空钞箱出库")) {
					getMoneyBoxOutDo().getemptyMoneyBoxoutdo(bussin,
							Getnumber.list_boxdeatil, planNum,
							BankDoublePersonLogin.userid1,
							BankDoublePersonLogin.userid2,
							GApplication.user.getOrganizationId(), bizNum,
							BoxDetailInfoDo.isfirst);
				} else {
					getMoneyBoxOutDo().getemptyMoneyBoxoutdo(bussin,
							GetBoxDetailListBiz.list, planNum,
							BankDoublePersonLogin.userid1,
							BankDoublePersonLogin.userid2,
							GApplication.user.getOrganizationId(), bizNum,
							BoxDetailInfoDo.isfirst);
				}
				// 移除超时提示
				managerClass.getAbnormal().remove();
				// 提示正在出库，同时屏蔽其他后续操作，以防多触发
				managerClass.getRuning().runding(BoxDoDetail.this, "正在出库...");

			}
		};

		// 根据出库操作返回的结果通知进行下一步操作
		getMoneyBoxOutDo().hand_out = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// 移除正在出库操作提示
				managerClass.getRuning().remove();
				super.handleMessage(msg);

				switch (msg.what) {
				case 1:
					managerClass.getResultmsg().resultmsg(BoxDoDetail.this,
							bussin + "成功");
					// 2秒后跳转
					OutDo();
					break;

				case -1:
					managerClass.getAbnormal().timeout(BoxDoDetail.this,
							"连接超时，要重试吗？", clickreplace);
					break;

				case 0:
					managerClass.getSureCancel().makeSuerCancel(
							BoxDoDetail.this, "操作失败",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getSureCancel().remove();
								}
							}, true);
					break;
				}
			}
		};

		// 扫描后通知的操作
		getGetnumber().handler_getNum = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {
				// 检测箱子数量
				case 1:
					boxcount.setText(getGetnumber().list_boxdeatil.size() + "");
					if (ad == null) {
						ad = new Ad();
						listview.setAdapter(ad);
					} else {
						ad.notifyDataSetChanged();
					}
					if (bussin.equals("空钞箱出库")) {
						out.setEnabled(false);
						out.setBackgroundResource(R.drawable.button_gray);
					}
					break;
				}
			}
		};

		// 根据钞箱编号获取品牌通知操作
		getBrandNameByCboxNumBiz().handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				// 有数据成功返回
				if (msg.what == 1) {
					// 更取到品牌后更新listview数据
					ad.notifyDataSetChanged();
				}
			}

		};

		// 把当前activity放进集合
		// GApplication.addActivity(this,"0boxd1");

		// 如果是大于0，表示是首次回收钞箱入库
		if (BoxDetailInfoDo.isfirst > 0) {
			cancel.setVisibility(View.GONE);
			out.setEnabled(true);
			out.setBackgroundResource(R.drawable.buttom_selector_bg);
			out.setText("首次入库");
		} else {

		}

	}

	// 不再检测钞箱上限，都显示出来，不正确的再删除
	public void isEnough() {
		// 判断是不是空钞箱出库
		if (bussin.equals("空钞箱出库")) {
			// 如果扫描到的箱子数量大于等于后台返回的箱子数量
			if (getGetnumber().list_boxdeatil.size() >= GetBoxDetailListBiz.boxCount) {
				Log.i("GetBoxDetailListBiz.boxCount",
						GetBoxDetailListBiz.boxCount + "");
				Log.i("getGetnumber().list_boxdeatil.size()",
						getGetnumber().list_boxdeatil.size() + "");
				// 暂停扫描
				managerClass.getRfid().stop_a20();
			}

		} else {
			// 扫到足够的箱子的时候就关闭扫描
			if (getGetnumber().list_boxdeatil.size() >= GetBoxDetailListBiz.list
					.size()) {
				// 暂停扫描
				managerClass.getRfid().stop_a20();
				out.setEnabled(true);
				out.setBackgroundResource(R.drawable.buttom_selector_bg);
			}
		}

	}

	// 触摸事件
	class Touch implements OnTouchListener {
		@Override
		public boolean onTouch(View view, MotionEvent even) {

			// 按下的时候
			if (MotionEvent.ACTION_DOWN == even.getAction()) {
				switch (view.getId()) {
				// 出库
				case R.id.out:
					out.setBackgroundResource(R.drawable.buttom_select_press);
					break;
				// 取消
				case R.id.cancel_do:
					cancel.setBackgroundResource(R.drawable.buttom_select_press);
					break;
				// 返回
				case R.id.outdo_back:
					outdo_back
							.setBackgroundResource(R.drawable.back_cirle_press);
					break;
				}
			}

			// 手指松开的时候
			if (MotionEvent.ACTION_UP == even.getAction()) {
				switch (view.getId()) {
				// 出库
				case R.id.out:
					out.setBackgroundResource(R.drawable.buttom_selector_bg);
					if (bussin.equals("ATM加钞出库")) {
						/*
						 * 判断出库状态是在行式还是离行式 在行式直接由押运员交接 离航式有加钞员跟押运员一起交接
						 */
						System.out.println("BoxDotail_type:" + type);

						// 在行式
						managerClass.getGolbalutil().gotoActivity(
								BoxDoDetail.this, SupercargoJoin.class,
								bundleBussin, 0);

					} else {
						// 提示正在出库
						managerClass.getRuning().runding(BoxDoDetail.this,
								"正在执行操作...");
						// 开始出库操作 参数：bussin, list, planNum,"审核员",
						// "复审核员","机构ID","业务单号"
						if (bussin.equals("空钞箱出库")) {
							getMoneyBoxOutDo().getemptyMoneyBoxoutdo(bussin,
									Getnumber.list_boxdeatil, planNum,
									BankDoublePersonLogin.userid1,
									BankDoublePersonLogin.userid2,
									GApplication.user.getOrganizationId(),
									bizNum, BoxDetailInfoDo.isfirst);
						} else {
							getMoneyBoxOutDo().getemptyMoneyBoxoutdo(bussin,
									GetBoxDetailListBiz.list, planNum,
									BankDoublePersonLogin.userid1,
									BankDoublePersonLogin.userid2,
									GApplication.user.getOrganizationId(),
									bizNum, BoxDetailInfoDo.isfirst);
						}
					}
					break;

				// 取消时清空列表，重新扫描
				case R.id.cancel_do:
					cancel.setBackgroundResource(R.drawable.buttom_selector_bg);

					if (getGetnumber().list_boxdeatil.size() > 0) {
						getGetnumber().list_boxdeatil.clear();
					}

					boxcount.setText(getGetnumber().list_boxdeatil.size() + "");
					if (ad != null) {
						ad.notifyDataSetChanged();
						out.setEnabled(false);
						out.setBackgroundResource(R.drawable.button_gray);
					}
					// 开始扫描
					managerClass.getRfid().start_a20();
					break;

				// 返回
				case R.id.outdo_back:
					outdo_back.setBackgroundResource(R.drawable.back_cirle);
					managerClass.getSureCancel().makeSuerCancel(
							BoxDoDetail.this, "确定要退出当前页面吗？",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getSureCancel().remove();
									BoxDoDetail.this.finish();
								}
							}, false);
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
				cancel.setBackgroundResource(R.drawable.buttom_selector_bg);
				out.setBackgroundResource(R.drawable.buttom_selector_bg);
				outdo_back.setBackgroundResource(R.drawable.back_cirle);

				managerClass.getGolbalutil().ismover = 0;
			}

			return true;
		}

	}

	// 适配器
	class Ad extends BaseAdapter {

		@Override
		public int getCount() {
			return Getnumber.list_boxdeatil.size();
		}

		@Override
		public Object getItem(int arg0) {
			return Getnumber.list_boxdeatil.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			v = view;
			final ViewHolder holder;
			if (v == null) {
				holder = new ViewHolder();
				v = GolbalView.getLF(BoxDoDetail.this).inflate(
						R.layout.boxinformation_item, null);
				v.setTag(holder);
				holder.brand = (TextView) v.findViewById(R.id.brand);
				holder.number = (TextView) v.findViewById(R.id.box_num);
				holder.delText = (TextView) v
						.findViewById(R.id.newstop_item_del);
				holder.cancelText = (TextView) v
						.findViewById(R.id.newstop_item_cancel);
				holder.llayout = (LinearLayout) v
						.findViewById(R.id.lboxdodetail_item);
				holder.menu = (LinearLayout) v
						.findViewById(R.id.newstop_item_menu);
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
			holder.number.setText(box.getNum());

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
						for (int i = 0; i < Getnumber.list_boxdeatil.size(); i++) {
							if (holder.number.getText().equals(
									Getnumber.list_boxdeatil.get(i).getNum())) {
								// 删除掉一条数据后，开始扫描
								managerClass.getRfid().start_a20();

								// 移除一条数据
								Getnumber.list_boxdeatil.remove(i);
								Log.i("Getnumber.list_boxdeatil",
										Getnumber.list_boxdeatil.size() + "");
								boxcount.setText(getGetnumber().list_boxdeatil
										.size() + "");
								ad.notifyDataSetChanged();
								out.setEnabled(false);
								out.setBackgroundResource(R.drawable.button_gray);

								break;
							}
						}
						holder.menu.setVisibility(view.GONE);
					}
					// 意外中断事件取消
					if (MotionEvent.ACTION_CANCEL == even.getAction()) {
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

			// 触摸事件
			holder.llayout.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent even) {
					// 按下的时候
					if (MotionEvent.ACTION_DOWN == even.getAction()) {
						view.setBackgroundResource(R.color.gray_msg_bg);
					}
					// 手指松开的时候
					if (MotionEvent.ACTION_UP == even.getAction()) {
						view.setBackgroundResource(R.color.white);
					}

					// 意外中断事件取消
					if (MotionEvent.ACTION_CANCEL == even.getAction()) {
						view.setBackgroundResource(R.color.white);
					}
					return true;
				}
			});

			// 长按事件
			holder.number.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View arg0) {
					holder.menu.setVisibility(View.VISIBLE);
					return true;
				}
			});

			// 品牌触摸事件
			holder.number.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View vi, MotionEvent even) {
					// 按下的时候
					if (MotionEvent.ACTION_DOWN == even.getAction()) {
						vi.setBackgroundResource(R.color.gray_msg_bg);
					}

					// 手指松开的时候
					if (MotionEvent.ACTION_UP == even.getAction()) {
						vi.setBackgroundResource(R.color.transparency);
					}

					// 意外中断事件取消
					if (MotionEvent.ACTION_CANCEL == even.getAction()) {
						vi.setBackgroundResource(R.color.transparency);
						GolbalUtil.ismover = 0;
					}
					return false;
				}
			});

			// 品牌触摸事件
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
							getBrandNameByCboxNumBiz()
									.getBrnadNamebyNum(
											holder.number.getText().toString(),
											"空钞箱出库");
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

	static class ViewHolder {
		TextView brand;
		TextView number;
		Button btndel;
		LinearLayout llayout;
		TextView delText; // 删除
		TextView cancelText; // 取消
		LinearLayout menu;
	}

	/**
	 * 出库成功2秒后，自动跳到下一个页面
	 */
	void OutDo() {
		t2.schedule(new TimerTask() {
			@Override
			public void run() {
				// 操作成功后释放数据
				try {
					GetBoxDetailListBiz.list.clear();
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 1.5秒后移除出库成功提示
				removeResultShow();
				if ("空钞箱出库".equals(bussin) || "未清回收钞箱出库".equals(bussin)) {
					// 跳到清分员交接验证页面
					bundleBussin.putString("user", "清分员");
					if ("空钞箱出库".equals(bussin)) {
						bundleBussin.putString("type", "01"); // 交接类型
					} else {
						bundleBussin.putString("type", "03"); // 交接类型
					}
					// GApplication.exit(false);
					managerClass.getGolbalutil().gotoActivity(BoxDoDetail.this,
							BankDoublePersonLogin.class, bundleBussin,
							managerClass.getGolbalutil().ismover);
				} else if ("ATM加钞出库".equals(bussin)) {
					// 跳到押运员交接验证页面
					bundleBussin.putString("user", "押运员");
					bundleBussin.putString("type", "02"); // 交接类型
					managerClass.getGolbalutil().gotoActivity(BoxDoDetail.this,
							SupercargoJoin.class, bundleBussin,
							managerClass.getGolbalutil().ismover);
				} else if ("停用钞箱出库".equals(bussin) || "新增钞箱入库".equals(bussin)) {
					// 不用交接，进入页面后直接完成
					managerClass.getGolbalutil().gotoActivity(BoxDoDetail.this,
							BoxAddStop.class, bundleBussin,
							managerClass.getGolbalutil().ismover);
				} else if ("钞箱装钞入库".equals(bussin) || "回收钞箱入库".equals(bussin)
						|| "已清回收钞箱入库".equals(bussin)) {
					managerClass.getGolbalutil().gotoActivity(BoxDoDetail.this,
							JoinResult.class, bundleBussin,
							managerClass.getGolbalutil().ismover);
					// 弹栈
					GApplication.getApplication().exit(false);
				}

			}
		}, 2000);
	}

	public void removeResultShow() {
		t2.schedule(new TimerTask() {
			@Override
			public void run() {
				managerClass.getResultmsg().remove();
			}
		}, 1500);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 移除提示窗口
		managerClass.getResultmsg().remove();

	}

	@Override
	protected void onPause() {
		super.onPause();
		managerClass.getRfid().close_a20();

	}

	// 出库还是入库
	public void outText(String content) {
		if ("空钞箱出库".equals(content) || "ATM加钞出库".equals(content)) {
			out.setText("出库");
		} else {
			out.setText("入库");
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 添加扫描通知
		managerClass.getRfid().addNotifly(new Getnumber());
		if (BoxDetailInfoDo.isfirst <= 0) {
			// 打开扫描
			managerClass.getRfid().open_a20();
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}
