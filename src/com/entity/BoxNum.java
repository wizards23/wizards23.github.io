package com.entity;

public class BoxNum {
	private  String empty;  //空钞箱出库数量
	private  String atm;     //ATM加钞出库
	private  String notclear;  //未清回收钞箱出库
	private  String putin;    //钞箱装钞入库
	private  String back;      //回收钞箱入库
	private  String clear;		//已清回收钞箱入库
	private String state;    //状态
	

	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getEmpty() {
		return empty;
	}
	public void setEmpty(String empty) {
		this.empty = empty;
	}
	public String getAtm() {
		return atm;
	}
	public void setAtm(String atm) {
		this.atm = atm;
	}
	public String getNotclear() {
		return notclear;
	}
	public void setNotclear(String notclear) {
		this.notclear = notclear;
	}
	
	public String getPutin() {
		return putin;
	}
	public void setPutin(String putin) {
		this.putin = putin;
	}
	public String getBack() {
		return back;
	}
	public void setBack(String back) {
		this.back = back;
	}
	public String getClear() {
		return clear;
	}
	public void setClear(String clear) {
		this.clear = clear;
	}
	
	
	
	
}
