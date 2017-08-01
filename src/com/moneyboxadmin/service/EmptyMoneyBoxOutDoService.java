package com.moneyboxadmin.service;

import org.ksoap2.serialization.SoapObject;

import com.entity.WebParameter;
import com.service.WebService;

public class EmptyMoneyBoxOutDoService {

	/**
	 * cbInfo:加钞计划编号;操作方式;钞箱品牌信息;钞箱编号;钞箱状态;审核员ID;复核员ID;机构ID;业务单号（多列时用\r\n分隔）
		空钞箱出库时 操作方式=3 钞箱状态=00 （业务单号为空字符串）
		ATM加钞出库时  操作方式=5 钞箱状态=02 （业务单号为空字符串）
		钞箱装钞入库时 操作方式=4 钞箱状态=01 （业务单号为空字符串）
		回收钞箱入库时 操作方式=6 钞箱状态=05 （业务单号为空字符串）
		未清回收钞箱出库 操作方式=8 钞箱状态=06 （加钞计划编号为空字符串）
		停用钞箱出库 操作方式=2 钞箱状态=08 （加钞计划编号和业务单号为空字符串）
		已清回收钞箱入库 操作方式=9 钞箱状态=07 （加钞计划编号为空字符串）
		新增钞箱入库 操作方式=1 钞箱状态=09 （加钞计划编号和业务单号为空字符串）
	 * @param cbinfo
	 * @return 成功或者失败
	 * @throws Exception
	 */
	public static String cashBoxOutOrIn(String cbinfo) throws Exception {

			String methodName = "cashBoxOutOrIn";

			WebParameter[] parameter = { new WebParameter<String>("arg0",cbinfo) };

			SoapObject soapObject = WebService.getSoapObject(methodName, parameter);

			String  msg = soapObject.getProperty("msg").toString();
			
			return msg;
						
			
	}
}
