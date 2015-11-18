package com.jiusg.mainscreenshow.animation.picturewall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.animation.anim.Animation;
import com.jiusg.mainscreenshow.base.C;

/**
 * Created by zk on 2015/10/7.
 */
public class PictureWallAnimation extends Animation {

    private Bitmap background = null;
    private Context context = null;
    private PictureWallSetting ps = null;
    public boolean isFrame = true;
    private int milliSecond = 0;
    private float scale = 0;
    private PictureWall pw = null;
    private int count = 0;
    private int alpha = 255;
    private int period = 50;
    private boolean onlyOne = false; // 只播放一张动画
    private int BITMAP_TYPE = PictureWall.BITMAP_TYPE_RANDOM;

    public PictureWallAnimation(Context context, int event) {
        super(context, event);
        this.context = context;
        init();
    }

    private void init(){
        ps = loadSetting();
        speed = 100;
        alpha = ps.getAlpha();
        // 这里除以0.8是因为有0.2的时间用来了渐变，目的是让时间还原
        period = (int) ((float)ps.getPeriod()/0.8);
        onlyOne = ps.isOnlyOne();
        scale = ps.getScaleV();
        BITMAP_TYPE = ps.getBITMAP_TYPE();
        paint.setAlpha(alpha);
        pw = new PictureWall(screenwidth, screenheight, period, alpha, BITMAP_TYPE, scale);
        if (onlyOne) {
            pw.setRatio(pw.getPeriod() / 3 * 2);
            pw.setRationBL(1.1f);
        }
    }

    private PictureWallSetting loadSetting(){
        ps = new PictureWallSetting();
        ps.setAlpha(getAlpha());
        ps.setBITMAP_TYPE(getBitmapType());
        ps.setOnlyOne(settingInfo.getBoolean("onlyone", false));
        ps.setPeriod(getPeriod());
        ps.setRecourse(settingInfo.getBoolean("recourse", false));
        ps.setScaleV(getScaleV());
        return ps;
    }

    /**
     * 具体绘制的方法
     *
     * @param mCanvas
     */
    private void onDraw(final Canvas mCanvas) {
        pw.getScale();
        // 这里进行透明度判断，主要是解决画图顺序的问题
        if (pw.getAlphaB1() > pw.getAlphaB2()) {
            if (pw.isBitmap1()) {
                paint.setAlpha(pw.getAlphaB1());
                if (onlyOne)
                    if (count >= period)
                        paint.setAlpha(0);
                mCanvas.drawBitmap(pw.getBitmap1(), pw.getPositionB1X(),
                        pw.getPositionB1Y(), paint);
            }
            if (pw.isBitmap2()) {
                paint.setAlpha(pw.getAlphaB2());
                if (onlyOne)
                    paint.setAlpha(0);
                mCanvas.drawBitmap(pw.getBitmap2(), pw.getPositionB2X(),
                        pw.getPositionB2Y(), paint);
            }
        } else {
            if (pw.isBitmap2()) {
                paint.setAlpha(pw.getAlphaB2());
                if (onlyOne)
                    paint.setAlpha(0);
                mCanvas.drawBitmap(pw.getBitmap2(), pw.getPositionB2X(),
                        pw.getPositionB2Y(), paint);
            }
            if (pw.isBitmap1()) {
                paint.setAlpha(pw.getAlphaB1());
                if (onlyOne)
                    if (count >= period)
                        paint.setAlpha(0);
                mCanvas.drawBitmap(pw.getBitmap1(), pw.getPositionB1X(),
                        pw.getPositionB1Y(), paint);
            }
        }

    }

    @Override
    public int getBackground() {
        return -1;
    }

    @Override
    public int getAnimation() {
        return C.ANIMATION_PICTUREWALL;
    }

    @Override
    public void draw(Canvas canvas) {
        if (onlyOne) {
            if (count < period) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                onDraw(canvas);
            }
        }else {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            onDraw(canvas);
        }

    }

    @Override
    public void count() {
        if (pw.isBitmap1())
            pw.setTimeB1(pw.getTimeB1() + 1);
        if (pw.isBitmap2())
            pw.setTimeB2(pw.getTimeB2() + 1);
        count++;
    }

    private int getBitmapType(){
        if(settingInfo.getString("bitmap_type", context.getString(R.string.pictureWall_play_random)).equals(context.getString(R.string.pictureWall_play_random)))
            return PictureWall.BITMAP_TYPE_RANDOM;
        else if(settingInfo.getString("bitmap_type",  context.getString(R.string.pictureWall_play_random)).equals(context.getString(R.string.pictureWall_play_order)))
            return PictureWall.BITMAP_TYPE_ORDER;
        return PictureWall.BITMAP_TYPE_RANDOM;
    }
    private int getPeriod(){
        return (int) (settingInfo.getFloat("period",4.0f)*10);
    }
    private int getAlpha(){
        return (int) (settingInfo.getFloat("alpha",0.7f)*255);
    }
    private float getScaleV(){
        int value = settingInfo.getInt("zoomin", 0);
        float scale = PictureWall.SCALE_BASE;
        switch (value) {
            case -5:
                return scale/1.001f;
            case -4:
                return scale/1.0008f;
            case -3:
                return scale/1.0006f;
            case -2:
                return scale/1.0004f;
            case -1:
                return scale/1.0002f;
            case 0:
                return scale;
            case 1:
                return scale*1.001f;
            case 2:
                return scale*1.002f;
            case 3:
                return scale*1.003f;
            case 4:
                return scale*1.004f;
            case 5:
                return scale*1.005f;
            default:
                return scale;
        }
    }
}
