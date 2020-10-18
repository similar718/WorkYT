package com.yt.bleandnfc.base;

import android.content.Context;

import com.kingja.loadsir.core.LoadSir;
import com.yt.base.application.BaseApplication;
import com.yt.base.loadsir.CustomCallback;
import com.yt.base.loadsir.EmptyCallback;
import com.yt.base.loadsir.ErrorCallback;
import com.yt.base.loadsir.LoadingCallback;
import com.yt.base.loadsir.TimeoutCallback;
import com.yt.base.preference.PreferencesUtil;
import com.yt.base.utils.ToastUtil;
import com.yt.network.base.NetworkApi;

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

        PreferencesUtil.init(this);
        NetworkApi.init(new NetworkRequestInfo(this));
        ToastUtil.init(this);
        LoadSir.beginBuilder()
                .addCallback(new ErrorCallback())//添加各种状态页
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .addCallback(new TimeoutCallback())
                .addCallback(new CustomCallback())
                .setDefaultCallback(LoadingCallback.class)//设置默认状态页
                .commit();

        FILE_PROVIDER = getPackageName() + ".provider";
    }

}
