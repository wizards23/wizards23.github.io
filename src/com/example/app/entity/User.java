package com.example.app.entity;

import java.io.Serializable;

public class User implements Serializable{
	private String jigouid;
	private String userid;
	private String userzhanghu;
	private String username;
	private String jigouname;
	private String pwd;
	

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getUserzhanghu() {
		return userzhanghu;
	}

	public void setUserzhanghu(String userzhanghu) {
		this.userzhanghu = userzhanghu;
	}

	public String getJigouid() {
		return jigouid;
	}

	public void setJigouid(String jigouid) {
		this.jigouid = jigouid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getJigouname() {
		return jigouname;
	}

	public void setJigouname(String jigouname) {
		this.jigouname = jigouname;
	}


	public User(String jigouid, String userid, String userzhanghu,
			String username, String jigouname) {
		super();
		this.jigouid = jigouid;
		this.userid = userid;
		this.userzhanghu = userzhanghu;
		this.username = username;
		this.jigouname = jigouname;
	}

	public User() {
		super();
	}

}
