package com.jiusg.mainscreenshow.animation.rain;

/**
 * Created by Administrator on 2015/6/26.
 */
public class RainSetting {

    private float alpha;
    private int speed;
    private int amount;
    private int style;
    public final int AMOUNT_VERY_MUNCH = 500;
    public final int AMOUNT_MUNCH = 350;
    public final int AMOUNT_GENERAL = 200;
    public final int AMOUNT_LESS = 100;
    public final int STYLE_1 = 1;
    public final int STYLE_2 = 2;

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
