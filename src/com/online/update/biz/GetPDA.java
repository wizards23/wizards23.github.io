package com.online.update.biz;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.online.update.service.PDAVersionInfoPathService;

public class GetPDA {
	
	LoadInfo load;
	
	/**
	 * 获取配置文件
	 */
	public void getpath(Handler h,Context context){
		new Thread(new Run(h,context)).start();
	}
	
	class Run implements Runnable{
		private Handler h;
		private Context context;
		Run(){}

		Run(Handler h,Context context){
		this.h = h;
		this.context = context;
		}
		@Override
		public void run() {
			Message m = Message.obtain();
			try {
				//获得配置文件路径
				String path =new PDAVersionInfoPathService().getpath();
				
				if(path!=null && path!=""){
					load = new LoadInfo();
					
				Log.i("获得配置文件路径", path);
				//下载配置文件,并解析
				boolean boo =load.loadInfo(path);
					if(boo){
						VersionsCheckBiz version = new VersionsCheckBiz();
						
						//对比版本，如果有新版本
						if(version.getVersionCode(this.context, VersionInfo.VERSION)){
						   m.what=99;
						   Log.i("有新版本", "有新版本");
						}else{
						  m.what=44;
						}
					}
				}else{
					m.what=0;
				}
			} catch (Exception e) {				
				e.printStackTrace();
				
				m.what=-1;
			}finally{
				this.h.sendMessage(m);
			}
			
		}		
	}
	
	
	
	
	
	
	
	
}
