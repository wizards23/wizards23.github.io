package com.ljsw.tjbankpda.yy.activity;

import hdjc.rfid.operator.RFID_Device;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pda.R;
import com.ljsw.tjbankpda.util.BianyiType;
import com.ljsw.tjbankpda.util.EqualsList;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.ljsw.tjbankpda.yy.application.S_application;
import com.ljsw.tjbankpda.yy.application.SaomiaoUtil;
import com.manager.classs.pad.ManagerClass;

/**
 * 请领周转箱扫描 页面
 * @author Administrator
 */
public class QingLzhxSaomiaoActivity extends FragmentActivity implements
		OnClickListener {
	private ImageView back;//返回
	private Button queDing, quXiao;//确定取消按钮
	private ManagerClass managerClass;
	private List<String> wlist = new ArrayList<String>(); //未扫描集合
	private List<String> sjlist = new ArrayList<String>(); //已扫描集合
	private ListView qlwsm_listView,qlysm_listView;//未扫描集合列表,已扫描集合列表
	private TextView ysmText_count,//已扫描数量
					 wsmText_count,//未扫描数量
					 ql_wrong;//系统提示错误
	QlysmiaoAdapter ysm;
	QlwsmiaoAdapter wsm;
	private Handler handler;
	private SaomiaoUtil sut;
	Bundle bundle;
	private RFID_Device rfid;

	private RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}
	
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yy_qlsaomiao_s);
		ysm = new QlysmiaoAdapter();
		wsm = new QlwsmiaoAdapter();
		sut = new SaomiaoUtil();
		Intent intent = getIntent();
		bundle = intent.getExtras();
		managerClass = new ManagerClass();
		initView();
		
		handler = new Handler(){
			@SuppressWarnings("static-access")
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);				
				switch (msg.what) {
				case 1:
					qlysm_listView.setAdapter(ysm);
//					new TurnListviewHeight(qlysm_listView);
					wsmText_count.setText("" +S_application.getApplication().leftlist.size());
					ysmText_count.setText(""+S_application.getApplication().rightlist.size());
//					ql_wrong.setText(S_application.getApplication().wrong);								
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
		sut.setHandler(handler);
	}
	
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
		getRfid().addNotifly(sut);
		getRfid().open_a20();
		ysmText_count.setText(""+0);
		if(wlist!=null && wlist.size()>0){
			wsmText_count.setText("" + wlist.size());
			qlwsm_listView.setAdapter(wsm);
		}
		
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
		S_application.getApplication().wrong="";
	}
	
	/**
	 * 初始化控件
	 */
	public void initView() {
		
		ql_wrong = (TextView) this.findViewById(R.id.ql_wrong);
		ysmText_count = (TextView) this.findViewById(R.id.qlsm_ysmiao);
		wsmText_count = (TextView) this.findViewById(R.id.qlsm_wsmiao);
		qlwsm_listView = (ListView) this.findViewById(R.id.qlwsm_listView);
		qlysm_listView = (ListView) this.findViewById(R.id.qlysm_listView);		
		back = (ImageView) this.findViewById(R.id.yayun_backS3);
		queDing = (Button) this.findViewById(R.id.btn_qlsmQd);	
		quXiao = (Button) this.findViewById(R.id.btn_qlsmQx);
		if(S_application.getApplication().leftlist!=null && S_application.getApplication().leftlist.size()>0){
			qlwsm_listView.setAdapter(wsm);
		}
		if(S_application.getApplication().rightlist!=null && S_application.getApplication().rightlist.size()>0){
			qlysm_listView.setAdapter(ysm);
		}
		queDing.setOnClickListener(this);
		quXiao.setOnClickListener(this);
		back.setOnClickListener(this);
	}
	 
	
	public void getData(){
		
		if(bundle!=null){
			sjlist = (List<String>) bundle.getSerializable("sjlist");
			wlist = (List<String>) bundle.getSerializable("qllist");
			StringBuffer sb=new StringBuffer();
			for (int i = 0; i < wlist.size(); i++) {
				sb.append(wlist.get(i));
				if((i+1)< wlist.size()){
					sb.append(BianyiType.xiahuaxian);
				}
			}
			S_application.getApplication().s_zzxQingling=sb.toString();
			for (int i = 0; i < wlist.size(); i++) {
				S_application.getApplication().leftlist.add(wlist.get(i));
			}
		}
	}
	
	
	/**
	 * 请领周转箱未扫描自定义样式
	 * @author Administrator
	 */
	class QlwsmiaoAdapter extends BaseAdapter{
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
			if(arg1==null){
				entity = new KongjianEntity();
				arg1 = LayoutInflater.from(QingLzhxSaomiaoActivity.this).inflate(
						R.layout.item_qlsm_s, null);
				entity.textView1 = (TextView) arg1.findViewById(R.id.boxNum1);
				arg1.setTag(entity);
			}else{
				entity = (KongjianEntity) arg1.getTag();
			}
			entity.textView1.setText(S_application.getApplication().leftlist.get(arg0));
			return arg1;
		}
		
	}
	
	/**
	 * 请领周转箱已扫描自定义样式
	 * @author Administrator
	 */
	class QlysmiaoAdapter extends BaseAdapter{
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
			if(arg1==null){
				entity = new KongjianEntity();
				arg1 = LayoutInflater.from(QingLzhxSaomiaoActivity.this).inflate(
						R.layout.item_qlsm_s, null);
				entity.textView1 = (TextView) arg1.findViewById(R.id.boxNum1);
				arg1.setTag(entity);
			}else{
				entity = (KongjianEntity) arg1.getTag();
			}
			entity.textView1.setText(S_application.getApplication().rightlist.get(arg0));
			return arg1;
		}	
	}
	
	static class KongjianEntity{
		public TextView textView1;
	}
		
	/**
	 * 单击事件
	 */
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_qlsmQd://跳转到上缴周转箱扫描页面
			Bundle bundle = new Bundle();
			bundle.putSerializable("sjlist", (Serializable) sjlist);
			if(sjlist.isEmpty() && sjlist.size()==0){
				managerClass.getRuning().runding(QingLzhxSaomiaoActivity.this, "正在开启指纹扫描,请等待...");
				Skip.skip(QingLzhxSaomiaoActivity.this, YyrwJiaojieActivity.class, bundle, 0);
			}else{
				Skip.skip(QingLzhxSaomiaoActivity.this, SjzzxSaomiaoActivity.class, bundle, 0);
			}
			break;
		case R.id.btn_qlsmQx://清空扫描信息
			queDing.setBackgroundResource(R.drawable.gray_btn_bg);
			queDing.setEnabled(false);
			ql_wrong.setText("");
			S_application.getApplication().rightlist.clear();
			S_application.getApplication().leftlist.clear();			
			S_application.getApplication().bianhao="";
			S_application.getApplication().leftlist.addAll(wlist);
			handler.sendEmptyMessage(1);
			break;

		case R.id.yayun_backS3:
			S_application.getApplication().s_zzxQingling=null;
			QingLzhxSaomiaoActivity.this.finish();
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			S_application.getApplication().s_zzxQingling=null;
			QingLzhxSaomiaoActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
