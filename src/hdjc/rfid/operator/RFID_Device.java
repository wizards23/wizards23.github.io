package hdjc.rfid.operator;



import a20.cn.uhf.admin.RfidAdmin;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.poka.device.DeviceInfo;
import com.poka.device.DeviceManage;

public class RFID_Device implements IRFID_Device {
	public static final int scanCheck = 0x88;
	public static final int scanMsg = 10;
	private static INotify notifys;
	private DeviceManage device = DeviceManage.getDeviceMObject();
	static String singlecode = null;
	public static final int send_Data = 0x300;
	public static final int fingerIval = 0x224;
	public static final int fingerPraper = 0x222;
	public static final int rdid_a20 = 99;
	private RfidAdmin rfid_a20;
	private boolean opanAndColse = true;
	
	final Handler h = new Handler(){         
        public void handleMessage(Message msg){  
            switch (msg.what) {  
            case scanMsg:
				Bundle bundle = msg.getData();
				String code = bundle.getString("code");
				System.out.println("10是否传递回来值:"+code);
				read(code);
				readScan(code);
				break;
            case 12:
				Bundle singlebundle = msg.getData();
				singlecode = singlebundle.getString("code");
				System.out.println("12是否传递回来值:"+singlecode);
				break;
            case fingerPraper:
				System.out.println("正在获取指纹特征值！");
				fingerPressState();
//				ShareUtil.toastShow("正在获取指纹特征值！", theActivity);
				break;
			case send_Data:
			//	String luru = "MATCH_FINGER";
				String luru = "ENROLL_FINGER";
				byte[] t = luru.getBytes();
				byte[] temp = new byte[76];
				for (int i = 0; i < t.length; ++i)
					temp[i] = t[i];
			//	ShareUtil.isCaiji = false;
				DeviceManage.fingersDevcie.getSerialPort().serialWrite(temp);
				break;
			case 281:
				String close = "QUIT";
				byte[] tt = close.getBytes();
				byte[] tempclose = new byte[76];
				for (int i = 0; i < tt.length; ++i)
					tempclose[i] = tt[i];
			//	ShareUtil.isCaiji = false;
				System.out.println("发送关闭命令");
				DeviceManage.fingersDevcie.getSerialPort().serialWrite(tempclose);
				break;
			case fingerIval:
				
				byte[] ival = (byte[]) msg.obj;
				System.out.println("指纹特征值获取成功！");
				fingerIval();
//				ShareUtil.toastShow("指纹特征值获取成功！", theActivity);
				
				break;
			case rdid_a20:
				Bundle b = msg.getData();
				String number = b.getString("number");
				RFID_Device.this.notify(number);
				break;
            }  
            super.handleMessage(msg);  
        }  
    };  
	//打开RFID扫描
	public void open() {
		Message mg = Message.obtain();
		mg.what = scanCheck;
		mg.obj = DeviceInfo.RFID_REPEAT;
		DeviceManage.getDeviceMObject();
		DeviceManage.setMainHandler(h);
		DeviceManage.getDeviceMObject().getMyHandler().sendMessage(mg);

	}

	//关闭RFID扫描
	public void close() {
		Message mg = Message.obtain();
		mg.what = 0x91;
		mg.obj = DeviceInfo.RFID_CLOSE;
		DeviceManage.getDeviceMObject();
		DeviceManage.setMainHandler(h);
		DeviceManage.getDeviceMObject().getMyHandler()
		.sendMessage(mg);
	}

	//添加通知，欧
	public void addNotifly(INotify notify) {
		RFID_Device.notifys = notify;

	}
	
	
	//循环读取RFID编号
	private void read(String rfidStr){
		System.out.println("读取到的编号:"+rfidStr);
			notify(rfidStr);
	}
	
	/*
	 * 通知外部，调用方得到编号
	 */
	private void notify(String number){				
			notifys.getNumber(number);
		
	}
	//打开单次读取RFID编号
	@Override
	public String singleReadOpen() {
		Message msg = Message.obtain();
		msg.obj = DeviceInfo.RFID_SINGLE;
		DeviceManage.getDeviceMObject();
		DeviceManage.setMainHandler(h);
		DeviceManage.getDeviceMObject().getMyHandler().sendMessage(msg);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		notify(singlecode);
		return singlecode;
	}
	//写数据到RFID芯片
	@Override
	public void writeRFID(String codeing) {
		device.setCode(codeing);
		Message mg = Message.obtain();
		mg.what = 11;
		mg.obj = DeviceInfo.RFID_WRITE;
		if (DeviceManage.getDeviceMObject().getMyHandler() != null) {
			DeviceManage.getDeviceMObject().getMyHandler()
					.sendMessage(mg);
		}
	}
	//打开一维码扫描
	@Override
	public void scanOpen() {
		DeviceManage.setMainHandler(h);
		Message mg = Message.obtain();
		mg.obj = DeviceInfo.SCAN_OPEN;
		DeviceManage.getDeviceMObject().getScanHandler().sendMessage(mg);
	}
	//关闭一维码扫描
	@Override
	public void scanclose() {
		DeviceManage.setMainHandler(h);
		Message mg = Message.obtain();
		mg.obj = DeviceInfo.SCAN_CLOSE;
		DeviceManage.getDeviceMObject().getScanHandler().sendMessage(mg);
	}
	//单次获取一维码扫描结果数据
	@Override
	public String readScan(String scan) {
		System.out.println("扫描回来的一维码:"+scan);
		notify(scan);
		return scan;
	}

	@Override
	public void fingerOpen() {
		Message mg = Message.obtain();
		mg.obj = DeviceInfo.FINGER_OPEN;
		DeviceManage.setLoginAcitvityHandler(h);
		System.out.println(DeviceManage.getDeviceMObject()+"主界面是否为null");
		DeviceManage.getDeviceMObject().getFingerHandler().sendMessage(mg);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		h.sendMessageDelayed(h.obtainMessage(send_Data), 100);
		
	}

	@Override
	public void fingerClose() {
		//不建议关掉指纹
		Message mg = Message.obtain();
		mg.obj = DeviceInfo.FINGER_CLOSE;
		DeviceManage.setLoginAcitvityHandler(h);
		System.out.println(DeviceManage.getDeviceMObject()+"主界面是否为null");
	
		DeviceManage.getDeviceMObject().getFingerHandler().sendMessage(mg);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		h.sendMessageDelayed(h.obtainMessage(send_Data), 100);
		
	}

	@Override
	public void fingerPressState() {
		notify("正在获取指纹特征值！");
	}

	@Override
	public void fingerIval() {
		notify("获取指纹特征值成功！");
	}
	
	
	
	@Override
	public void open_a20() {
		
		if(opanAndColse){
			getRfid_a20().setHandler(h);
			getRfid_a20().openRfid();
			Log.i("open","open");
			opanAndColse =false;
		}
		
	}

	@Override
	public void close_a20() {
		if(!opanAndColse){
			getRfid_a20().closeRfid();
		   Log.i("colse","colse");
		   opanAndColse = true;
		}
				
	}

	@Override
	public void stop_a20() {
		getRfid_a20().stopRfid();
		
	}

	@Override
	public void start_a20() {
		getRfid_a20().startRfid();
		
	}
	
	private RfidAdmin  getRfid_a20(){
		if(rfid_a20==null){
		 rfid_a20 = new RfidAdmin();	
		}
		return rfid_a20;
	}
	
	/*
	 * 特征值存在  ShareUtil.ivalBack;
	 * 采集或者验证的时候直接将此值
	 * 作为参数分割传入即可
	 * 
	 * 或者分为2个128的字节数组传入
	 */
	
}
