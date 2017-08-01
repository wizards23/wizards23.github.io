package com.main.pda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.application.GApplication;
import com.example.app.activity.KuanxiangCaidanActivity;
import com.example.app.activity.LookStorageTaskListActivity;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.ljsw.tjbankpda.main.ZhouzhuanxiangMenu;
import com.manager.classs.pad.ManagerClass;
import com.messagebox.MenuShow;
import com.moneyboxadmin.pda.BankDoublePersonLogin;
import com.out.admin.pda.OrderWork;
import com.service.FixationValue;

public class HomeMenu extends Activity implements OnTouchListener {
	//功能菜单界面
	
	ImageView atmboxadmin;   //ATM钞箱管理
	ImageView systemmanager;   //系统管理
	ImageView clearmanager;    //清分管理
	ImageView outjoin;    //出库交接
	ImageView kuanxiangguanli;
	ImageView zhouzhuanxiang;
	Bundle bundle = new Bundle();
	
	LinearLayout latmboxadmin;   //ATM钞箱管理
	LinearLayout lsystemmanager;   //系统管理
	LinearLayout lclearmanager;    //清分管理
	LinearLayout loutjoin;    //出库交接
	LinearLayout kxgl;    //款箱管理
	LinearLayout zzxgl;
	LinearLayout ll_look_storage;	//查库服务
	private ManagerClass manager;
	
