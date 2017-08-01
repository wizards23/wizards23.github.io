package com.ljsw.tjbankpda.qf.fragment;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.pda.R;
import com.ljsw.tjbankpda.db.entity.Qingfenxianjin;
import com.ljsw.tjbankpda.main.ShangJiaoQingFen_o_qf;
import com.ljsw.tjbankpda.qf.application.Mapplication;
import com.ljsw.tjbankpda.qf.entity.QuanbieXinxi;
import com.ljsw.tjbankpda.qf.entity.ZhuanxiangTongji;
import com.ljsw.tjbankpda.qf.service.QingfenRenwuService;
import com.ljsw.tjbankpda.util.MySpinner;
import com.ljsw.tjbankpda.util.NumFormat;
import com.ljsw.tjbankpda.util.Table;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.manager.classs.pad.ManagerClass;

/**
 * 请领装箱界面--现金Fragment
 * @author FUHAIQING
 */
@SuppressLint("ValidFragment")
public class QinglingZhuangxiangXianjinFragment extends Fragment {
	public QinglingZhuangxiangXianjinFragment(Map<String,QuanbieXinxi> quanbieXinxi){
		this.quanbieXinxi=quanbieXinxi;
	}
	/* 定义控件 */
	private TextView tvQuanbieName;// 券别名称
	private EditText etCount;// 数量
	private ListView lvQuanbieInfo;// 券别信息详情
	private TextView tvTotalCount;// 合计
	private TextView btnAdd;// 添加按钮
	private ScrollView scXj;
	
