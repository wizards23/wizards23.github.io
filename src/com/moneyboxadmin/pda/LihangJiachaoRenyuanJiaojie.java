package com.moneyboxadmin.pda;

import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.GApplication;
import com.entity.SystemUser;
import com.example.pda.R;
import com.golbal.pda.GolbalUtil;
import com.imple.getnumber.GetFingerValue;
import com.ljsw.tjbankpda.db.service.YanZhengZhiWenService;
import com.main.pda.Scan;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.service.SaveAuthLogService;
import com.poka.device.ShareUtil;

/**
 * 离行式加钞人员指纹交接
 * 
 * @author Administrator
 * 
 */
public class LihangJiachaoRenyuanJiaojie extends Activity {

	private TextView tishi; // 顶部提示控件
	private TextView tishiDibu; // 底部提示控件
	private TextView name1; // 顶部提示控件
	private TextView name2; // 顶部提示控件
	private ImageView zhiwen1; // 顶部提示控件
	private ImageView zhiwen2; // 顶部提示控件
	private ImageView isOkImg; // 验证成功图标
	private TextView isOKTxt; // 验证成功文字

	private String userLeftId = ""; // 加钞员1编号
	private String userRightId = ""; // 加钞员2编号
	public static int state = 0; // 交接状态 0.未交接 1.加钞员1已交接 2.交接完毕
	final ManagerClass managerClass = new ManagerClass();
	private Bundle bundleBussin;

	// 页面跳转
	private void gotoActivity() {
		bundleBussin.putString("jiachaoId1", userLeftId); // 把加钞员塞到bundle里
		bundleBussin.putString("jiachaoId2", userRightId); // 把加钞员塞到bundle里
		managerClass.getGolbalutil().gotoActivity(
				LihangJiachaoRenyuanJiaojie.this, SupercargoJoin.class,
				bundleBussin, GolbalUtil.ismover);
		LihangJiachaoRenyuanJiaojie.this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.y_bank_double_jiachaoyuan);
		tishi = (TextView) findViewById(R.id.resultmsg);
		tishiDibu = (TextView) findViewById(R.id.show);
		name1 = (TextView) findViewById(R.id.name1);
		name2 = (TextView) findViewById(R.id.name2);
		zhiwen1 = (ImageView) findViewById(R.id.finger_left);
		zhiwen2 = (ImageView) findViewById(R.id.finger_right);
		isOkImg = (ImageView) findViewById(R.id.resultimg);
		isOKTxt = (TextView) findViewById(R.id.resulttext);

