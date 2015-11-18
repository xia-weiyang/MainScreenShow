package com.jiusg.mainscreenshow.animation.bubble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.jiusg.mainscreenshow.R;

public class BubbleCount {

	private float ScreenHeight;
	private float ScreenWidth;
	private float Speed;
	private Context win;
	private Bubble[] bubbleCopy;
	private boolean IsAllNoCD;
	private BubbleSetting bs;
	private int px;

	public BubbleCount(float ScreenHeight, float ScreenWidth, Context win,
			BubbleSetting bs, int px) {

		this.px = px;
		this.ScreenHeight = ScreenHeight;
		this.ScreenWidth = ScreenWidth;
		this.win = win;
		this.bs = bs;
		this.Speed = (float) Math.sqrt(ScreenWidth * ScreenWidth + ScreenHeight
				* ScreenHeight) / 3;
		IsAllNoCD = false;
	}

	public float GetSpeed() {

		return (float) (Speed * 0.01);
	}


	public void PenBubble(Bubble b) {


		if (!b.isIsArea()) {

			if (b.getX() > -b.getR() && b.getX() < ScreenWidth - b.getR()
					&& b.getY() > -b.getR()
					&& b.getY() < ScreenHeight - b.getR())
				b.setIsArea(true);
		} else {


			if (b.getX() <= -b.getR() || b.getX() >= ScreenWidth - b.getR()) {

				b.getSpeed().setX((float) (-b.getSpeed().getX() * 0.85));
			}
			if (b.getY() <= -b.getR() || b.getY() >= ScreenHeight - b.getR()) {

				b.getSpeed().setY((float) (-b.getSpeed().getY() * 0.85));
			}
			while (!(b.getX() >= -b.getR() && b.getX() <= ScreenWidth
					- b.getR())) {

				b.setX(b.getX() + b.getSpeed().getX());

			}
			while (!(b.getY() >= -b.getR() && b.getY() <= ScreenHeight
					- b.getR())) {
				b.setY(b.getY() + b.getSpeed().getY());
			}

		}
	}

	public void GetNextBubble(Bubble[] bubble) {

		// if (count > 400)
		DuiPenBubble(bubble);
		// else
		// count++;
		for (int i = 0; i < bubble.length; i++) {

			if (bubble[i].getCount() > 410) {
				bubble[i].getSpeed().setX(
						(float) (bubble[i].getSpeed().getX() * 1.0025));
				bubble[i].getSpeed().setY(
						(float) (bubble[i].getSpeed().getY() * 1.0025));
				bubble[i].setX((float) (bubble[i].getX() + bubble[i].getSpeed()
						.getX()));
				bubble[i].setY((float) (bubble[i].getY() + bubble[i].getSpeed()
						.getY()));
				PenBubble(bubble[i]);
				if (bs.isChangeColor())
					Color(bubble[i]);
			} else {
				bubble[i].setCount(bubble[i].getCount() + 1);
			}
		}
	}


