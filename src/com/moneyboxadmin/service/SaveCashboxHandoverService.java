package com.moneyboxadmin.service;

import org.ksoap2.serialization.SoapObject;

import com.entity.WebParameter;
import com.service.WebService;

public class SaveCashboxHandoverService {
	//ATM加钞出库押运人员交接
	public boolean saveCashboxHandover(String planNum,String userId) throws Exception{
		
		String methodName = "saveCashboxHandover";
		@SuppressWarnings("rawtypes")
		WebParameter[] parameter = {
				new WebParameter<String>("arg0",planNum),
				new WebParameter<String>("arg1",userId)
		     };
				
		SoapObject soap = WebService.getSoapObject(methodName,parameter);
		if("00".equals(soap.getProperty("code").toString())){
			 return true;
		}else{
			return false;
		}
		
	}
}
