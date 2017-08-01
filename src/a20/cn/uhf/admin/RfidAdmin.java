package a20.cn.uhf.admin;

import hdjc.rfid.operator.RFID_Device;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import android.widget.ListView;
import a20.android_serialport_api.SerialPort;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android_rfid_control.powercontrol;


public class RfidAdmin{

	private Context context;
	private boolean runFlag = true;
	private boolean startFlag = false;
	//private boolean connectFlag = false;
	private UhfReader reader ; //�м������
	private NewSendCommendManager manager ;
	private SerialPort uhfSerialPort ;//����Ƶ����
	private static final String TAG = "MainActivityRFID";
	public  Handler hand = null;
	Bundle bundle;
	
	public RfidAdmin (){};
	public RfidAdmin (Context context){
		this.context = context;
	};
	
	private powercontrol rFidPowerControl;//yang
	
	public void setHandler(Handler hand){
		if(this.hand==null){
		 this.hand = hand;	
		}		
	}
	
	public void openRfid(){
		startFlag = true;
		runFlag = true;
		
		rFidPowerControl=new powercontrol();
		rFidPowerControl.openrfidPowerctl("/dev/rfidPowerctl");
		rFidPowerControl.rfidPowerctlSetSleep(0);
		//===================================
		/*
		 * ��ȡreaderʱ���д��ڵĳ�ʼ������
		 * ��readerΪnull���򴮿ڳ�ʼ��ʧ��
		 * 
		 */
		reader = UhfReader.getInstance();
		if(reader == null){
			return;
		}
		//��ȡָ�����
		manager = reader.getManager();
		
		Thread thread = new InventoryThread();
		thread.start();
		Log.i("扫描线程启动","扫描线程启动");
		//��ʼ��������
		//Util.initSoundPool(context);
	}
	public void stopRfid(){
		startFlag = false;
	}
	
	public void startRfid(){
		startFlag = true ;			
	}
	
	public void closeRfid(){
		runFlag = false;
		startFlag = false;
		if(reader != null){
			manager.close();
			//�˳�ʱ�ر�
			reader.close();
		}
		//=========================�ص�RFID
		rFidPowerControl.rfidPowerctlSetSleep(1);
		rFidPowerControl.rfidPowerctlClose();
	}
	
	/**
	 * �̴��߳�
	 * @author Administrator
	 *
	 */
	class InventoryThread extends Thread{
		private List<byte[]> epcList;

		@Override
		public void run() {
			super.run();
			while(runFlag){
				if(startFlag){
				    Log.d(TAG,"==========InventoryThread============");  
//					manager.stopInventoryMulti()
					epcList = manager.inventoryRealTime(); 
					
					if(epcList != null && !epcList.isEmpty()){
						//������ʾ��
						//Util.play(1, 0);
						for(byte[] epc:epcList){
							String epcStr = Tools.Bytes2HexString(epc, epc.length);
							Log.i("A20扫描的编号------->", epcStr);	
							Message m = Message.obtain();
							if(bundle==null){
							bundle = new Bundle();
							}
							m.what = RFID_Device.rdid_a20;
							bundle.putString("number", epcStr);
							m.setData(bundle);
							Log.i("hand",hand+"");
							hand.sendMessage(m);
													
						}
					}
					epcList = null ;
					try {
						Thread.sleep(40);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
	
	
	
	
}
