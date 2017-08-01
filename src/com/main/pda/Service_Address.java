package com.main.pda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.example.pda.R;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.SureCancelButton;

public class Service_Address extends Activity implements OnClickListener {
	private EditText nameSpace, url;
	private Button sumbitsave;
	private String getsaveinfo;
	private String text_namespace = "namespace.txt";
	private String text_url = "url.txt";
	private SureCancelButton sure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setweb_address);
		init();
		sure = new SureCancelButton();
		inputSDCardinfo(text_namespace, nameSpace);
		inputSDCardinfo(text_url, url);

	}

	public void init() {
		nameSpace = (EditText) findViewById(R.id.space);
		url = (EditText) findViewById(R.id.webservice);
		sumbitsave = (Button) findViewById(R.id.changeweb);
		sumbitsave.setOnClickListener(this);
	}

	/**
	 * 写入本机SD卡 文件名 控件（获取控件输入的信息）
	 * 
	 * @param urlname
	 * @param edit
	 */
	public void outputSDCard(String textname, EditText edit) {
		try {
			boolean bool = fileIsExists(textname);
			File urlFile = Environment.getExternalStorageDirectory();
			File outputFile = new File(urlFile, textname);
			if (bool==false) {
				System.out.println("-----创建文件-----");
				outputFile.createNewFile();
				OutputStream os = new FileOutputStream(outputFile);
				os.write(edit.getText().toString().getBytes());
				inputSDCardinfo(textname, edit);
				System.out.println("-----写入完成-----");
			}else{
				System.out.println("开始写入");
				OutputStream os = new FileOutputStream(outputFile);
				os.write(edit.getText().toString().getBytes());
				inputSDCardinfo(textname, edit);
				System.out.println("-----写入完成-----");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 本机读取保存的地址 文件名 需要赋值的控件
	 * 
	 * @param urlname
	 */
	public void inputSDCardinfo(String textname, EditText edit) {
		try {
			boolean bool = fileIsExists(textname);
			if (bool==true) {
				System.out.println("-----读取文件-----");
				File file = new File(Environment.getExternalStorageDirectory(),
						textname);
				BufferedReader br = new BufferedReader(new FileReader(file));
				String readline = "";
				StringBuffer sb = new StringBuffer();
				while ((readline = br.readLine()) != null) {
					sb.append(readline);
				}
				getsaveinfo = sb.toString();
				System.out.println("=====读取:" + getsaveinfo);
				edit.setText(getsaveinfo);
				br.close();
			} else {
				outputSDCard(textname, edit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断文件是否存在 传入文件名
	 * 
	 * @param textname
	 * @return
	 */
	public boolean fileIsExists(String textname) {
		try {
			File f = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/" + textname);
			if (!f.exists()) {
				System.out.println("-----文件不存在-----");
				return false;
			}

		} catch (Exception e) {
			return false;
		}
		System.out.println("-----文件存在-----");
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.changeweb:
			if (!TextUtils.isEmpty(nameSpace.getText())) {
				outputSDCard(text_namespace, nameSpace);
			}
			if (!TextUtils.isEmpty(url.getText())) {
				outputSDCard(text_url, url);
			}
			sure.makeSuerCancel(Service_Address.this, "保存成功",
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							inputSDCardinfo(text_namespace, nameSpace);
							inputSDCardinfo(text_url, url);
							sure.remove();
						}
					}, true);
			break;

		default:
			break;
		}
	}
}
