package com.yt.bleandnfc.ui.personal;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.yt.base.view.BaseViewModel;

public class PersonalViewModel extends BaseViewModel {

    private MutableLiveData<String> mText;

    public PersonalViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}