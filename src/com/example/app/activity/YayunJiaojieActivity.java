package com.example.app.activity;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import hdjc.rfid.operator.RFID_Device;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.GApplication;
import com.entity.BoxDetail;
import com.example.app.entity.User;
import com.example.app.entity.UserInfo;
import com.example.app.util.Skip;
import com.example.pda.R;
import com.imple.getnumber.GetFingerValue;
import com.manager.classs.pad.ManagerClass;
import com.o.service.ChuKuCaoZuoSerivce;
import com.o.service.YanZhengZhiWen;
import com.poka.device.ShareUtil;

public class YayunJiaojieActivity extends Activity implements OnClickListener{
	private ImageView img;
	private TextView yayun_name, yayun_bottom,textCount;
	private User result_user;
	private String code;
	int count = 0;// 验证指纹的次数
	private RFID_Device rfid;
	private ManagerClass manager;
	List list = new ArrayList<BoxDetail>();//按键返回传给上个页面的集合免得在重新扫描
	

	
//	Bundle bundle2;
	RFID_Device getRfid() {
		if (rfid == null) {
			rfid = new RFID_Device();
		}
		return rfid;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yuyayunjiaojie);	
		load();
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
						yayun_bottom.setText("正在验证指纹....");
					} else if (bundle != null
							&& bundle.getString("finger").equals("获取指纹特征值成功！")) {
						ZhiWenYanZheng();
					}
				}
			}

		};
		getRfid().addNotifly(new GetFingerValue());
		new Thread(new Runnable() {		
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getRfid().fingerOpen();
			}
		}).start();
		
	}

	public void load() {
	//	back_cirle = (ImageView) findViewById(R.id.image_yyjj);
		textCount = (TextView) findViewById(R.id.textCount);
		yayun_name = (TextView) findViewById(R.id.yayunjiaojie_name);
		yayun_bottom = (TextView) findViewById(R.id.yayunjiaojie_bottom);
		img = (ImageView) findViewById(R.id.yayunjiaojie_img);
	//	back_cirle.setOnClickListener(this);
	}
	public void ZhiWenYanZheng(){
		manager.getRuning().runding(YayunJiaojieActivity.this, "指纹验证中...");
		new Thread(){

			@Override
			public void run() {
				super.run();
				YanZhengZhiWen yz = new YanZhengZhiWen();
				try {
					getRfid().close_a20();//防止有指纹混入
					result_user = yz.yanzhengfinger(
							GApplication.user.getOrganizationId(), "9",
							ShareUtil.ivalBack);
					if(result_user!=null){
						handler.sendEmptyMessage(7);
					}else{
						handler.sendEmptyMessage(9);
					}
				}catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(8);
				}catch (NullPointerException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(9);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(10);
				}
			}
			
		}.start();
	}

	// 款箱提交
	public void saveBoxInfo() {
		manager.getRuning().runding(YayunJiaojieActivity.this, "提交中...");
		new Thread() {
			@Override
			public void run() {
				super.run();
				/**
				 * 1.提交数据到服务器 2.跳转 出库操作交接完成页面
				 */
				try {
					String bianhao = "";
					String ids = ShareUtil.zhiwenid_left + "|"
							+ ShareUtil.zhiwenid_right;
					for (int i = 0; i < GApplication.mingxi_list.size(); i++) {
						if (i == GApplication.mingxi_list.size() - 1) {
							bianhao += GApplication.mingxi_list.get(i)
									.getBoxIds();
							break;
						}
						bianhao += GApplication.mingxi_list.get(i).getBoxIds()
								+ "|";
					}
					if (ShareUtil.zhiwenid_left != null
							&& ShareUtil.zhiwenid_right != null) {
						System.out.println("出库时---两人都成功的方法========");
						ChuKuCaoZuoSerivce cc = new ChuKuCaoZuoSerivce();
						code = cc.saveBoxSendOutEarly(
								GApplication.kxc.getXianlubianhao(), bianhao,
								GApplication.kxc.getPeisongdate(),
								GApplication.user.getOrganizationId(), "9",
								ShareUtil.ivalBack, ids,
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());
						// System.out.println("[code的值++++++++++++]" + code);
					} else if (ShareUtil.zhiwenid_left != null
							&& ShareUtil.zhiwenid_right == null) {

						System.out
								.println("出库时---第一个人验证成功，第二个人验证失败的方法========");
						// 第一个人验证成功，第二个人验证失败
						ChuKuCaoZuoSerivce cc = new ChuKuCaoZuoSerivce();
						code = cc
								.saveBoxSendOutEarly(
										GApplication.kxc.getXianlubianhao(),
										bianhao,
										GApplication.kxc.getPeisongdate(),
										GApplication.user.getOrganizationId(),
										"9",
										null,
										ShareUtil.zhiwenid_left
												+ "|"
												+ GApplication.kuguan2
														.getUserzhanghu(),
										GApplication.userInfo.getNameZhanghao(),
										GApplication.userInfo.getPwd());
					} else if (ShareUtil.zhiwenid_left == null
							&& ShareUtil.zhiwenid_right != null) {
						// 第一个人验证失败，第二个人验证成功
						// System.out.println("第一个人验证失败，第二个人验证成功.........");
						ChuKuCaoZuoSerivce cc = new ChuKuCaoZuoSerivce();
						code = cc.saveBoxSendOutEarly(
								GApplication.kxc.getXianlubianhao(), bianhao,
								GApplication.kxc.getPeisongdate(),
								GApplication.user.getOrganizationId(), "9",
								ShareUtil.ivalBack,
								GApplication.kuguan1.getUserzhanghu() + "|"
										+ ShareUtil.zhiwenid_right,
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());
						handler.sendEmptyMessage(0);
					} else if (ShareUtil.zhiwenid_left == null
							&& ShareUtil.zhiwenid_right == null) {
						ChuKuCaoZuoSerivce cc = new ChuKuCaoZuoSerivce();
						code = cc
								.saveBoxSendOutEarly(
										GApplication.kxc.getXianlubianhao(),
										bianhao,
										GApplication.kxc.getPeisongdate(),
										GApplication.user.getOrganizationId(),
										"9",
										ShareUtil.ivalBack,
										GApplication.kuguan1.getUserzhanghu()
												+ "|"
												+ GApplication.kuguan2
														.getUserzhanghu(),
										GApplication.userInfo.getNameZhanghao(),
										GApplication.userInfo.getPwd());
					}
					String codes=code.split("_")[0];
					if("00".equals(codes)){
						handler.sendEmptyMessage(0);
					}else{
						String xiaoxi=code.split("_")[1];
						Message msg=handler.obtainMessage();
						msg.obj=xiaoxi;
						msg.what=11;
						handler.sendMessage(msg);
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(12);
				} catch (NullPointerException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(12);
				} catch (Exception e) {
					
					handler.sendEmptyMessage(13);
				}
			}

		}.start();
	}
	public void WanSaveBoex(){
		manager.getRuning().runding(YayunJiaojieActivity.this, "提交中...");
		new Thread(){

			@Override
			public void run() {
				super.run();
				String bianhao = "";
				String ids = ShareUtil.zhiwenid_left + "|"
						+ ShareUtil.zhiwenid_right;
				for (int i = 0; i < GApplication.mingxi_list.size(); i++) {
					if (i == GApplication.mingxi_list.size() - 1) {
						bianhao += GApplication.mingxi_list.get(i).getBoxIds();
						break;
					}
					bianhao += GApplication.mingxi_list.get(i).getBoxIds() + "|";
				}
				try {
					if (ShareUtil.zhiwenid_left != null
							&& ShareUtil.zhiwenid_right != null) {

						ChuKuCaoZuoSerivce cc = new ChuKuCaoZuoSerivce();
						code =cc.saveBoxStorageLate(GApplication.kxc.getXianlubianhao(),
								bianhao, GApplication.user.getOrganizationId(),
								"9", ShareUtil.ivalBack, ids,
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());
					} else if (ShareUtil.zhiwenid_left != null
							&& ShareUtil.zhiwenid_right == null) {

						// 第一个人验证成功，第二个人验证失败
						ChuKuCaoZuoSerivce cc = new ChuKuCaoZuoSerivce();
						code =cc.saveBoxStorageLate(GApplication.kxc.getXianlubianhao(),
								bianhao, GApplication.user.getOrganizationId(),
								"9", ShareUtil.ivalBack,
								ShareUtil.zhiwenid_left + "|"
										+ GApplication.kuguan2.getUserzhanghu(),
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());
					} else if (ShareUtil.zhiwenid_left == null
							&& ShareUtil.zhiwenid_right != null) {
						// 第一个人验证失败，第二个人验证成功
						ChuKuCaoZuoSerivce cc = new ChuKuCaoZuoSerivce();
						code =cc.saveBoxStorageLate(GApplication.kxc.getXianlubianhao(),
								bianhao, GApplication.user.getOrganizationId(),
								"9", ShareUtil.ivalBack,
								GApplication.kuguan1.getUserzhanghu() + "|"
										+ ShareUtil.zhiwenid_right,
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());
					} else if (ShareUtil.zhiwenid_left == null
							&& ShareUtil.zhiwenid_right == null) {
						// 两个人都验证失败
						ChuKuCaoZuoSerivce cc = new ChuKuCaoZuoSerivce();
						code = cc.saveBoxStorageLate(GApplication.kxc.getXianlubianhao(),
								bianhao, GApplication.user.getOrganizationId(),
								"9", ShareUtil.ivalBack,
								GApplication.kuguan1.getUserzhanghu() + "|"
										+ GApplication.kuguan2.getUserzhanghu(),
								GApplication.userInfo.getNameZhanghao(),
								GApplication.userInfo.getPwd());
					}
					if(code!=null){
						handler.sendEmptyMessage(0);
					}else{
						handler.sendEmptyMessage(5);
					}
				}catch (SocketTimeoutException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(4);
				}catch (NullPointerException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(5);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(6);
				}
			}
			
		}.start();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				manager.getRuning().remove();
				ShareUtil.ivalBack = null;
				GApplication.chukubiaoshi = 0;
				GApplication.chukubiaoshi = 0;
				if(GApplication.mingxi_list!=null){
					GApplication.mingxi_list.clear();
				}
				
				//弹出个提示框标识交接成功  点击其他的地方不让他消失
				final Dialog dialog = new Dialog(YayunJiaojieActivity.this);
				LayoutInflater inflater = LayoutInflater.from(YayunJiaojieActivity.this);
				View v = inflater.inflate(R.layout.dialog_success, null);
				Button but = (Button)v.findViewById(R.id.success);
				dialog.setCancelable(false);
				dialog.setContentView(v);
				if(but!=null){
					but.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {
							// 启动异步提交数据   然后获取数据
							Skip.skip(YayunJiaojieActivity.this,KuanxiangChuruActivity.class, null, 0);
							dialog.dismiss();
						}
					});
				}
				dialog.show();				
				
				break;
			case 1:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(YayunJiaojieActivity.this,
						"提交超时,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								
								manager.getAbnormal().remove();
								saveBoxInfo();
							}
						});
				break;
			case 2:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(YayunJiaojieActivity.this,
						"提交失败,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								manager.getAbnormal().remove();
								saveBoxInfo();
							}
						});
				break;
			case 3:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(YayunJiaojieActivity.this,
						"网络连接失败,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								manager.getAbnormal().remove();
								saveBoxInfo();
							}
						});
				break;
			case 4:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(YayunJiaojieActivity.this,
						"提交超时,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								manager.getAbnormal().remove();
								WanSaveBoex();
							}
						});
				break;
			case 5:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(YayunJiaojieActivity.this,
						"提交失败,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								manager.getAbnormal().remove();
								WanSaveBoex();
							}
						});
				break;
			case 6:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(YayunJiaojieActivity.this,
						"网络连接失败,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								manager.getAbnormal().remove();
								WanSaveBoex();
							}
						});
				break;
			case 7:
				manager.getRuning().remove();
				GApplication.userInfo = new UserInfo("", "");
				/*
				 * revised by zhangxuewei, at 2018/08/16 弹框确认押运员
				 */
