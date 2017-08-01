package com.ljsw.tjbankpda.yy.activity;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.app.util.Skip;
import com.example.pda.R;
import com.ljsw.tjbankpda.qf.entity.Qingfendan;
import com.ljsw.tjbankpda.yy.application.S_application;
import com.manager.classs.pad.ManagerClass;

/**
 * 计划单列表 页面(清分)
 * @author Administrator
 */
public class QingfenJhdActivity extends FragmentActivity implements OnItemClickListener,OnClickListener{
	private ListView listView;
	private List<Qingfendan> list = new ArrayList<Qingfendan>();
	
	private ManagerClass managerClass;
	
	
//	int aa;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qingfen_jihuadan_s);
		managerClass = new ManagerClass();
		listView = (ListView) this.findViewById(R.id.qfjhd_listView);
		
		
		
	}
	/**
	 * 初始化控件
	 */
	public void initView(){	
		listView.setAdapter(new LiebiaoAdapter());
		listView.setOnItemClickListener(this);
		findViewById(R.id.qingfen_backS1);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getData();
		initView();
	}
	
	/**
	 * 获取数据
	 */
	public void getData(){
		list = new ArrayList<Qingfendan>();
		for (int i = 0; i < S_application.getApplication().qfjhdlist.size(); i++) {
			Qingfendan qfd = new Qingfendan(S_application.getApplication().qfjhdlist.get(i),
					S_application.getApplication().qfrqlist.get(i));
			list.add(qfd);
		}
	}
	
	
	/**
	 * 任务列表自定义样式
	 * @author Administrator
	 */
	class LiebiaoAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return list.size();
		}

		@Override
		public Object getItem(int arg0) {

			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {

			return arg0;
		}
		
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			KongjianEntity entity;
			if (arg1 == null) {
				entity = new KongjianEntity();
				arg1 = LayoutInflater.from(QingfenJhdActivity.this).inflate(
						R.layout.item_qingfen_jhd_s, null);
				entity.textView1 = (TextView) arg1.findViewById(R.id.jihdHao);
				entity.textView2 = (TextView) arg1.findViewById(R.id.jihdDate);
				
				arg1.setTag(entity);
			} else {
				entity = (KongjianEntity) arg1.getTag();
				//resetViewHolder(entity);
			}
			entity.textView1.setText(list.get(arg0).getQfNum());
			entity.textView2.setText(list.get(arg0).getQfDate());
			return arg1;			
		}

	}

	/*
	 * listView 控件实体类
	 */
	static class KongjianEntity {
		public  TextView textView1;
		public  TextView textView2;			
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int checkId, long arg3) {
		//跳转计划单明细页面 启动线程
		S_application.getApplication().jhdId = list.get(checkId).getQfNum();
		System.out.println("我是计划单号:"+S_application.getApplication().jhdId);
		Skip.skip(QingfenJhdActivity.this,QingfenJhdMxActivity.class, null, 0);
	}

	/**
	 * 单击事件
	 */
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.qingfen_backS2://返回
			QingfenJhdActivity.this.finish();
			break;

		default:
			break;
		}
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		managerClass.getRuning().remove();
		super.onPause();
		
	}
	
	/*public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub					
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}*/
	
	
}
