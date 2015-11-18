package com.jiusg.mainscreenshow.animation.rain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jiusg.mainscreenshow.R;

/**
 * Created by Administrator on 2015/6/23.
 */
public class Rain {

    private static Bitmap bit = null;
    private Bitmap bitmap = null;
    private int x = -1;
    private int y = -1;
    private int speed = -1;
    private float scale = 1;
    private int alpha = -1;
    public static int screenheight;
    public static int screenwidth;
    private RainSetting setting;

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }


    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public int getY() {
        return y;

    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    public Rain(Context context, RainSetting setting) {
        initBit(context, setting);
        this.setting = setting;
        this.bitmap = Rain.bit;
        this.speed = 1;
        this.scale = getRandom();
        this.speed = 5;
        this.alpha = 1;
        this.bitmap = Bitmap.createScaledBitmap(this.bitmap, (int) (this.bitmap.getWidth() * scale), (int) (this.bitmap.getHeight() * scale), true);
        this.x = (int) (screenwidth * Math.random()) - (this.bitmap.getWidth() / 2);
        this.y = 0 - this.bitmap.getHeight() - (int) ((screenheight * 2) * Math.random());
    }

    public void countNext(Context context) {

        this.y = this.y + speed;
        if (this.y > screenheight) {
            this.scale = getRandom();
            if (setting.getStyle() != setting.STYLE_1)
                initBit(context, setting);
            this.bitmap = Rain.bit;
            this.bitmap = Bitmap.createScaledBitmap(this.bitmap, (int) (this.bitmap.getWidth() * scale), (int) (this.bitmap.getHeight() * scale), true);
            this.x = (int) (screenwidth * Math.random()) - (this.bitmap.getWidth() / 2);
            this.y = 0 - this.bitmap.getHeight() - (int) (screenheight * Math.random());
        }
    }


    public float getRandom() {
        float scale = (float) Math.random();
        if (scale < 0.2 || scale > 0.8)
            return getRandom();
        else
            return scale;
    }

    public void initBit(Context context, RainSetting setting) {


        if (setting.getStyle() == setting.STYLE_1)
            bit = BitmapFactory.decodeResource(context.getResources(), R.drawable.rain_1);
        else if (setting.getStyle() == setting.STYLE_2) {
            float x = (float) Math.random();
            if (x < 0.3)
                bit = BitmapFactory.decodeResource(context.getResources(), R.drawable.rain_3);
            else if (x >= 0.3 && x < 0.6)
                bit = BitmapFactory.decodeResource(context.getResources(), R.drawable.rain_4);
            else
                bit = BitmapFactory.decodeResource(context.getResources(), R.drawable.rain_2);
        } else
            bit = BitmapFactory.decodeResource(context.getResources(), R.drawable.rain_1);

    }
}
