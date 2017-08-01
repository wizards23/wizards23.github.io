package com.clearadmin.service;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.BoxDetail;
import com.entity.WebParameter;
import com.service.WebService;

public class GetRecycleCashboxCheckInfoService {
	//回收钞箱清点-扫描钞箱
	
	/**
	 * 
	 * @param orderNum  订单编号
	 * @param cashboxNum  钞箱编号
	 * @param userId1   用户ID
	 * @param userId2  用户ID
	 * @param corpId 机构ID
	 * @return
	 * @throws Exception
	 */
	public BoxDetail updateAndGetRecycleCashboxCheckInfo(String orderNum,String cashboxNum,String userId1,
			String userId2,String corpId) throws Exception{
		
		String methodName = "updateAndGetRecycleCashboxCheckInfo";
		WebParameter[] param ={ 
			new WebParameter<String>("arg0",orderNum),
			new WebParameter<String>("arg1",cashboxNum),
			new WebParameter<String>("arg2",userId1),
			new WebParameter<String>("arg3",userId2),
			new WebParameter<String>("arg4",corpId),
			};
	Log.i("cashboxNum", cashboxNum);
	SoapObject soap = WebService.getSoapObject(methodName, param);	
	BoxDetail box = null;
	
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		
		if("00".equals(code) && !params.equals("anyType{}")){
			box = new BoxDetail();
			String[] array =params.split(";");
			box.setBrand(array[0]);
			box.setNum(array[1]);
			box.setMoney(array[2]);
		}
	
	return box;
	
	}
}
