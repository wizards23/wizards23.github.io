package com.ljsw.tjbankpda.qf.entity;

import java.io.Serializable;
import java.util.List;

public class QingLingRuKu implements Serializable{
	private String danhao;
	private String riqi;
	private List<String> zhouzhuanxiang;
	public String getDanhao() {
		return danhao;
	}
	public void setDanhao(String danhao) {
		this.danhao = danhao;
	}
	public String getRiqi() {
		return riqi;
	}
	public void setRiqi(String riqi) {
		this.riqi = riqi;
	}
	public List<String> getZhouzhuanxiang() {
		return zhouzhuanxiang;
	}
	public void setZhouzhuanxiang(List<String> zhouzhuanxiang) {
		this.zhouzhuanxiang = zhouzhuanxiang;
	}
	public QingLingRuKu(String danhao, String riqi, List<String> zhouzhuanxiang) {
		super();
		this.danhao = danhao;
		this.riqi = riqi;
		this.zhouzhuanxiang = zhouzhuanxiang;
	}
	
}
