package com.jiusg.mainscreenshow.animation.snow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by xgt on 2015/9/15.
 */

public class Snow {
    //坐标
    public int x;
    public int y;
    //宽高
    private int width;
    private int height;
    //y轴速度
    private int y_speed;
    //生命状态
    private boolean isAlive;
    // 旋转时间间隔
    private long rotateTime;
    // 透明度变化时间间隔
    private long alphaTime;
    // 大小变化时间间隔
    private long scaleTime;
    //飘动时间间隔
    private long curveTime;
    // 角度
    private int degree;
    // 透明度
    private int alpha;
    //初始化图片宽高值
    private int w_h;
    // 当前宽高
    private int cw;
    private int ch;
    //图片资源
    public static Bitmap bit = null;
    double num;//控制大小变化开始的系数
    //波动运动参数
    private double t;
    private double angle;
    private double m;
    private double A;
    //运动形式
    private int moveType;
    //矩阵 画笔
    Matrix matrix;
    Paint paint;
    private SnowSetting setting;
    public static int screenWidth;
    public static int screenHeight;

    public Snow(int x, int y , boolean isAlive , int y_speed , int moveType , SnowSetting setting) {
        this.x = x;
        this.y = y;
        this.y_speed = y_speed;
        this.moveType = moveType;
        this.width = Snow.bit.getWidth();
        this.height = Snow.bit.getHeight();
        this.isAlive = isAlive;
        this.setting = setting;
        this.alphaTime = System.currentTimeMillis();
        this.rotateTime = System.currentTimeMillis();
        this.scaleTime = System.currentTimeMillis();
        this.curveTime = System.currentTimeMillis();
        this.degree = 0;
        this.matrix = new Matrix();
        this.paint = new Paint();
        this.alpha = 255;
        this.t = 0;
        this.m = 0;
        this.A = 5;
        this.angle = 2 * Math.PI;
        this.num = Math.random();
        //初始化图片宽高
        this.w_h = (int)(width * Math.random());
        this.cw = w_h;
        this.ch = cw;
        if(w_h > 0) {
            float w = ((float) w_h) / width;
            float h = ((float) w_h) / height;
            matrix.postScale(w, h, x, y);
        }
    }

    public void drawSnow(Canvas canvas){
            canvas.drawBitmap(Snow.bit, matrix, paint);
    }

    public void countSnow(){
        if(isAlive){
            snowRotate();
            snowAlphaChange(paint);
            move(this.moveType);
        }else{
            SnowAnimation.snow_list.remove(this);
        }
    }
    public void snowMove_1(){
        y += y_speed;
        if (y > screenHeight + bit.getHeight()) {
            this.isAlive = false;
        }
    }

    public void snowMove_2(){
        //飘动实现
        if(w_h < (width - width / 2)){
            y += y_speed / 2;
            if (y > screenHeight + bit.getHeight()) {
                this.isAlive = false;
            }
            if (System.currentTimeMillis() - curveTime > 20) {
                x += A * Math.cos(angle * t);
                t += 0.01;
                curveTime = System.currentTimeMillis();
            }
        }else{
            y += y_speed;
            if (y > screenHeight + bit.getHeight()) {
                this.isAlive = false;
            }
        }
    }
    public void move(int type){
        switch (type){
            case 1:
                snowMove_1();
                break;
            case 2:
                snowMove_2();
                break;
            default:
                break;
        }
    }
    /*
 * ͸透明度变化
 *
 */
    public void snowAlphaChange(Paint paint) {
        if (y > screenHeight -  screenHeight / 5) {
            if (System.currentTimeMillis() - alphaTime >= 10) {
                alpha -= 5;
                if (alpha >= 0) {
                    paint.setAlpha(alpha);
                }
                alphaTime = System.currentTimeMillis();
            }
        }
    }
    /*
     * 图片旋转
     *
     */
    public void snowRotate() {
        if (System.currentTimeMillis() - rotateTime > 10) {
            degree += 1;
            matrix.reset();
            move(this.moveType);
            matrix.setTranslate((float) x, (float) y);
            matrix.preRotate(degree, cw / 2, ch / 2);
            snowScaleChange();
            rotateTime = System.currentTimeMillis();
        }
    }
    /*
     * 大小变化
     *
     */
    public void snowScaleChange() {
        if (System.currentTimeMillis() - scaleTime > 100
                && y > ( screenHeight / 5) * (num * 4)) {
            //保证cw ch > 0
            if(cw > 2 && ch > 2){
                cw -= 2;
                ch -= 2;
            }
            scaleTime = System.currentTimeMillis();
        }
        float w = ((float) cw) / width;
        float h = ((float) ch) / height;
        matrix.postScale(w, h, (float)x, (float)y);
    }
}