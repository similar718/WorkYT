package com.yt.bleandnfc.mvvm.viewmodel;

import android.app.Activity;

import com.yt.base.view.BaseViewModel;
import com.yt.bleandnfc.R;
import com.yt.bleandnfc.api.YTApiInterface;
import com.yt.bleandnfc.api.model.BindModel;
import com.yt.bleandnfc.api.model.CarNumberInfoModel;
import com.yt.bleandnfc.base.observer.BaseHttpObserver;
import com.yt.bleandnfc.utils.NetworkUtil;
import com.yt.network.YTNetworkApi;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class InputActivateCodeViewModel extends BaseViewModel {

    private final String TAG = InputActivateCodeViewModel.class.getSimpleName();

    public MutableLiveData<CarNumberInfoModel> mCarNumberInfo;
    public MutableLiveData<BindModel> mBind;

    private Activity mContext;

    public InputActivateCodeViewModel(Activity context) {
        mContext = context;
        mCarNumberInfo = new MutableLiveData<>();
        mBind = new MutableLiveData<>();
    }

    /**
     * 获取当前二维码数据的信息
     */
    public void getCarNumberInfo(String numberInfo){
        if (!NetworkUtil.isNetworkConnected()) {
            mView.showToastMsg(mContext.getString(R.string.neterror_check_appversion_fail));
            return;
        }
        YTNetworkApi.getService(YTApiInterface.class)
                .getCarNumberInfo(numberInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseHttpObserver<CarNumberInfoModel>() {
                    @Override
                    public void getData(CarNumberInfoModel data) {
                        mCarNumberInfo.setValue(data);
                    }

                    @Override
                    public void onErrorInfo(Throwable e) {
                        mCarNumberInfo.setValue(null);
                    }
                });
    }

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
    /**
     * 绑定设备 解绑设备
     */
    public void optionBindAndUnBind(String userId,
                                    String numberInfo,
                                    boolean isBind,
                                    boolean isPhone,
                                    double longitude,
                                    String longitudeType,
                                    double latitude,
                                    String latitudeType,
                                    int satellite){
        if (!NetworkUtil.isNetworkConnected()) {
            mView.showToastMsg(mContext.getString(R.string.neterror_check_appversion_fail));
            return;
        }
        YTNetworkApi.getService(YTApiInterface.class)
                .bindDevice(userId,numberInfo,isBind ? "01" : "02",isPhone ? " 02" : "01" , longitude,longitudeType,latitude,latitudeType,satellite)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseHttpObserver<BindModel>() {
                    @Override
                    public void getData(BindModel data) {
                        mBind.setValue(data);
                    }

                    @Override
                    public void onErrorInfo(Throwable e) {
                        mBind.setValue(null);
                    }
                });
    }
}
