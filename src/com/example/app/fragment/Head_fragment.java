package com.example.app.fragment;

import com.application.GApplication;
import com.example.pda.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 *  页面头部 fragment
 * @author yuyunheng
 */
public class Head_fragment extends Fragment{
	private TextView name;
	private TextView address;
	private View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.fragment_head,null);
		load();
		return view;
	}
	public void load(){
		name=(TextView)view.findViewById(R.id.head_name);
		address = (TextView)view.findViewById(R.id.head_address);
		if(!"".equals(GApplication.loginUsername)||GApplication.loginUsername!=null)
			name.setText("欢迎你："+GApplication.loginUsername);
		if(!"".equals(GApplication.organizationName)||GApplication.organizationName!=null)
			address.setText(GApplication.organizationName);
	}
}
