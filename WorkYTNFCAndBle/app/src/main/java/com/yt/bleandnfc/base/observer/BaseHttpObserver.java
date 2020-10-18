package com.yt.bleandnfc.base.observer;

import android.app.Activity;
import android.os.Build;

import com.google.gson.stream.MalformedJsonException;
import com.yt.base.utils.LogUtlis;
import com.yt.base.utils.ToastUtils;
import com.yt.bleandnfc.base.YTApplication;
import com.yt.bleandnfc.manager.IntentManager;
import com.yt.bleandnfc.utils.NetworkUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import androidx.annotation.RequiresApi;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * 自定义基础Observer类 方便解析
 * com.lien.fitpatchh3t.baselibs.base
 * CLC  2020/7/20
 */
public abstract class BaseHttpObserver<T> implements Observer<T> {

    private final String TAG = BaseHttpObserver.class.getSimpleName();

    @Override
    public void onSubscribe(Disposable d) {
        // 开始
        LogUtlis.e(TAG,"onSubscribe");
    }

    @Override
    public void onNext(T o) {
        getData(o);
        LogUtlis.e(TAG,"onNext");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onError(Throwable e) {
        onErrorInfo(e);
        LogUtlis.e(TAG,"onError e : " + e.getMessage());
        if (e instanceof HttpException) {
            if (((HttpException) e).code() == 400) {
                ToastUtils.showText(YTApplication.getInstance(),"当前用户不存在");
            } else if (((HttpException) e).code() == 401){
                Activity activity = getGlobleActivity();
                if (activity != null) { // 可以跳转
                    IntentManager.getInstance().goLoginActivity(activity);
                } else {
                    ToastUtils.showText(YTApplication.getInstance(),"打不开登录界面，请先登录");
                }
            } else if (((HttpException) e).code() == 500 || ((HttpException) e).code() == 404) {
                ToastUtils.showText(YTApplication.getInstance(),"网络不稳定，请稍后再试");
            }
        } else if (e instanceof MalformedJsonException || e.getMessage().contains("at line 1 column")) {
            ToastUtils.showText(YTApplication.getInstance(),"解析异常");
        } else if (e.getMessage().contains("failed to connect to")){ // 界面反应比较慢 因为需要网络超时之后才会进行显示处理
            if (NetworkUtil.isNetworkConnected()) {
                ToastUtils.showText(YTApplication.getInstance(), "服务器正在维护，请稍后……");
            } else {
                ToastUtils.showText(YTApplication.getInstance(), "请连接网络");
            }
        } else if (e.getMessage().contains("timeout")){
            ToastUtils.showText(YTApplication.getInstance(), "服务器未启动，请耐心等待/稍后再试");
        }
    }

    @Override
    public void onComplete() {
        LogUtlis.e(TAG,"onComplete");
    }

    public abstract void getData(T data);

    public abstract void onErrorInfo(Throwable e);

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Activity getGlobleActivity() {
        Class activityThreadClass = null;
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
