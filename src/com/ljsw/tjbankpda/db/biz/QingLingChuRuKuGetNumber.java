package com.ljsw.tjbankpda.db.biz;

import hdjc.rfid.operator.INotify;


import android.os.Handler;

import com.ljsw.tjbankpda.db.application.o_Application;
import com.strings.tocase.CaseString;

public class QingLingChuRuKuGetNumber implements INotify{
	private Handler handler;
	public String str;
	@Override
	public void getNumber(String number) {
		if(number==null || !CaseString.reg(number)){
			return;
		 }
		str =CaseString.getBoxNum2(number);	
		
		System.out.println("--------------" + str);
		// 过滤
		if (!o_Application.guolv.contains(str)) {
			o_Application.guolv.add(str);
		} else {
			return;
		}
		int aa = -1;
		for (int i = 0; i < o_Application.qinglingchuruku.getKuxiang().size(); i++) {
			if (o_Application.qinglingchuruku.getKuxiang().get(i).equals(str)) {
				aa = i;
				break;
			}
		}
		System.out.println("=============扫描完成，aa为：" + aa);
		if (aa != -1) {
			o_Application.qinglingchuruku.getKuxiang().remove(aa);// 左侧集合删除
			o_Application.numberlist.add(str);// 右侧集合添加
			handler.sendEmptyMessage(0);
		} else {
			o_Application.wrong = str + "-错误箱号";
			handler.sendEmptyMessage(0);
		}
	}
	public void setHandler(Handler handler){
		this.handler=handler;
	}
}
