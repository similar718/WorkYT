package com.yt.bleandnfc.ui.settings;

import com.yt.base.view.BaseViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SettingsViewModel extends BaseViewModel {

    private MutableLiveData<String> mText;

    public SettingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Info Detail fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}