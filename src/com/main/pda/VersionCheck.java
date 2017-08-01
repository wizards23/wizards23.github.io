package com.main.pda;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import com.application.GApplication;
import com.example.pda.R;
import com.golbal.pda.GolbalView;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.Loading;
import com.messagebox.Runing;
import com.online.update.biz.GetPDA;
import com.online.update.biz.Online;
import com.online.update.biz.VersionInfo;
import com.service.FixationValue;
import com.sql.SQL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class VersionCheck extends Activity {

	TextView versionNum;
	Button btn;
	private GetPDA Pda;
	private ManagerClass managerClass;
	Loading on = new Loading();
	Handler handler = null; // 获取版本号
	public static boolean stopupdate = true;
	private Runing runing;
	SharedPreferences sharepre;
	String space; // 空间
	String webservice; // webservice地址
	private boolean install;

	GetPDA getPda() {
		if (Pda == null) {
			Pda = new GetPDA();
		}
		return Pda;
	}

	Online online = new Online();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 禁止休睡眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c_version_check);
		btn = (Button) findViewById(R.id.checkupdate);
		versionNum = (TextView) findViewById(R.id.version);
		runing = new Runing();
		versionNum.setText("当前版本" + getVersion());
		managerClass = new ManagerClass();
		MainActivity.list_version.add(this);

		// 更新按钮
		btn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent even) {
				switch (even.getAction()) {
				case MotionEvent.ACTION_DOWN:
					btn.setBackgroundResource(R.drawable.gray_btn_bg_press);
					break;
				case MotionEvent.ACTION_UP:
					btn.setBackgroundResource(R.drawable.gray_btn_bg);

					runing.runding(VersionCheck.this, "正在获取新版本");
					getPda().getpath(handler, VersionCheck.this);

					break;
				case MotionEvent.ACTION_CANCEL:
					btn.setBackgroundResource(R.drawable.gray_btn_bg);
					break;

				}

				return true;
			}
		});

		// 通知安装新版本
		final Handler h_load = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				// 取消计时器
				new Online().t.cancel();
				if (msg.what == 100) {
					if (!install) {
						install = true;
						Timer t = new Timer();
						t.schedule(new TimerTask() {
							@Override
							public void run() {
								installAPK();
							}
						}, 2500);
					}
				}
			}

		};

		// 获取版本号并更新
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				runing.remove();
				switch (msg.what) {
				case -1:
					managerClass.getSureCancel().makeSuerCancel(
							VersionCheck.this, "获取版本号失败,是否重新获取？",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getSureCancel().remove();
									runing.runding(VersionCheck.this, "正在获取新版本");
									getPda().getpath(handler, VersionCheck.this);
								}
							}, false);
					break;
				case 99:
					managerClass.getSureCancel().makeSuerCancel(
							VersionCheck.this, "发现新版本，是否现在更新？",
							new View.OnClickListener() {
								@Override
								public void onClick(View arg0) {
									managerClass.getSureCancel().remove();
									on.loading(VersionCheck.this, h_load);
								}
							}, false);
					break;
				case 44:
					GolbalView.toastShow(VersionCheck.this, "目前已是最高版本");
					break;
				}

			}

		};

	}

	// 安装应用程序
	public void installAPK() {
		String paths = Environment.getExternalStorageDirectory()
				+ "/PDA_Version" + "/" + VersionInfo.APKNAME;
		// String paths =
		// Environment.getExternalStorageDirectory()+"/PDA_Version"+"/PDAA20.apk";
		File file = new File(paths);
		if (file.exists()) {

			Intent intent = new Intent(Intent.ACTION_VIEW);
			try {
				intent.setDataAndType(Uri.fromFile(file),
						"application/vnd.android.package-archive");
				startActivity(intent);
				VersionCheck.this.finish();
				exit();
			} catch (Exception e) {
				e.getCause();
				System.out.println(e.getMessage());
			}
		}
	}

	public void exit() {
		for (int i = 0; i < MainActivity.list_version.size(); i++) {
			MainActivity.list_version.get(i).finish();
		}
	}

	public String getVersion() {
		String versioncode = "";
		PackageManager packageManager = VersionCheck.this.getPackageManager();
		PackageInfo info;
		try {
			info = packageManager.getPackageInfo(
					VersionCheck.this.getPackageName(), 0);
			// 当前版本号
			versioncode = info.versionName;
			Log.i("versioncode当前版本号", versioncode + "");
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}

		return versioncode;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		stopupdate = false;
		online.t.cancel();
		on.remove();
	}

	@Override
	protected void onStart() {
		super.onStart();
		stopupdate = true;
	}

}
