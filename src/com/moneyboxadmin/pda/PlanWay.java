package com.moneyboxadmin.pda;

import com.application.GApplication;
import com.entity.Way;
import com.example.pda.R;
import com.fragment.pda.PlanWayDate_fragment;
import com.fragment.pda.PlanWay_fragment;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.imple.getnumber.AddMoneygetNum;
import com.imple.getnumber.BackCleanBox;
import com.manager.classs.pad.ManagerClass;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PlanWay extends Activity implements OnTouchListener{
	//钞箱出库
	
	Bundle bundle_business;    //接收业务标识
	String businessNmae;	   //业务名称
	Bundle bundle_listitem;		//把当前点击的项的编号和路线放进bundle	
	ImageView back;             //返回按钮
	ImageView refresh;           //刷新
	TextView biz_name;           //业务标题控件
	Fragment way;				//没日期的fragment
	Fragment waydate;			//有日期的fragment
	private ManagerClass managerClass;
	
	private PlanWay_fragment planWay_fragment;		
	public PlanWay_fragment getPlanWay_fragment() {
		return planWay_fragment=planWay_fragment==null?new PlanWay_fragment():planWay_fragment;
	}	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.y_emptyvan_goout);
		
		 //全局异常处理
		// CrashHandler.getInstance().init(this);
		
		managerClass = new ManagerClass();
		back = (ImageView) findViewById(R.id.back);
		refresh= (ImageView) findViewById(R.id.refresh);
		biz_name = (TextView) findViewById(R.id.biz_name);
		
		
		//接收参数
		bundle_business = getIntent().getExtras();
		if(bundle_business!=null){
			//获取当前要执行的业务
			businessNmae = bundle_business.getString("business");
		}
		
		
		//根据条件改变业务显示
		biz_name.setText(businessNmae);
		
		//触摸事件
		back.setOnTouchListener(this);
		refresh.setOnTouchListener(this);
		
		
	  //判断加载那个fragment显示路线，因为路线显式字段不一样
		if("回收钞箱清点".equals(businessNmae)){
			if(waydate==null){   //业务单号、箱子数量、日期
			   waydate = new PlanWayDate_fragment();	
			}			
			managerClass.getGolbalView().replaceFragment(this,R.id.fragment_lay,waydate, bundle_business);
		}else{
			if(way==null){   //计划编号、路线
				way = new PlanWay_fragment();
				}
			managerClass.getGolbalView().replaceFragment(this,R.id.fragment_lay,way, bundle_business);			
		}
		
		 //把当前activity放进集合	
		// GApplication.addActivity(this,"0planway");	
		
						
	}

	@Override
	public boolean onTouch(View view, MotionEvent even) {
		//按下的时候
		if(MotionEvent.ACTION_DOWN==even.getAction()){
			switch(view.getId()){
			//返回
			case R.id.back:
				back.setImageResource(R.drawable.back_cirle_press);
				break;
			//刷新
			case R.id.refresh:
				refresh.setImageResource(R.drawable.refplace_cirle_press);
				
				break;				
			}
		}
		
		//手指松开的时候
		if(MotionEvent.ACTION_UP==even.getAction()){
			switch(view.getId()){
				//返回
			case R.id.back:
				back.setImageResource(R.drawable.back_cirle);
				PlanWay.this.finish();
				break;
				//刷新
			case R.id.refresh:
				refresh.setImageResource(R.drawable.refplace_cirle);
				planFragment.replace();				
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
			back.setImageResource(R.drawable.back_cirle);
			refresh.setImageResource(R.drawable.refplace_cirle);
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("回收钞箱清分已扫描list是否有数据");
		if(BackCleanBox.yiQingfenList!=null){
			BackCleanBox.yiQingfenList.clear();
			System.out.println("清空list/yiQingfenList="+BackCleanBox.yiQingfenList);
		}
		System.out.println("加钞清分已扫描list是否有数据");
		if(AddMoneygetNum.yiQingfenList!=null){
			AddMoneygetNum.yiQingfenList.clear();
			System.out.println("清分成功/yiQingfenList="+AddMoneygetNum.yiQingfenList);
		}
		
		if(BackCleanBox.map!=null){
			BackCleanBox.map.clear();
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}


	public  Handler hand_run = new Handler(){
		@Override
		public void handleMessage(Message msg) {			
			super.handleMessage(msg);
			if(msg.what==1){
				Log.i("sssss", "sss");
			managerClass.getRuning().remove();	
			}
			
		}
		
	};
	
	
	//内部刷新接口	
	PlanFragment planFragment;
	
	public interface PlanFragment{
		public void replace();
	}


	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);
		try {
			planFragment = (PlanFragment) fragment;
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	

	
		
}
