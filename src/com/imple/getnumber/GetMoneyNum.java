package com.imple.getnumber;

import hdjc.rfid.operator.INotify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GetMoneyNum implements INotify {
		//获取钱捆编号
	
	public static Handler handler;
	public static List<String> list = new ArrayList<String>();   //更新显示编号
	public static List<String> list_save = new ArrayList<String>();   //保存已扫过的编号
	Message m;
	public static int money;
	
	/**
	 * 
	 * @param number 钞捆编号
	 */
	@Override
	public void getNumber(String number) {
		Log.i("number",number);
		if(number==null || !checkNum(number)){
	    	return;
	     }
		
	
		
		
	        m = handler.obtainMessage();
		if(!list_save.contains(number)){
			m.what = 1;
			list.add(number);
			list_save.add(number);
			moneytoal();  //算总金额
			Bundle b= new Bundle();
			b.putString("money", number);
			b.putInt("moneyToal", money);
			m.setData(b);
			handler.sendMessage(m);		
		}
		
	}
	
	//计算钱捆总金额
	public static void moneytoal(){
		money = 0;
		String moneyNum;
		for (int i = 0; i < list.size(); i++) {
			try {
			  moneyNum =list.get(i).substring(8,10);	
			 if(moneyNum.equals("5A")){
					money = money+100000;	
				}else if(moneyNum.equals("9A")){
					money = money+50000;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		//money+= 50000;				
		}
	  
	}
	
	
	private boolean checkNum(String num){
		String reg = "^[0-9]{9}[A]{1}[0-9]{14}$";
		boolean boo = (num.matches(reg));
		return boo;
	}
	
}
