package com.messagebox;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pda.R;
import com.golbal.pda.GolbalView;
import com.main.pda.FingerGather;

public class Abnormal {
	
	private GolbalView g;	
	GolbalView getG() {
		if(g==null){
			g = new GolbalView();
		}
		return g;
	}

	private View v;
	/**
	 * 
	 * @param a   当前activity
	 * @param msg   要提示的信息内容
	 */
	public  void timeout(Context context,String msg,OnClickListener click){
		//连接超时布局
		if(v==null){
		   v = GolbalView.getLF(context).inflate(R.layout.timeout, null);
		}
		Button btnsuer = (Button)v.findViewById(R.id.suere_timeout);
		Button btncancel = (Button)v.findViewById(R.id.cancel_timeout);
		TextView msgcontent = (TextView)v.findViewById(R.id.msgcontent);
		msgcontent.setText(msg);
		if(click!=null){
		  btnsuer.setOnClickListener(click);		
		}
			
		//超时操作事件
		btncancel.setOnClickListener(new View.OnClickListener() {						
			@Override
			public void onClick(View arg0) {				
				getG().removeV(v);
			}
		});						
		
		getG().createFloatView(context,v);
	}
	
	
	public void remove(){
		getG().removeV(v);
	}
	
}
