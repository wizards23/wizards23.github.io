package com.clearadmin.biz;

import java.net.SocketTimeoutException;

import android.os.Handler;
import android.os.Message;

import com.clearadmin.service.GetRecycleCashboxCheckInfoService;
import com.entity.BoxDetail;
import com.golbal.pda.GolbalUtil;

public class GetRecycleCashboxCheckInfoBiz {
	//回收钞箱清点-扫描钞箱
	public static BoxDetail box;
	public Handler handler;
	
	GetRecycleCashboxCheckInfoService RecycleCashboxCheckInfo;	
	 GetRecycleCashboxCheckInfoService getRecycleCashboxCheckInfo() {
		return RecycleCashboxCheckInfo=RecycleCashboxCheckInfo==null?new GetRecycleCashboxCheckInfoService():RecycleCashboxCheckInfo;
	}

	 /**
	  * 
	  * @param orderNum  业务单编号
	  * @param cashboxNum  钞箱编号
	  * @param userId1  用户id
	  * @param userId2 用户id
	  * @param corpId  机构ID
	  */
	public void getrecycleCashboxCheckInfo(String orderNum,String cashboxNum,String userId1,String userId2,String corpId){
		if(GolbalUtil.onclicks){
		 GolbalUtil.onclicks = false;
		 new Thread(new Run(orderNum, cashboxNum, userId1, userId2, corpId)).start();		 
		}
	}

	class Run implements Runnable{
		String orderNum;
		String cashboxNum;
		String userId1;
		String userId2;
		String corpId;
		Message m;
		public Run(){}
		public Run(String orderNum,String cashboxNum,String userId1,String userId2,String corpId){
			this.orderNum = orderNum;
			this.cashboxNum = cashboxNum;
			this.userId1 = userId1;
			this.userId2 = userId2;
			this.corpId = corpId;
			m = handler.obtainMessage();
		}
		
		@Override
		public void run() {
		try {
			box=getRecycleCashboxCheckInfo().updateAndGetRecycleCashboxCheckInfo(orderNum, cashboxNum, userId1, userId2, corpId);
			if(box!=null){
			   m.what = 1;
			}else{
			   m.what=0;
			}
		}catch (SocketTimeoutException e) {
			e.printStackTrace();
			m.what = -4;
		} catch (Exception e) {
			e.printStackTrace();
			m.what = -1;
		}finally{
			handler.sendMessage(m);
			GolbalUtil.onclicks = true;
		}
			
		}
		
		
		
		
	};
}
