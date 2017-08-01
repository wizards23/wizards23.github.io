package com.ljsw.tjbankpda.main;

import hdjc.rfid.operator.RFID_Device;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import com.example.pda.R;
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

import com.ljsw.tjbankpda.qf.application.Mapplication;
import com.ljsw.tjbankpda.qf.service.QingfenRenwuService;
import com.ljsw.tjbankpda.util.QingfenLingquSaomiaoUtil;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.manager.classs.pad.ManagerClass;

/**
 * 上缴清分扫描周转箱
 * @author 殴昀 界面,FUHAIQING 数据
 *
 */
public class QingFenLingQu_qf extends FragmentActivity implements OnClickListener {
	/*定义控件变量*/
	private ImageView back;
	private TextView topleft, topright,tvMessage;
	private Button chuku, quxiao;
	private ListView listleft, listright;
	
	/*定义全局变量*/
	private String orderNum;//上缴清分订单号
	private List<String> leftlist = new ArrayList<String>();
	private List<String> rightlist = new ArrayList<String>();
	private LeftAdapter ladapter;
	private RightAdapter radapter;
	private QingfenLingquSaomiaoUtil getBoxFromQflq;//清分领取扫描获取周转箱id
	private RFID_Device rfid;
	private ManagerClass manager;//弹出框
	private List<String> listMark=new ArrayList<String>();//备份原始未扫描周转箱
	private	Table[] RenwuData;
	private Handler okHandle=new Handler(){//数据获取成功handler
	
		public void handleMessage(Message msg) {
			//绑定Adapter
			listMark.clear();
			leftlist=RenwuData[0].get("zhouzhuanxiang").getValues();
			Mapplication.getApplication().ltQflkBoxNum.addAll(leftlist);
			for (String str : leftlist) {
				listMark.add(str);
			}
			
			listleft.setAdapter(ladapter);
			listright.setAdapter(radapter);
			new TurnListviewHeight(listleft);
			//设置未扫描和已扫描数量
			topleft.setText(leftlist.size()+"");
			topright.setText(rightlist.size()+"");
			new TurnListviewHeight(listright);
			getRfid().addNotifly(getBoxFromQflq);
			getRfid().open_a20();
			manager.getRuning().remove();
		};
	};
	private Handler saomiaoHandler=new Handler(){//周转箱ID获取成功handler
		public void handleMessage(Message msg) {
			int boxCount=msg.getData().getInt("boxCount");
			String boxNum=msg.getData().getString("box");
			int flag=-1;
			for (int i = 0; i < leftlist.size(); i++) {
				if(leftlist.get(i).equals(boxNum)){
					leftlist.remove(i);
					rightlist.add(boxNum);
					flag=0;
					System.out.println(boxCount+"-该周转箱在未扫描列表中");
					if(!quxiao.isEnabled()){
						quxiao.setEnabled(true);
						quxiao.setBackgroundResource(R.drawable.buttom_selector_bg);
					}
				}
			}
			if(flag==0){
				listleft.setAdapter(ladapter);
				listright.setAdapter(radapter);
				new TurnListviewHeight(listleft);
				new TurnListviewHeight(listright);
				topleft.setText(leftlist.size()+"");
				topright.setText(rightlist.size()+"");

				if(rightlist.size()>0&&!quxiao.isEnabled()){
					quxiao.setEnabled(true);
					quxiao.setBackgroundResource(R.drawable.buttom_selector_bg);
				}
				if(rightlist.size()==listMark.size()){
					chuku.setEnabled(true);
					chuku.setBackgroundResource(R.drawable.buttom_selector_bg);
				}	
			}else if(flag==-1){
				for (int i = 0; i < rightlist.size(); i++) {
					if(rightlist.get(i).equals(boxNum)){
						System.out.println(boxCount+"- 该周转箱在扫描列表中");
						flag=1;
					}
				}
			}
			/*if(flag==1){
				tvMessage.setText(boxNum+"-已扫描");
			}else {
				tvMessage.setText(boxNum+"-为错误编号");
			}*/
		};
	};
	private Handler timeoutHandle=new Handler(){//连接超时handler
		public void handleMessage(Message msg) {
			manager.getRuning().remove();
			if(msg.what==0){
				manager.getAbnormal().timeout(QingFenLingQu_qf.this, "数据连接超时", new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						getLeft();
						manager.getAbnormal().remove();
						manager.getRuning().runding(QingFenLingQu_qf.this, "数据加载中...");
					}
				});
			}
			if(msg.what==1){
				manager.getAbnormal().timeout(QingFenLingQu_qf.this, "网络连接失败", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					getLeft();
					manager.getAbnormal().remove();
					manager.getRuning().runding(QingFenLingQu_qf.this, "数据加载中...");
				}
			});
			}
		};
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qingfenlingqu);
		getBoxFromQflq=new QingfenLingquSaomiaoUtil();
		getBoxFromQflq.setHand(saomiaoHandler);
		manager=new ManagerClass();
		manager.getRuning().runding(this, "数据加载中...");
		orderNum=super.getIntent().getExtras().getString("sjNum");
	}
	@Override
	protected void onResume() {
		super.onResume();
		listMark.clear();
		rightlist.clear();
		load();
		getLeft();
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		getRfid().close_a20();
	}

	/**
	 * 绑定控件id
	 */
	public void load() {
		back = (ImageView) findViewById(R.id.qf_lingqu_back);
		back.setOnClickListener(this);
		topleft = (TextView) findViewById(R.id.qf_lingqu_saomiao_left_text);
		topright = (TextView) findViewById(R.id.qf_lingqu_saomiao_right_text);
		tvMessage= (TextView) findViewById(R.id.tv_lingqu_saomiao_msg);
		tvMessage.setText("");
		chuku = (Button) findViewById(R.id.qf_lingqu_saomiao_chuku);
		chuku.setOnClickListener(this);
		quxiao = (Button) findViewById(R.id.qf_lingqu_saomiao_quxiao);
		quxiao.setOnClickListener(this);
		topleft.setText("" + leftlist.size());
		topright.setText("" + rightlist.size());
		listleft = (ListView) findViewById(R.id.qf_lingqu_saomiao_list_left);
		listright = (ListView) findViewById(R.id.qf_lingqu_saomiao_list_right);
		ladapter = new LeftAdapter();
		radapter=new RightAdapter();
		listleft.setAdapter(ladapter);
		listleft.setAdapter(radapter);
		chuku.setEnabled(false);
		chuku.setBackgroundResource(R.drawable.button_gray);
		quxiao.setEnabled(false);
		quxiao.setBackgroundResource(R.drawable.button_gray);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.qf_lingqu_back:
			QingFenLingQu_qf.this.finish();
			break;
		case R.id.qf_lingqu_saomiao_chuku:
			// 出库跳转
			Bundle bundle=new Bundle();
			bundle.putString("orderNum", orderNum);
			System.out.println("orderNum:"+orderNum);
			Skip.skip(this, QingFenJinDu_qf.class, bundle, 0);
			break;

		case R.id.qf_lingqu_saomiao_quxiao:
			// 清除集合 重新扫描
			rightlist.clear();
			radapter.notifyDataSetChanged();
			leftlist.clear();
			leftlist.addAll(listMark);
			ladapter.notifyDataSetChanged();
			topleft.setText("" + leftlist.size());
			topright.setText("" + rightlist.size());
			new TurnListviewHeight(listleft);
			new TurnListviewHeight(listright);
			getBoxFromQflq=new QingfenLingquSaomiaoUtil();
			getBoxFromQflq.setHand(saomiaoHandler);
			getRfid().addNotifly(getBoxFromQflq);
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
		LayoutInflater lf = LayoutInflater.from(QingFenLingQu_qf.this);

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
		LayoutInflater lf = LayoutInflater.from(QingFenLingQu_qf.this);

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

	public void getLeft() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				 String params;
				try {
					params = new QingfenRenwuService().getParams(orderNum, "getQfZhouzhuanxiangID");
					RenwuData=Table.doParse(params);
					
					okHandle.sendEmptyMessage(0);
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					timeoutHandle.sendEmptyMessage(0);
				}catch (Exception e) {
					e.printStackTrace();
					timeoutHandle.sendEmptyMessage(1);
				}
			}
		}).start();
		
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			QingFenLingQu_qf.this.finish();
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
