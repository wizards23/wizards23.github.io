package com.ljsw.tjbankpda.db.entity;

import java.io.Serializable;
import java.util.List;

public class ShangJiaoChuku implements Serializable {
	private static final long serialVersionUID = 1L;
	private String zongxianlu;
	private String zongxuanluid;
	private String zzxzongshu;
	private List<String> zzxbianhao;
	private List<String> zhixianlu;
	private List<String> zzxshuliang;
	public String getZongxianlu() {
		return zongxianlu;
	}
	public void setZongxianlu(String zongxianlu) {
		this.zongxianlu = zongxianlu;
	}
	public String getZongxuanluid() {
		return zongxuanluid;
	}
	public void setZongxuanluid(String zongxuanluid) {
		this.zongxuanluid = zongxuanluid;
	}
	public String getZzxzongshu() {
		return zzxzongshu;
	}
	public void setZzxzongshu(String zzxzongshu) {
		this.zzxzongshu = zzxzongshu;
	}
	public List<String> getZzxbianhao() {
		return zzxbianhao;
	}
	public void setZzxbianhao(List<String> zzxbianhao) {
		this.zzxbianhao = zzxbianhao;
	}
	public List<String> getZhixianlu() {
		return zhixianlu;
	}
	public void setZhixianlu(List<String> zhixianlu) {
		this.zhixianlu = zhixianlu;
	}
	public List<String> getZzxshuliang() {
		return zzxshuliang;
	}
	public void setZzxshuliang(List<String> zzxshuliang) {
		this.zzxshuliang = zzxshuliang;
	}
	public ShangJiaoChuku(String zongxianlu, String zongxuanluid,
			String zzxzongshu, List<String> zzxbianhao, List<String> zhixianlu,
			List<String> zzxshuliang) {
		super();
		this.zongxianlu = zongxianlu;
		this.zongxuanluid = zongxuanluid;
		this.zzxzongshu = zzxzongshu;
		this.zzxbianhao = zzxbianhao;
		this.zhixianlu = zhixianlu;
		this.zzxshuliang = zzxshuliang;
	}
	

}
