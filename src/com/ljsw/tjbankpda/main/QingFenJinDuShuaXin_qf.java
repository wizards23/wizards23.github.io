package com.ljsw.tjbankpda.main;

import java.util.ArrayList;
import java.util.List;
import com.example.pda.R;
import android.app.Activity;
import android.os.Bundle;
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

import com.ljsw.tjbankpda.qf.entity.PeiSongDan;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.TurnListviewHeight;

public class QingFenJinDuShuaXin_qf extends FragmentActivity implements OnClickListener{
	private ImageView back;
	private Button saomiao;
	private ListView wlist, ylist;
	List<PeiSongDan> wlistData=new ArrayList<PeiSongDan>();
	List<PeiSongDan> ylistData=new ArrayList<PeiSongDan>();
	WeiQingAdapter wadapter;
	YiQingAdapter yadapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_qingfenjindushuaxin);
		getwlistData();
		getylistData();
		load();
		wadapter = new WeiQingAdapter();
		yadapter = new YiQingAdapter();
		wlist.setAdapter(wadapter);
		new TurnListviewHeight(wlist);
		ylist.setAdapter(yadapter);
		new TurnListviewHeight(ylist);
	}
	
	public void load() {
		back = (ImageView) findViewById(R.id.qf_jindu_shuaxin_back);
		back.setOnClickListener(this);
		saomiao = (Button) findViewById(R.id.qf_jindu_shuaxin_chuku);
		saomiao.setOnClickListener(this);
		wlist = (ListView) findViewById(R.id.qf_jindu_shuaxin_weiqing_list);
		ylist = (ListView) findViewById(R.id.qf_jindu_shuaxin_yiqing_list);
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.qf_jindu_shuaxin_back:
			QingFenJinDuShuaXin_qf.this.finish();
			break;
		case R.id.qf_jindu_shuaxin_chuku:
		Skip.skip(this, ZhouZhuanXiangSaoMiao_qf.class, null, 0);
		break;
		default:
			break;
		}
	}
	
	class WeiQingAdapter extends BaseAdapter {
		WeiQingHolder wq;
		LayoutInflater lf = LayoutInflater.from(QingFenJinDuShuaXin_qf.this);

		@Override
		public int getCount() {
			return wlistData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return wlistData.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				wq = new WeiQingHolder();
				arg1 = lf.inflate(R.layout.adapter_jihuadan_xianjin, null);
				wq.danhao = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_xianjin_juanbie);
				wq.wangdian = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_xianjin_count);
				arg1.setTag(wq);
			} else {
				wq = (WeiQingHolder) arg1.getTag();
			}
			wq.danhao.setText(wlistData.get(arg0).getDanhao());
			wq.wangdian.setText(wlistData.get(arg0).getWangdian());
			return arg1;
		}

	}
	public static class WeiQingHolder {
		TextView danhao,wangdian;
	}
	
	class YiQingAdapter extends BaseAdapter {
		YiQingHolder yh;
		LayoutInflater lf = LayoutInflater.from(QingFenJinDuShuaXin_qf.this);

		@Override
		public int getCount() {
			return ylistData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return ylistData.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg1 == null) {
				yh = new YiQingHolder();
				arg1 = lf.inflate(R.layout.adapter_jihuadan_xianjin, null);
				yh.danhao = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_xianjin_juanbie);
				yh.wangdian = (TextView) arg1
						.findViewById(R.id.adapter_jihuadan_xianjin_count);
				arg1.setTag(yh);
			} else {
				yh = (YiQingHolder) arg1.getTag();
			}
			yh.danhao.setText(ylistData.get(arg0).getDanhao());
			yh.wangdian.setText(ylistData.get(arg0).getWangdian());
			return arg1;
		}

	}
	public static class YiQingHolder {
		TextView danhao,wangdian;
	}
	
	
	public void getwlistData() {
		for (int i = 0; i < 6; i++) {
			wlistData.add(new PeiSongDan("ZZSX0000"+i, "西青"+i+"线"));
		}
	}

	public void getylistData() {
		for (int i = 0; i < 5; i++) {
			ylistData.add(new PeiSongDan("ZZSX0000"+i, "红旗"+i+"线"));
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			QingFenJinDuShuaXin_qf.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
