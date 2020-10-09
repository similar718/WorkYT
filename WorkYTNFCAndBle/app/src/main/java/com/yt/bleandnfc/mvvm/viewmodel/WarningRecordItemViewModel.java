package com.yt.bleandnfc.mvvm.viewmodel;

import com.yt.base.customview.BaseCustomViewModel;

public class WarningRecordItemViewModel extends BaseCustomViewModel {
    public String typeStr;
    public String contentStr;
    public String timeStr;

    public WarningRecordItemViewModel(String typeStr, String contentStr, String timeStr) {
        this.typeStr = typeStr;
        this.contentStr = contentStr;
        this.timeStr = timeStr;
    }
}
