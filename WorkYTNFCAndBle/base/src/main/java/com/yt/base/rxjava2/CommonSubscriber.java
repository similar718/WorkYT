package com.yt.base.rxjava2;

import com.yt.base.application.BaseApplication;
import com.yt.base.utils.NetWorkUtils;
import com.yt.base.view.IView;

import java.net.ConnectException;

import io.reactivex.subscribers.ResourceSubscriber;

/**
 * 统一处理异常信息(用於FlowableSubscriber)
 *
 * @param <T>
 */
public abstract class CommonSubscriber<T> extends ResourceSubscriber<T> {
    private IView mView;

    public CommonSubscriber(IView mView) {
        this.mView = mView;
    }

    @Override
    public void onError(Throwable throwable) {
        if (throwable instanceof QzdsException) {
            QzdsException q = (QzdsException) throwable;
            if(mView!=null){
                mView.showToastMsg(throwable.getMessage());
            }

        } else if (throwable instanceof ConnectException) {
            if (!NetWorkUtils.isNetConnected(BaseApplication.getInstance1().getContext())) {
                if(mView!=null){
                    mView.showToastMsg("网络异常");
                }
            } else {
                if(mView!=null){
                    mView.showToastMsg("服务器异常");
                }
            }
        } else {
            if(mView!=null){
                mView.showToastMsg(throwable.getMessage());
            }
        }
    }

    @Override
    public void onComplete() {

    }
}
