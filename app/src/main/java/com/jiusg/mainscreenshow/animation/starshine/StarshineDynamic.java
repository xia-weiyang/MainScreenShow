package com.jiusg.mainscreenshow.animation.starshine;

import android.graphics.Bitmap;

import com.jiusg.mainscreenshow.animation.starshine.Starshine;

public class StarshineDynamic extends Starshine {

	private float scale; // 放大缩小倍数
	private float maxScale; // 最大倍数
	private float minScale; // 最小倍数
	private boolean IsShink = false;  // 是否缩小
	private float scaleWidth;  // 缩放时的宽
	private float scaleheight; // 

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getMaxScale() {
		return maxScale;
	}

	public void setMaxScale(float maxScale) {
		this.maxScale = maxScale;
	}

	public float getMinScale() {
		return minScale;
	}

	public void setMinScale(float minScale) {
		this.minScale = minScale;
	}

	
	public boolean isIsShink() {
		return IsShink;
	}

	public void setIsShink(boolean isShink) {
		IsShink = isShink;
	}

	public float getScaleWidth() {
		return scaleWidth;
	}

	public void setScaleWidth(float scaleWidth) {
		this.scaleWidth = scaleWidth;
	}

	public float getScaleheight() {
		return scaleheight;
	}

	public void setScaleheight(float scaleheight) {
		this.scaleheight = scaleheight;
	}

	public StarshineDynamic() {

		SetAlpha();
		//super();
		this.maxScale =  0.5f + (float) Math.random();
		this.minScale = 0.1f+ (0.5f * (float)Math.random());
		this.scale = this.minScale
				+ (float) ((this.maxScale - this.minScale) * Math.random());

	}
	/**
	 * 计算下一时刻的缩放倍数
	 */
	public void CountNextStarScale(){
		
		if(!this.IsShink){
			
			if(this.scale < this.maxScale){
				
				this.scale =this.scale + 0.05f;
				
			}else{
				
				this.IsShink = true;
			}
		}else{
			
			if(this.scale > this.minScale){
				
				this.scale =this.scale - 0.05f;
			}else{
				
				this.IsShink = false;
			}
		}
		this.scaleWidth = this.scaleheight = this.getWidth()*this.scale;
	}

	/**
	 * 重写方法，因为在缩小时可能是大小小于1
	 */
	@Override
	public void SetSize(float maxSize) {
		
		
		//super.SetSize(maxSize);
		this.setWidth((int) (10+(int) ((maxSize*this.getBt().getWidth())* Math.random())));
		this.setHeight(this.getWidth());
		this.setBt(Bitmap.createScaledBitmap(this.getBt(), this.getWidth(),
				this.getHeight(), true));
	}

	/**
	 * 得到下一时刻的中心点
	 * @return
	 */
	public float getNPositionX() {
		
		return this.getPositionX()-(this.scaleWidth/2);
	}

	public float getNPositionY() {
		
		return this.getPositionY()-(this.scaleheight/2);
	}

	@Override
	public void SetAlpha() {
		
		this.setAlpha(200 + (int) ((int) 50 * Math.random()));
	}

	

	
}
