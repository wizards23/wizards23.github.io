package com.example.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.pda.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ZhiWenCaiJiActivity extends Activity{
	private Spinner sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zhiwencaiji);
		load();
		sp.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getData()));
	}
	public void load(){
		sp=(Spinner)findViewById(R.id.zhiwen_spinner);
	}
	/**
	 * Spinner 数据
	 */
	public List<String> getData(){
		List<String> ls=new ArrayList<String>();
		ls.add("左手大拇指");
		ls.add("左手食指");
		ls.add("左手中指");
		ls.add("左手无名指");
		ls.add("左手小拇指");
		ls.add("右手大拇指");
		ls.add("右手食指");
		ls.add("右手中指");
		ls.add("右手无名指");
		ls.add("右手小拇指");
		return ls;
	}
}
