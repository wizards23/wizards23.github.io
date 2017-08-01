package com.messagebox;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pda.R;
import com.golbal.pda.GolbalView;
import com.main.pda.MainActivity;
import com.online.update.biz.Online;
import com.sql.SQL;

public class Loading {
	private GolbalView g;
	Handler h;
	Bundle bun = null;
	ProgressBar bar;
	TextView percentText;
	GolbalView getG() {
		if(g==null){
			g = new GolbalView();
		}
		return g;
	}
	private View v;
	/**
	 * 
	 * @param context  
	 * @param h     安装通知
	 */
	public  void loading(Context context,final Handler h_load){
		try {
			if(v==null){
			   v =GolbalView.getLF(context).inflate(R.layout.loading, null);			   
			}
			
			bar =(ProgressBar) v.findViewById(R.id.progress_version);
			percentText = (TextView) v.findViewById(R.id.percentText);
			
			h = new Handler(){			
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					
					switch(msg.what){
					case 1:
						 bun = msg.getData();
						 double percent = bun.getDouble("bar");
						 String p=null;
						 if(percent<=0){
						   p="0.00";	 
						 }
						
						 try {
							 if(percent>=100){
								p ="100"; 
							 }else{
								 p =(percent+"").substring(0,(percent+"").indexOf(".")+3); 
							 }
							  
							  
						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
						
						 Log.i("percent", percent+"");
						 percentText.setText(p+"%");
						 int progressValue =(int)percent;
						 Log.i("progressValue", progressValue+"");
						 bar.setProgress(progressValue);
						break;
						
						
					case 100:
						//下载完成后，通知安装
						Message ms = Message.obtain();
						ms.what = 100;
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						h_load.sendMessage(ms);
					 remove();	
						break;
					
					}
					
				}			
			};			
			getG().createFloatView(context,v);
			
			//下载新版本
			new Thread(new Run(h,context)).start();
			
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
	
	
	
	
	
	//下载APK
	class Run implements Runnable{
		private Handler h;
		Context context;
		public Run(){}
		public Run(Handler h,Context context){
			this.h =h;
			this.context = context;
		}
		@Override
		public void run() {
		  Online on = new Online(this.h);
		  try {
			  on.load(context);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		  
		}
		
	}
	
	
}
