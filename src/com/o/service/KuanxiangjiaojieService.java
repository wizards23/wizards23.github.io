package com.o.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import com.entity.WebParameter;
import com.example.app.entity.KuanXiangBox;
import com.example.app.entity.KuanXiangChuRu;
import com.example.app.entity.S_box;
import com.service.WebService;

/**
 * 款箱交接服务类
 * @author lenovo
 */
public class KuanxiangjiaojieService {

	public KuanXiangChuRu getLineByUserId(String userId) throws Exception {
		KuanXiangChuRu kxcr = null;
		String methodName = "getLineByUserId";
		WebParameter[] param = { new WebParameter<String>("arg0", userId) };

		SoapObject soap = null;
		soap = WebService.getSoapObject2(methodName, param);
		System.out.println("押运员编号：" + userId);
		System.out.println("ccccccccccc取得的线路编号信息" + soap);
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		List<KuanXiangChuRu> ls = new ArrayList<KuanXiangChuRu>();
		if (code.equals("00")) {
			if (params.equals("anyType{}")) {
				return null;
			}
			kxcr = new KuanXiangChuRu();
			String[] str = params.split(";");
			kxcr = new KuanXiangChuRu();
			kxcr.setCode(code);
			kxcr.setXianlubianhao(str[0]);
			kxcr.setChaochexianlu(str[1]);
			return kxcr;
		}
		return null;
	}

	public List<KuanXiangBox> getBoxHandoverList(String lineNum)
			throws Exception {
		KuanXiangBox kxcr = null;
		String methodName = "getBoxHandoverList";
		WebParameter[] param = { new WebParameter<String>("arg0", lineNum) };

		SoapObject soap = null;
		soap = WebService.getSoapObject2(methodName, param);
		System.out.println("------getBoxHandoverList:" + soap);
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		System.out.println("paramss" + params);
		List<KuanXiangBox> list = new ArrayList<KuanXiangBox>();
		if (code.equals("00")) {
			String[] split = params.split("\r\n");
			for (int i = 0; i < split.length; i++) {
				String[] str = split[i].split(";");
				if (params.equals("anyType{}")) {
					return null;
				}

				// params=HX01;河西一线;3;7;2015-05-12
				kxcr = new KuanXiangBox();
				kxcr.setNetId(str[0]);

				kxcr.setNetName(str[1]);
				kxcr.setBoxNum(str[2]);
				kxcr.setType(str[3]);

				list.add(kxcr);
			}
			return list;
		}
		return null;
	}

	/**
	 * 获取早送申请列表
	 * 
	 * @param corpId
	 * @return
	 * @throws Exception 
	 */
	public List<S_box> getBoxApplicationList(String corpId) throws Exception {
		S_box box = null;
		String methodName = "getBoxApplicationList";
		WebParameter[] param = { new WebParameter<String>("arg0", corpId) };
		System.out.println("-------getBoxApplicationList传入corpId" + corpId);
		SoapObject soap = null;
			soap = WebService.getSoapObject2(methodName, param);
		System.out.println("-------getBoxApplicationList:" + soap);
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		List<S_box> list = new ArrayList<S_box>();
		if (code.equals("00")) {
			String[] split = params.split("\r\n");
			for (int i = 0; i < split.length; i++) {
				String[] str = split[i].split(";");
				// params=HX01;河西一线;3;7;2015-05-12
				box = new S_box();
				box.setBoxId(str[0]);
				box.setState(str[1]);
				list.add(box);
			}
			return list;
		}

		return null;
	}

	/**
	 * 获取晚收交接明细
	 * @throws Exception
	 */
	public List<String> getBoxHandoverDetail(String corpId, String type)
			throws Exception {
		S_box box = null;
		String methodName = "getBoxHandoverDetail";
		WebParameter[] param = { new WebParameter<String>("arg0", corpId),
				new WebParameter<String>("arg1", type) };
		System.out.println("-----------getBoxHandoverDetail传入的参数-机构id:"
				+ corpId + ",state：" + type);
		SoapObject soap = null;
		soap = WebService.getSoapObject2(methodName, param);
		System.out.println("-------getBoxHandoverDetail:" + soap);
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		List<String> list = new ArrayList<String>();
		if (code.equals("00")) {
			String[] split = params.split("\\|");
			for (int i = 0; i < split.length; i++) {
				list.add(split[i]);
			}
			System.out.println("[service------------getBoxHandoverDetail]:"
					+ list.size());
			return list;
		}
		return null;
	}

