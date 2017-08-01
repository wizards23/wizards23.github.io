package com.poka.device;

import java.io.File;
import java.io.IOException;

import poka_global_constant.GlobalConstant;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android_serialport_api.SerialPort;



public class RFIDDevice {

	private SerialPort rfidSerialPort;

	private RFIDThread rfidThread;
	private GPIO PowerIO;
	private GPIO enableIO;
	private BeepDevice beeper;
	private int interval = 500;
	private static final String tag = "RFIDDevice";
	public static boolean taskTime;// 计时线程
	public static int repeatCodeCount;// 纪录循环扫描条码数量
	public static boolean stopRfidThread;// 是否停止rfidthread扫描线程
	public static boolean isRepeatTimeOut = true;// 是否连续读取条码超时
	private TaskTimeThread taskTimeThread;
	private TemperaterThread temperaterThread;

	public BeepDevice getBeeper() {
		return beeper;
	}

	public void setBeeper(BeepDevice beeper) {
		this.beeper = beeper;
	}

	public SerialPort getRfidSerialPort() {
		return rfidSerialPort;
	}

	public void setRfidSerialPort(SerialPort rfidSerialPort) {
		this.rfidSerialPort = rfidSerialPort;
	}

	public synchronized int getInterval() {
		return interval;
	}

	public synchronized void setInterval(int interval) {
		this.interval = interval;
	}

