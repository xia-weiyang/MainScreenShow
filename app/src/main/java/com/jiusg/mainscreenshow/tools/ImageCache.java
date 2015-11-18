package com.jiusg.mainscreenshow.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * Created by Administrator on 2015/8/19.
 */
public class ImageCache{

    public static LruCache lruCache = null;
    public int memClass = -1;
    private String TAG = "ImageChche";

    public ImageCache(Context context){
        memClass = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        if(memClass > 0) {
            final int cacheSize = 1024 * 1024 * memClass / 8;
            Log.i(TAG, "cacheSize=" + cacheSize);
            lruCache = new LruCache(cacheSize){
                @Override
                protected int sizeOf(Object key, Object value) {
                    return ((Bitmap)value).getByteCount();
                }
            };
        }
    }


}
