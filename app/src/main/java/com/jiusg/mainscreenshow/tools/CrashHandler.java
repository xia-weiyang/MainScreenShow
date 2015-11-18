package com.jiusg.mainscreenshow.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Properties;

import com.avos.avoscloud.AVObject;
import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.service.MSSService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * 
 * UncaughtExceptionHandler：线程未捕获异常控制器是用来处理未捕获异常的。 如果程序出现了未捕获异常默认情况下则会出现强行关闭对话框
 * 实现该接口并注册为程序中的默认未捕获异常处理 这样当未捕获异常发生时，就可以做些异常处理操作 例如：收集异常信息，发送错误报告 等。
 * 
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 */
@SuppressLint("ShowToast")
public class CrashHandler implements UncaughtExceptionHandler {
	/** Debug Log Tag */
	public static final String TAG = "CrashHandler";
	/** 是否开启日志输出, 在Debug状态下开启, 在Release状态下关闭以提升程序性能 */
	public static final boolean DEBUG = true;
	/** CrashHandler实例 */
	private static CrashHandler INSTANCE;
	/** 程序的Context对象 */
	private Context mContext;
	/** 系统默认的UncaughtException处理类 */
	// private Thread.UncaughtExceptionHandler mDefaultHandler;

	/** 使用Properties来保存设备的信息和错误堆栈信息 */
	private Properties mDeviceCrashInfo = new Properties();
	private static final String VERSION_NAME = "versionName";
	private static final String VERSION_CODE = "versionCode";

	// private static final String STACK_TRACE = "STACK_TRACE";

	private SharedPreferences sp;

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {


	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		if (INSTANCE == null)
			INSTANCE = new CrashHandler();
		return INSTANCE;
	}

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 * 
	 * @param ctx
	 */
	public void init(Context ctx) {
		mContext = ctx;
		// mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		MyLog.e(TAG, ex);
		// 使用Toast来显示异常信息
		ex.setStackTrace(ex.getStackTrace());
		ex.printStackTrace(printWriter);
		collectCrashDeviceInfo(mContext, result.toString());

		sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		if (sp.getBoolean("ErrorTip", true)) {
			new Thread() {
				@Override
				public void run() {
					// Toast 显示需要出现在一个线程的消息队列中
					Looper.prepare();
					Toast.makeText(mContext, mContext.getString(R.string.tip_error),
							Toast.LENGTH_LONG).show();
					Looper.loop();
				}
			}.start();
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				MyLog.e(TAG, "thread sleep error!/r/n" + e);
			}
		}
		android.os.Process.killProcess(android.os.Process.myPid());

		System.exit(0);
		mContext.startService(new Intent().setClass(mContext, MSSService.class));

		// 调用系统错误机制
		// mDefaultHandler.uncaughtException(thread, ex);

	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false
	 */

	/*
	 * private boolean handleException(Throwable ex) { if (ex == null) { return
	 * true; } final String msg = ex.getLocalizedMessage(); // 使用Toast来显示异常信息
	 * new Thread() {
	 * 
	 * @Override public void run() { // Toast 显示需要出现在一个线程的消息队列中
	 * Looper.prepare(); Toast.makeText(mContext, "程序出错啦:" + msg,
	 * Toast.LENGTH_LONG).show(); Looper.loop(); } }.start(); // 收集设备信息
	 * collectCrashDeviceInfo(mContext); // 保存错误报告文件 String crashFileName =
	 * saveCrashInfoToFile(ex); // 发送错误报告到服务器 //
	 * sendCrashReportsToServer(mContext); return true; }
	 */

	/**
	 * 收集程序崩溃的设备信息,并发送到服务器
	 * 
	 * @param ctx
	 */
	public void collectCrashDeviceInfo(Context ctx,String e) {

		String versionName = getVersionName();
		String model = android.os.Build.MODEL;
		String androidVersion = android.os.Build.VERSION.RELEASE;
		String manufacturer = android.os.Build.MANUFACTURER;

		AVObject object = new AVObject("Exception");
		object.put("version",versionName);
		object.put("android",androidVersion);
		object.put("model",model);
		object.put("manufacturer",manufacturer);
		object.put("exception",e);
		object.saveInBackground();
	}

	/**
	 * 获取当前应用版本号
	 *
	 * @return
	 */
	public String getVersionName() {

		PackageManager packageManager = mContext.getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(mContext.getPackageName(),
					0);
		} catch (NameNotFoundException e) {

			e.printStackTrace();
			return "0.0.0";
		}
		String version = packInfo.versionName;

		return version;
	}


}
