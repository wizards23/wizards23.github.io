package a20.cn.uhf.admin;

import hdjc.rfid.operator.RFID_Device;

import com.example.pda.R;

import a20.android_serialport_api.SerialPort;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android_rfid_control.powercontrol;

public class WriteAndRead extends Activity implements OnClickListener{
	private TextView textViewEPC;
	private Spinner spinnerMemBank;//数据区
	private EditText editPassword;//密码
	private EditText editAddr;//起始地址
	private EditText editLength;//读取的数据长度
	private Button	buttonRead;
	private Button	copy;
	private Button buttonWrite;
	private EditText editWriteData;//要写入的数据
	private EditText editReadData;//读取数据展示区
	private Button buttonClear;
	private final String[] strMemBank = {"RESERVE", "EPC", "TID", "USER"};//RESERVE EPC TID USER分别对应0,1,2,3
	private ArrayAdapter<String> adatpterMemBank;
	private Spinner spinnerLockType;//要锁定的数据区
	private Button buttonLock;//锁定按钮
	private EditText editKillPassword;//销毁密码
	private Button buttonKill;//销毁按钮
	private ArrayAdapter<CharSequence> adapterLockType;
	private int membank;//数据区
	private int lockMembank; 
	private int addr = 0;//起始地址
	private int length = 1;//读取数据的长度
	private int lockType;//
	private Button buttonBack ;
	private powercontrol rFidPowerControl;//yang
	private TextView read;
	
	private UhfReader reader ; //中间操作类
	private NewSendCommendManager manager ;
	private SerialPort uhfSerialPort ;//超高频串口
	String epc = "";
	
