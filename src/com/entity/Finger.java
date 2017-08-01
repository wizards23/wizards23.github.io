package com.entity;

public class Finger {

	
	private String corpId;  //机构ID
	private String roleId;  //角色ID 
	private byte[] cValue;  //特征值
	
	
	public String getCorpId() {
		return corpId;
	}
	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public byte[] getcValue() {
		return cValue;
	}
	public void setcValue(byte[] cValue) {
		this.cValue = cValue;
	}
	
	
	
	
}
