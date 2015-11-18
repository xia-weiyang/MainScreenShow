package com.jiusg.mainscreenshow.receiver;

import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.service.MSSService;
import com.jiusg.mainscreenshow.tools.MyLog;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * 接受电池信息的广播
 *
 * @author Administrator
 */
public class BatteryReceiver extends BroadcastReceiver {

    private int intLevel = -1;
    private SharedPreferences sp;
    private final String TAG = "BatteryReceiver";
    private MSSService serivce = null;

    public BatteryReceiver() {

    }

    public BatteryReceiver(MSSService serivce) {
        this.serivce = serivce;
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onReceive(Context context, Intent intent) {

        sp = PreferenceManager.getDefaultSharedPreferences(context);
        if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {

            intLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            switch (intent.getIntExtra(BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_UNKNOWN)) {
                case BatteryManager.BATTERY_STATUS_UNKNOWN: // 未知
                    C.BATTER_CHARING_STATUS = BatteryManager.BATTERY_STATUS_UNKNOWN;
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING: // 充电
                    C.BATTER_CHARING_STATUS = BatteryManager.BATTERY_STATUS_CHARGING;
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING: // 未充电
                    C.BATTER_CHARING_STATUS = BatteryManager.BATTERY_STATUS_NOT_CHARGING;
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING: // 放电状态
                    C.BATTER_CHARING_STATUS = BatteryManager.BATTERY_STATUS_DISCHARGING;
                    break;
                case BatteryManager.BATTERY_STATUS_FULL: // 充满电
                    C.BATTER_CHARING_STATUS = BatteryManager.BATTERY_STATUS_FULL;
                    break;
                default:
                    break;
            }
            switch (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
                case BatteryManager.BATTERY_PLUGGED_USB:
                    C.BATTER_CHARING_TYPE = BatteryManager.BATTERY_PLUGGED_USB;
                    break;
                case BatteryManager.BATTERY_PLUGGED_AC:
                    C.BATTER_CHARING_TYPE = BatteryManager.BATTERY_PLUGGED_AC;
                    break;
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    C.BATTER_CHARING_TYPE = BatteryManager.BATTERY_PLUGGED_WIRELESS;
                    break;
                default:
                    break;
            }
            if (sp.getBoolean("PowerSaving", true)) {
                if (intLevel <= C.POWERSAVING) {

                    C.BATTER_STATUS = C.BATTER_LOWER_POWER;
                }
            }else{

                C.BATTER_STATUS = C.BATTER_NORMAL_POWER;
            }
        }
        /*
		 * MyLog.i(TAG,
		 * "MSSValue.BATTER_CHARING_TYPE  "+MSSValue.BATTER_CHARING_TYPE);
		 * MyLog.i(TAG,
		 * "MSSValue.BATTER_CHARING_STATUS  "+MSSValue.BATTER_CHARING_STATUS);
		 * MyLog.i(TAG, "health"+intent.getIntExtra("health",
		 * BatteryManager.BATTERY_HEALTH_UNKNOWN));
		 */
        MyLog.i(TAG, "BATTER_STATUS=" + C.BATTER_STATUS);
        if (serivce != null)
            serivce.runCharing();
    }

}
