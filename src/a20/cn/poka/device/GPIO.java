package a20.cn.poka.device;

import android.os.IA10gpioService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

/**
 * �ϵ硢�ϵ�
 */
public class GPIO {
	private IA10gpioService gpioService = null;
	private int ioID;

	public GPIO(int ID) {
		super();
		ioID = ID*10;
		gpioService = IA10gpioService.Stub.asInterface(ServiceManager
				.getService("A10gpio"));
	}
	
	public void setGPIO(int val) {
		try {
			gpioService.setVal(ioID+val);
			System.out.println((ioID+val)+"");
			Log.i("rfidsetvalue-------------->", (ioID+val)+"");
			System.out.println("GPIO"+"--->setGPIO");
		} catch (RemoteException e) {
			System.out.println("remoteException");
		}
	}
	
	public int getGPIO() {
		int i = 1;
		try {
			i = gpioService.getVal()-ioID;
		} catch (RemoteException e) {
			System.out.println("remoteException");
		}
		return i;
	}
}
