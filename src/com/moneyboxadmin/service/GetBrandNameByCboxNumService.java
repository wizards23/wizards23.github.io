package com.moneyboxadmin.service;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.BoxDetail;
import com.entity.WebParameter;
import com.service.WebService;

public class GetBrandNameByCboxNumService {
	// 根据钞箱编号获取钞箱品牌

	/**
	 * 
	 * @param CboxNum
	 *            钞箱编号
	 * @return
	 * @throws Exception
	 */
	public String getBrandNameByCboxNum(String CboxNum) throws Exception {
		String methodName = "getBrandNameByCboxNum";
		Log.i("arg0", CboxNum);
		WebParameter[] parameter = { new WebParameter<String>("arg0", CboxNum) };

		SoapObject soap = WebService.getSoapObject(methodName, parameter);
		Log.i("arg0", soap + "");
		String params = soap.getProperty("params").toString();
		String code = soap.getProperty("code").toString();
		if ("00".equals(code) && !"anyType{}".equals(params)) {
			return params;
		} else {
			return null;
		}
	}

	/**
	 * 根据钞箱编号查询通atm内所有的钞箱信息
	 * 
	 * @return
	 */
	public List<BoxDetail> getBoxDetailInATM(String boxNum) throws Exception {
		String methodName = "getCBoxNums";
		Log.i("arg0", boxNum);
		List<BoxDetail> list=new ArrayList<BoxDetail>();
		WebParameter[] parameter = { new WebParameter<String>("arg0", boxNum) };

		SoapObject soap = WebService.getSoapObject(methodName, parameter);
		Log.i("arg0", soap + "");
		String params = soap.getProperty("params").toString();
		String code = soap.getProperty("code").toString();
		if ("00".equals(code) && !"anyType{}".equals(params)) {
			// 解析
			String[] content=params.split("\\|");
			for (String string : content) {
				BoxDetail box=new BoxDetail();
				box.setBrand(string.split(";")[1].trim());
				box.setNum(string.split(";")[0].trim());
				list.add(box);
			}
		}
		
		return list;
	}
}
