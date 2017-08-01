package com.ljsw.tjbankpda.db.biz;



import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ljsw.tjbankpda.db.application.o_Application;

import hdjc.rfid.operator.INotify;

public class DiZhiYaPinGetNumber implements INotify{
	int bb=-1;
	private Handler handler;
	private Bundle bundle;
	@Override
	public void getNumber(String number) {
		//过滤
		if(!o_Application.guolv.contains(number)){
			o_Application.guolv.add(number);
		}else{
			return;
		}
		System.out.println("----------number"+number);
		Message m = handler.obtainMessage();
		bundle=new Bundle();
		bundle.putString("Num", number);
		m.setData(bundle);
		m.what=1;
		handler.sendMessage(m);
		
	}
	public void setHandler(Handler handler){
		this.handler=handler;
	}
}
