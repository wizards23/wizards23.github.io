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
import com.ljsw.tjbankpda.db.biz.ShangJiaoQingFenSaoMiao;
import com.ljsw.tjbankpda.util.Skip;
import com.manager.classs.pad.ManagerClass;

@SuppressLint("HandlerLeak")
public class ShangJiaoQingFenSaoMiao_db extends FragmentActivity implements
		OnClickListener {
	private ImageView back;
	private TextView topleft, topright;
	private Button chuku, quxiao;
	private ListView listleft, listright;
	private TextView wrong;
	List<String> copylist = new ArrayList<String>();
	private LeftAdapter ladapter;
	private RightAdapter radapter;
	private ShangJiaoQingFenSaoMiao getnumber;
	private ManagerClass manager;
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
		setContentView(R.layout.activity_shangjiaoqinfensaomiao);
		load();
		getnumber=new ShangJiaoQingFenSaoMiao();
		getnumber.setHandler(handler);
		copylist.addAll(o_Application.qingfendanmingxi.getZhouzhuanxiang());
		manager = new ManagerClass();
		ladapter = new LeftAdapter();
		radapter = new RightAdapter();
	}

	// o_Application.qingfendanmingxi
	@Override
	protected void onResume() {
		super.onResume();
		o_Application.guolv.clear();
		o_Application.numberlist.clear();
		o_Application.qingfendanmingxi.getZhouzhuanxiang().clear();
		o_Application.qingfendanmingxi.getZhouzhuanxiang().addAll(copylist);
		getRfid().addNotifly(getnumber);
		new Thread(){

			@Override
			public void run() {
				super.run();
				getRfid().open_a20();
			}
			
		}.start();
		topleft.setText(""
				+ o_Application.qingfendanmingxi.getZhouzhuanxiang().size());
		topright.setText("" + o_Application.numberlist.size());
		ladapter.notifyDataSetChanged();
		radapter.notifyDataSetChanged();
		listleft.setAdapter(ladapter);
		listleft.setDividerHeight(0);
	
		listright.setAdapter(radapter);
		listright.setDividerHeight(0);

		if (o_Application.qingfendanmingxi.getZhouzhuanxiang().size() > 0) {
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
				if (o_Application.qingfendanmingxi.getZhouzhuanxiang().size() == 0) {
					chuku.setEnabled(true);
					chuku.setBackgroundResource(R.drawable.buttom_selector_bg);
				}
				if (o_Application.numberlist.size() > 0) {
					quxiao.setEnabled(true);
					quxiao.setBackgroundResource(R.drawable.buttom_selector_bg);
				}
			//	wrong.setText(o_Application.wrong);
				topleft.setText(""
						+ o_Application.qingfendanmingxi.getZhouzhuanxiang()
								.size());
				ladapter.notifyDataSetChanged();
				listleft.setAdapter(ladapter);
			
				radapter.notifyDataSetChanged();
				topright.setText("" + o_Application.numberlist.size());
				listright.setAdapter(radapter);
	
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
		getRfid().close_a20();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(o_Application.qingfendanmingxi.getZhouzhuanxiang().size()>0){
			o_Application.qingfendanmingxi.getZhouzhuanxiang().clear();
			o_Application.qingfendanmingxi.getZhouzhuanxiang().addAll(copylist);
		}else{
			o_Application.qingfendanmingxi.getZhouzhuanxiang().addAll(copylist);
		}
		copylist.clear();
		if(o_Application.guolv.size()>0){
			o_Application.guolv.clear();
		}
		if(o_Application.numberlist.size()>0){
			o_Application.numberlist.clear();
		}
	}

	public void load() {
		back = (ImageView) findViewById(R.id.qfshangjiao_saomiao_back);
		back.setOnClickListener(this);
		topleft = (TextView) findViewById(R.id.qfmingxi_saomiao_left_text);
		topright = (TextView) findViewById(R.id.qfmingxi_saomiao_right_text);
		chuku = (Button) findViewById(R.id.qfmingxi_saomiao_chuku);
		chuku.setOnClickListener(this);
		quxiao = (Button) findViewById(R.id.qfmingxi_saomiao_quxiao);
		quxiao.setOnClickListener(this);
		topleft.setText(""+ o_Application.qingfendanmingxi.getZhouzhuanxiang().size());
		listleft = (ListView) findViewById(R.id.qfmingxi_saomiao_list_left);
		listright = (ListView) findViewById(R.id.qfmingxi_saomiao_list_right);
		wrong = (TextView) findViewById(R.id.shangjiaoqingfen_tishi_cuowu);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.qfshangjiao_saomiao_back:
			ShangJiaoQingFenSaoMiao_db.this.finish();
			break;
		case R.id.qfmingxi_saomiao_chuku:
			manager.getRuning().runding(this, "指纹扫描开启中...");
			Skip.skip(this, QingFenZhouZhuanJiaoJie_db.class, null, 0);
			break;

		case R.id.qfmingxi_saomiao_quxiao:
			// 清除集合 重新扫描
			wrong.setText("");
			o_Application.numberlist.clear();
			o_Application.qingfendanmingxi.getZhouzhuanxiang().clear();
			o_Application.guolv.clear();
			o_Application.qingfendanmingxi.getZhouzhuanxiang().addAll(copylist);
			if (o_Application.qingfendanmingxi.getZhouzhuanxiang().size() > 0) {
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
		LayoutInflater lf = LayoutInflater
				.from(ShangJiaoQingFenSaoMiao_db.this);

		@Override
		public int getCount() {
			return o_Application.qingfendanmingxi.getZhouzhuanxiang().size();
		}

		@Override
		public Object getItem(int arg0) {
			return o_Application.qingfendanmingxi.getZhouzhuanxiang().get(arg0);
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
			lh.tv.setText(o_Application.qingfendanmingxi.getZhouzhuanxiang()
					.get(arg0));
			return arg1;
		}

	}

	public static class LeftHolder {
		TextView tv;
	}

	class RightAdapter extends BaseAdapter {
		RightHolder rh;
		LayoutInflater lf = LayoutInflater
				.from(ShangJiaoQingFenSaoMiao_db.this);

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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			ShangJiaoQingFenSaoMiao_db.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
