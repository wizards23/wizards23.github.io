package com.clearadmin.biz;

import java.net.SocketTimeoutException;

import android.os.Handler;
import android.os.Message;

import com.clearadmin.service.UpdateAndGetAddMoneySumService;
import com.entity.BoxDetail;
import com.golbal.pda.GolbalUtil;

public class GetAddMoneySumBiz {
//钞箱加钞-加钞操作-扫描钞箱编号
	
	
	
	UpdateAndGetAddMoneySumService updateAndGetAddMoneySum;
	UpdateAndGetAddMoneySumService getUpdateAndGetAddMoneySum() {
		return updateAndGetAddMoneySum==updateAndGetAddMoneySum?new UpdateAndGetAddMoneySumService():updateAndGetAddMoneySum;
	}
	
	public static BoxDetail box;
	public Handler handler;

	/**
	 * 
	 * @param planNum  加钞计划编号
	 * @param cashboxNum  钞箱编号
	 * @param userId1  用户编号1
	 * @param userId2  用户编号2
	 * @param corpId  机构编号
	 */
	public void getupdateAndGetAddMoneySum(String planNum,String cashboxNum,String userId1,String userId2,String corpId){
		if(GolbalUtil.onclicks){
			GolbalUtil.onclicks = false;
			new Thread(new Run(planNum, cashboxNum, userId1, userId2, corpId)).start();
		}
	}
	
	
	class Run implements Runnable{
		String planNum;
		String cashboxNum;
		String userId1;
		String userId2;
		String corpId;
		Message m;
		public Run(){};
		public Run(String planNum,String cashboxNum,String userId1,String userId2,String corpId){
			this.planNum = planNum;
			this.cashboxNum = cashboxNum;
			this.userId1= userId1;
			this.userId2 =userId2;
			this.corpId= corpId;
			m = handler.obtainMessage();
		}
		@Override
		public void run() {
		try {
			box = getUpdateAndGetAddMoneySum().updateAndGetAddMoneySum(planNum, cashboxNum, userId1, userId2, corpId);
			if(box!=null){
			m.what = 1;	  //有数据
			}else{
				m.what =0;	//无数据	
			}
		}catch (SocketTimeoutException e) {
			m.what = -4;	//异常
			e.printStackTrace();
		} catch (Exception e) {
			m.what = -1;	//异常
			e.printStackTrace();
		}finally{
			handler.sendMessage(m);
			GolbalUtil.onclicks = true;
		}
			
		}
		
	}
}
