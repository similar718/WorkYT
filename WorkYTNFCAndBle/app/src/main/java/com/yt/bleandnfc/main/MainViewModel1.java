package com.yt.bleandnfc.main;

import com.yt.base.view.BaseViewModel;

import androidx.lifecycle.MutableLiveData;

public class MainViewModel1 extends BaseViewModel {

    public MutableLiveData<Object> mSosData;

    public MainViewModel1() {
        mSosData = new MutableLiveData<Object>();
    }

    public void getPayInfo() {
    }
}
