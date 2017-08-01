package com.example.app.entity;

import java.util.Date;

public class KuanXiang {
	private String kuanxiangBianhao;//款箱编号
	private String xianlu;//线路
	private String wangdianCount;//网点数量
	private Date date;//时间
	private String kuanxiangMingcheng;//款箱名称
	private String kuanxiangCount;//款箱数量
	public String getKuanxiangMingcheng() {
		return kuanxiangMingcheng;
	}
	public void setKuanxiangMingcheng(String kuanxiangMingcheng) {
		this.kuanxiangMingcheng = kuanxiangMingcheng;
	}
	public String getKuanxiangCount() {
		return kuanxiangCount;
	}
	public void setKuanxiangCount(String kuanxiangCount) {
		this.kuanxiangCount = kuanxiangCount;
	}
	public String getKuanxiangBianhao() {
		return kuanxiangBianhao;
	}
	public void setKuanxiangBianhao(String kuanxiangBianhao) {
		this.kuanxiangBianhao = kuanxiangBianhao;
	}
	public String getXianlu() {
		return xianlu;
	}
	public void setXianlu(String xianlu) {
		this.xianlu = xianlu;
	}
	public String getWangdianCount() {
		return wangdianCount;
	}
	public void setWangdianCount(String wangdianCount) {
		this.wangdianCount = wangdianCount;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public KuanXiang(String kuanxiangBianhao, String xianlu, String wangdianCount,
			Date date, String kuanxiangMingcheng, String kuanxiangCount) {
		super();
		this.kuanxiangBianhao = kuanxiangBianhao;
		this.xianlu = xianlu;
		this.wangdianCount = wangdianCount;
		this.date = date;
		this.kuanxiangMingcheng = kuanxiangMingcheng;
		this.kuanxiangCount = kuanxiangCount;
	}
	public KuanXiang() {
		super();
	}
	
	
}
