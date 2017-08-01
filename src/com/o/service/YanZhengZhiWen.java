package com.o.service;

import org.ksoap2.serialization.SoapObject;

import android.os.Handler;

import com.entity.WebParameter;
import com.example.app.entity.User;
import com.service.WebService;

public class YanZhengZhiWen {
//	public User yanzhengfinger(String corpId,String roleId,byte[] cValue) throws Exception{
//		User user=null;
//		String mothedName="checkFingerprint";
//		WebParameter[] param={
//				new WebParameter<String>("arg0", corpId),
//				new WebParameter<String>("arg1", roleId),
//				new WebParameter<byte[]>("arg2", cValue)
//		};
//		SoapObject soap=WebService.getSoapObject(mothedName, param);
//		System.out.println("YanZhengZhiWen-------------传参："+corpId+"==="+roleId);
//		System.out.println("YanZhengZhiWen-------------"+soap);
//		String code = soap.getProperty("code").toString();
//		String msg = soap.getProperty("msg").toString();
//		String params = soap.getProperty("params").toString();
//		if("00".equals(code)){
//			String[] split=params.split(";");
//			user=new User();
//			user.setUserzhanghu(split[0]);
//			user.setUsername(split[1]);
//		}else{
//			return null;
//		}
//		
//		return user;
//		
//	}
	
	
	public User yanzhengfinger(String corpId,String roleId,byte[] cValue) throws Exception{
		User user=null;
		String mothedName="checkFingerprint";
		WebParameter[] param={
				new WebParameter<String>("arg0", corpId),
				new WebParameter<String>("arg1", roleId),
				new WebParameter<byte[]>("arg2", cValue)
		};
		SoapObject soap=WebService.getSoapObject(mothedName, param);
		System.out.println("YanZhengZhiWen-------------传参："+corpId+"==="+roleId);
		System.out.println("YanZhengZhiWen-------------"+soap);
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		if("00".equals(code)){
			String[] split=params.split(";");
			user=new User();
			user.setUserzhanghu(split[0]);
			user.setUsername(split[1]);
		}else{
			return null;
		}
		
		return user;
		
	}
}