	private Handler handler;

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public RFIDDevice() {
		System.out.println(tag + "init()--->RFIDDevice()");
		for (int i = 0; i < 2; i++) {
			File rFidFile = new File(DeviceInfo.RFID_FILE);
			try {
				this.rfidSerialPort = new SerialPort(rFidFile,
						DeviceInfo.RFIDDEVICE_KEY, 0);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (i == 0) {
				this.rfidSerialPort.close();
				Log.i("RFIDDevice", "rfidserialPort.close()");
			}
			System.out.println("DeviceManage---->init---->rfidSerialPort");
		}
		PowerIO = new GPIO(GlobalConstant.IO_RFID_POWER);
		enableIO = new GPIO(GlobalConstant.IO_RFID_ENABLE);

	}

	// 打开RFID串口
	public Boolean openRFID(Context context) {
		if (rfidSerialPort == null) {
			for (int i = 0; i < 2; i++) {
				File rFidFile = new File(DeviceInfo.RFID_FILE);
				try {
					this.rfidSerialPort = new SerialPort(rFidFile,
							DeviceInfo.RFIDDEVICE_KEY, 0);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (i == 0) {
					this.rfidSerialPort.close();
					Log.i("RFIDDevice", "rfidserialPort.close()");
				}
				System.out.println("DeviceManage---->init---->rfidSerialPort");
			}
		}
		if (rfidThread == null) {
			rfidThread = new RFIDThread();
			rfidThread.start();
			Log.i("ThreadManage", "RFIDThread------------------------->start");
		}

		if (beeper == null) {
			beeper = BeepDevice.getBeepDeviceObj();

		}
		if (temperaterThread == null) {
			temperaterThread = new TemperaterThread();
			temperaterThread.start();
			Log.i("ThreadManage",
					"temperaterThread------------------------->start");
		}
		if (taskTimeThread == null) {
			taskTimeThread = new TaskTimeThread();
			taskTimeThread.start();
			Log.i("ThreadManage",
					"taskTimeThread------------------------->start");
		}
		int count = 1;
		while (count != 4) {
			try {

				enableIO.setGPIO(GlobalConstant.ENABLE_IO);
				PowerIO.setGPIO(GlobalConstant.ENABLE_IO);
				System.out
						.println("RFIDDevice------------>openRfid()-------->start");
				try {
					Thread.sleep(200);
					// lanp----------------------->初始化设置信息
					if (count < 3) {
						if (getXmlDeviceInfo(context)) {
							break;

						} else {
							count++;
							PowerIO.setGPIO(GlobalConstant.DISABLE_IO);
							Thread.sleep(200);
						}

					} else {
						Log.i("RFIDDevice", "设置参数失败，采用默认参数");
						break;
					}

					// lanp--------------------------->end
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}

			} catch (SecurityException e) {
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	public Boolean closeRFID() {
		System.out.println(tag + "--->closeRFID()");
		if (taskTimeThread != null) {
			taskTimeThread.setThreadSignal(false);
			while (true) {
				if (taskTime == false) {
					break;
				}
			}
		}
		if (rfidThread != null) {
			rfidThread.stopThread();
			while (true) {
				if (RFIDDevice.isRepeatTimeOut) {
					break;
				}
			}
		}
		if (beeper != null) {
			beeper.getmHandler().removeCallbacksAndMessages(null);
		}
		if (PowerIO != null) {
			PowerIO.setGPIO(GlobalConstant.DISABLE_IO);
		}
		System.out.println("close RFID device !");
		System.out.println(tag + "---->closeRFID()");
		return true;
	}

	public Boolean overRFIDThread() {
		if (rfidThread != null) {
			rfidThread.offThread();
		}
		if (rfidSerialPort != null) {
			rfidSerialPort.close();
		}
		if (taskTimeThread != null) {
			taskTimeThread.setOff(false);
		}
		if (temperaterThread != null) {
			temperaterThread.ondestory();
		}

		System.out.println(tag + "---->overRFIDThread()");

		return true;
	}

	public Boolean enableRepeatReadRFID(int what) {
		if (rfidSerialPort.write("2000repeat") == 1) {
			Message msgMessage = Message.obtain();
			msgMessage.what = what;
			msgMessage.obj = DeviceInfo.RFID_REPEAT;
			if (handler != null) {
				handler.sendMessage(msgMessage);
			}
		}
		repeatCodeCount = 0;
		isBeeper = true;//关闭蜂鸣器
		if (rfidThread != null) {
			rfidThread.startThread();
		}
		if (taskTimeThread != null) {
			taskTimeThread.setThreadSignal(true);
		}
		return true;
	}

	public Boolean enableRepeatReadRFID() {
		rfidSerialPort.write("2000repeat");
		if (rfidThread != null) {
			rfidThread.startThread();
		}
		repeatCodeCount = 0;
		return true;
	}

	public Boolean wenableRepeatReadRFID(int what) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (rfidSerialPort.write("2000repeat") == 1) {
			Message msgMessage = Message.obtain();
			msgMessage.what = what;
			msgMessage.obj = DeviceInfo.RFID_REPEAT;
			if (handler != null) {
				handler.sendMessage(msgMessage);
			}

		}
		isBeeper = false;
		if (rfidThread != null) {
			rfidThread.startThread();
		}
		return true;
	}

	public Boolean stopRFID() {
		System.out.println(tag + "--->stopRFID()");
		if (taskTimeThread != null) {
			taskTimeThread.setThreadSignal(false);
			while (true) {
				if (taskTime == false) {
					break;
				}
			}
		}
		if (rfidThread != null) {
			rfidThread.stopThread();
			while (true) {
				if (RFIDDevice.isRepeatTimeOut) {
					break;
				}
			}
			if (rfidSerialPort.write("2000stop") == 1) {

			} else {

			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return true;

	}

	public Boolean stopRFID(int temp) {
		if (rfidThread != null) {
			rfidThread.stopThread();
			while (true) {
				if (RFIDDevice.isRepeatTimeOut) {
					break;
				}
			}

			rfidSerialPort.write("2000stop");
		}
		return true;

	}

	/**
	 * 提取设置界面信息
	 * 
	 * @param context
	 */
	public boolean getXmlDeviceInfo(Context context) {
//		System.out.println(tag + "--->getXmlDeviceInfo(Context context) ");
//		// 取得活动的Preferences对象
//		if (context == null) {
//			System.out.println("getXmlDeviceInfo  context = null !");
//		}
//
//		SharedPreferences settings = context.getSharedPreferences(
//				UserSetting.sharePref, Context.MODE_PRIVATE);
//		UserSetting set = new UserSetting(settings);
//		if (0 == rfidSerialPort.setParameter("power", set.getPower())) {
//			System.out.println(tag + "------->power set error!");
//			return false;
//
//		}
//		if (rfidSerialPort.setParameter("q_value", set.getQValue()) == 0) {
//
//			System.out.println(tag + "------->q_value set error!");
//			return false;
//		}
//		if (rfidSerialPort.setParameter("link", set.getLinkValue()) == 0) {
//
//			System.out.println(tag + "------->link set error!");
//			return false;
//		}
//
//		System.out.println("getInterval()>>>>>>>>>>>>" + set.getInterval());
//		System.out.println("get power value is >>>>>>" + set.getPower());
//		System.out.println("get Q value is >>>>>>" + set.getQValue());
//		System.out.println("get link value is >>>>>>" + set.getLinkValue());
		return true;
	}

	public Boolean singleReadRFID() {
		System.out.println(tag + "--->singleReadRFID(Handler handler)");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String codeString = rfidSerialPort.singleRead();
		if (codeString != null && !codeString.equals("")) {
			System.out.println(tag + "-->codeString=" + codeString);
			Message msg = Message.obtain();
			Bundle bundle = new Bundle();
			bundle.putString("code", codeString);
			msg.setData(bundle);
//			msg.what = ScanWriteActivity.readRFID;
			msg.what = 12;
			if (handler != null) {
				handler.sendMessage(msg);
			}

			System.out.println(tag
					+ " ---->singleReadRFID(Handler handler)--->true");
			codeString = null;
			return true;
		} else {
			System.out.println(tag + "-->codeString=" + codeString);
			Message msg = Message.obtain();
			Bundle bundle = new Bundle();
			bundle.putString("code", null);
			msg.setData(bundle);
//			msg.what = ScanWriteActivity.readRFID;
			msg.what = 12;
			if (handler != null) {
				handler.sendMessage(msg);
			}
			System.out.println(tag
					+ " ---->singleReadRFID(Handler handler)--->false");
			codeString = null;
			return false;
		}
	}

	public int setParameter() {
		return rfidSerialPort.setDefaultParameters();
	}

	public void writeRFID(String codeString) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(tag
				+ "--->writeRFID(String codeString, Handler handler)");
		Message msg = Message.obtain();
		Bundle bundle = new Bundle();
		for (int i = 0; i < 3; i++) {
			if (1 == rfidSerialPort.write(codeString)) {
				if (this.getBeeper() != null) {
					Message mg = Message.obtain();
					mg.what = 1;
					if (this.getBeeper().getMyLooper() != null) {
						if (!this.getBeeper().Objstate) {
							this.getBeeper().getmHandler().sendMessage(mg);
						}
					}
				}
				bundle.putString("code", "OK");
				break;
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (i == 2) {
					bundle.putString("code", "No");
					break;
				}
			}
		}
//		msg.what = ScanWriteActivity.writeRFID;
		msg.what = 11;
		msg.setData(bundle);
		if (handler != null) {
			handler.sendMessage(msg);
		}
	}

	private boolean isBeeper;// 是否是写入界面检测

	class RFIDThread extends Thread {

		private Boolean threadSignal = false;
		private Boolean isOff;

		public RFIDThread() {
		}

		public synchronized void startThread() {
			threadSignal = true;
		}

		public synchronized void stopThread() {
			threadSignal = false;
		}

		public synchronized void offThread() {
			isOff = false;
			System.out.println(tag + "--->offThread()--->isOff=false");
		}

		@Override
		public void run() {

			super.run();
			isOff = true;
			while (isOff) {
				synchronized (this) {
					if (!threadSignal) {
						stopRfidThread = true;
						isRepeatTimeOut = true;
						continue;
					}
				}
				stopRfidThread = false;
				isRepeatTimeOut = false;
				String codeString = rfidSerialPort.repeatRead();
				System.out.println("codeString ----------------------->"
						+ codeString);
				if ("timeout".equals(codeString)) {
					System.out
							.println("codeString ----------------------->timeout"
									+ codeString);
					isRepeatTimeOut = true;
					continue;
				}
				isRepeatTimeOut = true;
				if (codeString == null || codeString.equals("")) {
					continue;
				}
				codeString = codeString.trim();
				repeatCodeCount++;
				Message msg1 = Message.obtain();
				msg1.what = 0;
				if (isBeeper) {
					if (beeper.getMyLooper() != null) {
						beeper.getmHandler().sendMessage(msg1);
					}
				}
				Message msg = Message.obtain();
				Bundle bundle = new Bundle();
				bundle.putString("code", codeString);
				
				msg.setData(bundle);
				msg.what = 10;
				
				System.out.println("开始传递Coding");
				if (handler != null) {
					handler.sendMessage(msg);
				}
				codeString = null;
				stopRfidThread = true;
			}
			Log.i("ThreadManage", "RFIDThread------------------------->end");
		}
	}

	// --------->lp
	class TaskTimeThread extends Thread {
		private boolean isOff;
		private boolean threadSignal;

		public boolean isThreadSignal() {
			return threadSignal;
		}

		public synchronized void setThreadSignal(boolean threadSignal) {
			this.threadSignal = threadSignal;
		}

		public synchronized boolean getThreadSignal() {
			return threadSignal;
		}

		public boolean isOff() {
			return isOff;
		}

		public void setOff(boolean isOff) {
			this.isOff = isOff;
		}

		@Override
		public void run() {
			super.run();
			isOff = true;
			while (isOff) {
				synchronized (this) {
					if (!getThreadSignal()) {
						taskTime = false;
						continue;
					}
				}
				taskTime = true;
				try {

					Thread.sleep(500);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				stopRFID(0);
				while (true) {
					if (stopRfidThread) {
						break;
					}
				}
				try {
					Log.i("RFIDDevice", "repeatCodeCount" + repeatCodeCount);
					if (repeatCodeCount < 2) {
						Thread.sleep(500);
					} else {
						for (int i = 0; i < 10; i++) {
							Message msg1 = Message.obtain();
							msg1.what = 0;
							if (isBeeper) {
								if (beeper.getMyLooper() != null) {
									beeper.getmHandler().sendMessage(msg1);
								}
							}
							Thread.sleep(51);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				enableRepeatReadRFID();
			}
			taskTime = false;
			Log.i("ThreadManage", "taskTimeThread------------------------->end");
		}
	}

	// ------------>end
	// -------------->lp温度监控
	public static Handler temperaterHandler;

	public static Handler getTemperaterHandler() {
		return temperaterHandler;
	}

	public void setTemperaterHandler(Handler temperaterHandler) {
		this.temperaterHandler = temperaterHandler;
	}

	class TemperaterThread extends Thread {

		public void ondestory() {
			if (temperaterHandler != null) {
				temperaterHandler.removeCallbacksAndMessages(null);
				temperaterHandler.getLooper().quit();
			}
		}

		@Override
		public void run() {
			super.run();
			Looper.prepare();
			temperaterHandler = new Handler() {
				public int i = 0;

				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					// 获取温度监控jni接口
					Log.i("RFIDDevice", "temperaterHandler----->msg.what"
							+ msg.what);
					i = rfidSerialPort.gettemp();
					Log.i("RFIDDevice",
							"temperaterHandler----->	i = rfidSerialPort.gettemp();"
									+ i);
					// end
					if (handler != null) {
						Message mg = Message.obtain();
						mg.what = msg.what;
						mg.obj = i;
						handler.sendMessage(mg);
					}
				}

			};
			Looper.loop();
			Log.i("ThreadManage",
					"temperaterThread------------------------->end");
		}
	}
	// -------------->end
}
