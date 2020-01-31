package com.sadam.sadamlibarary;

import android.app.Application;
import android.content.Context;

/**
 * 专门用来获取全局Context的
 * 得在AndroidManifest.xml文件的Aplication内以name属性注册，才能有效。
 */
public class MyApplication extends Application {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
