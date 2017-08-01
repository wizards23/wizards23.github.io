package com.fragment.pda;

import com.application.GApplication;
import com.example.pda.R;
import com.loginsystem.biz.SystemLoginBiz;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Head_fragment extends Fragment {
	
	TextView name;
	TextView address;
	
	
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
		return inflater.inflate(R.layout.head,null);
	}

	@Override
	public void onStart() {
		super.onStart();
		name = (TextView) getActivity().findViewById(R.id.head_name);
		address = (TextView) getActivity().findViewById(R.id.head_address);
		if(GApplication.user!=null){			
			name.setText("欢迎你:"+GApplication.user.getLoginUserName());
			address.setText(GApplication.user.getOrganizationName());	
		}
		
	}
	
	
}
