package com.yt.bleandnfc.mvvm.viewmodel;

import android.app.Activity;

import com.yt.base.view.BaseViewModel;
import com.yt.bleandnfc.R;

import java.util.logging.LogManager;

import androidx.lifecycle.MutableLiveData;

/**
 * com.lien.fitpatchh3t.ui.activity.active.vm
 * CLC  2020/8/5
 */
public class InputActivateCodeViewModel extends BaseViewModel {

    private final String TAG = InputActivateCodeViewModel.class.getSimpleName();

//    public MutableLiveData<User> mUser;
//    public MutableLiveData<ActivateModel> mActivate;

    private Activity mContext;

    public InputActivateCodeViewModel(Activity context) {
        mContext = context;
//        mActivate = new MutableLiveData<>();
//        mUser = new MutableLiveData<>();
    }

    /**
     * 将授权码请求服务器
     */
    public void postActiviteInfo(String authCode){
//        if (!NetworkUtil.isNetworkConnected()) {
//            mView.showToastMsg(mContext.getString(R.string.neterror_check_appversion_fail));
//            return;
//        }
//        String json = "{ \n" +
//                "\"accountId\":" + SPManager.getInstance().getAccountId() + ",\n" +
//                " \"authCode\": \"" + authCode + "\"\n}";
//        RetrofitUtils.getInstance().requestData(ApiService.class)
//                .getActivate(RequestBodyUtils.getRequestBody(json))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseObserver<ActivateModel>() {
//                    @Override
//                    public void startAnalysis() {
//                        LogManager.e(TAG,"开始上传服务器 激活码");
//                    }
//
//                    @Override
//                    public void getData(ActivateModel data) {
//                        mActivate.setValue(data);
//                        if (data.success.equals("true")) {
//                            mView.showToastMsg("激活成功");
//                        } else {
//                            mView.showToastMsg("" + data.message);
//                        }
//                    }
//
//                    @Override
//                    public void onErrorInfo(Throwable e) {
//                        mActivate.setValue(null);
//                        mView.showToastMsg("请求激活失败 ： " + e.getMessage());
//                    }
//                });

    }

    /**
     * 获取用户信息
     */
    public void getUserInfo(){
//        if (!NetworkUtil.isNetworkConnected()) {
//            mView.showToastMsg(mContext.getString(R.string.neterror_check_appversion_fail));
//            return;
//        }
//        RetrofitUtils.getInstance().requestData(ApiService.class)
//                .getUserInfo(SPManager.getInstance().getAccountId())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseObserver<User>() {
//                    @Override
//                    public void startAnalysis() {
//                        LogManager.e(TAG,"开始请求个人信息");
//                    }
//
//                    @Override
//                    public void getData(User data) {
//                        mUser.setValue(data);
//                    }
//
//                    @Override
//                    public void onErrorInfo(Throwable e) {
//                        mUser.setValue(null);
//                        mView.showToastMsg("请求个人信息异常 ： " + e.getMessage());
//                    }
//                });
    }
}
