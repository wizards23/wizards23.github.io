package com.messagebox;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.pda.R;
import com.golbal.pda.GolbalView;

public class IrfidMsg {
	GolbalView g;
	GolbalView getG() {
		if(g==null){
			g = new GolbalView();
		}
		return g;
	}
	View v;
	public TextView TextMsg;
	public void irfidMsg(Activity a,String msg){
	
		if(v==null){
		   v = GolbalView.getLF(a).inflate(R.layout.irfid_msg, null);
		}
		if(TextMsg==null){
			TextMsg = (TextView) v.findViewById(R.id.irfid_text);	
		}		
		TextMsg.setText(msg);
		getG().createFloat(a, v, Gravity.TOP);		
	}
	
	public void removeV(){
		getG().removeV(v);
	}
}
