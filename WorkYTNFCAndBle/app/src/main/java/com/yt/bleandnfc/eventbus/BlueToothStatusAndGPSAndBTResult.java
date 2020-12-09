package com.yt.bleandnfc.eventbus;

public class BlueToothStatusAndGPSAndBTResult {
    /**
     *  0 蓝牙未通讯状态
     *  1 蓝牙通讯状态改变
     *  2 蓝牙打开状态
     *  3 蓝牙关闭状态
     *  4 GPS打开状态
     *  5 GPS关闭状态
     */
    public int type;

    public BlueToothStatusAndGPSAndBTResult(int type) {
        this.type = type;
    }
}
