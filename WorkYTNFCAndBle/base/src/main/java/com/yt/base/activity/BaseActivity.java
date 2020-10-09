package com.yt.base.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.yt.base.dialog.LoadingDialig;
import com.yt.base.utils.ToastUtils;
import com.yt.base.view.IView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity implements IView {

    public static List<BaseActivity> list = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list.add(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    public LoadingDialig mLoadingDialig;

    @Override
    public void showLoading(String content) {
        if (mLoadingDialig == null) {
            mLoadingDialig = new LoadingDialig(this, "正在加载");
        }
        if (!TextUtils.isEmpty(content)){
            mLoadingDialig.setTipsText(content);
        }
        mLoadingDialig.show();
    }

    @Override
    public void hideLoading() {
        if (mLoadingDialig != null) {
            mLoadingDialig.dismiss();
        }
    }

    @Override
    public void showToastMsg(String msg) {
        if(TextUtils.isEmpty(msg)){
            return;
        }
        if(msg.contains("timeout")){
            ToastUtils.showText(this,"网络连接超时");
        }else {
            ToastUtils.showText(this,msg);
        }
    }

    @Override
    public void showToastMsg(@StringRes int res) {
        ToastUtils.showText(this,res);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        list.remove(this);
    }
}
