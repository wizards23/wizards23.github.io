package com.poka.device;



import com.example.pda.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



public class ShareUtil {
	public static String WdId="5";//验证指纹失败的次数
	public static String zhiwenid_left;
	public static String zhiwenid_right;
	public static boolean isServiceAdd = true;
	public static Context context;
	public static int SEQ = 0;// 无线协议数据包seq
	public static boolean isRespon = false;// 判断服务器响应数据seq是否正确
	public static boolean toSendAcitivty = false;// 无线管理loop是否发消息给主界面
	public static boolean toRepeatrun = false;// 是否调用rfid的repeat命令
	public static byte[] ivalBack;    //指纹特征值
	public static byte[] fingerImg;
	public static boolean isCaiji = true;
	public static byte[] DepartMentIng;//全局机构编号
	public static byte[] User;//全局用户名
	public static byte[] ivalOne;
	public static byte[] ivalTwo;
	public static int recvstatus;
	public static int collectatus;
	public static String loginUser;

	
	
	public static Bitmap finger_bitmap_left;  //左边指纹
	public static Bitmap finger_bitmap_right;   //右边指纹
	public static Bitmap w_finger_bitmap_left;  //交接 左边指纹
	public static Bitmap w_finger_bitmap_right;   //交接 右边指纹
	public static Bitmap finger_kuguandenglu_left;//三期 库管登陆左指纹
	public static Bitmap finger_kuguandenglu_right;//三期 库管登陆右指纹
	public static Bitmap finger_qingfen_denglu_left;//三期 交接清分员左指纹
	public static Bitmap finger_qingfen_denglu_right;//三期 交接清分员右指纹
	public static Bitmap finger_jiaojie_qingfen_left;//三期 交接清分员左指纹
	public static Bitmap finger_jiaojie_qingfen_right;//三期 交接清分员右指纹
	public static Bitmap finger_wangdian_left;//三期 网点人员登陆左指纹
	public static Bitmap finger_wangdian_right;//三期 网点人员登陆右指纹
	public static Bitmap finger_gather;   //指纹采集
	public static int three=3;//验证指纹失败的次数
	
	
	public static int authType = 0;
	
	public static int battery = 0;
	
	public static short ByteToShort(byte[] b) {
		return (short) ((short) (b[0] & 0x00ff) + (short) ((b[1] & 0x00ff) << 8));
	}
	
	public static String byteToRMB(byte v) {
		byte top4 = (byte) ((v & 0xf0) >> 4);
		byte bottom4 = (byte) (v & 0x0f);
		String num = null;
		String unit = null;

		try {
			switch (bottom4) {
			case 1:
				num = "壹";
				break;
			case 2:
				num = "贰";
				break;
			case 5:
				num = "伍";
				break;
			}
			switch (top4) {
			case 0:
				unit = "分";
				break;
			case 1:
				unit = "角";
				break;
			case 2:
				unit = "元";
				break;
			case 3:
				unit = "拾元";
				break;
			case 4:
				unit = "百元";
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (top4 == 3 && bottom4 == 1) {
			return unit;
		}
		System.out
				.println("ShareUtil    ******************* RMB     bottom4 = "
						+ bottom4 + "    top4 = " + top4);
		return num + unit;
	}
	public static byte[] arraycopy(byte[] start, int s, byte[] addr, int a,
			int length) {
		System.arraycopy(start, s, addr, a, length);
		return addr;
	}
	public static byte[] getADDR() {

		if (context == null) {
			System.out.println("ShareUtil context== null !");
		}
//		SharedPreferences settings = context.getSharedPreferences(
//				UserSetting.sharePref, Context.MODE_PRIVATE);
//		System.out.println("工具类输出"+settings);
//		UserSetting sets = new UserSetting(settings);
//		short targetValue = isServiceAdd ? (short) sets.getServiceAdds()
//				: (short) sets.getBagNetSite();
		short sourceValue = (short) 12;
		byte[] addr = new byte[4];
		byte[] target = shortToByte((short)13);
		System.arraycopy(target, 0, addr, 2, 2);
		byte[] source = shortToByte(sourceValue);
		System.arraycopy(source, 0, addr, 0, 2);

		return addr;
	}
	public static byte[] shortToByte(short a) {
		byte[] b = new byte[2];
		b[0] = (byte) (a & 0x00ff);
		b[1] = (byte) ((a & 0xff00) >> 8);
		return b;
	}
//	public static void sendDataMsg(ServiceDataInfo info) {
//		ConsoleAgency concoleAgency = ConsoleAgency.getConsoleAgencyOBJ();
//		Message msg = Message.obtain();
//		msg.obj = info;
//		System.out
//				.println("ShareUtil        concoleAgency.getMainHandler() == null"
//						+ (concoleAgency.getMainHandler() == null));
//		if (concoleAgency.getMainHandler() != null) {
//			System.out
//					.println("ShareUtil   SendDataMsg!   concoleAgency.getmHander?=null"
//							+ concoleAgency.getmHandler() == null);
//			System.out.println("ShareUtil  mHandler = "
//					+ concoleAgency.getmHandler().toString());
//			concoleAgency.getmHandler().sendMessage(msg);
//		}
//	}

	public static void toastShow(String string, Context context) {
		Toast toast = new Toast(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.toast, null);
		TextView text = (TextView) view.findViewById(R.id.toast);
		text.setText(string);
		toast.setView(view);
		toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();
	}
	public static String append(String source,int len){
		if (source.length() < len){ 
			int num = len -source.length();
			for(int i=0;i<num;i++){ 
				source = source  + " ";
			}
		  }   
		return source;
	}
//	public static String heOne(String codeString){
//		
//		int what = 0;
//		what = Integer.parseInt(codeString.substring(0, 2));
//		char c = (char)what;
//		codeString =c+codeString.substring(1);
//		System.out.println("钞箱拼接后的RFID标签"+c+":"+codeString.substring(2));
//		
//		return c+""+codeString.substring(2);
//	}
public static String heOne(String codeString){
		
	int what = 0;
	int whatOne = 0;
	what = Integer.parseInt(codeString.substring(0, 2));
	whatOne = Integer.parseInt(codeString.substring(2, 4));
	char c = (char)what;
	char c1 = (char)whatOne;
	codeString =c+c1+codeString.substring(3);
	
	System.out.println("钞箱拼接后的RFID标签"+c+":"+c1+":"+codeString.substring(4));
	System.out.println(codeString+"*******************");
	return c+""+c1+""+codeString.substring(4);
	}
	public static String heTwo(String codeString){
		
		int what = 0;
		int whatOne = 0;
		what = Integer.parseInt(codeString.substring(0, 2));
		whatOne = Integer.parseInt(codeString.substring(2, 4));
		char c = (char)what;
		char c1 = (char)whatOne;
		codeString =c+c1+codeString.substring(3);
		
		System.out.println("ATM拼接后的RFID标签"+c+":"+c1+":"+codeString.substring(4));
		System.out.println(codeString+"*******************");
		return c+""+c1+""+codeString.substring(4);
		
		
	}
}