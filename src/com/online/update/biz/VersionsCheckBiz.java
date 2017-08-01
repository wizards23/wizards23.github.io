package com.online.update.biz;



import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class VersionsCheckBiz {
	//版本更新，在线升级
	public static final String fileName = "/PDA_Version/version_info.txt";
	
	
	/**
	 * 检查版本更新
	 * @param context
	 * @return
	 */
	public boolean getVersionCode(Context context,String version){		
		boolean isHasnew = false;
		double versioncode;		
		PackageManager packageManager = context.getPackageManager();
		try {
			PackageInfo  info =packageManager.getPackageInfo(context.getPackageName(), 0);
			//当前版本号
			versioncode = Double.parseDouble(info.versionName.trim());
			Log.i("versioncode当前版本号",versioncode+"");
			
			double newversion = Double.parseDouble(version.trim());
			Log.i("新版本",newversion+"");
			if(versioncode<newversion){
				isHasnew = true;
			}
						
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("错",e.getMessage());
		} 
		
		return isHasnew;
	}
	
}
