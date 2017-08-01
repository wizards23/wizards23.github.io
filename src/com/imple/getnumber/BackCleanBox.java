package com.imple.getnumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clearadmin.biz.RecycleCashboxCheckDetailBiz;
import com.clearadmin.pda.BackMoneyBoxCountDo;
import com.entity.BoxDetail;
import com.strings.tocase.CaseString;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import hdjc.rfid.operator.INotify;

public class BackCleanBox implements INotify {
	// 回收钞箱清点

	public static Handler handler;
	public static List<String> yiQingfenList = new ArrayList<String>();
	public static Map<String, BoxDetail> map = new HashMap<String, BoxDetail>();
	Bundle bundle;
	// public static boolean isall = false;
	int mapcount = 0;

	@Override
	public void getNumber(String number) {
		if (BackMoneyBoxCountDo.hadfindbox || number == null
				|| !CaseString.reg(number)) {
			return;
		}

		String num = CaseString.getBoxNum(number);
		if (yiQingfenList.contains(num) || "".equals(num)) {
			return;
		}
		// 临时数据
		if (map.size() <= 0 && mapcount <= 0) {
			mapcount++;
			for (int i = 0; i < RecycleCashboxCheckDetailBiz.list.size(); i++) {
				map.put(RecycleCashboxCheckDetailBiz.list.get(i).getNum(),
						RecycleCashboxCheckDetailBiz.list.get(i));
			}
		}

		Log.i("map", map.size() + "");
		Message m = handler.obtainMessage();
		for (Map.Entry<String, BoxDetail> item : map.entrySet()) {
			String key = item.getKey();
			Log.i("key", key);
			if (key.trim().equals(num.trim())) {
				m.what = 1;
				break;
			}
		}

		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putString("boxnum", num);
		m.setData(bundle);
		handler.sendMessage(m);

	}

}
