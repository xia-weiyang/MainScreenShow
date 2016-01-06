package com.jiusg.mainscreenshow.ui;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.service.MSSService;
import com.jiusg.mainscreenshow.service.MSSService.MSSSBinder;
import com.jiusg.mainscreenshow.tools.PropertiesUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.youmi.android.offers.PointsManager;


public class PreviewAnimation extends Activity {

    private MSSService mssservice;
    private ServiceConnection mSc;
    private mHandler handler;
    private int Animation;
    private ImageView background;
    private Button button = null;
    private SharedPreferences anim;
    private float pointsBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        anim = getSharedPreferences("animation", MODE_PRIVATE);
        pointsBalance = PointsManager.getInstance(PreviewAnimation.this).queryPoints();
//        PointsManager.getInstance(PreviewAnimation.this).awardPoints(20);

        setContentView(R.layout.activity_previewanimation);
        background = (ImageView) findViewById(R.id.img_previewA);
        Button jc = (Button) findViewById(R.id.tv_previewA);
        button = (Button) findViewById(R.id.bt_previewA);

        Animation = getIntent().getExtras().getInt("animation");

        handler = new mHandler();

        if (Animation == C.ANIMATION_PICTUREWALL) {
            jc.setVisibility(View.VISIBLE);
        }

        jc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri
                        .parse("http://m.weibo.cn/3857031889/3749774257820866?sourceType=sms&from=1045515010&wm=9848_0009");
                intent.setData(content_url);
                startActivity(intent);
                mssservice.previewStopA();
            }
        });


        mSc = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                mssservice = ((MSSSBinder) service).getMSSService();
                Message msg = handler.obtainMessage();
                msg.obj = "start";
                handler.sendMessageDelayed(msg, 500);
            }


        };

        switch (Animation) {
            case C.ANIMATION_BUBBLE:
                background.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.preview_bubble_background));
                button.setText(getIsActivated(C.ANIMATION_BUBBLE));
                break;
            case C.ANIMATION_STARSHINE:
                background.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.preview_starshine_background));
                button.setText(getIsActivated(C.ANIMATION_STARSHINE));
                break;
            case C.ANIMATION_PICTUREWALL:
                background.setBackgroundColor(Color.WHITE);
                button.setText(getIsActivated(C.ANIMATION_PICTUREWALL));
                break;
            case C.ANIMATION_RAIN:
                background.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.preview_rain_background));
                button.setText(getIsActivated(C.ANIMATION_RAIN));
                break;
            case C.ANIMATION_SNOW:
                background.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.preview_snow_background));
                button.setText(getIsActivated(C.ANIMATION_SNOW));
                break;
            default:
                break;
        }

        background.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                finish();
            }
        });

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (Animation) {
                    case C.ANIMATION_BUBBLE:
                        showDialogAnim(C.ANIMATION_BUBBLE);
                        break;
                    case C.ANIMATION_PICTUREWALL:
                        showDialogAnim(C.ANIMATION_PICTUREWALL);
                        break;
                    case C.ANIMATION_RAIN:
                        showDialogAnim(C.ANIMATION_RAIN);
                        break;
                    case C.ANIMATION_STARSHINE:
                        showDialogAnim(C.ANIMATION_STARSHINE);
                        break;
                    case C.ANIMATION_SNOW:
                        showDialogAnim(C.ANIMATION_SNOW);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();
        Intent service = new Intent(PreviewAnimation.this, MSSService.class);
        this.bindService(service, mSc, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Bitmap bitmap = ((BitmapDrawable) (background.getDrawable())).getBitmap();
            recycleBitmap(bitmap);
        } catch (Exception e) {

        }
    }

    /**
     * 回收内存
     *
     * @param bitmap
     */
    private void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    private void showDialogAnim(final int an) {
        if(isActivated(an)) {
            new AlertDialog.Builder(this).setTitle(R.string.tip)
                    .setMessage("您可以在设置中设置该动画了!")
                    .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                        }
                    }).show();
        }else{
            new AlertDialog.Builder(this).setTitle(R.string.tip)
                    .setMessage("您目前有"+pointsBalance+"积分,可在设置>应用推荐中赚取积分!")
                    .setPositiveButton("现在激活", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int point = getPoint(an);
                            boolean isSuccess = PointsManager.getInstance(PreviewAnimation.this).spendPoints(point);
                            if(isSuccess){
                                anim.edit().putBoolean(PropertiesUtils.getAnimationInfo(getApplicationContext())[an],true).apply();
                                showDialogAnim(an);
                                button.setText(getIsActivated(an));
                            }else{
                                Toast.makeText(getApplicationContext(),"激活失败!",Toast.LENGTH_LONG).show();
                            }
                        }
                    }).setNegativeButton("算了",null)
                    .show();
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        mssservice.previewStopA();
        this.unbindService(mSc);
    }


    private String getIsActivated(int an){
        if(anim.getBoolean(PropertiesUtils.getAnimationInfo(getApplicationContext())[an],false)){
                return getString(R.string.action_activated);
        }

        return getString(R.string.action_no_activated)+"("+getPoint(an)+"积分)";
    }

    private Boolean isActivated(int an){
        if(anim.getBoolean(PropertiesUtils.getAnimationInfo(getApplicationContext())[an],false)){
            return true;
        }

        return false;
    }

    private int getPoint(int anim){
        switch (anim){
            case C.ANIMATION_SNOW:
                return 100;
            default:
                return 0;
        }
    }

    @SuppressLint("HandlerLeak")
    class mHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            mssservice.previewStartA(Animation);

        }


    }
}
