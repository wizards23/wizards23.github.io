package com.ljsw.tjbankpda.db.activity;

import java.util.ArrayList;
import java.util.List;

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
import com.ljsw.tjbankpda.util.TurnListviewHeight;

public class QingLingChuKuPeiSong_db extends FragmentActivity implements OnClickListener{
	private ImageView back;
	private Button update,chuku,quxiao;
	private ListView leftlist,rightlist;
	List<String> listleft = new ArrayList<String>();
	List<String> listright = new ArrayList<String>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_peisongsaomiao);
		load();
		getLeft();
		getRight();
		
		
		LeftAdapter ladapter=new LeftAdapter();
		leftlist.setAdapter(ladapter);
		new TurnListviewHeight(leftlist);
		RightAdapter radapter=new RightAdapter();
		rightlist.setAdapter(radapter);
		new TurnListviewHeight(rightlist);
	}
	public void load(){
		back=(ImageView)findViewById(R.id.peisong_saomiao_back);
		back.setOnClickListener(this);
		update=(Button)findViewById(R.id.peisong_update);
		update.setOnClickListener(this);
		quxiao=(Button)findViewById(R.id.peisong_saomiao_quxiao);
		quxiao.setOnClickListener(this);
		chuku=(Button)findViewById(R.id.peisong_saomiao_chuku);
		chuku.setOnClickListener(this);
		leftlist=(ListView)findViewById(R.id.peisong_saomiao_list_left);
		rightlist=(ListView)findViewById(R.id.peisong_saomiao_list_right);
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.peisong_back:
			QingLingChuKuPeiSong_db.this.finish();
			break;

		default:
			break;
		}
	}
	class LeftAdapter extends BaseAdapter {
		LeftHolder lh;
		LayoutInflater lf = LayoutInflater
				.from(QingLingChuKuPeiSong_db.this);

		@Override
		public int getCount() {
			return listleft.size();
		}

		@Override
		public Object getItem(int arg0) {
			return listleft.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				lh = new LeftHolder();
				arg1 = lf.inflate(R.layout.adapter_dizhisaomiao_left, null);
				lh.tv = (TextView) arg1
						.findViewById(R.id.adapter_dizhisaomiao_left_text);
				arg1.setTag(lh);
			} else {
				lh = (LeftHolder) arg1.getTag();
			}
			lh.tv.setText(listleft.get(arg0));
			return arg1;
		}

	}

	public static class LeftHolder {
		TextView tv;
	}

	class RightAdapter extends BaseAdapter {
		RightHolder rh;
		LayoutInflater lf = LayoutInflater
				.from(QingLingChuKuPeiSong_db.this);

		@Override
		public int getCount() {
			return listright.size();
		}

		@Override
		public Object getItem(int arg0) {
			return listright.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				rh = new RightHolder();
				arg1 = lf.inflate(R.layout.adapter_dizhisaomiao_right, null);
				rh.tv = (TextView) arg1
						.findViewById(R.id.adapter_dizhisaomiao_right_text);
				arg1.setTag(rh);
			} else {
				rh = (RightHolder) arg1.getTag();
			}
			rh.tv.setText(listright.get(arg0));
			return arg1;
		}

	}

	public static class RightHolder {
		TextView tv;
	}

	public void getLeft() {
		for (int i = 0; i < 18; i++) {
			listleft.add("BC00001230" + i);
		}
	}

	public void getRight() {
		for (int i = 0; i < 2; i++) {
			listright.add("BC01003210" + i);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			QingLingChuKuPeiSong_db.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
