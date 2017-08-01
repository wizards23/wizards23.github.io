package com.moneyboxadmin.biz;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import com.entity.BoxDetail;
import com.golbal.pda.GolbalUtil;
import com.moneyboxadmin.service.EmptyMoneyBoxOutDoService;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 空钞箱出库,ATM加钞出库，钞箱装钞入库，回收钞箱入库，未清回收钞箱出库，停用钞箱出库，已清回收钞箱入库，新增钞箱入库
 * @ClassName: MoneyBoxOutDoBiz
 * @Description: TODO xxxxxxxx
 * @author liuchang
 * @date 2017-4-14 下午3:23:33
 */
public class MoneyBoxOutDoBiz {
	
	public  Handler hand_out; 	 
	 private GolbalUtil golbalUtil;		
	 public GolbalUtil getGolbalUtil() {
	 return golbalUtil=golbalUtil==null?new GolbalUtil():golbalUtil;
	 }
	 
	 	 
	  class Run implements Runnable{
		  Message m;
		  String state;
		  String cbinfo;
		  
		  public Run(){}		  
		  public Run(String cbinfo){
			 
			 m = hand_out.obtainMessage(); 
			 Log.i("m", m+"");
			 this.cbinfo = cbinfo;
		  }
		@Override
		public void run() {
			try {
			  state = EmptyMoneyBoxOutDoService.cashBoxOutOrIn(cbinfo);	
			  if("成功".equals(state)){
				  m.what = 1;
			  }else{
				  m.what=0;
			  }
			}catch (SocketTimeoutException e) {
				  m.what=-4;
			} catch (Exception e) {
				  m.what=-1;
			}finally{
				hand_out.sendMessage(m);
				getGolbalUtil().onclicks = true;
			}
			
		}
		  
	  }
		
					 
	 
	/**  空钞箱出库（业务单号为空字符串）
		ATM加钞出库（业务单号为空字符串）
		钞箱装钞入库（业务单号为空字符串）
		回收钞箱入库（业务单号为空字符串）
		未清回收钞箱出库（加钞计划编号为空字符串）
		停用钞箱出库 （加钞计划编号和业务单号为空字符串）
		已清回收钞箱入库（加钞计划编号为空字符串）
	 	新增钞箱入库（加钞计划编号和业务单号为空字符串）
	 * @param 出库操作
	 * @param bizName   业务名称
	 * @param list      扫描钞箱集合
	 * @param planNum   计划编号
	 * @param assessor  审核员
	 * @param assessors 复审核员
	 * @param corpId    机构ID
	 * @param bizNum    业务单号
	 */
	public  void getemptyMoneyBoxoutdo(String bizName,List<BoxDetail> list,String planNum,String assessor,String assessors,String corpId,String bizNum,int isfirst){		
		Log.i("gggassessor", assessor);
		Log.i("gggassessors", assessors);
		  if(getGolbalUtil().onclicks){
			  String cbinfo = getCbinfo( bizName,list,planNum, assessor, assessors, corpId, bizNum,isfirst);
			  Log.i("cbinfo", cbinfo);
			  Thread t = new Thread(new Run(cbinfo));
			  t.start();
			  getGolbalUtil().onclicks =false;
		  }
		  
	}	 
	 
	/**
	 * 加钞计划编号;操作方式;钞箱品牌信息;钞箱编号;钞箱状态;审核员ID;复核员ID;机构ID;业务单号（多列时用|分隔）
	          空钞箱出库时 操作方式=3 钞箱状态=00 （业务单号为空字符串）
		ATM加钞出库时  操作方式=5 钞箱状态=02 （业务单号为空字符串）
		钞箱装钞入库时 操作方式=4 钞箱状态=01 （业务单号为空字符串）
		回收钞箱入库时 操作方式=6 钞箱状态=05 （业务单号为空字符串）
		未清回收钞箱出库 操作方式=8 钞箱状态=06 （加钞计划编号为空字符串）
		停用钞箱出库 操作方式=2 钞箱状态=08 （加钞计划编号和业务单号为空字符串）
		已清回收钞箱入库 操作方式=9 钞箱状态=07 （加钞计划编号为空字符串）
		新增钞箱入库 操作方式=1 钞箱状态=09 （加钞计划编号和业务单号为空字符串）
		回收钞箱入库（首次入库）操作方式=0 （业务单号;钞箱品牌信息;钞箱编号;钞箱状态;审核员ID;复核员ID;为空字符串）
	 * @param list  扫描箱子集合（包含钞箱品牌和钞箱编号）
	 * @param planNum  计划编号
	 * @param assessor  审核员
	 * @param assessors 复审核员
	 * @param corpId   机构ID
	 * @param bizNum   业务单号
	 * @param bizName  业务名称
	 * @return
	 */
	 private  String getCbinfo(String bizName,List<BoxDetail> list,String planNum,String assessor,String assessors,String corpId,String bizNum,int isfirst){

		 StringBuffer sb = new StringBuffer();
		 String type="";  //操作方式
		 String state="";  //钞箱状态
		 String row="|"; // 列
		 String col=";";   //行
		if("空钞箱出库".equals(bizName)){
			type = "3";
			state = "00";
			bizNum = "";
		}else if("ATM加钞出库".equals(bizName)){
			type = "5";
			state = "02";
			bizNum = "";
		}else if("钞箱装钞入库".equals(bizName)){
			type = "4";
			state = "01";
			bizNum = "";
		}else if("回收钞箱入库".equals(bizName)){
			if(isfirst==1){
				type = "0";	
			}else{
				type = "6";	
			}
			
			state = "05";
			bizNum = "";
		}else if("未清回收钞箱出库".equals(bizName)){
			type = "8";
			state = "06";
			planNum="";
		}else if("停用钞箱出库".equals(bizName)){
			type = "2";
			state = "08";
			planNum="";
		}else if("已清回收钞箱入库".equals(bizName)){
			type = "9";
			state = "07";
			planNum="";
		}else if("新增钞箱入库".equals(bizName)){
			type = "1";
			state = "09";
			bizNum = "";
			planNum="";
		}
		 
		if(isfirst>0){
			sb.append(planNum);	 //计划编号		
			sb.append(col); 
			sb.append("");   //业务单号
			sb.append(col);
			sb.append(type);
			sb.append(col); 
			sb.append(""); 
			sb.append(col); 
			sb.append(""); 
			sb.append(col); 
			sb.append(""); 
			sb.append(col); 
			sb.append("");
			sb.append(col);
			sb.append("");
			sb.append(col);
			sb.append(corpId);						
				
			return sb.toString();
		}else{
			for (int i = 0; i < list.size(); i++) {						
				sb.append(planNum);	 //计划编号		
				sb.append(col); 
				sb.append(bizNum);   //业务单号
				sb.append(col);
				sb.append(type);
				sb.append(col); 
				sb.append(list.get(i).getBrand()); 
				sb.append(col); 
				sb.append(list.get(i).getNum()); 
				sb.append(col); 
				sb.append(state); 
				sb.append(col); 
				sb.append(assessor);
				sb.append(col);
				sb.append(assessors);
				sb.append(col);
				sb.append(corpId);						
				sb.append(row);			
			} 
			int end = sb.lastIndexOf("|");
			return sb.toString().substring(0,end);
		}
		
		
		
		
	 }
}
