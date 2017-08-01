package com.ljsw.tjbankpda.db.application;

import java.util.ArrayList;
import java.util.List;

import com.entity.SystemUser;
import com.ljsw.tjbankpda.db.entity.QingLingChuRuKu;
import com.ljsw.tjbankpda.db.entity.QingLingZhuangXiangChuKu;
import com.ljsw.tjbankpda.db.entity.ShangJiaoChuku;
import com.ljsw.tjbankpda.db.entity.ShangJiaoQingFenChuKu;
import com.ljsw.tjbankpda.db.entity.ShangJiaoRuKu;
import com.ljsw.tjbankpda.qf.entity.JiHuaDan_Xianjin;
import com.ljsw.tjbankpda.qf.entity.JiHuaDan_Zhongkong;
import com.ljsw.tjbankpda.qf.entity.QingLingRuKu;

import android.app.Application;

public class o_Application  {
	private static  o_Application application = null;
	public static boolean DiZhiYaPin_bool=false;
	public static SystemUser kuguan_db;
	public static String loginWangdian;
	public static List<String> numberlist=new ArrayList<String>();//一,二维码用
	public static String wrong=null;//错误的扫箱
	//扫描过滤用
	public static List<String> guolv=new ArrayList<String>();
	//请领装箱出库计划单集合
	public static List<QingLingZhuangXiangChuKu> qinglingchuku_jihuadan=new ArrayList<QingLingZhuangXiangChuKu>();
	//计划单明细 现金集合
	public static List<JiHuaDan_Xianjin> jihuadan_list_xianjin = new ArrayList<JiHuaDan_Xianjin>();
	//请领装箱出库明细
	public static QingLingZhuangXiangChuKu danhao;
	//计划单明细 重空凭证集合
	public static List<JiHuaDan_Zhongkong> jihuadan_list_zhongkong = new ArrayList<JiHuaDan_Zhongkong>();
	//计划单明细  抵质押品集合
	public static List<String> jihuadan_list_dizhiyapin=new ArrayList<String>();
	//上缴清分计划单明细
	public static List<ShangJiaoQingFenChuKu> shangjiao_qingfen_chuku=new ArrayList<ShangJiaoQingFenChuKu>();
	//清分计划单明细对象
	public static ShangJiaoQingFenChuKu qingfendanmingxi;
	//请领款项出入库
	public static List<QingLingChuRuKu> qingling_churuku=new ArrayList<QingLingChuRuKu>();
	//请领款项出入库对象
	public static QingLingChuRuKu qinglingchuruku;
	//上缴出库明细
	public static List<ShangJiaoChuku> shangjiaochuku=new ArrayList<ShangJiaoChuku>();
	//上缴出库明细(单个被点击对象)
	public static ShangJiaoChuku chukumingxi;
	//请领入库
	public static List<QingLingRuKu> qinglingruku=new ArrayList<QingLingRuKu>();
	//请领入库(单个选中对象)
	public static QingLingRuKu qlruku;
	//上缴入库
	public static List<ShangJiaoRuKu> shangjiaoruku=new ArrayList<ShangJiaoRuKu>();
	//上缴入库(单个选中对象)
	public static ShangJiaoRuKu rukumingxi;
	//指纹验证用
	public static SystemUser left_user;
	public static SystemUser right_user;
	//fragment
	public static String fragmentleft="";
	public static String fragmentright="";
	//库管登录双人帐号信息
	public static List<String> FingerLoginNum=new ArrayList<String>();
	//交接双人登录帐号信息
	public static List<String> FingerJiaojieNum=new ArrayList<String>();
	//交接双人登录帐号信息
	public static List<String> FingerQinfenLoginNum=new ArrayList<String>();
	//清分双人登录
	public static SystemUser qingfen;
	//清分管理员登录
	public static SystemUser qingfenyuan;
	//押运员登录
	public static SystemUser yayunyuan;
	//zhuangxiangJihuadan
	public static String zhuangxiangJihuadan;
	public static o_Application getApplication(){
		if(application==null){
		   application = new o_Application();			
		}
		return application;
	}


	
}
