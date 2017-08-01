package com.ljsw.tjbankpda.main;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pda.R;
import com.ljsw.tjbankpda.db.entity.Qingfenxianjin;
import com.ljsw.tjbankpda.qf.application.Mapplication;
import com.ljsw.tjbankpda.qf.entity.Box;
import com.ljsw.tjbankpda.qf.entity.ZhuanxiangTongji;
import com.ljsw.tjbankpda.qf.service.QingfenRenwuService;
import com.ljsw.tjbankpda.util.NumFormat;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.manager.classs.pad.ManagerClass;

/**
 * 请领装入量获取
 * @author yuyunheng
 *
 */
public class QinglingZhuangxiangInfoActivity extends FragmentActivity {
	/* 定义控件 */
	private LinearLayout llXianjin;
	private LinearLayout llZhongkong;
	private Button btnZhuangxiang;
	private TextView tvTitle;
	private TextView tvDiZhiCount;//抵质押品数量
	private TextView tvXianjinCount;//抵质押品数量
	private TextView tvZhongkongCount;//抵质押品数量
	private ImageView ivBack;
	
	/* 定义全局变量 */
	private List<Qingfenxianjin> ltXianjin=new ArrayList<Qingfenxianjin>();
	private List<Box> ltZhongkong=new ArrayList<Box>();
	private String diZhiCount;//抵质押品数量
	private String diZhibianhao;//抵质押品编号
	private int totalXianjin;//现金总数量
	private int totalZhongkong;//重空凭证总数量
	private int biaoqian;//标签,0为装箱领款,1为请领装箱明细.
	private Bundle bundle;
	private String orderNum;//请领任务订单号
	
	
	private ManagerClass manager;//弹出框
	private Handler okHandle=new Handler(){//数据获取成功handler
		public void handleMessage(Message msg) {
			for (int i = 0; i < ltXianjin.size(); i++) {
				View v=LayoutInflater.from(QinglingZhuangxiangInfoActivity.this).inflate(R.layout.item_qingfenrenwu_qinglingxiangqing, null);
				TextView tvType=(TextView)v.findViewById(R.id.tv_item_qinglingmingzi_type);
				TextView tvCount=(TextView)v.findViewById(R.id.tv_item_qinglingmingzi_count);
				tvType.setText(ltXianjin.get(i).getQuanbie());
				String count=ltXianjin.get(i).getShuliang()+"";
				tvCount.setText(count);
				llXianjin.addView(v);
				btnZhuangxiang.setEnabled(true);
				btnZhuangxiang.setBackgroundResource(R.drawable.buttom_selector_bg);
			}
			for (int i = 0; i < ltZhongkong.size(); i++) {
				View v=LayoutInflater.from(QinglingZhuangxiangInfoActivity.this).inflate(R.layout.item_qingfenrenwu_qinglingxiangqing, null);
				TextView tvType=(TextView)v.findViewById(R.id.tv_item_qinglingmingzi_type);
				TextView tvCount=(TextView)v.findViewById(R.id.tv_item_qinglingmingzi_count);
				tvType.setText(ltZhongkong.get(i).getType());
				tvCount.setText(""+ltZhongkong.get(i).getCount());
				llZhongkong.addView(v);
				btnZhuangxiang.setEnabled(true);
				btnZhuangxiang.setBackgroundResource(R.drawable.buttom_selector_bg);
			}
			tvDiZhiCount.setText(diZhiCount);
			tvXianjinCount.setText(new NumFormat().format(totalXianjin+""));
			tvZhongkongCount.setText(totalZhongkong+"");
			
			manager.getRuning().remove();
		};
	};
	private Handler timeoutHandle=new Handler(){//连接超时handler
		public void handleMessage(Message msg) {
			manager.getRuning().remove();
			if(msg.what==0){
				manager.getAbnormal().timeout(QinglingZhuangxiangInfoActivity.this, "数据连接超时", new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if(biaoqian==1){
							getDate();
						}
						manager.getAbnormal().remove();
					}
				});
			}
			if(msg.what==1){
				manager.getAbnormal().timeout(QinglingZhuangxiangInfoActivity.this, "网络连接失败", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					manager.getAbnormal().remove();
				}
			});
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_qingfengrenwu_qinfenxiangqing);
		llXianjin=(LinearLayout)findViewById(R.id.ll_qingfenrenwu_qinglingxiangqing_xianjinInfo);
		llZhongkong=(LinearLayout)findViewById(R.id.ll_qingfenrenwu_qinglingxiangqing_zhongkongInfo);
		btnZhuangxiang=(Button)findViewById(R.id.btn_qingfengrenwu_qinglingxiangqing_zhuangxiang);
		btnZhuangxiang.setEnabled(false);
		btnZhuangxiang.setBackgroundResource(R.drawable.button_gray);
		tvTitle=(TextView)findViewById(R.id.tv_qingfengrenwu_qinglingxiangqing_title);
		tvDiZhiCount=(TextView)findViewById(R.id.tv_qingfengrenwu_qinglingxiangqing_sizhi_total);
		tvXianjinCount=(TextView)findViewById(R.id.tv_qingfengrenwu_qinglingxiangqing_xianjing_total);
		tvZhongkongCount=(TextView)findViewById(R.id.tv_qingfengrenwu_qinglingxiangqing_zhongkong_total);
		ivBack=(ImageView)findViewById(R.id.iv_qingfenrenwu_qinglingxiangqing_back);
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				QinglingZhuangxiangInfoActivity.this.finish();
			}
		});
		bundle=super.getIntent().getExtras();
		biaoqian=bundle.getInt("biaoqian",-1);
		orderNum=bundle.getString("qlNum");
		
		System.out.println("orderNum1:"+orderNum);

		manager=new ManagerClass();
		manager.getRuning().runding(this, "数据加载中...");

		if(biaoqian==1){
			btnZhuangxiang.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					bundle.putString("zongMoney", totalXianjin+"");  //将现金总数传入bundle
					bundle.putString("zongZhongkong", totalZhongkong+"");  //将重空总数传入bundle
					bundle.putString("zongDizhi", diZhiCount+"");  //将抵质押品总数传入bundle
					manager.getRuning().runding(QinglingZhuangxiangInfoActivity.this, "加载中...");
					Skip.skip(QinglingZhuangxiangInfoActivity.this, QinglingZhuangxiangActivity.class, bundle, 0);
				}
			});
			getDate();   // 装入量获取
		}else if(biaoqian==0){
			System.out.println("装箱领款");
			btnZhuangxiang.setVisibility(View.GONE);
			tvTitle.setText("装箱领款");
			getTotalData();   // 装箱领款
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		manager.getRuning().remove();
	}
	
	/**
	 * 根据订单号获取数据  (装入量获取)
	 */
	private void getDate(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				 String params;
				try {
					//初始化全局变量
					Mapplication.getApplication().IsDizhiOK=false;
					Mapplication.getApplication().IsXianjingOK=false;
					Mapplication.getApplication().IsZhongkongOK=false;
					Mapplication.getApplication().zxLtXianjing.clear();
					Mapplication.getApplication().zxLtZhongkong.clear();
					Mapplication.getApplication().zxTjDizhi=null;
					Mapplication.getApplication().boxLtXianjing.clear();
					Mapplication.getApplication().boxLtZhongkong.clear();
					Mapplication.getApplication().boxLtDizhi.clear();
					ltXianjin.clear();
					ltZhongkong.clear();
					//连接服务器
					params = new QingfenRenwuService().getParams(orderNum, "getloadNum");
					System.out.println("装入量返回信息："+params);
					Table[] RenwuData=Table.doParse(params);
					List<String> ltx=RenwuData[0].get("shuliang").getValues();
					if(ltx.size()!=0){
						for (int i = 0; i < ltx.size(); i++) {
							ltXianjin.add(new Qingfenxianjin(RenwuData[0].get("quanbie").get(i), RenwuData[0].get("shuliang").get(i),
									RenwuData[0].get("quanbieId").get(i),RenwuData[0].get("quanJiazhi").get(i)));
						}
					}
					List<String> ltz=RenwuData[1].get("shuliang").getValues();
					if(ltz.size()!=0){
						for (int i = 0; i < ltz.size(); i++) {
							ltZhongkong.add(new Box(RenwuData[1].get("zhongkongtype").get(i), RenwuData[1].get("shuliang").get(i)));
						}
					}
					for (String str : ltz) {
						totalZhongkong+=Integer.parseInt(str);
					}
					totalXianjin=0;  // 重置现金总数
					for (Qingfenxianjin xianjin : ltXianjin) {
						// 取出现金价值
						double jiazhi=Double.parseDouble(xianjin.getQuanJiazhi().trim());
						// 取出现金数量
						int num=Integer.parseInt(xianjin.getShuliang().trim());
						
						totalXianjin+=jiazhi*num;
					}
					diZhiCount=RenwuData[2].get("dizhinum").get(0);
					//将获取到的数据填充到Mapplication里
					Mapplication.getApplication().xjType=new String[ltXianjin.size()];
					Mapplication.getApplication().zkType=new String[ltZhongkong.size()];
					for (int i = 0; i < ltXianjin.size(); i++) {
						Mapplication.getApplication().zxLtXianjing.add(new ZhuanxiangTongji(ltXianjin.get(i).getQuanbie(), 0, Integer.parseInt(ltXianjin.get(i).getShuliang())));
						Mapplication.getApplication().xjType[i]=ltXianjin.get(i).getQuanbie();
					}
					for (int j = 0; j < ltZhongkong.size(); j++) {
						Mapplication.getApplication().zxLtZhongkong.add(new ZhuanxiangTongji(ltZhongkong.get(j).getType(), 0, Integer.parseInt(ltZhongkong.get(j).getCount())));
						Mapplication.getApplication().zkType[j]=ltZhongkong.get(j).getType();
					}
					Mapplication.getApplication().zxTjDizhi=new ZhuanxiangTongji("抵质押品", 0,  Integer.parseInt(diZhiCount));
					Mapplication.getApplication().ltDizhiNum.clear();
					Mapplication.getApplication().ltZzxNumber.clear();
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
	
	/**
	 * 获得所有订单的数据   (装箱领款)
	 */
	private void getTotalData(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				 String params;
				try {
					params = new QingfenRenwuService().getParams(orderNum, "getReceive");
					System.out.println("装箱领款返回信息："+params);
					Table[] RenwuData=Table.doParse(params);
					List<String> ltx=RenwuData[0].get("shuliang").getValues();
					for (int i = 0; i < ltx.size(); i++) {
						ltXianjin.add(new Qingfenxianjin(RenwuData[0].get("quanbie").get(i), RenwuData[0].get("shuliang").get(i),
								RenwuData[0].get("quanbieId").get(i),RenwuData[0].get("quanJiazhi").get(i)));
						
					}
					List<String> ltz=RenwuData[1].get("shuliang").getValues();
					if(ltz.size()!=0){
						for (int i = 0; i < ltz.size(); i++) {
							ltZhongkong.add(new Box(RenwuData[1].get("zhongkongtype").get(i), RenwuData[1].get("shuliang").get(i)));
						}
					}
					for (String str : ltz) {
						totalZhongkong+=Integer.parseInt(str);
					}
				

					
					totalXianjin=0;  // 重置现金总数
					for (Qingfenxianjin xianjin : ltXianjin) {
						// 取出现金价值
						double jiazhi=Double.parseDouble(xianjin.getQuanJiazhi().trim());
						// 取出现金数量
						int num=Integer.parseInt(xianjin.getShuliang().trim());
						totalXianjin+=jiazhi*num;
					}
					
					diZhiCount=RenwuData[2].get("dizhinum").get(0);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			QinglingZhuangxiangInfoActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
