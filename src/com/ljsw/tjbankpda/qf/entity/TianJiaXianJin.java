package com.ljsw.tjbankpda.qf.entity;

import java.io.Serializable;

public class TianJiaXianJin implements Serializable {
	private String juanbie; // 券别id
	private String zhuangtai; // 状态
	private String count; // 数量
	private String quanJiazhi; // 全额价值
	private String canshunJiazhi; // 残损价值


	public String getQuanJiazhi() {
		return quanJiazhi;
	}

	public void setQuanJiazhi(String quanJiazhi) {
		this.quanJiazhi = quanJiazhi;
	}

	public String getCanshunJiazhi() {
		return canshunJiazhi;
	}

	public void setCanshunJiazhi(String canshunJiazhi) {
		this.canshunJiazhi = canshunJiazhi;
	}

	public String getJuanbie() {
		return juanbie;
	}

	public void setJuanbie(String juanbie) {
		this.juanbie = juanbie;
	}

	public String getZhuangtai() {
		return zhuangtai;
	}

	public void setZhuangtai(String zhuangtai) {
		this.zhuangtai = zhuangtai;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public TianJiaXianJin(String juanbie, String quanjiazhi,
			String canshunJiazhi, String zhuangtai, String count) {
		super();
		this.juanbie = juanbie;
		this.zhuangtai = zhuangtai;
		this.count = count;
		this.quanJiazhi=quanjiazhi;
		this.canshunJiazhi=canshunJiazhi;
	}

	public TianJiaXianJin() {
		super();
	}

}
