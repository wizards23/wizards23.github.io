package com.out.service;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.BoxDetail;
import com.entity.JoinInfo;
import com.entity.WebParameter;
import com.out.biz.CashboxNumByCorpIdBiz;
import com.service.WebService;

public class CashboxNumByCorpIdService {
//网点加钞钞箱交接-根据所在位置获取交接钞箱数量(加钞钞箱和回收钞箱数量相同)
	
	/**
	 * 
	 * @param userId   用户ID
	 * @param planNum  计划编号
	 */
	public List<BoxDetail> getCashboxNumByCorpId(String planNum,String corpId,String type) throws Exception{
		String methodName = "getCashboxNumByCorpId";
		WebParameter[] param ={ 
				new WebParameter<String>("arg0",planNum),
				new WebParameter<String>("arg1",corpId),
				new WebParameter<String>("arg2",type)
				};
		Log.i("arg0",planNum);
		Log.i("arg1",corpId);
		Log.i("arg2",type);
		List<BoxDetail> list = null;
		SoapObject soap = WebService.getSoapObject(methodName, param);
		Log.i("交接钞箱数量", soap+"");
		String code = soap.getProperty("code").toString();
		String params= soap.getProperty("params").toString();
		
		if("00".equals(code) && !"anyType{}".equals(params)){
			Log.i("来了没有","进");
			list = new ArrayList<BoxDetail>();
			
			String[] array = params.split("\r\n");
			Log.i("array",array.length+"进");
			CashboxNumByCorpIdBiz.boxCount = array[0];
			for (int i = 1; i < array.length; i++) {
				Log.i("list","进");
				String[] arr = array[i].split(";");
				BoxDetail box = new BoxDetail();
				box.setNum(arr[1]);
				box.setBrand(arr[0]);
				list.add(box);
				Log.i("listsize",list.size()+"");
			}
			
		}
		return list;
		
	}
	
	/**
	 * 离行式加钞查询
	 * @param planNum  计划编号
	 * @param ATM   ATM编号
	 * @param type  状态  0.新钞箱扫描  1.回收钞箱扫描
	 * @return
	 * @throws Exception
	 */
	public List<BoxDetail> getCashboxNumByATM(String planNum,String ATM,String type) throws Exception{
		String methodName = "getCashBoxInfo";
		WebParameter[] param ={ 
				new WebParameter<String>("arg0",planNum),
				new WebParameter<String>("arg1",ATM),
				new WebParameter<String>("arg2",type)
				};
		Log.i("arg0",planNum);
		Log.i("arg1",ATM);
		Log.i("arg2",type);
		List<BoxDetail> list = null;
		SoapObject soap = WebService.getSoapObject(methodName, param);
		Log.i("交接钞箱数量", soap+"");
		String code = soap.getProperty("code").toString();
		String params= soap.getProperty("params").toString();
		
		if("00".equals(code) && !"anyType{}".equals(params)){
			Log.i("来了没有","进");
			list = new ArrayList<BoxDetail>();
			
			String[] array = params.split("\r\n");
			Log.i("array",array.length+"进");
			CashboxNumByCorpIdBiz.boxCount = array[0];
			for (int i = 1; i < array.length; i++) {
				Log.i("list","进");
				String[] arr = array[i].split(";");
				BoxDetail box = new BoxDetail();
				box.setNum(arr[1]);
				box.setBrand(arr[0]);
				list.add(box);
				Log.i("listsize",list.size()+"");
			}
			
		}
		return list;
		
	}
}
