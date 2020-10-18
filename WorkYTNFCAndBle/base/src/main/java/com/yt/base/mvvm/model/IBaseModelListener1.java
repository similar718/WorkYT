package com.yt.base.mvvm.model;

public interface IBaseModelListener1<T> {
    void onLoadSuccess(T data, PagingResult... pageResult);

    void onLoadFail(String prompt);
}