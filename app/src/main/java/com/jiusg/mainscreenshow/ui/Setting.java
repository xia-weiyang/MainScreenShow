package com.jiusg.mainscreenshow.ui;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.service.MSSService;
import com.jiusg.mainscreenshow.service.MSSService.MSSSBinder;
import com.jiusg.mainscreenshow.tools.MyLog;
import com.jiusg.mainscreenshow.tools.SmartBarUtils;
import com.jiusg.mainscreenshow.tools.UpdateVersion;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.youmi.android.AdManager;
import net.youmi.android.listener.Interface_ActivityListener;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.PointsChangeNotify;
import net.youmi.android.offers.PointsManager;

public class Setting extends Activity implements PointsChangeNotify {
    private ServiceConnection mSc;
    private MSSService mssservice;
    private AlertDialog dialogUpdate = null;

    public static final int RESULT_SETTING = 101;

    private final String TAG = "Setting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingFragment()).commit();


        // 初始化有米广告接口
        AdManager.getInstance(this).init("cf3c9641dcb8b689", "c8fd1af7ae9d261d");
        OffersManager.getInstance(this).onAppLaunch();
        PointsManager.getInstance(this).registerNotify(this);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPointBalanceChange(float v) {
        MyLog.i(TAG, "point=" + (int) v);
    }

    @SuppressLint("ValidFragment")
    public class SettingFragment extends PreferenceFragment {

        private mHandler handler;
        private CheckBoxPreference powerSaving;
        private ListPreference frame;
//        private ListPreference ramLimit;
        private PreferenceScreen welcome;
        private PreferenceScreen version;
        private SharedPreferences sp_userinfo;
        private PreferenceScreen app;
        private PreferenceScreen more;
        private PreferenceScreen update;
        private PreferenceScreen advice;
        private PreferenceScreen checkUpdate;
        private PreferenceScreen help;
        private PreferenceScreen exit;

        public SettingFragment() {

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.setting);
            powerSaving = (CheckBoxPreference) findPreference("PowerSaving");
            frame = (ListPreference) findPreference("Frame");
//            ramLimit = (ListPreference) findPreference("RAMLimit");
            welcome = (PreferenceScreen) findPreference("Welcome");
            version = (PreferenceScreen) findPreference("Version");
            app = (PreferenceScreen) findPreference("App");
            more = (PreferenceScreen) findPreference("MoreSet");
            update = (PreferenceScreen) findPreference("Update");
            advice = (PreferenceScreen) findPreference("Advice");
            checkUpdate = (PreferenceScreen) findPreference("CheckUpdate");
            help = (PreferenceScreen) findPreference("Help");
            exit = (PreferenceScreen) findPreference("Exit");

            handler = new mHandler();
            sp_userinfo = getActivity().getSharedPreferences("userinfo", 0);

            frame.setSummary(frame.getEntry());
//            ramLimit.setSummary(ramLimit.getEntry());
            version.setSummary("未知");

            Message msg = handler.obtainMessage();
            msg.obj = "version";
            handler.sendMessageDelayed(msg, 500);
            Message msg1 = handler.obtainMessage();
            msg1.obj = "checkUpdate";
            handler.sendMessageDelayed(msg1, 500);
            Message msg2 = handler.obtainMessage();
            msg2.obj = "point";
            handler.sendMessageDelayed(msg2, 500);

            powerSaving
                    .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(
                                Preference preference, Object newValue) {

                            mssservice.registerBattery();
                            return true;
                        }
                    });
            frame.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {

                    Message msg = handler.obtainMessage();
                    msg.obj = "frame";
                    handler.sendMessageDelayed(msg, 500);
                    return true;
                }
            });
