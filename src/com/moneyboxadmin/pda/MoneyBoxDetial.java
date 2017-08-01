package com.moneyboxadmin.pda;


import com.example.pda.R;
import com.fragment.pda.MoneyAndBoxNum_fragment;
import com.fragment.pda.MoneyAndMoneyCount_fragment;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.biz.GetBoxDetailListBiz;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class MoneyBoxDetial extends Activity implements OnTouchListener {

	Bundle bundle;
	ImageView back;  //返回
	OnClickListener clickreplace;
	String planNum;  //计划编号
	String bizName;  //业务名称
	Fragment fragment;
	private ManagerClass magagerClass;

	
	private GetBoxDetailListBiz getBoxDetailList;		
	public GetBoxDetailListBiz getGetBoxDetailList() {
		return getBoxDetailList=getBoxDetailList==null ?new GetBoxDetailListBiz():getBoxDetailList;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c_moneybox_detail);
		magagerClass = new ManagerClass();
		back = (ImageView) findViewById(R.id.back_boxdetial);
		
		 //全局异常处理
	//	 CrashHandler.getInstance().init(this);
		
		back.setOnTouchListener(this);
		
		bundle = getIntent().getExtras();
		if(bundle!=null){
		   bizName = bundle.getString("businName");
		   Log.i("bizName",bizName);		   
		   if("钞箱加钞".equals(bizName)){
			   fragment = new MoneyAndMoneyCount_fragment();			   
			   magagerClass.getGolbalView().replaceFragment(this, R.id.momeybox_detail_fragment,fragment, bundle);
		   }else{
			   fragment = new MoneyAndBoxNum_fragment();
			   magagerClass.getGolbalView().replaceFragment(this, R.id.momeybox_detail_fragment,fragment, bundle);  
		   }
		}
		
		 //把当前activity放进集合	
		// GApplication.addActivity(this,"0moneybox");
		
	}
	

	//触摸事件
	@Override
	public boolean onTouch(View view, MotionEvent even) {
		//按下的时候
		if(MotionEvent.ACTION_DOWN==even.getAction()){
			switch(view.getId()){
			case R.id.back_boxdetial:
				back.setImageResource(R.drawable.back_cirle_press);
				
				break;
			}
		}
		
		//手指松开的时候
		if(MotionEvent.ACTION_UP==even.getAction()){
			switch(view.getId()){
			//返回
			case R.id.back_boxdetial:
				back.setImageResource(R.drawable.back_cirle);
				MoneyBoxDetial.this.finish();
				break;
			}			
			magagerClass.getGolbalutil().ismover=0;
		}
		//手指移动的时候
		if(MotionEvent.ACTION_MOVE==even.getAction()){
			magagerClass.getGolbalutil().ismover++;
		}
		//意外中断事件取消
		if(MotionEvent.ACTION_CANCEL==even.getAction()){
			magagerClass.getGolbalutil().ismover=0;
			switch(view.getId()){
			case R.id.back_boxdetial:
				back.setImageResource(R.drawable.back_cirle);
				break;
			}
		}

		return true;
	}

	
}
