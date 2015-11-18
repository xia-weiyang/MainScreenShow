package com.jiusg.mainscreenshow.process;

import android.view.WindowManager;

/**
 * 处理动画的通用接口
 * @author zk
 * @deprecated
 */

public interface AnimationProcess {

	void StartAnimation(WindowManager Wm, WindowManager.LayoutParams wmParams);
	void StopAnimation(WindowManager Wm);
	//void LoadAnimationSet();

	
}
