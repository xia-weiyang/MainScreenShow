package com.jiusg.mainscreenshow.animation.snow;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.animation.anim.Animation;
import com.jiusg.mainscreenshow.base.C;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Administrator on 2015/10/11.
 */
public class SnowAnimation extends Animation {
    private Context context = null;
    private SnowSetting snowSetting = null;
    private int snow_x;
    private int snow_y;
    private Random rd;
    private long time;
    public static ArrayList<Snow> snow_list = null;

    @Override
    public void draw(Canvas canvas) {
        if (event == C.EVENT_DESKTOP) {
            canvas.drawBitmap(background, 0, 0, backgroundPaint);
        } else
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        onDraw(canvas);
    }

    @Override
    public int getAnimation() {
        return C.ANIMATION_SNOW;
    }

    @Override
    public void count() {
        if (System.currentTimeMillis() - time >= 300) {
            createSnow1();
            time = System.currentTimeMillis();
        }
        for (int i = 0; i < snow_list.size(); i++) {
            snow_list.get(i).countSnow();
        }

    }

    @Override
    public int getBackground() {
        return R.drawable.preview_snow_background;
    }

    public SnowAnimation(Context context, int event) {
        super(context, event);
        this.context = context;
        init();
    }

    public void init() {
        snow_list = new ArrayList<>();
        snowSetting = loadSetting();
        Snow.screenWidth = screenwidth;
        Snow.screenHeight = screenheight;
        time = System.currentTimeMillis();
        rd = new Random(System.currentTimeMillis());
        SnowAnimation.chooseType(context, snowSetting.getStyle());
        paint.setAlpha((int)(snowSetting.getAlpha()*255));
        speed = 20;
    }

    public SnowSetting loadSetting() {
        snowSetting = new SnowSetting();
        snowSetting.setStyle(getStyle(settingInfo.getString("style", context.getString(R.string.snow_style_1))));
        snowSetting.setAmount(getAmount(settingInfo.getString("amount", context.getString(R.string.snow_amount_general))));
        snowSetting.setAlpha(settingInfo.getFloat("alpha", 0.9f));
        snowSetting.setSpeed(settingInfo.getInt("speed", 5));
        return snowSetting;
    }

    public void onDraw(Canvas canvas) {
        for (int i = 0; i < snow_list.size(); i++) {
            paint = snow_list.get(i).paint;
            snow_list.get(i).drawSnow(canvas);
        }
    }

    public void createSnow1() {
        for (int i = 0; i < snowSetting.getAmount(); i++) {
            snow_x = rd.nextInt(Snow.screenWidth - Snow.bit.getWidth());
            snow_y = -Snow.screenHeight / 5;
            int y_speed = Snow.screenHeight / (300+40*snowSetting.getSpeed());
            int moveType = 1;
            if (i == 0) {
                moveType = 2;
            }
            Snow snow = new Snow(snow_x, snow_y, true, y_speed, moveType, snowSetting);
            snow.matrix.setTranslate(snow_x, snow_y);
            SnowAnimation.snow_list.add(snow);
        }
    }

    public static void chooseType(Context context, int type) {
        switch (type) {
            case 1:
                Snow.bit = BitmapFactory.decodeResource(context.getResources(), R.drawable.snow_1);
                break;
            case 2:
                Snow.bit = BitmapFactory.decodeResource(context.getResources(), R.drawable.snow_2);
                break;
            case 3:
                Snow.bit = BitmapFactory.decodeResource(context.getResources(), R.drawable.snow_3);
                break;
            case 4:
                Snow.bit = BitmapFactory.decodeResource(context.getResources(), R.drawable.snow_4);
                break;
            default:
                Snow.bit = BitmapFactory.decodeResource(context.getResources(), R.drawable.snow_1);
                break;
        }
    }

    public int getAmount(String s) {

        if (s.equals(context.getString(R.string.snow_amount_less))) {
            return snowSetting.AMOUNT_LESS;
        } else if (s.equals(context.getString(R.string.snow_amount_general))) {
            return snowSetting.AMOUNT_GENERAL;
        } else if (s.equals(context.getString(R.string.snow_amount_munch))) {
            return snowSetting.AMOUNT_MUNCH;
        } else {
            return snowSetting.AMOUNT_GENERAL;
        }
    }


    public int getStyle(String s) {
        if (s.equals(context.getString(R.string.snow_style_1)))
            return snowSetting.STYLE_1;
        else if (s.equals(context.getString(R.string.snow_style_2)))
            return snowSetting.STYLE_2;
        else if (s.equals(context.getString(R.string.snow_style_3)))
            return snowSetting.STYLE_3;
        else if (s.equals(context.getString(R.string.snow_style_4)))
            return snowSetting.STYLE_4;
        else
            return snowSetting.STYLE_1;
    }
}
