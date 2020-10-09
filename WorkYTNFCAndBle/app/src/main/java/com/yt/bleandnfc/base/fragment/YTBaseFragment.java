package com.yt.bleandnfc.base.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yt.base.fragment.BaseFragment;
import com.yt.base.view.BaseViewModel;
import com.yt.bleandnfc.R;
import com.yt.bleandnfc.ui.dialog.CommonCenterDialog;
import com.yt.common.interfaces.IPermissionListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class YTBaseFragment<VM extends BaseViewModel, DB extends ViewDataBinding> extends BaseFragment {

    protected VM viewModel;
    public DB dataBinding;

    public Context mContext;

    private boolean isFirstLoad = false;

    public final int REQUEST_CODE = 1; // 权限监听的请求Code码

    public IPermissionListener mlistener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater,setLayoutId(),container,false);
        View view = dataBinding.getRoot();
        if (viewModel == null) {
            createViewModel();
        }
        initData();

        isFirstLoad = true;//视图创建完成，将变量置为true

        if (getUserVisibleHint()) {//如果Fragment可见进行数据加载
            onLazyLoad();
            isFirstLoad = false;
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showToast(final String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToastMsg(msg);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFirstLoad = false;//视图销毁将变量置为false
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isFirstLoad && isVisibleToUser) {//视图变为可见并且是第一次加载
            onLazyLoad();
            isFirstLoad = false;
        }

    }

    //数据加载接口，留给子类实现
    public abstract void onLazyLoad();

    protected abstract int setLayoutId();

    protected abstract VM createViewModel();

    protected abstract void initData();

    /**
     * 授权信息提示框
     */
    public void createDialog(String title) {
        final CommonCenterDialog confirmDialog = new CommonCenterDialog(getActivity(),
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
            if (ContextCompat.checkSelfPermission(getActivity(),permission) != PackageManager.PERMISSION_GRANTED){
                //未授权，则加入待授权的权限集合中
                permissionList.add(permission);
            }
        }
        //判断集合
        if (!permissionList.isEmpty()){  //如果集合不为空，则需要去授权
            ActivityCompat.requestPermissions(getActivity(),permissionList.toArray(new String[permissionList.size()]),REQUEST_CODE);
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
}
