package com.ljsw.tjbankpda.qf.entity;

import java.io.Serializable;

/**
 * 清分任务实体类
 * @author Administrator
 *
 */
public class Qingfendan implements Serializable{
	private String qfNum;//计划单编号
	private String qfDate;//计划单日期
	private String qfXianlu;
	private String qfXiaozu;//清分小组名称
	private String state;//状态 :1.选中 ;0:没有选中
	private String qfzzxbh;//周转箱编号
	
	
	public Qingfendan(String qfNum) {
		super();
		this.qfNum = qfNum;
	}
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getQfXianlu() {
		return qfXianlu;
	}
	public void setQfXianlu(String qfXianlu) {
		this.qfXianlu = qfXianlu;
	}
	public String getQfXiaozu() {
		return qfXiaozu;
	}
	public void setQfXiaozu(String qfXiaozu) {
		this.qfXiaozu = qfXiaozu;
	}
	public String getQfNum() {
		return qfNum;
	}
	public void setQfNum(String qfNum) {
		this.qfNum = qfNum;
	}
	public String getQfDate() {
		return qfDate;
	}
	public void setQfDate(String qfDate) {
		this.qfDate = qfDate;
	}
	public Qingfendan(String qfNum, String qfDate) {
		super();
		this.qfNum = qfNum;
		this.qfDate = qfDate;
	}
	
	
	public String getQfzzxbh() {
		return qfzzxbh;
	}

	public void setQfzzxbh(String qfzzxbh) {
		this.qfzzxbh = qfzzxbh;
	}

	public Qingfendan() {
		super();
		// TODO Auto-generated constructor stub
	}	
}
