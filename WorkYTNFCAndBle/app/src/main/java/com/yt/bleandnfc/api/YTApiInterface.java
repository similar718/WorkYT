package com.yt.bleandnfc.api;

import com.yt.bleandnfc.api.model.AlarmCountAlarmByStateModel;
import com.yt.bleandnfc.api.model.AlarmDealAlarmModel;
import com.yt.bleandnfc.api.model.AlarmFindAlarmByStateModel;
import com.yt.bleandnfc.api.model.AlarmSaveModel;
import com.yt.bleandnfc.api.model.BindModel;
import com.yt.bleandnfc.api.model.CarNumberInfoModel;
import com.yt.bleandnfc.api.model.LoginModel;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface YTApiInterface {

    /**
     * 登录接口
     * @param Username 用户名称
     * @param PassWord 用户密码
     * @return
     */
    @POST("login/login")
    Observable<LoginModel> login(@Query("username") String Username,
                                 @Query("password") String PassWord);

    /**
     * 根据设备编号查询部门及车辆状况接口
     * @param number
     *      device/findDeviceByNumber/{number}
     *      device/findDeviceBycarNumber/66660001
     * @return
     */
    @POST("device/findDeviceBycarNumber/{number}")
    Observable<CarNumberInfoModel> getCarNumberInfo(@Path("number") String number);

    /**
     * 绑定接口 解绑接口
     * @param UserId 人员绑号
     * @param EquipmentNumber 设备编号（二维码）
     * @param Type 01代表绑定，02代表解绑
     * @param CoordinateSource 02代表手机，01代表设备
     * @param Longitude 坐标
     * @param LongType E代表东经，W代表西经
     * @param Latitude 坐标
     * @param LatType  N代表北纬  S 代表南纬
     * @param Satellite 卫星数量
     * @return
     */
    @POST("deviceBind/bind")
    Observable<BindModel> bindDevice(
            @Query("UserId") String UserId,
            @Query("EquipmentNumber") String EquipmentNumber,
            @Query("Type") String Type,
            @Query("CoordinateSource") String CoordinateSource,
            @Query("Longitude") double Longitude,
            @Query("LongType") String LongType,
            @Query("Latitude") double Latitude,
            @Query("LatType") String LatType,
            @Query("Satellite") int Satellite);


    /**
     *  新增报警记录接口
     * @param msgType unauthorized
     * @param number 000006000000
     * @param lng 定位经纬度
     * @param lat 定位经纬度
     * @return
     */
    @POST("alarm/save")
    Observable<AlarmSaveModel> alarmSave(
            @Query("msgType") String msgType,
            @Query("number") String number,
            @Query("lng") double lng,
            @Query("lat") double lat);


    /**
     *  分页查询报警分页查询最新报警详情
     * @param userId 用户ID
     * @param page 当前页 默认 0
     * @param size 一页大小
     * @return
     */
    @POST("alarm/findAlarmBystate")
    Observable<AlarmFindAlarmByStateModel> alarmFindAlarmByState(
            @Query("userId") String userId,
            @Query("page") int page,
            @Query("size") int size);


    /**
     *  报警信息处置接口
     * @param msgId 报警信息编号
     * @param userId 用户编号
     * @param bak 备注
     * @return
     */
    @POST("alarm/dealAlarm")
    Observable<AlarmDealAlarmModel> alarmDealAlarm(
            @Query("msgId") String msgId,
            @Query("userId") String userId,
            @Query("bak") String bak);

    /**
     * 查询报警分页查询最新报警记录数
     * @param userId
     * @return 数量
     */
    @POST("alarm/countAlarmBystate")
    Observable<AlarmCountAlarmByStateModel> alarmCountAlarmByState(
            @Query("userId") String userId);
}
