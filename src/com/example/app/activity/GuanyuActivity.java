package com.example.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pda.R;

public class GuanyuActivity extends Activity {
	private Button guanyu;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guanyu);
		load();
	}
	/**
	 * 初始控件
	 */
	public void load(){
		guanyu=(Button)findViewById(R.id.guanyu_button);
		guanyu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Toast.makeText(GuanyuActivity.this, "此版本为最新版本", 4).show();
			}
		});
	}
}
