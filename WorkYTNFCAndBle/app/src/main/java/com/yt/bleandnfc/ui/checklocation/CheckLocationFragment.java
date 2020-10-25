package com.yt.bleandnfc.ui.checklocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.yt.bleandnfc.R;
import com.yt.bleandnfc.base.fragment.YTBaseFragment;
import com.yt.bleandnfc.constant.Constants;
import com.yt.bleandnfc.databinding.FragmentCheckLocationBinding;
import com.yt.bleandnfc.manager.SPManager;
import com.yt.bleandnfc.ui.view.CommonTitleBarView;

import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;

public class CheckLocationFragment extends YTBaseFragment<CheckLocationViewModel, FragmentCheckLocationBinding> {

    @Override
    public void onLazyLoad() {

    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_check_location;
    }

    @Override
    protected CheckLocationViewModel createViewModel() {
        viewModel = new CheckLocationViewModel();
        viewModel.setIView(this);
        return viewModel;
    }

    private String[] urls = {"https://map.baidu.com"};
    private String mCarNumber = "";

    @Override
    protected void initData() {
        initWebView();
        mCarNumber = SPManager.getInstance().getCarNum();
        if (!TextUtils.isEmpty(mCarNumber)) {
            // WebView
            dataBinding.wvView.loadUrl(Constants.CHECK_LOCATION_ADDRESS + mCarNumber);
            dataBinding.etCarCode.setText(mCarNumber);
        } else {
            // WebView
            dataBinding.wvView.loadUrl(urls[0]);
        }

        initClick();
    }

    private void initWebView() {
        WebSettings webSettings = dataBinding.wvView.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
        // 若加载的 html 里有JS 在执行动画等操作，会造成资源浪费（CPU、电量）
        // 在 onStop 和 onResume 里分别把 setJavaScriptEnabled() 给设置成 false 和 true 即可

        //支持插件
        webSettings.setPluginState(WebSettings.PluginState.ON);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
    }

    private void initClick() {
        // 返回
        dataBinding.titleView.setTitleLeftClick(new CommonTitleBarView.OnTitleLeftClick() {
            @Override
            public void onLeftClick() {
                // 返回到主界面
                Navigation.findNavController(dataBinding.titleView).navigateUp();
            }
        });

        // 确认提交
        dataBinding.tvSure.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                // 确认提交
                String inputData = dataBinding.etCarCode.getText().toString().trim();
                if (TextUtils.isEmpty(inputData)) {
                    showToastMsg(R.string.check_location_input_car_code);
                    return;
                }
                // WebView
                dataBinding.wvView.loadUrl(Constants.CHECK_LOCATION_ADDRESS+inputData);
            }
        });

        // webView
        dataBinding.wvView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // 开始加载
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 加载结束
            }

            // 链接跳转会走这个方法
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(String.valueOf(request.getUrl())); // 强制在当前WebView中加载Url
//                return true;
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        dataBinding.wvView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                // 加载进度
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                // 标题
            }
        });

        dataBinding.etCarCode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showSoftInputFromWindow(dataBinding.etCarCode);
                return false;
            }
        });
    }

    public void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.findFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }
}
