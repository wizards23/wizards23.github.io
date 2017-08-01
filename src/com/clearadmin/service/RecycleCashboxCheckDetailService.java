package com.clearadmin.service;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.BoxDetail;
import com.entity.WebParameter;
import com.service.WebService;

public class RecycleCashboxCheckDetailService {
	//获取回收钞箱清点明细
	
	/**
	 * 
	 * @param orderNum 业务单编号
	 * @return
	 * @throws Exception 
	 */
	public List<BoxDetail> getRecycleCashboxCheckDetail(String orderNum) throws Exception{
		String methodName = "getRecycleCashboxCheckDetail";
		Log.i("获取回收钞箱清点明细", "获取回收钞箱清点明细");
		WebParameter[] param ={ 
				new WebParameter<String>("arg0",orderNum),
				};
		SoapObject soap = WebService.getSoapObject(methodName, param);
		List<BoxDetail> list = null;
		if(soap.getProperty("code").toString().equals("00")){
			list = new ArrayList<BoxDetail>();
			String params = soap.getProperty("params").toString();
			Log.i("获取回收钞箱清点明细", params);
			String[] array = params.split("\r\n");
			for (int i = 0; i < array.length; i++) {
				String[] arr = array[i].split(";");
				BoxDetail box = new BoxDetail();
				box.setBrand(arr[1]);
				box.setNum(arr[0]);
				list.add(box);
			}
		}
		
		return list;
	}
}
