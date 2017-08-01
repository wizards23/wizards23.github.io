package com.ljsw.tjbankpda.yy.service;

import org.ksoap2.serialization.SoapObject;

import com.example.app.entity.WebParameter;
import com.ljsw.tjbankpda.util.WebServiceFromThree;
import com.service.FixationValue;

public class ICleaningManService {
	/**
	 * 扫描周转箱信息
	 * 
	 * @param zzxbianhao
	 * @return
	 * @throws Exception
	 */
	public String zhouzhuangxiangXinxi(String zzxbianhao) throws Exception {
		String methodName = "zhouzhuangxiangXinxi";
		WebParameter[] parameter = { new WebParameter<String>("arg0",
				zzxbianhao) };
		SoapObject soap = WebServiceFromThree.getSoapObject(methodName,
				parameter, FixationValue.NAMESPACE,
				FixationValue.URL4);
		if (soap != null) {
			String code = soap.getProperty("code").toString();
			String params = soap.getProperty("params").toString();
			if (code.equals("00")) {
				System.out.println("扫描周转箱信息接口调用成功  :" + params);
				return params;
			}
		}
		return null;

	}

	/**
	 * 押运员配送与上缴任务列表
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getYayunyuanRenwu(String userId, String jigouId)
			throws Exception {
		System.out.println("进入押运员任务列表方法");
		System.out.println("userId:" + userId);
		System.out.println("jigouId:" + jigouId);
		String methodName = "getYayunyuanRenwu";
		WebParameter[] parameter = { new WebParameter<String>("arg0", userId),
				new WebParameter<String>("arg1", jigouId) };
		SoapObject soap = WebServiceFromThree.getSoapObject(methodName,
				parameter, FixationValue.NAMESPACE,
				FixationValue.URL3);
		if (soap != null) {
			String code = soap.getProperty("code").toString();
			String params = soap.getProperty("params").toString();
			if (code.equals("00")) {
				System.out.println("押运员任务列表接口调用成功  :" + params);
				return params;
			}
		}
		return null;
	}

	/**
	 * 押运员获取一个机构下的请领与上缴的周转箱
	 * 
	 * @param corpId
	 * @param sjpaigongdan
	 * @param qlpaigongdan
	 * @return
	 * @throws Exception
	 */
	public String getYayunyuanZhouzhuangxiang(String corpId,
			String sjpaigongdan, String qlpaigongdan) throws Exception {
		String methodName = "getYayunyuanZhouzhuangxiang";
		WebParameter[] parameter = { new WebParameter<String>("arg0", corpId),
				new WebParameter<String>("arg1", sjpaigongdan),
				new WebParameter<String>("arg2", qlpaigongdan) };
		SoapObject soap = WebServiceFromThree.getSoapObject(methodName,
				parameter, FixationValue.NAMESPACE,
				FixationValue.URL3);
		if (soap != null) {
			String code = soap.getProperty("code").toString();
			String params = soap.getProperty("params").toString();
			if (code.equals("00")) {
				System.out
						.println("押运员周转箱获取接口调用成功  : 我要看看  ____++++:" + params);
				return params;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param zzxShangjiao 
	 * @param zzxQingling
	 * @param userWangdian
	 * @param userYayun
	 * @param qlJiaojieType
	 * @param sjJiaojieType
	 * @param qlpaigongdan
	 * @return
	 * @throws Exception
	 */
	public String SaveAuthLogYayun(String zzxShangjiao, String zzxQingling,
			String userWangdian, String userYayun, String yayunXianluId,String qlJiaojieType,
			String sjJiaojieType) throws Exception {
		System.out.println("调用 SaveAuthLogYayun 普通方法");
		System.out.println("zzxShangjiao:"+zzxShangjiao);
		System.out.println("zzxQingling:"+zzxQingling);
		System.out.println("userWangdian:"+userWangdian);
		System.out.println("userYayun:"+userYayun);
		System.out.println("yayunXianluId:"+yayunXianluId);
		System.out.println("qlJiaojieType:"+qlJiaojieType);
		System.out.println("sjJiaojieType:"+sjJiaojieType);
		String methodName = "saveAuthLogYayun";
		WebParameter[] parameter = { new WebParameter<String>("arg0", zzxShangjiao),
				new WebParameter<String>("arg1", zzxQingling),
				new WebParameter<String>("arg2", userWangdian),
				new WebParameter<String>("arg3", userYayun),
				new WebParameter<String>("arg4", yayunXianluId),
				new WebParameter<String>("arg5", qlJiaojieType),
				new WebParameter<String>("arg6", sjJiaojieType)};
		SoapObject soap = WebServiceFromThree.getSoapObject(methodName,
				parameter, FixationValue.NAMESPACE,FixationValue.URL5);
		if (soap != null) {
			String code = soap.getProperty("code").toString();
			String params = soap.getProperty("params").toString();
			if (code.equals("00")) {
				System.out.println("押运员交接接口调用成功  : 我要看看  ____++++:" + params);
				return params;
			}
		}
		return null;
	}
	/**
	 * 获取机构类别:0--总行 1--支行 2--网点
	 * @param jigouId
	 * @return
	 * @throws Exception
	 */
	public String getJigouLeibie(String jigouId) throws Exception{
		String methodName = "getJigouLeibie";
		WebParameter[] parameter = { new WebParameter<String>("arg0", jigouId)};
		
		SoapObject soap = WebServiceFromThree.getSoapObject(methodName,
				parameter, FixationValue.NAMESPACE,FixationValue.URL3);
		if (soap != null) {
			String code = soap.getProperty("code").toString();
			String params = soap.getProperty("params").toString();
			if (code.equals("00")) {
				System.out.println("params-->"+params);
				return params;
			}
		}
		return null;
	}
	
	
}
