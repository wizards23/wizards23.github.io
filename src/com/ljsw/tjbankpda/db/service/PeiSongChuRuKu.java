package com.ljsw.tjbankpda.db.service;

import org.ksoap2.serialization.SoapObject;

import com.example.app.entity.WebParameter;
import com.ljsw.tjbankpda.util.WebServiceFromThree;
import com.service.FixationValue;

public class PeiSongChuRuKu {
//getPeisongChuku
	public String getPeisongChuku(String corpId,String typeId) throws Exception{
		String mothedName = "getPeisongChuku";
		System.out.println("corpId:"+corpId);
		System.out.println("typeId:"+typeId);
		WebParameter[] param = { new WebParameter<String>("arg0", corpId),
				new WebParameter<String>("arg1", typeId)};
		SoapObject soap=null;
		soap = WebServiceFromThree.getSoapObject(mothedName, param,
					FixationValue.NAMESPACE, FixationValue.URL5);
		System.out.println("----------getPeisongChuku:"+soap);
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		if(code.equals("00")){
			return params;
		}
		return null;
	}
	
	public String getWaibaoRuku(String corpId) throws Exception{
		String mothedName = "getWaibaoRuku";
		System.out.println("corpId:"+corpId);
		WebParameter[] param = { new WebParameter<String>("arg0", corpId)};
		SoapObject soap=null;
		soap = WebServiceFromThree.getSoapObject(mothedName, param,
					FixationValue.NAMESPACE, FixationValue.URL5);
		System.out.println("----------getPeisongChuku:"+soap);
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		if(code.equals("00")){
			return params;
		}
		return null;
	}
}
