package com.service;

import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.main.pda.SystemLogin;
import com.messagebox.Abnormal;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class NetService extends Service {
	
	   ConnectivityManager connectivityManager;
		public static NetworkInfo info;
		Bundle bundle;
	    int type=-1;
	    public static Handler handnet;
	    Message m;
	

	@Override
	public void onCreate() {
		super.onCreate();
		 IntentFilter mFilter = new IntentFilter();
	     mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
	     registerReceiver(broadcastreceiver, mFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastreceiver);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {													       	         	 		
		return super.onStartCommand(intent, flags, startId);						
	}

	
	/**
	 * 网络每次发生变化，系统会发现一个ConnectivityManager.CONNECTIVITY_ACTION广播
	 * 通过这个广播监测网络状态，及网络类型
	 */
	
	private BroadcastReceiver broadcastreceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){					
				connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				
				info = connectivityManager.getActiveNetworkInfo();
																
				if(info!=null){										
					if(info.isConnectedOrConnecting()){  //有网络
						if(SystemLogin.current){ //如果是登录当前界面
							Log.i("info1", info+"");
							//发送通知
							sendmsg(1);
						   }							
						}
					}else{	
						 if(SystemLogin.current){ //如果是登录当前界面
						  Log.i("info-1", info+"");
						  sendmsg(-1);
						  new GolbalView().toastShow(context, "当前网络连接失败");
						 }
					}														
			}		
		}		
	};


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	} 
	
	
	private void sendmsg(int i){
		m = handnet.obtainMessage();
		m.what=i;
		handnet.sendMessage(m);	
	}
	



}
