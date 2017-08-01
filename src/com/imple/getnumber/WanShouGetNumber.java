package com.imple.getnumber;
import android.os.Handler;

import com.application.GApplication;
import com.entity.BoxDetail;
import com.strings.tocase.CaseString;

import hdjc.rfid.operator.INotify;
public class WanShouGetNumber implements INotify{
	public boolean isfind = true;
	public Handler handler;
	
	public void getNumber(String number) {
		String nowDate=GApplication.getApplication().strBox;
		String newDate=CaseString.getBoxNum3(number);
		if(GApplication.linshilist.size()>0){
			for (int i = 0; i < GApplication.linshilist.size(); i++) {
				if(GApplication.linshilist.get(i).getNum().equals(newDate)){
					return;
				}else{
					continue;
				}
			}
		}
		if (!newDate.equals(nowDate)) {
			if (number == null || !CaseString.reg(number)) {
				return;
			}
			// 十进制转换成字符
			GApplication.getApplication().strBox = CaseString.getBoxNum3(number);
			if("".equals(GApplication.getApplication().strBox))
				return;
			System.out.println("-------HH-------" + GApplication.getApplication().strBox);
			System.out.println("-------HSH-------"+ number);
			if(GApplication.jiaojiestate==3){
				int aa = -1;// 标识
				for (int i = 0; i < GApplication.zyzaolist.size(); i++) {
					
					if(GApplication.zyzaolist.get(i).equals(GApplication.getApplication().strBox)){
						aa = 0;
						BoxDetail bd = new BoxDetail();
						bd.setNum(GApplication.getApplication().strBox);
						bd.setBrand("正确");
						System.out.println("zou  走 走了我吗>>>>>>>>>>>");
						GApplication.linshilist.add(bd);						
						handler.sendEmptyMessage(1);
						break;
					}else{
						continue;			
					}
				}
				if(aa == -1){			
					if(GApplication.linshilist.size()==0){
						BoxDetail bd = new BoxDetail();
						bd.setNum(GApplication.getApplication().strBox);
						bd.setBrand("错误");
						GApplication.linshilist.add(bd);
						handler.sendEmptyMessage(1);
					}else{
						for (int i = 0; i < GApplication.linshilist.size(); i++) {
							if(GApplication.linshilist.get(i).getNum().equals(GApplication.getApplication().strBox)){
								continue;
							}else{
								BoxDetail bd = new BoxDetail();
								bd.setNum(GApplication.getApplication().strBox);
								bd.setBrand("错误");
								GApplication.linshilist.add(bd);
								handler.sendEmptyMessage(1);
								break;
							}
						}
					}
				}
			}else{
				int aa = -1;// 标识
				for (int i = 0; i < GApplication.zaolist.size(); i++) {
					if(GApplication.zaolist.get(i).equals(GApplication.getApplication().strBox)){
						aa = 0;
						BoxDetail bd = new BoxDetail();
						bd.setNum(GApplication.getApplication().strBox);
						bd.setBrand("正确");
						System.out.println("zou2222  走 走了我吗>>>>>>>>>>>");
						GApplication.linshilist.add(bd);									
						handler.sendEmptyMessage(1);
						break;
					}else{
						continue;			
					}
				}
				if(aa == -1){
					if(GApplication.linshilist.size()==0){
						BoxDetail bd = new BoxDetail();
						bd.setNum(GApplication.getApplication().strBox);
						bd.setBrand("错误");
						GApplication.linshilist.add(bd);
						handler.sendEmptyMessage(1);
					}else{
						for (int i = 0; i < GApplication.linshilist.size(); i++) {
							if(GApplication.linshilist.get(i).getNum().equals(GApplication.getApplication().strBox)){
								continue;
							}else{
								BoxDetail bd = new BoxDetail();
								bd.setNum(GApplication.getApplication().strBox);
								bd.setBrand("错误");
								GApplication.linshilist.add(bd);
								handler.sendEmptyMessage(1);
								break;
							}
						}
					}
				}
			}					
		}
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}




