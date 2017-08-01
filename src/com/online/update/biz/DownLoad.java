package com.online.update.biz;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler;
import android.util.Log;

import com.main.pda.VersionCheck;
import com.sql.SQL;

public class DownLoad extends Thread {
	//当前线程开始的位置
		private int startPos;
		 //当前线程开始的结束位置
	 	private int endIndex;
		//当前线程负责下载的文件块
		private String targetFile;

	    private RandomAccessFile currentPart;
	 	int i;

		public DownLoad(int start,int endIndex,String targetFile,
				int i,RandomAccessFile currentPart){
			this.startPos =start;    //开始位置
			
			this.targetFile = targetFile;
			this.endIndex = endIndex;
			this.i = i;
			this.currentPart = currentPart;
		}	
	
		
		
		
		@Override
		public void run() {
			super.run();
			try {
				URL url = new URL(VersionInfo.URL);
				//打开和URL之间的链接
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				//设置超时时间				
				conn.setConnectTimeout(10*1000);
				conn.setRequestMethod("GET");
				//System.out.println("开始位置----"+this.startPos+"结束位置----"+this.endIndex);
				//conn.setRequestProperty("Range", "bytes=" + startPos + "-"+ this.endIndex);//设置获取实体数据的范围 
				
				conn.setRequestProperty("Connection", "Keep-Alive");
																												 				
					//获取网络返回的流
					InputStream is = conn.getInputStream();	
					int code = conn.getResponseCode();
					Log.i("code",code+"");
					
					int len =0;	
					byte[] b = new byte[1024];				
					currentPart = new RandomAccessFile(this.targetFile,"rwd");					
					//currentPart.seek(startPos);
					//当前线程已下载长度
					int length=0;
			
				
						while((len=is.read(b))>0 && VersionCheck.stopupdate){					
							currentPart.write(b,0,len);	
							length+=len;							
							SQL.exupdate(length,this.i,1);
							System.out.println("线程ID--"+Thread.currentThread()+"---"+length);	
						}
						System.out.println("下载完成线程ID--"+Thread.currentThread()+"---"+length);	
						
						is.close();
						currentPart.close();
					
						
					
													
											
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}	
}
