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

public class ShangJiaoChuKuMingXi_db extends FragmentActivity implements
		OnClickListener {
	private TextView danhao, shuliang;
	private Button saomiao;
	// private Button update;
	private ImageView back;
	private ListView listview;
	private int countsum = 0;
	private ShangJiaoChuKuAdapter adapter;
	private ManagerClass manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shangjiaochuku_mingxi);
		getCountSum();
		load();
		adapter = new ShangJiaoChuKuAdapter();
		manager = new ManagerClass();

	}

	@Override
	protected void onResume() {
		super.onResume();
		listview.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		new TurnListviewHeight(listview);
	}

	public void load() {
		danhao = (TextView) findViewById(R.id.sj_jihuadan_danhao);
		danhao.setText(o_Application.chukumingxi.getZongxianlu());
		shuliang = (TextView) findViewById(R.id.sj_jihuadan_count);
		shuliang.setText(countsum + "");
		// update=(Button)findViewById(R.id.qf_jihuadan_update);
		back = (ImageView) findViewById(R.id.sj_jihuadan_back);
		back.setOnClickListener(this);
		listview = (ListView) findViewById(R.id.sj_jihua_listview);
		saomiao = (Button) findViewById(R.id.sj_jihuadan_dizhisaomiao);
		saomiao.setOnClickListener(this);
	}

	class ShangJiaoChuKuAdapter extends BaseAdapter {
		LayoutInflater lf = LayoutInflater.from(ShangJiaoChuKuMingXi_db.this);
		ViewHodler view;

		@Override
		public int getCount() {
			return o_Application.chukumingxi.getZhixianlu().size();
		}

		@Override
		public Object getItem(int arg0) {
			return o_Application.chukumingxi.getZhixianlu().get(arg0);
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
			view.renwu.setText(o_Application.chukumingxi.getZhixianlu().get(
					arg0));
			view.count.setText(o_Application.chukumingxi.getZzxshuliang().get(
					arg0));
			return arg1;
		}

	}

	public static class ViewHodler {
		TextView renwu, count;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.sj_jihuadan_back:
			ShangJiaoChuKuMingXi_db.this.finish();
			break;
		case R.id.sj_jihuadan_dizhisaomiao:
			manager.getRuning().runding(this, "扫描功能开启中...");
			Skip.skip(this, ShangJiaoChuKuSaoMiao_db.class, null, 0);
			break;
		default:
			break;
		}
	}

	public void getCountSum() {
		if (o_Application.chukumingxi.getZzxshuliang().size() > 0) {
			for (int i = 0; i < o_Application.chukumingxi.getZzxshuliang().size(); i++) {
				countsum += Integer.parseInt(o_Application.chukumingxi.getZzxshuliang().get(i).trim());
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		manager.getRuning().remove();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ShangJiaoChuKuMingXi_db.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
