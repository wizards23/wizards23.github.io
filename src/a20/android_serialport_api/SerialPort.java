/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package a20.android_serialport_api;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * RFID����ͨѶjni�ӿ�
 */
public class SerialPort {

	private static final String TAG = "SerialPort";
	/*
	 * Do not remove or rename the field mFd: it is used by native method
	 * close();
	 */
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	public SerialPort(File device, int baudrate, int flags)
			throws SecurityException, IOException {

		/* Check access permission */
		if (!device.canRead() || !device.canWrite()) {
			try {
				/* Missing read/write permission, trying to chmod the file */
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 777 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}

		mFd = open(device.getAbsolutePath(), baudrate, flags);
		if (mFd == null) {
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	
    /**
     * �ر�RFID
     */
	public native void close();
	
	/**
	 * RFID通讯接口
	 * @param message 协议报文
	 * @param len 应答报文的长度
	 * @return  返回应答报文字符串
	 * @author digl
	 */
	public native String r500write(String message,int len);

	private native static FileDescriptor open(String path, int baudrate,
			int flags);

	public native int setParameter(String name, int value);

	public Boolean setParameters(HashMap<String, Integer> para) {
		if (1 == setParameter("power", para.get("power"))) {
			return true;
		}
		return false;
	}

	public native int setDefaultParameters();

    public native int gettemp();
	
	public static final byte IMG0 = 2;
	public static final byte IVAL = 1;

	public static  native int getpower();
	
	static {
		System.loadLibrary("poka_serial_port_a20");
	}
}
