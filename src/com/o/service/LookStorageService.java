package com.o.service;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.LookStorage;
import com.entity.LookStorageDetail;
import com.entity.WebParameter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.service.WebService;

/**
 * 查库服务 与服务器交互
 * @author zhouKai
 *
 */
public class LookStorageService {
	
	/**
	 * 根据查看员的id获取查看任务
	 * @param codeOne
	 *            查库员1的id
	 * @param codeTwo
	 *            查库员2的id
	 * @return
	 */
	public static List<LookStorage> getTask(String codeOne, String codeTwo) throws Exception{
		List<LookStorage> result = new ArrayList<LookStorage>();
		String mothedName="getLookStorageTask";
		WebParameter<?>[] param={
				new WebParameter<String>("arg0", codeOne),
				new WebParameter<String>("arg1", codeTwo)
		};
		SoapObject soap = WebService.getSoapObject(mothedName, param);
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		if(code.equals("00")){
			result = new Gson().fromJson(params, new TypeToken<List<LookStorage>>(){}.getType());
		}else{
			return null;
		}
		Log.e("ZK", "" + result);
		return result;
	}
	
	/**
     * 根据查库任务表的id查询其对应的明细
     * @param taskId 查库任务表T_BANKINVENTORY的id
     * @return
     */
	public static List<LookStorageDetail> getTaskDetail(String taskId) throws Exception{
		List<LookStorageDetail> result = new ArrayList<LookStorageDetail>();
		String mothedName="getLookStorageTastDetail";
		WebParameter<?>[] param={
				new WebParameter<String>("arg0", taskId)
		};
		SoapObject soap = WebService.getSoapObject(mothedName, param);
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		if(code.equals("00")){
			result = new Gson().fromJson(params, new TypeToken<List<LookStorageDetail>>(){}.getType());
			//初始化清点状态为失败
			for (LookStorageDetail lsd : result) {
				lsd.setState(LookStorageDetail.FAILURE);
			}
		}else{
			return null;
		}
		Log.e("ZK", "com.o.service.LookStorageService.getTaskDetail(String):   " + result);
		return result;
	}
	
	/**
	 * 查库服务结束后，提交相应的数据
	 * @param detail	某个查库单下所对应的明细
	 * @return
	 */
	public static boolean commitTask(List<LookStorageDetail> detail) throws Exception{
		String mothedName="commitLookStorageTask";
		String json = new Gson().toJson(detail);
		WebParameter<?>[] param={
				new WebParameter<String>("arg0", json)
		};
		SoapObject soap = WebService.getSoapObject(mothedName, param);
		String code = soap.getProperty("code").toString();
		if(code.equals("00")){	//提交成功
			return true;
		}
		return false;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
