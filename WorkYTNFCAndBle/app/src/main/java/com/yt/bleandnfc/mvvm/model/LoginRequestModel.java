package com.yt.bleandnfc.mvvm.model;

import com.yt.base.mvvm.model.MvvmBaseModel;
import com.yt.bleandnfc.api.YTApiInterface;
import com.yt.bleandnfc.api.model.LoginModel;
import com.yt.network.YTNetworkApi;
import com.yt.network.observer.BaseObserver;

public class LoginRequestModel extends MvvmBaseModel<LoginModel, LoginModel> {

    private String mUserName;
    private String mUserPwd;

    public LoginRequestModel(String userName,String userPwd) {
        super(false, null, null);
        mUserName = userName;
        mUserPwd = userPwd;
    }


    @Override
    public void onSuccess(LoginModel newsChannelsBean, boolean isFromCache) {
        notifyResultToListeners(newsChannelsBean, newsChannelsBean, isFromCache);
    }

    @Override
    public void onFailure(Throwable e) {
        e.printStackTrace();
        loadFail(e.getMessage());
    }

    @Override
    public void load() {
        YTNetworkApi.getService(YTApiInterface.class)
                .login(mUserName,mUserPwd)
                .compose(YTNetworkApi.
                        getInstance().
                        applySchedulers(new BaseObserver<LoginModel>(this, this)));
    }
}
