package com.ljsw.tjbankpda.util;

import com.example.pda.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;



public class MessageDialog {
	private Context context;
	
	public MessageDialog(Context context) {
		super();
		this.context = context;
	}

	public void showMessag(String msg){
		AlertDialog.Builder builder=new Builder(context);
		LayoutInflater inflater=LayoutInflater.from(context);
		View v=inflater.inflate(R.layout.dialog_message, null);
		TextView tvMsg=(TextView)v.findViewById(R.id.tv_dialog_tip);
		tvMsg.setText(msg);
		builder.setView(v);
		builder.create().show();
	}

}
