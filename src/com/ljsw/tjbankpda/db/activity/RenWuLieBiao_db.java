package com.ljsw.tjbankpda.db.activity;

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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.application.GApplication;
import com.example.pda.R;
import com.ljsw.tjbankpda.db.application.o_Application;
import com.ljsw.tjbankpda.db.service.KuGuanRenWuLieBiao;
import com.ljsw.tjbankpda.qf.entity.RenWu;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.manager.classs.pad.ManagerClass;
import com.service.FixationValue;

@SuppressLint("HandlerLeak")
public class RenWuLieBiao_db extends FragmentActivity implements
		OnClickListener, OnItemClickListener {
	private LinearLayout layout1, layout2, layout3, layout4;
	private ListView listView1, listView2;
	private Button update;
	private ImageView back;
	List<RenWu> list = new ArrayList<RenWu>();
	List<RenWu> list2 = new ArrayList<RenWu>();

	private ManagerClass manager;
	private String reuwuliebiao;
	private Table[] renwuinfo;
	private OnClickListener OnClic1;
	private TextView chukucount, rukucount, wangdian;
	private ChuKuAdapter adapter;
	private RuChuKuAdapter ruadapter;
	private int chukuc_count, ruku_count;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_kuguangongzuorenwu_db);
		load();

		// 如果是清分管理员 隐藏部分功能
		if (GApplication.user.getLoginUserId().equals(
				FixationValue.waibaoQingfenString)) {
			layout1.setVisibility(View.GONE);
			layout3.setVisibility(View.GONE);
		}
		manager = new ManagerClass();
		adapter = new ChuKuAdapter();
		ruadapter = new RuChuKuAdapter();

		// 加载失败重新调用
		OnClic1 = new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				list.clear();
				list2.clear();
				getRenWuInfo();
				manager.getAbnormal().remove();
			}
		};

		listView1.setAdapter(adapter);
		listView2.setAdapter(ruadapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		list.clear();
		list2.clear();
		getRenWuInfo();
	}

	@Override
	protected void onPause() {
		super.onPause();
		manager.getAbnormal().remove();
		manager.getRuning().remove();
		list.clear();
		list2.clear();
		chukuc_count = 0;
		ruku_count = 0;
		chukucount.setText("");
		rukucount.setText("");
		wangdian.setText("");

	}

	/**
	 * 
	 */
	public void load() {
		layout1 = (LinearLayout) findViewById(R.id.renwu_layout1);
		layout2 = (LinearLayout) findViewById(R.id.renwu_layout2);
		layout3 = (LinearLayout) findViewById(R.id.renwu_layout3);
		layout4 = (LinearLayout) findViewById(R.id.renwu_layout4);
		listView1 = (ListView) findViewById(R.id.renwulistview1);
		listView2 = (ListView) findViewById(R.id.renwulistview2);
		back = (ImageView) findViewById(R.id.renwu_back);
		update = (Button) findViewById(R.id.renwu_update);
		chukucount = (TextView) findViewById(R.id.renwuliebiao_chuku_count);
		rukucount = (TextView) findViewById(R.id.renwuliebiao_ruku_count);
		wangdian = (TextView) findViewById(R.id.renwu_wangdian);
		update.setOnClickListener(this);
		back.setOnClickListener(this);
		layout1.setOnClickListener(this);
		layout2.setOnClickListener(this);
		listView1.setOnItemClickListener(this);
		listView2.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.renwu_layout1:
			layout3.setVisibility(View.VISIBLE);
			layout4.setVisibility(View.GONE);
			break;
		case R.id.renwu_layout2:
			layout3.setVisibility(View.GONE);
			layout4.setVisibility(View.VISIBLE);
			break;
		case R.id.renwu_back:
			RenWuLieBiao_db.this.finish();
			break;
		case R.id.renwu_update:
			// 无接口 未实现
			chukuc_count = 0;
			ruku_count = 0;
			list.clear();
			list2.clear();
			getRenWuInfo();
			break;
		default:
			break;
		}
	}

	/**
	 * 获得任务列表信息
	 */
	public void getRenWuInfo() {
		manager.getRuning().runding(this, "数据加载中...");
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					reuwuliebiao = new KuGuanRenWuLieBiao()
							.getStoremanTaskList(o_Application.kuguan_db
									.getOrganizationId(),GApplication.user.getLoginUserId());
					if (!reuwuliebiao.equals("")) {
						renwuinfo = Table.doParse(reuwuliebiao);
						handler.sendEmptyMessage(2);
					} else {
						handler.sendEmptyMessage(3);
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(0);
				} catch (NullPointerException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(3);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(1);
				}
			}

		}.start();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(RenWuLieBiao_db.this,
						"加载超时,是否重新加载?", OnClic1);
				break;
			case 1:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(RenWuLieBiao_db.this,
						"网络连接失败,重新加载?", OnClic1);
				break;
			case 2:
				manager.getRuning().remove();
				getData();
				getRuKu();
				if (list.size() > 0 && list2.size() > 0) {
					for (int i = 0; i < 4; i++) {
						chukuc_count += Integer
								.parseInt(list.get(i).getCount());
					}
					chukucount.setText("" + chukuc_count);

					for (int j = 0; j < list2.size(); j++) {
						ruku_count += Integer.parseInt(list2.get(j).getCount());
					}
					rukucount.setText("" + ruku_count);
				}
				wangdian.setText(o_Application.kuguan_db.getOrganizationName());
				break;
			case 3:
				manager.getRuning().remove();
				// manager.getAbnormal().timeout(RenWuLieBiao_db.this,
				// "获取任务失败,重试?", OnClic1);
				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg0.getId()) {
		case R.id.renwulistview1:
			switch (arg2) {
			case 0:
				Skip.skip(RenWuLieBiao_db.this,
						QingLingZhuangXiangChuKu_db.class, null, 0);
				break;
			case 1:
				Skip.skip(RenWuLieBiao_db.this,
						ShangJiaoQingFenLieBiao_db.class, null, 0);
				break;
			case 2:
				Skip.skip(RenWuLieBiao_db.this, PeiSongChuKuXianLu_db.class,
						null, 0);
				break;
			case 3:
				Skip.skip(RenWuLieBiao_db.this, ShangJiaoChuKu_db.class, null,
						0);
				break;
			default:
				break;
			}
			break;
		case R.id.renwulistview2:
			if (GApplication.user.getLoginUserId().equals(
					FixationValue.waibaoQingfenString)) {
				Skip.skip(RenWuLieBiao_db.this, ShangJiaoRuKu_db.class,
						null, 0);
			} else {
				switch (arg2) {
				case 0:// 跳转请领装箱入库
					Skip.skip(RenWuLieBiao_db.this,
							QingLingZhuangXiangRuKu_db.class, null, 0);
					break;
				case 1:
					Skip.skip(RenWuLieBiao_db.this, ShangJiaoRuKu_db.class,
							null, 0);
					break;
				}
				break;
			}

		}
	}

	class ChuKuAdapter extends BaseAdapter {
		LayoutInflater lf = LayoutInflater.from(RenWuLieBiao_db.this);
		ViewHodler view;

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

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = lf.inflate(R.layout.adapter_renwuliebiao, null);
				view = new ViewHodler();
				view.renwu = (TextView) arg1.findViewById(R.id.adapter_renwu);
				view.count = (TextView) arg1
						.findViewById(R.id.adapter_renwu_count);
				arg1.setTag(view);
			} else {
				view = (ViewHodler) arg1.getTag();
			}
			view.renwu.setText(list.get(arg0).getRenwu());
			view.count.setText(list.get(arg0).getCount());
			return arg1;
		}

	}

	public static class ViewHodler {
		TextView renwu, count;
	}

	class RuChuKuAdapter extends BaseAdapter {
		LayoutInflater lf = LayoutInflater.from(RenWuLieBiao_db.this);
		ruViewHodler view;

		@Override
		public int getCount() {
			return list2.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list2.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = lf.inflate(R.layout.adapter_renwuliebiao, null);
				view = new ruViewHodler();
				view.renwu = (TextView) arg1.findViewById(R.id.adapter_renwu);
				view.count = (TextView) arg1
						.findViewById(R.id.adapter_renwu_count);
				arg1.setTag(view);
			} else {
				view = (ruViewHodler) arg1.getTag();
			}
			view.renwu.setText(list2.get(arg0).getRenwu());
			view.count.setText(list2.get(arg0).getCount());
			return arg1;
		}

	}

	public static class ruViewHodler {
		TextView renwu, count;
	}

	public void getData() {
		list.add(new RenWu("请领装箱出库", renwuinfo[0].get("qinglingchuku")
				.getValues().get(0)));
		list.add(new RenWu("上缴清分出库", renwuinfo[0].get("shangjiaoqingfen")
				.getValues().get(0)));
		list.add(new RenWu("请领配送/派工交接", renwuinfo[0].get("qinglingpeisong")
				.getValues().get(0)));
		list.add(new RenWu("上缴出库", renwuinfo[0].get("shangjiaochuku")
				.getValues().get(0)));
		adapter.notifyDataSetChanged();

	}

	public void getRuKu() {
		if (!GApplication.user.getLoginUserId().equals(
				FixationValue.waibaoQingfenString)) {
			list2.add(new RenWu("请领装箱入库", renwuinfo[0].get("qingliruku")
					.getValues().get(0)));
		}

		list2.add(new RenWu("上缴入库", renwuinfo[0].get("shangjiaoruku")
				.getValues().get(0)));
		ruadapter.notifyDataSetChanged();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			RenWuLieBiao_db.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
