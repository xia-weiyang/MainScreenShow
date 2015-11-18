package com.jiusg.mainscreenshow.ui;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.service.MSSService;
import com.jiusg.mainscreenshow.service.MSSService.MSSSBinder;
import com.jiusg.mainscreenshow.tools.UpdateVersion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @deprecated
 */
public class MSSEvent extends Activity {

    private ListView lv_mssEvent;
    private MSSEventAdapter mssEA;
    private final int lv_mssEventLenght = 7; // lv_mssEvent的长度
    private SharedPreferences sp_date;
    private SharedPreferences sp_userinfo; // 存储基本数据
    private AlertDialog dialogUpdate = null;
    private MSSEventHandler hd;
    private SharedPreferences sp_tip;
    private MSSService mssservice;
    private ServiceConnection mSc;

    public static boolean isActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        isActive = true;

        if (C.ISMEIZU)
            getWindow().setUiOptions(
                    ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);

        setContentView(R.layout.activity_mssevent);

        lv_mssEvent = (ListView) findViewById(R.id.list_mss);

        hd = new MSSEventHandler();
        mssEA = new MSSEventAdapter();
        lv_mssEvent.setAdapter(mssEA);

        sp_date = getSharedPreferences("date", 0);
        sp_tip = getSharedPreferences("tip", 0);
        sp_userinfo = getSharedPreferences("userinfo", 0);

        checkUpdate();

        Message msg1 = hd.obtainMessage();
        msg1.what = 3;
        hd.sendMessageDelayed(msg1, 1000);

        // 写入免费版
        if (C.ISFREE) {
            sp_userinfo.edit().putString("UserVersionInfo", C.VERSION_FREE)
                    .commit();
        }

        if (C.ISUPDATE) {
            dialogUpdate = new AlertDialog.Builder(MSSEvent.this)
                    .setTitle(R.string.action_updateRecord).setMessage("")
                    .setNegativeButton(R.string.action_ok, null).show();
            Message msg = hd.obtainMessage();
            msg.what = 0;
            hd.sendMessageDelayed(msg, 500);
            C.ISUPDATE = false;
        }

/*		if (!sp_tip.getBoolean("PW", false)) {

			new AlertDialog.Builder(MSSEvent.this).setTitle(R.string.tip)
					.setMessage(R.string.tip_msg_)
					.setPositiveButton(R.string.action_look, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							Uri content_url = Uri
									.parse("http://m.weibo.cn/3857031889/3749774257820866?sourceType=sms&from=1045515010&wm=9848_0009");
							intent.setData(content_url);
							startActivity(intent);
						}
					}).setNegativeButton(R.string.action_notip, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							sp_tip.edit().putBoolean("PW", true).commit();
						}
					}).show();
		}*/

        if (!sp_tip.getBoolean("Help", false)) {

            new AlertDialog.Builder(MSSEvent.this).setTitle(R.string.tip)
                    .setMessage(R.string.tip_msg_help)
                    .setPositiveButton(R.string.action_look, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent();
                            intent.setClass(MSSEvent.this, Help.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton(R.string.action_notip, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    sp_tip.edit().putBoolean("Help", true).commit();
                }
            }).show();
        }

