package com.jiusg.mainscreenshow.base;

import com.jiusg.mainscreenshow.tools.CrashHandler;

import android.app.Application;  

/**  
 * 在Application中注册未捕获异常处理器。 
 * 程序意外停止时，捕获错误
 * 
 * 暂未使用了
 */  
public class CrashApplication extends Application {  
    @Override  
    public void onCreate() {  
        super.onCreate();  
        CrashHandler crashHandler = CrashHandler.getInstance();  
        // 注册crashHandler  
        crashHandler.init(getApplicationContext());  
    }  
}  