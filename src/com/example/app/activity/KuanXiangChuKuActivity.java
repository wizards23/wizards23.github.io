package com.example.app.activity;

import hdjc.rfid.operator.RFID_Device;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;





import com.application.GApplication;
import com.entity.BoxDetail;
import com.example.app.util.Skip;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.imple.getnumber.KuanXiangChuKuUtil;
import com.main.pda.Service_Address;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.biz.BrandNameByCboxNumBiz;
import com.moneyboxadmin.pda.BoxAddStop;
/**
 * 款箱出库页面
 * @author Administrator
 */
public class KuanXiangChuKuActivity extends Activity implements
		OnClickListener, OnItemClickListener {
	private Button chuku, quxiao;//出库  取消
//	private ImageView back;//返回按钮
	private TextView count,//数量
					 wrong,//款箱编号错误提示
					 kxck_title;//款箱出入库标题显示
	private RFID_Device rfid;//扫描工具类
	private ListView listView;
	showListAdapter adapter;
	private ManagerClass manager;//自定义弹框(Dialog)工具类
	private List<BoxDetail> list ;
	private List<BoxDetail> list2 ;
	private int ischeckid;
	KuanXiangChuKuUtil wsg;//款箱扫描工具类
	OnClickListener OnClick;//
	boolean del = true;
	
	private BrandNameByCboxNumBiz brandNameByCboxNumBiz;

	BrandNameByCboxNumBiz getBrandNameByCboxNumBiz() {
		if (brandNameByCboxNumBiz == null) {
			brandNameByCboxNumBiz = new BrandNameByCboxNumBiz();
		}
		return brandNameByCboxNumBiz;
	}
	
	RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);	
			switch (msg.what) {
			case 1:
				list.clear();
				list2.clear();
		//		list = new ArrayList<BoxDetail>();
		//		list2 = new ArrayList<BoxDetail>();
		//		wrong.setText(GApplication.getApplication().wrong);//错误款箱提示显示
				if(GApplication.smlist.size()>0){
					for (int i = 0; i < GApplication.smlist.size(); i++) {
						BoxDetail bd = new BoxDetail();
						bd.setBrand("错误网点");
						bd.setNum(GApplication.smlist.get(i));
						list.add(bd);
					}		
				}
				for (int j = 0; j < GApplication.saolist.size(); j++) {
					for (int i = 0; i < GApplication.mingxi_list.size(); i++) {
						if (GApplication.saolist != null
								&& GApplication.saolist.get(j)
										.equals(GApplication.mingxi_list.get(i)
												.getBoxIds())) {
							BoxDetail bd = new BoxDetail();
							bd.setNum(GApplication.saolist.get(j));
							bd.setBrand(GApplication.mingxi_list.get(i).getNetName());
							list.add(bd);
							list2.add(bd);
						}
					}
				}
				int aa = 0; //标识符
				for (int i = 0; i < list.size(); i++) {
					if(list.get(i).getBrand().equals("错误网点")){
						aa = 1;
						break;
					}else{
						continue;
					}
				}
				listView.setAdapter(adapter);
			//	adapter.notifyDataSetChanged();
				count.setText("" + list.size());
				TestHangGao(listView);
				System.out.println("出入库相等吗:"+list.equals(list2));
				if(aa==0 && list.equals(list2)){
					chuku.setBackgroundResource(R.drawable.buttom_selector_bg);
					chuku.setEnabled(true);
					adapter.notifyDataSetChanged();
				}
				
//				if(aa==0 && GApplication.mingxi_list.size()==list.size()){
//					chuku.setBackgroundResource(R.drawable.buttom_selector_bg);
//					chuku.setEnabled(true);
//					listView.setAdapter(adapter);
//				}
				if(aa==1||GApplication.mingxi_list.size()!=list.size()){
					chuku.setBackgroundResource(R.drawable.gray_btn_bg);
					chuku.setEnabled(false);
					adapter.notifyDataSetChanged();
				}
				System.out.println("数量:"+count.getText().toString());
				System.out.println("数量222:"+GApplication.mingxi_list.size());
				if(!count.getText().toString().equals(GApplication.mingxi_list.size()+"")){
					System.out.println("走进来了没有,,,,...");
					chuku.setBackgroundResource(R.drawable.gray_btn_bg);
					chuku.setEnabled(false);
					adapter.notifyDataSetChanged();
				}
				break;
			default:
				break;
			}			
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kuanxiangchuku);
		wsg = new KuanXiangChuKuUtil();
		adapter = new showListAdapter();
		manager = new ManagerClass();
		wsg.setHandler(handler);
	}

	@Override
	protected void onResume() {
		super.onResume();
		list = new ArrayList<BoxDetail>();
		list2 = new ArrayList<BoxDetail>();
		load();
		count.setText("" + list.size());
		
		getRfid().addNotifly(wsg);
		getRfid().open_a20();
		
		/*listView.setAdapter(adapter);
		TestHangGao(listView);*/
	}

	/**
	 * 初始化控件和设置监听事件
	 */
	public void load() {
		kxck_title = (TextView) this.findViewById(R.id.kxck_title);
	//	wrong = (TextView) this.findViewById(R.id.smWrong);
	//	back = (ImageView) findViewById(R.id.chuku_back);
		listView = (ListView) findViewById(R.id.aaa__listView1);
		chuku = (Button) findViewById(R.id.zaochu_button1);
		quxiao = (Button) findViewById(R.id.zaochu_button2);
		count = (TextView) findViewById(R.id.chuku_count);
	//	back.setOnClickListener(this);
		chuku.setOnClickListener(this);
		quxiao.setOnClickListener(this);
		listView.setOnItemClickListener(this);
		if(GApplication.churuShow==1){
			kxck_title.setText("款箱出库");
			chuku.setText("出库");
		}else if(GApplication.churuShow==2){
			kxck_title.setText("款箱入库");
			chuku.setText("入库");
		}
	}

	/**
	 * 自定义适配器
	 * @author Administrator
	 */
	class showListAdapter extends BaseAdapter {

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
		/*
		 * (non-Javadoc)
		 * @author zhangxuewei,revised at 30,AUG. add new fucntion(删除无用钞箱) 
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(final int arg0, View view, ViewGroup arg2) {
			
			View v = view;
			final ViewHolder holder;
			if (v == null) {
				v = GolbalView.getLF(KuanXiangChuKuActivity.this).inflate(
						R.layout.boxinformation_item, null);
				holder = new ViewHolder();
				holder.brand = (TextView) v.findViewById(R.id.brand);
				holder.Num = (TextView) v.findViewById(R.id.box_num);
				holder.llayout = (LinearLayout) v
						.findViewById(R.id.lboxdodetail_item);

				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			BoxDetail box = (BoxDetail) getItem(arg0);

			// 隔行变色
			if (arg0 % 2 == 0) {
				holder.llayout.setBackgroundColor(Color.parseColor("#dce8ef"));
			} else {
				holder.llayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
			}

			holder.brand.setText(box.getBrand());
			holder.Num.setText(box.getNum());
			
			// 提示触摸事件
			holder.Num.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View view, MotionEvent even) {
					// 按下的时候
					if (MotionEvent.ACTION_DOWN == even.getAction()) {
						view
								.setBackgroundResource(R.color.gray_msg_bg);
					}
					// 手指松开的时候
					if (MotionEvent.ACTION_UP == even.getAction()) {
						view.setBackgroundResource(R.color.transparency);
						manager.getSureCancel().makeSuerCancel(KuanXiangChuKuActivity.this, "确认删除"+((BoxDetail) getItem(arg0)).getNum()+"?",
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										manager.getSureCancel().remove();
										for (int i = 0; i < list.size(); i++) {
											BoxDetail box = (BoxDetail)getItem(arg0);
											System.out.println("aaaaaaaaaaaaaa"+box.getNum());
											System.out.println("aaaaaaaaaaaaaaa"+list.get(i).getNum());
											String num = list.get(i).getNum();
											if (box.getNum().equals(num)) {
												list.remove(i);
												count.setText(list.size() + "");
												GApplication.mingxi_list.remove(num);
												if (del) {
													getRfid().start_a20();
													del = false;
												}
											}
										}
										int aa=0;
										for(int i=0;i<list.size();i++){
											if(list.get(i).getBrand().equals("错误网点")){
												aa = 1;
											}
										}
										if(aa==0 && list.equals(list2)){
											chuku.setBackgroundResource(R.drawable.buttom_selector_bg);
											chuku.setEnabled(true);
											adapter.notifyDataSetChanged();
										}
										if(aa==1||GApplication.mingxi_list.size()!=list.size()){
											chuku.setBackgroundResource(R.drawable.gray_btn_bg);
											chuku.setEnabled(false);
											adapter.notifyDataSetChanged();
										}

									}
								}, false);
					}

					// 意外中断事件取消
					if (MotionEvent.ACTION_CANCEL == even.getAction()) {
						view
								.setBackgroundResource(R.color.transparency);
					}
					return true;
				}
			});

			holder.brand.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View vi, MotionEvent even) {
					// 按下的时候
					if (MotionEvent.ACTION_DOWN == even.getAction()) {
						vi.setBackgroundResource(R.color.gray_msg_bg);
						System.out.println("wwwwwwwwwwwwwwww");
					}

					// 手指松开的时候
					if (MotionEvent.ACTION_UP == even.getAction()) {
						System.out.println("qqqqqqqqqqqqqqqqq");
						vi.setBackgroundResource(R.color.transparency);
						if (GolbalUtil.ismover <= 5
								&& holder.brand.getText().equals("点击获取品牌")) {
							holder.brand.setText("正在获取...");
							// 通过编号获取品牌
							getBrandNameByCboxNumBiz().getBrnadNamebyNum(
									holder.Num.getText().toString(), "");
						}
						GolbalUtil.ismover = 0;
					}
					// 手指移动的时候
					if (MotionEvent.ACTION_MOVE == even.getAction()) {
						GolbalUtil.ismover++;
					}
					// 意外中断事件取消
					if (MotionEvent.ACTION_CANCEL == even.getAction()) {
						vi.setBackgroundResource(R.color.transparency);
						GolbalUtil.ismover = 0;
					}
					return true;
				}
			});

			return v;
		}
	
	}
	
	class ViewHolder {
		TextView brand; // 品牌
		TextView Num; // 编号
		TextView delText; // 删除
		TextView cancelText; // 取消
		LinearLayout llayout;
		LinearLayout menu;
	}
	/*
	 * listView 控件实体类
	 */
	private class KongjianEntity {
		public TextView textView1;
		public TextView textView2;
	}
	
	public void TestHangGao(ListView lv) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int checkId,
			long arg3) {
		/*ischeckid = checkId;
		manager.getAbnormal().timeout(KuanXiangChuKuActivity.this, "是否删除",
				OnClick);*/
	}

	/**
	 * 单击事件
	 */
	public void onClick(View arg0) {
		/*if (arg0 == back) {
			KuanXiangChuKuActivity.this.finish();
		}*/
		if (arg0 == quxiao) {
			GApplication.saolist.clear();
			GApplication.smlist.clear();
			list.clear();
			list2.clear();
			count.setText(""+0);
			GApplication.strBox="";
			adapter.notifyDataSetChanged();
			chuku.setBackgroundResource(R.drawable.gray_btn_bg);
			chuku.setEnabled(false);
		}
		if (arg0 == chuku) {
			manager.getRuning().runding(KuanXiangChuKuActivity.this, "指纹验证开启中...");
			Skip.skip(KuanXiangChuKuActivity.this,
					YayunJiaojieActivity.class, null, 0);
		}
	}
	
	/**
	 * SM
	 * @param a
	 * @param b
	 * @return
	 */
	public static <T> boolean equals(Collection<T> a, Collection<T> b) {
		if (a == null) {
			return false;
		}
		if (b == null) {
			return false;
		}
		if (a.isEmpty() && b.isEmpty()) {
			return true;
		}
		if (a.size() != b.size()) {
			return false;
		}
		List<T> alist = new ArrayList<T>(a);
		List<T> blist = new ArrayList<T>(b);
		Collections.sort(alist, new Comparator<T>() {
			public int compare(T o1, T o2) {
				return o1.hashCode() - o2.hashCode();
			}
		});

		Collections.sort(blist, new Comparator<T>() {
			public int compare(T o1, T o2) {
				return o1.hashCode() - o2.hashCode();
			}
		});

		return alist.equals(blist);
	}
	

	@Override
	protected void onPause() {
		super.onPause();
		manager.getRuning().remove();
		getRfid().close_a20();
	//	GApplication.list_g.clear();
		GApplication.saolist.clear();
		GApplication.smlist.clear();
		list.clear();
		list2.clear();
		GApplication.strBox="";
	//	GApplication.wrong ="";
	//	wrong.setText("");
		chuku.setBackgroundResource(R.drawable.gray_btn_bg);
		chuku.setEnabled(false);		
	}

}
