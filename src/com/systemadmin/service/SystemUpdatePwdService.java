package com.systemadmin.service;

import java.net.SocketTimeoutException;

import org.ksoap2.serialization.SoapObject;

import com.entity.SystemUser;
import com.entity.WebParameter;
import com.loginsystem.biz.SystemLoginBiz;
import com.service.WebService;

public class SystemUpdatePwdService {


	/**
	 * 修改密码 pwd 密码 newPwd 新密码 confirmPwd 确认密码
	 * @throws Exception 
	 */
	public static boolean updatePwd(String userid,String pwd,String newpwd) throws Exception {

		System.out.println("修改密码传参");
		System.out.println("userid:"+userid);
		System.out.println("pwd:"+pwd);
		System.out.println("newpwd:"+newpwd);
		String method = "changePassword";
		// 封装参数
		WebParameter[] Paras = {
				new WebParameter<String>("arg0",userid),
				new WebParameter<String>("arg1", pwd),
				new WebParameter<String>("arg2", newpwd) };
	
			SoapObject soapObject = WebService.getSoapObject(method, Paras);
			if(soapObject.getProperty("code").toString().equals("00")){
				return true;
			}
			return false;
	}
}
