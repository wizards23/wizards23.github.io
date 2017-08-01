package com.ljsw.tjbankpda.main;

import com.example.pda.R;

import hdjc.rfid.operator.RFID_Device;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;

import com.ljsw.tjbankpda.qf.application.Mapplication;
import com.ljsw.tjbankpda.qf.entity.Box;
import com.ljsw.tjbankpda.qf.service.QingfenRenwuService;
import com.ljsw.tjbankpda.util.DizhiYapinSaomiaoUtil;
import com.ljsw.tjbankpda.util.MessageDialog;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.ljsw.tjbankpda.util.ZzxLuruSaomaoUtil;
import com.manager.classs.pad.ManagerClass;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 周转箱录入界面
 * @author FUHAIQING
 */
public class QinglingZhouzhuanxiangluruActivity extends FragmentActivity {
	/* 定义控件变量 */
	private TextView tvNo;// 编号
	private EditText edOnceNo;// 一次性锁扣编号
	private TextView tvAdd;// 录入
	private ListView lvInfo;// 装箱情况明细
	private TextView tvTotal;// 合计
	private Button btnTongji;// 装箱统计
	private Button btnOk;// 装箱完成
	private ImageView ivBack;// 返回上一个界面

	/* 定义全局变量 */
	private ZzxLuruSaomaoUtil dysmUtil = new ZzxLuruSaomaoUtil();
	private String orderNum;// 订单编号
	private ZhuangxiangInfoAdapter za;
	private ManagerClass manager = new ManagerClass();
	private List<Box> ltZhouzhuanxiang = Mapplication.getApplication().ltZzxNumber;// 存放抵质押品信息
	private Bundle bundle;
	private RFID_Device rfid;

	private RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				System.out.println("进入handel1");
				String no = msg.getData().getString("Num");
				boolean flag = false;
				for (int i = 0; i < ltZhouzhuanxiang.size(); i++) {
					if (no.equals(ltZhouzhuanxiang.get(i).getType())) {
						flag = true;
					}
				}
				if (flag) {
					manager.getResultmsg().resultmsg(
							QinglingZhouzhuanxiangluruActivity.this, "该周转箱已扫描",
							false);
				} else {
					tvNo.setText(no);
				}
				za.notifyDataSetChanged();
				break;

			case 2:
				manager.getRuning().remove();
				String[] nos = (String[]) msg.obj;
				String nowNo = nos[0];
				String nowOnceNo = nos[1];
				ltZhouzhuanxiang.add(new Box(nowNo, nowOnceNo, orderNum));
				btnOk.setEnabled(true);
				btnOk.setBackgroundResource(R.drawable.buttom_selector_bg);
				za.notifyDataSetChanged();
				new TurnListviewHeight(lvInfo);
				System.out.println("是否可提交dizhi="
						+ Mapplication.getApplication().IsDizhiOK + " ,zk="
						+ Mapplication.getApplication().IsZhongkongOK + ",xj="
						+ Mapplication.getApplication().IsXianjingOK);
				if (Mapplication.getApplication().IsDizhiOK
						&& Mapplication.getApplication().IsXianjingOK
						&& Mapplication.getApplication().IsZhongkongOK) {
					btnOk.setEnabled(true);
					btnOk.setBackgroundResource(R.drawable.buttom_selector_bg);
				} else {
					btnOk.setEnabled(false);
					btnOk.setBackgroundResource(R.drawable.button_gray);
				}
				tvTotal.setText(ltZhouzhuanxiang.size() + "");
				edOnceNo.setText("");
				tvNo.setText("");
				Mapplication.getApplication().ltZzxNumber = ltZhouzhuanxiang;

