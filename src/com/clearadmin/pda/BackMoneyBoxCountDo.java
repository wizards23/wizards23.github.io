package com.clearadmin.pda;

import hdjc.rfid.operator.IRFID_Device;
import hdjc.rfid.operator.RFID_Device;

import com.application.GApplication;
import com.clearadmin.biz.GetRecycleCashboxCheckInfoBiz;
import com.clearadmin.biz.RecycleCashboxCheckConfirmBiz;
import com.clearadmin.biz.RecycleCashboxCheckDetailBiz;
import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.imple.getnumber.AddMoneygetNum;
import com.imple.getnumber.BackCleanBox;
import com.loginsystem.biz.SystemLoginBiz;
import com.main.pda.Scan;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.Abnormal;
import com.messagebox.ResultMsg;
import com.messagebox.Runing;
import com.moneyboxadmin.pda.BankDoublePersonLogin;
import com.out.admin.pda.OrderWork;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BackMoneyBoxCountDo extends Activity{
	//回收钞箱清点
	
	Button suer;  //确定
	ImageView back;  //返回
	TextView plan;  //业务单编号
	TextView boxnum; //钞箱编号
	TextView notclearbox; //钞箱编号
	EditText money;  //余额
	EditText fmoney;  //废钞箱余额
	
	String cashboxNum;  //钞箱编号
	String orderNum;   //业务单编号
	String balance1;   //钞箱编号
	String balance2;   //钞箱编号
	//String userId;     //用户编号
	String planNum;
	Bundle bundle;   //
	TextView nowbox;
	
	public static boolean hadfindbox;
	
	private RFID_Device rfid;
	RFID_Device getRfid(){
		if(rfid==null){
		rfid = new RFID_Device();
		}
		return rfid;
	}
	
	View.OnClickListener getboxclick;
	View.OnClickListener click;
	String cashbox;  //后台返回的钞箱编号
	private ManagerClass managerClass;
	
	GetRecycleCashboxCheckInfoBiz recycleCashboxCheckInfo;		
	GetRecycleCashboxCheckInfoBiz getRecycleCashboxCheckInfo() {
		if(recycleCashboxCheckInfo==null){
			recycleCashboxCheckInfo = new GetRecycleCashboxCheckInfoBiz();
		}
		return recycleCashboxCheckInfo;
	}
						
		RecycleCashboxCheckConfirmBiz recycleCashboxCheckConfirm;
	    RecycleCashboxCheckConfirmBiz getRecycleCashboxCheckConfirm() {
			return recycleCashboxCheckConfirm=recycleCashboxCheckConfirm==null?new RecycleCashboxCheckConfirmBiz():recycleCashboxCheckConfirm;
		}
	    SystemLoginBiz systemLogin;
	    SystemLoginBiz getSystemLogin() {
			return systemLogin=systemLogin==null?new SystemLoginBiz():systemLogin;
		}
	    
	    BackCleanBox backCleanBox;    
	    BackCleanBox getBackCleanBox() {
			return backCleanBox=backCleanBox==null?new BackCleanBox():backCleanBox;
		}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//禁止休睡眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.backbox_count_do);
		
		 //全局异常处理
		// CrashHandler.getInstance().init(this);
		
		managerClass = new ManagerClass();
		suer = (Button) findViewById(R.id.count_suer);
		back = (ImageView) findViewById(R.id.countback);
		plan = (TextView) findViewById(R.id.count_planNum);
		boxnum = (TextView) findViewById(R.id.count_boxnum);
		money = (EditText) findViewById(R.id.count_money);
		fmoney = (EditText) findViewById(R.id.count_fmoney);
		notclearbox = (TextView) findViewById(R.id.notclear_box);
		nowbox = (TextView) findViewById(R.id.nowclearBox);
		
		
		//未清点数量
		try {
		notclearbox.setText(RecycleCashboxCheckDetailBiz.list.size()+"");	
		} catch (Exception e) {
		//时间比较紧，还没测，先try
		}
		
		
		bundle = getIntent().getExtras();
		if(bundle!=null){
		planNum = bundle.getString("number");  //业务单编号
		}
		
		suer.setOnTouchListener(new Touch());
		back.setOnTouchListener(new Touch());
		
		//清点重试单击事件
		click = new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
			managerClass.getAbnormal().remove();
			managerClass.getRuning().runding(BackMoneyBoxCountDo.this,"正在清点...");
			//清点操作,参数：钞箱编号，业务单编号，余额1，余额2，用户编号
			getRecycleCashboxCheckConfirm().getCheckConfirmResult(cashboxNum, orderNum, balance1, balance2, BankDoublePersonLogin.userid1,BankDoublePersonLogin.userid2);				
			}
		};
		
		
		//回收钞箱清点-确定
		getRecycleCashboxCheckConfirm().handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				managerClass.getRuning().remove();
				switch(msg.what){				
				case 0:
					 managerClass.getSureCancel().makeSuerCancel(BackMoneyBoxCountDo.this,"清点失败！", new View.OnClickListener() {					
							@Override
							public void onClick(View arg0) {
							managerClass.getSureCancel().remove();
							}
						},true);															
					break;
				case 1:
					hadfindbox=false;
					suer.setEnabled(false);
					suer.setBackgroundResource(R.drawable.button_gray);
				    BackCleanBox.map.remove(cashbox);  //移除成功清点的箱子 
				    backCleanBox.yiQingfenList.add(cashbox);
				    notclearbox.setText(BackCleanBox.map.size()+"");
				    Log.i("BackCleanBox.map", BackCleanBox.map.size()+"");
				    if(BackCleanBox.map.size()==0){
					
				    managerClass.getSureCancel().makeSuerCancel(BackMoneyBoxCountDo.this, "所有钞算清点完毕",new View.OnClickListener() {					
					@Override
					public void onClick(View arg0) {
					  managerClass.getSureCancel().remove();
					  new GolbalUtil().gotoActivity(BackMoneyBoxCountDo.this, ClearManager.class, null, 0);
					
					  }
				    }, true);
					
				    }else{
					managerClass.getGolbalView().toastShow(BackMoneyBoxCountDo.this, "清点成功！");				
					managerClass.getSureCancel().remove();
					money.setText("0");
					fmoney.setText("0");
					boxnum.setText("");
					//打开扫描
					getRfid().open_a20();
				    }
										
					break;
				case -1:
					managerClass.getAbnormal().timeout(BackMoneyBoxCountDo.this, "连接异常，重新连接？", click);															
					break;
				case -4:
					managerClass.getAbnormal().timeout(BackMoneyBoxCountDo.this, "连接超时，重新连接？", click);															
					break;
				
				}
			}			
		};
		
		
		//回收清点扫描箱子通知
		BackCleanBox.handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				cashbox= msg.getData().getString("boxnum");	 //钞箱编号
				nowbox.setText(cashbox);
				switch(msg.what){
				case 1:
					hadfindbox=true;
					//关闭 扫描
					getRfid().close_a20();				
					suer.setEnabled(true);
					suer.setBackgroundResource(R.drawable.buttom_selector_bg);			    
					managerClass.getRuning().runding(BackMoneyBoxCountDo.this,"正在获取信息...");								
					//获取钞箱信息
					getRecycleCashboxCheckInfo().getrecycleCashboxCheckInfo(planNum, cashbox, BankDoublePersonLogin.userid1, BankDoublePersonLogin.userid2, GApplication.user.getOrganizationId());	
										
					break;
				}
			}
			
		};
		
		//获取钞箱信息重试单击事件
		getboxclick = new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				managerClass.getRuning().runding(BackMoneyBoxCountDo.this, "正在获取信息...");
				//获取钞箱信息
				getRecycleCashboxCheckInfo().getrecycleCashboxCheckInfo(planNum, cashbox, BankDoublePersonLogin.userid1, BankDoublePersonLogin.userid2, GApplication.user.getOrganizationId());	
			}
		};
		
		//扫描钞箱后台获取数据通知
		getRecycleCashboxCheckInfo().handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				managerClass.getRuning().remove();
				super.handleMessage(msg);
				
				switch(msg.what){
				case 1:
					plan.setText(getRecycleCashboxCheckInfo().box.getBrand());
					//money.setText(getRecycleCashboxCheckInfo().box.getMoney());
					fmoney.setText("0");
					boxnum.setText(getRecycleCashboxCheckInfo().box.getNum());
					break;
					
				case 0:
					Toast.makeText(BackMoneyBoxCountDo.this, "没有该信息", Toast.LENGTH_SHORT).show();
					break;					
				case -1:
					managerClass.getAbnormal().timeout(BackMoneyBoxCountDo.this, "连接异常！", getboxclick);
					break;	
				case -4:
					managerClass.getAbnormal().timeout(BackMoneyBoxCountDo.this, "连接超时！", getboxclick);
					break;	
				}				
			}			
		};
		
		
	}

	@Override
	protected void onPause() {
		super.onPause();		
		getRfid().close_a20();
		hadfindbox=false;
	}



	@Override
	protected void onStart() {
		super.onStart();
		getRfid().addNotifly(new BackCleanBox());
		getRfid().open_a20();
		hadfindbox=false;
		
	}

	class Touch implements OnTouchListener{
		@Override
		public boolean onTouch(View view, MotionEvent even) {
			//按下的时候
			if(MotionEvent.ACTION_DOWN==even.getAction()){
				switch(view.getId()){
				case R.id.countback:
					back.setBackgroundResource(R.drawable.back_cirle_press);
					break;
				case R.id.count_suer:
					suer.setBackgroundResource(R.drawable.buttom_select_press);
					break;
					
				}
			}
			
			//手指松开的时候
			if(MotionEvent.ACTION_UP==even.getAction()){
				switch(view.getId()){
				//返回
				case R.id.countback:
					back.setBackgroundResource(R.drawable.back_cirle);
					getRfid().close_a20();
					BackMoneyBoxCountDo.this.finish();
					break;
				//确定
				case R.id.count_suer:
					suer.setBackgroundResource(R.drawable.buttom_selector_bg);
					//清点操作,参数：钞箱编号，业务单编号，余额1，余额2，用户编号
					cashboxNum=boxnum.getText().toString();
					orderNum = plan.getText().toString();
					balance1 = money.getText().toString();
					balance2 = fmoney.getText().toString();
					managerClass.getRuning().runding(BackMoneyBoxCountDo.this,"正在清点...");
					getRecycleCashboxCheckConfirm().getCheckConfirmResult(cashboxNum, orderNum, balance1, balance2, BankDoublePersonLogin.userid1,BankDoublePersonLogin.userid2);
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
				case R.id.countback:
					back.setBackgroundResource(R.drawable.back_cirle);
					break;
				case R.id.count_suer:
					suer.setBackgroundResource(R.drawable.buttom_selector_bg);
					break;
					
				}
				GolbalUtil.ismover=0;
			}
			return true;
		}

	}
	


	@Override
	public void onBackPressed() {
		super.onBackPressed();
		getRfid().close_a20();
	}
	
	
	
}
