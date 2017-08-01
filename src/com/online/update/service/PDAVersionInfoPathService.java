package com.online.update.service;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.OrderDetail;
import com.entity.WebParameter;
import com.service.FixationValue;
import com.service.WebService;

public class PDAVersionInfoPathService {
	//返回配置文件路径	
	public String getpath() throws Exception{
		
		String methodName = "getPDAVersionInfoPath";
		WebParameter[] param =new WebParameter[0];
		System.out.println("获取配置文件路径");
		
		SoapObject soap = WebService.getSoapObject(methodName, param);
		System.out.println(""+soap);
		String params = soap.getProperty("params").toString();		
		return params.toString();
		
	}
}
