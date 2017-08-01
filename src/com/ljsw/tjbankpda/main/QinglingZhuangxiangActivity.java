package com.ljsw.tjbankpda.main;
import hdjc.rfid.operator.RFID_Device;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.example.app.util.Skip;
import com.example.pda.R;
import com.ljsw.tjbankpda.qf.application.Mapplication;
import com.ljsw.tjbankpda.qf.entity.QuanbieXinxi;
import com.ljsw.tjbankpda.qf.fragment.QinglingZhuangxiangDizhiFragment;
import com.ljsw.tjbankpda.qf.fragment.QinglingZhuangxiangXianjinFragment;
import com.ljsw.tjbankpda.qf.fragment.QinglingZhuangxiangZHongkongFragment;
import com.ljsw.tjbankpda.qf.service.QingfenRenwuService;
import com.ljsw.tjbankpda.util.Table;
import com.manager.classs.pad.ManagerClass;

/**
 * 请领装箱 界面
 * @author FUHAIQING
 *
 */
public class QinglingZhuangxiangActivity extends FragmentActivity  {
	private Button btnXianjin;
	private Button btnZhongkong;
	private Button btnDizhi;
	private Button btnTongji;
	private Button btnLuru;
	private Bundle bundle;//获得订单编号Bundle
	private ImageView ivBack;

	FrameLayout fl;
	private FragmentManager fm;
	public Map<String,Fragment> fragments = new HashMap<String,Fragment>();
	private Map<String,QuanbieXinxi> quanbieXinxi=new HashMap<String, QuanbieXinxi>();
	private ManagerClass manager=new ManagerClass();
	private RFID_Device rfid;
	private RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}
	
	Handler handler;
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getRfid().scanOpen();
		bundle=super.getIntent().getExtras();//接收从上个界面（QinglingZhuangxiangInfoActivity.class）传入的bundle
		System.out.println("装箱界面--订单号="+bundle.getString("qlNum"));
		setContentView(R.layout.activity_qingfen_zhuangxiang);
		btnXianjin=(Button)findViewById(R.id.btn_qingfen_zhuangxiang_xianjin);
		btnZhongkong=(Button)findViewById(R.id.btn_qingfen_zhuangxiang_zhongkong);
		btnDizhi=(Button)findViewById(R.id.btn_qingfen_zhuangxiang_dizhi);
		btnTongji=(Button)findViewById(R.id.btn_qingfen_zhuangxiang_tongji);
		btnLuru=(Button)findViewById(R.id.btn_qingfen_zhuangxiang_zhouzhuanxiangluru);
		ivBack=(ImageView)findViewById(R.id.iv_qingfen_zhuangxiang_back);
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				QinglingZhuangxiangActivity.this.finish();
			}
		});
		
		if(Mapplication.getApplication().zxLtXianjing.size()==0){
			Mapplication.getApplication().IsXianjingOK=true;
		}
		if(Mapplication.getApplication().zxLtZhongkong.size()==0){
			Mapplication.getApplication().IsZhongkongOK=true;
		}
		if(Mapplication.getApplication().zxTjDizhi.getWeiZhuang()==0){
			Mapplication.getApplication().IsDizhiOK=true;
		}
		
		fm=getSupportFragmentManager();  
		
		
		btnXianjin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(!fragments.containsKey("xianjin")){
					fragments.put("xianjin", new QinglingZhuangxiangXianjinFragment(quanbieXinxi));
				}
				fm.beginTransaction().replace(R.id.fl_qingfen_zhuangxiang_context,fragments.get("xianjin")).commit();
				btnXianjin.setBackgroundResource(R.color.white);
				btnZhongkong.setBackgroundResource(R.drawable.btn_bottom_gray);
				btnDizhi.setBackgroundResource(R.drawable.btn_bottom_gray);
			}
		});
		btnZhongkong.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(!fragments.containsKey("zhongkong")){
					fragments.put("zhongkong", new QinglingZhuangxiangZHongkongFragment());
				}
				fm.beginTransaction().replace(R.id.fl_qingfen_zhuangxiang_context,fragments.get("zhongkong")).commit();
				btnXianjin.setBackgroundResource(R.drawable.btn_bottom_gray);
				btnZhongkong.setBackgroundResource(R.color.white);
				btnDizhi.setBackgroundResource(R.drawable.btn_bottom_gray);
			}
		});
		btnDizhi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//manager.getRuning().runding(QinglingZhuangxiangActivity.this, "正在开启RIFI,请稍等...");
				if(!fragments.containsKey("dizhi")){
					fragments.put("dizhi", new QinglingZhuangxiangDizhiFragment());
				}
				fm.beginTransaction().replace(R.id.fl_qingfen_zhuangxiang_context,fragments.get("dizhi")).commit();
				btnXianjin.setBackgroundResource(R.drawable.btn_bottom_gray);
				btnZhongkong.setBackgroundResource(R.drawable.btn_bottom_gray);
				btnDizhi.setBackgroundResource(R.color.white);
			}
		});
		btnLuru.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				manager.getRuning().runding(QinglingZhuangxiangActivity.this, "正在开启RIFI,请稍等...");
				getRfid().scanclose();
				Skip.skip(QinglingZhuangxiangActivity.this, QinglingZhouzhuanxiangluruActivity.class, bundle, 0);
			}
		});
		btnTongji.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//manager.getResultmsg().resultOnlyMsg(QinglingZhuangxiangActivity.this, "加载中...");
				Skip.skip(QinglingZhuangxiangActivity.this, QinglingZhuangxiangTongjiActivity.class, bundle, 0);
