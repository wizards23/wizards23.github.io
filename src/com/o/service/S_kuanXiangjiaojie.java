package com.o.service;

import java.io.Serializable;

/**
 * 款箱交接模块
 * @author Administrator
 *
 */
public class S_kuanXiangjiaojie implements Serializable {
	private String  wangdianId;//网点ID
	private String wangdianName;//网点名称
	private String kuanxiangNum;//款箱数量
	private String type;//交接类型
	
	public String getWangdianId() {
		return wangdianId;
	}
	public void setWangdianId(String wangdianId) {
		this.wangdianId = wangdianId;
	}
	public String getWangdianName() {
		return wangdianName;
	}
	public void setWangdianName(String wangdianName) {
		this.wangdianName = wangdianName;
	}
	public String getKuanxiangNum() {
		return kuanxiangNum;
	}
	public void setKuanxiangNum(String kuanxiangNum) {
		this.kuanxiangNum = kuanxiangNum;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public S_kuanXiangjiaojie(String wangdianName, String kuanxiangNum) {
		super();
		this.wangdianName = wangdianName;
		this.kuanxiangNum = kuanxiangNum;
	}
	public S_kuanXiangjiaojie() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
