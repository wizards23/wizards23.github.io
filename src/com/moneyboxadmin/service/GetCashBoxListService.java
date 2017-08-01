package com.moneyboxadmin.service;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.R.integer;
import android.util.Log;

import com.entity.Box;
import com.entity.WebParameter;
import com.service.WebService;

public class GetCashBoxListService {

	/**
	 * 空钞箱出库,ATM加钞出库，钞箱装钞入库，回收钞箱入库列表，钞箱加钞列表
	 * 
	 * @param corpId
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public List<Box> getCashBoxListResult(String corpId, String type)
			throws Exception {

		String methodName = "getCashBoxList";

		WebParameter[] parameter = { new WebParameter<String>("arg0", corpId),
				new WebParameter<String>("arg1", type) };

		Log.i("corpId", corpId);
		Log.i("type", type);

		SoapObject soapObject = WebService.getSoapObject(methodName, parameter);
		String state = soapObject.getProperty("msg").toString();
		String params = soapObject.getPropertyAsString("params");
		Log.i("state", state);
		if ("失败".equals(state) || params.equals("anyType{}")) {
			Log.i("什么也没有", "什么也没有");
			return null;
		}
		String[] array = params.split("\r\n");
		List<Box> list = new ArrayList<Box>();
		for (int i = 0; i < array.length; i++) {
			String[] arr = array[i].split(";");
			Box b = new Box();
			b.setPlanNum(arr[0]);
			b.setWay(arr[1]);
			b.setType(arr[2]);
			b.setMoney((Double.parseDouble(arr[3]) / 10000.0) + "");
			b.setBoxNum(arr[4]);
			b.setDate(arr[5]);
			b.setIsFirst(arr[6]);
			b.setState(state);
			list.add(b);

		}

		Log.i("code", state);
		Log.i("params", params);

		return list;
	}

}
