package com.yt.bleandnfc.api.model;

import com.google.gson.Gson;

import java.util.List;

public class GetUserBindByUserId {

    /**
     * code : 200
     * obj : {"bindState":true,"devices":[{"id":"2c9865ca71ad131c0171f8b40308000e","number":"000006000000","carNumber":"民航D9005","name":"定位","type":"wheelbarrow","latestLocation":"103.9564435,30.5667546","lastTime":"2020-10-26 18:42:34","company":"car","deptId":"jps","deptName":"机坪室","procurementTime":"2020-05-08 00:00:00","maxSpeed":25,"maxMileage":11111,"state":0,"configureCode":"code1","remarks":null,"personId":null,"createTime":null,"useState":null,"deviceConfig":null,"dictionary":null,"deviceGisList":null,"deviceGis":null,"person":null,"deviceUseRecord":null,"deviceUseRecords":null,"deviceGisSubsectionList":null,"deviceBind":{"id":"402803057526085701752609646d0003","userId":"111","userName":"admin","number":"000006000000","type":"01","source":"02","bindTime":"2020-10-14 15:35:51","unbindTime":null}}]}
     * message : 服务器请求成功！
     * e : null
     */

    private int code;
    private ObjBean obj;
    private String message;
    private Object e;

    public static GetUserBindByUserId objectFromData(String str) {

        return new Gson().fromJson(str, GetUserBindByUserId.class);
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
         * bindState : true
         * devices : [{"id":"2c9865ca71ad131c0171f8b40308000e","number":"000006000000","carNumber":"民航D9005","name":"定位","type":"wheelbarrow","latestLocation":"103.9564435,30.5667546","lastTime":"2020-10-26 18:42:34","company":"car","deptId":"jps","deptName":"机坪室","procurementTime":"2020-05-08 00:00:00","maxSpeed":25,"maxMileage":11111,"state":0,"configureCode":"code1","remarks":null,"personId":null,"createTime":null,"useState":null,"deviceConfig":null,"dictionary":null,"deviceGisList":null,"deviceGis":null,"person":null,"deviceUseRecord":null,"deviceUseRecords":null,"deviceGisSubsectionList":null,"deviceBind":{"id":"402803057526085701752609646d0003","userId":"111","userName":"admin","number":"000006000000","type":"01","source":"02","bindTime":"2020-10-14 15:35:51","unbindTime":null}}]
         */

        private boolean bindState;
        private List<DevicesBean> devices;

        public static ObjBean objectFromData(String str) {

            return new Gson().fromJson(str, ObjBean.class);
        }

        public boolean isBindState() {
            return bindState;
        }

        public void setBindState(boolean bindState) {
            this.bindState = bindState;
        }

        public List<DevicesBean> getDevices() {
            return devices;
        }

        public void setDevices(List<DevicesBean> devices) {
            this.devices = devices;
        }

        public static class DevicesBean {
            /**
             * id : 2c9865ca71ad131c0171f8b40308000e
             * number : 000006000000
             * carNumber : 民航D9005
             * name : 定位
             * type : wheelbarrow
             * latestLocation : 103.9564435,30.5667546
             * lastTime : 2020-10-26 18:42:34
             * company : car
             * deptId : jps
             * deptName : 机坪室
             * procurementTime : 2020-05-08 00:00:00
             * maxSpeed : 25.0
             * maxMileage : 11111.0
             * state : 0
             * configureCode : code1
             * remarks : null
             * personId : null
             * createTime : null
             * useState : null
             * deviceConfig : null
             * dictionary : null
             * deviceGisList : null
             * deviceGis : null
             * person : null
             * deviceUseRecord : null
             * deviceUseRecords : null
             * deviceGisSubsectionList : null
             * deviceBind : {"id":"402803057526085701752609646d0003","userId":"111","userName":"admin","number":"000006000000","type":"01","source":"02","bindTime":"2020-10-14 15:35:51","unbindTime":null}
             */

            private String id;
            private String number;
            private String carNumber;
            private String name;
            private String type;
            private String latestLocation;
            private String lastTime;
            private String company;
            private String deptId;
            private String deptName;
            private String procurementTime;
            private double maxSpeed;
            private double maxMileage;
            private int state;
            private String configureCode;
            private Object remarks;
            private Object personId;
            private Object createTime;
            private Object useState;
            private Object deviceConfig;
            private Object dictionary;
            private Object deviceGisList;
            private Object deviceGis;
            private Object person;
            private Object deviceUseRecord;
            private Object deviceUseRecords;
            private Object deviceGisSubsectionList;
            private DeviceBindBean deviceBind;

            public static DevicesBean objectFromData(String str) {

                return new Gson().fromJson(str, DevicesBean.class);
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
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

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getLatestLocation() {
                return latestLocation;
            }

            public void setLatestLocation(String latestLocation) {
                this.latestLocation = latestLocation;
            }

            public String getLastTime() {
                return lastTime;
            }

            public void setLastTime(String lastTime) {
                this.lastTime = lastTime;
            }

            public String getCompany() {
                return company;
            }

            public void setCompany(String company) {
                this.company = company;
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

            public String getProcurementTime() {
                return procurementTime;
            }

            public void setProcurementTime(String procurementTime) {
                this.procurementTime = procurementTime;
            }

            public double getMaxSpeed() {
                return maxSpeed;
            }

            public void setMaxSpeed(double maxSpeed) {
                this.maxSpeed = maxSpeed;
            }

            public double getMaxMileage() {
                return maxMileage;
            }

            public void setMaxMileage(double maxMileage) {
                this.maxMileage = maxMileage;
            }

            public int getState() {
                return state;
            }

            public void setState(int state) {
                this.state = state;
            }

            public String getConfigureCode() {
                return configureCode;
            }

            public void setConfigureCode(String configureCode) {
                this.configureCode = configureCode;
            }

            public Object getRemarks() {
                return remarks;
            }

            public void setRemarks(Object remarks) {
                this.remarks = remarks;
            }

            public Object getPersonId() {
                return personId;
            }

            public void setPersonId(Object personId) {
                this.personId = personId;
            }

            public Object getCreateTime() {
                return createTime;
            }

            public void setCreateTime(Object createTime) {
                this.createTime = createTime;
            }

            public Object getUseState() {
                return useState;
            }

            public void setUseState(Object useState) {
                this.useState = useState;
            }

            public Object getDeviceConfig() {
                return deviceConfig;
            }

            public void setDeviceConfig(Object deviceConfig) {
                this.deviceConfig = deviceConfig;
            }

            public Object getDictionary() {
                return dictionary;
            }

            public void setDictionary(Object dictionary) {
                this.dictionary = dictionary;
            }

            public Object getDeviceGisList() {
                return deviceGisList;
            }

            public void setDeviceGisList(Object deviceGisList) {
                this.deviceGisList = deviceGisList;
            }

            public Object getDeviceGis() {
                return deviceGis;
            }

            public void setDeviceGis(Object deviceGis) {
                this.deviceGis = deviceGis;
            }

            public Object getPerson() {
                return person;
            }

            public void setPerson(Object person) {
                this.person = person;
            }

            public Object getDeviceUseRecord() {
                return deviceUseRecord;
            }

            public void setDeviceUseRecord(Object deviceUseRecord) {
                this.deviceUseRecord = deviceUseRecord;
            }

            public Object getDeviceUseRecords() {
                return deviceUseRecords;
            }

            public void setDeviceUseRecords(Object deviceUseRecords) {
                this.deviceUseRecords = deviceUseRecords;
            }

            public Object getDeviceGisSubsectionList() {
                return deviceGisSubsectionList;
            }

            public void setDeviceGisSubsectionList(Object deviceGisSubsectionList) {
                this.deviceGisSubsectionList = deviceGisSubsectionList;
            }

            public DeviceBindBean getDeviceBind() {
                return deviceBind;
            }

            public void setDeviceBind(DeviceBindBean deviceBind) {
                this.deviceBind = deviceBind;
            }

            public static class DeviceBindBean {
                /**
                 * id : 402803057526085701752609646d0003
                 * userId : 111
                 * userName : admin
                 * number : 000006000000
                 * type : 01
                 * source : 02
                 * bindTime : 2020-10-14 15:35:51
                 * unbindTime : null
                 */

                private String id;
                private String userId;
                private String userName;
                private String number;
                private String type;
                private String source;
                private String bindTime;
                private Object unbindTime;

                public static DeviceBindBean objectFromData(String str) {

                    return new Gson().fromJson(str, DeviceBindBean.class);
                }

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
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

                public String getNumber() {
                    return number;
                }

                public void setNumber(String number) {
                    this.number = number;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getSource() {
                    return source;
                }

                public void setSource(String source) {
                    this.source = source;
                }

                public String getBindTime() {
                    return bindTime;
                }

                public void setBindTime(String bindTime) {
                    this.bindTime = bindTime;
                }

                public Object getUnbindTime() {
                    return unbindTime;
                }

                public void setUnbindTime(Object unbindTime) {
                    this.unbindTime = unbindTime;
                }
            }
        }
    }
}
