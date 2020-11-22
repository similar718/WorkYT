package com.yt.bleandnfc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
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
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;

import com.clj.fastble.BleNFCManager;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.nfc.BleNFCListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.yt.base.utils.LogUtlis;
import com.yt.bleandnfc.api.model.AlarmCountAlarmByStateModel;
import com.yt.bleandnfc.base.YTApplication;
import com.yt.bleandnfc.base.activity.YTBaseActivity;
import com.yt.bleandnfc.bean.NotifyBLEDataConstructerBean;
import com.yt.bleandnfc.constant.Constants;
import com.yt.bleandnfc.databinding.ActivityMainBinding;
import com.yt.bleandnfc.eventbus.AlarmAddResult;
import com.yt.bleandnfc.listener.SocketListener;
import com.yt.bleandnfc.manager.IntentManager;
import com.yt.bleandnfc.mvvm.viewmodel.MainViewModel;
import com.yt.bleandnfc.nfcres.NfcHandler;
import com.yt.bleandnfc.nfcres.NfcView;
import com.yt.bleandnfc.service.KeepAppLifeService;
import com.yt.bleandnfc.udp.UDPThread;
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

    private UDPThread udpThread;
    private BluetoothMonitorReceiver bleListenerReceiver = null;

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
        initBlueTooth();
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

        // 初始化广播
        this.bleListenerReceiver = new BluetoothMonitorReceiver();
        IntentFilter intentFilter = new IntentFilter();
        // 监视蓝牙关闭和打开的状态
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 监视蓝牙设备与APP连接的状态
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        // 注册广播
        registerReceiver(this.bleListenerReceiver, intentFilter);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
        BleNFCManager.getInstance().destroyBlueToothPlugin();
        stopTimer();
        unregisterReceiver(this.bleListenerReceiver);

        getContentResolver().unregisterContentObserver(mGpsMonitor);
    }

    private IPermissionListener iPermissionListener = new IPermissionListener() {
        @Override
        public void onGranted() {
            if (!BLEAndGPSUtils.isOpenBLE()) {
                // 蓝牙没有打开
                showBleAndGPSHintDialog("请打开蓝牙，可正常使用APP内的功能",false);
            } else if (!BLEAndGPSUtils.isOpenGPS(YTApplication.getInstance())){
                showBleAndGPSHintDialog("请打开GPS，可正常使用APP内的功能",false);
            } else {
                startBleTimer();
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
        LogUtlis.d(TAG, "onNewIntent()! action is:" + intent.getAction());
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
            LogUtlis.e(TAG, "appendResponse: data______________________________" + response);
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
    private final int BLE_CLOSE_OK = 0x0109;
    private final int BLE_OPEN_OK = 0x0108;

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
                case HANDLER_SEND_SERVER_UDP_STATUS: // 发送UDP数据的状态
//                    dataBinding.tvServerStatus.setText(mUDPStatusStr);
                    showToastMsg(mUDPStatusStr);
                    break;

                case BLE_CLOSE_OK:
                    break;

                case BLE_OPEN_OK:
                    if (BLEAndGPSUtils.isOpenBLE() && BLEAndGPSUtils.isOpenGPS(YTApplication.getInstance()) && !mInitSuccess) {
                        mClickInit = true;
                        BleNFCManager.getInstance().initBleNFC(YTApplication.getInstance(),(Activity) mContext,mListener);
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

    // ************************************************************************ 蓝牙相关
    private final int HANDLER_INIT_IMAGEVIEW = 0x0101;
    private final int HANDLER_SEND_SERVER_UDP_STATUS = 0x0104;

    private boolean mInitSuccess = false; // 是否默认蓝牙插件初始化成功
    private boolean mClickInit = false; // 是否默认点击扫描初始化

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initBlueTooth() {
        BleNFCManager.getInstance().initBleNFC(getApplication(),MainActivity.this,mListener);
    }


    private void startThread() {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if (BLEAndGPSUtils.isOpenBLE()) {
                    BleNFCManager.getInstance().getBleNFCInfo();
                }
            }
        }).start();
    }

    private BleNFCListener mListener = new BleNFCListener() {
        @Override
        public void initFailed(byte data) {// TODO  初始化失败 需要配合相关操作之后再重新初始化
            if (data == (byte) 0x0001){ //没有打开GPS的情况
//                Toast.makeText(mContext,"请打开GPS位置信息",Toast.LENGTH_LONG).show();
                LogUtlis.e(TAG,"initFailed 请打开GPS位置信息");
            } else if (data == (byte) 0x0010) { // 判断是否打开蓝牙设备
//                Toast.makeText(mContext,"请打开蓝牙",Toast.LENGTH_LONG).show();
                LogUtlis.e(TAG,"initFailed 请打开蓝牙");
            } else {
//                Toast.makeText(mContext,"初始化失败，其他情况",Toast.LENGTH_LONG).show();
                LogUtlis.e(TAG,"initFailed 初始化失败，其他情况");
            }
            mInitSuccess = false;
            mHandler.sendEmptyMessage(HANDLER_INIT_IMAGEVIEW);
        }

        @Override
        public void initSuccess() {
            // 初始化成功 可以正常的扫描设备
            mInitSuccess = true;
            if (mClickInit){
                mClickInit = false;
                initBleAndStartScan();
            }
        }

        @Override
        public void scanDevice() {// 扫描到目标设备
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    dataBinding.tvReplyDev.setText("");
//                    mIsParseSuccess = false;
//                    Toast.makeText(mContext, "搜索到目标设备", Toast.LENGTH_LONG).show();
                    LogUtlis.e(TAG, "搜索到目标设备");
//                    dataBinding.tvStatus.setText("当前状态：搜索到目标设备正在连接中");
                    isStop = false;
//                    String mLocation = "蓝牙插件定位信息\n经度："+ Constants.LOCATION_LAT +"\n纬度："+ Constants.LOCATION_LNG;
//                    dataBinding.tvLocation.setText(mLocation);
                }
            });

        }

        @Override
        public void scanNotDevice() { // 未扫描到目标设备
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    dataBinding.tvReplyDev.setText("");
//                    mIsParseSuccess = false;
//                    Toast.makeText(mContext, "未搜索到目标设备", Toast.LENGTH_LONG).show();
                    LogUtlis.e(TAG, "未搜索到目标设备");
//                    dataBinding.tvStatus.setText("当前状态：未搜索到目标设备 请打开设备之后重试");

                    // TODO 间隔扫描时间的问题 需要进行处理
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    isStop = true;
//                    String mLocation = "蓝牙插件定位信息\n经度："+ Constants.LOCATION_LAT +"\n纬度："+ Constants.LOCATION_LNG;
//                    dataBinding.tvLocation.setText(mLocation);
                }
            });

        }

        @Override
        public void startConnDevice(BleDevice bleDevice) { // 开始连接
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LogUtlis.e(TAG, "开始连接");
//                    dataBinding.tvStatus.setText("当前状态：开始连接");
                    isStop = false;
//                    String mLocation = "蓝牙插件定位信息\n经度："+ Constants.LOCATION_LAT +"\n纬度："+ Constants.LOCATION_LNG;
//                    dataBinding.tvLocation.setText(mLocation);
                }
            });
        }

        @Override
        public void connSuccesDevice(BleDevice bleDevice) { // 连接成功
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(mContext, "连接成功", Toast.LENGTH_LONG).show();
                    LogUtlis.e(TAG, "连接成功");
//                    dataBinding.tvStatus.setText("当前状态：连接成功 正准备获取数据");
                    isStop = false;
//                    String mLocation = "蓝牙插件定位信息\n经度："+ Constants.LOCATION_LAT +"\n纬度："+ Constants.LOCATION_LNG;
//                    dataBinding.tvLocation.setText(mLocation);
                }
            });
        }

        @Override
        public void getNotifyConnDeviceSuccess(BleDevice bleDevice, String scanDeviceData) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    dataBinding.tvStatus.setText("当前状态：获取到蓝牙连接之后的打开通知成功");
