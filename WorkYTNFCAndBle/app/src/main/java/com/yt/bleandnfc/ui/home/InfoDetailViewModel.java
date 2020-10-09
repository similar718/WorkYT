package com.yt.bleandnfc.ui.home;

import com.yt.base.view.BaseViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InfoDetailViewModel extends BaseViewModel {

    private MutableLiveData<String> mText;

    public InfoDetailViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Info Detail fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}