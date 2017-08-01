package com.out.service;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.ATM;
import com.entity.WebParameter;
import com.service.WebService;

public class AtmDetailService {
	//派工单明细中的ATM明细信息
	
	/**
	 * 
	 * @param planNum   加钞计划编号
	 * @return
	 * @throws Exception
	 */
	public List<ATM> getAtmDetail(String planNum) throws Exception{
		String methodName = "getAtmDetail";
		WebParameter[] param ={ 
				new WebParameter<String>("arg0",planNum)
				};
		Log.i("ATM明细信息", planNum);
		SoapObject soap = WebService.getSoapObject(methodName, param);
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		List<ATM> list = null;
		if("00".equals(code) && !"anyType{}".equals(params)){
			list = new ArrayList<ATM>();
			if(params.indexOf("\r\n")>0){
				String[] array = params.split("\r\n");
				for (int i = 0; i < array.length; i++) {
					String[] arr = array[i].split(";");
					ATM atm = new ATM();
					atm.setAtmNum(arr[0]);
					atm.setAddress(arr[1]);
					atm.setBoxNum(arr[2]);
					list.add(atm);
				}				
			}else{
				String[] array = params.split(";");
				ATM atm = new ATM();
				atm.setAtmNum(array[0]);
				atm.setAddress(array[1]);
				atm.setBoxNum(array[2]);
				list.add(atm);
			}
									
		}
		
		
		return list;
	}
}
