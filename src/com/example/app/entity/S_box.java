package com.example.app.entity;

import java.io.Serializable;

/**
 * 早送申请列表对应类
 * @author Administrator
 *
 */
public class S_box implements Serializable{
	private String boxId;//款箱编号
	private String state;//箱子状态
	private boolean isZaoSong;//是否早送
	private String edit;
	
	
	public String getEdit() {
		return edit;
	}
	public void setEdit(String edit) {
		this.edit = edit;
	}
	public boolean isZaoSong() {
		return isZaoSong;
	}
	public void setZaoSong(boolean isZaoSong) {
		this.isZaoSong = isZaoSong;
	}
	public String getBoxId() {
		return boxId;
	}
	public void setBoxId(String boxId) {
		this.boxId = boxId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public S_box() {
		super();
	}
/*	public S_box(String boxId, String state, boolean isZaoSong, String edit) {
		super();
		this.boxId = boxId;
		this.state = state;
		this.isZaoSong = isZaoSong;
		this.edit = edit;
	}
*/	public S_box(String boxId, String state) {
		super();
		this.boxId = boxId;
		this.state = state;
	}
	
}
