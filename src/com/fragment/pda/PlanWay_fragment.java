package com.fragment.pda;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.application.GApplication;
import com.entity.Box;
import com.entity.ClearBox;
import com.example.pda.R;
import com.golbal.pda.GolbalView;
import com.loginsystem.biz.SystemLoginBiz;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.biz.GetEmptyRecycleCashboxInListBiz;
import com.moneyboxadmin.biz.GetPlanWayListBiz;
import com.moneyboxadmin.pda.BoxDetailInfoDo;
import com.moneyboxadmin.pda.PlanWay.PlanFragment;

public class PlanWay_fragment extends Fragment implements PlanFragment {
	// 显示路线的fragment
	Bundle bundle_listitem;
    String busin; // 业务名称
    String type; //业务类别 (在行，离行)
	ListView listview;
	OnClickListener clickReplace; // 重试事件
	OnClickListener clickReplaceback; // 重试事件
	TextView planNum;
	TextView way;
	WayAd wayad;
	LinearLayout notdata;
	Button btnNotData;
	private ManagerClass managerClass;

	// 系统登陆
	private SystemLoginBiz systemLogin;

	SystemLoginBiz getSystemLogin() {
		return systemLogin = systemLogin == null ? new SystemLoginBiz()
				: systemLogin;
	}

	// 获取路线
	private GetPlanWayListBiz planwaylist;

	GetPlanWayListBiz getPlanwaylist() {
		return planwaylist = planwaylist == null ? new GetPlanWayListBiz()
				: planwaylist;
	}

	private GetEmptyRecycleCashboxInListBiz emptyRecycleCashboxInList;

