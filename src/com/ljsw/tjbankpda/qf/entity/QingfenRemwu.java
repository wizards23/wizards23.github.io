package com.ljsw.tjbankpda.qf.entity;

/**
 * 清分任务实体类
 * @time 2015-06-19 11:21
 * @author FUHAIQING
 */
public class QingfenRemwu {
	private String id;//支行id
	private String name;//支行名称
	private String wangdianCount;//网点数量
	private String zhouzhuanxiangCount;//周转箱数量
	
	public QingfenRemwu(String id, String name, String wangdianCount) {
		super();
		this.id = id;
		this.name = name;
		this.wangdianCount = wangdianCount;
	}
	
	public QingfenRemwu(String id, String name, String wangdianCount,
			String zhouzhuanxiangCount) {
		super();
		this.id = id;
		this.name = name;
		this.wangdianCount = wangdianCount;
		this.zhouzhuanxiangCount = zhouzhuanxiangCount;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWangdianCount() {
		return wangdianCount;
	}
	public void setWangdianCount(String wangdianCount) {
		this.wangdianCount = wangdianCount;
	}
	public String getZhouzhuanxiangCount() {
		return zhouzhuanxiangCount;
	}
	public void setZhouzhuanxiangCount(String zhouzhuanxiangCount) {
		this.zhouzhuanxiangCount = zhouzhuanxiangCount;
	}
	
	
}
