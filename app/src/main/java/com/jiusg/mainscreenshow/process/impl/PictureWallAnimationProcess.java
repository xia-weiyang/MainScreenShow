package com.jiusg.mainscreenshow.process.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.animation.picturewall.PictureWall;
import com.jiusg.mainscreenshow.animation.picturewall.PictureWallSetting;
import com.jiusg.mainscreenshow.animation.picturewall.PictureWallView;
import com.jiusg.mainscreenshow.process.AnimationProcess;

/**
 * @deprecated
 */
public class PictureWallAnimationProcess implements AnimationProcess{
	private PictureWallView pwv;
	private PictureWallSetting pws;
	private SharedPreferences sp;
	private Context context;
	private final String TAG = "PictureWallAnimationProcess";
	public PictureWallAnimationProcess(Context context, String sp_name) {
		sp = context.getSharedPreferences(sp_name, 0);
		this.context = context;
		pws = new PictureWallSetting();
		loadSetting(pws);
		pwv = new PictureWallView(context, pws);
		Log.i(TAG, "has create PictureWallView.");
	}
	@Override
	public void StartAnimation(WindowManager Wm, LayoutParams wmParams) {
		Wm.addView(pwv, wmParams);
	}

	@Override
	public void StopAnimation(WindowManager Wm) {
		pwv.mLoop = false;
		Wm.removeViewImmediate(pwv);
		pwv = null;
	}
	
	public void loadSetting(PictureWallSetting pws){
		pws.setAlpha(getAlpha());
		pws.setBITMAP_TYPE(getBitmapType());
		pws.setOnlyOne(sp.getBoolean("onlyone", false));
		pws.setPeriod(getPeriod());
		pws.setRecourse(sp.getBoolean("recourse", false));
		pws.setScaleV(getScaleV());
	}
	private int getBitmapType(){
		if(sp.getString("bitmap_type", context.getString(R.string.pictureWall_play_random)).equals(context.getString(R.string.pictureWall_play_random)))
			return PictureWall.BITMAP_TYPE_RANDOM;
		else if(sp.getString("bitmap_type",  context.getString(R.string.pictureWall_play_random)).equals(context.getString(R.string.pictureWall_play_order)))
			return PictureWall.BITMAP_TYPE_ORDER;
		return PictureWall.BITMAP_TYPE_RANDOM;
	}
	private int getPeriod(){
		return (int) (sp.getFloat("period",4.0f)*10);	
		}
	private int getAlpha(){
		return (int) (sp.getFloat("alpha",0.7f)*255);
	}
	private float getScaleV(){
		int value = sp.getInt("zoomin", 0);
		float scale = PictureWall.SCALE_BASE;
		switch (value) {
		case -5:
			return scale/1.001f;
		case -4:
			return scale/1.0008f;
		case -3:
			return scale/1.0006f;
		case -2:
			return scale/1.0004f;
		case -1:
			return scale/1.0002f;
		case 0:
			return scale;
		case 1:
			return scale*1.001f;
		case 2:
			return scale*1.002f;
		case 3:
			return scale*1.003f;
		case 4:
			return scale*1.004f;
		case 5:
			return scale*1.005f;
		default:
			return scale;
		}
	}
}
