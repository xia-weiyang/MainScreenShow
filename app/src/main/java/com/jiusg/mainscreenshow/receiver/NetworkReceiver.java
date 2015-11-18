package com.jiusg.mainscreenshow.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {

	private final String TAG = "NetworkReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// NetworkInfo mobileInfo =
		// manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiInfo = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		if(wifiInfo.isConnected()){
			
			Log.i(TAG, "WiFi is connected!");
			Intent it = new Intent();
			it.setAction("com.jiusg.mainscreenshow");
			it.putExtra("msg", "wifi");
			context.sendBroadcast(it);
		}

	}

}