//                    dataBinding.tvCheckData.setText(scanDeviceData);
                    isStop = false;
//                    String mLocation = "蓝牙插件定位信息\n经度："+ Constants.LOCATION_LAT +"\n纬度："+ Constants.LOCATION_LNG;
//                    dataBinding.tvLocation.setText(mLocation);
                }
            });
        }

        @Override
        public void getNotifyConnDeviceFail(BleDevice bleDevice, String scanDeviceData) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    dataBinding.tvStatus.setText("当前状态：获取到蓝牙连接之后的打开通知失败~~~~~~~");
//                    dataBinding.tvCheckData.setText(scanDeviceData);
                    isStop = false;
//                    String mLocation = "蓝牙插件定位信息\n经度："+ Constants.LOCATION_LAT +"\n纬度："+ Constants.LOCATION_LNG;
//                    dataBinding.tvLocation.setText(mLocation);
                }
            });
        }

        @Override
        public void getNotifyConnDeviceData(BleDevice bleDevice, String scanDeviceData) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    dataBinding.tvStatus.setText("当前状态：获取到蓝牙连接之后的通知成功获取数据信息");
//                    dataText.append(scanDeviceData + "\n");
//                    dataBinding.tvCheckData.setText(dataText.toString());
//                    dataBinding.tvServer.setText(scanDeviceData);
                    isStop = false;
