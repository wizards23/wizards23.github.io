package com.example.app.entity;

import java.io.Serializable;

/**、
 * 款箱出入线路信息实体类
 * @author yuyunheng
 *
 */
public class KuanxiangChuruEntity implements Serializable{

	private String xianluMingcheng;    //线路名称
	private int wangdianshu;   //该线路网点数
	private int kuanxiangshu;   //该线路款箱数
	
	public String getXianluMingcheng() {
		return xianluMingcheng;
	}
	public void setXianluMingcheng(String xianluMingcheng) {
		this.xianluMingcheng = xianluMingcheng;
	}
	public int getWangdianshu() {
		return wangdianshu;
	}
	public void setWangdianshu(int wangdianshu) {
		this.wangdianshu = wangdianshu;
	}
	public int getKuanxiangshu() {
		return kuanxiangshu;
	}
	public void setKuanxiangshu(int kuanxiangshu) {
		this.kuanxiangshu = kuanxiangshu;
	}
	
	
	
}
