package a20.cn.poka.device;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import poka_global_constant.GlobalConstant;

/**
 * �������豸��
 */
public class BeepDevice extends Thread {

	private Handler mHandler;
	private GPIO beepIO;
	private static final String TAG = "BeepDevice";
	public static BeepDevice BD = null;
	private Looper myLooper;
	// �жϸ��߳��Ƿ����
	public boolean Objstate = false;

	public Looper getMyLooper() {
		return myLooper;
	}

	public BeepDevice(boolean isTest) {
		beepIO = new GPIO(GlobalConstant.IO_BEEP);
	}

	public void setMyLooper(Looper myLooper) {
		this.myLooper = myLooper;
	}

	private BeepDevice() {
		beepIO = new GPIO(GlobalConstant.IO_BEEP);
		super.start();
	}

	public static BeepDevice getBeepDeviceObj() {
		if (BD == null) {
			BD = new BeepDevice();
		}
		return BD;
	}

	public void closeBeeperThread() {
		if (BD != null) {
			if (BD.getMyLooper() != null) {
				BD.getmHandler().removeCallbacksAndMessages(null);
				BD.getmHandler().getLooper().quit();

			}
			BeepDevice.BD = null;
		}
	}

	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	public void enable() {
		beepIO.setGPIO(GlobalConstant.ENABLE_IO);
	}

	public void disenable() {
		beepIO.setGPIO(GlobalConstant.DISABLE_IO);
	}

	/**
	 * �򿪷�����
	 */
	public void openBeep() {
		this.enable();
	}

	/**
	 * �رշ�����
	 */
	public void closeBeep() {
		this.disenable();
	}

	public void beep(int frequency) {
		System.out.println(TAG + "---BeepDevice--->open");
		this.enable();
		try {
			sleep(frequency);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.disenable();
		System.out.println(TAG + "------BeepDevice------>close");
	}

	@Override
	public void run() {
		super.run();
		Looper.prepare();
		myLooper = Looper.myLooper();
		Log.i("ThreadManage", "BeeperThread------------------------->start");
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					beep(50);
					this.removeMessages(0);
					this.removeMessages(1);
					this.removeMessages(2);
					break;
				case 1:
					beep(300);
					this.removeMessages(0);
					this.removeMessages(1);
					this.removeMessages(2);
					break;
				case 2:
					beep(50);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					beep(50);
					this.removeMessages(0);
					this.removeMessages(1);
					this.removeMessages(2);
					break;
				default:
					break;
				}

			}
		};
		Looper.loop();
		Objstate = true;
		Log.i("ThreadManage", "BeeperThread------------------------->end");
	}
}
