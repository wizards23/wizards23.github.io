package com.loginsystem.biz;

import java.net.SocketTimeoutException;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.application.GApplication;
import com.entity.SystemUser;
import com.example.app.entity.User;
import com.golbal.pda.GolbalUtil;
import com.o.service.LoginService;
import com.systemadmin.service.SystemLoginService;

/**
 * 系统登陆业务
 * @author Administrator
 */
public class SystemLoginBiz {

	public  Handler handler_login;
	public static boolean islogin;
	Message m;
	SystemUser demoUser;
	User use;
	//public static SystemUser user;
	
	
	private GolbalUtil getUtil;	
	public GolbalUtil getGetUtil() {return getUtil=getUtil==null?new GolbalUtil():getUtil;}

	public  void login(String uid,String pwd){
		m = handler_login.obtainMessage();		
		if(isnull(pwd,uid)){	
			Log.i("pwd----------", pwd+"mmmmmmmmm");
			if(getGetUtil().onclicks){
				getGetUtil().asynctask=new AsyncBoxNum(uid,pwd);
				getGetUtil().asynctask.execute();
				getGetUtil().onclicks = false;
				Log.i("bb", "bb");
			}						
		}else{
			m.what=-3;
		}
			
	}
	
	
	/**
	 * 新增方法
	 * @param corpId
	 * @param roleId
	 * @param cValue
	 * @param uid
	 * @param pwd
	 */
	@SuppressWarnings("static-access")
	public void kuguanLogin(String corpId,String roleId,byte[] cValue,String uid,String pwd) {
		m = handler_login.obtainMessage();
		if (isnull(pwd, uid)) {
			Log.i("pwd----------", pwd + "mmmmmmmmm");
			if (getGetUtil().onclicks) {
				getGetUtil().asynctask = new AsyncKuguan(pwd, uid, corpId, roleId, cValue);
				getGetUtil().asynctask.execute();
				getGetUtil().onclicks = false;
				Log.i("bb", "bb");
			}
		} else {
			m.what = -3;
		}

	}
	/**
	 * 新增内容
	 * @author Administrator
	 *
	 */
	protected class AsyncKuguan extends AsyncTask {
		String pwd;
		String uid;
		String corpId;
		String roleId;
		byte[] cValue;

		public AsyncKuguan() {
		}

		public AsyncKuguan(String pwd, String uid, String corpId,
				String roleId, byte[] cValue) {
			super();
			this.pwd = pwd;
			this.uid = uid;
			this.corpId = corpId;
			this.roleId = roleId;
			this.cValue = cValue;
		}

		// 任务取消后
		@Override
		protected void onCancelled() {
			super.onCancelled();
			getGetUtil().onclicks = true;
			// m.what=-1;
			handler_login.sendMessage(m);
		}

		// 后台任务完成后
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			getGetUtil().onclicks = true;
			if (GApplication.use != null) {
				m.what = 1;
				islogin = true;
			} else {
				m.what = 0;
				m.obj=use.getUsername();
			}
			handler_login.sendMessage(m);

		}

		// 后台操作
		@Override
		protected Object doInBackground(Object... arg0) {
			try {
				LoginService ls = new LoginService();
			//	GApplication.use = ls.checkFingerprint(corpId, roleId, cValue, uid, pwd);
				use=ls.checkFingerprint(corpId, roleId, cValue, uid, pwd);
				if(use.getUserid()==null){
					GApplication.use=null;
				}else{
					GApplication.use=use;
				}
			} catch (SocketTimeoutException e) {
				m.what = -4;
				this.cancel(true);
				System.out.println(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				m.what = -1;
				this.cancel(true);
			} finally {

			}
			return null;
		}	
	}
						
	protected  class AsyncBoxNum extends AsyncTask{
		String pwd;
		String uid;
		public AsyncBoxNum(){}
		public AsyncBoxNum(String uid,String pwd){
			this.uid = uid;
			this.pwd = pwd;
		}
		
		//任务取消后
		@Override
		protected void onCancelled() {
			super.onCancelled();
			getGetUtil().onclicks = true;
			//m.what=-1;	
			handler_login.sendMessage(m);
		}

		//后台任务完成后
		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			getGetUtil().onclicks = true;
			if(GApplication.user!=null){
				m.what = 1;	
				islogin = true;
			}else {
				m.what = 0;
				m.obj=demoUser.getLoginUserName();	
			}			
			handler_login.sendMessage(m);
			
		}

		//后台操作
		@Override
		protected Object doInBackground(Object... arg0) {			
			try {
	//			GApplication.user = SystemLoginService.system_login(uid, pwd);
				demoUser=SystemLoginService.system_login(uid, pwd);
				if(demoUser.getLoginUserId()==null){
					GApplication.user=null;
				}else{
					GApplication.user=demoUser;
				}
			}catch(SocketTimeoutException e){
				m.what = -4;
				this.cancel(true);
				System.out.println(e.getMessage());
			}catch (Exception e) {
				e.printStackTrace();
				m.what=-1;	
				this.cancel(true);
			}finally{
				
			}
			return null;
		}
						
	}
			
	private  boolean isnull(String pwd,String uid){
		if(pwd==null || uid==null || pwd=="" || uid==""){
			return false;
		 }
		return true;
	}


}
