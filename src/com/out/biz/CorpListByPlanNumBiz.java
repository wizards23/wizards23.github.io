package com.out.biz;

import java.net.SocketTimeoutException;
import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.entity.Organization;
import com.golbal.pda.GolbalUtil;
import com.moneyboxadmin.pda.BoxDoDetail;
import com.out.admin.pda.OrderWork;
import com.out.service.CashboxHandoverInfoService;
import com.out.service.CorpListByPlanNumService;

public class CorpListByPlanNumBiz {
	// 根据加钞计划编号获取机构列表
	CorpListByPlanNumService corpListByPlanNum;

	CorpListByPlanNumService getCorpListByPlanNum() {
		return corpListByPlanNum = corpListByPlanNum == null ? new CorpListByPlanNumService()
				: corpListByPlanNum;
	}

	public Handler handler;
	public static List<Organization> list;

	/**
	 * 
	 * @param planNum
	 *            计划编号
	 */
	public void getCorpList(String planNum,String type) {
		if (GolbalUtil.onclicks) {
			Thread t = new Thread(new Run(planNum,type));
			t.start();
			GolbalUtil.onclicks = false;
		}

	}

	class Run implements Runnable {
		String planNum;
		String type;
		Message m;

		public Run() {
		};

		public Run(String planNum,String type) {
			this.planNum = planNum;
			this.type=type;
			m = handler.obtainMessage();
		};

		@Override
		public void run() {
			try {
				if("在行式".equals(OrderWork.type))   //在行式交接查询网点
					list = getCorpListByPlanNum().getCorpListByPlanNum(planNum);
				else    //离行式交接查询ATM
					list=getCorpListByPlanNum().getATMListByPlanNum(planNum,type);
				if (list != null) {
					m.what = 1;
				} else {
					m.what = 0;
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				m.what = -4;
			} catch (Exception e) {
				e.printStackTrace();
				m.what = -1;
			} finally {
				Log.i("m机构列表", m.what + "");
				handler.sendMessage(m);
				GolbalUtil.onclicks = true;
			}
		}

	}

}
