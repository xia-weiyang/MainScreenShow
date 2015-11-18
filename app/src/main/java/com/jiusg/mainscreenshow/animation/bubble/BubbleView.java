package com.jiusg.mainscreenshow.animation.bubble;

import java.util.Timer;
import java.util.TimerTask;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.animation.bubble.Bubble;
import com.jiusg.mainscreenshow.animation.bubble.BubbleCount;
import com.jiusg.mainscreenshow.animation.bubble.BubbleSetting;
import com.jiusg.mainscreenshow.animation.bubble.BubbleSpeed;
import com.jiusg.mainscreenshow.base.C;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
 * @author zk
 * @deprecated
 * 
 */

public class BubbleView extends SurfaceView implements SurfaceHolder.Callback {

	private Paint mPaint;
	private Bubble[] bubble;
	private BubbleCount cb;
	private int screenheight;
	private int screenwidth;
	private BubbleSetting bs;
	private SurfaceHolder mSurfaceHolder = null;
	private Canvas canvas;
	private Timer timer;
	private Timer timerCuont;
	private Handler mHandler;
	public boolean mLoop;
	private final String TAG = "BubbleView";
	private int px;

	public BubbleView(Context context) {
		super(context);
		getHolder().setFormat(PixelFormat.TRANSPARENT);
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		this.setFocusable(true);
		mHandler = new BubbleHandler();
		mLoop = false;

	}

	public BubbleView(Context context, BubbleSetting bs) {
		super(context);
		getHolder().setFormat(PixelFormat.TRANSPARENT);
		mSurfaceHolder = getHolder();
		this.setFocusable(true);
		mHandler = new BubbleHandler();
		this.bs = bs;
		mPaint = new Paint();
		mPaint.setAlpha((int) (255 * bs.getAlpha()));
		getPX();
		SharedPreferences sp_userinfo;
		sp_userinfo = context.getSharedPreferences("userinfo", 0);
		screenheight = sp_userinfo.getInt("screenheight", 0);
		screenwidth = sp_userinfo.getInt("screenwidth", 0);
		cb = new BubbleCount(screenheight, screenwidth, context, bs,px);
		mLoop = false;
		Bubble();
		mSurfaceHolder.addCallback(this);
	}