//            ramLimit.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
//
//                @Override
//                public boolean onPreferenceChange(Preference preference,
//                                                  Object newValue) {
//
//                    Message msg = handler.obtainMessage();
//                    msg.obj = "ramLimit";
//                    handler.sendMessageDelayed(msg, 500);
//                    return true;
//                }
//            });
            welcome.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Bundle b = new Bundle();
                    b.putBoolean("welcome", true);
                    startActivity(new Intent().setClass(getActivity(),
                            Welcome.class).putExtras(b));
                    return true;
                }
            });
            help.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    startActivity(new Intent().setClass(getActivity(), Help.class));
                    return true;
                }
            });
            version.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (C.ISMEIZU) {
                        String appIdentify = "1d56f2d8ea07440abbec2511b5f59ac1";
                        Uri appUri = Uri
                                .parse("mstore:http://app.meizu.com/phone/apps/"
                                        + appIdentify);
                        Intent intent = new Intent(Intent.ACTION_VIEW, appUri);
                        startActivity(intent);
                    }
                    return true;
                }
            });
            checkUpdate.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    final UpdateVersion updateVersion = new UpdateVersion(Setting.this);
                    final ProgressDialog progressDialog = ProgressDialog.show(Setting.this, null, getString(R.string.action_checking), true);
                    updateVersion.checkVersionFromNetwork(updateVersion.new CallBack() {

                        @Override
                        public void done(String versionName, String versionUrl, String versionInfo) {

                            progressDialog.dismiss();

                            if (versionName.equals(updateVersion.getVersionName())) {

                                Toast.makeText(getApplication(), getString(R.string.tip_newVersion_no), Toast.LENGTH_LONG).show();
                            } else {

                                updateVersion.showUpdate();
                            }
                        }
                    });
                    return true;
                }
            });
            app.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    OffersManager.getInstance(getActivity()).showOffersWall(new Interface_ActivityListener() {
                        @Override
                        public void onActivityDestroy(Context context) {

                            MyLog.i(TAG, "exit offerswall");
                        }
                    });

