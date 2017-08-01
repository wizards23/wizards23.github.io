package com.o.service;

import org.ksoap2.serialization.SoapObject;

import com.entity.WebParameter;
import com.example.app.entity.User;
import com.service.WebService;

public class LoginService {
	public User Login(String name,String pwd) throws Exception{
		User user=null;
		String mothedName="login";
		WebParameter[] param={
				new WebParameter<String>("arg0", name),
				new WebParameter<String>("arg1", pwd)
		};
		SoapObject soap=WebService.getSoapObject(mothedName, param);
		System.out.println(soap);
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		//params=系统管理;总行清算中心;1;988030000; }
		if(code.equals("00")){
			String[] split = params.split(";");
			user=new User();
			user.setJigouname(split[0]);
			user.setUsername(split[1]);
			user.setUserid(split[2]);
			user.setJigouid(split[3]);
		}else{
			return null;
		}
		return user;
	}
	
	/**
	 * 指纹验证失败3次调用的接口
	 * @param corpId 机构Id
	 * @param roleId 角色ID（可以为空）
	 * @param cValue 特征值
	 * @param userId 登录帐号
	 * @param pwd 密码
	 * @return
	 * @throws Exception
	 */
	public User checkFingerprint(String corpId,String roleId,byte[] cValue,String userId,String pwd) throws Exception{
		User user = null;
		String mothedName="checkFingerprint";
		WebParameter[] param={
				new WebParameter<String>("arg0", corpId),
				new WebParameter<String>("arg1", roleId),
				new WebParameter<byte[]>("arg2", cValue),
				new WebParameter<String>("arg3", userId),
				new WebParameter<String>("arg4", pwd)
		};
		SoapObject soap=WebService.getSoapObject2(mothedName, param);
		System.out.println("指纹验证失败3次调用的接口------"+soap);
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();		
		if(code.equals("00")){
			String[] split = params.split(";");
			user=new User();
			user.setUserid(split[0]);
			user.setUsername(split[1]);
		}else if(code.equals("99")&&msg.length()>10){// 要显示登录失败的时候用到的
			user=new User();
			user.setUsername(msg);
		}else if(code.equals("99")){
			return null;
		}
		return user;
	}
}
