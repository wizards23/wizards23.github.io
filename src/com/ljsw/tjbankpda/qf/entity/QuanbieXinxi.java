package com.ljsw.tjbankpda.qf.entity;

/**
 * 券别信息实体类
 * @author yuyunheng
 *
 */
public class QuanbieXinxi {

	private String quanbieId;     // 券别id
	private String quanbieName;   // 券别名称
	private String quanJiazhi;    // 全额价值
	private String canshunJiazhi; // 残损价值
	public String getQuanbieId() {
		return quanbieId;
	}
	public void setQuanbieId(String quanbieId) {
		this.quanbieId = quanbieId;
	}
	public String getQuanbieName() {
		return quanbieName;
	}
	public void setQuanbieName(String quanbieName) {
		this.quanbieName = quanbieName;
	}
	public String getQuanJiazhi() {
		return quanJiazhi;
	}
	public void setQuanJiazhi(String quanJiazhi) {
		this.quanJiazhi = quanJiazhi;
	}
	public String getCanshunJiazhi() {
		return canshunJiazhi;
	}
	public void setCanshunJiazhi(String canshunJiazhi) {
		this.canshunJiazhi = canshunJiazhi;
	}
	
	
}
