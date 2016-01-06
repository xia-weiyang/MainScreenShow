package com.jiusg.mainscreenshow.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.animation.picturewall.PictureWall;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.service.MSSService;
import com.jiusg.mainscreenshow.tools.LaunchImage;
import com.jiusg.mainscreenshow.tools.MyLog;
import com.jiusg.mainscreenshow.tools.PropertiesUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Welcome extends Activity {

    private ViewPager vp;
    private ArrayList<View> views;
    private ImageView cicre1;
    private ImageView cicre2;
    private ImageView cicre3;
    private ImageView cicre4;
    private Button bt;
    private mHandler handler;
    private boolean isWelcome = false;
    private final String TAG = "Welcome";
    private SharedPreferences sp_userinfo; // 存储基本数据
    private SharedPreferences sp_date;
    public int screenwidth;
    public int screenheight; // 除去状态栏的高度
    private MSSService mssservice;
    private ServiceConnection mSc = null;
    private Button button;
    private ImageView text;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        sp_userinfo = getSharedPreferences("userinfo", 0);
        sp_date = getSharedPreferences("date", 0);
        C.ISUPDATE = isUpdateVersion();
        try {

            isWelcome = getIntent().getExtras().getBoolean("welcome");
        } catch (Exception e) {
            Log.e(TAG, "welcome:" + e);
        }

        if (!sp_userinfo.getString("frist", "").equals("no") || isWelcome
                || isUpdateVersion()) {
            if (sp_userinfo.getString("frist", "").equals("no")) {


                setContentView(R.layout.activity_welcome__);
                startService(new Intent(Welcome.this, MSSService.class));
                frist();
                updateVersion();
                screenwidth = sp_userinfo.getInt("screenwidth", 0);
                screenheight = sp_userinfo.getInt("screenheight", 0);

                button = (Button) findViewById(R.id.button_vp);
                text = (ImageView) findViewById(R.id.img_rain);

                mSc = new ServiceConnection() {

                    @Override
                    public void onServiceDisconnected(ComponentName name) {

                    }

                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {

                        mssservice = ((MSSService.MSSSBinder) service).getMSSService();
                        mssservice.previewStartA(C.ANIMATION_SNOW);

                        handler = new mHandler();
                        Message msg = handler.obtainMessage();
                        msg.obj = "wel";
                        handler.sendMessageDelayed(msg, 1500);
                    }
                };

                Intent service = new Intent(Welcome.this, MSSService.class);
                this.bindService(service, mSc, Context.BIND_AUTO_CREATE);


            } else {
                setContentView(R.layout.activity_welcome);
                handler = new mHandler();
                Message msg = handler.obtainMessage();
                msg.obj = "welcome";
                handler.sendMessageDelayed(msg, 100);
                vp = (ViewPager) findViewById(R.id.viewpager);

                cicre1 = (ImageView) findViewById(R.id.img_vp_page1);
                cicre2 = (ImageView) findViewById(R.id.img_vp_page2);
                cicre3 = (ImageView) findViewById(R.id.img_vp_page3);
                cicre4 = (ImageView) findViewById(R.id.img_vp_page4);

                LayoutInflater mLi = LayoutInflater.from(Welcome.this);
                View view1 = mLi.inflate(R.layout.viewpage1, null);
                View view2 = mLi.inflate(R.layout.viewpage2, null);
                View view3 = mLi.inflate(R.layout.viewpage3, null);
                View view4 = mLi.inflate(R.layout.viewpage4, null);
                views = new ArrayList<View>();
                views.add(view1);
                views.add(view2);
                views.add(view3);
                views.add(view4);

                bt = (Button) view4.findViewById(R.id.button_vp);
                bt.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (!isWelcome) {
                            startActivity(new Intent()
                                    .setClass(Welcome.this, MSS.class));
                            overridePendingTransition(android.R.anim.fade_in,
                                    android.R.anim.fade_out);
                        }
                        finish();

                    }
                });

                vp.setAdapter(new WelcomeAdapter());

                vp.setOnPageChangeListener(new OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int arg0) {

                        switch (arg0) {
                            case 0:
                                cicre1.setImageResource(R.drawable.circle_selected);
                                cicre2.setImageResource(R.drawable.circle_unselected);
                                break;
                            case 1:
                                cicre1.setImageResource(R.drawable.circle_unselected);
                                cicre2.setImageResource(R.drawable.circle_selected);
                                cicre3.setImageResource(R.drawable.circle_unselected);
                                break;
                            case 2:
                                cicre2.setImageResource(R.drawable.circle_unselected);
                                cicre3.setImageResource(R.drawable.circle_selected);
                                cicre4.setImageResource(R.drawable.circle_unselected);
                                break;
                            case 3:
                                cicre3.setImageResource(R.drawable.circle_unselected);
                                cicre4.setImageResource(R.drawable.circle_selected);
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onPageScrolled(int arg0, float arg1, int arg2) {


                    }

                    @Override
                    public void onPageScrollStateChanged(int arg0) {


                    }
                });
            }

        } else {

            setContentView(R.layout.activity_welcome_);
            ImageView imageView = (ImageView) findViewById(R.id.img_vp);
            LaunchImage launchImage = new LaunchImage(Welcome.this);
            if (launchImage.isExistImage()) {
                imageView.setImageBitmap(launchImage.getBitmap());
            }
            handler = new mHandler();
            Message msg = handler.obtainMessage();
            msg.obj = "ok";
            handler.sendMessageDelayed(msg, 1500);

        }

    }


    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    protected void onStop() {

        super.onStop();
        try {
            if (mSc != null)
                this.unbindService(mSc);
        } catch (Exception e) {
        }

    }

    class WelcomeAdapter extends PagerAdapter {

        @Override
        public int getCount() {

            return views.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {

            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(View container, int position) {

            ((ViewPager) container).addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {

            ((ViewPager) arg0).removeView(views.get(arg1));
        }

    }

    /**
     * 获取屏幕宽度
     *
     * @return 屏幕宽度
     */
    public int getScreenWidth() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return 屏幕高度（不除去状态栏）
     */
    public int getScreenHeight() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获取顶栏的高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 如果是第一次进入软件，需要获取些数据
     */
    private void frist() {

        if (!sp_userinfo.getString("frist", "").equals("no")) {

            sp_userinfo.edit().putString("frist", "no").commit();
            sp_userinfo.edit().putInt("screenwidth", getScreenWidth()).commit();
            sp_userinfo.edit().putInt("screenheight", getScreenHeight())
                    .commit();

            MyLog.delFile(); // 删除日志文件
            MyLog.i(TAG,
                    "FristAPP" + "\r\nScreenWidth:"
                            + sp_userinfo.getInt("screenwidth", 0)
                            + "\r\nScreenHeight"
                            + sp_userinfo.getInt("screenheight", 0)
                            + "\r\nModel:" + android.os.Build.MODEL
                            + "\r\nVersion:Android"
                            + android.os.Build.VERSION.RELEASE + "\r\n");

        }
    }

    private boolean isUpdateVersion() {
        if (!sp_userinfo.getString("version", "").equals(getVersionName()))
            return true;
        return false;
    }

    /**
     * 版本更新操作
     */
    private void updateVersion() {

        if (!sp_userinfo.getString("version", "").equals(getVersionName())) {

            sp_userinfo.edit().putString("version", getVersionName()).commit();
            SharedPreferences sp_setA;

            C.ISUPDATE = true;

            // 每次更新时都需要进行该操作
            // 无论是从哪个版本更新到现在的版本
            // 写入必要应用数据！

            // 事件初始数据

            // EVENT_DESKTOP
            if (!sp_date.getString("start" + C.EVENT_DESKTOP, "").equals(
                    PropertiesUtils.GetEventInfo(C.EVENT_DESKTOP, this)[0])) {
                sp_date.edit()
                        .putString(
                                "start" + C.EVENT_DESKTOP,
                                PropertiesUtils.GetEventInfo(C.EVENT_DESKTOP,
                                        this)[0]).commit();
                sp_date.edit().putString("state" + C.EVENT_DESKTOP, getString(R.string.action_started))
                        .commit();
                sp_date.edit()
                        .putString(
                                "animation" + C.EVENT_DESKTOP,
                                PropertiesUtils.getAnimationInfo(this)[C.ANIMATION_BUBBLE])
                        .commit();
                sp_date.edit()
                        .putString(
                                "end" + C.EVENT_DESKTOP,
                                PropertiesUtils.GetEventInfo(C.EVENT_DESKTOP,
                                        this)[1]).commit();
            }
            // EVENT_LOCKSCREEN
            if (!sp_date.getString("start" + C.EVENT_LOCKSCREEN, "").equals(
                    PropertiesUtils.GetEventInfo(C.EVENT_LOCKSCREEN, this)[0])) {
                sp_date.edit()
                        .putString(
                                "start" + C.EVENT_LOCKSCREEN,
                                PropertiesUtils.GetEventInfo(
                                        C.EVENT_LOCKSCREEN, this)[0]).commit();
                sp_date.edit().putString("state" + C.EVENT_LOCKSCREEN, getString(R.string.action_started))
                        .commit();
                sp_date.edit()
                        .putString(
                                "animation" + C.EVENT_LOCKSCREEN,
                                PropertiesUtils.getAnimationInfo(this)[C.ANIMATION_PICTUREWALL])
                        .commit();
                sp_date.edit()
                        .putString(
                                "end" + C.EVENT_LOCKSCREEN,
                                PropertiesUtils.GetEventInfo(
                                        C.EVENT_LOCKSCREEN, this)[1]).commit();
                // 开启锁屏动画，幻灯片特有设置（仅播放一张图片）
                sp_setA = getSharedPreferences(
                        PropertiesUtils.GetEventInfo(C.EVENT_LOCKSCREEN, this)[0]
                                + PropertiesUtils
                                .getAnimationInfo(Welcome.this)[C.ANIMATION_PICTUREWALL],
                        0);
                sp_setA.edit().putBoolean("onlyone", true).commit();
            }

            // EventCall
            if (!sp_date.getString("start" + C.EVENT_CALL, "").equals(
                    PropertiesUtils.GetEventInfo(C.EVENT_CALL, this)[0])) {
                sp_date.edit()
                        .putString(
                                "start" + C.EVENT_CALL,
                                PropertiesUtils
                                        .GetEventInfo(C.EVENT_CALL, this)[0])
                        .commit();
                sp_date.edit().putString("state" + C.EVENT_CALL, getString(R.string.action_started))
                        .commit();
                sp_date.edit()
                        .putString("animation" + C.EVENT_CALL,
                                PropertiesUtils.getAnimationInfo(this)[1])
                        .commit();
                sp_date.edit()
                        .putString(
                                "end" + C.EVENT_CALL,
                                PropertiesUtils
                                        .GetEventInfo(C.EVENT_CALL, this)[1])
                        .commit();
            }
            // EventSMS
            if (!sp_date.getString("start" + C.EVENT_SMS, "").equals(
                    PropertiesUtils.GetEventInfo(C.EVENT_SMS, this)[0])) {
                sp_date.edit()
                        .putString(
                                "start" + C.EVENT_SMS,
                                PropertiesUtils.GetEventInfo(C.EVENT_SMS, this)[0])
                        .commit();
                sp_date.edit().putString("state" + C.EVENT_SMS, getString(R.string.action_stoped)).commit();
                sp_date.edit()
                        .putString(
                                "animation" + C.EVENT_SMS,
                                PropertiesUtils.getAnimationInfo(this)[C.ANIMATION_STARSHINE])
                        .commit();
                sp_date.edit()
                        .putString(
                                "end" + C.EVENT_SMS,
                                PropertiesUtils.GetEventInfo(C.EVENT_SMS, this)[1])
                        .commit();
            }
            // EventCharing
            if (!sp_date.getString("start" + C.EVENT_CHARGING, "").equals(
                    PropertiesUtils.GetEventInfo(C.EVENT_CHARGING, this)[0])) {
                sp_date.edit()
                        .putString(
                                "start" + C.EVENT_CHARGING,
                                PropertiesUtils.GetEventInfo(C.EVENT_CHARGING,
                                        this)[0]).commit();
                sp_date.edit().putString("state" + C.EVENT_CHARGING, getString(R.string.action_stoped))
                        .commit();
                sp_date.edit()
                        .putString(
                                "animation" + C.EVENT_CHARGING,
                                PropertiesUtils.getAnimationInfo(this)[C.ANIMATION_STARSHINE])
                        .commit();
                sp_date.edit()
                        .putString(
                                "end" + C.EVENT_CHARGING,
                                PropertiesUtils.GetEventInfo(C.EVENT_CHARGING,
                                        this)[2]).commit();
            }

            // 往存储卡中存入幻灯片所需要的图片
            // 仅在该目录为空的时候执行

            if (PictureWall.getListPath(PictureWall.path).length == 0) {
                Bitmap bitmap0 = ((BitmapDrawable) getResources().getDrawable(
                        R.drawable.picturewall_0)).getBitmap();
                Bitmap bitmap1 = ((BitmapDrawable) getResources().getDrawable(
                        R.drawable.picturewall_1)).getBitmap();
                try {
                    saveImage(bitmap0, "picturewall_0.png");
                    saveImage(bitmap1, "picturewall_1.png");
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(
                            Welcome.this,
                            getString(R.string.tip_msg_sd),
                            Toast.LENGTH_LONG).show();
                }
            }

            // 动画激活信息

            SharedPreferences animation = getSharedPreferences("animation",MODE_PRIVATE);
            animation.edit().putBoolean(PropertiesUtils.getAnimationInfo(getApplicationContext())[C.ANIMATION_BUBBLE], true).apply();
            animation.edit().putBoolean(PropertiesUtils.getAnimationInfo(getApplicationContext())[C.ANIMATION_PICTUREWALL],true).apply();
            animation.edit().putBoolean(PropertiesUtils.getAnimationInfo(getApplicationContext())[C.ANIMATION_STARSHINE],true).apply();
            animation.edit().putBoolean(PropertiesUtils.getAnimationInfo(getApplicationContext())[C.ANIMATION_RAIN], true).apply();
            MyLog.i(TAG, "update" + getVersionName());
        }
    }

    /**
     * 获取当前应用版本号
     *
     * @return
     */
    private String getVersionName() {

        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {

            e.printStackTrace();
            MyLog.e(TAG, e);
        }
        String version = packInfo.versionName;

        return version;
    }

    /**
     * 保存图片到指定目录中
     *
     * @param bitmap
     * @param imageName
     * @throws Exception
     */
    public void saveImage(Bitmap bitmap, String imageName) throws Exception {
        String filePath = getPath();
        File file = new File(filePath, imageName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPath() {
        String path = Environment.getExternalStorageDirectory().getPath()
                + "/MSShow/PictureWall/Img/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    @SuppressLint("HandlerLeak")
    class mHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            startService(new Intent(Welcome.this, MSSService.class));
            if (msg.obj.toString().equals("ok")) {

                frist();
                updateVersion();
                screenwidth = sp_userinfo.getInt("screenwidth", 0);
                screenheight = sp_userinfo.getInt("screenheight", 0);

                Log.i("ScreenWidth", screenwidth + "");
                Log.i("ScreenHeight", screenheight + "");

                startActivity(new Intent()
                        .setClass(Welcome.this, MSS.class));

                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                finish();
            } else if (msg.obj.toString().equals("welcome")) {

                frist();
                updateVersion();
                screenwidth = sp_userinfo.getInt("screenwidth", 0);
                screenheight = sp_userinfo.getInt("screenheight", 0);

                Log.i("ScreenWidth", screenwidth + "");
                Log.i("ScreenHeight", screenheight + "");

                // mydialog.dismiss();
            } else if (msg.obj.toString().equals("wel")) {

                if (text != null) {
                    text.setVisibility(View.VISIBLE);
                    Animation animation = new ScaleAnimation(0.2f, 1f, 0.2f, 1f,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    animation.setDuration(2000);
                    text.startAnimation(animation);
                    Message msg1 = this.obtainMessage();
                    msg1.obj = "wel_";
                    handler.sendMessageDelayed(msg1, 2500);
                }
            } else if (msg.obj.toString().equals("wel_")) {

                if (button != null) {

                    button.setVisibility(View.VISIBLE);

                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mssservice.previewStopA();
                            if (!isWelcome) {
                                startActivity(new Intent()
                                        .setClass(Welcome.this, MSS.class));

                                overridePendingTransition(android.R.anim.fade_in,
                                        android.R.anim.fade_out);
                            }
                            finish();
                        }
                    });

                }
            }
        }

    }
}
