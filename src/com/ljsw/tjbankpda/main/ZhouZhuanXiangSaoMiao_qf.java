package com.ljsw.tjbankpda.main;

import com.example.pda.R;

import hdjc.rfid.operator.RFID_Device;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.ljsw.tjbankpda.qf.application.Mapplication;
import com.ljsw.tjbankpda.qf.entity.PeiSongDan;
import com.ljsw.tjbankpda.qf.service.QingfenRenwuService;
import com.ljsw.tjbankpda.util.MyListView;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.ljsw.tjbankpda.yy.application.S_application;
import com.ljsw.tjbankpda.yy.application.ZzxSaomiaoUtil;
import com.manager.classs.pad.ManagerClass;

/**
 * 周转箱扫描,并提交数据到服务器
 * 
 * @author FUHAIQING
 */
public class ZhouZhuanXiangSaoMiao_qf extends FragmentActivity implements
		OnClickListener {
	private ImageView back;
	private TextView bianhao, peisongdan, wangdian, count;
	private TextView queding;// 确定
	private Button hedui;
	private ListView listleft;
	private List<String> leftlist = new ArrayList<String>();// 配送单下属订单号,
	private LeftAdapter ladapter;
	private String zhouzhuanxiangId;// 订单编号
	private String peisongId;// 配送单号
	private String peisongWangdan;// 配送网点
	private int peisongBoxCount;// 配送周转箱数量
	private String peisongBoxInfo;// 配送单周转箱信息
	private ZzxSaomiaoUtil zsm;
	private SaomiaoThread stm;
	boolean flag = true;
	boolean saomiaoFlag = true;
	private Bundle bun_box;
	private String orderNum;
	private Bundle bundle;

	String box;// 扫到的周转箱编号

	private RFID_Device rfid;

	private boolean yiqing = false; // 判断扫描到的配送单是否已清
//	private List<PeiSongDan> ylistData; // 已清配送单集合

	private RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}

	private ManagerClass manager;// 弹出框
	private Handler okHandle = new Handler() {// 数据获取成功handler
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				leftlist = (List<String>) msg.obj;
				bianhao.setText(zhouzhuanxiangId);
				/*
				 * 检测配送单是否已被清分
				 */
//				if(ylistData!=null){
//					for (int i=0; i<ylistData.size();i++) {
//						if(peisongId.equals(peisongdan)){
//							yiqing=true;
//						}
//					}
//				}
				
				
				wangdian.setText(peisongWangdan);
				count.setText(peisongBoxCount + "");
				// 绑定Adapter
				listleft.setAdapter(ladapter);
				
				//如果是已经配送单 红字提醒
//				if(yiqing){
//					peisongdan.setText(peisongId+"(已清)");
//					peisongdan.setTextColor(Color.RED);
//					
//					hedui.setEnabled(false);
//					hedui.setBackgroundResource(R.drawable.gray_btn_bg);
//				}else{
					peisongdan.setText(peisongId);
					peisongdan.setTextColor(Color.BLACK);
					
					hedui.setEnabled(true);
					hedui.setBackgroundResource(R.drawable.buttom_selector_bg);
				//}
				// new TurnListviewHeight(listleft);
				hedui.setText("周转箱核对");
				
				manager.getRuning().remove();
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(10000);// 睡眠10秒让用户有时间进行下一步操作
							okHandle.sendEmptyMessage(2);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				break;

			case 2:
				getRfid().open_a20();
				break;
			}
		};
	};
	private Handler timeoutHandle = new Handler() {// 连接超时handler
		public void handleMessage(Message msg) {
			manager.getRuning().remove();
			saomiaoFlag = true;
			if (msg.what == 0) {
				manager.getAbnormal().timeout(ZhouZhuanXiangSaoMiao_qf.this,
						"数据连接超时", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								manager.getRuning().runding(
										ZhouZhuanXiangSaoMiao_qf.this,
										"数据加载中...");
								stm = new SaomiaoThread();
								stm.start();
								saomiaoFlag = false;
							}
						});
			}
			if (msg.what == 1) {
				manager.getAbnormal().timeout(ZhouZhuanXiangSaoMiao_qf.this,
						"网络连接失败", new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								manager.getRuning().runding(
										ZhouZhuanXiangSaoMiao_qf.this,
										"数据加载中...");
								stm = new SaomiaoThread();
								stm.start();
								saomiaoFlag = false;
							}
						});
			}
			if (flag) {
				getRfid().open_a20();
			}
		};
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			box = msg.getData().getString("box");
			System.out.println("周转箱编号为=" + box);
			if (Mapplication.getApplication().ltQflkBoxNum.contains(box)) {
				if (null != box) {
					getRfid().close_a20();
					System.out.println("关闭了...");
					System.out.println("//////////////"
							+ S_application.getApplication().bianhao);
					queding.setEnabled(true);
					queding.setBackgroundResource(R.drawable.buttom_selector_bg);
					bianhao.setText(box);
					peisongdan.setText("");
					wangdian.setText("");
					count.setText("");
					hedui.setText("周转箱核对");
				}
			} else {
				bianhao.setText(box);
				System.out.println("不关闭...");
				peisongdan.setText("");
				wangdian.setText("");
				count.setText("");
				leftlist.clear();
				ladapter.notifyDataSetChanged();
				queding.setEnabled(false);
				queding.setBackgroundResource(R.drawable.button_gray);
				if (queding.isEnabled() == false) {
					getRfid().open_a20();
				}
				hedui.setEnabled(false);
				hedui.setBackgroundResource(R.drawable.button_gray);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhouzhuanxiangxinxisaomiao);
		manager = new ManagerClass();
		ladapter = new LeftAdapter();
		zsm = new ZzxSaomiaoUtil();
		zsm.setHandler(handler);
		orderNum = super.getIntent().getExtras().getString("orderNum");
