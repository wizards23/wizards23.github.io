package com.out.service;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.JoinInfo;
import com.entity.Organization;
import com.entity.WebParameter;
import com.service.WebService;

public class CorpListByPlanNumService {
	//根据加钞计划编号获取机构列表
	public List<Organization> getCorpListByPlanNum(String planNum) throws Exception{
		String methodName = "getCorpListByPlanNum";
		WebParameter[] param ={ 
				new WebParameter<String>("arg0",planNum)
				};
		List<Organization> list = null;
		Log.i("planNum", planNum);
		SoapObject soap = WebService.getSoapObject(methodName, param);
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		
		if("00".equals(code) && !params.equals("anyType{}")){
			list = new ArrayList<Organization>();
			Organization oo = new Organization();
			oo.setId("-1");
			oo.setName("请选择地点");
			list.add(oo);
			String[] array = params.split("\r\n");
			for (int i = 0; i < array.length; i++) {
				String[] arr = array[i].split(";");
				Organization o = new Organization();
				o.setId(arr[0]);
				o.setName(arr[1]);
				list.add(o);
			}
		}
		return list;
	}
	
	/**
	 * 根据计划编号返回ATM机
	 * @param planNum
	 * @return
	 * @throws Exception
	 */
	public List<Organization> getATMListByPlanNum(String planNum,String type) throws Exception{
		String methodName = "getAtmNum";
		WebParameter[] param ={ 
				new WebParameter<String>("arg0",planNum),
				new WebParameter<String>("arg1",type)
				};
		List<Organization> list = null;
		Log.i("planNum", planNum);
		SoapObject soap = WebService.getSoapObject(methodName, param);
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		
		if("00".equals(code) && !params.equals("anyType{}")){
			list = new ArrayList<Organization>();
			Organization oo = new Organization();
			oo.setId("-1");
			oo.setName("请选择ATM机");
			list.add(oo);
			String[] array = params.split("\r\n");
			for (int i = 0; i < array.length; i++) {
				String[] arr = array[i].split(";");
				Organization o = new Organization();
				o.setId(arr[0]);
				o.setName(arr[1]);
				list.add(o);
			}
		}
		return list;
	}
}
