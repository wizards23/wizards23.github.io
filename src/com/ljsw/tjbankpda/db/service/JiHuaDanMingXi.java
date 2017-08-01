package com.ljsw.tjbankpda.db.service;

import org.ksoap2.serialization.SoapObject;

import com.example.app.entity.WebParameter;
import com.ljsw.tjbankpda.util.WebServiceFromThree;
import com.service.FixationValue;

public class JiHuaDanMingXi {
	public String getZhuangxiangJihuadanDetail(String planId) throws Exception{
		String mothedName = "getZhuangxiangJihuadanDetail";
		WebParameter[] param = { new WebParameter<String>("arg0", planId) };
		SoapObject soap=null;
		soap = WebServiceFromThree.getSoapObject(mothedName, param,
					FixationValue.NAMESPACE, FixationValue.URL5);
		System.out.println("----------getZhuangxiangJihuadanDetail:"+soap);
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		if(code.equals("00")){
			return params;
		}
		return null;
	}
}
