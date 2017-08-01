package com.clearadmin.service;

import org.ksoap2.serialization.SoapObject;

import com.entity.BoxDetail;
import com.entity.WebParameter;
import com.service.WebService;

public class UpdateAndGetAddMoneySumService {
	//钞箱加钞-加钞操作-扫描钞箱编号

 public BoxDetail updateAndGetAddMoneySum(String planNum,String cashboxNum,
		 String userId1,String userId2,String corpId) throws Exception{
	    
	 	String methodName = "updateAndGetAddMoneySum";
		WebParameter[] param ={ 
				new WebParameter<String>("arg0",planNum),
				new WebParameter<String>("arg1",cashboxNum),
				new WebParameter<String>("arg2",userId1),
				new WebParameter<String>("arg3",userId2),
				new WebParameter<String>("arg4",corpId),
				};
		SoapObject soap = WebService.getSoapObject(methodName, param);
		
		BoxDetail box = null;
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		if("00".equals(code) && !params.equals("anyType{}")){
			box = new BoxDetail();
			String[] array = params.split(";");
			box.setNum(array[0]);
			box.setBrand(array[1]);
			box.setMoney(array[2]);
		}
	    					 	 	 	 
	  return box;	 
 	}
}
