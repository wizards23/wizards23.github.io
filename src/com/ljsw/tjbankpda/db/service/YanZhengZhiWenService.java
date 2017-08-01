package com.ljsw.tjbankpda.db.service;

import org.ksoap2.serialization.SoapObject;

import com.entity.SystemUser;
import com.example.app.entity.WebParameter;
import com.ljsw.tjbankpda.qf.service.QingfenRenwuService;
import com.ljsw.tjbankpda.util.WebServiceFromThree;
import com.service.FixationValue;

public class YanZhengZhiWenService {
	public SystemUser checkFingerprint(String corpId, String roleId,
			byte[] cValue) throws Exception {
		System.out.println("进入方法");
		SystemUser user = null;
		String mothedName = "checkFingerprint";
		WebParameter[] param = { new WebParameter<String>("arg0", corpId),
				new WebParameter<String>("arg1", roleId),
				new WebParameter<byte[]>("arg2", cValue) };
		System.out.println("----------checkFingerprint传参:" + corpId + ":"
				+ roleId + ":" + cValue);
		SoapObject soap = null;
		soap = WebServiceFromThree.getSoapObject(mothedName, param,
				FixationValue.NAMESPACE, FixationValue.URL);
		System.out.println("----------checkFingerprint:" + soap);
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		if (code.equals("00")) {
			// 如果登录成功 同时获取 
			String[] split = params.split(";");
			user = new SystemUser();
			user.setLoginUserId(split[0]);
			user.setLoginUserName(split[1]);
			return user;
		}
		return null;
	}
	
	/**
	 * 清分员专用双人登录接口
	 * @param corpId
	 * @param roleId
	 * @param cValue
	 * @return
	 * @throws Exception
	 */
	public SystemUser checkFingerprint2(String corpId, String roleId,
			byte[] cValue) throws Exception {
		SystemUser user = null;
		String mothedName = "checkFingerprint";
		WebParameter[] param = { new WebParameter<String>("arg0", corpId),
				new WebParameter<String>("arg1", roleId),
				new WebParameter<byte[]>("arg2", cValue) };
		System.out.println("----------checkFingerprint传参:" + corpId + ":"
				+ roleId + ":" + cValue);
		SoapObject soap = null;
		soap = WebServiceFromThree.getSoapObject(mothedName, param,
				FixationValue.NAMESPACE, FixationValue.URL);
		System.out.println("----------checkFingerprint:" + soap);
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		if (code.equals("00")) {
			// 如果登录成功 同时获取 清分员所在小组
			String[] split = params.split(";");
			String qingfenXiaozuId= getQingfenXiaozu(split[0]);
			user = new SystemUser();
			user.setYonghuZhanghao(split[0]);
			user.setLoginUserName(split[1]);
			user.setXiaozu(qingfenXiaozuId);
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
