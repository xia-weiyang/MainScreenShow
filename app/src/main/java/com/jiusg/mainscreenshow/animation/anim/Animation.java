package com.jiusg.mainscreenshow.animation.anim;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Log;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.tools.PropertiesUtils;
import com.jiusg.mainscreenshow.ui.DesktopSet;

/**
 * Created by zk on 2015/10/4.
 */
public abstract class Animation {

    protected Context context = null;
    public int speed = 60;  // 每隔多少毫秒计算一次
    protected Paint backgroundPaint = null;
    protected Paint paint = null;
    private SharedPreferences sp = null;
    protected int screenheight;
    protected int screenwidth;

    protected Bitmap background = null;

    protected int animation = -1;

    protected SharedPreferences settingInfo = null;

    public int event = -1;

    public Animation(Context context, int event) {
        this.context = context;
        this.event = event;
        animation = getAnimation();
        initAnimation();
    }


    /**
     * 初始化
     */
    public void initAnimation() {
        // 初始画笔
        backgroundPaint = new Paint();
        paint = new Paint();
        backgroundPaint.setAlpha(255);
        backgroundPaint.setAntiAlias(false);
        backgroundPaint.setDither(false);
        backgroundPaint.setFilterBitmap(true);
        backgroundPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        paint.setAlpha(255);
        paint.setAntiAlias(false);
        paint.setDither(false);
        paint.setFilterBitmap(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        // 初始屏幕信息
        SharedPreferences sp_userinfo = context.getSharedPreferences("userinfo", 0);
        screenheight = sp_userinfo.getInt("screenheight", 0);
        screenwidth = sp_userinfo.getInt("screenwidth", 0);

        // 初始设置信息
        Log.i("animation", "" + animation);
        sp = context.getSharedPreferences("date", 0);
        settingInfo = context.getSharedPreferences(sp.getString("start" + event, "")
                + PropertiesUtils.getAnimationInfo(context)[animation], 0);

        if (event == C.EVENT_DESKTOP)
            setBackground();

    }

    protected void setBackground() {
        if(getBackground()!=-1) {
            if(sp.getString("background" + event, "默认").equals("自定义")) {
                background = BitmapFactory.decodeFile(DesktopSet.backPath+DesktopSet.backName);
                background = Bitmap.createScaledBitmap(background, screenwidth, screenheight, false);
            }else{
                background = BitmapFactory.decodeResource(context.getResources(), getBackground());
                background = Bitmap.createScaledBitmap(background, screenwidth, screenheight, false);
            }
        }
    }

    /**
     *
     * @return 没有背景返回-1
     */
    public abstract int getBackground();

    /**
     * 返回某一具体动画
     *
     * @return
     */
    public abstract int getAnimation();

    /**
     * 绘图
     *
     * @param canvas
     */
    public abstract void draw(Canvas canvas);

    /**
     * 计算
     */
    public abstract void count();
}
