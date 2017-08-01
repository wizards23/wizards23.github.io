package com.clear.machine.pda;

import java.util.Timer;
import java.util.TimerTask;

import com.example.pda.R;
import com.golbal.pda.GolbalUtil;

import android.app.Activity;
import android.os.Bundle;

public class ClearMachineLogin extends Activity {

	private GolbalUtil getUtil;	
	public GolbalUtil getGetUtil() {return getUtil==null?new GolbalUtil():getUtil;}
	public void setGetUtil(GolbalUtil getUtil) {this.getUtil = getUtil;}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.c_clear_addmoney_login);
		
		Timer t = new Timer();
		
		t.schedule(new TimerTask() {
			
			@Override
			public void run() {
				getGetUtil().gotoActivity(ClearMachineLogin.this,ClearMachineDo.class, null, getGetUtil().ismover);
			}
		}, 2000);
		
	}
	
}
