package com.ljsw.tjbankpda.qf.entity;

public class JiHuaDan_Zhongkong {
	private String pingzheng;
	private String count;
	public String getPingzheng() {
		return pingzheng;
	}
	public void setPingzheng(String pingzheng) {
		this.pingzheng = pingzheng;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public JiHuaDan_Zhongkong(String pingzheng, String count) {
		super();
		this.pingzheng = pingzheng;
		this.count = count;
	}
	public JiHuaDan_Zhongkong() {
		super();
	}
	
}
