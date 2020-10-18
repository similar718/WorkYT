package com.yt.bleandnfc.eventbus;

/**
 * 扫描返回数据
 */
public class AlarmAddResult {

    public int type; // 1 增加

    public AlarmAddResult(int type) {
        this.type = type;
    }
}
