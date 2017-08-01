package com.ljsw.tjbankpda.db.entity;

import java.io.Serializable;
import java.util.List;

public class ShangJiaoRuKu implements Serializable{
	private static final long serialVersionUID = 1L;
	private String xianluming;
	private String xianluid;
	private List<String> kuxiang;
	private String xianluType;
	public String getXianluType() {
		return xianluType;
	}
	public void setXianluType(String xianluType) {
		this.xianluType = xianluType;
	}
	public String getXianluming() {
		return xianluming;
	}
	public void setXianluming(String xianluming) {
		this.xianluming = xianluming;
	}
	public String getXianluid() {
		return xianluid;
	}
	public void setXianluid(String xianluid) {
		this.xianluid = xianluid;
	}
	public List<String> getKuxiang() {
		return kuxiang;
	}
	public void setKuxiang(List<String> kuxiang) {
		this.kuxiang = kuxiang;
	}
	



	public ShangJiaoRuKu(String xianluming, String xianluid,
			List<String> kuxiang,String xianluType) {
		super();
		this.xianluType=xianluType;
		this.xianluming = xianluming;
		this.xianluid = xianluid;
		this.kuxiang = kuxiang;
	}
	
}
