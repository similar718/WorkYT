package com.yt.bleandnfc.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yt.bleandnfc.R;

/**
 * Created by admin on 2017/6/2.
 */

public class CommonCenterDialog extends Dialog {
    private Context context;
    private String remindMsg;
    private String confirmButtonText;
    private String cacelButtonText;
    private ClickListenerInterface clickListenerInterface;

    private TextView remind;
    public TextView confirmBtn;
    private TextView cancelBtn;
    private int resourceLayout = 0;

    public CommonCenterDialog(Context context, String remindMsg, String confirmButtonText, String cacelButtonText, int resourceLayout) {
        super(context, R.style.SetHeadPortraitDialog);
        this.context = context;
        this.remindMsg = remindMsg;
        this.confirmButtonText = confirmButtonText;
        this.cacelButtonText = cacelButtonText;
        this.resourceLayout = resourceLayout;

        if (context == null) {
            return;
        }
    }

    public CommonCenterDialog(Context context,String remindMsg, String confirmButtonText, String cacelButtonText) {
        super(context, R.style.SetHeadPortraitDialog);
        this.context = context;
        this.remindMsg = remindMsg;
        this.confirmButtonText = confirmButtonText;
        this.cacelButtonText = cacelButtonText;
        if (context == null) {
            return;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (resourceLayout == 0) {
            view = inflater.inflate(R.layout.dialog_remind_layout, null);
        } else {
            view = inflater.inflate(resourceLayout, null);
        }

        setContentView(view);

        remind = (TextView) view.findViewById(R.id.remind_content_msg);
        confirmBtn = (TextView) view.findViewById(R.id.dialog_confirm_btn);
        cancelBtn = (TextView) view.findViewById(R.id.dialog_cancel_btn);

        remind.setText(remindMsg);
        confirmBtn.setText(confirmButtonText);
        cancelBtn.setText(cacelButtonText);

        confirmBtn.setOnClickListener(new clickListener());
        cancelBtn.setOnClickListener(new clickListener());

        setCancelable(false);// 不可以用“返回键”取消
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);

    }

    /**
     * 设置提示文字
     * @param remindMsg
     * @param confirmButtonText
     * @param cacelButtonText
     */
    public void setValue(String remindMsg, String confirmButtonText, String cacelButtonText) {
        remind.setText(remindMsg);
        confirmBtn.setText(confirmButtonText);
        cancelBtn.setText(cacelButtonText);

    }


    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.dialog_confirm_btn:
                    clickListenerInterface.doConfirm();
                    break;
                case R.id.dialog_cancel_btn:
                    clickListenerInterface.doCancel();
                    break;
            }
        }
    }

    public interface ClickListenerInterface {

        public void doConfirm();

        public void doCancel();

    }
}
