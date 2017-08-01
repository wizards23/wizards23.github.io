package com.ljsw.tjbankpda.util;

import com.strings.tocase.CaseString;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import hdjc.rfid.operator.INotify;

public class DizhiYapinSaomiaoUtil implements INotify {
	private Handler handler;
	private String nowNumber="";
	private String newNumber;
	private Bundle bundle;

	@Override
	public void getNumber(String number) {
		if(number==null){
			return;
		}
		System.out.println("扫到的标签为="+number);
		newNumber=number;
		if(nowNumber.equals(newNumber)){
			return;
		}
		if("Ѐ".equals(newNumber)){
			return;
		}
		nowNumber=newNumber;
		bundle=new Bundle();
		Message m=new Message();
		bundle.putString("Num", number);
		m.setData(bundle);
		m.what=1;
		handler.sendMessage(m);
			
	}
	public void setHand(Handler handler){
		this.handler=handler;
	}
}
