package com.clj.fastble;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.clj.fastble.manager.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.config.BLEConstants;
import com.clj.fastble.nfc.BleNFCListener;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.clj.fastble.utils.HexUtil;

import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class BleNFCManager {
    // 单例模式
    private BleNFCManager() {

    }

    public static synchronized BleNFCManager getInstance() {
        return SingletonHolder.instance;
    }

    private static final class SingletonHolder { // 静态内部类进行初始化当前单例
        private static BleNFCManager instance = new BleNFCManager(); // 类加载机制会在对象初始化的时候加锁  使不会进行重排序的情况
    }

    private Context mContext = null;

    public void initBleNFC(Application context, Activity activity, BleNFCListener listener) {
        // 蓝牙插件的监听事件
        mBlueToothListener = listener;
        // 上下文事件
        mContext = context;
        // 判断GPS权限问题
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mBlueToothListener.initFailed((byte) 0x0001); // 位置权限未打开
            return;
        }
        // 蓝牙是否打开
        if (!BleIsOpen()) { // 蓝牙未打开
            mBlueToothListener.initFailed((byte) 0x0010);
            return;
        }
        // 初始化控件
        BleManager.getInstance().init(context);
        // 设置扫描设备配置
        BleManager.getInstance()
                .enableLog(true) // 是否需要log
                .setReConnectCount(1, 5000) //重连次数一次 每隔5秒重连一次
                .setConnectOverTime(20000) // 连接超时的时间设置
                .setOperateTimeout(5000); // 操作超时的时间设置
        // 插件初始化成功
        mBlueToothListener.initSuccess();
    }

    /**
     * 判断当前蓝牙硬件是否已经打开
     * @return
     */
    public boolean BleIsOpen(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    // 插件监听的实例化
    private static BleNFCListener mBlueToothListener;

    public void getBleNFCInfo(){
        setScanRule();
        startScan();
    }

    /**
     * 设置扫描规则
     */
    private void setScanRule() {
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setAutoConnect(false)      // 连接时的autoConnect参数，可选，默认false
                .setScanTimeOut(10_000)      // 扫描超时时间，可选，默认10秒
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    /**
     * TODO 是否扫描到可用设备
     */
    private boolean mIsScanDes = false;

    /**
     * 开始扫描
     */
    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onScanning(BleDevice bleDevice) {
                if (bleDevice.getName() != null && bleDevice.getName().contains(BLEConstants.mBleName)){  // TODO 判断是否是我们需要的设备名称的设备
                    if (!BleManager.getInstance().isConnected(bleDevice)) { // 判断设备名称是正常的设备是否已经被连接
                        mIsScanDes = true;
                        mBlueToothListener.scanDevice(); // 已经扫描到一个可用设备
                        BleManager.getInstance().cancelScan(); // 已经找到可以连接的设备 停止扫描设备
                        connect(bleDevice); // 连接当前设备
                    }
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                if (!mIsScanDes){
                    mBlueToothListener.scanNotDevice(); // 提示监听 没有找到设备
                } else {
                    mIsScanDes = false;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                mBlueToothListener.startConnDevice((BleDevice) bleDevice); // 开始连接设备
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                mBlueToothListener.connFailedDevice((BleDevice) bleDevice); // 设备连接失败
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onConnectSuccess(final BleDevice bleDevice, final BluetoothGatt gatt, int status) {
                mBlueToothListener.connSuccesDevice((BleDevice) bleDevice); // 成功连接设备  准备验证数据
                // 需要打开notify 准备接收数据
                /*
                 uuid_service = 00001800-0000-1000-8000-00805f9b34fb
                 uuid_chara = 00002a00-0000-1000-8000-00805f9b34fb
                 uuid_chara = 00002a01-0000-1000-8000-00805f9b34fb
                 uuid_chara = 00002a04-0000-1000-8000-00805f9b34fb
                 uuid_chara = 00002ac9-0000-1000-8000-00805f9b34fb

                 uuid_service = 00001801-0000-1000-8000-00805f9b34fb
                 uuid_chara = 00002a05-0000-1000-8000-00805f9b34fb

                 uuid_service = 0000180a-0000-1000-8000-00805f9b34fb
                 uuid_chara = 00002a23-0000-1000-8000-00805f9b34fb
                 uuid_chara = 00002a25-0000-1000-8000-00805f9b34fb
                 uuid_chara = 00002a26-0000-1000-8000-00805f9b34fb
                 uuid_chara = 00002a27-0000-1000-8000-00805f9b34fb
                 uuid_chara = 00002a29-0000-1000-8000-00805f9b34fb

                 uuid_service = 0000ffe0-0000-1000-8000-00805f9b34fb  支持notify和indicate
                 uuid_chara = 0000ffe4-0000-1000-8000-00805f9b34fb

                 uuid_service = 0000ffe5-0000-1000-8000-00805f9b34fb 可写入
                 uuid_chara = 0000ffe9-0000-1000-8000-00805f9b34fb

                 uuid_service = 0000ff90-0000-1000-8000-00805f9b34fb 多操作
                 uuid_chara = 0000ff91-0000-1000-8000-00805f9b34fb
                 uuid_chara = 0000ff92-0000-1000-8000-00805f9b34fb
                 uuid_chara = 0000ff93-0000-1000-8000-00805f9b34fb
                 uuid_chara = 0000ff94-0000-1000-8000-00805f9b34fb
                 uuid_chara = 0000ff95-0000-1000-8000-00805f9b34fb
                 uuid_chara = 0000ff96-0000-1000-8000-00805f9b34fb
                 uuid_chara = 0000ff97-0000-1000-8000-00805f9b34fb
                 uuid_chara = 0000ff98-0000-1000-8000-00805f9b34fb
                 uuid_chara = 0000ff99-0000-1000-8000-00805f9b34fb

                 uuid_service = 0000ffc0-0000-1000-8000-00805f9b34fb  应该是设置密码和名称
                 uuid_chara = 0000ffc1-0000-1000-8000-00805f9b34fb
                 uuid_chara = 0000ffc2-0000-1000-8000-00805f9b34fb
                 */
//                List<BluetoothGattService> serviceList = gatt.getServices();
//                for (BluetoothGattService service : serviceList) {
//                    UUID uuid_service = service.getUuid();
////                    Log.e("oooooooooooooo","uuid_service = " + uuid_service);
//                    List<BluetoothGattCharacteristic> characteristicList= service.getCharacteristics();
//                    for(BluetoothGattCharacteristic characteristic : characteristicList) {
//                        UUID uuid_chara = characteristic.getUuid();
//                        int descriptor = characteristic.getProperties();
//
//                        if (descriptor == BluetoothGattCharacteristic.PROPERTY_NOTIFY && notify_uuid_service == null && notify_uuid_chara == null) { // 表示支持notify
//                            notify_uuid_service = uuid_service;
//                            notify_uuid_chara = uuid_chara;
//                        }
//                        if (descriptor == BluetoothGattCharacteristic.PROPERTY_WRITE && write_uuid_chara == null && write_uuid_service == null) { // 表示支持write
//                            write_uuid_service = uuid_service;
//                            write_uuid_chara = uuid_chara;
//                        }
//                        if (descriptor == BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE && write_uuid_chara_no == null && write_uuid_service_no == null) { // 表示支持write no response
//                            write_uuid_service_no = uuid_service;
//                            write_uuid_chara_no = uuid_chara;
//                        }
////                        if (descriptor != 0) {
////                            Log.e("oooooooooooooo", "uuid_chara = " + uuid_chara + "\n" + descriptor);
////                        } else {
////                            Log.e("oooooooooooooo", "uuid_chara = " + uuid_chara);
////                        }
//                    }
//                }
//                if (notify_uuid_chara != null && notify_uuid_service != null) {
                    BleManager.getInstance().notify(bleDevice, BLEConstants.UUID_NOTIFY_SERVICE, BLEConstants.UUID_NOTIFY_CHARA, new BleNotifyCallback() {
                        @Override
                        public void onNotifySuccess() {
                            mBlueToothListener.getNotifyConnDeviceSuccess(bleDevice,"打开notify成功");
                        }

                        @Override
                        public void onNotifyFailure(BleException exception) {
                            mBlueToothListener.getNotifyConnDeviceFail(bleDevice,"打开notify失败");
                        }

                        @Override
                        public void onCharacteristicChanged(byte[] data) {
                            mBlueToothListener.getNotifyConnDeviceData(bleDevice,HexUtil.encodeHexStr(data));
                        }
                    });
//                } else {
//                    mBlueToothListener.getNotifyConnDeviceFail("设备可能不支持notify，没有搜索到notify的UUID");
//                }
            }
            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                mBlueToothListener.disConnDevice((BleDevice) bleDevice); // 断开连接
            }
        });
    }

    // 清除掉我们打开的蓝牙设备
    public void destroyBlueToothPlugin(){
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

    public static void sendWriteData(final BleDevice device, final byte[] data) {
        if (device != null) {
            BleManager.getInstance().write(device, BLEConstants.UUID_WRITE_SERVICE, BLEConstants.UUID_WRITE_CHARA, data, new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    mBlueToothListener.replyDataToDeviceSuccess(device,HexUtil.encodeHexStr(data));
                }

                @Override
                public void onWriteFailure(BleException exception) {
                    mBlueToothListener.replyDataToDeviceFailed(device,HexUtil.encodeHexStr(data) + exception.toString());
                }
            });
        } else {
            mBlueToothListener.replyDataToDeviceFailed(device,HexUtil.encodeHexStr(data) + "当前设备无回复特性");
        }
    }
}
