package com.service;

public class FixationValue {

	// 地址空间
 	public static String NAMESPACE = "http://service.timer.cashman.poka.cn/";
 	// http://192.168.1.110:9080/webcash0908/webservice
 	public static String url = "http://192.168.1.113:8080/CashWebServices/webservice";
 	// webservice地址
 	public static String URL=url+"/cash_pda";//ATM钞箱模块以及 登录、指纹验证接口   对应 IPdaOperateService.java接口
    public static String URL2=url+"/cash_box";//款箱模块（早送、晚收） 对应 IPdaOperateService.java接口

 	public static String URL3 =url+"/cash_cm"; //对应请领上缴模块中的 清分员操作     对应 ICleaningManService.java
 	
 	public static String URL4 =url+"/cash_cmanagement";//对应请领上缴模块中的 清分管理员操作  对应ICleaningManagementService.java
 	
 	public static String URL5 =url+"/cash_kuguanyuan";//对应请领上缴模块中的 押运员操作   对应IKuGuanYuanService.java
 	
 	// 三期命名空间+url地址
// 	public static String NAMESPACE3 = NAMESPACE;
 //	public static String ICleaningManService = Url
 	//		+ "/CashWebServices/webservice/cash_cm";
// 	public static String ICleaningManagementService = Url
 //			+ "/CashWebServices/webservice/cash_cmanagement";
 //	public static String IKuGuanYuanService = Url
 //			+ "/CashWebServices/webservice/cash_kuguanyuan";
 	// 登录地址
// 	public static String IPdaOperateService = Url
// 			+ "/CashWebServices/webservice/cash_pda";
// 	public static String IPdaOfBoxOperateService = Url
// 			+ "/CashWebServices/webservice/cash_box";

 //	public static String IPdaOperateService = Url+"/CashWebServices/webservice/cash_pda";
 //	public static String IPdaOfBoxOperateService =Url+"/CashWebServices/webservice/cash_box";

    
	//钞箱操作类型参数
	public static final String TYPE_EMPTYBOXOUT="02";   //空钞箱出库
	public static final String TYPE_ATM="04";	//ATM加钞出库
	public static final String TYPE_PUTIN="03";   //钞箱装钞入库
	public static final String TYPE_BACKBOX="05";	//回收钞箱入库
	public static final String TYPE_ADDMONEY="03";	//钞箱加钞
	
	
	public static final int supercargo = 9;  //押运员角色ID   原来是8
	public static final int warehouse = 4;  //库管员角色ID
	public static final int clearer = 7;  //清分员角色ID
	public static final int webuser = 5;  //网点人员角色ID   原来这个ID是6，因为后来要求作出修改，把6改成了5，实为加钞人员，只改了值，名称固定不变
	public static final int examine  = 3;  //审核员角色ID
	public static final int waibaoqingfen=19;  //外包清分员
	public static final String waibaoQingfenString=waibaoqingfen+"";
	
	
	
	
}
