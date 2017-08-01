package com.out.biz;

import java.net.SocketTimeoutException;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.entity.JoinInfo;
import com.golbal.pda.GolbalUtil;
import com.out.service.CashboxHandoverInfoService;

public class CashboxHandoverInfoBiz {
	//交接情况
	public Handler handler;
	public static List<JoinInfo> list = null;
	
	CashboxHandoverInfoService cashboxHandoverInfo;
    CashboxHandoverInfoService getCashboxHandoverInfo() {
		return cashboxHandoverInfo=cashboxHandoverInfo==null?new CashboxHandoverInfoService():cashboxHandoverInfo;
	}

	/**
	 * 
	 * @param planNum  加钞计划编号
	 * @return
	 * @throws Exception
	 */
	public void getJionInfo(String planNum){
		if(GolbalUtil.onclicks){
			Thread t = new Thread(new Run(planNum));
			t.start();	
			GolbalUtil.onclicks = false;
		}
		
	}
	
	
	class Run implements Runnable{
		String planNum;
		Message m;
		public Run (){};
		public Run (String planNum){
			this.planNum = planNum;
			m = handler.obtainMessage();
		};
		@Override
		public void run() {
			try {
				list = getCashboxHandoverInfo().getCashboxHandoverInfo(planNum);
				Log.i("list",list+"");
				if(list!=null){
				   m.what=1;
				}else{
				   m.what=0;
				}
			}catch (SocketTimeoutException e) {
				
				e.printStackTrace();
				m.what=-4;
			} catch (Exception e) {
				Log.i("出错了","出错了");
				e.printStackTrace();
				m.what=-1;
			}finally{
				handler.sendMessage(m);
				GolbalUtil.onclicks = true;
			}
			
		}
		
		
	}
}
