package com.ljsw.tjbankpda.qf.entity;

/**
 * 装箱统计实体类
 * @author FUHAIQING
 *
 */
public class ZhuanxiangTongji {
	private String name;//名字或者类型
	private int yiZhuang;//已装数量
	private int weiZhuang;//未装数量
	public ZhuanxiangTongji(String name, int yiZhuang, int weiZhuang) {
		super();
		this.name = name;
		this.yiZhuang = yiZhuang;
		this.weiZhuang = weiZhuang;
	}
	public ZhuanxiangTongji() {
		super();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getYiZhuang() {
		return yiZhuang;
	}
	public void setYiZhuang(int yiZhuang) {
		this.yiZhuang = yiZhuang;
	}
	public int getWeiZhuang() {
		return weiZhuang;
	}
	public void setWeiZhuang(int weiZhuang) {
		this.weiZhuang = weiZhuang;
	}
	
}
