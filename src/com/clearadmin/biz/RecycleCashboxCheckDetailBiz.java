package com.clearadmin.biz;

import java.net.SocketTimeoutException;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.clearadmin.service.RecycleCashboxCheckDetailService;
import com.entity.BoxDetail;
import com.golbal.pda.GolbalUtil;

public class RecycleCashboxCheckDetailBiz {
	//获取回收钞箱清点明细
	boolean onclick = true;
	public Handler handler;
	public static List<BoxDetail> list;	
	RecycleCashboxCheckDetailService recycleCashboxCheckDetail;
	 RecycleCashboxCheckDetailService getRecycleCashboxCheckDetail() {
		return recycleCashboxCheckDetail =recycleCashboxCheckDetail==null?new RecycleCashboxCheckDetailService():recycleCashboxCheckDetail;
	}

	/**
	 * 
	 * @param orderNum  业务单编号
	 */
	public void getboxDetailList(String orderNum){
		if(onclick){
		   Log.i("执行","执行");
		  onclick = false;	
		  Log.i("11","111");
		  Thread t = new Thread(new Run(orderNum));
		  Log.i("22","222");
		  t.start();
		  Log.i("33","333");
		}
	}

	class Run implements Runnable{
		String orderNum;
		Message m;
		public Run(){};
		public Run(String orderNum){
			 Log.i("44","444");
			this.orderNum = orderNum;
			 Log.i("55","555");
			m = handler.obtainMessage();
			 Log.i("66","666");
		};
		@Override
		public void run() {
			try {
				list=getRecycleCashboxCheckDetail().getRecycleCashboxCheckDetail(orderNum);
				if(list!=null){
					m.what=1;
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
				onclick = true;
			}
			
		}
		
	}
}
