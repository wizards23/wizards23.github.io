package com.moneyboxadmin.service;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.BoxDetail;
import com.entity.WebParameter;
import com.service.WebService;

public class GetemptyCashBoxOutDetailService {
	//空钞箱出库明细
	
	/**
	 * 
	 * @param planNum
	 * @return
	 * @throws Exception
	 */
	public  List<BoxDetail> getemptyCashBoxOutDetail(String planNum) throws Exception{
		
		String methodName = "getemptyCashBoxOutDetail";
		Log.i("planNum",planNum);
		WebParameter[] parameter = {
				new WebParameter<String>("arg0", planNum),
				};
		Log.i("a1111","11111");
		SoapObject soapObject = WebService.getSoapObject(methodName, parameter);
		Log.i("a222","2222");
		String state = soapObject.getProperty("msg").toString();
		Log.i("a33333","33333");
		String params = soapObject.getPropertyAsString("params");
		Log.i("a4444444","444444444");
		List<BoxDetail> list = null;
		Log.i("a5555555555","55555555");
		Log.i("soapObject", soapObject+"");
		Log.i("params", params);
		Log.i("state", state);
		
		if("成功".equals(state) && !params.equals("anyType{}")){
			String[] array = params.split("\r\n");
		    list = new ArrayList<BoxDetail>(); 
			for (int i = 0; i < array.length; i++) {
				BoxDetail box = new BoxDetail();
				String[] arr = array[i].split(";");
				box.setBrand(arr[0]);
				box.setNum(arr[1]);

				list.add(box);
			}
			
		}
		return list;
	}
	
}
