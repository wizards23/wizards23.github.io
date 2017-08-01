package com.example.app.activity;

import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.GApplication;
import com.example.app.entity.KuanXiangChuRu;
import com.example.app.entity.User;
import com.example.app.util.Skip;
import com.example.pda.R;
import com.manager.classs.pad.ManagerClass;
import com.o.service.KuanXiangZaochuWanruService;
import com.o.service.KuanxiangChuruService;
import com.poka.device.ShareUtil;

/**
 * 款箱　早入晚出
 */
public class KuanxiangChuruActivity extends Activity {

	private ListView listZaochu; // 款箱早出 list
	private ListView listWanru; // 款箱晚入 list
//	private ImageView back; // 退出
	private RelativeLayout layoutZaochu;
	private RelativeLayout layoutWanru;
	private TextView Ztotalwangdian, Ztotalkuanxiang, Wtotalwangdian,
			Wtotalkuanxiang;
	private KuanXiangZaochuWanruService kxzc = new KuanXiangZaochuWanruService();
	private int zswangdiantotal = 0, zskuanxiangtotal = 0;
	private int wrwangdiantotal = 0, wrkuanxiangtotal = 0;
	KuanXiangChuRu zaochu = null;
	KuanXiangChuRu wanru = null;
	private ManagerClass manager;
	List<KuanXiangChuRu> wanrulist = new ArrayList<KuanXiangChuRu>();
	List<KuanXiangChuRu> zaochulist = new ArrayList<KuanXiangChuRu>();
	private ZaochuAdapter zaoAdapter;
	private WanruAdapter wanAdapter;
	private ImageView replace;//刷新
	
	Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_kuanxiangchuru);
		manager = new ManagerClass();
		
		load();
		Listener();
		zaoAdapter = new ZaochuAdapter();
		wanAdapter = new WanruAdapter();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (GApplication.zaosonglist != null) {
			GApplication.zaosonglist.clear();
		}
		
		if (GApplication.wanrulist != null) {
			GApplication.wanrulist.clear();
		}
		if (wanrulist != null) {
			wanrulist.clear();
		}
		if (zaochulist != null) {
			zaochulist.clear();
		}
		zswangdiantotal = 0;
		zskuanxiangtotal = 0;
		wrwangdiantotal = 0;
		wrkuanxiangtotal = 0;
		Ztotalwangdian.setText("0");
		Ztotalkuanxiang.setText("0");
		Wtotalwangdian.setText("0");
		Wtotalkuanxiang.setText("0");
		listZaochu.setAdapter(zaoAdapter);
		zaoAdapter.notifyDataSetChanged();
		listWanru.setAdapter(wanAdapter);
		wanAdapter.notifyDataSetChanged();
		getDate();//获取日期
	//	getZaoChuInfo();

	}

	/*
	 * 控件初始化
	 */
	private void load() {
		replace = (ImageView) findViewById(R.id.churu_shuaxin);
		listZaochu = (ListView) findViewById(R.id.churu_listView1);
		listWanru = (ListView) findViewById(R.id.churu_listView2);
	//	back = (ImageView) findViewById(R.id.churu_back);
		layoutZaochu = (RelativeLayout) findViewById(R.id.zaochu);
		layoutWanru = (RelativeLayout) findViewById(R.id.wanru);
		Ztotalwangdian = (TextView) findViewById(R.id.zaosong_wangdian);
		Ztotalkuanxiang = (TextView) findViewById(R.id.zaosong_kuanxiangcount);
		Wtotalwangdian = (TextView) findViewById(R.id.wanru_wangdian);
		Wtotalkuanxiang = (TextView) findViewById(R.id.wanru_kuanxiangcount);

	}

	public void Listener() {
		ShijianChuli shijianchuli = new ShijianChuli();
		replace.setOnClickListener(shijianchuli);
		layoutZaochu.setOnClickListener(shijianchuli);
		listZaochu.setOnItemClickListener(shijianchuli);
		layoutWanru.setOnClickListener(shijianchuli);
		listWanru.setOnItemClickListener(shijianchuli);
	}

	/*
	 * 早出线路信息读取 (以下为测试数据 真实数据请从服务器获取)
	 */

	/**
	 * 事件处理内部类
	 * @author yuyunheng
	 */
	private class ShijianChuli implements OnClickListener, OnItemClickListener {

		/**
		 * 单机事件
		 */
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.churu_shuaxin:
				wanrulist.clear();
				zaochulist.clear();
				getDate();//获取日期
	//			getZaoChuInfo();
				break;
			case R.id.zaochu:
				listZaochu.setVisibility(View.VISIBLE);
				listWanru.setVisibility(View.GONE);
				break;
			case R.id.wanru:
				listZaochu.setVisibility(View.GONE);
				listWanru.setVisibility(View.VISIBLE);
				break;
			}
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int checkId,
				long arg3) {
			switch (arg0.getId()) {
			case R.id.churu_listView1:
				zaochu = GApplication.zaosonglist.get(checkId);
				GApplication.chukubiaoshi = 1;
				if(wanru!=null){
					wanru=null;
				}
				if(compare_date(GApplication.pcDate,zaochu.getPeisongdate())==true){			
					getMingXi();
				}else{
				//	Toast.makeText(KuanxiangChuruActivity.this, "175行", 2000).show();
					//弹出个提示框标识交接成功  点击其他的地方不让他消失
					dialog = new Dialog(KuanxiangChuruActivity.this);
					LayoutInflater inflater = LayoutInflater.from(KuanxiangChuruActivity.this);
					View v = inflater.inflate(R.layout.dialog_success, null);
					Button but = (Button)v.findViewById(R.id.success);
					but.setText("这个是"+zaochu.getPeisongdate()+"的任务");
					dialog.setCancelable(false);
					dialog.setContentView(v);
					if(but!=null){
						but.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {				
								dialog.dismiss();
							}
						});
					}
					dialog.show();
				}
				break;
			case R.id.churu_listView2:
				wanru = GApplication.wanrulist.get(checkId);
				GApplication.chukubiaoshi = 2;
				if(zaochu!=null){
					zaochu=null;
				}
				getMingXi();
				break;
			}
		}

	}

	/**
	 * 款箱晚入数据配置
	 * @author yuyunheng
	 */
	private class WanruAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return wanrulist.size();
		}

		@Override
		public Object getItem(int arg0) {
			return wanrulist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			KongjianEntity entity;
			if (arg1 == null) {
				entity = new KongjianEntity();
				arg1 = LayoutInflater.from(KuanxiangChuruActivity.this)
						.inflate(R.layout.listview_chuangxiangchuru, null);
				entity.textView1 = (TextView) arg1
						.findViewById(R.id.adapter_kname);
				entity.textView2 = (TextView) arg1
						.findViewById(R.id.adapter_kuanxiang);
				entity.textView3 = (TextView) arg1
						.findViewById(R.id.adapter_wangdian);
				arg1.setTag(entity);
			} else {
				entity = (KongjianEntity) arg1.getTag();
			}
			entity.textView1.setText(wanrulist.get(arg0)
					.getChaochexianlu());
			entity.textView2.setText(wanrulist.get(arg0)
					.getKuanxiangcount() + "");
			entity.textView3.setText(wanrulist.get(arg0).getWangdiancount()+ "");
			return arg1;
		}

	}

	/**
	 * ZaochuAdapter 款箱早出数据配置
	 */
	private class ZaochuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return zaochulist.size();
		}

		@Override
		public Object getItem(int arg0) {
			return zaochulist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			KongjianEntity entity;
			if (arg1 == null) {
				entity = new KongjianEntity();
				arg1 = LayoutInflater.from(KuanxiangChuruActivity.this)
						.inflate(R.layout.listview_chuangxiangchuru, null);
				entity.textView1 = (TextView) arg1
						.findViewById(R.id.adapter_kname);
				entity.textView2 = (TextView) arg1
						.findViewById(R.id.adapter_kuanxiang);
				entity.textView3 = (TextView) arg1
						.findViewById(R.id.adapter_wangdian);
				arg1.setTag(entity);
			} else {
				entity = (KongjianEntity) arg1.getTag();
			}
			entity.textView1.setText(zaochulist.get(arg0).getChaochexianlu());
			entity.textView2.setText(zaochulist.get(arg0).getKuanxiangcount()+ "");
			entity.textView3.setText(zaochulist.get(arg0).getWangdiancount()+ "");
			return arg1;
		}
	}

	/*
	 * listView 控件实体类
	 */
	private class KongjianEntity {
		public TextView textView1;
		public TextView textView2;
		public TextView textView3;
	}
	/**
	 * 获取款箱早送出库明细
	 */
	public void getMingXi() {
		manager.getRuning().runding(KuanxiangChuruActivity.this, "数据获取中...");
		new Thread() {
			@Override
			public void run() {
				super.run();
				KuanxiangChuruService ks = new KuanxiangChuruService();
				try {//获取款箱明细接口 
					if (zaochu != null && wanru == null) {
						GApplication.mingxi_list = ks.getBoxSendOutEarlyDetail(
								zaochu.getXianlubianhao(),
								zaochu.getPeisongdate());
						if(GApplication.mingxi_list!=null){
							handler.sendEmptyMessage(6);
						}else{
							handler.sendEmptyMessage(8);
						}
					} else if (zaochu == null && wanru != null) {
						GApplication.mingxi_list = ks
								.getBoxStorageLateDetail(wanru
										.getXianlubianhao());
						if(GApplication.mingxi_list!=null){
							handler.sendEmptyMessage(6);
						}else{
							handler.sendEmptyMessage(8);
						}
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(7);
				} catch (NullPointerException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(8);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(9);
				}
			}

		}.start();
	}
	/**
	 * 获取任务日期
	 */
	public void getDate(){
		manager.getRuning().runding(KuanxiangChuruActivity.this, "日期获取中...");
		new Thread() {
			@Override
			public void run() {
				super.run();
				KuanxiangChuruService ks = new KuanxiangChuruService();
				try {//日期系统接口
					 
					GApplication.pcDate=ks.getSysTime();
					if(null!=GApplication.pcDate){
						handler.sendEmptyMessage(13);
					}
				
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(10);
				} catch (NullPointerException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(11);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(12);
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
				System.out.println("---------------0");
				manager.getRuning().remove();
				/*manager.getAbnormal().timeout(KuanxiangChuruActivity.this,
						"晚收入库未获取到任务,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getWanRuInfo();
								return;
							}
						});*/
				updateLitView();
				break;
			case 1:		
				zaochulist.clear();//刷新时起作用
				zaochulist.addAll(GApplication.zaosonglist);
				getWanRuInfo();			
				break;
			case 2:
				System.out.println("---------------2");
				manager.getRuning().remove();
				manager.getAbnormal().timeout(KuanxiangChuruActivity.this,
						"数据获取超时,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getZaoChuInfo();
								return;
							}
						});
				updateLitView();
				break;
			case 3:
				System.out.println("---------------3");
				manager.getRuning().remove();
				manager.getAbnormal().timeout(KuanxiangChuruActivity.this,
						"加载异常,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getZaoChuInfo();
								return;
							}
						});
				updateLitView();
				break;
			case 4:
				System.out.println("---------------4");
				manager.getRuning().remove();
				wanrulist.clear();//刷新时起作用
				wanrulist.addAll(GApplication.wanrulist);
				updateLitView();
				break;
			case 5:
				System.out.println("---------------5");
				manager.getRuning().remove();
				/*manager.getAbnormal().timeout(KuanxiangChuruActivity.this,
						"早送出库未能查到任务,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getZaoChuInfo();
								return;
							}
						});*/
				getWanRuInfo();
				updateLitView();
				break;
			case 6:
				manager.getRuning().remove();
				System.out.println("---------------6");
				Bundle bundle = new Bundle();
				
				if (zaochu != null && wanru == null) {
					bundle.putSerializable("zaochu", zaochu);
					GApplication.churuShow=1;//早送列表点击后
					Skip.skip(KuanxiangChuruActivity.this,
							KuanXiangZaochuMingxiActivity.class, bundle, 0);
				} else if (zaochu == null && wanru != null) {
					bundle.putSerializable("wanru", wanru);
					GApplication.churuShow=2;//晚入列表点击后
					Skip.skip(KuanxiangChuruActivity.this,
							KuanXiangZaochuMingxiActivity.class, bundle, 0);
				}
				break;
			case 7:
				System.out.println("---------------7");
				manager.getRuning().remove();
				manager.getAbnormal().timeout(KuanxiangChuruActivity.this,
						"加载超时,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getMingXi();
								return;
							}
						});
				updateLitView();
				break;
			case 8:
				System.out.println("---------------8");
				manager.getRuning().remove();
				manager.getAbnormal().timeout(KuanxiangChuruActivity.this,
						"未获取到任务明细,重试?", new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getMingXi();
								return;
							}
						});
			
				updateLitView();
				break;
			case 9:
				System.out.println("---------------9");
				manager.getRuning().remove();
				manager.getAbnormal().timeout(KuanxiangChuruActivity.this,
						"加载异常,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getMingXi();
								return;
							}
						});
				updateLitView();
				break;
			case 10:
				System.out.println("---------------10");
				manager.getRuning().remove();
				manager.getAbnormal().timeout(KuanxiangChuruActivity.this,
						"加载超时,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getDate();
								return;
							}
						});
				break;
			case 11:
				System.out.println("---------------11");
				manager.getRuning().remove();
				break;
			case 12:
				System.out.println("---------------12");
				manager.getRuning().remove();
				manager.getAbnormal().timeout(KuanxiangChuruActivity.this,
						"加载异常,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								getDate();
								return;
							}
						});
				break;
			case 13:
				manager.getRuning().remove();
				getZaoChuInfo();
				break;
			}
		}

	};

	public void getZaoChuInfo() {
		System.out.println("------------------2");
		manager.getRuning().runding(KuanxiangChuruActivity.this, "数据加载中...");
		new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					
					GApplication.zaosonglist = kxzc.KuanXiangZaochu(GApplication.loginJidouId);
					
					System.out.println("------------------3");
					if (GApplication.zaosonglist != null) {
						handler.sendEmptyMessage(1);
					} else {
						handler.sendEmptyMessage(5);
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(2);
				} catch (NullPointerException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(5);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void getWanRuInfo() {
		new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					
					GApplication.wanrulist = kxzc
							.KuanXiangWanru(GApplication.loginJidouId);
					if (GApplication.wanrulist != null) {
						handler.sendEmptyMessage(4);
					} else {
						System.out.println("---akak--==");
						handler.sendEmptyMessage(0);
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(2);
				} catch (NullPointerException e) {
					e.printStackTrace();
					System.out.println("---akbbbbbBBBBak--==");
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(3);
				}
			}

		}.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		manager.getAbnormal().remove();
		manager.getRuning().remove();
	}

	public void updateLitView() {
		if(zswangdiantotal!=0){
			zswangdiantotal=0;
		}
		if(zskuanxiangtotal!=0){
			zskuanxiangtotal=0;
		}
		if(wrwangdiantotal!=0){
			wrwangdiantotal=0;
		}
		if(wrkuanxiangtotal!=0){
			wrkuanxiangtotal=0;
		}
		if (GApplication.zaosonglist != null) {
			listZaochu.setAdapter(zaoAdapter);
			zaoAdapter.notifyDataSetChanged();
			for (int i = 0; i < GApplication.zaosonglist.size(); i++) {
				zswangdiantotal +=Integer.parseInt(GApplication.zaosonglist.get(i).getWangdiancount());
				zskuanxiangtotal += Integer.parseInt(GApplication.zaosonglist.get(i).getKuanxiangcount());
			}
			Ztotalwangdian.setText("" + zswangdiantotal);
			Ztotalkuanxiang.setText("" + zskuanxiangtotal);
		}
		if (GApplication.wanrulist != null) {
			listWanru.setAdapter(wanAdapter);
			wanAdapter.notifyDataSetChanged();
			for (int i = 0; i < GApplication.wanrulist.size(); i++) {
				wrwangdiantotal += Integer.parseInt(GApplication.wanrulist.get(i).getWangdiancount());
				wrkuanxiangtotal += Integer.parseInt(GApplication.wanrulist.get(i).getKuanxiangcount());
			}
			Wtotalwangdian.setText("" + wrwangdiantotal);
			Wtotalkuanxiang.setText("" + wrkuanxiangtotal);
		}
	}
	/**
	 * 比较2个日期的大小
	 * @param date1
	 * @param date2
	 * @return
	 */
	public boolean compare_date(String date1,String date2){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt1 = df.parse(date1);
			Date dt2 = df.parse(date2);
			if (dt1.getTime() >= dt2.getTime()) {
                System.out.println("dt1 大于dt2");
                return true;
            } else{
                System.out.println("dt1小于dt2");
                return false;
            }
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return true;
	} 
	
	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			//按返回键情况指纹帐号密码信息
			System.out.println("564行代码,返回键提示的信息:");
			if (GApplication.kuguan1 != null) {
				GApplication.kuguan1 = null;
			}
			if (GApplication.kuguan2 != null) {
				GApplication.kuguan2 = null;
			}
			if(ShareUtil.zhiwenid_left!=null){
				ShareUtil.zhiwenid_left=null;
			}
			if(ShareUtil.zhiwenid_right!=null){
				ShareUtil.zhiwenid_right=null;
			}		
			Skip.skip(KuanxiangChuruActivity.this,KuanxiangCaidanActivity.class, null, 0);
			KuanxiangChuruActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
