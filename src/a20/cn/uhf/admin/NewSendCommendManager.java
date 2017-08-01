package a20.cn.uhf.admin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
//import com.android.hdhe.uhf.util.Tools;

public class NewSendCommendManager implements CommendManager {
	
	private InputStream in;
	private OutputStream out;
	
	private final byte HEAD = (byte) 0xBB;
	private final byte END = (byte) 0x7E;
	
	public static final byte RESPONSE_OK = 0x00; //��Ӧ֡0K
	/**
	 * ����ʧ�ܣ��������
	 */
	public static final byte ERROR_CODE_ACCESS_FAIL = 0x16;
	/**
	 * ���������޿�Ƭ��EPC�������
	 */
	public static final byte ERROR_CODE_NO_CARD = 0x09;
	/**
	 * �����ʱ����ʼƫ��������ݳ��ȳ�����ݴ洢��
	 */
	public static final byte ERROR_CODE_READ_SA_OR_LEN_ERROR = (byte)0xA3;
	/**
	 * д���ʱ����ʼƫ��������ݳ��ȳ�����ݴ洢��
	 */
	public static final byte ERROR_CODE_WRITE_SA_OR_LEN_ERROR = (byte)0xB3;
	/**
	 * �����ȸ�
	 */
	public static final int SENSITIVE_HIHG = 3;
	/**
	 * ��������
	 */
	public static final int SENSITIVE_MIDDLE = 2;
	/**
	 * �����ȵ�
	 */
	public static final int SENSITIVE_LOW = 1;
	/**
	 * �����ȼ���
	 */
	public static final int SENSITIVE_VERY_LOW = 0;
	
	private static NewSendCommendManager manager ;
	
	private byte[] selectEPC = null;
	
	private  NewSendCommendManager(InputStream serialPortInput, OutputStream serialportOutput){
		in = serialPortInput;
		out = serialportOutput;
	}
	
