package com.jiusg.mainscreenshow.receiver;

import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.service.MSSService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * 接收系统广播，开机自启服务
 * 
 * @author Administrator
 * 
 */
public class BootReceiver extends BroadcastReceiver {

	private final String TAG = "BootReceiver";
	private SharedPreferences sp;
	private SharedPreferences sp_userinfo;

	@Override
	public void onReceive(Context context, Intent intent) {

		sp = PreferenceManager.getDefaultSharedPreferences(context);
		sp_userinfo = context.getSharedPreferences("userinfo", 0);

		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

			if (sp.getBoolean("PowerBoot", true)) {
				if (sp_userinfo.getString("UserVersionInfo", "").equals(
						"OfficialVersion")
						|| sp_userinfo.getString("UserVersionInfo", "").equals(
								"TrialVersion")
						|| sp_userinfo.getString("UserVersionInfo", "").equals(
								C.VERSION_FREE)) {
					context.startService(new Intent(context, MSSService.class)
							.setFlags(C.SERVICE_FLAG_BOOTRECEIVER));
					Log.i(TAG, "MSSService has started.");
				}
			}
		}

	}

}
