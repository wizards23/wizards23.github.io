package com.clearadmin.service;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.WebParameter;
import com.service.WebService;

public class GetCashboxNumClearCheckService {
	//清分管理获取钞箱加钞数量和回收钞箱清点数量
	
	/**
	 * 
	 * @param corpId  机构ID
	 * @return
	 * @throws Exception
	 */
	public String getCashboxNumClearCheck(String corpId) throws Exception{
		Log.i("corpId", corpId);
		String methodName = "getCashboxNumClearCheck";
		WebParameter[] param ={ 
				new WebParameter<String>("arg0",corpId),
				};
		SoapObject soap = WebService.getSoapObject(methodName, param);
		
		if(soap.getProperty("code").toString().equals("00")){
			return soap.getProperty("params").toString();
		}
		return null;
	}
}
