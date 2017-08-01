package com.ljsw.tjbankpda.db.activity;

import hdjc.rfid.operator.RFID_Device;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.ljsw.tjbankpda.db.biz.QingLingChuRuKuGetNumber;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.manager.classs.pad.ManagerClass;

@SuppressLint("HandlerLeak")
public class PeiSongSaoMiao_db extends FragmentActivity implements
		OnClickListener {
	private ImageView back;
	private TextView topleft, topright;
	private Button chuku, quxiao;
	private ListView listleft, listright;
	List<String> copylist = new ArrayList<String>();
	List<String> rightlist = new ArrayList<String>();
	private LeftAdapter ladapter;
	private RightAdapter radapter;
	private TextView wrong;
	private ManagerClass manager;
	private QingLingChuRuKuGetNumber getnumber;
	private RFID_Device rfid;

	private RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_peisongsaomiao);
		// getRight();
		load();
		getnumber = new QingLingChuRuKuGetNumber();
		getnumber.setHandler(handler);
		copylist.addAll(o_Application.qinglingchuruku.getKuxiang());
		ladapter = new LeftAdapter();
		radapter = new RightAdapter();
		manager = new ManagerClass();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getRfid().addNotifly(getnumber);
		new Thread() {

			@Override
			public void run() {
				super.run();
				getRfid().open_a20();
			}

		}.start();
		o_Application.guolv.clear();
		o_Application.numberlist.clear();
		o_Application.qinglingchuruku.getKuxiang().clear();
		o_Application.qinglingchuruku.getKuxiang().addAll(copylist);
		topright.setText("" + o_Application.numberlist.size());
		topleft.setText("" + o_Application.qinglingchuruku.getKuxiang().size());
		listleft.setAdapter(ladapter);
		// new TurnListviewHeight(listleft);
		listright.setAdapter(radapter);
		// new TurnListviewHeight(listright);

		if (o_Application.qinglingchuruku.getKuxiang().size() > 0) {
			chuku.setEnabled(false);
			chuku.setBackgroundResource(R.drawable.button_gray);
		}
		if (o_Application.numberlist.size() == 0) {
			quxiao.setEnabled(false);
			quxiao.setBackgroundResource(R.drawable.button_gray);
		}

	}

	/**
	 * 无限刷新集合
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (o_Application.qinglingchuruku.getKuxiang().size() == 0) {
					chuku.setEnabled(true);
					chuku.setBackgroundResource(R.drawable.buttom_selector_bg);
				}
				if (o_Application.numberlist.size() > 0) {
					quxiao.setEnabled(true);
					quxiao.setBackgroundResource(R.drawable.buttom_selector_bg);
				}
				// wrong.setText(o_Application.wrong);
				topright.setText("" + o_Application.numberlist.size());
				topleft.setText(""
						+ o_Application.qinglingchuruku.getKuxiang().size());
				ladapter.notifyDataSetChanged();
				radapter.notifyDataSetChanged();
				listleft.setAdapter(ladapter);
				// new TurnListviewHeight(listleft);
				listright.setAdapter(radapter);
				// new TurnListviewHeight(listright);
				break;
			default:
				break;
			}

		}

	};

	@Override
	protected void onPause() {
		super.onPause();
		manager.getRuning().remove();
		o_Application.wrong = "";
		// bool = false;
		getRfid().close_a20();
	}

	public void load() {
		back = (ImageView) findViewById(R.id.peisong_saomiao_back);
		back.setOnClickListener(this);
		topleft = (TextView) findViewById(R.id.peisong_saomiao_left_text);
		topright = (TextView) findViewById(R.id.peisong_saomiao_right_text);
		chuku = (Button) findViewById(R.id.peisong_saomiao_chuku);
		chuku.setOnClickListener(this);
		quxiao = (Button) findViewById(R.id.peisong_saomiao_quxiao);
		quxiao.setOnClickListener(this);
		listleft = (ListView) findViewById(R.id.peisong_saomiao_list_left);
		listright = (ListView) findViewById(R.id.peisong_saomiao_list_right);
		wrong = (TextView) findViewById(R.id.peisong_tishi_cuowu);
		listleft.setDividerHeight(0);
		listright.setDividerHeight(0);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.peisong_saomiao_back:
			PeiSongSaoMiao_db.this.finish();
			break;
		case R.id.peisong_saomiao_chuku:
			// 出库跳转
			manager.getRuning().runding(this, "指纹扫描开启中...");
			Skip.skip(this, PeiSongJiaoJie_db.class, null, 0);
			break;

		case R.id.peisong_saomiao_quxiao:
			// 清除集合 重新扫描
			o_Application.numberlist.clear();
			o_Application.guolv.clear();
			o_Application.qinglingchuruku.getKuxiang().clear();
			o_Application.qinglingchuruku.getKuxiang().addAll(copylist);
			if (o_Application.qinglingchuruku.getKuxiang().size() > 0) {
				chuku.setEnabled(false);
				chuku.setBackgroundResource(R.drawable.button_gray);
			}
			if (o_Application.numberlist.size() == 0) {
				quxiao.setEnabled(false);
				quxiao.setBackgroundResource(R.drawable.button_gray);
			}
			handler.sendEmptyMessage(0);
			break;
		default:
			break;
		}

	}

	class LeftAdapter extends BaseAdapter {
		LeftHolder lh;
		LayoutInflater lf = LayoutInflater.from(PeiSongSaoMiao_db.this);

		@Override
		public int getCount() {
			return o_Application.qinglingchuruku.getKuxiang().size();
		}

		@Override
		public Object getItem(int arg0) {
			return o_Application.qinglingchuruku.getKuxiang().get(arg0);
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
			lh.tv.setText(o_Application.qinglingchuruku.getKuxiang().get(arg0));
			return arg1;
		}

	}

	public static class LeftHolder {
		TextView tv;
	}

	class RightAdapter extends BaseAdapter {
		RightHolder rh;
		LayoutInflater lf = LayoutInflater.from(PeiSongSaoMiao_db.this);

		@Override
		public int getCount() {
			return o_Application.numberlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			return o_Application.numberlist.get(arg0);
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
			rh.tv.setText(o_Application.numberlist.get(arg0));
			return arg1;
		}

	}

	public static class RightHolder {
		TextView tv;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		manager.getAbnormal().remove();
		getRfid().close_a20();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			PeiSongSaoMiao_db.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
