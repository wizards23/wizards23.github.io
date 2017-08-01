package com.out.service;

import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.Organization;
import com.entity.WebParameter;
import com.service.WebService;


   //清机加钞获取机构信息和柜员编号
public class GetCorpInfoByIdService {
	public String getCorpInfoById(String corpId) throws Exception{
		String methodName = "getCorpInfoById";
		WebParameter[] param ={ 
				new WebParameter<String>("arg0",corpId)
				};
		Log.i("corpId", corpId);
		String str = null;
		SoapObject soap = WebService.getSoapObject(methodName, param);
		
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		if("00".equals(code) && !params.equals("anyType{}")){
			 str= params;
		}
		return str;
	}
	
}
