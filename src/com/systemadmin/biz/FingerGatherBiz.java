package com.systemadmin.biz;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.golbal.pda.GolbalUtil;
import com.systemadmin.service.FingerGatherService;

public class FingerGatherBiz{	
	//指纹采集
	
	public Handler hand;
	private FingerGatherService f;	//服务		
	FingerGatherService getF() {
		return f=f==null?new FingerGatherService():f;
	}
	
	private GolbalUtil golbalUtil;	
	public GolbalUtil getGolbalUtil() {
		return golbalUtil=golbalUtil==null?new GolbalUtil():golbalUtil;
	}
	
	
	/**
	 * 	
	 * @param userId  用户ID
	 * @param finger  手指
	 * @param cValue  特征值1
	 * @param cValue1   特征值2
	 */
	public void fingerprintInput(String userId,String finger,byte[] cValue,byte[] cValue1){	
		if(GolbalUtil.onclicks){
			GolbalUtil.onclicks = false;
		new Thread(new FingerT(userId,finger,cValue,cValue1)).start();	
		}
		
	   }
	

	
	class FingerT implements Runnable{
		String userId;
		String finger;
		byte[] cValue;
		byte[] cValue1;
		boolean is;
		Message m;
		
		public FingerT(){};
		
		
		/**
		 * 指纹采集
		 * @param userId   用户ID
		 * @param finger   手指
		 * @param cValue   特征值
		 * @param cValue1      特征值
		 */
		public FingerT(String userId,String finger,byte[] cValue,byte[] cValue1){
			this.userId = userId;
			this.finger = finger;
			this.cValue = cValue;
			this.cValue1 = cValue1;	
			m = hand.obtainMessage();
		};
		
		@Override
		public void run() {									
		  try {
			is=getF().fingerprintInput(userId, finger, cValue, cValue1);
			Log.i("is",is+"");
			if(is){
			m.what=1;   //采集成功
			}else{
 			m.what = 0;	//采集失败
			}
		} catch (Exception e) {
			e.printStackTrace();
			m.what = -1;  //异常处理
		}finally{
			hand.sendMessage(m);
			getGolbalUtil().onclicks = true;
		}							
		}
		
	}
	

	
	
	
}
