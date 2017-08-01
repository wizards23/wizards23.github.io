package com.ljsw.tjbankpda.db.entity;

import java.util.List;

public class QingLingChuRuKu {
	private String xianluming;
	private String xianluid;
	private List<String> kuxiang;
	private String type;
	private String caozuotype;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCaozuotype() {
		return caozuotype;
	}
	public void setCaozuotype(String caozuotype) {
		this.caozuotype = caozuotype;
	}
	public QingLingChuRuKu(String xianluming, String xianluid,
			List<String> kuxiang, String type, String caozuotype) {
		super();
		this.xianluming = xianluming;
		this.xianluid = xianluid;
		this.kuxiang = kuxiang;
		this.type = type;
		this.caozuotype = caozuotype;
	}
	
}
