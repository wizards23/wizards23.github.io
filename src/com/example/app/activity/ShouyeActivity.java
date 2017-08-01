package com.example.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.example.app.util.Skip;
import com.example.pda.R;

/**
 * 
 * @author yuyunheng 首页
 */
public class ShouyeActivity extends Activity {

	private ImageView xianchaoGuanli;
	private ImageView xitongShezhi;
	private ImageView guanyu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		load();

		/*
		 * 给控件设置处理事件
		 */
		ShijianChuli shijianChuli = new ShijianChuli();
		xianchaoGuanli.setOnClickListener(shijianChuli);
		xitongShezhi.setOnClickListener(shijianChuli);
		guanyu.setOnClickListener(shijianChuli);
	}

	/**
	 * 控件初始化
	 */
	private void load() {
		xianchaoGuanli = (ImageView) findViewById(R.id.home);
		xitongShezhi = (ImageView) findViewById(R.id.systeset);
		guanyu = (ImageView) findViewById(R.id.version);
	}

	/**
	 * 　　控件事件处理
	 * 
	 * @author yuyunheng
	 * 
	 */
	private class ShijianChuli implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.home:
				Skip.skip(ShouyeActivity.this, XitongDengluActivity.class,
						null, 0);
				break;
			case R.id.systeset:

				break;
			case R.id.version:
				Skip.skip(ShouyeActivity.this, GuanyuActivity.class, null, 0);
				break;

			}
		}

	}

}
