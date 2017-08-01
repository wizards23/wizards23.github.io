package com.ljsw.tjbankpda.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ljsw.tjbankpda.db.entity.ZhongkongModel;
import com.ljsw.tjbankpda.qf.entity.TianJiaZhongKong;

/**
 * 工具类　
 * @author yuyunheng
 *
 */
public class BiaohaoJiequ {

	StringGetNum getnum=new StringGetNum();  //截取工具类
	/**
	 * 将编号分拆
	 * @return
	 */
	public List<String> biaohaoFenchai(TianJiaZhongKong zk){
		System.out.println("进入编号分拆方法");
		List<String> bhList=new ArrayList<String>();
		String kaishiHao=zk.getKaishiHao();
		System.out.println("获取的开始号为："+kaishiHao);
		int count=zk.getXianxushu();
		for(int i=1;i<=count;i++){
			bhList.add(getnum.jisuanweiHao(kaishiHao, i));  
		}
		return bhList;
	}
	
	
	/**
	 * 将清点的信息汇总
	 * @return
	 */
	public List<ZhongkongModel> getmodel(List<TianJiaZhongKong> zklist){
		System.out.println("进入汇总方法");
		Map<String, ZhongkongModel> model=new HashMap<String, ZhongkongModel>();  //创建重空的键值集合
		
		for (TianJiaZhongKong tianJiaZhongKong : zklist) {
			String zhongkongId=tianJiaZhongKong.getZhongleiId(); // 取出重空类别id
			ZhongkongModel m=model.get(zhongkongId);
			if(m==null){
				// 如果没有这个类别 则新建并添加进入集合
				ZhongkongModel model1=new ZhongkongModel();
				model1.typeId=zhongkongId;
				model.put(zhongkongId, model1);
			}
			List<String> bh=biaohaoFenchai(tianJiaZhongKong);
			System.out.println("计算的长度为");
			m.bianhao.addAll(bh);
		}
		
		List<ZhongkongModel> mo=new ArrayList<ZhongkongModel>();
		for (String key : model.keySet()) {
			mo.add(model.get(key));
		}
		
		return mo;
	}
}
