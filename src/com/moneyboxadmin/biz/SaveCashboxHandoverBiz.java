package com.moneyboxadmin.biz;

import java.net.SocketTimeoutException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.golbal.pda.GolbalUtil;
import com.moneyboxadmin.service.SaveCashboxHandoverService;

public class SaveCashboxHandoverBiz {
	//ATM加钞出库押运人员交接
	public Handler handler;
			
	 SaveCashboxHandoverService saveCashboxHandover;	  	  
	  public SaveCashboxHandoverService getSaveCashboxHandover() {
		return saveCashboxHandover=saveCashboxHandover==null?new SaveCashboxHandoverService():saveCashboxHandover;
	}

	public void superJoin(String planNum,String userId){
		if(GolbalUtil.onclicks){
			Log.i("11111111", "1111111111");
			GolbalUtil.onclicks = false;
			Thread t = new Thread(new Run(planNum,userId));
			t.start();
		}
	}
	  
	  

	class Run implements Runnable{
		String planNum;
		String userId;
		Message m;
		boolean is;
		
		public Run(){}
		public Run(String planNum,String userId){
			this.planNum = planNum;
			this.userId = userId;
			m = handler.obtainMessage();
		}
		@Override
		public void run() {
			try {
				is =getSaveCashboxHandover().saveCashboxHandover(planNum, userId);	
				if(is){
					m.what=1;
				}else{
					m.what=0;
				}
			}catch (SocketTimeoutException e) {
				   m.what=-4;
			} catch (Exception e) {
				   m.what=-1;
			}finally{
			handler.sendMessage(m);
			GolbalUtil.onclicks = true;
			}
			
		}				
	}
}
