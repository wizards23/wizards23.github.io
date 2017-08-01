package com.ljsw.tjbankpda.db.entity;

import java.io.Serializable;
import java.util.List;

public class ShangJiaoQingFenChuKu implements Serializable{
	private static final long serialVersionUID = 1L;
	private String jihuadan;
	private List<String> xianluming;
	private List<String> count;//各线路分别的周转箱数量
	private List<String> zhouzhuanxiang;
	private int zzxcount;
	public String getJihuadan() {
		return jihuadan;
	}
	public void setJihuadan(String jihuadan) {
		this.jihuadan = jihuadan;
	}
	public List<String> getXianluming() {
		return xianluming;
	}
	public void setXianluming(List<String> xianluming) {
		this.xianluming = xianluming;
	}
	public List<String> getCount() {
		return count;
	}
	public void setCount(List<String> count) {
		this.count = count;
	}
	public List<String> getZhouzhuanxiang() {
		return zhouzhuanxiang;
	}
	public void setZhouzhuanxiang(List<String> zhouzhuanxiang) {
		this.zhouzhuanxiang = zhouzhuanxiang;
	}
	public int getZzxcount() {
		return zzxcount;
	}
	public void setZzxcount(int zzxcount) {
		this.zzxcount = zzxcount;
	}
	public ShangJiaoQingFenChuKu(String jihuadan, List<String> xianluming,
			List<String> count, List<String> zhouzhuanxiang, int zzxcount) {
		super();
		this.jihuadan = jihuadan;
		this.xianluming = xianluming;
		this.count = count;
		this.zhouzhuanxiang = zhouzhuanxiang;
		this.zzxcount = zzxcount;
	}
	
}
