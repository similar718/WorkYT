package com.yt.bleandnfc.ui.home;

import android.app.Activity;

import com.yt.base.view.BaseViewModel;
import com.yt.bleandnfc.R;
import com.yt.bleandnfc.api.YTApiInterface;
import com.yt.bleandnfc.api.model.AlarmCountAlarmByStateModel;
import com.yt.bleandnfc.api.model.GetUserBindByUserId;
import com.yt.bleandnfc.base.observer.BaseHttpObserver;
import com.yt.bleandnfc.manager.SPManager;
import com.yt.bleandnfc.utils.NetworkUtil;
import com.yt.network.YTNetworkApi;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class InfoDetailViewModel extends BaseViewModel {

    public MutableLiveData<AlarmCountAlarmByStateModel> mAlarmCountAlarmByStateModel;
    public MutableLiveData<GetUserBindByUserId> mGetUserBindByUserId;

    private Activity mContext;

    public InfoDetailViewModel(Activity context) {
        mContext = context;
        mAlarmCountAlarmByStateModel = new MutableLiveData<>();
        mGetUserBindByUserId = new MutableLiveData<>();
    }

    public void getAlarmNum(){
        if (!NetworkUtil.isNetworkConnected()) {
            mView.showToastMsg(mContext.getString(R.string.neterror_check_appversion_fail));
            return;
        }
        YTNetworkApi.getService(YTApiInterface.class)
                .alarmCountAlarmByState(SPManager.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseHttpObserver<AlarmCountAlarmByStateModel>() {
                    @Override
                    public void getData(AlarmCountAlarmByStateModel data) {
                        mAlarmCountAlarmByStateModel.setValue(data);
                    }

                    @Override
                    public void onErrorInfo(Throwable e) {
                        mAlarmCountAlarmByStateModel.setValue(null);
                    }
                });
    }

    public void getUserBindByUserId(){
        if (!NetworkUtil.isNetworkConnected()) {
            mView.showToastMsg(mContext.getString(R.string.neterror_check_appversion_fail));
            return;
        }
        YTNetworkApi.getService(YTApiInterface.class)
                .getUserBindByUserId(SPManager.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseHttpObserver<GetUserBindByUserId>() {
                    @Override
                    public void getData(GetUserBindByUserId data) {
                        mGetUserBindByUserId.setValue(data);
                    }

                    @Override
                    public void onErrorInfo(Throwable e) {
                        mGetUserBindByUserId.setValue(null);
                    }
                });
    }
}