package com.example.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.application.GApplication;
import com.example.pda.R;


public class KuanXiangMingXiActivity extends Activity implements
		OnClickListener {
	private Button back;
	private ListView listview;
	Thread t1, t2;
	Intent intent;
	Bundle bundle;
	// 创建早出信息集合
	// 创建晚入信息集合

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kuanxiangmingxi);
		initView();
	//	turngaodu(listview);
	}

	/**
	 * 初始化控件
	 */
	public void initView() {
		listview = (ListView) this.findViewById(R.id.mingxiView);
		listview.setDivider(null);
	//	findViewById(R.id.mingxi_back).setOnClickListener(this);
		listview.setAdapter(new ZaochuAdapter());
		// listview.setAdapter(new WanruAdapter());
	}

	public void turngaodu(ListView lv) {
		// 获取listView对应的Adapter
		ListAdapter adapter = lv.getAdapter();
		// 返回数据项的数目
		int hangshu = adapter.getCount();
		int totalhanggao = 0;
		for (int i = 0; i < hangshu; i++) {
			View v = adapter.getView(i, null, lv);
			/*
			 * 在没有构建View之前无法取得View的宽度。选定measure 调用getMeasuredHeight()的方法
			 */
			v.measure(View.MeasureSpec.UNSPECIFIED,
					View.MeasureSpec.UNSPECIFIED);
			totalhanggao += v.getMeasuredHeight();
		}
		double fenggefu = lv.getDividerHeight() * (hangshu - 1);// 得到分隔符的高度
		totalhanggao += fenggefu;// 得到整个高度
		LayoutParams lp = lv.getLayoutParams();
		lp.height = totalhanggao;// 设置总高度
		lv.setLayoutParams(lp);
	}

	/**
	 * ZaochuAdapter 款箱早出数据配置
	 */
	private class ZaochuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return GApplication.mingxi_list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return GApplication.mingxi_list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			KongjianEntity entity;
			if (arg1 == null) {
				entity = new KongjianEntity();
				arg1 = LayoutInflater.from(KuanXiangMingXiActivity.this)
						.inflate(R.layout.s_item_kuangxiangmingxi, null);
				entity.textView1 = (TextView) arg1.findViewById(R.id.netName3);
				entity.textView2 = (TextView) arg1.findViewById(R.id.boxNum3);

				arg1.setTag(entity);
			} else {
				entity = (KongjianEntity) arg1.getTag();
			}
			entity.textView1.setText(GApplication.mingxi_list.get(arg0).getNetName());
			entity.textView2.setText(GApplication.mingxi_list.get(arg0).getBoxIds() + "");

			return arg1;
		}

	}

	/**
	 * 款箱晚入数据配置
	 * 
	 */
	private class WanruAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return GApplication.mingxi_list2.size();
		}

		@Override
		public Object getItem(int arg0) {
			return GApplication.mingxi_list2.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			KongjianEntity entity;
			if (arg1 == null) {
				entity = new KongjianEntity();
				arg1 = LayoutInflater.from(KuanXiangMingXiActivity.this)
						.inflate(R.layout.s_item_kuangxiangmingxi, null);
				entity.textView1 = (TextView) arg1.findViewById(R.id.netName3);
				entity.textView2 = (TextView) arg1.findViewById(R.id.boxNum3);

				arg1.setTag(entity);
			} else {
				entity = (KongjianEntity) arg1.getTag();
			}
			entity.textView1.setText(GApplication.mingxi_list2.get(arg0).getNetName());
			entity.textView2.setText(GApplication.mingxi_list2.get(arg0).getBoxIds() + "");

			return arg1;
		}

	}

	private class KongjianEntity {
		public TextView textView1;
		public TextView textView2;
	}

	@Override
	public void onClick(View v) {
		/*switch (v.getId()) {
		case R.id.mingxi_back:
			KuanXiangMingXiActivity.this.finish();
			break;
		default:
			break;
		}*/

	}

}
