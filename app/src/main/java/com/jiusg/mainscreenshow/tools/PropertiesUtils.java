package com.jiusg.mainscreenshow.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 加载配置文件类
 *
 * @author Administrator
 */
public class PropertiesUtils {

    final static String filename = "info.properties";
    public final static int EventLength = 5; // 事件String数组的长度约定为5

    /**
     * 获取触发事件的总个数
     *
     * @param c
     * @return
     */
    public static int GetLength(Context c) {

        Properties pro = new Properties();

        InputStream is;
        try {
            is = c.getAssets().open(filename);
            pro.load(is);
            is.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return -1;
        }

        int i = 0;
        while (pro.getProperty("start." + i) != null) {

            i++;
        }
        return i;
    }

    /**
     * 获取动画的总个数
     *
     * @param c
     * @return
     */
    public static int GetAnimationLength(Context c) {

        Properties pro = new Properties();

        InputStream is;
        try {
            is = c.getAssets().open(filename);
            pro.load(is);
            is.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return -1;
        }

        int i = 0;
        while (pro.getProperty("animation." + i) != null) {

            i++;
        }
        return i;
    }

    /**
     * 获取某个事件结束条件的个数
     *
     * @param i
     * @param c
     * @return
     */
    public static int getEndLength(int i, Context c) {

        Properties pro = new Properties();

        InputStream is;
        try {
            is = c.getAssets().open(filename);
            pro.load(is);
            is.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return -1;
        }

        int x = 0;
        while (pro.getProperty("end." + i + "." + x) != null) {

            x++;
        }
        return x;
    }

    /**
     * 获取某个事件的所有选项数据 list长度为5
     *
     * @param i 事件的值
     * @param c
     * @return
     */
    public static String[] GetEventInfo(int i, Context c) {

        Properties pro = new Properties();
        String[] s = new String[5];
        InputStream is;
        try {
            is = c.getAssets().open(filename);
            pro.load(is);
            is.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return null;
        }
        s[0] = pro.getProperty("start." + i);

        for (int j = 1; j < 5; j++) {

            s[j] = pro.getProperty("end." + i + "." + (j - 1));
        }
        return s;
    }

    /**
     * 获取某个事件的结束所有选项
     *
     * @param i
     * @param c
     * @return
     */
    public static String[] getEventEndInfo(int i, Context c) {

        String[] s = GetEventInfo(i, c);
        String[] s1 = new String[getEndLength(i, c)];
        for (int j = 0; j < s1.length; j++) {

            if (s[j + 1] != null)
                s1[j] = s[j + 1];
        }

        return s1;
    }

    /**
     * 获得已激活的动画信息
     *
     * @return
     */
    public static String[] getActiveAnimationInfo(Context c) {
        String[] s = getAnimationInfo(c);
        SharedPreferences sp = c.getSharedPreferences("animation", Context.MODE_PRIVATE);
        int j = 0;
        for (int i = 0; i < s.length; i++) {
            if (sp.getBoolean(s[i], false)) {
                j++;
            }
        }
        String[] ss = new String[j];
        int x = 0;
        for (int i = 0; i < s.length; i++) {
            if (sp.getBoolean(s[i], false)) {
                ss[x] = s[i];
                x++;
            }
        }
        return ss;
    }


    /**
     * 获取所有的动画信息
     *
     * @param c
     * @return
     */
    public static String[] getAnimationInfo(Context c) {

        Properties pro = new Properties();
        String[] s = new String[GetAnimationLength(c)];
        InputStream is;
        try {
            is = c.getAssets().open(filename);
            pro.load(is);
            is.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return null;
        }

        for (int j = 0; j < s.length; j++) {

            s[j] = pro.getProperty("animation." + j);
        }
        return s;
    }

    public static int getAnimationListLenght(String animation, Context mContext, int value) {

        if (animation
                .equals(PropertiesUtils.getAnimationInfo(mContext)[C.ANIMATION_BUBBLE])) {
            return 7;
        } else if (animation
                .equals(PropertiesUtils.getAnimationInfo(mContext)[C.ANIMATION_STARSHINE])) {
            return 5;
        } else if (animation
                .equals(PropertiesUtils.getAnimationInfo(mContext)[C.ANIMATION_PICTUREWALL])) {
            if (value == C.EVENT_LOCKSCREEN)
                return 7;
            return 6;
        } else if (animation
                .equals(PropertiesUtils.getAnimationInfo(mContext)[C.ANIMATION_RAIN])) {
            return 5;
        } else if (animation
                .equals(PropertiesUtils.getAnimationInfo(mContext)[C.ANIMATION_SNOW])) {
            return 5;
        } else {
            return 1;
        }
    }

    public static String getHelpInfo(int i, String animation, Context c) {

        String s1 = "", s2 = "";

        if (i == C.EVENT_DESKTOP)
            s1 = c.getResources().getString(R.string.help_desktop);
        else if (i == C.EVENT_LOCKSCREEN)
            s1 = c.getResources().getString(R.string.help_lockscreen);
        else if (i == C.EVENT_CALL)
            s1 = c.getResources().getString(R.string.help_call);
        else if (i == C.EVENT_SMS)
            s1 = c.getResources().getString(R.string.help_sms);
        else if (i == C.EVENT_CHARGING)
            s1 = c.getResources().getString(R.string.help_charing);

        if (getAnimationInfo(c)[0].equals(animation))
            s2 = c.getResources().getString(R.string.help_bubble);
        else if (getAnimationInfo(c)[1].equals(animation))
            s2 = c.getResources().getString(R.string.help_starshine);
        else if (getAnimationInfo(c)[C.ANIMATION_PICTUREWALL].equals(animation))
            s2 = c.getResources().getString(R.string.help_picturewall);
        else if (getAnimationInfo(c)[C.ANIMATION_RAIN].equals(animation))
            s2 = c.getString(R.string.help_rain);
        return s1 + s2;

    }
}
