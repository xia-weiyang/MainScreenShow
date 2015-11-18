package com.jiusg.mainscreenshow.animation.rain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.animation.anim.Animation;
import com.jiusg.mainscreenshow.base.C;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/7.
 */
public class RainAnimation extends Animation {

    private Context context = null;
    private RainSetting rs = null;
    private ArrayList<Rain> rains = null;

    public RainAnimation(Context context, int event) {
        super(context, event);
        this.context = context;
        init();
    }

    private void init() {
        rs = loadSetting();
        rains = new ArrayList<Rain>();
        Rain.screenheight = screenheight;
        Rain.screenwidth = screenwidth;
        paint.setAlpha((int)(rs.getAlpha()*255));
        initalRain();
    }

    private RainSetting loadSetting() {
        rs = new RainSetting();
        speed = 30;
        rs.setAlpha(settingInfo.getFloat("alpha", 0.9f));
        rs.setSpeed(settingInfo.getInt("speed", 5));
        rs.setAmount(getAmount(settingInfo.getString("amount", context.getString(R.string.rain_amount_very_munch))));
        rs.setStyle(getStyle(settingInfo.getString("style", context.getString(R.string.rain_style_1))));
        return rs;
    }

    public void initalRain() {

        for (int i = 0; i < rs.getAmount(); i++) {

            Rain rain = new Rain(context,rs);
            rain.setSpeed(rain.getSpeed()+(5*rs.getSpeed()));
            rains.add(rain);
        }
    }

    public void onDraw(Canvas canvas) {

        for (int i = 0; i < rains.size(); i++) {

            canvas.drawBitmap(rains.get(i).getBitmap(), rains.get(i).getX(), rains.get(i).getY(), paint);
        }
    }

    @Override
    public int getBackground() {
        return R.drawable.preview_rain_background;
    }

    @Override
    public int getAnimation() {
        return C.ANIMATION_RAIN;
    }

    @Override
    public void draw(Canvas canvas) {
        if (event == C.EVENT_DESKTOP)
            canvas.drawBitmap(background, 0, 0, backgroundPaint);
        else
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        onDraw(canvas);
    }

    @Override
    public void count() {
        for (int i = 0; i < rains.size(); i++) {
            rains.get(i).countNext(context);
        }
    }


    public int getAmount(String s) {

        if (s.equals(context.getString(R.string.rain_amount_less))) {
            return rs.AMOUNT_LESS;
        } else if (s.equals(context.getString(R.string.rain_amount_general))) {
            return rs.AMOUNT_GENERAL;
        } else if (s.equals(context.getString(R.string.rain_amount_munch))) {
            return rs.AMOUNT_MUNCH;
        } else if (s.equals(context.getString(R.string.rain_amount_very_munch))) {
            return rs.AMOUNT_VERY_MUNCH;
        } else {
            return rs.AMOUNT_GENERAL;
        }
    }

    public int getStyle(String s) {
        if (s.equals(context.getString(R.string.rain_style_1)))
            return rs.STYLE_1;
        else if (s.equals(context.getString(R.string.rain_style_2)))
            return rs.STYLE_2;
        else
            return rs.STYLE_1;
    }
}
