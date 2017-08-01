package com.ljsw.tjbankpda.db.service;

import org.ksoap2.serialization.SoapObject;

import com.example.app.entity.WebParameter;
import com.ljsw.tjbankpda.util.WebServiceFromThree;
import com.service.FixationValue;

public class QingLingZhuangXiangRuKu {
	public String getQinglingRuku(String corpId) throws Exception{
		String mothedName = "getQinglingRuku";
		WebParameter[] param = { new WebParameter<String>("arg0", corpId) };
		SoapObject soap=null;
		soap = WebServiceFromThree.getSoapObject(mothedName, param,
					FixationValue.NAMESPACE, FixationValue.URL5);
		System.out.println("----------getQinglingRuku:"+soap);
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		if(code.equals("00")){
			return params;
		}
		return null;
	}
}
