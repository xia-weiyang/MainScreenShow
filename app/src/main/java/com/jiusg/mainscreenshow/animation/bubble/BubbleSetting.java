package com.jiusg.mainscreenshow.animation.bubble;

public class BubbleSetting {

	private String Size;
	private boolean ChangeColor;
	private int Number;
	private float Alpha;
	private boolean Shadow;
	private int bubbleSpeed;
	public String getSize() {
		return Size;
	}
	public void setSize(String size) {
		Size = size;
	}
	public boolean isChangeColor() {
		return ChangeColor;
	}
	public void setChangeColor(boolean changeColor) {
		ChangeColor = changeColor;
	}
	public int getNumber() {
		return Number;
	}
	public void setNumber(int number) {
		Number = number;
	}
	public float getAlpha() {
		return Alpha;
	}
	public void setAlpha(float alpha) {
		Alpha = alpha;
	}
	public boolean isShadow() {
		return Shadow;
	}
	public void setShadow(boolean shadow) {
		Shadow = shadow;
	}
	public int getBubbleSpeed() {
		return bubbleSpeed;
	}
	public void setBubbleSpeed(int bubbleSpeed) {
		this.bubbleSpeed = bubbleSpeed;
	}
	
}
