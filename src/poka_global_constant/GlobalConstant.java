package poka_global_constant;

import android.content.Context;
import android.widget.Toast;

//IO:1-6为输出，7-8为输入
public final class GlobalConstant {
	
	public static final int IO_BEEP = 1;
	//无线
	public static final int IO_WIRELESS_POWER = 2;
	public static final int IO_RFID_POWER = 3;
	//一二维码
	public static final int IO_SCAN_POWER = 4;
	public static final int IO_8V4To4V2 = 5;
	//指纹
	public static final int IO_AS602_POWER= 6;
	public static final int IO_12V_POWER = 7;
	//一二维码
	public static final int IO_SCAN_ENABLE = 8;
	public static final int IO_RFID_ENABLE = 9;
	
	public static final int IO_GPS_POWER = 11;
	
	
	public static final int ENABLE_IO = 1;
	public static final int DISABLE_IO = 0;
	
	public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i=begin; i<begin+count; i++) bs[i-begin] = src[i];
        return bs;
    }
	
	public static void ShowMessage(Context context,String sMsg) {
		Toast.makeText(context, sMsg, Toast.LENGTH_SHORT).show();
	}
}
