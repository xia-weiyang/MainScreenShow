package com.jiusg.mainscreenshow.animation.picturewall;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.jiusg.mainscreenshow.animation.picturewall.PictureWall;
import com.jiusg.mainscreenshow.animation.picturewall.PictureWallSetting;
import com.jiusg.mainscreenshow.base.C;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @deprecated
 */
public class PictureWallView extends SurfaceView implements
		SurfaceHolder.Callback {
	public boolean mLoop = false;
	public boolean isFrame = true;
	private int frame = 0; // 帧数
	private SurfaceHolder mSurfaceHolder = null;
	private Paint mPaint;
	private int milliSecond = 0;
	private Canvas mCanvas;
	private float scale = 0;
	private Handler mHandler;
	private Timer timer;
	private PictureWall pw;
	private Timer timerPeriod;
	private Handler periodHandler;
	private int count = 0;
	private int alpha = 255;
	private int period = 50;
	private boolean onlyOne = false; // 只播放一张动画
	private int BITMAP_TYPE = PictureWall.BITMAP_TYPE_RANDOM;
	private String TAG = "PictureWallView";

	public int getFrame() {
		return frame;
	}

	public PictureWallView(Context context, PictureWallSetting pws) {
		super(context);
		alpha = pws.getAlpha();
		// 这里除以0.8是因为有0.2的时间用来了渐变，目的是让时间还原
		period = (int) ((float)pws.getPeriod()/0.8);
		onlyOne = pws.isOnlyOne();
		scale = pws.getScaleV();
		BITMAP_TYPE = pws.getBITMAP_TYPE();
		mPaint = new Paint();
		getHolder().setFormat(PixelFormat.TRANSPARENT);
		mSurfaceHolder = getHolder();
		this.setFocusable(true);
		mPaint.setAlpha(alpha);
		mHandler = new mHandler();
		periodHandler = new periodHandler();
		SharedPreferences sp_userinfo;
		sp_userinfo = context.getSharedPreferences("userinfo", 0);
		int screenheight = sp_userinfo.getInt("screenheight", 0);
		int screenwidth = sp_userinfo.getInt("screenwidth", 0);
		pw = new PictureWall(screenwidth, screenheight, period, alpha, BITMAP_TYPE, scale);
		if (onlyOne) {
			pw.setRatio(pw.getPeriod() / 3 * 2);
			pw.setRationBL(1.1f);
		}
		mSurfaceHolder.addCallback(this);
	}

	private void Draw() {

		if (mSurfaceHolder == null) 
			return;
		mCanvas = mSurfaceHolder.lockCanvas();
		if(mCanvas == null)
			return;
		// 清屏
		mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		mDraw(mCanvas);
		mSurfaceHolder.unlockCanvasAndPost(mCanvas);
	}

	/**
	 * 具体绘制的方法
	 * 
	 * @param mCanvas
	 */
	private void mDraw(final Canvas mCanvas) {
		pw.getScale();
		// 这里进行透明度判断，主要是解决画图顺序的问题
		if (pw.getAlphaB1() > pw.getAlphaB2()) {
			if (pw.isBitmap1()) {
				mPaint.setAlpha(pw.getAlphaB1());
				if (onlyOne)
					if (count >= period)
						mPaint.setAlpha(0);
				mCanvas.drawBitmap(pw.getBitmap1(), pw.getPositionB1X(),
						pw.getPositionB1Y(), mPaint);
			}
			if (pw.isBitmap2()) {
				mPaint.setAlpha(pw.getAlphaB2());
				if (onlyOne)
					mPaint.setAlpha(0);
				mCanvas.drawBitmap(pw.getBitmap2(), pw.getPositionB2X(),
						pw.getPositionB2Y(), mPaint);
			}
		} else {
			if (pw.isBitmap2()) {
				mPaint.setAlpha(pw.getAlphaB2());
				if (onlyOne)
					mPaint.setAlpha(0);
				mCanvas.drawBitmap(pw.getBitmap2(), pw.getPositionB2X(),
						pw.getPositionB2Y(), mPaint);
			}
			if (pw.isBitmap1()) {
				mPaint.setAlpha(pw.getAlphaB1());
				if (onlyOne)
					if (count >= period)
						mPaint.setAlpha(0);
				mCanvas.drawBitmap(pw.getBitmap1(), pw.getPositionB1X(),
						pw.getPositionB1Y(), mPaint);
			}
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// 在有图片的资源下再开始执行动画
		if (pw.getUrlBitmap().length > 0) {
			TimerTask task = new TimerTask() {

				@Override
				public void run() {

					Message msg = mHandler.obtainMessage();
					msg.obj = "timer";
					mHandler.sendMessage(msg);
				}
			};
			timer = new Timer(true);
			timer.schedule(task, 0, 1000 / C.FRAME);
			TimerTask task1 = new TimerTask() {

				@Override
				public void run() {

					Message msg = mHandler.obtainMessage();
					msg.obj = "period";
					periodHandler.sendMessage(msg);
				}
			};
			timerPeriod = new Timer(true);
			timerPeriod.schedule(task1, 0, 100);
			Log.i(TAG, "PictureWallView surfaceCreated and has start.");
			mLoop = true;
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		if (pw.getUrlBitmap().length > 0){
		timer.cancel();
		timerPeriod.cancel();
		Log.i(TAG, "PictureWallView surfaceDestroyed.");
		mLoop = false;
		}
		pw = null;
	}

	@SuppressLint("HandlerLeak")
	class mHandler extends Handler {

		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {

			if (mLoop) {
				if (onlyOne)
					if (count >= period)
						timer.cancel();

				Draw();
				if (isFrame)
					countFrame();
				mHandler.removeCallbacksAndMessages("timer");
			}
		}

	}

	@SuppressLint("HandlerLeak")
	class periodHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			if (mLoop) {
				if (pw.isBitmap1())
					pw.setTimeB1(pw.getTimeB1() + 1);
				if (pw.isBitmap2())
					pw.setTimeB2(pw.getTimeB2() + 1);
				count++;
				// System.out.println(frame);
			}

		}

	}

	private void countFrame() {
		Calendar cld = Calendar.getInstance();
		int nextmilliSecond = cld.get(Calendar.MILLISECOND)
				+ (cld.get(Calendar.SECOND) * 1000);
		if (milliSecond == 0) {
			milliSecond = nextmilliSecond;
			return;
		}
		if ((nextmilliSecond - milliSecond) <= 0) {
			milliSecond = nextmilliSecond;
			return;
		}
		frame = (1000) / (nextmilliSecond - milliSecond);
		milliSecond = nextmilliSecond;
	}
	
	@Override
	protected void onAttachedToWindow() {
		if (getParent() != null)
			super.onAttachedToWindow();
	}
}
