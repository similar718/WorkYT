package com.yt.base.mvvm.viewmodel;

import com.yt.base.mvvm.model.IBaseModelListener;
import com.yt.base.mvvm.model.MvvmBaseModel;
import com.yt.base.mvvm.model.PagingResult;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

public abstract class MvvmBaseViewModel<M extends MvvmBaseModel, D> extends ViewModel implements LifecycleObserver, IBaseModelListener<List<D>> {
    protected M model;
    public MutableLiveData<List<D>> dataList = new MutableLiveData();
    public MutableLiveData<ViewStatus> viewStatusLiveData = new MutableLiveData();
    public MutableLiveData<String> errorMessage = new MutableLiveData();

    public MvvmBaseViewModel() {
        dataList.setValue(new ArrayList<>());
        viewStatusLiveData.setValue(ViewStatus.LOADING);
        errorMessage.setValue("");
    }

    public void tryToRefresh() {
        if (model != null) {
            model.refresh();
        }
    }

    public void tryToLoadNextPage() {
        model.load();
    }

    @Override
    protected void onCleared() {
        if (model != null) {
            model.cancel();
        }
    }

    @Override
    public void onLoadFinish(MvvmBaseModel model, List<D> data, PagingResult... pagingResult) {
        if (model == this.model) {
            if (model.isPaging()) {
                if (pagingResult[0].isEmpty) {
                    if (pagingResult[0].isFirstPage) {
                        viewStatusLiveData.postValue(ViewStatus.EMPTY);
                    } else {
                        viewStatusLiveData.postValue(ViewStatus.NO_MORE_DATA);
                    }
                } else {
                    if(pagingResult[0].isFirstPage){
                        dataList.postValue(data);
                    }else {
                        dataList.getValue().addAll(data);
                        dataList.postValue(dataList.getValue());
                    }
                    viewStatusLiveData.postValue(ViewStatus.SHOW_CONTENT);
                }
            } else {
                dataList.postValue(data);
                viewStatusLiveData.postValue(ViewStatus.SHOW_CONTENT);
            }
        }
    }

    @Override
    public void onLoadFail(MvvmBaseModel model, String prompt, PagingResult... pagingResult) {
        errorMessage.setValue(prompt);
        if(model.isPaging() && !pagingResult[0].isFirstPage) {
            viewStatusLiveData.postValue(ViewStatus.LOAD_MORE_FAILED);
        } else {
            viewStatusLiveData.postValue(ViewStatus.REFRESH_ERROR);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {
        if(dataList == null || dataList.getValue() == null || dataList.getValue().size() == 0) {
            model.getCachedDataAndLoad();
        } else {
            dataList.postValue(dataList.getValue());
            viewStatusLiveData.postValue(viewStatusLiveData.getValue());
        }
    }
}