		bundleBussin = getIntent().getExtras();
		Scan scan = new Scan();
		scan.scan();
		scan.handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				managerClass.getRfid().addNotifly(new GetFingerValue()); // 添加通知
				managerClass.getRfid().fingerOpen(); // 打开指纹

			}
		};

		// 获得指纹通知
		GetFingerValue.handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle;
				if (msg.what == 1) {
					bundle = msg.getData();
					String tishiString = bundle.getString("finger");
					tishi.setText("正在验证...");
					System.out.println("tishi:" + tishiString);
					if (GolbalUtil.onclicks
							&& bundle.getString("finger").equals("获取指纹特征值成功！")) { // 指纹获取成功
						System.out.println("state:" + state);
						GolbalUtil.onclicks = false;
						switch (state) {
						case 0: // 第一人验证
							zhiwen1.setImageBitmap(ShareUtil.finger_bitmap_left); // 现实指纹图片
							break;

						case 1: // 第二人验证
							zhiwen2.setImageBitmap(ShareUtil.finger_bitmap_left); // 现实指纹图片
							break;
						}

						/*
						 * 验证交接
						 */
						String corpId = GApplication.user.getOrganizationId(); // 机构ID
						String roidId = "5";
						byte[] cValue = ShareUtil.ivalBack;
						System.out.println("准备交接");
						jiaojie(corpId, roidId, cValue);
					}

				}
			}
		};
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		GolbalUtil.onclicks = true;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		ShareUtil.finger_bitmap_right=null;
		ShareUtil.finger_bitmap_left=null;
	}
	
	/**
	 * 交接方法 验证指纹信息
	 * 
	 * @param corpId
	 *            机构Id
	 * @param roidId
	 *            角色Id
	 * @param cValue
	 *            指纹byte数组
	 */
	private void jiaojie(String corpId, String roidId, byte[] cValue) {
		Thread thread = new Thread(new CheckFingerprint(corpId, roidId, cValue));
		thread.start();
	}

	/**
	 * 指纹交接验证
	 * 
	 * @author Administrator
	 * 
	 */
	private class CheckFingerprint implements Runnable {
		private String corpId;
		private String roidId;
		private byte[] cValue;

		public CheckFingerprint(String corpId, String roidId, byte[] cValue) {
			this.corpId = corpId;
			this.roidId = roidId;
			this.cValue = cValue;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			YanZhengZhiWenService yanzheng = new YanZhengZhiWenService();
			Message msg = handler.obtainMessage();
			try {
				SystemUser result_user = yanzheng.checkFingerprint(corpId,
						roidId, cValue);

				if (result_user != null) {
					msg.obj = result_user;
					msg.what = 1;
				} else {
					msg.what = 2;
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				msg.what = 3;
			} catch (Exception e1) {
				e1.printStackTrace();
				msg.what = 4;
			} finally {
				handler.sendMessage(msg);
			}
		}

	}

	/*
	 * 指纹验证handelr
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: // 验证成功
				
				SystemUser result_user = (SystemUser) msg.obj;
				/*
				 * 继续验证该加钞员是否能领取该任务(判断是否处于空闲状态)
				 */
				Thread thread = new Thread(new IsKongxian(result_user));
				thread.start();
				break;
			case 2: // 验证失败
				tishi.setText("验证失败,请重按");
				GolbalUtil.onclicks = true;
				break;
			case 3:// 验证超时
				tishi.setText("验证超时,请重按");
				GolbalUtil.onclicks = true;
				break;
			case 4: // 验证异常
				tishi.setText("验证异常,请重按");
				GolbalUtil.onclicks = true;
				break;
			}

		};
	};

	/**
	 * 判断加钞员是否处于空闲状态线程函数
	 * 
	 * @author Administrator
	 * 
	 */
	private class IsKongxian implements Runnable {
		private SystemUser user;

		public IsKongxian(SystemUser user) {
			this.user = user;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = kongxianHandelr.obtainMessage();
			try {
				System.out
						.println("钓调用空闲查询——参数userId:" + user.getLoginUserId());
				String code = new SaveAuthLogService().checkEscortState(user
						.getLoginUserId());
				System.out
				.println("钓调用空闲查询——结果code:" + code);
				if ("00".equals(code)) {
					msg.what = 1; // 空闲
					msg.obj = user;
				} else if ("01".equals(code)) {
					msg.what = 2; // 被占用
				}else{
					msg.what=4;  //验证异常
				}
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				msg.what = 3;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg.what = 4;
			} finally {
				kongxianHandelr.sendMessage(msg);
			}
		}
	}

	/*
	 * 空闲验证handler
	 */
	private Handler kongxianHandelr = new Handler() {
		public void handleMessage(Message msg) {
			SystemUser user = (SystemUser) msg.obj;

			switch (msg.what) {
			case 1: // 空闲(可用)
				switch (state) {
				case 0:
					userLeftId = user.getLoginUserId(); // 记录下第一个加钞员的编号
					tishi.setText("成功一位");
					tishiDibu.setText("请第二位加钞员按压手指...");
					name1.setText(user.getLoginUserName());
					state++;
					break;

				case 1:
					userRightId = user.getLoginUserId(); // 记录下第一个加钞员的编号
					if (userLeftId.equals(userRightId)) {
						// 重复验证
						tishi.setText("不可重复验证...");
						break;
					}
					tishiDibu.setText("");
					name2.setText(user.getLoginUserName());
					isOkImg.setVisibility(View.VISIBLE);
					isOKTxt.setVisibility(View.VISIBLE);
					state++;

					// 延迟跳转到押韵验证界面
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							gotoActivity();
						}
					}, 2000);
					break;
				}
				break;
			case 2: // 占用(不可用)
				tishi.setText("该加钞员已被占用！验证无效");
				break;
			case 3:// 验证超时
				tishi.setText("验证超时,请重按");
				System.out.println("验证空闲超时");
				break;
			case 4: // 验证异常
				tishi.setText("验证异常,请重按");
				System.out.println("验证空闲异常");
				break;
			}

			GolbalUtil.onclicks = true;
		};
	};

}
