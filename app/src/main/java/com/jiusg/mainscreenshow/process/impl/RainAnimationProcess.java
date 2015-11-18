package com.jiusg.mainscreenshow.process.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.WindowManager;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.animation.rain.RainSetting;
import com.jiusg.mainscreenshow.animation.rain.RainView;
import com.jiusg.mainscreenshow.process.AnimationProcess;

/**
 * Created by Administrator on 2015/6/26.
 */
public class RainAnimationProcess implements AnimationProcess {

    private RainView win;
    private RainSetting setting;
    private SharedPreferences sp;
    private Context context;
    private final String TAG = "RainAnimationProcess";

    public RainAnimationProcess(Context context, String sp_name){

        sp = context.getSharedPreferences(sp_name, 0);
        this.context = context;
        setting = new RainSetting();
        loadSetting(setting);
        win = new RainView(context, setting);
        Log.i(TAG, "has create RainView.");

    }

    @Override
    public void StartAnimation(WindowManager Wm, WindowManager.LayoutParams wmParams) {

        Wm.addView(win, wmParams);
    }

    @Override
    public void StopAnimation(WindowManager Wm) {

    //    Wm.removeView(win);
        Wm.removeViewImmediate(win);
    }

    public void loadSetting(RainSetting setting){

        setting.setAlpha(sp.getFloat("alpha",0.9f));
        setting.setSpeed(sp.getInt("speed",5));
        setting.setAmount(getAmount(sp.getString("amount",context.getString(R.string.rain_amount_very_munch))));
        setting.setStyle(getStyle(sp.getString("style",context.getString(R.string.rain_style_1))));
    }

    public int getAmount(String s){

        if(s.equals(context.getString(R.string.rain_amount_less))){
            return setting.AMOUNT_LESS;
        }else if(s.equals(context.getString(R.string.rain_amount_general))){
            return setting.AMOUNT_GENERAL;
        }else if(s.equals(context.getString(R.string.rain_amount_munch))){
            return setting.AMOUNT_MUNCH;
        }else if (s.equals(context.getString(R.string.rain_amount_very_munch))) {
            return setting.AMOUNT_VERY_MUNCH;
        } else {
            return setting.AMOUNT_GENERAL;
        }
    }

    public int getStyle(String s){
        if(s.equals(context.getString(R.string.rain_style_1)))
            return setting.STYLE_1;
        else if(s.equals(context.getString(R.string.rain_style_2)))
            return setting.STYLE_2;
        else
            return setting.STYLE_1;
    }
}
