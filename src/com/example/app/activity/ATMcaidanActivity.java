package com.example.app.activity;


import com.example.pda.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 *  ATM菜单
 * @author yuyunheng
 *
 */
public class ATMcaidanActivity extends Activity {

	private ImageView atmChaoxiangGuanli;  //钞箱管理 btn
	private ImageView qingfenGuanli;     //清分管理 btn
	private ImageView chukuJiaojie;      // 出库交接 btn
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_atmguanli);
		load();
		ShijianChuli shijianchuli=new ShijianChuli();
		atmChaoxiangGuanli.setOnClickListener(shijianchuli);
		qingfenGuanli.setOnClickListener(shijianchuli);
		chukuJiaojie.setOnClickListener(shijianchuli);
	}

	/**
	 * 控件初始化
	 */
	private void load(){
		atmChaoxiangGuanli=(ImageView) findViewById(R.id.systemanager);
		qingfenGuanli=(ImageView) findViewById(R.id.money_box_admins);
		chukuJiaojie=(ImageView) findViewById(R.id.cleanadmin);
	}
	
	
	private class ShijianChuli implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.systemanager:
				break;
			case R.id.money_box_admins:
				break;
			case R.id.cleanadmin:
				break;
			}
		}
		
	}
	
}
