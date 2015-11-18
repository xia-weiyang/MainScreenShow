package com.jiusg.mainscreenshow.animation.starshine;

import java.util.Timer;
import java.util.TimerTask;

import com.jiusg.mainscreenshow.animation.starshine.Starshine;
import com.jiusg.mainscreenshow.animation.starshine.StarshineDynamic;
import com.jiusg.mainscreenshow.animation.starshine.StarshineMeteor;
import com.jiusg.mainscreenshow.animation.starshine.StarshineSetting;
import com.jiusg.mainscreenshow.base.C;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
public class StarshineView extends SurfaceView implements
		SurfaceHolder.Callback {

	public boolean mLoop = false;
	SurfaceHolder mSurfaceHolder = null;
	private Context mContext;
	private Paint mPaint;
	private Canvas canvas;
	private int allCount = 200;
	private int starCount;
	private int starDynamicCount;
	private int starMeteorCount = 6; // 流星个数
	private Starshine[] starS;
	private StarshineDynamic[] starSD;
	private StarshineMeteor[] starSM;
	private Timer timer;
	private Timer timerCuont;
	private Handler mHandler;
	private Matrix matrix;
	private int screenheight;
	private int screenwidth;
	private boolean starMeteorSwitch = false; // 流星开关
	private String TAG = "StarshineView";

	public StarshineView(Context context) {
		super(context);
	}

	public StarshineView(Context context, StarshineSetting ss) {
		super(context);
		this.mContext = context;
		mPaint = new Paint();
		getHolder().setFormat(PixelFormat.TRANSPARENT);
		mSurfaceHolder = getHolder();
		this.setFocusable(true);
		mHandler = new StarshineHandler();
		matrix = new Matrix();
		SharedPreferences sp_userinfo;
		sp_userinfo = context.getSharedPreferences("userinfo", 0);
		screenheight = sp_userinfo.getInt("screenheight", 0);
		screenwidth = sp_userinfo.getInt("screenwidth", 0);
		allCount = ss.getAllCount();
		starMeteorCount = ss.getStarMeteorCount();
		starMeteorSwitch = ss.isStarMeteorSwitch();
		SetCount(ss.getStyle());
		InitalStar(ss.getStyle());
		mSurfaceHolder.addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		final StarshineCountHandler countHandler = new StarshineCountHandler();
		TimerTask task1 = new TimerTask() {
			public void run() {

				Message msg = countHandler.obtainMessage();
//				msg.obj = ("run");
				countHandler.sendMessage(msg);
			}
		};
		timerCuont = new Timer(true);
		timerCuont.schedule(task1, 0, 35);

		TimerTask task = new TimerTask() {
			public void run() {

				Message msg = mHandler.obtainMessage();
				msg.obj = ("run");
				mHandler.sendMessage(msg);
			}
		};
		timer = new Timer(true);
		timer.schedule(task, 0, 1000 / C.FRAME);
		Log.i(TAG, "Starshine surfaceCreated and has start.");
		mLoop = true;

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		timer.cancel();
		timerCuont.cancel();
		Log.i(TAG, "Starshine surfaceDestroyed.");
		mLoop = false;
	}

	public void Draw() {

		if (mSurfaceHolder == null) 
			return;
		canvas = mSurfaceHolder.lockCanvas();
		if(canvas == null)
			return;
		canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR); // 清屏效果
		OnDraw(canvas);
		// 解锁画布
		mSurfaceHolder.unlockCanvasAndPost(canvas);
	}

	public void OnDraw(Canvas canvas) {

		
		for (int i = 0; i < starS.length; i++) {

			mPaint.setAlpha(starS[i].getAlpha());
			canvas.drawBitmap(starS[i].getBt(), starS[i].getPositionX(),
					starS[i].getPositionY(), mPaint);
		}
		for (int i = 0; i < starSD.length; i++) {

//			starSD[i].CountNextStarScale();
			matrix.reset();
			matrix.postScale(starSD[i].getScale(), starSD[i].getScale());
			Bitmap bt = Bitmap.createBitmap(starSD[i].getBt(), 0, 0,
					starSD[i].getWidth(), starSD[i].getHeight(), matrix, true);
			mPaint.setAlpha(starSD[i].getAlpha());
			// canvas.translate((starSD[i].getWidth()-bt.getWidth())/2,
			// (starSD[i].getHeight()-bt.getHeight())/2);
			canvas.drawBitmap(bt, starSD[i].getNPositionX(),
					starSD[i].getNPositionY(), mPaint);
			// canvas.translate(-(starSD[i].getWidth()-bt.getWidth())/2,-(
			// starSD[i].getHeight()-bt.getHeight())/2);
		}
		if (starMeteorSwitch) {
			
			for (int i = 0; i < starSM.length; i++) {

//				starSM[i].GetNextPosition();
				mPaint.setAlpha(starSM[i].getAlpha());
				canvas.drawBitmap(starSM[i].getBt(), starSM[i].getPositionX(),
						starSM[i].getPositionY(), mPaint);
			}
		}

	}

	/**
	 * 分配静态的星光与动态的星光的个数 9:1的比率
	 */
	private void SetCount(int style) {

		starCount = allCount / 10 * 9;
		starDynamicCount = allCount / 10;
		if(style == StarshineSetting.STARS){
			starCount = (int)(starCount*2);
		}
	}

	/**
	 * 初始化星光
	 */
	private void InitalStar(int style) {

		starS = new Starshine[starCount];
		for (int i = 0; i < starCount; i++) {

			Starshine ss = new Starshine();
			ss.setStyle(style);
			ss.SetBitmap(mContext, 1);
			ss.SetSize(0.15f);
			ss.SetPosition(0, screenwidth, screenheight);
			starS[i] = ss;
		}
		starSD = new StarshineDynamic[starDynamicCount];
		for (int i = 0; i < starSD.length; i++) {

			StarshineDynamic ssd = new StarshineDynamic();
			ssd.setStyle(style);
			ssd.SetBitmap(mContext, 2);
			ssd.SetSize(0.4f);
			ssd.SetPosition(0, screenwidth, screenheight);
			starSD[i] = ssd;
		}
		if (starMeteorSwitch) {
			starSM = new StarshineMeteor[starMeteorCount];
			for (int i = 0; i < starSM.length; i++) {

				StarshineMeteor ssm = new StarshineMeteor();
				ssm.setStyle(style);
				ssm.SetBitmap(mContext, 3);
				ssm.SetSize(0.4f);
				ssm.SetPosition(100, screenwidth, screenheight);
				ssm.SetScreenWidthHeight(screenwidth, screenheight);
				ssm.Speed();
				starSM[i] = ssm;
			}
		}
	}

	@SuppressLint("HandlerLeak")
	class StarshineHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			if (mLoop) {
				Draw();
				mHandler.removeCallbacksAndMessages(null);
			}
		}

	}

	class StarshineCountHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			for (int i = 0; i < starSD.length;i++)
				starSD[i].CountNextStarScale();
			for (int j = 0; j <starSM.length;j++)
				starSM[j].GetNextPosition();
		}
	}
	
	@Override
	protected void onAttachedToWindow() {
		if (getParent() != null)
			super.onAttachedToWindow();
	}
	
}
