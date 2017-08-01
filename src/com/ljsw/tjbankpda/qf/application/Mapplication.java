package com.ljsw.tjbankpda.qf.application;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

import com.entity.SystemUser;
import com.ljsw.tjbankpda.db.entity.Qingfenxianjin;
import com.ljsw.tjbankpda.qf.entity.Box;
import com.ljsw.tjbankpda.qf.entity.ZhuanxiangTongji;

/**
 * 清分全局变量
 * @author FUHAIQING
 */
public class Mapplication {
	private static Mapplication ap;
	public SystemUser user1;//第一个登陆的用户
	public SystemUser user2;//第二个登陆的用户
	public String UserId;//第2个用户id
	
	public List<ZhuanxiangTongji> zxLtXianjing=new ArrayList<ZhuanxiangTongji>();//装箱现金统计列表
	public List<ZhuanxiangTongji> zxLtZhongkong=new ArrayList<ZhuanxiangTongji>();//装箱重空凭证统计列表
	public ZhuanxiangTongji zxTjDizhi=new ZhuanxiangTongji();//装箱抵质押品统计
	public List<String> ltDizhiNum=new ArrayList<String>();//抵质押品编号列表
	
	/**上缴清分领款需领取周转箱列表**/
	public List<String> ltQflkBoxNum=new ArrayList<String>();
	
	public String[] xjType;
	public String[] zkType;

	public List<Box>ltZzxNumber=new ArrayList<Box>();//周转箱编号列表
	
	public List<Qingfenxianjin> boxLtXianjing=new ArrayList<Qingfenxianjin>();
	public List<Box> boxLtZhongkong=new ArrayList<Box>();
	public List<String> boxLtDizhi=new ArrayList<String>();
	
	public String renwudan;//任务单号
	public String jigouid;//机构id
	
	/**请领装箱现金是否装完**/
	public boolean IsXianjingOK;
	/**请领装箱重空凭证是否装完**/
	public boolean IsZhongkongOK;
	/**请领装箱抵质是否装完**/
	public boolean IsDizhiOK;
	
	
	
	private Mapplication(){};
	
	public static Mapplication getApplication(){
		if(ap==null){
		   ap = new Mapplication();
		}		
		return ap;
	}

}
