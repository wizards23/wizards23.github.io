package com.ljsw.tjbankpda.db.activity;

import java.net.SocketTimeoutException;

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
import android.widget.ListView;
import android.widget.TextView;

import com.example.pda.R;
import com.ljsw.tjbankpda.db.application.o_Application;
import com.ljsw.tjbankpda.db.service.QingLingZhuangXiangRuKu;
import com.ljsw.tjbankpda.qf.entity.QingLingRuKu;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.manager.classs.pad.ManagerClass;

@SuppressLint("HandlerLeak")
public class QingLingZhuangXiangRuKu_db extends FragmentActivity implements
		OnClickListener, OnItemClickListener {

	private Button update;
	private ImageView back;
	private ListView listview;
	// List<QingLingRuKu> list = new ArrayList<QingLingRuKu>();

	private QingLingAdapter adapter;
	private String parms;
	private OnClickListener OnClick1;
	private Table[] rukulist;
	private ManagerClass manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_qinglingzhuangxiangruku);
		load();
		adapter = new QingLingAdapter();
		manager = new ManagerClass();
		OnClick1 = new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				manager.getAbnormal().remove();
				getRuKu();
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (o_Application.qinglingruku.size() > 0) {
			o_Application.qinglingruku.clear();
		}
		adapter.notifyDataSetChanged();
		listview.setAdapter(adapter);
//		new TurnListviewHeight(listview);
		getRuKu();
	}

	public void getRuKu() {
		manager.getRuning()
				.runding(QingLingZhuangXiangRuKu_db.this, "数据加载中...");
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					parms = new QingLingZhuangXiangRuKu()
							.getQinglingRuku(o_Application.kuguan_db
									.getOrganizationId());
					if (!parms.equals("")) {
						rukulist = Table.doParse(parms);
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
				manager.getAbnormal().timeout(QingLingZhuangXiangRuKu_db.this,
						"加载超时,重试?", OnClick1);
				break;
			case 1:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(QingLingZhuangXiangRuKu_db.this,
						"网络连接失败,重试?", OnClick1);
				break;
			case 2:
				manager.getRuning().remove();
				getData();
				adapter.notifyDataSetChanged();
				listview.setAdapter(adapter);
//				new TurnListviewHeight(listview);
				break;
			case 3:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(QingLingZhuangXiangRuKu_db.this,
						"没有任务", new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
							}
						});
				break;
			default:
				break;
			}
		}

	};

	public void load() {
		update = (Button) findViewById(R.id.ql_ruku_update);
		back = (ImageView) findViewById(R.id.ql_ruku_back);
		listview = (ListView) findViewById(R.id.ql_ruku_listView1);
		listview.setOnItemClickListener(this);
		update.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		o_Application.qlruku = o_Application.qinglingruku.get(arg2);
		manager.getRuning().runding(this, "扫描功能开启中...");
		Skip.skip(this, QingLingZhuangXiangRuKuSaoMiao_db.class, null, 0);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ql_ruku_back:
			QingLingZhuangXiangRuKu_db.this.finish();
			break;
		case R.id.ql_ruku_update:
			if (o_Application.qinglingruku.size() > 0) {
				o_Application.qinglingruku.clear();
			}
			getRuKu();
			break;
		default:
			break;
		}

	}

	class QingLingAdapter extends BaseAdapter {
		LayoutInflater lf = LayoutInflater
				.from(QingLingZhuangXiangRuKu_db.this);
		ViewHodler view;

		@Override
		public int getCount() {
			return o_Application.qinglingruku.size();
		}

		@Override
		public Object getItem(int arg0) {
			return o_Application.qinglingruku.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				view = new ViewHodler();
				arg1 = lf.inflate(R.layout.adapter_qinglingruku, null);
				view.danhao = (TextView) arg1
						.findViewById(R.id.adapter_qingling_bianhao);
				view.riqi = (TextView) arg1
						.findViewById(R.id.adapter_qingling_xianlu);
				view.count = (TextView) arg1
						.findViewById(R.id.adapter_qingling_count);
				arg1.setTag(view);
			} else {
				view = (ViewHodler) arg1.getTag();
			}
			view.danhao.setText(o_Application.qinglingruku.get(arg0)
					.getDanhao());
			view.riqi.setText(o_Application.qinglingruku.get(arg0).getRiqi());
			view.count.setText(o_Application.qinglingruku.get(arg0)
					.getZhouzhuanxiang().size()
					+ "");
			return arg1;
		}

	}

	public static class ViewHodler {
		TextView danhao, riqi, count;
	}

	public void getData() {
		if (rukulist[0].get("riqi").getValues().size() > 0) {
			for (int i = 0; i < rukulist.length; i++) {
				o_Application.qinglingruku.add(new QingLingRuKu(rukulist[i]
						.get("jihuadan").getValues().get(0), rukulist[i]
						.get("riqi").getValues().get(0), rukulist[i].get(
						"zhouzhuanxiang").getValues()));
				System.out.println("-");
				/*
				 * System.out.println("请领装箱入库：" +
				 * rukulist[i].get("zhouzhuanxiang").getValues());
				 * System.out.println("请领装箱入库：" +
				 * rukulist[i].get("riqi").getValues().get(0));
				 * System.out.println("请领装箱入库：" +
				 * rukulist[i].get("jihuadan").getValues().get(0));
				 */
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		manager.getRuning().remove();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			QingLingZhuangXiangRuKu_db.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
