package com.jiusg.mainscreenshow.ui;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.process.SetAdapter;
import com.jiusg.mainscreenshow.service.MSSLiveWallpaper;
import com.jiusg.mainscreenshow.tools.PropertiesUtils;
import com.jiusg.mainscreenshow.tools.SmartBarUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class DesktopSet extends BaseSet {

    private DesktopAdapter dA;
    private ListView lv;
    private SharedPreferences sp_date;
    private SharedPreferences sp_setA; // 用来存储该事件对应的动画信息
    private final int VALUE = C.EVENT_DESKTOP;
    private String eventName = "";
    private ProgressDialog pd = null;
    private final int MSG_ADD = 1;
    private DesktopHandler handler = null;
    private final int RESULT_LOAD_IMAGE = 1;
    private boolean isStart = false;  // 是否启用了桌面动画
    private String picturePath = "";
    public static final String backPath = getPath();
    public static final String backName = "background.jpg";

    /**
     * 事件设置的长度 *
     */
    private final int SETLENGHT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mssevent);

        lv = (ListView) findViewById(R.id.list_mss);

        sp_date = getSharedPreferences("date", 0);
        isStart = isStart();
        eventName = sp_date.getString("start" + VALUE, "error");

        handler = new DesktopHandler();
        dA = new DesktopAdapter();
        lv.setAdapter(dA);

        setAdapter(dA);
        setVALUE(VALUE);
        setSetLenght(SETLENGHT);
        setListLength(initializeListLenght());
        setHanlder(new mHanler());

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {

                switch (position) {
                    case 3:
                        showEventBack();
                        break;
                    case 4:
                        showAnimation();
                        break;
                    case 6:
                        showPictureWall_ChoosePicture();
                        showRain_Style();
                        showSnow_Style();
                        break;
                    case 7:
                        showStarShine_Style();
                        showPictureWall_PlayOrder();
                        showRain_Amount();
                        showSnow_Amount();
                        break;
                    case 8:
                        showBubble_Size();
                        showStarsShine_Shars();
                        break;
                    case 9:
                        showStarsShine_Starmeteors();
                        break;
                    default:
                        break;
                }
            }
        });

    }


    class DesktopAdapter extends BaseAdapter implements SetAdapter {

        @Override
        public int getCount() {

            return getListLength();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(DesktopSet.this);
            if (position == 0 || position == getListLength() - 1)

                convertView = inflater.inflate(R.layout.list_alpha, parent,
                        false);
            else if (position == 2 || position == 5) {

                convertView = inflater.inflate(R.layout.list_alpha, parent,
                        false);
                TextView title = (TextView) convertView
                        .findViewById(R.id.tv_alpha_title);
                switch (position) {
                    case 2:
                        title.setText(R.string.title_base);
                        break;
                    case 5:
                        title.setText(R.string.title_anomation);
                        break;
                    default:
                        break;
                }
            } else {

                convertView = inflater.inflate(R.layout.list_msseventset,
                        parent, false);
                switch (position) {
                    case 1:
                        showEventStartForDesktop(convertView, getString(R.string.event_desktop) + getString(R.string.animation), isStart);
                        break;
                    case 3:
                        showEventBackTitle(convertView, getString(R.string.setting_stop));
                        break;
                    case 4:
                        showAnimationTitle(convertView, getString(R.string.setting_animation));
                        break;
                    case 6:
                        showBubble_Color(convertView);
                        showStarsShine_StaemeteorSwitch(convertView);
                        showPictureWall_ChoosePictureTitle(convertView);
                        showRain_StyleInfo(convertView);
                        showSnow_StyleInfo(convertView);
                        break;
                    case 7:
                        showBubble_Shadow(convertView);
                        showStarShine_StyleInfo(convertView);
                        showPictureWall_PlayOrderInfo(convertView);
                        showRain_AmountInfo(convertView);
                        showSnow_AmountInfo(convertView);
                        break;
                    case 8:
                        showBubble_SizeInfo(convertView);
                        showStarsShine_SharsInfo(convertView);
                        showPictureWall_Period(convertView);
                        showRain_Speed(convertView);
                        showSnow_Speed(convertView);
                        break;
                    case 9:
                        showBubble_Number(convertView);
                        showStarsShine_StarmeteorsInfo(convertView);
                        showPictureWall_Alpha(convertView);
                        showRain_Alpha(convertView);
                        showSnow_Alpha(convertView);
                        break;
                    case 10:
                        showBubble_Alpha(convertView);
                        showPictureWall_ScaleSpeed(convertView);
                        break;
                    case 11:
                        showBubble_Speed(convertView);
                        break;
                    default:
                        break;
                }
            }
            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {

            if (position == 0 || position == getListLength() - 1
                    || position == 2 || position == 5)
                return false;
            return true;
        }

    }

    private void clearDate() {

        sp_date.edit()
                .putString("start" + VALUE,
                        PropertiesUtils.GetEventInfo(VALUE, DesktopSet.this)[0])
                .apply();
        sp_date.edit().putString("state" + VALUE, getString(R.string.action_started)).commit();
        sp_date.edit()
                .putString("animation" + VALUE,
                        PropertiesUtils.getAnimationInfo(DesktopSet.this)[0])
                .apply();
        sp_date.edit()
                .putString("end" + VALUE,
                        PropertiesUtils.GetEventInfo(VALUE, DesktopSet.this)[1])
                .apply();
        sp_date.edit().putInt(eventName + "time", 5).apply();

        for (int i = 0; i < PropertiesUtils.getAnimationInfo(DesktopSet.this).length; i++) {

            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils.getAnimationInfo(DesktopSet.this)[i],
                    0);
            sp_setA.edit().clear().apply();
        }

        setListLength(initializeListLenght());
        dA.notifyDataSetChanged();

    }

    @SuppressLint("HandlerLeak")
    class mHanler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if (msg.obj.toString().equals("clear")) {
                clearDate();
                dialogDismiss();
            }
        }

    }

    private boolean isStart() {
        String packageName = "";
        WallpaperManager manager = WallpaperManager.getInstance(this);
        try {
            packageName = manager.getWallpaperInfo().getPackageName();
            if (packageName.equals(getPackageName()))
                return true;
        } catch (Exception e) {

        }
        return false;
    }


    /**
     * show Title 设置背景
     *
     * @param convertView
     * @param title
     */
    protected void showEventBackTitle(View convertView, String title) {

        TextView title_ = (TextView) convertView
                .findViewById(R.id.tv_msseventset_title);
        TextView tvSet = (TextView) convertView
                .findViewById(R.id.tv_msseventset_setting);
        title_.setText("设置背景");
        tvSet.setVisibility(View.VISIBLE);
        tvSet.setText(sp_date.getString("background" + VALUE, "默认"));
    }

    /**
     * show AlertDialog 背景
     */
    protected void showEventBack() {

        final String[] s = {"默认", "自定义"};
        int x = 0;
        x = 0;
        for (int i = 0; i < s.length; i++) {

            if (sp_date.getString("background" + VALUE, "默认").equals(s[i]))
                x = i;
        }
        new AlertDialog.Builder(DesktopSet.this).setTitle(R.string.action_ChoosePlease)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setItems(s, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case 0:
                                sp_date.edit().putString("background" + VALUE, "默认")
                                        .apply();
                                getAdapter().notifyDataSetChanged();
                                break;
                            case 1:
                                Intent i = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, RESULT_LOAD_IMAGE);
                                break;
                            default:
                                break;
                        }


                    }
                }).show();
    }

    public static String getPath() {
        String path = Environment.getExternalStorageDirectory().getPath()
                + "/MSShow/Event/Desktop/Background/";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStart = isStart();
        if (isStart) {
            listLength = setLenght + baseLenght + animationLenght;
        } else {
            listLength = baseLenght;
        }
        getAdapter().notifyDataSetChanged();
        Intent it = new Intent(this, MSSLiveWallpaper.class);
        it.setFlags(MSSLiveWallpaper.ANIMATION);
        startService(it);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            this.picturePath = picturePath;
            File file = new File(picturePath);
            if (file.exists()) {
                pd = ProgressDialog.show(DesktopSet.this, null,
                        getString(R.string.action_loading), true);
                Message msg = handler.obtainMessage();
                msg.what = MSG_ADD;
                handler.sendMessageDelayed(msg, 500);
            } else {

            }
        }
    }

    class DesktopHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_ADD) {
                SharedPreferences sp_userinfo = getSharedPreferences("userinfo", 0);
                int screenwidth = sp_userinfo.getInt("screenwidth", 0);
                try {
                    Bitmap bitmap = PictureWallChoose.decodeSampledBitmapFromFile(picturePath, (int) (screenwidth * 1.2));
                    bitmap = PictureWallChoose.degreeBitmap(picturePath, bitmap);
                    PictureWallChoose.saveJpgFile(bitmap, backName, backPath);
                    sp_date.edit().putString("background" + VALUE,"自定义").apply();
                } catch (Exception e) {
                    showToast("自定义背景失败!");
                }
                if(pd!=null&&pd.isShowing()){
                    pd.dismiss();
                }
                getAdapter().notifyDataSetChanged();
            }
        }
    }
}


