package com.out.service;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.JoinInfo;
import com.entity.WebParameter;
import com.service.WebService;

public class CashboxHandoverInfoService {
	//交接情况
	/**
	 * 
	 * @param planNum  加钞计划编号
	 * @return
	 * @throws Exception
	 */
	public List<JoinInfo> getCashboxHandoverInfo(String planNum) throws Exception{
		String methodName = "getCashboxHandoverInfo";
		WebParameter[] param ={ 
				new WebParameter<String>("arg0",planNum)
				};
		List<JoinInfo> list = null;
		Log.i("planNum",planNum);
				
		SoapObject soap = WebService.getSoapObject(methodName, param);
		String code = soap.getProperty("code").toString();
		Log.i("code",code);
		if("00".equals(code)){
			list = new ArrayList<JoinInfo>();
			String params = soap.getProperty("params").toString();
			if(params.equals("anyType{}")){
				return null;
			}
			String[] array = params.split("\r\n");
			for (int i = 0; i < array.length; i++) {
				String[] arr = array[i].split(";");
				JoinInfo join = new JoinInfo();
				join.setAddress(arr[0]);
				join.setAddnum(arr[1]);
				join.setAddstate(arr[2]);
				join.setBacknum(arr[3]);
				join.setBackstate(arr[4]);
				Log.i("arr[1]", arr[1]);
				list.add(join);
			}
		}
		Log.i("list.size()", list.size()+"");
		return list;
	}
}