	GetEmptyRecycleCashboxInListBiz getEmptyRecycleCashboxInList() {
		return emptyRecycleCashboxInList = emptyRecycleCashboxInList == null ? new GetEmptyRecycleCashboxInListBiz()
				: emptyRecycleCashboxInList;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		managerClass = new ManagerClass();
		// 接收上一个页面传递进来的参数
		busin = getArguments().getString("business");

		// 空钞箱出库，ATM钞箱出库，钞箱装钞入库，回收钞箱入库,钞箱加钞获取 重试单击事件
		clickReplace = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				managerClass.getRuning().runding(getActivity(), "正在获取路线...");
				// 参数机构ID
				getPlanwaylist().getBoxList(busin,
						GApplication.user.getOrganizationId());
			}
		};

		// 空钞箱出库，ATM钞箱出库，钞箱装钞入库，回收钞箱入库,钞箱加钞列表
		getPlanwaylist().hand_way = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.i("移除下", "移除下");
				// 移除提示
				managerClass.getRuning().remove();
				super.handleMessage(msg);

				switch (msg.what) {
				case 1:
					notdata.setVisibility(View.GONE);
					// listview加载数据
					if (wayad == null) {
						wayad = new WayAd();
						listview.setAdapter(wayad);
					} else {
						wayad.notifyDataSetChanged();
					}

					break;
				case -1:
					managerClass.getAbnormal().timeout(getActivity(),
							"连接异常，重新连接？", clickReplace);
					notdata.setVisibility(View.GONE);
					break;
				case 0:
					// 没有数据
					notdata.setVisibility(View.VISIBLE);
					break;
				case -4:
					managerClass.getAbnormal().timeout(getActivity(),
							"连接超时，重新连接？", clickReplace);
					notdata.setVisibility(View.GONE);
					break;
				}
			}
		};

		// 重试单击事件
		clickReplaceback = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				managerClass.getRuning().runding(getActivity(), "正在获取业务单号...");
				// 参数，机构ID
				getEmptyRecycleCashboxInList().getemptyback(
						GApplication.user.getOrganizationId());

			}
		};

		// 已清回收钞箱入库
		getEmptyRecycleCashboxInList().hand_backbox = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// 移除提示
				managerClass.getRuning().remove();
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					notdata.setVisibility(View.GONE);
					// listview加载数据
					listview.setAdapter(new WayAd());
					break;
				case -1:
					notdata.setVisibility(View.GONE);
					managerClass.getAbnormal().timeout(getActivity(),
							"连接异常，重新连接？", clickReplaceback);
					break;
				case -4:
					notdata.setVisibility(View.GONE);
					managerClass.getAbnormal().timeout(getActivity(),
							"连接超时，重新连接？", clickReplaceback);
					break;
				case 0:
					notdata.setVisibility(View.VISIBLE);
					break;

				}

			}
		};

		return inflater.inflate(R.layout.planway_fragment, null);

	}

	@Override
	public void onStart() {
		super.onStart();
		planNum = (TextView) getActivity().findViewById(R.id.plan_num_fra);
		way = (TextView) getActivity().findViewById(R.id.plan_content_fra);
		listview = (ListView) getActivity().findViewById(R.id.listview_way);
		notdata = (LinearLayout) getActivity().findViewById(R.id.way_notdata);

		if (busin.equals("已清回收钞箱入库")) {
			planNum.setText("业务单号");
			way.setText("清分时间");
		} else {
			planNum.setText("加钞计划编号");
			way.setText("加钞路线");
		}

		if (busin.equals("已清回收钞箱入库")) {
			if (wayad == null) {
				// 操作友好提示
				managerClass.getRuning().runding(getActivity(), "正在获取路线...");
				// 获取路线业务数据，参数机构ID
				getEmptyRecycleCashboxInList().getemptyback(
						GApplication.user.getOrganizationId());
			} else {
				listview.setAdapter(wayad);
			}

		} else {
			if (wayad == null) {
				// 操作友好提示
				Log.i("aaaaaa", "aaaaaaaaa");
				managerClass.getRuning().runding(getActivity(), "正在获取路线...");
				// 获取路线业务数据，参数机构ID
				getPlanwaylist().getBoxList(busin,
						GApplication.user.getOrganizationId());

			} else {
				listview.setAdapter(wayad);
			}
		}

	}

	// 适配器
	class WayAd extends BaseAdapter {
		@Override
		public int getCount() {
			if (busin.equals("已清回收钞箱入库")) {
				return getEmptyRecycleCashboxInList().list.size();
			}
			return getPlanwaylist().list_box.size();
		}

		@Override
		public Object getItem(int arg0) {
			if (busin.equals("已清回收钞箱入库")) {
				return getEmptyRecycleCashboxInList().list.get(arg0);
			}
			return getPlanwaylist().list_box.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			View v = view;
			final ViewHolder holder;
			if (v == null) {
				holder = new ViewHolder();
				v = GolbalView.getLF(getActivity()).inflate(
						R.layout.c_way_item, null);
				holder.planNum = (TextView) v.findViewById(R.id.plannumber);
				holder.planContent = (TextView) v
						.findViewById(R.id.plancontent);
				holder.isfirst = (TextView) v.findViewById(R.id.isfirst);

				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}

			if (busin.equals("已清回收钞箱入库")) {
				ClearBox box = (ClearBox) getItem(arg0);
				holder.planNum.setText(box.getBizNum());
				holder.planContent.setText(box.getClearDate());
			} else {
				Box box = (Box) getItem(arg0);
				holder.planNum.setText(box.getPlanNum());
				holder.planContent.setText(box.getWay());
				holder.isfirst.setText(box.getIsFirst());
				type=box.getType();
			}

			v.setOnTouchListener(new OnTouchListener() {

				// 触摸事件
				@Override
				public boolean onTouch(View view, MotionEvent even) {

					if (MotionEvent.ACTION_DOWN == even.getAction()) {
						holder.planNum
								.setBackgroundResource(R.color.bleu_pressdown);
						holder.planContent
								.setBackgroundResource(R.color.bleu_pressdown);
					}

					if (MotionEvent.ACTION_UP == even.getAction()) {
						holder.planNum
								.setBackgroundResource(R.color.gray_background);
						holder.planContent
								.setBackgroundResource(R.color.gray_background);
						// 把当前点击的项的编号、路线、业务、放进bundle
						bundle_listitem = new Bundle();
						bundle_listitem.putString("number", holder.planNum
								.getText().toString());
						bundle_listitem.putString("way", holder.planContent
								.getText().toString());
						bundle_listitem.putString("businName", busin);
						bundle_listitem.putString("isfirst", holder.isfirst
								.getText().toString());
						if(type!=null&&!"".equals(type)){
							bundle_listitem.putString("type", type);
						}
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
						holder.planNum
								.setBackgroundResource(R.color.gray_background);
						holder.planContent
								.setBackgroundResource(R.color.gray_background);
						managerClass.getGolbalutil().ismover = 0;
					}

					return true;
				}
			});

			return v;
		}

	}

	static class ViewHolder {
		TextView planNum;
		TextView isfirst;
		TextView planContent;

	}

	@Override
	public void replace() {
		managerClass.getRuning().runding(getActivity(), "正在刷新...");
		if (busin.equals("已清回收钞箱入库")) {
			// 获取路线业务数据，参数机构ID
			getEmptyRecycleCashboxInList().getemptyback(
					GApplication.user.getOrganizationId());
		} else {
			// 获取路线业务数据，参数机构ID
			Log.i("busin", busin);
			getPlanwaylist().getBoxList(busin,
					GApplication.user.getOrganizationId());
		}

	}

}
