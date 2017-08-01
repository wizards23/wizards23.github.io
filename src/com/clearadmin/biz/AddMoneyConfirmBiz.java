package com.clearadmin.biz;

import java.net.SocketTimeoutException;

import com.clearadmin.service.AddMoneyConfrimService;
import com.golbal.pda.GolbalUtil;

import android.os.Handler;
import android.os.Message;

public class AddMoneyConfirmBiz {
	//出库操作
	public Handler handler;
	AddMoneyConfrimService dddMoneyConfrim;			
	AddMoneyConfrimService getDddMoneyConfrim() {
		return dddMoneyConfrim=dddMoneyConfrim==null?new AddMoneyConfrimService():dddMoneyConfrim;
	}
	/**
	 * 
	 * @param planNum 计划编号
	 * @param cashboxNum 钞箱编号
	 * @param userId  用户编号
	 * @param corpId  机构ID
	 * @param bagNum1   钱捆编号
	 * @param bagNum2
	 * @param bagNum3
	 * @param bagNum4
	 * @param bagNum5
	 * @param bagNum6
	 */
	public void addMoneyConfrim(String planNum,String cashboxNum,String userId1,String userId2,String corpId,
		String bagNum1,String bagNum2,String bagNum3,String bagNum4,String bagNum5,String bagNum6){
		if(GolbalUtil.onclicks){
			GolbalUtil.onclicks = false;		
		Thread t = new Thread(new Run(planNum, cashboxNum, userId1, userId2,corpId, bagNum1, bagNum2, bagNum3, bagNum4, bagNum5, bagNum6));
		t.start();
		}
	}
	

	class Run implements Runnable{
		Message m;
		String planNum;
		String cashboxNum;
		String userId1;
		String userId2;
		String corpId;
		String bagNum1;
		String bagNum2;
		String bagNum3;
		String bagNum4;
		String bagNum5;
		String bagNum6;
		public Run(){};
		public Run(String planNum,String cashboxNum,String userId1,String userId2,String corpId,
	    String bagNum1,String bagNum2,String bagNum3,String bagNum4,String bagNum5,String bagNum6){
		m = handler.obtainMessage();	
		 this.planNum =planNum;
		 this.cashboxNum=cashboxNum;
		 this.userId1=userId1;
		 this.userId2=userId2;
		 this.corpId=corpId;
		 this.bagNum1=bagNum1;
		 this.bagNum2=bagNum2;
		 this.bagNum3=bagNum3;
		 this.bagNum4=bagNum4;
		 this.bagNum5=bagNum5;
		 this.bagNum6=bagNum6;	
		};
		@Override
		public void run() {
		try {
			if(getDddMoneyConfrim().addMoneyConfirm(planNum, cashboxNum, userId1,userId2, corpId, bagNum1, bagNum2, bagNum3, bagNum4, bagNum5, bagNum6)){
				m.what=1;				
			}else{
				m.what = 0;
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
