package com.poka.device;

import android.os.IA10gpioService;
import android.os.RemoteException;
import android.os.ServiceManager;

public class GPIO {
	private IA10gpioService gpioService = null;
	private int ioID;

	
	public GPIO(int ID) {
		super();
		// TODO Auto-generated constructor stub
		ioID = ID*10;
		gpioService = IA10gpioService.Stub.asInterface(ServiceManager
				.getService("A10gpio"));
	}

	public void setGPIO(int val) {
		try {
			gpioService.setVal(ioID+val);
			System.out.println((ioID+val)+"");
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
