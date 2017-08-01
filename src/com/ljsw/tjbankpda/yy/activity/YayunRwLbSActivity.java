package com.ljsw.tjbankpda.yy.activity;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.application.GApplication;
import com.example.app.activity.KuanXiangJiaoJieActivity;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.ljsw.tjbankpda.db.activity.ShangJiaoRuKuSaoMiao_db;
import com.ljsw.tjbankpda.db.application.o_Application;
import com.ljsw.tjbankpda.main.ZhouzhuanxiangMenu;
import com.ljsw.tjbankpda.qf.entity.YaYunLb;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.ljsw.tjbankpda.util.TableQuzhi;
import com.ljsw.tjbankpda.yy.application.S_application;
import com.ljsw.tjbankpda.yy.service.ICleaningManService;
import com.manager.classs.pad.ManagerClass;
import com.service.NetService;

/**
 * 押运员任务列表
 * 
 * @author Administrator
 */
public class YayunRwLbSActivity extends FragmentActivity implements OnClickListener, OnItemClickListener {
	private ListView listView;
	private List<YaYunLb> list = new ArrayList<YaYunLb>();
	boolean network = true; // 是否有网络
	YayunThread yt;
	private ManagerClass managerClass;
	private Handler handler;// 获取列表前台
	Table[] tables;
	private TextView yy_XianluName,// 线路名
			jigoushuliangView; // 机构数量显示

	OnClickListener onclickreplace, onclickreplace2, onclickreplace1;
	private String sjPgdan,// 上缴派工单
			qlPgdan,// 请领派工单
			xianluming,// 线路名称
			xianluType, // 线路类别
			pdate;// 配送日期

	private String biaoshi;
	private String jigouleibie;

	private List<String> qlZzxlist = new ArrayList<String>(); // 请领周转箱集合
	private List<String> sjZzxlist = new ArrayList<String>(); // 上缴周转箱集合
	private YaYunLb YaYunLb; // 被选中的网点
	LiebiaoAdapter liebiaoadapter;

