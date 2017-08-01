package a20.cn.uhf.admin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import a20.cn.poka.entity.DeviceInfo;
import android_serialport_api.SerialPort;

public class UhfReader {
	
	private static   NewSendCommendManager manager ;
	
	private static SerialPort mSerialPort;
	
	private static UhfReader reader ;
	
	private static InputStream mInput;
	
	private static OutputStream mOutput;
	
	private UhfReader(){
//		this.manager = manager;
	}

	/*
	 * ��ȡ������
	 */
	public static UhfReader getInstance(){
		if(reader == null){
			if(mSerialPort == null){
				File rFidFile = new File(DeviceInfo.RFID_FILE);
				try {
					mSerialPort = new SerialPort(rFidFile,
							DeviceInfo.RFIDDEVICE_KEY, 0);
					mInput = mSerialPort.getInputStream();
					mOutput = mSerialPort.getOutputStream();
					manager = NewSendCommendManager.getInstance(mInput, mOutput);
					reader = new UhfReader();
					
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}
		return reader;
	}
	
	
	public NewSendCommendManager getManager() {
		return manager;
	}


	public SerialPort getmSerialPort() {
		return mSerialPort;
	}

	
	public void close(){
		if(manager != null){
			manager.close();
			manager = null;
			
		}
		if(mSerialPort != null){
			mSerialPort.close();
			mSerialPort = null;
			try {
				mInput.close();
				mOutput.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		if(reader != null){
			reader = null;
		}


	}

}
