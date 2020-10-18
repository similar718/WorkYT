package com.yt.network.observer;

import com.yt.base.mvvm.model.MvvmBaseModel;
import com.yt.base.mvvm.model.MvvmDataObserver;
import com.yt.network.errorhandler.ExceptionHandle;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Allen on 2017/7/20.
 * 保留所有版权，未经允许请不要分享到互联网和其他人
 */
public class BaseObserver<T> implements Observer<T> {
    MvvmBaseModel baseModel;
    MvvmDataObserver<T> mvvmDataObserver;
    public BaseObserver(MvvmBaseModel baseModel, MvvmDataObserver<T> mvvmDataObserver) {
        this.baseModel = baseModel;
        this.mvvmDataObserver = mvvmDataObserver;
    }
    @Override
    public void onError(Throwable e) {
        if(e instanceof ExceptionHandle.ResponeThrowable){
            mvvmDataObserver.onFailure(e);
        } else {
            mvvmDataObserver.onFailure(new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.UNKNOWN));
        }
    }

    @Override
    public void onNext(T t) {
        mvvmDataObserver.onSuccess(t, false);
    }

    @Override
    public void onSubscribe(Disposable d) {
        if(baseModel != null){
            baseModel.addDisposable(d);
        }
    }

    @Override
    public void onComplete() {
    }
}
