package com.example.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.application.GApplication;
import com.example.app.util.Skip;
import com.example.pda.R;
import com.manager.classs.pad.ManagerClass;
import com.poka.device.ShareUtil;

public class KuanxiangCaidanActivity extends Activity {
	private ImageView kucunGuanli, churukuGuanli, kuanxiangJiaojie; // 库存管理 btn
																	// 出入库管理 btn
																	// 款箱交接 btn
	private LinearLayout chukulayout, jiaojielayout;
	private ManagerClass manager;
	OnClickListener OnClick;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_kuanxiangguanli);
		load();
		manager = new ManagerClass();
		quanxian();
		ShijianChuli shijianchuli = new ShijianChuli();
		kucunGuanli.setOnClickListener(shijianchuli);
		churukuGuanli.setOnClickListener(shijianchuli);
		kuanxiangJiaojie.setOnClickListener(shijianchuli);
	}

	/**
	 * 控件初始化
	 */
	private void load() {
		kucunGuanli = (ImageView) findViewById(R.id.systemanager);
		churukuGuanli = (ImageView) findViewById(R.id.money_box_admins);
		kuanxiangJiaojie = (ImageView) findViewById(R.id.kuanxiangjiaojie);
		chukulayout = (LinearLayout) findViewById(R.id.churuku_guanli);
		jiaojielayout=(LinearLayout) findViewById(R.id.layout_kx_jiaojie);
	}

	private class ShijianChuli implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.systemanager:
				// 库存管理
				break;
			case R.id.money_box_admins:
				// 　出入库管理
				manager.getRuning().runding(KuanxiangCaidanActivity.this,
						"指纹功能开启中...");
				Skip.skip(KuanxiangCaidanActivity.this,
						KuguanDengluActivity.class, null, 0);
				break;
			case R.id.kuanxiangjiaojie:
				// 款箱交接
				Skip.skip(KuanxiangCaidanActivity.this, JiaoJieActivity.class,
						null, 0);
				break;
			}
		}

	}


	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(KuanxiangCaidanActivity.this,
						"数据加载失败", new View.OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								manager.getRuning().remove();
								
							}
						});
				break;
			default:
				break;
			}

		}

	};

	public void quanxian() {
		if (GApplication.getApplication().loginjueseid.equals("9")) {
			chukulayout.setVisibility(View.GONE);
			jiaojielayout.setVisibility(View.VISIBLE);
		} else if (GApplication.getApplication().loginjueseid.equals("4")) {
			chukulayout.setVisibility(View.VISIBLE);
			jiaojielayout.setVisibility(View.GONE);
		} else if (GApplication.getApplication().loginjueseid.equals(ShareUtil.WdId)) {
			chukulayout.setVisibility(View.GONE);
			jiaojielayout.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			KuanxiangCaidanActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (manager.getRuning() != null) {
			manager.getRuning().remove();
		}
	}

}
