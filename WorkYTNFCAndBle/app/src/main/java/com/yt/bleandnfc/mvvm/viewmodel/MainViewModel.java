package com.yt.bleandnfc.mvvm.viewmodel;

import android.app.Activity;

import com.yt.base.view.BaseViewModel;
import com.yt.bleandnfc.api.YTApiInterface;
import com.yt.bleandnfc.api.model.AlarmFindAlarmByStateModel;
import com.yt.bleandnfc.base.observer.BaseHttpObserver;
import com.yt.bleandnfc.manager.SPManager;
import com.yt.bleandnfc.utils.NetworkUtil;
import com.yt.network.YTNetworkApi;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends BaseViewModel {

    public MutableLiveData<AlarmFindAlarmByStateModel> mAlarmFindAlarmByStateModel;

    public MainViewModel(){
        mAlarmFindAlarmByStateModel = new MutableLiveData<>();
    }

    private int mPage = 0;
    private int mSize = 100;

    public void alarmList(){
        if (!NetworkUtil.isNetworkConnected()) {
            return;
        }
        YTNetworkApi.getService(YTApiInterface.class)
                .alarmFindAlarmByState(SPManager.getInstance().getUserId(),mPage,mSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseHttpObserver<AlarmFindAlarmByStateModel>() {
                    @Override
                    public void getData(AlarmFindAlarmByStateModel data) {
                        if (data.getCode() == 200) {
                            mAlarmFindAlarmByStateModel.setValue(data);
                        }
                    }

                    @Override
                    public void onErrorInfo(Throwable e) {

                    }
                });
    }
}
