package com.messagebox;

import com.application.GApplication;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MenuShow {
	
	View view;
	GolbalView g;
	public static PopupWindow pw;
	TextView exit;
	TextView cancel;
	
	public void menu(Activity a){
		try {
			g = new GolbalView();	   	    	    
			if(view==null){
				view =GolbalView.getLF(a).inflate(R.layout.menu, null);
			}		
			pw = new PopupWindow(view,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
			//pw.setBackgroundDrawable(a.getResources().getDrawable(R.drawable.buttom_selector_bg));
			 exit = (TextView) view.findViewById(R.id.menu_exit);
			 cancel = (TextView) view.findViewById(R.id.menu_cancel);
			 
			 exit.setOnTouchListener(new Touch());	
			 cancel.setOnTouchListener(new Touch());
			//设置触摸外面时消失
			pw.setOutsideTouchable(true);
			pw.update();
			pw.setTouchable(true);
			//设置系统 动画
			pw.setAnimationStyle(android.R.style.Animation_Dialog);
			//设置点击menu以外的其他地方及返回键退出
			pw.setFocusable(true);
			view.setFocusableInTouchMode(true);
			view.setOnKeyListener(new View.OnKeyListener() {			
				@Override
				public boolean onKey(View v, int keycode, KeyEvent even) {
					if(keycode==even.KEYCODE_MENU && pw.isShowing() || keycode==even.KEYCODE_BACK){
						pw.dismiss();
						return true;
					}
					return false;
				}
			});	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		
	}
	
	
	class Touch implements OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent even) {
			//按下的时候
			if(MotionEvent.ACTION_DOWN==even.getAction()){
				switch(v.getId()){
				case R.id.menu_cancel:
					cancel.setBackgroundColor(Color.parseColor("#a6bdbdbd"));
					break;
				case R.id.menu_exit:
					exit.setBackgroundColor(Color.parseColor("#a6bdbdbd"));
					break;						
				}
			  }
			
			//手指松开的时候
			if(MotionEvent.ACTION_UP==even.getAction()){
				switch(v.getId()){
				case R.id.menu_cancel:
					cancel.setBackgroundColor(Color.TRANSPARENT);
					pw.dismiss();
					break;
				case R.id.menu_exit:
					exit.setBackgroundColor(Color.TRANSPARENT);
					Log.i("aaaaaaa","aaaaaaa");
					GApplication.exit(true);
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
				switch(v.getId()){
				case R.id.menu_cancel:
					cancel.setBackgroundColor(Color.TRANSPARENT);
					break;
				case R.id.menu_exit:
					exit.setBackgroundColor(Color.TRANSPARENT);
					break;						
				}
			}			
			return true;
		}
		
	}
	
	
	
}
