package com.jiusg.mainscreenshow.animation.bubble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.animation.anim.Animation;
import com.jiusg.mainscreenshow.base.C;

/**
 * Created by Administrator on 2015/10/4.
 */
public class BubbleAnimation extends Animation {

    private BubbleSetting bs = null;
    private int px; // 气泡的实际大小
    private BubbleCount cb = null;
    private Bubble[] bubble = null;

    public BubbleAnimation(Context context, int event) {
        super(context, event);
        init();
    }


    public void init() {
        bs = loadSetting();
        paint.setAlpha((int) (255 * bs.getAlpha()));
        getPX();
        cb = new BubbleCount(screenheight, screenwidth, context, bs, px);
        speed = 50 - (bs.getBubbleSpeed() * 4);
        Bubble();

    }

    /**
     * 加载气泡设置信息
     *
     * @return
     */
    private BubbleSetting loadSetting() {
        BubbleSetting bs = new BubbleSetting();
        bs.setChangeColor(settingInfo.getBoolean("color", true));
        bs.setShadow(settingInfo.getBoolean("shadow", true));
        bs.setSize(settingInfo.getString("size", context.getResources().getString(R.string.bubble_size_big)));
        bs.setNumber(settingInfo.getInt("number", 7));
        bs.setAlpha(settingInfo.getFloat("alpha", 0.9f));
        bs.setBubbleSpeed(settingInfo.getInt("speed", 5));
        return bs;
    }

    @Override
    public int getBackground() {
        return R.drawable.preview_bubble_background;
    }

    @Override
    public int getAnimation() {
        return C.ANIMATION_BUBBLE;
    }

    @Override
    public void draw(Canvas canvas) {
        if (event == C.EVENT_DESKTOP)
            canvas.drawBitmap(background, 0, 0, backgroundPaint);
        else
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (int i = 0; i < bubble.length; i++) {
            DrawImage(canvas, bubble[i]);
        }

    }

    @Override
    public void count() {
        cb.GetNextBubble(bubble);
    }


    private void DrawImage(Canvas canvas, Bubble b) {


//            Rect rect = new Rect(222,222,555,555);
//            canvas.drawBitmap(background,rect,rect,mPaint);
        canvas.drawBitmap(b.getBubble(), b.getX(), b.getY(), paint);
        if (bs.isShadow()) {
            // if (bs.getSize().equals("随机")) {
            // bubble_background = createBitmapBySize(
            // ((BitmapDrawable) getResources().getDrawable(
            // R.drawable.bubble_background)).getBitmap(), b);
            // }
            canvas.drawBitmap(b.getBubbleShadow(),
                    (float) (b.getX() + (0.1 * b.getR())),
                    (float) (b.getY() + (0.3 * b.getR())), paint);
        }

    }

    /**
     * 得到bubble的实际大小px
     */
    private void getPX() {

        Bitmap b = ((BitmapDrawable) context.getResources().getDrawable(
                R.drawable.bubble_blue)).getBitmap();
        px = b.getWidth();

    }


    /**
     * 实例化所有的Bubble 给每个bubble设定初始值，并存入bubble数组中
     */
    private void Bubble() {

        int j = 1;
        bubble = new Bubble[bs.getNumber()];
        for (int i = 0; i < bubble.length; i++) {

            j = -j;
            Bubble b = new Bubble();
            if (!this.bs.getSize().equals(context.getResources().getString(R.string.bubble_size_random))) {
                b.setSize(this.bs.getSize());
            } else {
                b.setSize(getRandomSize());
            }
            b.setBubble(Getcolor(b, i + 1));
            b.setBubbleShadow(createBitmapBySize(
                    ((BitmapDrawable) context.getResources().getDrawable(
                            R.drawable.bubble_background)).getBitmap(), b));
            BubbleSpeed bs = new BubbleSpeed();
            bs.setX((float) (cb.GetSpeed() * 0.1 * i * j));
            bs.setY((float) (-cb.GetSpeed() * (1.5 - (i * 0.1))));
            b.setSpeed(bs);
            b.setIsArea(false);
            b.setIsNoCD(false);
            b.setR(b.getBubble().getWidth() / 2);
            b.setX(screenwidth / 2 - b.getR());
            b.setY(screenheight);
            // bubble_9.setCount((int) ((int) 400 * Math.random()));
            b.setCount(400 - i * 40);
            b.setColor((int) ((int) 500 * Math.random()));
            b.setFalg(true);
            bubble[i] = b;
        }

    }