//					startActivity(new Intent().setClass(getActivity(),
//									/**		SettingApp.class));

                    return false;
                }
            });
            more.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent().setClass(getActivity(),
                            SettingMore.class));
                    return false;
                }
            });
            update.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    dialogUpdate = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.action_updateRecord).setMessage("")
                            .setNegativeButton(R.string.action_ok, null).show();
                    Message msg = handler.obtainMessage();
                    msg.obj = "update";
                    handler.sendMessageDelayed(msg, 500);
                    return true;
                }
            });
            advice.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    new AlertDialog.Builder(getActivity()).setTitle(R.string.action_advice)
                            .setMessage(getString(R.string.tip_msg_advice))
                            .setNegativeButton(R.string.action_cancel, null)
                            .setNeutralButton(R.string.action_e_mail, new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // 系统邮件系统的动作为android.content.Intent.ACTION_SEND
                                    Intent email = new Intent(
                                            android.content.Intent.ACTION_SEND);
                                    email.setType("text/plain");
                                    String[] emailReciver = new String[]{"zkzmzk@163.com"};

                                    // 设置邮件默认地址
                                    email.putExtra(
                                            android.content.Intent.EXTRA_EMAIL,
                                            emailReciver);
                                    // 设置邮件默认标题
                                    email.putExtra(
                                            android.content.Intent.EXTRA_SUBJECT,
                                            "");
                                    // 设置要默认发送的内容
                                    email.putExtra(
                                            android.content.Intent.EXTRA_TEXT,
                                            "");
                                    // 调用系统的邮件系统
                                    startActivity(Intent.createChooser(email,
                                            getString(R.string.tip_msg_mail)));

                                }
                            }).setPositiveButton(R.string.action_adviceOnline, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {

                            Intent intent = new Intent();
//									intent.setAction("android.intent.action.VIEW");
//									Uri content_url = Uri
//											.parse("http://weibo.com/guowangg");
//									intent.setData(content_url);
                            intent.setClass(Setting.this, FeedBack.class);
                            startActivity(intent);
                        }
                    }).show();

                    return true;
                }
            });

            exit.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getActivity()).setTitle(R.string.action_warn)
                        .setMessage(R.string.tip_exit)
                        .setPositiveButton(R.string.action_ok, new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MSSService.isExit = true;
                                stopService(new Intent().setClass(getActivity(),
                                        MSSService.class));
                                getActivity().setResult(RESULT_SETTING);
                                getActivity().finish();
                            }
                        }).setNegativeButton(R.string.action_cancel, null).show();
                    return true;
                }
            });

            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @SuppressLint("HandlerLeak")
        class mHandler extends Handler {

            @Override
            public void handleMessage(Message msg) {

                super.handleMessage(msg);
                if (msg.obj.toString().equals("frame")) {

                    C.FRAME = Integer.parseInt(frame.getValue());
                    frame.setSummary(frame.getEntry());
                } else if (msg.obj.toString().equals("ramLimit")) {

//                    ramLimit.setSummary(ramLimit.getEntry());
                } else if (msg.obj.toString().equals("version")) {

                    String s = getString(R.string.version_unKnow) + " ";
                    if (sp_userinfo.getString("UserVersionInfo", "").equals(
                            "OfficialVersion")) {

                        s = getString(R.string.version_Official) + " ";
                    } else if (sp_userinfo.getString("UserVersionInfo", "")
                            .equals("TrialVersion")) {

                        s = getString(R.string.version_Trial) + " ";
                    } else if (sp_userinfo.getString("UserVersionInfo", "")
                            .equals("TrialVersionOver")) {

                        s = getString(R.string.version_TrialOver) + " ";
                    } else if (sp_userinfo.getString("UserVersionInfo", "")
                            .equals(C.VERSION_FREE)) {
                        s = getString(R.string.version_free) + " ";
                    }
                    version.setSummary(s + sp_userinfo.getString("version", ""));
                } else if (msg.obj.toString().equals("update")) {
                    dialogUpdate.setMessage(getUpdateInfo(Setting.this));
                } else if (msg.obj.toString().equals("checkUpdate")) {

                    UpdateVersion updateVersion = new UpdateVersion(Setting.this);
                    if (!updateVersion.getNewVersionName().equals("error") && updateVersion.getNewVersionName().compareTo(updateVersion.getVersionName()) > 0) {

                        checkUpdate.setSummary(getString(R.string.tip_newVersion) + updateVersion.getNewVersionName());
                    }
                } else if (msg.obj.toString().equals("point")) {
                    float pointsBalance = PointsManager.getInstance(Setting.this).queryPoints();
                    app.setSummary("积分:" + (int) pointsBalance);
                }
            }

        }

    }

    @Override
    protected void onStart() {

        super.onStart();
        Intent service = new Intent(Setting.this, MSSService.class);
        this.bindService(service, mSc, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {

        super.onStop();
        this.unbindService(mSc);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PointsManager.getInstance(this).unRegisterNotify(this);
        OffersManager.getInstance(this).onAppExit();
    }

    public static String getUpdateInfo(Context mContext) {

        String update = "";
        update = update + mContext.getString(R.string.Ver_1_4_8);
        update = update + mContext.getString(R.string.Ver_1_4_5);
        update = update + mContext.getString(R.string.Ver_1_4_0);
        update = update + mContext.getString(R.string.Ver_1_3_9);
        update = update + mContext.getString(R.string.Ver_1_3_8);
        update = update + mContext.getString(R.string.Ver_1_3_7);
        update = update + mContext.getString(R.string.Ver_1_3_4);
        update = update + mContext.getString(R.string.Ver_1_3_3);
        update = update + mContext.getResources().getString(R.string.Ver_1_3_2);
        update = update + mContext.getResources().getString(R.string.Ver_1_3_0);
        update = update + mContext.getResources().getString(R.string.Ver_1_2_5);
        update = update + mContext.getResources().getString(R.string.Ver_1_2_3);
        update = update + mContext.getResources().getString(R.string.Ver_1_2_1);
        update = update + mContext.getResources().getString(R.string.Ver_1_2_0);
        update = update + mContext.getResources().getString(R.string.Ver_1_1_5);
        update = update + mContext.getResources().getString(R.string.Ver_1_1_4);
        update = update + mContext.getResources().getString(R.string.Ver_1_1_2);
        update = update + mContext.getResources().getString(R.string.Ver_1_0_9);
        update = update + mContext.getResources().getString(R.string.Ver_1_0_6);
        update = update + mContext.getResources().getString(R.string.Ver_1_0_2);
        update = update + mContext.getResources().getString(R.string.Ver_1_0_0);
        return update;
    }
}