	public void DuiPenBubble(Bubble[] bubble) {

		/*
		还是新创建个Bubble数组，添加没有与其他气泡重叠的气泡对象，他们之间才可以碰撞，然后直到把所有的对象都加进来，那么好复杂....
		直接哪个对象的IsNoCD为true时，他们之间碰撞不就完了？
		每次都判断，麻烦，为何是数组，不是list？ 不过它们的原理都一样，只是list方便罢了。
		还是，来个成员，呵呵，都一样的..
		就从当前数组中，去掉为false的，然后设个值，都为true了，就不再进行这个操作。
		OK,就这样子！
		检测当前气泡是否与其它气泡没有重叠
		*/
		for (int i = 0; i < bubble.length; i++) {

			int flag = 0;

			if (!bubble[i].isIsNoCD()) {
				for (int j = 0; j < bubble.length; j++) {
					// 如果是本身就不能去判断
					if (!(i == j)) {

						if (((bubble[i].getX() - bubble[j].getX())
								* (bubble[i].getX() - bubble[j].getX()) + (bubble[i]
								.getY() - bubble[j].getY())
								* (bubble[i].getY() - bubble[j].getY())) > ((bubble[i]
								.getR() + bubble[j].getR()) * (bubble[i].getR() + bubble[j]
								.getR()))) {

							flag++;
						}
					}
				}
				if (flag >= (bubble.length - 1)) {

					bubble[i].setIsNoCD(true);
				}
			}
		}

		// 统计应该计算碰撞的气泡
		if (!IsAllNoCD) {
			int lenth = 0;
			for (int i = 0; i < bubble.length; i++) {

				if (bubble[i].isIsNoCD()) {
					lenth++;
				}
			}
			bubbleCopy = new Bubble[lenth];
			lenth = 0;
			for (int i = 0; i < bubble.length; i++) {

				if (bubble[i].isIsNoCD()) {
					bubbleCopy[lenth] = bubble[i];
					lenth++;
				}
			}
			if (lenth == bubble.length) {

				IsAllNoCD = true;
			}
		} else {

			bubbleCopy = bubble;
		}
		// 对可以碰撞的气泡进行碰撞操作
		int length = bubbleCopy.length;
		for (int i = 0; i < length; i++) {

			for (int k = i + 1; k < length; k++) {

				float x1 = bubbleCopy[i].getX();
				float y1 = bubbleCopy[i].getY();
				float x2 = bubbleCopy[k].getX();
				float y2 = bubbleCopy[k].getY();
				float r1 = bubbleCopy[i].getR();
				float r2 = bubbleCopy[k].getR();
				if ((((x1 + r1) - (x2 + r2)) * ((x1 + r1) - (x2 + r2)) + ((y1 + r1) - (y2 + r2))
						* ((y1 + r1) - (y2 + r2))) > (r1 + r2) * (r1 + r2))
					continue;
				float vx1 = bubbleCopy[i].getSpeed().getX();
				float vy1 = bubbleCopy[i].getSpeed().getY();
				float vx2 = bubbleCopy[k].getSpeed().getX();
				float vy2 = bubbleCopy[k].getSpeed().getY();

				/*
				 * if(y1==y2) { bubbleCopy[i].getSpeed().setX(vx2);
				 * bubbleCopy[k].getSpeed().setX(vx1); continue; }
				 */
				// 延球心连线和垂直建立坐标系
				float sx = (x1 - x2)
						/ (bubbleCopy[i].getR() + bubbleCopy[k].getR());
				float sy = (y1 - y2)
						/ (bubbleCopy[i].getR() + bubbleCopy[k].getR()); // 沿两球心方向的单位向量s
				float w = (-1 - x1 + x2) / (y1 - y2);
				float tx = (float) (1 / Math.sqrt(1 + w * w));
				float ty = (float) (w / Math.sqrt(1 + w * w)); // 垂直球心方向的单位向量t
				float p = (vx2 * sx) + (vy2 * sy); // i球在s方向上的投影
				float q = (vx1 * tx) + (vy1 * ty); // i球在t方向上的投影
				float ppx = p * sx;
				float ppy = p * sy; // i球沿s方向的分量
				float qqx = q * tx;
				float qqy = q * ty;
				bubbleCopy[i].getSpeed().setX((float) ((ppx + qqx) * 0.8));
				bubbleCopy[i].getSpeed().setY((float) ((ppy + qqy) * 0.8));

				sx = (x2 - x1) / (bubbleCopy[i].getR() + bubbleCopy[k].getR());
				sy = (y2 - y1) / (bubbleCopy[i].getR() + bubbleCopy[k].getR());
				w = (-1 - x2 + x1) / (y2 - y1);
				tx = (float) (1 / Math.sqrt(1 + w * w));
				ty = (float) (w / Math.sqrt(1 + w * w));
				p = (vx1 * sx) + (vy1 * sy);
				q = (vx2 * tx) + (vy2 * ty);
				ppx = p * sx;
				ppy = p * sy;
				qqx = q * tx;
				qqy = q * ty;
				bubbleCopy[k].getSpeed().setX((float) ((ppx + qqx) * 0.8));
				bubbleCopy[k].getSpeed().setY((float) ((ppy + qqy) * 0.8));

				while ((((bubbleCopy[i].getX() + r1) - (bubbleCopy[k].getX() + r2))
						* ((bubbleCopy[i].getX() + r1) - (bubbleCopy[k].getX() + r2)) + ((bubbleCopy[i]
						.getY() + r1) - (bubbleCopy[k].getY() + r2))
						* ((bubbleCopy[i].getY() + r1) - (bubbleCopy[k].getY() + r2))) < ((r1 + r2) * (r1 + r2))) {

					SetSpeed(bubbleCopy[i]);
					SetSpeed(bubbleCopy[k]);
					// bubble[i].setX(bubble[i].getX()
					// + bubble[i].getSpeed().getX());
					// bubble[i].setY(bubble[i].getY()
					// + bubble[i].getSpeed().getY());
					// bubble[k].setX(bubble[k].getX()
					// + bubble[k].getSpeed().getX());
					// bubble[k].setY(bubble[k].getY()
					// + bubble[k].getSpeed().getY());
					bubbleCopy[i].setX(bubbleCopy[i].getX()
							+ bubbleCopy[i].getSpeed().getX());
					bubbleCopy[i].setY(bubbleCopy[i].getY()
							+ bubbleCopy[i].getSpeed().getY());
					bubbleCopy[k].setX(bubbleCopy[k].getX()
							+ bubbleCopy[k].getSpeed().getX());
					bubbleCopy[k].setY(bubbleCopy[k].getY()
							+ bubbleCopy[k].getSpeed().getY());

				}
			}
		}
	}

