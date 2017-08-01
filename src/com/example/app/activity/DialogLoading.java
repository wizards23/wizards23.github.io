package com.example.app.activity;

import com.example.pda.R;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

public class DialogLoading {
	private static PopupWindow popuLoading;
	public static TextView text;
	private static ImageView img;
	private static AnimationDrawable ad;
	private static View v;
	public static void loading(Activity activity,String msg,View view){
		//加载布局
		v = activity.getLayoutInflater().inflate(R.layout.dialogloading,null);
		popuLoading = new PopupWindow(v,LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,true);
		text = (TextView) v.findViewById(R.id.o_msg);
		img = (ImageView) v.findViewById(R.id.o_img_loading);
		ad = (AnimationDrawable) img.getDrawable();
		text.setText(msg);
		popuLoading.showAtLocation(view, Gravity.CENTER,0,0);
		ad.start();
	}
	
	public static void removeV(){
		if(popuLoading!=null){
		 popuLoading.dismiss();		 
		}
		//System.out.println("removeV--移除加载中");
	}
}
