package com.out.biz;

import java.net.SocketTimeoutException;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.entity.BoxDetail;
import com.golbal.pda.GolbalUtil;
import com.imple.getnumber.WebJoin;
import com.out.service.CashboxNumByCorpIdService;

public class CashboxNumByCorpIdBiz {
	// 网点加钞钞箱交接-根据所在位置获取交接钞箱数量(加钞钞箱和回收钞箱数量相同)
	public static Handler handler;
	// public static int boxNum = 0;
	CashboxNumByCorpIdService cashboxNumByCorpId;
	public static String boxCount = "0";
	public static List<BoxDetail> list = null;

	CashboxNumByCorpIdService getCashboxNumByCorpId() {
		return cashboxNumByCorpId = cashboxNumByCorpId == null ? new CashboxNumByCorpIdService()
				: cashboxNumByCorpId;
	}

	/**
	 * 
	 * @param userId
	 *            机构ID
	 * @param planNum
	 *            计划编号
	 */
	public void gettCashboxNumByCorpId(String planNum, String corpId,
			String type) {
		if (GolbalUtil.onclicks) {
			GolbalUtil.onclicks = false;
			Thread t = new Thread(new Run(planNum, corpId, type));
			t.start();
			
		}

	}

	public void gettCashboxNumByATM(String planNum, String ATM, String type) {
		if (GolbalUtil.onclicks) {
			GolbalUtil.onclicks = false;
			Thread t = new Thread(new Run2(planNum, ATM, type));
			t.start();
			
		}

	}

	/**
	 * 网点交接钞箱钞箱查询
	 * 
	 * @author Administrator
	 * 
	 */
	class Run implements Runnable {
		String corpId;
		String planNum;
		Message m;
		String type;

		public Run() {
		};

		public Run(String planNum, String corpId, String type) {
			this.corpId = corpId;
			this.planNum = planNum;
			this.type = type;
			Log.i("handler", handler + "");
			m = handler.obtainMessage();
		}

		@Override
		public void run() {
			try {
				list = getCashboxNumByCorpId().getCashboxNumByCorpId(planNum,
						corpId, type);
				System.out.println("list----" + list.size());
				copy();
				if (list != null) {
					/**Modify begin by zhangxuewei at 01-DEC-2016
					 *返回type到活动，用来区分加钞or回收*/
					Bundle data= new Bundle();
					data.putString("type", type);
					m.setData(data);
					/**Modify end*/
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
				handler.sendMessage(m);
				GolbalUtil.onclicks = true;
			}

		}
	}

	/**
	 * 离行加钞钞箱查询
	 * @author Administrator
	 *
	 */
	class Run2 implements Runnable {
		String ATM;
		String planNum;
		Message m;
		String type;

		public Run2() {
		};

		public Run2(String planNum, String ATM, String type) {
			this.ATM = ATM;
			this.planNum = planNum;
			this.type = type;
			Log.i("handler", handler + "");
			m = handler.obtainMessage();
		}

		@Override
		public void run() {
			try {
				list = getCashboxNumByCorpId().getCashboxNumByATM(planNum, ATM,
						type);
				System.out.println("list----" + list.size());
				copy();
				if (list != null) {
					/**Modify begin by zhangxuewei at 12-DEC-2016
					 *返回type到活动，用来区分加钞or回收*/
					Bundle data= new Bundle();
					data.putString("type", type);
					m.setData(data);
					/**Modify end*/
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
				handler.sendMessage(m);
				GolbalUtil.onclicks = true;
			}

		}
	}

	private void copy() {
		WebJoin.map_atm.clear();
		WebJoin.list.clear();
		System.out.println("WebJoin.map_atm.size()----"
				+ WebJoin.map_atm.size());
		for (int i = 0; i < CashboxNumByCorpIdBiz.list.size(); i++) {
			String boxNum = CashboxNumByCorpIdBiz.list.get(i).getNum();
			String boxBrand = CashboxNumByCorpIdBiz.list.get(i).getBrand();
			Log.i("boxNum", boxNum + "");
			Log.i("boxBrand", boxBrand + "");
			WebJoin.map_atm.put(boxNum, boxBrand);
		}
		System.out.println("WebJoin.map_atm.size()2----"
				+ WebJoin.map_atm.size());
	}
}
