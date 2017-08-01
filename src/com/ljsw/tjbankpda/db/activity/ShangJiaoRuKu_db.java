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

import com.application.GApplication;
import com.example.pda.R;
import com.ljsw.tjbankpda.db.application.o_Application;
import com.ljsw.tjbankpda.db.entity.ShangJiaoRuKu;
import com.ljsw.tjbankpda.db.service.PeiSongChuRuKu;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.manager.classs.pad.ManagerClass;
import com.service.FixationValue;

@SuppressLint("HandlerLeak")
public class ShangJiaoRuKu_db extends FragmentActivity implements
		OnClickListener, OnItemClickListener {
	private ImageView back;
	private Button update;
	private ListView listview;

	private ShangJiaoAdapter adapter;
	private String parms;
	private OnClickListener OnClick1;
	private ManagerClass manager;
	private Table[] rukulist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shangjiaoruku);
		adapter = new ShangJiaoAdapter();
		load();
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
		o_Application.shangjiaoruku.clear();  //重新获取数据
		getRuKu();
	}

	public void getRuKu() {
		manager.getRuning().runding(ShangJiaoRuKu_db.this, "数据加载中...");
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					if (GApplication.user.getLoginUserId().equals(
							FixationValue.waibaoQingfenString)) {
						parms = new PeiSongChuRuKu()
								.getWaibaoRuku(o_Application.kuguan_db
										.getOrganizationId());
					} else {
						parms = new PeiSongChuRuKu().getPeisongChuku(
								o_Application.kuguan_db.getOrganizationId(),
								"1");
					}

					if (!parms.equals("")) {
						rukulist = Table.doParse(parms);
						handler.sendEmptyMessage(2);
					} else {
						handler.sendEmptyMessage(3);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(3);
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(0);
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
				manager.getAbnormal().timeout(ShangJiaoRuKu_db.this,
						"加载超时,重试?", OnClick1);
				break;
			case 1:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(ShangJiaoRuKu_db.this,
						"网络连接超时,重试?", OnClick1);
				break;
			case 2:
				manager.getRuning().remove();
				getData();
				adapter.notifyDataSetChanged();
				break;
			case 3:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(ShangJiaoRuKu_db.this, "没有任务",
						new View.OnClickListener() {

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
		back = (ImageView) findViewById(R.id.shangjiao_back);
		back.setOnClickListener(this);
		update = (Button) findViewById(R.id.shangjiao_update);
		update.setOnClickListener(this);
		listview = (ListView) findViewById(R.id.shangjiao_listView1);
		listview.setOnItemClickListener(this);
		listview.setAdapter(adapter);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.shangjiao_back:
			ShangJiaoRuKu_db.this.finish();
			break;
		case R.id.shangjiao_update:
			if (o_Application.shangjiaoruku.size() > 0) {
				o_Application.shangjiaoruku.clear();
			}
			getRuKu();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		manager.getRuning().runding(this, "开启扫码功能中...");
		o_Application.rukumingxi = o_Application.shangjiaoruku.get(arg2);
		Skip.skip(this, ShangJiaoRuKuSaoMiao_db.class, null, 0);
	}

	class ShangJiaoAdapter extends BaseAdapter {
		LayoutInflater lf = LayoutInflater.from(ShangJiaoRuKu_db.this);
		ViewHolder view;

		@Override
		public int getCount() {
			return o_Application.shangjiaoruku.size();
		}

		@Override
		public Object getItem(int arg0) {
			return o_Application.shangjiaoruku.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = lf.inflate(R.layout.adapter_qinglingruku, null);
				view = new ViewHolder();
				view.bianhao = (TextView) arg1
						.findViewById(R.id.adapter_qingling_bianhao);
				view.xianlu = (TextView) arg1
						.findViewById(R.id.adapter_qingling_xianlu);
				view.count = (TextView) arg1
						.findViewById(R.id.adapter_qingling_count);
				arg1.setTag(view);
			} else {
				view = (ViewHolder) arg1.getTag();
			}
			view.bianhao.setText(o_Application.shangjiaoruku.get(arg0)
					.getXianluid());
			view.xianlu.setText(o_Application.shangjiaoruku.get(arg0)
					.getXianluming());
			view.count.setText(o_Application.shangjiaoruku.get(arg0)
					.getKuxiang().size()
					+ "");
			return arg1;
		}

	}

	public static class ViewHolder {
		TextView bianhao, xianlu, count;
	}

	/**
	 * 获取数据.. -->SM 修改
	 */
	public void getData() {
		if (rukulist[0].get("zhouzhuangxiang").getValues().size() > 0) {
			for (int i = 0; i < rukulist.length; i++) {
				o_Application.shangjiaoruku.add(new ShangJiaoRuKu(rukulist[i]
						.get("xianlumingcheng").getValues().get(0), rukulist[i]
						.get("xianluId").getValues().get(0), rukulist[i].get(
						"zhouzhuangxiang").getValues(), rukulist[i]
						.get("xianluType").getValues().get(0)));
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		o_Application.shangjiaoruku.clear();
		manager.getRuning().remove();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ShangJiaoRuKu_db.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
