package com.yt.bleandnfc.mvvm.view;

import android.content.Context;
import android.view.View;

import com.yt.base.customview.BaseCustomView;
import com.yt.bleandnfc.R;
import com.yt.bleandnfc.databinding.ItemWarningRecordBinding;
import com.yt.bleandnfc.mvvm.viewmodel.WarningRecordItemViewModel;
import com.yt.bleandnfc.ui.dialog.WarningRecordDetailDialog;

public class WarningRecordItemView extends BaseCustomView<ItemWarningRecordBinding, WarningRecordItemViewModel> {

    public WarningRecordItemView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_warning_record;
    }

    @Override
    public void onRootClicked(View view) {
        showWarningRecordDetail(data.contentStr);
    }

    @Override
    protected void setDataToView(WarningRecordItemViewModel titleViewModel) {
        binding.setViewModel(data);
    }

    private WarningRecordDetailDialog mWarningRecordDetail;

    private void showWarningRecordDetail(String content){
        if (mWarningRecordDetail == null) {
            mWarningRecordDetail = new WarningRecordDetailDialog(getContext());
        }
        mWarningRecordDetail.showDialog(content);
    }
}
