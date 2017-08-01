package com.example.app.activity;

import hdjc.rfid.operator.RFID_Device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.GApplication;
import com.entity.BoxDetail;
import com.example.app.entity.S_box;
import com.example.app.util.Skip;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.imple.getnumber.WanShouGetNumber;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.pda.SupercargoJoin;
/**
 * 晚收款箱交接 页面
 * @author Administrator
 */
public class WanShouXiangActivity extends Activity implements OnClickListener{
	private Button shouxiang,chongsao;// 确定    重扫
	private TextView oxianlu, kuanxiangNum, ws_count,
					 title_jjName;//页面标题名称
//	private ImageView back;
	int count =0;//数量显示问题
	private ListView listView;
//	private List<String> ls;// 传送过来的箱子集合
	public boolean Flag;
	private RFID_Device rfid;
	ShowListView adapter;
	private ManagerClass manager;
	private int checkId;
	OnClickListener OnClick;
	int cscount = 0;//重扫
	
	int cscount1 = 0;//重扫
	int cscount2 = 0;//重扫
	boolean del = true;
	WanShouGetNumber wsn;

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
				int flag = 0;//标识  
				
				for (int i = 0; i < GApplication.linshilist.size(); i++) {
					if(GApplication.linshilist.get(i).getBrand().equals("错误")){
						flag = 1;				
						break;
					}else{		
						continue;
					}
				}
												
				ws_count.setText("" + GApplication.linshilist.size());
				listView.setAdapter(adapter);
				//TestHangGao(listView);
				
				if(flag==1 && GApplication.zaolist.size()!=GApplication.linshilist.size()){
					shouxiang.setBackgroundResource(R.drawable.gray_btn_bg);
					shouxiang.setEnabled(false);
					adapter.notifyDataSetChanged();
				}
				
				if(flag==0 && GApplication.zaolist.size()==GApplication.linshilist.size()){
					shouxiang.setBackgroundResource(R.drawable.buttom_selector_bg);
					shouxiang.setEnabled(true);
					//默认重扫,防止显示错误
					if(cscount==0){
						cscount++;
						ws_count.setText("" + 0);
						GApplication.linshilist.clear();
						listView.setAdapter(adapter);
						GApplication.getApplication().strBox = "";
						shouxiang.setBackgroundResource(R.drawable.gray_btn_bg);
						shouxiang.setEnabled(false);
						System.out.println("我重扫了-cscount:" + cscount);
					}
					adapter.notifyDataSetChanged();
				}
				if(flag==1 || GApplication.zaolist.size()!=GApplication.linshilist.size()){
					shouxiang.setBackgroundResource(R.drawable.gray_btn_bg);
					shouxiang.setEnabled(false);
					adapter.notifyDataSetChanged();
				}
				/**
				 * 进入早送申请页面的扫面显示
				 */
				if(flag==1 && GApplication.zaolist.size()!=GApplication.linshilist.size() && GApplication.jiaojiestate==3){
					shouxiang.setBackgroundResource(R.drawable.gray_btn_bg);
					shouxiang.setEnabled(false);
				//	adapter.notifyDataSetChanged();
				}
				
				if(flag==0 && GApplication.jiaojiestate==3 && GApplication.zyzaolist.size()==GApplication.linshilist.size()){
					shouxiang.setBackgroundResource(R.drawable.buttom_selector_bg);
					shouxiang.setEnabled(true);
					if(cscount1==0){
						cscount1++;
						ws_count.setText("" + 0);
						GApplication.linshilist.clear();
						listView.setAdapter(adapter);
						GApplication.getApplication().strBox = "";
						shouxiang.setBackgroundResource(R.drawable.gray_btn_bg);
						shouxiang.setEnabled(false);
						System.out.println("我重扫了-cscount1:" + cscount1);
					}
					adapter.notifyDataSetChanged();
				}else if(flag==1 || GApplication.zaolist.size()!=GApplication.linshilist.size() && GApplication.jiaojiestate==3){
					shouxiang.setBackgroundResource(R.drawable.gray_btn_bg);
					shouxiang.setEnabled(false);
					adapter.notifyDataSetChanged();
				}
				System.out.println("数量3:"+ws_count.getText().toString());
				System.out.println("数量4:"+kuanxiangNum.getText().toString());
				if(!ws_count.getText().toString().equals(kuanxiangNum.getText().toString())){
					System.out.println("zou了吗?");
					shouxiang.setBackgroundResource(R.drawable.gray_btn_bg);
					shouxiang.setEnabled(false);
					adapter.notifyDataSetChanged();
				}
				int bbb = Integer.parseInt(ws_count.getText().toString()); //
				int ccc = Integer.parseInt(kuanxiangNum.getText().toString());
				if(bbb>ccc){//重扫,防止出错
					if(cscount2==0){
						cscount2++;
						ws_count.setText("" + 0);
						GApplication.linshilist.clear();
						listView.setAdapter(adapter);
						GApplication.getApplication().strBox = "";
						shouxiang.setBackgroundResource(R.drawable.gray_btn_bg);
						shouxiang.setEnabled(false);
						System.out.println("我重扫了-cscount2:" + cscount2);
					}
				}
							
