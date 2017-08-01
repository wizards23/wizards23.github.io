package com.clearadmin.pda;

import java.util.Map;
import java.util.Map.Entry;

import hdjc.rfid.operator.IRFID_Device;
import hdjc.rfid.operator.RFID_Device;

import com.application.GApplication;
import com.clearadmin.biz.AddMoneyConfirmBiz;
import com.clearadmin.biz.CashboxAddMoneyDetailBiz;
import com.clearadmin.biz.GetAddMoneySumBiz;
import com.entity.BoxDetail;
import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.imple.getnumber.AddMoneygetNum;
import com.imple.getnumber.BackCleanBox;
import com.imple.getnumber.GetMoneyNum;
import com.loginsystem.biz.SystemLoginBiz;
import com.main.pda.Scan;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.Abnormal;
import com.messagebox.ResultMsg;
import com.messagebox.Runing;
import com.moneyboxadmin.pda.BankDoublePersonLogin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//钞箱加钞操作


public class ClearAddMoneyOutDo extends Activity{
	//获取钞箱明细
	private CashboxAddMoneyDetailBiz cashboxAddMoneyDetail;		
	public CashboxAddMoneyDetailBiz getCashboxAddMoneyDetail() {
		return cashboxAddMoneyDetail=cashboxAddMoneyDetail==null?new CashboxAddMoneyDetailBiz():cashboxAddMoneyDetail;
	}
	
	//筛选数据
	private AddMoneygetNum addMoneygetNum;
	 AddMoneygetNum getAddMoneygetNum() {
		return addMoneygetNum=addMoneygetNum==null?new AddMoneygetNum():addMoneygetNum;
	}

	//获取钱捆编号
	private GetMoneyNum getMoneyNum;		
	 GetMoneyNum getGetMoneyNum() {
		return getMoneyNum=getMoneyNum==null?new GetMoneyNum():getMoneyNum;
	}
	//
	private AddMoneyConfirmBiz addMoneyConfirm;
	 AddMoneyConfirmBiz getAddMoneyConfirm() {
		return addMoneyConfirm=addMoneyConfirm==null?new AddMoneyConfirmBiz():addMoneyConfirm;
	}

	private SystemLoginBiz systemLogin;
	 SystemLoginBiz getSystemLogin() {
		return systemLogin=systemLogin==null?new SystemLoginBiz():systemLogin;
	}
	private GetAddMoneySumBiz getAddMoneySum;
	GetAddMoneySumBiz getGetAddMoneySum() {
		return getAddMoneySum=getAddMoneySum==null?new GetAddMoneySumBiz():getAddMoneySum;
	}
	private ManagerClass managerClass;
	private RFID_Device rfid;
	RFID_Device getRfid(){
		if(rfid==null){
			rfid = new RFID_Device();
		}
		return rfid;
	}
	
	
	TextView showBoxNum;   //显示当前扫描到的钞箱编号
	TextView domsg;  //操作提示
	TextView planNum; //计划编号
	TextView boxNum;  //钞箱编号
	TextView money;   //金额
	TextView Toal;   //扫描总金额
	ListView listView;  //
	String plan;    //计划编号
	Bundle bundle;
	String boxnum_text;  //钞箱编号
	int toal;   //返回的总金额
	Ad ad;  //适配器
	ImageView back;
	Button btn;  //确定
	//BoxDetail box; //钞箱
	String bagNum1="";  //钱捆编号
	String bagNum2="";  //钱捆编号
	String bagNum3="";  //钱捆编号
	String bagNum4="";  //钱捆编号
	String bagNum5="";  //钱捆编号
	String bagNum6="";  //钱捆编号
	double moneyToal = 0;//钱捆总金额
	View.OnClickListener click;
	Bundle b;
	TextView waitaddbox;
	private boolean first;  //只执行一次
	
	
	AddMoneygetNum scanbox = new AddMoneygetNum();  //钞箱编号扫描类
	GetMoneyNum scanMoneyNum=new GetMoneyNum();  //钱捆编号扫描类
	View.OnClickListener clickgetbox;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//禁止休睡眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.addmomey_out_detail);
		
