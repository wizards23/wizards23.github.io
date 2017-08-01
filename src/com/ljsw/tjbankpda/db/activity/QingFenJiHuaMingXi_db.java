package com.ljsw.tjbankpda.db.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pda.R;
import com.ljsw.tjbankpda.db.application.o_Application;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.manager.classs.pad.ManagerClass;

public class QingFenJiHuaMingXi_db extends FragmentActivity implements
		OnClickListener {
	private TextView danhao, shuliang;
	private Button saomiao;
	private ImageView back;
	private ListView listview;

	private QingFenAdapter adapter;
	private ManagerClass manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_qingfenjihuamingxi);
		load();
		adapter = new QingFenAdapter();
		manager = new ManagerClass();
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
		listview.setAdapter(adapter);
		new TurnListviewHeight(listview);
	}


	public void load() {
		danhao = (TextView) findViewById(R.id.qf_jihuadan_danhao);
		danhao.setText(o_Application.qingfendanmingxi.getJihuadan());
		shuliang = (TextView) findViewById(R.id.qf_jihuadan_count);
		shuliang.setText(o_Application.qingfendanmingxi.getZzxcount()+"");
		// update=(Button)findViewById(R.id.qf_jihuadan_update);
		back = (ImageView) findViewById(R.id.qf_jihuadan_back);
		back.setOnClickListener(this);
		listview = (ListView) findViewById(R.id.qf_jihua_listview);
		listview.setDividerHeight(0);
		saomiao = (Button) findViewById(R.id.qf_jihuadan_dizhisaomiao);
		saomiao.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.qf_jihuadan_back:
			QingFenJiHuaMingXi_db.this.finish();
			break;
		case R.id.qf_jihuadan_dizhisaomiao:
			manager.getRuning().runding(this, "正在开启扫描,请稍后...");
			Skip.skip(this, ShangJiaoQingFenSaoMiao_db.class, null, 0);
			break;
		default:
			break;
		}

	}

	class QingFenAdapter extends BaseAdapter {
		LayoutInflater lf = LayoutInflater.from(QingFenJiHuaMingXi_db.this);
		ViewHodler view;

		@Override
		public int getCount() {
			return o_Application.qingfendanmingxi.getXianluming().size();
		}

		@Override
		public Object getItem(int arg0) {
			return o_Application.qingfendanmingxi.getXianluming().get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				arg1 = lf.inflate(R.layout.adapter_jihuadan_zhongkong, null);
				view = new ViewHodler();
				view.renwu = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_zhongkong_type);
				view.count = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_zhongkong_count);
				arg1.setTag(view);
			} else {
				view = (ViewHodler) arg1.getTag();
			}
			view.renwu.setText(o_Application.qingfendanmingxi.getXianluming().get(arg0));
			view.count.setText(o_Application.qingfendanmingxi.getCount().get(arg0));
			return arg1;
		}

	}

	public static class ViewHodler {
		TextView renwu, count;
	}

	/*public void getData() {
		for (int i = 0; i < 10; i++) {
			list.add(new JiHuaDan_Zhongkong("西青" + i + "线", "3" + i));
		}
	}*/

	@Override
	protected void onPause() {
		super.onPause();
		manager.getRuning().remove();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			QingFenJiHuaMingXi_db.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