//                    String mLocation = "蓝牙插件定位信息\n经度："+ Constants.LOCATION_LAT +"\n纬度："+ Constants.LOCATION_LNG;
//                    dataBinding.tvLocation.setText(mLocation);
                    if (scanDeviceData.startsWith("FF") || scanDeviceData.startsWith("ff")){
                        dataText1 = new StringBuilder();
                        dataText1.append(scanDeviceData);
                    } else if (scanDeviceData.endsWith("9c") || scanDeviceData.endsWith("9C")){
                        dataText1.append(scanDeviceData);
                    }
                    LogUtlis.e("ooooooooooo","data = " + dataText1.toString());
                    if(dataText1.toString().length() > 40/* && !mIsParseSuccess*/) {
                        // TODO 开启线程解析数据
                        parseData(bleDevice,dataText1.toString());
                        dataText1 = new StringBuilder();
                    }
                }
            });
        }

        private StringBuilder dataText = new StringBuilder();

        private StringBuilder dataText1 = new StringBuilder();

        @Override
        public void connFailedDevice(BleDevice bleDevice) { // 连接失败
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(mContext, "连接失败", Toast.LENGTH_LONG).show();
                    LogUtlis.e(TAG, "连接失败");
//                    dataBinding.tvStatus.setText("当前状态：连接失败");
                    isStop = true;
//                    String mLocation = "蓝牙插件定位信息\n经度："+ Constants.LOCATION_LAT +"\n纬度："+ Constants.LOCATION_LNG;
//                    dataBinding.tvLocation.setText(mLocation);
                }
            });
        }

        @Override
        public void disConnDevice(BleDevice bleDevice) { // 断开连接
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mIsParseSuccess = false;
//                    Toast.makeText(mContext, "断开连接", Toast.LENGTH_LONG).show();
                    LogUtlis.e(TAG, "断开连接");
//                    dataBinding.tvStatus.setText("当前状态：设备 断开连接");
                    isStop = true;
//                    String mLocation = "蓝牙插件定位信息\n经度："+ Constants.LOCATION_LAT +"\n纬度："+ Constants.LOCATION_LNG;
//                    dataBinding.tvLocation.setText(mLocation);
                }
            });
        }

        @Override
        public void replyDataToDeviceSuccess(BleDevice bleDevice, String data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    dataBinding.tvStatus.setText("当前状态：回复设备（" + data +" ）成功");
                    isStop = true;
//                    String mLocation = "蓝牙插件定位信息\n经度："+ Constants.LOCATION_LAT +"\n纬度："+ Constants.LOCATION_LNG;
//                    dataBinding.tvLocation.setText(mLocation);
//                    dataBinding.tvReplyDev.setText(data + "------回复成功");
//                    dataText = new StringBuilder();
//                    dataText.append("");
//                    dataBinding.tvCheckData.setText(dataText.toString());
                }
            });
        }

        @Override
        public void replyDataToDeviceFailed(BleDevice bleDevice, String data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    mIsParseSuccess = false;
//                    dataBinding.tvStatus.setText("当前状态：回复设备（" + data +" ）失败----呜呜呜");
                    isStop = false;
//                    String mLocation = "蓝牙插件定位信息\n经度："+ Constants.LOCATION_LAT +"\n纬度："+ Constants.LOCATION_LNG;
//                    dataBinding.tvLocation.setText(mLocation);
//                    dataBinding.tvReplyDev.setText(data + "------回复失败");
                }
            });
        }
    };

    byte[] reply_data = new byte[]{(byte)0x8E,(byte)0x9C};
    byte reply_data1 = (byte) 0x32;

    private boolean mIsParse = false;
    private void parseData(BleDevice device,final String datas){
        if (!mIsParse) {
            mIsParse = true;
            String data = datas.toUpperCase();
            String content = ""; // 装所有数据的字符串
            // 判断当前数据有头有尾
            if (data.contains("FF") && data.contains("9C")) {
                // 开始截取头部之后的数据 判断是否是以FF 或者 ff 开始
                if (data.startsWith("FF")) {
                    content = data;
                } else {
                    // 需要进行截取
                    String[] splitFF = data.split("FF");
                    if (splitFF.length > 1) {
                        content = "FF" + splitFF[1];
                    } else {
                        content = "";
                    }
                }
                // 开始截取尾部之前的位置 判断是否是以9C 或者 9c 结尾
                if (content.endsWith("9C")) {
                } else {
                    // 需要进行截取
                    String[] split9C = content.split("9C");
                    if (split9C.length > 0) {
                        content = split9C[0] + "9C";
                    } else {
                        content = "";
                    }
                }
            }
            // 上面的数据表示 58 表示数据是全的
            if (content.startsWith("FF") && content.endsWith("9C") && content.length() == 60) {
                NotifyBLEDataConstructerBean bean = new NotifyBLEDataConstructerBean();
                bean.setBaotou(content.substring(0, 2));
                bean.setKehudaima(content.substring(2, 4));
                bean.setShujubaoType(content.substring(4, 6));
                bean.setIpAndPort(content.substring(6, 18));
                bean.setDevId(content.substring(18, 22));
                bean.setPower(content.substring(22, 24));
                bean.setLatlng(content.substring(24, 40));
                bean.setLatlngType(content.substring(40, 42));
                bean.setWeixingnum(content.substring(42, 44));
                bean.setMac(content.substring(44, 56));
                bean.setVersion(content.substring(56, 58));
                bean.setBaowei(content.substring(58, 60));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToastMsg("数据解析成功：" + bean.toString() + "准备发送关闭命令");
                    }
                });
                if (Byte.parseByte(bean.version,16) == reply_data1) {
                    BleNFCManager.getInstance().sendWriteData(device,reply_data);
                } else {
                    String bledata = "8FEBAC606EBC0EDA6F6C04007717E2ED8023329C";
                    BleNFCManager.getInstance().sendWriteData(device,hexStrToByteArray(bledata));
                }
                // 上报服务器的数据信息
                String result = content.substring(0,4) + "1" + content.substring(5,60);
                String ipandport = bean.getIpAndPort();
                String ip = bean.getIpAddress();
                int port = bean.getIPPort();
                if (udpThread == null) {
                    udpThread = new UDPThread(ip, port);
                    udpThread.setSocketListener(mSockestListener);
                    udpThread.start();
                }
                upService(result);
            } else {
                String showContent = content;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToastMsg("数据不全 解析之后的数据：" + showContent + "\n 解析之前的数据：" + datas);
                    }
                });
            }
            mIsParse = false;
        }
    }

    public static byte[] hexStrToByteArray(String data) {
        if (TextUtils.isEmpty(data)) {
            return  null;
        }
        byte[] bytes = new byte[data.length() / 2];
        for (int i = 0; i < bytes.length; i++){
            String subStr = data.substring(2*i,2*i+2);
            bytes[i] = (byte) Integer.parseInt(subStr,16);
        }
        return bytes;
    }
    /**
     * 上报服务器
     * @param data
     */
    private void upService(String data) {  // TODO 119.23.226.237：9088 使用UDP发送数据信息
        if (TextUtils.isEmpty(data)){
            return;
        }
        if (udpThread == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                udpThread.sendSocketData(data);
            }
        }).start();
    }

    private String mUDPStatusStr = "";
    /**
     * UDP服务器的监听
     */
    private SocketListener mSockestListener = new SocketListener() {
        @Override
        public void receiveSocketData(String socketData) {
            // TODO 接收到服务端的数据
            mUDPStatusStr = "接收到服务端信息：" +socketData;
            mHandler.sendEmptyMessage(HANDLER_SEND_SERVER_UDP_STATUS);
        }

        @Override
        public void sendSocketData(String packs) {
            // TODO 已发送数据
            mUDPStatusStr = "已经发送到服务端信息：" +packs;
            mHandler.sendEmptyMessage(HANDLER_SEND_SERVER_UDP_STATUS);
        }

        @Override
        public void error(Throwable e) {
            // TODO 收发数据出现异常
            mUDPStatusStr = "接收出现异常：" +e.toString();
            mHandler.sendEmptyMessage(HANDLER_SEND_SERVER_UDP_STATUS);
        }
    };

    private void initBleAndStartScan(){
//        dataBinding.tvStatus.setText("当前状态：正在搜索设备");
        isStop = false;
        startThread();
        startBleTimer();
//        String mLocation = "蓝牙插件定位信息\n经度：" + Constants.LOCATION_LAT + "\n纬度：" + Constants.LOCATION_LNG;
//        dataBinding.tvLocation.setText(mLocation);
    }

    private Timer mBleTimer = null;
    private TimerTask mBleTimerTask = null;
    private static int count = 0;
    private boolean isPause = false;
    private boolean isStop = true;
    private static int delay = 1000;  //1s
    private static int period = 1000;  //1s

    private void startBleTimer() {
        if (mBleTimer != null && mBleTimerTask != null) {
            stopBleTimer();
        }
        if (mBleTimer == null) {
            mBleTimer = new Timer();
        }
        if (mBleTimerTask == null) {
            mBleTimerTask = new TimerTask() {
                @Override
                public void run() {
                    LogUtlis.i(TAG, "count: " + String.valueOf(count));
                    do {
                        try {
                            LogUtlis.i(TAG, "sleep(1000)...");
                            Thread.sleep(1000);
                            if (isStop) {
                                isStop = false;
                                startThread();
                            }
                        } catch (InterruptedException e) {
                        }
                    } while (isPause);
                    count++;
                }
            };
        }
        if (mBleTimer != null && mBleTimerTask != null) {
            mBleTimer.schedule(mBleTimerTask, delay, period);
        }
    }


    private void stopBleTimer() {
        if (mBleTimer != null) {
            mBleTimer.cancel();
            mBleTimer = null;
        }
        if (mBleTimerTask != null) {
            mBleTimerTask.cancel();
            mBleTimerTask = null;
        }
        count = 0;
    }
    // ************************************************************************ 蓝牙相关


    // ************************************************************************ 蓝牙监听相关
    public class BluetoothMonitorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null){
                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                        switch (blueState) {
                            case BluetoothAdapter.STATE_TURNING_ON:
//                                showToastMsg("蓝牙正在打开");
                                break;
                            case BluetoothAdapter.STATE_ON:
                                showToastMsg("蓝牙已经打开");
                                mHandler.sendEmptyMessage(BLE_OPEN_OK);
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
//                                showToastMsg("蓝牙正在关闭");
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                showToastMsg("蓝牙已经关闭");
                                mHandler.sendEmptyMessage(BLE_CLOSE_OK);
                                break;
                        }
                        break;

                    case BluetoothDevice.ACTION_ACL_CONNECTED:
//                        showToastMsg("蓝牙设备已连接");
                        break;

                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
//                        showToastMsg("蓝牙设备已断开");
                        break;
                }

            }
        }
    }
    // ************************************************************************ 蓝牙监听相关


    // ************************************************************************ GPS监听相关
    private final ContentObserver mGpsMonitor = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            boolean enabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            System.out.println("gps enabled? " + enabled);
        }
    };

    private LocationManager mLocationManager;
    // ************************************************************************ GPS监听相关


}
