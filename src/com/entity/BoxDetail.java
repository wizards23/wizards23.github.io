package com.entity;

public class BoxDetail {
	private String brand;   //钞箱品牌   或者 业务单编号
	private String Num;     //钞箱编号
	private String money;   //加钞金额   或者  核心余额
	
	
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getNum() {
		return Num;
	}
	public void setNum(String num) {
		Num = num;
	}
	
	public BoxDetail(){};
	
	public BoxDetail(String brand,String Num){
		this.brand = brand;
		this.Num = Num;
	}
	
	
	
}