				break;
			case 3:
				manager.getRuning().remove();
				String yesNo=msg.obj.toString();
				if(yesNo==null){
					manager.getResultmsg().resultmsg(
							QinglingZhouzhuanxiangluruActivity.this,
							"传入的周转箱为空", false);
				}else if(yesNo.equals("no1")){
					manager.getResultmsg().resultmsg(
							QinglingZhouzhuanxiangluruActivity.this,
							"周转箱已被使用", false);
				}else if(yesNo.equals("no3")){
					manager.getResultmsg().resultmsg(
							QinglingZhouzhuanxiangluruActivity.this,
							"访问超时", false);
				}else{
					manager.getResultmsg().resultmsg(
							QinglingZhouzhuanxiangluruActivity.this,
							"无效周转箱号", false);
				}
				
			}

		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_qingling_zhouzhuanxiangluru);
		
		bundle = super.getIntent().getExtras();
		orderNum = bundle.getString("qlNum");
		Mapplication.getApplication().renwudan = bundle.getString("renwudan");
		Mapplication.getApplication().jigouid = bundle.getString("jigouid");
		System.out.println("周转箱界面--订单号=" + orderNum);
		tvNo = (TextView) findViewById(R.id.tv_qingling_zhouzhuanxiangluru_bianhao);
		edOnceNo = (EditText) findViewById(R.id.ed_qingling_zhouzhuanxiangluru_suokoubianhao);
		tvAdd = (TextView) findViewById(R.id.tv_qingling_zhouzhuanxiangluru_luru);
		lvInfo = (ListView) findViewById(R.id.lv_qingling_zhouzhuanxiangluru_zhuangxiangInfo);
		tvTotal = (TextView) findViewById(R.id.tv_qingling_zhouzhuanxiangluru_total);
		btnTongji = (Button) findViewById(R.id.btn_qingling_zhouzhuanxiangluru_tongji);
		btnOk = (Button) findViewById(R.id.btn_qingling_zhouzhuanxiangluru_wancheng);
		btnOk.setEnabled(false);
		btnOk.setBackgroundResource(R.drawable.button_gray);
		ivBack = (ImageView) findViewById(R.id.iv_qingling_zhouzhuanxiangluru_back);
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				QinglingZhouzhuanxiangluruActivity.this.finish();
			}
		});
		if (Mapplication.getApplication().IsDizhiOK
				&& Mapplication.getApplication().IsXianjingOK
				&& Mapplication.getApplication().IsZhongkongOK
				&& Mapplication.getApplication().ltZzxNumber.size() > 0) {
			btnOk.setEnabled(true);
			btnOk.setBackgroundResource(R.drawable.buttom_selector_bg);
		} else {
			btnOk.setEnabled(false);
			btnOk.setBackgroundResource(R.drawable.button_gray);
		}
		dysmUtil.setHand(handler);
		za = new ZhuangxiangInfoAdapter(ltZhouzhuanxiang);
		lvInfo.setAdapter(za);
		// 判断是否有录入周转箱,如果没有,确认按钮为不可点击状态
		tvAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int flag = 0;// 定义标识,默认为0
				String nowNo = tvNo.getText() + "";// 当前扫描的周转箱编号
				String nowOnceNo = edOnceNo.getText() + "";// 当前输入的一次性锁扣编号
				if (nowNo.equals("") || nowNo == null) {
					manager.getResultmsg().resultmsg(
							QinglingZhouzhuanxiangluruActivity.this,
							"请扫描周转箱......", false);
				} else if (nowOnceNo.equals("") || nowOnceNo == null) {
					manager.getResultmsg().resultmsg(
							QinglingZhouzhuanxiangluruActivity.this,
							"请输入一次性锁扣编号！.", false);
				} else {
					for (int i = 0; i < ltZhouzhuanxiang.size(); i++) {// 遍历已录入的周转箱列表
						// 如果有相同的周转箱编号,flag标识为-2
						if (nowNo.equals(ltZhouzhuanxiang.get(i).getCount())) {
							flag = -2;
						}
						// 如果有相同的一次性锁扣编号,flag标识为-1
						if (nowNo.equals(ltZhouzhuanxiang.get(i).getType())) {
							flag = -1;
						}
					}
					// ------------------------------------------------------------------------------------s
					if (flag == 0) {
						// 检查周转箱可用性
						manager.getRuning().runding(QinglingZhouzhuanxiangluruActivity.this, "正在核实周转箱");
						Thread thread=new Thread(new ZzxShifoukeyong(nowNo,nowOnceNo));
						thread.start();
					} else if (flag == -1) {
						manager.getResultmsg().resultmsg(
								QinglingZhouzhuanxiangluruActivity.this,
								"此周转箱已录入", false);
					} else if (flag == -2) {
						manager.getResultmsg().resultmsg(
								QinglingZhouzhuanxiangluruActivity.this,
								"此一次性锁扣已被使用", false);
					}
				}
			}
		});
		btnOk.setOnClickListener(new OnClickListener() {
			Dialog dialog;

			@Override
			public void onClick(View arg0) {
				manager.getResultmsg().submitZzxInfo(
						QinglingZhouzhuanxiangluruActivity.this,
						QinglingZhouzhuanxiangluruActivity.this, "您确定提交吗?",
						true);
			}
		});
		btnTongji.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				Intent intent = new Intent(
//						QinglingZhouzhuanxiangluruActivity.this,
//						QinglingZhuangxiangTongjiActivity.class);
//				startActivity(intent);
				Skip.skip(QinglingZhouzhuanxiangluruActivity.this, QinglingZhuangxiangTongjiActivity.class, bundle, 0);
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		tvTotal.setText(ltZhouzhuanxiang.size() + "");
		getRfid().addNotifly(dysmUtil);
		getRfid().open_a20();
		new TurnListviewHeight(lvInfo);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getRfid().close_a20();
	}

	class ZhuangxiangInfoAdapter extends BaseAdapter {
		private List<Box> lt;
		private ViewHolder vh;

		public ZhuangxiangInfoAdapter(List<Box> lt) {
			super();
			this.lt = lt;
			System.out.println();
		}

		@Override
		public int getCount() {
			return lt.size();
		}

		@Override
		public Object getItem(int arg0) {
			return lt.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View v, ViewGroup arg2) {
			LayoutInflater inflater = LayoutInflater
					.from(QinglingZhouzhuanxiangluruActivity.this);
			if (v == null) {
				v = inflater.inflate(R.layout.item_qingling_zhuangxiang, null);
				vh = new ViewHolder();
				vh.tvType = (TextView) v
						.findViewById(R.id.tv_item_qingling_zhuangxiang_type);
				vh.tvCount = (TextView) v
						.findViewById(R.id.tv_item_qingling_zhuangxiang_count);
				vh.tvDel = (TextView) v
						.findViewById(R.id.tv_item_qingling_zhuangxiang_delete);
				v.setTag(vh);
			} else {
				vh = (ViewHolder) v.getTag();
			}
			vh.tvType.setText(lt.get(arg0).getType());
			vh.tvCount.setText(lt.get(arg0).getCount());
			vh.tvDel.setOnClickListener(new QuanbieDelListener(arg0));
			return v;
		}

		public class ViewHolder {
			TextView tvType;
			TextView tvCount;
			TextView tvDel;
		}

		class QuanbieDelListener implements OnClickListener {
			private int position;

			public QuanbieDelListener(int position) {
				super();
				this.position = position;
			}

			@Override
			public void onClick(View arg0) {
				lt.remove(position);
				// 判断是否有录入周转箱,如果没有,确认按钮为不可点击状态
				if (ltZhouzhuanxiang.size() == 0) {	
					btnOk.setEnabled(false);
					btnOk.setBackgroundResource(R.drawable.button_gray);
				}
				dysmUtil.zhikong();
				za.notifyDataSetChanged();
				new TurnListviewHeight(lvInfo);
				tvTotal.setText(ltZhouzhuanxiang.size() + "");
			}
		}
	}

	/**
	 * 检查周转箱是否可用
	 * 
	 * @author yuyunheng
	 * 
	 */
	private class ZzxShifoukeyong implements Runnable {
		private String zzxId;
		private String suokouId;

		public ZzxShifoukeyong(String zzxId,String suokouId) {
			this.zzxId = zzxId;
			this.suokouId=suokouId;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String yesNo = "";
			try {
				yesNo = new QingfenRenwuService().getZzxShifoukeyong(zzxId);
			}catch(SocketTimeoutException time){
				time.printStackTrace();
				yesNo = "no3";
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				yesNo = "no2";
			}

			Message msg=handler.obtainMessage();
			
			if(yesNo!=null&&yesNo.equals("yes")){
				msg.what=2;
				String[] obj={zzxId,suokouId};
				msg.obj=obj;
			}else{
				msg.what=3;
				msg.obj=yesNo;
			}
			
			handler.sendMessage(msg);
		}

	}

}
