package com.ljsw.tjbankpda.main;
import com.example.pda.R;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import com.ljsw.tjbankpda.qf.application.Mapplication;
import com.ljsw.tjbankpda.qf.entity.QingLingRuKu;
import com.ljsw.tjbankpda.qf.entity.QingfenRemwu;
import com.ljsw.tjbankpda.qf.service.QingfenRenwuService;
import com.ljsw.tjbankpda.util.MessageDialog;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.ljsw.tjbankpda.util.WebServiceFromThree;
import com.ljsw.tjbankpda.util.onTouthImageStyle;
import com.ljsw.tjbankpda.yy.activity.QingfenGlyRwActivity;
import com.manager.classs.pad.ManagerClass;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 清分员任务列表界面
 * @time 2015-06-18  15:16
 * @author FUHAIQING
 */
public class QingfenRenwuActivity extends FragmentActivity implements OnTouchListener{
	/* 定义控件 */
	private RelativeLayout rlTitleQingling;//请领装箱标题
	private LinearLayout llQingling;//请领装箱详情
	private ListView lvQingling;//请领装箱列表
	private RelativeLayout rlTitleShangjiao;//上缴清分标题
	private LinearLayout llShangjiao;//上缴清分详情
	private ListView lvShangjiao;//上缴清分列表
	private Button btnZhuangxiang;//装箱领款按钮
	private Button btnShangjiao;//清分领取按钮
	private TextView tvQLNo;//清分领取任务单号
	private TextView tvSJNo;//上缴清分任务单号
	private ImageView ivBack;//返回按钮
	private ImageView ivRefresh;//刷新按钮
	private BaseAdapter qlAdapter;  //请领适配器
	private BaseAdapter sjAdapter;  //上缴适配器

