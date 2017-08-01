package com.loginsystem.biz;

import java.net.SocketTimeoutException;

import com.systemadmin.service.SystemUpdatePwdService;


import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;



/**
 * 系统登陆密码修改
 * 
 * @author Administrator
 * 
 */
public class SystemUpdatePwdBiz {
	
	public static Handler handler;
	
	public static void updatePwd(String userid,String pwd,String newpwd){
		Thread thread = new Thread(new Run(userid,pwd,newpwd));
		thread.start();		
	}
		
		

		
	static class Run implements Runnable{
		String userid;
		String pwd;
		String newpwd;
		Message m;
		public Run(){};
		
		public Run(String userid,String pwd,String newpwd){
			this.userid = userid;
			this.pwd = pwd;
			this.newpwd = newpwd;	
			m = handler.obtainMessage();
		};
		
		
		@Override
		public void run() {		
			try {
				if(SystemUpdatePwdService.updatePwd(userid, pwd, newpwd)){
					m.what=1;
				}else{
					m.what=0;
				}
			} catch (Exception e) {
				e.printStackTrace();
				m.what=-1;
			}finally{
				handler.sendMessage(m);
			}	
		}
		
	}

}
