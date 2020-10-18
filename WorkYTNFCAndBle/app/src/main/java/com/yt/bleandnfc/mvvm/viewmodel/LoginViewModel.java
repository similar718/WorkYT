package com.yt.bleandnfc.mvvm.viewmodel;

import android.app.Activity;

import com.yt.base.view.BaseViewModel;
import com.yt.bleandnfc.R;
import com.yt.bleandnfc.api.YTApiInterface;
import com.yt.bleandnfc.api.model.LoginModel;
import com.yt.bleandnfc.base.observer.BaseHttpObserver;
import com.yt.bleandnfc.utils.NetworkUtil;
import com.yt.network.YTNetworkApi;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends BaseViewModel {

    public MutableLiveData<LoginModel> mLoginModel;

    private Activity mContext;

    public LoginViewModel(Activity context){
        mContext = context;
        mLoginModel = new MutableLiveData<>();
    }

    public void login(String username,String userpwd){
        if (!NetworkUtil.isNetworkConnected()) {
            mView.showToastMsg(mContext.getString(R.string.neterror_check_appversion_fail));
            return;
        }
        YTNetworkApi.getService(YTApiInterface.class)
                .login(username,userpwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseHttpObserver<LoginModel>() {
                    @Override
                    public void getData(LoginModel data) {
                        mLoginModel.setValue(data);
                    }

                    @Override
                    public void onErrorInfo(Throwable e) {
                        mLoginModel.setValue(null);
                    }
                });
    }
}
