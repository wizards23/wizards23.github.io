package com.out.biz;

import java.net.SocketTimeoutException;
import java.util.List;

import com.entity.ClearMoney;
import com.golbal.pda.GolbalUtil;
import com.out.service.CleanAtmAddMoneyService;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ClearMachineIngBiz {
	// 清机加钞
	public Handler handler;
	public static List<ClearMoney> list = null;
	public static String msg = "";

	CleanAtmAddMoneyService cleanAtmAddMoneyService;

	CleanAtmAddMoneyService getCleanAtmAddMoneyService() {
		if (cleanAtmAddMoneyService == null) {
			cleanAtmAddMoneyService = new CleanAtmAddMoneyService();
		}
		return cleanAtmAddMoneyService;
	}

	/**
	 * 
	 * @param cashboxNums
	 *            钞箱编号列表
	 * @param corpId
	 *            机构ID
	 * @param userId1
	 *            用户ID
	 * @param userId2
	 *            用户ID
	 */
	public void cleanAtmAddMoney(String cashboxNums, String corpId,
			String userId1, String userId2) {
		if (GolbalUtil.onclicks) {
			GolbalUtil.onclicks = false;
			Log.i("清机加钞业务", "清机加钞");
			new Thread(new Run(cashboxNums, corpId, userId1, userId2)).start();
		}
	}

	class Run implements Runnable {
		String cashboxNums;
		String corpId;
		String userId1;
		String userId2;
		Message m;

		public Run() {
		}

		public Run(String cashboxNums, String corpId, String userId1,
				String userId2) {
			this.cashboxNums = cashboxNums;
			this.corpId = corpId;
			this.userId1 = userId1;
			this.userId2 = userId2;
			m = handler.obtainMessage();
		}

		@Override
		public void run() {
			try {
				list = getCleanAtmAddMoneyService().cleanAtmAddMoney(
						cashboxNums, corpId, userId1, userId2);
				if (list != null && list.size() > 0) {
					m.what = 1;
				} else {
					m.what = 0;
				}
			} catch (SocketTimeoutException e) {
				m.what = -4;
				e.printStackTrace();
			} catch (Exception e) {
				m.what = -1;
				e.printStackTrace();
			} finally {
				GolbalUtil.onclicks = true;
				handler.sendMessage(m);
			}
		}

	}

}
