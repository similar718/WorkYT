package com.yt.bleandnfc.api.model;

import com.google.gson.Gson;

public class AlarmDealAlarmModel {

    /**
     * code : 200
     * obj : {"msgId":"402803057526b465017526b922d20002","msg":"未授权使用","number":"000006000000","carNumber":"民航D9005","msgType":"unauthorized","deptId":"jps","deptName":"机坪室","carType":"wheelbarrow","userId":"111","userName":"admin","state":"true","createTime":"2020-10-14 18:47:48","createBy":"sys","updateTime":"2020-10-14 19:10:47","updateBy":"111","bak":"备注","lng":"103.3432423","lat":"30.2321334"}
     * message : 服务器请求成功！
     * e : null
     */

    private int code;
    private ObjBean obj;
    private String message;
    private Object e;

    public static AlarmDealAlarmModel objectFromData(String str) {

        return new Gson().fromJson(str, AlarmDealAlarmModel.class);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ObjBean getObj() {
        return obj;
    }

    public void setObj(ObjBean obj) {
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

    public static class ObjBean {
        /**
         * msgId : 402803057526b465017526b922d20002
         * msg : 未授权使用
         * number : 000006000000
         * carNumber : 民航D9005
         * msgType : unauthorized
         * deptId : jps
         * deptName : 机坪室
         * carType : wheelbarrow
         * userId : 111
         * userName : admin
         * state : true
         * createTime : 2020-10-14 18:47:48
         * createBy : sys
         * updateTime : 2020-10-14 19:10:47
         * updateBy : 111
         * bak : 备注
         * lng : 103.3432423
         * lat : 30.2321334
         */

        private String msgId;
        private String msg;
        private String number;
        private String carNumber;
        private String msgType;
        private String deptId;
        private String deptName;
        private String carType;
        private String userId;
        private String userName;
        private String state;
        private String createTime;
        private String createBy;
        private String updateTime;
        private String updateBy;
        private String bak;
        private String lng;
        private String lat;

        public static ObjBean objectFromData(String str) {

            return new Gson().fromJson(str, ObjBean.class);
        }

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getCarNumber() {
            return carNumber;
        }

        public void setCarNumber(String carNumber) {
            this.carNumber = carNumber;
        }

        public String getMsgType() {
            return msgType;
        }

        public void setMsgType(String msgType) {
            this.msgType = msgType;
        }

        public String getDeptId() {
            return deptId;
        }

        public void setDeptId(String deptId) {
            this.deptId = deptId;
        }

        public String getDeptName() {
            return deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }

        public String getCarType() {
            return carType;
        }

        public void setCarType(String carType) {
            this.carType = carType;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getCreateBy() {
            return createBy;
        }

        public void setCreateBy(String createBy) {
            this.createBy = createBy;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String getUpdateBy() {
            return updateBy;
        }

        public void setUpdateBy(String updateBy) {
            this.updateBy = updateBy;
        }

        public String getBak() {
            return bak;
        }

        public void setBak(String bak) {
            this.bak = bak;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }
    }
}
