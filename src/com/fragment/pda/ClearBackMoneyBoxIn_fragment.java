package com.fragment.pda;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.pda.R;
import com.moneyboxadmin.biz.GetEmptyRecycleCashboxInListBiz;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//已清回收钞箱
public class ClearBackMoneyBoxIn_fragment extends Fragment {
	
	TextView bizNum;    //业务单号
	TextView date;		//日期
	TextView hours;    //日期数精确到小时
	TextView boxnum;   //箱子数量
	String biznumber;   //业务单号值
	TextView clear_date;  //入库时间
	
	private GetEmptyRecycleCashboxInListBiz emptyRecycleCashboxInList;
		
	public GetEmptyRecycleCashboxInListBiz getEmptyRecycleCashboxInList() {
		return emptyRecycleCashboxInList;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		biznumber = getArguments().getString("number");
		return inflater.inflate(R.layout.clearbusin_fragment,null);
	}

	
	
	@Override
	public void onStart() {
		super.onStart();
		
		bizNum = (TextView) getActivity().findViewById(R.id.clear_biz);
		date = (TextView) getActivity().findViewById(R.id.clear_date);
		hours = (TextView) getActivity().findViewById(R.id.clear_hour);
		boxnum = (TextView) getActivity().findViewById(R.id.clear_boxnum);
		clear_date = (TextView) getActivity().findViewById(R.id.clear_date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
		String dates = sdf.format(new Date()); 
		Log.i("biznumber", biznumber);
		for (int i = 0; i <getEmptyRecycleCashboxInList().list.size(); i++) {
			if(biznumber.equals(getEmptyRecycleCashboxInList().list.get(i).getBizNum())){
				bizNum.setText(getEmptyRecycleCashboxInList().list.get(i).getBizNum());
				String[] date2 = getEmptyRecycleCashboxInList().list.get(i).getClearDate().split(" ");
				date.setText(date2[0]);
				hours.setText(getEmptyRecycleCashboxInList().list.get(i).getClearDate());
				boxnum.setText(getEmptyRecycleCashboxInList().list.get(i).getBoxNum());
				clear_date.setText(dates);
			}
		}
		
		
	}
	
	
	
	
}
