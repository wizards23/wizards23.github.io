package com.ljsw.tjbankpda.util;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

import com.example.app.entity.WebParameter;

/**
 * Webservice解析方法（3期使用） 参数： 1.webservice命名空间 2.调用的方法名 3.webservice路径 4.参数数组
 * 
 * @author Administrator
 * 
 */
public class WebServiceFromThree {
	/**
	 * 
	 * @param methodName
	 * @param parameter
	 * @param namespace
	 * @param path 包括（cash_cm，cash_cmanagement，cash_kuguanyuan）
	 * @return
	 * @throws Exception
	 */
	public static SoapObject getSoapObject(String methodName,
			WebParameter[] parameter, String namespace, String path)
	
			throws Exception {
		// 创建Soap对象，并指定命名空间和方法名
		SoapObject request = new SoapObject(namespace, methodName);
		
		// 遍历添加参数 WebParameter为泛型类 属性1：参数名称 属性2：参数值
		
		if (parameter != null) {
			for (WebParameter webParameter : parameter) {
				request.addProperty(webParameter.getName(),
						webParameter.getValue());
			}
		}
		// 指定版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);

		// 设置Bodyout属性
		envelope.bodyOut = request;
		(new MarshalBase64()).register(envelope);

		envelope.encodingStyle = "UTF-8";

		// 创建HTTPtransports对象，并指定WSDL文档的URL
		HttpTransportSE ht = new HttpTransportSE(path, 20000);

		// 调用webservice
		String pacth = path + "/" + methodName;
		System.out.println("test--"+pacth);
		
		ht.call(pacth, envelope);
		
		// 返回结果集
		SoapObject soapObject = (SoapObject) envelope.getResponse();
		return soapObject;
	}
}
