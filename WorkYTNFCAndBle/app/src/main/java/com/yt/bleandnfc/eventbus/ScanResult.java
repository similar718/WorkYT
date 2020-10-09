package com.yt.bleandnfc.eventbus;

/**
 * 扫描返回数据
 */
public class ScanResult {

    public int type;
    public String content;

    public ScanResult(int type, String content) {
        this.type = type;
        this.content = content;
    }
}
