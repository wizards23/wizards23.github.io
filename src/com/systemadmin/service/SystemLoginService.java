package com.systemadmin.service;


import org.ksoap2.serialization.SoapObject;


import android.util.Log;

import com.entity.SystemUser;
import com.entity.WebParameter;
import com.service.WebService;

/**
 * 用户登录　webservice
 * 
 * @author Administrator
 * 
 */
public class SystemLoginService {

	// 系统登陆业务
	public static SystemUser system_login(String name, String pwd)
			throws Exception {
		Log.i("name", name);
		Log.i("pwd", pwd);
		// 封装参数
		WebParameter[] Paras = { 
				new WebParameter<String>("arg0", name),
				new WebParameter<String>("arg1",pwd)
				};
		// 调用方法
		SoapObject soapObject = WebService.getSoapObject("login", Paras);
		Log.i("2222222", "222222222");
		SystemUser user = null;
		String code = soapObject.getProperty("code").toString();
		Log.i("code", code);
		String params = soapObject.getProperty("params").toString();
		String msg = soapObject.getProperty("msg").toString();
		Log.i("params", params);
		if (code.equals("00")) {
			// 登陆成功
			user = new SystemUser();
			String[] array = params.split(";");
			Log.i("length", array.length+"");
			user.setLoginUserName(array[0]);
			user.setOrganizationName(array[1]);			
			user.setLoginUserId(array[2]);
			user.setOrganizationId(array[3]);
			user.setYonghuZhanghao(name);
			
		}else if(code.equals("99")){
			user = new SystemUser();
			user.setLoginUserName(msg);//占时通知前台的显示信息 并不是真正的用户
			
		}
		
		return user;
	}
}
