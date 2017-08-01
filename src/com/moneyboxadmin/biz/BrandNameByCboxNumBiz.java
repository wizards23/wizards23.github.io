package com.moneyboxadmin.biz;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.golbal.pda.GolbalUtil;
import com.imple.getnumber.Getnumber;
import com.imple.getnumber.StopNewClearBox;
import com.moneyboxadmin.service.GetBrandNameByCboxNumService;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

//根据编号获取品牌
public class BrandNameByCboxNumBiz {
	public static Handler handler;
	Bundle bundle;
	String brand = null;

	// 线程池
	ExecutorService pool = Executors.newFixedThreadPool(2);

	GetBrandNameByCboxNumService getBrandNameByCboxNumService;

	GetBrandNameByCboxNumService getGetBrandNameByCboxNumService() {
		if (getBrandNameByCboxNumService == null) {
			getBrandNameByCboxNumService = new GetBrandNameByCboxNumService();
		}
		return getBrandNameByCboxNumService;
	}

	/**
	 * 通过钞箱编号获取钞箱品牌
	 * 
	 * @param CboxNum
	 *            钞箱编号
	 * @param type
	 *            类型（调用者）
	 */
	public void getBrnadNamebyNum(String CboxNum, String type) {
		pool.execute(new Thread(new Run(CboxNum, type)));

	}

	class Run implements Runnable {
		String CboxNum;
		String type;
		Message m;

		public Run() {
		}

		public Run(String CboxNum, String type) {
			this.CboxNum = CboxNum;
			this.type = type;
			m = handler.obtainMessage();
		}

		@Override
		public void run() {
			try {
				brand = getGetBrandNameByCboxNumService()
						.getBrandNameByCboxNum(CboxNum);
				Log.i("brand", brand);
				m.what = 1;
				if (brand != null) {
				} else {
					// m.what=0;
					brand = "找不到品牌";
				}
			} catch (Exception e) {
				// m.what=-1;
				brand = "点击获取品牌";
			} finally {
				// 把品牌添加进集合,再发送通知
				if ("空钞箱出库".equals(type)) {
					addBrand(CboxNum);
				} else {
					addBrandOther(CboxNum);
				}
				handler.sendMessage(m);
			}
		}
	}

	// 把品牌添加进集合，空钞箱
	private void addBrand(String CboxNum) {
		for (int i = 0; i < Getnumber.list_boxdeatil.size(); i++) {
			String num = Getnumber.list_boxdeatil.get(i).getNum();
			if (num.equals(CboxNum)) {
				Getnumber.list_boxdeatil.get(i).setBrand(brand);
				break;
			}
		}
	}

	// 把品牌添加进集合，空钞箱
	private void addBrandOther(String CboxNum) {
		for (int i = 0; i < StopNewClearBox.list.size(); i++) {
			String num = StopNewClearBox.list.get(i).getNum();
			if (num.equals(CboxNum)) {
				StopNewClearBox.list.get(i).setBrand(brand);
				break;
			}
		}
	}
}
