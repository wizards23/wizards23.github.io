package com.fragment.pda;


import com.application.GApplication;
import com.clearadmin.biz.RecycleCashboxCheckListBiz;
import com.entity.Box;
import com.example.pda.R;
import com.golbal.pda.GolbalView;
import com.loginsystem.biz.SystemLoginBiz;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.pda.BoxDetailInfoDo;
import com.moneyboxadmin.pda.PlanWay.PlanFragment;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class PlanWayDate_fragment extends Fragment implements PlanFragment {

	Bundle bundle_listitem;
	String busin; // 业务名称
	ListView listview;

	TextView notTask;
	Ad ad;
	View.OnClickListener click;
	private ManagerClass managerClass;
	View v;

	// 测试
	// public List<Box> listtest = new ArrayList<Box>();

	private RecycleCashboxCheckListBiz recycleCashboxCheckList;

	RecycleCashboxCheckListBiz getRecycleCashboxCheckList() {
		return recycleCashboxCheckList = recycleCashboxCheckList == null ? new RecycleCashboxCheckListBiz()
				: recycleCashboxCheckList;
	}

	private SystemLoginBiz systemLogin;

	SystemLoginBiz getSystemLogin() {
		return systemLogin = systemLogin == null ? new SystemLoginBiz()
				: systemLogin;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		busin = getArguments().getString("business");
		managerClass = new ManagerClass();

		// 重新连接事件
		click = new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				managerClass.getRuning().runding(getActivity(), "正在获取业务单号...");
				// 开始获取业务单号,参数为机构ID
				getRecycleCashboxCheckList().gettRecycleCashboxCheckList(
						GApplication.user.getOrganizationId());
			}
		};

		// hand通知
		getRecycleCashboxCheckList().handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				managerClass.getRuning().remove();
				super.handleMessage(msg);
				
				switch (msg.what) {
				case 1:
					notTask.setVisibility(View.GONE);
					listview.setVisibility(View.VISIBLE);
					if (getRecycleCashboxCheckList().list != null && ad == null) {
						
						ad = new Ad();
						listview.setAdapter(ad);
						Log.i("aaaaaaaaaaaa", ad + "");
					} else {
						ad.notifyDataSetChanged();
					}

					break;
				case -1:
					managerClass.getAbnormal().timeout(getActivity(),
							"连接异常，重新连接？", click);
					break;
				case -4:
					managerClass.getAbnormal().timeout(getActivity(),
							"连接超时，重新连接？", click);
					break;
				case 0:
					notTask.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
					break;
				}
			}

		};

		return inflater.inflate(R.layout.planway_date_fragment, null);
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onStart() {
		super.onStart();
		listview = (ListView) getActivity().findViewById(R.id.listview_waydate);
		notTask = (TextView) getActivity().findViewById(R.id.wardate_nottast);
		notTask.setVisibility(View.GONE);

		// 测试
		// listview.setAdapter(new Ad());
		
		if (getRecycleCashboxCheckList().list == null && ad == null) {
			Log.i("ad", ad + "");
			// 提示
			managerClass.getRuning().runding(getActivity(), "正在获取业务单号...");	
			// 开始获取业务单号,参数为机构ID
			getRecycleCashboxCheckList().gettRecycleCashboxCheckList(
					GApplication.user.getOrganizationId());

			Log.i("3", "3");
		} else {
			Log.i("adadadadad", ad + "");
			
			listview.setAdapter(new Ad());
		}

	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println("结束");
		
		RecycleCashboxCheckListBiz.list=null;
		ad=null;
	}

	// 适配器
	class Ad extends BaseAdapter {

		@Override
		public int getCount() {
			return getRecycleCashboxCheckList().list.size();
			// return listtest.size();
		}

		@Override
		public Object getItem(int arg0) {
			return getRecycleCashboxCheckList().list.get(arg0);
			// return listtest.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			Log.i("4", "4");
			final ViewHolder holder;
			v = view;
			if (v == null) {

				v = GolbalView.getLF(getActivity()).inflate(
						R.layout.c_way_date_item, null);
				holder = new ViewHolder();

				holder.plan = (TextView) v.findViewById(R.id.way);
				holder.date = (TextView) v.findViewById(R.id.date);
				holder.num = (TextView) v.findViewById(R.id.plannumber);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}

			Box box = (Box) getItem(arg0);
			holder.plan.setText(box.getPlanNum());
			holder.date.setText(box.getDate());
			holder.num.setText(box.getBoxNum());

			v.setOnTouchListener(new OnTouchListener() {

				// 触摸事件
				@Override
				public boolean onTouch(View view, MotionEvent even) {

					if (MotionEvent.ACTION_DOWN == even.getAction()) {
						holder.plan
								.setBackgroundResource(R.color.bleu_pressdown);
						holder.num
								.setBackgroundResource(R.color.bleu_pressdown);
						holder.date
								.setBackgroundResource(R.color.bleu_pressdown);
						v.setBackgroundColor(Color.parseColor("#333333"));
					}

					if (MotionEvent.ACTION_UP == even.getAction()) {
						holder.plan
								.setBackgroundResource(R.color.gray_background);
						holder.num
								.setBackgroundResource(R.color.gray_background);
						holder.date
								.setBackgroundResource(R.color.gray_background);
						v.setBackgroundColor(Color.parseColor("#FFFFFF"));

						// 把当前点击的项的编号、路线、业务、放进bundle
						bundle_listitem = new Bundle();
						bundle_listitem.putString("number", holder.plan
								.getText().toString());
						bundle_listitem.putString("businName", busin);
						Log.i("holder.plan.getText().toString()", holder.plan
								.getText().toString());

						managerClass.getGolbalutil().gotoActivity(
								getActivity(), BoxDetailInfoDo.class,
								bundle_listitem,
								managerClass.getGolbalutil().ismover);
						managerClass.getGolbalutil().ismover = 0;
					}
					if (MotionEvent.ACTION_MOVE == even.getAction()) {
						managerClass.getGolbalutil().ismover++;
					}

					if (MotionEvent.ACTION_CANCEL == even.getAction()) {
						holder.plan
								.setBackgroundResource(R.color.gray_background);
						holder.num
								.setBackgroundResource(R.color.gray_background);
						holder.date
								.setBackgroundResource(R.color.gray_background);
						managerClass.getGolbalutil().ismover = 0;
						v.setBackgroundColor(Color.parseColor("#FFFFFF"));
					}

					Log.i("5", "5");
					return true;
				}
			});

			return v;
		}

	}

	class ViewHolder {
		TextView plan;
		TextView date;
		TextView num;
	}

	@Override
	public void replace() {
		
		// 提示
		managerClass.getRuning().runding(getActivity(), "正在获取业务单号...");
		// 开始获取业务单号,参数为机构ID
		getRecycleCashboxCheckList().gettRecycleCashboxCheckList(
				GApplication.user.getOrganizationId());

	}

}
