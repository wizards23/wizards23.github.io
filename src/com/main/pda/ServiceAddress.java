package com.main.pda;

import com.example.pda.R;
import com.golbal.pda.GolbalView;
import com.messagebox.SureCancelButton;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ServiceAddress extends Activity {
	
	EditText space;
	EditText webservice;
	Button btn;
	ImageView back;
	SharedPreferences sharepre;
	SureCancelButton sure;
	Editor editor;
	String sp="";
	String service="";
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setweb_address);
		
		space = (EditText) findViewById(R.id.space);
		webservice = (EditText) findViewById(R.id.webservice);
		btn = (Button) findViewById(R.id.changeweb);
		back = (ImageView) findViewById(R.id.webserviceid);		
						
		sharepre = getSharedPreferences("web", Context.MODE_PRIVATE);
		
		try {
			sp = sharepre.getString("space","").toString();
			service = sharepre.getString("webservice","").toString();
		} catch (Exception e) {
			e.printStackTrace();
		  System.out.println(e.getMessage());
		}
		
		
		if(sp=="" && service==""){
		GolbalView.webServiceAddress = true;
		editor = sharepre.edit();
		editor.putString("space","http://service.timer.cashman.poka.cn");
		editor.putString("webservice","http://http://192.168.1.121:8080/CashWebServices/webservice/cash_pda");
		editor.commit();
		}
		
		
		btn.setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View view, MotionEvent even) {
				
				switch(even.getAction()){
				case MotionEvent.ACTION_DOWN:
					btn.setBackgroundResource(R.drawable.buttom_select_press);
					break;
				case MotionEvent.ACTION_UP:
					btn.setBackgroundResource(R.drawable.buttom_selector_bg);
					editor = sharepre.edit();
					editor.putString("space",space.getText().toString());
					editor.putString("webservice",webservice.getText().toString());
					
					if(editor.commit()){
						if(sure==null){
							sure = new SureCancelButton();
						}
						sure.makeSuerCancel(ServiceAddress.this,"保存成功",new View.OnClickListener() {							
							@Override
							public void onClick(View arg0) {
								sure.remove();
							}
						}, true);
					}
					
					break;
					
				case MotionEvent.ACTION_CANCEL:
					btn.setBackgroundResource(R.drawable.buttom_selector_bg);	
					break;
				
				}
				return true;
			}
		});
		
		back.setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View view, MotionEvent even) {
				
				switch(even.getAction()){
				case MotionEvent.ACTION_DOWN:
					back.setBackgroundResource(R.drawable.back_cirle_press);
					break;
				case MotionEvent.ACTION_UP:
					back.setBackgroundResource(R.drawable.back_cirle);
					//ServiceAddress.this.finish();						
					break;
					
				case MotionEvent.ACTION_CANCEL:
					back.setBackgroundResource(R.drawable.back_cirle);
					break;
				
				}
				return true;
			}
		});
		
		
	}
	@Override
	protected void onStart() {
		super.onStart();
		 sp = sharepre.getString("space","").toString();
		 service = sharepre.getString("webservice","").toString();
		Log.i("sp",sp);
		Log.i("service",service);
		space.setText(sp);
		webservice.setText(service);
				
	}
}
