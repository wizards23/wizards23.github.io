package com.moneyboxadmin.biz;

import java.net.SocketTimeoutException;

import com.entity.Finger;
import com.entity.User;
import com.golbal.pda.GolbalUtil;
import com.moneyboxadmin.service.BankDoublePersonLoginService;
import com.moneyboxadmin.service.SaveCashboxHandoverService;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

//指纹验证登陆
public class FingerCheckBiz {

	public Handler hand_finger;
	public int num = 0;
	User user;
	Message m;
	Bundle bundle;

	private GolbalUtil getUtil;

	public GolbalUtil getGetUtil() {
		return getUtil = getUtil == null ? new GolbalUtil() : getUtil;
	}

	/**
	 * 
	 * @param finger
	 *            手指对象
	 */
	public void fingerLoginCheck(Finger finger) {
		if (getGetUtil().onclicks) {
			getGetUtil().onclicks = false;
			AsyncTask asynctask = new AsyncTaskFinger(finger);
			asynctask.execute();

		}
	}

	/**
	 * 双人登录获取指纹验证结果
	 * 
	 * @author Administrator
	 * 
	 */
	private class AsyncTaskFinger extends AsyncTask {
		Finger finger;

		public AsyncTaskFinger() {
		};

		public AsyncTaskFinger(Finger finger) {
			this.finger = finger;
			bundle = new Bundle();
			m = hand_finger.obtainMessage();
		};

		// 取消任务后的操作
		@Override
		protected void onCancelled() {
			super.onCancelled();
			getGetUtil().onclicks = true;
			hand_finger.sendMessage(m);
		}

		// 异步完成后的操作
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			getGetUtil().onclicks = true;
			// 如果验证通过
			if (user != null) {
				bundle.putString("username", user.getName()); // 用户名
				bundle.putString("userid", user.getId()); // 用户编号
				num++;
				bundle.putInt("num", num);
				m.setData(bundle);
				m.what = 1;

			} else {
				m.what = 0;
			}
			hand_finger.sendMessage(m);
		}

		// 异步后台操作
		@Override
		protected Object doInBackground(Object... arg0) {
			try {
				user = BankDoublePersonLoginService.checkFingerprint(finger);
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				m.what = -4;
				// 取消任务
				this.cancel(true);
			} catch (Exception e) {
				m.what = -1;
				e.printStackTrace();
				this.cancel(true);
				// 取消任务

			}
			return null;
		}
	}

}
