package com.imple.getnumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.entity.BoxDetail;
import com.moneyboxadmin.biz.BrandNameByCboxNumBiz;
import com.moneyboxadmin.biz.GetBoxDetailListBiz;
import com.strings.tocase.CaseString;

import hdjc.rfid.operator.INotify;

public class StopNewClearBox implements INotify{
	//未清回收钞箱出库、停用钞箱出库、新增钞箱入库扫描过滤	
	public static List<BoxDetail> list  = new ArrayList<BoxDetail>();
	public static LinkedList<String> liststr = new LinkedList<String>();
	public static Handler handler;
	private BrandNameByCboxNumBiz brandNameByCboxNumBiz;
	public static int type = 0;
	private boolean have = true;
	
	BrandNameByCboxNumBiz getBrandNameByCboxNumBiz() {
		if(brandNameByCboxNumBiz==null){
			brandNameByCboxNumBiz = new BrandNameByCboxNumBiz();
		}
		return brandNameByCboxNumBiz;
	}


	@Override
	public void getNumber(String number) {
		Log.i("未清停用","usr");
		if(number!=null && !CaseString.reg(number)){
			return;
		 }
		//新增和停用
		if(type==0){
		addAndStop(number);	
		//未清回收
		}else if(type==4){
		clearOutCheck(number);
		}

	}
	
	/**
	 * 未清回收箱钞出库扫描过滤
	 */
	private void clearOutCheck(String number){
		String boxNum =CaseString.getBoxNum(number);
		Log.i("boxNum", boxNum);				
		 if(liststr.contains(boxNum)||"".equals(boxNum)){
			 return;
		 }else{
		    liststr.add(boxNum); 	 
		 }
		Message m = handler.obtainMessage();
		for (int i = 0; i <GetBoxDetailListBiz.list.size(); i++) {
		String boxM = GetBoxDetailListBiz.list.get(i).getNum();
		Log.i("boxM", boxM);
		//如果是有效数据
		  if(boxM.equals(boxNum)){			 	  				
				 BoxDetail box = new BoxDetail();  
				 box.setNum(boxM);
				 box.setBrand(GetBoxDetailListBiz.list.get(i).getBrand());
				 list.add(box); 
				 have = false;							 
			break;  
		  }			  		  
		}
		
		if(have){
			BoxDetail box = new BoxDetail();  
			box.setNum(boxNum);
			box.setBrand("无效钞箱");
			list.add(box); 	
		}
		
		m.what=1;
		handler.sendMessage(m);
		have = true;								
	}
	
	/**
	 * 新增钞箱入库和停用钞箱出库扫描过滤
	 */
	private void addAndStop(String number){
		BoxDetail box;
		//String str =number.substring(0,8);		
		String num =CaseString.getBoxNum(number);
		if("".equals(num))
			return;
			if(liststr.size()==0){			 			  
			  liststr.add(num);
			  box = new BoxDetail("正在获取...",num);
			  list.add(box);
			  Message m = handler.obtainMessage();
			  m.what=1;
			  handler.sendMessage(m);
			  //根据钞箱编号获取品牌			  
			  getBrandNameByCboxNumBiz().getBrnadNamebyNum(num,""); 
			}			
			if(!liststr.contains(num)){				 
				// num =CaseString.getBoxNum(num); 
				 box = new BoxDetail("正在获取...",num);
				 Log.i("number",list.size()+"");
				 liststr.add(num);			
				 list.add(box);
				 Message m = handler.obtainMessage();
				 m.what=1;
				 handler.sendMessage(m);
				 //根据钞箱编号获取品牌
				 getBrandNameByCboxNumBiz().getBrnadNamebyNum(num,""); 
			  }
	}
	
}
