package com.ljsw.tjbankpda.qf.fragment;

import hdjc.rfid.operator.RFID_Device;

import java.util.ArrayList;
import java.util.List;

import com.example.pda.R;
import com.ljsw.tjbankpda.db.biz.DiZhiYaPinGetNumber;
import com.ljsw.tjbankpda.main.QinglingZhuangxiangActivity;
import com.ljsw.tjbankpda.qf.application.Mapplication;
import com.ljsw.tjbankpda.qf.entity.ZhuanxiangTongji;
import com.ljsw.tjbankpda.util.DizhiYapinSaomiaoUtil;
import com.ljsw.tjbankpda.util.MessageDialog;
import com.ljsw.tjbankpda.util.TurnListviewHeight;
import com.manager.classs.pad.ManagerClass;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class QinglingZhuangxiangDizhiFragment extends Fragment {
	private ListView lvInfo;
	private EditText edNo;
	private TextView tvAdd;
	
	private DizhiYapinSaomiaoUtil dysmUtil=new DizhiYapinSaomiaoUtil();
	private DizhiBaseAdapter da;
	private List<String> ltDizhi=Mapplication.getApplication().boxLtDizhi;//存放抵质押品信息
	private ManagerClass manager=new ManagerClass();
	private RFID_Device rfid;
	private RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String no=msg.getData().getString("Num");
//			if(Mapplication.getApplication().boxLtDizhi.contains(no)){
//				manager.getResultmsg().resultmsg(getActivity(), "该抵质押品不需要录入", false);
//			}else 
			if(ltDizhi.size()>Mapplication.getApplication().zxTjDizhi.getWeiZhuang()){
				manager.getResultmsg().resultmsg(getActivity(), "抵质押品已扫完", false);
			}else{
				boolean flag=false;
				for (int i = 0; i < ltDizhi.size(); i++) {
					if(no.equals(ltDizhi.get(i))){
						flag=true;
					}
				}
				if(flag){
					manager.getResultmsg().resultmsg(getActivity(), "该抵质押品已扫描", false);
				}else{
					ltDizhi.add(no);
					new TurnListviewHeight(lvInfo);
					updateInfo();

					//判断抵质押品是否装箱完毕
					//,如果未装的重空凭证数=0,则让Mapplication.getApplication().IsZhongkongOK=true;
					if(Mapplication.getApplication().zxTjDizhi.getWeiZhuang()==0){
						Mapplication.getApplication().IsDizhiOK=true;
					}
				}
				da.notifyDataSetChanged();
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.fg_qingling_zhuangxiang_dizhi, null);
		lvInfo=(ListView)v.findViewById(R.id.lv_zhuangxiang_dizhi_no);
		edNo=(EditText)v.findViewById(R.id.ed_zhuangxiang_dizhi_count);
		tvAdd=(TextView)v.findViewById(R.id.tv_zhuangxiang_dizhi_add);
		dysmUtil.setHand(handler);
		da=new DizhiBaseAdapter();
		lvInfo.setAdapter(da);
		tvAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(edNo.getText()+"")) {
					manager.getResultmsg().resultmsg(getActivity(), "编号不能为空", false);
//				}else if(Mapplication.getApplication().boxLtDizhi.contains(edNo.getText().toString().trim())){
//					manager.getResultmsg().resultmsg(getActivity(), "该抵质押品不需要录入", false);
				}else if(ltDizhi.size()>=(Mapplication.getApplication().zxTjDizhi.getWeiZhuang()+Mapplication.getApplication().zxTjDizhi.getYiZhuang())){
					manager.getResultmsg().resultmsg(getActivity(), "抵质押品已扫完", false);
				}else {
					String no=edNo.getText()+"";
					boolean flag=false;
					for (int i = 0; i < ltDizhi.size(); i++) {
						if(no.equals(ltDizhi.get(i))){
							flag=true;
						}
					}
					if(flag){
						manager.getResultmsg().resultmsg(getActivity(), "该抵质押品已录入", false);
					}else{
						ltDizhi.add(no);
						da.notifyDataSetChanged();
						new TurnListviewHeight(lvInfo);
						updateInfo();
						edNo.setText("");
						//判断抵质押品是否装箱完毕
						//,如果未装的重空凭证数=0,则让Mapplication.getApplication().IsZhongkongOK=true;
						if(Mapplication.getApplication().zxTjDizhi.getWeiZhuang()==0){
							Mapplication.getApplication().IsDizhiOK=true;
						}
					}
				}
			}
		});
		return v;
	}
	@Override
	public void onResume() {
		super.onResume();
		new TurnListviewHeight(lvInfo);
		getRfid().addNotifly(dysmUtil);
		//getRfid().scanOpen();
	}
	@Override
	public void onPause() {
		super.onPause();
		//getRfid().scanclose();
	}
	
	class DizhiBaseAdapter extends BaseAdapter{
		//private List<String> lt;
		private ViewHolder vh;
		
//		public DizhiBaseAdapter(List<String> lt) {
//			super();
//			this.lt = lt;
//		}
		@Override
		public int getCount() {
			return ltDizhi.size();
		}
		@Override
		public Object getItem(int arg0) {
			return ltDizhi.get(arg0);
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
			vh.tvType.setText(ltDizhi.get(arg0));
			vh.tvCount.setVisibility(View.GONE);
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
				int weizhuang=Mapplication.getApplication().zxTjDizhi.getWeiZhuang();
				int yizhuang=Mapplication.getApplication().zxTjDizhi.getYiZhuang();
				weizhuang++;
				yizhuang--;
				Mapplication.getApplication().zxTjDizhi.setWeiZhuang(weizhuang);
				Mapplication.getApplication().zxTjDizhi.setYiZhuang(yizhuang);
				ltDizhi.remove(position);
				da.notifyDataSetChanged();
				new TurnListviewHeight(lvInfo);
				Mapplication.getApplication().IsDizhiOK=false;
				
			}
		}
	}
	private void updateInfo() {
		Mapplication.getApplication().ltDizhiNum=ltDizhi;
		ZhuanxiangTongji dizhi=Mapplication.getApplication().zxTjDizhi;
		int totalDzCount=dizhi.getWeiZhuang()+dizhi.getYiZhuang();
		int yzdzCount=ltDizhi.size();
		dizhi.setYiZhuang(yzdzCount);
		dizhi.setWeiZhuang(totalDzCount-yzdzCount);
		Mapplication.getApplication().zxTjDizhi=dizhi;
		Mapplication.getApplication().boxLtDizhi=ltDizhi;
	}
	
	
}
