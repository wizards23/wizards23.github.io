package android_rfid_control;

public class powercontrol {
	
	private static final String TAG = "powercontrol";
	
	public native int openrfidPowerctl(String path);
	public native void rfidPowerctlClose();
	public native int rfidPowerctlSetSleep(int enable);
 
	static {
		System.loadLibrary("rfidPowerctl");
	}

}
