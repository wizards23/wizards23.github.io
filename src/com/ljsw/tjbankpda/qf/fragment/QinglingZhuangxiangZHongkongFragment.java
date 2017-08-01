package com.ljsw.tjbankpda.qf.fragment;

import java.util.ArrayList;
import java.util.List;

import com.example.pda.R;
import com.ljsw.tjbankpda.qf.application.Mapplication;
import com.ljsw.tjbankpda.qf.entity.Box;
import com.ljsw.tjbankpda.qf.entity.ZhuanxiangTongji;
import com.ljsw.tjbankpda.util.MessageDialog;
import com.ljsw.tjbankpda.util.MySpinner;
import com.ljsw.tjbankpda.util.NumFormat;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.manager.classs.pad.ManagerClass;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class QinglingZhuangxiangZHongkongFragment extends Fragment {
	/*定义控件变量*/
	private TextView tvYiluru;//已录入数量
	private TextView tvWeiluru;//未录入数量
	private TextView tvAdd;
	private ListView lvInfo;//凭证信息
	private TextView tvType;
	private EditText edCount;
	
	/*定义全局变量*/
	private PinzhengBaseAdapter pa;
	private ManagerClass manager;
	private List<Box> ltPinzheng=Mapplication.getApplication().boxLtZhongkong;//存放券别信息
	private MySpinner spinner;
	private String[] str_juanbie=Mapplication.getApplication().zkType;//重空凭证类型
	private int yiLuru;//已录入数量
	private int weiLuru;//未录入数量
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.fg_qingling_zhuangxiang_zhongkong, null);
		tvYiluru=(TextView)v.findViewById(R.id.tv_zhuangxiang_zhongkong_yiluru);
		tvWeiluru=(TextView)v.findViewById(R.id.tv_zhuangxiang_zhongkong_weiluru);
		tvAdd=(TextView)v.findViewById(R.id.tv_zhuangxiang_zhongkong_add);
		lvInfo=(ListView)v.findViewById(R.id.lv_zhuangxiang_zhongkong_info);
		tvType=(TextView)v.findViewById(R.id.tv_zhuangxiang_zhongkong_type);
		edCount=(EditText)v.findViewById(R.id.ed_zhuangxiang_zhongkong_count);
		
		manager=new ManagerClass();
		updateInfo();
		tvType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				spinner = new MySpinner(getActivity(), tvType,
						tvType);
				spinner.setSpinnerHeight(tvType.getHeight() * 2);
				spinner.setList(getActivity(), str_juanbie);
				spinner.showPopupWindow(tvType);
				spinner.setList(getActivity(), str_juanbie, 40);
			}
		});
		pa=new PinzhengBaseAdapter(ltPinzheng);
		lvInfo.setAdapter(pa);
		tvAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int weizhuangCount=getCount(tvType.getText()+"", Mapplication.getApplication().zxLtZhongkong);
				if(tvType.getText().equals("请选择")){//判断凭证类型有没有选择
					manager.getResultmsg().resultmsg(getActivity(), "请选择凭证类型", false);
				} else if (TextUtils.isEmpty(edCount.getText()+"")) {//判断凭证数量是否为空
					manager.getResultmsg().resultmsg(getActivity(), "请输入凭证数量", false);
				}else if(Integer.parseInt(edCount.getText()+"")>weizhuangCount){
					String str=tvType.getText()+" \n未装数量为"+weizhuangCount;
					manager.getResultmsg().resultmsg(getActivity(), str, false);
					edCount.setText("");
				}else {
					String qbName = tvType.getText()+"";
					int count = Integer.parseInt(edCount.getText().toString().trim());
					int no = -1;
					for (int i = 0; i < ltPinzheng.size(); i++) {
						if (qbName.equals(ltPinzheng.get(i).getType())) {
							no = i;
						}
					}
					if (no == -1) {
						ltPinzheng.add(new Box(qbName, count + ""));
					} else {
						int a = Integer.parseInt(ltPinzheng.get(no).getCount());
						int allCount = a + count;
						ltPinzheng.set(no, new Box(qbName, allCount + ""));
					}
					pa.notifyDataSetChanged();
					new TurnListviewHeight(lvInfo);
					updateInfo();
					tvYiluru.setText(yiLuru+"");
					tvWeiluru.setText(weiLuru+"");
					//判断重空凭证是否装箱完毕
					//遍历数组
					int weizhuang=0;
					for (ZhuanxiangTongji zk : Mapplication.getApplication().zxLtZhongkong) {
						weizhuang+=zk.getWeiZhuang();
					}
					//,如果未装的重空凭证数=0,则让Mapplication.getApplication().IsZhongkongOK=true;
					if(weizhuang==0){
						Mapplication.getApplication().IsZhongkongOK=true;
					}
				}
			}
		});
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		edCount.setText("");
		tvYiluru.setText(yiLuru+"");
		tvWeiluru.setText(weiLuru+"");
		new TurnListviewHeight(lvInfo);
	}

	class PinzhengBaseAdapter extends BaseAdapter{
		private List<Box> lt;
		private ViewHolder vh;
		
		public PinzhengBaseAdapter(List<Box> lt) {
			super();
			this.lt = lt;
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
			LayoutInflater inflater=LayoutInflater.from(getActivity());
			if(v==null){
				v=inflater.inflate(R.layout.item_qingling_zhuangxiang, null);
				vh=new ViewHolder();
				vh.tvType=(TextView)v.findViewById(R.id.tv_item_qingling_zhuangxiang_type);
				vh.tvCount=(TextView)v.findViewById(R.id.tv_item_qingling_zhuangxiang_count);
				vh.tvDel=(TextView)v.findViewById(R.id.tv_item_qingling_zhuangxiang_delete);
				v.setTag(vh);
			}else{
				vh=(ViewHolder) v.getTag();
			}
			vh.tvType.setText(lt.get(arg0).getType());
			vh.tvCount.setText(lt.get(arg0).getCount()+"");
			vh.tvDel.setOnClickListener(new QuanbieDelListener(arg0));
			return v;
		}

		public class ViewHolder{
			TextView tvType;
			TextView tvCount;
			TextView tvDel;
		}
		class QuanbieDelListener implements OnClickListener{
			private int position;
			
			public QuanbieDelListener(int position) {
				super();
				this.position = position;
			}
			@Override
			public void onClick(View arg0) {
				delete(lt.get(position).getType());
				lt.remove(position);
				pa.notifyDataSetChanged();
				new TurnListviewHeight(lvInfo);
				Mapplication.getApplication().IsZhongkongOK=false;
				
			}
		}
	}
	public int getCount(String type,List<ZhuanxiangTongji> lt){
		int count=0;
		for (ZhuanxiangTongji z : lt) {
			if (type.equals(z.getName())) {
				count=z.getWeiZhuang();
			}
		}
		return count;
	}
	private void delete(String xianjingType){
		List<ZhuanxiangTongji> ltzk=Mapplication.getApplication().zxLtZhongkong;
		int position=-1;
		int newWeizhuang=-1;
		for (int i = 0; i < ltzk.size(); i++) {
			if (xianjingType.equals(ltzk.get(i).getName())) {
				int yizhuang=ltzk.get(i).getYiZhuang();
				int weizhuang=ltzk.get(i).getWeiZhuang();
				newWeizhuang=yizhuang+weizhuang;
				position=i;
			}
		}
		if(position!=-1){
			Mapplication.getApplication().zxLtZhongkong.get(position).setYiZhuang(0);
			Mapplication.getApplication().zxLtZhongkong.get(position).setWeiZhuang(newWeizhuang);
		}
	}
	private void updateInfo() {
		List<ZhuanxiangTongji> ltzk=Mapplication.getApplication().zxLtZhongkong;
		for (int i = 0; i < ltPinzheng.size(); i++) {
			for (int j = 0; j < ltzk.size(); j++) {
				if(ltPinzheng.get(i).getType().equals(ltzk.get(j).getName()))	{
					int totalNum=ltzk.get(j).getWeiZhuang()+ltzk.get(j).getYiZhuang();
					int yzNum=Integer.parseInt(ltPinzheng.get(i).getCount());
					ltzk.get(j).setYiZhuang(yzNum);
					ltzk.get(j).setWeiZhuang(totalNum-yzNum);
				}
			}
		}
		yiLuru=0;
		weiLuru=0;
		for (ZhuanxiangTongji z : ltzk) {
			yiLuru+=z.getYiZhuang();
			weiLuru+=z.getWeiZhuang();
		}
		Mapplication.getApplication().zxLtZhongkong=ltzk;
		Mapplication.getApplication().boxLtZhongkong=ltPinzheng;
	}
}
