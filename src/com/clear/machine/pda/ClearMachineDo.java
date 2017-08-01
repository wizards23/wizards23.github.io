package com.clear.machine.pda;

import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.out.admin.pda.ClearMachineIing;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

public class ClearMachineDo extends Activity implements OnTouchListener {

	Button cleanmachine;
	Button addmoney;
	TextView msg;
	
	private GolbalUtil getUtil;
	
	public GolbalUtil getGetUtil() {
		return getUtil==null?new GolbalUtil():getUtil;
	}
	public void setGetUtil(GolbalUtil getUtil) {
		this.getUtil = getUtil;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.c_clear_machine_work);
		
		cleanmachine = (Button) findViewById(R.id.cleanmachine);
		addmoney = (Button) findViewById(R.id.addmoney);
		msg = (TextView) findViewById(R.id.msg);
		
		cleanmachine.setOnTouchListener(this);
		addmoney.setOnTouchListener(this);
	}

	
	
	
	@Override
	public boolean onTouch(View view, MotionEvent even) {
		//按下的时候
		if(MotionEvent.ACTION_DOWN==even.getAction()){
			
		}
		
		//手指松开的时候
		if(MotionEvent.ACTION_UP==even.getAction()){
			switch(view.getId()){
			//清机
			case R.id.cleanmachine:
				msg.setText("清机已完成");
			
				break;
			//加钞
			case R.id.addmoney:
				getGetUtil().gotoActivity(ClearMachineDo.this, ClearMachineIing.class, null, getGetUtil().ismover);;
				break;
				
			}
			getGetUtil().ismover=0;
		}
		//手指移动的时候
		if(MotionEvent.ACTION_MOVE==even.getAction()){
			getGetUtil().ismover++;
		}
		//意外中断事件取消
		if(MotionEvent.ACTION_CANCEL==even.getAction()){
			getGetUtil().ismover=0;
		}
		return true;
	}
	
}
