package com.ljsw.tjbankpda.db.service;

import org.ksoap2.serialization.SoapObject;

import com.entity.SystemUser;
import com.example.app.entity.WebParameter;
import com.ljsw.tjbankpda.qf.service.QingfenRenwuService;
import com.ljsw.tjbankpda.util.WebServiceFromThree;
import com.ljsw.tjbankpda.yy.application.S_application;
import com.service.FixationValue;

public class SecondLogin {
	public SystemUser login(String userId,String pwd) throws Exception{
		String mothedName = "login";
		WebParameter[] param = { new WebParameter<String>("arg0", userId),
				new WebParameter<String>("arg1", pwd)};
		SoapObject soap=null;
		soap = WebServiceFromThree.getSoapObject(mothedName, param,
					FixationValue.NAMESPACE, FixationValue.URL);
		System.out.println("----------login:"+soap);
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		S_application.wrong = msg;
		SystemUser user = null;
		if(code.equals("00")){
			user = new SystemUser();
			String[] array = params.split(";");
			user.setLoginUserName(array[0]);
			user.setOrganizationName(array[1]);
			user.setLoginUserId(array[2]);
			user.setOrganizationId(array[3]);
			user.setYonghuZhanghao(userId);
			String qingfenXiaozu=getQingfenXiaozu(userId);
			user.setXiaozu(qingfenXiaozu);
			return user;
		}
		return null;
	}
	
	/**
	 * 获取清分员所属小组
	 * 
	 * @param userId
	 * @throws Exception
	 */
	private String getQingfenXiaozu(String useId) throws Exception {
		System.out.println("调用获取清分小组接口---userId：" + useId);
		QingfenRenwuService qs = new QingfenRenwuService();

		String params = qs.getQingfenXiaozu(useId);
		System.out.println("返回清分员小组：" + params);
		if (params != null) {
			return params;
		}
		
		return null;
	}
}
