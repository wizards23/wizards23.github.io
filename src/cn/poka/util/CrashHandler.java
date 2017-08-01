package cn.poka.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * 异常捕捉器
 */
public class CrashHandler implements UncaughtExceptionHandler {
	/**
	 * Debug Log Tag
	 */
	public static final String TAG = "CrashHandler";
	/**
	 * CrashHandler实例
	 */
	private static CrashHandler INSTANCE;
	/**
	 * 程序的Context对象
	 */
	private Context mContext;

	private UncaughtExceptionHandler mDefaultHandler;
	
	private StringBuffer mErrorLogBuffer = new StringBuffer();
	
	private static final String SINGLE_RETURN = "\n";
	
    private static final String SINGLE_LINE = "----------------分割线----------------";
	
	/**
	 * 保证只有一个CrashHandler实例
	 */
	private CrashHandler() {
	}

	/**
	 * 获取CrashHandler实例 ,单例模式
	 * 
	 * @return
	 */
	public static CrashHandler getInstance() {
		if (INSTANCE == null) {
			synchronized (CrashHandler.class) {
				INSTANCE = new CrashHandler();
			}
		}
		return INSTANCE;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */
	public void init(Context ctx) {
		mContext = ctx;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	public void uncaughtException(Thread thread, Throwable ex) {
		final String msg = ex.getLocalizedMessage();
		//begin ：注释此段代码，不使用toast提示异常改为log文件方式  modify by liuchang 2017-04-29
		// // 使用Toast来显示异常信息
		// new Thread() {
		// @Override
		// public void run() {
		// // Toast 显示需要出现在一个线程的消息队列中
		// Looper.prepare();
		// Toast.makeText(mContext, "系统异常:" + msg, Toast.LENGTH_SHORT)
		// .show();
		// Looper.loop();
		// }
		// }.start();
		//
		// try {
		// Thread.sleep(3000);
		// } catch (InterruptedException e) {
		// System.out.println(e.getLocalizedMessage());
		// }
		// android.os.Process.killProcess(android.os.Process.myPid());
		// System.exit(10);
		// end
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理异常就由系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			android.os.Process.killProcess(android.os.Process.myPid());
		}

	}
	
	/**
	 * 
	 * @param ex
	 * @return
	 */
	private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉,现钞管理系统停止运行,即将退出.", Toast.LENGTH_SHORT)
                        .show();
                Looper.loop();
            }
        }).start();

        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 收集错误日志
        collectCrashInfo(ex);
        // 保存错误日志
        saveErrorLog();

        return true;
    }
	
	/**
	 * 保存日志到/mnt/sdcard/AppLog/目录下，文件名已时间yyyy-MM-dd_hh-mm-ss.log的形式保存
	 */
    private void saveErrorLog() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss", Locale.getDefault());
            String format = sdf.format(new Date());
            format += ".log";
            //Environment.getExternalStorageDirectory().getPath()获取SDCard路径
            String path = Environment.getExternalStorageDirectory().getPath()+"/PokaAppLog/";
            File file = new File(path);
            if (!file.exists()){
                file.mkdirs();
            }

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path+format);
                fos.write(mErrorLogBuffer.toString().getBytes());
                fos.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                        fos = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 收集错误信息
     * @param ex
     */
    private void collectCrashInfo(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        String result = info.toString();
        printWriter.close();

        //将错误信息加入mErrorLogBuffer中
        append("", result);
        mErrorLogBuffer.append(SINGLE_LINE + SINGLE_RETURN);

        Log.d(TAG, "saveCrashInfo2File:" + mErrorLogBuffer.toString());
    }

    /**
     * 收集应用和设备信息
     * @param context
     */
    private void collectDeviceInfo(Context context) {
        //每次使用前，清掉mErrorLogBuffer里的内容
        mErrorLogBuffer.setLength(0);
        mErrorLogBuffer.append(SINGLE_RETURN + SINGLE_LINE + SINGLE_RETURN);

        //获取应用的信息
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                append("versionCode", pi.versionCode);
                append("versionName", pi.versionName);
                append("packageName", pi.packageName);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        mErrorLogBuffer.append(SINGLE_LINE + SINGLE_RETURN);

        //获取设备的信息
        Field[] fields = Build.class.getDeclaredFields();
        getDeviceInfoByReflection(fields);

        fields = Build.VERSION.class.getDeclaredFields();
        getDeviceInfoByReflection(fields);

        mErrorLogBuffer.append(SINGLE_LINE + SINGLE_RETURN);
    }

    /**
     * 获取设备的信息通过反射方式
     * @param fields
     */
    private void getDeviceInfoByReflection(Field[] fields) {
        for (Field field : fields) {
            try {
            	//对private成员变量设置可访问
                field.setAccessible(true);
                append(field.getName(), field.get(null));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    //mErrorLogBuffer添加友好的log信息
    private void append(String key, Object value) {
        mErrorLogBuffer.append("" + key + ":" + value + SINGLE_RETURN);
    }

}