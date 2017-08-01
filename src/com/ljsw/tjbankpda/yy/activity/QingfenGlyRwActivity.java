package com.ljsw.tjbankpda.yy.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.pda.R;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.yy.application.S_application;
/**
 * 清分管理员工作任务页面
 * @author Administrator
 *
 */
public class QingfenGlyRwActivity extends FragmentActivity implements OnClickListener{
	
	private TextView qlrw_count;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qingfen_renwu_s);
		initView();
	}
	
	/**
	 * 初始化控件
	 */
	public void initView(){
		qlrw_count = (TextView) this.findViewById(R.id.qlrw_size);
		if(S_application.getApplication().qfjhdlist.size()>0){
			qlrw_count.setText(""+S_application.getApplication().qfjhdlist.size());
		}
		findViewById(R.id.qingfen_backS1).setOnClickListener(this);
		findViewById(R.id.qingling).setOnClickListener(this);
	}
	
	/**
	 * 单击事件
	 */
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.qingfen_backS1://返回
			QingfenGlyRwActivity.this.finish();
			break;

		case R.id.qingling://跳转清分计划单列表
			Skip.skip(QingfenGlyRwActivity.this, QingfenJhdActivity.class, null, 0);
			break;
		}	
	}
	
	
}
