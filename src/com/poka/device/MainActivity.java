package com.poka.device;



import hdjc.rfid.operator.RFID_Device;

import java.lang.ref.WeakReference;

import com.example.pda.R;



import cn.poka.util.ShareUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity{
	public static final int scanCheck = 0x88;
	public static final int scanMsg = 10;
	public static final int send_Data = 0x300;
	public static final int fingerIval = 0x224;
	private Button closebtn,openbtn;
	RFID_Device rfiddev = new  RFID_Device();
	private Button read,scanOpen,scanClose;
	private EditText writeLable;
	private Button write;
	public static final int fingerPraper = 0x222;
	static class ScanHandler extends Handler{
		WeakReference<MainActivity>  mActivity;
		
		public ScanHandler(MainActivity activity) {
			mActivity = new WeakReference<MainActivity>(activity);
		}
		
		
		@Override
		public void handleMessage(android.os.Message msg) {
			MainActivity theActivity = mActivity.get();
			switch (msg.what) {
			case scanMsg:
				Bundle bundle = msg.getData();
				String code = bundle.getString("code");
				System.out.println("是否传递回来值");
				
				ShareUtil.toastShow(code,theActivity);
				break;
			case fingerPraper:
							
					ShareUtil.toastShow("正在获取指纹特征值！", theActivity);
					break;
			case send_Data:
			//	String luru = "MATCH_FINGER";
				String luru = "ENROLL_FINGER";
				byte[] t = luru.getBytes();
				byte[] temp = new byte[76];
				for (int i = 0; i < t.length; ++i)
					temp[i] = t[i];
			//	ShareUtil.isCaiji = false;
				DeviceManage.fingersDevcie.getSerialPort().serialWrite(temp);
				break;
			case 281:
				String close = "QUIT";
				byte[] tt = close.getBytes();
				byte[] tempclose = new byte[76];
				for (int i = 0; i < tt.length; ++i)
					tempclose[i] = tt[i];
			//	ShareUtil.isCaiji = false;
				System.out.println("发送关闭命令");
				DeviceManage.fingersDevcie.getSerialPort().serialWrite(tempclose);
				break;
			case fingerIval:
				
				byte[] ival = (byte[]) msg.obj;
				
			//	theActivity.iValBack = ival;
			//	ShareUtil.ivalBack = ival;
				ShareUtil.toastShow("指纹特征值获取成功！", theActivity);
				break;
			}
		};
	}
	
	
	
	
	
	
	private ScanHandler h = new ScanHandler(this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maindemo);
//		DeviceManage.setMainHandler(h);
		DeviceManage.setLoginAcitvityHandler(h);//设置指纹线程
//		rfiddev.fingerOpen();
		
		 new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message mg = Message.obtain();
					mg.obj = DeviceInfo.FINGER_OPEN;
					
					
					try {
						System.out.println(DeviceManage.getDeviceMObject()+"主界面是否为null");
						Thread.sleep(2500);
						DeviceManage.getDeviceMObject().getFingerHandler().sendMessage(mg);
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						h.sendMessageDelayed(h.obtainMessage(send_Data), 100);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("发生异常");
					}finally{
//						DeviceManage.getDeviceMObject().getFingerHandler().sendMessage(mg);
//						try {
//							Thread.sleep(5000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						h.sendMessageDelayed(h.obtainMessage(send_Data), 100);
					}
				}
			}).start();
		writeLable = (EditText) findViewById(R.id.writeLabelTest);
		
		openbtn = (Button) findViewById(R.id.openRFID);
		
		openbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				rfiddev.open();
				}

			
		});
		closebtn = (Button) findViewById(R.id.closeRFID);
		closebtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				rfiddev.close();
				}

			
		});
		DeviceManage.context = this;
		
		write = (Button) findViewById(R.id.writeTest);
		write.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				rfiddev.writeRFID("6666666666");

			}
		});
		read = (Button) findViewById(R.id.readTest);
		read.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				System.out.println("单次读返回的结果"+rfiddev.singleReadOpen());
				
			}
		});
		
		scanOpen = (Button) findViewById(R.id.scanOpen);
		scanOpen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				rfiddev.scanOpen();
				
			}
		});
		scanClose = (Button) findViewById(R.id.scanClose);
		scanClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				rfiddev.scanclose();
				
			}
		});
	}

}
