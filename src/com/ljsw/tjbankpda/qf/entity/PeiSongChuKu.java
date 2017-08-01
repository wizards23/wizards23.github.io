package com.ljsw.tjbankpda.qf.entity;

import java.io.Serializable;

public class PeiSongChuKu implements Serializable{
	private String bianhao;
	private String xianlu;
	private String count;
	public String getBianhao() {
		return bianhao;
	}
	public void setBianhao(String bianhao) {
		this.bianhao = bianhao;
	}
	public String getXianlu() {
		return xianlu;
	}
	public void setXianlu(String xianlu) {
		this.xianlu = xianlu;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public PeiSongChuKu(String bianhao, String xianlu, String count) {
		super();
		this.bianhao = bianhao;
		this.xianlu = xianlu;
		this.count = count;
	}
	public PeiSongChuKu() {
		super();
	}
	
}
