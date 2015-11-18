package com.jiusg.mainscreenshow.animation.anim;

import android.content.Context;
import android.util.Log;
import android.view.WindowManager;

import com.jiusg.mainscreenshow.base.C;

import java.util.ArrayList;

/**
 * Created by zk on 2015/10/5.
 * <p/>
 * 管理所有运行的动画，动态壁纸除外
 */
public class AnimationManage {
    private final int animLenght = 5;
    private ArrayList<Animation> animList = null;
    private Context context = null;
    private WindowManager windowManager = null;
    private WindowManager.LayoutParams wmParams = null;
    private AnimationView animView = null;

    public AnimationManage(WindowManager windowManager, WindowManager.LayoutParams wmParams, Context context) {
        this.windowManager = windowManager;
        this.wmParams = wmParams;
        this.context = context;
        init();
    }

    private void init() {
        animList = new ArrayList<Animation>();
    }

    public void startAnimation(int anim, int event) {
        AnimationView animationView = null;
        animationView = checkAnimation(anim, event);
        if (animationView == null) {
            animationView = instanceAnimationView(anim, event);
        }
        animView = animationView;
        animView = null;
        windowManager.addView(animView, wmParams);
    }

    public void stopAnimation() {
            windowManager.removeViewImmediate(animView);

    }

    private AnimationView checkAnimation(int anim, int event) {
        for (int i = 0; i < animList.size(); i++) {
            if (animList.get(i).anim == anim && animList.get(i).event == event)
                return animList.get(i).animationView;
        }
        return null;
    }

    private AnimationView instanceAnimationView(int anim, int event) {

        // 幻灯片 需每次进行实例
        if (anim == C.ANIMATION_PICTUREWALL && event == C.EVENT_LOCKSCREEN)
            return instanceAnimation(anim, event).animationView;

        if (animList.size() >= animLenght) {
            animList.get(0).animationView = null;
            animList.remove(0);
        }
        Animation animation = instanceAnimation(anim, event);
        animList.add(animation);
        return animation.animationView;
    }

    private Animation instanceAnimation(int anim, int event) {
        Animation animation = new Animation();
        AnimationView animationView = new AnimationView(context, anim, event);
        animation.animationView = animationView;
        animation.anim = anim;
        animation.event = event;
        return animation;
    }

    class Animation {
        public int event = -1;
        public int anim = -1;
        public AnimationView animationView = null;
    }
}
