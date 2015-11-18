package com.jiusg.mainscreenshow.process.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.animation.starshine.StarshineSetting;
import com.jiusg.mainscreenshow.animation.starshine.StarshineView;
import com.jiusg.mainscreenshow.process.AnimationProcess;

/**
 * @deprecated
 */
public class StarshineAnimationProcess implements AnimationProcess {

	private StarshineView win;
	private StarshineSetting ss;
	private SharedPreferences sp;
	private Context context;
	private final String TAG = "StarshineAnimationProcess";

	public StarshineAnimationProcess(Context context, String sp_name) {

		sp = context.getSharedPreferences(sp_name, 0);
		this.context = context;
		ss = new StarshineSetting();
		LoadSetting(ss);
		win = new StarshineView(context, ss);
		Log.i(TAG, "has create StarshineView.");
	}

	@Override
	public void StartAnimation(WindowManager Wm, LayoutParams wmParams) {
		
		Wm.addView(win, wmParams);
	}

	@Override
	public void StopAnimation(WindowManager Wm) {

		win.mLoop = false;   // 控制结束
		Wm.removeViewImmediate(win);
	}

	public void LoadSetting(StarshineSetting ss) {

		ss.setAllCount(GetAllCount(sp.getString("allcount", context.getString(R.string.starShine_num_general))));
		ss.setStarMeteorCount(GetStarMeteorCount(sp.getString("starmeteorcount", context.getString(R.string.starShine_num_general))));
		ss.setStarMeteorSwitch(sp.getBoolean("starmeteorswitch", true));
		ss.setStyle(getStyle(sp.getString("style",context.getString(R.string.starShine_style_classical))));
	}

	public int GetAllCount(String flag) {

		if (flag.equals(context.getString(R.string.starShine_num_less))) {

			return 100;
		} else if (flag.equals(context.getString(R.string.starShine_num_general))) {

			return 200;
		} else if (flag.equals(context.getString(R.string.starShine_num_much))) {

			return 300;
		} else {

			return 200;
		}
	}

	public int GetStarMeteorCount(String flag) {

		if (flag.equals("少")) {

			return 3;
		} else if (flag.equals("一般")) {

			return 7;
		} else if (flag.equals("多")) {

			return 15;
		} else {

			return 3;
		}
	}

	private int getStyle(String style){
		if(style.equals(context.getString(R.string.starShine_style_classical))){
			return StarshineSetting.CLASSICAL;
		}else if(style.equals(context.getString(R.string.starShine_style_stars))){
			return  StarshineSetting.STARS;
		}else{
			return StarshineSetting.CLASSICAL;
		}
	}
}
