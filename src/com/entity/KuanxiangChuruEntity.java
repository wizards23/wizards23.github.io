package com.entity;

import java.io.Serializable;

/**、
 * 款箱出入线路信息实体类
 * @author yuyunheng
 *
 */
public class KuanxiangChuruEntity implements Serializable{
	
	private String lineNum;//路线编号	
	private String moneyCarLine;//钞车线路
	private String netNum;//网点数量
	private String boxNum;//款箱数量
	private String date;//配送日期
	private String netId;//网点编号
	private String netName;//网点名称
	private String boxId;//款箱编号
	
	
	
	public KuanxiangChuruEntity(String moneyCarLine, String netNum,
			String boxNum) {
		super();
		this.moneyCarLine = moneyCarLine;
		this.netNum = netNum;
		this.boxNum = boxNum;
	}
	public KuanxiangChuruEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
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
	public String getBoxId() {
		return boxId;
	}
	public void setBoxId(String boxId) {
		this.boxId = boxId;
	}
	public String getLineNum() {
		return lineNum;
	}
	public void setLineNum(String lineNum) {
		this.lineNum = lineNum;
	}
	public String getMoneyCarLine() {
		return moneyCarLine;
	}
	public void setMoneyCarLine(String moneyCarLine) {
		this.moneyCarLine = moneyCarLine;
	}
	public String getNetNum() {
		return netNum;
	}
	public void setNetNum(String netNum) {
		this.netNum = netNum;
	}
	public String getBoxNum() {
		return boxNum;
	}
	public void setBoxNum(String boxNum) {
		this.boxNum = boxNum;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
			
}
