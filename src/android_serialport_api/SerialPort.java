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

package android_serialport_api;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

//import android.util.Log;

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

		System.out.println("111111111111111111111111");
		mFd = open(device.getAbsolutePath(), baudrate, flags);
		if (mFd == null) {
			// Log.e(TAG, "native open returns null");
			System.out.println("22222222222222222222222");
			throw new IOException();
		}
		System.out.println("3333333333333333333333333");
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

	// JNI

	public native void close();
	
	private native static FileDescriptor open(String path, int baudrate,
			int flags);


	// public native void write(String something);
	public native byte[] wirelessRead();

	public native int wirelessWrite(byte[] temp);

	public native byte[] read();
	
	public native byte[] r500read();

	public native String cashread();

	public native byte[] newcashread();

	// private native int open(String path,int baudrate, int flags);
	// private native Boolean close();
	public native String repeatRead();

	public native String singleRead();

	public native int write(String codeString);

	public native int setParameter(String name, int value);

	public Boolean setParameters(HashMap<String, Integer> para) {
		if (1 == setParameter("power", para.get("power"))) {
			return true;
		}
		return false;
	}

	public native int setDefaultParameters();

	public native String getRFIDState();
	//
    public native int gettemp();
	// 1D 2D SCAN
	public native String scanRead();

	// public native int isPressButton();

	// Finger
	public native byte[] fingerRead(byte dataType);
	public static final byte IMG0 = 2;
	public static final byte IVAL = 1;

	public native byte[] fingerReadIval();
	
	public static native short CRC16(byte[] array, int count);

	public native int isFingerPressed();
	
	public static native int fingerVerify(byte[] src, byte[] dst);
	public native String fingerVerify2(byte[] src);
	public static  native int getpower();
//	public static native void shutdown();
	
	public native int serialRead(byte[] bytes, int len);
	
	public native int serialWrite(byte[] temp);//д����������temp.Length���ֽ�
	
	
	static {
		System.loadLibrary("poka_serial_port");
	}
}
