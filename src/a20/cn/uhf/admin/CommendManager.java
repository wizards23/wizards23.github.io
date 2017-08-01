package a20.cn.uhf.admin;

import java.util.List;

public interface CommendManager {

	/**
	 * ���ò�����  
	 * @return
	 */
	public boolean setBaudrate();
	
	/**
	 * ��ȡӲ���汾
	 */
	public byte[] getFirmware() ;
	
	/**
	 * ���������������
	 * @param value
	 * @return
	 */
	public boolean setOutputPower(int value);
	
	/**
	 * ʵʱ�̴�
	 * @return
	 */
	public List<byte[]> inventoryRealTime();
	
	/**
	 * ѡ����ǩ
	 * @param epc
	 */
	public void selectEPC(byte[] epc);
	
	/**
	 * �����
	 * int memBank�����
	 * int startAddr�������ʼ��ַ������Ϊ��λ��
	 * int lengthҪ��ȡ����ݳ���(����Ϊ��λ)
	 * byte[] accessPassword ��������
	 * ���ص�byte[] Ϊ  EPC + ��ȡ�����
	 */
	public byte[] readFrom6C(int memBank, int startAddr, int length, byte[] accessPassword);
	
    /**
     * д���
     * byte[] password ��������
     * int memBank �����
     * int startAddr ��ʼ��ַ����WORDΪ��λ��
     * int wordCnt д����ݵĳ��ȣ���WORDΪ��λ 1word = 2bytes��
     * byte[] data д�����
     * ���� boolean��trueд�������ȷ��falseд�����ʧ��
     */
	public boolean writeTo6C(byte[] password, int memBank, int startAddr, int dataLen, byte[] data);
	
	/**
	 * ����������
	 * @param value
	 */
	public void setSensitivity(int value);
	
	/**
	 * ���ǩ
	 * @param password ��������
	 * @param memBank  �����
	 * @param lockType ������
	 * @return
	 */
	public boolean lock6C(byte[] password, int memBank, int lockType);
	
	
	/**
	 * 
	 */
	public void close();
	
	/**
	 * ����У���
	 * @param data
	 * @return
	 */
	public byte checkSum(byte[] data);
	
	
	/**
	 * 
	 * @param startFrequency ��ʼƵ��
	 * @param freqSpace Ƶ����
	 * @param freqQuality Ƶ������
	 * @return
	 */
	public int setFrequency(int startFrequency, int freqSpace, int freqQuality);
	
	
	
}
