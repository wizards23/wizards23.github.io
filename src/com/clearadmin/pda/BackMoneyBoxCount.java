package com.clearadmin.pda;

import com.clearadmin.biz.AddMoneyConfirmBiz;
import com.clearadmin.biz.RecycleCashboxCheckDetailBiz;
import com.entity.BoxDetail;
import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.loginsystem.biz.SystemLoginBiz;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.Abnormal;
import com.messagebox.ResultMsg;
import com.messagebox.Runing;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BackMoneyBoxCount extends Activity implements OnTouchListener{

	//回收钞箱清点明细
	Button btncount;
	View.OnClickListener click;
	String plan;   //业务单编号
	Bundle bundle;
	Ad ad;  //适配器
	ListView listview;  //
	int count = 0;
	private ManagerClass managerClass;
	
	//钞箱明细
	private RecycleCashboxCheckDetailBiz recycleCashboxCheckDetail;	
	 RecycleCashboxCheckDetailBiz getRecycleCashboxCheckDetail() {
		return recycleCashboxCheckDetail=recycleCashboxCheckDetail==null?new RecycleCashboxCheckDetailBiz():recycleCashboxCheckDetail;
	}
		
		private AddMoneyConfirmBiz addMoneyConfirm;
		 AddMoneyConfirmBiz getAddMoneyConfirm() {
			return addMoneyConfirm=addMoneyConfirm==null?new AddMoneyConfirmBiz():addMoneyConfirm;
		}

		private SystemLoginBiz systemLogin;
		 SystemLoginBiz getSystemLogin() {
			return systemLogin=systemLogin==null?new SystemLoginBiz():systemLogin;
		}
		 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.back_countbox_detail);
		managerClass = new ManagerClass();
		bundle = getIntent().getExtras();
		//获取业务单编号
		plan = bundle.getString("number");
		
		 //全局异常处理
		// CrashHandler.getInstance().init(this);
		
		btncount = (Button) findViewById(R.id.countdo);
		listview = (ListView) findViewById(R.id.listview_backcount);
		
		btncount.setOnTouchListener(this);
		
		//重试单击整个年
		click = new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
			managerClass.getAbnormal().remove();
			managerClass.getRuning().runding(BackMoneyBoxCount.this,"正在获取钞箱明细...");
			//开启进程获取数据，参数为：业务单编号
			getRecycleCashboxCheckDetail().getboxDetailList(plan);	
			}
		};
		
		
		//handler通知
		getRecycleCashboxCheckDetail().handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				managerClass.getRuning().remove(); 
				super.handleMessage(msg);
				
				switch(msg.what){
				case 1:
					count++;
					ad = new Ad();
					listview.setAdapter(ad);
					break;
				case -1:
					managerClass.getAbnormal().timeout(BackMoneyBoxCount.this, "连接异常，重新连接？", click); 
					break;
				case 0:
					 Toast.makeText(BackMoneyBoxCount.this, "暂无数据",Toast.LENGTH_LONG).show();
					break;
				case -4:
					managerClass.getAbnormal().timeout(BackMoneyBoxCount.this, "连接超时，重新连接？", click); 
					break;
				
				}
				
		
			}
			
		};
		
		
		
	}
	@Override
	protected void onStart() {
		super.onStart();
		if(count==0){
		//提示
		managerClass.getRuning().runding(this,"正在获取钞箱明细...");
		    }
		//开启进程获取数据，参数为：业务单编号
		getRecycleCashboxCheckDetail().getboxDetailList(plan);	
	}

	@Override
	public boolean onTouch(View view, MotionEvent even) {
		//按下的时候
		if(MotionEvent.ACTION_DOWN==even.getAction()){
			btncount.setBackgroundResource(R.drawable.buttom_select_press);
		}
		
		//手指松开的时候
		if(MotionEvent.ACTION_UP==even.getAction()){
			switch(view.getId()){
			//开始清点
			case R.id.countdo:
				btncount.setBackgroundResource(R.drawable.buttom_selector_bg);		
				managerClass.getGolbalutil().gotoActivity(BackMoneyBoxCount.this, BackMoneyBoxCountDo.class, bundle, GolbalUtil.ismover);
				break;
				
			}
			managerClass.getGolbalutil().ismover=0;
		}
		//手指移动的时候
		if(MotionEvent.ACTION_MOVE==even.getAction()){
			managerClass.getGolbalutil().ismover++;
		}
		//意外中断事件取消
		if(MotionEvent.ACTION_CANCEL==even.getAction()){
			managerClass.getGolbalutil().ismover=0;
			btncount.setBackgroundResource(R.drawable.buttom_selector_bg);
		}
		
		
		return true;
	}
	
	
	//适配器
	class Ad extends BaseAdapter{

		@Override
		public int getCount() {
			return getRecycleCashboxCheckDetail().list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return getRecycleCashboxCheckDetail().list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			View v = view;
			ViewHoler holer = null;
			if(v==null){
				v = GolbalView.getLF(BackMoneyBoxCount.this).inflate(R.layout.box_item, null);
				holer = new ViewHoler();
				holer.brand = (TextView) v.findViewById(R.id.boxcount_brand);
				holer.boxNum = (TextView) v.findViewById(R.id.boxcount_boxnum);
				v.setTag(holer);
			}else{
				holer = (ViewHoler) v.getTag();
			}
			
			BoxDetail box = new BoxDetail();
			box = (BoxDetail) getItem(arg0);
			holer.brand.setText(box.getBrand());
			holer.boxNum.setText(box.getNum());
			
			
			return v;
		}
		
	}
	
	static class ViewHoler{
		TextView brand;
		TextView boxNum;
	} 
	
}
