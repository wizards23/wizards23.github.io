package com.out.service;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.application.GApplication;
import com.entity.OrderDetail;
import com.entity.WebParameter;
import com.service.WebService;

public class AssignService {
	// 根据押运人员ID获取派工单列表
	/**
	 * 
	 * @param userId
	 *            用户id
	 * @return
	 * @throws Exception
	 */
	public OrderDetail getAssign(String userId, String type) throws Exception {
		String methodName = "getAssign2";
		WebParameter[] param = { new WebParameter<String>("arg0", userId),
				new WebParameter<String>("arg1", type) };

		SoapObject soap = WebService.getSoapObject(methodName, param);
		System.out.println("userid-------" + userId);
		System.out.println("soap-------" + soap);
		Log.i("userid", userId);
		Log.i("任务", soap + "");
		OrderDetail order = null;
		String params = soap.getProperty("params").toString();
		String code = soap.getProperty("code").toString();
		if (code.equals("00") && !params.equals("anyType{}")) {
			// BC91201603231;北辰九一线;离行式;2;2;津RG5458;2016-03-24;2;0
			order = new OrderDetail();
			String[] array = params.split(";");
			order.setPlanNum(array[0]); // 计划编号
			order.setWay(array[1]); // 加钞路线
			order.setType(array[2]); // 类型
			order.setATM(array[3]); // ATM数量
			order.setBoxNum(array[4]); // 钞箱数量
			order.setCarBrand(array[5]); // 车牌号码
			order.setDate(array[6]); // 日期
			order.setAddMoneyJoin(array[7]); // 网点待交接的加钞钞箱数量
			order.setBackNum(array[8]); // 网点待交接的回收钞箱数量
			/*
			 * revised by zhangXueWei
			 */
			order.setTaskCorpId(array[9]);//任务所属加钞计划编号
			GApplication.taskCorpId=array[9];

		} else if (code.equals("88")) {
			order = new OrderDetail();
			order.setCarBrand("没有做派工任务");
		}
		return order;
	}

	/**
	 * 根据押运员编号查询计划单类型(在行离行)
	 * 
	 * @param userId
	 * @return
	 */
	public String getJihuadanLeibie(String userId) throws Exception {
		String methodName = "getPlanType";
		WebParameter[] param = { new WebParameter<String>("arg0", userId) };
		SoapObject soap = WebService.getSoapObject(methodName, param);

		String params = soap.getProperty("params").toString();
		String code = soap.getProperty("code").toString();
		if (code.equals("00") && !params.equals("anyType{}")) {
			return params;
		}
		return null;
	}
}
