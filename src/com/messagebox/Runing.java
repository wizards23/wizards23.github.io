package com.messagebox;

import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Runing {
	
	private GolbalView g;	
	GolbalView getG() {
		if(g==null){
			g = new GolbalView();
		}
		return g;
	}
	private View v;
	
	public  void runding(Context context,String msg){
		try {
			if(v==null){
			   v =GolbalView.getLF(context).inflate(R.layout.runing, null);			   
			}
			TextView text =(TextView)v.findViewById(R.id.runingtext);
			text.setText(msg);
			getG().createFloatView(context,v);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void remove(){
		try {
			if(v!=null){
				getG().removeV(v);	
				}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
