package com.imple.getnumber;

import com.example.app.activity.LookStorageTaskDetailActivity;
import com.strings.tocase.CaseString;

import android.os.Handler;
import android.os.Message;
import hdjc.rfid.operator.INotify;

/**
 * 查看服务时扫描标签获取标签编号
 * @author zhouKai
 *
 */
public class LookStorageScan implements INotify {

	public static Handler handler;
	
	@Override
	public void getNumber(String number) {
		//如果是ATM钞箱
		number=CaseString.getBoxNum(number);
		if (number.equals("")) {
			//如果是周转箱或款箱
			number = CaseString.getBoxNum2(number);
		}
		
		Message message = handler.obtainMessage();
		message.obj = number;
		message.what = LookStorageTaskDetailActivity.SCAN_BOX;
		handler.sendMessage(message);
	}

}