//				yayun_name.setText(result_user.getUsername());
//				img.setImageBitmap(ShareUtil.finger_gather);
//				yayun_bottom.setText("验证成功");
//				// 提交交接信息
//				if (GApplication.chukubiaoshi == 1) {
//					manager.getRuning().runding(YayunJiaojieActivity.this,
//							"验证成功，正在提交数据");
//					saveBoxInfo();
//				} else if (GApplication.chukubiaoshi == 2) {
//					manager.getRuning().runding(YayunJiaojieActivity.this,
//							"验证成功，正在提交数据");
//					WanSaveBoex();
//				}
				yayun_name.setText("");
				img.setImageBitmap(ShareUtil.finger_gather);
				yayun_bottom.setText("");
				manager.getSureCancel().makeSuerCancel2(
						YayunJiaojieActivity.this, "押运员："+result_user.getUsername(),
						new View.OnClickListener() {
							@Override
							public void onClick(View arg0) {
								manager.getSureCancel().remove();
								// 交接
								if (GApplication.chukubiaoshi == 1) {
									manager.getRuning().runding(YayunJiaojieActivity.this,
										"验证成功，正在提交数据");
									saveBoxInfo();
								} else if (GApplication.chukubiaoshi == 2) {
									manager.getRuning().runding(YayunJiaojieActivity.this,
										"验证成功，正在提交数据");
									WanSaveBoex();
								}
							}
						}, false);
				
				break;
			case 8:
				manager.getRuning().remove();
				count++;
				textCount.setText("验证失败"+count+"次");
				if (count >= 3) {
					// 跳登陆页面进行完成出库操作
					Intent intent = new Intent();
					Bundle budnle = new Bundle();
					budnle.putString("FLAG", "chuku");
					ShareUtil.finger_gather = null;
					intent.putExtras(budnle);
					Skip.skip(YayunJiaojieActivity.this,
							YayunCheckFingerActivity.class, budnle, 0);
				}
				break;
			case 9:
				manager.getRuning().remove();
				yayun_bottom.setText("验证失败");	
				count++;
				textCount.setText("验证失败"+count+"次");
				if (count >= 3) {
					// 跳登陆页面进行完成出库操作
					Intent intent = new Intent();
					Bundle budnle = new Bundle();
					budnle.putString("FLAG", "chuku");
					ShareUtil.finger_gather = null;
					intent.putExtras(budnle);
					Skip.skip(YayunJiaojieActivity.this,
							YayunCheckFingerActivity.class, budnle, 0);
				}
				
				break;
			case 10:
				manager.getRuning().remove();
				count++;
				textCount.setText("验证失败"+count+"次");
				if (count >= 3) {
					// 跳登陆页面进行完成出库操作
					Intent intent = new Intent();
					Bundle budnle = new Bundle();
					budnle.putString("FLAG", "chuku");
					ShareUtil.finger_gather = null;
					intent.putExtras(budnle);
					Skip.skip(YayunJiaojieActivity.this,
							YayunCheckFingerActivity.class, budnle, 0);
				}
				break;
			case 11:
				String xiaoxi=msg.obj.toString();
				manager.getRuning().remove();
				manager.getGoBack().back(YayunJiaojieActivity.this,xiaoxi,new View.OnClickListener(){
					@Override
					public void onClick(View arg0) {
						manager.getGoBack().remove();
						Skip.skip(YayunJiaojieActivity.this,KuanxiangChuruActivity.class, null, 0);
					}					
				});			
				break;
			case 12:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(YayunJiaojieActivity.this,
						"提交超时,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								manager.getAbnormal().remove();
								saveBoxInfo();
							}
						});
				break;
			case 13:
				manager.getRuning().remove();
				manager.getAbnormal().timeout(YayunJiaojieActivity.this,
						"网络连接失败,重试?", new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								manager.getAbnormal().remove();
								saveBoxInfo();
							}
						});
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onStop() {
		super.onStop();
		manager.getRuning().remove();
		getRfid().close_a20();
		YayunJiaojieActivity.this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//bundle2.putSerializable("list", (Serializable) list);
			Skip.skip(YayunJiaojieActivity.this,KuanxiangChuruActivity.class, null, 0);
			YayunJiaojieActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			ShareUtil.ivalBack = null;
			String name = bundle.getString("name");
			img.setImageResource(R.drawable.sccuss);
			yayun_name.setText(name);
			textCount.setText("帐号密码登录成功");
			yayun_bottom.setText("帐号密码登录成功");
			// 提交交接信息
			if (GApplication.chukubiaoshi == 1) {
				saveBoxInfo();
			} else if (GApplication.chukubiaoshi == 2) {
				WanSaveBoex();
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		/*switch (arg0.getId()) {
		case R.id.image_yyjj:
			Skip.skip(YayunJiaojieActivity.this,KuanxiangChuruActivity.class, null, 0);
			break;
		default:
			break;
		}*/
		
	}
}
