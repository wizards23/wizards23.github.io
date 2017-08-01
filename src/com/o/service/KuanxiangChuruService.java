package com.o.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import com.entity.KuanXiangmingxi;
import com.entity.KuanxiangChuruEntity;
import com.entity.WebParameter;
import com.service.WebService;
/**
 * 获取款箱早送出库
 * @author Administrator
 *
 */
public class KuanxiangChuruService {
	/**
	 * 获取款箱早送出库列表
	 * @param name 机构编号
	 * @return
	 */
	public List<KuanxiangChuruEntity> getBoxSendOutEarlyList(String name){
		
		String methodName = "getBoxSendOutEarlyList";
		
		WebParameter[] param ={new WebParameter("arg0", name)};
		
		SoapObject so = null;
		try {
			so = WebService.getSoapObject2(methodName, param);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("-------------------kkkkk"+so);
		String code = so.getProperty("code").toString();
		String state = so.getProperty("msg").toString();//成功
		String params = so.getProperty("params").toString();	
		String[] array = params.split("\r\n");
		List<KuanxiangChuruEntity> list = new ArrayList<KuanxiangChuruEntity>();		
		if(code.equals("00")){		
			for (int i = 0; i < array.length; i++) {
				String[] arr = array[i].split(";");
				KuanxiangChuruEntity entity = new KuanxiangChuruEntity();
				entity.setLineNum(arr[0]);
				entity.setMoneyCarLine(arr[1]);
				entity.setNetNum(arr[2]);
				entity.setBoxNum(arr[3]);
				entity.setDate(arr[4]);
				list.add(entity);
			}		
		}		
		return list;
	}
	/**
	 * 获取款箱晚收入库列表
	 * @param name
	 * @return
	 */
	public List<KuanxiangChuruEntity> getBoxStorageLateList(String name){
		
		String methodName = "getBoxStorageLateList";
		
		WebParameter[] param ={new WebParameter("arg0", name)};
		
		SoapObject so = null;
		try {
			so = WebService.getSoapObject2(methodName, param);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String code = so.getProperty("code").toString();
		String state = so.getProperty("msg").toString();//成功
		String params = so.getProperty("params").toString();	
		String[] array = params.split("\r\n");
		List<KuanxiangChuruEntity> list = new ArrayList<KuanxiangChuruEntity>();		
		if(code.equals("00")){		
			for (int i = 0; i < array.length; i++) {
				String[] arr = array[i].split(";");
				KuanxiangChuruEntity entity = new KuanxiangChuruEntity();
				entity.setLineNum(arr[0]);
				entity.setMoneyCarLine(arr[1]);
				entity.setNetNum(arr[2]);
				entity.setBoxNum(arr[3]);
				entity.setDate(arr[4]);
				list.add(entity);
			}		
		}		
		return list;
	}
	
	/**
	 * 获取款箱早送出库明细
	 * @param lineNum 路线编号
	 * @param date 日期
	 * @return
	 * @throws Exception 
	 */
	public List<KuanXiangmingxi> getBoxSendOutEarlyDetail(String lineNum,String date) throws Exception{
		String methodName = "getBoxSendOutEarlyDetail";
		WebParameter[] param = {new WebParameter("arg0", lineNum),
				new WebParameter("arg1", date)};
		SoapObject so = null;
			so = WebService.getSoapObject2(methodName, param);
			System.out.println("--------getBoxSendOutEarlyDetail传参："+lineNum+"+"+date);
		System.out.println("-------------------getBoxSendOutEarlyDetail"+so);
		String code = so.getProperty("code").toString();
		String state = so.getProperty("msg").toString();//成功
		String params = so.getProperty("params").toString();
		String[] array = params.split("\r\n");
		List<KuanXiangmingxi> list = new ArrayList<KuanXiangmingxi>();
		if(code.equals("00")){				
			for (int i = 0; i < array.length; i++) {
				String[] arr = array[i].split(";");	
				KuanXiangmingxi kt = new KuanXiangmingxi();
				kt.setNetId(arr[0]);
				kt.setNetName(arr[1]);
				kt.setBoxIds(arr[2]);
				list.add(kt);
			}									
		}
		return list;
	}
	
	/**
	 * 获取款箱晚收入库明细
	 * @param lineNum 路线编号
	 * @param date 日期
	 * @return
	 */
	public List<KuanXiangmingxi> getBoxStorageLateDetail(String lineNum){
		String methodName = "getBoxStorageLateDetail";
		WebParameter[] param = {new WebParameter("arg0", lineNum)};
		SoapObject so = null;
		try {
			so = WebService.getSoapObject2(methodName, param);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("[晚收明细---]："+so);
		String code = so.getProperty("code").toString();
		String state = so.getProperty("msg").toString();//成功
		String params = so.getProperty("params").toString();
		String[] array = params.split("\r\n");
		List<KuanXiangmingxi> list = new ArrayList<KuanXiangmingxi>();
		
		if(code.equals("00")){			
			for (int i = 0; i < array.length; i++) {
				String[] arr = array[i].split(";");
				KuanXiangmingxi kt = new KuanXiangmingxi();			
				kt.setNetId(arr[0]);
				kt.setNetName(arr[1]);
				kt.setBoxIds(arr[2]);
				list.add(kt);
			}									
			return list;
		}
		return null;
	}
	/**
	 * 获取任务日期
	 */
	public String getSysTime() throws Exception{
		String methodName = "getSysTime";
		WebParameter[] param = {};
		SoapObject so = WebService.getSoapObject2(methodName, param);
		String code = so.getProperty("code").toString();
		String params = so.getProperty("params").toString();	
		if(code.equals("00")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			System.out.println(sdf.format(sdf.parse(params)));
			return sdf.format(sdf.parse(params));
		}
		return null;
	}
	
}