    private String getRandomSize() {

        int i = (int) ((int) 10 * Math.random());

        if (i > 0 && i <= 2)
            return context.getResources().getString(R.string.bubble_size_muchSmall);
        else if (i > 2 && i <= 4)
            return context.getResources().getString(R.string.bubble_size_small);
        else if (i > 4 && i <= 6)
            return context.getResources().getString(R.string.bubble_size_big);
        else if (i > 6 && i <= 8)
            return context.getResources().getString(R.string.bubble_size_muchBig);
        else
            return getRandomSize();
    }

    /**
     * 随机获取颜色
     *
     * @param bubble
     * @return
     */
    public Bitmap Getcolor(Bubble bubble, int num) {

        Bitmap b = null;
        int i = 0;
        if (bs.isChangeColor()) {
            while (i > 10 | i == 0) {

                i = (int) ((int) 10 * Math.random());
            }
        } else {
            i = num;

        }
        switch (i) {
            case 1:
                b = ((BitmapDrawable) context.getResources().getDrawable(
                        R.drawable.bubble_blue)).getBitmap();
                break;
            case 2:
                b = ((BitmapDrawable) context.getResources().getDrawable(
                        R.drawable.bubble_green)).getBitmap();
                break;
            case 3:
                b = ((BitmapDrawable) context.getResources().getDrawable(
                        R.drawable.bubble_pink)).getBitmap();
                break;
            case 4:
                b = ((BitmapDrawable) context.getResources().getDrawable(
                        R.drawable.bubble_purple)).getBitmap();
                break;
            case 5:
                b = ((BitmapDrawable) context.getResources().getDrawable(
                        R.drawable.bubble_red)).getBitmap();
                break;
            case 6:
                b = ((BitmapDrawable) context.getResources().getDrawable(
                        R.drawable.bubble_yellow)).getBitmap();
                break;
            case 7:
                b = ((BitmapDrawable) context.getResources().getDrawable(
                        R.drawable.bubble_grey)).getBitmap();
                break;
            case 8:
                b = ((BitmapDrawable) context.getResources().getDrawable(
                        R.drawable.bubble_qing)).getBitmap();
                break;
            case 9:
                b = ((BitmapDrawable) context.getResources().getDrawable(
                        R.drawable.bubble_qianblue)).getBitmap();
                break;
            case 10:
                b = ((BitmapDrawable) context.getResources().getDrawable(
                        R.drawable.bubble_qianpink)).getBitmap();
                break;
            default:
                b = ((BitmapDrawable) context.getResources().getDrawable(
                        R.drawable.bubble_blue)).getBitmap();
                break;
        }

        b = createBitmapBySize(b, bubble);

        return b;
    }

    /**
     * 得到具体大小的Bitmap
     *
     * @param bitmap
     * @return
     */
    public Bitmap createBitmapBySize(Bitmap bitmap, Bubble b) {

        int size;
        if (b != null) {
            size = GetSizeValue(b.getSize());
        } else {
            size = GetSizeValue(bs.getSize());
        }
        return Bitmap.createScaledBitmap(bitmap, size, size, true);
    }

    /**
     * 得到Bitmap具体大小的值
     *
     * @param size
     * @return
     */
    public int GetSizeValue(String size) {

        if (size.equals(context.getResources().getString(R.string.bubble_size_muchBig))) {

            return (int) ((int) px * 1.2);
        } else if (size.equals(context.getResources().getString(R.string.bubble_size_big))) {

            return px;
        } else if (size.equals(context.getResources().getString(R.string.bubble_size_small))) {

            return (int) ((int) px * 0.8);
        } else if (size.equals(context.getResources().getString(R.string.bubble_size_muchSmall))) {

            return (int) ((int) px * 0.6);
        } else
            return px;
    }
}
