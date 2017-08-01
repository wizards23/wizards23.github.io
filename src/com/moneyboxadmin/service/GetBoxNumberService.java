package com.moneyboxadmin.service;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.BoxNum;
import com.entity.WebParameter;
import com.service.WebService;


public  class GetBoxNumberService {
	
	/**
	 * ATM钞箱管理获取钞箱出库数量和钞箱入库数量
	 */
	public static BoxNum getCashboxNum(String corpId) throws Exception {
			String methodName = "getCashboxNum";
			Log.i("corpId",corpId);
			WebParameter[] parameter = { 
				new WebParameter<String>("arg0",corpId) 
					};

			SoapObject soapObject = WebService.getSoapObject(methodName, parameter);
			
			String value = soapObject.getProperty("params").toString();
			String state = soapObject.getProperty("msg").toString();
			
			BoxNum box = new BoxNum();		
				String[] arry = value.split(";");								
				box.setState(state);
				box.setEmpty(arry[0]);  //空钞箱出库
				box.setAtm(arry[1]);   //ATM加钞出库
				box.setNotclear(arry[2]);  //未清回收钞箱出库
				box.setPutin(arry[3]); //钞箱装钞入库
				box.setBack(arry[4]); //回收钞箱入库
				box.setClear(arry[5]);  //已清回收钞箱入库
				
				for (int i = 0; i < arry.length; i++) {
					Log.i("arry",arry[i]);
				}	
	
			
		return box;
	}

}
