package com.golbal.pda;

import com.example.pda.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class GolbalView {

    public WindowManager wm = null;

    public LayoutParams wmParams = null;

    public static boolean webServiceAddress;

    /**
     * 准备载入的页面
     */
    private static LayoutInflater lf;
    
    /**
     * @Title: getLF 
     * @Description: TODO 获取指定的Activity对象
     * @param context
     * @return
     * @author liuchang
     * @date 2017-4-26 上午9:47:05
     */
    public static LayoutInflater getLF(Context context) {
        if (lf == null) {
            lf = ((Activity) context).getLayoutInflater();
        }
        return lf;
    }

    /**
     * 悬浮窗口 全屏
     * @param context
     *            当前所在程序
     * @param v
     *            布局
     */
    public void createFloatView(Context context, View v) {
        if (wm == null) {
            // 全局
            // wm=(WindowManager)context.getApplicationContext().getSystemService(context.WINDOW_SERVICE);
            // 非全局
            wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        }
        if (wmParams == null) {
            wmParams = new WindowManager.LayoutParams();
        }
        wmParams.type = 2002; // 这里的2002表示系统级窗口，
        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.TRANSLUCENT;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;

        Activity a = (Activity) context;
        DisplayMetrics dm = new DisplayMetrics();
        a.getWindowManager().getDefaultDisplay().getMetrics(dm);

        // 设置浮动窗口可聚焦（） FLAG_ALT_FOCUSABLE_IM聚集
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.width = dm.widthPixels;
        wmParams.height = 800;
        wmParams.gravity = Gravity.CENTER_VERTICAL;
        try {
            wm.addView(v, wmParams);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.out.println("WindowManager----error");

        }
    }

    /**
     * 悬浮窗口 非全屏
     * 
     * @param context
     *            当前所在程序
     * @param v
     *            布局
     */
    public void createFloat(Context context, View v, int place) {
        if (wm == null) {
            // 非全局
            // wm=(WindowManager)context.getSystemService(context.WINDOW_SERVICE);
            // 全局
            wm = (WindowManager) context.getApplicationContext().getSystemService(context.WINDOW_SERVICE);

        }
        if (wmParams == null) {
            wmParams = new WindowManager.LayoutParams();
        }
        wmParams.type = 2002; // 这里的2002表示系统级窗口，
        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.TRANSLUCENT;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;

        Activity a = (Activity) context;
        DisplayMetrics dm = new DisplayMetrics();
        a.getWindowManager().getDefaultDisplay().getMetrics(dm);

        // 设置浮动窗口可聚焦（） FLAG_ALT_FOCUSABLE_IM聚集
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.width = dm.widthPixels;
        wmParams.height = 130;
        wmParams.gravity = Gravity.CENTER_VERTICAL | place;
        try {
            wm.addView(v, wmParams);
        } catch (Exception e) {
            e.printStackTrace();
            removeV(v);
        }
    }

    /**
     * 移除悬浮窗口
     */
    public void removeV(View v) {
        try {
            wm.removeView(v);
            wm = null;
            wmParams = null;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("removewindow" + e.getMessage());
        }
    }

    /**
     * 初始化一些常用的全局变量
     * 
     * @param context
     */
    public void Init(Context context) {
        lf = ((Activity) context).getLayoutInflater();
    }

    /**
     * 添加fragment
     * 
     * @param a
     *            activity
     * @param f
     *            fragment实例
     * @param v
     *            ID
     * @param bundle
     *            Bundle实例传值
     * @param first
     *            标记在前次页面的传值的操作次数（setArguments(bundle)在同一页面多次调用会报异常）
     */

    public int addFragment(Activity a, int v, Fragment f, Bundle bundle) {
        FragmentManager fragmentManager = a.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(v, f);
        // if(first){
        f.setArguments(bundle);
        // }
        return fragmentTransaction.commit();

    }

    /**
     * 替换fragment
     * 
     * @param a
     *            activity
     * @param v
     *            要替换的位置
     * @param f
     *            fragment实例
     * @param bundle
     *            Bundle传值
     * @param isfirst只能在当前页面传一次值
     *            ，禁止在同一个页面重复调用setArguments方法，会报异常
     */
    public void replaceFragment(Activity a, int v, Fragment f, Bundle bundle) {

        FragmentManager fragmentManager = a.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(v, f);
        // fragmentTransaction.addToBackStack(null);
        // if(isfirst){
        f.setArguments(bundle);
        // }

        fragmentTransaction.commit();
    }

    /**
     * 删除fragment
     * 
     * @param a
     *            activity
     * @param f
     *            fragment实例
     * @param v
     *            ID
     */
    public static void delFragment(Activity a, Fragment f) {
        FragmentManager fragmentManager = a.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(f);
        fragmentTransaction.commit();

    }

    /**
     * @param msg
     * @param context
     */
    public static void toastShow(Context context, String msg) {
        View vtoast = GolbalView.lf.inflate(R.layout.toast, null);
        TextView text = (TextView) vtoast.findViewById(R.id.toast);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        text.setText(msg);
        toast.setView(text);
        toast.setDuration(300);
        toast.show();
    }

}
