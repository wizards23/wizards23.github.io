package com.example.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.GApplication;
import com.example.app.entity.KuanXiangChuRu;
import com.example.app.util.Skip;
import com.example.pda.R;
import com.manager.classs.pad.ManagerClass;
import com.o.service.KuanxiangChuruService;
/**
 * 款箱早送出库|晚收入库明细用户页面
 * @author Administrator
 *
 */
public class KuanXiangZaochuMingxiActivity extends Activity {
	private Button bt_mingxi, bt_chuku;
//	private ImageView back;
	private TextView tv_chaoche, tv_wangdian, tv_kuanxiang, tv_date,
					ym_show;//页面标题显示文字
	KuanXiangChuRu kx;
	KuanXiangChuRu zaochu;
	KuanXiangChuRu wanru;
	private ManagerClass manager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kuanxiangzaochumingxi);	
		manager = new ManagerClass();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		load();
		ButtonListener();
	}
	
	public void load() {
		ym_show = (TextView) this.findViewById(R.id.ym_show);
		bt_mingxi = (Button) findViewById(R.id.zaochu_button1);
		bt_chuku = (Button) findViewById(R.id.zaochu_button2);
	//	back = (ImageView) findViewById(R.id.zaochu_back);
		// tv_chaoche,tv_wangdian,tv_kuanxiang,tv_date;
		tv_chaoche = (TextView) findViewById(R.id.tv_chaoche);
		tv_wangdian = (TextView) findViewById(R.id.tv_wangdiancount);
		tv_kuanxiang = (TextView) findViewById(R.id.tv_kuanxiangcount);
		tv_date = (TextView) findViewById(R.id.tv_date);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		zaochu = (KuanXiangChuRu) bundle.getSerializable("zaochu");
		if (zaochu == null) {
			wanru = (KuanXiangChuRu) bundle.getSerializable("wanru");
			GApplication.kxc = wanru;
			kx = wanru;
		} else {
			GApplication.kxc = zaochu;
			kx = zaochu;
		}
		if(GApplication.churuShow==1){
			ym_show.setText("款箱早送出库明细");
			bt_chuku.setText("出库操作");
		}else if(GApplication.churuShow==2){
			ym_show.setText("款箱晚收入库明细");
			bt_chuku.setText("入库操作");
		}
		//GApplication.mingxi_list
		tv_chaoche.setText(kx.getChaochexianlu());
		tv_wangdian.setText(kx.getWangdiancount());
		tv_kuanxiang.setText(kx.getKuanxiangcount());
		tv_date.setText(kx.getPeisongdate());

	}
	public void ButtonListener() {
		bt_mingxi.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Skip.skip(KuanXiangZaochuMingxiActivity.this,KuanXiangMingXiActivity.class, null, 0);
			}
		});
		bt_chuku.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				manager.getRuning().runding(KuanXiangZaochuMingxiActivity.this, "扫箱功能开启中，请稍后...");
				Skip.skip(KuanXiangZaochuMingxiActivity.this,
						KuanXiangChuKuActivity.class, null, 0);
			}
		});
		/*back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				KuanXiangZaochuMingxiActivity.this.finish();
			}
		});*/
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			KuanXiangZaochuMingxiActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		manager.getRuning().remove();
	}

}
