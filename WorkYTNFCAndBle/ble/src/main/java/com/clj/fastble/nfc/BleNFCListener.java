package com.clj.fastble.nfc;

import com.clj.fastble.data.BleDevice;

public interface BleNFCListener {
    void initFailed(byte data);  //  初始化失败 需要配合相关操作之后再重新初始化
    void initSuccess(); // 初始化成功
    void scanDevice(); // 扫描到目标设备
    void scanNotDevice(); // 未扫描到目标设备
    void startConnDevice(BleDevice bleDevice); // 开始连接
    void connSuccesDevice(BleDevice bleDevice); // 连接成功

    void getNotifyConnDeviceSuccess(BleDevice bleDevice,String scanDeviceData); // 获取已经连接之后的数据 并将数据更改一个位数 准备上传到服务器
    void getNotifyConnDeviceFail(BleDevice bleDevice,String scanDeviceData); // 获取已经连接之后的数据 并将数据更改一个位数 准备上传到服务器
    void getNotifyConnDeviceData(BleDevice bleDevice,String scanDeviceData); // 获取已经连接之后的数据 并将数据更改一个位数 准备上传到服务器

    void connFailedDevice(BleDevice bleDevice); // 连接失败
    void disConnDevice(BleDevice bleDevice); // 断开连接
    void replyDataToDeviceSuccess(BleDevice bleDevice,String data); // 回复硬件蓝牙成功
    void replyDataToDeviceFailed(BleDevice bleDevice,String data); // 回复硬件蓝牙失败
}
