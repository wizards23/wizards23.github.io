package com.out.admin.pda;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.entity.BoxDetail;
import com.example.pda.R;
import com.golbal.pda.GolbalView;
import com.imple.getnumber.ATMJoin;
import com.imple.getnumber.WebJoin;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.MenuShow;
import com.moneyboxadmin.biz.EmptyOutCheck;
import com.moneyboxadmin.pda.BankDoublePersonLogin;
import com.moneyboxadmin.pda.JoinResult;
import com.moneyboxadmin.pda.SupercargoJoin;
import com.out.biz.AssignBiz;
import com.out.biz.CashboxNumByCorpIdBiz;
import com.out.biz.CorpListByPlanNumBiz;

//网点加钞和网点回收
public class WebSiteJoin extends Activity implements OnTouchListener {

	ListView listview;
	String addorback; // 标识是加钞还是回收
	Button btnjoin; // 钞箱交接
	Button joincheck; // 交接检测
	Bundle bundle;
	Spinner spinner; // 机构列表
	ArrayAdapter<String> arrayad;
	TextView title; // 标题
	String[] address;
	TextView needNum; // 需交接数量
	TextView nowNum; // 扫描数量
	public static String spinnerText; // spinner选中项的值
	View.OnClickListener click; // 机构列表重试单击事件
	public static String corp;// 机构ID
	public static String type; // 0.加钞 1.回收
	Ad ad;
	ImageView back;
	private ManagerClass managerClass;
	View.OnClickListener click_getNum;
	WebJoin webjoin = new WebJoin();
	EmptyOutCheck ch = null;
	public static String webJoinID;

	private CorpListByPlanNumBiz corpListByPlanNum;

	CorpListByPlanNumBiz getCorpListByPlanNum() {
		return corpListByPlanNum = corpListByPlanNum == null ? new CorpListByPlanNumBiz()
				: corpListByPlanNum;
	}

	private CashboxNumByCorpIdBiz cashboxNumByCorpId;

	CashboxNumByCorpIdBiz getCashboxNumByCorpId() {
		return cashboxNumByCorpId = cashboxNumByCorpId == cashboxNumByCorpId ? new CashboxNumByCorpIdBiz()
				: cashboxNumByCorpId;
	}
	
