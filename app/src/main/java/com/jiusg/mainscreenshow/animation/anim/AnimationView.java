package com.jiusg.mainscreenshow.animation.anim;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jiusg.mainscreenshow.animation.bubble.BubbleAnimation;
import com.jiusg.mainscreenshow.animation.picturewall.PictureWallAnimation;
import com.jiusg.mainscreenshow.animation.rain.RainAnimation;
import com.jiusg.mainscreenshow.animation.snow.SnowAnimation;
import com.jiusg.mainscreenshow.animation.starshine.StarshineAnimation;
import com.jiusg.mainscreenshow.base.C;

/**
 * Created by zk on 2015/10/5.
 */
public class AnimationView extends SurfaceView implements SurfaceHolder.Callback {

    public int anim = C.ANIMATION_BUBBLE;
    private Animation animation = null;
    private SurfaceHolder mSurfaceHolder = null;
    private Context context = null;

    private int frame = 60;
    //每多少毫秒计算一次
    private int speed = 60;
    private DrawThread drawThread = null;
    private CountThread countThread = null;
    private boolean isLoopDraw = true;
    private boolean isLoopCount = true;
    private boolean isVisible = false;

    private int event = -1;

    private final String TAG = "AnimationView";

    public AnimationView(Context context, int anim, int event) {
        super(context);
        this.context = context;
        this.anim = anim;
        this.event = event;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        init();
    }


    private void init() {
        animation = instanceAnimation(anim);
        speed = animation.speed;
    }

    /**
     * 实例动画
     *
     * @param anim
     * @return 默认返回气泡动画
     */
    private Animation instanceAnimation(int anim) {
        switch (anim) {
            case C.ANIMATION_BUBBLE:
                return new BubbleAnimation(context, event);
            case C.ANIMATION_STARSHINE:
                return new StarshineAnimation(context, event);
            case C.ANIMATION_PICTUREWALL:
                return new PictureWallAnimation(context, event);
            case C.ANIMATION_RAIN:
                return new RainAnimation(context, event);
            case C.ANIMATION_SNOW:
                return new SnowAnimation(context,event);
            default:
                break;
        }

        return new BubbleAnimation(context, event);
    }

    private void startAnimation() {
        isLoopDraw = true;
        isLoopCount = true;
        drawThread = new DrawThread();
        countThread = new CountThread();
        drawThread.start();
        countThread.start();
        Log.i(TAG, "satrtAnimation=" + anim);
    }


    public void reset() {
        animation = null;
        instanceAnimation(anim);
        speed = animation.speed;
    }

    public void stopAnimation() {
        isLoopDraw = false;
        isLoopCount = false;
        drawThread = null;
        countThread = null;
        Log.i(TAG, "stopAnimation=" + anim);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startAnimation();
        Log.i(TAG,"surfaceCreated anim="+anim);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stopAnimation();
        Log.i(TAG, "surfaceDestroyed anim=" + anim);
    }

    class DrawThread extends Thread {

        private Canvas canvas = null;

        @Override
        public void run() {
            while (isLoopDraw) {
                long oldTime = System.currentTimeMillis();
                canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas();
                    if (canvas != null) {
                        animation.draw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                long time = 1000 / frame - (System.currentTimeMillis() - oldTime);
                if (time > 0)
                    try {
                        sleep(time);
                    } catch (Exception e) {

                    }
                else
                    try {
                        sleep(1);  // 这里是为了防止阻塞主线程
                    } catch (Exception e) {

                    }
            }
        }
    }

    class CountThread extends Thread {
        @Override
        public void run() {
            while (isLoopCount) {
                long oldTime = System.currentTimeMillis();
                animation.count();
                long time = speed - (System.currentTimeMillis() - oldTime);
                if (time > 0)
                    try {
                        sleep(time);
                    } catch (Exception e) {

                    }
            }
        }
    }
}
