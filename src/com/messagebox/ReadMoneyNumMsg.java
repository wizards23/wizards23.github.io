package com.messagebox;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.pda.R;
import com.golbal.pda.GolbalView;

public class ReadMoneyNumMsg {
	private GolbalView golbalView;
	 GolbalView getGolbalViewt() {
		return golbalView=golbalView==null ? new GolbalView():golbalView;
	}
	
	/**
	 * 弹出窗口提示信息
	 * @param a
	 * @param msg
	 */
//	public  void readMsg(View parent,int x,int y){
//		PopupWindow pw = new PopupWindow(getGolbalViewt().readmsg, 210, 45);
//		pw.showAtLocation(parent,Gravity.CENTER_HORIZONTAL, x, y);					
//		
//	} 
}
