package com.ljsw.tjbankpda.main;
import com.example.pda.R;

import hdjc.rfid.operator.RFID_Device;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
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

import com.ljsw.tjbankpda.util.QingfenLingquSaomiaoUtil;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.ljsw.tjbankpda.util.ZzxHeduiSaomiaoUtil;

public class ZhouZhuanXiangHeDui_qf extends FragmentActivity implements OnClickListener{
	private ImageView back;
	private TextView topleft, topright;
	private Button chuku, quxiao;
	private ListView listleft, listright;
	private Bundle bundle;//接受上个页面的(ZhouZhuanXiangSaoMiao_qf.this)传过来的Bundle
	private List<String> listMark=new ArrayList<String>();//备份需要扫描周转箱的全部.
	private ZzxHeduiSaomiaoUtil getBoxFromZzxHedui;//周转箱核对扫描获取周转箱id
	private String nowBoxNo;
	private RFID_Device rfid;
	List<String> leftlist = new ArrayList<String>();
	List<String> rightlist = new ArrayList<String>();
	LeftAdapter ladapter;
	RightAdapter radapter;
	private String orderNum;
	
	private Handler saomiaoHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			int boxCount=msg.getData().getInt("boxCount");
			String boxNum=msg.getData().getString("box");
			System.out.println("boxNum:"+boxNum);
	
			System.out.println("刚开始left："+leftlist.size());
			System.out.println("刚开始right："+leftlist.size());
			
			
			if(leftlist.contains(boxNum)){
				rightlist.add(boxNum);
				leftlist.remove(boxNum);
			}
			ladapter.notifyDataSetChanged();
			radapter.notifyDataSetChanged();
			topleft.setText("" + leftlist.size());
			topright.setText("" + rightlist.size());
			if(rightlist.size()>0&&!quxiao.isEnabled()){
				quxiao.setEnabled(true);
				quxiao.setBackgroundResource(R.drawable.buttom_selector_bg);
			}
			if(leftlist.size()==0){
				chuku.setEnabled(true);
				chuku.setBackgroundResource(R.drawable.buttom_selector_bg);
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhouzhuanxianghedui);
		load();
		getList();
		ladapter = new LeftAdapter();
		radapter = new RightAdapter();
		getBoxFromZzxHedui=new ZzxHeduiSaomiaoUtil(nowBoxNo);
		getBoxFromZzxHedui.setHand(saomiaoHandler);
		listleft.setAdapter(ladapter);
		
		orderNum=bundle.getString("orderNum");
		listright.setAdapter(radapter);
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(leftlist.size()==0){
			leftlist.addAll(listMark);
		}
		getRfid().addNotifly(getBoxFromZzxHedui);
		getRfid().open_a20();
	}
	@Override
	protected void onPause() {
		super.onPause();
		getRfid().close_a20();
		leftlist.clear();
		rightlist.clear();
		chuku.setEnabled(false);
		chuku.setBackgroundResource(R.drawable.button_gray);
	}
	public void load() {
		back = (ImageView) findViewById(R.id.zhouzhuan_hedui_back);
		back.setOnClickListener(this);
		topleft = (TextView) findViewById(R.id.zhouzhuan_hedui_left_text);
		topright = (TextView) findViewById(R.id.zhouzhuan_hedui_right_text);
		chuku = (Button) findViewById(R.id.zhouzhuan_hedui_chuku1);
		chuku.setOnClickListener(this);
		quxiao = (Button) findViewById(R.id.zhouzhuan_hedui_quxiao);
		quxiao.setOnClickListener(this);
		topleft.setText("" + leftlist.size());
		topright.setText("" + rightlist.size());
		listleft = (ListView) findViewById(R.id.zhouzhuan_hedui_list_left);
		listright = (ListView) findViewById(R.id.zhouzhuan_hedui_list_right);
		chuku.setEnabled(false);
		chuku.setBackgroundResource(R.drawable.button_gray);
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.zhouzhuan_hedui_back:
			ZhouZhuanXiangHeDui_qf.this.finish();
			break;
		case R.id.zhouzhuan_hedui_chuku1:
			// 出库跳转
			bundle.putString("orderNum", orderNum);
			Skip.skip(this, ShangJiaoQingFen_o_qf.class, bundle, 0);
			break;
		case R.id.zhouzhuan_hedui_quxiao:
			// 清除集合 重新扫描
			rightlist.clear();
			leftlist.clear();
			leftlist.addAll(listMark);
			topright.setText(rightlist.size()+"");
			topleft.setText(leftlist.size()+"");
			getBoxFromZzxHedui.cleanList();
			quxiao.setEnabled(false);
			quxiao.setBackgroundResource(R.drawable.button_gray);
			chuku.setEnabled(false);
			chuku.setBackgroundResource(R.drawable.button_gray);
			break;
		default:
			break;
		}
		
	}
	class LeftAdapter extends BaseAdapter {
		LeftHolder lh;
		LayoutInflater lf = LayoutInflater
				.from(ZhouZhuanXiangHeDui_qf.this);

		@Override
		public int getCount() {
			return leftlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			return leftlist.get(arg0);
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
			lh.tv.setText(leftlist.get(arg0));
			return arg1;
		}

	}

	public static class LeftHolder {
		TextView tv;
	}

	class RightAdapter extends BaseAdapter {
		RightHolder rh;
		LayoutInflater lf = LayoutInflater
				.from(ZhouZhuanXiangHeDui_qf.this);

		@Override
		public int getCount() {
			return rightlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			return rightlist.get(arg0);
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
			rh.tv.setText(rightlist.get(arg0));
			return arg1;
		}

	}

	public static class RightHolder {
		TextView tv;
	}
	
	private void getList(){
		bundle=super.getIntent().getExtras();
		String peisongBoxInfo=bundle.getString("peisongBoxInfo");
		System.out.println("peisongBoxInfo"+peisongBoxInfo);
		nowBoxNo=bundle.getString("nowBoxNo");
		System.out.println("nowBoxNo="+nowBoxNo);
		String[] zhouzhuanxiangs=peisongBoxInfo.split(",");
		leftlist.clear();
		rightlist.clear();
	//	rightlist.add(nowBoxNo);
	//	listMark.add(nowBoxNo);
		for (int i = 0; i < zhouzhuanxiangs.length; i++) {
				leftlist.add(zhouzhuanxiangs[i]);
				listMark.add(zhouzhuanxiangs[i]);
		}
//		for (int i = 0; i < listMark.size(); i++) {
//			System.out.println("listMark:="+listMark.get(i));
//		}
//		System.out.println("left.="+leftlist.size());
//		System.out.println("listMark.="+listMark.size());
		if(leftlist.size()==0){
			chuku.setEnabled(true);
			chuku.setBackgroundResource(R.drawable.buttom_selector_bg);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			ZhouZhuanXiangHeDui_qf.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	private RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}

}
