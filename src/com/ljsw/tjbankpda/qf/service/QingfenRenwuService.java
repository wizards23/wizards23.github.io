package com.ljsw.tjbankpda.qf.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.example.app.entity.WebParameter;
import com.ljsw.tjbankpda.qf.entity.TianJiaXianJin;
import com.ljsw.tjbankpda.qf.entity.TianJiaZhongKong;
import com.ljsw.tjbankpda.util.Table;
import com.ljsw.tjbankpda.util.WebServiceFromThree;
import com.service.FixationValue;


/**
 * 清分员任务数据获取Service类
 * @time 2015-06-18  15:16
 * @author FUHAIQING
 */
public class QingfenRenwuService {
	
	/**
	 * 获取清分员任务
	 * @param userId 用户ID
	 * @return 获取成功返回数据,失败则返回null
	 * @throws Exception
	 */
	public String getQingfenRenwu( String userId) throws Exception {
		String methodName = "getQingfenRenwu";//接口方法
		WebParameter[] param = { new WebParameter<String>("arg0", userId) };//传入参数
		SoapObject soap = null;//创建返回值接收对象
		soap =WebServiceFromThree.getSoapObject(methodName, param, FixationValue.NAMESPACE, FixationValue.URL3);//根据路径获得返回值
		String code = soap.getProperty("code").toString().trim();
		String msg = soap.getProperty("msg").toString();
		String params=soap.getProperty("params").toString();
		//String params="renwudan:SJQF002|xianluming:东青支行一线,东青支行二线,东青支行三线|wangdianshu:2,2,2|zhouzhuanxiangshu:6,4,5|\nrenwudan:QLZX001|xianluming:西青支行一线,西青支行二线|wangdianshu:2,2|";
		System.out.println("result--code="+code+"/msg="+msg+"/params="+params);
		if ("00".equals(code)) {//code=00->成功,code=99->失败
			return params;
		}else{
			return null;
		}
	}
	
	/**
	 * 清分员根据线路获取该线路下网点以及网点下的订单列表
	 * @param lineId 线路id
	 * @param renwudan 任务单号
	 * @return
	 * @throws Exception
	 */
	public String getQinglingMingxi(String lineId,String renwudan) throws Exception {
		String methodName = "getQinglingMingxi";//接口方法
		WebParameter[] param = { new WebParameter<String>("arg0", lineId),
				new WebParameter<String>("arg1", renwudan) };
		SoapObject soap = null;//创建返回值接收对象
		soap =WebServiceFromThree.getSoapObject(methodName, param, FixationValue.NAMESPACE, FixationValue.URL3);//根据路径获得返回值
		String code = soap.getProperty("code").toString().trim();
		String msg = soap.getProperty("msg").toString();
		String params=soap.getProperty("params").toString();
		System.out.println("result--code="+code+"/msg="+msg+"/params="+params);
		if ("00".equals(code)) {//code=00->成功,code=99->失败
			return params;
		}else{
			return null;
		}
	}
	
	/**
	 * 只有一个传入参数的接口使用连接类
	 * @param inputParams 传入参数
	 * @param methodName 方法名
	 * @return 获取成功返回数据,失败则返回null
	 * @throws Exception
	 */
	public String getParams( String inputParams,String methodName) throws Exception {
		WebParameter[] param = { new WebParameter<String>("arg0", inputParams) };//传入参数
		SoapObject soap = null;//创建返回值接收对象
		soap =WebServiceFromThree.getSoapObject(methodName, param, FixationValue.NAMESPACE, FixationValue.URL3);//根据路径获得返回值
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params=soap.getProperty("params").toString();
		
		System.out.println("msg:"+msg+" params:"+params);
		if ("00".equals(code)) {//code=00->成功,code=99->失败
			return params;
		}else{
			return null;
		}
	}
	
	/**
	 * 清分员上缴清分获取订单所有周转箱
	 * @param taskId 订单号
	 * @return 获取成功返回数据,失败则返回null
	 * @throws Exception
	 */
	public String getQfZhouzhuanxiangID( String taskId) throws Exception {
		String methodName = "getQfZhouzhuanxiangID";//接口方法
		WebParameter[] param = { new WebParameter<String>("arg0", taskId) };//传入参数
		SoapObject soap = null;//创建返回值接收对象
		soap =WebServiceFromThree.getSoapObject(methodName, param, FixationValue.NAMESPACE, FixationValue.URL3);//根据路径获得返回值
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params=soap.getProperty("params").toString();
		if ("00".equals(code)) {//code=00->成功,code=99->失败
			return params;
		}else{
			return null;
		}
	}
	