	private GolbalUtil getUtil;	
	public GolbalUtil getGetUtil() {
		if(getUtil==null){
			getUtil=new GolbalUtil();	
		}
		return getUtil;
		}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.y_home_menus);
		manager=new ManagerClass();
		 //全局异常处理
		// CrashHandler.getInstance().init(this);
		
		atmboxadmin = (ImageView) findViewById(R.id.money_box_admins);
		systemmanager = (ImageView) findViewById(R.id.systemanager);
		clearmanager = (ImageView) findViewById(R.id.cleanadmin);
		outjoin = (ImageView) findViewById(R.id.outjoin);
		outjoin = (ImageView) findViewById(R.id.outjoin);
		kuanxiangguanli= (ImageView) findViewById(R.id.kuanxiangguanli);
		zhouzhuanxiang=(ImageView) findViewById(R.id.home_menu_img_zzx);
		
		zzxgl=(LinearLayout)findViewById(R.id.home_menu_layout_zzx);
		ll_look_storage = (LinearLayout)findViewById(R.id.ll_look_storage);
		latmboxadmin = (LinearLayout) findViewById(R.id.lmoney_box_admins);
		lsystemmanager = (LinearLayout) findViewById(R.id.lsystemanager);
		lclearmanager = (LinearLayout) findViewById(R.id.lcleanadmin);
		loutjoin = (LinearLayout) findViewById(R.id.loutjoin);
		loutjoin = (LinearLayout) findViewById(R.id.loutjoin);
		kxgl=(LinearLayout)findViewById(R.id.zzxgl);
		
		
		atmboxadmin.setOnTouchListener(this);  
		systemmanager.setOnTouchListener(this);
		clearmanager.setOnTouchListener(this);
		outjoin.setOnTouchListener(this);
		kuanxiangguanli.setOnTouchListener(this);
		zhouzhuanxiang.setOnTouchListener(this);
		ll_look_storage.setOnTouchListener(this);
		if(GApplication.user.getLoginUserId()!=null){
			show(Integer.parseInt(GApplication.user.getLoginUserId()));
		}
	//	show(Integer.parseInt(GApplication.user.getLoginUserId()));
	}
	//触摸事件
	@Override
	public boolean onTouch(View view, MotionEvent even) {
		//按下的时候
				if(MotionEvent.ACTION_DOWN==even.getAction()){
				
				switch(view.getId()){					
					//ATM钞箱管理
				case R.id.money_box_admins:
					atmboxadmin.setImageResource(R.drawable.atmbox_down);
					break;
					//系统管理
				case R.id.systemanager:
					systemmanager.setImageResource(R.drawable.systemadmin_down);
					break;
					//清分管理
				case R.id.cleanadmin:						
					clearmanager.setImageResource(R.drawable.clearf_down);
					break;
					//出库交接
				case R.id.outjoin:
					outjoin.setImageResource(R.drawable.warehouse_down);
					break;										
				}
					
				}
				
				//手指松开的时候
				if(MotionEvent.ACTION_UP==even.getAction()){
					Log.i("getGetUtil().ismover", getGetUtil().ismover+"");
					switch(view.getId()){
					
						//ATM钞箱管理
					case R.id.money_box_admins:
						atmboxadmin.setImageResource(R.drawable.atmbox);
						//跳到库管双人登陆页面
						bundle.putString("user", "库管员");
						getGetUtil().gotoActivity(HomeMenu.this,BankDoublePersonLogin.class, bundle, getGetUtil().ismover);
//						HomeMenu.this.finish();//关闭当前页面
						break;
						//系统管理
					case R.id.systemanager:
						systemmanager.setImageResource(R.drawable.systemadmin);
						Log.i("getGetUtil()",getUtil+"");
						getGetUtil().gotoActivity(HomeMenu.this,SystemManager.class, null, getGetUtil().ismover);
						break;
						//清分管理
					case R.id.cleanadmin:
						clearmanager.setImageResource(R.drawable.clearf);
						//跳到清分员交接验证页面	
						bundle.putString("user", "清分员");
						bundle.putString("where", "清分管理");
						getGetUtil().gotoActivity(HomeMenu.this,BankDoublePersonLogin.class , bundle, getGetUtil().ismover);	
//						HomeMenu.this.finish();  //关闭当前页面
						break;
						//出库交接
					case R.id.outjoin:
						outjoin.setImageResource(R.drawable.warehouse);
						getGetUtil().gotoActivity(HomeMenu.this,OrderWork.class, null, getGetUtil().ismover);
//						HomeMenu.this.finish();//关闭当前页面
						break;
					case R.id.kuanxiangguanli:
						outjoin.setImageResource(R.drawable.warehouse);
						getGetUtil().gotoActivity(HomeMenu.this,KuanxiangCaidanActivity.class, null, getGetUtil().ismover);
						break;
					case R.id.home_menu_img_zzx:
						getGetUtil().gotoActivity(HomeMenu.this,ZhouzhuanxiangMenu.class, null, getGetUtil().ismover);
						break;	
					case R.id.ll_look_storage: // 查库服务
						/*
						 * 进入双人指纹验证界面
						 * @author zhouKai
						 */
						bundle.putString("user", "库管员");
						bundle.putString("taskType", "lookStorageService");
						getGetUtil().gotoActivity(HomeMenu.this, BankDoublePersonLogin.class, bundle, getGetUtil().ismover);
						break;	
					}
					getGetUtil().ismover=0;
				}
				//手指移动的时候
				if(MotionEvent.ACTION_MOVE==even.getAction()){
					getGetUtil().ismover++;
				}
				//意外中断事件取消
				if(MotionEvent.ACTION_CANCEL==even.getAction()){
					switch(view.getId()){					
					//ATM钞箱管理
				case R.id.money_box_admins:
					atmboxadmin.setImageResource(R.drawable.atmbox);
					break;
					//系统管理
				case R.id.systemanager:
					systemmanager.setImageResource(R.drawable.systemadmin);
					break;
					//清分管理
				case R.id.cleanadmin:						
					clearmanager.setImageResource(R.drawable.clearf);
					break;
					//出库交接
				case R.id.outjoin:
					outjoin.setImageResource(R.drawable.warehouse);
					break;										
				}
					getGetUtil().ismover=0;
				}
		
		return true;
	}
	
	/**
	 * 根据权限显示图标 传入的角色编号（3456789）
	 * @param permission
	 */
	public void show(int permission){
		switch(permission){
		case FixationValue.examine:  //审核岗
			 latmboxadmin.setVisibility(View.GONE);   //ATM钞箱管理
			 lsystemmanager.setVisibility(View.VISIBLE);   //系统管理
			 lclearmanager.setVisibility(View.GONE);    //清分管理
			 loutjoin.setVisibility(View.GONE);    //出库交接
			 kxgl.setVisibility(View.GONE);
			 zzxgl.setVisibility(View.GONE);
			 /*
			  * 清楚查库服务按钮，下同
			  * @author zhouKai
			  */
			 ll_look_storage.setVisibility(View.GONE);	//查库服务
			break;  
		case FixationValue.warehouse:   //管库员
			 latmboxadmin.setVisibility(View.VISIBLE);   //ATM钞箱管理
			 lsystemmanager.setVisibility(View.VISIBLE);   //系统管理
			 lclearmanager.setVisibility(View.GONE);    //清分管理
			 loutjoin.setVisibility(View.GONE);    //出库交接
			 kxgl.setVisibility(View.VISIBLE);
			 zzxgl.setVisibility(View.VISIBLE);
			 ll_look_storage.setVisibility(View.GONE);	//查库服务 // 去掉查库 reviseed by zhangxuewei
			break;
		case 5:   //加钞人员
			 latmboxadmin.setVisibility(View.GONE);   //ATM钞箱管理
			 lsystemmanager.setVisibility(View.VISIBLE);   //系统管理
			 lclearmanager.setVisibility(View.GONE);    //清分管理
			 loutjoin.setVisibility(View.VISIBLE);    //出库交接
			 kxgl.setVisibility(View.GONE);
			 zzxgl.setVisibility(View.GONE);
			 ll_look_storage.setVisibility(View.GONE);	//查库服务
			break;
		case 6:   //网点加钞
			 latmboxadmin.setVisibility(View.GONE);   //ATM钞箱管理
			 lsystemmanager.setVisibility(View.VISIBLE);   //系统管理
			 lclearmanager.setVisibility(View.GONE);    //清分管理
			 loutjoin.setVisibility(View.VISIBLE);    //出库交接
			 kxgl.setVisibility(View.VISIBLE);
			 zzxgl.setVisibility(View.GONE);
			 ll_look_storage.setVisibility(View.GONE);	//查库服务
			break;
		case FixationValue.clearer:   //清分员
			 latmboxadmin.setVisibility(View.GONE);   //ATM钞箱管理
			 lsystemmanager.setVisibility(View.VISIBLE);   //系统管理
			 lclearmanager.setVisibility(View.VISIBLE);    //清分管理
			 loutjoin.setVisibility(View.GONE);    //出库交接
			 kxgl.setVisibility(View.GONE);
			 zzxgl.setVisibility(View.VISIBLE);
			 ll_look_storage.setVisibility(View.GONE);	//查库服务
			break;
		case FixationValue.supercargo:   //押运员
			 latmboxadmin.setVisibility(View.GONE);   //ATM钞箱管理
			 lsystemmanager.setVisibility(View.VISIBLE);   //系统管理
			 lclearmanager.setVisibility(View.GONE);    //清分管理
			 loutjoin.setVisibility(View.VISIBLE);    //出库交接
			 kxgl.setVisibility(View.VISIBLE);
			 zzxgl.setVisibility(View.VISIBLE);
			 ll_look_storage.setVisibility(View.GONE);	//查库服务
			break;
		case FixationValue.waibaoqingfen:   //外包清分员
			 latmboxadmin.setVisibility(View.GONE);   //ATM钞箱管理
			 lsystemmanager.setVisibility(View.GONE);   //系统管理
			 lclearmanager.setVisibility(View.GONE);    //清分管理
			 loutjoin.setVisibility(View.GONE);    //出库交接
			 kxgl.setVisibility(View.GONE);
			 zzxgl.setVisibility(View.VISIBLE);
			 ll_look_storage.setVisibility(View.GONE);	//查库服务
			break;
			
		default:
			 latmboxadmin.setVisibility(View.GONE);   //ATM钞箱管理
			 lsystemmanager.setVisibility(View.GONE);   //系统管理
			 lclearmanager.setVisibility(View.GONE);    //清分管理
			 loutjoin.setVisibility(View.GONE);    //出库交接
			 kxgl.setVisibility(View.GONE);
			 ll_look_storage.setVisibility(View.GONE);	//查库服务
		break;
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {	
		menu.add("a");
		return true;
	}

	// 拦截Menu
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		new MenuShow().menu(this);
		MenuShow.pw.showAtLocation(findViewById(R.id.homemenu_box), Gravity.BOTTOM, 0, 0);
		return false;
	}
		
		
		
		
		
		
		
		
}