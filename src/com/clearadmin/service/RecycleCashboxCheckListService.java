package com.clearadmin.service;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import com.entity.Box;
import com.entity.WebParameter;
import com.service.WebService;


//回收钞箱清点列表
public class RecycleCashboxCheckListService {
	
	public List<Box> getRecycleCashboxCheckList(String corpId) throws Exception{
		String methodName = "getRecycleCashboxCheckList";
		WebParameter[] param ={ 
				new WebParameter<String>("arg0",corpId)
				};
		SoapObject soap = WebService.getSoapObject(methodName, param);
		List<Box> list = null;
		if(soap.getProperty("code").toString().equals("00")){
			list = new ArrayList<Box>();
			String params = soap.getProperty("params").toString();
			String[] array =params.split("\r\n");
			for (int i = 0; i < array.length; i++) {
				Box box = new Box();
				String[] arr = array[i].split(";");
				box.setPlanNum(arr[0]);
				box.setBoxNum(arr[1]);
				box.setDate(arr[2]);
			    list.add(box);
			}
		}
		
		return list;
	}
}
