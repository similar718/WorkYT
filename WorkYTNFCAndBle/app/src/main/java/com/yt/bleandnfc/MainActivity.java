package com.yt.bleandnfc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.yt.base.utils.LogUtlis;
import com.yt.bleandnfc.api.model.AlarmCountAlarmByStateModel;
import com.yt.bleandnfc.base.YTApplication;
import com.yt.bleandnfc.base.activity.YTBaseActivity;
import com.yt.bleandnfc.constant.Constants;
import com.yt.bleandnfc.databinding.ActivityMainBinding;
import com.yt.bleandnfc.eventbus.AlarmAddResult;
import com.yt.bleandnfc.manager.IntentManager;
import com.yt.bleandnfc.mvvm.viewmodel.MainViewModel;
import com.yt.bleandnfc.nfcres.NfcHandler;
import com.yt.bleandnfc.nfcres.NfcView;
import com.yt.bleandnfc.service.KeepAppLifeService;
import com.yt.bleandnfc.ui.dialog.BLEAndGPSHintDialog;
import com.yt.bleandnfc.utils.BLEAndGPSUtils;
import com.yt.common.interfaces.IPermissionListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends YTBaseActivity<MainViewModel, ActivityMainBinding> {

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected MainViewModel createViewModel() {
        viewModel = new MainViewModel((Activity) mContext);
        viewModel.setIView(this);
        viewModel.mAlarmCountAlarmByStateModel.observe(this, new Observer<AlarmCountAlarmByStateModel>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChanged(AlarmCountAlarmByStateModel alarmFindAlarmByStateModel) {
                LogUtlis.e("ooooooooooooooo",alarmFindAlarmByStateModel.getObj() + "  " + Constants.mAlarmNum);
                if (alarmFindAlarmByStateModel.getObj() > Constants.mAlarmNum){
                    Constants.mAlarmNum = alarmFindAlarmByStateModel.getObj();
                    EventBus.getDefault().post(new AlarmAddResult(1));
                    showBleAndGPSHintDialog("有新报警信息",false);

                    viewModel.getNotification();
                }
            }
        });
        return viewModel;
    }

    private NavController mNavController;

    // APP主要权限 存储权限和位置权限 位置权限主要是在搜索心贴的时候有用到
    public static final String[] PERMISSIONPERMS_STORAGE_LOCATION = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA};

    @Override
    protected void initData() {
        dataBinding.navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_info_detail,
                R.id.navigation_warning_record,
                R.id.navigation_check_location,
                R.id.navigation_wb)
                .build();
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, mNavController, appBarConfiguration);
        NavigationUI.setupWithNavController(dataBinding.navView, mNavController);

        // 底部文字大小的问题处理
        dataBinding.navView.setItemTextAppearanceActive(R.style.bottom_selected_text);
        dataBinding.navView.setItemTextAppearanceInactive(R.style.bottom_normal_text);

        // 底部点击事件的处理 保存状态信息的处理
        //bottomNavigationView Item 选择监听
        dataBinding.navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean popBackStack = mNavController.popBackStack(item.getItemId(),false);
                if (popBackStack) {
                    return popBackStack;
                } else {
                    if (item.isChecked()) {
                        return false;
                    } else {
                        return NavigationUI.onNavDestinationSelected(item, mNavController);
                    }
                }
            }
        });
        // 定位信息相关信息
        setLocationInfo(YTApplication.getInstance());
        // 开始申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestRunTimePermission(PERMISSIONPERMS_STORAGE_LOCATION,iPermissionListener);
        }
        startTimer();

        // 初始化NFC数据
        mNfcHandler = new NfcHandler(mNFCView);
        mNfcHandler.init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 开始使用NFC
        mNfcHandler.enableNfc(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 保持前台进程 离开界面就开始服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, KeepAppLifeService.class));
        } else {
            startService(new Intent(this,KeepAppLifeService.class));
        }
    }

    private LocationManager locationManager = null;
    String mProviderName = "";

    private void setLocationInfo(Application activity) {
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) activity.getSystemService(serviceName);
        // 查找到服务信息
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置是否需要方位信息connNotDesDevice
        criteria.setBearingRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(true);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗

        // 为获取地理位置信息时设置查询条件
        String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息

        Location lastKnownLocation = null;
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mProviderName = LocationManager.GPS_PROVIDER;
        if (lastKnownLocation == null) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            mProviderName = LocationManager.NETWORK_PROVIDER;
        }
        if (mProviderName != null && !"".equals(mProviderName)) {
            locationManager.requestLocationUpdates(mProviderName, 1000, 1, locationListener);
        }
    }

    private LocationListener locationListener = new LocationListener() {
        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            Constants.LOCATION_LAT = location.getLatitude();
            Constants.LOCATION_LNG = location.getLongitude();

            LogUtlis.e("ooooooooo","lat = " + Constants.LOCATION_LAT + " lng = " + Constants.LOCATION_LNG);
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    // 当前GPS状态为可见状态
                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    // 当前GPS状态为服务区外状态
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    // 当前GPS状态为暂停服务状态
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            if (
                    ActivityCompat.checkSelfPermission(mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(mContext,
                                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            Constants.LOCATION_LAT = location.getLatitude();
            Constants.LOCATION_LNG = location.getLongitude();
            LogUtlis.e("ooooooooo","lat = " + Constants.LOCATION_LAT + " lng = " + Constants.LOCATION_LNG);
        }
        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
        stopTimer();
    }

    private IPermissionListener iPermissionListener = new IPermissionListener() {
        @Override
        public void onGranted() {
            if (!BLEAndGPSUtils.isOpenBLE()) {
                // 蓝牙没有打开
                showBleAndGPSHintDialog("请打开蓝牙，可正常使用APP内的功能",false);
            } else if (!BLEAndGPSUtils.isOpenGPS(YTApplication.getInstance())){
                showBleAndGPSHintDialog("请打开GPS，可正常使用APP内的功能",false);
            }
        }

        @Override
        public void onGranted(List<String> grantedPermission) {

        }

        @Override
        public void onDenied(List<String> deniedPermission) {
            // 拒绝部分权限
            showBleAndGPSHintDialog("拒绝权限APP将不能正常使用，是否前往开启权限",true);
        }

        @Override
        public void onDenied() {
            // 拒绝全部权限
            showBleAndGPSHintDialog("拒绝权限APP将不能正常使用，是否前往开启权限",true);
        }
    };

    private BLEAndGPSHintDialog mBLEAndGPSHintDialog;

    private void showBleAndGPSHintDialog(String title,boolean isPermissionHint){
        if (!mIsActive){
            return;
        }
        if (mBLEAndGPSHintDialog == null) {
            mBLEAndGPSHintDialog = new BLEAndGPSHintDialog(mContext);
            mBLEAndGPSHintDialog.setBLEAndGPSHintClicklistener(new BLEAndGPSHintDialog.BLEAndGPSHintClickListenerInterface() {
                @Override
                public void doSure() {
                    if (isPermissionHint) {
                        IntentManager.getInstance().goToAppSetting(mContext);
                    } else {

                    }
                }
            });
        }
        mBLEAndGPSHintDialog.showDialog(title, isPermissionHint);
    }

    private AlarmTimerTask mTimerTask;
    private Timer mTimer;

    private void startTimer(){
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new AlarmTimerTask();
        }
        mTimer.schedule(mTimerTask, 30*1000, 30*1000); // 目前间隔4秒获取一次
    }

    private void stopTimer(){
        if (mTimerTask != null && mTimer != null){
            mTimerTask.cancel();
            mTimerTask = null;
            mTimer = null;
        }
    }

    /**
     * 获取设备电池电量定时器
     */
    class AlarmTimerTask extends TimerTask {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void run() {
            viewModel.alarmList();
        }
    }


    private String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onNewIntent(Intent intent) { // TODO nfc必须要使用的
        Log.d(TAG, "onNewIntent()! action is:" + intent.getAction());
        super.onNewIntent(intent);
        setIntent(intent);
    }

    // NFC 硬件相关东西
    private NfcHandler mNfcHandler;
    private boolean mIsRequestNFCUid = false;
    private boolean mIsOpenNFC = true;

    private String mNFCContent = "";


    private NfcView mNFCView = new NfcView() {
        @Override
        public void appendResponse(final String response) {
            Log.e(TAG, "appendResponse: data______________________________" + response);
            // NFC相关信息的回调事件
            if (TextUtils.isEmpty(response)){
                return;
            }
            mIsRequestNFCUid = true; // 从线程中读取到NFC的相关数据
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(1000); // 获取成功只有震动1秒的钟
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    ToastUtils.showText(mContext,response);
                    StringBuilder data = new StringBuilder();
                    data.append("NFC相关信息：").append(response);
                    mNFCContent = response;
                    if (mIsActive) {
                        // TODO 去请求服务器
                        // dataBinding.etScanInput.setText(mNFCContent);
                        showBleAndGPSHintDialog("获取到NFC数据信息：\n " + mNFCContent,false);
                    }
                }
            });
        }

        @Override
        public void notNfcDevice() {
            if (Constants.mIsFirstHint) {
                showToastMsg("当前设备不支持NFC");
                Constants.mIsFirstHint = false;
            }
        }

        @Override
        public void notOpenNFC() {
            mIsOpenNFC = false;
            mHandler.sendEmptyMessage(HANDLER_INIT_IMAGEVIEW_NFC);
            if (Constants.mIsFirstHint) {
                showToastMsg("请在设置中打开NFC开关！");
                Constants.mIsFirstHint = false;
            }
        }

        @Override
        public void getNFCStatusOk() {
            mIsOpenNFC = true;
            mHandler.sendEmptyMessage(HANDLER_INIT_IMAGEVIEW_NFC);
        }
    };

    // 开始查看NFC是否被读取
    private void getNFCInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!mIsRequestNFCUid) {
                    try {
                        Thread.sleep(1000);
                        // 循环读取数据
                        mNfcHandler.readCardId(getIntent());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private final int HANDLER_INIT_IMAGEVIEW_NFC = 0x0102;

    // 主线程的Handler用来刷新界面
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void handleMessage(@NonNull Message msg) {
            // 由主线程中的Looper不断的loop将handler里面的信息不断的轮询，将符合要求的数据dispatchMessage分发
            // 到主线程的handlerMessage进行更新界面的数据
            switch (msg.what){
                case HANDLER_INIT_IMAGEVIEW_NFC:
                    if (mIsOpenNFC) { // 如果出现就开始获取nfc数据
                        getNFCInfo();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mNfcHandler.disableNfc(this);
    }
}
