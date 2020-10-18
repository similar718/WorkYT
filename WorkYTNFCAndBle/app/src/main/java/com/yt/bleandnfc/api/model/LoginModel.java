package com.yt.bleandnfc.api.model;

import com.google.gson.Gson;

public class LoginModel {

    /**
     * code : 200 成功  400 返回异常
     * obj : {"id":"111","name":"admin","username":"admin","sex":"1","createTime":"2020-01-29 09:13:07","deptId":"jps","deptName":"机坪室"}
     * message : 服务器请求成功！
     * e : null
     */

    public int code;
    public LoginModel.ObjBean obj;
    public String message;
    public Object e;

    public static LoginModel objectFromData(String str) {

        return new Gson().fromJson(str, LoginModel.class);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public LoginModel.ObjBean getObj() {
        return obj;
    }

    public void setObj(LoginModel.ObjBean obj) {
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
         * id : 111
         * name : admin
         * username : admin
         * sex : 1
         * createTime : 2020-01-29 09:13:07
         * deptId : jps
         * deptName : 机坪室
         */

        private String id;
        private String name;
        private String username;
        private String sex;
        private String createTime;
        private String deptId;
        private String deptName;

        public static LoginModel.ObjBean objectFromData(String str) {

            return new Gson().fromJson(str, LoginModel.ObjBean.class);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
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
    }
}