	public void SetSpeed(Bubble b) {

		if (b.getX() <= -b.getR() || b.getX() >= ScreenWidth - b.getR()) {

			b.getSpeed().setX((float) (-b.getSpeed().getY() * 0.9));
		}
		if (b.getY() <= -b.getR() || b.getY() >= ScreenHeight - b.getR()) {

			b.getSpeed().setY((float) (-b.getSpeed().getY() * 0.9));
		}
	}

	public void Color(Bubble bubble) {

		if (bubble.getColor() >= 1500) {
			bubble.setColor(0);
			int i = 0;
			while (i > 10 | i == 0) {

				i = (int) ((int) 10 * Math.random());
			}
			switch (i) {
			case 1:
				bubble.setBubble(createBitmapBySize(
						((BitmapDrawable) win.getResources().getDrawable(
								R.drawable.bubble_blue)).getBitmap(), bubble));
				break;
			case 2:
				bubble.setBubble(createBitmapBySize(
						((BitmapDrawable) win.getResources().getDrawable(
								R.drawable.bubble_green)).getBitmap(), bubble));
				break;
			case 3:
				bubble.setBubble(createBitmapBySize(
						((BitmapDrawable) win.getResources().getDrawable(
								R.drawable.bubble_pink)).getBitmap(), bubble));
				break;
			case 4:
				bubble.setBubble(createBitmapBySize(
						((BitmapDrawable) win.getResources().getDrawable(
								R.drawable.bubble_purple)).getBitmap(), bubble));
				break;
			case 5:
				bubble.setBubble(createBitmapBySize(
						((BitmapDrawable) win.getResources().getDrawable(
								R.drawable.bubble_red)).getBitmap(), bubble));
				break;
			case 6:
				bubble.setBubble(createBitmapBySize(
						((BitmapDrawable) win.getResources().getDrawable(
								R.drawable.bubble_yellow)).getBitmap(), bubble));
				break;
			case 7:
				bubble.setBubble(createBitmapBySize(
						((BitmapDrawable) win.getResources().getDrawable(
								R.drawable.bubble_grey)).getBitmap(), bubble));
				break;
			case 8:
				bubble.setBubble(createBitmapBySize(
						((BitmapDrawable) win.getResources().getDrawable(
								R.drawable.bubble_qing)).getBitmap(), bubble));
				break;
			case 9:
				bubble.setBubble(createBitmapBySize(
						((BitmapDrawable) win.getResources().getDrawable(
								R.drawable.bubble_qianblue)).getBitmap(),
						bubble));
				break;
			case 10:
				bubble.setBubble(createBitmapBySize(
						((BitmapDrawable) win.getResources().getDrawable(
								R.drawable.bubble_qianpink)).getBitmap(),
						bubble));
				break;
			default:
				break;
			}
		} else {

			bubble.setColor(bubble.getColor() + 1);
		}
	}
	/**
	 * 得到具体大小的Bitmap
	 *
	 * @param bitmap
	 * @return
	 */
	public Bitmap createBitmapBySize(Bitmap bitmap, Bubble b) {

		int size;
		if (b != null) {
			size = GetSizeValue(b.getSize());
		} else {
			size = GetSizeValue(bs.getSize());
		}
		return Bitmap.createScaledBitmap(bitmap, size, size, true);
	}

	/**
	 * 得到Bitmap具体大小的值
	 *
	 * @param size
	 * @return
	 */
	public int GetSizeValue(String size) {

		if (size.equals(win.getResources().getString(R.string.bubble_size_muchBig))) {

			return (int) ((int) px * 1.2);
		} else if (size.equals(win.getResources().getString(R.string.bubble_size_big))) {

			return px;
		} else if (size.equals(win.getResources().getString(R.string.bubble_size_small))) {

			return (int) ((int) px * 0.8);
		} else if (size.equals(win.getResources().getString(R.string.bubble_size_muchSmall))) {

			return (int) ((int) px * 0.6);
		} else
			return px;
	}


}
