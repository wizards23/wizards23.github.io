package com.ljsw.tjbankpda.yy.application;

import android.os.Handler;
import com.strings.tocase.CaseString;

import hdjc.rfid.operator.INotify;

public class SaomiaoUtil implements INotify {
	public boolean isfind = true;
	public Handler handler;
	
	public void getNumber(String number) {
		String nowDate=S_application.getApplication().bianhao;
		String newDate=CaseString.getBoxNum2(number);
		if (!newDate.equals(nowDate)) {
			if (number == null || !CaseString.reg(number)) {
				return;
			}
			// 十进制转换成字符
			S_application.getApplication().bianhao = CaseString.getBoxNum2(number);
			
			System.out.println("--------------" + number);
			int aa = -1;// 标识
			for (int i = 0; i < S_application.getApplication().leftlist.size(); i++) {
				if (S_application.getApplication().leftlist.get(i).equals(
						S_application.getApplication().bianhao)) {
					aa = i;
					break;
				}
			}
			System.out.println("=============扫描完成，aa为：" + aa);
			if (aa != -1) {
				// o_Application.DiZhiYaPin_bool=true;
				S_application.getApplication().leftlist.remove(aa);// 左侧集合删除
				S_application.getApplication().rightlist.add(S_application.getApplication().bianhao);// 右侧集合添加
			} else {
				for (int i = 0; i < S_application.getApplication().rightlist.size(); i++) {
					if(S_application.getApplication().rightlist.get(i).equals(S_application.getApplication().bianhao)){
						S_application.getApplication().wrong = "";
						isfind = false;
						break;
					}
				}
				if(isfind){
					S_application.getApplication().wrong = S_application.getApplication().bianhao + "-错误箱号";
				}
				
			}
			System.out.println("=============扫描完成，集合长度："
					+ S_application.getApplication().rightlist.size());
//			setHandler(handler);
//			Message m = handler.obtainMessage();
//			m.what = 1;
			isfind = true;
			handler.sendEmptyMessage(1);
		}
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
