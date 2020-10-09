package com.yt.base.view;

import androidx.databinding.BaseObservable;

/**
 * @describe
 */

public class BaseViewModel extends BaseObservable {
    protected IView mView;

    public IView getIView() {
        return mView;
    }

    public void setIView(IView mView) {
        this.mView = mView;
    }
}
