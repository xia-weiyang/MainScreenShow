package com.jiusg.mainscreenshow.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.avos.avoscloud.AVAnalytics;
import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.animation.anim.AnimationManage;
import com.jiusg.mainscreenshow.base.AnimationConvert;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.process.AnimationProcess;
import com.jiusg.mainscreenshow.process.impl.BubbleAnimationProcess;
import com.jiusg.mainscreenshow.process.impl.PictureWallAnimationProcess;
import com.jiusg.mainscreenshow.process.impl.RainAnimationProcess;
import com.jiusg.mainscreenshow.process.impl.StarshineAnimationProcess;
import com.jiusg.mainscreenshow.receiver.BatteryReceiver;
import com.jiusg.mainscreenshow.tools.ImageCache;
import com.jiusg.mainscreenshow.tools.LaunchImage;
import com.jiusg.mainscreenshow.tools.MyLog;
import com.jiusg.mainscreenshow.tools.PropertiesUtils;
import com.jiusg.mainscreenshow.tools.UpdateVersion;
import com.jiusg.mainscreenshow.ui.MSSEvent;
import com.jiusg.mainscreenshow.ui.Welcome;

/**
 * 2014.6.9 凌乱的逻辑，重写Service
 * 2015.10.5 更改service
 *
 * @author zk
 */
public class MSSService extends Service implements OnTouchListener {

    private int FLAGS = 0;
    private WindowManager Wm;
    private WindowManager Wm_control;
    private WindowManager.LayoutParams wmParams;
    private WindowManager.LayoutParams wmParams_control;
    private ActivityManager mActivityManager;
    private View win_control;
    private SharedPreferences sp;
    private SharedPreferences sp_setting;
    private ScreenReceiver sr;
    private final String TAG = "MSSService";
    private MSSReceiver mssr;
    private BatteryReceiver br = null;
    private SharedPreferences sp_userinfo;

    private SparseBooleanArray sa_IsEvent; // 存放事件是否启动
    private SparseArray<Integer> sa_Animation; // 存放事件对应的动画
    private AnimationManage animManage = null;

    private boolean isView; // WindowManager上是否已加载了View
    private boolean isCharing = false;
    private boolean isWifi = false; // 是否wifi已连接 这里主要是防止多次收到wifi连接信息

    private MssHandler mHandler;

    private final int HANDLER_ISWIFI = 0;
    private final int HANDLER_TIMER = 1;

    public NotificationManager nm = null;
    public final int NOTIFY_ID = 186;

    public static ImageCache imageCache = null;
    public static int imageCacheFlag = 0;
    public static boolean isExit = false;

    @Override
    public IBinder onBind(Intent intent) {

        return new MSSSBinder();
    }

    @Override
    public void onCreate() {

        super.onCreate();

        sp = getSharedPreferences("date", 0);
        sp_setting = PreferenceManager.getDefaultSharedPreferences(this);
        sp_userinfo = getSharedPreferences("userinfo", 0);
        C.FRAME = Integer.parseInt(sp_setting.getString("Frame", "60"));
        Wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        // 设置悬浮窗为全屏
        wmParams.flags = 1280;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.alpha = 0.8f;
        wmParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        wmParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wmParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

        Wm_control = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        wmParams_control = new WindowManager.LayoutParams();
        wmParams_control.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        wmParams_control.format = PixelFormat.RGB_888;
        wmParams_control.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        wmParams_control.alpha = 0;
        wmParams_control.width = 0;
        wmParams_control.height = 0;
        win_control = new View(this);
        Wm_control.addView(win_control, wmParams_control);

        // 注册系统广播
        sr = new ScreenReceiver();
        sr.registerScreenActionReceiver(MSSService.this);
        registerBattery();

        // 注册自己程序的广播
        IntentFilter inf = new IntentFilter();
        inf.addAction("com.jiusg.mainscreenshow");
        mssr = new MSSReceiver();
        registerReceiver(mssr, inf);

        sa_IsEvent = new SparseBooleanArray();
        sa_Animation = new SparseArray<Integer>();
        animManage = new AnimationManage(Wm, wmParams, getApplicationContext());

        isView = false;

        mHandler = new MssHandler();

        imageCache = new ImageCache(getApplicationContext());

        AVAnalytics.onEvent(MSSService.this, "Server Create", TAG);

        startService(new Intent(this, MSSLiveWallpaper.class));


    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        sr.unRegisterScreenActionReceiver(MSSService.this);
        unregisterBattery();
        if (!isExit)
            startService(new Intent(this, MSSService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            FLAGS = intent.getFlags();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        Log.i(TAG, "FLAGS " + FLAGS);

        // 加载事件信息
        loadEvent();

        return START_STICKY;
    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     * @deprecated
     */
    public List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        // 属性
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }

        for (int i = 0; i < names.size(); i++) {
            MyLog.i(TAG, "getHomes=" + names.get(i));
        }
        return names;
    }

    /**
     * 判断当前界面是否是桌面
     *
     * @deprecated
     */
    public boolean isHome() {

        String packName = "";
        if (Build.VERSION.SDK_INT >= 22) {
        } else if (Build.VERSION.SDK_INT >= 21) {
//            packName = getTopPackNameOld();
            packName = getTopPackNameNew();
//            Log.i(TAG,getTopPackNameOld()+"   *****");
        } else {
            packName = getTopPackNameOld();
        }
        //   Log.i(TAG,packName);
        return getHomes().contains(packName);
    }

    /**
     * @return
     * @deprecated
     */
    private String getTopPackNameOld() {

        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        }

        List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        List<ActivityManager.RunningAppProcessInfo> tasksInfos = mActivityManager.getRunningAppProcesses();
        return rti.get(0).topActivity.getPackageName();
    }

