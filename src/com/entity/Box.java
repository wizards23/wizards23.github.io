package com.entity;

public class Box {
	private String planNum;   //加钞计划编号
	private String way;		//路线
	private String boxNum;   //箱子数量
	private String date;    //日期
	private String type;    //计划类型
	private String money;  //金额
	private String state;   //状态
	private String isFirst; //回收钞箱是否为首次入库
	
	
	
	
	public String getIsFirst() {
		return isFirst;
	}
	public void setIsFirst(String isFirst) {
		this.isFirst = isFirst;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPlanNum() {
		return planNum;
	}
	public void setPlanNum(String planNum) {
		this.planNum = planNum;
	}
	public String getWay() {
		return way;
	}
	public void setWay(String way) {
		this.way = way;
	}
	public String getBoxNum() {
		return boxNum;
	}
	public void setBoxNum(String boxNum) {
		this.boxNum = boxNum;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	
	
	
	
}
