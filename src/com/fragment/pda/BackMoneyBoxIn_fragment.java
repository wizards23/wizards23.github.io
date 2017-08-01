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

//回收钞箱
public class BackMoneyBoxIn_fragment extends Fragment {
	
	TextView planNum;
	TextView planway;
	TextView boxNum;
	TextView date;
	Bundle bundle;
	
	
	public GetPlanWayListBiz planwaylist;		
    GetPlanWayListBiz getPlanwaylist() {
    	if(planwaylist==null){
    	   planwaylist = new GetPlanWayListBiz();
    	}
		return planwaylist;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.information_out_fragment,null);
	}

	@Override
	public void onStart() {
		super.onStart();
		
		planNum = (TextView) getActivity().findViewById(R.id.backout_planNum);
		planway = (TextView) getActivity().findViewById(R.id.backout_planway);
		boxNum = (TextView) getActivity().findViewById(R.id.backout_boxnum);
		date = (TextView) getActivity().findViewById(R.id.backout_date);
		Log.i("回收钞箱入库", "回收钞箱入库");
		bundle = getActivity().getIntent().getExtras();
		if(bundle!=null){
			String plannum = bundle.getString("number");
			Log.i("plannum明细", plannum);
			
			for (int i = 0; i < getPlanwaylist().list_box.size(); i++) {
				if(plannum.equals(getPlanwaylist().list_box.get(i).getPlanNum())){
					Log.i("planNum", getPlanwaylist().list_box.get(i).getPlanNum());
					planNum.setText(getPlanwaylist().list_box.get(i).getPlanNum());
					planway.setText(getPlanwaylist().list_box.get(i).getWay());					
					boxNum.setText(getPlanwaylist().list_box.get(i).getBoxNum());
					date.setText(getPlanwaylist().list_box.get(i).getDate());

					break;
				}
			}
		}
		
	}
	
	
	
	
}
