package com.systemadmin.service;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.WebParameter;
import com.service.WebService;

public class FingerGatherService {
	//指纹采集
	/**
	 * 
	 * @param userId   用户编号
	 * @param finger   手指
	 * @param cValue   特征值1
	 * @param cValue1      特征值1
	 * @return
	 * @throws Exception 
	 */
	public boolean fingerprintInput(String userId,String finger,byte[] cValue,byte[] cValue1) throws Exception{
		String methodName = "fingerprintInput";
		WebParameter[] param ={ 
				new WebParameter<String>("arg0",userId),
				new WebParameter<String>("arg1",finger),
				new WebParameter<byte[]>("arg2",cValue),
				new WebParameter<byte[]>("arg3",cValue1),
				};
		
		Log.i("arg0", userId+"");
		Log.i("arg1", finger+"");
		Log.i("arg2", cValue+"");
		Log.i("arg3", cValue1+"");
		
	
		SoapObject soap = WebService.getSoapObject(methodName, param);
		String code = soap.getProperty("code").toString();
		Log.i("code",code);
		if("00".equals(code)){
			return true;
		}
		return false;
			
		}
		

	
}
