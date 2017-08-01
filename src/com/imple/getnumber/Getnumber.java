package com.imple.getnumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;

import com.entity.BoxDetail;
import com.moneyboxadmin.biz.BrandNameByCboxNumBiz;
import com.moneyboxadmin.biz.GetBoxDetailListBiz;
import com.moneyboxadmin.pda.BoxDoDetail;
import com.strings.tocase.CaseString;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import hdjc.rfid.operator.INotify;

public class Getnumber implements INotify {

	// 空钞箱出库,ATM加钞出库，钞箱装钞入库，回收钞箱入库钞箱明细,已清回收钞箱明细 过滤
	public static Handler handler_getNum;
	public static List<BoxDetail> list_boxdeatil = new ArrayList<BoxDetail>();
	public static Map<String, BoxDetail> map = new HashMap<String, BoxDetail>();

	Message m = null;
	private static String str;
	boolean is = true;
	private boolean have = true;

	int num = 0;

	private GetBoxDetailListBiz boxDetailListBiz;

	GetBoxDetailListBiz getBoxDetailListBiz() {
		return boxDetailListBiz == null ? new GetBoxDetailListBiz()
				: boxDetailListBiz;
	}

	private BrandNameByCboxNumBiz brandNameByCboxNumBiz;

	BrandNameByCboxNumBiz getBrandNameByCboxNumBiz() {
		if (brandNameByCboxNumBiz == null) {
			brandNameByCboxNumBiz = new BrandNameByCboxNumBiz();
		}
		return brandNameByCboxNumBiz;
	}

	@Override
	public void getNumber(String number) {
		Log.i("getnumbe--扫描返回的编号", number);
		if (number == null || !CaseString.reg(number)) {
			return;
		}

		// 十进制转换成字符
		str = CaseString.getBoxNum(number);
		if ("".equals(str))
			return;
		Log.i("转换后的钞箱编号-----", str);
		// 空钞箱过滤
		if (BoxDoDetail.bussin.equals("空钞箱出库")) {
			emptycheck(str);
			// ATM加钞出库，钞箱装钞入库，回收钞箱入库钞箱明细,已清回收钞箱明细
		} else {
			temp();
			check(str);
		}

	}

	// 空钞箱明细过滤
	public void emptycheck(String str) {
		if ("".equals(str))
			return;
		BoxDetail box;
		if (list_boxdeatil.size() == 0) {
			box = new BoxDetail();
			box.setBrand("正在获取...");
			box.setNum(str);
			list_boxdeatil.add(box);
			m = handler_getNum.obtainMessage();
			m.what = 1;
			handler_getNum.sendMessage(m);
			// 通过猜钞箱获取品牌
			getBrandNameByCboxNumBiz().getBrnadNamebyNum(str, "空钞箱出库");
		}

		for (int i = 0; i < list_boxdeatil.size(); i++) {
			Log.i("list_boxdeatil.get(i).getNum()", list_boxdeatil.get(i)
					.getNum());
			Log.i("num", str);
			if (list_boxdeatil.get(i).getNum().equals(str)) {
				is = false;
				break;
			}
		}

		if (is) {
			box = new BoxDetail();
			box.setBrand("正在获取...");
			box.setNum(str);
			list_boxdeatil.add(box);
			m = handler_getNum.obtainMessage();
			m.what = 1;
			handler_getNum.sendMessage(m);
			// 通过猜钞箱获取品牌
			getBrandNameByCboxNumBiz().getBrnadNamebyNum(str, "空钞箱出库");
		}
		is = true;

	}

	// ATM加钞出库，钞箱装钞入库，回收钞箱入库钞箱明细,已清回收钞箱明细 过滤
	public void check(String str) {
		Log.i("str", str);
		Log.i("list_boxdeatil", list_boxdeatil.size() + "");

		// 如果已经添加过，则返回
		for (int i = 0; i < list_boxdeatil.size(); i++) {
			if (str.equals(list_boxdeatil.get(i).getNum())) {
				return;
			}
		}

		// 从map中遍历，检查是否有重复数据，如果没有就
		for (Entry<String, BoxDetail> it : map.entrySet()) {
			Log.i("it.getKey()", it.getKey());
			// 如果是有效数据
			if (it.getKey().equals(str)) {
				list_boxdeatil.add(it.getValue());
				have = false;
				break;
			}
		}

		// 无效的钞箱
		if (have) {
			BoxDetail box = new BoxDetail();
			box.setBrand("无效钞箱");
			box.setNum(str);
			list_boxdeatil.add(box);
		}
		m = handler_getNum.obtainMessage();
		m.what = 1;
		handler_getNum.sendMessage(m);
		have = true;
	}

	// 把从后台得到的钞箱明细数据转移到map集合中，提高遍历速度，同时保持原数据
	public void temp() {
		Log.i("getBoxDetailListBiz().list", getBoxDetailListBiz().list.size()
				+ "");
		if (map.size() <= 0) {
			for (int i = 0; i < getBoxDetailListBiz().list.size(); i++) {
				String num = getBoxDetailListBiz().list.get(i).getNum();
				map.put(getBoxDetailListBiz().list.get(i).getNum(),
						getBoxDetailListBiz().list.get(i));
			}
		}
		Log.i("map", map.size() + "");
	}

}