	private String TAG = "MorehandleActivity";//DEBUG
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.iso1800_6c);
		
		//================================打开RFID电源控制
				rFidPowerControl=new powercontrol();
				rFidPowerControl.openrfidPowerctl("/dev/rfidPowerctl");
				rFidPowerControl.rfidPowerctlSetSleep(0);
				//===================================
		
		epc = getIntent().getStringExtra("epc");
		initView();
		listener();
		textViewEPC.setText(epc);
		/*
		 * 获取reader时，有串口的初始化操作
		 * 若reader为null，则串口初始化失败
		 * 
		 */
		reader = UhfReader.getInstance();
		if(reader == null){
			Toast.makeText(getApplicationContext(), "串口初始化失败", 0).show();
			return;
		}
		//获取指令操作
		manager = reader.getManager();
		
	
	}
	
	private void initView(){
		textViewEPC = (TextView) findViewById(R.id.textViewEPC);
		this.read = (TextView) findViewById(R.id.rfidreadtext);
		this.spinnerMemBank = (Spinner) findViewById(R.id.spinner_membank);
		this.editAddr = (EditText) findViewById(R.id.edittext_addr);
		this.editLength = (EditText) findViewById(R.id.edittext_length);
		this.editPassword = (EditText) findViewById(R.id.editTextPassword);
		this.buttonRead = (Button) findViewById(R.id.button_read);
		this.copy = (Button) findViewById(R.id.rfidcopy);
		this.buttonWrite = (Button) findViewById(R.id.button_write);
		this.buttonClear = (Button) findViewById(R.id.button_readClear);
		this.buttonLock = (Button) findViewById(R.id.button_lock_6c);
		this.buttonKill = (Button) findViewById(R.id.button_kill_6c);
		this.buttonBack = (Button) findViewById(R.id.button_back);
		this.editKillPassword = (EditText) findViewById(R.id.edit_kill_password);
		this.editWriteData = (EditText) findViewById(R.id.edittext_write);
		this.editReadData = (EditText) findViewById(R.id.linearLayout_readData);
		this.adatpterMemBank = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strMemBank);
		this.adatpterMemBank.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.spinnerLockType = (Spinner) findViewById(R.id.spinner_lock_type);
		this.adapterLockType =  ArrayAdapter.createFromResource(this, R.array.arr_lockType, android.R.layout.simple_spinner_item);
		this.adapterLockType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.spinnerLockType.setAdapter(adapterLockType);
		this.spinnerMemBank.setAdapter(adatpterMemBank);
		
		
		setButtonClickable(buttonKill, false);
		setButtonClickable(buttonLock, false);
		
		
		this.copy.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String text = WriteAndRead.this.read.getText().toString();
				if(text!=null && text!=""){
					WriteAndRead.this.editWriteData.setText(text);	
				}
				
				
			}
		});
		
		
		
	}

	//监听
	private void listener(){
		this.buttonClear.setOnClickListener(this);
		this.buttonRead.setOnClickListener(this);
		this.buttonWrite.setOnClickListener(this);
		this.buttonKill.setOnClickListener(this);
		this.buttonLock.setOnClickListener(this);
		this.buttonBack.setOnClickListener(this);
		spinnerMemBank.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				membank = arg2;
				lockMembank = arg2 + 1;
				Log.i(TAG, "memeBank " + membank);
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		spinnerLockType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				lockType = arg2 ;
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	
	//设置按钮是否可用
	private void setButtonClickable(Button button, boolean flag){
		button.setClickable(flag);
		if(flag){
			button.setTextColor(Color.BLACK);
		}else{
			button.setTextColor(Color.GRAY);
		}
	}
	
	
	@Override
	public void onClick(View v) {
		byte[] accessPassword = Tools.HexString2Bytes(editPassword.getText().toString());
		addr = Integer.valueOf(editAddr.getText().toString());
		length = Integer.valueOf(editLength.getText().toString());
		switch (v.getId()) {
		//读标签数据
		case R.id.button_read:
			
			
				if(accessPassword.length != 4){
					Toast.makeText(getApplicationContext(), "密码为4个字节", Toast.LENGTH_SHORT).show();
					return;
				}
				
				//读取数据区数据
				//byte[] data = manager.readFrom6C(membank, addr, length, accessPassword);
				//目前只操作EPC
				byte[] data = manager.readFrom6C(1, 2, 6, accessPassword);
				
				if(data != null && data.length > 1){
					String dataStr =  Tools.Bytes2HexString(data, data.length);
//					Toast.makeText(getApplicationContext(),dataStr, 0).show();
					read.setText(dataStr);				
					editReadData.append("读数据：" + dataStr + "\n");
					break;					
				}else{
//					Toast.makeText(getApplicationContext(), "读数据失败", Toast.LENGTH_SHORT).show();
					if(data != null){
						editReadData.append("读数据失败，错误码：" + (data[0]&0xff) + "\n");
						return;
					}
					editReadData.append("读数据失败，返回为空"  + "\n");
				}
				

			
			
			break;
		//写标签数据
		case R.id.button_write:
			for (int i = 0; i <5; i++) {
				
				if(accessPassword.length != 4){
					Toast.makeText(getApplicationContext(), "密码为4个字节", Toast.LENGTH_SHORT).show();
					return;
				}
				StringBuffer sb = new StringBuffer();
				String editWrite =  editWriteData.getText().toString();
				if(editWrite.length()<24){
					sb.append(editWrite);
					for (int j = 0; j <24-editWrite.length(); j++) {
						sb.append("0");
					}
				}else{
					sb.append(editWrite.substring(0, 24));
				}
				String writeData = sb.toString();
				Log.i("writeData.length()",writeData.length()+"");
				Log.i("writeData",writeData);
				
				if(writeData.length()%4 != 0){
					Toast.makeText(getApplicationContext(), "写入数据的长度以字为单位，1word = 2bytes", Toast.LENGTH_SHORT).show();
				}
				byte[] dataBytes = Tools.HexString2Bytes(writeData);
				//dataLen = dataBytes/2 dataLen是以字为单位的
				//boolean writeFlag = manager.writeTo6C(accessPassword, membank, addr, dataBytes.length/2, dataBytes);
				boolean writeFlag = manager.writeTo6C(accessPassword, 1, 2, dataBytes.length/2, dataBytes);
				if(writeFlag){					
					editReadData.append("写数据成功！"  + "\n");
					break;
				}else{
					editReadData.append("写数据失败！"  + "\n");
					continue;
				}
				
			}
			break;
		//锁定标签
		case R.id.button_lock_6c:
			
			break;
		//销毁标签
		case R.id.button_kill_6c:
			
			break;
		//清空内容
		case R.id.button_readClear:
			editReadData.setText("");
			this.read.setText("");
			this.editWriteData.setText("");

			break;
		//返回
		case R.id.button_back:
			finish();
			break;
		default:
			break;
		}
		
	}

	
}
