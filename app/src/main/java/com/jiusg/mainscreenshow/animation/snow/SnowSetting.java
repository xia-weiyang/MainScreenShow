package com.jiusg.mainscreenshow.animation.snow;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/11.
 */
public class SnowSetting{
    private float alpha;
    private int speed;
    private int amount;
    private int style;
    public final int STYLE_1 = 1;
    public final int STYLE_2 = 2;
    public final int STYLE_3 = 3;
    public final int STYLE_4 = 4;
    public final int AMOUNT_MUNCH = 5;
    public final int AMOUNT_GENERAL = 4;
    public final int AMOUNT_LESS = 3;
    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}