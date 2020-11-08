package com.clj.fastble.config;

public class Constants {
    public static String mBleName = "BTELITE"; // 扫描蓝牙设备的名称

//    uuid_service = 0000ffe0-0000-1000-8000-00805f9b34fb  支持notify和indicate
//    uuid_chara = 0000ffe4-0000-1000-8000-00805f9b34fb
    public static final String UUID_NOTIFY_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static final String UUID_NOTIFY_CHARA = "0000ffe4-0000-1000-8000-00805f9b34fb";

//    uuid_service = 0000ffe5-0000-1000-8000-00805f9b34fb 可写入
//    uuid_chara = 0000ffe9-0000-1000-8000-00805f9b34fb
    public static final String UUID_WRITE_SERVICE = "0000ffe5-0000-1000-8000-00805f9b34fb";
    public static final String UUID_WRITE_CHARA = "0000ffe9-0000-1000-8000-00805f9b34fb";

}
