package com.ljsw.tjbankpda.main;

import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import com.example.pda.R;
import com.ljsw.tjbankpda.qf.entity.PeiSongDan;
import com.ljsw.tjbankpda.qf.entity.QingfenRemwu;
import com.ljsw.tjbankpda.qf.service.QingfenRenwuService;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.manager.classs.pad.ManagerClass;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 清分进度页面
 * 
 * @author Administrator
 * 
 */
public class QingFenJinDu_qf extends FragmentActivity implements
		OnClickListener {
	private ImageView back;
	private Button saomiao;
	private ListView wlist, ylist;
	private String orderNum;
	private TextView tvYQinpeiCount, tvWQinpeiCount;
	List<PeiSongDan> wlistData = new ArrayList<PeiSongDan>(); // 未清分集合
	List<PeiSongDan> ylistData = new ArrayList<PeiSongDan>();// 已清分集合
	WeiQingAdapter wadapter;
	YiQingAdapter yadapter;
	Table[] RenwuData;
	private ManagerClass manager;// 弹出框
	private Handler okHandle = new Handler() {// 数据获取成功handler
		public void handleMessage(Message msg) {
			// 绑定Adapter
			ylistData.clear();
			wlistData.clear();
			List<String> ltY = RenwuData[0].get("yiqingpeisong").getValues();
			List<String> ltW = RenwuData[1].get("weiqingpeisong").getValues();
			if (ltY.size() > 0) {
				for (int i = 0; i < ltY.size(); i++) {
					ylistData.add(new PeiSongDan(RenwuData[0].get(
							"yiqingpeisong").get(i), RenwuData[0].get(
							"suoshuwangdian").get(i)));
				}
			}
			if (ltW.size() > 0) {
				for (int i = 0; i < ltW.size(); i++) {
					wlistData.add(new PeiSongDan(RenwuData[1].get(
							"weiqingpeisong").get(i), RenwuData[1].get(
							"suoshuwangdian").get(i)));
				}
			}

			wlist.setAdapter(wadapter);
			new TurnListviewHeight(wlist);
			ylist.setAdapter(yadapter);
			new TurnListviewHeight(ylist);
			// 设置未清配送单数量和已清配送单数量
			tvYQinpeiCount.setText(ylistData.size() + "");
			tvWQinpeiCount.setText(wlistData.size() + "");
			if (wlistData.size() != 0) {
				saomiao.setEnabled(true);
				saomiao.setBackgroundResource(R.drawable.buttom_selector_bg);
			} else {
				saomiao.setEnabled(false);
				saomiao.setBackgroundResource(R.drawable.gray_btn_bg);
			}
			manager.getRuning().remove();

			// 如果没有未清的配送单了 等待2秒 跳出该页面
			if (wlistData.size() <= 0) {
				Thread tiaozhuan = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(2000); // 等待2秒
							timeoutHandle.sendEmptyMessage(2);  //发送消息退出当前activity
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});

				tiaozhuan.start();

			}
		};
	};
	private Handler timeoutHandle = new Handler() {// 连接超时handler
		public void handleMessage(Message msg) {
			manager.getRuning().remove();
			if (msg.what == 0) {
				manager.getAbnormal().timeout(QingFenJinDu_qf.this, "数据连接超时",
						new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								getData();
								manager.getAbnormal().remove();
								manager.getRuning().runding(
										QingFenJinDu_qf.this, "数据加载中...");
							}
						});
			}
			if (msg.what == 1) {
				manager.getAbnormal().timeout(QingFenJinDu_qf.this, "网络连接失败",
						new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								getData();
								manager.getAbnormal().remove();
								manager.getRuning().runding(
										QingFenJinDu_qf.this, "数据加载中...");
							}
						});
			}else if(msg.what == 2){
				// 退出当前activity 并跳转到任务列表页面
				Skip.skip(QingFenJinDu_qf.this,QingfenRenwuActivity.class, null, 0);
				QingFenJinDu_qf.this.finish();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qingfenjindu);
		manager = new ManagerClass();
		manager.getRuning().runding(this, "数据加载中...");
		orderNum = super.getIntent().getExtras().getString("orderNum");
		load();

	}

	@Override
	protected void onStart() {
		super.onStart();
		if (ylistData.size() > 0) {
			ylistData.clear();
		}
		if (wlistData.size() > 0) {
			wlistData.clear();
		}
		getData();
	}

	public void load() {
		back = (ImageView) findViewById(R.id.qf_jindu_back);
		back.setOnClickListener(this);
		saomiao = (Button) findViewById(R.id.qf_jindu_chuku);
		saomiao.setEnabled(false);
		saomiao.setBackgroundResource(R.drawable.button_gray);
		saomiao.setOnClickListener(this);
		wlist = (ListView) findViewById(R.id.qf_jindu_weiqing_list);
		ylist = (ListView) findViewById(R.id.qf_jindu_yiqing_list);
		tvWQinpeiCount = (TextView) findViewById(R.id.qf_jindu_weiqing_count);
		tvYQinpeiCount = (TextView) findViewById(R.id.qf_jindu_yiqing_count);
		wadapter = new WeiQingAdapter();
		yadapter = new YiQingAdapter();

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.qf_jindu_back:
			QingFenJinDu_qf.this.finish();
			break;
		case R.id.qf_jindu_chuku:
			Bundle bundle = new Bundle();
			bundle.putString("orderNum", orderNum);
		//    bundle.putSerializable("list", (Serializable) ylistData);
			System.out.println("传过去了吗??");
			Skip.skip(this, ZhouZhuanXiangSaoMiao_qf.class, bundle, 0);
			break;
		default:
			break;
		}
	}

	class WeiQingAdapter extends BaseAdapter {
		WeiQingHolder wq;
		LayoutInflater lf = LayoutInflater.from(QingFenJinDu_qf.this);

		@Override
		public int getCount() {
			return wlistData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return wlistData.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				wq = new WeiQingHolder();
				arg1 = lf.inflate(R.layout.adapter_jihuadan_xianjin, null);
				wq.danhao = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_xianjin_juanbie);
				wq.wangdian = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_xianjin_count);
				arg1.setTag(wq);
			} else {
				wq = (WeiQingHolder) arg1.getTag();
			}
			wq.danhao.setText(wlistData.get(arg0).getDanhao());
			wq.wangdian.setText(wlistData.get(arg0).getWangdian());
			return arg1;
		}

	}

	public static class WeiQingHolder {
		TextView danhao, wangdian;
	}

	class YiQingAdapter extends BaseAdapter {
		YiQingHolder yh;
		LayoutInflater lf = LayoutInflater.from(QingFenJinDu_qf.this);

		@Override
		public int getCount() {
			return ylistData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return ylistData.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				yh = new YiQingHolder();
				arg1 = lf.inflate(R.layout.adapter_jihuadan_xianjin, null);
				yh.danhao = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_xianjin_juanbie);
				yh.wangdian = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_xianjin_count);
				arg1.setTag(yh);
			} else {
				yh = (YiQingHolder) arg1.getTag();
			}
			yh.danhao.setText(ylistData.get(arg0).getDanhao());
			yh.wangdian.setText(ylistData.get(arg0).getWangdian());
			return arg1;
		}

	}

	public static class YiQingHolder {
		TextView danhao, wangdian;
	}

	public void getData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String params;
				try {
					params = new QingfenRenwuService().getParams(orderNum,
							"getQfschedule");
					RenwuData = Table.doParse(params);
					okHandle.sendEmptyMessage(0);
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					timeoutHandle.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
					timeoutHandle.sendEmptyMessage(1);
				}
			}
		}).start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			QingFenJinDu_qf.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
