package com.jiusg.mainscreenshow.animation.bubble;

import android.graphics.Bitmap;

/**
 * bubble
 * @author Administrator
 *
 */
public class Bubble {

	private String size;
	private Bitmap bubble;
	private Bitmap bubbleShadow;
	private float X;
	private float Y;
	private BubbleSpeed Speed;
	private boolean IsArea;
	private int count;
	private float r;
	private int color;
	private boolean falg;
	private boolean IsNoCD;
	public Bitmap getBubble() {
		return bubble;
	}
	public void setBubble(Bitmap bubble) {
		this.bubble = bubble;
	}
	public float getX() {
		return X;
	}
	public void setX(float x) {
		X = x;
	}
	public float getY() {
		return Y;
	}
	public void setY(float y) {
		Y = y;
	}
	public BubbleSpeed getSpeed() {
		return Speed;
	}
	public void setSpeed(BubbleSpeed speed) {
		Speed = speed;
	}
	public boolean isIsArea() {
		return IsArea;
	}
	public void setIsArea(boolean isArea) {
		IsArea = isArea;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public float getR() {
		return r;
	}
	public void setR(float r) {
		this.r = r;
	}
	public boolean isIsNoCD() {
		return IsNoCD;
	}
	public void setIsNoCD(boolean isNoCD) {
		IsNoCD = isNoCD;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public boolean isFalg() {
		return falg;
	}
	public void setFalg(boolean falg) {
		this.falg = falg;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public Bitmap getBubbleShadow() {
		return bubbleShadow;
	}
	public void setBubbleShadow(Bitmap bubbleShadow) {
		this.bubbleShadow = bubbleShadow;
	}
	
	
}
