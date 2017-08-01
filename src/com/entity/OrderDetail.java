package com.entity;

public class OrderDetail extends Box {
	private String carBrand;   //车牌
	private String ATM;    
	private String addMoneyJoin;  //加钞交接的钞箱数量
	private String backNum;  //回收交接的钞箱数量
	private String taskCorpId;//任务所属机构 revised by zhangXueWei
	
	
	public String getTaskCorpId() {
		return taskCorpId;
	}
	public void setTaskCorpId(String taskCorpId) {
		this.taskCorpId = taskCorpId;
	}
	
	public String getAddMoneyJoin() {
		return addMoneyJoin;
	}
	public void setAddMoneyJoin(String addMoneyJoin) {
		this.addMoneyJoin = addMoneyJoin;
	}
	public String getBackNum() {
		return backNum;
	}
	public void setBackNum(String backNum) {
		this.backNum = backNum;
	}
	public String getCarBrand() {
		return carBrand;
	}
	public void setCarBrand(String carBrand) {
		this.carBrand = carBrand;
	}
	public String getATM() {
		return ATM;
	}
	public void setATM(String aTM) {
		ATM = aTM;
	}
	
	
	
}
