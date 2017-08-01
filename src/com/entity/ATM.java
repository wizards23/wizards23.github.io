package com.entity;

public class ATM { 
	private String AtmNum;   //ATM编号
	private String boxNum;     //钞箱编号
	private String address;    //地址
	public String getAtmNum() {
		return AtmNum;
	}
	public void setAtmNum(String atmNum) {
		AtmNum = atmNum;
	}
	public String getBoxNum() {
		return boxNum;
	}
	public void setBoxNum(String boxNum) {
		this.boxNum = boxNum;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
