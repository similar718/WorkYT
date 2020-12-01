package com.yt.bleandnfc.ui.login;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import com.clj.fastble.BleNFCManager;
import com.yt.bleandnfc.R;
import com.yt.bleandnfc.api.model.LoginModel;
import com.yt.bleandnfc.base.YTApplication;
import com.yt.bleandnfc.databinding.ActivityLoginBinding;
import com.yt.bleandnfc.manager.IntentManager;
import com.yt.bleandnfc.manager.SPManager;
import com.yt.bleandnfc.mvvm.viewmodel.LoginViewModel;
import com.yt.bleandnfc.service.KeepAppLifeService;
import com.yt.bleandnfc.udp.demo.UDPBuild;
import com.yt.bleandnfc.ui.loginhome.BaseBleActivity;

import androidx.lifecycle.Observer;

import java.net.DatagramPacket;

public class LoginActivity extends BaseBleActivity<LoginViewModel, ActivityLoginBinding> {

    @Override
    protected int setLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected LoginViewModel createViewModel() {
        viewModel = new LoginViewModel((Activity) mContext);
        viewModel.setIView(this);

        viewModel.mLoginModel.observe(this, new Observer<LoginModel>() {
            @Override
            public void onChanged(LoginModel loginModel) {
                if (loginModel != null) {
                    if (loginModel.obj != null && loginModel.code == 200) {
                        showToastMsg("登录成功");
                        SPManager.getInstance().setUserId(loginModel.getObj().getId());
                        SPManager.getInstance().setDeptId(loginModel.getObj().getDeptId());
                        SPManager.getInstance().setDeptName(loginModel.getObj().getDeptName());
                        SPManager.getInstance().setName(loginModel.getObj().getName());
                        loginSuccess();
                    } else {
                        showToastMsg(loginModel.message);
                    }
                } else {
                    showToastMsg("请求异常");
                }
            }
        });
        return viewModel;
    }
    // APP主要权限 存储权限和位置权限 位置权限主要是在搜索心贴的时候有用到
    public static final String[] PERMISSIONPERMS_STORAGE_LOCATION = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA};

    @Override
    protected void initData() {
        initBlueTooth();
        String username = SPManager.getInstance().getUserName();
        String userpwd = SPManager.getInstance().getUserPwd();
        if (!TextUtils.isEmpty(username)){
            dataBinding.etUsername.setText(username);
        }
        if (!TextUtils.isEmpty(userpwd)){
            dataBinding.etPwd.setText(userpwd);
        }

        dataBinding.cbSaveInfo.setChecked(SPManager.getInstance().getSaveStatusLogin());

        // 开始申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestRunTimePermission(PERMISSIONPERMS_STORAGE_LOCATION,iPermissionListener);
        }

        dataBinding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = dataBinding.etUsername.getText().toString().trim();
                String userpwd = dataBinding.etPwd.getText().toString().trim();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(userpwd)){
                    showToastMsg("请输入工号/名称或者密码");
                    return;
                }
                viewModel.login(username,userpwd);
            }
        });

        // 定位信息相关信息
        setLocationInfo(YTApplication.getInstance());

        // 初始化广播
        bleListenerReceiver = new BluetoothMonitorReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // 监视蓝牙关闭和打开的状态
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 监视蓝牙设备与APP连接的状态
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        // 注册广播
        registerReceiver(this.bleListenerReceiver, intentFilter);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        udpBuild = UDPBuild.getUdpBuild();
        udpBuild.setUdpReceiveCallback(new UDPBuild.OnUDPReceiveCallbackBlock() {
            @Override
            public void OnParserComplete(DatagramPacket data) {
                String strReceive = new String(data.getData(), 0, data.getLength());
                mUDPStatusStr = "已经发送到服务端信息：" +strReceive;
                mHandler.sendEmptyMessage(HANDLER_SEND_SERVER_UDP_STATUS);
            }
        });
    }

    private void loginSuccess(){
        // 登录
        if (dataBinding.cbSaveInfo.isChecked()) {
            SPManager.getInstance().setUserName(dataBinding.etUsername.getText().toString().trim());
            SPManager.getInstance().setUserPwd(dataBinding.etPwd.getText().toString().trim());
            SPManager.getInstance().setSaveStatusLogin(true);
        } else {
            SPManager.getInstance().setUserName("");
            SPManager.getInstance().setUserPwd("");
            SPManager.getInstance().setSaveStatusLogin(false);
        }
        IntentManager.getInstance().goMainActivity(mContext);
        mIsGoMainActivity = true;
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getContentResolver()
                .registerContentObserver(
                        Settings.Secure
                                .getUriFor(Settings.System.LOCATION_PROVIDERS_ALLOWED),
                        false, mGpsMonitor);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mIsGoMainActivity) {
            // 保持前台进程 离开界面就开始服务
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, KeepAppLifeService.class));
            } else {
                startService(new Intent(this, KeepAppLifeService.class));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        BleNFCManager.getInstance().destroyBlueToothPlugin();
        unregisterReceiver(this.bleListenerReceiver);

        getContentResolver().unregisterContentObserver(mGpsMonitor);
    }
}
