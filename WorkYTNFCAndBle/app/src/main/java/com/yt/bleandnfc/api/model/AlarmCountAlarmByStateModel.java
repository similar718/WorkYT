package com.yt.bleandnfc.api.model;

import com.google.gson.Gson;

public class AlarmCountAlarmByStateModel {

    /**
     * code : 200
     * obj : 3
     * message : 服务器请求成功！
     * e : null
     */

    private int code;
    private int obj;
    private String message;
    private Object e;

    public static AlarmCountAlarmByStateModel objectFromData(String str) {

        return new Gson().fromJson(str, AlarmCountAlarmByStateModel.class);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getObj() {
        return obj;
    }

    public void setObj(int obj) {
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
