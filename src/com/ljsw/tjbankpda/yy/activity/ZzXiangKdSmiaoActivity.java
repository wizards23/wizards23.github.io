package com.ljsw.tjbankpda.yy.activity;

import hdjc.rfid.operator.RFID_Device;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ListView;
import android.widget.TextView;

import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.ljsw.tjbankpda.qf.entity.Qingfendan;
import com.ljsw.tjbankpda.util.EqualsList;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.ljsw.tjbankpda.yy.application.S_application;
import com.ljsw.tjbankpda.yy.application.SaomiaoUtil;
import com.ljsw.tjbankpda.yy.service.ICleaningManagementService;
import com.manager.classs.pad.ManagerClass;

/**
 * 周转箱核对扫描页面(清分管理员)
 * @author Administrator
 */
public class ZzXiangKdSmiaoActivity extends FragmentActivity implements OnClickListener{
	Table[] tables;
	private ManagerClass managerClass;
	OnClickListener onclickreplace;
	private List<Qingfendan> wlist = new ArrayList<Qingfendan>();//未扫描集合(上个页面传过来的计划单任务明的集合)
	private List<String> wsmlist = new ArrayList<String>();//未扫描款箱集合
	private List<String> ylist = new ArrayList<String>();//已扫描款箱集合
	private ListView wsm_list,ysm_list;//未扫描;已扫描
	private TextView ysmText_count,//已扫描
					 wsmText_count,//为扫描
					 zzXbox;//错误的周转箱编号
	//private String jhdId;//计划单号
	private String lineName ="lineId:";//任务单名称  多个任务单名称的时候用“_”分隔
	LiebiaoAdapter wsm = new LiebiaoAdapter();
	YliebiaoAdapter ysm = new YliebiaoAdapter();
//	TijiaoAnytask tjTask;
	SaomiaoUtil stu;
	private RFID_Device rfid;

	private RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}
	
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qingfen_zzxhdsm_s);
		managerClass = new ManagerClass();
		stu = new SaomiaoUtil();
		
		// 重试单击事件
		onclickreplace = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				managerClass.getRuning().runding(ZzXiangKdSmiaoActivity.this,
						"提交信息中...");
				Thread thread=new Thread(new TijiaoAnytask2());
				thread.start();
			}
		};		
		handler = new Handler(){
			@SuppressWarnings("static-access")
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:	
					zzXbox.setText(S_application.getApplication().wrong);
					ysmText_count.setText(""+S_application.getApplication().rightlist.size());
					wsmText_count.setText(""+S_application.getApplication().leftlist.size());
					ysm_list.setAdapter(ysm);
//					new TurnListviewHeight(ysm_list);
					wsm.notifyDataSetChanged();
					ysm.notifyDataSetChanged();
					if(EqualsList.equals(wsmlist,S_application.getApplication().rightlist)){
						findViewById(R.id.btn_heduicg).setBackgroundResource(R.drawable.buttom_selector_bg);
						findViewById(R.id.btn_heduicg).setEnabled(true);
						return;
					}
					break;

				default:
					break;
				}
			}
			
		};		
		stu.setHandler(handler);
	}
	
	/**
	 * 初始化控件
	 */
	public void initView(){	
		zzXbox = (TextView) this.findViewById(R.id.zzXbox);
		wsmText_count = (TextView) this.findViewById(R.id.qf_weisaomiao);
		ysmText_count = (TextView) this.findViewById(R.id.qf_yisaomiao);
		wsm_list = (ListView) this.findViewById(R.id.wsm_list);
		ysm_list = (ListView) this.findViewById(R.id.ysm_list);
		findViewById(R.id.btn_heduicg).setOnClickListener(this);
		findViewById(R.id.btn_quxiao).setOnClickListener(this);
		wsm_list.setAdapter(wsm);	
		ysm_list.setAdapter(ysm);		
	}
	
	/**
	 * 获取数据
	 */
	public void getData(){
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle!=null){
			wlist = (List<Qingfendan>) bundle.getSerializable("newlist");
			for (int i = 0; i < wlist.size(); i++) {
				String[] str = wlist.get(i).getQfzzxbh().split(",");
				lineName +=wlist.get(i).getQfNum()+",";
				for (int j = 0; j < str.length; j++) {
					String[] zzxs=str[j].split("_");
					for (String string : zzxs) {
						S_application.getApplication().leftlist.add(string);
						wsmlist.add(string);
					}
					
				}
			}
			
			if(!("lineId:").equals(lineName)){
				lineName=lineName.substring(0,lineName.length()-1);
			}
			
			
			System.out.println("lineName(任务单名称:)"+lineName);
		}		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getData();
		initView();
		getRfid().addNotifly(stu);
		getRfid().open_a20();
		wsmText_count.setText("" + S_application.getApplication().leftlist.size());
		ysmText_count.setText("" + S_application.getApplication().rightlist.size());
		wsm_list.setAdapter(wsm);
