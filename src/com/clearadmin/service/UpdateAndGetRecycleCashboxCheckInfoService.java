package com.clearadmin.service;

import org.ksoap2.serialization.SoapObject;

import com.entity.BoxDetail;
import com.entity.WebParameter;
import com.service.WebService;

public class UpdateAndGetRecycleCashboxCheckInfoService {
	//回收钞箱清点-扫描钞箱
	
	/**
	 * @param orderNum  业务单编号
	 * @param cashboxNum  钞箱编号
	 * @param userId1  用户编号1
	 * @param userId2  用户编号2
	 * @param corpId  机构ID
	 * @return
	 * @throws Exception 
	 */
	public BoxDetail updateAndGetRecycleCashboxCheckInfo(String orderNum,String cashboxNum,
			String userId1,String userId2,String corpId) throws Exception{
		
		String methodName = "updateAndAddMoneyConfirm";
		WebParameter[] param ={ 
			new WebParameter<String>("arg0",orderNum),
			new WebParameter<String>("arg1",cashboxNum),
			new WebParameter<String>("arg2",userId1),
			new WebParameter<String>("arg3",userId2),
			new WebParameter<String>("arg4",corpId)			
			};
	SoapObject soap = WebService.getSoapObject(methodName, param);
		BoxDetail box = null;
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		if("00".equals(code) && !"anyType{}".equals(params)){
			box = new BoxDetail();
			String[] array= params.split(";");
			box.setBrand(array[0]);
			box.setNum(array[1]);
			box.setMoney(array[2]);
		}
		return box;
	}
		
	
}
