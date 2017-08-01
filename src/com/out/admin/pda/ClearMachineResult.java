package com.out.admin.pda;

import com.application.GApplication;
import com.clear.machine.pda.ClearMachineLogin;
import com.entity.ClearMoney;
import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.manager.classs.pad.ManagerClass;
import com.out.biz.ClearMachineIngBiz;
import com.out.biz.GetCorpInfoByIdBiz;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ClearMachineResult extends Activity implements OnTouchListener {

	Button clearmachine;   //清机操作

	private ManagerClass managerClass;		
	ListView listivew;
	TextView name;  //机构名称
	TextView corpid; //机构编号
	TextView num;   //柜员编号
	Bundle bundle;
	ImageView back;
	
	GetCorpInfoByIdBiz corpInfoByIdBiz;
	GetCorpInfoByIdBiz getCorpInfoByIdBiz(){
		if(corpInfoByIdBiz==null){
			corpInfoByIdBiz = new GetCorpInfoByIdBiz();
		}
		return corpInfoByIdBiz;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		managerClass = new ManagerClass();
		setContentView(R.layout.clear_atm_result);
		
		name = (TextView) findViewById(R.id.clear_result_address);
		corpid = (TextView) findViewById(R.id.clear_restlu_corpid);
		num = (TextView) findViewById(R.id.clear_result_moneyNum);
		back = (ImageView) findViewById(R.id.clearresult_back);
		 //全局异常处理
		// CrashHandler.getInstance().init(this);
		
		clearmachine = (Button) findViewById(R.id.sure_clearresult);
		clearmachine.setOnTouchListener(this);		
		listivew = (ListView) findViewById(R.id.listview_clearresult);
		back.setOnTouchListener(this);
		
		if(ClearMachineIngBiz.list!=null && ClearMachineIngBiz.list.size()>0){
		    listivew.setAdapter(new Ad());
		}
		
		
		//清机加钞获取机构信息和柜员编号
		getCorpInfoByIdBiz().handler = new Handler(){			
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				managerClass.getRuning().remove();
				
				switch(msg.what){				
				case 1:
					bundle = msg.getData();	
					String params = bundle.getString("params");
					String[] arr = params.split(";");
					//name.setText(arr[0]);
					name.setText(WebSiteJoin.spinnerText);
					num.setText(arr[1]);
					corpid.setText(WebSiteJoin.corp);								
					Log.i("ClearMachineIngBiz.list.size",ClearMachineIngBiz.list.size()+"");
					break;
				case -1:
					managerClass.getAbnormal().timeout(ClearMachineResult.this, "查找异常！", new View.OnClickListener() {					
						@Override
						public void onClick(View arg0) {
							 managerClass.getAbnormal().remove();
							 managerClass.getRuning().runding(ClearMachineResult.this, "正在获取机构信息...");
							 //getCorpInfoByIdBiz().getCorpInfoById(GApplication.getApplication().user.getOrganizationId());
							 //revised by zhangxuewei
							 getCorpInfoByIdBiz().getCorpInfoById(GApplication.getApplication().taskCorpId);
						}
					});	
					break;
				case -4:
					managerClass.getAbnormal().timeout(ClearMachineResult.this, "查找超时！", new View.OnClickListener() {					
						@Override
						public void onClick(View arg0) {
							 managerClass.getAbnormal().remove();
							 managerClass.getRuning().runding(ClearMachineResult.this, "正在获取机构信息...");
							 //revised by zhangXueWei
							 //getCorpInfoByIdBiz().getCorpInfoById(GApplication.getApplication().user.getOrganizationId());	
							 getCorpInfoByIdBiz().getCorpInfoById(GApplication.getApplication().taskCorpId);
							 
						}
					});	
					break;
				case 0:
					managerClass.getSureCancel().makeSuerCancel(ClearMachineResult.this,"查找机构信息失败",new View.OnClickListener() {					
					@Override
					public void onClick(View arg0) {
					managerClass.getSureCancel().remove();	
							}
						}, true);
					break;
				
				}
				
				
				
			}
			
		};
		
	}

	
	
	
	@Override
	protected void onStart() {
		super.onStart();
		String numText = num.getText()+"";
		if(!num.equals("")){
			managerClass.getRuning().runding(this,"正在获取机构信息...");
//			getCorpInfoByIdBiz().getCorpInfoById(GApplication.getApplication().user.getOrganizationId());	
			getCorpInfoByIdBiz().getCorpInfoById(GApplication.getApplication().taskCorpId);
		}	   		
	}

	//触摸事件
	@Override
	public boolean onTouch(View view, MotionEvent even) {
		
		//按下的时候
		if(MotionEvent.ACTION_DOWN==even.getAction()){
			
			switch(view.getId()){
			//清机加钞结果确定
			case R.id.sure_clearresult: 
				clearmachine.setBackgroundResource(R.drawable.buttom_select_press);
				break;
			case R.id.clearresult_back:
				back.setImageResource(R.drawable.back_cirle_press);
				
				break;
				
			}
		}
		
		//手指松开的时候
		if(MotionEvent.ACTION_UP==even.getAction()){
			switch(view.getId()){
			//清机加钞结果确定
			case R.id.sure_clearresult: 
				clearmachine.setBackgroundResource(R.drawable.buttom_selector_bg);				
				managerClass.getGolbalutil().gotoActivity(ClearMachineResult.this, OrderWork.class, null, GolbalUtil.ismover);				
				//GApplication.getApplication().exit(false);
				//ClearMachineResult.this.finish();
				break;
			case R.id.clearresult_back:
				back.setImageResource(R.drawable.back_cirle);
				ClearMachineResult.this.finish();
				break;
				
			}
			GolbalUtil.ismover=0;
		}
		//手指移动的时候
		if(MotionEvent.ACTION_MOVE==even.getAction()){
			GolbalUtil.ismover++;
		}
		//意外中断事件取消
		if(MotionEvent.ACTION_CANCEL==even.getAction()){
			switch(view.getId()){
			//清机加钞结果确定
			case R.id.sure_clearresult: 
				clearmachine.setBackgroundResource(R.drawable.buttom_select_press);
				break;
			case R.id.clearresult_back:
				back.setImageResource(R.drawable.back_cirle_press);
				
				break;
				
			}
			GolbalUtil.ismover=0;
		}
		return true;
	}
	
	class Ad extends BaseAdapter{

		@Override
		public int getCount() {
			return ClearMachineIngBiz.list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return ClearMachineIngBiz.list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			View v = view;
			ViewHolder holder;
			if(v==null){
			   holder = new ViewHolder();
			   v = GolbalView.getLF(ClearMachineResult.this).inflate(R.layout.clear_result_item, null);
			   holder.address = (TextView) v.findViewById(R.id.clearresult_address);
			   holder.boxNum = (TextView) v.findViewById(R.id.clearresult_boxnum);
			   holder.result = (TextView) v.findViewById(R.id.clearresult_result);
			   v.setTag(holder);
			}else{
			   holder = (ViewHolder) v.getTag();
			}
			   
			ClearMoney money =(ClearMoney) getItem(arg0);
			holder.address.setText(money.getAtmNum());
			holder.boxNum.setText(money.getBoxNum());
			if(money.getState().equals("1")){
			  holder.result.setText("完成");	
			}else{
			  holder.result.setText("未完成");	
			}
			
			return v;
		}
		
	}
	
	static class ViewHolder{
		TextView address;
		TextView boxNum;
		TextView result;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}
	
	
}
