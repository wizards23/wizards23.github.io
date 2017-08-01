package com.ljsw.tjbankpda.qf.entity;

import java.io.Serializable;

/**
 * 押运员任务列表页面所需实体类
 * @author Administrator
 */
public class YaYunLb implements Serializable{
	private String name;
	private String qlrwState;//请领任务的状态 1.表示领了任务 0.表示没有任务
	private String sjrwState;//上缴任务的状态 1.表示上缴了任务 0.表示没有上缴任务
	private String JigouId;
	
	public String getJigouId() {
		return JigouId;
	}
	public void setJigouId(String jigouId) {
		JigouId = jigouId;
	}
	public YaYunLb(String name, String qlrwState, String sjrwState,
			String jigouId) {
		super();
		this.name = name;
		this.qlrwState = qlrwState;
		this.sjrwState = sjrwState;
		JigouId = jigouId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getQlrwState() {
		return qlrwState;
	}
	public void setQlrwState(String qlrwState) {
		this.qlrwState = qlrwState;
	}
	public String getSjrwState() {
		return sjrwState;
	}
	public void setSjrwState(String sjrwState) {
		this.sjrwState = sjrwState;
	}
	public YaYunLb() {
		super();
		// TODO Auto-generated constructor stub
	}
	public YaYunLb(String name, String qlrwState, String sjrwState) {
		super();
		this.name = name;
		this.qlrwState = qlrwState;
		this.sjrwState = sjrwState;
	}	
}
