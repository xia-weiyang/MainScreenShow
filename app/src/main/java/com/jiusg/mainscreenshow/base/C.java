package com.jiusg.mainscreenshow.base;

import android.os.BatteryManager;

public class C {

	/**
	 *   魅族适配
	 */
	public final static boolean MEIZU_ADAPTE = false;  
	
	public static boolean ISMEIZU = false;
	
	/**
	 * 是否启用CrashHandler
	 */
	public static boolean ISCRASHHANDLER = true;
	
	/**
	 *  Service开机启动
	 */
	public final static  int SERVICE_FLAG_BOOTRECEIVER = 100;  
	
	public final static boolean ISFREE = true;  // 免费版
	
	public final static int EVENT_NO = 0;   //  没有事件
	public final static int EVENT_CALL = 1;   // 来电事件
	public final static int EVENT_DESKTOP = 2;  // 桌面事件
	public final static int EVENT_LOCKSCREEN = 3; // 锁屏事件 
	public final static int EVENT_SMS = 4;   // 短信事件
	public final static int EVENT_CHARGING = 5;  // 充电事件
	public final static int EVENT_PREVIEW = 6;  // 动画预览事件
	
	public static int FRAME = 60;  // 帧数
	public static int POWERSAVING = 20;  // 省电的最低电量
	
	public final static int ANIMATION_BUBBLE = 0;  //气泡
	public final static int ANIMATION_STARSHINE = 1; // 星光
	public final static int ANIMATION_PICTUREWALL = 2; //幻灯片
	public final static int ANIMATION_RAIN = 3; // 雨
	public final static int ANIMATION_SNOW = 4; // 雪
	
	public static int BATTER_STATUS = 100;  // 电池状态
	public static int BATTER_CHARING_STATUS = BatteryManager.BATTERY_STATUS_UNKNOWN; // 电池充电状态
	public static int BATTER_CHARING_TYPE = BatteryManager.BATTERY_PLUGGED_AC; // 电池充电类型
	public final static int BATTER_NORMAL_POWER = 100; //正常电量状态 
	public final static int BATTER_LOWER_POWER = 101; //低电量状态
	
	public static boolean ISUPDATE = false;  // 用于在更新软件后，弹出更新记录
	
	public final static String VERSION_OFFICAL = "OfficialVersion";
	public final static String VERSION_TRIAL = "TrialVersion";
	public final static String VERSION_TRIAL_OVER = "TrialVersionOver";
	public final static String VERSION_FREE = "FreeVersion";
}
