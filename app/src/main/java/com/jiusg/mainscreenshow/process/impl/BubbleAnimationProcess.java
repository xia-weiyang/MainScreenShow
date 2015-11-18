package com.jiusg.mainscreenshow.process.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.jiusg.mainscreenshow.animation.anim.AnimationView;
import com.jiusg.mainscreenshow.animation.bubble.BubbleSetting;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.process.AnimationProcess;

/**
 * @deprecated
 */
public class BubbleAnimationProcess implements AnimationProcess {

	private AnimationView win;
	private BubbleSetting bs;
	private SharedPreferences sp;
	private Context context;
	private final String TAG = "BubbleAnimationProcess";

	public BubbleAnimationProcess(Context context, String sp_name) {

		this.context = context;
		win = new AnimationView(context, C.ANIMATION_BUBBLE,C.EVENT_DESKTOP);
		Log.i(TAG, "has create BubbleView.");
	}

	@Override
	public void StartAnimation(WindowManager Wm, LayoutParams wmParams) {

		Wm.addView(win, wmParams);

	}

	@Override
	public void StopAnimation(WindowManager Wm) {

		Wm.removeViewImmediate(win);
	}


}
