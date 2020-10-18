package com.yt.bleandnfc.utils;

import android.Manifest;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

public class BLEAndGPSUtils {

    /**
     * 蓝牙是否打开
     * @return
     */
    public static boolean isOpenBLE(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) { // 设备不支持蓝牙
            return false;
        }
        if (!bluetoothAdapter.isEnabled()) { // 蓝牙未打开
            return false;
        }
        return true;
    }

    /**
     * 是否给定位权限
     * @param mContext
     * @return
     */
    public static boolean isOpenPermissionGPS(Context mContext){
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    /**
     * 是否开启GPS
     * @param mContext
     * @return
     */
    public static boolean isOpenGPS(Application mContext){
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
