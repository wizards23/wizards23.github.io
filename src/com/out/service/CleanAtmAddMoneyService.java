package com.out.service;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.util.Log;

import com.entity.ClearMoney;
import com.entity.WebParameter;
import com.out.biz.ClearMachineIngBiz;
import com.service.WebService;


//清机加钞
public class CleanAtmAddMoneyService {
	
	/**
	 * 
	 * @param cashboxNums  钞箱编号，多个用|隔开
	 * @param corpId  机构ID
	 * @param userId1  用户ID1
	 * @param userId2  用户ID2
	 * @return
	 * @throws Exception
	 */
	public List<ClearMoney> cleanAtmAddMoney(String cashboxNums ,String corpId,String userId1,String userId2) throws Exception{
		
		String methodName = "updateAndCleanAtmAddMoney";
	
		WebParameter[] param ={ 
				new WebParameter<String>("arg0",cashboxNums),
				new WebParameter<String>("arg1",corpId),
				new WebParameter<String>("arg2",userId1),
				new WebParameter<String>("arg3",userId2)
				};
	
		List<ClearMoney> list = null;
		SoapObject soap = WebService.getSoapObject(methodName, param);
		String code = soap.getProperty("code").toString();
		String params = soap.getProperty("params").toString();
		String msg = soap.getProperty("msg").toString();

				
		ClearMoney clear = null;				
		ClearMachineIngBiz.msg = msg;
		list = new ArrayList<ClearMoney>();	
		
		if("00".equals(code) && !params.equals("anyType{}")){			
			String[] array = null;
			
			//如果是多条记录
			if(params.indexOf("\r\n")>0){	
				//把每条记录拆成数组，分别由ATM编号;钞箱编号;状态组成,分隔符是\r\n
				array = params.split("\r\n");
				System.out.println("arraylength"+array.length);
				
				for (int i = 0; i < array.length; i++) {
					clear = new ClearMoney();
					//每ATM编号，钞箱编号，状态拆成数组，分隔符是;
					String[] arr = array[i].split(";");
									
					//如果是多个钞箱
					if(arr[1].indexOf("|")>0){
						//把多个钞箱拆成数组，分隔符是|
						String arrBox[] = arr[1].split("\\|");			
						for (int j = 0; j < arrBox.length; j++) {
							clear =new ClearMoney();
							clear.setAtmNum(arr[0]);  //ATM编号
							clear.setBoxNum(arrBox[j]);   //钞箱编号
							clear.setState(arr[2]);   //状态
							list.add(clear);
						
							}
						
					//单个钞箱
					}else{
					
						clear.setAtmNum(arr[0]);
						clear.setBoxNum(arr[1]);
						clear.setState(arr[2]);
						list.add(clear);	
						}
								
					
				}
				
			//单条记录的时候
			}else{	
				array = params.split(";");
				if(array[1].indexOf("|")>0){		
					String arr[] = array[1].split("\\|");								
					for (int k = 0; k < arr.length; k++) {
						clear = new ClearMoney();
						clear.setAtmNum(array[0]);
						clear.setBoxNum(arr[k]);
						
						clear.setState(array[2]);
						list.add(clear);
						
					}
					
				}else{
				
					clear = new ClearMoney();
					clear.setAtmNum(array[0]);
					clear.setBoxNum(array[1]);
					clear.setState(array[2]);
					list.add(clear);	
				}

			}			 			
		}
		return list;
	}
}
