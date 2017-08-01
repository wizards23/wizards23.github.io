package com.clearadmin.biz;

import java.net.SocketTimeoutException;
import java.util.List;

import com.clearadmin.service.CashboxAddMoneyDetailService;
import com.entity.BoxDetail;
import com.golbal.pda.GolbalUtil;
import com.imple.getnumber.AddMoneygetNum;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CashboxAddMoneyDetailBiz {
	
	private CashboxAddMoneyDetailService cashboxAddMoneyDetail;
	CashboxAddMoneyDetailService getCashboxAddMoneyDetail() {
		return cashboxAddMoneyDetail=cashboxAddMoneyDetail==null?new CashboxAddMoneyDetailService():cashboxAddMoneyDetail;
	}

	//获取钞箱加钞的钞箱明细
	public Handler handler;
	public static List<BoxDetail> list;
	
	
	/**
	 * 
	 * @param planNum  计划编号
	 */
	public void getBoxAddMoneyDetail(String planNum){
		if(GolbalUtil.onclicks){
			Thread t = new Thread(new Run(planNum));
			t.start();
			GolbalUtil.onclicks = false;
		}
		
	}
	
		
	class Run implements Runnable{
		Message m;
		String planNum;
		
		public Run(){};
		public Run(String planNum){
			this.planNum = planNum;
			m = handler.obtainMessage();
		};
		@Override
		public void run() {
			try {
				list = getCashboxAddMoneyDetail().getCashboxAddMoneyDetail(planNum);
				if(list!=null){
					m.what = 1;
					createTthem();
				}else{
					m.what=0;
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
	
	
	//钞箱明细临时数据
		 private void createTthem(){
			 AddMoneygetNum.map.clear();
				for (int i = 0; i < list.size(); i++) {
					try {
						int money = (int) Double.parseDouble(list.get(i).getMoney());
						//如果是0万的不算进有效数据
						if(money!=0){
							AddMoneygetNum.map.put(list.get(i).getNum(),list.get(i));	
						}
					} catch (Exception e) {
						e.printStackTrace();
					}									
				}
				Log.i("map临时",AddMoneygetNum.map.size()+"");
			}
}
