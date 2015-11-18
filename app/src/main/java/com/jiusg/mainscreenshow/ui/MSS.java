package com.jiusg.mainscreenshow.ui;

import java.util.ArrayList;
import java.util.Calendar;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.service.MSSService;
import com.jiusg.mainscreenshow.tools.SmartBarUtils;
import com.meizu.mstore.license.ILicensingService;
import com.meizu.mstore.license.LicenseCheckHelper;
import com.meizu.mstore.license.LicenseResult;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MSS extends FragmentActivity implements View.OnClickListener {

    private ILicensingService mLicensingService = null;
    private AlertDialog dialogUpdate = null;
    private final String APKPublic = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCL1JrmG/y+pHE67dj99Myr+ZVVX7QgRUIuTWcQvdSmM8o57UEA214tzy9IZkDpAk7KWE9s4h2c3a4JwecCXIwbiT4K5X+7YNqPkAh1EIQ3MR7l3+WSqyAISzOf9XUMv7mzZ3QtKiAZmKH7SEs4M4VpFp+g5/DeBvIzjrKM47pYAQIDAQAB";
    private ServiceConnection mLicenseConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mLicensingService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mLicensingService = ILicensingService.Stub.asInterface(service);
        }
    };

    private ViewPager viewPager = null;
    private ArrayList<Fragment> list = null;

    private Fragment mssEvent = null;
    private Fragment mssPreview = null;

    private LicenseHandler hd;

    private SharedPreferences sp_userinfo; // 存储基本数据


    private ImageButton setting = null;
    private TextView title = null;
    private ImageButton event = null;
    private ImageButton preview = null;

    public MSSService mssservice = null;
    private ServiceConnection mSc = null;
    private final int REQUEST_SETTING = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

//		boolean findMethod = findActionBarTabsShowAtBottom();
//		if (!findMethod) { // 取消ActionBar拆分，换用TabHost
//			getWindow().setUiOptions(0);
//		}
//
//		if(C.ISMEIZU){
//			getWindow().setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
//			setTheme(R.style.AppTheme_Meizu);
//		}

//		setContentView(R.layout.tab_content);

        setActionBarLayout(R.layout.actionbar_mss);
        setContentView(R.layout.activity_mss);
        sp_userinfo = getSharedPreferences("userinfo", 0);

        viewPager = (ViewPager) findViewById(R.id.mss_vp);
        setting = (ImageButton) findViewById(R.id.actionbar_setting);
        title = (TextView) findViewById(R.id.actionbar_title);
        event = (ImageButton) findViewById(R.id.actionbar_event);
        preview = (ImageButton) findViewById(R.id.actionbar_preview);

        setting.setOnClickListener(this);
        event.setOnClickListener(this);
        preview.setOnClickListener(this);

        list = new ArrayList<android.support.v4.app.Fragment>();
        mssEvent = new MSSEventFragment();
        mssPreview = new MssPreviewFragment();
        list.add(mssEvent);
        list.add(mssPreview);

        viewPager.setAdapter(new MSSFragmentAdapter(getSupportFragmentManager(), list));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        title.setText(R.string.app_name);
                        event.setImageResource(R.drawable.ic_event_press);
                        preview.setImageResource(R.drawable.ic_preview);
                        break;
                    case 1:
                        title.setText(R.string.action_MSSPreview);
                        event.setImageResource(R.drawable.ic_event);
                        preview.setImageResource(R.drawable.ic_preview_press);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//		final TabHost tabHost = getTabHost();
//		tabHost.addTab(tabHost
//				.newTabSpec("event")
//				.setIndicator(null,
//						getResources().getDrawable(R.drawable.ic_tab_event))
//				.setContent(new Intent(this, MSSEvent.class)));
//		tabHost.addTab(tabHost
//				.newTabSpec("preview")
//				.setIndicator(null,
//						getResources().getDrawable(R.drawable.ic_tab_preview))
//				.setContent(new Intent(this, MSSPreview.class)));

