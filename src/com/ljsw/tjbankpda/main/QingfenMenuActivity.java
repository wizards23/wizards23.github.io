package com.ljsw.tjbankpda.main;

import java.net.SocketTimeoutException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.application.GApplication;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.ljsw.tjbankpda.util.Skip;
import com.ljsw.tjbankpda.util.Table;
import com.ljsw.tjbankpda.util.onTouthImageStyle;
import com.ljsw.tjbankpda.yy.activity.QingfenGlyRwActivity;
import com.ljsw.tjbankpda.yy.application.S_application;
import com.ljsw.tjbankpda.yy.service.ICleaningManagementService;
import com.manager.classs.pad.ManagerClass;

/**
 * 清分双人登陆后的菜单界面
 * @author FUHAIQING
 *
 */
public class QingfenMenuActivity extends FragmentActivity implements OnTouchListener{
	/*定义控件变量*/
	private ImageView ivQinfen;//清分
	private ImageView ivQinfenshenhe;//清分审核
	private Handler handler;
	private ManagerClass managerClass;
	boolean network = true; // 是否有网络
	OnClickListener onclickreplace;
	QingfenThread qt;
	Table[] tables;
	
	/*定义全局变量*/
	private int move=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qingfen_menu);
		ivQinfen=(ImageView)findViewById(R.id.iv_qingfen_menu_qingfen);
		ivQinfenshenhe=(ImageView)findViewById(R.id.iv_qingfen_menu_qingfenshenghe);
		ivQinfen.setOnTouchListener(this);
		ivQinfenshenhe.setOnTouchListener(this);
		System.out.println("QingfenMenuActivity:"+GApplication.user);
		if("7".equals(GApplication.user.getLoginUserId())){
			findViewById(R.id.ll_qingfen_menu_qingfenshenghe).setVisibility(View.GONE);
		}else if("17".equals(GApplication.user.getLoginUserId())){
			findViewById(R.id.ll_qingfen_menu_qingfen).setVisibility(View.GONE);
		}
		managerClass = new ManagerClass();
		
		// 重试单击事件
				onclickreplace = new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						managerClass.getAbnormal().remove();
						managerClass.getRuning().runding(QingfenMenuActivity.this,"加载信息中...");
						qt = new QingfenThread();
						qt.start();			
					}
				};
				
				handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						// TODO Auto-generated method stub
						super.handleMessage(msg);
						switch (msg.what) {
						case 1://验证成功跳转
							managerClass.getRuning().remove();
							Skip.skip(QingfenMenuActivity.this,QingfenGlyRwActivity.class, null, 0);
							break;
						case -1:
							managerClass.getRuning().remove();
							managerClass.getAbnormal().timeout(QingfenMenuActivity.this,"信息加载异常", onclickreplace);
							break;
						case -4:
							managerClass.getRuning().remove();
							managerClass.getAbnormal().timeout(QingfenMenuActivity.this,"连接超时，重新链接？", onclickreplace);
							break;
						}
					}
				};
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent e) {
		if(e.getAction()==MotionEvent.ACTION_DOWN){
			switch (v.getId()) {
			case R.id.iv_qingfen_menu_qingfen:
				new onTouthImageStyle().setFilter(ivQinfen);
				break;
			case R.id.iv_qingfen_menu_qingfenshenghe:
				new onTouthImageStyle().setFilter(ivQinfenshenhe);
				break;
			default:
				break;
			}
		}
		if(e.getAction()==MotionEvent.ACTION_MOVE){
			move++;
			System.out.println("QingfenMenu--move:" + move);
		}
		if (e.getAction() == MotionEvent.ACTION_UP) {
			switch (v.getId()) {
			case R.id.iv_qingfen_menu_qingfen:
				new onTouthImageStyle().removeFilter(ivQinfen);
				if(move<10){
					Intent intent = new Intent(QingfenMenuActivity.this,
							QingfenRenwuActivity.class);
					startActivity(intent);
				}
				break;
			case R.id.iv_qingfen_menu_qingfenshenghe:
				new onTouthImageStyle().removeFilter(ivQinfenshenhe);
				if(move<10){
					new onTouthImageStyle().removeFilter(ivQinfenshenhe);
					
					//Skip.skip(QingfenMenuActivity.this,QingfenGlyRwActivity.class, null, 0);
					
					//加载自定义Dialog
					managerClass.getRuning().runding(QingfenMenuActivity.this, "加载信息中...");
					qt = new QingfenThread();
					qt.start();		
				}
				break;
			default:
				break;
			}
			move = 0;
		}
		if(e.getAction()==MotionEvent.ACTION_CANCEL){
			switch (v.getId()) {
			case R.id.iv_qingfen_menu_qingfen:
				new onTouthImageStyle().removeFilter(ivQinfen);
				break;
			case R.id.iv_qingfen_menu_qingfenshenghe:
				new onTouthImageStyle().removeFilter(ivQinfenshenhe);
				break;
			default:
				break;
			}
		}
		return true;
	}
	
	/**
	 * 获取清分管理员任务计划单列表
	 * @author Administrator
	 */
	class QingfenThread extends Thread {
		Message m;
		public QingfenThread() {
			super();
			m = handler.obtainMessage();
		}
		public void run() {
			super.run();
			ICleaningManagementService is = new ICleaningManagementService();
			try {
				System.out.println("ko---"+GApplication.user.getOrganizationId());
				String str = is.getQingfenGuanliJihuadan(GApplication.user.getOrganizationId());
				if(!"".equals(str)){
					tables = Table.doParse(str);
					S_application.getApplication().qfjhdlist = tables[0].get("jihuadan").getValues();
					S_application.getApplication().qfrqlist =  tables[0].get("riqi").getValues();
					m.what = 1;
				}
			} catch (SocketTimeoutException e) {
				m.what = -4;// 超时
			} catch (Exception e) {
				e.printStackTrace();
				m.what = -1;// 失败
			}finally{
			    handler.sendMessage(m);
			    GolbalUtil.onclicks = true;
			}
		}
	}
	
	
}
