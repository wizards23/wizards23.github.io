package com.ljsw.tjbankpda.db.service;

import java.util.List;

import org.ksoap2.serialization.SoapObject;

import com.example.app.entity.WebParameter;
import com.ljsw.tjbankpda.util.WebServiceFromThree;
import com.service.FixationValue;

public class ShangJiaoChuKu {

	public String getShangjiaochukuMingxi(String corpId) throws Exception {
		String mothedName = "getShangjiaochukuMingxi";
		WebParameter[] param = { new WebParameter<String>("arg0", corpId) };
		SoapObject soap=null;
			soap = WebServiceFromThree.getSoapObject(mothedName, param,
						FixationValue.NAMESPACE, FixationValue.URL5);
		System.out.println("----------getShangjiaochukuMingxi:"+soap);
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		if(code.equals("00")){
			return params;
		}
		return null;
	}
}
