package com.moneyboxadmin.service;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.BoxDetail;
import com.entity.WebParameter;
import com.service.WebService;

public class GetBoxDetailListService {
	
	/**
	 * ATM加钞出库，钞箱装钞入库，回收钞箱入库钞箱,未清回收钞箱明细
	 * @param planNum  计划编号
	 * @param type  ATM加钞出库 type=1;钞箱装钞入库 type=2;回收钞箱入库 type=3
	 * @return
	 * @throws Exception
	 */
	public List<BoxDetail> getCashBoxDetail(String planNum,String type) throws Exception {
	
			String methodName = "getCashBoxDetail";

			Log.i("planNum", planNum);
			Log.i("type", type);
			
			@SuppressWarnings("rawtypes")
			WebParameter[] parameter = {
				new WebParameter<String>("arg0",planNum),
				new WebParameter<String>("arg1",type) 
			};
			SoapObject soapObject = WebService.getSoapObject(methodName, parameter);
			
			String state = soapObject.getProperty("msg").toString();
			String params = soapObject.getProperty("params").toString();
			if("成功".equals(state)  && !params.equals("anyType{}")){
				List<BoxDetail> list = new ArrayList<BoxDetail>();
				
				String[] array = params.split("\r\n");
				for (int j = 0; j < array.length; j++) {
					String[] arr = array[j].split(";");
					BoxDetail box = new BoxDetail();
					box.setBrand(arr[0]);
					box.setNum(arr[1]);
					Log.i("brand", arr[0]);
					Log.i("num", arr[1]);
					list.add(box);
				}
				
				return list;
			}else{
				return null;
			}
			
	}
	
}
