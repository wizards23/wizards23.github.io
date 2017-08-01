package com.poka.device;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;



import poka_global_constant.GlobalConstant;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android_serialport_api.SerialPort;

public class ScanDevice {

	private SerialPort serialPort;
	private ScanButton scanButton;
	private ScanThread scanThread;
	private GPIO scanPowerIO;
	private Handler handler;
	private BeepDevice beepDevice;
	private ScanCheckReadThread readThread;

	private static final String TAG = "ScanDevice";
	// 保存已有扫描的条码
	private HashMap<String, String> map;
	public static Handler mHandler;
	private DeepManage deepManage;

	//
	public Handler getHandler() {
		return handler;
	}

	public SerialPort getSerialPort() {
		return serialPort;
	}

	public void setSerialPort(SerialPort serialPort) {
		this.serialPort = serialPort;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
		
	}

	public ScanDevice() {

		File sCanFile = new File(DeviceInfo.SCAN_FILE);
		try {
			serialPort = new SerialPort(sCanFile, DeviceInfo.SCANDEVICE_KEY, 0);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("DeviceManage---->init---->sCanSerialPort");
		scanPowerIO = new GPIO(GlobalConstant.IO_SCAN_POWER);
		scanButton = new ScanButton();
		System.out.println(TAG
				+ "-->ScanDevice(Handler handler, SerialPort serialPort)");
	}

	public boolean openScan(int what) {
		if (serialPort == null) {
			File sCanFile = new File(DeviceInfo.SCAN_FILE);
			try {
				serialPort = new SerialPort(sCanFile,
						DeviceInfo.SCANDEVICE_KEY, 0);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("DeviceManage---->init---->sCanSerialPort");
		}

		if (deepManage == null) {
			System.out
					.println("ScanDevice----------------------------->start--->DeepManage()");
			deepManage = new DeepManage();
			deepManage.start();
			Log.i("ThreadManage",
					"deepManageThread------------------------->start");
		}
		if (scanThread == null) {
			scanThread = new ScanThread();
			scanThread.start();
			Log.i("ThreadManage", "scanThread------------------------->start");
		}
		if (beepDevice == null) {
			beepDevice = BeepDevice.getBeepDeviceObj();
		}
		if (scanButton == null) {
			scanButton = new ScanButton();
		}
		if (readThread == null) {
			readThread = new ScanCheckReadThread();
			readThread.start();
			Log.i("ThreadManage",
					"ScanCheckReadThread------------------------->start");
		}
		if (map == null) {
			map = new HashMap<String, String>();
		}
		try {
			scanPowerIO.setGPIO(GlobalConstant.ENABLE_IO);
			System.out.println(TAG + "-->ScanDevice----->Power UP!");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(TAG + "------->Port OPEN!");
			serialPort.write("scansetpara");
			System.out.println(TAG + "------>para set!");
			readThread.startThread();
			System.out.println(TAG + "--->readThread()--runningwork");
			scanThread.startThread();
			System.out.println(TAG + "--->startThreand()--runningwork");

		} catch (SecurityException e) {
			e.printStackTrace();
			return false;
		}
		Message msgMessage = Message.obtain();
		msgMessage.what = what;
		msgMessage.obj = DeviceInfo.SCAN_OPEN;
		if (handler != null) {
			handler.sendMessage(msgMessage);
		}

		return true;
	}

	public boolean closeScan() {
		System.out.println(TAG + "--->ScanDevice----->closeSan()");
		if (scanThread != null) {
			scanThread.stopThread();
		}
		if (scanPowerIO != null) {
			scanPowerIO.setGPIO(GlobalConstant.DISABLE_IO);
		}

		if (readThread != null) {
			readThread.stopThread();
		}
		if (beepDevice != null) {
			beepDevice.getmHandler().removeCallbacksAndMessages(null);
		}

		return true;
	}

	public boolean overScanThread() {
		if (scanThread != null) {
			Log.i("DeviceManage", "scanThread.destoryThread()");
			scanThread.destoryThread();
			scanThread.interrupt();
		}
		if (readThread != null) {
			readThread.destoryThread();
		}
		if (serialPort != null) {
			serialPort.close();
		}
		if (map != null) {
			map.clear();

		}
		if (deepManage != null) {
			deepManage.ondestory();

		}
		System.out.println("ScanDevice-------->overScanThread()");
		if (serialPort != null) {
			serialPort.close();
		}
		return true;
	}

	class ScanThread extends Thread {

		private Boolean threadSignal = false;
		private Boolean isOff;

		public synchronized void startThread() {
			threadSignal = true;
		}

		public synchronized void stopThread() {
			threadSignal = false;
		}

		public synchronized boolean getThreadSignal() {
			return threadSignal;
		}

		public void destoryThread() {
			isOff = false;
		}

		@Override
		public void run() {
			super.run();
			isOff = true;
			int count = 0;
			while (isOff) {

				try {
					sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (this) {
					if (!getThreadSignal()) {
						count = 0;
						continue;
					}
				}
				count++;
				// ---------------------->lp
				if (count == 1) {

					try {
						Thread.sleep(5000);
						if (serialPort != null) {
							serialPort.write("scanopen");
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// ------------------------->end
				/**
				 * if (scanButton.isPressButton()) { System.out.println(TAG +
				 * "--->button pressed!"); serialPort.write("scanopen"); } else
				 * { System.out.println(TAG + "--->no button pressed!");
				 * continue; }
				 **/
			}
			Log.i("ThreadManage", "scanThread------------------------->end");
		}
	}

	// -------------------------------------------->

	class ScanCheckReadThread extends Thread {

		private Boolean threadSignal = false;
		private Boolean isOff;
		private String codeString;

		public void startThread() {
			threadSignal = true;
		}

		public void stopThread() {
			threadSignal = false;
		}

		public void destoryThread() {
			isOff = false;
		}

		@Override
		public void run() {
			super.run();
			System.out.println(TAG
					+ "--->ScanCheckReadThread ........................start!");
			isOff = true;
			while (isOff) {
				synchronized (this) {
					if (!threadSignal) {
						continue;
					}
				}

				codeString = serialPort.scanRead();
				if (codeString == null || codeString.equals("")
						|| codeString.getBytes().length < 1) {
					continue;
				} else {
					codeString = codeString.trim();
					Message mg = Message.obtain();
					Bundle bundle = new Bundle();
					bundle.putString("code", codeString);
					mg.setData(bundle);
					mg.what = 10;
					if (handler != null) {
						handler.sendMessage(mg);
					}
					System.out.println(TAG
							+ "-->handler.sendMessage(msg)--handler--"
							+ handler);
					if (beepDevice != null) {

						if (map != null && map.containsKey(codeString)) {
							// 重复响声
							Message msg = Message.obtain();
							msg.what = 1;
							if (beepDevice.getMyLooper() != null) {
								if (!beepDevice.Objstate) {
									beepDevice.getmHandler().sendMessage(msg);
								}
							}
						} else {
							map.put(codeString, codeString);
							System.out
									.println("ScanDevice------------->beep(50)");
							Message msg = Message.obtain();
							msg.what = 0;
							if (beepDevice.getMyLooper() != null) {
								if (!beepDevice.Objstate) {
									beepDevice.getmHandler().sendMessage(msg);
								}
							}
						}

					}
				}
			}
			Log.i("ThreadManage",
					"ScanCheckReadThread------------------------->end");
			System.out.println(TAG
					+ "-->ScanCheckReadThread.........................off!");
		}
	}

	// ------------------------------------------------

	//

	class DeepManage extends Thread {
		public DeepManage() {

		}

		public void ondestory() {
			if (mHandler != null) {
				mHandler.removeCallbacksAndMessages(null);
				mHandler.getLooper().quit();
			}
		}

		@Override
		public void run() {
			super.run();
			System.out.println("ScanDevice------------->"
					+ "deepManageThrad------>start");
			Looper.prepare();
			mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case 1:
						System.out.println("ScanDevice------------->"
								+ "msg.what=1");
						if (beepDevice != null) {
							Message mg = Message.obtain();
							mg.what = 2;
							if (beepDevice.getMyLooper() != null) {
								if (!beepDevice.Objstate) {
									beepDevice.getmHandler().sendMessage(mg);
								}
							}
						}
						break;

					default:
						break;
					}

				}
			};
			Looper.loop();
			Log.i("ThreadManage",
					"deepManageThread------------------------->end");
		}

	}

	//
}

class ScanButton {
	private GPIO scanButtonIO;

	public ScanButton() {
		scanButtonIO = new GPIO(8);
	}

	public Boolean isPressButton() {
		if (1 == scanButtonIO.getGPIO()) {
			return false;
		}
		return true;
	}
}
