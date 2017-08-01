package com.messagebox;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.pda.R;
import com.golbal.pda.GolbalView;

public class GoBack {
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
	public  void back(Context context,String msg,OnClickListener click){
		//连接超时布局
		if(v==null){
		   v = GolbalView.getLF(context).inflate(R.layout.fanhui_dialog, null);
		}
		Button btnsuer = (Button)v.findViewById(R.id.fanhui_back);
		TextView msgcontent = (TextView)v.findViewById(R.id.msg_back);
		msgcontent.setText(msg);
		if(click!=null){
		  btnsuer.setOnClickListener(click);		
		}							
		getG().createFloatView(context,v);
	}
	public void remove(){
		getG().removeV(v);
	}
}
