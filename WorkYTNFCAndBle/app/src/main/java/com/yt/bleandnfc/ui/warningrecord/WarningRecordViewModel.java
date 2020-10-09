package com.yt.bleandnfc.ui.warningrecord;

import com.yt.base.view.BaseViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WarningRecordViewModel extends BaseViewModel {

    private MutableLiveData<String> mText;

    public WarningRecordViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}