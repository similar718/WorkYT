package com.yt.bleandnfc.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.yt.bleandnfc.base.YTApplication;

public class SPManager {

    private final String FILE_NAME = "yt_tech";//文件名

    private final String USER_NAME = "username"; // 登录名称 String

    private final String USER_PWD = "pwd"; // 登录密码 String

    private final String CAR_NUM = "carNum"; // 车辆编号 String

    private final String USER_ID = "userid"; // userid String

    private final String DEPT_NAME = "deptName"; // deptName String

    private final String DEPT_ID = "deptId"; // deptId String

    private final String SAVE_STATUS_LOGIN = "saveStatusLogin"; // 是否记住账号和密码 boolean

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

    /**
     * 保存车辆编号
     * @param carNum
     */
    public void setCarNum(String carNum){
        sp.edit().putString(CAR_NUM,carNum).commit();
    }

    /**
     * 获取车辆编号
     * @return
     */
    public String getCarNum(){
        return sp.getString(CAR_NUM,"");
    }

    /**
     * 保存用户编号
     * @param userId
     */
    public void setUserId(String userId){
        sp.edit().putString(USER_ID,userId).commit();
    }

    /**
     * 获取用户编号
     * @return
     */
    public String getUserId(){
        return sp.getString(USER_ID,"");
    }
    /**
     * 保存用户编号
     * @param deptId
     */
    public void setDeptId(String deptId){
        sp.edit().putString(DEPT_ID,deptId).commit();
    }

    /**
     * 获取用户编号
     * @return
     */
    public String getDeptId(){
        return sp.getString(DEPT_ID,"");
    }

    /**
     * 保存部门名称
     * @param deptName
     */
    public void setDeptName(String deptName){
        sp.edit().putString(DEPT_NAME,deptName).commit();
    }

    /**
     * 获取部门名称
     * @return
     */
    public String getDeptName(){
        return sp.getString(DEPT_NAME,"");
    }

    /**
     * 保存登录状态
     * @param saveStatusLogin
     */
    public void setSaveStatusLogin(boolean saveStatusLogin){
        sp.edit().putBoolean(SAVE_STATUS_LOGIN,saveStatusLogin).commit();
    }

    /**
     * 获取登录状态
     * @return
     */
    public boolean getSaveStatusLogin(){
        return sp.getBoolean(SAVE_STATUS_LOGIN,false);
    }

}
