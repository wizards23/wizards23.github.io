package com.ljsw.tjbankpda.main;
import com.example.pda.R;
import java.util.ArrayList;
import java.util.List;

import com.ljsw.tjbankpda.main.QinglingZhouzhuanxiangluruActivity.ZhuangxiangInfoAdapter;
import com.ljsw.tjbankpda.qf.application.Mapplication;
import com.ljsw.tjbankpda.qf.entity.Box;
import com.ljsw.tjbankpda.qf.entity.ZhuanxiangTongji;
import com.ljsw.tjbankpda.util.MoneyType;
import com.ljsw.tjbankpda.util.NumFormat;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 装箱统计界面
 * @author FUHAIQING
 */
public class QinglingZhuangxiangTongjiActivity extends FragmentActivity {
	/*定义控件变量*/
	private TextView tvTotalXianjin;//总现金数量
	private TextView tvTotalZhongkong;//总重空凭证数量
	private TextView tvTotalDizhi;//总抵质押品数量
	private TextView tvDizhiYizhuang;//抵质押品已装数量
	private TextView tvDizhiWeizhuang;//抵质押品未装数量
	private LinearLayout llXianjin;//现金信息
	private LinearLayout llZhongkong;//重空凭证信息
	private ZhuanxiangTongji zxTjDizhi;//抵质押品信息
	private ImageView ivBack;
	
	private Bundle bundle;
	
	/*定义全局变量*/
	private List<ZhuanxiangTongji> ltXianjin=new ArrayList<ZhuanxiangTongji>();
	private List<ZhuanxiangTongji> ltZhongkong=new ArrayList<ZhuanxiangTongji>();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_qingling_zhuangxiang_tongji);
		llXianjin=(LinearLayout)findViewById(R.id.ll_qingling_zhuangxiang_tongji_xianjinInfo);
		llZhongkong=(LinearLayout)findViewById(R.id.ll_qingling_zhuangxiang_tongji_zhongkongInfo);
		tvTotalXianjin=(TextView)findViewById(R.id.tv_qingling_zhuangxiang_tongji_xianjing_total);
		tvTotalZhongkong=(TextView)findViewById(R.id.tv_qingling_zhuangxiang_tongji__zhongkong_total);
		tvTotalDizhi=(TextView)findViewById(R.id.tv_qingling_zhuangxiang_tongji_dizhi_total);
		tvDizhiYizhuang=(TextView)findViewById(R.id.tv_qingling_zhuangxiang_tongji_dizhi_yizhuang);
		tvDizhiWeizhuang=(TextView)findViewById(R.id.tv_qingling_zhuangxiang_tongji_dizhi_weizhuang);
		ivBack=(ImageView)findViewById(R.id.iv_qingling_zhuangxiang_tongji_back);
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				QinglingZhuangxiangTongjiActivity.this.finish();
			}
		});
		getDate();
		
		bundle=super.getIntent().getExtras();
		String xianjingZhongji=bundle.getString("zongMoney");
		String zhongkongZhongji=bundle.getString("zongZhongkong");
		String dizhiZhongji=bundle.getString("zongDizhi");
		
		tvTotalXianjin.setText(new NumFormat().format(xianjingZhongji));
		for (int i = 0; i < ltXianjin.size(); i++) {
			View v=LayoutInflater.from(this).inflate(R.layout.item_qingling_zhuangxiang_tongji, null);
			TextView tvType=(TextView)v.findViewById(R.id.tv_item_qingling_zhuangxiang_tongji_name);
			TextView tvYizhuang=(TextView)v.findViewById(R.id.tv_item_qingling_zhuangxiang_tongji_yizhuang);
			TextView tvWeizhuang=(TextView)v.findViewById(R.id.tv_item_qingling_zhuangxiang_tongji_weizhuang);
			tvType.setText(ltXianjin.get(i).getName());
			tvYizhuang.setText(ltXianjin.get(i).getYiZhuang()+"");
			tvWeizhuang.setText(ltXianjin.get(i).getWeiZhuang()+"");
			llXianjin.addView(v);
		}
		tvTotalZhongkong.setText(zhongkongZhongji+"");
		for (int i = 0; i < ltZhongkong.size(); i++) {
			View v=LayoutInflater.from(this).inflate(R.layout.item_qingling_zhuangxiang_tongji, null);
			TextView tvType=(TextView)v.findViewById(R.id.tv_item_qingling_zhuangxiang_tongji_name);
			TextView tvYizhuang=(TextView)v.findViewById(R.id.tv_item_qingling_zhuangxiang_tongji_yizhuang);
			TextView tvWeizhuang=(TextView)v.findViewById(R.id.tv_item_qingling_zhuangxiang_tongji_weizhuang);
			tvType.setText(ltZhongkong.get(i).getName());
			tvYizhuang.setText(ltZhongkong.get(i).getYiZhuang()+"");
			tvWeizhuang.setText(ltZhongkong.get(i).getWeiZhuang()+"");
			llZhongkong.addView(v);
		}
		tvTotalDizhi.setText(dizhiZhongji+"");
		tvDizhiYizhuang.setText(zxTjDizhi.getYiZhuang()+"");
		tvDizhiWeizhuang.setText(zxTjDizhi.getWeiZhuang()+"");
	}

	private void getDate(){
		
		ltXianjin=Mapplication.getApplication().zxLtXianjing;
		ltZhongkong=Mapplication.getApplication().zxLtZhongkong;
		zxTjDizhi=Mapplication.getApplication().zxTjDizhi;
//		int xjCount=0;
//		for (ZhuanxiangTongji xianjin : ltXianjin) {
//			if(MoneyType.A100.equals(xianjin.getName())){
//				xjCount+=(xianjin.getWeiZhuang()*100+xianjin.getYiZhuang()*100);
//			}else if(MoneyType.A50.equals(xianjin.getName())){
//				xjCount+=(xianjin.getWeiZhuang()*50+xianjin.getYiZhuang()*50);
//			}else if(MoneyType.A20.equals(xianjin.getName())){
//				xjCount+=(xianjin.getWeiZhuang()*20+xianjin.getYiZhuang()*20);
//			}else if(MoneyType.A10.equals(xianjin.getName())){
//				xjCount+=(xianjin.getWeiZhuang()*10+xianjin.getYiZhuang()*10);
//			}
//		}
//		totalXianjing=xjCount+"";
//		int zkCount=0;
//		for (ZhuanxiangTongji zk : ltZhongkong) {
//			zkCount+=zk.getWeiZhuang()+zk.getYiZhuang();
//		}
//		totalZhongkong=zkCount+"";
//		totalDizhi=zxTjDizhi.getYiZhuang()+zxTjDizhi.getWeiZhuang()+"";
		tvDizhiWeizhuang.setText(zxTjDizhi.getWeiZhuang()+"");
		tvDizhiYizhuang.setText(zxTjDizhi.getYiZhuang()+"");
	}

}
