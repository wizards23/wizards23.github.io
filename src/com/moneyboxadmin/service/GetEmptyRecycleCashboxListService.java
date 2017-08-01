package com.moneyboxadmin.service;

import java.util.LinkedList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.ClearBox;
import com.entity.WebParameter;
import com.service.WebService;

public class GetEmptyRecycleCashboxListService {
	
	//已清回收钞箱入库列表
	public static LinkedList<ClearBox> getEmptyRecycleCashboxInList(String corpId) throws Exception{
		
		String methodName = "getEmptyRecycleCashboxInList";

		WebParameter[] parameter = {
				new WebParameter<String>("arg0", corpId),
				};

		SoapObject soapObject = WebService.getSoapObject(methodName, parameter);
		Log.i("corpId",corpId);
		String state = soapObject.getProperty("msg").toString();
		String params = soapObject.getPropertyAsString("params");
		LinkedList<ClearBox> list = null;
		if("成功".equals(state) && !params.equals("anyType{}")){
			String[] array = params.split("\r\n");
		    list = new LinkedList<ClearBox>(); 
			for (int i = 0; i < array.length; i++) {
				ClearBox box = new ClearBox();
				String[] arr = array[i].split(";");
				box.setBizNum(arr[0]);
				box.setClearDate(arr[1]);
				box.setBoxNum(arr[2]);
				list.addLast(box);
			}
			
		}
		return list;
	}
}
