package com.systemadmin.service;

import org.ksoap2.serialization.SoapObject;

import com.entity.WebParameter;
import com.service.WebService;

//采集指纹输入用户编号获取用户姓名
public class GettheNameByIdService {

	public static String getName(String userId) throws Exception {
		// 封装参数
		WebParameter[] Paras = { new WebParameter<String>("arg0", userId) };

		SoapObject soapObject = WebService.getSoapObject("getUserInfoById",Paras);
		String code = soapObject.getProperty("code").toString();
		String params = soapObject.getProperty("params").toString();
		if (code.trim().equals("00") && !"anyType{}".equals(params)){
			return params;
		}else{
		return null;	
		}
			
		
			
	}
}
