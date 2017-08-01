package com.out.admin.pda;

import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.manager.classs.pad.ManagerClass;
import com.out.biz.AssignBiz;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderWorkInformation extends Activity implements OnTouchListener{
	
	//派工单明细
	Button btnATM;
	TextView plan; //计划编号
	TextView way;   //路线
	TextView type;   //类型
	TextView atm;		//ATM数量	
	TextView boxnum;   //箱子数量
	TextView carbrand;  //车牌号
	TextView date;   //日期
	ImageView back;
	
	Bundle bundle;
	private ManagerClass managerClass;
	
	
	AssignBiz assign;
	public AssignBiz getAssign() {
		return assign=assign==null?new AssignBiz():assign;
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.y_workorder_detail);
		
		 //全局异常处理
		// CrashHandler.getInstance().init(this);
		
		managerClass = new ManagerClass();
		
		btnATM = (Button) findViewById(R.id.atminfo);
		plan = (TextView) findViewById(R.id.webinfo_plan);
		way = (TextView) findViewById(R.id.webinfo_way);
		type = (TextView) findViewById(R.id.webinfo_type);
		atm = (TextView) findViewById(R.id.webinfo_atm);
		boxnum = (TextView) findViewById(R.id.webinfo_boxnum);
		carbrand = (TextView) findViewById(R.id.webinfo_carbrand);
		date = (TextView) findViewById(R.id.webinfo_date);
		back = (ImageView) findViewById(R.id.orderinfo_backs);
		
		plan.setText(getAssign().order.getPlanNum());
		way.setText(getAssign().order.getWay());
		type.setText(getAssign().order.getType());
		atm.setText(getAssign().order.getATM());
		boxnum.setText(getAssign().order.getBoxNum());
		carbrand.setText(getAssign().order.getCarBrand());
		date.setText(getAssign().order.getDate());
		
		btnATM.setOnTouchListener(this);
		back.setOnTouchListener(this);
		
		bundle = new Bundle();
		bundle.putString("plan", plan.getText().toString());
		
	}
	
	
	@Override
	public boolean onTouch(View view, MotionEvent even) {
		//按下的时候
		if(MotionEvent.ACTION_DOWN==even.getAction()){
			switch(view.getId()){
			//ATM详细信息
			case R.id.atminfo:
				btnATM.setBackgroundResource(R.drawable.buttom_select_press);
				break;	
			case R.id.orderinfo_backs:
				back.setImageResource(R.drawable.back_cirle_press);
				break;
			}
		}
		
		//手指松开的时候
		if(MotionEvent.ACTION_UP==even.getAction()){
			switch(view.getId()){
			//ATM详细信息
			case R.id.atminfo:
				btnATM.setBackgroundResource(R.drawable.buttom_selector_bg);
				managerClass.getGolbalutil().gotoActivity(OrderWorkInformation.this,ATMInformation.class , bundle, managerClass.getGolbalutil().ismover);
				break;	
			case R.id.orderinfo_backs:
				back.setImageResource(R.drawable.back_cirle);
				OrderWorkInformation.this.finish();
				break;
			}
			
			managerClass.getGolbalutil().ismover=0;
		}
		//手指移动的时候
		if(MotionEvent.ACTION_MOVE==even.getAction()){
			managerClass.getGolbalutil().ismover++;
			
			switch(view.getId()){
			//ATM详细信息
			case R.id.atminfo:
				btnATM.setBackgroundResource(R.drawable.buttom_selector_bg);
				break;	
			case R.id.orderinfo_backs:
				back.setBackgroundResource(R.drawable.back_cirle);
				break;
			}
		}
		//意外中断事件取消
		if(MotionEvent.ACTION_CANCEL==even.getAction()){
			managerClass.getGolbalutil().ismover=0;
			back.setImageResource(R.drawable.back_cirle);
		}
		
		return true;
	}



	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}
	
	
	

}
