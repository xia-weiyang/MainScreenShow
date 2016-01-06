package com.jiusg.mainscreenshow.ui;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.process.SetAdapter;
import com.jiusg.mainscreenshow.tools.PropertiesUtils;
import com.jiusg.mainscreenshow.tools.SmartBarUtils;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SMSSet extends BaseSet {

    private final int VALUE = C.EVENT_SMS;
    private String eventName;
    private SMSAdapter sA;
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

        sA = new SMSAdapter();
        lv.setAdapter(sA);

        setAdapter(sA);
        setVALUE(VALUE);
        setSetLenght(SETLENGHT);
        setListLength(initializeListLenght());
        setHanlder(new mHadler());

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


    class SMSAdapter extends BaseAdapter implements SetAdapter {

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
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(SMSSet.this);
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
                        showEventStart(convertView, getString(R.string.event_sms) + getString(R.string.animation));
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

            if (position == 0 || position == getListLength() - 1 || position == 2
                    || position == 5)
                return false;
            return true;
        }
    }

    @SuppressLint("HandlerLeak")
    class mHadler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if (msg.obj.toString().equals("clear")) {
                clearDate();
                dialogDismiss();
            }
        }

    }


    private void clearDate() {

        sp_date.edit()
                .putString("start" + VALUE,
                        PropertiesUtils.GetEventInfo(VALUE, SMSSet.this)[0])
                .apply();
        sp_date.edit().putString("state" + VALUE, getString(R.string.action_stoped)).apply();
        sp_date.edit()
                .putString("animation" + VALUE,
                        PropertiesUtils.getAnimationInfo(SMSSet.this)[1])
                .apply();
        sp_date.edit()
                .putString("end" + VALUE,
                        PropertiesUtils.GetEventInfo(VALUE, SMSSet.this)[1])
                .apply();

        for (int i = 0; i < PropertiesUtils.getAnimationInfo(SMSSet.this).length; i++) {

            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils.getAnimationInfo(SMSSet.this)[i],
                    0);
            sp_setA.edit().clear().apply();
        }

        setListLength(initializeListLenght());
        sA.notifyDataSetChanged();

    }
}
