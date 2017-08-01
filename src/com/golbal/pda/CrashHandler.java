package com.golbal.pda;

import hdjc.rfid.operator.IRFID_Device;
import hdjc.rfid.operator.RFID_Device;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

import com.application.GApplication;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class CrashHandler{
//
//	private static CrashHandler crashHandler = new CrashHandler();
//	private Context mcontext;	 
//	private Thread.UncaughtExceptionHandler mdefaultHandler;		
//	private CrashHandler(){		
//	}
//	
//	private static IRFID_Device irfid;
//	public static boolean isOpen = true;
//	public static boolean isScan = true;
//	
//	public static CrashHandler getInstance(){
//		return crashHandler;
//	}
//	
//	
//	public void init(Context ctx){
//		mcontext = ctx;
//		//获取系统默认的UncaughtException处理器  
//		mdefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
//		 //设置该CrashHandler为程序的默认处理器 
//		Thread.setDefaultUncaughtExceptionHandler(this);
//	}
//	
//	
//	/**
//	 * 当UncaughtException发生时会转入该函数来处理
//	 */
//	@Override
//	public void uncaughtException(Thread th, Throwable ex) {
//		 StringBuffer sb = new StringBuffer();
//		 Writer writer = new StringWriter();  
//	        PrintWriter printWriter = new PrintWriter(writer);  
//	        ex.printStackTrace(printWriter);  
//	        Throwable cause = ex.getCause();  
//	        while (cause != null) {  
//	            cause.printStackTrace(printWriter);  
//	            cause = cause.getCause();  
//	        }  
//	        printWriter.close();  
//	        String result = writer.toString();  
//	        sb.append(result);   
//		Log.i("全局异常错误信息", sb.toString());	
//		try {
//			ex.getMessage();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		new Thread(){
//			@Override
//			public void run() {
//				super.run();
//				Looper.prepare();
//				if(!isOpen){
//				   
//				}
//				if(!isScan){
//				  	
//					}
//				android.os.Process.killProcess(android.os.Process.myPid());
//				System.exit(10);
//			    AlertDialog.Builder dog = new Builder(mcontext);
//				dog.setMessage("发生未知错误，系统退出!");
//				dog.setPositiveButton("确认",new DialogInterface.OnClickListener() {					
//					@Override
//					public void onClick(DialogInterface arg0, int arg1) {
//					GApplication.getApplication().exit(true);	
//					
//					}				
//				});
//				dog.show();	
//				Looper.loop();
//			}
//			
//		}.start();
//		
//		
//		
//	}
	

//	
//	public static IRFID_Device getirfid(){
//		if(irfid==null){
//			irfid = new RFID_Device();
//			Log.i("aaaaaaaaaaaa123", "aaaaaaaaaaaa123");
//		}	
//		return irfid;
//	}
//	

	
	/**
	 * 扫描开关
	 * @param 扫描开关
	 */
//	public static void switchByScan(){
//			Log.i("isopen",true+"");
//			if(isOpen){			
//				getirfid().open_a20();
//				isOpen = false;	
//			}else{
//				getirfid().close_a20();
//				isOpen = true;
//			}	
//			
//	}
	
	/**
	 * 一维码扫描开关
	 * @param 一维码扫描开关
	 */
//	public static void switchByScanOne(){
//		//如果循环扫描是关闭的才操作一维码扫描
//		Log.i("isScan", isOpen+"");
//		//if(isOpen){
//			Log.i("isScan", isScan+"");
//			if(isScan){			
//				getirfid().scanOpen();
//				isScan = false;			
//				Log.i("一维码打开了","打开了");
//			}else{		
//				getirfid().scanclose();
//				isScan = true;	
//				Log.i("一维码关闭了","关闭了");
//			}		
//		//}
//		
//	}
	

}