//				Intent intent=new Intent(QinglingZhuangxiangActivity.this,QinglingZhuangxiangTongjiActivity.class);
//				startActivity(intent);
			}
		});
		
		
		handler = new Handler(){  
	        public void handleMessage(Message msg) { 
	        	manager.getRuning().remove();  //关闭提示框
	        	switch (msg.what) {
				case 1:
					//正常获取
					String params=msg.obj.toString();
					Table table=Table.doParse(params)[0];
					List<String> quanbieIds=table.get("quanbieId").getValues();  // 券别id
					List<String> quanbieName=table.get("quanbieName").getValues(); //券别名称
					List<String> quanbieJiazhi=table.get("quanjiazhi").getValues();  //券别价值
					
					//将信息封装如map集合
					for (int i = 0; i < quanbieIds.size(); i++) {
						QuanbieXinxi xianjin=new QuanbieXinxi();
						xianjin.setQuanbieId(quanbieIds.get(i));
						xianjin.setQuanbieName(quanbieName.get(i));
						xianjin.setQuanJiazhi(quanbieJiazhi.get(i));
						quanbieXinxi.put(quanbieName.get(i), xianjin);
					}
					
					// 加载默认的券别信息
					fragments.put("xianjin", new QinglingZhuangxiangXianjinFragment(quanbieXinxi));
					fm.beginTransaction().replace(R.id.fl_qingfen_zhuangxiang_context,fragments.get("xianjin")).commit();
					break;

				case 2:
					//获取超时
					manager.getAbnormal().timeout(QinglingZhuangxiangActivity.this,
							"数据连接超时", new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									manager.getAbnormal().remove();
									getXQuanbieMingxi(); // 点击重新获取
								}
							});
					break;
					
				case 3:
					//获取失败
					manager.getAbnormal().timeout(QinglingZhuangxiangActivity.this,
							"获取券别信息失败", new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									manager.getAbnormal().remove();
									getXQuanbieMingxi(); // 点击重新获取
								}
							});
					break;
				}
	        };  
	    };
	    
	    getXQuanbieMingxi();  // 获取券别信息
	}
	@Override
	protected void onResume() {
		super.onResume();
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				getRfid().scanOpen();
//			}
//		}).start();
	}
	@Override
	protected void onPause() {
		super.onPause();
		manager.getRuning().remove();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("请领装箱界面Destroy");
		getRfid().scanclose();
	}
	
	
	/**
	 *  获取券别信息
	 */
	private void getXQuanbieMingxi(){
		manager.getRuning().runding(QinglingZhuangxiangActivity.this, "数据加载中...");
		Thread thread=new Thread(new QuanbieXinxis());
		thread.start();
	}
	/**
	 * 
	 * @author yuyunheng
	 */
	private class QuanbieXinxis implements Runnable{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			QingfenRenwuService service=new QingfenRenwuService();
			Message msg=handler.obtainMessage();
			try {
				String params=service.getQuanbieList();
				if(params!=null&&!params.equals("")){
					// 获取成功
					msg.what=1;
					msg.obj=params;
				}else{
					msg.what=3;
				}
			}catch(SocketTimeoutException time){
				// 连接超时
				msg.what=2;
			}catch (Exception e) {
				// 异常
				msg.what=3;
				e.printStackTrace();		
			}
			
			handler.sendMessage(msg);
		}
		
	}

	
}
