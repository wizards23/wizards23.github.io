package com.poka.device;

public class DeviceInfo {

	public static final String RFID_FILE = "/dev/ttyS7";
	//public static final String RFID_FILE = "/dev/ttyS4";
	public static final int RFIDDEVICE_KEY = 115200;
	public static final int FINGER_KEY = 115200;
//	public static final int FINGER_KEY = 9600;
	public static final int SCANDEVICE_KEY = 9600;
	public static final int WIRELESS_KEY = 9600;
	public static final int GPS_KEY = 9600;
	public static final int GPRS_KEY = 115200;
	
//	public static final String SCAN_FILE = "/dev/ttyS3";
//	public static final String FINGER_FILE = "/dev/ttyS2";
//	public static final String WIRELESS_FILE = "/dev/ttyS1";
//	public static final String GPS_FILE = "/dev/ttyS6";
//	public static final String GPRS_FILE = "/dev/ttyS5";
	
	public static final String SCAN_FILE = "/dev/ttyS3";
	public static final String FINGER_FILE = "/dev/ttyS4";
	public static final String WIRELESS_FILE = "/dev/ttyS2";
	public static final String GPS_FILE = "/dev/ttyS5";
	public static final String GPRS_FILE = "/dev/ttyS6";
	
	public static final String RFID_REPEAT = "RFID_REPEAT";
	public static final String RFID_OPEN = "RFID_OPEN";
	public static final String RFID_CLOSE = "RFID_CLOSE";
	public static final String RFID_STOP = "RFID_STOP";
	public static final String RFID_SINGLE = "RFID_SINGLE";
	public static final String RFID_WRITE = "RFID_WRITE";
	public static final String WIRELESS_OPEN = "WIRELESS_OPEN";
	public static final String WIRELESS_CLOSE = "WIRELESS_CLOSE";
	public static final String FINGER_OPEN = "FINGER_OPEN";
	public static final String FINGER_CLOSE = "FINGER_CLOSE";
	public static final String RFID_SET = "RFID_SET";
	public static final String SCAN_OPEN = "SCAN_OPEN";
	public static final String SCAN_CLOSE = "SCAN_CLOSE";
	
	public static final String GPRS_OPEN = "GPRS_OPEN";
	public static final String GPRS_CLOSE = "GPRS_CLOSE";
	public static final String GPS_OPEN = "GPS_OPEN";

}
