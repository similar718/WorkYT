package com.yt.bleandnfc.ui.checklocation;

import com.yt.base.view.BaseViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class CheckLocationViewModel extends BaseViewModel {

    private MutableLiveData<String> mText;

    public CheckLocationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}