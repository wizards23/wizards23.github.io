package com.ljsw.tjbankpda.qf.entity;

public class Box {
	private String type;
	private String count;
	private String mark;
	private String quanbieId;//券别Id
	
	
	public Box() {
		super();
	}
	public Box(String type, String count) {
		super();
		this.type = type;
		this.count = count;
	}
	public Box(String type, String count, String mark) {
		super();
		this.type = type;
		this.count = count;
		this.mark = mark;
	}
	
	
	
	public Box(String type, String count, String quanbieId ,String mark) {
		super();
		this.type = type;
		this.count = count;
		this.quanbieId = quanbieId;
		this.mark = mark;
		
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public String getQuanbieId() {
		return quanbieId;
	}
	public void setQuanbieId(String quanbieId) {
		this.quanbieId = quanbieId;
	}
	
	

}
