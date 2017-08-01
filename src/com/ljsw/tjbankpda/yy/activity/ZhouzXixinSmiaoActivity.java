package com.ljsw.tjbankpda.yy.activity;


import hdjc.rfid.operator.RFID_Device;

import java.net.SocketTimeoutException;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.ljsw.tjbankpda.util.Table;
import com.ljsw.tjbankpda.yy.application.S_application;
import com.ljsw.tjbankpda.yy.application.ZzxSaomiaoUtil;
import com.ljsw.tjbankpda.yy.service.ICleaningManService;
import com.manager.classs.pad.ManagerClass;
/**
 * 周转箱信息扫描
 * @author Administrator
 */
public class ZhouzXixinSmiaoActivity extends Activity implements OnClickListener{
	private TextView zzx_smbhao,//周转箱编号
					 zzx_ssjigou,//所属机构
					 zzx_ssxl;//所属线路
	private ManagerClass managerClass;
	OnClickListener onclickreplace;
	Table[] tables;
	SaomiaoThread stm;
	private Handler handl;
	
	private ZzxSaomiaoUtil zsm;
	private RFID_Device rfid;

	private RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	};
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:	
				if(null!=S_application.getApplication().bianhao){
					managerClass.getRuning().runding(ZhouzXixinSmiaoActivity.this,"加载信息中...");
					stm = new SaomiaoThread();
					stm.start();
				}		
				break;

			default:
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zzxx_saomiao_s);
		findViewById(R.id.zhouzhuan_back).setOnClickListener(this);
		managerClass = new ManagerClass();
		initView();
		zsm = new ZzxSaomiaoUtil();
		zsm.setHandler(handler);
		// 重试单击事件
		onclickreplace = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				managerClass.getRuning().runding(ZhouzXixinSmiaoActivity.this,
						"加载信息中...");
				stm = new SaomiaoThread();
				stm.start();
			}
		};
		
		handl = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				managerClass.getRuning().remove();
				super.handleMessage(msg);
				switch (msg.what) {
				case 1://验证成功跳转
					zzx_smbhao.setText(S_application.getApplication().bianhao);
					List<String> jigous=tables[0].get("jigou").getValues();
					if(jigous.size()<=0){
						zzx_ssjigou.setText("");
					}else{
						zzx_ssjigou.setText(jigous.get(0));
					}
					
					List<String> xianlus=tables[0].get("xianlu").getValues();
					if(xianlus.size()<=0){
						zzx_ssxl.setText("");
					}else{
						zzx_ssxl.setText(xianlus.get(0));
					}
					
					
					
					break;
				case -1:
					managerClass.getAbnormal().timeout(ZhouzXixinSmiaoActivity.this,"信息加载异常", onclickreplace);
					break;
				case -4:					
					managerClass.getAbnormal().timeout(ZhouzXixinSmiaoActivity.this,"连接超时，重新链接？", onclickreplace);
					break;
				}
			}
		};
		
			
		
	}
	
	public void initView(){
		 zzx_smbhao=(TextView) this.findViewById(R.id.zzx_smbhao);//周转箱编号
		 zzx_ssjigou=(TextView) this.findViewById(R.id.zzx_ssjigou);//所属机构
		 zzx_ssxl=(TextView) this.findViewById(R.id.zzx_ssxl);
	}
	
	class SaomiaoThread extends Thread{
		Message m;
		public SaomiaoThread() {
			super();
			m = handl.obtainMessage();
		}
		@Override
		public void run() {
			super.run();
			ICleaningManService is = new ICleaningManService();
			try {
				tables = Table.doParse(is.zhouzhuangxiangXinxi(S_application.getApplication().bianhao));
				
				if(tables.length>0){
					m.what=1;
				}				
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				m.what = -4;
			}catch (Exception e) {
				e.printStackTrace();
				m.what=-1;
			}finally{
				handl.sendMessage(m);
				GolbalUtil.onclicks = true;
			}
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		//S_application.getApplication().bianhao="";
		getRfid().addNotifly(zsm);
		getRfid().open_a20();
	}
		
	
	@Override
	protected void onPause() {
		super.onPause();
		getRfid().close_a20();
		S_application.getApplication().bianhao="";
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.zhouzhuan_back:
			S_application.getApplication().bianhao="";
			//System.out.println("S_application.getApplication().bianhao="+S_application.getApplication().bianhao);		
			ZhouzXixinSmiaoActivity.this.finish();
			break;

		default:
			break;
		}
		
	}
}
