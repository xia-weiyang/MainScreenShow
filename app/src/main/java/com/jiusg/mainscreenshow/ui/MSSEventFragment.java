package com.jiusg.mainscreenshow.ui;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.tools.UpdateVersion;

/**
 * Created by zk on 2015/9/27.
 */
public class MSSEventFragment extends Fragment implements View.OnTouchListener {

    private RelativeLayout main = null;
    private ImageView img = null;

    private SharedPreferences sp_date;
    private SharedPreferences sp_userinfo; // 存储基本数据
    private SharedPreferences sp_tip;
    private AlertDialog dialogUpdate = null;
    private MSSEventHandler hd = null;

    private boolean isImg = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp_date = getActivity().getSharedPreferences("date", 0);
        sp_tip = getActivity().getSharedPreferences("tip", 0);
        sp_userinfo = getActivity().getSharedPreferences("userinfo", 0);
        hd = new MSSEventHandler();
        initData();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main = (RelativeLayout) inflater.inflate(R.layout.fragment_mssevent, container, false);
        img = (ImageView) main.findViewById(R.id.img_mss);
        img.setOnTouchListener(this);
        return main;
    }

    /**
     * 启动应用后需要初始化一些数据
     */
    private void initData() {
        // 写入免费版
        if (C.ISFREE) {
            sp_userinfo.edit().putString("UserVersionInfo", C.VERSION_FREE)
                    .commit();
        }

        if (C.ISUPDATE) {
            dialogUpdate = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.action_updateRecord).setMessage("")
                    .setNegativeButton(R.string.action_ok, null).show();
            Message msg = hd.obtainMessage();
            msg.what = 0;
            hd.sendMessageDelayed(msg, 500);
            C.ISUPDATE = false;
        }

        if (!sp_tip.getBoolean("Help", false)) {

            new AlertDialog.Builder(getActivity()).setTitle(R.string.tip)
                    .setMessage(R.string.tip_msg_help)
                    .setPositiveButton(R.string.action_look, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent();
                            intent.setClass(getActivity(), Help.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton(R.string.action_notip, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    sp_tip.edit().putBoolean("Help", true).commit();
                }
            }).show();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.img_mss:
                int lastX = 0;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        disposeClickImg(clickImg(event));
                        lastX = (int) event.getRawX();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isImg) {
                            disposeClickImg(C.EVENT_NO);
                        }
                        switch (clickImg(event)) {
                            case C.EVENT_DESKTOP:
                                startActivity(new Intent().setClass(getActivity(), DesktopSet.class));
                                break;
                            case C.EVENT_LOCKSCREEN:
                                startActivity(new Intent().setClass(getActivity(), LockScreenSet.class));
                                break;
                            case C.EVENT_CALL:
                                startActivity(new Intent().setClass(getActivity(), CallSet.class));
                                break;
                            case C.EVENT_SMS:
                                startActivity(new Intent().setClass(getActivity(), SMSSet.class));
                                break;
                            case C.EVENT_CHARGING:

                                startActivity(new Intent().setClass(getActivity(), CharingSet.class));
                                break;
                            default:
                                break;
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if (!isImg) {
                            disposeClickImg(C.EVENT_NO);
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void disposeClickImg(int event) {
        Bitmap bitmap = ((BitmapDrawable) (img.getDrawable())).getBitmap();
        switch (event) {
            case C.EVENT_DESKTOP:
                isImg = false;
                img.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mssb_desktop));
                recycleBitmap(bitmap);
                break;
            case C.EVENT_CALL:
                isImg = false;
                img.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mssb_call));
                recycleBitmap(bitmap);
                break;
            case C.EVENT_SMS:
                isImg = false;
                img.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mssb_sms));
                recycleBitmap(bitmap);
                break;
            case C.EVENT_CHARGING:
                isImg = false;
                img.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mssb_chargin));
                recycleBitmap(bitmap);
                break;
            case C.EVENT_LOCKSCREEN:
                isImg = false;
                img.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mssb_lockscreen));
                recycleBitmap(bitmap);
                break;
            case C.EVENT_NO:
                isImg = true;
                img.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mssb));
                recycleBitmap(bitmap);
                break;
            default:
                break;
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

    /**
     * 计算点击的是哪个事件
     *
     * @param event
     * @return
     */
    private int clickImg(MotionEvent event) {
        float x = event.getX() / img.getWidth();
        float y = event.getY() / img.getHeight();
        if (y < 738f / 1698f && x < countImg(y, 0))
            return C.EVENT_DESKTOP;
        if (y < 485f / 1698f && x > countImg(y, 0))
            return C.EVENT_LOCKSCREEN;
        if (y > 738f / 1698f && x < countImg(y, 0))
            return C.EVENT_CALL;
        if (y > 485f / 1698f && x > countImg(y, 0) && x > countImg(y, 1))
            return C.EVENT_SMS;
        if (x < countImg(y, 1) && x > countImg(y, 0))
            return C.EVENT_CHARGING;
        return -1;
    }

    private float countImg(float y, int type) {
        float x = 0;
        switch (type) {
            case 0:
                return (764f - 484f * y) / 1080f;
            case 1:
                return (1698f * y - 432f) / 1266f;
            default:
                break;
        }
        return x;
    }

    /**
     * 检查更新版本操作
     */
    private void checkUpdate() {

        final UpdateVersion updateVersion = new UpdateVersion(getActivity());
        if (updateVersion.isTipVersion()
                && !updateVersion.getNewVersionName().equals(
                updateVersion.getVersionName())) {
            updateVersion.showUpdate();
        }
    }

    class MSSEventHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0) {

                dialogUpdate.setMessage(Setting.getUpdateInfo(getActivity()));
            } else if (msg.what == 3) {

                if (((MSS) getActivity()).mssservice != null) {
                    ((MSS) getActivity()).mssservice.canclaNotify();
                    ((MSS) getActivity()).mssservice.loadEvent();
                }

            }
        }
    }
}
