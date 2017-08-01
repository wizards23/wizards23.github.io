package com.ljsw.tjbankpda.qf.entity;

import java.io.Serializable;

/**
 * 周转和上缴用到的款箱编号的类
 * @author Administrator
 *
 */
public class YayKuanXiang implements Serializable{
	private String boxNum;//款箱编号

	public String getBoxNum() {
		return boxNum;
	}

	public void setBoxNum(String boxNum) {
		this.boxNum = boxNum;
	}

	public YayKuanXiang() {
		super();
		// TODO Auto-generated constructor stub
	}

	public YayKuanXiang(String boxNum) {
		super();
		this.boxNum = boxNum;
	}
	
	
}
