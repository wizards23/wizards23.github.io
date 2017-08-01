package com.ljsw.tjbankpda.qf.entity;

import java.io.Serializable;

public class TianJiaZhongKong implements Serializable {
	private String kaishiHao; // 开始号
	private String jieshuHao; //结束号段
	private String haoZimu; // 号段前面的字母
	private int haoShuzi; // 号段后面的数字
	private String zhongleiId; // 凭证种类id
	private String zhongleiName;  //种类名称
	private int xianxushu; // 连续数
	

	
	
	public String getJieshuHao() {
		return jieshuHao;
	}

	public void setJieshuHao(String jieshuHao) {
		this.jieshuHao = jieshuHao;
	}

	public String getHaoZimu() {
		return haoZimu;
	}

	public void setHaoZimu(String haoZimu) {
		this.haoZimu = haoZimu;
	}
	
	public String getZhongleiName() {
		return zhongleiName;
	}

	public void setZhongleiName(String zhongleiName) {
		this.zhongleiName = zhongleiName;
	}

	public int getHaoShuzi() {
		return haoShuzi;
	}

	public void setHaoShuzi(int haoShuzi) {
		this.haoShuzi = haoShuzi;
	}

	public String getKaishiHao() {
		return kaishiHao;
	}

	public void setKaishiHao(String kaishiHao) {
		this.kaishiHao = kaishiHao;
	}

	public String getZhongleiId() {
		return zhongleiId;
	}

	public void setZhongleiId(String zhongleiId) {
		this.zhongleiId = zhongleiId;
	}

	public int getXianxushu() {
		return xianxushu;
	}

	public void setXianxushu(int xianxushu) {
		this.xianxushu = xianxushu;
	}

	public TianJiaZhongKong(String kaishiHao, String haoZimu, int haoShuzi,
			String zhongleiId, int xianxushu,String zhongleiName,String jieshuHao) {
		this.kaishiHao=kaishiHao;
		this.haoShuzi=haoShuzi;
		this.haoZimu=haoZimu;
		this.zhongleiId=zhongleiId;
		this.xianxushu=xianxushu;
		this.zhongleiName=zhongleiName;
		this.jieshuHao=jieshuHao;
	}

	public TianJiaZhongKong() {
		super();
	}
	

}
