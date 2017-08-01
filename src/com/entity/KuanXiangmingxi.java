package com.entity;

import java.io.Serializable;
import java.util.List;

public class KuanXiangmingxi implements Serializable{
	private String netId;
	private String netName;
	private String boxIds;
	public String getNetId() {
		return netId;
	}
	public void setNetId(String netId) {
		this.netId = netId;
	}
	public String getNetName() {
		return netName;
	}
	public void setNetName(String netName) {
		this.netName = netName;
	}
	public String getBoxIds() {
		return boxIds;
	}
	public void setBoxIds(String boxIds) {
		this.boxIds = boxIds;
	}
	public KuanXiangmingxi(String netId, String netName, String boxIds) {
		super();
		this.netId = netId;
		this.netName = netName;
		this.boxIds = boxIds;
	}
	public KuanXiangmingxi() {
	}
	
}