	/* 定义全局变量 */
	private QuanbieBaseAdapter qa;
	private List<Qingfenxianjin> ltQuanbie =Mapplication.getApplication().boxLtXianjing;// 存放券别信息
	private Map<String,QuanbieXinxi> quanbieXinxi;  //券别信息
	private double totalCount;// 合计
	private MySpinner spinner;
	private String[] str_juanbie=Mapplication.getApplication().xjType;
	private ManagerClass manager;
	UpdateViewOfXianjin updateView;
  

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fg_qingling_zhuangxiang_xianjing,
				null);
		tvQuanbieName = (TextView) v.findViewById(R.id.tv_zhuangxiang_xianjing_type);
		etCount = (EditText) v.findViewById(R.id.ed_zhuangxiang_xianjing_count);
		tvTotalCount = (TextView) v.findViewById(R.id.tv_zhuangxiang_xianjing_total);
		lvQuanbieInfo = (ListView) v.findViewById(R.id.lv_zhuangxiang_xianjing_info);
		btnAdd = (TextView) v.findViewById(R.id.tv_zhuangxiang_xianjing_add);
		//scXj=(ScrollView)v.findViewById(R.id.sv_xj);
		manager=new ManagerClass();
		qa = new QuanbieBaseAdapter(ltQuanbie);
		lvQuanbieInfo.setAdapter(qa);
		tvQuanbieName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				spinner = new MySpinner(getActivity(), tvQuanbieName,
						tvQuanbieName);
				spinner.setSpinnerHeight(tvQuanbieName.getHeight() * 2);
				spinner.setList(getActivity(), str_juanbie);
				spinner.showPopupWindow(tvQuanbieName);
				spinner.setList(getActivity(), str_juanbie, 40);
			}
		});
		/**
		 * 添加的事件
		 */
		btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int weizhuangCount=getCount(tvQuanbieName.getText()+"", Mapplication.getApplication().zxLtXianjing);
				if (tvQuanbieName.getText().equals("请选择")){
					manager.getResultmsg().resultmsg(getActivity(), "请输入券别类型", false);
				}else if(TextUtils.isEmpty(etCount.getText()+"")) {
					manager.getResultmsg().resultmsg(getActivity(), "请输入券别数量", false);
				}else if(weizhuangCount-Integer.parseInt(etCount.getText()+"")<0){
					String str=tvQuanbieName.getText()+" \n未装数量为"+weizhuangCount+"";
					manager.getResultmsg().resultmsg(getActivity(), str, false);
					etCount.setText("");
				}else {
					
					String qbName = tvQuanbieName.getText()+"";
					int count = Integer.parseInt(etCount.getText().toString().trim());
					int no = -1;
					for (int i = 0; i < ltQuanbie.size(); i++) {
						if (qbName.equals(ltQuanbie.get(i).getQuanbie())) {
							no = i;   //如果该券别存在，记录券别的下标,数量直接累加在上面
							break;
						}
					}
					QuanbieXinxi quanbiexinxi=quanbieXinxi.get(qbName);  //根据券别名称从集合中获取券别的全部信息
					String quanbie=quanbiexinxi.getQuanbieName();
					String quanbieId=quanbiexinxi.getQuanbieId();
					String quanJiazhi=quanbiexinxi.getQuanJiazhi();
					if (no == -1) {
						
						ltQuanbie.add(new Qingfenxianjin(quanbie,count + "",quanbieId,quanJiazhi));
					} else {
						int a = Integer.parseInt(ltQuanbie.get(no).getShuliang());
						int allCount = a + count;   //数量累加
						ltQuanbie.set(no, new Qingfenxianjin(quanbie,allCount + "",quanbieId,quanJiazhi));
					}
					qa.notifyDataSetChanged();
					tvTotalCount.setText("合计：" + FindTotalCount(ltQuanbie));
					new TurnListviewHeight(lvQuanbieInfo);
					updateInfo();
					
					//判断现金是否装箱完毕
					//遍历数组
					int weizhuang=0;
					for (ZhuanxiangTongji xj : Mapplication.getApplication().zxLtXianjing) {
						weizhuang+=xj.getWeiZhuang();
					}
					//,如果未装的现金张数=0,则让Mapplication.getApplication().IsXianjingOK=true;
					if(weizhuang==0){
						Mapplication.getApplication().IsXianjingOK=true;
					}
				}
				etCount.setText("");//新修改的
			}
		});
		
		return v;
	}
	@Override
	public void onResume() {
		super.onResume();
		tvTotalCount.setText("合计：" + FindTotalCount(ltQuanbie));
		etCount.setText("");
		new TurnListviewHeight(lvQuanbieInfo);
	}

	class SpinerAdapter extends ArrayAdapter<String> {
		public SpinerAdapter(Context context, String[] objects) {
			super(context, android.R.layout.simple_spinner_item, objects);
		}
	}

	class QuanbieBaseAdapter extends BaseAdapter {
		private List<Qingfenxianjin> lt;
		private ViewHolder vh;

		public QuanbieBaseAdapter(List<Qingfenxianjin> lt) {
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
			LayoutInflater inflater = LayoutInflater.from(getActivity());
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
			vh.tvType.setText(lt.get(arg0).getQuanbie());
			vh.tvCount.setText(lt.get(arg0).getShuliang() + "");
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
				delete(lt.get(position).getQuanbie());
				lt.remove(position);
				qa.notifyDataSetChanged();
				new TurnListviewHeight(lvQuanbieInfo);
				tvTotalCount.setText("合计：" + FindTotalCount(ltQuanbie));
				Mapplication.getApplication().IsXianjingOK=false;

			}
		}
	}
	
	

	public String FindTotalCount(List<Qingfenxianjin> lt) {
		totalCount = 0;
		for (Qingfenxianjin b : lt) {
			double Jiazhi= Double.parseDouble(b.getQuanJiazhi().trim());
			int shuliang=Integer.parseInt(b.getShuliang().trim());
			totalCount+=Jiazhi*shuliang;
		}
		return new NumFormat().format(totalCount + "");
	}
	public int getCount(String XianjinType,List<ZhuanxiangTongji> lt){
		int count=0;
		for (ZhuanxiangTongji z : lt) {
			if (XianjinType.equals(z.getName())) {
				count=z.getWeiZhuang();
			}
		}
		return count;
	}
	private void delete(String xianjingType){
		List<ZhuanxiangTongji> ltxj=Mapplication.getApplication().zxLtXianjing;
		int position=-1;
		int newWeizhuang=-1;
		for (int i = 0; i < ltxj.size(); i++) {
			if (xianjingType.equals(ltxj.get(i).getName())) {
				int yizhuang=ltxj.get(i).getYiZhuang();
				int weizhuang=ltxj.get(i).getWeiZhuang();
				newWeizhuang=yizhuang+weizhuang;
				position=i;
			}
		}
		if(position!=-1){
			Mapplication.getApplication().zxLtXianjing.get(position).setYiZhuang(0);
			Mapplication.getApplication().zxLtXianjing.get(position).setWeiZhuang(newWeizhuang);
		}
	}
	private void updateInfo() {
		List<ZhuanxiangTongji> ltxj=Mapplication.getApplication().zxLtXianjing;
		for (int i = 0; i < ltQuanbie.size(); i++) {
			for (int j = 0; j < ltxj.size(); j++) {
				if(ltQuanbie.get(i).getQuanbie().equals(ltxj.get(j).getName()))	{
					int totalNum=ltxj.get(j).getWeiZhuang()+ltxj.get(j).getYiZhuang();
					int yzNum=Integer.parseInt(ltQuanbie.get(i).getShuliang());
					ltxj.get(j).setYiZhuang(yzNum);
					ltxj.get(j).setWeiZhuang(totalNum-yzNum);
				}
			}
		}
		Mapplication.getApplication().zxLtXianjing=ltxj;
		Mapplication.getApplication().boxLtXianjing=ltQuanbie;
	}

	public interface UpdateViewOfXianjin{
		public void UpdateViewOfXianjin(EditText et);
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			updateView=(UpdateViewOfXianjin)activity;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
