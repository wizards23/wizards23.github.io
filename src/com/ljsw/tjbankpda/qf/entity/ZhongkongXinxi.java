package com.ljsw.tjbankpda.qf.entity;

/**
 * 重空信息
 * @author yuyunheng
 */
public class ZhongkongXinxi {

	private String zhongkongId;   // 重空id
	private String zhongkongName;  // 重空名称
	
	public ZhongkongXinxi(String zhongkongId,String zhongkongName){
		this.zhongkongId=zhongkongId;
		this.zhongkongName=zhongkongName;
	}

	public String getZhongkongId() {
		return zhongkongId;
	}

	public void setZhongkongId(String zhongkongId) {
		this.zhongkongId = zhongkongId;
	}

	public String getZhongkongName() {
		return zhongkongName;
	}

	public void setZhongkongName(String zhongkongName) {
		this.zhongkongName = zhongkongName;
	}
	
	
}
