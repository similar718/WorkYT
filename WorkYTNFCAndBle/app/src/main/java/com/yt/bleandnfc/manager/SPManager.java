package com.yt.bleandnfc.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.yt.bleandnfc.base.YTApplication;

public class SPManager {

    private final String FILE_NAME = "yt_tech";//文件名

    private final String USER_NAME = "username"; // 登录名称 String

    private final String USER_PWD = "pwd"; // 登录密码 String

    private static SharedPreferences sp ;

    private SPManager(){
        sp = YTApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    private static SPManager instance;

    /**
     * 单例
     * @return
     */
    public static SPManager getInstance(){
        if (instance == null) {
            synchronized (SPManager.class) {
                if (instance == null) {
                    instance = new SPManager();
                }
            }
        }
        return instance;
    }

    /**
     * 保存用户登陆的手机号
     * @param username
     */
    public void setUserName(String username){
        sp.edit().putString(USER_NAME,username).commit();
    }

    /**
     * 获取用户登陆的手机号
     * @return
     */
    public String getUserName(){
        return sp.getString(USER_NAME,"");
    }

    /**
     * 保存用户登陆的密码 方便下次登录
     * @param password
     */
    public void setUserPwd(String password){
        sp.edit().putString(USER_PWD,password).commit();
    }

    /**
     * 获取用户登陆的密码
     * @return
     */
    public String getUserPwd(){
        return sp.getString(USER_PWD,"");
    }

}
