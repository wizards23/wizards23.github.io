package com.imple.getnumber;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import com.entity.BoxDetail;
import com.moneyboxadmin.service.GetBrandNameByCboxNumService;
import com.strings.tocase.CaseString;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import hdjc.rfid.operator.INotify;

public class WebJoin implements INotify {

	public static LinkedList<BoxDetail> list = new LinkedList<BoxDetail>();
	public static Handler handler;

	public static Map<String, String> map_atm = new HashMap<String, String>();
	private boolean have = true;
	private static String num; // 扫描返回的钞箱编号

	GetBrandNameByCboxNumService getBrandNameByCboxNumService;

	GetBrandNameByCboxNumService getGetBrandNameByCboxNumService() {
		if (getBrandNameByCboxNumService == null) {
			getBrandNameByCboxNumService = new GetBrandNameByCboxNumService();
		}
		return getBrandNameByCboxNumService;
	}

	@Override
	public void getNumber(String number) {
		if (number == null && !CaseString.reg(number)) {
			return;
		}

		Log.i("getnumber", number);

		// 把扫描到的编号转化为钞箱编号
		num = CaseString.getBoxNum(number);
		if ("".equals(num))
			return;
		// 如果相等表示已经添加过，则返回
		for (int i = 0; i < list.size(); i++) {
			if (num.equals(list.get(i).getNum())) {
				return;
			}
		}

		Message m = handler.obtainMessage();
		BoxDetail box = new BoxDetail();

		for (Map.Entry<String, String> item : map_atm.entrySet()) {
			String key = item.getKey();
			String value = item.getValue();
			Log.i("key", key + "");
			// 是有效数据
			if (key.equals(num)) {
				box.setNum(num);
				box.setBrand(value);
				list.add(box);
				have = false;
				break;
			}
		}

		if (have) {
			box.setNum(num);
			box.setBrand("无效钞箱");
			list.add(box);
		}
		m.what = 1;
		handler.sendMessage(m);
		have = true;
	}

}
