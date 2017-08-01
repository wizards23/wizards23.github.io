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
import com.ljsw.tjbankpda.db.entity.QingLingZhuangXiangChuKu;
import com.ljsw.tjbankpda.db.service.QingLingZhuangXiangChuKuLieBiao;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.manager.classs.pad.ManagerClass;

//getZhuangxiangJihuadanList
@SuppressLint("HandlerLeak")
public class QingLingZhuangXiangChuKu_db extends FragmentActivity implements
		OnClickListener, OnItemClickListener {
	private ImageView back;
	private Button update;
	private ListView listView;
	private String zhuangxiangJihuadan;
	private Table[] jihuadan;
	private ManagerClass manager;
	private OnClickListener OnClick1;
	private MingXiAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_renwumingxi);
		load();
		adapter = new MingXiAdapter();
		o_Application.danhao = null;
		manager = new ManagerClass();
		OnClick1 = new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getZhuangXiangInfo();
				manager.getAbnormal().remove();
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(o_Application.qinglingchuku_jihuadan.size()>0){
			o_Application.qinglingchuku_jihuadan.clear();
		}	
		getZhuangXiangInfo();

	}

	public void load() {
		back = (ImageView) findViewById(R.id.renwumingxi_back);
		back.setOnClickListener(this);
		update = (Button) findViewById(R.id.renwumingxi_update);
		update.setOnClickListener(this);
		listView = (ListView) findViewById(R.id.renwumingxi_listview);
		listView.setOnItemClickListener(this);
	}

	public void getZhuangXiangInfo() {
		manager.getRuning().runding(this, "数据加载中...");
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					zhuangxiangJihuadan = new QingLingZhuangXiangChuKuLieBiao()
							.getZhuangxiangJihuadanList(o_Application.kuguan_db
									.getOrganizationId());
					if (!zhuangxiangJihuadan.equals("")) {
						jihuadan = Table.doParse(zhuangxiangJihuadan);
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
				manager.getAbnormal().timeout(QingLingZhuangXiangChuKu_db.this,
						"连接超时...", OnClick1);
				break;
			case 1:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(QingLingZhuangXiangChuKu_db.this,
						"网络连接失败,重试?", OnClick1);
				break;
			case 2:
				manager.getRuning().remove();
				getData();
				adapter.notifyDataSetChanged();
				listView.setAdapter(adapter);
//				new TurnListviewHeight(listView);
				break;
			case 3:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(QingLingZhuangXiangChuKu_db.this,
						"没有任务", OnClick1);
				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// 跳计划单明细
		o_Application.danhao = o_Application.qinglingchuku_jihuadan.get(arg2);
		Skip.skip(this, JiHuaDanMingXi_db.class, null, 0);

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.renwumingxi_back:
			QingLingZhuangXiangChuKu_db.this.finish();
			break;
		case R.id.renwumingxi_update:
			// 调接口 未实现
			o_Application.qinglingchuku_jihuadan.clear();
			getZhuangXiangInfo();
			break;
		default:
			break;
		}
	}
	/**
	 * 加载数据
	 */
	public void getData() {

		QingLingZhuangXiangChuKu ck =null;
		if (jihuadan[0].get("jihuaxiang").getValues().size() > 0) {
			for (int i = 0; i < jihuadan[0].get("jihuaxiang").getValues().size(); i++) {
				ck = new QingLingZhuangXiangChuKu(jihuadan[0].get("jihuaxiang").getValues().get(i), jihuadan[0].get("riqi").getValues().get(i));
				o_Application.qinglingchuku_jihuadan.add(ck);
			}
		}
	}

	class MingXiAdapter extends BaseAdapter {
		LayoutInflater lf = LayoutInflater
				.from(QingLingZhuangXiangChuKu_db.this);
		ViewHodler view;

		@Override
		public int getCount() {
			return o_Application.qinglingchuku_jihuadan.size();
		}

		@Override
		public Object getItem(int arg0) {
			return o_Application.qinglingchuku_jihuadan.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = lf.inflate(R.layout.adapter_renwumingxi, null);
				view = new ViewHodler();
				view.danhao = (TextView) arg1
						.findViewById(R.id.adapter_renwumingxi_danhao);
				view.riqi = (TextView) arg1
						.findViewById(R.id.adapter_renwumingxi_riqi);
				arg1.setTag(view);
			} else {
				view = (ViewHodler) arg1.getTag();
			}
			view.danhao.setText(o_Application.qinglingchuku_jihuadan.get(arg0)
					.getJihuadan());
			view.riqi.setText(o_Application.qinglingchuku_jihuadan.get(arg0)
					.getRiqi());
			return arg1;
		}

	}

	public static class ViewHodler {
		TextView danhao, riqi;
	}

	@Override
	protected void onPause() {
		super.onPause();
		manager.getRuning().remove();
		o_Application.qinglingchuku_jihuadan.clear();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		o_Application.danhao = null;
		System.gc();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			QingLingZhuangXiangChuKu_db.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
