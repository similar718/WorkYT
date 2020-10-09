package com.yt.base.application;

import android.app.Application;
import android.content.Context;

import com.yt.base.utils.CrashHandlerUtils;

public class BaseApplication extends Application {

    public static BaseApplication INSTANCE;

    public static BaseApplication getInstance1() {
        return INSTANCE;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandlerUtils.getInstance().init(this);
    }

    public Context getContext(){
        return getApplicationContext();
    }
}
