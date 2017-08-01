package com.ljsw.tjbankpda.util;

import hdjc.rfid.operator.INotify;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ShangjiaoDizhiSaomiaoUtil implements INotify {
	private Handler handler;
	private Bundle bundle;

	@Override
	public void getNumber(String number) {
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
