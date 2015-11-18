package com.jiusg.mainscreenshow.ui;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.tools.SmartBarUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;

public class SettingApp extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if(C.ISMEIZU)
			getWindow().setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingAppFragment()).commit();
		SmartBarUtils.setBackIcon(getActionBar(),
				getResources().getDrawable(R.drawable.ic_back));

	}

	@SuppressLint("ValidFragment")
	public class SettingAppFragment extends PreferenceFragment {

		private PreferenceScreen lockhelper;
		private PreferenceScreen flashlight;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);
			
			addPreferencesFromResource(R.xml.setting_app);
			
			lockhelper = (PreferenceScreen) findPreference("LockHelper");
			flashlight = (PreferenceScreen) findPreference("FlashLight");
			
			lockhelper.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					if(C.ISMEIZU){
					String appIdentify = "360f07008a874b078626604f4ba99a7f";
					Uri appUri = Uri
							.parse("mstore:http://app.meizu.com/phone/apps/"
									+ appIdentify);
					Intent intent = new Intent(Intent.ACTION_VIEW, appUri);
					startActivity(intent);
					}else{
						String url = "http://app.flyme.cn/apps/public/detail?package_name=com.jiusg.lockhelper";

						Intent intent = new Intent(Intent.ACTION_VIEW);

						intent.setData(Uri.parse(url));

						startActivity(intent);
					}
					return true;
				}
			});
			flashlight.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					if(C.ISMEIZU){
					String appIdentify = "d1db0217ce204532a739a633209c0e52";
					Uri appUri = Uri
							.parse("mstore:http://app.meizu.com/phone/apps/"
									+ appIdentify);
					Intent intent = new Intent(Intent.ACTION_VIEW, appUri);
					startActivity(intent);
					}else{
						String url = "http://app.flyme.cn/apps/public/detail?package_name=com.jiusg.flashlight";

						Intent intent = new Intent(Intent.ACTION_VIEW);

						intent.setData(Uri.parse(url));

						startActivity(intent);
					}
					return true;
				}
			});
		}

	}
}
