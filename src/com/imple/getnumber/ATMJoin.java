package com.imple.getnumber;

import com.strings.tocase.CaseString;

import android.os.Handler;
import android.os.Message;
import hdjc.rfid.operator.INotify;

/**
 * 扫描atm机
 * @author Administrator
 *
 */
public class ATMJoin implements INotify{
	
	public static Handler handler;   

	@Override
	public void getNumber(String number) {
		// TODO Auto-generated method stub
		number=CaseString.getATMNum(number);
		System.out.println("扫描周转箱..："+number);
		
		Message msg=handler.obtainMessage();
		msg.obj=number;
		msg.what=1;
		handler.sendMessage(msg);
		
	}
	
	

}
