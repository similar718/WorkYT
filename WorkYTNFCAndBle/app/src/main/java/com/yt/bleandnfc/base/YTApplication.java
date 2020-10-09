package com.yt.bleandnfc.base;

import android.content.Context;

import com.yt.base.application.BaseApplication;

public class YTApplication extends BaseApplication {

    private static YTApplication application;

    public String FILE_PROVIDER;

    public static YTApplication getInstance() {
        return application;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        FILE_PROVIDER = getPackageName() + ".provider";
    }

}
