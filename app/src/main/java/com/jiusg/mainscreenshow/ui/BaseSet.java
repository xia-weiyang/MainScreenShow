package com.jiusg.mainscreenshow.ui;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.process.SetAdapter;
import com.jiusg.mainscreenshow.service.MSSService;
import com.jiusg.mainscreenshow.service.MSSService.MSSSBinder;
import com.jiusg.mainscreenshow.tools.PropertiesUtils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class BaseSet extends Activity {

    protected MSSService mssservice = null;
    protected ProgressDialog dialog = null;
    private Handler hanlder = null;
    private SetAdapter adapter = null;
    private ServiceConnection mSc;

    /**
     * listView 总长度 *
     */
    public int listLength = 3;
    /**
     * listView 事件设置长度 *
     */
    public int setLenght = 0;
    /**
     * listView 基本长度 *
     */
    public final int baseLenght = 3;
    /**
     * listView 动画设置长度 *
     */
    public int animationLenght = 0;
    /**
     * 事件 *
     */
    private int VALUE = C.EVENT_NO;
    /**
     * 事件的名字 *
     */
    private String eventName;
    /**
     * 用来存储该事件对应的动画信息 *
     */
    private SharedPreferences sp_setA;

    private SharedPreferences sp_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        sp_date = getSharedPreferences("date", 0);

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



    public int getVALUE() {
        return VALUE;
    }

    public void setVALUE(int vALUE) {
        VALUE = vALUE;
        eventName = sp_date.getString("start" + VALUE, "error");
    }

    public SetAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(SetAdapter adapter) {
        this.adapter = adapter;
    }

    public int getListLength() {
        return listLength;
    }

    public void setListLength(int listLength) {
        this.listLength = listLength;
    }

    public int getSetLenght() {
        return setLenght;
    }

    public void setSetLenght(int setLenght) {
        this.setLenght = setLenght;
    }

    public int getAnimationLenght() {
        return animationLenght;
    }

    public void setAnimationLenght(int animationLenght) {
        this.animationLenght = animationLenght;
    }

    public int getBaseLenght() {
        return baseLenght;
    }


    public Handler getHanlder() {
        return hanlder;
    }

    public void setHanlder(Handler hanlder) {
        this.hanlder = hanlder;
    }


    public ProgressDialog getDialog() {
        return dialog;
    }

    public void setDialog(ProgressDialog dialog) {
        this.dialog = dialog;
    }

    public void dialogDismiss() {
        System.out.println(dialog);
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        Intent service = new Intent(BaseSet.this, MSSService.class);
        this.getApplicationContext().bindService(service, mSc,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {

        super.onStop();
        this.getApplicationContext().unbindService(mSc);
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (mssservice != null)
            mssservice.loadEvent();
    }

    @Override
    protected void onPause() {

        super.onPause();
        if (mssservice != null)
            mssservice.loadEvent();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_mssevent_set, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_clear:
                new AlertDialog.Builder(BaseSet.this).setTitle(R.string.action_warn)
                        .setMessage(R.string.tip_msg_cancel)
                        .setPositiveButton(R.string.action_ok, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                BaseSet.this.dialog = ProgressDialog.show(BaseSet.this,
                                        null, getString(R.string.action_pleaseWait), true);
                                Message msg = hanlder.obtainMessage();
                                msg.obj = "clear";
                                hanlder.sendMessageDelayed(msg, 100);
                            }
                        }).setNegativeButton(R.string.action_cancel, null).show();
                break;
            case R.id.action_help:
                if (sp_date.getString("state" + VALUE, "").equals(getString(R.string.action_started)))
                    new AlertDialog.Builder(BaseSet.this)
                            .setTitle(R.string.action_help)
                            .setMessage(
                                    PropertiesUtils.getHelpInfo(VALUE, sp_date
                                                    .getString("animation" + VALUE, ""),
                                            BaseSet.this))
                            .setPositiveButton(R.string.action_ok, null).show();
                break;
            default:
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    protected void showToast(String content) {

        Toast.makeText(getApplication(), content, Toast.LENGTH_LONG).show();
    }

    /**
     * 初始化listView的长度
     *
     * @return
     */
    protected int initializeListLenght() {

        animationLenght = PropertiesUtils.getAnimationListLenght(
                sp_date.getString("animation" + VALUE, "error"), BaseSet.this, VALUE);

        if (!sp_date.getString("state" + VALUE, "").equals(getString(R.string.action_started)))
            return baseLenght;
        else {

            return setLenght + baseLenght + animationLenght;
        }
    }

    /**
     * show Switch 事件开关
     *
     * @param convertView
     * @param title
     */
    protected void showEventStartForDesktop(View convertView, String title,boolean isStart) {

        TextView title1 = (TextView) convertView
                .findViewById(R.id.tv_msseventset_title);
        final Switch sw = (Switch) convertView.findViewById(R.id.sw_msseventset);
        title1.setText(title);
        sw.setVisibility(View.VISIBLE);
        if(isStart){
            sw.setChecked(true);
        }else{
            sw.setChecked(false);
        }
        sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    new AlertDialog.Builder(BaseSet.this).setTitle(R.string.tip)
                            .setMessage("请设置桌面秀动态壁纸，以此激活桌面动画!")
                            .setPositiveButton("前往", new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                                    startActivity(intent);
                                    listLength = setLenght + baseLenght + animationLenght;
                                    adapter.notifyDataSetChanged();
                                }
                            }).setNegativeButton(R.string.action_cancel, null).show();
                    sw.setChecked(false);

                } else {
                    new AlertDialog.Builder(BaseSet.this).setTitle(R.string.tip)
                            .setMessage("请设重新设置壁纸，即可关闭桌面动画!")
                            .setPositiveButton(R.string.action_ok, null).show();
                    sw.setChecked(true);
                }
            }
        });
    }


    /**
     * show Switch 事件开关
     *
     * @param convertView
     * @param title
     */
    protected void showEventStart(View convertView, String title) {

        TextView title1 = (TextView) convertView
                .findViewById(R.id.tv_msseventset_title);
        Switch sw = (Switch) convertView.findViewById(R.id.sw_msseventset);
        title1.setText(title);
        sw.setVisibility(View.VISIBLE);
        if (sp_date.getString("state" + VALUE, "").equals(getString(R.string.action_started)))

            sw.setChecked(true);
        else
            sw.setChecked(false);
        sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    sp_date.edit().putString("state" + VALUE, getString(R.string.action_started)).commit();
                    listLength = setLenght + baseLenght + animationLenght;
                    adapter.notifyDataSetChanged();
                } else {
                    sp_date.edit().putString("state" + VALUE, getString(R.string.action_stoped)).commit();
                    listLength = baseLenght;
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * show Title 事件结束
     *
     * @param convertView
     * @param title
     */
    protected void showEventEndTitle(View convertView, String title) {

        TextView title_ = (TextView) convertView
                .findViewById(R.id.tv_msseventset_title_);
        TextView tip = (TextView) convertView
                .findViewById(R.id.tv_msseventset_tip);
        TextView tvSet = (TextView) convertView
                .findViewById(R.id.tv_msseventset_setting);
        title_.setText(title);
        tip.setText(R.string.tip_end);
        tvSet.setVisibility(View.VISIBLE);
        tvSet.setText(sp_date.getString("end" + VALUE, "error"));
    }

    /**
     * show AlertDialog 事件结束
     */
    protected void showEventEnd() {

        final String[] s;
        int x = 0;
        s = PropertiesUtils.getEventEndInfo(VALUE, BaseSet.this);
        x = 0;
        for (int i = 0; i < s.length; i++) {

            if (sp_date.getString("end" + VALUE, "").equals(s[i]))
                x = i;
        }
        new AlertDialog.Builder(BaseSet.this).setTitle(R.string.action_ChoosePlease)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(s, x, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        sp_date.edit().putString("end" + VALUE, s[which])
                                .commit();
                        adapter.notifyDataSetChanged();
                    }
                }).show();
    }

    /**
     * show AlertDialog 动画选择
     */
    protected void showAnimation() {
        final String[] s;
        int x = 0;
        s = PropertiesUtils.getActiveAnimationInfo(BaseSet.this);
        x = 0;
        for (int i = 0; i < s.length; i++) {

            if (sp_date.getString("animation" + VALUE, "").equals(s[i]))
                x = i;
        }
        new AlertDialog.Builder(BaseSet.this).setTitle(R.string.action_ChoosePlease)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(s, x, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        sp_date.edit().putString("animation" + VALUE, s[which])
                                .apply();
                        animationLenght = PropertiesUtils
                                .getAnimationListLenght(sp_date.getString(
                                                "animation" + VALUE, "error"),
                                        BaseSet.this, VALUE);
                        listLength = setLenght + baseLenght + animationLenght;
                        adapter.notifyDataSetChanged();
                    }
                }).show();
    }

    /**
     * show SeekBar 桌面 延迟时间
     *
     * @param convertView
     */
    protected void showEventDesktop_Time(View convertView) {

        TextView title_ = (TextView) convertView
                .findViewById(R.id.tv_msseventset_title_);
        TextView tip = (TextView) convertView
                .findViewById(R.id.tv_msseventset_tip);
        final TextView tvSet = (TextView) convertView
                .findViewById(R.id.tv_msseventset_setting);
        TextView title = (TextView) convertView
                .findViewById(R.id.tv_msseventset_title);
        title_.setText(R.string.setting_desktop_time);
        title.setText(R.string.setting_desktop_time);
        title.setVisibility(View.INVISIBLE);
        tip.setText(R.string.tip_desktop_time);
        tvSet.setVisibility(View.VISIBLE);
        tvSet.setText(sp_date.getInt(eventName + "time", 5) + getString(R.string.second));
        SeekBar sb = (SeekBar) convertView
                .findViewById(R.id.tv_msseventset_seekbar);
        sb.setVisibility(View.VISIBLE);
        sb.setMax(18);
        sb.setProgress(sp_date.getInt(eventName + "time", 5) - 2);
        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                sp_date.edit()
                        .putInt(eventName + "time", seekBar.getProgress() + 2)
                        .commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                tvSet.setText("" + (progress + 2) + getString(R.string.second));
            }
        });

    }

    /**
     * show AlertDialog 充电 运行界面
     */
    protected void showEventCharing_Run() {
        final String[] s;
        int x = 0;
        s = new String[]{getString(R.string.setting_charing_run_1)};
        x = 0;
        for (int i = 0; i < s.length; i++) {

            if (sp_date.getString(eventName + "runscreen", "").equals(s[i]))
                x = i;
        }
        new AlertDialog.Builder(BaseSet.this).setTitle(R.string.action_ChoosePlease)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(s, x, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {

                        dialog.dismiss();
                        sp_date.edit()
                                .putString(eventName + "runscreen", s[which])
                                .commit();
                        adapter.notifyDataSetChanged();
                    }
                }).show();
    }

    /**
     * show Title 充电 运行界面
     *
     * @param convertView
     */
    protected void showEventCharing_RunInfo(View convertView) {
        TextView title_ = (TextView) convertView
                .findViewById(R.id.tv_msseventset_title_);
        TextView tip = (TextView) convertView
                .findViewById(R.id.tv_msseventset_tip);
        final TextView tvSet = (TextView) convertView
                .findViewById(R.id.tv_msseventset_setting);
        title_.setText(R.string.setting_charing_run);
        tip.setText(R.string.tip_charing_run);
        tvSet.setVisibility(View.VISIBLE);
        tvSet.setText(sp_date.getString(eventName + "runscreen", getString(R.string.setting_charing_run_1)));
    }

    /**
     * show Title 动画
     *
     * @param convertView
     * @param title
     */
    protected void showAnimationTitle(View convertView, String title) {
        TextView title1 = (TextView) convertView
                .findViewById(R.id.tv_msseventset_title);
        TextView tvSet = (TextView) convertView
                .findViewById(R.id.tv_msseventset_setting);
        title1.setText(title);
        tvSet = (TextView) convertView
                .findViewById(R.id.tv_msseventset_setting);
        tvSet.setVisibility(View.VISIBLE);
        tvSet.setText(sp_date.getString("animation" + VALUE, "error"));
    }

    /**
     * show AlertDialog Bubble 大小
     */
    protected void showBubble_Size() {

        final String[] s;
        int x = 0;
        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_BUBBLE])) {

            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_BUBBLE],
                    0);

            s = new String[]{getString(R.string.bubble_size_muchSmall), getString(R.string.bubble_size_small), getString(R.string.bubble_size_big), getString(R.string.bubble_size_muchBig), getString(R.string.bubble_size_random)};
            x = 0;
            for (int i = 0; i < s.length; i++) {

                if (sp_setA.getString("size", getString(R.string.bubble_size_big)).equals(s[i]))
                    x = i;
            }
            new AlertDialog.Builder(BaseSet.this)
                    .setTitle(R.string.action_ChoosePlease)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setSingleChoiceItems(s, x,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    sp_setA.edit().putString("size", s[which])
                                            .commit();
                                    adapter.notifyDataSetChanged();
                                }
                            }).show();

        }
    }

    /**
     * show Switch Bubble 变色
     *
     * @param convertView
     */
    protected void showBubble_Color(View convertView) {

        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_BUBBLE])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            Switch sw = (Switch) convertView.findViewById(R.id.sw_msseventset);
            title_.setText(R.string.setting_bubble_changeColor);
            tip.setText(R.string.tip_bubble_color);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_BUBBLE],
                    0);

            sw.setVisibility(View.VISIBLE);
            sw.setChecked(sp_setA.getBoolean("color", true));
            sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {

                    sp_setA.edit().putBoolean("color", isChecked).commit();

                }
            });
        }
    }

    /**
     * show Switch Bubble 气泡影子
     *
     * @param convertView
     */
    protected void showBubble_Shadow(View convertView) {
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_BUBBLE])) {
            TextView title = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title);
            Switch sw = (Switch) convertView.findViewById(R.id.sw_msseventset);
            title.setText(R.string.setting_bubble_shadow);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_BUBBLE],
                    0);
            sw = (Switch) convertView
                    .findViewById(R.id.sw_msseventset);
            sw.setVisibility(View.VISIBLE);
            sw.setChecked(sp_setA.getBoolean("shadow", true));
            sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(
                        CompoundButton buttonView, boolean isChecked) {

                    sp_setA.edit().putBoolean("shadow", isChecked)
                            .commit();

                }
            });
        }
    }

    /**
     * show Title Bubble 大小
     *
     * @param convertView
     */
    protected void showBubble_SizeInfo(View convertView) {
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_BUBBLE])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title_.setText(R.string.setting_bubble_size);
            tip.setText(R.string.tip_bubble_size);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_BUBBLE],
                    0);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet.setVisibility(View.VISIBLE);
            tvSet.setText(sp_setA.getString("size", getString(R.string.bubble_size_big)));
        }
    }

    /**
     * show SeekBar Bubble 个数
     *
     * @param convertView
     */
    protected void showBubble_Number(View convertView) {
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_BUBBLE])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView title = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title.setText(R.string.setting_bubble_number);
            title.setVisibility(View.INVISIBLE);
            title_.setText(R.string.setting_bubble_number);
            tip.setText(R.string.tip_bubble_number);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_BUBBLE],
                    0);
            final TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet.setVisibility(View.VISIBLE);
            tvSet.setText(sp_setA.getInt("number", 7) + "");
            SeekBar sb = (SeekBar) convertView
                    .findViewById(R.id.tv_msseventset_seekbar);
            sb.setVisibility(View.VISIBLE);
            sb.setMax(5);
            sb.setProgress(sp_setA.getInt("number", 7) - 5);
            sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    sp_setA.edit()
                            .putInt("number",
                                    seekBar.getProgress() + 5)
                            .commit();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {

                    tvSet.setText("" + (progress + 5));
                }
            });
        }
    }

    /**
     * show SeekBar Bubble 透明度
     *
     * @param convertView
     */
    protected void showBubble_Alpha(View convertView) {
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_BUBBLE])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView title = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title.setText(R.string.setting_bubble_alpha);
            title.setVisibility(View.INVISIBLE);
            title_.setText(R.string.setting_bubble_alpha);
            tip.setText(R.string.tip_bubble_alpha);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_BUBBLE],
                    0);
            final TextView tvSet_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting_);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet_.setText(sp_setA.getFloat("alpha", 1f) + "");
            tvSet.setText(sp_setA.getFloat("alpha", 1f) + "");
            tvSet_.setVisibility(View.VISIBLE);
            tvSet.setVisibility(View.INVISIBLE);
            SeekBar sb = (SeekBar) convertView
                    .findViewById(R.id.tv_msseventset_seekbar);
            sb.setVisibility(View.VISIBLE);
            sb.setMax(20);
            sb.setProgress((int) (sp_setA.getFloat("alpha", 1f) * 20));
            sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    sp_setA.edit()
                            .putFloat(
                                    "alpha",
                                    ((float) (seekBar.getProgress()) / 20))
                            .commit();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {

                    tvSet_.setText(((float) (progress) / 20) + "");
                }
            });
        }
    }

    /**
     * show SeekBar Bubble 速度
     * @param convertView
     */
    protected void showBubble_Speed(View convertView) {
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_BUBBLE])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView title = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title.setText(R.string.setting_bubble_speed);
            title.setVisibility(View.INVISIBLE);
            title_.setText(R.string.setting_bubble_speed);
            tip.setText(R.string.tip_bubble_speed);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_BUBBLE],
                    0);
            final TextView tvSet_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting_);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet_.setText(sp_setA.getInt("speed", 5) + "");
            tvSet.setText(sp_setA.getInt("speed", 5) + "");
            tvSet_.setVisibility(View.VISIBLE);
            tvSet.setVisibility(View.INVISIBLE);
            SeekBar sb = (SeekBar) convertView
                    .findViewById(R.id.tv_msseventset_seekbar);
            sb.setVisibility(View.VISIBLE);
            sb.setMax(9);
            sb.setProgress(sp_setA.getInt("speed", 5)-1);
            sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    sp_setA.edit()
                            .putInt(
                                    "speed",
                                    seekBar.getProgress() + 1)
                            .commit();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {

                    tvSet_.setText(seekBar.getProgress() + 1 + "");
                }
            });
        }
    }

    /**
     * show AlertDialog ShatsShine 星光数量
     */
    protected void showStarsShine_Shars() {

        final String[] s;
        int x = 0;

        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_STARSHINE])) {

            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_STARSHINE],
                    0);

            s = new String[]{getString(R.string.starShine_num_less), getString(R.string.starShine_num_general), getString(R.string.starShine_num_much)};
            x = 0;
            for (int i = 0; i < s.length; i++) {

                if (sp_setA.getString("allcount", getString(R.string.starShine_num_general)).equals(s[i]))
                    x = i;
            }
            new AlertDialog.Builder(BaseSet.this)
                    .setTitle(R.string.action_ChoosePlease)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setSingleChoiceItems(s, x,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    sp_setA.edit()
                                            .putString("allcount", s[which])
                                            .commit();
                                    adapter.notifyDataSetChanged();
                                }
                            }).show();
        }
    }

    /**
     * show AlertDialog ShatsShine 流星数量
     */
    protected void showStarsShine_Starmeteors() {

        final String[] s;
        int x = 0;
        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_STARSHINE])) {

            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_STARSHINE],
                    0);

            s = new String[]{getString(R.string.starShine_num_less), getString(R.string.starShine_num_general), getString(R.string.starShine_num_much)};
            x = 0;
            for (int i = 0; i < s.length; i++) {

                if (sp_setA.getString("starmeteorcount", getString(R.string.starShine_num_general)).equals(s[i]))
                    x = i;
            }
            new AlertDialog.Builder(BaseSet.this)
                    .setTitle(R.string.action_ChoosePlease)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setSingleChoiceItems(s, x,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    sp_setA.edit()
                                            .putString("starmeteorcount",
                                                    s[which]).commit();
                                    adapter.notifyDataSetChanged();
                                }
                            }).show();
        }

    }

    /**
     * show AlertDialog ShatsShine 星光样式
     */
    protected void showStarShine_Style() {

        final String[] s;
        int x = 0;
        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_STARSHINE])) {

            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_STARSHINE],
                    0);

            s = new String[]{getString(R.string.starShine_style_classical), getString(R.string.starShine_style_stars)};
            x = 0;
            for (int i = 0; i < s.length; i++) {

                if (sp_setA.getString("style", getString(R.string.starShine_style_classical)).equals(s[i]))
                    x = i;
            }
            new AlertDialog.Builder(BaseSet.this)
                    .setTitle(R.string.action_ChoosePlease)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setSingleChoiceItems(s, x,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    sp_setA.edit()
                                            .putString("style",
                                                    s[which]).commit();
                                    adapter.notifyDataSetChanged();
                                }
                            }).show();
        }
    }

    /**
     * show Title SharsShine 星光样式
     * @param convertView
     */
    protected void showStarShine_StyleInfo(View convertView){
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_STARSHINE])) {
            TextView title = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title);
            title.setText(R.string.setting_starShine_style);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_STARSHINE],
                    0);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet.setVisibility(View.VISIBLE);
            tvSet.setText(sp_setA.getString("style", getString(R.string.starShine_style_classical)));
        }
    }

    /**
     * show Switch SharsShine 流星开关
     *
     * @param convertView
     */
    protected void showStarsShine_StaemeteorSwitch(View convertView) {

        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_STARSHINE])) {

            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            Switch sw = (Switch) convertView.findViewById(R.id.sw_msseventset);
            title_.setText(R.string.setting_starShine_meteor);
            tip.setText(R.string.tip_starshine_starmeteorswitch);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_STARSHINE],
                    0);
            sw.setVisibility(View.VISIBLE);
            sw.setChecked(sp_setA.getBoolean("starmeteorswitch",
                    true));
            sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(
                        CompoundButton buttonView, boolean isChecked) {

                    sp_setA.edit()
                            .putBoolean("starmeteorswitch",
                                    isChecked).commit();

                }
            });
        }
    }

    /**
     * show Title SharsShine 星光数量
     *
     * @param convertView
     */
    protected void showStarsShine_SharsInfo(View convertView) {
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_STARSHINE])) {
            TextView title = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title);
            title.setText(R.string.setting_starShine_starNum);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_STARSHINE],
                    0);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet.setVisibility(View.VISIBLE);
            tvSet.setText(sp_setA.getString("allcount", getString(R.string.starShine_num_general)));
        }
    }

    /**
     * show Title SharsShine 流星数量
     *
     * @param convertView
     */
    protected void showStarsShine_StarmeteorsInfo(View convertView) {
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_STARSHINE])) {
            TextView title = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title);
            title.setText(R.string.setting_starShine_meteorNum);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_STARSHINE],
                    0);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet.setVisibility(View.VISIBLE);
            tvSet.setText(sp_setA
                    .getString("starmeteorcount", getString(R.string.starShine_num_general)));
        }
    }

    /**
     * show Activity PictureWall 图片选择
     */
    protected void showPictureWall_ChoosePicture() {

        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_PICTUREWALL])) {
            startActivity(new Intent().setClass(BaseSet.this,
                    PictureWallChoose.class));
        }
    }

    /**
     * show AlertDialog PictureWall 播放顺序
     */
    protected void showPictureWall_PlayOrder() {

        final String[] s;
        int x = 0;

        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_PICTUREWALL])) {

            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_PICTUREWALL],
                    0);

            s = new String[]{getString(R.string.pictureWall_play_order), getString(R.string.pictureWall_play_random)};
            x = 0;
            for (int i = 0; i < s.length; i++) {

                if (sp_setA.getString("bitmap_type", getString(R.string.pictureWall_play_random)).equals(s[i]))
                    x = i;
            }
            String[] s1 = new String[]{getString(R.string.pictureWall_play_order) + getString(R.string.pictureWall_play_order_), getString(R.string.pictureWall_play_random)};
            new AlertDialog.Builder(BaseSet.this)
                    .setTitle(R.string.action_ChoosePlease)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setSingleChoiceItems(s1, x,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    sp_setA.edit()
                                            .putString("bitmap_type", s[which])
                                            .apply();
                                    adapter.notifyDataSetChanged();
                                }
                            }).show();
        }
    }

    /**
     * show Title PictureWall 图片选择
     *
     * @param convertView
     */
    protected void showPictureWall_ChoosePictureTitle(View convertView) {
        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_PICTUREWALL])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title_.setText(R.string.setting_pictureWall_choose);
            tip.setText(R.string.tip_picturewall_picture);
        }
    }

    /**
     * show Title PictureWall 播放顺序
     *
     * @param convertView
     */
    protected void showPictureWall_PlayOrderInfo(View convertView) {
        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_PICTUREWALL])) {
            TextView title = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title);
            title.setText(R.string.setting_pictureWall_play);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_PICTUREWALL],
                    0);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet.setVisibility(View.VISIBLE);
            tvSet.setText(sp_setA.getString("bitmap_type", getString(R.string.pictureWall_play_random)));
        }
    }

    /**
     * show SeekBar PictureWall 播放周期
     *
     * @param convertView
     */
    protected void showPictureWall_Period(View convertView) {

        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_PICTUREWALL])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView title = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title.setText(R.string.setting_pictureWall_period);
            title.setVisibility(View.INVISIBLE);
            title_.setText(R.string.setting_pictureWall_period);
            tip.setText(R.string.tip_picturewall_period);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_PICTUREWALL],
                    0);
            final TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet.setVisibility(View.VISIBLE);
            tvSet.setText(sp_setA.getFloat("period", 4.0f) + getString(R.string.second));
            SeekBar sb = (SeekBar) convertView
                    .findViewById(R.id.tv_msseventset_seekbar);
            sb.setVisibility(View.VISIBLE);
            sb.setMax(60);
            sb.setProgress((int) (sp_setA.getFloat("period", 4.0f) * 10) - 10);
            sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    sp_setA.edit()
                            .putFloat(
                                    "period",
                                    ((float) (seekBar.getProgress() + 10) / 10))
                            .commit();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {

                    tvSet.setText(((float) (progress + 10) / 10)
                            + getString(R.string.second));
                }
            });
        }
    }

    /**
     * show SeekBar PictureWall 透明度
     *
     * @param convertView
     */
    protected void showPictureWall_Alpha(View convertView) {
        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_PICTUREWALL])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView title = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title.setText(R.string.setting_pictureWall_alpha);
            title.setVisibility(View.INVISIBLE);
            title_.setText(R.string.setting_pictureWall_alpha);
            tip.setText(R.string.tip_picturewall_alpha);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_PICTUREWALL],
                    0);
            final TextView tvSet_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting_);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet_.setText(sp_setA.getFloat("alpha", 1f) + "");
            tvSet.setText("0.15");
            tvSet_.setVisibility(View.VISIBLE);
            tvSet.setVisibility(View.INVISIBLE);
            SeekBar sb = (SeekBar) convertView
                    .findViewById(R.id.tv_msseventset_seekbar);
            sb.setVisibility(View.VISIBLE);
            sb.setMax(20);
            sb.setProgress((int) (sp_setA.getFloat("alpha", 1f) * 20));
            sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    sp_setA.edit()
                            .putFloat(
                                    "alpha",
                                    ((float) (seekBar.getProgress()) / 20))
                            .commit();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {

                    tvSet_.setText(((float) (progress) / 20) + "");
                }
            });
        }
    }

    /**
     * show SeekBar PictureWall 放大速率
     *
     * @param convertView
     */
    protected void showPictureWall_ScaleSpeed(View convertView) {

        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(getApplication())[C.ANIMATION_PICTUREWALL])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView title = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);

            title.setText(R.string.setting_pictureWall_zoomIn);
            title.setVisibility(View.INVISIBLE);
            title_.setText(R.string.setting_pictureWall_zoomIn);
            tip.setText(R.string.tip_picturewall_zoomin);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(getApplication())[C.ANIMATION_PICTUREWALL],
                    0);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            final TextView tvSet_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting_);
            tvSet.setText("-1");
            tvSet_.setVisibility(View.VISIBLE);
            tvSet.setVisibility(View.INVISIBLE);
            tvSet_.setText(sp_setA.getInt("zoomin", 0) + "");
            SeekBar sb = (SeekBar) convertView
                    .findViewById(R.id.tv_msseventset_seekbar);
            sb.setVisibility(View.VISIBLE);
            sb.setMax(10);
            sb.setProgress(sp_setA.getInt("zoomin", 0) + 5);
            sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    sp_setA.edit()
                            .putInt("zoomin",
                                    seekBar.getProgress() - 5)
                            .commit();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {

                    tvSet_.setText(seekBar.getProgress() - 5 + "");
                }
            });
        }
    }

    /**
     * show Switch PictureWall 锁屏专属(仅播放一张图片)
     *
     * @param convertView
     */
    protected void showPictureWall_LockScreen(View convertView) {
        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_PICTUREWALL])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title_.setText(R.string.setting_pictureWall_onlyOne);
            tip.setText(R.string.tip_picturewall_onlyone);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_PICTUREWALL],
                    0);
            Switch sw = (Switch) convertView
                    .findViewById(R.id.sw_msseventset);
            sw.setVisibility(View.VISIBLE);
            sw.setChecked(sp_setA.getBoolean("onlyone", false));
            sw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(
                        CompoundButton buttonView, boolean isChecked) {

                    sp_setA.edit().putBoolean("onlyone", isChecked)
                            .commit();
                    if (isChecked) {
                        new AlertDialog.Builder(BaseSet.this)
                                .setTitle(R.string.tip)
                                .setMessage(
                                        getString(R.string.tip_msg_picture))
                                .setNegativeButton(R.string.action_ok, null)
                                .show();
                    }
                }
            });
        }
    }


    /**
     * show Title rain 样式
     */
    protected void showRain_StyleInfo(View convertView) {
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_RAIN])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title_.setText(R.string.setting_rain_style);
            tip.setText(R.string.tip_rain_style);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_RAIN],
                    0);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet.setVisibility(View.VISIBLE);
            tvSet.setText(sp_setA.getString("style", getString(R.string.rain_style_1)));
        }
    }

    /**
     * show AlertDialog rain 样式
     */
    protected void showRain_Style() {

        final String[] s;
        int x = 0;
        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_RAIN])) {

            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_RAIN],
                    0);

            s = new String[]{getString(R.string.rain_style_1), getString(R.string.rain_style_2)};
            x = 0;
            for (int i = 0; i < s.length; i++) {

                if (sp_setA.getString("style", getString(R.string.rain_style_1)).equals(s[i]))
                    x = i;
            }
            new AlertDialog.Builder(BaseSet.this)
                    .setTitle(R.string.action_ChoosePlease)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setSingleChoiceItems(s, x,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    sp_setA.edit().putString("style", s[which])
                                            .apply();
                                    adapter.notifyDataSetChanged();
                                }
                            }).show();

        }
    }


    /**
     * show Title rain 数量
     */
    protected void showRain_AmountInfo(View convertView) {
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_RAIN])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title_.setText(R.string.setting_rain_amount);
            tip.setText(R.string.tip_rain_amount);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_RAIN],
                    0);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet.setVisibility(View.VISIBLE);
            tvSet.setText(sp_setA.getString("amount", getString(R.string.rain_amount_general)));
        }
    }

    /**
     * show AlertDialog rain 数量
     */
    protected void showRain_Amount() {

        final String[] s;
        int x = 0;
        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_RAIN])) {

            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_RAIN],
                    0);

            s = new String[]{getString(R.string.rain_amount_very_munch), getString(R.string.rain_amount_munch), getString(R.string.rain_amount_general), getString(R.string.rain_amount_less)};
            x = 0;
            for (int i = 0; i < s.length; i++) {

                if (sp_setA.getString("amount", getString(R.string.rain_amount_general)).equals(s[i]))
                    x = i;
            }
            new AlertDialog.Builder(BaseSet.this)
                    .setTitle(R.string.action_ChoosePlease)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setSingleChoiceItems(s, x,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    sp_setA.edit().putString("amount", s[which])
                                            .apply();
                                    adapter.notifyDataSetChanged();
                                }
                            }).show();

        }
    }

    /**
     * show SeekBar Rain 速度
     *
     * @param convertView
     */
    protected void showRain_Speed(View convertView) {
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_RAIN])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView title = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title.setText(R.string.setting_rain_speed);
            title.setVisibility(View.INVISIBLE);
            title_.setText(R.string.setting_rain_speed);
            tip.setText(R.string.tip_rain_speed);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_RAIN],
                    0);
            final TextView tvSet_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting_);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet_.setText(sp_setA.getInt("speed", 3) + "");
            tvSet.setText(sp_setA.getInt("speed", 3) + "");
            tvSet_.setVisibility(View.VISIBLE);
            tvSet.setVisibility(View.INVISIBLE);
            SeekBar sb = (SeekBar) convertView
                    .findViewById(R.id.tv_msseventset_seekbar);
            sb.setVisibility(View.VISIBLE);
            sb.setMax(9);
            sb.setProgress(sp_setA.getInt("speed", 3)-1);
            sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    sp_setA.edit()
                            .putInt(
                                    "speed",
                                    seekBar.getProgress() + 1)
                            .commit();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {

                    tvSet_.setText(seekBar.getProgress() + 1 + "");
                }
            });
        }
    }

    /**
     * show SeekBar Rain 透明度
     *
     * @param convertView
     */
    protected void showRain_Alpha(View convertView) {
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_RAIN])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView title = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title.setText(R.string.setting_rain_alpha);
            title.setVisibility(View.INVISIBLE);
            title_.setText(R.string.setting_rain_alpha);
            tip.setText(R.string.tip_rain_alpha);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_RAIN],
                    0);
            final TextView tvSet_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting_);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet_.setText(sp_setA.getFloat("alpha", 1f) + "");
            tvSet.setText(sp_setA.getFloat("alpha", 1f) + "");
            tvSet_.setVisibility(View.VISIBLE);
            tvSet.setVisibility(View.INVISIBLE);
            SeekBar sb = (SeekBar) convertView
                    .findViewById(R.id.tv_msseventset_seekbar);
            sb.setVisibility(View.VISIBLE);
            sb.setMax(20);
            sb.setProgress((int) (sp_setA.getFloat("alpha", 1f) * 20));
            sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    sp_setA.edit()
                            .putFloat(
                                    "alpha",
                                    ((float) (seekBar.getProgress()) / 20))
                            .commit();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {

                    tvSet_.setText(((float) (progress) / 20) + "");
                }
            });
        }
    }

    /**
     * show Title snow 样式
     */
    protected void showSnow_StyleInfo(View convertView) {
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_SNOW])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title_.setText("雪花样式");
            tip.setText("改变雪花样式");
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_SNOW],
                    0);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet.setVisibility(View.VISIBLE);
            tvSet.setText(sp_setA.getString("style", getString(R.string.snow_style_1)));
        }
    }

    /**
     * show AlertDialog snow 样式
     */
    protected void showSnow_Style() {

        final String[] s;
        int x = 0;
        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_SNOW])) {

            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_SNOW],
                    0);

            s = new String[]{getString(R.string.snow_style_1), getString(R.string.snow_style_2),getString(R.string.snow_style_3),getString(R.string.snow_style_4)};
            x = 0;
            for (int i = 0; i < s.length; i++) {

                if (sp_setA.getString("style", getString(R.string.snow_style_1)).equals(s[i]))
                    x = i;
            }
            new AlertDialog.Builder(BaseSet.this)
                    .setTitle(R.string.action_ChoosePlease)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setSingleChoiceItems(s, x,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    sp_setA.edit().putString("style", s[which])
                                            .apply();
                                    adapter.notifyDataSetChanged();
                                }
                            }).show();

        }
    }

    /**
     * show Title snow 数量
     */
    protected void showSnow_AmountInfo(View convertView) {
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_SNOW])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title_.setText("雪花数量");
            tip.setText("数量越多代表雪下的越大");
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_SNOW],
                    0);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet.setVisibility(View.VISIBLE);
            tvSet.setText(sp_setA.getString("amount", getString(R.string.snow_amount_general)));
        }
    }

    /**
     * show AlertDialog snow 数量
     */
    protected void showSnow_Amount() {

        final String[] s;
        int x = 0;
        if (sp_date
                .getString("animation" + VALUE, "")
                .equals(PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_SNOW])) {

            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils.getAnimationInfo(BaseSet.this)[C.ANIMATION_SNOW],
                    0);

            s = new String[]{getString(R.string.snow_amount_munch), getString(R.string.snow_amount_general), getString(R.string.snow_amount_less)};
            x = 0;
            for (int i = 0; i < s.length; i++) {

                if (sp_setA.getString("amount", getString(R.string.snow_amount_general)).equals(s[i]))
                    x = i;
            }
            new AlertDialog.Builder(BaseSet.this)
                    .setTitle(R.string.action_ChoosePlease)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setSingleChoiceItems(s, x,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    sp_setA.edit().putString("amount", s[which])
                                            .apply();
                                    adapter.notifyDataSetChanged();
                                }
                            }).show();

        }
    }

    /**
     * show SeekBar snow 速度
     *
     * @param convertView
     */
    protected void showSnow_Speed(View convertView) {
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_SNOW])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView title = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title.setText("雪下落速度");
            title.setVisibility(View.INVISIBLE);
            title_.setText("雪下落速度");
            tip.setText("调整雪下落速度");
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_SNOW],
                    0);
            final TextView tvSet_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting_);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet_.setText(sp_setA.getInt("speed", 3) + "");
            tvSet.setText(sp_setA.getInt("speed", 3) + "");
            tvSet_.setVisibility(View.VISIBLE);
            tvSet.setVisibility(View.INVISIBLE);
            SeekBar sb = (SeekBar) convertView
                    .findViewById(R.id.tv_msseventset_seekbar);
            sb.setVisibility(View.VISIBLE);
            sb.setMax(9);
            sb.setProgress(sp_setA.getInt("speed", 3)-1);
            sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    sp_setA.edit()
                            .putInt(
                                    "speed",
                                    seekBar.getProgress() + 1)
                            .commit();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {

                    tvSet_.setText(seekBar.getProgress() + 1 + "");
                }
            });
        }
    }

    /**
     * show SeekBar snow 透明度
     *
     * @param convertView
     */
    protected void showSnow_Alpha(View convertView) {
        if (sp_date.getString("animation" + VALUE, "")
                .equals(PropertiesUtils
                        .getAnimationInfo(BaseSet.this)[C.ANIMATION_SNOW])) {
            TextView title_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title_);
            TextView title = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_title);
            TextView tip = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_tip);
            title.setText(R.string.setting_rain_alpha);
            title.setVisibility(View.INVISIBLE);
            title_.setText(R.string.setting_rain_alpha);
            tip.setText(R.string.tip_rain_alpha);
            sp_setA = getSharedPreferences(
                    eventName
                            + PropertiesUtils
                            .getAnimationInfo(BaseSet.this)[C.ANIMATION_SNOW],
                    0);
            final TextView tvSet_ = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting_);
            TextView tvSet = (TextView) convertView
                    .findViewById(R.id.tv_msseventset_setting);
            tvSet_.setText(sp_setA.getFloat("alpha", 1f) + "");
            tvSet.setText(sp_setA.getFloat("alpha", 1f) + "");
            tvSet_.setVisibility(View.VISIBLE);
            tvSet.setVisibility(View.INVISIBLE);
            SeekBar sb = (SeekBar) convertView
                    .findViewById(R.id.tv_msseventset_seekbar);
            sb.setVisibility(View.VISIBLE);
            sb.setMax(20);
            sb.setProgress((int) (sp_setA.getFloat("alpha", 1f) * 20));
            sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    sp_setA.edit()
                            .putFloat(
                                    "alpha",
                                    ((float) (seekBar.getProgress()) / 20))
                            .commit();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onProgressChanged(SeekBar seekBar,
                                              int progress, boolean fromUser) {

                    tvSet_.setText(((float) (progress) / 20) + "");
                }
            });
        }
    }


}
