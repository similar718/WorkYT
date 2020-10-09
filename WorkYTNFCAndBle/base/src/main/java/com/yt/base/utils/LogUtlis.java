package com.yt.base.utils;

import android.util.Log;

public class LogUtlis {

    private static boolean mIsShowLog = true; // release的时候需要将这个值设置为false 表示不显示Log的打印

    public static final int INFO = 4;

    public static void v(String tag, String content){
        if (mIsShowLog) {
            Log.v(tag, content);
        }
    }

    public static void d(String tag, String content){
        if (mIsShowLog) {
            Log.d(tag, content);
        }
    }

    public static void i(String tag, String content){
        if (mIsShowLog) {
            Log.i(tag,content);
        }
    }

    public static void w(String tag, String content){
        if (mIsShowLog) {
            Log.w(tag, content);
        }
    }

    public static void w(String tag, Throwable e){
        if (mIsShowLog) {
            Log.w(tag, e);
        }
    }

    public static void w(String tag, String content, Throwable e){
        if (mIsShowLog) {
            Log.w(tag, content, e);
        }
    }

    public static void e(String tag, String content){
        if (mIsShowLog) {
            Log.e(tag,content);
        }
    }

    public static void e(String tag, String content, Throwable e){
        if (mIsShowLog) {
            Log.e(tag,content,e);
        }
    }

    public static boolean isLoggable(String tag, int info){
        return Log.isLoggable(tag, Log.INFO);
    }
}
