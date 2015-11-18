package com.jiusg.mainscreenshow.ui;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.tools.MyLog;
import com.jiusg.mainscreenshow.tools.SmartBarUtils;
import com.jiusg.mainscreenshow.tools.UpdateVersion;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.MenuItem;

public class SettingMore extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingMoreFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ValidFragment")
    public class SettingMoreFragment extends PreferenceFragment {

        private SharedPreferences sp_tip;
        private PreferenceScreen rt;
        private CheckBoxPreference debug;

        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting_more);
            sp_tip = getActivity().getSharedPreferences("tip", 0);
            rt = (PreferenceScreen) findPreference("ResumeTip");
            debug = (CheckBoxPreference) findPreference("Debug");

            debug.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (!debug.isChecked()) {
                        debug.setChecked(false);
                        new AlertDialog.Builder(getActivity())
                                .setTitle(getString(R.string.tip))
                                .setMessage("确认进入调试模式?")
                                .setNegativeButton(getString(R.string.action_cancel), null)
                                .setPositiveButton(getString(R.string.action_ok), new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        debug.setChecked(true);
                                        // 进入调试模式输出必要信息
                                        MyLog.MYLOG_SWITCH = true;
                                        MyLog.delFile();
                                        String versionName = new UpdateVersion(getActivity()).getVersionName();
                                        String model = android.os.Build.MODEL;
                                        String androidVersion = android.os.Build.VERSION.RELEASE;
                                        String manufacturer = android.os.Build.MANUFACTURER;
                                        MyLog.i("debug", "version=" + versionName + "\r\nmodel=" + model + "\r\nandroidVersion=" + androidVersion + "\r\nmanufacturer=" + manufacturer);
                                    }
                                }).show();
                    } else {
                        debug.setChecked(false);
                    }

                    return false;
                }
            });

            rt.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getActivity()).setTitle("警告")
                            .setMessage("确认恢复所有\"不在提醒\"的提示信息?")
                            .setPositiveButton("确认", new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    sp_tip.edit().clear().commit();
                                }
                            }).setNegativeButton("取消", null)
                            .show();
                    return false;
                }
            });
        }

    }
}
