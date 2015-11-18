package com.jiusg.mainscreenshow.animation.picturewall;

public class PictureWallSetting {
	private boolean isOnlyOne;
	private int period;
	private int alpha;
	private boolean isRecourse; // 用于标记是否sd卡对应目录下是否有图片资源
	private int BITMAP_TYPE;
	private float scaleV;
	
	public float getScaleV() {
		return scaleV;
	}
	public void setScaleV(float scaleV) {
		this.scaleV = scaleV;
	}
	public boolean isOnlyOne() {
		return isOnlyOne;
	}
	public void setOnlyOne(boolean isOnlyOne) {
		this.isOnlyOne = isOnlyOne;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public int getAlpha() {
		return alpha;
	}
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	public boolean isRecourse() {
		return isRecourse;
	}
	public void setRecourse(boolean isRecourse) {
		this.isRecourse = isRecourse;
	}
	public int getBITMAP_TYPE() {
		return BITMAP_TYPE;
	}
	public void setBITMAP_TYPE(int bITMAP_TYPE) {
		BITMAP_TYPE = bITMAP_TYPE;
	}
	
}
