package com.ljsw.tjbankpda.util;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * ImageView点击效果类
 * @author FUHAIQING-MANAGER
 */
public class onTouthImageStyle {
	/**
	 * 设置滤镜，使图片变暗
	 * @param v
	 */
	public void setFilter(ImageView v){
		Drawable drawable=v.getDrawable();
		if(drawable==null){
			drawable=v.getBackground();
		}
		if(drawable!=null){
			drawable.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
		}
	}
	
	/**
	 * 移除滤镜
	 * @param v
	 */
	public void removeFilter(ImageView v){
		Drawable drawable=v.getDrawable();
		if(drawable==null){
			drawable=v.getBackground();
		}
		if(drawable!=null){
			drawable.clearColorFilter();
		}
	}
}
