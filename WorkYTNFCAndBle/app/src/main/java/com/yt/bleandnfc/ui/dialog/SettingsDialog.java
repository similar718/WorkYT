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
import com.yt.bleandnfc.databinding.DialogSettingsBinding;

import androidx.databinding.DataBindingUtil;

public class SettingsDialog extends Dialog {

    private Context context;
    private SettingsClickListenerInterface clickListenerInterface;

    DialogSettingsBinding dataBinding;

    public SettingsDialog(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
        if (context == null) {
            return;
        }
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.dialog_settings,null,false);

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

    private int mType = 0;

    /**
     * @param type 1 ip 2 port
     */
    public void showDialog(int type){
        mType = type;
        if (type == 1) {
            dataBinding.tvTitle.setText(R.string.setting_new_ip);
            dataBinding.etInputContent.setHint(R.string.setting_new_ip_input);
        } else if (type == 2) {
            dataBinding.tvTitle.setText(R.string.setting_new_port);
            dataBinding.etInputContent.setHint(R.string.setting_new_port_input);
        }
        show();
    }

    public void setSettingsClicklistener(SettingsClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.dialog_sure_btn: // 确定
                    if (clickListenerInterface != null) {
                        clickListenerInterface.doSure(mType);
                    }
                    cancel();
                    break;
                case R.id.dialog_cancel_btn: // 取消
                    cancel();
                    break;
            }
        }
    }

    public interface SettingsClickListenerInterface {
        void doSure(int type);
    }
}
