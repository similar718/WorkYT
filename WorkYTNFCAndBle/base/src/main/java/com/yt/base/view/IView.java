package com.yt.base.view;

/**
 * @describe
 */
public interface IView {
    void showLoading(String content);
    void hideLoading();
    void showToastMsg(String msg);
    void showToastMsg(int res);
}