	public static NewSendCommendManager getInstance(InputStream in , OutputStream output){
		if(manager == null){
			manager = new NewSendCommendManager(in, output);
		}
		return manager;
	}
	/**
	 * ����ָ��
	 * @param cmd
	 */
	private void sendCMD(byte[] cmd){
		try {
			out.write(cmd);
			out.flush();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean setBaudrate() {
		byte[] cmd = {};
		return false;
	}

	@Override
	public byte[] getFirmware() {
		byte[] cmd = {(byte)0xBB ,(byte)0x00,(byte) 0x03 ,(byte)0x00 ,(byte)0x01 ,(byte)0x00 ,(byte)0x04 ,(byte)0x7E };
		byte[] version = null;
		sendCMD(cmd);
		byte[] response = this.read();
		if(response != null){
			byte[] resolve = handlerResponse(response);
			if(resolve != null &&resolve.length > 1){
				version = new byte[resolve.length - 1];
				System.arraycopy(resolve, 1, version, 0, resolve.length - 1);
			}
		}
		return version;
	}

	
	/**
	 * ���ö�д���������ȣ�
	 * @param value
	 */
	public void setSensitivity(int value){
		byte[] cmd = {(byte)0xBB ,(byte)0x00 ,(byte)0xF0 ,(byte)0x00 ,(byte)0x04 ,(byte)0x02 ,
				(byte)0x06 ,(byte)0x00 ,(byte)0xA0 ,(byte)0x9C ,(byte)0x7E };
		cmd[5] = (byte)value;
		cmd[cmd.length - 2] = checkSum(cmd);
		sendCMD(cmd);
		
		byte[] response = this.read();
		if(response != null){
			Log.e("setSensitivity ", Tools.Bytes2HexString(response, response.length));
		}
	}
	
	/**
	 * ��ȡ��Ӧ֡
	 * @return
	 */
	private byte[] read(){
		byte[] responseData = null;
		byte[] response = null;
		int available = 0 ;
		int index = 0;
		int headIndex = 0;
		//500ms����ѯ�����������ݷ���
		try {
		while(index < 10){
			Thread.sleep(50);
			available = in.available();
			//�����������
			if(available > 7) break;
			index++;
		}
		if(available > 0){
			responseData = new byte[available];
			in.read(responseData);
			for(int i = 0; i < available; i++){
				if(responseData[i] == HEAD){
					headIndex = i;
					break;
				}
			}
			response = new byte[available - headIndex];
			System.arraycopy(responseData, headIndex, response, 0, response.length);
			
		}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return response;
	}
	
	/**
	 * �����������
	 */
	@Override
	public boolean setOutputPower(int value) {
		byte[] cmd = {(byte)0xBB ,(byte)0x00 ,(byte)0xB6 ,(byte)0x00 ,(byte)0x02 ,(byte)0x0A ,(byte)0x28 
				,(byte)0xEA ,(byte)0x7E };
		cmd[5] = (byte)((0xff00 & value)>>8);
		cmd[6] = (byte)(0xff & value);
		cmd[cmd.length - 2] = checkSum(cmd);
		Log.e("", Tools.Bytes2HexString(cmd, cmd.length));
		sendCMD(cmd);
		byte[] recv = read();
		if(recv != null){
			Log.e("", Tools.Bytes2HexString(recv, recv.length));
			return true;
		}
		return false;
	}
	
	/**
	 * ���ǩ�̴�
	 * @return
	 */
	public List<byte[]> inventoryMulti(){
		List<byte[]> list = new ArrayList<byte[]>();
		byte[] cmd = {(byte)0xBB ,(byte)0x00 ,(byte)0x27 ,(byte)0x00 ,
				(byte)0x03 ,(byte)0x22 ,(byte)0x27 ,(byte)0x10 ,(byte)0x83 ,(byte)0x7E};
		sendCMD(cmd);
		byte[] response = this.read();
		if(response != null){
			int responseLength = response.length;
//			Log.e("", Tools.Bytes2HexString(response, response.length));
			int start = 0;
			//���ſ����ص���� 
//			byte[] sigleCard = new byte[24];
			//���ſ�Ƭ�����Զ���֡ �ĸ�ʽ����
			if(responseLength > 15){
//				Log.e("���ſ�", Tools.Bytes2HexString(response, response.length));
				//Ҫȡ����ݳ���
				while(responseLength > 5){
//					Log.e("���ſ�", Tools.Bytes2HexString(response, response.length));
					//��ȡ�����һ��ָ��
					int paraLen = response[start + 4]&0xff;
					int singleCardLen = paraLen + 7;
					//ָ��������
					if(singleCardLen > responseLength) break;
					byte[] sigleCard = new byte[singleCardLen];
					System.arraycopy(response, start, sigleCard, 0, singleCardLen);
					
					byte[] resolve = handlerResponse(sigleCard);
//					Log.e("���ſ�", Tools.Bytes2HexString(resolve, resolve.length));
					//��������ݵ�һλ��ָ����룬��2λRSSI����3-4λ��PC����5λ�������EPC
					if(resolve != null && paraLen > 5){
						byte[] epcBytes = new byte[paraLen - 5];
						System.arraycopy(resolve, 4, epcBytes, 0, paraLen - 5);
//						Log.e("����EPC", Tools.Bytes2HexString(epcBytes, epcBytes.length));
						list.add(epcBytes);
					}
					start+=singleCardLen;
					responseLength-=singleCardLen;
				}
			}else{
				handlerResponse(response);
			}
		}
		return list;
	}

	/**
	 * ֹͣ���ǩ�̴�
	 */
	public void stopInventoryMulti(){
		byte[] cmd = {(byte)0xBB ,(byte)0x00 ,(byte)0x28 ,(byte)0x00 ,
				(byte)0x00 ,(byte)0x28 ,(byte)0x7E};
		sendCMD(cmd);
		byte[] recv = read();
		
	}
	
	/**
	 * ʵʱ�̴�
	 */
	@Override
	public List<byte[]> inventoryRealTime() {
		byte[] cmd = {(byte)0xBB ,(byte)0x00 ,(byte)0x22 ,(byte)0x00 ,
				(byte)0x00 ,(byte)0x22 ,(byte)0x7E};
		sendCMD(cmd);
		List<byte[]> list = new ArrayList<byte[]>();
		byte[] response = this.read();
		if(response != null){
			int responseLength = response.length;
//			Log.e("", Tools.Bytes2HexString(response, response.length));
			int start = 0;
			//���ſ����ص���� 
//			byte[] sigleCard = new byte[24];
			//���ſ�Ƭ�����Զ���֡ �ĸ�ʽ����
			if(responseLength > 15){
//				Log.e("���ſ�", Tools.Bytes2HexString(response, response.length));
				//Ҫȡ����ݳ���
				while(responseLength > 5){
//					Log.e("���ſ�", Tools.Bytes2HexString(response, response.length));
					//��ȡ�����һ��ָ��
					int paraLen = response[start + 4]&0xff;
					int singleCardLen = paraLen + 7;
					//ָ��������
					if(singleCardLen > responseLength) break;
					byte[] sigleCard = new byte[singleCardLen];
					System.arraycopy(response, start, sigleCard, 0, singleCardLen);
					
					byte[] resolve = handlerResponse(sigleCard);
//					Log.e("���ſ�", Tools.Bytes2HexString(resolve, resolve.length));
					//��������ݵ�һλ��ָ����룬��2λRSSI����3-4λ��PC����5λ�������EPC
					if(resolve != null && paraLen > 5){
						byte[] epcBytes = new byte[paraLen - 5];
						System.arraycopy(resolve, 4, epcBytes, 0, paraLen - 5);
//						Log.e("����EPC", Tools.Bytes2HexString(epcBytes, epcBytes.length));
						list.add(epcBytes);
					}
					start+=singleCardLen;
					responseLength-=singleCardLen;
				}
			}else{
				handlerResponse(response);
			}
		}
		return list;
	}

	//BB 00 0C 00 13 01 00 00 00 20 60 00 30 75 1F EB 70 5C 59 04 E3 D5 0D 70 AD 7E
	@Override
	public void selectEPC(byte[] epc) {
		byte[] cmd = {(byte)0xBB ,(byte)0x00 ,(byte)0x12 ,(byte)0x00,
				(byte)0x01 ,(byte)0x00 ,(byte)0x13 ,(byte)0x7E };
		this.selectEPC = epc;
		sendCMD(cmd);
		byte[] response = this.read();
		if(response != null){
//			Log.e("select epc", Tools.Bytes2HexString(response, response.length));
		}

	}
	
//	public int dese
	
	/**
	 * ����select�Ĳ����ڶԿ����в���֮ǰ����
	 */
	private void setSelectPara(){
		byte[] cmd = {(byte)0xBB ,(byte)0x00,(byte)0x0C ,(byte)0x00 ,(byte)0x13 ,
				(byte)0x01 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x20 ,(byte)0x60 ,
				(byte)0x00 ,(byte)0x01 ,(byte)0x61 ,(byte)0x05 ,(byte)0xB8 ,(byte)0x03 ,
				(byte)0x48 ,(byte)0x0C ,(byte)0xD0 ,(byte)0x00 ,(byte)0x03 ,(byte)0xD1 ,
				(byte)0x9E ,(byte)0x58 ,(byte)0x7E};
		if(this.selectEPC != null ){
			Log.e("", "select epc");
			System.arraycopy(selectEPC, 0, cmd, 12, selectEPC.length);
			cmd[cmd.length - 2] = checkSum(cmd);
//			Log.e("setSelectPara", Tools.Bytes2HexString(cmd, cmd.length));
			sendCMD(cmd);
			byte[] response = this.read();
			if(response != null){}
//			Log.e("setSelectPara response", Tools.Bytes2HexString(response, response.length));
		}
	}

	@Override
	public byte[] readFrom6C(int memBank, int startAddr, int length, byte[] accessPassword) {
		//���ж�д����ǰ��ѡ������Ŀ�
//		this.setSelectPara();
		byte[] cmd = {(byte)0xBB ,(byte)0x00 ,(byte)0x39 ,(byte)0x00 ,(
				byte)0x09 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,
				(byte)0x00 ,(byte)0x03 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,
				(byte)0x08 ,(byte)0x4D ,(byte)0x7E };
		byte[] data = null;
		if(accessPassword == null || accessPassword.length != 4){
			return null;
		}
		
		System.arraycopy(accessPassword, 0, cmd, 5, 4);
		cmd[9] = (byte) memBank;
		if(startAddr <= 255){
			cmd[10] = 0x00;
			cmd[11] = (byte)startAddr;
		}else{
			int addrH = startAddr/256;
			int addrL = startAddr%256;
			cmd[10] = (byte)addrH;
			cmd[11] = (byte)addrL;
		}
		if(length <= 255){
			cmd[12] = 0x00;
			cmd[13] = (byte)length;
		}else{
			int lengH = length/256;
			int lengL = length%256;
			cmd[12] = (byte)lengH;
			cmd[13] = (byte)lengL;
		}
		cmd[14] = checkSum(cmd);
		sendCMD(cmd);
		byte[] response = this.read();
		if(response != null){
			Log.e("readFrom6c response", Tools.Bytes2HexString(response, response.length));
			byte[] resolve = handlerResponse(response);
			
			if(resolve != null){
				Log.e("readFrom6c resolve", Tools.Bytes2HexString(resolve, resolve.length));
				//����Ӧ֡BB 01 39 00 1F 0E 30 00 01 61 05 B8 03 48 0C D0 00 03 D1 9E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 4F 7E 
				if(resolve[0] == (byte)0x39){
					int lengData = resolve.length - resolve[1] - 2;
					data = new byte[lengData];
					System.arraycopy(resolve, resolve[1] + 2, data, 0, lengData);
//					Log.e("readFrom6c", Tools.Bytes2HexString(data, data.length));
				}else{
					//����֡�����ش������ 
					data = new byte[1];
					data[0] = resolve[1];
//					Log.e("readFrom6c", Tools.Bytes2HexString(data, data.length));
				}
			}
		}
		return data;
	}

	@Override
	public boolean writeTo6C(byte[] password, int memBank, int startAddr,
			int dataLen, byte[] data) {
		
		
		//���ж�д����ǰ��ѡ������Ŀ�
				this.setSelectPara();
		if(password == null || password.length != 4){
			return false;
		}
		boolean writeFlag = false;
		int cmdLen = 16 + data.length;
		int parameterLen = 9 + data.length;
		byte[] cmd = new byte[cmdLen];
		cmd[0] = (byte) 0xBB;
		cmd[1] = 0x00;
		cmd[2] = 0x49;
		if(parameterLen < 256){
			cmd[3] = 0x00;
			cmd[4] = (byte)parameterLen;
		}else{
			int paraH = parameterLen/256;
			int paraL = parameterLen%256;
			cmd[3] = (byte)paraH;
			cmd[4] = (byte)paraL;
		}
		System.arraycopy(password, 0, cmd, 5, 4);
		cmd[9] = (byte)memBank;
		if(startAddr < 256){
			cmd[10] = 0x00;
			cmd[11] = (byte) startAddr;
		}else{
			int startH = startAddr/256;
			int startL = startAddr%256;
			cmd[10] = (byte) startH;
			cmd[11] = (byte) startL;
		}
		if(dataLen < 256){
			cmd[12] = 0x00;
			cmd[13] = (byte) dataLen;		
		}else{
			int dataLenH = dataLen/256;
			int dataLenL = dataLen%256;
			cmd[12] = (byte)dataLenH;
			cmd[13] = (byte)dataLenL;
		}
		System.arraycopy(data, 0, cmd, 14, data.length);
		cmd[cmdLen -2] = checkSum(cmd);
		cmd[cmdLen - 1] = (byte)0x7E;
//		Log.e("write data", Tools.Bytes2HexString(cmd, cmdLen));
		sendCMD(cmd);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] response = this.read();
		if(response != null) {
//			Log.e("write data response", Tools.Bytes2HexString(response, response.length));
			byte[] resolve = this.handlerResponse(response);
			if(resolve != null){
//				Log.e("write data resolve", Tools.Bytes2HexString(resolve, resolve.length));
				if(resolve[0] == 0x49 && resolve[resolve.length - 1] == RESPONSE_OK){
					writeFlag = true;
				}
			}
		}
		
		return writeFlag;
	}

	@Override
	public boolean lock6C(byte[] password, int memBank, int lockType) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void close(){
		try {
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		manager = null;
		
	}
	@Override
	public byte checkSum(byte[] data) {
		byte crc = 0x00;
		//��ָ�������ۼӵ��������һλ
		for(int i = 1; i < data.length - 2; i++){
			crc+=data[i];
		}
		return crc;
	}

	/**
	 * ������Ӧ֡
	 * @param response
	 * @return
	 */
	private byte[] handlerResponse(byte[] response){
		byte[] data = null;
		byte crc = 0x00;
		int responseLength = response.length;
		if(response[0] != HEAD) {
			Log.e("handlerResponse", "head error");
			return data;
		}
		if(response[responseLength - 1] != END){
			Log.e("handlerResponse", "end error");
			return data;
		}
		if(responseLength < 7) return data;
		//ת���޷��int
		int lengthHigh = response[3]&0xff;
		int lengthLow = response[4]&0xff;
		int dataLength = lengthHigh*256 + lengthLow;
		//����CRC
		crc = checkSum(response);
		if(crc != response[responseLength - 2]){
			Log.e("handlerResponse", "crc error");
			return data;
		}
		if(dataLength != 0 && responseLength == dataLength + 7){
			Log.e("handlerResponse", "response right");
			data = new byte[dataLength + 1];
			data[0] = response[2];
			System.arraycopy(response, 5, data, 1, dataLength);
		}
		return data;
	}
	
	@Override
	public int setFrequency(int startFrequency, int freqSpace, int freqQuality) {
		int frequency = 1;//Ϊ921.125MƵ��
		
		if(startFrequency > 840125 && startFrequency < 844875){//�й�1
			frequency = (startFrequency - 840125)/250;
		}else if(startFrequency > 920125 && startFrequency < 924875){//�й�2
			frequency = (startFrequency - 920125)/250;
		}else if(startFrequency > 865100 && startFrequency < 867900){//ŷ��
			frequency = (startFrequency - 865100)/200;
		}else if(startFrequency > 902250 && startFrequency < 927750){//����
			frequency = (startFrequency - 902250)/500;
		}
		byte[] cmd = {(byte)0xBB, (byte)0x00, (byte)0xAB, (byte)0x00, (byte)0x01, (byte)0x04, (byte)0xB0, (byte)0x7E};
		cmd[5] = (byte)frequency;
		cmd[6] = checkSum(cmd);
		sendCMD(cmd);
		byte[] recv = read();
		if(recv != null){
			Log.e("frequency",Tools.Bytes2HexString(recv, recv.length));
		}
		return 0;
	}
	
	public int setWorkArea(int area){
		//BB 00 07 00 01 01 09 7E 
		byte[] cmd = {(byte)0xBB ,(byte)0x00 ,(byte)0x07 ,(byte)0x00 ,(byte)0x01 ,(byte)0x01 ,(byte)0x09 ,(byte)0x7E };
		cmd[5] = (byte) area;
		cmd[6] = checkSum(cmd);
		sendCMD(cmd);
		byte[] recv = read();
		if(recv != null){
			Log.e("setWorkArea",Tools.Bytes2HexString(recv, recv.length));
			handlerResponse(recv);
			
		}
		return 0;
	}
	
	public int getFrequency(){
		byte[] cmd = {(byte)0xBB ,(byte)0x00 ,(byte)0xAA ,(byte)0x00 ,(byte)0x00 ,(byte)0xAA ,(byte)0x7E };
		sendCMD(cmd);
		byte[] recv = read();
		if(recv != null){
			Log.e("getFrequency",Tools.Bytes2HexString(recv, recv.length));
			handlerResponse(recv);
		}
		return 0;
	}
}
