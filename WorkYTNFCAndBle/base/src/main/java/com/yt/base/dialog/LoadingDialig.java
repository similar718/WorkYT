package com.yt.base.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yt.base.R;

/**
 * 加载dialog
 */
public class LoadingDialig extends AlertDialog {
    private String content="";
    private Context mContext;
    private TextView tips;
    private ImageView spinnerImageView;

    public LoadingDialig(Context context) {
        super(context);
        mContext = context;
    }

    public LoadingDialig(Context context, String s) {
        super(context, R.style.dialog_map_content); // 界面全透明
//        super(context); // 有半透明背景
        content = s;
        mContext = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 如果要没有背景的灰色透明度 需要重新写一个style
        setContentView(R.layout.dialog_loading);
        tips = findViewById(R.id.message);
        spinnerImageView = findViewById(R.id.spinnerImageView);
        if (!TextUtils.isEmpty(content)){
            tips.setText(content);
        }
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT ;
        getWindow().setAttributes(params);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
    }

    public void setTipsText(String tipsText){
        content=tipsText;
        if (tips != null){
            tips.setText(content);
        }
    }
}
