package com.loginsystem.biz;

import com.entity.Finger;
import com.entity.User;
import com.moneyboxadmin.service.BankDoublePersonLoginService;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

//指纹验证登陆
public class FingerCheckBiz {
	
	
	  public static Handler hand_finger; 
	  public static Handler hand_super; 
	  public  static int num=0; 
	  static User user;		 
	  static Message m;		 
	  static Bundle bundle;	
	  
	  /**
	   * 
	   * @param finger  手指对象
	   * @param bizName 押运员或双人
	   */
	  public static void fingerLoginCheck(Finger finger,String bizName){
		  AsyncTask asynctask=null;
		 if("押运员".equals(bizName)){
			 asynctask=new  AsyncTaskSuper(finger);
		 }else if("双人".equals(bizName)){
			 asynctask = new AsyncTaskFinger(finger); 
		 }
		  asynctask.execute();
	  } 
	
			
	 /**
	   * 获取指纹验证结果
	   * @author Administrator
	   *
	   */
	 private static class AsyncTaskFinger extends AsyncTask{
		 Finger finger;      
	 
		 public AsyncTaskFinger(){};
		 public AsyncTaskFinger(Finger finger){
			 this.finger = finger;
			 bundle = new Bundle();
			 m= hand_finger.obtainMessage();
		 };
	
		//取消任务后的操作  
		@Override
		protected void onCancelled() {
			super.onCancelled();
			m.what=-1;
			hand_finger.sendMessage(m);		
		}
		//异步完成后的操作
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			//如果验证通过
			if(user.isState()){				
				bundle.putString("username", user.getName()); //用户名
				num++;    //验证次数加1
				bundle.putInt("num",num);
				m.setData(bundle);	
				m.what=1;
				hand_finger.sendMessage(m);
				}
		}

		//异步后台操作
		@Override
		protected Object doInBackground(Object... arg0) {			
			try {
			user= BankDoublePersonLoginService.checkFingerprint(finger);						
			} catch (Exception e) {
				e.printStackTrace();
				//取消任务	
							
				this.cancel(true);
			}			
			return null;
		}		   
	  }
	 
	 
	 
	 /**
	   * 押运员获取指纹验证结果
	   * @author Administrator
	   *
	   */
	 private static class AsyncTaskSuper extends AsyncTask{
		 Finger finger;      
	 
		 public AsyncTaskSuper(){};
		 public AsyncTaskSuper(Finger finger){
			 this.finger = finger;
			 bundle = new Bundle();
			 m=hand_super.obtainMessage();
		 };
	
		//取消任务后的操作  
		@Override
		protected void onCancelled() {
			super.onCancelled();
			m.what=-1;
			hand_super.sendMessage(m);		
		}
		//异步完成后的操作
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			//如果验证通过
			if(user.isState()){				
				bundle.putString("username", user.getName()); //用户名
				m.setData(bundle);	
				if(user.isState()){
				   m.what=1;
				}
				
				hand_super.sendMessage(m);
				}
		}

		//异步后台操作
		@Override
		protected Object doInBackground(Object... arg0) {			
			try {
			user= BankDoublePersonLoginService.checkFingerprint(finger);						
			} catch (Exception e) {
				e.printStackTrace();
				//取消任务	
							
				this.cancel(true);
			}			
			return null;
		}		   
	  }
	
	
	
}
