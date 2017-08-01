package com.fragment.pda;

import com.application.GApplication;
import com.example.pda.R;
import com.loginsystem.biz.SystemLoginBiz;
import com.moneyboxadmin.pda.BankDoublePersonLogin;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HeadUser_fragment extends Fragment {
	
	TextView name1;
	TextView name2;
	
	
	private SystemLoginBiz systemLogin;		
	public SystemLoginBiz getSystemLogin() {
		return systemLogin==null?new SystemLoginBiz():systemLogin;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.head_user,null);
	}

	@Override
	public void onStart() {
		super.onStart();
		name1 = (TextView) getActivity().findViewById(R.id.head_user1);
		name2 = (TextView) getActivity().findViewById(R.id.head_user2);
		
		if(GApplication.getApplication().user.getLoginUserId().equals("8")){
			name1.setText("押运员："+GApplication.getApplication().user.getLoginUserName());
			name2.setText("");
		}else{
			name1.setText(BankDoublePersonLogin.textname1);
			name2.setText(BankDoublePersonLogin.textname2);
		}
		
		
		
		
	}
	
	
}
