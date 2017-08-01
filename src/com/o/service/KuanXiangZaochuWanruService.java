package com.o.service;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import com.entity.WebParameter;
import com.example.app.entity.KuanXiangChuRu;
import com.service.WebService;

public class KuanXiangZaochuWanruService {
	public List<KuanXiangChuRu> KuanXiangZaochu (String jigouid) throws Exception {
		KuanXiangChuRu kxcr = null;
		String methodName = "getBoxSendOutEarlyList";
		WebParameter[] param = { new WebParameter<String>("arg0", jigouid) };
		SoapObject soap = WebService.getSoapObject2(methodName, param);
		System.out.println("早出传入的机构id:" + jigouid);
		System.out.println("早出------------:" + soap);

		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		String[] split = params.split("\r\n");
		List<KuanXiangChuRu> ls = new ArrayList<KuanXiangChuRu>();
		if (msg.equals("成功")&&!params.equals("anyType{}")) {
			for (int i = 0; i < split.length; i++) {
				String[] arry = split[i].split(";");
				if(params.equals("anyType{}")){
					return null;
				}
				kxcr = new KuanXiangChuRu();
				kxcr.setXianlubianhao(arry[0]);
				kxcr.setChaochexianlu(arry[1]);
				kxcr.setWangdiancount(arry[2]);
				kxcr.setKuanxiangcount(arry[3]);
				kxcr.setPeisongdate(arry[4]);
				ls.add(kxcr);
			}
			return ls;
		}
		return null;
	}

	public List<KuanXiangChuRu> KuanXiangWanru(String jigouid) throws Exception {
		KuanXiangChuRu kxcr = null;
		String methodName = "getBoxStorageLateList";
		WebParameter[] param = { new WebParameter<String>("arg0", jigouid) };
		SoapObject soap = WebService.getSoapObject2(methodName, param);
		System.out.println("晚入传入的机构id:" + jigouid);
		System.out.println("晚入------------：" + soap);
		String code = soap.getProperty("code").toString();
		String msg = soap.getProperty("msg").toString();
		String params = soap.getProperty("params").toString();
		String[] split = params.split("\r\n");
		List<KuanXiangChuRu> ls = new ArrayList<KuanXiangChuRu>();
		if (msg.equals("成功")&&!params.equals("anyType{}")) {
			for (int i = 0; i < split.length; i++) {
				String[] arry = split[i].split(";");
				kxcr = new KuanXiangChuRu();
				kxcr.setXianlubianhao(arry[0]);
				kxcr.setChaochexianlu(arry[1]);
				kxcr.setWangdiancount(arry[2]);
				kxcr.setKuanxiangcount(arry[3]);
				ls.add(kxcr);
			}
			return ls;
		}
		//System.out.println("[晚入Service 数据拉取完成 保存list长度为]："+ls.size());
		return null;
	}
}
