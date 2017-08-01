package com.service;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.entity.WebParameter;

import android.util.Log;


/**
 * Webservice解析方法
 * 参数：
 * 1.webservice命名空间
 * 2.调用的方法名
 * 3.webservice路径
 * 4.参数数组
 * @author Administrator
 *
 */
public class WebService {

	/**
	 *  URL2=url+"/cash_box";
	 * @param methodName
	 * @param parameter
	 * @return
	 * @throws Exception
	 */
	public static SoapObject getSoapObject2(String methodName, WebParameter[] parameter) throws Exception {
		// 创建Soap对象，并指定命名空间和方法名
		
		
		SoapObject request = new SoapObject(FixationValue.NAMESPACE, methodName);
		//遍历添加参数    WebParameter为泛型类   属性1：参数名称  属性2：参数值
		if (parameter != null) {
			for (WebParameter webParameter : parameter) {
				request.addProperty(webParameter.getName(),webParameter.getValue());
			}
		}
		// 指定版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		
		// 设置Bodyout属性
		envelope.bodyOut = request;
		(new MarshalBase64()).register(envelope);
		
		envelope.encodingStyle="UTF-8";
		
		// 创建HTTPtransports对象，并指定WSDL文档的URL
		HttpTransportSE ht = new HttpTransportSE(FixationValue.URL2, 20000);

		// 调用webservice
		String patch=FixationValue.URL2+"/"+methodName;
		System.out.println("----------访问的地址："+patch);
		ht.call(patch, envelope);
		//返回结果集
		SoapObject soapObject = (SoapObject)envelope.getResponse();
		
		return soapObject;
	}

	/**
	 * url+"/cash_pda";
	 * @param methodName
	 * @param parameter
	 * @return
	 * @throws Exception
	 */
public static SoapObject getSoapObject(String methodName, WebParameter[] parameter) throws Exception {
	// 创建Soap对象，并指定命名空间和方法名
	
	
	SoapObject request = new SoapObject(FixationValue.NAMESPACE, methodName);
	//遍历添加参数    WebParameter为泛型类   属性1：参数名称  属性2：参数值
	if (parameter != null) {
		for (WebParameter webParameter : parameter) {
			request.addProperty(webParameter.getName(),webParameter.getValue());
		}
	}
	// 指定版本
	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	
	// 设置Bodyout属性
	envelope.bodyOut = request;
	(new MarshalBase64()).register(envelope);
	
	envelope.encodingStyle="UTF-8";
	
	// 创建HTTPtransports对象，并指定WSDL文档的URL
	HttpTransportSE ht = new HttpTransportSE(FixationValue.URL, 10000);

	// 调用webservice
	String patch=FixationValue.URL+"/"+methodName;
	System.out.println("----------访问的地址："+patch);
	ht.call(patch, envelope);
	//返回结果集
	SoapObject soapObject = (SoapObject)envelope.getResponse();
	
	return soapObject;
}}


