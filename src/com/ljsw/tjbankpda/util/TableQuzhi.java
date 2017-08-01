package com.ljsw.tjbankpda.util;

import java.util.List;
/**
 * Table 根据键取首值 
 * @author yuyunheng
 *
 */
public class TableQuzhi {

	/*
	 * 取出唯一值
	 */
	public static String getZhi(Table t,String key){
		Column c=t.get(key);
		if(c!=null){
			List<String> list=c.getValues();
			
			if(list!=null&&list.size()>0){
				return list.get(0);
			}
		}
		return null;
	}
	
	/*
	 * 取出所有值
	 */
	public static List<String> getList(Table t,String key){
		Column c=t.get(key);
		if(c!=null){
			return c.getValues();
		}
		return null;
	}
}