	/**
	 * 款项早送交接
	 * 
	 * @throws Exception
	 */
	public String saveBoxHandover(String lineNum, String boxes,
			String corpIdOfBox, String corpId, String roleId, byte[] cValue,
			String userIds, String userId, String pwd) throws Exception {
		String methodName = "saveBoxHandover";
		WebParameter[] param = { new WebParameter<String>("arg0", lineNum),
				new WebParameter<String>("arg1", boxes),
				new WebParameter<String>("arg2", corpIdOfBox),
				new WebParameter<String>("arg3", corpId),
				new WebParameter<String>("arg4", roleId),
				new WebParameter<byte[]>("arg5", cValue),
				new WebParameter<String>("arg6", userIds),
				new WebParameter<String>("arg7", userId),
				new WebParameter<String>("arg8", pwd) };
		System.out.println("-----------getBoxHandoverDetail传入的参数-机构id:"
				+ "lineNum:" + lineNum + ",boxes：" + boxes + ",corpIdOfBox："
				+ corpIdOfBox + ",corpId：" + corpId + ",roleId：" + roleId
				+ ",cValue：" + cValue + ",userIds：" + userIds + ",userId："
				+ userId + ",pwd：" + pwd);
		SoapObject soap = null;
		soap = WebService.getSoapObject2(methodName, param);
		System.out.println("-------saveBoxHandover:" + soap);
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		if (code.equals("00")) {
			return msg;
		}

		return null;
	}

	/**
	 * 款箱晚收交接
	 * 
	 * @param lineNum
	 * @param boxes
	 * @param toboxes
	 * @param date
	 * @param corpIdOfBox
	 * @param corpId
	 * @param roleId
	 * @param cValue
	 * @param userIds
	 * @return
	 * @throws Exception
	 */
	public String saveBoxHandoverStorageLate(String lineNum, String boxes,
			String toboxes, String date, String corpIdOfBox, String corpId,
			String roleId, byte[] cValue, String userIds, String userId,
			String pwd) throws Exception {
		String methodName = "saveBoxHandoverStorageLate";
		WebParameter[] param = { new WebParameter<String>("arg0", lineNum),
				new WebParameter<String>("arg1", boxes),
				new WebParameter<String>("arg2", toboxes),
				new WebParameter<String>("arg3", date),
				new WebParameter<String>("arg4", corpIdOfBox),
				new WebParameter<String>("arg5", corpId),
				new WebParameter<String>("arg6", roleId),
				new WebParameter<byte[]>("arg7", cValue),
				new WebParameter<String>("arg8", userIds),
				new WebParameter<String>("arg9", userId),
				new WebParameter<String>("arg10", pwd) };
		System.out.println("提交参数：" + lineNum);
		System.out.println("boxes:"+boxes);
		System.out.println("toboxes:"+toboxes);
		System.out.println("date:"+date);
		System.out.println("corpIdOfBox:"+corpIdOfBox);
		System.out.println("corpId:"+corpId);
		System.out.println("roleId:"+roleId);
		System.out.println("cValue:"+cValue);
		System.out.println("userIds:"+userIds);
		System.out.println("userId:"+userId);
		System.out.println("pwd:"+pwd);
		
		SoapObject soap = null;
		soap = WebService.getSoapObject2(methodName, param);
		System.out.println("-------saveBoxHandoverStorageLate:" + soap);
		String code = soap.getProperty("code").toString();
		return code;
	}

	// 是否需要早送 交接ListView的item点击调用
	public String getApplicationByCorpId(String corpId) throws Exception {
		String methodName = "getApplicationByCorpId";
		WebParameter[] param = { new WebParameter<String>("arg0", corpId) };
		SoapObject soap = null;
		soap = WebService.getSoapObject2(methodName, param);
		System.out.println("-------getApplicationByCorpId:corpId:" + corpId);
		System.out.println("-------getApplicationByCorpId-soap:" + soap);
		String code = soap.getProperty("code").toString();
		if (code.equals("99")) {
			String params = soap.getProperty("params").toString();
			System.out.println("-------getApplicationByCorpId-params:" + params);
			return params;
		}
		return null;
	}

}
