package com.ljsw.tjbankpda.yy.service;



import org.ksoap2.serialization.SoapObject;
import com.example.app.entity.WebParameter;
import com.ljsw.tjbankpda.util.WebServiceFromThree;
import com.service.FixationValue;

/**
 * 清分审核服务类
 * @author Administrator
 */
public class ICleaningManagementService {
	/**
	 * 清分管理员任务计划单列表
	 * @return
	 * @throws Exception 
	 */
	public String getQingfenGuanliJihuadan(String corpId) throws Exception{
		String methodName = "getQingfenGuanliJihuadan";
		WebParameter[] parameter = {new WebParameter<String>("arg0", corpId)};
		SoapObject soap = WebServiceFromThree.getSoapObject(methodName, parameter, FixationValue.NAMESPACE, FixationValue.URL4);		
		if(soap!=null){
			String code = soap.getProperty("code").toString();
			String params = soap.getProperty("params").toString();
			if (code.equals("00")) {
				System.out.println("清分管理员任务计划单列表接口调用成功");
				return params;
			}
		}	
		return null;
	}
	/**
	 * 装箱任务任务完成总进度
	 * @param plandId  计划单Id
	 * @return
	 * @throws Exception 
	 */
	public String getQinglinzongjindu(String plandId) throws Exception{
		String methodName = "getQinglinzongjindu";
		WebParameter[] parameter = {new WebParameter<String>("arg0", plandId)};			
		SoapObject soap = WebServiceFromThree.getSoapObject(methodName, parameter, FixationValue.NAMESPACE, FixationValue.URL4);		
		if(soap!=null){
			String code = soap.getProperty("code").toString();
			String params = soap.getProperty("params").toString();
			if (code.equals("00")) {
				System.out.println("装箱任务任务完成总进度接口调用成功 : ");
				System.out.println(params);
				return params;
			}
		}	
		return null;
	}
	/**
	 * 装箱审核提交
	 * @param jihuadan 计划单Id
	 * @param lineName 任务单号,多个任务单号用_拼接
	 * @return
	 * @throws Exception 
	 */
	public String setQingfenShenheSubmit(String jihuadan ) throws Exception{
		String methodName ="setQingfenShenheSubmit";
		WebParameter[] parameter = {
				new WebParameter<String>("arg0", jihuadan)
				};
		
		SoapObject soap = WebServiceFromThree.getSoapObject(methodName, parameter, FixationValue.NAMESPACE, FixationValue.URL4);
		System.out.println("---setQingfenShenheSubmit传参："+jihuadan);
		System.out.println("---setQingfenShenheSubmit返回："+soap);
		if(soap!=null){
			String code = soap.getProperty("code").toString();
			String params = soap.getProperty("params").toString();
			if (code.equals("00")) {
				System.out.println("装箱任务任务完成总进度接口调用成功 params:"+params);
				return params;
			}
		}
		return null;	
	}
}
