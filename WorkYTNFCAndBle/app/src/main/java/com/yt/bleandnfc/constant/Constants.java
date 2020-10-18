package com.yt.bleandnfc.constant;

/**
 * 用来存放静态数据
 */
public class Constants {

    /**
     * 所有文件保存地址
     */
    public static final String FILE_NAME = "YT";

    /**
     * 查车定位的接口位置
     */
    public static final String CHECK_LOCATION_ADDRESS = "http://47.108.48.111:85/?carNumber=";

    /**
     * 定位信息
     */
    public static double LOCATION_LNG = 103.3432423;
    public static double LOCATION_LAT = 30.2321334;
    public static String LOCATION_LNG_TYPE = "E"; // E代表东经，W代表西经
    public static String LOCATION_LAT_TYPE = "N"; // N代表北纬  S 代表南纬
    public static int LOCATION_SATELLIE = 12; // 卫星数量

    /**
     * 报警记录的数量
     */
    public static int mAlarmNum = 0;
}