//		ylistData = (List<PeiSongDan>) getIntent().getExtras().getSerializable(
//				"list");
		load();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getRfid().addNotifly(zsm);
		getRfid().open_a20();
	}

	@Override
	protected void onPause() {
		super.onPause();

		getRfid().close_a20();
		S_application.getApplication().bianhao = "";
	}

	class SaomiaoThread extends Thread {
		@Override
		public void run() {
			super.run();
			String params;
			try {
				zhouzhuanxiangId = S_application.getApplication().bianhao;
				params = new QingfenRenwuService().getParams(zhouzhuanxiangId,
						"getZhouzhuanxiangXinxi");
				Table[] RenwuData = Table.doParse(params);
				peisongId = RenwuData[0].get("peisongdan").get(0);
				peisongWangdan = RenwuData[0].get("wangdian").get(0);
				List<String> ltBox = RenwuData[0].get("zhouzhuanxiang")
						.getValues();
				peisongBoxCount = ltBox.size();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < ltBox.size(); i++) {
					sb.append(ltBox.get(i) + ",");
				}
				peisongBoxInfo = sb + "";
				List<String> ltw = RenwuData[0].get("dingdan").getValues();
				Message msg = new Message();
				msg.what = 1;
				msg.obj = ltw;
				okHandle.sendMessage(msg);
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				timeoutHandle.sendEmptyMessage(0);
			} catch (NullPointerException e) {
				e.printStackTrace();
				timeoutHandle.sendEmptyMessage(2);
			} catch (Exception e) {
				e.printStackTrace();
				timeoutHandle.sendEmptyMessage(1);
			} finally {
				// S_application.getApplication().bianhao="";
			}
		}
	}

	public void load() {
		queding = (TextView) this.findViewById(R.id.qf_zhouzhuan_queding);
		back = (ImageView) findViewById(R.id.qf_zhouzhuan_saomiao_back);
		back.setOnClickListener(this);
		bianhao = (TextView) findViewById(R.id.qf_zhouzhuan_saomiao_bianhao);
		peisongdan = (TextView) findViewById(R.id.qf_zhouzhuan_saomiao_peisongdan);
		wangdian = (TextView) findViewById(R.id.qf_zhouzhuan_saomiao_wangdian);
		count = (TextView) findViewById(R.id.qf_zhouzhuan_saomiao_count);
		hedui = (Button) findViewById(R.id.qf_zhouzhuan_saomiao_hedui);
		hedui.setOnClickListener(this);
		queding.setOnClickListener(this);
		listleft = (ListView) findViewById(R.id.qf_zhouzhuan_saomiao_list_left);
		hedui.setEnabled(false);
		hedui.setBackgroundResource(R.drawable.button_gray);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.qf_zhouzhuan_saomiao_back:
			ZhouZhuanXiangSaoMiao_qf.this.finish();
			break;
		case R.id.qf_zhouzhuan_saomiao_hedui:
			Bundle bundle = new Bundle();
			bundle.putString("peisongBoxInfo", peisongBoxInfo);
			bundle.putString("nowBoxNo", zhouzhuanxiangId);
			bundle.putString("peisongId", peisongId);
			bundle.putString("orderNum", orderNum);
			Skip.skip(this, ZhouZhuanXiangHeDui_qf.class, bundle, 0);
			break;
		case R.id.qf_zhouzhuan_queding:
			manager.getRuning().runding(ZhouZhuanXiangSaoMiao_qf.this,
					"加载信息中...");
			queding.setEnabled(false);
			queding.setBackgroundResource(R.drawable.gray_btn_bg);
			stm = new SaomiaoThread();
			stm.start();
			break;
		default:
			break;
		}
	}

	class LeftAdapter extends BaseAdapter {
		LeftHolder lh;
		LayoutInflater lf = LayoutInflater.from(ZhouZhuanXiangSaoMiao_qf.this);

		@Override
		public int getCount() {
			return leftlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			return leftlist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				lh = new LeftHolder();
				arg1 = lf.inflate(R.layout.adapter_dizhisaomiao_left, null);
				lh.tv = (TextView) arg1
						.findViewById(R.id.adapter_dizhisaomiao_left_text);
				arg1.setTag(lh);
			} else {
				lh = (LeftHolder) arg1.getTag();
			}
			lh.tv.setText(leftlist.get(arg0));
			return arg1;
		}

	}

	public static class LeftHolder {
		TextView tv;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			ZhouZhuanXiangSaoMiao_qf.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
