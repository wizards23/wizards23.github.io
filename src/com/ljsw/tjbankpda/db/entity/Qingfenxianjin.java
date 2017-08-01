package com.ljsw.tjbankpda.db.entity;

/*
 * 清分现金实体类
 */
public class Qingfenxianjin {

	private String quanbie;  // 券别名
	private String shuliang; //数量
	private String quanbieId; // 券别id
	private String quanJiazhi; //全部价值
	
	public Qingfenxianjin(String quanbie,String shuliang,String quanbieId,String quanJiazhi){
		this.quanbie=quanbie;
		this.shuliang=shuliang;
		this.quanbieId=quanbieId;
		this.quanJiazhi=quanJiazhi;
	}
	
	
	public String getQuanbie() {
		return quanbie;
	}
	public void setQuanbie(String quanbie) {
		this.quanbie = quanbie;
	}
	public String getShuliang() {
		return shuliang;
	}
	public void setShuliang(String shuliang) {
		this.shuliang = shuliang;
	}
	public String getQuanbieId() {
		return quanbieId;
	}
	public void setQuanbieId(String quanbieId) {
		this.quanbieId = quanbieId;
	}
	public String getQuanJiazhi() {
		return quanJiazhi;
	}
	public void setQuanJiazhi(String quanJiazhi) {
		this.quanJiazhi = quanJiazhi;
	}
	
	
}
