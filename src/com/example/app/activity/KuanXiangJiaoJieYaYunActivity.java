package com.example.app.activity;

import java.net.SocketTimeoutException;

import hdjc.rfid.operator.RFID_Device;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.GApplication;
import com.example.app.entity.User;
import com.example.app.entity.UserInfo;
import com.example.app.util.Skip;
import com.example.pda.R;
import com.imple.getnumber.GetFingerValue;
import com.ljsw.tjbankpda.db.activity.PeiSongJiaoJie_db;
import com.main.pda.Scan;
import com.manager.classs.pad.ManagerClass;
import com.moneyboxadmin.biz.FingerCheckBiz;
import com.moneyboxadmin.biz.SaveAuthLogBiz;
import com.o.service.KuanxiangjiaojieService;
import com.o.service.YanZhengZhiWen;
import com.poka.device.ShareUtil;

public class KuanXiangJiaoJieYaYunActivity extends Activity {
	private ImageView finger;// 回退 指纹图片
	private TextView top, fname, bottom;// 顶部提示 指纹对应人员姓名 底部提示
	private User result_user2;
	Scan scan;
	public static String userid1; // 角色ID
	public static String userid2; // 角色ID
	private int shibaiCount = 0;
	String planNum; // 计划编号
	String type = null; // 交接类型
	String admin;
	View vtoast;
	Dialog dialog;
	private String zsisOk;
	private String wzisOk;// 晚收提交成功标识 早送
	private String wisOk;// 晚收提交成功标识 不需要早送
	private String isOk;// 早上提交标识
	private FingerCheckBiz fingerCheck;
	KuanxiangjiaojieService kj = new KuanxiangjiaojieService();
	private ManagerClass manager;

	FingerCheckBiz getFingerCheck() {
		return fingerCheck = fingerCheck == null ? new FingerCheckBiz()
				: fingerCheck;
	}

	SaveAuthLogBiz saveAuthLogBiz;

	SaveAuthLogBiz getSaveAuthLogBiz() {
		return saveAuthLogBiz = saveAuthLogBiz == null ? new SaveAuthLogBiz()
				: saveAuthLogBiz;
	}

	private RFID_Device rfid;

	RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_kuguanjiaojie_yayun);
		load();
		System.out.println("GApplication.jiaojiestate:"
				+ GApplication.jiaojiestate);
		manager = new ManagerClass();

		GetFingerValue.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle;
				if (msg.what == 1) {
					bundle = msg.getData();
					if (bundle != null
							&& bundle.getString("finger").equals("正在获取指纹特征值！")) {
						top.setText("正在进行指纹验证....");
					} else if (bundle != null
							&& bundle.getString("finger").equals("获取指纹特征值成功！")) {
						if (ShareUtil.ivalBack != null) {
							yanzhengFinger();
						}
					}
				}
			}
		};
	}

	public void load() {
	//	back = (ImageView) findViewById(R.id.yayun_back_bank);
		finger = (ImageView) findViewById(R.id.yayun_imageView1);
		top = (TextView) findViewById(R.id.yanyun_top);
		fname = (TextView) findViewById(R.id.yayun_fname);
		bottom = (TextView) findViewById(R.id.yayun_bottom);
	}

	public void yanzhengFinger() {
		manager.getRuning().runding(KuanXiangJiaoJieYaYunActivity.this,
				"验证中...");
		new Thread() {
			
			@Override
			public void run() {
				super.run();
				YanZhengZhiWen yz = new YanZhengZhiWen();
				try {
					System.out.println("---库管指纹用登录用户的机构："+GApplication.loginJidouId);
					result_user2 = yz.yanzhengfinger(
							GApplication.loginJidouId,"9",
							ShareUtil.ivalBack);
					if (result_user2 != null) {
						handler.sendEmptyMessage(0);
					} else {
						handler.sendEmptyMessage(1);
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(2);
				} catch (NullPointerException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(1);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(3);
				}
			}

		}.start();
	}

	/**
	 * 早送
	 */
	public void saveBoxinfo() {
		manager.getRuning().runding(KuanXiangJiaoJieYaYunActivity.this,
				"提交中...");
		new Thread() {

			@Override
			public void run() {
				super.run();
				StringBuffer Box = new StringBuffer();
				String userIds;
				for (int i = 0; i < GApplication.zaolist.size(); i++) {
					Box.append(GApplication.zaolist.get(i));
					Box.append("|");
				}
				userIds = ShareUtil.zhiwenid_left + "|"
						+ ShareUtil.zhiwenid_right;
				try {
					if (ShareUtil.zhiwenid_left != null
							&& ShareUtil.zhiwenid_right != null) {
						// 款箱早送交接
						System.out.println("---------------提交-1");
						zsisOk = kj.saveBoxHandover(
								GApplication.kxc.getXianlubianhao(),
								Box.substring(0, Box.length() - 1),
								GApplication.sk.getNetId(),// listView列表对象的网点号
								GApplication.loginJidouId,
								GApplication.loginjueseid,
								ShareUtil.ivalBack, userIds,
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());// 此处修改添加指纹验证失败的帐号登录信息

					} else if (ShareUtil.zhiwenid_left != null
							&& ShareUtil.zhiwenid_right == null) {
						// 第一个人验证成功，第二个人验证失败
						// 款箱早送交接
						System.out.println("---------------提交-2");
						zsisOk = kj.saveBoxHandover(
								GApplication.kxc.getXianlubianhao(),
								Box.substring(0, Box.length() - 1),
								GApplication.sk.getNetId(),// listView列表对象的网点号
								GApplication.loginJidouId,
								GApplication.loginjueseid,
								ShareUtil.ivalBack,
								ShareUtil.zhiwenid_left
										+ "|"
										+ GApplication.wd_user2
												.getUserzhanghu(),
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());// 此处修改添加指纹验证失败的帐号登录信息
					} else if (ShareUtil.zhiwenid_left == null
							&& ShareUtil.zhiwenid_right != null) {
						// 第一个人验证失败，第二个人验证成功
						// 款箱早送交接
						System.out.println("---------------提交-3");
						zsisOk = kj.saveBoxHandover(
								GApplication.kxc.getXianlubianhao(),
								Box.substring(0, Box.length() - 1),
								GApplication.sk.getNetId(),// listView列表对象的网点号
								GApplication.loginJidouId,
								GApplication.loginjueseid,
								ShareUtil.ivalBack,
								GApplication.wd_user1.getUserzhanghu() + "|"
										+ ShareUtil.zhiwenid_right,
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());// 此处修改添加指纹验证失败的帐号登录信息
					} else if (ShareUtil.zhiwenid_left == null
							&& ShareUtil.zhiwenid_right == null) {
						System.out.println("---------------提交-4");
						// 款箱早送交接
						zsisOk = kj.saveBoxHandover(
								GApplication.kxc.getXianlubianhao(),
								Box.substring(0, Box.length() - 1),
								GApplication.sk.getNetId(),// listView列表对象的网点号
								GApplication.loginJidouId,
								GApplication.loginjueseid,
								ShareUtil.ivalBack,
								GApplication.wd_user1.getUserzhanghu()
										+ "|"
										+ GApplication.wd_user2
												.getUserzhanghu(),
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());// 此处修改添加指纹验证失败的帐号登录信息
					}
					if (zsisOk != null) {
						handler.sendEmptyMessage(17);
					} else {
						handler.sendEmptyMessage(16);
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(18);
				} catch (NullPointerException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(16);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(19);
				}
			}

		}.start();
	}

	/**
	 * 款箱晚收交接提交  (中间跳转了早送申请的页面的)
	 * @author Administrator
	 */
	public void saveBoxWanShou(){
		manager.getRuning().runding(KuanXiangJiaoJieYaYunActivity.this,
				"提交中...");
		new Thread(){
			@Override
			public void run() {
				super.run();
				StringBuffer Boxes = new StringBuffer();
				StringBuffer toBoxes = new StringBuffer();
				String userIds = ShareUtil.zhiwenid_left + "|"+ ShareUtil.zhiwenid_right;
				if(GApplication.zyzaolist.size()>0){//在用状态的箱子
					for (int i = 0; i < GApplication.zyzaolist.size(); i++) {						
						Boxes.append(GApplication.zssqlist.get(i)+";"+GApplication.zyzaolist.get(i)+"|");				
					}
				}				
				String boxes = Boxes.toString();
				String toboxes ="";
				if(GApplication.zaolist.size()>0){//选中的款箱
					for (int i = 0; i < GApplication.zaolist.size(); i++) {
						toBoxes.append(GApplication.zaolist.get(i)+"|");
					}
					toboxes = toBoxes.toString();
					toboxes = toboxes.substring(0, toboxes.length() - 1);
				}else if(GApplication.zaolist.size()==0){
					toboxes="";
				}
						
				String date = GApplication.curDate;//日期
				String wangDian = GApplication.jigouid;//申请送款箱的网点
				String yayunjgId = GApplication.loginJidouId;//押运人员机构编号
				String roleId =GApplication.loginjueseid;//角色编号
				// ShareUtil.ivalBack 指纹信息
				// GApplication.userInfo.getNameZhanghao(), 帐号
				// GApplication.userInfo.getPwd()); 密码
				
				System.out.println("参数1:"+GApplication.kxc.getXianlubianhao());
				System.out.println("参数2:"+boxes.substring(0, boxes.length() - 1));
				if(!"".equals(toboxes)){
					System.out.println("参数3:"+toboxes.substring(0, toboxes.length() - 1));
				}			
				System.out.println("参数4:"+date);
				System.out.println("参数5:"+wangDian);
				System.out.println("参数6:"+yayunjgId);
				System.out.println("参数7:"+roleId);
				System.out.println("参数8:"+ShareUtil.ivalBack);
				System.out.println("参数9:"+userIds);
				System.out.println("参数10:"+GApplication.userInfo.getNameZhanghao());
				System.out.println("参数11:"+GApplication.userInfo.getPwd());
				
				
				String code="";
				try {
					if (ShareUtil.zhiwenid_left != null
							&& ShareUtil.zhiwenid_right != null){
						System.out.println("晚收网点双人指纹-----进入");
					code = kj.saveBoxHandoverStorageLate(GApplication.kxc.getXianlubianhao(),
							boxes.substring(0, boxes.length() - 1),
							toboxes,date,wangDian
							,yayunjgId,roleId,ShareUtil.ivalBack,userIds
							,GApplication.userInfo.getNameZhanghao(),
							GApplication.userInfo.getPwd());
					}else if(ShareUtil.zhiwenid_left != null
							&& ShareUtil.zhiwenid_right == null){
						// 第一个人验证成功，第二个人验证失败
						System.out.println("晚收网点第一个人验证成功，第二个人验证失败-----进入");
						// 款箱晚收交接
						code = kj.saveBoxHandoverStorageLate(
								GApplication.kxc.getXianlubianhao(), 
								boxes.substring(0, boxes.length() - 1),
								toboxes,
								date, wangDian,
								yayunjgId,roleId,ShareUtil.ivalBack,
								ShareUtil.zhiwenid_left+ "|"+ GApplication.wd_user2.getUserzhanghu(),
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());
					}else if(ShareUtil.zhiwenid_left == null
							&& ShareUtil.zhiwenid_right != null){
						System.out.println("晚收网点第一个人验证失败，第二个人验证成功-----进入");
						code =kj.saveBoxHandoverStorageLate(
								GApplication.kxc.getXianlubianhao(), 
								boxes.substring(0, boxes.length() - 1),
								toboxes,
								date, wangDian,
								yayunjgId,roleId,ShareUtil.ivalBack,
								GApplication.wd_user1.getUserzhanghu()+ "|"+ ShareUtil.zhiwenid_right,
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());
					}else{
						System.out.println("网点--" + "双人失败");
						code = kj.saveBoxHandoverStorageLate(
									GApplication.kxc.getXianlubianhao(),
									boxes.substring(0, boxes.length() - 1),
									toboxes,
									date, wangDian,
									yayunjgId,roleId,ShareUtil.ivalBack,
									GApplication.wd_user1.getUserzhanghu()+ "|"+ GApplication.wd_user2.getUserzhanghu(),
									GApplication.userInfo.getNameZhanghao(),
									GApplication.userInfo.getPwd());
					}
					if(code.equals("00")){
						System.out.println("晚收成功!");
						handler.sendEmptyMessage(4);
					}else if(code.equals("99")){
						System.out.println("晚收失败!");
						handler.sendEmptyMessage(5);
					}
				}  catch (SocketTimeoutException e) {
					System.out.println("晚收失败6");
					e.printStackTrace();
					
					handler.sendEmptyMessage(6);
				} catch (NullPointerException e) {
					System.out.println("晚收失败5");
					e.printStackTrace();
					
					handler.sendEmptyMessage(5);
				} catch (Exception e) {
					System.out.println("晚收失败7");
					e.printStackTrace();
					handler.sendEmptyMessage(7);
				}
			}
		}.start();
	}
	/**
	 * 款箱晚收交接提交  (中间没有跳转早送申请的页面的)
	 * @author Administrator
	 */
	public void saveNoZsBoxWanShou(){
		manager.getRuning().runding(KuanXiangJiaoJieYaYunActivity.this,
				"提交中...");
		new Thread(){
			public void run() {
				super.run();
				try {
					StringBuffer toBoxes = new StringBuffer();
					String userIds;
					String kunqian = ";";
					for (int j = 0; j < GApplication.zaolist.size(); j++) {
						toBoxes.append(GApplication.zaolist.get(j));
						toBoxes.append("|;");
					}
					userIds = ShareUtil.zhiwenid_left + "|"
							+ ShareUtil.zhiwenid_right;
					if (ShareUtil.zhiwenid_left != null
							&& ShareUtil.zhiwenid_right != null) {

						System.out.println("--提交--" + "双人指纹验证成功");
						// 款箱晚收交接
						wisOk = kj.saveBoxHandoverStorageLate(
								GApplication.kxc.getXianlubianhao(),
								kunqian+toBoxes.substring(0, toBoxes.length() - 2), "",
								"", GApplication.sk.getNetId(),
								GApplication.loginJidouId,
								GApplication.loginjueseid,
								ShareUtil.ivalBack, userIds, 
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());
					} else if (ShareUtil.zhiwenid_left != null
							&& ShareUtil.zhiwenid_right == null) {
						// 第一个人验证成功，第二个人验证失败
						// 款箱晚收交接
						System.out.println("---------------提交-9");
						wisOk = kj.saveBoxHandoverStorageLate(
								GApplication.kxc.getXianlubianhao(),
								kunqian+toBoxes.substring(0, toBoxes.length() - 2),
								"",
								"",
								GApplication.sk.getNetId(),
								GApplication.loginJidouId,
								GApplication.loginjueseid,
								ShareUtil.ivalBack,
								ShareUtil.zhiwenid_left
										+ "|"
										+ GApplication.wd_user2
												.getUserzhanghu(),
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());
					} else if (ShareUtil.zhiwenid_left == null
							&& ShareUtil.zhiwenid_right != null) {
						// 第一个人验证失败，第二个人验证成功
						// 款箱晚收交接
						System.out.println("---------------提交-10");
						wisOk = kj.saveBoxHandoverStorageLate(
								GApplication.kxc.getXianlubianhao(),
								kunqian+toBoxes.substring(0, toBoxes.length() - 2), "",
								"", GApplication.sk.getNetId(),
								GApplication.loginJidouId,
								GApplication.loginjueseid,
								ShareUtil.ivalBack,
								GApplication.wd_user1.getUserzhanghu() + "|"
										+ ShareUtil.zhiwenid_right,
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());
					} else if (ShareUtil.zhiwenid_left == null
							&& ShareUtil.zhiwenid_right == null) {
						System.out.println("---------------提交-11");

						// 款箱晚收交接
						wisOk = kj.saveBoxHandoverStorageLate(
								GApplication.kxc.getXianlubianhao(),
								kunqian+toBoxes.substring(0, toBoxes.length() - 2),
								"",
								"",
								GApplication.sk.getNetId(),
								GApplication.loginJidouId,
								GApplication.loginjueseid,
								ShareUtil.ivalBack,
								GApplication.wd_user1.getUserzhanghu()
										+ "|"
										+ GApplication.wd_user2
												.getUserzhanghu(),
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());
					}
					if (wisOk.equals("99")||wisOk.equals("97")) {
						handler.sendEmptyMessage(9);
					} else {
						handler.sendEmptyMessage(8);
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(10);
				} catch (NullPointerException e) {
					manager.getRuning().remove();
					handler.sendEmptyMessage(9);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(11);
				}
			}	
		}.start();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			// 0 早送提交失败 重新提交
			case 0:
				manager.getRuning().remove();
				// 下行代码为了防止UserInfo空指针
				if (GApplication.userInfo == null) {
					GApplication.userInfo = new UserInfo("", "");
				}
				/*
				 * revised by zhangxuewei 
				 * 2016-8-25 add frame confirm
				 */
				String yayunname=null;
				if(result_user2!=null){
					yayunname=result_user2.getUsername();	
				}
				finger.setImageBitmap(ShareUtil.finger_gather);
				fname.setText(result_user2.getUsername());
				manager.getSureCancel().makeSuerCancel2(
						
						KuanXiangJiaoJieYaYunActivity.this, "押运员："+yayunname,
						new View.OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								manager.getSureCancel().remove();
								manager.getRuning().runding(KuanXiangJiaoJieYaYunActivity.this, "提交中...");
								// 交接
								// 调用service提交
								if (GApplication.jiaojiestate == 1) {
									// 调用早收交接 完成
									System.out.println("jiaojiestate :"+GApplication.jiaojiestate);
									/*manager.getRuning()
											.runding(KuanXiangJiaoJieYaYunActivity.this,
													"验证完成,提交数据中...");*/
									saveBoxinfo();
								} else if (GApplication.jiaojiestate == 2) {
									System.out.println("jiaojiestate :"+GApplication.jiaojiestate);
									/*manager.getRuning().runding(
											KuanXiangJiaoJieYaYunActivity.this,
											"验证完成,提交数据中...");*/
											
									saveNoZsBoxWanShou();
								}else if(GApplication.jiaojiestate == 3){
									System.out.println("jiaojiestate :"+GApplication.jiaojiestate);
									saveBoxWanShou();	
								}
							}
						}, false);
				break;
			case 1:
				manager.getRuning().remove();
				// 累计验证失败的次数
				shibaiCount++;
				top.setText("验证失败"+shibaiCount+"次");
				if (shibaiCount >=3) {
					// 跳转登录页面
					// 跳转登录页面
					Intent intent = new Intent();
					Bundle budnle = new Bundle();
					budnle.putString("FLAG", "jiaojie");
					intent.putExtras(budnle);
					Skip.skip(KuanXiangJiaoJieYaYunActivity.this,
							YayunCheckFingerActivity.class, budnle, 0);
				}
				break;
			case 2:
				manager.getRuning().remove();
//				manager.getAbnormal().timeout(
//						KuanXiangJiaoJieYaYunActivity.this, "验证超时,重试?",
//						new View.OnClickListener() {
//							@Override
//							public void onClick(View v) {
//								top.setText("正在进行指纹验证....");
//								yanzhengFinger();
//								manager.getAbnormal().remove();
//							}
//						});
				shibaiCount++;
				top.setText("验证失败"+shibaiCount+"次");
				if (shibaiCount >=3) {
					// 跳转登录页面
					// 跳转登录页面
					Intent intent = new Intent();
					Bundle budnle = new Bundle();
					budnle.putString("FLAG", "jiaojie");
					intent.putExtras(budnle);
					Skip.skip(KuanXiangJiaoJieYaYunActivity.this,
							YayunCheckFingerActivity.class, budnle, 0);
				}
				break;
			case 3:
				manager.getRuning().remove();
//				manager.getAbnormal().timeout(
//						KuanXiangJiaoJieYaYunActivity.this, "网络连接失败,重试?",
//						new View.OnClickListener() {
//
//							@Override
//							public void onClick(View v) {
//								top.setText("正在进行指纹验证....");
//								yanzhengFinger();
//								manager.getAbnormal().remove();
//							}
//						});
				shibaiCount++;
				top.setText("验证失败"+shibaiCount+"次");
				if (shibaiCount >=3) {
					// 跳转登录页面
					// 跳转登录页面
					Intent intent = new Intent();
					Bundle budnle = new Bundle();
					budnle.putString("FLAG", "jiaojie");
					intent.putExtras(budnle);
					Skip.skip(KuanXiangJiaoJieYaYunActivity.this,
							YayunCheckFingerActivity.class, budnle, 0);
				}
				break;
			case 4:
				manager.getRuning().remove();
				GApplication.wd_user1 = null;
				GApplication.map = null;
				GApplication.wd_user2 = null;
				GApplication.zyzaolist.clear();
				GApplication.zssqlist.clear();
				GApplication.zaolist.clear();
				GApplication.jiaojiestate = 0;
				//弹出个提示框标识交接成功  点击其他的地方不让他消失
				dialog = new Dialog(KuanXiangJiaoJieYaYunActivity.this);
				LayoutInflater inflater = LayoutInflater.from(KuanXiangJiaoJieYaYunActivity.this);
				View v = inflater.inflate(R.layout.dialog_success, null);
				Button but = (Button)v.findViewById(R.id.success);
				dialog.setCancelable(false);
				dialog.setContentView(v);
				if(but!=null){
					but.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// 启动异步提交数据   然后获取数据
							Skip.skip(KuanXiangJiaoJieYaYunActivity.this,JiaoJieActivity.class, null, 0);
							dialog.dismiss();
						}
					});
				}
				dialog.show();
				break;
			case 5:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						KuanXiangJiaoJieYaYunActivity.this, "晚收早送申请提交失败,重试?",
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								saveBoxWanShou();
								manager.getAbnormal().remove();
							}
						});
				break;
			case 6:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(KuanXiangJiaoJieYaYunActivity.this, "提交超时,重试?",
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								saveBoxWanShou();
								manager.getAbnormal().remove();
							}
						});
				break;
			case 7:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						KuanXiangJiaoJieYaYunActivity.this, "提交异常,重试?",
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								saveBoxWanShou();
								manager.getAbnormal().remove();

							}
						});
				break;
			case 8:
				manager.getRuning().remove();
				GApplication.wd_user1 = null;
				GApplication.map = null;
				GApplication.wd_user2 = null;
				GApplication.zaolist.clear();
				GApplication.jiaojiestate = 0;
				//弹出个提示框标识交接成功  点击其他的地方不让他消失
				dialog = new Dialog(KuanXiangJiaoJieYaYunActivity.this);
				LayoutInflater inflater1 = LayoutInflater.from(KuanXiangJiaoJieYaYunActivity.this);
				View v1 = inflater1.inflate(R.layout.dialog_success, null);
				Button but1 = (Button)v1.findViewById(R.id.success);
				dialog.setCancelable(false);
				dialog.setContentView(v1);
				if(but1!=null){
					but1.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// 启动异步提交数据   然后获取数据
							Skip.skip(KuanXiangJiaoJieYaYunActivity.this,JiaoJieActivity.class, null, 0);
							dialog.dismiss();
						}
					});
				}
				dialog.show();
				break;
			case 9:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						KuanXiangJiaoJieYaYunActivity.this, "晚收不早送提交失败,重试?",
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								saveNoZsBoxWanShou();
								manager.getAbnormal().remove();

							}
						});
				break;
			case 10:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						KuanXiangJiaoJieYaYunActivity.this, "提交超时,重试?",
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								saveNoZsBoxWanShou();
								manager.getAbnormal().remove();

							}
						});
				break;
			case 11:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						KuanXiangJiaoJieYaYunActivity.this, "网络连接失败,重试?",
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								saveNoZsBoxWanShou();
								manager.getAbnormal().remove();

							}
						});
				break;
			case 12:

				break;
			case 13:
				break;
			case 14:
				break;
			case 15:
				break;
			case 16:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						KuanXiangJiaoJieYaYunActivity.this, "早送提交失败,重试?",
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								saveBoxinfo();
								manager.getAbnormal().remove();

							}
						});
				break;
			case 17:
				if (GApplication.zaolist != null) {
					GApplication.zaolist.clear();
				}
				manager.getRuning().remove();
				GApplication.wd_user1 = null;
				GApplication.map = null;
				GApplication.wd_user2 = null;
				//GApplication.zaolist.clear();
				GApplication.jiaojiestate = 0;
				//弹出个提示框标识交接成功  点击其他的地方不让他消失
				dialog = new Dialog(KuanXiangJiaoJieYaYunActivity.this);
				LayoutInflater inflater2 = LayoutInflater.from(KuanXiangJiaoJieYaYunActivity.this);
				View v2 = inflater2.inflate(R.layout.dialog_success, null);
				Button but2 = (Button)v2.findViewById(R.id.success);
				dialog.setCancelable(false);
				dialog.setContentView(v2);
				if(but2!=null){
					but2.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// 启动异步提交数据   然后获取数据
							Skip.skip(KuanXiangJiaoJieYaYunActivity.this,JiaoJieActivity.class, null, 0);
							dialog.dismiss();
						}
					});
				}
				dialog.show();				
				break;
			case 18:
				
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						KuanXiangJiaoJieYaYunActivity.this, "提交超时,重试?",
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								saveBoxinfo();
								manager.getAbnormal().remove();

							}
						});
				break;
			case 19:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(
						KuanXiangJiaoJieYaYunActivity.this, "提交超时,重试?",
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								saveBoxinfo();
								manager.getAbnormal().remove();
							}
						});
				break;
			default:
				break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			if (GApplication.zaolist != null) {
				GApplication.zaolist.clear();
			}
			if(GApplication.zyzaolist!=null){
				GApplication.zyzaolist.clear();
			}
			if(GApplication.zssqlist!=null){
				GApplication.zssqlist.clear();
			}		
			GApplication.wd_user1 = null;
			GApplication.map = null;
			GApplication.wd_user2 = null;
			GApplication.jiaojiestate = 0;			
			Skip.skip(KuanXiangJiaoJieYaYunActivity.this,JiaoJieActivity.class, null, 0);
			KuanXiangJiaoJieYaYunActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onStop() {
		super.onStop();
		ShareUtil.ivalBack = null;
		manager.getRuning().remove();
		getRfid().close_a20();
	}
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			ShareUtil.ivalBack = null;
			String name = bundle.getString("name");
			finger.setImageResource(R.drawable.sccuss);
			fname.setText(name);
			top.setText("验证成功!");
			// 提交交接信息
			if (GApplication.jiaojiestate == 1) {
				// 调用早收交接 完成
				saveBoxinfo();
			} else if (GApplication.jiaojiestate == 2) {
				//调用晚收交接	接口 没有进入早送申请页面的提交方式
				saveNoZsBoxWanShou();			
			} else if(GApplication.jiaojiestate == 3){
				//调用晚收交接   进入早送申请页面的提交方式
				saveBoxWanShou();	
			}
		}
	}

}