	ICleaningManService is = new ICleaningManService();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yayun_renwuitem_s);
		managerClass = new ManagerClass();
		initView();

		// 接收网络状态广播后，发出handler通知
		NetService.handnet = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 1) {
					network = true;
				} else {
					network = false;
				}
			}
		};
		// 手动判断是否有网络
		if (NetService.info != null && !NetService.info.isConnectedOrConnecting()) {
			network = false;
		} else if (NetService.info == null) {
			network = false;
		}

		onclickreplace2 = new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				managerClass.getAbnormal().remove();
				Xianlu();
			}
		};
		onclickreplace1 = new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				managerClass.getAbnormal().remove();
				getJigouLeibie();
			}
		};

		// 重试单击事件
		onclickreplace = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				NetService.handnet = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						super.handleMessage(msg);
						if (msg.what == 1) {
							network = true;
						} else {
							network = false;
						}
					}
				};
				// 手动判断是否有网络
				if (NetService.info != null && !NetService.info.isConnectedOrConnecting()) {
					network = false;
				} else if (NetService.info == null) {
					network = false;
				}
				managerClass.getAbnormal().remove();
				managerClass.getRuning().runding(YayunRwLbSActivity.this, "正在获取数据...");
				yt = new YayunThread();
				yt.start();
				if ("qitiao".equals(biaoshi)) {
					managerClass.getRuning().runding(YayunRwLbSActivity.this, "正在获取数据...");
					HuoquZzxThread ht = new HuoquZzxThread();
					ht.start();
				}

			}
		};

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:// 显示数据
					managerClass.getRuning().remove();

					String str = msg.obj.toString();
					tables = Table.doParse(str);
					System.out.println("str:" + str);
					// 获取线路id
					S_application.getApplication().s_yayunXianluId = TableQuzhi.getZhi(tables[0], "xianluId");
					// 获取线路名称
					xianluming = TableQuzhi.getZhi(tables[0], "xianluming");
					xianluming = xianluming == null ? "无" : xianluming;
					// 获取线路类型
					xianluType = TableQuzhi.getZhi(tables[0], "xianluType");
					// 获取上缴派工单
					sjPgdan = TableQuzhi.getZhi(tables[0], "sjpaigongdan");
					System.out.println("sjPgdan:" + sjPgdan);
					S_application.getApplication().sjpaigongdan = sjPgdan;
					// 获取请领派工单
					qlPgdan = TableQuzhi.getZhi(tables[0], "qlpaigongdan");
					System.out.println("qlPgdan:" + qlPgdan);
					S_application.getApplication().s_qlpaigongdan = qlPgdan;
					// 获取配送日期
					pdate = TableQuzhi.getZhi(tables[0], "peisongriqi");
					pdate = pdate == null ? "" : pdate;

					yy_XianluName.setText(xianluming);
					getData();
					managerClass.getRuning().remove();
					// 获取交接类型
					if (!"".equals(xianluType) && null != xianluType && xianluType.equals("0")) { // 如果是普通线路
						if (jigouleibie.equals("0")) {
							S_application.getApplication().jiaojieType = 1; // 总库押运
						} else {
							S_application.getApplication().jiaojieType = 2; // 分库押运
						}
					} else { // 如果是直属线路
						S_application.getApplication().jiaojieType = 3;
						System.out.println("交接类型:" + S_application.getApplication().jiaojieType);
					}
					// Xianlu();
					break;
				case -1:
					managerClass.getRuning().remove();
					managerClass.getAbnormal().timeout(YayunRwLbSActivity.this, "信息加载异常", onclickreplace);
					break;
				case 0:
					managerClass.getRuning().remove();
					// managerClass.getAbnormal().timeout(YayunRwLbSActivity.this,
					// "信息加载失败", onclickreplace);
					break;
				case 3:// 跳转下一个页面
					managerClass.getRuning().remove();
					Bundle bundle = new Bundle();
					bundle.putSerializable("qllist", (Serializable) qlZzxlist);
					bundle.putSerializable("sjlist", (Serializable) sjZzxlist);
					bundle.putSerializable("YaYunLb", YaYunLb);
					bundle.putString("pdate", pdate);
					bundle.putString("xianluType", xianluType);
					bundle.putString("XianLu", xianluming);
					System.out.println("准备跳转页面");
					Skip.skip(YayunRwLbSActivity.this, YaYunRwMingxiActivity.class, bundle, 0);
					break;
				case -4:
					managerClass.getRuning().remove();
					managerClass.getAbnormal().timeout(YayunRwLbSActivity.this, "连接超时，重新链接？", onclickreplace);
					break;
				case 5:
					managerClass.getRuning().remove();
					// 获取交接类型
					if (!"".equals(xianluType) && null != xianluType && xianluType.equals("0")) { // 如果是普通线路

						if (params != null && params.equals("0")) {
							S_application.getApplication().jiaojieType = 1; // 总库押运
						} else {
							S_application.getApplication().jiaojieType = 2; // 分库押运
						}
					} else { // 如果是直属线路
						S_application.getApplication().jiaojieType = 3;
						System.out.println("交接类型:" + S_application.getApplication().jiaojieType);
					}
					break;
				case 6:
					managerClass.getRuning().remove();
					managerClass.getAbnormal().timeout(YayunRwLbSActivity.this, "连接超时，重新链接？", onclickreplace2);
					break;
				case 7:
					managerClass.getRuning().remove();
					// managerClass.getAbnormal().timeout(YayunRwLbSActivity.this,
					// "获取失败", onclickreplace2);
					break;
				case 8:
					managerClass.getRuning().remove();
					managerClass.getAbnormal().timeout(YayunRwLbSActivity.this, "信息加载异常", onclickreplace2);
					break;
				case 15:
					managerClass.getRuning().remove();
					managerClass.getAbnormal().timeout(YayunRwLbSActivity.this, "连接超时，重新链接？", onclickreplace2);
					break;
				case 13:
					managerClass.getRuning().remove();
					// managerClass.getAbnormal().timeout(YayunRwLbSActivity.this,
					// "获取失败", onclickreplace2);
					break;
				case 14:
					managerClass.getRuning().remove();
					managerClass.getAbnormal().timeout(YayunRwLbSActivity.this, "信息加载异常", onclickreplace2);
					break;
				}
			}
		};

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (list.size() > 0) {
			list.clear();
		}
		if (network) {
			managerClass.getRuning().runding(YayunRwLbSActivity.this, "正在获取数据...");
			yt = new YayunThread();
			yt.start();
		} else {
			managerClass.getAbnormal().timeout(YayunRwLbSActivity.this, "网络连接失败是否重连", onclickreplace);
		}
	}

	String params;// 返回机构的类别

	/**
	 * 通过机构ID获取线路类型
	 */
	public void Xianlu() {
		managerClass.getRuning().runding(YayunRwLbSActivity.this, "获取线路中.....");
		new Thread() {
			public void run() {
				super.run();
				try {
					params = is.getJigouLeibie(S_application.getApplication().s_yayunJigouId);
					if (params != null || !params.equals("")) {
						// S_application.getApplication().jiaojieType=1;
						handler.sendEmptyMessage(5);
					} else {
						handler.sendEmptyMessage(7);
					}
				} catch (SocketTimeoutException e) {
					handler.sendEmptyMessage(6);
				} catch (NullPointerException e) {
					handler.sendEmptyMessage(7);
				} catch (Exception e) {
					handler.sendEmptyMessage(8);
				}

			};
		}.start();
	}

	/**
	 * 获取数据
	 * 
	 * @author Administrator
	 */
	class YayunThread extends Thread {
		Message m;

		public YayunThread() {
			super();
			m = handler.obtainMessage();
		}

		@Override
		public void run() {
			super.run();
			try {
				System.out.println("BBB-0000ID :" + S_application.getApplication().s_yayunJigouId);
				System.out.println("BBB-0000 :" + GApplication.use.getUserzhanghu());
				String str = is.getYayunyuanRenwu(GApplication.use.getUserzhanghu(),
						S_application.getApplication().s_yayunJigouId);

				if (!"".equals(str) && null != str) {
					m.obj = str;
					m.what = 1;
					System.out.println("不走吗?");
				} else {
					System.out.println("走吗1");
					m.what = 0;
				}
			} catch (SocketTimeoutException e) {
				// TODO: handle exception
				e.printStackTrace();
				m.what = -4;
			} catch (NullPointerException e) {
				// TODO: handle exception
				e.printStackTrace();
				System.out.println("走吗12");
				m.what = 0;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				m.what = -1;
			} finally {
				handler.sendMessage(m);
				GolbalUtil.onclicks = true;
			}
		}
	}

	public void initView() {
		liebiaoadapter = new LiebiaoAdapter();
		yy_XianluName = (TextView) this.findViewById(R.id.yy_XianluName);
		listView = (ListView) this.findViewById(R.id.yylistView);
		findViewById(R.id.yayun_backS1).setOnClickListener(this);

		findViewById(R.id.yyrw_update).setOnClickListener(this);
		// listView.setAdapter(new LiebiaoAdapter());
		listView.setOnItemClickListener(this);
		jigoushuliangView = (TextView) this.findViewById(R.id.textView5);
		// turngaodu(listView);
		listView.setAdapter(liebiaoadapter);
	}

	/**
	 * 获取押运员任务列表
	 */
	public void getData() {
		if (list.size() > 0) {
			list.clear();
		}

		for (int i = 0; i < tables[1].get("Jigouming").getValues().size(); i++) {
			YaYunLb yy = new YaYunLb();
			yy.setJigouId(tables[1].get("JigouId").getValues().get(i));
			yy.setName(tables[1].get("Jigouming").getValues().get(i));
			String str = tables[1].get("renwuType").getValues().get(i);
			String[] st = str.split("_");
			yy.setJigouId(tables[1].get("JigouId").getValues().get(i));
			yy.setQlrwState(st[0]);
			yy.setSjrwState(st[1]);
			list.add(yy);
		}

		liebiaoadapter.notifyDataSetChanged();
		jigoushuliangView.setText(list.size() + "");
	}

	/**
	 * 滚动条(备用)
	 * 
	 * @param lv
	 */
	public void turngaodu(ListView lv) {
		// 获取listView对应的Adapter
		ListAdapter adapter = lv.getAdapter();
		// 返回数据项的数目
		int hangshu = adapter.getCount();
		int totalhanggao = 0;
		for (int i = 0; i < hangshu; i++) {
			View v = adapter.getView(i, null, lv);
			/*
			 * 在没有构建View之前无法取得View的宽度。选定measure 调用getMeasuredHeight()的方法
			 */
			v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			totalhanggao += v.getMeasuredHeight();
		}
		double fenggefu = lv.getDividerHeight() * (hangshu - 1);// 得到分隔符的高度
		totalhanggao += fenggefu;// 得到整个高度
		LayoutParams lp = lv.getLayoutParams();
		lp.height = totalhanggao;// 设置总高度
		lv.setLayoutParams(lp);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		getJigouLeibie();
	}

	/**
	 * 获取机构类别
	 */
	public void getJigouLeibie() {
		managerClass.getRuning().runding(YayunRwLbSActivity.this, "获取机构类别中...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					/**
					 * 取消押运员机构限制后，根据押运员的userId查询出其所领取的任务的机构ID，
					 * 以此机构id作为查询机构类别的参数。
					 * @author zhouKai  
					 * @date 2016-7-11 下午1:21:03
					 */
//					jigouleibie = is.getJigouLeibie(o_Application.kuguan_db.getOrganizationId());
					jigouleibie = is.getJigouLeibie(GApplication.use.getUserzhanghu());

					if (null != jigouleibie || !"".equals(jigouleibie)) {
						handler.sendEmptyMessage(5);
					} else {
						handler.sendEmptyMessage(13);
					}
				} catch (SocketTimeoutException e) {
					handler.sendEmptyMessage(15);
					e.printStackTrace();
				} catch (NullPointerException e) {
					handler.sendEmptyMessage(13);
					e.printStackTrace();
				} catch (Exception e) {
					handler.sendEmptyMessage(14);
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 押运员任务列表自定义样式
	 * 
	 * @author Administrator
	 */
	class LiebiaoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@SuppressLint("NewApi")
		public View getView(int arg0, View arg1, ViewGroup arg2) {

			KongjianEntity entity;
			if (arg1 == null) {
				entity = new KongjianEntity();
				arg1 = LayoutInflater.from(YayunRwLbSActivity.this).inflate(R.layout.item_yayun_rwll_s, null);
				entity.textView1 = (TextView) arg1.findViewById(R.id.wdm_s);
				entity.textView2 = (ImageView) arg1.findViewById(R.id.qlrw_s);
				entity.textView3 = (ImageView) arg1.findViewById(R.id.sjrw_s);
				arg1.setTag(entity);
			} else {
				entity = (KongjianEntity) arg1.getTag();
				// resetViewHolder(entity);
			}
			entity.textView1.setText(list.get(arg0).getName());

			// 上缴状态勾选
			if (list.get(arg0).getSjrwState() != null && list.get(arg0).getSjrwState().equals("0")) {
				entity.textView3.setVisibility(View.INVISIBLE);
			} else if (list.get(arg0).getSjrwState() != null && list.get(arg0).getSjrwState().equals("1")) {
				entity.textView3.setVisibility(View.VISIBLE);
			}

			// 请领状态勾选
			if (list.get(arg0).getQlrwState() != null && list.get(arg0).getQlrwState().equals("0")) {
				entity.textView2.setVisibility(View.INVISIBLE);
			} else if (list.get(arg0).getQlrwState() != null && list.get(arg0).getQlrwState().equals("1")) {
				entity.textView2.setVisibility(View.VISIBLE);
			}
			return arg1;
		}

	}

	/*
	 * listView 控件实体类
	 */
	static class KongjianEntity {
		public TextView textView1;
		public ImageView textView2;
		public ImageView textView3;
	}

	/**
	 * listView Item点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int CheckId, long arg3) {
		// 启动线程 加载数据
		biaoshi = "qitiao";
		YaYunLb = list.get(CheckId);
		// 选中的机构id等于所到达网点的机构id
		S_application.getApplication().wangdianJigouId = YaYunLb.getJigouId();
		System.out.println("选中的机构ID-->" + S_application.getApplication().wangdianJigouId);
		managerClass.getRuning().runding(YayunRwLbSActivity.this, "正在获取数据...");
		HuoquZzxThread ht = new HuoquZzxThread();
		ht.start();

	}

	/**
	 * 押运员周转箱获取
	 * 
	 * @author Administrator
	 */
	class HuoquZzxThread extends Thread {
		Message m;

		public HuoquZzxThread() {
			super();
			m = handler.obtainMessage();
		}

		@Override
		public void run() {
			super.run();

			try {
				System.out.println("机构id=" + S_application.getApplication().wangdianJigouId);
				System.out.println("sj=" + sjPgdan);
				System.out.println("ql=" + qlPgdan);
				String str = is.getYayunyuanZhouzhuangxiang(S_application.getApplication().wangdianJigouId, sjPgdan,
						qlPgdan);
				if (!"".equals(str) && null != str) {
					Table[] tables2 = Table.doParse(str);
					sjZzxlist = TableQuzhi.getList(tables2[0], "shangjiaoZhouzhuangxiang");
					qlZzxlist = TableQuzhi.getList(tables2[1], "qinglingZhouzhuangxiang");
					m.what = 3;
				}

			} catch (SocketTimeoutException e) {
				// TODO: handle exception
				e.printStackTrace();
				m.what = -4;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				m.what = -1;
			} finally {
				handler.sendMessage(m);
				GolbalUtil.onclicks = true;
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.yayun_backS1:// 返回

			Skip.skip(YayunRwLbSActivity.this, ZhouzhuanxiangMenu.class, null, 0);
			YayunRwLbSActivity.this.finish();
			break;

		case R.id.yyrw_update:// 刷新任务
			if (network) {
				managerClass.getRuning().runding(YayunRwLbSActivity.this, "正在获取数据...");
				yt = new YayunThread();
				yt.start();
			} else {
				managerClass.getAbnormal().timeout(YayunRwLbSActivity.this, "网络连接失败是否重连", onclickreplace);
			}
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		managerClass.getRuning().remove();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (S_application.getApplication().s_userYayun != null) {
				S_application.getApplication().s_userYayun = null;
			}
			Skip.skip(YayunRwLbSActivity.this, ZhouzhuanxiangMenu.class, null, 0);
			YayunRwLbSActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		S_application.getApplication().s_userYayun = null;
		super.onDestroy();
	}

}
