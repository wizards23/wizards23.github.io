package com.ljsw.tjbankpda.yy.service;

import org.ksoap2.serialization.SoapObject;

import com.example.app.entity.WebParameter;
import com.ljsw.tjbankpda.util.WebServiceFromThree;
import com.service.FixationValue;

public class IKuGuanYuanService {
	
	/**
	 * 押运员周转箱交接
	 * @param zzxShangjiao 上缴的周转箱编号(多个周转箱用"_"分隔)
	 * @param zzxQingling 请领的周转箱编号(多个周转箱用"_"分隔)
	 * @param userWangdian 网点人员编号 （多个用户用"_"分隔）
	 * @param userYayun 押运员编号
	 * @param qlJiaojieType 请领交接类别 (类别见交接类别文档)
	 * @param sjJiaojieType 上缴交接类别
	 * @param qlpaigongdan 请领派工单
	 * @return 获取成功返回数据,失败则返回null
	 * @throws Exception
	 */
	public String SaveAuthLogYayun(String zzxShangjiao, String zzxQingling,
			String userWangdian, String userYayun, String qlJiaojieType,
			String sjJiaojieType, String qlpaigongdan) throws Exception {
		String methodName = "saveAuthLog";// 接口方法
		WebParameter[] param = { new WebParameter<String>("arg0", zzxShangjiao),
				new WebParameter<String>("arg1", zzxQingling),
				new WebParameter<String>("arg2", userWangdian),
				new WebParameter<String>("arg3", userYayun),
				new WebParameter<String>("arg4", qlJiaojieType),
				new WebParameter<String>("arg5", sjJiaojieType),
				new WebParameter<String>("arg6", qlpaigongdan)};// 传入参数
		SoapObject soap = null;// 创建返回值接收对象
		soap = WebServiceFromThree.getSoapObject(methodName, param,
				FixationValue.NAMESPACE, FixationValue.URL5);// 根据路径获得返回值
		String code = soap.getProperty("code").toString().trim();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		System.out.println("result--code=" + code + "/msg=" + msg + "/params="+ params);
		if ("00".equals(code)) {// code=00->成功,code=99->失败
			return params;
		} else {
			return null;
		}
	}

}
