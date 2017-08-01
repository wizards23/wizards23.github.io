package com.ljsw.tjbankpda.util;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 *	跳转
 * @author lenovo
 *
 */
public class Skip {

	public static void skip(Activity activity,Class className,Bundle bundle,int action){
		Intent intent = new Intent(activity, className);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		activity.startActivity(intent);
	}
}
