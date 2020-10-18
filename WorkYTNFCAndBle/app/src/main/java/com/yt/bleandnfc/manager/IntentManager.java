package com.yt.bleandnfc.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.yt.bleandnfc.MainActivity;
import com.yt.bleandnfc.ui.login.LoginActivity;
import com.yt.bleandnfc.ui.scan.NextStepScanActivity;
import com.yt.bleandnfc.ui.zxing.CaptureActivity;

public class IntentManager {

    private final String TAG = IntentManager.class.getSimpleName();

    private IntentManager() {
    }

    public static final IntentManager getInstance() {
        return IntentManagerHolder.instance;
    }

    private void startActivity(Context context, Intent intent) {
        if (context == null) {
            return;
        }
        context.startActivity(intent);
        // page jump anim
//        if (context instanceof Activity) {
//            ((Activity) context).overridePendingTransition(R.anim.anim_push_left_in,
//                    R.anim.anim_push_left_out);
//        }
    }

    public void startActivity(Context context, Class clz) {
        startActivity(context, new Intent(context, clz));
    }

    private void startAcitivityForResult(Activity context, Intent intent, int requestCode) {
        if (context == null) {
            return;
        }
        context.startActivityForResult(intent, requestCode);
    }

    public void goActivity(Context context, Intent intent) {
        startActivity(context, intent);
    }


    private static class IntentManagerHolder {
        private static final IntentManager instance = new IntentManager();
    }

    /**
     * 登录
     * @param context
     */
    public void goLoginActivity(Activity context){
        startActivity(context, LoginActivity.class);
        context.finish(); // 只要是跳转到登录界面 当前界面都需要关闭
    }

    /**
     * 主界面
     * @param context
     */
    public void goMainActivity(Context context){
        Intent intent = new Intent(context, MainActivity.class);//主界面
        startActivity(context,intent);
    }

    /**
     * 跳转到当前应用的设置界面
     * @param context
     */
    public void goToAppSetting(Context context){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        startActivity(context,intent);
    }

    /**
     * 跳转到扫描界面前一个界面
     * @param context
     * @param type 1 绑定  2 维修保养  3 解绑  4 维保归还
     */
    public void goNextStepScanActivity(Context context,int type){
        Intent it_qr = new Intent(context, NextStepScanActivity.class);
        it_qr.putExtra("type",type);
        startActivity(context,it_qr);
    }

    /**
     * 跳转到扫描界面
     * @param context
     */
    public void goCaptureActivity(Context context,int type){
        Intent it_qr = new Intent(context, CaptureActivity.class);
        it_qr.putExtra("type",type);
        startActivity(context,it_qr);
    }
}
