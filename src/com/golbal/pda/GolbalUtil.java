package com.golbal.pda;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

public class GolbalUtil {

    public static int ismover = 0; // 标识是否有滑动

    public static boolean onclicks = true; // 防多触发

    public static AsyncTask asynctask; // 异步全局引用

    private static final int distance = 15;

    /**
     * activity跳转
     * 
     * @param activity
     *            当前activity
     * @param clas
     *            目标activity
     * @param bundle
     *            Bundle传值
     * @param ismover
     *            滑动状态不触发事件
     * @param
     */
    public void gotoActivity(Activity activity, Class clas, Bundle bundle, int ismover) {
        if (ismover <= distance) {
            Intent intent = new Intent(activity, clas);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            activity.startActivity(intent);

        }
    }

    /**
     * 发送广播
     * 
     * @param action_name
     *            动作
     * @param a
     *            所在Activity
     */
    public void sendBroadcastReceiver(String action_name, Activity a, Bundle b) {
        Intent intent = new Intent(action_name); // 定义动作
        if (b != null) {
            intent.putExtras(b);
        }
        a.sendBroadcast(intent);
    }

    /**
     * 注册广播
     * 
     * @param action_name
     *            动作
     * @param borad
     *            广播实例
     * @param a
     *            所在Activity
     */
    public void registerBoradcastReceiver(String action_name, BroadcastReceiver borad, Activity a) {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(action_name);
        a.registerReceiver(borad, myIntentFilter);
    }

    /**
     * 获取当前时间
     * 
     * @throws ParseException
     */
    public long gettime() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
        Log.i("time", sdf.format(new Date()) + "");
        Log.i("time", sdf.parse(sdf.format(new Date())).getTime() + "");
        return 0;
    }

}
