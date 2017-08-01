package com.moneyboxadmin.service;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.Finger;
import com.entity.User;
import com.entity.WebParameter;
import com.service.WebService;


public class BankDoublePersonLoginService {
	/**
	 * 库管员登陆
	 */
	public static User checkFingerprint(Finger finger) throws Exception {
			
			String methodName = "checkFingerprint";
			@SuppressWarnings("rawtypes")
			WebParameter[] parameter = {
					new WebParameter<String>("arg0",finger.getCorpId()),
					new WebParameter<String>("arg1", finger.getRoleId()),
					new WebParameter<byte[]>("arg2",finger.getcValue())
			     };
			Log.i("arg0",finger.getCorpId());
			Log.i("arg1",finger.getRoleId())	;	
			Log.i("arg2",finger.getcValue()+"")	;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < finger.getcValue().length; i++) {
				sb.append(finger.getcValue()[i]);
			}
			Log.i("arg2",sb+"")	;
			SoapObject soapObject = WebService.getSoapObject(methodName,parameter);
			Log.i("soapObject", soapObject+"");
			
			// 封装数据
			String code = soapObject.getProperty("code").toString().trim();
			String params = soapObject.getProperty("params").toString().trim();
			
			User user =null;
			if(code.equals("00") && !params.equals("anyType{}")){
				user = new User();
				String[] arr = params.split(";");								
				Log.i("code", code+"");
				Log.i("id", arr[0]+"");	
				Log.i("name", arr[1]+"");
								
				user.setId(arr[0]);
				user.setName(arr[1]);				
			}		
			return user;					
	        }	
}
