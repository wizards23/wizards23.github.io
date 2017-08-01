package com.poka.device;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android_serialport_api.SerialPort;


import cn.poka.util.ShareUtil;


/**
 * 
 * 
 *
 * 
 */
public class DeviceManage {
	public static DeviceManage dM;
	public static SerialPort wirelessCanSerialPort;
	public static FingerDevcie fingersDevcie;
	public static RFIDDevice fRIDDevice;
	public static ScanDevice scanDevice;
	public static Handler mHandler;
	public static int interval;
	public static Context context;
	private String codeString = null;
	
	public String getCode() {
		return codeString;
	}

	public void setCode(String code) {
		this.codeString = code;
	}

	// 设备线程
	public static RfidDeviceManageThread rfidThread;
	
	public static ScanDeviceManageThread scanThread;
	public static FingerDevcieManageThread fingerThread;

	/**
	 * 单例模式
	 */
	private DeviceManage() {

	}

	public static DeviceManage getDeviceMObject() {
		initDevice();
		if (dM == null) {
			Log.i("DeviceManage", "getDeviceMObject---->dM==null");
			dM = new DeviceManage();
		}
		

		if (rfidThread == null) {
			rfidThread = dM.new RfidDeviceManageThread();
			rfidThread.start();
			Log.i("ThreadManage",
					"RfidDeviceManageThread------------------------->start");
		}
		if (scanThread == null) {
			scanThread = dM.new ScanDeviceManageThread();
			scanThread.start();

			Log.i("ThreadManage",
					"ScanDeviceManageThread------------------------->start");
		}
		if (fingerThread == null) {
			fingerThread = dM.new FingerDevcieManageThread();
			fingerThread.start();
			Log.i("ThreadManage",
					"FingerDevcieManageThread------------------------->start");
		}
		try {
			Thread.sleep(1500);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dM;
	}

	public void destory() {

		if (fRIDDevice != null) {
			fRIDDevice.closeRFID();
			fRIDDevice.overRFIDThread();
		}
		if (myHandler != null) {
			myHandler.removeCallbacksAndMessages(null);
			myHandler.getLooper().quit();
		}
		rfidThread = null;

		

		if (BeepDevice.BD != null) {
			BeepDevice.BD.closeBeeperThread();
		}
		
		
	}

	/**
	 * 初始化串口
	 * 
	 * @throws Exception
	 */
	public static void initDevice() {

		if (wirelessCanSerialPort == null) {
			File wirelessFile = new File(DeviceInfo.WIRELESS_FILE);//
			try {
				wirelessCanSerialPort = new SerialPort(wirelessFile,
						DeviceInfo.WIRELESS_KEY, 0);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 设置主线程Handler
	 * 
	 * @param handler
	 */

	public static void setMainHandler(Handler handler) {
		mHandler = handler;
		if (fRIDDevice != null) {
			fRIDDevice.setHandler(handler);
		}
		if (scanDevice != null) {
			scanDevice.setHandler(handler);
			
		}

	}

	public static void setLoginAcitvityHandler(Handler handler) {
		if (fingersDevcie != null) {
			fingersDevcie.setHandler(handler);
		}
	}


	public void setContext(Context con) {
		context = con;
	}

	private Handler myHandler;

	public Handler getMyHandler() {
		Log.i("DeviceMsg", " getMyHandler()   =   " + myHandler);
		return myHandler;
	}

	public void setMyHandler(Handler myHandler) {
		this.myHandler = myHandler;
	}

	class RfidDeviceManageThread extends Thread {
		public RfidDeviceManageThread() {
			fRIDDevice = new RFIDDevice();

		}

		@Override
		public void run() {
			super.run();
			Looper.prepare();
			Log.i("DeviceMsg", "NEW  MYHANDLER ");
			myHandler = new Handler() {
				boolean isOpen = false;

				@Override
				public void handleMessage(Message msg) {
					String otherRfidState = null;
					otherRfidState = (String) msg.obj;
					Log.i("DeviceMsg", "otherRfidState " + otherRfidState
							+ "  msg.what= " + msg.what);
					if (fRIDDevice == null) {
						fRIDDevice = new RFIDDevice();
					}
					if(DeviceInfo.RFID_OPEN.equals(otherRfidState)){
						if (!isOpen && fRIDDevice != null) {
							isOpen = fRIDDevice.openRFID(context);
						}
					}
					if (DeviceInfo.RFID_SINGLE.equals(otherRfidState)) {
						if (!isOpen && fRIDDevice != null) {
							isOpen = fRIDDevice.openRFID(context);

						}
						if (fRIDDevice != null) {
							fRIDDevice.singleReadRFID();
						}
					}
					if (DeviceInfo.RFID_CLOSE.equals(otherRfidState)) {
						Log.i("DeviceManage", "DeviceInfo.RFID_CLOSE");

						if (isOpen) {
							if (fRIDDevice != null) {
								fRIDDevice.closeRFID();
							}
						}
						isOpen = false;
						if (ShareUtil.toRepeatrun == true) {
						}
						if (isOpen) {
							if (fRIDDevice != null) {
								fRIDDevice.closeRFID();
							}
						}
						isOpen = false;
						ShareUtil.toRepeatrun = false;
						if (mHandler != null) {
							Message msgMessage = Message.obtain();
							msgMessage.what = msg.what;
							mHandler.sendMessage(msgMessage);
							System.out.println("Device Manager  RFID_CLOSE");
						}
					}
					if (DeviceInfo.RFID_STOP.equals(otherRfidState)) {
						Log.i("DeviceManage", "DeviceInfo.RFID_STOP");
						if (!isOpen && fRIDDevice != null) {
							isOpen = fRIDDevice.openRFID(context);
						}
						if (fRIDDevice != null && isOpen == true) {
							fRIDDevice.stopRFID();
							if (ShareUtil.toRepeatrun == true) {
							}
							ShareUtil.toRepeatrun = false;
							if (mHandler != null) {
								Message msgMessage = Message.obtain();
								msgMessage.what = msg.what;
								mHandler.sendMessage(msgMessage);
								System.out.println("Device Manager  RFID_STOP");
							}
						}
					}
					if (DeviceInfo.RFID_REPEAT.equals(otherRfidState)) {
						ShareUtil.toRepeatrun = false;// lp
						Log.i("DeviceManage", "isOpen=" + isOpen);
						if (!isOpen && fRIDDevice != null) {
							Log.i("DeviceManage", "正打开－open－－》isOpen=" + isOpen);
							isOpen = fRIDDevice.openRFID(context);
							Log.i("DeviceManage", "isOpen=" + isOpen);
						}
						if (fRIDDevice != null && isOpen == true) {
							Log.i("DeviceManage", "enableRepeatReadRFID");
//							if (msg.what == ScanWriteActivity.scanCheck) {
							if (msg.what == 1) {
								fRIDDevice.wenableRepeatReadRFID(msg.what);
							} else {
								fRIDDevice.enableRepeatReadRFID(msg.what);
							}

							ShareUtil.toRepeatrun = true;// lp
						}

					}
					if (DeviceInfo.RFID_WRITE.equals(otherRfidState)) {
						if (!isOpen && fRIDDevice != null) {
							isOpen = fRIDDevice.openRFID(context);
						}
						if (fRIDDevice != null && isOpen == true) {
							Log.i("DeviceManage", "fRIDDevice.writeRFID()");
							fRIDDevice.writeRFID(codeString);
//							fRIDDevice.writeRFID("12345678901234567890");
						}
					}
				}
			};
			Looper.loop();
			fRIDDevice = null;
			Log.i("ThreadManage",
					"RFIDDeviceManageThread------------------------->end");
		}
	}

	private Handler scanHandler;

	public Handler getScanHandler() {
		Log.i("DeviceMsg", "getScanHandler()    =   " + scanHandler);
		return scanHandler;
	}

	public void setScanHandler(Handler scanHandler) {
		this.scanHandler = scanHandler;
	}

	class ScanDeviceManageThread extends Thread {

		public ScanDeviceManageThread() {
			scanDevice = new ScanDevice();
		}

		@Override
		public void run() {
			super.run();

			//
			Looper.prepare();
			Log.i("DeviceMsg", "NEW  ScanHANDLER ");
			scanHandler = new Handler() {
				boolean isOff = false;

				@Override
				public void handleMessage(Message msg) {

					String otherScanState = null;
					otherScanState = (String) msg.obj;
					Log.i("DeviceMsg", "otherScanState " + otherScanState
							+ " msg.what= " + msg.what);
					if (DeviceInfo.SCAN_OPEN.equals(otherScanState)) {
						if (scanDevice != null) {
							isOff = scanDevice.openScan(msg.what);
							Log.i("DeviceManage",
									"DeviceInfo.SCAN_OPEN--->isOff  " + isOff);
						}
					}
					if (DeviceInfo.SCAN_CLOSE.equals(otherScanState)) {
						if (isOff) {
							if (scanDevice != null) {
								scanDevice.closeScan();
							}
						}
						isOff = false;
						Message msgMessage = Message.obtain();
						msgMessage.what = msg.what;
						msgMessage.obj = DeviceInfo.SCAN_CLOSE;
						if (mHandler != null) {
							mHandler.sendMessage(msgMessage);
							Log.i("DeviceManage", "DeviceInfo.SCAN_CLOSE");
						}

					}
				}
			};
			Looper.loop();
			scanDevice = null;
			Log.i("ThreadManage",
					"ScanDeviceManageThread------------------------->end");
		}
	}

	private Handler fingersHandler;

	public Handler getFingerHandler() {
		return fingersHandler;
	}

	public void setFingerHandler(Handler fingerHandler) {
		this.fingersHandler = fingerHandler;
	}
	class FingerDevcieManageThread extends Thread {
		public FingerDevcieManageThread() {
			fingersDevcie = new FingerDevcie();
			
		}

		@Override
		public void run() {
			super.run();
			Looper.prepare();
			fingersHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					String otherFingerState = null;
					otherFingerState = (String) msg.obj;
					Log.i("DeviceMsg", "otherFingerState " + otherFingerState
							+ " msg.what= " + msg.what);
					if (DeviceInfo.FINGER_OPEN.equals(otherFingerState)) {
						if (fingersDevcie != null) {
							fingersDevcie.open();
						}
					}
					if (DeviceInfo.FINGER_CLOSE.equals(otherFingerState)) {
						if (fingersDevcie != null) {
						    fingersDevcie.close();
						}
					}
				}
			};
			Looper.loop();
			fingersDevcie = null;
			Log.i("ThreadManage",
					"FingerDevcieManageThread------------------------->end");
		}
	}
	
	
	
}
