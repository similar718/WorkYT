package com.yt.bleandnfc.eventbus;

/**
 * 蓝牙状态信息
 */
public class BlueStatusResult {

    public int type; // 1 蓝牙打开 2 蓝牙关闭

    public BlueStatusResult(int type) {
        this.type = type;
    }
}