		managerClass = new ManagerClass();
		managerClass.getGolbalView().Init(this);
		first = true;
		
		
		 //全局异常处理
		//CrashHandler.getInstance().init(this);		
		planNum = (TextView) findViewById(R.id.addmoneyout_plan);
		boxNum = (TextView) findViewById(R.id.addmoneyout_planNum);
		money = (TextView) findViewById(R.id.addmoenyout_money);
		listView = (ListView) findViewById(R.id.listview_addmoney);
		back = (ImageView) findViewById(R.id.addmoney_back);
		btn = (Button) findViewById(R.id.addmoney_suer_out);
		Toal = (TextView) findViewById(R.id.addmoneytoal);
		domsg = (TextView) findViewById(R.id.domsg);
		waitaddbox = (TextView) findViewById(R.id.waiteaddbox);
		showBoxNum = (TextView) findViewById(R.id.showboxnum);
		
		bundle = getIntent().getExtras();
		plan = bundle.getString("number");	 //计划编号	
		planNum.setText(plan);
		
		btn.setOnTouchListener(new Touch());
		back.setOnTouchListener(new Touch());
		
		waitaddbox.setText(AddMoneygetNum.map.size()+"");
		
		//一、首先扫描钞箱编号，钞箱扫描通知
		AddMoneygetNum.handler = new Handler(){
			@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//钞箱扫描返回的编号
			b = msg.getData();			
			if(b!=null){
			   boxnum_text = b.getString("num");			   
			}else{
			return;
			}
			
		    //显示当前扫描到的钞箱编号
			showBoxNum.setText(boxnum_text);			
			switch(msg.what){
			case 1:
				//停止返回扫描数据
				getRfid().stop_a20();		
				Log.i("boxnum_text",boxnum_text+"");
			    managerClass.getRuning().runding(ClearAddMoneyOutDo.this, "正在获取箱钞信息...");
				//开启线程，获取钞箱的信息
				getGetAddMoneySum().getupdateAndGetAddMoneySum(plan, boxnum_text,BankDoublePersonLogin.userid1 , BankDoublePersonLogin.userid2, GApplication.user.getOrganizationId());	
				
				break;
			case 0:
				
				break;
			case -1:
	
				break;
				
		    //所有钞箱已扫描完成
			case 3:
				getRfid().stop_a20();
				break;
				
			case 2:
				
				break;
				
			}
			  }				
			
		    };
				
		    
		
		//取得箱钞信息，更新信息重试事件
		clickgetbox = new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				managerClass.getRuning().runding(ClearAddMoneyOutDo.this, "正在获取箱钞信息...");
				getGetAddMoneySum().getupdateAndGetAddMoneySum(plan, boxnum_text,BankDoublePersonLogin.userid1 , BankDoublePersonLogin.userid2, GApplication.user.getOrganizationId());
			}
		};
		
		
		//获得钞箱信息（编号，金额），更新信息
		getGetAddMoneySum().handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				managerClass.getRuning().remove();
				
				switch(msg.what){
				case 1:
					//当前钞箱金额
					 moneyToal=Double.parseDouble(GetAddMoneySumBiz.box.getMoney());					
					 money.setText((moneyToal/10000)+"万");
					 
					//钞箱编号和品牌
					 boxNum.setText(GetAddMoneySumBiz.box.getNum()+"    "+GetAddMoneySumBiz.box.getBrand());

					//增加二维码接口通知
					getRfid().addNotifly(scanMoneyNum);
					//打开二维码扫描
					managerClass.getRfid().scanOpen(); 
					
					domsg.setText("请扫描钱捆冠字编号");		
										
					break;
				case 0:
					 managerClass.getSureCancel().makeSuerCancel(ClearAddMoneyOutDo.this,"没有这个钞箱的记录",new View.OnClickListener() {					
						@Override
						public void onClick(View arg0) {
						managerClass.getSureCancel().remove();	
						//开始扫描
						getRfid().start_a20();
						}
					}, true);
					
					
					break;
				case -1:
					managerClass.getAbnormal().timeout(ClearAddMoneyOutDo.this,"连接异常!", clickgetbox);
					break;
				case -4:
					managerClass.getAbnormal().timeout(ClearAddMoneyOutDo.this,"连接超时!", clickgetbox);
					break;
				}												
				
			}			
		};	
		
		
	
		
		
		//钞捆信息扫描返回更新
		getGetMoneyNum().handler = new Handler(){
			Bundle bundlemoney;
			@Override
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				
				switch(msg.what){
				case 1:
					if(ad==null){
						ad = new Ad();
						listView.setAdapter(ad);
					}else{
						ad.notifyDataSetChanged();	
					}
					
					bundlemoney = msg.getData();
					if(bundlemoney!=null){
						//接收统计扫描钞捆的总金额
						toal = bundlemoney.getInt("moneyToal");
					}	
					
					//当前扫描总金额
					Toal.setText(toal/10000+"万"); 
					//如果扫描的总金额和钞箱总金额相等
					if(moneyCount(toal)){
					//  managerClass.getIrfidmsg().removeV();     
					//设置确定可用
					btn.setBackgroundResource(R.drawable.buttom_selector_bg);
					btn.setEnabled(true);
					}
					break;
				
				}								
			}
			
		};
		
		//出库重试事件
		click = new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
			 managerClass.getAbnormal().remove();
			 managerClass.getRuning().runding(ClearAddMoneyOutDo.this, "正在执行加钞操作...");
			//开始加钞
				getAddMoneyConfirm().addMoneyConfrim(plan, GetAddMoneySumBiz.box.getNum(),
				BankDoublePersonLogin.userid1,BankDoublePersonLogin.userid2, GApplication.user.getOrganizationId(),
				bagNum1, bagNum2, bagNum3, bagNum4, bagNum5, bagNum6);
				 
			}
		};
		
		
		//加钞确定
		getAddMoneyConfirm().handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
			
				managerClass.getRuning().remove();
				super.handleMessage(msg);
								
				switch(msg.what){
				case 1:
					 //设置确定不可用
					 btn.setBackgroundResource(R.drawable.button_gray);
					 btn.setEnabled(false);
					 
					 //加钞成功后，把加钞过的项从集合中移除					
					 getAddMoneygetNum().map.remove(boxnum_text);
					 getAddMoneygetNum().yiQingfenList.add(boxnum_text);
					 
					 waitaddbox.setText(AddMoneygetNum.map.size()+"");
					 Log.i("boxnum_text", boxnum_text+"");
					 Log.i("map",  getAddMoneygetNum().map.size()+"");
					 //加钞成功提示
					 managerClass.getSureCancel().makeSuerCancel(ClearAddMoneyOutDo.this,"加钞成功",new View.OnClickListener() {						
						@Override
						public void onClick(View arg0) {
						managerClass.getSureCancel().remove();
						if(getAddMoneygetNum().map.size()>0){	
							//开始扫描
							getRfid().start_a20();						
						}
						   
						}
						
						}, true);
					 
					 //加钞成功后，进行信息清空处理------------------
					 //清空信息
					 money.setText("");
					 boxNum.setText("");
					 Toal.setText("");
					 //成功后，重置总金额
					 getGetMoneyNum().money = 0;
					 //清空listview
					 getGetMoneyNum().list.clear();
					 
					 //当所有钞相都扫描完成后
					 if(getAddMoneygetNum().map.size()==0){
						 //清空信息
						 money.setText("");
						 boxNum.setText("");
						 Toal.setText("");
						 //清空listview
						 getGetMoneyNum().list.clear();
						 listView.setAdapter(ad);
						 //关闭扫描
						 getRfid().close_a20();
						 //关闭一维码扫描
						 managerClass.getRfid().scanclose(); 						
						 managerClass.getSureCancel().makeSuerCancel(ClearAddMoneyOutDo.this,"所有钞箱扫描完毕！", new View.OnClickListener() {				
								@Override
								public void onClick(View arg0) {
									managerClass.getSureCancel().remove();
									new GolbalUtil().gotoActivity(ClearAddMoneyOutDo.this, ClearManager.class, null, 0);
								}
							}, true);
						 
					  }else{
						listView.setAdapter(new Ad());	
						domsg.setText("请扫描钞箱");
						//开始扫描
					    getRfid().start_a20();
						//添加钞箱扫描通知
					    managerClass.getRfid().addNotifly(scanbox);
						listView.setAdapter(ad);
					    
					  }
					break;
					
				case 0:
					//加钞提示
					managerClass.getSureCancel().makeSuerCancel(ClearAddMoneyOutDo.this,"加钞失败",new View.OnClickListener() {					
						@Override
						public void onClick(View arg0) {
						managerClass.getSureCancel().remove();	
						
						/**
						 * 2015-7-13 增
						 */
						//开始扫描
					    getRfid().start_a20();
						//添加钞箱扫描通知
					    managerClass.getRfid().addNotifly(scanbox);
						
						
						}
					}, true); 
					break;
					
				case -1:
					 managerClass.getAbnormal().timeout(ClearAddMoneyOutDo.this,"连接异常，重试？", click);
					 /**
					  * 2015-7-13 增
					  */
					   //开始扫描
					   getRfid().start_a20();
					   //添加钞箱扫描通知
					   managerClass.getRfid().addNotifly(scanbox);
					break;
				case -4:
					 managerClass.getAbnormal().timeout(ClearAddMoneyOutDo.this,"连接超时，重试？", click);
					 /**
					  * 2015-7-13 增
					  */
					   //开始扫描
					   getRfid().start_a20();
					   //添加钞箱扫描通知
					   managerClass.getRfid().addNotifly(scanbox);
					break;
				}
				
																				
			}
			
		};
		
		
	}
	
	//总金额比对
	public boolean moneyCount(int money){
		if(moneyToal==money){
			return true;
		}
		return false;
	}
	
	//适配器
	class Ad extends BaseAdapter{

		@Override
		public int getCount() {
			return getGetMoneyNum().list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return getGetMoneyNum().list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			View v = view;
			final ViewHolder holder;
			if(v==null){
				holder = new ViewHolder();
				v =GolbalView.getLF(ClearAddMoneyOutDo.this).inflate(R.layout.money_num_item,null);
				holder.moneyNum = (TextView) v.findViewById(R.id.money_num_text);
				holder.del = (TextView) v.findViewById(R.id.clear_del_item);
				holder.cancel = (TextView) v.findViewById(R.id.clear_cancel_item);
				holder.lmenu = (LinearLayout)v.findViewById(R.id.lclear_item);
				holder.moneyItem = (RelativeLayout) v.findViewById(R.id.money_item_id);
				
				v.setTag(holder);
			}else{
				holder = (ViewHolder) v.getTag();	
			}
			
			holder.moneyNum.setText(getItem(arg0).toString());	
			
			//长按事件
			v.setOnLongClickListener(new View.OnLongClickListener() {				
				@Override
				public boolean onLongClick(View arg0) {
					holder.lmenu.setVisibility(View.VISIBLE);
					Log.i("长按","long");
					return true;
				}
			});
			
			v.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View view, MotionEvent even) {
					//按下的时候
					if(MotionEvent.ACTION_DOWN==even.getAction()){
						holder.moneyItem.setBackgroundResource(R.color.gray_msg_bg);
						Log.i("按压","down");
					}						
					//手指松开的时候
					if(MotionEvent.ACTION_UP==even.getAction()){
						holder.moneyItem.setBackgroundResource(R.color.transparency);
						Log.i("起","up");
					}
					
					//意外中断事件取消
					if(MotionEvent.ACTION_CANCEL==even.getAction()){
						holder.moneyItem.setBackgroundResource(R.color.transparency);					
					}
					return false;
				}
			});
			
			
			//取消触摸事件
			holder.cancel.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View view, MotionEvent even) {					
					//按下的时候
					if(MotionEvent.ACTION_DOWN==even.getAction()){
						view.setBackgroundResource(R.color.blue_title);													 
					}						
					//手指松开的时候
					if(MotionEvent.ACTION_UP==even.getAction()){
						view.setBackgroundResource(R.color.transparency);						
						holder.lmenu.setVisibility(view.GONE);						
					}
					
					//意外中断事件取消
					if(MotionEvent.ACTION_CANCEL==even.getAction()){
					 view.setBackgroundResource(R.color.transparency);					
					}
					return true;
				}
			});
			
			
			//删除触摸事件
			holder.del.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View view, MotionEvent even) {					
					//按下的时候
					if(MotionEvent.ACTION_DOWN==even.getAction()){
						view.setBackgroundResource(R.color.blue_title);													 
					}						
					//手指松开的时候
					if(MotionEvent.ACTION_UP==even.getAction()){
						view.setBackgroundResource(R.color.transparency);
						for (int i = 0; i < getGetMoneyNum().list.size(); i++) {
						if(getGetMoneyNum().list.get(i).equals(holder.moneyNum.getText())){
							getGetMoneyNum().list.remove(i);
							getGetMoneyNum().list_save.remove(i);
							break;
						  }
						}
						
						//重新计算总金额
						GetMoneyNum.moneytoal();
						
						managerClass.getSureCancel().remove();	
						ad.notifyDataSetChanged();
						holder.lmenu.setVisibility(view.GONE);
						Toal.setText(GetMoneyNum.money/10000+"万");
						
						if(moneyCount(GetMoneyNum.money)){     
							  //设置确定可用
							  btn.setBackgroundResource(R.drawable.buttom_selector_bg);
							  btn.setEnabled(true);
						  }else{
							 btn.setEnabled(false);
							 btn.setBackgroundResource(R.drawable.button_gray);  
						  }
						
						
					}
					
					
					//意外中断事件取消
					if(MotionEvent.ACTION_CANCEL==even.getAction()){
					 view.setBackgroundResource(R.color.transparency);
					 Log.i("ACTION_CANCEL", "ACTION_CANCEL");
					 holder.lmenu.setVisibility(view.GONE);	
					}
					return true;
				}
			});
														
			
			return v;
		}		
		
	}
	
	class ViewHolder{
		TextView moneyNum;
		TextView del;
		TextView cancel;
		LinearLayout lmenu;
		RelativeLayout moneyItem;
	}

	
	//触摸事件
	class Touch implements OnTouchListener{
	@Override
	public boolean onTouch(View view, MotionEvent even) {
		//按下的时候
		if(MotionEvent.ACTION_DOWN==even.getAction()){
			switch(view.getId()){
			case R.id.addmoney_back:
				back.setImageResource(R.drawable.back_cirle_press);
			 break;
			case R.id.addmoney_suer_out:
				btn.setBackgroundResource(R.drawable.buttom_select_press);
				break;
			}
		}		
		//手指松开的时候
		if(MotionEvent.ACTION_UP==even.getAction()){
			switch(view.getId()){
			//返回
			case R.id.addmoney_back:
				back.setImageResource(R.drawable.back_cirle);
				managerClass.getRfid().scanclose();
				ClearAddMoneyOutDo.this.finish();
			 break;
			//加钞确定
			case R.id.addmoney_suer_out:
				btn.setBackgroundResource(R.drawable.buttom_selector_bg);
				//为钱捆参数赋值
				setGagNum();
				//提示
				managerClass.getRuning().runding(ClearAddMoneyOutDo.this,"正在执行加钞...");
				//开始加钞
				//参数：计划编号，钞箱编号，审核员1，复审核员2，机构编号，钱捆编号1，2，3，4，5，6
				getAddMoneyConfirm().addMoneyConfrim(plan, GetAddMoneySumBiz.box.getNum(),
						BankDoublePersonLogin.userid1,BankDoublePersonLogin.userid2, GApplication.user.getOrganizationId(),
						bagNum1, bagNum2, bagNum3, bagNum4, bagNum5, bagNum6);
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
			case R.id.addmoney_back:
				back.setImageResource(R.drawable.back_cirle);
			 break;
			case R.id.addmoney_suer_out:
			   btn.setBackgroundResource(R.drawable.buttom_selector_bg);
			break;
			}
			GolbalUtil.ismover=0;
		}
		return true;
		}
	}

	
	//为钱捆参数赋值
	public void setGagNum(){
		for (int i = 0; i < getGetMoneyNum().list.size(); i++) {
			if(getGetMoneyNum().list.get(i)!=null && getGetMoneyNum().list.get(i)!=""){
				switch(i){
				case 0:
					bagNum1 =getGetMoneyNum().list.get(i);
					break;
				case 1:
					bagNum2 =getGetMoneyNum().list.get(i);
					break;
				case 2:
					bagNum3 =getGetMoneyNum().list.get(i);
					break;
				case 3:
					bagNum4 =getGetMoneyNum().list.get(i);
					break;
				case 4:
					bagNum5 =getGetMoneyNum().list.get(i);
					break;
				case 5:
					bagNum6 =getGetMoneyNum().list.get(i);
					break;
				}
			
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		
	}
	
	
	
	
	
	@Override
	protected void onResume() {		
		super.onResume();
		
		//开启RFID扫描，只代码在同一生命周期里面只执行一次
		if(first){
		first = false;
		//添加钞箱通知
		managerClass.getRfid().addNotifly(scanbox);  
		//打开扫描，进行钞箱扫描
		getRfid().open_a20();
		
		}		
	}
	
	
	

	@Override
	protected void onPause() {
		super.onPause();		
		//关闭扫描
		getRfid().close_a20();		
		/**
		 * 2015-7-13  增
		 */
		getRfid().scanclose();		
	}
	
	
	
	
	
	
	
}
