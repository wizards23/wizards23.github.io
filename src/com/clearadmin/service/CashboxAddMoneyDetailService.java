package com.clearadmin.service;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.BoxDetail;
import com.entity.WebParameter;
import com.service.WebService;

public class CashboxAddMoneyDetailService {
	//获取钞箱加钞的钞箱明细
	
	/**
	 * 
	 * @param planNum  :加钞计划编号
	 * @return
	 * @throws Exception 
	 */
	public List<BoxDetail> getCashboxAddMoneyDetail(String planNum) throws Exception{
		String methodName = "getCashboxAddMoneyDetail";
		Log.i("钞箱加钞planNum", planNum);
		WebParameter[] param ={ 
				new WebParameter<String>("arg0",planNum),
				};
		SoapObject soap = WebService.getSoapObject(methodName, param);
		if(soap.getProperty("code").toString().equals("00")){
			List<BoxDetail> list = new ArrayList<BoxDetail>();
			
			String params = soap.getProperty("params").toString();
			String[] array = params.split("\r\n");
			for (int i = 0; i < array.length; i++) {
				BoxDetail box = new BoxDetail();
				String[] arr = array[i].split(";");
				box.setNum(arr[0]);
				box.setBrand(arr[1]);
				box.setMoney(arr[2]);
				list.add(box);
			}
			return list;
		}else{
			return null;
		}
	}
}