//		new TurnListviewHeight(wsm_list);
		ysm_list.setAdapter(ysm);
//		new TurnListviewHeight(ysm_list);				
	}

	@Override
	protected void onPause() {
		super.onPause();
		getRfid().close_a20();
		S_application.getApplication().leftlist.clear();
		S_application.getApplication().rightlist.clear();
		S_application.getApplication().bianhao="";
		S_application.getApplication().wrong ="";
	}
	
	private Handler handle= new Handler(){ //加载提交数据和获取数据的前台通知
		public void handleMessage(Message msg) {
			managerClass.getRuning().remove();
			switch (msg.what) {
			case 1://验证成功
				yanzhengYesNo=true;
				//弹出成功的Dialog
//				final AlertDialog.Builder builder = new Builder(ZzXiangKdSmiaoActivity.this);
//				LayoutInflater inflater = LayoutInflater.from(ZzXiangKdSmiaoActivity.this);
//				View v = inflater.inflate(R.layout.dialog_success, null);
//				but = (Button)v.findViewById(R.id.success);
//		//		but.setClickable(false);   //初始化为不可点击状态
//				builder.setView(v);
//				final AlertDialog dialog=builder.create();
//				but.setText("验证成功");
//				but.setClickable(true);
//				yanzhengYesNo=true;
//				if(but!=null){
//					but.setOnClickListener(new View.OnClickListener() {
//						@Override
//						public void onClick(View arg0) {
//							dialog.cancel();
//							if(yanzhengYesNo){  
//								HuoqThread task = new HuoqThread();
//								task.start();				
//							}
//						}
//					});
//				}
//				dialog.show();

				managerClass.getGoBack().back(ZzXiangKdSmiaoActivity.this,
						"验证成功", new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								managerClass.getGoBack().remove();
								if(yanzhengYesNo){
									Skip.skip(ZzXiangKdSmiaoActivity.this,QingfenJhdMxActivity.class, null, 0);				
								}
							}
						});
				
				break;
			case 2: //验证失败
//				but.setText("提交失败");
//				but.setClickable(true);
				break;
			case -1:		
				managerClass.getAbnormal().timeout(ZzXiangKdSmiaoActivity.this,"提交异常", onclickreplace);
				break;
			case -4:			
				managerClass.getAbnormal().timeout(ZzXiangKdSmiaoActivity.this,"连接超时，重新链接？", onclickreplace);
				break;
//			case 3:	//获取数据成功 跳转页面		
//				Skip.skip(ZzXiangKdSmiaoActivity.this,QingfenJhdMxActivity.class, null, 0);
//				break;
//			case 4:			
//				managerClass.getAbnormal().timeout(ZzXiangKdSmiaoActivity.this,"连接超时，重新链接？", onclickreplace2);
//				break;
//			case 5:			
//				managerClass.getAbnormal().timeout(ZzXiangKdSmiaoActivity.this,"获取数据异常", onclickreplace2);
//				break;
			}
		}
	};
		
	/**
	 * 未扫描 列表
	 * @author Administrator
	 */
	class LiebiaoAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return S_application.getApplication().leftlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return S_application.getApplication().leftlist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			QingfenEntivity entity;	
			if(arg1==null){
				entity = new QingfenEntivity();
				arg1 = LayoutInflater.from(ZzXiangKdSmiaoActivity.this).inflate(
						R.layout.item_qingfen_zzxhdsm_s, null);				
				entity.textView1 = (TextView) arg1.findViewById(R.id.qfzzxhdNum);
				arg1.setTag(entity);
			}else{
				entity = (QingfenEntivity) arg1.getTag();
			}
			entity.textView1.setText(S_application.getApplication().leftlist.get(arg0));
			return arg1;
		}	
	}
	
	/**
	 * 已扫描 列表
	 * @author Administrator
	 */
	class YliebiaoAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return S_application.getApplication().rightlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			return S_application.getApplication().rightlist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			QingfenEntivity entity;	
			if(arg1==null){
				entity = new QingfenEntivity();
				arg1 = LayoutInflater.from(ZzXiangKdSmiaoActivity.this).inflate(
						R.layout.item_qingfen_zzxhdsm_s, null);				
				entity.textView1 = (TextView) arg1.findViewById(R.id.qfzzxhdNum);
				arg1.setTag(entity);
			}else{
				entity = (QingfenEntivity) arg1.getTag();
			}
			entity.textView1.setText(S_application.getApplication().rightlist.get(arg0));
			return arg1;
		}	
	}
	
	/**
	 * 实体类控件
	 * @author Administrator
	 */
	static class QingfenEntivity{
		public TextView textView1;
	}
		