//		if (true) {
//			getTabWidget().setVisibility(View.GONE);
//
//			final ActionBar bar = getActionBar();
//			bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//
//			bar.addTab(bar.newTab().setIcon(R.drawable.ic_tab_event)
//					.setTabListener(this));
//			bar.addTab(bar.newTab().setIcon(R.drawable.ic_tab_preview)
//					.setTabListener(this));
//
//			// 如果是用户自定义的View，可以像下面这样操作
//			// bar.addTab(bar.newTab().setTabListener(this).setCustomView(R.layout.tab_widget_indicator).setTabListener(this));
//
//			// 设置ActionBar Tab显示在底栏
//			SmartBarUtils.setActionBarTabsShowAtBottom(bar, true);
//		}
//
//		SmartBarUtils.setBackIcon(getActionBar(),
//				getResources().getDrawable(R.drawable.ic_back));

        // 写入免费版
        if (C.ISFREE) {
            sp_userinfo.edit().putString("UserVersionInfo", C.VERSION_FREE)
                    .commit();
        }

        // 写入正式版本
        // sp_userinfo.edit().putString("UserVersionInfo", "OfficialVersion")
        // .commit();

        // 如果已为免费版，就不用再验证任何了
        if (!sp_userinfo.getString("UserVersionInfo", "")
                .equals(C.VERSION_FREE)) {

            // 如果已经验证了用户为正式版，那么每次启动应用就没必要验证了
            if (!sp_userinfo.getString("UserVersionInfo", "").equals(
                    "OfficialVersion")) {
                // 绑定服务 这些都是用来验证是否正式版本的
                if (mLicensingService == null) {

                    Intent intent = new Intent();
                    intent.setAction(ILicensingService.class.getName());
                    bindService(intent, mLicenseConnection,
                            Context.BIND_AUTO_CREATE);

                }

                hd = new LicenseHandler();
                Message msg = hd.obtainMessage();
                msg.obj = "License";
                hd.sendMessageDelayed(msg, 3000);
            }
        }

        mSc = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                mssservice = ((MSSService.MSSSBinder) service).getMSSService();

            }
        };
    }

    // 查找设置ActionBar Tab显示在底栏的方法，找不到method则返回false。
    private boolean findActionBarTabsShowAtBottom() {
        try {
            Class.forName("android.app.ActionBar").getMethod(
                    "setTabsShowAtBottom", new Class[]{boolean.class});
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent service = new Intent(MSS.this, MSSService.class);
        this.getApplicationContext().bindService(service, mSc,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.getApplicationContext().unbindService(mSc);
    }


    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_settings, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_settings:
//                startActivity(new Intent().setClass(MSS.this, Setting.class));
//                break;
//            case R.id.menu_exit:
//                new AlertDialog.Builder(MSS.this).setTitle(R.string.action_warn)
//                        .setMessage(R.string.tip_exit)
//                        .setPositiveButton(R.string.action_ok, new OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                stopService(new Intent().setClass(MSS.this,
//                                        MSSService.class));
//                                System.exit(0);
//                            }
//                        }).setNegativeButton(R.string.action_cancel, null).show();
//
//                break;
//            default:
//                break;
//        }
//        return super.onMenuItemSelected(featureId, item);
//    }

    @SuppressLint("HandlerLeak")
    class LicenseHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            // super.handleMessage(msg);

            if (msg.obj.toString().equals("License")) {
                // 调用服务检查License，并返回检查结果
                LicenseResult result = null;
                try {

                    result = mLicensingService.checkLicense(getApplication()
                            .getPackageName());

                } catch (RemoteException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplication(), "错误:4 验证License发生严重错误！",
                            Toast.LENGTH_SHORT).show();
                }
                // 进行判断
                if (result.getResponseCode() == LicenseResult.RESPONSE_CODE_SUCCESS) {

                    boolean bSuccess = LicenseCheckHelper.checkResult(
                            APKPublic, result);

                    if (bSuccess
                            && result.getPurchaseType() == LicenseResult.PURCHASE_TYPE_NORMAL) {
                        // 当前验证为正式版
                        sp_userinfo
                                .edit()
                                .putString("UserVersionInfo", "OfficialVersion")
                                .commit();

                    } else {

                        if (bSuccess
                                && result.getPurchaseType() == LicenseResult.PURCHASE_TYPE_TRIAL) {
                            // 当前验证为试用版
                            Calendar cal = result.getStartDate();
                            cal.add(Calendar.DAY_OF_MONTH, 3);
                            Calendar cNow = Calendar.getInstance(); // 获取当前系统时间
                            if (cNow.after(cal)) {

                                Toast.makeText(getApplication(),
                                        "试用时间已经结束！桌面秀将不再提供服务！",
                                        Toast.LENGTH_SHORT).show();
                                Intent it = new Intent();
                                it.setClass(MSS.this, MSSService.class);
                                stopService(it);
                                sp_userinfo
                                        .edit()
                                        .putString("UserVersionInfo",
                                                "TrialVersionOver").commit();

                            } else {

                                Toast.makeText(getApplication(), "当前为试用版！",
                                        Toast.LENGTH_SHORT).show();
                                sp_userinfo
                                        .edit()
                                        .putString("UserVersionInfo",
                                                "TrialVersion").commit();
                            }
                        } else {
                            Intent it = new Intent();
                            it.setClass(MSS.this, MSSService.class);
                            stopService(it);
                            Toast.makeText(getApplication(), "错误:1 验证失败！请重试",
                                    Toast.LENGTH_SHORT).show();
                            sp_userinfo.edit()
                                    .putString("UserVersionInfo", "Error")
                                    .commit();
                        }
                    }
                } else {
                    if (result.getResponseCode() == LicenseResult.RESPONSE_CODE_NO_LICENSE_FILE) {

                        Intent it = new Intent();
                        it.setClass(MSS.this, MSSService.class);
                        stopService(it);
                        Toast.makeText(getApplication(), "错误:2 验证失败！请重试！",
                                Toast.LENGTH_SHORT).show();
                        sp_userinfo.edit()
                                .putString("UserVersionInfo", "Error").commit();
                    } else {

                        Intent it = new Intent();
                        it.setClass(MSS.this, MSSService.class);
                        stopService(it);
                        Toast.makeText(getApplication(), "错误:3 验证失败！请重试",
                                Toast.LENGTH_SHORT).show();
                        sp_userinfo.edit()
                                .putString("UserVersionInfo", "Error").commit();
                    }
                }
                // 解除绑定
                if (mLicensingService != null) {
                    unbindService(mLicenseConnection);
                }
            } else if (msg.obj.toString().equals("update")) {
                dialogUpdate.setMessage(Setting.getUpdateInfo(MSS.this));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_setting:
                startActivityForResult(new Intent(MSS.this, Setting.class),REQUEST_SETTING);
                break;
            case R.id.actionbar_event:
                viewPager.setCurrentItem(0);
                break;
            case R.id.actionbar_preview:
                viewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    class MSSFragmentAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list = null;

        public MSSFragmentAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_SETTING)
            if(resultCode == Setting.RESULT_SETTING){
                finish();
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置ActionBar的布局
     *
     * @param layoutId 布局Id
     */
    public void setActionBarLayout(int layoutId) {
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);

            LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(layoutId, null);
            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            actionBar.setCustomView(v, layout);
        }
    }

}
