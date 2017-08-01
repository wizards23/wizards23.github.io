package com.example.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.example.app.util.Skip;
import com.example.pda.R;

/**
 * 现钞管理 菜单
 * 
 * @author yuyunheng
 * 
 */
public class XianchaoGuanliActivity extends Activity {

	private ImageView xitongGuanli; // 系统管理
	private ImageView atmGuanli; // ATM管理
	private ImageView kuanxiangGuanli; // 款箱管理
	private String jigouid;
	private Bundle bundle;//传递机构id

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_zhucaidan);
		load();
		Shijianchuli shijianchuli = new Shijianchuli();
		xitongGuanli.setOnClickListener(shijianchuli);
		atmGuanli.setOnClickListener(shijianchuli);
		kuanxiangGuanli.setOnClickListener(shijianchuli);
	}

	/**
	 * 控件初始化
	 */
	private void load() {
		xitongGuanli = (ImageView) findViewById(R.id.systemanager);
		atmGuanli = (ImageView) findViewById(R.id.money_box_admins);
		kuanxiangGuanli = (ImageView) findViewById(R.id.cleanadmin);
		Intent intent=getIntent();
		bundle=intent.getExtras();
		jigouid = bundle.getString("jigouid");
		//System.out.println("管理activity机构id："+jigouid);
	}

	/**
	 * 事件处理
	 * 
	 * @author yuyunheng
	 * 
	 */
	private class Shijianchuli implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.systemanager:
				Skip.skip(XianchaoGuanliActivity.this, XitongGuanliActivity.class, null, 0);
				break;

			case R.id.money_box_admins:
				// 页面跳转  ATM菜单
				Skip.skip(XianchaoGuanliActivity.this, ATMcaidanActivity.class, null, 0);
				break;
			case R.id.cleanadmin:
				// 页面跳转 款箱菜单
				Skip.skip(XianchaoGuanliActivity.this, KuanxiangCaidanActivity.class, bundle, 0);
				break;
			}
		}
	}

}