//	private Button but;    // 提示按键
	private boolean yanzhengYesNo=false;   //验证状态 
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_heduicg://确定
			managerClass.getRuning().runding(ZzXiangKdSmiaoActivity.this,"提交信息中...");
			Thread thread=new Thread(new TijiaoAnytask2());
			thread.start();
//			//弹出成功的Dialog
//			final AlertDialog.Builder builder = new Builder(ZzXiangKdSmiaoActivity.this);
//			LayoutInflater inflater = LayoutInflater.from(ZzXiangKdSmiaoActivity.this);
//			View v = inflater.inflate(R.layout.dialog_success, null);
//			but = (Button)v.findViewById(R.id.success);
//			but.setClickable(false);   //初始化为不可点击状态
//			builder.setView(v);
//			final AlertDialog dialog=builder.create();
//			if(but!=null){
//				but.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View arg0) {
//						dialog.cancel();
//						if(yanzhengYesNo){  
//							HuoqThread task = new HuoqThread();
//							task.start();
//							
//						}
//					}
//				});
//			}
//			dialog.show();
			break;

		case R.id.btn_quxiao://取消情况集合
			zzXbox.setText("");	
			findViewById(R.id.btn_heduicg).setBackgroundResource(R.drawable.gray_btn_bg);
			findViewById(R.id.btn_heduicg).setEnabled(false);
			S_application.getApplication().rightlist.clear();
			S_application.getApplication().leftlist.clear();
			S_application.getApplication().leftlist.addAll(wsmlist);			
			wsmText_count.setText(""+S_application.getApplication().leftlist.size());
			ysmText_count.setText(""+0);
			wsm.notifyDataSetChanged();
			ysm.notifyDataSetChanged();
			break;
		}
	}
	
	/**
	 * 提交数据
	 * @author yuyunheng
	 */
	class TijiaoAnytask2 implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ICleaningManagementService is = new ICleaningManagementService();
			try {
				String str = is.setQingfenShenheSubmit(lineName);
				System.out.println("装箱审核："+str);
				if(str.equals("true")){    //修改成功
					handle.sendEmptyMessage(1);
				}else{
					handle.sendEmptyMessage(-1);            //修改失败
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				handle.sendEmptyMessage(-4);//超时
			}catch (Exception e) {
				e.printStackTrace();
				handle.sendEmptyMessage(-1);//异常提交|失败提交
			}	
		}
		
	}

	/**
	 * 从新获取数据
	 * @author Administrator
	 */
//	class HuoqThread extends Thread{
//		
//		@Override
//		public void run() {
//			super.run();
//			
//			System.out.println("jhdId:计划单:"+jhdId);
//			ICleaningManagementService is = new ICleaningManagementService();
//			try {
//				String str = is.getQinglinzongjindu(jhdId);
//				if(!"".equals(str)){
//					tables = Table.doParse(str);
//					S_application.getApplication().ywcjhdlist = tables[1].get("renwudan").getValues();
//					S_application.getApplication().ywcjhdzmlist = tables[1].get("zuming").getValues();
//					S_application.getApplication().ywcjhdzzbhlist = tables[1].get("zzxbianhao").getValues();
//					S_application.getApplication().wwcjhdlist = tables[2].get("renwudan").getValues();
//					S_application.getApplication().wwcjhdzmlist = tables[2].get("zuming").getValues();	
//					System.out.println("获取了吗?");
//					handle.sendEmptyMessage(3);
//				}
//			} catch (SocketTimeoutException e) {
//				e.printStackTrace();
//				handle.sendEmptyMessage(4);
//			}catch (Exception e) {
//				e.printStackTrace();
//				handle.sendEmptyMessage(5);
//			}
//		}
//	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			ZzXiangKdSmiaoActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
