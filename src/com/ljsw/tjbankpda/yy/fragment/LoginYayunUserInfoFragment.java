package com.ljsw.tjbankpda.yy.fragment;

import com.application.GApplication;
import com.example.pda.R;
import com.ljsw.tjbankpda.qf.application.Mapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LoginYayunUserInfoFragment extends Fragment{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fg_yayundenglu_userinfo, null);
		if(GApplication.use!=null){
			TextView tvUser1Name=(TextView)view.findViewById(R.id.tv_fg_yayundenglu_user1);
			tvUser1Name.setText(GApplication.use.getUsername());
		}
		return view;
	}
}
