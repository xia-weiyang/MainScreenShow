package com.jiusg.mainscreenshow.base;

import android.content.Context;

import com.jiusg.mainscreenshow.tools.PropertiesUtils;

/**
 * Created by Administrator on 2015/11/18.
 * 记录动画是否被激活
 */
public class AnimationConvert {

    public static int convertAnimation(String anim,Context context) {
        if (anim.equals(PropertiesUtils.getAnimationInfo(context)[C.ANIMATION_BUBBLE])) {

            return C.ANIMATION_BUBBLE;

        } else if (anim
                .equals(PropertiesUtils.getAnimationInfo(context)[C.ANIMATION_STARSHINE])) {

            return C.ANIMATION_STARSHINE;

        } else if (anim
                .equals(PropertiesUtils.getAnimationInfo(context)[C.ANIMATION_PICTUREWALL])) {
            return C.ANIMATION_PICTUREWALL;
        } else if (anim
                .equals(PropertiesUtils.getAnimationInfo(context)[C.ANIMATION_RAIN])) {

            return C.ANIMATION_RAIN;
        }else if (anim
                .equals(PropertiesUtils.getAnimationInfo(context)[C.ANIMATION_SNOW])) {

            return C.ANIMATION_SNOW;
        }
        return C.ANIMATION_BUBBLE;
    }

}
