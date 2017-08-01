package com.ljsw.tjbankpda.db.activity;

import java.net.SocketTimeoutException;
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
import android.widget.ListView;
import android.widget.TextView;

import com.example.pda.R;
import com.ljsw.tjbankpda.db.application.o_Application;
import com.ljsw.tjbankpda.db.entity.ShangJiaoChuku;
import com.ljsw.tjbankpda.db.service.ShangJiaoChuKu;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.manager.classs.pad.ManagerClass;

@SuppressLint("HandlerLeak")
public class ShangJiaoChuKu_db extends FragmentActivity implements
		OnClickListener, OnItemClickListener {
	private ImageView back;
	private Button update;
	private ListView listview;
	// List<RenWuMingXi> list = new ArrayList<RenWuMingXi>();

	private MingXiAdapter adapter;
	private ManagerClass manager;
	private String parms;
	private OnClickListener OnClick1;
	private Table[] chukulist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shangjiaochuku);
		load();

		adapter = new MingXiAdapter();
		manager = new ManagerClass();
		OnClick1 = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getShangJiaoChuKu();
				manager.getAbnormal().remove();
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
		listview.setAdapter(adapter);
		getShangJiaoChuKu();
	}

	public void getShangJiaoChuKu() {
		manager.getRuning().runding(this, "数据加载中...");
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {

					// GApplication.user.getOrganizationId()
					parms = new ShangJiaoChuKu()
							.getShangjiaochukuMingxi(o_Application.kuguan_db
									.getOrganizationId());
					if (!parms.equals("")) {
						manager.getRuning().remove();
						chukulist = Table.doParse(parms);
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
				manager.getAbnormal().timeout(ShangJiaoChuKu_db.this,
						"加载超时,重试?", OnClick1);
				break;
			case 1:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(ShangJiaoChuKu_db.this,
						"加载超时,重试?", OnClick1);
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
				manager.getAbnormal().timeout(ShangJiaoChuKu_db.this, "无任务/获取任务失败,重试?",
						new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								getShangJiaoChuKu();
								manager.getAbnormal().remove();

							}
						});
			default:
				break;
			}
		}

	};

	public void load() {
		back = (ImageView) findViewById(R.id.shangjiaochuku_back);
		back.setOnClickListener(this);
		update = (Button) findViewById(R.id.shangjiaochuku_update);
		update.setOnClickListener(this);
		listview = (ListView) findViewById(R.id.shangjiaochuku_listview);
		listview.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		o_Application.chukumingxi = o_Application.shangjiaochuku.get(arg2);
		Skip.skip(this, ShangJiaoChuKuMingXi_db.class, null, 0);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.shangjiaochuku_back:
			ShangJiaoChuKu_db.this.finish();
			break;
		case R.id.shangjiaochuku_update:
			// 为实现
			getShangJiaoChuKu();
			break;
		default:
			break;
		}

	}

	public void getData() {
		//zongxianluId:DL01|zongxianlu:东丽总行一线|zzxzongshu:6|zzxbianhao:BC0011,BC0012,BC0013,BC0014,BC0015,BC0016|
		//zhixianlu:西青一线,西青二线,西青三线|zzxshuliang:10,10,20; 
		/*public ShangJiaoChuku(String zongxianlu, String zongxuanluid,
				String zzxzongshu, List<String> zzxbianhao, List<String> zhixianlu,
				List<String> zzxshuliang) {*/
		if(o_Application.shangjiaochuku.size()>0){
			o_Application.shangjiaochuku.clear();
		}
		if (chukulist[0].get("zzxbianhao").getValues().size() > 0) {
			for (int i = 0; i < chukulist.length; i++) {
				o_Application.shangjiaochuku.add(new ShangJiaoChuku(
						chukulist[i].get("zongxianlu").getValues().get(0),
						chukulist[i].get("zongxianluId").getValues().get(0),
						chukulist[i].get("zzxzongshu").getValues().get(0),
						chukulist[0].get("zzxbianhao").getValues(),
						chukulist[0].get("zhixianlu").getValues(), chukulist[0]
								.get("zzxshuliang").getValues()));
			}

		}

	}

	class MingXiAdapter extends BaseAdapter {
		LayoutInflater lf = LayoutInflater.from(ShangJiaoChuKu_db.this);
		ViewHodler view;

		@Override
		public int getCount() {
			return o_Application.shangjiaochuku.size();
		}

		@Override
		public Object getItem(int arg0) {
			return o_Application.shangjiaochuku.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = lf.inflate(R.layout.adapter_qinglingruku, null);
				view = new ViewHodler();
				view.bianhao = (TextView) arg1
						.findViewById(R.id.adapter_qingling_bianhao);
				view.xianlu = (TextView) arg1
						.findViewById(R.id.adapter_qingling_xianlu);
				view.count = (TextView) arg1
						.findViewById(R.id.adapter_qingling_count);
				arg1.setTag(view);
			} else {
				view = (ViewHodler) arg1.getTag();
			}
			view.bianhao.setText(o_Application.shangjiaochuku.get(arg0)
					.getZongxuanluid());
			view.xianlu.setText(o_Application.shangjiaochuku.get(arg0)
					.getZongxianlu());
			view.count.setText(o_Application.shangjiaochuku.get(arg0)
					.getZzxzongshu());
			return arg1;
		}

	}

	public static class ViewHodler {
		TextView bianhao, xianlu, count;
	}

	@Override
	protected void onPause() {
		super.onPause();
		o_Application.shangjiaochuku.clear();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ShangJiaoChuKu_db.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
