package com.yt.bleandnfc.ui.checklocation;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.yt.base.utils.LogUtlis;
import com.yt.bleandnfc.R;
import com.yt.bleandnfc.base.fragment.YTBaseFragment;
import com.yt.bleandnfc.constant.Constants;
import com.yt.bleandnfc.databinding.FragmentCheckLocationBinding;
import com.yt.bleandnfc.manager.SPManager;
import com.yt.bleandnfc.ui.view.CommonTitleBarView;
import com.yt.bleandnfc.utils.TimeUtil;

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

    @Override
    protected void initData() {
        initWebView();
        // http://47.108.48.111:85/?type=user&lng=103.9535407&lat=30.5821512&userName=admin&userId=123&deptName=机坪室&updateTime=2020-10-27 14:03
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(Constants.CHECK_LOCATION_ADDRESS)
                .append("type=user")
                .append("&lng=")
                .append(Constants.LOCATION_LNG)
                .append("&lat=")
                .append(Constants.LOCATION_LAT)
                .append("&userName=")
                .append(SPManager.getInstance().getUserName())
                .append("&userId=")
                .append("222")
//                .append(SPManager.getInstance().getUserId())
                .append("&deptName=")
                .append(SPManager.getInstance().getDeptName())
                .append("&updateTime=")
                .append(TimeUtil.getTodayTimeYMDHMS())
        ;
        LogUtlis.e("oooooooooooo",stringBuilder.toString());
        dataBinding.wvView.loadUrl(stringBuilder.toString());

        //通过加载xml文件配置的数据源
        ArrayAdapter spinnerAdapterPart = ArrayAdapter.createFromResource(getActivity(), R.array.CheckLocationPartType,R.layout.custom_spinner_text_item);
        //设置下拉选项的方式
        spinnerAdapterPart.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        dataBinding.elvPartType.setAdapter(spinnerAdapterPart);

        //通过加载xml文件配置的数据源
        ArrayAdapter spinnerAdapterCar = ArrayAdapter.createFromResource(getActivity(), R.array.CheckLocationCarType,R.layout.custom_spinner_text_item);
        //设置下拉选项的方式
        spinnerAdapterCar.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        dataBinding.elvCarType.setAdapter(spinnerAdapterCar);


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

    private final String[] partType = {"","df","jps","csc","cca"};
    private final String[] carType = {"","powerless","wheelbarrow"};

    private int mCarType = 0;
    private int mPartType = 0;

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
            @Override
            public void onClick(View v) {
                // 确认提交
                String inputData = dataBinding.etCarCode.getText().toString().trim();

                if (mCarType == 0 && mPartType == 0 && TextUtils.isEmpty(inputData)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToastMsg("请选择单位/车辆类型/填写车辆编号");
                        }
                    });
                    return;
                }

                getURLAndRequestLocation(inputData);
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

        dataBinding.elvCarType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCarType = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dataBinding.elvPartType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mPartType = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

    /**
     * @param carCode
     */
    private void getURLAndRequestLocation(String carCode){
        /**
         * http://47.108.48.111:85/?carNumber=66660001&type=car&dept=jps&carType=wheelbarrow&lng=103.9535407&lat=
         * 30.5821512&userName=admin&userId=123&deptName=机坪室&updateTime=2020-10-27 14:03
         */
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(Constants.CHECK_LOCATION_ADDRESS)
                .append("carNumber=")
                .append(carCode)
                .append("&type=car&dept=")
                .append(partType[mPartType])
                .append("&carType=")
                .append(carType[mCarType])
                .append("&lng=")
                .append(Constants.LOCATION_LNG)
                .append("&lat=")
                .append(Constants.LOCATION_LAT)
                .append("&userName=")
                .append(SPManager.getInstance().getUserName())
                .append("&userId=")
                .append("222")
//                .append(SPManager.getInstance().getUserId())
                .append("&deptName=")
                .append(SPManager.getInstance().getDeptName())
                .append("&updateTime=")
                .append(TimeUtil.getTodayTimeYMDHMS())
                ;
        LogUtlis.e("oooooooooooo",stringBuilder.toString());
        dataBinding.wvView.loadUrl(stringBuilder.toString());
    }
}
