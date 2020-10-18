package com.yt.bleandnfc.mvvm.model;

import com.yt.base.mvvm.model.IBaseModelListener1;
import com.yt.base.mvvm.model.PagingResult;
import com.yt.bleandnfc.api.YTApiInterface;
import com.yt.bleandnfc.api.model.AlarmFindAlarmByStateModel;
import com.yt.bleandnfc.base.observer.BaseHttpObserver;
import com.yt.bleandnfc.manager.SPManager;
import com.yt.bleandnfc.utils.NetworkUtil;
import com.yt.network.YTNetworkApi;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WarningRecordModel {
    private IBaseModelListener1<List<AlarmFindAlarmByStateModel.ObjBean>> mListener;
    private int mPage = -1;
    private final int mSize = 10;
    private final int init_page = 0;

    public WarningRecordModel(IBaseModelListener1 listener){
        mListener = listener;
    }

    public void refresh(){
        mPage = init_page;
        loadNextPage();
    }

    public void loadNextPage() {
        if (!NetworkUtil.isNetworkConnected()) {
            mListener.onLoadFail("手机网络不可用，请检查手机网络设置");
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
                            mListener.onLoadSuccess(data.getObj(), new PagingResult(data.getObj().isEmpty(), mPage == init_page, data.getObj().size() == mSize));
                            mPage++;
                        } else {
                            mListener.onLoadFail(data.getMessage());
                        }
                    }

                    @Override
                    public void onErrorInfo(Throwable e) {
                        mListener.onLoadFail(e.getMessage());
                    }
                });
    }
}
