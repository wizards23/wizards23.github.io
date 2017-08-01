package com.clearadmin.biz;

import java.net.SocketTimeoutException;
import java.util.List;

import android.os.Handler;
import android.os.Message;

import com.clearadmin.service.RecycleCashboxCheckListService;
import com.entity.Box;
import com.golbal.pda.GolbalUtil;

public class RecycleCashboxCheckListBiz {
	
	//回收钞箱清点列表
	public static List<Box> list;
	public Handler handler;
	
	private RecycleCashboxCheckListService recycleCashboxCheckList;		
	RecycleCashboxCheckListService getRecycleCashboxCheckList() {
		return recycleCashboxCheckList =recycleCashboxCheckList==null?new RecycleCashboxCheckListService():recycleCashboxCheckList;
	}

	/**
	 * 
	 * @param corpId 机构Id;
	 */
	public void gettRecycleCashboxCheckList(String corpId){
		if(GolbalUtil.onclicks){
			GolbalUtil.onclicks = false;
		Thread t = new Thread(new Run(corpId));
		t.start();
		}
	}
	
	class Run implements Runnable{
		String corpId;
		Message m;
		public Run(){};
		public Run(String corpId){
			this.corpId =corpId;
			m = handler.obtainMessage();
		};
		
		@Override
		public void run() {
		 try {
			 list= getRecycleCashboxCheckList().getRecycleCashboxCheckList(corpId);
			 if(list!=null){
				 m.what=1;
			 }else{
				 m.what = 0;
			 }
		}catch (SocketTimeoutException e) {
			m.what = -4;
		} catch (Exception e) {
			m.what = -1;
		}finally{
			handler.sendMessage(m);
			GolbalUtil.onclicks = true;
		}	
			
		}
		
	}
	
}
