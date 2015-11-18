package com.jiusg.mainscreenshow.tools;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.jiusg.mainscreenshow.R;

/**
 * 版本更新操作
 *
 * @author Administrator
 */
public class UpdateVersion {

    private Context mContext;
    private SharedPreferences sp = null;
    private AlertDialog alertDialog = null;
    private UpdateHandler hd = null;

    private final String CLASS_NAME = "Version";
    private final String VERSION_NAME = "versionName";
    private final String VERSION_INFO = "content";
    private final String VERSION_URL = "url";
    private final String PATH = Environment.getExternalStorageDirectory()
            .getPath() + "/MSShow/Apk/";
    private final String APKNAME = "MainScreenShow.apk";
    private final String PACKAGENAME = "com.jiusg.mainscreenshow";


    public UpdateVersion(Context mContext) {
        this.mContext = mContext;
        this.sp = mContext.getSharedPreferences("versionupdate", 0);
        hd = new UpdateHandler();
    }

    /**
     * 检测是否需要进行版本更新操作
     *
     * @param day       隔几天进行一次操作
     * @param startHour 允许操作的开始时间
     * @param stopHour  允许操作的结束时间
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public boolean isCheckUpdate(int day, int startHour, int stopHour) {

        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        if (sp.getString("time", "null").equals("null")) {
            sp.edit().putString("time", Time.getNowYMDHMSTime()).commit();
        } else {
            DateFormat fmtDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = fmtDateTime.parse(sp.getString("time", "null"));
                calendar2.setTime(date);
            } catch (ParseException e) {

                e.printStackTrace();
                return false;
            }
        }
        if (calendar2 != null) {

            calendar2.add(Calendar.DAY_OF_MONTH, day);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if (calendar.after(calendar2) && hour >= startHour
                    && hour <= stopHour)
                return true;
        }
        return false;
    }

    /**
     * 暂不提醒操作
     */
    public void notUpdate() {

        sp.edit().putString("time", Time.getNowYMDHMSTime()).commit();
        sp.edit().putBoolean("isTip", false).commit();
    }

    /**
     * 从网络检查有没有新版本
     *
     * @param callBack 回调，返回检查结果，如果没有版本更新则返回当前版本号
     */
    public void checkVersionFromNetwork(final CallBack callBack) {

        if (!getVersionName().equals("0.0.0")) {
            AVQuery<AVObject> query = new AVQuery<AVObject>(CLASS_NAME);
            query.orderByDescending(VERSION_NAME);
            query.whereGreaterThan(VERSION_NAME, getVersionName());
            query.findInBackground(new FindCallback<AVObject>() {

                @Override
                public void done(List<AVObject> versions, AVException e) {

                    if (null == e) {
                        if (versions.size() > 0) {

                            String versionInfo = "";
                            String versionName = versions.get(0).getString(
                                    VERSION_NAME);
                            String versionUrl = versions.get(0).getString(
                                    VERSION_URL);
                            for (int i = 0; i < versions.size(); i++) {

                                versionInfo = versionInfo
                                        + "\r\nV  "
                                        + versions.get(i).getString(
                                        VERSION_NAME) + "\r\n";
                                versionInfo = versionInfo
                                        + versions.get(i).getString(
                                        VERSION_INFO) + "\r\n";
                            }
                            putNewVersionInfo(versionName, versionUrl, versionInfo);
                            callBack.done(versionName, versionUrl, versionInfo);
                        } else {
                            callBack.done(getVersionName(), "", "");
                        }
                    } else {
                        callBack.done(getVersionName(), "", "");
                    }
                }
            });
        }
    }

     /**
     * 获取当前应用版本号
     *
     * @return
     */
    public String getVersionName() {

        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(mContext.getPackageName(),
                    0);
        } catch (NameNotFoundException e) {

            e.printStackTrace();
            return "0.0.0";
        }
        String version = packInfo.versionName;

