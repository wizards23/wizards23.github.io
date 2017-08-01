package com.imple.getnumber;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import hdjc.rfid.operator.IFingerNotify;
import hdjc.rfid.operator.INotify;

public class GetFingerValue implements INotify {

	public static Handler handler;
//	public static byte[] fingerByte;
	Bundle bundle = new Bundle();

	@Override
	public void getNumber(String number) {
		Message m = handler.obtainMessage();
		bundle.putString("finger", number);
		Log.i("hand押运员", "hand押运员");
		m.setData(bundle);
		m.what = 1;
		handler.sendMessage(m);
	}

}