	public void Draw() {

		// long startTime=System.currentTimeMillis();
		// cb.GetNextBubble(bubble);

		if (mSurfaceHolder == null)
			return;
		canvas = mSurfaceHolder.lockCanvas();
		if (canvas == null)
			return;
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR); // 清屏效果
		for (int i = 0; i < bubble.length; i++) {

			DrawImage(canvas, bubble[i]);
		}
		mSurfaceHolder.unlockCanvasAndPost(canvas);
		// System.out.println(System.currentTimeMillis()-startTime);
	}

	private void DrawImage(Canvas canvas, Bubble b) {

		canvas.drawBitmap(b.getBubble(), b.getX(), b.getY(), mPaint);
		if (bs.isShadow()) {
			// if (bs.getSize().equals("随机")) {
			// bubble_background = createBitmapBySize(
			// ((BitmapDrawable) getResources().getDrawable(
			// R.drawable.bubble_background)).getBitmap(), b);
			// }
			canvas.drawBitmap(b.getBubbleShadow(),
					(float) (b.getX() + (0.1 * b.getR())),
					(float) (b.getY() + (0.3 * b.getR())), mPaint);
		}
	}

	/**
	 * 实例化所有的Bubble 给每个bubble设定初始值，并存入bubble数组中
	 */
	private void Bubble() {

		int j = 1;
		bubble = new Bubble[bs.getNumber()];
		for (int i = 0; i < bubble.length; i++) {

			j = -j;
			Bubble b = new Bubble();
			if (!this.bs.getSize().equals(getResources().getString(R.string.bubble_size_random))) {
				b.setSize(this.bs.getSize());
			} else {
				b.setSize(getRandomSize());
			}
			b.setBubble(Getcolor(b, i + 1));
			b.setBubbleShadow(createBitmapBySize(
					((BitmapDrawable) getResources().getDrawable(
							R.drawable.bubble_background)).getBitmap(), b));
			BubbleSpeed bs = new BubbleSpeed();
			bs.setX((float) (cb.GetSpeed() * 0.1 * i * j));
			bs.setY((float) (-cb.GetSpeed() * (1.5 - (i * 0.1))));
			b.setSpeed(bs);
			b.setIsArea(false);
			b.setIsNoCD(false);
			b.setR(b.getBubble().getWidth() / 2);
			b.setX(screenwidth / 2 - b.getR());
			b.setY(screenheight);
			// bubble_9.setCount((int) ((int) 400 * Math.random()));
			b.setCount(400 - i * 40);
			b.setColor((int) ((int) 500 * Math.random()));
			b.setFalg(true);
			bubble[i] = b;
		}

	}

	/**
	 * 随机获取颜色
	 * 
	 * @param bubble
	 * @return
	 */
	public Bitmap Getcolor(Bubble bubble, int num) {

		Bitmap b = null;
		int i = 0;
		if (bs.isChangeColor()) {
			while (i > 10 | i == 0) {

				i = (int) ((int) 10 * Math.random());
			}
		} else {
			i = num;

		}
		switch (i) {
		case 1:
			b = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.bubble_blue)).getBitmap();
			break;
		case 2:
			b = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.bubble_green)).getBitmap();
			break;
		case 3:
			b = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.bubble_pink)).getBitmap();
			break;
		case 4:
			b = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.bubble_purple)).getBitmap();
			break;
		case 5:
			b = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.bubble_red)).getBitmap();
			break;
		case 6:
			b = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.bubble_yellow)).getBitmap();
			break;
		case 7:
			b = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.bubble_grey)).getBitmap();
			break;
		case 8:
			b = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.bubble_qing)).getBitmap();
			break;
		case 9:
			b = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.bubble_qianblue)).getBitmap();
			break;
		case 10:
			b = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.bubble_qianpink)).getBitmap();
			break;
		default:
			b = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.bubble_blue)).getBitmap();
			break;
		}

		b = createBitmapBySize(b, bubble);

		return b;
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

		if (size.equals(getResources().getString(R.string.bubble_size_muchBig))) {

			return (int) ((int) px * 1.2);
		} else if (size.equals(getResources().getString(R.string.bubble_size_big))) {

			return px;
		} else if (size.equals(getResources().getString(R.string.bubble_size_small))) {

			return (int) ((int) px * 0.8);
		} else if (size.equals(getResources().getString(R.string.bubble_size_muchSmall))) {

			return (int) ((int) px * 0.6);
		} else
			return px;
	}

	private String getRandomSize() {

		int i = (int) ((int) 10 * Math.random());

		if (i > 0 && i <= 2)
			return getResources().getString(R.string.bubble_size_muchSmall);
		else if (i > 2 && i <= 4)
			return getResources().getString(R.string.bubble_size_small);
		else if (i > 4 && i <= 6)
			return getResources().getString(R.string.bubble_size_big);
		else if (i > 6 && i <= 8)
			return getResources().getString(R.string.bubble_size_muchBig);
		else
			return getRandomSize();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// new Thread(this).start();
		mLoop = true;
		final BubbleCountHandler countHandler = new BubbleCountHandler();

		Message msg = countHandler.obtainMessage();
		msg.what = 1;
		TimerTask task1 = new TimerTask() {
			public void run() {

				Message msg = countHandler.obtainMessage();
				msg.obj = ("run");
				countHandler.sendMessage(msg);
			}
		};
		timerCuont = new Timer(true);
		timerCuont.schedule(task1, 0, 50 - (bs.getBubbleSpeed() * 4));

		TimerTask task = new TimerTask() {
			public void run() {

				Message msg = mHandler.obtainMessage();
				msg.obj = ("run");
				mHandler.sendMessage(msg);
			}
		};
		timer = new Timer(true);
		timer.schedule(task, 0, 1000 / C.FRAME);
		Log.i(TAG, "Bubble surfaceCreated and has start.");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mLoop = false;
		timer.cancel();
		timerCuont.cancel();
		Log.i(TAG, "Bubble surfaceDestroyed.");

	}

	@SuppressLint("HandlerLeak")
	class BubbleCountHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			cb.GetNextBubble(bubble);
		}

	}

	@SuppressLint("HandlerLeak")
	class BubbleHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			if (mLoop) {

				Draw();
				mHandler.removeCallbacksAndMessages(null);
			}

		}

	}

	/**
	 * 得到bubble的实际大小px
	 */
	private void getPX() {

		Bitmap b = ((BitmapDrawable) getResources().getDrawable(
				R.drawable.bubble_blue)).getBitmap();
		px = b.getWidth();
		Log.i(TAG, "px=" + px);
	}

/*	@Override
	protected void onAttachedToWindow() {
		if (getParent() != null)
			super.onAttachedToWindow();
	}*/

}