        return version;
    }

    /**
     * 存入新版本信息
     */
    private void putNewVersionInfo(String newVersionName, String url,
                                   String versionInfo) {

        sp.edit().putBoolean("isTip", true).commit();
        sp.edit().putString("newVersionName", newVersionName).commit();
        sp.edit().putString("url", url).commit();
        sp.edit().putString("versionInfo", versionInfo).commit();
    }

    /**
     * 是否提示版本更新
     *
     * @return
     */
    public boolean isTipVersion() {
        if(sp.getString("newVersionName",getVersionName()).compareTo(getVersionName()) > 0)
            return sp.getBoolean("isTip", false);
        return false;
    }

    public String getUrl() {
        return sp.getString("url", "error");
    }

    /**
     * 返回最新的版本，如果没有数据则返回"error"
     *
     * @return
     */
    public String getNewVersionName() {
        return sp.getString("newVersionName", "error");
    }

    public String getVersionInfo() {
        return sp.getString("versionInfo", "error");
    }

    /**
     * show updateDialog
     */
    public void showUpdate() {

        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.tip_newVersion_) + "  " + getNewVersionName())
                .setMessage(getVersionInfo())
                .setPositiveButton(R.string.action_updateNow, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String versionName = getApkVersionName(PATH + APKNAME);
                        if (versionName == null || !versionName.equals(getNewVersionName())) {

                            alertDialog = new AlertDialog.Builder(mContext)
                                    .setTitle(R.string.action_updating).setMessage(mContext.getString(R.string.tip_msg_getData))
                                    .setPositiveButton(R.string.action_updateBackground, null).show();
                            try {

                                final Download download = new Download(getUrl());
                                download.getLenghtInBackground(download.new DoneSizeCallback() {

                                    @Override
                                    public void Done(int size) {

                                        int size0 = size / (1024 * 1024);
                                        int size1 = size % (1024 * 1024);
                                        int size2 = size1 / 10000;
                                        final String strSize = size0 + "." + size2
                                                + "MB  ";

                                        download.dowmSdInBackground(PATH,
                                                APKNAME,
                                                download.new DoneCallBack() {

                                                    @Override
                                                    public void done(String path) {

                                                        Message msg = hd
                                                                .obtainMessage();
                                                        msg.what = 0;
                                                        msg.obj = mContext.getString(R.string.tip_downloadComplete);
                                                        hd.sendMessage(msg);
                                                        Message msg1 = hd
                                                                .obtainMessage();
                                                        msg1.what = 2;
                                                        msg1.obj = mContext.getString(R.string.tip_downloadComplete);
                                                        hd.sendMessage(msg1);

                                                    }
                                                }, download.new ProgressCallback() {

                                                    @Override
                                                    public void done(int progress) {

                                                        Message msg = hd
                                                                .obtainMessage();
                                                        msg.what = 0;
                                                        msg.obj = mContext.getString(R.string.tip_download_size) + strSize
                                                                + mContext.getString(R.string.tip_download_) + progress
                                                                + "%";
                                                        hd.sendMessage(msg);
                                                    }
                                                }, download.new FailedCallback() {

                                                    @Override
                                                    public void failed(Exception e) {

                                                        Message msg = hd
                                                                .obtainMessage();
                                                        msg.what = 1;
                                                        msg.obj = mContext.getString(R.string.tip_downloadFailed);
                                                        hd.sendMessage(msg);
                                                    }
                                                });
                                    }
                                }, download.new FailedCallback() {

                                    @Override
                                    public void failed(Exception e) {

                                        Message msg = hd
                                                .obtainMessage();
                                        msg.what = 1;
                                        msg.obj = mContext.getString(R.string.tip_downloadFailed);
                                        hd.sendMessage(msg);
                                    }
                                });


                            } catch (Exception e) {

                                Message msg = hd
                                        .obtainMessage();
                                msg.what = 1;
                                msg.obj = mContext.getString(R.string.tip_downloadFailed);
                                hd.sendMessage(msg);
                            }

                        } else {

                            Message msg1 = hd
                                    .obtainMessage();
                            msg1.what = 2;
                            msg1.obj = mContext.getString(R.string.tip_downloadComplete);
                            hd.sendMessage(msg1);
                        }
                    }
                }).setNegativeButton(R.string.action_WaitSay, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                notUpdate();
            }
        }).show();
    }


    /**
     * show downFailedDialog.
     */
    private void showDownFailed() {
        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.tip)
                .setMessage(mContext.getString(R.string.tip_downloadFailed_))
                .setPositiveButton(R.string.action_ok, null).show();
    }

    public abstract class CallBack {
        public abstract void done(String versionName, String versionUrl,
                                  String versionInfo);
    }

    @SuppressLint("HandlerLeak")
    class UpdateHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0) {

                if (alertDialog != null && alertDialog.isShowing()) {

                    alertDialog.setMessage(msg.obj.toString());
                }
            } else if (msg.what == 1) {
                if (alertDialog != null && alertDialog.isShowing()) {

                    showDownFailed();

                }
            } else if (msg.what == 2) {

                if (alertDialog != null && alertDialog.isShowing()) {

                    alertDialog.dismiss();
                }

                installApk();
            }
        }

    }

    /**
     * 安装apk
     */
    public void installApk() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(PATH + APKNAME)),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    /**
     * 获得某个安装包的版本名称
     *
     * @param path
     * @return
     */
    public String getApkVersionName(String path) {

        PackageManager manager = mContext.getPackageManager();
        PackageInfo info = manager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo applicationInfo = info.applicationInfo;
            String packageName = applicationInfo.packageName;
            System.out.println(packageName);
            if (packageName.equals(PACKAGENAME)) {
                System.out.println(info.versionName);
                return info.versionName;
            }
        }
        return null;
    }

}
