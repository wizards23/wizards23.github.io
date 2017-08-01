package com.moneyboxadmin.biz;

import java.net.SocketTimeoutException;
import java.util.List;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.application.GApplication;
import com.entity.BoxDetail;
import com.golbal.pda.GolbalUtil;
import com.moneyboxadmin.service.GetBoxDetailListService;
import com.moneyboxadmin.service.GetEmptyRecycleCashboxInDetailService;
import com.moneyboxadmin.service.GetemptyCashBoxOutDetailService;

//空钞箱出库,ATM加钞出库，钞箱装钞入库，回收钞箱入库钞箱明细,已清回收钞箱明细
public class GetBoxDetailListBiz {
	
	public static List<BoxDetail> list = null;  //箱子明细集合
	
	public  Handler hand_detail; 
	public static int boxCount=0;  //空钞箱出库数量
	
	
	//工具类
	 private GolbalUtil getUtil;	
	 GolbalUtil getGetUtil() {return getUtil=getUtil==null?new GolbalUtil():getUtil;}
	//已清回收钞箱明细
	 GetEmptyRecycleCashboxInDetailService getEmptyRecycleCashboxInDetail; 	  
	  GetEmptyRecycleCashboxInDetailService getGetEmptyRecycleCashboxInDetail() {
		return getEmptyRecycleCashboxInDetail=getEmptyRecycleCashboxInDetail==null?new GetEmptyRecycleCashboxInDetailService():getEmptyRecycleCashboxInDetail;
	}
	 //空钞箱出库存明细
	 GetemptyCashBoxOutDetailService emptyCashBoxOutDetail;
	 GetemptyCashBoxOutDetailService getEmptyCashBoxOutDetail() {
		return emptyCashBoxOutDetail=emptyCashBoxOutDetail==null?new GetemptyCashBoxOutDetailService():emptyCashBoxOutDetail;
	}
	//ATM加钞出库，钞箱装钞入库，回收钞箱入库钞箱明细
	GetBoxDetailListService boxDetailList;	
	GetBoxDetailListService getBoxDetailList() {
		return boxDetailList=boxDetailList==null?new GetBoxDetailListService():boxDetailList;
	}

	
	/**
	 * 
	 * @param plan   业务编号
	 * @param bizName  根据业务名称传入不同的值
	 */
	public  void getBoxDetailList(String plan,String bizName){
		Log.i("plan", plan);
		Log.i("bizName明细", bizName);
		String type="";
		
		if(getGetUtil().onclicks){
		if(bizName.equals("ATM加钞出库")){
			type = "1";
		}else if(bizName.equals("钞箱装钞入库")){
			type = "2";
		}else if(bizName.equals("回收钞箱入库")){
			type = "3";
		}else if(bizName.equals("未清回收钞箱出库")){
			type = "4";
		}
		AsyncTaskBoxDetail asyn = new AsyncTaskBoxDetail(plan,bizName,type);
		asyn.execute();
		getGetUtil().onclicks = false;
		}
		
	}
	
	
	/**
	 * 开始获取数据
	 * @author Administrator
	 *
	 */
	 private  class AsyncTaskBoxDetail extends AsyncTask{
		 	String planNum;
		 	String bizName;
		 	String type;
			Message m;
			
			public AsyncTaskBoxDetail(){};
			public AsyncTaskBoxDetail(String planNum,String bizName,String type){
				this.planNum = planNum;	
				this.bizName =bizName;
				this.type = type;
				m = hand_detail.obtainMessage();
				
			};
			//取消任务后的操作  
			@Override
			protected void onCancelled() {
				super.onCancelled();
				getGetUtil().onclicks = true;
				//m.what=-1;
				hand_detail.sendMessage(m);
			}

			//异步完成后的操作
			@Override
			protected void onPostExecute(Object result) {
				super.onPostExecute(result);
				getGetUtil().onclicks = true;
				if(list==null){
					m.what=0;
				}else{
					m.what=1;
					
				}
				hand_detail.sendMessage(m);
			}
		
			//异步后台操作
			@Override
			protected Object doInBackground(Object... arg0) {
				Log.i("00000", bizName);
				try {
					//已清回收钞箱入库明细
					if(bizName.equals("已清回收钞箱入库")){
					 Log.i("111", bizName);
					list = getGetEmptyRecycleCashboxInDetail().getEmptyRecycleCashboxInList(planNum);	
					boxCount = list.size();
					
					//空钞箱出库明细
					}else if("空钞箱出库".equals(bizName)){
					list = getEmptyCashBoxOutDetail().getemptyCashBoxOutDetail(planNum);
					boxCount = 0;
						//获取总钞箱数量
						for (int i = 0; i <list.size(); i++) {
						boxCount =boxCount+ Integer.parseInt(list.get(i).getNum());	 //箱子数量
						}					
					
					Log.i("222", bizName);
					//
					}else{
					list = getBoxDetailList().getCashBoxDetail(planNum,type);
					boxCount = list.size();  //箱子数量
					Log.i("333", bizName);
					}
					
				}catch (SocketTimeoutException e) {				
					e.printStackTrace();
					m.what=-4;
					this.cancel(true);
				} catch (Exception e) {				
					e.printStackTrace();
					m.what=-1;
					this.cancel(true);
				}
				return null;
			}		   
		  }
	
	
}
