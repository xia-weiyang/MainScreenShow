package com.jiusg.mainscreenshow.animation.rain;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.jiusg.mainscreenshow.animation.rain.Rain;
import com.jiusg.mainscreenshow.animation.rain.RainSetting;
import com.jiusg.mainscreenshow.base.C;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/6/23.
 * @deprecated
 */
public class RainView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder = null;
    private Paint paint;
    private Canvas canvas;
    private Context context;
    private ArrayList<Rain> rains = null;
    private Timer timer;
    private Timer timerCount;
    private RainHandler handler;
    private RainCountHandler countHandler;
    private RainSetting setting;

    public RainView(Context context) {
        super(context);
    }

    public RainView(Context context, RainSetting setting){

        super(context);
        this.context = context;
        this.paint = new Paint();
        this.setting = setting;
        rains = new ArrayList<Rain>();
        countHandler = new RainCountHandler();
        handler = new RainHandler();
        SharedPreferences sp_userinfo = context.getSharedPreferences("userinfo", 0);
        Rain.screenheight = sp_userinfo.getInt("screenheight", 0);
        Rain.screenwidth = sp_userinfo.getInt("screenwidth", 0);
        paint.setAlpha((int)(setting.getAlpha()*255));
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        surfaceHolder = getHolder();
        this.setFocusable(true);
        initalRain();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        TimerTask task = new TimerTask() {
            public void run() {

                Message msg = countHandler.obtainMessage();
                countHandler.sendMessage(msg);
            }
        };
        timerCount = new Timer();
        timerCount.schedule(task, 0, 30);

        TimerTask task1 = new TimerTask() {
            public void run() {

                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        };
        timer = new Timer();
        timer.schedule(task1, 0, 1000 / C.FRAME);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        timer.cancel();
        timerCount.cancel();
    }

    public void Draw() {

        if (surfaceHolder == null)
            return;
        canvas = surfaceHolder.lockCanvas();
        if (canvas == null)
            return;
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        OnDraw(canvas);
        surfaceHolder.unlockCanvasAndPost(canvas);
    }

    public void OnDraw(Canvas canvas) {

        for (int i = 0; i < rains.size(); i++) {

            canvas.drawBitmap(rains.get(i).getBitmap(), rains.get(i).getX(), rains.get(i).getY(), paint);
        }
    }

    public void initalRain() {

        for (int i = 0; i < setting.getAmount(); i++) {

            Rain rain = new Rain(context,setting);
            rain.setSpeed(rain.getSpeed()+(5*setting.getSpeed()));
            rains.add(rain);
        }
    }

    class RainHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Draw();
            RainHandler.this.removeCallbacksAndMessages(null);
        }
    }

    class RainCountHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            for (int i = 0; i < rains.size(); i++) {
                rains.get(i).countNext(context);
            }
            RainCountHandler.this.removeCallbacksAndMessages(null);
        }
    }
}
