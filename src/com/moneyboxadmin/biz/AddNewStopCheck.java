package com.moneyboxadmin.biz;

import com.imple.getnumber.StopNewClearBox;

public class AddNewStopCheck {
	//新增钞箱，停用，未清
	boolean is = false;
	public boolean check(){
		for (int i = 0; i < StopNewClearBox.list.size(); i++) {
			String brand = StopNewClearBox.list.get(i).getBrand();
			if(brand.equals("点击获取品牌") || brand.equals("正在获取...")){
				is = true;
			}
		}
		return is;
	}
	
	//重置清空
	public void clearNull(){
		if(StopNewClearBox.list.size()>0 || StopNewClearBox.liststr.size()>0){
			try {
				StopNewClearBox.list.clear();
				StopNewClearBox.liststr.clear();
			} catch (Exception e) {
			  e.printStackTrace();
			}
		}
	}
}
