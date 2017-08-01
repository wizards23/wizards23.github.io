package com.systemadmin.service;

import org.ksoap2.serialization.SoapObject;

import com.entity.WebParameter;
import com.service.WebService;

public class GetNameNumByFingerService {
	//PDA用户指纹录入
	
	/**
	 * 
	 * @param userId  用户ID
	 * @return
	 * @throws Exception
	 */
	public String getUserInfoById(String userId) throws Exception{
		String methodName = "fingerprintInput";
		WebParameter[] param ={ 
				new WebParameter<String>("arg0",userId)
				};
		SoapObject soap = WebService.getSoapObject(methodName, param);
		if(soap.getProperty("code").toString().equals("00")){
			return soap.getProperty("params").toString();
		}
		return null;
			
		}
		
		
}
