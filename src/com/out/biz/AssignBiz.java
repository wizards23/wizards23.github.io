package com.out.biz;

import java.net.SocketTimeoutException;
import java.util.List;

import android.os.Handler;
import android.os.Message;

import com.entity.OrderDetail;
import com.golbal.pda.GolbalUtil;
import com.out.service.AssignService;

public class AssignBiz {
	//根据押运人员ID获取派工单列表
	
	public Handler handler;
	public static OrderDetail order;
	AssignService assign;		
	AssignService getAssign() {
		return assign=assign==null?new AssignService():assign;
	}
	
	
	/**
	 * 
	 * @param userId 用户ID 
	 */
	public void getAssig(String userId,String type){
		//if(GolbalUtil.onclicks){
			Thread t = new Thread(new Run(userId,type));
			t.start();	
			//GolbalUtil.onclicks = false;
	//	}
		
	}
	

	class Run implements Runnable{
		String userId;
		String type;
		Message m;
		public Run(){};
		public Run(String userId,String type){
			this.userId = userId;
			this.type=type;
			m = handler.obtainMessage();
		}
		@Override
		public void run() {
			try {
				order = getAssign().getAssign(userId,type);
				if(order!=null){
					m.what=1;  //有数据
				}else{
					m.what=0;   //无数据
				}
			}catch (SocketTimeoutException e) {
				m.what=-4;   //异常
			} catch (Exception e) {
				m.what=-1;   //异常
			}finally{
				handler.sendMessage(m);
			//	GolbalUtil.onclicks = true;
			}
			
		}				
	}
}
