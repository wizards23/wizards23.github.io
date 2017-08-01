package com.moneyboxadmin.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.BoxDetail;
import com.entity.WebParameter;
import com.moneyboxadmin.pda.BoxDoDetail;
import com.service.WebService;

public class GetEmptyRecycleCashboxInDetailService {
	//已清回收钞箱明细
	
		/**
		 * 业务单号
		 */
		public  List<BoxDetail> getEmptyRecycleCashboxInList(String bizNum) throws Exception{
			
			String methodName = "getEmptyRecycleCashboxInDetail";

			WebParameter[] parameter = {
					new WebParameter<String>("arg0", bizNum),
					};
			Log.i("bizNum已清回收钞箱明细", bizNum);
			SoapObject soapObject = WebService.getSoapObject(methodName, parameter);
			String state = soapObject.getProperty("msg").toString();
			String params = soapObject.getPropertyAsString("params");
			List<BoxDetail> list = null;
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
