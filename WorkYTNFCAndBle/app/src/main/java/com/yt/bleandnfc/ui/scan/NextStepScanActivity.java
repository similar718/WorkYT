package com.yt.bleandnfc.ui.scan;

import com.yt.bleandnfc.R;
import com.yt.bleandnfc.base.activity.YTBaseActivity;
import com.yt.bleandnfc.databinding.ActivityNextStepScanBinding;
import com.yt.bleandnfc.manager.IntentManager;
import com.yt.bleandnfc.mvvm.viewmodel.EmptyViewModel;

/**
 * 该类用于跳转到扫描二维码界面的过度界面，处理从fragment直接跳转扫描界面出现黑屏
 */
public class NextStepScanActivity extends YTBaseActivity<EmptyViewModel, ActivityNextStepScanBinding> {

    @Override
    protected int setLayoutId() {
        return R.layout.activity_next_step_scan;
    }

    @Override
    protected EmptyViewModel createViewModel() {
        viewModel = new EmptyViewModel();
        viewModel.setIView(this);
        return viewModel;
    }

    @Override
    protected void initData() {
        //跳转到扫描界面
        IntentManager.getInstance().goCaptureActivity(mContext,getIntent().getIntExtra("type",1));
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();//只能在这里或者后finish，之前的话会出现黑屏
    }
}
