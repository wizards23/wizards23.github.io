package com.fragment.pda;


import com.example.pda.R;
import com.moneyboxadmin.biz.GetPlanWayListBiz;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//出库内容显示
public class EmptyBoxOut_fragment extends Fragment {
	
	TextView plan;   //计划编号
	TextView way;	 //路线
	TextView type;	 //类型
	TextView money;	 //金额
	TextView boxNum; //箱子数量	
	TextView date;    //日期
	Bundle bundle;    
	String palnNum;   //计划编号值
	
	
	public GetPlanWayListBiz planwaylist;		
	public GetPlanWayListBiz getPlanwaylist() {
		return planwaylist=planwaylist==null?new GetPlanWayListBiz():planwaylist;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.information_in_fragment,null);
	}
				
	@Override
	public void onStart() {
		super.onStart();
		plan = (TextView) getActivity().findViewById(R.id.planf);
		way = (TextView) getActivity().findViewById(R.id.wayf);
		type = (TextView) getActivity().findViewById(R.id.typef);
		money = (TextView) getActivity().findViewById(R.id.moneyf);
		boxNum = (TextView) getActivity().findViewById(R.id.boxnumf);
		date = (TextView) getActivity().findViewById(R.id.datef);
		//接收当activity传递 进来的参数
		bundle = getActivity().getIntent().getExtras();
		if(bundle!=null){
			palnNum = bundle.getString("number");
			Log.i("palnNum", palnNum);
			Log.i("palnNum", getPlanwaylist().list_box+"");
			
			for (int i = 0; i < getPlanwaylist().list_box.size(); i++) {
				if(palnNum.equals(getPlanwaylist().list_box.get(i).getPlanNum())){
					plan.setText(getPlanwaylist().list_box.get(i).getPlanNum());
					way.setText(getPlanwaylist().list_box.get(i).getWay());
					money.setText(getPlanwaylist().list_box.get(i).getMoney()+"万");
					boxNum.setText(getPlanwaylist().list_box.get(i).getBoxNum());
					date.setText(getPlanwaylist().list_box.get(i).getDate());
					type.setText(getPlanwaylist().list_box.get(i).getType());
					break;
				}
			}
		}
		
	}

			
	
}