	/* 定义全局变量 */
	private Table[] RenwuData;
	List<QingfenRemwu> ltQLRenwu=new ArrayList<QingfenRemwu>(); //请领任务列表
	List<QingfenRemwu> ltSJRenwu=new ArrayList<QingfenRemwu>(); //上缴任务列表
	private String qlNum;//请领任务单号
	private String sjNum;//上缴任务单号 
	private int move;
	private ManagerClass manager;//弹出框
	private Handler okHandle=new Handler(){//数据获取成功handler
		public void handleMessage(Message msg) {
			if(ltSJRenwu.size()>0){
				ltSJRenwu.clear();
			}
			if(ltQLRenwu.size()>0){
				ltQLRenwu.clear();
			}
			System.out.println("解析字符串RenwuData:"+RenwuData.toString());
			//获取请领装箱数据
			if(RenwuData[1].get("renwudan").getValues().size()>0){
				System.out.println("获取请领装箱数据");
				qlNum=RenwuData[1].get("renwudan").get(0);
				List<String> ltQl=RenwuData[1].get("xianluming").getValues();
				for (int i = 0; i < ltQl.size(); i++) {
					ltQLRenwu.add(new QingfenRemwu(RenwuData[1].get("xianluId").get(i),RenwuData[1].get("xianluming").get(i), RenwuData[1].get("wangdianshu").get(i)));
				}
			}
			// 更新adapter
			qlAdapter.notifyDataSetChanged();
			//获取上缴清分数据
			if(RenwuData[0].get("renwudan").getValues().size()>0){
				System.out.println("获取上缴清分数据");
				sjNum=RenwuData[0].get("renwudan").get(0);
				System.out.println("sjNum----->"+sjNum);
				List<String> ltSj=RenwuData[0].get("xianluming").getValues();
				for (int i = 0; i < ltSj.size(); i++) {
					ltSJRenwu.add(new QingfenRemwu(RenwuData[0].get("xianluId").get(i),RenwuData[0].get("xianluming").get(i), RenwuData[0].get("wangdianshu").get(i),RenwuData[0].get("zhouzhuanxiangshu").get(i)));
				}
			}
			sjAdapter.notifyDataSetChanged();
			
			if (ltQLRenwu.size() > 0) {
				btnZhuangxiang.setEnabled(true);
				btnZhuangxiang.setBackgroundResource(R.drawable.buttom_selector_bg);
				//设置任务单号
				tvQLNo.setText(qlNum);
			}else{
				tvQLNo.setText("");
			}
			if (ltSJRenwu.size() > 0) {
				btnShangjiao.setEnabled(true);
				btnShangjiao.setBackgroundResource(R.drawable.buttom_selector_bg);
				tvSJNo.setText(sjNum);
			}else{
				tvSJNo.setText("");
			}
			
			manager.getRuning().remove();
		};
	};
	private Handler timeoutHandle=new Handler(){//连接超时handler
		public void handleMessage(Message msg) {
			manager.getRuning().remove();
			if(msg.what==0){
				manager.getAbnormal().timeout(QingfenRenwuActivity.this, "数据连接超时", new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						getDate();
						manager.getAbnormal().remove();
						manager.getRuning().runding(QingfenRenwuActivity.this, "数据加载中...");
					}
				});
			}
			if(msg.what==1){
				manager.getAbnormal().timeout(QingfenRenwuActivity.this, "网络连接失败", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					btnZhuangxiang.setEnabled(false);
					btnZhuangxiang.setBackgroundResource(R.drawable.button_gray);
					getDate();
					manager.getAbnormal().remove();
					manager.getRuning().runding(QingfenRenwuActivity.this, "数据加载中...");
				}
			});
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_qingfenrenwu);
		manager=new ManagerClass();
		manager.getRuning().runding(this, "数据加载中...");
		
		//绑定控件
		findView();
		
		//设置监听
		ivBack.setOnTouchListener(this);
		ivRefresh.setOnTouchListener(this);
		setListener();
		findListView();   // 绑定listView
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("调用getDate");
		//获取数据
		getDate();
	}
	
	/**
	 * 初始化控件
	 */
	private void findView(){
		llQingling=(LinearLayout)findViewById(R.id.ll_qingfenrenwu_qingling);
		llShangjiao=(LinearLayout)findViewById(R.id.ll_qingfenrenwu_shangjiao);
		rlTitleQingling=(RelativeLayout)findViewById(R.id.rl_qingfenrenwu_qingling_title);
		rlTitleShangjiao=(RelativeLayout)findViewById(R.id.rl_qingfenrenwu_shangjiao_title);
		lvQingling=(ListView)findViewById(R.id.lv_qingfenrenwu_qingling);
		lvShangjiao=(ListView)findViewById(R.id.lv_qingfenrenwu_shangjiao);
		btnZhuangxiang=(Button)findViewById(R.id.btn_qingfenrenwu_qingling);
		btnShangjiao=(Button)findViewById(R.id.btn_qingfenrenwu_shangjiao);
		tvQLNo=(TextView)findViewById(R.id.tv_qingfenrenwu_qingling_orderno);
		tvSJNo=(TextView)findViewById(R.id.tv_qingfenrenwu_shangjiao_orderno);
		ivBack=(ImageView)findViewById(R.id.iv_qingfenrenwu_back);
		ivRefresh=(ImageView)findViewById(R.id.iv_qingfenrenwu_refresh);
		btnZhuangxiang.setEnabled(false);
		btnZhuangxiang.setBackgroundResource(R.drawable.button_gray);
		btnShangjiao.setEnabled(false);
		btnShangjiao.setBackgroundResource(R.drawable.button_gray);
	}
	
	/**
	 * listView 绑定适配器
	 */
	private void findListView(){
		System.out.println("Adapter绑定");
		qlAdapter=new QinglingBaseAdapter(ltQLRenwu);
		sjAdapter=new ShangjiaoBaseAdapter(ltSJRenwu);
		//绑定Adapter
		lvQingling.setAdapter(qlAdapter);
		lvShangjiao.setAdapter(sjAdapter);
		
		
		//设置监听
		lvQingling.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bundle bundle=new Bundle();
				String xianluId=RenwuData[1].get("xianluId").get(arg2);
				bundle.putString("XianluName", ltQLRenwu.get(arg2).getName());
				bundle.putString("XianluId", xianluId);
				bundle.putString("renwudan", qlNum);
				
				Skip.skip(QingfenRenwuActivity.this, QinglingWangdianActivity.class, bundle, 0);
			}
		});
	}
	
	/**
	 * 设置监听
	 */
	private void setListener(){
		rlTitleQingling.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				llShangjiao.setVisibility(View.GONE);
				llQingling.setVisibility(View.VISIBLE);
			}
		});
		rlTitleShangjiao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				llQingling.setVisibility(View.GONE);
				llShangjiao.setVisibility(View.VISIBLE);
			}
		});
		btnZhuangxiang.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Bundle bundle=new Bundle();
				bundle.putString("qlNum", qlNum);
		//		bundle.putString("tvSJNo", tvSJNo.getText().toString()); 
				bundle.putInt("biaoqian", 0);
				Skip.skip(QingfenRenwuActivity.this, QinglingZhuangxiangInfoActivity.class, bundle, 0);
			}
		});
		btnShangjiao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Bundle bundle=new Bundle();
				bundle.putString("sjNum", sjNum);
				Skip.skip(QingfenRenwuActivity.this, QingFenLingQu_qf.class, bundle, 0);
			}
		});
	}
	
	/**
	 * 获得请领装箱支行列表数据，上缴清分任务线路
	 * @returns 
	 */
	private void getDate(){
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				 String params;
				try {
					
					//调用接口获得全部数据
					System.out.println("上传的参数（清分员1）="+Mapplication.getApplication().UserId);
					params = new QingfenRenwuService().getQingfenRenwu(Mapplication.getApplication().UserId);
					System.out.println("返回的结果:");
					//解析字符串
					RenwuData=Table.doParse(params);
					Message msg=okHandle.obtainMessage();
					msg.obj=RenwuData;
					
					okHandle.sendEmptyMessage(0);
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					timeoutHandle.sendEmptyMessage(0);
				}catch (Exception e) {
					e.printStackTrace();
					timeoutHandle.sendEmptyMessage(1);
				}
			}
		}).start();
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		btnZhuangxiang.setEnabled(false);
		btnZhuangxiang.setBackgroundResource(R.drawable.button_gray);
		btnShangjiao.setEnabled(false);
		btnShangjiao.setBackgroundResource(R.drawable.button_gray);
		
	}
	
	/**
	 * 请领装箱自定义Adapter
	 */
	class QinglingBaseAdapter extends BaseAdapter{
		private List<QingfenRemwu> lt;
		private ViewHolder vh;
		
		public QinglingBaseAdapter(List<QingfenRemwu> lt) {
			super();
			this.lt = lt;
		}
		@Override
		public int getCount() {
			return lt.size();
		}
		@Override
		public Object getItem(int arg0) {
			return lt.get(arg0);
		}
		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		@Override
		public View getView(int position, View v, ViewGroup arg2) {
			LayoutInflater inflater=LayoutInflater.from(QingfenRenwuActivity.this);
			if(v==null){
				v=inflater.inflate(R.layout.item_qingfenrenwu_qingling, null);
				vh=new ViewHolder();
				vh.tvName=(TextView)v.findViewById(R.id.tv_item_qingfenrenwu_qingling_xianluName);
				vh.tvCount=(TextView)v.findViewById(R.id.tv_item_qingfenrenwu_qingling_orderNumber);
				v.setTag(vh);
			}else{
				vh=(ViewHolder)v.getTag();
			}
			if(lt.size()==0){
				btnZhuangxiang.setEnabled(false);
				btnZhuangxiang.setBackgroundResource(R.drawable.button_gray);
			}else{
				btnZhuangxiang.setEnabled(true);
				btnZhuangxiang.setBackgroundResource(R.drawable.buttom_selector_bg);
			}
			vh.tvName.setText(lt.get(position).getName());
			vh.tvCount.setText(""+lt.get(position).getWangdianCount());
			return v;
		}
		public class ViewHolder{
			TextView tvName;
			TextView tvCount;
		}
	}
	

	/**
	 * 上缴清分自定义Adapter
	 */
	class ShangjiaoBaseAdapter extends BaseAdapter{
		private List<QingfenRemwu> lt;
		private ViewHolder vh;
		
		public ShangjiaoBaseAdapter(List<QingfenRemwu> lt) {
			this.lt = lt;
		}
		
		@Override
		public int getCount() {
			return lt.size();
		}
		@Override
		public Object getItem(int arg0) {
			return lt.get(arg0);
		}
		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		@Override
		public View getView(int position, View v, ViewGroup arg2) {
			LayoutInflater inflater=LayoutInflater.from(QingfenRenwuActivity.this);
			if(v==null){
				v=inflater.inflate(R.layout.item_qingfenrenwu_shangjiao, null);
				vh=new ViewHolder();
				vh.tvName=(TextView)v.findViewById(R.id.tv_item_qingfenrenwu_shnagjiao_name);
				vh.tvWangdianCount=(TextView)v.findViewById(R.id.tv_item_qingfenrenwu_shnagjiao_wangdianshuliang);
				vh.tvZhouzhuanxiangCount=(TextView)v.findViewById(R.id.tv_item_qingfenrenwu_shnagjiao_boxCount);
				v.setTag(vh);
			}else{
				vh=(ViewHolder)v.getTag();
			}
			if(lt.size()==0){
				btnShangjiao.setEnabled(false);
				btnShangjiao.setBackgroundResource(R.drawable.button_gray);
			}else{
				btnShangjiao.setEnabled(true);
				btnShangjiao.setBackgroundResource(R.drawable.buttom_selector_bg);
			}
			vh.tvName.setText(lt.get(position).getName());
			vh.tvWangdianCount.setText(""+lt.get(position).getWangdianCount());
			vh.tvZhouzhuanxiangCount.setText(""+lt.get(position).getZhouzhuanxiangCount());
			return v;
		}
		public class ViewHolder{
			TextView tvName;
			TextView tvWangdianCount;
			TextView tvZhouzhuanxiangCount;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		if(e.getAction()==MotionEvent.ACTION_DOWN){
			switch (v.getId()) {
			case R.id.iv_qingfenrenwu_back:
				new onTouthImageStyle().setFilter(ivBack);
				break;
			case R.id.iv_qingfenrenwu_refresh:
				new onTouthImageStyle().setFilter(ivRefresh);
				break;
			default:
				break;
			}
		}
		if(e.getAction()==MotionEvent.ACTION_MOVE){
			//每移动一次move+1
			move++;
//			System.out.println("=====move:"+move);
		}
		if (e.getAction() == MotionEvent.ACTION_UP) {
			//只有在move<canMove的情况下才能执行操作.
			int canMove=10;
			switch (v.getId()) {
			case R.id.iv_qingfenrenwu_back:
				new onTouthImageStyle().removeFilter(ivBack);
				if(move<canMove){
					this.finish();
				}
				break;
			case R.id.iv_qingfenrenwu_refresh:
				new onTouthImageStyle().removeFilter(ivRefresh);
				if(move<canMove){
					getDate();
					manager.getRuning().runding(QingfenRenwuActivity.this, "正在刷新中...");
				}
				break;
			default:
				break;
			}
			move = 0;
		}
		if(e.getAction()==MotionEvent.ACTION_CANCEL){
			switch (v.getId()) {
			case R.id.iv_qingfenrenwu_back:
				new onTouthImageStyle().setFilter(ivBack);
				break;
			case R.id.iv_qingfenrenwu_refresh:
				new onTouthImageStyle().setFilter(ivRefresh);
				break;
			default:
				break;
			}
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}
}
