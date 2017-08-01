package com.ljsw.tjbankpda.db.activity;

import java.net.SocketTimeoutException;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.pda.R;
import com.ljsw.tjbankpda.db.application.o_Application;
import com.ljsw.tjbankpda.db.service.JiHuaDanMingXi;
import com.ljsw.tjbankpda.qf.entity.JiHuaDan_Xianjin;
import com.ljsw.tjbankpda.qf.entity.JiHuaDan_Zhongkong;
import com.ljsw.tjbankpda.util.MyListView;
import com.ljsw.tjbankpda.util.NumFormat;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.manager.classs.pad.ManagerClass;

/**
 * 装箱计划单明细
 * @author yuyunheng
 *
 */
@SuppressLint("HandlerLeak")
public class JiHuaDanMingXi_db extends FragmentActivity implements
		OnClickListener {
	private Button update, saomiao;
	private ImageView back;
	private MyListView xianjin_list, zhongkong_list;
	private TextView dizhiyapin, xianjin_count, zhongkong_count;

	private ManagerClass manager;
	private OnClickListener OnClick1;
	private String mingxi;
	private Table[] mingxilist;
	private XianJinAdapter adapterXianjin;
	private ZhongKongAdapter adapterZhongkong;
	private int zksum;
	private double xjsum;  //现金数量

	// private QingLingZhuangXiangChuKu danhao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_jihuamingxi);
		load();
		adapterXianjin = new XianJinAdapter();
		adapterZhongkong = new ZhongKongAdapter();
		manager = new ManagerClass();
		OnClick1 = new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getJiHuaMingXi();
				manager.getAbnormal().remove();

			}
		};

	}

	@Override
	protected void onResume() {
		super.onResume();
		o_Application.jihuadan_list_dizhiyapin.clear();
		o_Application.jihuadan_list_xianjin.clear();
		o_Application.jihuadan_list_zhongkong.clear();
		adapterXianjin.notifyDataSetChanged();
		adapterZhongkong.notifyDataSetChanged();
		getJiHuaMingXi();
	}

	public void load() {
		back = (ImageView) findViewById(R.id.jihuadan_back);
		update = (Button) findViewById(R.id.jihuadan_update);
		update.setOnClickListener(this);
		xianjin_list = (MyListView) findViewById(R.id.jihuadan_xianjin_listview);
		zhongkong_list = (MyListView) findViewById(R.id.jihuadan_zhongkong_list);
		saomiao = (Button) findViewById(R.id.jihuadan_dizhisaomiao);
		dizhiyapin = (TextView) findViewById(R.id.tv_jihuadan_dizhiyapin);
		xianjin_count = (TextView) findViewById(R.id.tv_jihuadan_xianjin);
		zhongkong_count = (TextView) findViewById(R.id.tv_jihuadan_zhongkong);
		back.setOnClickListener(this);
		saomiao.setOnClickListener(this);
	}

	/*
	 * public void getBundle() { Intent intent = getIntent(); Bundle bundle =
	 * intent.getExtras(); danhao = (QingLingZhuangXiangChuKu)
	 * bundle.getSerializable("danhao"); }
	 */

	public void getJiHuaMingXi() {
		manager.getRuning().runding(this, "数据加载中...");
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					mingxi = new JiHuaDanMingXi()
							.getZhuangxiangJihuadanDetail(o_Application.danhao
									.getJihuadan());
					if (!mingxi.equals("")) {
						mingxilist = Table.doParse(mingxi);
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
				manager.getAbnormal().timeout(JiHuaDanMingXi_db.this,
						"连接超时...", OnClick1);
				break;
			case 1:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(JiHuaDanMingXi_db.this,
						"网络连接失败,重试?", OnClick1);
				break;
			case 2: //调用接口成功
				manager.getRuning().remove();
				if (o_Application.jihuadan_list_xianjin.size() == 0) {
					getXianjin();
					getZhongkong();
					getDiZhiYaPin();
					XJSUM();
					ZKSUM();
				}
				adapterXianjin.notifyDataSetChanged();
				adapterZhongkong.notifyDataSetChanged();
				xianjin_list.setAdapter(adapterXianjin);
//				new TurnListviewHeight(xianjin_list);
				zhongkong_list.setAdapter(adapterZhongkong);
//				new TurnListviewHeight(zhongkong_list);
				dizhiyapin.setText(mingxilist[2].get("dizhishu").getValues().get(0));
				System.out.println("现金总数:--->"+xjsum);
				xianjin_count.setText(new NumFormat().format(xjsum + ""));
				zhongkong_count.setText(zksum + "");
				break;
			case 3:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(JiHuaDanMingXi_db.this,
						"获取数据失败...", new View.OnClickListener() {

							@Override
							public void onClick(View v) {
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
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.jihuadan_back:
			JiHuaDanMingXi_db.this.finish();
			break;
		case R.id.jihuadan_update:
			// 调用接口 未实现
			xjsum = 0;
			zksum = 0;
			o_Application.jihuadan_list_xianjin.clear();
			o_Application.jihuadan_list_zhongkong.clear();
			o_Application.jihuadan_list_dizhiyapin.clear();
			getJiHuaMingXi();
			break;
		case R.id.jihuadan_dizhisaomiao:
			manager.getAbnormal().remove();
			if ((Integer.parseInt(mingxilist[2].get("dizhishu").getValues()
					.get(0)) != o_Application.jihuadan_list_dizhiyapin.size())) {
				manager.getAbnormal().timeout(this, "需要扫箱数量与可扫码数不符,是否继续?",
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								manager.getRuning().runding(
										JiHuaDanMingXi_db.this, "正在开启条形码扫描...");
								Log.i("起跳到验证指纹页面", "占时决定----");
								Skip.skip(JiHuaDanMingXi_db.this,
										QingLingChuKuJiaoJie_db.class, null, 0);
								System.out.println("跳过去了");
							}
						});
			} else {
				Log.i("起跳到验证指纹页面", "占时决定2222----");
				Skip.skip(JiHuaDanMingXi_db.this, QingLingChuKuJiaoJie_db.class,
						null, 0);
			}
			break;
		default:
			break;
		}
	}

	class XianJinAdapter extends BaseAdapter {
		LayoutInflater lf = LayoutInflater.from(JiHuaDanMingXi_db.this);
		ViewHolderXianJin viewXianJin;

		@Override
		public int getCount() {
			return o_Application.jihuadan_list_xianjin.size();
		}

		@Override
		public Object getItem(int arg0) {
			return o_Application.jihuadan_list_xianjin.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = lf.inflate(R.layout.adapter_jihuadan_xianjin, null);
				viewXianJin = new ViewHolderXianJin();
				viewXianJin.juanbie = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_xianjin_juanbie);
				viewXianJin.count = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_xianjin_count);
				arg1.setTag(viewXianJin);
			} else {
				viewXianJin = (ViewHolderXianJin) arg1.getTag();
			}
			viewXianJin.juanbie.setText(o_Application.jihuadan_list_xianjin
					.get(arg0).getJuanbie());
			viewXianJin.count.setText(o_Application.jihuadan_list_xianjin.get(
					arg0).getCount());
			return arg1;
		}

	}

	public static class ViewHolderXianJin {
		TextView juanbie, count;
	}

	class ZhongKongAdapter extends BaseAdapter {
		LayoutInflater lf = LayoutInflater.from(JiHuaDanMingXi_db.this);
		ViewHolderZhongkong viewzhongkong;

		@Override
		public int getCount() {
			return o_Application.jihuadan_list_zhongkong.size();
		}

		@Override
		public Object getItem(int arg0) {
			return o_Application.jihuadan_list_zhongkong.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = lf.inflate(R.layout.adapter_jihuadan_zhongkong, null);
				viewzhongkong = new ViewHolderZhongkong();
				viewzhongkong.pingzheng = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_zhongkong_type);
				viewzhongkong.count = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_zhongkong_count);
				arg1.setTag(viewzhongkong);
			} else {
				viewzhongkong = (ViewHolderZhongkong) arg1.getTag();
			}
			viewzhongkong.pingzheng
					.setText(o_Application.jihuadan_list_zhongkong.get(arg0)
							.getPingzheng());
			viewzhongkong.count.setText(o_Application.jihuadan_list_zhongkong
					.get(arg0).getCount());
			return arg1;
		}

	}

	/*
	 * 计算现金的总数
	 */
	public void XJSUM() {
		xjsum=0;  // 重置xjsum
		if (o_Application.jihuadan_list_xianjin.size() > 0) {
			for (int i = 0; i < o_Application.jihuadan_list_xianjin.size(); i++) {
				JiHuaDan_Xianjin xj=o_Application.jihuadan_list_xianjin.get(i);
				double jiazhi=Double.parseDouble(xj.getQuanJiazhi());   //取出现金的价值
				double shuliang=Double.parseDouble(xj.getCount());     //取出现金的数量
				xjsum+=jiazhi*shuliang;
			}
		}

	}

	public void ZKSUM() {
		zksum=0;  //重置zksum
		if (o_Application.jihuadan_list_zhongkong.size() > 0) {
			for (int i = 0; i < o_Application.jihuadan_list_zhongkong.size(); i++) {
				zksum += Integer.parseInt(o_Application.jihuadan_list_zhongkong
						.get(i).getCount());
			}
		}
	}

	public static class ViewHolderZhongkong {
		TextView pingzheng, count;
	}
	
	/**
	 * 获取现金 sm添加注释
	 */
	public void getXianjin() {
		if (mingxilist[0].get("quanbie").getValues().size() > 0) {
			for (int i = 0; i < mingxilist[0].get("quanbie").getValues().size(); i++) {
				o_Application.jihuadan_list_xianjin.add(new JiHuaDan_Xianjin(
						mingxilist[0].get("quanbie").getValues().get(i),
						mingxilist[0].get("shuliang").getValues().get(i),
						mingxilist[0].get("quanbieId").getValues().get(i),
						mingxilist[0].get("quanJiazhi").getValues().get(i)));
			}
		}
	}
	/**
	 * 获取重空 sm添加注释
	 */
	public void getZhongkong() {
		if (mingxilist[1].get("zhongkongtype").getValues().size() > 0) {
			for (int i = 0; i < mingxilist[1].get("zhongkongtype").getValues()
					.size(); i++) {
				o_Application.jihuadan_list_zhongkong
						.add(new JiHuaDan_Zhongkong(mingxilist[1]
								.get("zhongkongtype").getValues().get(i),
								mingxilist[1].get("shuliang").getValues()
										.get(i)));
			}
		}
	}
	/**
	 * 获取抵质押品 sm添加注释
	 */
	public void getDiZhiYaPin() {
		if (mingxilist[2].get("dizhibianhao").getValues().size() > 0) {
			for (int i = 0; i < mingxilist[2].get("dizhibianhao").getValues()
					.size(); i++) {
				o_Application.jihuadan_list_dizhiyapin.add(mingxilist[2]
						.get("dizhibianhao").getValues().get(i));
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		manager.getRuning().remove();
		manager.getAbnormal().remove();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			JiHuaDanMingXi_db.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
