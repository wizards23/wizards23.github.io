package com.ljsw.tjbankpda.qf.entity;

import java.io.Serializable;

public class RenWuMingXi implements Serializable{
	private String danhao;//任务单号
	private String shijian;
	public String getDanhao() {
		return danhao;
	}
	public void setDanhao(String danhao) {
		this.danhao = danhao;
	}
	public String getShijian() {
		return shijian;
	}
	public void setShijian(String shijian) {
		this.shijian = shijian;
	}
	public RenWuMingXi(String danhao, String shijian) {
		super();
		this.danhao = danhao;
		this.shijian = shijian;
	}
	public RenWuMingXi() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
