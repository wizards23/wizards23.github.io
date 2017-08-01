package com.ljsw.tjbankpda.qf.entity;

import java.io.Serializable;

public class JiHuaDan_Xianjin implements Serializable{
	private String quanbieId;//卷别ID
	private String juanbie;  //券别名称
	private String count;   //券别数量
	private String quanJiazhi;  //券别价值(全额)
	public String getJuanbie() {
		return juanbie;
	}
	public void setJuanbie(String juanbie) {
		this.juanbie = juanbie;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public JiHuaDan_Xianjin(String juanbie, String count) {
		super();
		this.juanbie = juanbie;
		this.count = count;
	}
	public JiHuaDan_Xianjin() {
		super();
	}
	public String getQuanbieId() {
		return quanbieId;
	}
	public void setQuanbieId(String quanbieId) {
		this.quanbieId = quanbieId;
	}
	
	public String getQuanJiazhi() {
		return quanJiazhi;
	}
	public void setQuanJiazhi(String quanJiazhi) {
		this.quanJiazhi = quanJiazhi;
	}
	public JiHuaDan_Xianjin(String juanbie, String count, String quanbieId,String quanbieJiazhi) {
		super();
		this.quanbieId = quanbieId;
		this.juanbie = juanbie;
		this.count = count;
		this.quanJiazhi=quanbieJiazhi;
	}
	
	
}
