package com.out.biz;

import java.net.SocketTimeoutException;

import com.golbal.pda.GolbalUtil;
import com.out.service.GetCorpInfoByIdService;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetCorpInfoByIdBiz {
	public Handler handler;
	Bundle  bundle;
	
	GetCorpInfoByIdService corpInfoByIdService;
	GetCorpInfoByIdService getGetCorpInfoByIdService(){
		if(corpInfoByIdService==null){
			corpInfoByIdService = new GetCorpInfoByIdService();
		}
		return corpInfoByIdService;
	}
	
	/**
	 * 
	 * @param corpId 机构ID
	 */
	public void getCorpInfoById(String corpId){
		
		if(GolbalUtil.onclicks){
			GolbalUtil.onclicks = false;
			new Thread(new Run(corpId)).start();
		}
	}
	
	class Run implements Runnable{
		Message m;
		String corpId;
		public Run(){};
		public Run(String corpId){
			m = handler.obtainMessage();
			this.corpId = corpId;
			bundle = new Bundle();
		};
		@Override
		public void run() {
		try {			
			String params=getGetCorpInfoByIdService().getCorpInfoById(corpId);
			if(params!=null){
				m.what=1;
				bundle.putString("params", params);
				m.setData(bundle);
				
			}else{
				m.what=0;
			}
		}catch (SocketTimeoutException e) {
			e.printStackTrace();
			m.what=-4;
		} catch (Exception e) {
			e.printStackTrace();
			m.what=-1;
		}finally{
			handler.sendMessage(m);
			GolbalUtil.onclicks = true;
		}
			
			
		}
		
	}
	
}
