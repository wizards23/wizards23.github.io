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
import com.ljsw.tjbankpda.db.entity.ShangJiaoQingFenChuKu;
import com.ljsw.tjbankpda.db.service.ShangJiaoQingFenJiHuaDan;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.manager.classs.pad.ManagerClass;
/**
 * 上缴清分计划单页面
 * @author Administrator
 *
 */
@SuppressLint("HandlerLeak")
public class ShangJiaoQingFenLieBiao_db extends FragmentActivity implements
		OnClickListener, OnItemClickListener {
	private ListView listview;
	private Button update;
	private ImageView back;
	private QingFenAdapter adapter;
	private ManagerClass manager;
	private String jihuadan;
	private Table[] jihualist;
	private OnClickListener OnClick1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shangjiaoqingfenchuku);
		load();
		adapter = new QingFenAdapter();
		manager = new ManagerClass();
		OnClick1 = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getQingFenJiHua();
				manager.getAbnormal().remove();
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		getQingFenJiHua();
		adapter.notifyDataSetChanged();
		listview.setAdapter(adapter);
//		new TurnListviewHeight(listview);

	}

	public void load() {
		listview = (ListView) findViewById(R.id.sj_qingfen_listview);
		listview.setOnItemClickListener(this);
		update = (Button) findViewById(R.id.sj_qingfen_update);
		update.setOnClickListener(this);
		back = (ImageView) findViewById(R.id.sj_qingfen_back);
		back.setOnClickListener(this);
	}

	public void getQingFenJiHua() {
		manager.getRuning().runding(this, "数据加载中...");
		new Thread() {

			@Override
			public void run() {
				super.run();
				try {// GApplication.user.getOrganizationId()
					jihuadan = new ShangJiaoQingFenJiHuaDan()
							.getShangjiaoJihuaMingxi(o_Application.kuguan_db
									.getOrganizationId());
					if (!jihuadan.equals("")) {
						manager.getRuning().remove();
						jihualist = Table.doParse(jihuadan);
						handler.sendEmptyMessage(2);
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
				manager.getAbnormal().timeout(ShangJiaoQingFenLieBiao_db.this,
						"连接超时...", OnClick1);
				break;
			case 1:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(ShangJiaoQingFenLieBiao_db.this,
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
				manager.getAbnormal().timeout(ShangJiaoQingFenLieBiao_db.this,
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		o_Application.qingfendanmingxi = o_Application.shangjiao_qingfen_chuku
				.get(arg2);
		Skip.skip(this, QingFenJiHuaMingXi_db.class, null, 0);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.sj_qingfen_back:
			ShangJiaoQingFenLieBiao_db.this.finish();
			break;
		case R.id.sj_qingfen_update:
			o_Application.shangjiao_qingfen_chuku.clear();
			getQingFenJiHua();
			break;
		default:
			break;
		}
	}

	class QingFenAdapter extends BaseAdapter {
		LayoutInflater lf = LayoutInflater
				.from(ShangJiaoQingFenLieBiao_db.this);
		ViewHodler view;

		@Override
		public int getCount() {
			return o_Application.shangjiao_qingfen_chuku.size();
		}

		@Override
		public Object getItem(int arg0) {
			return o_Application.shangjiao_qingfen_chuku.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = lf.inflate(R.layout.adapter_jihuadan_xianjin, null);
				view = new ViewHodler();
				view.renwu = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_xianjin_juanbie);
				view.count = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_xianjin_count);
				arg1.setTag(view);
			} else {
				view = (ViewHodler) arg1.getTag();
			}
			view.renwu.setText(o_Application.shangjiao_qingfen_chuku.get(arg0)
					.getJihuadan());
			view.count.setText(o_Application.shangjiao_qingfen_chuku.get(arg0)
					.getZzxcount() + "");
			return arg1;
		}

	}

	public static class ViewHodler {
		TextView renwu, count;
	}

	public void getData() {
		if (jihualist[0].get("jihuadan").getValues().size() > 0) {
			for (int i = 0; i < jihualist.length; i++) {
				o_Application.shangjiao_qingfen_chuku
						.add(new ShangJiaoQingFenChuKu(jihualist[i]
								.get("jihuadan").getValues().get(0),
								jihualist[i].get("xianluming").getValues(),
								jihualist[i].get("shuliang").getValues(),
								jihualist[i].get("zhouzhuanxiang").getValues(),
								jihualist[i].get("zhouzhuanxiang").getValues()
										.size()));
				/*
				 * System.out.println("jihualist:" + jihualist[i].toString());
				 * System.out.println("jihuadan:"+
				 * jihualist[i].get("jihuadan").getValues().get(0));
				 * System.out.println("xianluming:"+
				 * jihualist[i].get("xianluming").getValues());
				 * System.out.println("shuliang:"+
				 * jihualist[i].get("shuliang").getValues());
				 * System.out.println("xianluming:"+
				 * jihualist[i].get("zhouzhuanxiang").getValues());
				 * System.out.println("xianluming:"+
				 * jihualist[i].get("zhouzhuanxiang").getValues().size());
				 */
			}
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		o_Application.shangjiao_qingfen_chuku.clear();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ShangJiaoQingFenLieBiao_db.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
