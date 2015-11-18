package com.jiusg.mainscreenshow.base;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.jiusg.mainscreenshow.tools.CrashHandler;
import com.jiusg.mainscreenshow.tools.MyLog;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BaseApplication extends Application {

	private final String TAG = "BaseApplication";

	@Override
	public void onCreate() {

		super.onCreate();

		AVAnalytics.setAnalyticsEnabled(true);
		AVOSCloud.initialize(this,
				"344d1fgl5cnwwvihhenzxg7lodwrzh9ktm0e0ngg1ngbhz4a",
				"46meqe2r876ejiega2nilqhivjxwiyqbx5wgrb50n6vuwrs7");
		// AVAnalytics.start(this); 已经不再需要这行代码了
		AVAnalytics.enableCrashReport(this, true);

		if (C.ISCRASHHANDLER) {
			CrashHandler crashHandler = CrashHandler.getInstance();
			// 注册crashHandler
			crashHandler.init(getApplicationContext());
		}

		// 判断是否魅族机型
		Log.i(TAG, "MANUFACTURER" + android.os.Build.MANUFACTURER);
		if (android.os.Build.MANUFACTURER.equals("Meizu")
				|| android.os.Build.MANUFACTURER.equals("MeiZu")
				|| android.os.Build.MANUFACTURER.equals("meizu")) {
			C.ISMEIZU = true;
		}

		SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(this);
		if(setting.getBoolean("Debug",false)){
			MyLog.MYLOG_SWITCH = true;
		}
	}

}
