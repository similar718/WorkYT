package com.yt.bleandnfc.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yt.bleandnfc.R;

import androidx.annotation.RequiresApi;

/**
 * 自定义公共使用的TitleBar
 * com.lien.fitpatchh3t.view
 * CLC  2020/7/17
 */
public class CommonTitleBarView extends LinearLayout {

    private final String TAG = CommonTitleBarView.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CommonTitleBarView(Context context) {
        this(context,null); //调用同名构造方法
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CommonTitleBarView(Context context, AttributeSet attrs) {
        this(context, attrs,0); //调用同名构造方法
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CommonTitleBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CommonTitleBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super(context, attrs, defStyleAttr, defStyleRes);
        initInfo(context,attrs);
    }

    private TextView mLeftTv,mTitleTv,mRightTv,mBgTv; // 左边 标题 右边文字 背景控件
    private ImageView mLeftIv,mRightIv; // 左边 右边图片

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initInfo(Context context, AttributeSet attrs){
        //将打气筒根据自定义控件的布局文件，创建的view 对象挂载到当前类上面，然后显示
        LayoutInflater.from(context).inflate(R.layout.title_bar_common, this,true);
        // 背景
        mBgTv = findViewById(R.id.title_bar_bg);
        // 左边
        mLeftTv = findViewById(R.id.back_tv);
        mLeftIv = findViewById(R.id.back_iv);
        // 标题
        mTitleTv = findViewById(R.id.title);
        // 右边
        mRightTv = findViewById(R.id.right_tv);
        mRightIv = findViewById(R.id.right_iv);

        mLeftIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLeftClick != null) {
                    mLeftClick.onLeftClick();
                }
            }
        });

        mLeftTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLeftClick != null) {
                    mLeftClick.onLeftClick();
                }
            }
        });

        mRightIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightClick != null) {
                    mRightClick.onRightClick();
                }
            }
        });

        mRightTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightClick != null) {
                    mRightClick.onRightClick();
                }
            }
        });
        //初始化相关自定义属性
        initStyle(context,attrs);
    }

    /**
     * <attr name="title_bg_res" format="integer"/> <!-- 背景图片 -->
     * <attr name="left_txt" format="string"/> <!-- 左边文字 默认为空-->
     * <attr name="left_iv_res" format="integer"/> <!-- 左边图片 默认为返回-->
     * <attr name="left_tv_show" format="boolean"/> <!-- 左边文字是否显示 默认为不显示-->
     * <attr name="left_iv_show" format="boolean"/> <!-- 左边图片是否显示 默认为显示-->
     * <attr name="title_txt" format="string"/> <!-- 标题文字 默认为首页 -->
     * <attr name="right_txt" format="string"/> <!-- 右边文字 默认为空 -->
     * <attr name="right_iv_res" format="integer"/> <!-- 右边图片 默认为返回 -->
     * <attr name="right_tv_show" format="boolean"/> <!-- 右边文字是否显示 默认不显示 -->
     * <attr name="right_iv_show" format="boolean"/> <!-- 右边图片是否显示 默认不显示-->
     * @param context
     * @param attrs
     */
    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initStyle(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBarView);
        Drawable title_bg_res = a.getDrawable(R.styleable.CommonTitleBarView_title_bg_res);
        String left_txt = a.getString(R.styleable.CommonTitleBarView_left_txt);
        Drawable left_iv_res = a.getDrawable(R.styleable.CommonTitleBarView_left_iv_res);
        boolean left_tv_show = a.getBoolean(R.styleable.CommonTitleBarView_left_tv_show,false);
        boolean left_iv_show = a.getBoolean(R.styleable.CommonTitleBarView_left_iv_show,true);
        String title_txt = a.getString(R.styleable.CommonTitleBarView_title_txt);
        String right_txt = a.getString(R.styleable.CommonTitleBarView_right_txt);
        Drawable right_iv_res = a.getDrawable(R.styleable.CommonTitleBarView_right_iv_res);
        boolean right_tv_show = a.getBoolean(R.styleable.CommonTitleBarView_right_tv_show,false);
        boolean right_iv_show = a.getBoolean(R.styleable.CommonTitleBarView_right_iv_show,false);
        boolean title_bg_show = a.getBoolean(R.styleable.CommonTitleBarView_title_bg_show,true);

        // 进行赋值操作
        // 背景
        if (title_bg_res!=null && title_bg_res != getContext().getDrawable(R.drawable.title_bg)) {
            mBgTv.setBackground(title_bg_res);
        } else {
            mBgTv.setBackgroundResource(R.drawable.title_bg);
        }

        // 是否显示背景图片
        if (title_bg_show) {
            mBgTv.setVisibility(VISIBLE);
        } else {
            mBgTv.setVisibility(INVISIBLE);
        }

        // 左边处理
        if (!TextUtils.isEmpty(left_txt)) {
            mLeftTv.setText(left_txt);
        }
        if (left_tv_show){
            mLeftTv.setVisibility(View.VISIBLE);
            mLeftIv.setVisibility(View.INVISIBLE);
        }

        if (left_iv_res != null && left_iv_res != getContext().getDrawable(R.drawable.back_icon)) {
            mLeftIv.setImageDrawable(left_iv_res);
        } else {
            mLeftIv.setImageResource(R.drawable.back_icon);
        }

        if (left_iv_show){
            mLeftIv.setVisibility(View.VISIBLE);
            mLeftTv.setVisibility(View.INVISIBLE);
        } else {
            mLeftIv.setVisibility(View.INVISIBLE);
        }
        // 标题
        if (!TextUtils.isEmpty(title_txt)){
            mTitleTv.setText(title_txt);
        }
        // 右边处理
        if (!TextUtils.isEmpty(right_txt)){
            mRightTv.setText(right_txt);
        }

        if (right_iv_res != null && right_iv_res != getContext().getDrawable(R.drawable.back_icon)) {
            mRightIv.setImageDrawable(right_iv_res);
        } else {
            mRightIv.setImageResource(R.drawable.back_icon);
        }

        if (right_tv_show){
            mRightTv.setVisibility(View.VISIBLE);
            mRightIv.setVisibility(View.INVISIBLE);
        }
        if (right_iv_show){
            mRightTv.setVisibility(View.INVISIBLE);
            mRightIv.setVisibility(View.VISIBLE);
        }
    }

    private OnTitleLeftClick mLeftClick;

    public void setTitleLeftClick(OnTitleLeftClick left){
        mLeftClick = left;
    }

    private OnTitleRightClick mRightClick;

    public void setTitleRightClick(OnTitleRightClick right){
        mRightClick = right;
    }

    public interface OnTitleLeftClick{
        void onLeftClick();
    }

    public interface OnTitleRightClick{
        void onRightClick();
    }

    /**
     * 右边文字是否显示 个人信息界面需要
     * @param isShow
     */
    public void setRightTextIsShow(boolean isShow) {
        mRightTv.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 设置中间title的文字 主要用于一个xml界面 多个activity使用
     * @param data
     */
    public void setTitleText(String data) {
        mTitleTv.setText(data);
    }

    /**
     * 设置中间title的文字 主要用于一个xml界面 多个activity使用
     * @param res
     */
    public void setTitleText(int res) {
        mTitleTv.setText(res);
    }

    /**
     * 左边图标是否显示 个人信息界面需要
     * @param isShow
     */
    public void setLeftIvIsShow(boolean isShow) {
        mLeftIv.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }
}
