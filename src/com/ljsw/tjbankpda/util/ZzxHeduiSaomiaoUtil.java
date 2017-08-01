package com.ljsw.tjbankpda.util;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.strings.tocase.CaseString;

import hdjc.rfid.operator.INotify;

/**
 * RIFI扫码过滤
 * 实例化的时候将之前已扫描的周转箱先加入list
 * 再将后扫描过的周转箱加入List
 * 扫描成功后发送handler,通知前台.
 * @author FUHAIQING
 * @time 2015-7-3
 *
 */
public class ZzxHeduiSaomiaoUtil implements INotify {
	private Handler handler;
	private  List<String> list_ishave = new ArrayList<String>(); // 存入已扫描的周转箱
	private String BoxNum;// 字符型周转箱编号
	private String nowBoxNo;//之前扫描的周转箱编号
	Bundle bun;

	public ZzxHeduiSaomiaoUtil(String nowBoxNo) {
		super();
		this.nowBoxNo = nowBoxNo;
		list_ishave.add(nowBoxNo);
	}

	@Override
	public void getNumber(String number) {
		if (list_ishave.contains(number)) {
			return;
		}else{
			System.out.println("number:"+number);
		}
		list_ishave.add(number);

		if (number == null || !CaseString.reg(number)) {
			return;
		}
		// 十进制转换成字符
		BoxNum = CaseString.getBoxNum2(number);
		Message m = handler.obtainMessage();
		bun = new Bundle();
		bun.putString("box", BoxNum);
		bun.putInt("boxCount", list_ishave.size());
		m.setData(bun);
		m.what = 1;
		handler.sendMessage(m);
	}

	public void setHand(Handler handler){
		this.handler=handler;
	}
	public void cleanList(){
		list_ishave.clear();
	}

}
