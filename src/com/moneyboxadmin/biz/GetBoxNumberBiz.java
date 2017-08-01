package com.moneyboxadmin.biz;

import java.net.SocketTimeoutException;

import com.entity.BoxNum;
import com.golbal.pda.GolbalUtil;
import com.moneyboxadmin.service.GetBoxNumberService;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


//ATM钞箱管理获取钞箱出库数量和钞箱入库数量
public class GetBoxNumberBiz {
	
	public  Handler hand_boxNum;
	public static BoxNum box;
	
	private GolbalUtil golbalUtil;		
	public GolbalUtil getGolbalUtil() {
		return golbalUtil=golbalUtil==null?new GolbalUtil():golbalUtil;
	}
	
	
	
	public  void getBoxNum(String corpId){
		if(getGolbalUtil().onclicks){
		AsyncBoxNum asyn = new AsyncBoxNum(corpId);
		asyn.execute();
		Log.i("222", "222");
		getGolbalUtil().onclicks = false;
		}
	}
	
	
	 private  class AsyncBoxNum extends AsyncTask{	
		 String corpId;  //机构ID
		 Message m;
		 
		 public AsyncBoxNum(){};	 
		 public AsyncBoxNum(String corpId){
			 m = hand_boxNum.obtainMessage();
			 this.corpId = corpId;
			 
		 };
		 
		 
			//取消任务后的操作  
			@Override
			protected void onCancelled() {
				super.onCancelled();
				getGolbalUtil().onclicks = true;
				//m.what=-1;
				hand_boxNum.sendMessage(m);
			}

			//异步完成后的操作
			@Override
			protected void onPostExecute(Object result) {
				super.onPostExecute(result);
				getGolbalUtil().onclicks = true;
				m.what=1;	
				hand_boxNum.sendMessage(m);
			}

			//异步后台操作
			@Override
			protected Object doInBackground(Object... arg0) {
				try {
				box=GetBoxNumberService.getCashboxNum(corpId);
				}catch (SocketTimeoutException e) {
					e.printStackTrace();
					m.what=-4;
					 this.cancel(true);
				} catch (Exception e) {
					e.printStackTrace();
					m.what=-1;
					 this.cancel(true);
				}
				return null;
			}		   
		  }
		 
	
}
