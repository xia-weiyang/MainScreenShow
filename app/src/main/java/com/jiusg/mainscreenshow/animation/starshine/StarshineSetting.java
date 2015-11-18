package com.jiusg.mainscreenshow.animation.starshine;

public class StarshineSetting {

	private int allCount;
	private int starMeteorCount;
	private boolean starMeteorSwitch;
	private int style;

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public static final int CLASSICAL = 100;
	public static final int STARS = 101;
	public int getAllCount() {
		return allCount;
	}
	public void setAllCount(int allCount) {
		this.allCount = allCount;
	}
	public int getStarMeteorCount() {
		return starMeteorCount;
	}
	public void setStarMeteorCount(int starMeteorCount) {
		this.starMeteorCount = starMeteorCount;
	}
	public boolean isStarMeteorSwitch() {
		return starMeteorSwitch;
	}
	public void setStarMeteorSwitch(boolean starMeteorSwitch) {
		this.starMeteorSwitch = starMeteorSwitch;
	}
	
	
}
