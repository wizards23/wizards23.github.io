package com.fragment.pda;

import com.clearadmin.biz.CashboxAddMoneyDetailBiz;
import com.entity.BoxDetail;
import com.example.pda.R;
import com.golbal.pda.GolbalView;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.biz.GetBoxDetailListBiz;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MoneyAndMoneyCount_fragment extends Fragment {

	private GetBoxDetailListBiz getBoxDetailList;

	public GetBoxDetailListBiz getGetBoxDetailList() {
		return getBoxDetailList = getBoxDetailList == null ? new GetBoxDetailListBiz()
				: getBoxDetailList;
	}

	private CashboxAddMoneyDetailBiz cashboxAddMoneyDetail;

	public CashboxAddMoneyDetailBiz getCashboxAddMoneyDetail() {
		return cashboxAddMoneyDetail = cashboxAddMoneyDetail == null ? new CashboxAddMoneyDetailBiz()
				: cashboxAddMoneyDetail;
	}

	ListView listview;
	String planNum; // 计划编号
	String bizName; // 业务名称
	OnClickListener clickreplace;
	TextView moneycuont; // 金额
	double num = 0;
	Ad ad;
	private ManagerClass managerClass;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		managerClass = new ManagerClass();
		// 重试单击事件
		clickreplace = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 连接超时后确定再次请求数据
				getCashboxAddMoneyDetail().getBoxAddMoneyDetail(planNum);
				managerClass.getAbnormal().remove();
			}
		};

		planNum = getArguments().getString("number");
		bizName = getArguments().getString("businName");
		Log.i("bizName111111111111", bizName);
		Log.i("planNum111111111111", planNum);
		// hand通知操作
		getCashboxAddMoneyDetail().handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// 移除获取钞箱明细提示
				managerClass.getRuning().remove();
				super.handleMessage(msg);

				switch (msg.what) {
				case 1:
					if (ad == null) {
						ad = new Ad();
						listview.setAdapter(ad);
					} else {
						ad.notifyDataSetChanged();
					}

					// 获取金额总数
					getCuont();
					moneycuont.setText(num + "万");
					break;
				case -1:
					managerClass.getAbnormal().timeout(getActivity(),
							"连接异常，要重试吗？", clickreplace);
					break;
				case -4:
					managerClass.getAbnormal().timeout(getActivity(),
							"连接超时，要重试吗？", clickreplace);
					break;
				case 0:
					break;
				}

			}
		};

		return inflater.inflate(R.layout.money_and_moneycount_fragment, null);
	}

	@Override
	public void onStart() {
		super.onStart();
		listview = (ListView) getActivity().findViewById(
				R.id.listview_boxdetial);
		moneycuont = (TextView) getActivity().findViewById(R.id.money_countNum);

		if (ad == null) {
			// 提示获取钞箱明细
			managerClass.getRuning().runding(getActivity(), "正在获取钞箱明细...");
			// 开始请求数据
			getCashboxAddMoneyDetail().getBoxAddMoneyDetail(planNum);
		} else {
			ad.notifyDataSetInvalidated();
		}

	}

	// 适配器
	class Ad extends BaseAdapter {

		@Override
		public int getCount() {
			return getCashboxAddMoneyDetail().list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return getCashboxAddMoneyDetail().list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = arg1;
			ViewHolder holder;
			if (v == null) {
				holder = new ViewHolder();
				v = GolbalView.getLF(getActivity()).inflate(
						R.layout.addmoneycount_fragment_item, null);
				holder.brand = (TextView) v
						.findViewById(R.id.moneycount_brand_fragment);
				holder.num = (TextView) v
						.findViewById(R.id.moneycount_num_fragment);
				holder.money = (TextView) v
						.findViewById(R.id.moneycount_money_fragment);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			BoxDetail box = (BoxDetail) getItem(arg0);

			holder.brand.setText(box.getBrand());
			holder.num.setText(box.getNum());

			holder.money.setText((Double.parseDouble(box.getMoney()) / 10000)
					+ "万");

			return v;
		}

	}

	static class ViewHolder {
		TextView brand;
		TextView num;
		TextView money;
	}

	/**
	 * 统计
	 */
	void getCuont() {
		for (int i = 0; i < getCashboxAddMoneyDetail().list.size(); i++) {
			double money = Double.parseDouble(getCashboxAddMoneyDetail().list
					.get(i).getMoney());
			num = num + money;
		}
		num = num / 10000;
	}

}
