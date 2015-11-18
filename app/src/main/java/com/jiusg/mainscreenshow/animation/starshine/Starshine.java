package com.jiusg.mainscreenshow.animation.starshine;

import com.jiusg.mainscreenshow.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class Starshine {

    private Bitmap bt;
    private float positionX;
    private float positionY;
    private int Width;
    private int Height;
    private int alpha;
    private int style;

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public Bitmap getBt() {
        return bt;
    }

    public void setBt(Bitmap bt) {
        this.bt = bt;
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public int getWidth() {
        return Width;
    }

    public void setWidth(int Width) {
        this.Width = Width;
    }

    public int getHeight() {
        return Height;
    }

    public void setHeight(int Height) {
        this.Height = Height;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public Starshine() {

        SetAlpha();
    }

    /**
     * 初始位置操作-将屏幕竖向分成四份进行位置的分配
     *
     * @param flag         为0代表自行分配按照 6:3:1 ,1 代表屏幕最上方区域，依次类推，最大为4
     * @param screenWidth  屏幕宽
     * @param screenHeight 屏幕高
     */
    public void SetPosition(int flag, int screenWidth, int screenHeight) {

        switch (flag) {
            case 0:
                int i = (int) ((int) 10 * Math.random());
                if (i < 6)
                    this.SetPosition(1, screenWidth, screenHeight);
                else if (i < 9 && i >= 6)
                    this.SetPosition(2, screenWidth, screenHeight);
                else
                    this.SetPosition(3, screenWidth, screenHeight);
                break;
            case 1:
                this.positionX = (int) ((int) screenWidth * Math.random());
                this.positionY = (int) ((int) (screenHeight / 4) * Math.random());
                break;
            case 2:
                this.positionX = (int) ((int) screenWidth * Math.random());
                this.positionY = (int) ((int) (screenHeight / 4) * Math.random()) + screenHeight / 4;
                break;
            case 3:
                this.positionX = (int) ((int) screenWidth * Math.random());
                this.positionY = (int) ((int) (screenHeight / 4) * Math.random()) + screenHeight / 2;
                break;
            case 4:
                this.positionX = (int) ((int) screenWidth * Math.random());
                this.positionY = (int) ((int) (screenHeight / 4) * Math.random()) + screenHeight / 4 * 3;
            default:
                break;
        }
    }

    /**
     * 初始大小操作
     *
     * @param maxSize 大小(1-maxSize)
     */
    public void SetSize(float maxSize) {

        this.Height = this.Width = 1 + (int) ((maxSize * bt.getWidth()) * Math.random());
        //	this.Height = this.Width = 1 + (int) ((int) maxSize * Math.random());
        this.bt = Bitmap.createScaledBitmap(this.bt, this.Width,
                this.Height, true);
        //	System.out.println(this.Width+"%%%"+(bt.getWidth()));

    }

    /**
     * 初始透明度 (100-250)
     */
    public void SetAlpha() {

        this.alpha = 100 + (int) ((int) 150 * Math.random());
    }

    /**
     * 初始Bitmap (1:2)
     *
     * @param mContext
     * @param type     1:静态星光 2:动态星光 3:流星
     */
    public void SetBitmap(Context mContext, int type) {

        //	int flag = (int) ((int) 10 * Math.random());
        switch (type) {
            case 1:
                if (style == StarshineSetting.CLASSICAL)
                    this.bt = ((BitmapDrawable) mContext.getResources().getDrawable(
                            R.drawable.starshine)).getBitmap();
                else if (style == StarshineSetting.STARS)
                    this.bt = getStaticStar(mContext);
                else
                    this.bt = ((BitmapDrawable) mContext.getResources().getDrawable(
                            R.drawable.starshine)).getBitmap();
                break;
            case 2:
                if (style == StarshineSetting.CLASSICAL)
                    this.bt = ((BitmapDrawable) mContext.getResources().getDrawable(
                            R.drawable.starshine)).getBitmap();
                else if (style == StarshineSetting.STARS)
                    this.bt = getDnamicStar(mContext);
                else
                    this.bt = ((BitmapDrawable) mContext.getResources().getDrawable(
                            R.drawable.starshine)).getBitmap();
                break;
            case 3:
                if (style == StarshineSetting.CLASSICAL)
                    this.bt = ((BitmapDrawable) mContext.getResources().getDrawable(
                            R.drawable.starshine_meteor)).getBitmap();
                else if (style == StarshineSetting.STARS)
                    this.bt = ((BitmapDrawable) mContext.getResources().getDrawable(
                            R.drawable.starshine_meteor_1)).getBitmap();
                else
                    this.bt = ((BitmapDrawable) mContext.getResources().getDrawable(
                            R.drawable.starshine_meteor)).getBitmap();
                break;
            default:
                break;
        }

		/*this.bt = Bitmap.createScaledBitmap(this.bt, this.Width,
                this.Height, true);*/
    }

    /**
     * 得到静态的星光资源  繁星
     *
     * @param mContext
     * @return
     */
    private Bitmap getStaticStar(Context mContext) {
        int i = (int) ((int) 8 * Math.random());
        switch (i) {
            case 1:
                return ((BitmapDrawable) mContext.getResources().getDrawable(
                        R.drawable.starshine_1)).getBitmap();
            case 2:
                return ((BitmapDrawable) mContext.getResources().getDrawable(
                        R.drawable.starshine_2)).getBitmap();
            case 3:
                return ((BitmapDrawable) mContext.getResources().getDrawable(
                        R.drawable.starshine_3)).getBitmap();
            case 4:
                return ((BitmapDrawable) mContext.getResources().getDrawable(
                        R.drawable.starshine_4)).getBitmap();
            case 5:
                return ((BitmapDrawable) mContext.getResources().getDrawable(
                        R.drawable.starshine_5)).getBitmap();
            case 6:
                return ((BitmapDrawable) mContext.getResources().getDrawable(
                        R.drawable.starshine_6)).getBitmap();
            case 7:
                return ((BitmapDrawable) mContext.getResources().getDrawable(
                        R.drawable.starshine_7)).getBitmap();
            default:
                return ((BitmapDrawable) mContext.getResources().getDrawable(
                        R.drawable.starshine_3)).getBitmap();
        }
    }

    private Bitmap getDnamicStar(Context context) {

        int i = (int) ((int) 10 * Math.random());
        if (i <= 5) {
            return ((BitmapDrawable) context.getResources().getDrawable(
                    R.drawable.starshine_1)).getBitmap();
        } else {
            return ((BitmapDrawable) context.getResources().getDrawable(
                    R.drawable.starshine_2)).getBitmap();
        }
    }
}
