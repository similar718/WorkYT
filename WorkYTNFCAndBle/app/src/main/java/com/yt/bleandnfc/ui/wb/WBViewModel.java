package com.yt.bleandnfc.ui.wb;

import com.yt.base.view.BaseViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WBViewModel extends BaseViewModel {

    private MutableLiveData<String> mText;

    public WBViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is WB fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}