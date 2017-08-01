package com.clearadmin.service;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.WebParameter;
import com.service.WebService;

public class RecycleCashboxCheckConfirmService {
	//回收钞箱清点-确定
	
	/**
	 * 
	 * @param cashboxNum:钞箱编号;
	 * @param orderNum:业务单编号;
	 * @param balance1:钞箱余额;
	 * @param balance2:废钞箱余额;
	 * @param userId:用户编号
	 * @return
	 * @throws Exception
	 */
	public boolean recycleCashboxCheckConfirm(String cashboxNum,String orderNum,String balance1,String balance2,String userId1,String userId2) throws Exception{
			String methodName = "updateAndRecycleCashboxCheckConfirm";
			WebParameter[] param ={ 
				new WebParameter<String>("arg0",cashboxNum),
				new WebParameter<String>("arg1",orderNum),
				new WebParameter<String>("arg2",balance1),
				new WebParameter<String>("arg3",balance2),
				new WebParameter<String>("arg4",userId1),
				new WebParameter<String>("arg5",userId2),
				};
		SoapObject soap = WebService.getSoapObject(methodName, param);
		String code = soap.getProperty("code").toString();
		Log.i("回收钞箱清点-确定", soap+"");
		Log.i("code", code);
		if(code.equals("00")){
			return true;
		}
		
		return false;
		
	}
}
