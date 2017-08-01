package com.out.biz;

import java.net.SocketTimeoutException;
import java.util.List;

import android.os.Handler;
import android.os.Message;

import com.entity.ATM;
import com.golbal.pda.GolbalUtil;
import com.out.service.AtmDetailService;

public class AtmDetailBiz {
	
	 AtmDetailService atmDetail;		
	 AtmDetailService getAtmDetail() {
	 return atmDetail=atmDetail==null?new AtmDetailService():atmDetail;
	 	}

	 public static List<ATM> list = null;
	 public Handler handler;
	 
	 /**
	  * 计划编号
	  * @param planNum
	  */
	 public void getAtmDetail(String planNum){
		 if(GolbalUtil.onclicks){
			GolbalUtil.onclicks = false; 
			new Thread(new Run(planNum)).start();
		 }
	 }
	 
	class Run implements Runnable{
		String planNum;
		Message m;
		public Run(){}
		
		public Run(String planNum){
			this.planNum = planNum;
			m = handler.obtainMessage();
		}
		@Override
		public void run() {
			try {
				list=getAtmDetail().getAtmDetail(planNum);
				if(list!=null){
					m.what = 1;
				}else{
					m.what = 0;
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
		
		
	}
}
