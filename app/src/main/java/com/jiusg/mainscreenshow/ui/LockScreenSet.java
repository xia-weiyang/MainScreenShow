package com.jiusg.mainscreenshow.ui;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.tools.PropertiesUtils;
import com.jiusg.mainscreenshow.tools.SmartBarUtils;

public class LockScreenSet extends BaseSet {

    private final int VALUE = C.EVENT_LOCKSCREEN;
    private String eventName;
    private LockScreenAdapter lsA;
    private ListView lv;
    private SharedPreferences sp_date;
    private SharedPreferences sp_setA;

    /**
     * 事件设置的长度 *
     */
    private final int SETLENGHT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mssevent);

        SmartBarUtils.setBackIcon(getActionBar(),
                getResources().getDrawable(R.drawable.ic_back));

        lv = (ListView) findViewById(R.id.list_mss);

        sp_date = getSharedPreferences("date", 0);
        eventName = sp_date.getString("start" + VALUE, "error");

        lsA = new LockScreenAdapter();
        lv.setAdapter(lsA);

        setAdapter(lsA);
        setVALUE(VALUE);
        setSetLenght(SETLENGHT);
        setListLength(initializeListLenght());
        setHanlder(new mHandler());

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {

                switch (position) {
                    case 3:
                        showEventEnd();
                        break;
                    case 4:
                        showAnimation();
                        break;
                    case 6:
                        showRain_Style();
                        break;
                    case 7:
                        showStarShine_Style();
                        showPictureWall_ChoosePicture();
                        showRain_Amount();
                        break;
                    case 8:
                        showBubble_Size();
                        showStarsShine_Shars();
                        showPictureWall_PlayOrder();
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

    class LockScreenAdapter extends BaseAdapter implements
            com.jiusg.mainscreenshow.process.SetAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return getListLength();
        }

        @Override
        public Object getItem(int arg0) {
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

            LayoutInflater inflater = LayoutInflater.from(LockScreenSet.this);
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
                    default:
                        break;
                }
            } else {

                convertView = inflater.inflate(R.layout.list_msseventset,
                        parent, false);
                switch (position) {
                    case 1:
                        showEventStart(convertView, getString(R.string.event_lockScreen) + getString(R.string.animation));
                        break;
                    case 3:
                        showEventEndTitle(convertView, getString(R.string.setting_stop));
                        break;
                    case 4:
                        showAnimationTitle(convertView, getString(R.string.setting_animation));
                        break;
                    case 6:
                        showBubble_Color(convertView);
                        showStarsShine_StaemeteorSwitch(convertView);
                        showPictureWall_LockScreen(convertView);
                        showRain_StyleInfo(convertView);
                        break;
                    case 7:
                        showBubble_Shadow(convertView);
                        showStarShine_StyleInfo(convertView);
                        showPictureWall_ChoosePictureTitle(convertView);
                        showRain_AmountInfo(convertView);
                        break;
                    case 8:
                        showBubble_SizeInfo(convertView);
                        showStarsShine_SharsInfo(convertView);
                        showPictureWall_PlayOrderInfo(convertView);
                        showRain_Speed(convertView);
                        break;
                    case 9:
                        showBubble_Number(convertView);
                        showStarsShine_StarmeteorsInfo(convertView);
                        showPictureWall_Period(convertView);
                        showRain_Alpha(convertView);
                        break;
                    case 10:
                        showBubble_Alpha(convertView);
                        showPictureWall_Alpha(convertView);
                        break;
                    case 11:
                        showBubble_Speed(convertView);
                        showPictureWall_ScaleSpeed(convertView);
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
                .putString(
                        "start" + VALUE,
                        PropertiesUtils.GetEventInfo(VALUE, LockScreenSet.this)[0])
                .apply();
        sp_date.edit().putString("state" + VALUE, getString(R.string.action_started)).commit();
        sp_date.edit()
                .putString(
                        "animation" + VALUE,
                        PropertiesUtils.getAnimationInfo(LockScreenSet.this)[C.ANIMATION_PICTUREWALL])
                .apply();
        sp_date.edit()
                .putString(
                        "end" + VALUE,
                        PropertiesUtils.GetEventInfo(VALUE, LockScreenSet.this)[1])
                .apply();

        for (int i = 0; i < PropertiesUtils
                .getAnimationInfo(LockScreenSet.this).length; i++) {

            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(LockScreenSet.this)[i],
                    0);
            sp_setA.edit().clear().apply();
        }
        sp_setA = getSharedPreferences(
                PropertiesUtils.GetEventInfo(C.EVENT_LOCKSCREEN, this)[0]
                        + PropertiesUtils.getAnimationInfo(LockScreenSet.this)[C.ANIMATION_PICTUREWALL],
                0);
        sp_setA.edit().putBoolean("onlyone", true).apply();

        setListLength(initializeListLenght());
        lsA.notifyDataSetChanged();

    }

    @SuppressLint("HandlerLeak")
    class mHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if (msg.obj.toString().equals("clear")) {
                clearDate();
                dialogDismiss();
            }
        }

    }
}
