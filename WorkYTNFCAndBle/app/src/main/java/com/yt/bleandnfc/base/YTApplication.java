package com.yt.bleandnfc.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kingja.loadsir.core.LoadSir;
import com.tencent.bugly.crashreport.CrashReport;
import com.yt.base.application.BaseApplication;
import com.yt.base.loadsir.CustomCallback;
import com.yt.base.loadsir.EmptyCallback;
import com.yt.base.loadsir.ErrorCallback;
import com.yt.base.loadsir.LoadingCallback;
import com.yt.base.loadsir.TimeoutCallback;
import com.yt.base.preference.PreferencesUtil;
import com.yt.base.utils.ToastUtil;
import com.yt.bleandnfc.MainActivity;
import com.yt.bleandnfc.manager.SPManager;
import com.yt.network.base.NetworkApi;

import java.util.ArrayList;
import java.util.List;

public class YTApplication extends BaseApplication implements Application.ActivityLifecycleCallbacks {

    private static YTApplication application;

    public String FILE_PROVIDER;

    private List<Activity> activityList;
    private float fontScale;

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


        fontScale = getFontScale();
        registerActivityLifecycleCallbacks(this);
        CrashReport.initCrashReport(getApplicationContext(), "a8269e0d16", false);
    }

    public static float getFontScale() {
        float fontScale = 1.0f;
        if (application != null) {
            fontScale = SPManager.getInstance().getSaveFontScale();
        }
        return fontScale;
    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        if (activityList == null) {
            activityList = new ArrayList<>();
        }
        // 禁止字体大小随系统设置变化
        Resources resources = activity.getResources();
        if (resources != null && resources.getConfiguration().fontScale != fontScale) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = fontScale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        activityList.add(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        if (activityList != null) {
            activityList.remove(activity);
        }
    }
    public static void setAppFontSize(float fontScale) {
        if (application != null) {
            List<Activity> activityList = application.activityList;
            if (activityList != null) {
                for (Activity activity : activityList) {
//                    if (activity instanceof MainActivity) {
//                        continue;
//                    }
                    Resources resources = activity.getResources();
                    if (resources != null) {
                        android.content.res.Configuration configuration = resources.getConfiguration();
                        configuration.fontScale = fontScale;
                        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
                        activity.recreate();
                        if (fontScale != application.fontScale) {
                            application.fontScale = fontScale;
                            SPManager.getInstance().setSaveFontScale(fontScale);
                        }
                    }
                }
            }
        }
    }
}