	Dialog log;  //atm扫描弹出窗

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.out_addmoney_join);
		managerClass = new ManagerClass();
		managerClass.getGolbalView().Init(this);

		// 全局异常处理
		// CrashHandler.getInstance().init(this);

		listview = (ListView) findViewById(R.id.listwebjoin);
		btnjoin = (Button) findViewById(R.id.webatminfo);
		spinner = (Spinner) findViewById(R.id.spinn_out);
		needNum = (TextView) findViewById(R.id.join_need_num);
		nowNum = (TextView) findViewById(R.id.join_now_num);
		back = (ImageView) findViewById(R.id.webjoin_back);
		title = (TextView) findViewById(R.id.webjoin_titel);
		joincheck = (Button) findViewById(R.id.webjoincheck);

		btnjoin.setOnTouchListener(this);
		joincheck.setOnTouchListener(this);

		bundle = getIntent().getExtras();
		if (bundle != null) {
			addorback = bundle.getString("clear");
			title.setText(addorback);
			if ("在行回收钞箱交接".equals(addorback) || "离行回收钞箱交接".equals(addorback)) {
				type = "1";
			} else {
				type = "0";
			}

		}

		ch = new EmptyOutCheck();

		if (ad == null) {
			ad = new Ad();
			listview.setAdapter(ad);
		} else {
			ad.notifyDataSetChanged();
		}
		ad.notifyDataSetChanged();

		// 如果交接后再次进放这个界面
		if (JoinResult.result) {
			btnjoin.setEnabled(false);
			btnjoin.setBackgroundResource(R.drawable.button_gray);
			if(type!=null && type.equals("0")){
				needNum.setText(Integer.parseInt(CashboxNumByCorpIdBiz.boxCount)
						+ "");
			}else{
				nowNum.setText(Integer.parseInt(CashboxNumByCorpIdBiz.boxCount)
						+ "");
			}
			

			if (CashboxNumByCorpIdBiz.list != null
					&& CashboxNumByCorpIdBiz.list.size() > 0) {
				CashboxNumByCorpIdBiz.list.clear();
			}

		}

		// 机构列表重试事件
		click = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				managerClass.getRuning().runding(WebSiteJoin.this,
						"正在获取机构列表...");
				// 获取机构列表 参数:计划编号
				getCorpListByPlanNum().getCorpList(
						AssignBiz.order.getPlanNum(), type);
			}
		};
		
		log=new AtmDialog(this);
		
		// 获取机构列表后
		getCorpListByPlanNum().handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				managerClass.getRuning().remove();
				super.handleMessage(msg);

				switch (msg.what) {
				case 1:
					// 把机构名称放入数组
					getarray();
					arrayad = new ArrayAdapter<String>(WebSiteJoin.this,
							R.layout.spinner_show, R.id.spinnertext, address);
					spinner.setAdapter(arrayad);

					break;
				case -1:
					managerClass.getAbnormal().timeout(WebSiteJoin.this,
							"连接异常！", click);
					break;
				case -4:
					managerClass.getAbnormal().timeout(WebSiteJoin.this,
							"连接超时！", click);
					break;
				case 0:
					managerClass.getSureCancel().makeSuerCancel(
							WebSiteJoin.this, "查找失败，没有数据",
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

		// 钞箱扫描后
		WebJoin.handler = new Handler() {
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
		
		
		
		

		// 根据所在位置获取交接钞箱数量 重试事件
		click_getNum = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				if ("网点加钞钞箱交接".equals(addorback)) {
					managerClass.getRuning().runding(WebSiteJoin.this,
							"正在获取钞箱数量...");
					getCashboxNumByCorpId().gettCashboxNumByCorpId(
							AssignBiz.order.getPlanNum(),
							getCorpId(spinnerText), "0");
				} else if ("离行加钞钞箱交接".equals(addorback)) {
					managerClass.getRuning().runding(WebSiteJoin.this,
							"正在获取钞箱数量...");
					getCashboxNumByCorpId().gettCashboxNumByATM(
							AssignBiz.order.getPlanNum(),
							getCorpId(spinnerText), "0");
				} else if ("离行回收钞箱交接".equals(addorback)) {
					managerClass.getRuning().runding(WebSiteJoin.this,
							"正在获取钞箱数量...");
					getCashboxNumByCorpId().gettCashboxNumByATM(
							AssignBiz.order.getPlanNum(),
							getCorpId(spinnerText), "1");
				} else {
					managerClass.getRuning().runding(WebSiteJoin.this,
							"正在获取钞箱数量...");
					getCashboxNumByCorpId().gettCashboxNumByCorpId(
							AssignBiz.order.getPlanNum(),
							getCorpId(spinnerText), "1");
				}
			}
		};

		// 根据所在位置获取交接钞箱数量 后
		getCashboxNumByCorpId().handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				managerClass.getRuning().remove();
				type=msg.getData().getString("type");
				switch (msg.what) {
				case 1:
					/**Modify begin by zhangxuewei at 01-DEC-2016
					 *type到活动，用来区分加钞or回收*/
					if(type!=null && type.equals("0")){
						needNum.setText(Integer.parseInt(CashboxNumByCorpIdBiz.boxCount)
								+ "");
					}else{
						nowNum.setText(Integer.parseInt(CashboxNumByCorpIdBiz.boxCount)
								+ "");
					}
					/**Modify end*/
					// 开启扫描
					managerClass.getRfid().addNotifly(new WebJoin());
					managerClass.getRfid().open_a20();

					WebJoin.list.clear(); // 重置箱子数量
					ad.notifyDataSetChanged();

					break;
				case 0:
					managerClass.getSureCancel().makeSuerCancel(
							WebSiteJoin.this, "查找失败，没有数据",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getSureCancel().remove();
								}
							}, true);
					break;
				case -1:
					managerClass.getAbnormal().timeout(WebSiteJoin.this,
							"连接异常，要重试吗？", click_getNum);
					break;
				case -4:
					managerClass.getAbnormal().timeout(WebSiteJoin.this,
							"连接超时，要重试吗？", click_getNum);
					break;
				}

			}

		};

		// 下拉列表选择单击事件
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long arg3) {
				spinnerText = (String) parent.getItemAtPosition(position);
				if (!"请选择地点".equals(spinnerText)
						&& !"请选择ATM机".equals(spinnerText)) {
					boolean isOK=false;
					// 根据所在位置获取交接和加钞钞箱数量 参数：计划编号，机构ID
					if ("网点加钞钞箱交接".equals(addorback)) {
						managerClass.getRuning().runding(WebSiteJoin.this,
								"正在获取钞箱数量...");
						getCashboxNumByCorpId().gettCashboxNumByCorpId(
								AssignBiz.order.getPlanNum(),
								getCorpId(spinnerText), "0");
					} else if ("离行加钞钞箱交接".equals(addorback)) {
						// 弹出提示框 要求扫描atm机
						 log.show();
						
					} else if ("离行回收钞箱交接".equals(addorback)) {
						managerClass.getRuning().runding(WebSiteJoin.this,
								"正在获取钞箱数量...");
						getCashboxNumByCorpId().gettCashboxNumByATM(
								AssignBiz.order.getPlanNum(),
								getCorpId(spinnerText), "1");
					} else {
						managerClass.getRuning().runding(WebSiteJoin.this,
								"正在获取钞箱数量...");
						getCashboxNumByCorpId().gettCashboxNumByCorpId(
								AssignBiz.order.getPlanNum(),
								getCorpId(spinnerText), "1");
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		back.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent even) {
				if (even.getAction() == MotionEvent.ACTION_DOWN) {
					back.setImageResource(R.drawable.back_cirle_press);
				}
				if (even.getAction() == MotionEvent.ACTION_UP) {
					back.setImageResource(R.drawable.back_cirle);
					managerClass.getRfid().close_a20();
					WebJoin.list.clear();
				}
				if (even.getAction() == MotionEvent.ACTION_CANCEL) {
					back.setImageResource(R.drawable.back_cirle_press);
				}
				return true;
			}
		});

	}

	// 适配器
	class Ad extends BaseAdapter {

		@Override
		public int getCount() {
			return WebJoin.list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return WebJoin.list.get(arg0);
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
				holder = new ViewHolder();
				v = GolbalView.getLF(WebSiteJoin.this).inflate(
						R.layout.boxinformation_item, null);
				holder.boxNum = (TextView) v.findViewById(R.id.box_num);
				holder.btndel = (Button) v.findViewById(R.id.webjoin_del);
				holder.detele = (TextView) v
						.findViewById(R.id.newstop_item_del);
				holder.cancel = (TextView) v
						.findViewById(R.id.newstop_item_cancel);
				holder.brand = (TextView) v.findViewById(R.id.brand);
				holder.lmenu = (LinearLayout) v
						.findViewById(R.id.newstop_item_menu);

				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}

			// 取消触摸事件
			holder.cancel.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent even) {
					// 按下的时候
					if (MotionEvent.ACTION_DOWN == even.getAction()) {
						view.setBackgroundResource(R.color.blue_title);
					}
					// 手指松开的时候
					if (MotionEvent.ACTION_UP == even.getAction()) {
						view.setBackgroundResource(R.color.transparency);
						holder.lmenu.setVisibility(view.GONE);
					}

					// 意外中断事件取消
					if (MotionEvent.ACTION_CANCEL == even.getAction()) {
						view.setBackgroundResource(R.color.transparency);
					}
					return true;
				}
			});

			// 删除触摸事件
			holder.detele.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent even) {
					// 按下的时候
					if (MotionEvent.ACTION_DOWN == even.getAction()) {
						view.setBackgroundResource(R.color.blue_title);
					}
					// 手指松开的时候
					if (MotionEvent.ACTION_UP == even.getAction()) {
						view.setBackgroundResource(R.color.transparency);

						for (int i = 0; i < WebJoin.list.size(); i++) {
							BoxDetail box = WebJoin.list.get(i);
							if (box.getNum().equals(holder.boxNum.getText())) {
								WebJoin.list.remove(i);
								ad.notifyDataSetChanged();
								btnjoin.setEnabled(false);
								btnjoin.setBackgroundResource(R.drawable.button_gray);
								// 打开或关闭扫描
								managerClass.getRfid().start_a20();
								break;
							}
						}

						holder.lmenu.setVisibility(view.GONE);
					}

					// 意外中断事件取消
					if (MotionEvent.ACTION_CANCEL == even.getAction()) {
						view.setBackgroundResource(R.color.transparency);
					}
					return true;
				}
			});

			// V触摸事件
			v.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent even) {
					// 按下的时候
					if (MotionEvent.ACTION_DOWN == even.getAction()) {
						holder.brand.setBackgroundResource(R.color.gray_msg_bg);
						holder.boxNum
								.setBackgroundResource(R.color.gray_msg_bg);
					}
					// 手指松开的时候
					if (MotionEvent.ACTION_UP == even.getAction()) {
						holder.brand
								.setBackgroundResource(R.color.transparency);
						holder.boxNum
								.setBackgroundResource(R.color.transparency);
					}

					// 意外中断事件取消
					if (MotionEvent.ACTION_CANCEL == even.getAction()) {
						holder.brand
								.setBackgroundResource(R.color.transparency);
						holder.boxNum
								.setBackgroundResource(R.color.transparency);
					}
					return false;
				}
			});

			// 长按事件
			v.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View arg0) {
					// 显示删除取消菜单
					holder.lmenu.setVisibility(View.VISIBLE);
					return true;
				}
			});

			BoxDetail b = (BoxDetail) getItem(arg0);
			holder.boxNum.setText(b.getNum());
			holder.brand.setText(b.getBrand());
			return v;
		}

	}

	static class ViewHolder {
		TextView boxNum;
		TextView detele;
		TextView brand;
		TextView cancel;
		Button btndel;
		LinearLayout lmenu;
	}

	@Override
	public boolean onTouch(View view, MotionEvent even) {
		// 按下的时候
		if (MotionEvent.ACTION_DOWN == even.getAction()) {
			switch (view.getId()) {
			case R.id.webatminfo:
				btnjoin.setBackgroundResource(R.drawable.buttom_select_press);
				break;
			case R.id.webjoincheck:
				joincheck.setBackgroundResource(R.drawable.buttom_select_press);
				break;
			}

		}

		// 手指松开的时候
		if (MotionEvent.ACTION_UP == even.getAction()) {

			switch (view.getId()) {
			case R.id.webatminfo:
				btnjoin.setBackgroundResource(R.drawable.buttom_selector_bg);
				if ("网点加钞钞箱交接".equals(addorback)
						|| "离行加钞钞箱交接".equals(addorback)) {
					bundle.putString("user", "加钞人员");
					bundle.putString("type", "04");
					bundle.putString("planNum", AssignBiz.order.getPlanNum());
					managerClass.getGolbalutil().gotoActivity(WebSiteJoin.this,
							BankDoublePersonLogin.class, bundle,
							managerClass.getGolbalutil().ismover);
				} else if ("网点回收钞箱交接".equals(addorback)
						|| "离行回收钞箱交接".equals(addorback)) {
					bundle.putString("businName", "网点回收钞箱交接");
					bundle.putString("type", "05");
					managerClass.getGolbalutil().gotoActivity(WebSiteJoin.this,
							SupercargoJoin.class, bundle,
							managerClass.getGolbalutil().ismover);
				}
				
				break;

			case R.id.webjoincheck:
				joincheck.setBackgroundResource(R.drawable.buttom_selector_bg);
				check();
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

			switch (view.getId()) {
			case R.id.webatminfo:
				btnjoin.setBackgroundResource(R.drawable.buttom_selector_bg);
				break;
			case R.id.webjoincheck:
				joincheck.setBackgroundResource(R.drawable.buttom_selector_bg);
				break;
			}
			managerClass.getGolbalutil().ismover = 0;

		}
		return true;
	}

	// 机构名称数组
	public void getarray() {
		address = new String[getCorpListByPlanNum().list.size()];
		for (int i = 0; i < getCorpListByPlanNum().list.size(); i++) {
			address[i] = getCorpListByPlanNum().list.get(i).getName();
		}
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// 关闭扫描
		managerClass.getRfid().close_a20();
	}
	

	@Override
	protected void onStart() {
		super.onStart();
		try {
			managerClass.getRuning().runding(this, "正在获取机构列表...");
			// 获取机构列表 参数:计划编号
			getCorpListByPlanNum().getCorpList(AssignBiz.order.getPlanNum(),
					type);

		} catch (Exception e) {
			managerClass.getGolbalView()
					.toastShow(WebSiteJoin.this, "请再次获取派工单");
			WebSiteJoin.this.finish();
		}

	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuShow().menu(this);
		MenuShow.pw.showAtLocation(findViewById(R.id.main_box), Gravity.BOTTOM,
				0, 0);
		return false;
	}
	

	// 根据选择的机构名称获取机构ID
	public String getCorpId(String corpName) {
		String id = "";
		String name;
		for (int i = 0; i < CorpListByPlanNumBiz.list.size(); i++) {
			name = CorpListByPlanNumBiz.list.get(i).getName();
			id = CorpListByPlanNumBiz.list.get(i).getId();
			webJoinID = id;
			if (name.equals(corpName)) {
				corp = CorpListByPlanNumBiz.list.get(i).getId();
				break;
			}
		}
		return id;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		managerClass.getRfid().close_a20();
		if (WebJoin.list != null) {
			WebJoin.list.clear();
		}
		this.finish();

	}

	// 交接前检测
	public void check() {
		// 如果扫描的钞箱数量大于等于要交接的数量
		int count = Integer.parseInt(CashboxNumByCorpIdBiz.boxCount);
		if (WebJoin.list != null && ch.checkBox(WebJoin.list) && count > 0
				&& count == WebJoin.list.size()) {
			btnjoin.setEnabled(true);
			btnjoin.setBackgroundResource(R.drawable.buttom_selector_bg);
			managerClass.getRfid().stop_a20();
			Log.i("检测通过", "检测通过");
		} else {
			Log.i("检测不通过", "检测不通过");
			managerClass.getGolbalView().toastShow(WebSiteJoin.this,
					"请检测钞箱数量及有效性");
		}

	}

	/**
	 * 弹出提示窗
	 * 
	 * @author Administrator
	 * 
	 */
	private class AtmDialog extends Dialog {
		Button fanhui; // 返回按钮
		TextView textView; // 提示控件

		public AtmDialog(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			System.out.println("进入onCreate:");
			setContentView(R.layout.fanhui_dialog);
			fanhui = (Button) findViewById(R.id.fanhui_back);
			fanhui.setOnClickListener(new View.OnClickListener() {		
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dismiss();
				}
			});
			textView = (TextView) findViewById(R.id.msg_back);
			textView.setText("请扫描ATM机:" + spinnerText);
			fanhui.setText("返回");
			// atm扫描后
			ATMJoin.handler=new Handler(){
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					if (msg.what == 1) {
						String atmBianhao=msg.obj.toString();
						if(atmBianhao.equals(spinnerText)){
							dismiss();   //扫描成功关闭弹出窗
							managerClass.getRuning().runding(WebSiteJoin.this,
									"正在获取钞箱数量...");
							getCashboxNumByCorpId().gettCashboxNumByATM(
									AssignBiz.order.getPlanNum(),
									getCorpId(spinnerText), "0");
						}
					}
				}
			};
		}
		
		// 重写 关闭   在关闭的过程中 同时关闭扫描
		@Override
		public void dismiss() {
			// TODO Auto-generated method stub
			managerClass.getRfid().close_a20();
			super.dismiss();
			
		}
		
		@Override
		public void show() {
			// TODO Auto-generated method stub
			if(textView!=null)
				textView.setText("请扫描ATM机:" + spinnerText);
			// 开启扫描
			managerClass.getRfid().addNotifly(new ATMJoin());
			managerClass.getRfid().open_a20();
			System.out.println("进入show:");
			super.show();
		}

	}
	

}
