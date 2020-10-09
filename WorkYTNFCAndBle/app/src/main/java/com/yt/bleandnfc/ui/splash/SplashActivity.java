package com.yt.bleandnfc.ui.splash;

import android.app.Activity;
import android.os.Handler;

import com.yt.bleandnfc.R;
import com.yt.bleandnfc.base.activity.YTBaseActivity;
import com.yt.bleandnfc.databinding.ActivitySplashBinding;
import com.yt.bleandnfc.manager.IntentManager;
import com.yt.bleandnfc.mvvm.viewmodel.EmptyViewModel;


public class SplashActivity extends YTBaseActivity<EmptyViewModel, ActivitySplashBinding> {

    @Override
    protected int setLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected EmptyViewModel createViewModel() {
        viewModel = new EmptyViewModel();
        viewModel.setIView(this);
        return viewModel;
    }

    @Override
    protected void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                IntentManager.getInstance().goLoginActivity((Activity) mContext);
            }
        },2000); // 两秒后开始执行
    }
}
