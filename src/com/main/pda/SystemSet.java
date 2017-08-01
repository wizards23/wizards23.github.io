package com.main.pda;

import a20.cn.uhf.admin.WriteAndRead;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.application.GApplication;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;

public class SystemSet extends Activity {
	
	ImageView netset;
	ImageView rfid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c_system_set);
		
		netset = (ImageView) findViewById(R.id.netset);
		rfid = (ImageView) findViewById(R.id.rfidreadwrite);
		
		netset.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
			 new GolbalUtil().gotoActivity(SystemSet.this, Service_Address.class, null, 0);	
			 	
			}
		});
		
		rfid.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
			 new GolbalUtil().gotoActivity(SystemSet.this, WriteAndRead.class, null, 0);	
			 	
			}
		});
		
		
	}
}
