package com.example.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.example.app.util.Skip;
import com.example.pda.R;

public class XitongGuanliActivity extends Activity {
	private ImageView PDAsetting, updatePass, fingergather;
	private Dialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xitongguanli);
		load();
		BtListener();
	}

	public void load() {
		PDAsetting = (ImageView) findViewById(R.id.PDAshezhi);
		updatePass = (ImageView) findViewById(R.id.updatepass);
		fingergather = (ImageView) findViewById(R.id.fingergather);
	}

	public void BtListener() {
		PDAsetting.setOnClickListener(new ButtonListner());
		updatePass.setOnClickListener(new ButtonListner());
		fingergather.setOnClickListener(new ButtonListner());
	}

	class ButtonListner implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			if (arg0 == PDAsetting) {

			}
			if (arg0 == updatePass) {
				AlertDialog.Builder builder = new Builder(
						XitongGuanliActivity.this);
				LayoutInflater lf = LayoutInflater
						.from(XitongGuanliActivity.this);
				View view = lf.inflate(R.layout.updatepass_title, null);
				Button bt1, bt2;
				bt1 = (Button) view.findViewById(R.id.dialig_bt1);
				bt1.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				bt2 = (Button) view.findViewById(R.id.dialig_bt2);
				bt2.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				builder.setView(view);
				dialog=builder.create();
				dialog.show();
			}
			if (arg0 == fingergather) {
				Skip.skip(XitongGuanliActivity.this, ZhiWenCaiJiActivity.class, null, 0);
			}

		}

	}

}
