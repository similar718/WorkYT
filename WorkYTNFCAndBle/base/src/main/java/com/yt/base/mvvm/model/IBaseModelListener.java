package com.yt.base.mvvm.model;

public interface IBaseModelListener<T> {
    void onLoadFinish(MvvmBaseModel model, T data, PagingResult... pageResult);

    void onLoadFail(MvvmBaseModel model, String prompt, PagingResult... pageResult);
}