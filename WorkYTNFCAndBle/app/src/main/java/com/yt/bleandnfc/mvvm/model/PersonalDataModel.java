package com.yt.bleandnfc.mvvm.model;

import com.yt.base.mvvm.model.IBaseModelListener1;
import com.yt.base.mvvm.model.PagingResult;
import com.yt.bleandnfc.api.model.PersonalModel;
import com.yt.bleandnfc.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

public class PersonalDataModel {
    private IBaseModelListener1<List<PersonalModel>> mListener;
    private int mPage = -1;
    private final int mSize = 10;
    private final int init_page = 0;

    public PersonalDataModel(IBaseModelListener1 listener){
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
        List<PersonalModel> data = new ArrayList<>();
        if (mPage != init_page) {
            for (int i = 0; i < mSize-1; i++) {
                data.add(new PersonalModel("张三" + i, 0, "9:1" + i));
            }
        } else {
            for (int i = 0; i < mSize; i++) {
                data.add(new PersonalModel("张三" + i, 1, "9:1" + i));
            }
        }
        mListener.onLoadSuccess(data, new PagingResult(data.isEmpty(), mPage == init_page, data.size() == mSize));
        mPage++;

//        YTNetworkApi.getService(YTApiInterface.class)
//                .alarmFindAlarmByState(SPManager.getInstance().getUserId(),mPage,mSize)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseHttpObserver<AlarmFindAlarmByStateModel>() {
//                    @Override
//                    public void getData(AlarmFindAlarmByStateModel data) {
//                        if (data.getCode() == 200) {
//                            mListener.onLoadSuccess(data.getObj(), new PagingResult(data.getObj().isEmpty(), mPage == init_page, data.getObj().size() == mSize));
//                            mPage++;
//                        } else {
//                            mListener.onLoadFail(data.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void onErrorInfo(Throwable e) {
//                        mListener.onLoadFail(e.getMessage());
//                    }
//                });
    }
}
