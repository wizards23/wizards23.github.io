package com.moneyboxadmin.service;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.User;
import com.entity.WebParameter;
import com.service.WebService;

public class SaveAuthLogService {
	// 钞箱交接

	/**
	 * 
	 * @param planNum
	 *            加钞计划编号或者业务单号
	 * @param cashBoxNum
	 *            交接的钞箱编号(多个钞箱用|分隔)
	 * @param corpId
	 *            机构ID
	 * @param roleId
	 *            角色ID
	 * @param cValue
	 *            特征值
	 * @param authType
	 *            交接类型（见交接说明）
	 * @param perNum
	 *            第几个人交接 （一般2个人交接，如果第一人按指纹就传"1";如果第二人按指纹就传"2"）
	 * @param sumPerNum
	 *            一共需要几个人交接 如果1个人交接传"1" 如果2个人交接传"2"
	 * @return
	 * @throws Exception
	 */
	public User saveAuthLog(String planNum, String cashBoxNum, String corpId,
			String roleId, byte[] cValue, String authType, String perNum,
			String sumPerNum) throws Exception {

		String methodName = "saveAuthLog";

		WebParameter[] parameter = { new WebParameter<String>("arg0", planNum),
				new WebParameter<String>("arg1", cashBoxNum),
				new WebParameter<String>("arg2", corpId),
				new WebParameter<String>("arg3", roleId),
				new WebParameter<byte[]>("arg4", cValue),
				new WebParameter<String>("arg5", authType),
				new WebParameter<String>("arg6", perNum),
				new WebParameter<String>("arg7", sumPerNum) };
		User user = null;
		Log.i("planNum", planNum + "");
		Log.i("cashBoxNum", cashBoxNum);
		Log.i("corpId", corpId);
		Log.i("roleId", roleId);
		Log.i("cValue", cValue + "");
		Log.i("authType", authType);
		Log.i("perNum", perNum);
		Log.i("sumPerNum", sumPerNum);

		SoapObject soap = WebService.getSoapObject(methodName, parameter);
		Log.i("钞箱交接", "钞箱交接");
	
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		if ("00".equals(code) && !params.equals("anyType{}")) {
			user = new User();
			String[] str = params.split(";");
			Log.i("setId", str[0]);
			Log.i("setName", str[1]);
			user.setId(str[0]);
			user.setName(str[1]);
		}else if("01".equals(code)){   //如果查询状态为01 抛出异常代办验证人省份不合法(离行押运交接)
			throw new NumberFormatException();
		}
		return user;
	}

	/**
	 * 检测加钞员是否处于空闲状态
	 * 
	 * @param userId
	 *            加钞员ID
	 * @return
	 * @throws Exception
	 */
	public String checkEscortState(String userId) throws Exception {
		String methodName = "checkEscortState";
		WebParameter[] parameter = { new WebParameter<String>("arg0", userId) };
		SoapObject soap = WebService.getSoapObject(methodName, parameter);
		Log.i("checkEscortState", "userId:" + userId);
		String code = soap.getProperty("code").toString();
		return code;
	}
	/**
	 * 检测押运员是否处于空闲状态
	 * 
	 * @param userId
	 *            加钞员ID
	 * @return
	 * @throws Exception
	 * @author zhangxuewei 2016 8 19
	 */
	public String checkEscort1State(String userId) throws Exception {
		String methodName = "checkEscort1State";
		WebParameter[] parameter = { new WebParameter<String>("arg0", userId) };
		SoapObject soap = WebService.getSoapObject(methodName, parameter);
		Log.i("checkEscortState", "userId:" + userId);
		String code = soap.getProperty("code").toString();
		return code;
	}
}