				break;
			case 0:			
				break;
			}			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wanshouxiang);
		manager = new ManagerClass();
		adapter = new ShowListView();
		wsn = new WanShouGetNumber();
		wsn.setHandler(handler);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		load();
		
		getRfid().addNotifly(wsn);
		getRfid().open_a20();		
		ws_count.setText("" + 0);
		if(GApplication.jiaojiestate==1){
			title_jjName.setText("早送款箱交接");
		}
			
		/*listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		TestHangGao(listView);*/			
	}

	public void load() {
		title_jjName = (TextView) this.findViewById(R.id.title_jjName);
//		wrong = (TextView) this.findViewById(R.id.textWrong);
		oxianlu = (TextView) this.findViewById(R.id.oxianlu);
		shouxiang = (Button) findViewById(R.id.wanshou_bt);
		chongsao = (Button) findViewById(R.id.wanshou_cs);
		listView = (ListView) this.findViewById(R.id.bianhaolistView);
		ws_count = (TextView) findViewById(R.id.wanshou_count);
	//	back = (ImageView) findViewById(R.id.wanshou_back);
		kuanxiangNum = (TextView) this.findViewById(R.id.kuanxiang_num);
//		back.setOnClickListener(this);
		shouxiang.setOnClickListener(this);
		chongsao.setOnClickListener(this);
		// 配送线路
		oxianlu.setText(GApplication.kxc.getChaochexianlu());
		if (GApplication.zaolist != null && GApplication.jiaojiestate==1) {//早送交接跳转晚收款箱页面
		//	ls = GApplication.zaolist;
			count = GApplication.zaolist.size();
			kuanxiangNum.setText(count + "");	
		}
		if(GApplication.zaolist != null && GApplication.jiaojiestate==2){//晚收交接跳转晚收款箱页面  没有调早送申请页面的
		//	ls = GApplication.zaolist;
			count = GApplication.zaolist.size();
			kuanxiangNum.setText(count + "");	
		}else if(GApplication.zyzaolist != null && GApplication.jiaojiestate==3){//早送声请的在用状态
			kuanxiangNum.setText(GApplication.zyzaolist.size() + "");
		}
		
	}


	class ShowListView extends BaseAdapter {
		LayoutInflater lf = LayoutInflater.from(WanShouXiangActivity.this);
		KongjianEntity holder;
		
		@Override
		public int getCount() {
			return GApplication.linshilist.size();
		}

		@Override
		public Object getItem(int arg0) {
			return GApplication.linshilist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			final int index=arg0; 
			View v=arg1;
			if (v == null) {
				v = lf.inflate(R.layout.item_wanshou, null);
				holder = new KongjianEntity();
				holder.bianhao = (TextView) v.findViewById(R.id.item_Bianhao);
				holder.llayout = (LinearLayout) v
						.findViewById(R.id.lboxdodetail_itemcopy);
				v.setTag(holder);
			} else {
				holder = (KongjianEntity) v.getTag();
			}
			BoxDetail box = (BoxDetail) getItem(arg0);
			if (GApplication.getApplication().linshilist.get(arg0).getBrand().equals("正确")) {
				System.out.println("我是正确的:"+GApplication.getApplication().linshilist.get(arg0).getNum());
				holder.bianhao.setTextColor(android.graphics.Color.BLACK);
			}else{
				holder.bianhao.setTextColor(android.graphics.Color.RED);
			}		
			holder.bianhao.setText(box.getNum());
			// 触摸事件
						holder.llayout.setOnTouchListener(new OnTouchListener() {
							@Override
							public boolean onTouch(View view, MotionEvent even) {
								// 按下的时候
								if (MotionEvent.ACTION_DOWN == even.getAction()) {
									view.setBackgroundResource(R.color.gray_msg_bg);
								}
								// 手指松开的时候
								if (MotionEvent.ACTION_UP == even.getAction()) {
									view.setBackgroundResource(R.color.white);
									manager.getSureCancel().makeSuerCancel(
											WanShouXiangActivity.this, "确认删除"+((BoxDetail) getItem(arg0)).getNum()+"?",
											new View.OnClickListener() {
												@Override
												public void onClick(View view) {
													manager.getSureCancel().remove();
													int aa=0;
													for (int i = 0; i < GApplication.linshilist.size(); i++) {
														BoxDetail box = (BoxDetail) getItem(arg0);
														String num = GApplication.linshilist.get(i).getNum();
														System.out
																.println("num"+num);
														System.out
																.println("box"+box.getNum());
														if (box.getNum().equals(num)) {
															System.out
																	.println("删掉无用钞箱"+GApplication.linshilist.get(i).getNum());
															GApplication.linshilist.remove(i);
															ws_count.setText(GApplication.linshilist.size()+"");
															if (del) {
																getRfid().start_a20();
																del = false;
															}
															break;
														};
													};
													/*
													 * 查询是否有红色箱子
													 */
													for(int i=0;i<GApplication.linshilist.size(); i++){
														if(!GApplication.getApplication().linshilist.get(i).getBrand().equals("正确")){
															aa=1;
														} 
													};
													shouxiang.setBackgroundResource(R.drawable.gray_btn_bg);
													shouxiang.setEnabled(false);
													adapter.notifyDataSetChanged();
													System.out
															.println("1"+aa);
													System.out
															.println("111");
													System.out
															.println("zyzaolist"+GApplication.zyzaolist.size());
													System.out
													.println("zaolist"+GApplication.zaolist.size());
													System.out
															.println(GApplication.linshilist.size());
													if(aa==0 && GApplication.zyzaolist.size()==GApplication.linshilist.size()){
														System.out
														.println("2"+aa);
														shouxiang.setBackgroundResource(R.drawable.buttom_selector_bg);
														shouxiang.setEnabled(true);
														adapter.notifyDataSetChanged();
													};
													if(aa==0 && GApplication.zaolist.size()==GApplication.linshilist.size()){
														System.out
														.println("2"+aa);
														shouxiang.setBackgroundResource(R.drawable.buttom_selector_bg);
														shouxiang.setEnabled(true);
														adapter.notifyDataSetChanged();
													};
													if(GApplication.linshilist.size()==0 ){
														System.out
																.println("3");
														shouxiang.setBackgroundResource(R.drawable.gray_btn_bg);
														shouxiang.setEnabled(false);
														adapter.notifyDataSetChanged();
													};
												}
											}, false);
								}
								// 意外中断事件取消
								if (MotionEvent.ACTION_CANCEL == even.getAction()) {
									view.setBackgroundResource(R.color.white);
								}
								return true;
							}
						});
			return v;
		}
	}

	public class KongjianEntity {
		TextView bianhao;
		Button btndel;
		LinearLayout llayout;
		TextView delText; // 删除
		TextView cancelText; // 取消
		LinearLayout menu;
	}

	public void TestHangGao(ListView lv) {
		ListAdapter adapter = lv.getAdapter();
		int hangshu = adapter.getCount();
		int zonggaodu = 0;
		for (int i = 0; i < hangshu; i++) {
			View view = adapter.getView(i, null, lv);
			view.measure(View.MeasureSpec.UNSPECIFIED,
					View.MeasureSpec.UNSPECIFIED);
			zonggaodu += view.getMeasuredHeight();
		}
		double fengefu = (lv.getDividerHeight()) * (hangshu - 1);
		zonggaodu += fengefu;
		LayoutParams lp = lv.getLayoutParams();
		lp.height = zonggaodu;
		lv.setLayoutParams(lp);
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
		count=0;
		getRfid().close_a20();
		GApplication.linshilist.clear();
	//	ls.clear();
	//	GApplication.getApplication().wrong="";
		GApplication.getApplication().strBox="";
		shouxiang.setBackgroundResource(R.drawable.gray_btn_bg);
		shouxiang.setEnabled(false);
		chongsao.setBackgroundResource(R.drawable.buttom_selector_bg);
		chongsao.setEnabled(false);
	}

	@Override
	protected void onStop() {
		super.onStop();
		manager.getRuning().remove();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			WanShouXiangActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		/*case R.id.wanshou_back:
			WanShouXiangActivity.this.finish();
			break;*/
		case R.id.wanshou_bt:
			manager.getRuning().runding(WanShouXiangActivity.this,"正在启动指纹扫描,请稍后...");
			Skip.skip(WanShouXiangActivity.this,KuanXiangJiaoJieActivity.class, null, 0);
			break;
		case R.id.wanshou_cs:
			ws_count.setText("" + 0);
			GApplication.linshilist.clear();
	//		ls.clear();
			adapter.notifyDataSetChanged();
			GApplication.getApplication().strBox="";
			shouxiang.setBackgroundResource(R.drawable.gray_btn_bg);
			shouxiang.setEnabled(false);
			break;
		}
	}
}
