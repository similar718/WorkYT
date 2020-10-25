package com.yt.bleandnfc.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yt.bleandnfc.R;
import com.yt.bleandnfc.databinding.DialogBleAndGpsBinding;

import androidx.databinding.DataBindingUtil;

public class BLEAndGPSHintDialog extends Dialog {

    private Context context;
    DialogBleAndGpsBinding dataBinding;

    private BLEAndGPSHintClickListenerInterface clickListenerInterface;

    public BLEAndGPSHintDialog(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
        if (context == null) {
            return;
        }
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.dialog_ble_and_gps,null,false);

        setContentView(dataBinding.getRoot());

        setCancelable(false);// 不可以用“返回键”取消

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.8
        lp.gravity = Gravity.CENTER;
        getWindow().setAttributes(lp);

        dataBinding.dialogCancelBtn.setOnClickListener(new clickListener());
        dataBinding.dialogSureBtn.setOnClickListener(new clickListener());
    }

    public void showDialog(String content,boolean isPermissionHint){
        if (isShowing()){
            return;
        }
        if (isPermissionHint){
            dataBinding.dialogCancelBtn.setVisibility(View.VISIBLE);
        } else {
            dataBinding.dialogCancelBtn.setVisibility(View.GONE);
        }
        dataBinding.tvTitle.setText(content);
        show();
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.dialog_sure_btn: // 确定
                    cancel();
                    if (clickListenerInterface != null) {
                        clickListenerInterface.doSure();
                    }
                    break;
                case R.id.dialog_cancel_btn: // 取消
                    cancel();
                    break;
            }
        }
    }
    public void setBLEAndGPSHintClicklistener(BLEAndGPSHintClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    public interface BLEAndGPSHintClickListenerInterface {
        void doSure();
    }
}
