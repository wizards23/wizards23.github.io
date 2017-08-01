package com.entity;

public class ClearMoney {
	private String atmNum;  //ATM编号
	private String boxNum;  //钞箱编号
	private String state;   //状态，1完成，0未完成
	
	

	public String getAtmNum() {
		return atmNum;
	}
	public void setAtmNum(String atmNum) {
		this.atmNum = atmNum;
	}
	public String getBoxNum() {
		return boxNum;
	}
	public void setBoxNum(String boxNum) {
		this.boxNum = boxNum;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	
	
}
