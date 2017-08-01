package com.ljsw.tjbankpda.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * 数字格式转化类
 * 
 * @author FUHAIQING
 */
public class NumFormat {

	/**
	 * 转换现金数字显示格式 如10000转换为10,000
	 * 
	 * @param count
	 * @return
	 */
	public String format(String count) {
		if (count != null && !count.equals("")) {
			Double money=Double.parseDouble(count.trim());
			
			NumberFormat currencyFormatA = NumberFormat
					.getCurrencyInstance(Locale.CHINA);
			
			String monryString=currencyFormatA.format(money);
			int index=monryString.indexOf("￥");
			monryString=monryString.substring(index+1);
			return monryString;
		}
		return "0";
	}

}
