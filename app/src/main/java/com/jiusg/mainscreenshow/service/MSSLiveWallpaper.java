package com.jiusg.mainscreenshow.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.jiusg.mainscreenshow.animation.anim.Animation;
import com.jiusg.mainscreenshow.animation.bubble.BubbleAnimation;
import com.jiusg.mainscreenshow.animation.picturewall.PictureWallAnimation;
import com.jiusg.mainscreenshow.animation.rain.RainAnimation;
import com.jiusg.mainscreenshow.animation.snow.SnowAnimation;
import com.jiusg.mainscreenshow.animation.starshine.StarshineAnimation;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.tools.PropertiesUtils;

/**
 * Created by zk on 2015/10/4.
 */
public class MSSLiveWallpaper extends WallpaperService {

    public int animation = -1;
    private MSSEngine engine = null;
    public static final int ANIMATION = 100;

    private final String TAG = "MSSLiveWallpaper";

    @Override
    public Engine onCreateEngine() {
        engine = new MSSEngine();
        return engine;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        engine = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getFlags() == ANIMATION && engine != null)
            engine.reset();
        return START_STICKY;
    }

    class MSSEngine extends Engine {
        private Animation animation = null;
        //帧数
        private int frame = 60;
        //每多少毫秒计算一次
        private int speed = 60;
        private DrawThread drawThread = null;
        private CountThread countThread = null;
        private boolean isLoopDraw = true;
        private boolean isLoopCount = true;
        private boolean isVisible = false;
        private boolean isReset = false;

        public MSSEngine() {
            init();
        }

        private void init() {
            animation = instanceAnimation(loadSettingAnimation());
            speed = animation.speed;
        }

        /**
         * 实例动画
         *
         * @param animation
         * @return 默认返回气泡动画
         */
        private Animation instanceAnimation(int animation) {
            switch (animation) {
                case C.ANIMATION_BUBBLE:
                    return new BubbleAnimation(getApplicationContext(), C.EVENT_DESKTOP);
                case C.ANIMATION_STARSHINE:
                    return new StarshineAnimation(getApplicationContext(), C.EVENT_DESKTOP);
                case C.ANIMATION_PICTUREWALL:
                    return new PictureWallAnimation(getApplicationContext(), C.EVENT_DESKTOP);
                case C.ANIMATION_RAIN:
                    return new RainAnimation(getApplicationContext(), C.EVENT_DESKTOP);
                case C.ANIMATION_SNOW:
                    return new SnowAnimation(getApplicationContext(), C.EVENT_DESKTOP);
                default:
                    break;
            }

            return new BubbleAnimation(getApplicationContext(), C.EVENT_DESKTOP);
        }

        /**
         * 加载桌面动画设置信息
         *
         * @return 动画  默认返回气泡动画
         */
        private int loadSettingAnimation() {
            SharedPreferences sp = getSharedPreferences("date", 0);
            String animation = sp.getString("animation" + C.EVENT_DESKTOP, "");

            if (animation.equals(PropertiesUtils.getAnimationInfo(getApplicationContext())[C.ANIMATION_BUBBLE])) {

                return C.ANIMATION_BUBBLE;
            } else if (animation.equals(PropertiesUtils.getAnimationInfo(getApplicationContext())[C.ANIMATION_STARSHINE])) {

                return C.ANIMATION_STARSHINE;
            } else if (animation.equals(PropertiesUtils.getAnimationInfo(getApplicationContext())[C.ANIMATION_PICTUREWALL])) {

                return C.ANIMATION_PICTUREWALL;
            } else if (animation.equals(PropertiesUtils.getAnimationInfo(getApplicationContext())[C.ANIMATION_RAIN])) {

                return C.ANIMATION_RAIN;
            } else if (animation.equals(PropertiesUtils.getAnimationInfo(getApplicationContext())[C.ANIMATION_SNOW])) {

                return C.ANIMATION_SNOW;
            }
            return C.ANIMATION_BUBBLE;
        }

        private void startAnimation() {
            isLoopDraw = true;
            isLoopCount = true;
            drawThread = new DrawThread();
            countThread = new CountThread();
            drawThread.start();
            countThread.start();
            Log.i(TAG, "satrtAnimation=" + animation);
        }


        private void reset() {
            isReset = true;
        }

        private void stopAnimation() {
            isLoopDraw = false;
            isLoopCount = false;
            drawThread = null;
            countThread = null;
            Log.i(TAG, "stopAnimation=" + animation);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            isVisible = visible;
            if (visible) {
                if (isReset) {
                    animation = null;
                    init();
                    isReset = false;

                }
                startAnimation();
            } else {
                stopAnimation();
                // 启动service
                startService(new Intent(getApplicationContext(), MSSService.class));
            }
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
        }

        class DrawThread extends Thread {

            private Canvas canvas = null;
            final SurfaceHolder holder = getSurfaceHolder();

            @Override
            public void run() {
                while (isLoopDraw) {
                    long oldTime = System.currentTimeMillis();
                    canvas = null;
                    try {
                        canvas = holder.lockCanvas();
                        if (canvas != null) {
                            animation.draw(canvas);
                        }
                    } finally {
                        if (canvas != null) {
                            try {
                                holder.unlockCanvasAndPost(canvas);
                            } catch (Exception e) {
                            }
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
                            sleep(1);
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
}
