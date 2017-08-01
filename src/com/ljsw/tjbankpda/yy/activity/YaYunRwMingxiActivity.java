package com.ljsw.tjbankpda.yy.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pda.R;
import com.ljsw.tjbankpda.qf.entity.YaYunLb;
import com.ljsw.tjbankpda.util.Skip;
import com.manager.classs.pad.ManagerClass;


/**
 * 押运任务明细页面
 * @author Administrator 
 */
public class YaYunRwMingxiActivity extends FragmentActivity implements OnClickListener {
	private TextView    saomiao,// 扫描的跳转
						yy_wdName,//机构名称
						yy_xlName,//所属线路
						yy_sjNum,//上缴周转箱数量
						yy_qlNum,//请领周转箱数量
						yy_xlDate;//日期
	private ImageView back;
	private List<String> qlZzxlist = new ArrayList<String>(); // 请领周转箱集合
	private List<String> sjZzxlist = new ArrayList<String>(); //上缴周转箱集合
	private ManagerClass managerClass;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("进入押运RWMINGXI");
		setContentView(R.layout.activity_yayun_mingxi_s);
		managerClass = new ManagerClass();
		initView();
	}

	public void initView() {
		yy_wdName = (TextView) this.findViewById(R.id.yy_wdName);
		yy_xlName = (TextView) this.findViewById(R.id.yy_xlName);
		yy_sjNum = (TextView) this.findViewById(R.id.yy_sjNum);
		yy_qlNum = (TextView) this.findViewById(R.id.yy_qlNum);
		yy_xlDate = (TextView) this.findViewById(R.id.yy_xlDate);
		saomiao = (TextView) this.findViewById(R.id.yy_saomiao);
		back = (ImageView) this.findViewById(R.id.yayun_backS2);
		back.setOnClickListener(this);
		saomiao.setOnClickListener(this);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle!=null){
			qlZzxlist = (List<String>) bundle.getSerializable("qllist");
			sjZzxlist = (List<String>) bundle.getSerializable("sjlist");
			System.out.println("sjZzxlist:    ---"+sjZzxlist.size());
			yy_xlDate.setText(bundle.getString("pdate"));
			YaYunLb yb = (YaYunLb) bundle.getSerializable("YaYunLb");
			yy_wdName.setText(yb.getName());
			yy_xlName.setText(bundle.getString("XianLu"));
			yy_sjNum.setText(""+sjZzxlist.size()+"");
			//System.out.println("清零周转箱:"+qlZzxlist.get(0));
			yy_qlNum.setText(""+qlZzxlist.size()+"");
			//System.out.println("上缴周转箱:"+sjZzxlist.get(0));
		}
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		managerClass.getRuning().remove();
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.yy_saomiao://跳转到请领周转箱扫描
			Bundle bundle = new Bundle();
			bundle.putSerializable("qllist", (Serializable) qlZzxlist);	
			bundle.putSerializable("sjlist", (Serializable) sjZzxlist);
			managerClass.getRuning().runding(YaYunRwMingxiActivity.this, "正在开起周转箱扫描...");
			if(qlZzxlist.isEmpty() && qlZzxlist.size()==0){
				Skip.skip(YaYunRwMingxiActivity.this, SjzzxSaomiaoActivity.class, bundle, 0);
			}else{
				Skip.skip(YaYunRwMingxiActivity.this, QingLzhxSaomiaoActivity.class, bundle, 0);
			}
			break;

		case R.id.yayun_backS2:
			YaYunRwMingxiActivity.this.finish();
			break;
		}

	}
}
