package com.ljsw.tjbankpda.qf.entity;

import java.util.List;

public class OrderInfo {
	private String name;//网点名称
	private List<String> ltOrder;//订单号
	private String jigouid;//机构id
	
	public OrderInfo(String name, List<String> ltOrder, String jigouid) {
		super();
		this.name = name;
		this.ltOrder = ltOrder;
		this.jigouid = jigouid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getLtOrder() {
		return ltOrder;
	}
	public void setLtOrder(List<String> ltOrder) {
		this.ltOrder = ltOrder;
	}
	public String getJigouid() {
		return jigouid;
	}
	public void setJigouid(String jigouid) {
		this.jigouid = jigouid;
	}
	
	

}
