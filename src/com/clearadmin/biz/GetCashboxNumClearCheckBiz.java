package com.clearadmin.biz;

import java.net.SocketTimeoutException;

import com.clearadmin.service.GetCashboxNumClearCheckService;
import com.golbal.pda.GolbalUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class GetCashboxNumClearCheckBiz {
	String boxNumClear;
	public Handler handler;
	private GetCashboxNumClearCheckService getCashboxNumClearCheck;			
	GetCashboxNumClearCheckService getGetCashboxNumClearCheck() {
		return getCashboxNumClearCheck=getCashboxNumClearCheck==null?new GetCashboxNumClearCheckService():getCashboxNumClearCheck;
	}

	/**
	 * 清分管理获取钞箱加钞数量和回收钞箱清点数量
	 * @param corpId  机构Id
	 */
	public void getClearNumBox(String corpId){
		//if(GolbalUtil.onclicks){
		//	GolbalUtil.onclicks = false;
			Thread t = new Thread(new Run(corpId));
			t.start();	
		//}
		
	}
	
	class Run implements Runnable{
		Message m;
		String corpId;
		public Run(){};
		public Run(String corpId){
			m = handler.obtainMessage();
			this.corpId = corpId;
		};
		@Override
		public void run() {
			try {
				boxNumClear = getGetCashboxNumClearCheck().getCashboxNumClearCheck(corpId);
				if(boxNumClear!=null && boxNumClear!=""){
					m.what=1;
					String[] arr = boxNumClear.split(";");
					Bundle bundle =new Bundle();
					bundle.putString("addmoney", arr[0]);
					bundle.putString("backmoney", arr[1]);
					m.setData(bundle);
				}else if(boxNumClear!=""){
					m.what=0;
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				m.what = -4;
			} catch (Exception e) {
				e.printStackTrace();
				m.what = -1;
			} finally{
				handler.sendMessage(m);
				GolbalUtil.onclicks = true;
			}
			
		}
		
	}
}
