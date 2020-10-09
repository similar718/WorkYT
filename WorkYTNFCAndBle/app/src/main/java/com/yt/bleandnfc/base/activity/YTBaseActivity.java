package com.yt.bleandnfc.base.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yt.base.activity.BaseActivity;
import com.yt.base.view.BaseViewModel;
import com.yt.bleandnfc.R;
import com.yt.bleandnfc.eventbus.ScanResult;
import com.yt.bleandnfc.ui.dialog.CommonCenterDialog;
import com.yt.common.interfaces.IPermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class YTBaseActivity<VM extends BaseViewModel, DB extends ViewDataBinding> extends BaseActivity {

    protected VM viewModel;
    public DB dataBinding;

    public Context mContext;

    public final int REQUEST_CODE = 1; // 权限监听的请求Code码

    public IPermissionListener mlistener;

    @Subscribe
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, setLayoutId());
        getSupportActionBar().hide();//去掉标题栏
        EventBus.getDefault().register(this);
        mContext = this;
        if (viewModel == null) {
            viewModel = createViewModel();
        }
        viewModel.setIView(this);

        //改变状态栏颜色
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected abstract int setLayoutId();

    protected abstract VM createViewModel();

    protected abstract void initData();

    /**
     * 授权信息提示框
     */
    public void createDialog(String title) {
        final CommonCenterDialog confirmDialog = new CommonCenterDialog(mContext,
                title,
                getString(R.string.permission_confirm),
                getString(R.string.permission_deny));
        confirmDialog.show();
        confirmDialog.setClicklistener(new CommonCenterDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                confirmDialog.dismiss();
                toGrantAuthorization();
            }

            @Override
            public void doCancel() {
                confirmDialog.dismiss();
                denyPermission();
            }
        });
    }

    /**
     * 拒绝权限的处理情况
     */
    public void denyPermission(){}

    /**
     * 点击了去授权
     */
    public void toGrantAuthorization(){}

    /**
     * 权限申请
     * @param permissions 待申请的权限集合
     * @param listener  申请结果监听事件
     */
    protected void requestRunTimePermission(String[] permissions, IPermissionListener listener){
        this.mlistener = listener;
        //用于存放为授权的权限
        List<String> permissionList = new ArrayList<>();
        //遍历传递过来的权限集合
        for (String permission : permissions) {
            //判断是否已经授权
            if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                //未授权，则加入待授权的权限集合中
                permissionList.add(permission);
            }
        }
        //判断集合
        if (!permissionList.isEmpty()){  //如果集合不为空，则需要去授权
            ActivityCompat.requestPermissions(this,permissionList.toArray(new String[permissionList.size()]),REQUEST_CODE);
        }else{  //为空，则已经全部授权
            if(listener != null){
                listener.onGranted();
            }
        }
    }

    /**
     * 权限申请结果
     * @param requestCode  请求码
     * @param permissions  所有的权限集合
     * @param grantResults 授权结果集合
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0){
                    //被用户拒绝的权限集合
                    List<String> deniedPermissions = new ArrayList<>();
                    //用户通过的权限集合
                    List<String> grantedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        //获取授权结果，这是一个int类型的值
                        int grantResult = grantResults[i];

                        if (grantResult != PackageManager.PERMISSION_GRANTED){ //用户拒绝授权的权限
                            String permission = permissions[i];
                            deniedPermissions.add(permission);
                        }else{  //用户同意的权限
                            String permission = permissions[i];
                            grantedPermissions.add(permission);
                        }
                    }

                    if (deniedPermissions.isEmpty()){  //用户拒绝权限为空
                        if(mlistener != null){
                            mlistener.onGranted();
                        }
                    }else {  //不为空
                        if(mlistener != null){
                            //回调授权成功的接口
                            mlistener.onDenied(deniedPermissions);
                            //回调授权失败的接口
                            mlistener.onGranted(grantedPermissions);
                            mlistener.onDenied();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Subscribe
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
