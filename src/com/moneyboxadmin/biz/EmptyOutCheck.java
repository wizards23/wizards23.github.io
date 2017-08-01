package com.moneyboxadmin.biz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.entity.BoxDetail;
import com.imple.getnumber.Getnumber;

public class EmptyOutCheck {
	//空钞箱出库前检测
	
	boolean ch = true;
	//记录每种钞箱品牌的数量
	Map<String,Integer> hashmap = new HashMap<String,Integer>();

	public boolean check(){		
		for (int i = 0; i <GetBoxDetailListBiz.list.size(); i++) {
			hashmap.put(GetBoxDetailListBiz.list.get(i).getBrand(),0);
		}
		for (int i = 0; i < Getnumber.list_boxdeatil.size(); i++) {
			String brand = Getnumber.list_boxdeatil.get(i).getBrand();
			for (Map.Entry<String, Integer> item:hashmap.entrySet()) {
				if(item.getKey().equals(brand)){
					int n = item.getValue();
					n++;
					item.setValue(n);
					break;
				}				
			}
		}
		
		for (int i = 0; i <GetBoxDetailListBiz.list.size(); i++) {
			String brand = GetBoxDetailListBiz.list.get(i).getBrand();
			String num = GetBoxDetailListBiz.list.get(i).getNum();
			
			for (Map.Entry<String, Integer> item:hashmap.entrySet()) {
				String mbrand = item.getKey();
				String mnum = item.getValue().toString();
				
				if(brand.equals(mbrand)){
					if(!mnum.equals(num)){
					   ch = false;
					   break;
					}
				}
			}
		}				
		return ch;
	} 
	
	boolean issame = true;
	StringBuffer sb = new StringBuffer();
	//移除没有的品牌
	public void remove(){
	  for (int i = 0; i < Getnumber.list_boxdeatil.size(); i++) {
		  String gbrand =Getnumber.list_boxdeatil.get(i).getBrand();
		for (int j = 0; j <GetBoxDetailListBiz.list.size(); j++) {
			String brand =Getnumber.list_boxdeatil.get(i).getBrand();
			if(gbrand.equals(brand)){
				issame = false;					
			}
		}
		if(issame){
		  sb.append(i);	
		  issame = true;
		}
	  }	
	  
	  for (int i = 0; i < sb.length(); i++) {
	  Getnumber.list_boxdeatil.remove(Integer.parseInt(sb.toString().charAt(i)+""));
	  }
	  
	  
	}
	
	
	
	/**
	 * 检测是否存在无效钞箱
	 * @return
	 */
	public boolean checkBox(List<BoxDetail> list){
		if(list==null || list.size()<=0){
			return false;
		}
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i).getBrand().equals("无效钞箱")){
				return false;
			}
		}
		return true;
		
	}
	
	
}
