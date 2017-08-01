package com.moneyboxadmin.biz;

import java.net.SocketTimeoutException;
import java.util.List;

import com.entity.Box;
import com.golbal.pda.GolbalUtil;
import com.moneyboxadmin.service.GetCashBoxListService;
import com.service.FixationValue;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

//空钞箱出库,ATM加钞出库，钞箱装钞入库，回收钞箱入库,钞箱加钞列表
public class GetPlanWayListBiz {
	public static List<Box> list_box;
	//public  Handler hand_boxlist;
	public static Handler hand_way;
	///public  Handler hand_waydate;
	boolean state;    //异步是否完整执行
	
	private GolbalUtil getUtil;	
	public GolbalUtil getGetUtil() {return getUtil=getUtil==null?new GolbalUtil():getUtil;}
	
	GetCashBoxListService getCashBoxList;			
	public GetCashBoxListService getGetCashBoxList() {
		return getCashBoxList=getCashBoxList==null?new GetCashBoxListService():getCashBoxList;
	}

	public  void getBoxList(String type,String corpid){
		if(getGetUtil().onclicks){
			getListByType(type,corpid);
			getGetUtil().onclicks = false;	
		}
		
	}
				
	/**
	 * 获取数据
	 * @author Administrator
	 *
	 */
	 private  class AsyncTaskGetBox extends AsyncTask{
		 	String corpId;
		 	String type;
		 	Message m;
		 	
		 	public AsyncTaskGetBox(){};
		 	public AsyncTaskGetBox(String corpId,String type){
		 		this.corpId = corpId;
		 		this.type = type;
		 		Log.i("hand_way", hand_way+"");
		 		m = hand_way.obtainMessage();
		 	};
		 	
			//取消任务后的操作  
			@Override
			protected void onCancelled() {
				super.onCancelled();
				getGetUtil().onclicks = true;
				//m.what = -1;
				hand_way.sendMessage(m);
			}

			//异步完成后的操作
			@Override
			protected void onPostExecute(Object result) {
				super.onPostExecute(result);
				getGetUtil().onclicks = true;
				
				if(list_box==null){
					m.what=0;
				}else{
					m.what=1;
				}			
				hand_way.sendMessage(m);
			}

			//异步后台操作
			@Override
			protected Object doInBackground(Object... arg0) {
				try {
					list_box = getGetCashBoxList().getCashBoxListResult(corpId, type);
				}catch (SocketTimeoutException e) {					
					e.printStackTrace();
					m.what = -4;
					this.cancel(true);
				} catch (Exception e) {
					e.printStackTrace();
					m.what = -1;
					this.cancel(true);
				}
				return null;
			}		   
		  }
		 
	   /**
	    * 业务类型
	    * @param type
	    */
	   private  void getListByType(String type,String corpid){	
		   AsyncTaskGetBox asyn = null;
		  if("空钞箱出库".equals(type)){
			 asyn=new AsyncTaskGetBox(corpid,FixationValue.TYPE_EMPTYBOXOUT); 			
		  }else if("ATM加钞出库".equals(type)){
			 asyn=new AsyncTaskGetBox(corpid,FixationValue.TYPE_ATM); 			  
		  }else if("钞箱装钞入库".equals(type)){
			 asyn=new AsyncTaskGetBox(corpid,FixationValue.TYPE_PUTIN);   
		  }else if("回收钞箱入库".equals(type)){
		    asyn=new AsyncTaskGetBox(corpid,FixationValue.TYPE_BACKBOX);   
		  }else if("钞箱加钞".equals(type)){
			  asyn=new AsyncTaskGetBox(corpid,FixationValue.TYPE_ADDMONEY);   
		  }
		  asyn.execute(); 
	   }
	   
	  
	
}
