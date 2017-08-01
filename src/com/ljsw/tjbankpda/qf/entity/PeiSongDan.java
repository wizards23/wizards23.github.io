package com.ljsw.tjbankpda.qf.entity;

public class PeiSongDan {
	private String danhao;
	private String wangdian;
	public String getDanhao() {
		return danhao;
	}
	public void setDanhao(String danhao) {
		this.danhao = danhao;
	}
	public String getWangdian() {
		return wangdian;
	}
	public void setWangdian(String wangdian) {
		this.wangdian = wangdian;
	}
	public PeiSongDan(String danhao, String wangdian) {
		super();
		this.danhao = danhao;
		this.wangdian = wangdian;
	}
	public PeiSongDan() {
		super();
	}
	
}
