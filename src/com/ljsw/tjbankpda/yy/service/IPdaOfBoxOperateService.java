package com.ljsw.tjbankpda.yy.service;

import org.ksoap2.serialization.SoapObject;
import com.example.app.entity.User;
import com.example.app.entity.WebParameter;
import com.ljsw.tjbankpda.util.WebServiceFromThree;
import com.service.FixationValue;

/**
 * 押运服务类
 * @author Administrator
 *
 */
public class IPdaOfBoxOperateService {
	/**
	 * 指纹验证
	 * @param corpId
	 * @param roleId
	 * @param cValue
	 * @return
	 * @throws Exception
	 */
	public User checkFingerprint(String corpId,String roleId,byte[] cValue) throws Exception{
		System.out.println("进指纹验证checkFingerprint");
		User user = null;
		String mothedName="checkFingerprint";
		WebParameter[] param={
				new WebParameter<String>("arg0", corpId),
				new WebParameter<String>("arg1", roleId),
				new WebParameter<byte[]>("arg2", cValue)
		};
		SoapObject soap = WebServiceFromThree.getSoapObject(mothedName, param, FixationValue.NAMESPACE, FixationValue.URL2);
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		if("00".equals(code)){
			String[] split=params.split(";");
			user=new User();
			user.setUserzhanghu(split[0]);
			user.setUsername(split[1]);
		}else{
			return null;
		}	
		return user;
		
	}
	
	/*public User yanzhengfinger(String corpId,String roleId,byte[] cValue) throws Exception{
		User user=null;
		String mothedName="checkFingerprint";
		WebParameter[] param={
				new WebParameter<String>("arg0", corpId),
				new WebParameter<String>("arg1", roleId),
				new WebParameter<byte[]>("arg2", cValue)
		};
		SoapObject soap=WebService2.getSoapObject(mothedName, param);
		System.out.println("YanZhengZhiWen-------------传参："+corpId+"==="+roleId);
		System.out.println("YanZhengZhiWen-------------"+soap);
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		if("00".equals(code)){
			String[] split=params.split(";");
			user=new User();
			user.setUserzhanghu(split[0]);
			user.setUsername(split[1]);
		}else{
			return null;
		}
		
		return user;
		
	}*/
}
