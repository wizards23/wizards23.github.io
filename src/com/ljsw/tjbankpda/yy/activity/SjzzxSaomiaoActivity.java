package com.ljsw.tjbankpda.yy.activity;

import hdjc.rfid.operator.RFID_Device;

import java.util.ArrayList;
import java.util.List;

import com.example.pda.R;
import com.ljsw.tjbankpda.qf.entity.YayKuanXiang;
import com.ljsw.tjbankpda.util.BianyiType;
import com.ljsw.tjbankpda.util.EqualsList;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.ljsw.tjbankpda.yy.application.S_application;
import com.ljsw.tjbankpda.yy.application.SaomiaoUtil;
import com.manager.classs.pad.ManagerClass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 上缴周转箱扫描页面
 * @author Administrator
 */
public class SjzzxSaomiaoActivity extends FragmentActivity implements
		OnClickListener {
	private Button queDing, quXiao;// 确定取消按钮
	private List<String> wlist = new ArrayList<String>();// 未扫描集合
	private List<String> ylist = new ArrayList<String>();// 已扫描集合
	private TextView ysmText_count,// 已扫描数量
			wsmText_count,// 未扫描数量
			sj_wrong;// 错误提示;
	private ListView sjwsm_listView, sjysm_listView;// 未扫描列表,已扫描列表
	private ManagerClass managerClass;
	SjwsmiaoAdapter wsm;
	SjysmiaoAdapter ysm;
	private SaomiaoUtil stu;
	Bundle bundle;
	private RFID_Device rfid;

	private RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yy_sjsaomiao_s);
		managerClass = new ManagerClass();
		wsm = new SjwsmiaoAdapter();
		ysm = new SjysmiaoAdapter();
		stu = new SaomiaoUtil();
		Intent intent = getIntent();
		bundle = intent.getExtras();
		stu.setHandler(handler);
		initView();

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				ysmText_count.setText(""
						+ S_application.getApplication().rightlist.size());
				wsmText_count.setText(""
						+ S_application.getApplication().leftlist.size());
				sjysm_listView.setAdapter(ysm);
				ysm.notifyDataSetChanged();
				wsm.notifyDataSetChanged();
				if (S_application.getApplication().leftlist.size()==0) {
					queDing.setBackgroundResource(R.drawable.buttom_selector_bg);
					queDing.setEnabled(true);
				} 
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onResume() {
		super.onResume();
		getData();
		if (S_application.getApplication().leftlist.size()==0) {
			queDing.setBackgroundResource(R.drawable.buttom_selector_bg);
			queDing.setEnabled(true);
		}else{
			queDing.setBackgroundResource(R.drawable.gray_btn_bg);
			queDing.setEnabled(false);
		}
		getRfid().addNotifly(stu);
		getRfid().open_a20();
		wsmText_count.setText(""+ S_application.getApplication().leftlist.size());
		ysmText_count.setText("" + 0);
		sjwsm_listView.setAdapter(wsm);
		sjysm_listView.setAdapter(ysm);
		
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		getRfid().close_a20();
		managerClass.getRuning().remove();
		queDing.setBackgroundResource(R.drawable.gray_btn_bg);
		queDing.setEnabled(false);
		S_application.getApplication().rightlist.clear();
		S_application.getApplication().leftlist.clear();
		S_application.getApplication().bianhao="";
		S_application.getApplication().wrong = "";
	}

	/**
	 * 初始化控件
	 */
	public void initView() {
		sj_wrong = (TextView) this.findViewById(R.id.sj_wrong);
		ysmText_count = (TextView) this.findViewById(R.id.sjsm_ysmiao);
		wsmText_count = (TextView) this.findViewById(R.id.sjsm_wsmiao);
		sjwsm_listView = (ListView) this.findViewById(R.id.sjwsm_listView);
		sjysm_listView = (ListView) this.findViewById(R.id.sjysm_listView);
		queDing = (Button) findViewById(R.id.btn_sjsmQd);
		quXiao = (Button) findViewById(R.id.btn_sjsmQx);
		sjwsm_listView.setAdapter(wsm);
		sjysm_listView.setAdapter(ysm);
		findViewById(R.id.yayun_backS4).setOnClickListener(this);// 返回
		queDing.setOnClickListener(this);// 跳转到押运任务交接页面
		quXiao.setOnClickListener(this);// 取消操作
	}

	/**
	 * 获取数据
	 */
	public void getData() {
		if (bundle != null) {
			wlist = (List<String>) bundle.getSerializable("sjlist");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < wlist.size(); i++) {
				sb.append(wlist.get(i));
				if ((i + 1) < wlist.size()) {
					sb.append(BianyiType.xiahuaxian);
				}
			}
			S_application.getApplication().s_zzxShangjiao = sb.toString();
			for (int i = 0; i < wlist.size(); i++) {
				S_application.getApplication().leftlist.add(wlist.get(i));
			}
		}
		

	}

	/**
	 * 上缴周转箱未扫描自定义样式
	 * @author Administrator
	 */
	class SjwsmiaoAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return S_application.getApplication().leftlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return S_application.getApplication().leftlist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {

			KongjianEntity entity;
			if (arg1 == null) {
				entity = new KongjianEntity();
				arg1 = LayoutInflater.from(SjzzxSaomiaoActivity.this).inflate(
						R.layout.item_qlsm_s, null);
				entity.textView1 = (TextView) arg1.findViewById(R.id.boxNum1);

				arg1.setTag(entity);
			} else {
				entity = (KongjianEntity) arg1.getTag();
			}
			entity.textView1.setText(S_application.getApplication().leftlist
					.get(arg0));
			return arg1;
		}
	}

	/**
	 * 上缴周转箱已扫描自定义样式
	 * 
	 * @author Administrator
	 */
	class SjysmiaoAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return S_application.getApplication().rightlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return S_application.getApplication().rightlist.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {

			KongjianEntity entity;
			if (arg1 == null) {
				entity = new KongjianEntity();
				arg1 = LayoutInflater.from(SjzzxSaomiaoActivity.this).inflate(
						R.layout.item_qlsm_s, null);
				entity.textView1 = (TextView) arg1.findViewById(R.id.boxNum1);

				arg1.setTag(entity);
			} else {
				entity = (KongjianEntity) arg1.getTag();
			}
			entity.textView1.setText(S_application.getApplication().rightlist
					.get(arg0));
			return arg1;
		}
	}

	static class KongjianEntity {
		public TextView textView1;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.yayun_backS4:// 返回
			S_application.getApplication().s_zzxShangjiao = null;
			SjzzxSaomiaoActivity.this.finish();
			break;
		case R.id.btn_sjsmQd:// 跳转到押运任务交接页面
			managerClass.getRuning().runding(SjzzxSaomiaoActivity.this,
					"正在开启指纹验证,请等待...");
			Skip.skip(SjzzxSaomiaoActivity.this, YyrwJiaojieActivity.class,
					null, 0);
			break;
		case R.id.btn_sjsmQx:
			S_application.getApplication().bianhao="";
			queDing.setBackgroundResource(R.drawable.gray_btn_bg);
			queDing.setEnabled(false);
			sj_wrong.setText("");
			S_application.getApplication().leftlist.clear();
			S_application.getApplication().rightlist.clear();
			S_application.getApplication().leftlist.addAll(wlist);
			wsmText_count.setText(""+ S_application.getApplication().leftlist.size());
			handler.sendEmptyMessage(1);
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			S_application.getApplication().s_zzxShangjiao = null;
			SjzzxSaomiaoActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
