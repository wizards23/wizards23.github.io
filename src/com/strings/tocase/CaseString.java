package com.strings.tocase;

import java.io.ByteArrayOutputStream;

import android.util.Log;

public class CaseString {

	/*
	 * 16进制数字字符集
	 */
	private static String hexString = "0123456789ABCDEF";
	public static boolean isOk = false;

	/**
	 * 转化字符串为十六进制编码
	 * 
	 * @param s
	 * @return
	 */
	public static String toHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str.toUpperCase();
	}

	/**
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文）
	 */
	public static String decode(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());
	}

	/**
	 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
	 */
	public static String encode(String str) {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/**
	 * 转化为钞箱编号
	 * 
	 * @param number
	 * @return
	 */
	public static String getBoxNum(String number) {

		if (number.length() < 8) {
			return "";
		}

		if (number.length() >= 14) {
			String jiewei = number.substring(10, 14);
			System.out.println("打印：" + jiewei);
			if ("9090".equals(jiewei.trim()) || "7588".equals(jiewei.trim())) {
				return "";
			}
		}

		String num = number.substring(0, 8);

		Log.i("转化传进来的", num);
		StringBuffer sb = new StringBuffer();
		try {
			sb.append((char) Integer.parseInt(num.substring(0, 2)));
			sb.append((char) Integer.parseInt(num.substring(2, 4)));
			sb.append(num.substring(4, 8));
		} catch (Exception e) {
			e.printStackTrace();

		}
		return sb.toString();
	}

	/**
	 * 周转箱扫描
	 * 
	 * @param number
	 * @return
	 */
	public static String getBoxNum2(String number) {
		if (number.length() < 14) {
			return "";
		}

		String num = number.substring(0, 14);
		Log.i("转化传进来的2", num);
		StringBuffer sb = new StringBuffer();
		try {
			sb.append((char) Integer.parseInt(num.substring(0, 2)));
			sb.append((char) Integer.parseInt(num.substring(2, 4)));
			sb.append(num.substring(4, 10));
			sb.append((char) Integer.parseInt(num.substring(10, 12)));
			sb.append((char) Integer.parseInt(num.substring(12, 14)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("转化传进来3的", sb.toString());
		System.out.println("转化传进来3的:" + sb.toString());
		return sb.toString();

	}

	/**
	 * 款箱扫描
	 * 
	 * @param number
	 * @return
	 */
	public static String getBoxNum3(String number) {
		if (number.length() < 14) {
			return "";
		}

		if (number.length() >= 14) {
			String jiewei = number.substring(10, 14);
			System.out.println("打印：" + jiewei);
			if (!"7588".equals(jiewei.trim())) {
				return "";
			}
		}

		String num = number.substring(0, 14);
		Log.i("转化传进来的2", num);
		StringBuffer sb = new StringBuffer();
		try {
			sb.append((char) Integer.parseInt(num.substring(0, 2)));
			sb.append((char) Integer.parseInt(num.substring(2, 4)));
			sb.append(num.substring(4, 10));
			sb.append((char) Integer.parseInt(num.substring(10, 12)));
			sb.append((char) Integer.parseInt(num.substring(12, 14)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("转化传进来3的", sb.toString());
		System.out.println("转化传进来3的:" + sb.toString());
		return sb.toString();
		// return num.toString();

	}
	
	/**
	 * 返回ATM编号
	 * @param number
	 * @return
	 */
	public static String getATMNum(String number){
		if(number.length()<10){
			return "";
		}
		String num = number.substring(0, 10);
		StringBuffer sb = new StringBuffer();
		try {
			sb.append((char) Integer.parseInt(num.substring(0, 2)));
			sb.append((char) Integer.parseInt(num.substring(2, 4)));
			sb.append(num.substring(4, 10));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i("转化传进来3的", sb.toString());
		System.out.println("转化传进来getATMNum:" + sb.toString());
		return sb.toString();
		
	}

	/**
	 * 得到10进制的编号
	 * 
	 * @param str
	 * @return
	 */
	public static String getNum(String str) {
		String s = str.substring(0, 2);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			sb.append((int) s.charAt(i));
		}
		return sb.append(str.substring(2, str.length())).toString();
	}

	/**
	 * 编号正则表达式
	 * 
	 * @param str
	 *            编号
	 * @return
	 */
	public static boolean reg(String str) {
		if (str.length() < 8) {
			return false;
		}
		StringBuffer sb = new StringBuffer();
		try {
			sb.append((char) (Integer.parseInt(str.substring(0, 2))));
			sb.append((char) (Integer.parseInt(str.substring(2, 4))));
		} catch (Exception e) {
			return false;
		}

		sb.append(str.substring(4, str.length()));
		String r = "^[A-Z]{2}[0-9]{20}$";
		System.out.println(sb + "----" + sb.length());

		return sb.toString().matches(r);

	}

}
