package com.messagebox;

import com.example.pda.R;
import com.golbal.pda.GolbalView;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SureCancelButton {
	
	
	GolbalView g;
	GolbalView getG() {
		if(g==null){
			g = new GolbalView();
		}
		return g;
	}
	View v;
	/**
	 * 
	 * @param a
	 * @param msg
	 * @param click  确定事件
	 * @param can  true为只显示确定  ，false为显示确定和取消
	 */
	public void makeSuerCancel(Activity a,String msg,OnClickListener click,boolean can){
		
		if(v==null){
			v = GolbalView.getLF(a).inflate(R.layout.suer_cancel_button, null);
		}
		TextView text = (TextView) v.findViewById(R.id.suercanceltext);
		text.setText(msg);
		Button suer = (Button)v.findViewById(R.id.suerbtn);
		Button cancel = (Button)v.findViewById(R.id.cancelbtn);
		
		if(can){
			cancel.setVisibility(View.GONE);
			suer.setVisibility(View.VISIBLE);
			Log.i("取消","取消");
		}else{
			suer.setVisibility(View.VISIBLE);
			cancel.setVisibility(View.VISIBLE);
			cancel.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View arg0) {
					remove();				
				}
			});	
		}		
		getG().createFloatView(a, v);
		suer.setOnClickListener(click);
			
	}
	
	/**
	 * author zhangXuewei
	 * @param a
	 * @param msg
	 * @param click  确定事件
	 * @param can  true为只显示确定  ，false为显示确定和取消
	 */
	public void makeSuerCancel2(Activity a,String msg,OnClickListener click,boolean can){
		
		if(v==null){
			v = GolbalView.getLF(a).inflate(R.layout.suer_cancel_button, null);
		}
		TextView text = (TextView) v.findViewById(R.id.suercanceltext);
		text.setText(msg);
		Button suer = (Button)v.findViewById(R.id.suerbtn);
		Button cancel = (Button)v.findViewById(R.id.cancelbtn);
		cancel.setText("重新录入");
		if(can){
			cancel.setVisibility(View.GONE);
			suer.setVisibility(View.VISIBLE);
			Log.i("取消","取消");
		}else{
			suer.setVisibility(View.VISIBLE);
			cancel.setVisibility(View.VISIBLE);
			cancel.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View arg0) {
					remove();				
				}
			});	
		}		
		getG().createFloatView(a, v);
		suer.setOnClickListener(click);
			
	}
	public void remove(){
	   getG().removeV(v);
	}
	
	
}
