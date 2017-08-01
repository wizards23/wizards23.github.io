package com.ljsw.tjbankpda.qf.entity;

import java.io.Serializable;

public class RenWu implements Serializable{
	private String renwu;
	private String count;
	public String getRenwu() {
		return renwu;
	}
	public void setRenwu(String renwu) {
		this.renwu = renwu;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public RenWu(String renwu, String count) {
		super();
		this.renwu = renwu;
		this.count = count;
	}
	public RenWu() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
