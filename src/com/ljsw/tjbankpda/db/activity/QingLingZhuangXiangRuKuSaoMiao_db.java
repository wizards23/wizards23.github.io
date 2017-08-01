package com.ljsw.tjbankpda.db.activity;

import hdjc.rfid.operator.RFID_Device;

import java.net.SocketTimeoutException;
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

import com.application.GApplication;
import com.example.pda.R;
import com.ljsw.tjbankpda.db.application.o_Application;
import com.ljsw.tjbankpda.db.biz.QingLingRuKuSaoMiao;
import com.ljsw.tjbankpda.db.service.ZhouZhuanXiangJiaoJie;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.ljsw.tjbankpda.yy.service.ICleaningManService;
import com.manager.classs.pad.ManagerClass;

/**
 * 请领装箱入库
 * 总库&分库 装箱入库
 * @author yuyunheng
 *
 */
@SuppressLint("HandlerLeak")
public class QingLingZhuangXiangRuKuSaoMiao_db extends FragmentActivity
		implements OnClickListener {
	private ImageView back;
	private TextView topleft, topright, wrong;
	private Button chuku, quxiao;
	private ListView listleft, listright;
	private List<String> copylist = new ArrayList<String>();
	private LeftAdapter ladapter;
	private RightAdapter radapter;
	private String jiaojieOk;
	private String cashBoxNum = "";// 交接拼接款箱
	private String userId = "";// 交接拼接登录人帐号
	private ManagerClass manager;
	private QingLingRuKuSaoMiao getnumber;
	ICleaningManService is = new ICleaningManService();
	OnClickListener onclickreplace,onClick;
	private String jigouleibie;
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
		setContentView(R.layout.activity_qinglingrukusaomiao);
		load();
		getnumber = new QingLingRuKuSaoMiao();
		getnumber.setHandler(handler);
		copylist.addAll(o_Application.qlruku.getZhouzhuanxiang());
		ladapter = new LeftAdapter();
		radapter = new RightAdapter();
		manager = new ManagerClass();

		onclickreplace = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				manager.getAbnormal().remove();
				Xianlu();
			}
		};
		onClick = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				manager.getAbnormal().remove();
				getJigouLeibie();
			}
		};
	}

	String params;// 返回机构的类别

	/**
	 * 通过机构ID获取线路类型
	 */
	public void Xianlu() {
		manager.getRuning().runding(QingLingZhuangXiangRuKuSaoMiao_db.this,
				"获取线路中.....");
		new Thread() {
			public void run() {
				super.run();
				try {
					params = is.getJigouLeibie(GApplication.user
							.getOrganizationId());
					if (params != null || !params.equals("")) {
						// S_application.getApplication().jiaojieType=1;
						handler.sendEmptyMessage(5);
					} else {
						handler.sendEmptyMessage(7);
					}
				} catch (SocketTimeoutException e) {
					handler.sendEmptyMessage(6);
				} catch (NullPointerException e) {
					handler.sendEmptyMessage(7);
				} catch (Exception e) {
					handler.sendEmptyMessage(8);
				}

			};
		}.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Xianlu();
		getJigouLeibie();
		o_Application.numberlist.clear();
		o_Application.guolv.clear();
		o_Application.qlruku.getZhouzhuanxiang().clear();
		o_Application.qlruku.getZhouzhuanxiang().addAll(copylist);
		getRfid().addNotifly(getnumber);
		new Thread() {
			@Override
			public void run() {
				super.run();
				getRfid().open_a20();
			}

		}.start();
		topright.setText("" + o_Application.numberlist.size());
		topleft.setText("" + o_Application.qlruku.getZhouzhuanxiang().size());
		listleft.setAdapter(ladapter);
		listright.setAdapter(radapter);
		ladapter.notifyDataSetChanged();
		radapter.notifyDataSetChanged();

		if (o_Application.qlruku.getZhouzhuanxiang().size() > 0) {
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

				if (o_Application.qlruku.getZhouzhuanxiang().size() == 0) {
					chuku.setEnabled(true);
					chuku.setBackgroundResource(R.drawable.buttom_selector_bg);
				}
				if (o_Application.numberlist.size() > 0) {
					quxiao.setEnabled(true);
					quxiao.setBackgroundResource(R.drawable.buttom_selector_bg);
				}
			//	wrong.setText(o_Application.wrong);
				topright.setText("" + o_Application.numberlist.size());
				topleft.setText(""
						+ o_Application.qlruku.getZhouzhuanxiang().size());
				ladapter.notifyDataSetChanged();
				radapter.notifyDataSetChanged();
				listleft.setAdapter(ladapter);
			
				listright.setAdapter(radapter);
				
				break;
			case 1:
				manager.getRuning().remove();
				Skip.skip(QingLingZhuangXiangRuKuSaoMiao_db.this,
						RenWuLieBiao_db.class, null, 0);
				break;
			case 2:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						QingLingZhuangXiangRuKuSaoMiao_db.this, "提交超时,重试?",
						new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {

								manager.getAbnormal().remove();
								JiaoJie();
							}
						});
				break;
			case 3:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						QingLingZhuangXiangRuKuSaoMiao_db.this, jiaojieOk+"重试？",
						new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								JiaoJie();
							}
						});
				break;
			case 4:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						QingLingZhuangXiangRuKuSaoMiao_db.this, "网络连接失败,重试?",
						new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								manager.getAbnormal().remove();
								JiaoJie();
							}
						});

				break;
			case 5:
				manager.getRuning().remove();
				break;
			case 6:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						QingLingZhuangXiangRuKuSaoMiao_db.this, "连接超时，重新链接？",
						onclickreplace);
				break;
			case 7:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						QingLingZhuangXiangRuKuSaoMiao_db.this, "获取失败",
						onclickreplace);
				break;
			case 8:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						QingLingZhuangXiangRuKuSaoMiao_db.this, "信息加载异常",
						onclickreplace);
				break;
			case 15:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						QingLingZhuangXiangRuKuSaoMiao_db.this, "连接超时，重新链接？",
						onClick);
				break;
			case 13:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						QingLingZhuangXiangRuKuSaoMiao_db.this, "获取失败",
						onClick);
				break;
			case 14:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						QingLingZhuangXiangRuKuSaoMiao_db.this, "信息加载异常",
						onClick);
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

	public void load() {
		back = (ImageView) findViewById(R.id.ql_ruku_saomiao_back);
		back.setOnClickListener(this);
		topleft = (TextView) findViewById(R.id.ql_ruku_saomiao_left_text);
		topright = (TextView) findViewById(R.id.ql_ruku_saomiao_right_text);
		chuku = (Button) findViewById(R.id.ql_ruku_saomiao_chuku);
		chuku.setOnClickListener(this);
		quxiao = (Button) findViewById(R.id.ql_ruku_saomiao_quxiao);
		quxiao.setOnClickListener(this);
		listleft = (ListView) findViewById(R.id.ql_ruku_saomiao_list_left);
		listright = (ListView) findViewById(R.id.ql_ruku_saomiao_list_right);
		wrong = (TextView) findViewById(R.id.qinglingchuku_tishi_cuowu);
		listleft.setDividerHeight(0);
		listright.setDividerHeight(0);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ql_ruku_saomiao_back:
			QingLingZhuangXiangRuKuSaoMiao_db.this.finish();
			break;
		case R.id.ql_ruku_saomiao_chuku:
			// 出库跳转
			JiaoJie();
			break;
		case R.id.ql_ruku_saomiao_quxiao:
			// 清除集合 重新扫描
			chuku.setEnabled(false);
			chuku.setBackgroundResource(R.drawable.button_gray);

			if (o_Application.numberlist.size() == 0) {
				quxiao.setEnabled(false);
				quxiao.setBackgroundResource(R.drawable.button_gray);
			}
			o_Application.numberlist.clear();
			o_Application.qlruku.getZhouzhuanxiang().clear();
			o_Application.qlruku.getZhouzhuanxiang().addAll(copylist);
			o_Application.guolv.clear();

			handler.sendEmptyMessage(0);
			break;
		default:
			break;
		}
	}

	public void JiaoJie() {
		manager.getRuning().runding(this, "提交中...");
		getcashBoxNumAnduserId();
		new Thread() {
			@Override
			public void run() {
				super.run();
				ZhouZhuanXiangJiaoJie jiaojie = new ZhouZhuanXiangJiaoJie();
				try {
					/*
					 * revised by zhangxuewei，新添加分库清分功能，分库也具有了清分功能所以，将限制放开
					 */
					jiaojieOk = jiaojie.saveAuthLog(cashBoxNum, userId,"",
							"22", o_Application.qlruku.getDanhao());
//					if (jigouleibie.equals("0")) {
//						jiaojieOk = jiaojie.saveAuthLog(cashBoxNum, userId,"",
//								"22", o_Application.qlruku.getDanhao());
//					} else {
//						jiaojieOk = jiaojie.saveAuthLog(cashBoxNum, userId,"",
//								"25", o_Application.qlruku.getDanhao());
//					}
					System.out.println("---------交接完成，返回：" + jiaojieOk);
					cashBoxNum = "";
					userId = "";
					o_Application.FingerJiaojieNum.clear();
					jiaojieOk=jiaojieOk==null?"":jiaojieOk;
					if (jiaojieOk.trim().equals("00")) {
						handler.sendEmptyMessage(1);
					} else {
						handler.sendEmptyMessage(3);
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(2);
				} catch (NullPointerException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(3);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(4);
				}
			}

		}.start();
	}

	/**
	 * 拼接周转箱和交接人账户信息
	 */
	public void getcashBoxNumAnduserId() {
		int a = 0;
		// int b = 0;
		int c = 0;
		cashBoxNum="";
		for (int i = 0; i < o_Application.numberlist.size(); i++) {
			if (a == o_Application.numberlist.size() - 1) {
				cashBoxNum += o_Application.numberlist.get(i);
			} else {
				cashBoxNum += o_Application.numberlist.get(i) + "_";
			}
			a++;
		}
		o_Application.FingerJiaojieNum.addAll(o_Application.FingerLoginNum);
		userId="";
		for (int i = 0; i < o_Application.FingerJiaojieNum.size(); i++) {
			System.out.println("o_Application.FingerJiaojieNum:"+o_Application.FingerJiaojieNum.size());
			if (c == o_Application.FingerJiaojieNum.size() - 1) {
				userId += o_Application.FingerJiaojieNum.get(i);
			} else {
				userId += o_Application.FingerJiaojieNum.get(i) + "_";
			}
			c++;
		}

	}

	class LeftAdapter extends BaseAdapter {
		LeftHolder lh;
		LayoutInflater lf = LayoutInflater
				.from(QingLingZhuangXiangRuKuSaoMiao_db.this);

		@Override
		public int getCount() {
			return o_Application.qlruku.getZhouzhuanxiang().size();
		}

		@Override
		public Object getItem(int arg0) {
			return o_Application.qlruku.getZhouzhuanxiang().get(arg0);
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
			lh.tv.setText(o_Application.qlruku.getZhouzhuanxiang().get(arg0));
			return arg1;
		}

	}
	
	/**
	 * 获取机构类别
	 */
	public void getJigouLeibie(){
		manager.getRuning().runding(QingLingZhuangXiangRuKuSaoMiao_db.this, "获取机构类别中...");
		new Thread(new Runnable() {	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					jigouleibie = is.getJigouLeibie(GApplication.user.getOrganizationId());
					if(null!=jigouleibie ||!"".equals(jigouleibie)){
						handler.sendEmptyMessage(5);
					}else{
						handler.sendEmptyMessage(13);
					}
				} catch (SocketTimeoutException e) {
					handler.sendEmptyMessage(15);
					e.printStackTrace();
				}catch (NullPointerException e) {
					handler.sendEmptyMessage(13);
					e.printStackTrace();
				}catch (Exception e) {
					handler.sendEmptyMessage(14);
					e.printStackTrace();
				}
			}
		}).start();
	}

	public static class LeftHolder {
		TextView tv;
	}

	class RightAdapter extends BaseAdapter {
		RightHolder rh;
		LayoutInflater lf = LayoutInflater
				.from(QingLingZhuangXiangRuKuSaoMiao_db.this);

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
		manager.getRuning().remove();
		if (o_Application.numberlist.size() > 0) {
			o_Application.numberlist.clear();
		}
		if (o_Application.guolv.size() > 0) {
			o_Application.guolv.clear();
		}
		getRfid().close_a20();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			QingLingZhuangXiangRuKuSaoMiao_db.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