        lv_mssEvent.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {

                switch (getEventValue(position)) {
                    case C.EVENT_DESKTOP:
                        startActivity(new Intent().setClass(MSSEvent.this,
                                DesktopSet.class));
                        break;
                    case C.EVENT_LOCKSCREEN:
                        startActivity(new Intent().setClass(MSSEvent.this,
                                LockScreenSet.class));
                        break;
                    case C.EVENT_CALL:
                        startActivity(new Intent().setClass(MSSEvent.this,
                                CallSet.class));
                        break;
                    case C.EVENT_SMS:
                        startActivity(new Intent().setClass(MSSEvent.this,
                                SMSSet.class));
                        break;
                    case C.EVENT_CHARGING:

                        startActivity(new Intent().setClass(MSSEvent.this,
                                CharingSet.class));
                        break;
                    default:
                        break;
                }

            }
        });

        lv_mssEvent.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int position, long arg3) {

                final String[] s = new String[2];
                final int x = position;
                if (sp_date.getString("state" + getEventValue(position), "")
                        .equals(getResources().getString(R.string.action_stoped)))
                    s[0] = getResources().getString(R.string.action_start);
                else
                    s[0] = getResources().getString(R.string.action_stop);
                s[1] = getResources().getString(R.string.action_Setting);
                new AlertDialog.Builder(MSSEvent.this).setTitle(R.string.action_ChoosePlease)
                        .setItems(s, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                switch (which) {
                                    case 0:
                                        sp_date.edit()
                                                .putString(
                                                        "state" + getEventValue(x),
                                                        getResources().getString(R.string.action_alread) + s[0]).commit();
                                        mssservice.loadEvent();
                                        mssEA.notifyDataSetChanged();
                                        break;
                                    case 1:
                                        switch (getEventValue(x)) {
                                            case C.EVENT_DESKTOP:
                                                startActivity(new Intent()
                                                        .setClass(MSSEvent.this,
                                                                DesktopSet.class));
                                                break;
                                            case C.EVENT_LOCKSCREEN:
                                                startActivity(new Intent().setClass(
                                                        MSSEvent.this,
                                                        LockScreenSet.class));
                                                break;
                                            case C.EVENT_CALL:
                                                startActivity(new Intent().setClass(
                                                        MSSEvent.this, CallSet.class));
                                                break;
                                            case C.EVENT_SMS:
                                                startActivity(new Intent().setClass(
                                                        MSSEvent.this, SMSSet.class));
                                                break;
                                            case C.EVENT_CHARGING:
                                                startActivity(new Intent()
                                                        .setClass(MSSEvent.this,
                                                                CharingSet.class));
                                                break;
                                            default:
                                                break;
                                        }
                                    default:
                                        break;
                                }
                            }
                        }).show();

                return true;
            }
        });

        mSc = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                mssservice = ((MSSSBinder) service).getMSSService();

            }
        };
    }

    @Override
    protected void onStart() {

        super.onStart();
        Intent service = new Intent(MSSEvent.this, MSSService.class);
        this.getApplicationContext().bindService(service, mSc,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {

        super.onStop();
        this.getApplicationContext().unbindService(mSc);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        isActive = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!C.ISMEIZU) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_setting_nomeizu, menu);
        }
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_preview:
                startActivity(new Intent()
                        .setClass(MSSEvent.this, MSSPreview.class));
                break;
            case R.id.menu_settings:
                startActivity(new Intent().setClass(MSSEvent.this, Setting.class));
                break;
            case R.id.menu_exit:
                new AlertDialog.Builder(MSSEvent.this).setTitle(R.string.action_warn)
                        .setMessage(R.string.tip_exit)
                        .setPositiveButton(R.string.action_ok, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                stopService(new Intent().setClass(MSSEvent.this,
                                        MSSService.class));
                                System.exit(0);
                            }
                        }).setNegativeButton(R.string.action_cancel, null).show();

                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (C.ISMEIZU) {
            getParent().getActionBar().setTitle(R.string.app_name);
            mssEA.notifyDataSetChanged();
        } else
            setTitle(R.string.app_name);
    }

    /**
     * lv_mssEvent适配器
     *
     * @author Administrator
     */
    class MSSEventAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            return lv_mssEventLenght;
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(MSSEvent.this);
            if (position == 0 || position == lv_mssEventLenght - 1)
                convertView = inflater.inflate(R.layout.list_alpha, parent,
                        false);
            else {
                convertView = inflater.inflate(R.layout.list_mssevent, parent,
                        false);
                TextView eventName = (TextView) convertView
                        .findViewById(R.id.tv_mssevent);
                TextView eventOC = (TextView) convertView
                        .findViewById(R.id.tv_mssevent_oc);
                ImageView eventImg = (ImageView) convertView
                        .findViewById(R.id.iv_mssevent);
                ImageView eventImgOC = (ImageView) convertView
                        .findViewById(R.id.iv_mssevent_oc);
                switch (position) {
                    case 1:
                        eventName.setText(R.string.event_desktop);
                        eventImg.setImageResource(R.drawable.mss_screen);
                        if (!sp_date.getString("state" + C.EVENT_DESKTOP, "")
                                .equals("")) {

                            eventOC.setText(sp_date.getString("state"
                                    + C.EVENT_DESKTOP, ""));
                            if (sp_date.getString("state" + C.EVENT_DESKTOP, "")
                                    .equals(getResources().getString(R.string.action_started)))
                                eventImgOC
                                        .setImageResource(R.drawable.list_mss_open);
                            else
                                eventImgOC
                                        .setImageResource(R.drawable.list_mss_close);
                        }
                        break;
                    case 2:
                        eventName.setText(R.string.event_lockScreen);
                        eventImg.setImageResource(R.drawable.mss_lockscreen);
                        if (!sp_date.getString("state" + C.EVENT_LOCKSCREEN, "")
                                .equals("")) {

                            eventOC.setText(sp_date.getString("state"
                                    + C.EVENT_LOCKSCREEN, ""));
                            if (sp_date.getString("state" + C.EVENT_LOCKSCREEN, "")
                                    .equals(getResources().getString(R.string.action_started)))
                                eventImgOC
                                        .setImageResource(R.drawable.list_mss_open);
                            else
                                eventImgOC
                                        .setImageResource(R.drawable.list_mss_close);
                        }
                        break;
                    case 3:
                        eventName.setText(R.string.event_call);
                        eventImg.setImageResource(R.drawable.mss_call);
                        if (!sp_date.getString("state" + C.EVENT_CALL, "").equals(
                                "")) {

                            eventOC.setText(sp_date.getString("state"
                                    + C.EVENT_CALL, ""));
                            if (sp_date.getString("state" + C.EVENT_CALL, "")
                                    .equals(getResources().getString(R.string.action_started)))
                                eventImgOC
                                        .setImageResource(R.drawable.list_mss_open);
                            else
                                eventImgOC
                                        .setImageResource(R.drawable.list_mss_close);
                        }
                        break;
                    case 4:
                        eventName.setText(R.string.event_sms);
                        eventImg.setImageResource(R.drawable.mss_sms);
                        if (!sp_date.getString("state" + C.EVENT_SMS, "")
                                .equals("")) {

                            eventOC.setText(sp_date.getString(
                                    "state" + C.EVENT_SMS, ""));
                            if (sp_date.getString("state" + C.EVENT_SMS, "")
                                    .equals(getResources().getString(R.string.action_started)))
                                eventImgOC
                                        .setImageResource(R.drawable.list_mss_open);
                            else
                                eventImgOC
                                        .setImageResource(R.drawable.list_mss_close);
                        }
                        break;
                    case 5:
                        eventName.setText(R.string.event_charing);
                        eventImg.setImageResource(R.drawable.mss_charging);
                        if (!sp_date.getString("state" + C.EVENT_CHARGING, "")
                                .equals("")) {

                            eventOC.setText(sp_date.getString("state"
                                    + C.EVENT_CHARGING, ""));
                            if (sp_date.getString("state" + C.EVENT_CHARGING, "")
                                    .equals(getResources().getString(R.string.action_started)))
                                eventImgOC
                                        .setImageResource(R.drawable.list_mss_open);
                            else
                                eventImgOC
                                        .setImageResource(R.drawable.list_mss_close);
                        }
                    default:
                        break;
                }
            }

            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {

            if (position == 0 || position == lv_mssEventLenght - 1)
                return false;
            return true;
        }
    }

    /**
     * @param position list的位置
     * @return 事件的值
     */
    private int getEventValue(int position) {

        switch (position) {
            case 1:
                return C.EVENT_DESKTOP;
            case 2:
                return C.EVENT_LOCKSCREEN;
            case 3:
                return C.EVENT_CALL;
            case 4:
                return C.EVENT_SMS;
            case 5:
                return C.EVENT_CHARGING;
            default:
                return 0;
        }
    }

    @SuppressLint("HandlerLeak")
    class MSSEventHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0) {

                dialogUpdate.setMessage(Setting.getUpdateInfo(MSSEvent.this));
            } else if (msg.what == 3) {

                if (mssservice != null) {
                    mssservice.canclaNotify();
                    mssservice.loadEvent();
                }

            }
        }

    }

    /**
     * 检查更新版本操作
     */
    private void checkUpdate() {

        final UpdateVersion updateVersion = new UpdateVersion(MSSEvent.this);
        if (updateVersion.isTipVersion()
                && !updateVersion.getNewVersionName().equals(
                updateVersion.getVersionName())) {
            updateVersion.showUpdate();
        }
    }
}
