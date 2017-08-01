package com.moneyboxadmin.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class Test {


	public static  void test() throws Exception{

		//创建Soap对象，并指定命名空间和方法名
		SoapObject request = new SoapObject("http://WebXml.com.cn/","getMobileCodeInfo");				
		//指定版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);	
		//设置参数				
		request.addProperty("mobileCode","13800138000");
		request.addProperty("userID","");																
		//设置Bodyout属性
		envelope.bodyOut = request;
		(new MarshalBase64()).register(envelope);  
		//创建HTTPtransports对象，并指定WSDL文档的URL,并设置超时间为5秒			
		HttpTransportSE ht = new HttpTransportSE("http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx?",200);		
		envelope.dotNet = true;  //因为Service是用.net写的
						
		//调用webservice
		  ht.debug = true;  //是否进入调试模式
				
			ht.call("http://WebXml.com.cn/getMobileCodeInfo",envelope);
			
			String soap = envelope.bodyIn+"";		
			Log.i("soap",soap);		
	}
	
	
	public static void httptest() throws Exception{
		String NAMESPACE = "http://WebXml.com.cn/";
		String methodName = "getMobileCodeInfo";
		String URL = "http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx?";
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(URL+methodName);
		List nvps = new ArrayList();
		nvps.add(new BasicNameValuePair("mobileCode", "13800138000"));
		nvps.add(new BasicNameValuePair("userID", ""));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		CloseableHttpResponse httppHttpResponse2 = httpClient.execute(httpPost);
		String str =EntityUtils.toString(httppHttpResponse2.getEntity());
		Log.i("http", str);
	}
	
}