    /**
     * Android Api 21及以上
     *
     * @return
     * @deprecated
     */
    private String getTopPackNameNew() {
        final int PROCESS_STATE_TOP = 2;
        ActivityManager.RunningAppProcessInfo currentInfo = null;
        Field field = null;
        try

        {
            field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
        } catch (Exception e)

        {
            return "";
        }

        ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
        for (
                ActivityManager.RunningAppProcessInfo app
                : appList)

        {
            Log.i(TAG, "sss" + app.processName);
            if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    app.importanceReasonCode == 0) {

                Integer state = null;
                try {
                    state = field.getInt(app);
                } catch (Exception e) {
                    return "";
                }
                if (state != null && state == PROCESS_STATE_TOP) {
                    currentInfo = app;
                    break;
                }
            }
        }

        if (currentInfo == null) {
            return "";
        }

        return currentInfo.processName;
    }


    /**
     * 判断当前界面是否为锁屏界面
     *
     * @param c
     * @return
     */
    public final boolean isScreenLocked(Context c) {
        @SuppressWarnings("static-access")
        android.app.KeyguardManager mKeyguardManager = (KeyguardManager) c
                .getSystemService(c.KEYGUARD_SERVICE);
        return mKeyguardManager.inKeyguardRestrictedInputMode();
    }

    public class MSSSBinder extends Binder {

        public MSSService getMSSService() {

            return MSSService.this;
        }
    }

    class ScreenReceiver extends BroadcastReceiver {

        private boolean isRegisterReceiver = false;

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {

                // 锁屏事件
                if (sa_IsEvent.get(C.EVENT_LOCKSCREEN) && !sa_IsEvent.get(C.EVENT_CHARGING))
                    if (isScreenLocked(MSSService.this)) {
                        startAnimation(sa_Animation.get(C.EVENT_LOCKSCREEN), C.EVENT_LOCKSCREEN);
                    }
                // 充电事件
                if (sa_IsEvent.get(C.EVENT_CHARGING)) {
                    if (isScreenLocked(MSSService.this)) {
                        if (isCharing)
                            startAnimation(sa_Animation.get(C.EVENT_CHARGING), C.EVENT_CHARGING);
                    }
                }


            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {

                stopAnimation();

            } else if (action.equals(Intent.ACTION_USER_PRESENT)) {

                // 解锁

                stopAnimation();

            }
        }


        // 注册广播的方法
        public void registerScreenActionReceiver(Context mContext) {
            if (!isRegisterReceiver) {
                isRegisterReceiver = true;

                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_SCREEN_OFF); // 屏幕熄灭
                filter.addAction(Intent.ACTION_SCREEN_ON); // 屏幕点亮
                filter.addAction(Intent.ACTION_USER_PRESENT); // 解锁

                mContext.registerReceiver(ScreenReceiver.this, filter);

            }
        }

        // 注销屏幕解锁、加锁广播接收者...
        public void unRegisterScreenActionReceiver(Context mContext) {
            if (isRegisterReceiver) {
                isRegisterReceiver = false;
                mContext.unregisterReceiver(ScreenReceiver.this);
            }
        }
    }

    /**
     * 接收广播
     *
     * @author Administrator
     */
    class MSSReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getStringExtra("msg").equals("call_ring")) {

                if (sa_IsEvent.get(C.EVENT_CALL)) {

                    startAnimation(sa_Animation.get(C.EVENT_CALL),
                            C.EVENT_CALL);
                }

            } else if (intent.getStringExtra("msg").equals("call_start")) {

                if (sa_IsEvent.get(C.EVENT_CALL)) {

                    startAnimation(sa_Animation.get(C.EVENT_CALL),
                            C.EVENT_CALL);
                }
            } else if (intent.getStringExtra("msg").equals("call_stop")) {


                stopAnimation();

            } else if (intent.getStringExtra("msg").equals("sms")) {

                if (sa_IsEvent.get(C.EVENT_SMS)) {

                    startAnimation(sa_Animation.get(C.EVENT_SMS),
                            C.EVENT_SMS);

                }

            } else if (intent.getStringExtra("msg").equals("wifi")) {

                if (!isWifi) {
                    isWifi = true;
                    MyLog.i(TAG, "WiFi is connected!");
                    Message msg = mHandler.obtainMessage();
                    msg.what = HANDLER_ISWIFI;
                    mHandler.sendMessageDelayed(msg, 5000);
                }
            }
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.equals(win_control))
            if (isView) {

                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {


                    // 来电事件

                    if (sp.getString("end" + C.EVENT_CALL, "").equals(
                            PropertiesUtils.GetEventInfo(C.EVENT_CALL,
                                    MSSService.this)[1])) {

                        stopAnimation();
                    }


                    // 锁屏事件

                    if (sp.getString("end" + C.EVENT_LOCKSCREEN, "")
                            .equals(PropertiesUtils
                                    .GetEventInfo(C.EVENT_LOCKSCREEN,
                                            MSSService.this)[1])) {

                        stopAnimation();
                    }


                    // 信息事件

                    if (sp.getString("end" + C.EVENT_SMS, "").equals(
                            PropertiesUtils.GetEventInfo(C.EVENT_SMS,
                                    MSSService.this)[1])) {

                        stopAnimation();

                    }
                    // 充电事件

                    if (sp.getString("end" + C.EVENT_CHARGING, "")
                            .equals(PropertiesUtils.GetEventInfo(
                                    C.EVENT_CHARGING, MSSService.this)[1])) {

                        stopAnimation();

                    }
                }
            }
        return true;
    }

    /**
     * 加载事件信息，包括（是否有此事件，以及该事件的动画）
     */
    public void loadEvent() {

        // 清理SparseArray
        sa_IsEvent.clear();
        sa_Animation.clear();


        if (sp_userinfo.getString("UserVersionInfo", "").equals(
                "OfficialVersion")
                || sp_userinfo.getString("UserVersionInfo", "").equals(
                "TrialVersion")
                || sp_userinfo.getString("UserVersionInfo", "").equals(
                C.VERSION_FREE)) {

            // 来电事件 MSSValue.EVENT_CALL
            if (sp.getString("start" + C.EVENT_CALL, "")
                    .equals(PropertiesUtils.GetEventInfo(C.EVENT_CALL,
                            MSSService.this)[0])
                    && sp.getString("state" + C.EVENT_CALL, "").equals(getString(R.string.action_started))) {

                sa_IsEvent.put(C.EVENT_CALL, true);
                sa_Animation.put(C.EVENT_CALL,
                        AnimationConvert.convertAnimation(sp.getString("animation" + C.EVENT_CALL, ""), getApplicationContext()));
            } else {

                sa_IsEvent.put(C.EVENT_CALL, false);
                sa_Animation.put(C.EVENT_CALL, C.ANIMATION_BUBBLE);
            }

            // 锁屏事件 MSSValue.EVENT_LOCKSCREEN
            if (sp.getString("start" + C.EVENT_LOCKSCREEN, "").equals(
                    PropertiesUtils.GetEventInfo(C.EVENT_LOCKSCREEN,
                            MSSService.this)[0])
                    && sp.getString("state" + C.EVENT_LOCKSCREEN, "").equals(
                    getString(R.string.action_started))) {

                sa_IsEvent.put(C.EVENT_LOCKSCREEN, true);
                sa_Animation.put(C.EVENT_LOCKSCREEN,
                        AnimationConvert.convertAnimation(sp.getString("animation" + C.EVENT_LOCKSCREEN, ""), getApplicationContext()));
            } else {

                sa_IsEvent.put(C.EVENT_LOCKSCREEN, false);
                sa_Animation.put(C.EVENT_LOCKSCREEN, C.ANIMATION_BUBBLE);
            }

            // 信息事件 MSSValue.EVENT_SMS
            if (sp.getString("start" + C.EVENT_SMS, "")
                    .equals(PropertiesUtils.GetEventInfo(C.EVENT_SMS,
                            MSSService.this)[0])
                    && sp.getString("state" + C.EVENT_SMS, "").equals(getString(R.string.action_started))) {

                sa_IsEvent.put(C.EVENT_SMS, true);
                sa_Animation.put(C.EVENT_SMS,
                        AnimationConvert.convertAnimation(sp.getString("animation" + C.EVENT_SMS, ""), getApplicationContext()));
            } else {

                sa_IsEvent.put(C.EVENT_SMS, false);
                sa_Animation.put(C.EVENT_SMS, C.ANIMATION_BUBBLE);
            }

            // 充电事件 MSSValue.EVENT_CHARING
            if (sp.getString("start" + C.EVENT_CHARGING, "").equals(
                    PropertiesUtils.GetEventInfo(C.EVENT_CHARGING,
                            MSSService.this)[0])
                    && sp.getString("state" + C.EVENT_CHARGING, "").equals(
                    getString(R.string.action_started))) {
                sa_IsEvent.put(C.EVENT_CHARGING, true);
                sa_Animation.put(C.EVENT_CHARGING,
                        AnimationConvert.convertAnimation(sp.getString("animation" + C.EVENT_CHARGING, ""), getApplicationContext()));

            } else {
                sa_IsEvent.put(C.EVENT_CHARGING, false);
                sa_Animation.put(C.EVENT_CHARGING, C.ANIMATION_BUBBLE);
            }

            MyLog.i(TAG, "EVENT_CALL  " + sa_IsEvent.get(C.EVENT_CALL) + "  "
                    + sa_Animation.get(C.EVENT_CALL));
            MyLog.i(TAG,
                    "EVENT_LOCKSCREEN  " + sa_IsEvent.get(C.EVENT_LOCKSCREEN)
                            + "  " + sa_Animation.get(C.EVENT_LOCKSCREEN));
            MyLog.i(TAG, "EVENT_SMS   " + sa_IsEvent.get(C.EVENT_SMS) + "   "
                    + sa_Animation.get(C.EVENT_SMS));
            MyLog.i(TAG, "EVENT_CHARING   " + sa_IsEvent.get(C.EVENT_CHARGING)
                    + "   " + sa_Animation.get(C.EVENT_CHARGING));
        }
    }

    /**
     * 开始一个动画 强行开始，立即停止当前动画并开始
     *
     * @param anim  动画
     * @param event 事件
     */
    public void startAnimation(int anim, int event) {

        // 首先是对isView的判断
        // 如果true，将其停止掉
        // 如果false，在开始动画
        // 其次查找animationL中有没有对应的动画
        // 如果有直接拿出来运行，如果没有，创建一个并保存

        // 省电模式中如果低电量状态则不执行任何动画
        if (C.BATTER_STATUS != C.BATTER_LOWER_POWER) {

            // 停止动画
            stopAnimation();

            if (!isView) {

                AVAnalytics.onEvent(MSSService.this, "Start Animation",
                        sp.getString("animation" + event, ""));
                animManage.startAnimation(anim, event);
                isView = true;
            }
        }
    }

    /**
     * 停止动画
     */
    public void stopAnimation() {

        if (isView) {

            animManage.stopAnimation();
            isView = false;
        }

    }

    /**
     * 得到对应动画接口实现类的类名
     *
     * @param name
     * @return 类名
     *
     * @deprecated
     */
    private String GetAnimationClassName(String name) {

        if (name.equals(PropertiesUtils.getAnimationInfo(MSSService.this)[C.ANIMATION_BUBBLE])) {

            return "BubbleAnimationProcess";
        } else if (name.equals(PropertiesUtils.getAnimationInfo(MSSService.this)[C.ANIMATION_STARSHINE])) {

            return "StarshineAnimationProcess";
        } else if (name.equals(PropertiesUtils.getAnimationInfo(MSSService.this)[C.ANIMATION_PICTUREWALL])) {

            // 幻灯片动画不需要让动画继续进行
            // 每次重新开始即可
            return null;
        } else if (name.equals(PropertiesUtils.getAnimationInfo(MSSService.this)[C.ANIMATION_RAIN])) {

            return "RainAnimationProcess";
        }

        return null;
    }

    /**
     * 实例一个动画 其中，存放动画的sharePreference的name为事件的name+动画的name
     *
     * @param name  动画名字
     * @param event 事件
     * @return AnimationProcess
     * @deprecated
     */
    private AnimationProcess instanceAnimation(String name, int event) {

        AnimationProcess animationP = null;
        if (name.equals(PropertiesUtils.getAnimationInfo(this)[C.ANIMATION_BUBBLE])) {

            animationP = new BubbleAnimationProcess(
                    MSSService.this,
                    sp.getString("start" + event, "")
                            + PropertiesUtils.getAnimationInfo(this)[C.ANIMATION_BUBBLE]);

        } else if (name
                .equals(PropertiesUtils.getAnimationInfo(this)[C.ANIMATION_STARSHINE])) {

            animationP = new StarshineAnimationProcess(
                    MSSService.this,
                    sp.getString("start" + event, "")
                            + PropertiesUtils.getAnimationInfo(this)[C.ANIMATION_STARSHINE]);

        } else if (name
                .equals(PropertiesUtils.getAnimationInfo(this)[C.ANIMATION_PICTUREWALL])) {
            animationP = new PictureWallAnimationProcess(
                    MSSService.this,
                    sp.getString("start" + event, "")
                            + PropertiesUtils.getAnimationInfo(this)[C.ANIMATION_PICTUREWALL]);
        } else if (name
                .equals(PropertiesUtils.getAnimationInfo(this)[C.ANIMATION_RAIN])) {

            animationP = new RainAnimationProcess(
                    MSSService.this,
                    sp.getString("start" + event, "")
                            + PropertiesUtils.getAnimationInfo(this)[C.ANIMATION_RAIN]);
        }

        return animationP;
    }


    class MssHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == HANDLER_ISWIFI) {

                isWifi = false;
                disposeWifi();
            } else if (msg.what == 3) {

            }
        }

    }


    class Event {
        // 所有事件的优先级
        public final int EVENT_NO = 0;   //  没有事件
        public final int EVENT_CALL = 5;   // 来电事件
        public final int EVENT_DESKTOP = 0;  // 桌面事件
        public final int EVENT_LOCKSCREEN = 1; // 锁屏事件
        public final int EVENT_SMS = 4;   // 短信事件
        public final int EVENT_CHARGING = 2;  // 充电事件
        public final int EVENT_PREVIEW = 3;  // 动画预览事件


    }




    /**
     * 执行预览动画
     *
     * @param animation
     */
    public void previewStartA(int animation) {


        startAnimation(animation, C.EVENT_PREVIEW);

    }

    /**
     * 停止预览动画
     */
    public void previewStopA() {


        stopAnimation();


    }


    /**
     * 注册监听电池广播
     */
    public void registerBattery() {

        br = new BatteryReceiver(MSSService.this);
        registerReceiver(br, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    /**
     * 注销监听电池广播
     */
    public void unregisterBattery() {

        if (br != null)
            unregisterReceiver(br);
    }

    /**
     * 充电事件的运行方法，由BatteryReceiver调用
     */
    public void runCharing() {
        Log.i(TAG, "runCharing");
        switch (C.BATTER_CHARING_STATUS) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                isCharing = true;
                break;
            default:
                stopAnimation();
                isCharing = false;
                break;
        }
    }

    /**
     * 处理Wifi变化
     */
    public void disposeWifi() {

        checkUpdate();
        disposeLaunchImage();
    }

    /**
     * 处理 LaunchImage
     */
    public void disposeLaunchImage() {

        LaunchImage launchImage = new LaunchImage(MSSService.this);
        launchImage.downloadImage();
    }

    /**
     * 检查更新
     */
    public void checkUpdate() {

        final UpdateVersion updateVersion = new UpdateVersion(MSSService.this);
        if (updateVersion.isCheckUpdate(3, 11, 23)) {

            updateVersion.checkVersionFromNetwork(updateVersion.new CallBack() {

                @Override
                public void done(String versionName, String versionUrl,
                                 String versionInfo) {

                    if (!versionName.equals(updateVersion.getVersionName())) {
                        // 发现新版本
                        // 通知栏提示
                        if (!MSSEvent.isActive) {
                            notifyVersion(versionName, versionInfo);
                        }
                    }
                }
            });
        }

    }

    /**
     * 通知栏显示更新信息
     *
     * @param versionName
     * @param versionInfo
     */
    private void notifyVersion(String versionName, String versionInfo) {

        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        Intent i = new Intent(MSSService.this, Welcome.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, i, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentTitle(getString(R.string.app_name) + "  " + versionName)
                .setContentIntent(pendingIntent)
                .setTicker(getString(R.string.tip_newVersion))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentInfo(versionInfo);
        nm.notify(NOTIFY_ID, mBuilder.build());

    }

    public void canclaNotify() {

        if (nm != null)
            nm.cancel(NOTIFY_ID);
    }

}