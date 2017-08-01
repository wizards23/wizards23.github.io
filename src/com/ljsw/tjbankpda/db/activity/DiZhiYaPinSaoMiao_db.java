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
import com.ljsw.tjbankpda.db.biz.DiZhiYaPinGetNumber;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.manager.classs.pad.ManagerClass;

@SuppressLint("HandlerLeak")
public class DiZhiYaPinSaoMiao_db extends FragmentActivity implements
		OnClickListener {
	private ListView left;
	private ListView right;
	private Button chuku, quxiao;
	private TextView left_count;
	private TextView right_count;
	private TextView wrong;
	private ImageView back;
	List<String> copylist = new ArrayList<String>();
	LeftAdapter leftadapter;
	RightAdapter rightadapter;
	private ManagerClass manager;
	DiZhiYaPinGetNumber getNum;
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
		setContentView(R.layout.activity_dizhiyapinsaomiao);
		load();
		leftadapter = new LeftAdapter();
		rightadapter = new RightAdapter();
		manager = new ManagerClass();
		copylist.addAll(o_Application.jihuadan_list_dizhiyapin);
	}

	@Override
	protected void onResume() {
		super.onResume();
		o_Application.numberlist.clear();
		o_Application.guolv.clear();
		o_Application.jihuadan_list_dizhiyapin.clear();
		o_Application.jihuadan_list_dizhiyapin.addAll(copylist);
		getNum = new DiZhiYaPinGetNumber();
		getNum.setHandler(handler);
		getRfid().addNotifly(getNum);
		new Thread() {
			@Override
			public void run() {
				super.run();
				getRfid().scanOpen();
			}
		}.start();
		left_count.setText("" + o_Application.jihuadan_list_dizhiyapin.size());
		right_count.setText("" + o_Application.numberlist.size());
		left.setAdapter(leftadapter);
//		new TurnListviewHeight(left);
		right.setAdapter(rightadapter);
//		new TurnListviewHeight(right);
		if (o_Application.jihuadan_list_dizhiyapin.size() > 0) {
			chuku.setEnabled(false);
			chuku.setBackgroundResource(R.drawable.button_gray);
		}
		if (o_Application.numberlist.size() == 0) {
			quxiao.setEnabled(false);
			chuku.setBackgroundResource(R.drawable.button_gray);
		}
	}

	/**
	 * 无限刷新集合
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String number = msg.getData().getString("Num");
			int aa = -1;
			for (int i = 0; i < o_Application.jihuadan_list_dizhiyapin.size(); i++) {
				if (o_Application.jihuadan_list_dizhiyapin.get(i)
						.equals(number)) {
					aa = i;
					break;
				}
			}
			if (aa != -1) {
				o_Application.jihuadan_list_dizhiyapin.remove(aa);// 左侧集合删除
				o_Application.numberlist.add(number);// 右侧集合添加
			} else {
				o_Application.wrong = number + "-错误箱号";
			}
			switch (msg.what) {
			case 1:
				if (o_Application.jihuadan_list_dizhiyapin.size() == 0) {
					chuku.setEnabled(true);
					chuku.setBackgroundResource(R.drawable.buttom_selector_bg);
				}
				if (o_Application.numberlist.size() > 0) {
					quxiao.setEnabled(true);
					chuku.setBackgroundResource(R.drawable.buttom_selector_bg);
				}
				wrong.setText(o_Application.wrong);
				left_count.setText(""
						+ o_Application.jihuadan_list_dizhiyapin.size());
				leftadapter.notifyDataSetChanged();
				left.setAdapter(leftadapter);
//				new TurnListviewHeight(left);
				rightadapter.notifyDataSetChanged();
				right_count.setText("" + o_Application.numberlist.size());
				right.setAdapter(rightadapter);
//				new TurnListviewHeight(right);
				break;
			default:
				break;
			}

		}

	};

	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("上缴清分出库扫描已关闭");
		manager.getRuning().remove();
		o_Application.wrong = "";
		getRfid().close_a20();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (o_Application.jihuadan_list_dizhiyapin.size() != 0) {
			o_Application.jihuadan_list_dizhiyapin.clear();
			o_Application.jihuadan_list_dizhiyapin.addAll(copylist);
		} else {
			o_Application.jihuadan_list_dizhiyapin.addAll(copylist);
		}
		copylist.clear();
	}

	public void load() {
		left_count = (TextView) findViewById(R.id.dizhisaomiao_left_text);
		right_count = (TextView) findViewById(R.id.dizhisaomiao_right_text);
		left = (ListView) findViewById(R.id.dizhiyapin_list_left);
		right = (ListView) findViewById(R.id.dizhiyapin_list_right);
		wrong = (TextView) findViewById(R.id.dizhiyapin_tishi_cuowu);
		back = (ImageView) findViewById(R.id.dizhisaomiao_back);
		back.setOnClickListener(this);
		chuku = (Button) findViewById(R.id.dizhisaomiao_chuku);
		chuku.setOnClickListener(this);
		quxiao = (Button) findViewById(R.id.dizhisaomiao_quxiao);
		quxiao.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.dizhisaomiao_back:
			manager.getAbnormal().timeout(this, "退出将会清空已扫描数据!",
					new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							DiZhiYaPinSaoMiao_db.this.finish();
						}
					});
			break;
		case R.id.dizhisaomiao_chuku:
			manager.getRuning().runding(this, "验证完毕,开启指纹扫描中...");
			Skip.skip(DiZhiYaPinSaoMiao_db.this, QingLingChuKuJiaoJie_db.class,
					null, 0);
			break;
		case R.id.dizhisaomiao_quxiao:
			wrong.setText("");
			o_Application.numberlist.clear();
			o_Application.guolv.clear();
			o_Application.jihuadan_list_dizhiyapin.clear();
			o_Application.jihuadan_list_dizhiyapin.addAll(copylist);
			if (o_Application.jihuadan_list_dizhiyapin.size() > 0) {
				chuku.setEnabled(false);
				chuku.setBackgroundResource(R.drawable.button_gray);
			}
			if (o_Application.numberlist.size() == 0) {
				quxiao.setEnabled(false);
				chuku.setBackgroundResource(R.drawable.button_gray);
			}
			rightadapter.notifyDataSetChanged();
			right_count.setText("" + o_Application.numberlist.size());
			break;

		default:
			break;
		}
	}

	class LeftAdapter extends BaseAdapter {
		LeftHolder lh;
		LayoutInflater lf = LayoutInflater.from(DiZhiYaPinSaoMiao_db.this);

		@Override
		public int getCount() {
			return o_Application.jihuadan_list_dizhiyapin.size();
		}

		@Override
		public Object getItem(int arg0) {
			return o_Application.jihuadan_list_dizhiyapin.get(arg0);
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
			lh.tv.setText(o_Application.jihuadan_list_dizhiyapin.get(arg0));
			return arg1;
		}

	}

	public static class LeftHolder {
		TextView tv;
	}

	class RightAdapter extends BaseAdapter {
		RightHolder rh;
		LayoutInflater lf = LayoutInflater.from(DiZhiYaPinSaoMiao_db.this);

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
			DiZhiYaPinSaoMiao_db.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
