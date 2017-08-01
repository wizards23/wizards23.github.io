package com.imple.getnumber;

import hdjc.rfid.operator.INotify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Notation;

import com.clearadmin.biz.CashboxAddMoneyDetailBiz;
import com.entity.BoxDetail;
import com.strings.tocase.CaseString;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class AddMoneygetNum implements INotify {
	//钞箱加钞-加钞操作-扫描钞箱编号
	
	public static Handler handler;
	Bundle bundle;
	public static Map<String,BoxDetail> map = new HashMap<String,BoxDetail>();
	public static List<String> yiQingfenList = new ArrayList<String>();//已清分列表
	boolean temp = true;
	public List<String> list=new ArrayList<String>();

	    //钞箱明细类
		private CashboxAddMoneyDetailBiz cashboxAddMoneyDetail;		
		CashboxAddMoneyDetailBiz getCashboxAddMoneyDetail() {
			return cashboxAddMoneyDetail=cashboxAddMoneyDetail==null?new CashboxAddMoneyDetailBiz():cashboxAddMoneyDetail;
		}
				
		@Override
		public void getNumber(String number) {
			if(number==null || !CaseString.reg(number)){
				return;
			 }
			Message m;						
			//把10进制转换成钞箱编号
			String boxNum = CaseString.getBoxNum(number);
	
			if(yiQingfenList.contains(boxNum)||"".equals(boxNum)){
				return;
			}
			Log.i("boxNum", boxNum);
			
			//如果集合里面没有数据表示所有钞箱扫描完成
			if(map.size()==0){
			
				Log.i("未扫钞箱个数", map.size()+"");
				m = handler.obtainMessage();
				m.what = 3;  //所有箱子已扫描加钞完成
				handler.sendMessage(m);	
				return;
			}
			
			     m = handler.obtainMessage();
			     String num=null;
			for (Map.Entry<String,BoxDetail> item:map.entrySet()) {					
				//集合里面的钞箱编号
				num = item.getKey();
				Log.i("num", num);
				//如果相等，则表示为有效数据
				if(num.equals(boxNum)){														
					m.what = 1;					
					break;
				}
			}
			
			
			if(bundle==null){
			bundle = new Bundle();	
			}								
			bundle.putString("num",boxNum);
			m.setData(bundle);
			handler.sendMessage(m);
			
			
			
		}
		
		
}
