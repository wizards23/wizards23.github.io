package com.ljsw.tjbankpda.yy.activity;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.ljsw.tjbankpda.qf.entity.Qingfendan;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.ljsw.tjbankpda.util.TableQuzhi;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.ljsw.tjbankpda.yy.application.S_application;
import com.ljsw.tjbankpda.yy.service.ICleaningManagementService;
import com.manager.classs.pad.ManagerClass;

/**
 * 计划单任务明细 页面(清分)
 * 
 * @author Administrator
 */
public class QingfenJhdMxActivity extends FragmentActivity implements
		OnClickListener, OnItemClickListener {
	private ManagerClass managerClass;
	private TextView allQfdan,// 清分单(完成)所有条数
			qfdXuanzNum,// 清分单(完成) 线路条数
			QfzzxNum,// 清分单(完成) 个周转箱数
			jhdbh;// 计划单编号
	private WanChengAdapter wan;
	private List<Qingfendan> newlist = new ArrayList<Qingfendan>();// 完成计划单中选中的集合
	private List<Qingfendan> list = new ArrayList<Qingfendan>();// 完成计划单
	private List<Qingfendan> wlist = new ArrayList<Qingfendan>();// 未完成计划单
	private LinearLayout qfwc_show,// 清分完成显示
			qfwwc_show;// 清分未完成显示
	private ListView wclistView, wwclistView;// 清分完成ListView ,未清分完成ListView
	private CheckBox allCb;// 全选按钮
	private int isFlag = 1;// 标识(1)全选或者(0)单个点击
	private int count = 0;
	private int total = 0;// 最终选中的款箱输
	private boolean flag = true;// 标识单个刷新 false 单个不刷新 true 单个允许刷新
	OnClickListener onclickreplace;
	private Handler handler;
	Table[] tables;

	private List<Integer> clist = new ArrayList<Integer>();// 控制cheBox的状态
	private S_application myApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qingfen_jihdmx_s);
		managerClass = new ManagerClass();
		myApplication = S_application.getApplication();
		initView();
		setListener();
		// 重试单击事件
		onclickreplace = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				managerClass.getRuning().runding(QingfenJhdMxActivity.this,"加载列表信息中...");
				JhdanThread jhd = new JhdanThread();
				jhd.start();
			}
		};
				
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				managerClass.getRuning().remove();
				switch (msg.what) {
				case 1:// 验证成功跳转
					getData();
					jhdbh.setText(S_application.getApplication().jhdId);
					allQfdan.setText("" + list.size());
					wan = new WanChengAdapter();
					wclistView.setAdapter(wan);
					wwclistView.setAdapter(new WeiWanChengAdapter());
					new TurnListviewHeight(wclistView);
					break;
				case -1:					
					managerClass.getAbnormal().timeout(QingfenJhdMxActivity.this, "信息加载异常",onclickreplace);
					break;
				case -4:
					managerClass.getAbnormal().timeout(QingfenJhdMxActivity.this, "连接超时，重新链接？",onclickreplace);
					break;
				}
			}
		};		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		S_application.getApplication().ywcjhdlist.clear();
		S_application.getApplication().ywcjhdzmlist.clear();
		S_application.getApplication().ywcjhdzidlist.clear(); 
		S_application.getApplication().ywcjhdzzbhlist.clear(); 		
		// 未完成任务单
		S_application.getApplication().wwcjhdlist.clear();
		S_application.getApplication().wwcjhdzidlist.clear();
		S_application.getApplication().wwcjhdzmlist.clear();
		managerClass.getRuning().runding(QingfenJhdMxActivity.this,"加载列表信息中...");
		JhdanThread jtd=new JhdanThread();
		jtd.start();		
	}
	
	/**
	 * 初始化控件
	 */
	public void initView() {
		qfdXuanzNum = (TextView) this.findViewById(R.id.qfdXuanzNum);
		QfzzxNum = (TextView) this.findViewById(R.id.QfzzxNum);
		allQfdan = (TextView) this.findViewById(R.id.allQfdan);
		allCb = (CheckBox) this.findViewById(R.id.AllcheckBox);
		wclistView = (ListView) this.findViewById(R.id.qfwc_listView);// 完成listView
		wwclistView = (ListView) this.findViewById(R.id.qfwwc_listView);// 未完成listView
		qfwc_show = (LinearLayout) this.findViewById(R.id.qfwc_show);
		qfwwc_show = (LinearLayout) this.findViewById(R.id.qfwwc_show);
		jhdbh = (TextView) this.findViewById(R.id.qingfenHao2);
	}

	/**
	 * 设置监听
	 */
	public void setListener() {
		findViewById(R.id.qingfen_backS3).setOnClickListener(this);
		findViewById(R.id.item_qfwc).setOnClickListener(this);
		findViewById(R.id.item_qfwwc).setOnClickListener(this);
		findViewById(R.id.btn_qfsh).setOnClickListener(this);
		
		allCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (isFlag == 1) {
					int aa = 0;
					if (arg1) {
						if (newlist.size() > 0) {
							newlist.clear();
						}
						// quan xuan
						for (int j = 0; j < list.size(); j++) {
							newlist.add(list.get(j));
							String[] str = list.get(j).getQfzzxbh().split("_");
							aa = aa + str.length;
						}
						for (int i = 0; i < clist.size(); i++) {
							clist.set(i, 1);
						}

						total = aa;
						qfdXuanzNum.setText("" + list.size());
						findViewById(R.id.btn_qfsh).setBackgroundResource(
								R.drawable.buttom_selector_bg);
						findViewById(R.id.btn_qfsh).setEnabled(true);
					} else {
						// quan bu xuan
						for (int i = 0; i < clist.size(); i++) {
							clist.set(i, 0);
						}
						qfdXuanzNum.setText("" + 0);
						total = 0;
						findViewById(R.id.btn_qfsh).setBackgroundResource(
								R.drawable.gray_btn_bg);
						findViewById(R.id.btn_qfsh).setEnabled(false);
						QfzzxNum.setText("" + 0);
					}
					QfzzxNum.setText("" + total);
					flag = false;
				} else {
					if (arg1) {
						for (int i = 0; i < clist.size(); i++) {
							clist.set(i, 1);
						}
						int aa = 0;
						for (int j = 0; j < list.size(); j++) {
							String[] str = list.get(j).getQfzzxbh().split("_");
							aa = aa + str.length;
						}
						// System.out.println("aa  bb:" + aa);
						qfdXuanzNum.setText("" + list.size());
						QfzzxNum.setText("" + aa);
						total = aa;
						findViewById(R.id.btn_qfsh).setBackgroundResource(
								R.drawable.buttom_selector_bg);
						findViewById(R.id.btn_qfsh).setEnabled(true);
					}
					QfzzxNum.setText("" + total);
				}
				wan.notifyDataSetChanged();
			}
		});
	}
	
	/**
	 * 获取清分管理元任务列表 线程
	 * @author Administrator
	 */
	class JhdanThread extends Thread{
		Message m;
		
		public JhdanThread() {
			super();
			m = handler.obtainMessage();
		}
		@Override
		public void run() {
			super.run();
			ICleaningManagementService is = new ICleaningManagementService();
			try {
				String str = is.getQinglinzongjindu(S_application.getApplication().jhdId);
				if(!"".equals(str)){
					tables = Table.doParse(str);
					//已完成任务单
					S_application.getApplication().ywcjhdlist =TableQuzhi.getList(tables[0], "renwudan");
					S_application.getApplication().ywcjhdzmlist =TableQuzhi.getList(tables[0], "zuming");
					S_application.getApplication().ywcjhdzidlist = TableQuzhi.getList(tables[0], "zuId");
					S_application.getApplication().ywcjhdzzbhlist = TableQuzhi.getList(tables[0], "zzxbianhao");
							
					// 未完成任务单
					S_application.getApplication().wwcjhdlist =TableQuzhi.getList(tables[1],"renwudan");
					S_application.getApplication().wwcjhdzidlist = TableQuzhi.getList(tables[1],"zuId");
					S_application.getApplication().wwcjhdzmlist = TableQuzhi.getList(tables[1],"zuming");
					
					m.what = 1;
				}
			} catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				m.what = -4;
			}catch (Exception e) {
				e.printStackTrace();
				m.what = -1;
			}finally{
				handler.sendMessage(m);
				GolbalUtil.onclicks = true;
			}
		}
	}
		
	/**
	 * 获取数据
	 */
	public void getData() {
		list = new ArrayList<Qingfendan>();
		// 封装已完成信息数据
		if (myApplication.ywcjhdlist != null) {
			for (int i = 0; i < myApplication.ywcjhdlist.size(); i++) {
				Qingfendan qf = new Qingfendan();
				qf.setQfNum(myApplication.ywcjhdlist.get(i));
				qf.setQfXiaozu(myApplication.ywcjhdzmlist.get(i));
				qf.setQfzzxbh(myApplication.ywcjhdzzbhlist.get(i));// 周转箱编号
				list.add(qf);
			}
		}
		// 封装未完成信息数据
		if (myApplication.ywcjhdlist != null) {
			for (int i = 0; i < myApplication.wwcjhdlist.size(); i++) {
				Qingfendan qf = new Qingfendan();
				qf.setQfNum(myApplication.wwcjhdlist.get(i));
				qf.setQfXiaozu(myApplication.wwcjhdzmlist.get(i));
				wlist.add(qf);
			}
		}
		for (int i = 0; i < list.size(); i++) {
			clist.add(0);
		}
	}
	
	/**
	 * 清分完成列表显示
	 * 
	 * @author Administrator
	 */
	class WanChengAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			final QingfenEntivity entity;
			if (arg1 == null) {
				entity = new QingfenEntivity();
				arg1 = LayoutInflater.from(QingfenJhdMxActivity.this).inflate(
						R.layout.item_qingfen_wanceng_s, null);
				entity.checkbox1 = (CheckBox) arg1
						.findViewById(R.id.wccheckBox);
				entity.textView1 = (TextView) arg1
						.findViewById(R.id.qfxiaozuName);
				entity.textView2 = (TextView) arg1
						.findViewById(R.id.qfxianluName);
				arg1.setTag(entity);
			} else {
				entity = (QingfenEntivity) arg1.getTag();
			}
			entity.checkbox1.setOnCheckedChangeListener(new SelectListen(arg0));
			if (clist.get(arg0) == 1) {
				entity.checkbox1.setChecked(true);
			} else {
				entity.checkbox1.setChecked(false);
			}
			entity.textView2.setText(list.get(arg0).getQfNum());
			entity.textView1.setText(list.get(arg0).getQfXiaozu());
			return arg1;
		}
	}

	/**
	 * cheBox 单击事件
	 * 
	 * @author Administrator
	 * 
	 */
	class SelectListen implements OnCheckedChangeListener {
		private int position;

		public SelectListen(int position) {
			super();
			this.position = position;

		}

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			isFlag = 0;
			if (arg1) {
				if (count < list.size()) {
					count++;
					clist.set(position, 1);
					int a = 0;
					for (int b : clist) {
						if (b == 1) {
							a++;
						}
					}
					if (a == clist.size()) {
						allCb.setChecked(true);
					} else {
						allCb.setChecked(false);
					}
					if (flag == true) {
						int aaaaa = -1;
						if (newlist.size() != 0) {
							for (int i = 0; i < newlist.size(); i++) {
								if (list.get(position).getQfXiaozu()
										.equals(newlist.get(i).getQfXiaozu())) {
									aaaaa = i;
									break;
								} else {
									aaaaa = -1;
								}
							}
						}
						if (aaaaa == -1) {
							newlist.add(list.get(position));
						}

						int cd = 0;
						for (int i = 0; i < newlist.size(); i++) {
							String[] str = newlist.get(i).getQfzzxbh()
									.split("_");
							cd = cd + str.length;
						}
						QfzzxNum.setText("" + cd);
					} else {
						QfzzxNum.setText("" + total);
					}
					qfdXuanzNum.setText("" + count);
				}

			} else {
				// flag = false;
				if (count > 0) {
					count--;
					clist.set(position, 0);
					allCb.setChecked(false);
					for (int i = 0; i < newlist.size(); i++) {
						if (list.get(position).getQfXiaozu()
								.equals(newlist.get(i).getQfXiaozu())) {
							newlist.remove(i);
							break;
						}
					}
					int dd = 0;
					for (int i = 0; i < newlist.size(); i++) {
						String[] str = newlist.get(i).getQfzzxbh().split("_");
						dd = dd + str.length;
					}
					qfdXuanzNum.setText("" + count);
					QfzzxNum.setText("" + dd);
					flag = true;
				}
			}
			findViewById(R.id.btn_qfsh).setBackgroundResource(
					R.drawable.buttom_selector_bg);
			findViewById(R.id.btn_qfsh).setEnabled(true);
			if (count == 0) {
				findViewById(R.id.btn_qfsh).setBackgroundResource(
						R.drawable.gray_btn_bg);
				findViewById(R.id.btn_qfsh).setEnabled(false);
			}
			wan.notifyDataSetChanged();
			isFlag = 1;

		}

	}

	/**
	 * 清分未完成列表显示
	 * 
	 * @author Administrator
	 */
	class WeiWanChengAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return wlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return wlist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			QingfenEntivity entity;
			if (arg1 == null) {
				entity = new QingfenEntivity();
				arg1 = LayoutInflater.from(QingfenJhdMxActivity.this).inflate(
						R.layout.item_qingfen_weiwanceng_s, null);
				entity.textView1 = (TextView) arg1
						.findViewById(R.id.qfxianluName1);
				entity.textView2 = (TextView) arg1
						.findViewById(R.id.qfxiaozuName1);
				arg1.setTag(entity);
			} else {
				entity = (QingfenEntivity) arg1.getTag();
			}
			entity.textView1.setText(wlist.get(arg0).getQfNum());
			entity.textView2.setText(wlist.get(arg0).getQfXiaozu());
			return arg1;
		}

	}

	/**
	 * 实体类控件
	 * 
	 * @author Administrator
	 */
	static class QingfenEntivity {
		public CheckBox checkbox1;
		public TextView textView1;
		public TextView textView2;
	}

	/**
	 * 列表点击事件
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		// Intent intent = new Intent(QingfenJhdMxActivity.this, null);
		// startActivity(intent);
	}

	/**
	 * 单击事件
	 */
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.qingfen_backS3:
			QingfenJhdMxActivity.this.finish();
			break;
		case R.id.item_qfwc:// 清分完成列表
			qfwc_show.setVisibility(View.VISIBLE);
			qfwwc_show.setVisibility(View.GONE);
			break;
		case R.id.item_qfwwc:// 清分未完成列表
			qfwc_show.setVisibility(View.GONE);
			qfwwc_show.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_qfsh:// 跳转 传选择的集合过去
			managerClass.getRuning().runding(QingfenJhdMxActivity.this,
					"开启扫描功能中...");
			Bundle bundle = new Bundle();
			bundle.putSerializable("newlist", (Serializable) newlist);
			Skip.skip(QingfenJhdMxActivity.this, ZzXiangKdSmiaoActivity.class,
					bundle, 0);
			break;
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		managerClass.getRuning().remove();
	}
}
