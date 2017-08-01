package com.clearadmin.service;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.WebParameter;
import com.service.WebService;

public class AddMoneyConfrimService {
	//钞箱加钞确定操作
	/**
	 * 
	 * @param planNum  计划编号
	 * @param cashboxNum 钞箱编号
	 * @param userId   用户编号
	 * @param corpId   机构ID
	 * @param bagNum1 钱捆编号
	 * @param bagNum2
	 * @param bagNum3
	 * @return
	 * @throws Exception 
	 */
	public boolean addMoneyConfirm(String planNum,String cashboxNum,String userId1,String userId2,String corpId,
			String bagNum1,String bagNum2,String bagNum3,String bagNum4,String bagNum5,String bagNum6) throws Exception{
			String methodName = "updateAndAddMoneyConfirm";
			WebParameter[] param ={ 
				new WebParameter<String>("arg0",planNum),
				new WebParameter<String>("arg1",cashboxNum),
				new WebParameter<String>("arg2",userId1),
				new WebParameter<String>("arg3",userId2),
				new WebParameter<String>("arg4",corpId),
				new WebParameter<String>("arg5",bagNum1),
				new WebParameter<String>("arg6",bagNum2),
				new WebParameter<String>("arg7",bagNum3),
				new WebParameter<String>("arg8",bagNum4),
				new WebParameter<String>("arg9",bagNum5),
				new WebParameter<String>("arg10",bagNum6),
				};
		Log.i("arg0", planNum);
		Log.i("arg1", cashboxNum);
		Log.i("arg2", userId1);
		Log.i("arg3", userId2);
		Log.i("arg4", corpId);
		Log.i("arg5", bagNum1+"");
		Log.i("arg6", bagNum2+"");
		Log.i("arg7", bagNum3+"");
		Log.i("arg8", bagNum4+"");
		Log.i("arg9", bagNum5+"");
		Log.i("arg10", bagNum6+"");
		
		SoapObject soap = WebService.getSoapObject(methodName, param);
		if(soap.getProperty("code").toString().equals("00")){
			return true;
		}
		return false;
	}
}
