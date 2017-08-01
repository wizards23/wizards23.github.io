package a20.cn.poka.device;

import java.io.File;
import java.io.IOException;

import poka_global_constant.GlobalConstant;
import android.os.Message;
import android.util.Log;
import a20.android_serialport_api.SerialPort;
import a20.cn.poka.entity.DeviceInfo;

/**
 * RFID�豸��
 */
public class RFIDDevice {
	
	//RFID���ڽӿ�
	private SerialPort rfidSerialPort;
	private GPIO powerIO;
	private GPIO enableIO;
	//������
	private BeepDevice beeper;
	private static final String tag = "RFIDDevice";
	
	public RFIDDevice(){
		initRFID();
		powerIO = new GPIO(GlobalConstant.IO_RFID_POWER);
		enableIO = new GPIO(GlobalConstant.IO_RFID_ENABLE);
	}

    /**
     * ��RFID
     * @return
     */
	public void openRFID(){
		if(rfidSerialPort == null){
			initRFID();
		}
		
		if (beeper == null) {
			beeper = BeepDevice.getBeepDeviceObj();
		}
		Log.i("open", "open");
		enableIO.setGPIO(GlobalConstant.ENABLE_IO);
		Log.i("open-----------rfid--enableIO", GlobalConstant.ENABLE_IO+"");
		powerIO.setGPIO(GlobalConstant.ENABLE_IO);
		Log.i("open-----------rfid--powerIO", GlobalConstant.ENABLE_IO+"");
		beep();
	}
	
	/**
	 * rfidͨѶ�ӿ�
	 * @param message Э�鱨��
	 * @param len Ӧ���ĵĳ���
	 * @return ����Ӧ�����ַ�
	 */
	public String rfidSerial(String message,int len){
		beep();
		String result = rfidSerialPort.r500write(message, len);
		
		return result;				
	}
	
	/**
	 * �ر�RFID
	 * @return
	 */
	public void closeRFID(){
		if (beeper != null) {
			beeper.getmHandler().removeCallbacksAndMessages(null);
		}
		if (powerIO != null) {
			powerIO.setGPIO(GlobalConstant.DISABLE_IO);
		}
	}
	
	/**
	 * ��ʼ��RFID�豸
	 */
	private void initRFID(){
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
			}
		}
	}
	
	/**
	 * ��������һ��
	 */
	private void beep(){
		if (this.getBeeper() != null) {
			Message mg = Message.obtain();
			mg.what = 1;
			if (this.getBeeper().getMyLooper() != null) {
				if (!this.getBeeper().Objstate) {
					this.getBeeper().getmHandler().sendMessage(mg);
				}
			}
		}
	}
	
	//----------------------------------------------------------------------------------
	
	public SerialPort getRfidSerialPort() {
		return rfidSerialPort;
	}

	public void setRfidSerialPort(SerialPort rfidSerialPort) {
		this.rfidSerialPort = rfidSerialPort;
	}
	
	public BeepDevice getBeeper() {
		return beeper;
	}

	public void setBeeper(BeepDevice beeper) {
		this.beeper = beeper;
	}
    
}
