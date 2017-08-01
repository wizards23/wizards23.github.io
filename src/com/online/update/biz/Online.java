package com.online.update.biz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.protocol.ExecutionContext;
import org.json.JSONObject;

import com.main.pda.VersionCheck;
import com.sql.SQL;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
//在线升级类


public class Online {
	public  Timer t = new Timer();
	//下载的线程数量
    private int count = 1; 
    //文件存保存位置
    private String paths;
    //定义文件完整路径
    private String targetFile;
	//下载资源路径
	private Handler h;	
	private File fileRand;
	//RandomAccessFile currentPart = null;
	//是否清除数据库记录
	private boolean delete;
	//取消计时器
	boolean load;
	private int[] lengths =new int[2]; 
	//文件总大小
	private int fileSize;  
	private int size = 0;
	private boolean netconn = false;
	public Online(){};
	public Online(Handler h){
		this.h = h;
	};
	
	Context context;
/**
 * 检查在线升级
 * @param context
 * @param ph 
 */
public void load(Context context) throws Exception{	
	
	paths = Environment.getExternalStorageDirectory()+"/PDA_Version";
	targetFile = paths+"/"+VersionInfo.APKNAME;
	

	File file = new File(paths);
	//如果不存在，则创建文件夹
	if(!file.exists()){
	   file.mkdirs();
	   Log.i("file.mkdirs",file+"");
	}
		
		URL url = new URL(VersionInfo.URL);
		 Log.i("VersionInfo.URL",VersionInfo.URL);
		//打开和URL之间的链接
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();	
		//设置超时时间
		conn.setConnectTimeout(10*1000);
		conn.setRequestMethod("GET");
		
		conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*"); 
		conn.setRequestProperty("Accept-Language", "zh-CN"); 
		conn.setRequestProperty("Charset", "UTF-8"); 
		conn.setRequestProperty("Connection", "Keep-Alive"); 
		int code = conn.getResponseCode();
		Log.i("code---", code+"");
		
		if(code==404){
			return;
		}
		
		//打开数据库
		SQL.creageDatabase();
						
		//获取文件总大小
		fileSize = conn.getContentLength();
		Log.i("fileSize", fileSize+"");
		
		//计算每个线程应该下载的数据量。
		int block = fileSize/count+1;
		//创建一个相同大小的文件
		Log.i("targetFile",this.targetFile);
								
		
		//测试，重置数据库记录			
		SQL.exupdate(0,1,1);
		SQL.exupdate(0,2,1);
		//查询数据库表的长度记录
		Cursor cursor = SQL.select("select len1,len2 from load2 where id=1");
		int length1=0;
		int length2=0;		
		int k = 0;		
		while(cursor.moveToNext()){			
			lengths[k] = Integer.parseInt(cursor.getString(0));				
			System.out.println(length1+"--"+length2);
		}									
		cursor.close();
		
		RandomAccessFile currentPart = new RandomAccessFile(this.targetFile, "rwd");
		//设置本地文件大小
		currentPart.setLength(fileSize);
		currentPart.close();
		
		for (int i = 0; i < count; i++) {
			//计算每条线程开始下载的位置			
			int start=0;
			
				if(i>=count-1){
					 start = i*block+1;
					}else{
					start = i*block;			
					}
			
				int endIndex=0;
				if(i==count-1){
				 endIndex =fileSize;	
				}else{
				 endIndex =(i+1)*block;
				}			
							
		    new DownLoad(start+lengths[i],endIndex,this.targetFile,i+1,currentPart).start();	

			
		}
		
		
		
		final Bundle b = new Bundle();
		
		t.schedule(new TimerTask() {			
			@Override
			public void run() {
				Log.i("更新滚动条", getCount()+"");
				double percent=(getCount()*100);	
				Message m = h.obtainMessage();
				load =false;
				if(percent>=100){						 															
					load = true;
					percent = 100.00;
					m.what=100;
					
				}else{
					m.what=1;	
				}
					
				b.putDouble("bar", percent);
				m.setData(b);
				h.sendMessage(m);
				if(load){
				 load = false;
				 t.cancel();
				}
			}
		}, 0,100);
		
		
				
}
	
   
	private double getCount(){
		size = 0;					
		if(size>=fileSize){
			//下载完成后清空记录
			SQL.exupdate(0,1,1);
			SQL.exupdate(0,2,1);
			System.out.println("已经清空数据库记录");	
			size = fileSize;			
		}else{
			Cursor cursor1 = SQL.select("select len1 from load2 where id="+(1));
			cursor1.moveToFirst();									
			size+=Integer.parseInt(cursor1.getString(0));
		}		
		double per = size*1.0/fileSize;
		
		System.out.println("百分比---"+per);
		return per;
	} 
	
						
}

