package com.example.app.util;

import com.example.app.entity.KuanxiangChuruEntity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 页面跳转类
 * @author yuyunheng
 *
 */
public class Skip {

	/**
	 * 页面跳转方法
	 * @param activity　　　当前activity
	 * @param className   跳往的页面
	 * @param bundle       参数
	 * @param action   动画
	 */
	public static void skip(Activity activity,Class className,Bundle bundle,int action){
		Intent intent = new Intent(activity, className);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		activity.startActivity(intent);
	}
}
