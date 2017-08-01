package com.fragment.pda;

import com.clearadmin.pda.BackMoneyBoxCount;
import com.entity.BoxDetail;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.Abnormal;
import com.messagebox.Runing;
import com.moneyboxadmin.biz.GetBoxDetailListBiz;
import com.moneyboxadmin.pda.MoneyBoxDetial;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MoneyAndBoxNum_fragment extends Fragment{

	
	
	private GetBoxDetailListBiz getBoxDetailList;		
	public GetBoxDetailListBiz getGetBoxDetailList() {
		return getBoxDetailList=getBoxDetailList==null ?new GetBoxDetailListBiz():getBoxDetailList;
	}
	
	ListView listview;
	String planNum;  //计划编号
	String bizName;  //业务名称
	LinearLayout notdata;   //无数据时提示
	OnClickListener clickreplace;
	Button btn_notdata;
	TextView boxNum;  //钞箱编号或数量

	Ad ad;
	private ManagerClass managerClass;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		managerClass = new ManagerClass();
		//重试单击事件
				clickreplace = new OnClickListener() {			
					@Override
					public void onClick(View arg0) {
						managerClass.getAbnormal().remove();
						managerClass.getRuning().runding(getActivity(), "正在获取钞箱明细...");						
						//连接超时后确定再次请求数据
						getGetBoxDetailList().getBoxDetailList(planNum,bizName);						
						
					}
				};
				
				
				   planNum = getArguments().getString("number");
				   bizName = getArguments().getString("businName");
				   
				   Log.i("bizName111111111111",bizName);
				   Log.i("planNum111111111111",planNum);
				   
					//hand通知操作
					getGetBoxDetailList().hand_detail = new Handler(){
						@Override
						public void handleMessage(Message msg) {
							//移除获取钞箱明细提示
							managerClass.getRuning().remove();
							super.handleMessage(msg);
							if(msg.what==1){   //有数据
							notdata.setVisibility(View.GONE);
							if(ad==null){
								ad = new Ad();
								listview.setAdapter(ad);	
							}else{
								ad.notifyDataSetChanged();
							}
															
							}else if(msg.what==0){ //没数据
								
							notdata.setVisibility(View.VISIBLE);	
							 
							}else if(msg.what==-1){  //连接超时
							notdata.setVisibility(View.GONE);
							managerClass.getAbnormal().timeout(getActivity(),"连接超时，要重试吗？", clickreplace);	
							}
						}			
					};
					Log.i("planNum", planNum);
					
		
				
		return inflater.inflate(R.layout.money_and_boxnum_fragment, null);
	}
	

	@Override
	public void onStart() {
		super.onStart();
		listview = (ListView)getActivity().findViewById(R.id.listview_boxdetial);
		notdata = (LinearLayout) getActivity().findViewById(R.id.layout_notdata);
		boxNum = (TextView) getActivity().findViewById(R.id.boxnum_detail);
		
		if("空钞箱出库".equals(bizName)){
			boxNum.setText("钞箱数量");
		}else{
			boxNum.setText("钞箱编号");
		}
		
		if(ad==null){
			//提示获取钞箱明细
			 managerClass.getRuning().runding(getActivity(),"正在获取钞箱明细...");
			//开始请求数据			 
			getGetBoxDetailList().getBoxDetailList(planNum,bizName);	
		}else{
			ad.notifyDataSetChanged();
		}
						
		
		
	}
	
	
	
	
	
	//适配器
		class Ad extends BaseAdapter{
			
			@Override
			public int getCount() {
				return getGetBoxDetailList().list.size();
			}

			@Override
			public Object getItem(int arg0) {
				return getGetBoxDetailList().list.get(arg0);
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) {
				View v =arg1;
				ViewHolder holder = new ViewHolder();
				if(v==null){
					v = GolbalView.getLF(getActivity()).inflate(R.layout.boxinformation_item, null);
					holder.brand = (TextView) v.findViewById(R.id.brand);
					holder.num = (TextView) v.findViewById(R.id.box_num);
					
					v.setTag(holder);
				}else{
					holder = (ViewHolder) v.getTag();	
				}			 		
				BoxDetail box = (BoxDetail) getItem(arg0);
										
				holder.brand.setText(box.getBrand());
				holder.num.setText(box.getNum());
							
				return v;
			}
			
			
		}
		
		static class ViewHolder{
			TextView brand;
			TextView num;
		}

		
		
		
		
		
		

}
