package com.yt.bleandnfc.api.model;

import com.google.gson.Gson;

public class BindModel {

    /**
     * code : 100 绑定成功 101 绑定失败 102 解绑成功 103 解绑失败
     * obj : null
     * message : 绑定成功
     * e : null
     */

    private int code;
    private Object obj;
    private String message;
    private Object e;

    public static BindModel objectFromData(String str) {

        return new Gson().fromJson(str, BindModel.class);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getE() {
        return e;
    }

    public void setE(Object e) {
        this.e = e;
    }
}
