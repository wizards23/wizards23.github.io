package com.imple.getnumber;

import hdjc.rfid.operator.INotify;
import android.os.Handler;

import com.application.GApplication;
import com.strings.tocase.CaseString;

public class KuanXiangChuKuUtil implements INotify{
	public boolean isfind = true;
	public Handler handler;
	
	public void getNumber(String number) {
		String nowDate=GApplication.getApplication().strBox;
		String newDate=CaseString.getBoxNum3(number);
		if("".equals(newDate))
			return;
		
		if(GApplication.saolist.contains(newDate)){
			return;
		}
		if(GApplication.smlist.contains(newDate)){
			return;
		}
		if (!newDate.equals(nowDate)) {
			if (number == null || !CaseString.reg(number)) {
				return;
			}
			// 十进制转换成字符
			GApplication.getApplication().strBox = CaseString.getBoxNum3(number);
			if("".equals(GApplication.getApplication().strBox))
				return;
			System.out.println("---UUOO---"+GApplication.getApplication().strBox);
			System.out.println("-------ww-------" + number);
			int aa = -1;// 标识
			for (int i = 0; i < GApplication.mingxi_list.size(); i++) {
				if(GApplication.mingxi_list.get(i).getBoxIds().equals(GApplication.getApplication().strBox)){
					aa = 0;
					GApplication.saolist.add(GApplication.getApplication().strBox);		
					handler.sendEmptyMessage(1);
					break;
				}else{					
					continue;
				}		
			}
			if(aa==-1){				
				if(GApplication.smlist.size()==0){				
					GApplication.smlist.add(GApplication.getApplication().strBox);
					handler.sendEmptyMessage(1);
				}else{
					for (int i = 0; i < GApplication.smlist.size(); i++) {
						if(GApplication.smlist.get(i).equals(GApplication.getApplication().strBox)){
							continue;
						}else{
							GApplication.smlist.add(GApplication.getApplication().strBox);
							handler.sendEmptyMessage(1);
							break;
						}
					}
				}						
				System.out.println("GApplication.smlist.size() :"+GApplication.smlist.size());
			}
		}
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
