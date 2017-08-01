package com.moneyboxadmin.biz;

import java.net.SocketTimeoutException;
import java.util.LinkedList;

import com.entity.ClearBox;
import com.golbal.pda.GolbalUtil;
import com.moneyboxadmin.service.GetCashBoxListService;
import com.moneyboxadmin.service.GetEmptyRecycleCashboxListService;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class GetEmptyRecycleCashboxInListBiz {
	// 已清回收钞箱入库
	public Handler hand_backbox;
	public static LinkedList<ClearBox> list;

	private GolbalUtil golbalUtil;

	public GolbalUtil getGolbalUtil() {
		return golbalUtil = golbalUtil == null ? new GolbalUtil() : golbalUtil;
	}

	/**
	 * 获取数据
	 * 
	 * @param corpid
	 */
	public void getemptyback(String corpid) {
		if (getGolbalUtil().onclicks) {
			AsyncTaskGetBiz asyn = new AsyncTaskGetBiz(corpid);
			asyn.execute();
			getGolbalUtil().onclicks = false;
		}
	}

	/**
	 * 获取数据
	 * 
	 * @author Administrator
	 * 
	 */
	private class AsyncTaskGetBiz extends AsyncTask {
		String corpId;
		Message m;

		public AsyncTaskGetBiz() {
		};

		public AsyncTaskGetBiz(String corpId) {
			this.corpId = corpId;
			m = hand_backbox.obtainMessage();
		};

		// 取消任务后的操作
		@Override
		protected void onCancelled() {
			super.onCancelled();
			getGolbalUtil().onclicks = true;
			// m.what = -1;
			hand_backbox.sendMessage(m);
		}

		// 异步完成后的操作
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			getGolbalUtil().onclicks = true;
			m.what = 1;
			if (list == null) {
				m.what = 0;
			}
			hand_backbox.sendMessage(m);
		}

		// 异步后台操作
		@Override
		protected Object doInBackground(Object... arg0) {
			try {
				list = GetEmptyRecycleCashboxListService
						.getEmptyRecycleCashboxInList(corpId);
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				m.what = -4;
				this.cancel(true);
			} catch (Exception e) {
				e.printStackTrace();
				m.what = -1;
				this.cancel(true);
			}
			return null;
		}
	}

}