	/**
	 * 请领装箱提交
	 * @param renwudan 任务单
	 * @param userid 清分员id
	 * @param boxNum 周转箱编号
	 * @param dingdianID 订单id
	 * @param onceKeyNum 一次性锁扣编号
	 * @param jigouid 机构id
	 * @return
	 * @throws Exception
	 */
	public boolean submitZzxInfo( String renwudan,String userid,String boxNum,String dingdianID,String onceKeyNum,String jigouid) throws Exception {
		System.out.println("renwudan="+renwudan);
		System.out.println("userid="+userid);
		System.out.println("boxNum="+boxNum);
		System.out.println("dingdianID="+dingdianID);
		System.out.println("onceKeyNum="+onceKeyNum);
		System.out.println("jigouid="+jigouid);
		String methodName = "setZhuangxiangSubmit";//接口方法
		WebParameter[] param = { new WebParameter<String>("arg0", renwudan),
				new WebParameter<String>("arg1", userid), 
				new WebParameter<String>("arg2", boxNum), 
				new WebParameter<String>("arg3", dingdianID), 
				new WebParameter<String>("arg4", onceKeyNum), 
				new WebParameter<String>("arg5", jigouid)};//传入参数
		SoapObject soap = null;//创建返回值接收对象
		soap =WebServiceFromThree.getSoapObject(methodName, param, FixationValue.NAMESPACE, FixationValue.URL3);//根据路径获得返回值
		String code = soap.getProperty("code").toString();
		System.out.println("code="+code);
		if ("00".equals(code)) {//code=00->成功,code=99->失败
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 
	 * @param peisongId
	 * @return
	 * @throws Exception
	 */
	public String isCanSubmit(String peisongId) throws Exception {
		String methodName = "getQingfendengji";// 接口方法
		WebParameter[] param = { new WebParameter<String>("arg0", peisongId) };// 配送单ID
		SoapObject soap = null;// 创建返回值接收对象
		soap = WebServiceFromThree.getSoapObject(methodName, param,
				FixationValue.NAMESPACE, FixationValue.URL3);// 根据路径获得返回值
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		System.out.println("params-->:"+params);
		if ("00".equals(code)) {// code=00->成功,code=99->失败
			return params;
		} else {
			return null;
		}
	}
	
	/**
	 * 将清点的数据提交至服务器，服务器对数据审核，来判断清分员是否需要复清
	 * @param peisongId 配送单ID
	 * @param xianjin 现金msg
	 * @param zhongkong 重空凭证msg
	 * @param dizhi 抵质押品msg
	 * @return boolean 00为成功,返回true,99为失败,返回false
	 * @throws Exception
	 */
	public boolean submitShangjiao(String renwudan,String peisongId,String user,String xianjin,String zhongkong,String dizhi) throws Exception{
		String methodName = "setQingfenContrast";//接口方法
		System.out.println("user:"+user);
		WebParameter[] param = { new WebParameter<String>("arg0", renwudan),//任务单号
								new WebParameter<String>("arg1", peisongId), //配送单ID
								new WebParameter<String>("arg2", user),//登录用户帐号
													  new WebParameter<String>("arg3", xianjin),//现金数据
													  new WebParameter<String>("arg4", zhongkong),//重空凭证
													  new WebParameter<String>("arg5", dizhi)};//抵质押品
		SoapObject soap = null;//创建返回值接收对象
		
		soap =WebServiceFromThree.getSoapObject(methodName, param, FixationValue.NAMESPACE, FixationValue.URL3);
		//根据路径获得返回值
		System.out.println("soap--->"+soap);
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		if ("00".equals(code)) {//code=00->成功,code=99->失败
			System.out.println("msg--->true-->"+msg);
			return true;
		}else{
			System.out.println("msg--->flase-->"+msg);
			return false;
		}
	}
	/**
	 * 获取清分员小组
	 * @param userId 新添接口
	 * @return
	 * @throws Exception
	 */
	public String getQingfenXiaozu(String userId) throws Exception {
		String methodName = "getQingfenXiaozu";// 接口方法
		WebParameter[] param = { new WebParameter<String>("arg0", userId) };// 清分员用户id
		SoapObject soap = null;// 创建返回值接收对象
		soap = WebServiceFromThree.getSoapObject(methodName, param,
				FixationValue.NAMESPACE, FixationValue.URL3);// 根据路径获得返回值
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		System.out.println("params-->:"+params);
		if ("00".equals(code)) {// code=00->成功,code=99->失败
			return params;
		} else {
			return null;
		}
	}
	
	/**
	 * 查询周转箱是否可用
	 * @param zzxId
	 * @return
	 * @throws Exception
	 */
	public String getZzxShifoukeyong(String zzxId) throws Exception{
		String methodName = "getZzxShifoukeyong";// 接口方法
		WebParameter[] param = { new WebParameter<String>("arg0", zzxId) };//周转箱id
		SoapObject soap = null;// 创建返回值接收对象
		soap = WebServiceFromThree.getSoapObject(methodName, param,
				FixationValue.NAMESPACE, FixationValue.URL3);// 根据路径获得返回值
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		System.out.println("params-->:"+params);
		if ("00".equals(code)) {   // yes: 可用    no1:已被使用   no2: 无效周转箱  null：周转箱为空
			return params;
		} else {
			return null;
		}
	}
	
	/**
	 * 查询券别信息
	 * @param zzxId
	 * @return
	 * @throws Exception
	 */
	public String getQuanbieList() throws Exception{
		String methodName = "getQuanbieList";// 接口方法
		
		SoapObject soap = null;// 创建返回值接收对象
		soap = WebServiceFromThree.getSoapObject(methodName, null,
				FixationValue.NAMESPACE, FixationValue.URL3);// 根据路径获得返回值
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		System.out.println("params-->:"+params);
		if ("00".equals(code)) {
			return params;
		} else {
			return null;
		}
	}
	
	/**
	 * 获取重空信息集合
	 * @param zzxId
	 * @return
	 * @throws Exception
	 */
	public String getZhongkongList() throws Exception{
		String methodName = "getZhongkongList";// 接口方法
		
		SoapObject soap = null;// 创建返回值接收对象
		soap = WebServiceFromThree.getSoapObject(methodName, null,
				FixationValue.NAMESPACE, FixationValue.URL3);// 根据路径获得返回值
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		System.out.println("params-->:"+params);
		if ("00".equals(code)) {
			return params;
		} else {
			return null;
		}
	}
}
