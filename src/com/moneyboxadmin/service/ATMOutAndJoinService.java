package com.moneyboxadmin.service;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.User;
import com.entity.WebParameter;
import com.service.WebService;

public class ATMOutAndJoinService {
	
	/**
	 * 
	 * @param cbInfo cbInfo:加钞计划编号;业务单号;操作方式;钞箱品牌信息;钞箱编号;钞箱状态;审核员ID;复核员ID;机构ID（多列时用|分隔）
	 * @param corpId 机构ID
	 * @param roleId 角色ID
	 * @param cValue 特征值 
	 * @return
	 * @throws Exception
	 */
	public User saveAtmAddMoneyOut(String cbInfo,String corpId,String roleId,byte[] cValue,String jiachao1,String jiachao2) throws Exception{
		String methodName = "atmAddMoneyOut2";
	
		WebParameter[] parameter = {
				new WebParameter<String>("arg0",cbInfo),
				new WebParameter<String>("arg1",corpId),
				new WebParameter<String>("arg2",roleId),
				new WebParameter<byte[]>("arg3",cValue),
				new WebParameter<String>("arg4",jiachao1),
				new WebParameter<String>("arg5",jiachao2)
		     };
		
		Log.i("cbInfo", cbInfo);
		Log.i("corpId", corpId);
		Log.i("roleId", roleId);
		Log.i("cValue", cValue+"");
		StringBuffer sb = new StringBuffer();		
		SoapObject soap = WebService.getSoapObject(methodName,parameter);
		String code = soap.getProperty("code").toString();
		User user =null;
		if(code.equals("00")){
			String params = soap.getProperty("params").toString();
			user = new User();
			String[] arr = params.split(";");
			user.setId(arr[0]);
			user.setName(arr[1]);
		}
		return user;
		
	}
	
}
