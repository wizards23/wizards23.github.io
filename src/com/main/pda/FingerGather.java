package com.main.pda;

import hdjc.rfid.operator.IRFID_Device;
import hdjc.rfid.operator.RFID_Device;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.application.GApplication;
import com.clearadmin.pda.BackMoneyBoxCount;
import com.example.pda.R;
import com.golbal.pda.CrashHandler;
import com.golbal.pda.GolbalUtil;
import com.golbal.pda.GolbalView;
import com.imple.getnumber.GetFingerValue;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.Abnormal;
import com.messagebox.MenuShow;
import com.messagebox.Runing;
import com.messagebox.SureCancelButton;
import com.poka.device.ShareUtil;
import com.systemadmin.biz.FingerGatherBiz;
import com.systemadmin.biz.GetNaneNumByUseridBiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.pm.PackageParser.NewPermissionInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.AlteredCharSequence;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FingerGather extends Activity implements OnTouchListener {

	String fingerNumberText;   //编号
	TextView spinnertext;   //手指文本框 
	TextView fingerUserName;   //姓名
	EditText fingerUserNumber;  //编号输入框
	ImageView back;   //返回
	ListView listview;
	Button btn_getname;  //确定
	View popuv;  //下拉列表视图
	View vtoast;
	ImageView finger_image;   //指纹
	Ad ad;
	PopupWindow mpop = null;
	IRFID_Device irfid;
	View.OnClickListener nameclick;
	View.OnClickListener fingerclick;
	TextView resultText;
	ImageView success;
	ImageView fail;
	Scan scan;  //延迟开启扫描
	int count=0;  //按压手指次数
	String finger_num;  //手指编号
    byte[] firstFinger = null;
    boolean is = false;
    
    private ManagerClass managerClass;
	
	 List<String> list_finger = new ArrayList<String>() ;	//手指集合

	private GetNaneNumByUseridBiz NaneNumByUserid;		
	 GetNaneNumByUseridBiz getNaneNumByUserid() {
		return NaneNumByUserid =NaneNumByUserid==null?new GetNaneNumByUseridBiz():NaneNumByUserid;
	}

	FingerGatherBiz fingerbiz; 			
	FingerGatherBiz getFingerbiz() {
		return fingerbiz=fingerbiz==null?new FingerGatherBiz():fingerbiz;
	}
	 
	public void setFingerbiz(FingerGatherBiz fingerbiz) {		
		this.fingerbiz = fingerbiz;
	}
	
	SureCancelButton suercancel;
	SureCancelButton getSuercancel(){
		if(suercancel==null){
			suercancel = new SureCancelButton();
		}
		return suercancel;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.c_finger_gather);
		
		 //全局异常处理
		// CrashHandler.getInstance().init(this);
		
		managerClass = new ManagerClass();
		spinnertext = (TextView) findViewById(R.id.spinnertext);
		fingerUserNumber = (EditText) findViewById(R.id.finger_usernumber);
		resultText = (TextView) findViewById(R.id.fingerGather_text);
		success = (ImageView) findViewById(R.id.finger_success);
		fail= (ImageView) findViewById(R.id.finger_fail);
		fingerUserName = (TextView) findViewById(R.id.finger_username);
		btn_getname = (Button) findViewById(R.id.finger_getname);
		back = (ImageView) findViewById(R.id.finger_back);
		finger_image = (ImageView) findViewById(R.id.finger_images);
		
		fingerInit();
		
		//1.5秒后开启扫描
		scan = new Scan();
		scan.scan();
		scan.handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
			super.handleMessage(msg);
			irfid = new RFID_Device();
			irfid.addNotifly(new GetFingerValue());  //添加通知 	
			}
			
		};
		
		spinnertext.setOnTouchListener(this);
		btn_getname.setOnTouchListener(this);
		back.setOnTouchListener(this);
		
		
		//获取指纹通知操作
		GetFingerValue.handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle;
				if(msg.what==1){
					//验证是否选择了手指和编号是否填写
					if(check()){
						return;
					}
					bundle = msg.getData();
					vtoast =GolbalView.getLF(FingerGather.this).inflate(R.layout.toast,null);
					TextView text = (TextView) vtoast.findViewById(R.id.toast);
					
					if(bundle!=null && bundle.getString("finger").equals("正在获取指纹特征值！")){
//						Toast toast = new Toast(FingerGather.this);
//						toast.setGravity(Gravity.CENTER, 0, 0);						
//						text.setText("正在获取指纹特征值！");
//						toast.setView(text);
//						toast.setDuration(300);
//						toast.show();
					}else if(bundle!=null && bundle.getString("finger").equals("获取指纹特征值成功！")){
						finger_image.setImageBitmap(ShareUtil.finger_gather);
//						Toast toast = new Toast(FingerGather.this);
//						toast.setGravity(Gravity.CENTER, 0, 0);
//						text.setText("获取指纹特征值成功！");
//						toast.setView(text);
//						toast.setDuration(300);
//						toast.show();											
					//保存第一次按压指纹的结果
					if(count==0){
						count++;
						finger_num = spinnertext.getText().toString();
						firstFinger = ShareUtil.ivalBack;
						resultText.setText("请再次按压手指");
						fail.setVisibility(View.GONE);
						success.setVisibility(View.GONE);
					}else{
						//指纹采集:参数 userId:用户ID finger:手指  cValue:特征值1 cValue:特征值2
						managerClass.getRuning().runding(FingerGather.this, "请等待...");
						getFingerbiz().fingerprintInput(fingerNumberText,fingerInt(),firstFinger, ShareUtil.ivalBack);		
						}													
					}														
				}
			}
			
		};
		
		
		
		
		//重新采集事件
		fingerclick = new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
			managerClass.getAbnormal().remove();
			managerClass.getRuning().runding(FingerGather.this, "请等待...");
			//指纹采集:参数 userId:用户ID finger:手指  cValue:特征值1 cValue:特征值2
			getFingerbiz().fingerprintInput(fingerNumberText,fingerInt(),firstFinger,ShareUtil.ivalBack);	
			}
		};
		
		//采集通知
		getFingerbiz().hand = new Handler(){
			@Override
			public void handleMessage(Message msg) {			
				super.handleMessage(msg);
				managerClass.getRuning().remove();
				Log.i("aaa", "进来了");
				count = 0; 
				if(msg.what==1){  //成功
				resultText.setText("采集成功");
				fingerUserName.setText("");
				fingerUserNumber.setText("");
				success.setVisibility(View.VISIBLE);
				fail.setVisibility(View.GONE);
				}else if(msg.what==0){  //失败
				resultText.setText("采集失败");
				success.setVisibility(View.GONE);
				fail.setVisibility(View.VISIBLE);
				}else if(msg.what==-1){   //超时
					managerClass.getAbnormal().timeout(FingerGather.this, "连接超时!",fingerclick);
				}
			}
			
		};
		
						
		//获取姓名重试单击事件
		nameclick = new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				managerClass.getAbnormal().remove();
				//开始线程获取姓名
				managerClass.getRuning().runding(FingerGather.this, "正在获取姓名...");
				getNaneNumByUserid().getNameById(fingerNumberText);				
			}
		};
		
		//获取姓名通知handler
		getNaneNumByUserid().handler = new Handler(){			
			@Override
			public void handleMessage(Message msg) {
				managerClass.getRuning().remove();
				super.handleMessage(msg);
				if(msg.what==1){
				resultText.setVisibility(View.VISIBLE);
				resultText.setText("请按压手指...");				
				irfid.fingerOpen();  //打开指纹
				Bundle bundle = msg.getData();
				String name = bundle.getString("fingerName");
				Log.i("name", name);
				fingerUserName.setText(name);
				}else if(msg.what==0){
				AlertDialog.Builder dlog = new Builder(FingerGather.this);
				dlog.setMessage("找不到与该编号对应的姓名");
				dlog.setPositiveButton("确定", null);
				dlog.show();
				}else if(msg.what==-1){
					managerClass.getAbnormal().timeout(FingerGather.this, "连接超时!", nameclick);	
				}
			}
			
		};
		
		
		//手指下拉列表
		popuv = GolbalView.getLF(FingerGather.this).inflate(R.layout.spinner_popu,null);
	    mpop = new PopupWindow(popuv, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    
		listview = (ListView) popuv.findViewById(R.id.listview_finger);
		ad = new Ad();
		
		
		//加载显示手指列表
		spinnertext.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
			for (int i = 0; i < list_finger.size(); i++) {
				Log.i("list_finger", list_finger.get(i));
			}
			mpop.showAsDropDown(v);
			listview.setAdapter(ad);
			}
		});
		
		
		
		 //把当前activity放进集合	
		// GApplication.addActivity(this,"0finger");
		
	}

	@Override
	protected void onStart() {
		
		super.onStart();
	}
	
	
	
	 void fingerInit(){
		if(list_finger.size()<=0){
			//list_finger.add("请选择手指");
			list_finger.add("右手大拇指");			
			list_finger.add("右手食指");
			list_finger.add("右手中指");
			list_finger.add("右手无名指");
			list_finger.add("右手小拇指");
			list_finger.add("左手大拇指");
			list_finger.add("左手食指");
			list_finger.add("左手中指");
			list_finger.add("左手无名指");
			list_finger.add("左手小拇指");	
		}
		
	}
	
	 
	 class Ad extends BaseAdapter{
		@Override
		public int getCount() {
			return list_finger.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list_finger.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View content, ViewGroup arg2) {
			View v = content;
			final ViewHolder viewholder;  //内容类
			if(v==null){
			 v= GolbalView.getLF(FingerGather.this).inflate(R.layout.finger_dorplist_tem,null);	
			 viewholder = new ViewHolder();
			 viewholder.finger =(TextView) v.findViewById(R.id.fingertext);
			 v.setTag(viewholder);
			}else{
				viewholder = (ViewHolder) v.getTag();	
			}
			
			
			
			viewholder.finger.setOnTouchListener(new OnTouchListener() {				
				@Override
				public boolean onTouch(View view, MotionEvent even) {
					if(MotionEvent.ACTION_DOWN==even.getAction()){
						viewholder.finger.setBackgroundResource(R.color.bleu_pressdown);
					}
					
					if(MotionEvent.ACTION_UP==even.getAction()){
						viewholder.finger.setBackgroundResource(R.color.white);	
						spinnertext.setText(viewholder.finger.getText());
						mpop.dismiss();
						
					}
					if(MotionEvent.ACTION_CANCEL==even.getAction()){
						viewholder.finger.setBackgroundResource(R.color.white);
						managerClass.getGolbalutil().ismover = 0;
					}
					return true;
				}
			});
			String fingerValue = getItem(arg0).toString();
			viewholder.finger.setText(fingerValue);
			
			return v;
		}
		 
	 }
	 
	 class ViewHolder{
		TextView finger;				
	 }
	 
	 public String fingerInt(){
		 String finger = spinnertext.getText().toString();
		 String num = "0";
		 if(finger.equals("右手大拇指")){
			 num = "0"; 
		 }else if(finger.equals("右手食指")){
			 num = "1"; 
		 }else if(finger.equals("右手中指")){
			 num = "2"; 
		 }else if(finger.equals("右手无名指")){
			 num = "3"; 
		 }else if(finger.equals("右手小拇指")){
			 num = "4"; 
		 }else if(finger.equals("左手大拇指")){
			 num = "5"; 
		 }else if(finger.equals("左手食指")){
			 num = "6"; 
		 }else if(finger.equals("左手中指")){
			 num = "7"; 
		 }else if(finger.equals("左手无名指")){
			 num = "8"; 
		 }else if(finger.equals("左手小拇指")){
			 num = "9"; 
		 }
			
		return num;
	 }
	 
	 
	
	 //手指验证、编号验证
	 public boolean check(){	
		 is = false;
		 fingerNumberText = fingerUserNumber.getText()+"";	
		 if(fingerNumberText==null || fingerNumberText.equals("")){
			 Toast.makeText(this, "请输入编号", Toast.LENGTH_SHORT).show();
			 is = true;
		 }
		 
//		 if(count==0){
//		  finger_num = spinnertext.getText().toString(); 
//		  resultText.setText("请再次按压手指...");
//		  count++;
//		 }else{
		 if(count>0){
		  if(!finger_num.equals(spinnertext.getText().toString())){			  
			  getSuercancel().makeSuerCancel(this,"2次输入指纹的手指不一致", new View.OnClickListener() {				
				@Override
				public void onClick(View arg0) {
				Log.i("aaaa","aaaa");
				count =0;	
				finger_num ="";
				getSuercancel().remove();
				resultText.setText("请按压手指");
				}
			}, true);
			is = true;  
		   }
		 }
		 return is;
	 }

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {	
		menu.add("a");
		return true;
	}
	
	//拦截Menu
		@Override
		public boolean onMenuOpened(int featureId, Menu menu) {
			new MenuShow().menu(this);
			MenuShow.pw.showAtLocation(findViewById(R.id.finger_box), Gravity.BOTTOM, 0, 0);
			return false;
		}
	
		
	//根据编号获取姓名
	public void getname(){
			
			   //获取编号fingerUserName				
				fingerNumberText = fingerUserNumber.getText()+"";	
				if(fingerNumberText==null || fingerNumberText.equals("")){
				Toast.makeText(FingerGather.this, "请输入编号", Toast.LENGTH_SHORT).show();
				}else{
					//开始线程获取姓名
					managerClass.getRuning().runding(FingerGather.this, "正在获取姓名...");
					getNaneNumByUserid().getNameById(fingerNumberText);	
				}
		       
		
	}

	
	@Override
	public boolean onTouch(View view, MotionEvent even) {
		//按下的时候
		if(MotionEvent.ACTION_DOWN==even.getAction()){
			switch(view.getId()){
			//获取姓名  确定
			case R.id.finger_getname:
				btn_getname.setBackgroundResource(R.drawable.buttom_select_press);	
				break;
			//获取手指
			case R.id.spinnertext:
				spinnertext.setBackgroundResource(R.color.gray_msg_bg);
				break;
			//返回
			case R.id.finger_back:
				back.setImageResource(R.drawable.back_cirle_press);
				break;
				
			}
		}
		
		//手指松开的时候
		if(MotionEvent.ACTION_UP==even.getAction()){
			switch(view.getId()){
			//获取姓名  确定
			case R.id.finger_getname:
				btn_getname.setBackgroundResource(R.drawable.buttom_selector_bg);
				getname();
				success.setVisibility(View.GONE);
				fail.setVisibility(View.GONE);
				resultText.setText("");
				break;
			//获取手指
			case R.id.spinnertext:
			spinnertext.setBackgroundResource(R.color.transparency);
			mpop.showAsDropDown(view);
			listview.setAdapter(ad);	
				break;
			//返回
			case R.id.finger_back:
				back.setImageResource(R.drawable.back_cirle);
				FingerGather.this.finish();
				break;
				
			}
			
		}
		//手指移动的时候
		if(MotionEvent.ACTION_MOVE==even.getAction()){
			
		}
		//意外中断事件取消
		if(MotionEvent.ACTION_CANCEL==even.getAction()){
			switch(view.getId()){
			//获取姓名  确定
			case R.id.finger_getname:
				btn_getname.setBackgroundResource(R.drawable.buttom_selector_bg);	
				break;
			//获取手指
			case R.id.spinnertext:
				spinnertext.setBackgroundResource(R.color.transparency);
				break;
			//返回
			case R.id.finger_back:
				back.setImageResource(R.drawable.back_cirle);
				break;
				
			}
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		ShareUtil.finger_gather = null;
		this.finish();
	}
	
	 
	 
}
