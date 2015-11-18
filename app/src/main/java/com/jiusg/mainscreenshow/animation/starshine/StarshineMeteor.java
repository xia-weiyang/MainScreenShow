package com.jiusg.mainscreenshow.animation.starshine;

import com.jiusg.mainscreenshow.animation.starshine.Starshine;

public class StarshineMeteor extends Starshine {

	private int T;
	private int time;
	private float speedX;
	private float speedY;
	public float speed;
	private int screenWidth;
	private int screenHeight;


	public StarshineMeteor() {

		SetAlpha();
		T = 50+(int) ((int) 100 * Math.random());
		time = T + (int) ((int) (2*T) * Math.random());
	}

	public void SetScreenWidthHeight(int screenWidth, int screenHeight){
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}
	/**
	 * flag=100
	 */
	@Override
	public void SetPosition(int flag, int screenWidth, int screenHeight) {

		if (flag == 100) {
			super.SetPosition(1, screenWidth, screenHeight);
			while (this.getPositionX() < (screenWidth / 3)) {

				super.SetPosition(1, screenWidth, screenHeight);
			}
		}
	}

	@Override
	public void SetSize(float maxSize) {
		super.SetSize(maxSize);
	}
	public void GetNextPosition() {

		if (time < T) {
			this.setPositionX(this.getPositionX()+this.speedX);
			this.setPositionY(this.getPositionY()+this.speedY);
			time++;
		} else if(time > 3*T){

			this.SetPosition(100, this.screenWidth, this.screenHeight);
			time = 0;
		}else{
			this.setPositionX(this.screenWidth);
			this.setPositionY(this.screenHeight);
			time++;
		}
	}
	/**
	 * 设置并且可以得到基本速度
	 * @return
	 */
	public float Speed(){
		
		speed = (float) Math.sqrt(this.screenWidth * this.screenWidth + this.screenHeight
				* this.screenHeight)/60;
		speedX = -speed*0.5f;
		speedY = speed*0.5f;
		return speed;
	}
	
	
}
