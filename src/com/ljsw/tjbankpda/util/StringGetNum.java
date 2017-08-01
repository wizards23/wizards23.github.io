package com.ljsw.tjbankpda.util;

/**
 * 从字符串中提取数字
 * @author yuyunheng
 */
public class StringGetNum {

	/**
	 * 返回后面数字
	 * @param s
	 * @return
	 */
	public int getNum(String s){
		// 反遍历 字符串
		for(int i=s.length()-1;i>0;i--){
			int c=s.charAt(i);   // 取出每个字节的 字节码
			if(c>=48&&c<=57){ 
				//比较字符是否为数字
				continue;
			}else{
				// 不是数字 记录下下一个标
				String ss=s.substring(i+1).trim();
				
				if(ss!=null&&!ss.equals(""))
					return Integer.parseInt(ss);
				else 
					return -1;
			}
		}

		return -1;
	}
	
	/**
	 * 计算尾号段
	 * @param s
	 * @return
	 */
	public String getStringNum(String s){
		for(int i=s.length()-1;i>0;i--){
			int c=s.charAt(i);   // 取出每个字节的 字节码
			
			if(c>=48&&c<=57){ 
				//比较字符是否为数字
				continue;
			}else{
				// 不是数字 记录下下一个标
				String ss=s.substring(i+1).trim();
				return ss;
			}
		}

		return "-1";
	}
	
	/**
	 * 返回前面字母
	 * @param s
	 * @return
	 */
	public String getChar(String s,String num){
		if(s==null || num==null)
			return "";		
		int xiabiao=s.indexOf(num);
		if(xiabiao==-1)
			return "";
		return s.substring(0,xiabiao);
	}
	
	/**
	 * 计算尾号段
	 * (此方法比较特殊，考虑到截取的数字如果前面为0会被int型抹去，此方法用比较填充的
	 * 方式将抹去的0补充回来)
	 * @return
	 */
	public String jisuanweiHao(String beginHaoduan,int count){
		String num=getStringNum(beginHaoduan);   //截取末尾数字字符串
		String zimu=getChar(beginHaoduan, num);  // 截取前面字母
		int numLength=num.trim().length();     // 获取后面数字的长度
		int numInt=Integer.parseInt(num.trim());  // 将字符串数字转成整形
		numInt=numInt+count-1;    // 计算出末尾的号段
		String numString=numInt+"";  //将末尾的号段转换成 字符串 
		int num2Length=numString.trim().length();  //计算末尾数字的长度
		if(numLength>num2Length){   //比较开始号段与末尾号段长度，如果末尾号段长度小于开始号段用"0"填充
			String n="";
			 int cha=numLength-num2Length;
			 for(int i=0;i<cha;i++){
				n+="0";
			 }
			 numString=n+numString;
		}
		
		return zimu+numString;
	}
	
}
