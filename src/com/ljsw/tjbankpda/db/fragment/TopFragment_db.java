package com.ljsw.tjbankpda.db.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pda.R;
import com.ljsw.tjbankpda.db.application.o_Application;

public class TopFragment_db extends Fragment {
	private View view;
	private TextView loginleft, loginright;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.top_fragment, null);
		loginleft = (TextView) view.findViewById(R.id.fragment_tv1);
		loginright = (TextView) view.findViewById(R.id.fragment_tv2);
		if (!o_Application.fragmentleft.equals("")) {
			loginleft.setText(o_Application.fragmentleft);
		}
		if (!o_Application.fragmentright.equals("")) {
			loginright.setText(o_Application.fragmentright);
		}
		return view;
	}
	

}
