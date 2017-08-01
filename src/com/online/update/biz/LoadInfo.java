package com.online.update.biz;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xmlpull.v1.XmlPullParser;


import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class LoadInfo {
	//把配置文件下载到本地
	BufferedInputStream bis = null;
	FileOutputStream fos = null;
	int size=0;
	
	byte[] buf = new byte[1024];
	
	public boolean loadInfo (String path) throws Exception{
	
			URL url = new URL(path);	
			//打开和URL之间的链接
			System.out.println("准备打开链接");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();				
			conn.setRequestMethod("POST");
			//设置超时时间
			conn.setConnectTimeout(10*1000);
			
			int code = conn.getResponseCode();
			System.out.println("网络状态----"+code);
				 //获取网络输入流  
		    bis = new BufferedInputStream(conn.getInputStream()); 
		    return  parserString(bis);	
			
			
        
						
	}
	
	
//	private  boolean saxXML(String fileName){
//		Log.i("解析", fileName);
//		//读取器
//		SAXReader read = new SAXReader();
//		Log.i("1111", "111");
//		try {
//		//文档对象
//		File file = new File(fileName);
//		Log.i("file", file.exists()+"");		
//		Document document = read.read(file);			
//		Log.i("222", "222");
//		//获取文档根节点
//		Element root = document.getRootElement();
//		Log.i("333", "33");
//		//获取子节点
//		Element version = root.element("version");
//		Log.i("44", "44");
//		VersionInfo.VERSION =version.getText(); 
//		System.out.println("版本----"+version.getText());
//		Log.i("55", "555");
//		Element apk = root.element("apk");	
//		VersionInfo.APKNAME = apk.getText();
//		System.out.println("apk名称----"+apk.getText());
//		Log.i("66", "66");
//		Element url = root.element("url");
//		VersionInfo.URL = url.getText();
//		System.out.println("路径----"+url.getText());
//		Log.i("77", "77");
//		return true;
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			return false;
//		}				
//	}
//	
//	
	
	/**
	  * 解析XML
	  * @param xml
	  * @throws Exception
	  */
	 private boolean parserString(InputStream  is) throws Exception{
		 Log.i("pull解析", "pull解析");
		 if(is==null){
			 return false;
		 }
		 //获取得XmlPullParser解析器
		 XmlPullParser parser = Xml.newPullParser();		 
		//设置输入流，并指定编码格式
		 parser.setInput(is, "UTF-8");
			// 获得解析到的事件类别，这里有开始文档，结束文档，开始标签，结束标签，文本等等事件。
			int evtType = parser.getEventType();			
			while(evtType!=XmlPullParser.END_DOCUMENT){				
				switch(evtType){				
				//文档事件开始
				case XmlPullParser.START_DOCUMENT:					
					break;					
				//标签元素事件开始
				case XmlPullParser.START_TAG:
					if(parser.getName().equals("version")){
						VersionInfo.VERSION = parser.nextText();
						Log.i("VersionInfo.VERSION", VersionInfo.VERSION);
					}else if(parser.getName().equals("apk")){
						VersionInfo.APKNAME = parser.nextText();
						Log.i("URL", VersionInfo.APKNAME);
					}else if(parser.getName().equals("url")){
						VersionInfo.URL = parser.nextText();
						Log.i("URL", VersionInfo.URL);
					}
					break;
					
				//标签元素事件结束	
				case XmlPullParser.END_TAG:
					
					break;
				}				
				evtType = parser.next();	
			    }	
			
			return true;
	 }
	
	
	
	
	
	
}
