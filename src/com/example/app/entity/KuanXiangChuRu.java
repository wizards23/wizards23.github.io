package com.example.app.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class KuanXiangChuRu implements Serializable {
	// 路线编号;钞车线路;网点数量;款箱数量;配送日期
	private String xianlubianhao;
	private String chaochexianlu;
	private String wangdiancount;
	private String kuanxiangcount;
	private String peisongdate;
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getKuanxiangcount() {
		return kuanxiangcount;
	}

	public void setKuanxiangcount(String kuanxiangcount) {
		this.kuanxiangcount = kuanxiangcount;
	}

	public String getXianlubianhao() {
		return xianlubianhao;
	}

	public void setXianlubianhao(String xianlubianhao) {
		this.xianlubianhao = xianlubianhao;
	}

	public String getChaochexianlu() {
		return chaochexianlu;
	}

	public void setChaochexianlu(String chaochexianlu) {
		this.chaochexianlu = chaochexianlu;
	}

	public String getWangdiancount() {
		return wangdiancount;
	}

	public void setWangdiancount(String wangdiancount) {
		this.wangdiancount = wangdiancount;
	}

	public String getPeisongdate() {
		return peisongdate;
	}

	public void setPeisongdate(String peisongdate) {
		this.peisongdate = peisongdate;
	}

	public KuanXiangChuRu(String xianlubianhao, String chaochexianlu,
			String wangdiancount, String kuanxiangcount, String peisongdate) {
		super();
		this.xianlubianhao = xianlubianhao;
		this.chaochexianlu = chaochexianlu;
		this.wangdiancount = wangdiancount;
		this.kuanxiangcount = kuanxiangcount;
		this.peisongdate = peisongdate;
	}

	public KuanXiangChuRu() {
		super();
		// TODO Auto-generated constructor stub
	}

}
