package com.o.service;

import org.ksoap2.serialization.SoapObject;

import com.entity.WebParameter;
import com.service.WebService;


/**
 * 出库操作服务类
 * 
 * @author lenovo
 * 
 */
public class ChuKuCaoZuoSerivce {
	public String saveBoxSendOutEarly(String lineNum, String boxes,
			String date, String corpId, String roleId, byte[] cValue,
			String userIds) throws Exception {

		String methodName = "saveBoxSendOutEarly";
		WebParameter[] param = { new WebParameter<String>("arg0", lineNum),
				new WebParameter<String>("arg1", boxes),
				new WebParameter<String>("arg2", date),
				new WebParameter<String>("arg3", corpId),
				new WebParameter<String>("arg4", roleId),
				new WebParameter<byte[]>("arg5", cValue),
				new WebParameter<String>("arg6", userIds), };
		SoapObject soap = null;
			soap = WebService.getSoapObject2(methodName, param);
		System.out.println("[soap:----]" + soap);
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		// String params = soap.getProperty("params").toString();
		if (code.equals("00")) {
			System.out.println("返回 : "+msg);
			return code;
		}
		return null;
	}
	
	public String saveBoxSendOutEarly(String lineNum, String boxes,
			String date, String corpId, String roleId, byte[] cValue,
			String userIds, String userId, String pwd) throws Exception {
		String methodName = "saveBoxSendOutEarly";
		WebParameter[] param = { new WebParameter<String>("arg0", lineNum),
				new WebParameter<String>("arg1", boxes),
				new WebParameter<String>("arg2", date),
				new WebParameter<String>("arg3", corpId),
				new WebParameter<String>("arg4", roleId),
				new WebParameter<byte[]>("arg5", cValue),
				new WebParameter<String>("arg6", userIds),
				new WebParameter<String>("arg7", userId),
				new WebParameter<String>("arg8", pwd)};
		
		System.out.println("[bbbbbb]交接的传参:" + lineNum + boxes + date + corpId
				+ roleId + cValue + userIds+userId+pwd);
		SoapObject soap = null;
			soap = WebService.getSoapObject2(methodName, param);
		System.out.println("[soap:----]" + soap);
		String code = soap.getProperty("code").toString();
		//String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("msg").toString();
		
		return code+"_"+params;
		
	}

	public String saveBoxStorageLate(String lineNum, String boxes,
			String corpId, String roleId, byte[] cValue, String userIds,
			String userId, String pwd) throws Exception {
		String methodName = "saveBoxStorageLate";
		WebParameter[] param = { new WebParameter<String>("arg0", lineNum),
				new WebParameter<String>("arg1", boxes),
				new WebParameter<String>("arg2", corpId),
				new WebParameter<String>("arg3", roleId),
				new WebParameter<byte[]>("arg4", cValue),
				new WebParameter<String>("arg5", userIds),
				new WebParameter<String>("arg6", userId),
				new WebParameter<String>("arg7", pwd),};
		System.out.println("-------saveBoxStorageLate传参："+lineNum);
		System.out.println("-------saveBoxStorageLate传参："+boxes);
		System.out.println("-------saveBoxStorageLate传参："+corpId);
		System.out.println("-------saveBoxStorageLate传参："+roleId);
		System.out.println("-------saveBoxStorageLate传参："+cValue);
		System.out.println("-------saveBoxStorageLate传参："+userIds);
		System.out.println("-------saveBoxStorageLate传参："+userId);
		System.out.println("-------saveBoxStorageLate传参："+pwd);
		SoapObject soap = null;
			soap = WebService.getSoapObject2(methodName, param);
		System.out.println("[soap:----]" + soap);
		String code = soap.getProperty("code").toString();
		if(code.equals("00")){
			System.out.println("成功");
			return code;
		}
		return null;
	}
}
