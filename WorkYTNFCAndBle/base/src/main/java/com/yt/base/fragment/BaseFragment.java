package com.yt.base.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import com.yt.base.dialog.LoadingDialig;
import com.yt.base.utils.ToastUtils;
import com.yt.base.view.IView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment implements IView {

    public static List<BaseFragment> list = new ArrayList<>();

    public boolean mIsActive = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list.add(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsActive = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsActive = false;
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    public LoadingDialig mLoadingDialig;

    @Override
    public void showLoading(String content) {
        if (!mIsActive){
            return;
        }
        if (mLoadingDialig == null) {
            mLoadingDialig = new LoadingDialig(getActivity(), "正在加载");
        }
        if (!TextUtils.isEmpty(content)){
            mLoadingDialig.setTipsText(content);
        }
        mLoadingDialig.show();
    }

    @Override
    public void hideLoading() {
        if (!mIsActive){
            return;
        }
        if (mLoadingDialig != null) {
            mLoadingDialig.dismiss();
        }
    }

    @Override
    public void showToastMsg(String msg) {
        if (!mIsActive){
            return;
        }
        if(TextUtils.isEmpty(msg)){
            return;
        }
        if(msg.contains("timeout")){
            ToastUtils.showText(getActivity(),"网络连接超时");
        }else {
            ToastUtils.showText(getActivity(),msg);
        }
    }

    @Override
    public void showToastMsg(@IdRes int res) {
        if (!mIsActive){
            return;
        }
        ToastUtils.showText(getActivity(),res);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        list.remove(this);
    }
}
