package com.systemadmin.biz;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView.FindListener;

import com.golbal.pda.GolbalUtil;
import com.systemadmin.service.GetNameNumByFingerService;
import com.systemadmin.service.GettheNameByIdService;

public class GetNaneNumByUseridBiz{
	//采集指纹输入用户编号获取用户姓名
	String name = null;
	public Handler handler;
	Bundle bundle;
	private GettheNameByIdService getname=null;
			
	GettheNameByIdService getGetname() {
		return getname = getname==null?new GettheNameByIdService():getname;
	}

	/**
	 * 采集指纹输入用户编号获取用户姓名
	 * @param userid  用户ID
	 */
	public void getNameById(String userid){
		if(GolbalUtil.onclicks){
		  GolbalUtil.onclicks = false;
		  Thread t = new Thread(new Run(userid));
		  t.start();
		}
	}
	
	class Run implements Runnable{
		Message m;
		String userid;
		public Run(){};
		public Run(String userid){
			m = handler.obtainMessage();	
			this.userid = userid;
		};
		
		@Override
		public void run() {
			try {				
				name  = getGetname().getName(userid);				
				if(name!=null){
				Log.i("name", name+"");
				bundle = new Bundle();
				bundle.putString("fingerName", name);
				m.setData(bundle);
				m.what = 1;	 //成功返回
				}else{
					m.what=0;//无数据
				}
			} catch (Exception e) {
				Log.e("", "", e);
				m.what = -1;   //异常
			}finally{ 
				handler.sendMessage(m);
				GolbalUtil.onclicks = true;
			}
			
		}		
	}
	
	
	
	
	
}
