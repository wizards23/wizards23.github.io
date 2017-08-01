package com.ljsw.tjbankpda.db.entity;

import java.io.Serializable;

public class QingLingZhuangXiangChuKu implements Serializable{
	private static final long serialVersionUID = 1L;
	private String jihuadan;
	private String riqi;
	public String getJihuadan() {
		return jihuadan;
	}
	public void setJihuadan(String jihuadan) {
		this.jihuadan = jihuadan;
	}
	public String getRiqi() {
		return riqi;
	}
	public void setRiqi(String riqi) {
		this.riqi = riqi;
	}
	public QingLingZhuangXiangChuKu(String jihuadan, String riqi) {
		super();
		this.jihuadan = jihuadan;
		this.riqi = riqi;
	}
	
}
