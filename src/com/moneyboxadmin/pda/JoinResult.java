package com.moneyboxadmin.pda;

import com.application.GApplication;
import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.manager.classs.pad.ManagerClass;
import com.out.admin.pda.OrderWork;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//空钞箱交接结果
public class JoinResult extends Activity implements OnTouchListener {

	Button surebtn;
	TextView content;
	TextView user_type;
	String biz;
	Bundle bundle;
	ImageView back;
	public static boolean result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c_clearingmember_joinresult);

		 //全局异常处理
		// CrashHandler.getInstance().init(this);
		
		surebtn = (Button) findViewById(R.id.sure);		
		content = (TextView) findViewById(R.id.result_text);
		user_type=(TextView) findViewById(R.id.user_type);
		back = (ImageView) findViewById(R.id.joinrs_back);
		
		
		bundle = getIntent().getExtras();
		Log.i("bundle", bundle+"");
		biz = bundle.getString("businName");
		Log.i("biz", biz+"");
		
		if("ATM加钞出库".equals(biz)){
			content.setText("ATM加钞出库交接完成");
			user_type.setText("押运员交接");
		}else if("空钞箱出库".equals(biz)){
			content.setText("空钞箱出库交接完成");
			user_type.setText("清分员交接");
		}else if("未清回收钞箱出库".equals(biz)){
			user_type.setText("清分员交接");
			content.setText("未清回收钞箱出库交接完成");
		}else if("停用钞箱出库".equals(biz)){
			user_type.setText("停用钞箱出库");
			content.setText("停用钞箱出库出库完成");
		}else if("钞箱装钞入库".equals(biz)){
			user_type.setText("钞箱装钞入库");
			content.setText("钞箱装钞入库完成");
		}else if("回收钞箱入库".equals(biz)){
			user_type.setText("回收钞箱入库");
			content.setText("回收钞箱入库完成");
		}else if("已清回收钞箱入库".equals(biz)){
			user_type.setText("已清回收钞箱入库");
			content.setText("已清回收钞箱入库完成");
		}else if("新增钞箱入库".equals(biz)){
			user_type.setText("新增钞箱入库");
			content.setText("新增钞箱入库完成");
		}else if("网点回收钞箱交接".equals(biz)){
			user_type.setText("网点回收钞箱交接");
			content.setText("与押运人员交接完成");
			result = true;
		}
						
		surebtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {			
			Log.i("弹出栈", "弹出栈");
			//把不用的Activity弹出栈
			//GApplication.getApplication().exit(false);	
		if("网点回收钞箱交接".equals(biz)){
		new GolbalUtil().gotoActivity(JoinResult.this, OrderWork.class, null,0);	
		}else{
		new GolbalUtil().gotoActivity(JoinResult.this, MoneyBoxManager.class, null,0);	
		}
			
			}
		});

		 //把当前activity放进集合	
		// GApplication.addActivity(this,"0joinresult");
	}
	@Override
	protected void onPause() {
		super.onPause();
		this.finish();
	}

	@Override
	public boolean onTouch(View view, MotionEvent even) {
		//按下的时候
		if(MotionEvent.ACTION_DOWN==even.getAction()){
			switch(view.getId()){
			case R.id.joinrs_back:
				back.setBackgroundResource(R.drawable.back_cirle_press);
				break;
			
			}
		}
		
		//手指松开的时候
		if(MotionEvent.ACTION_UP==even.getAction()){
			switch(view.getId()){
			case R.id.joinrs_back:
				back.setBackgroundResource(R.drawable.back_cirle);
				this.finish();
				break;
				
			}
			GolbalUtil.ismover=0;
		}
		//手指移动的时候
		if(MotionEvent.ACTION_MOVE==even.getAction()){
			GolbalUtil.ismover++;
		}
		//意外中断事件取消
		if(MotionEvent.ACTION_CANCEL==even.getAction()){			
			GolbalUtil.ismover=0;
			switch(view.getId()){
			case R.id.joinrs_back:
				back.setBackgroundResource(R.drawable.back_cirle);
				break;				
			}
		}
		return true;
	}
	
	
		
}
