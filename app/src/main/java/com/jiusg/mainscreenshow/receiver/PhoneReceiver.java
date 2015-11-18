package com.jiusg.mainscreenshow.receiver;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * 这里用发送广播来通知Service
 * @author Administrator
 *
 */
public class PhoneReceiver extends BroadcastReceiver {

	
	private Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		this.context = context;

		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
			
			
		} else {
			// 非去电即来电
			
			
			TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);     
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);  

		}
	}
	
	PhoneStateListener listener=new PhoneStateListener(){  
		   
        @Override  
        public void onCallStateChanged(int state, String incomingNumber) {  
                // TODO Auto-generated method stub  
                //state 当前状态 incomingNumber,貌似没有去电的API  
                super.onCallStateChanged(state, incomingNumber);  
                Intent it = new Intent();
                switch(state){  
                case TelephonyManager.CALL_STATE_IDLE:  
        			it.setAction("com.jiusg.mainscreenshow");
        			it.putExtra("msg", "call_stop");
        			context.sendBroadcast(it);
                        break;  
                case TelephonyManager.CALL_STATE_OFFHOOK:  
        			it.setAction("com.jiusg.mainscreenshow");
        			it.putExtra("msg", "call_start");
        			context.sendBroadcast(it);  
                        break;  
                case TelephonyManager.CALL_STATE_RINGING:  
                      //  System.out.println("响铃:来电号码"+incomingNumber);
        			it.setAction("com.jiusg.mainscreenshow");
        			it.putExtra("msg", "call_ring");
        			context.sendBroadcast(it);
                        //输出来电号码  
                        break; 
                }  
        }   
};  

}
