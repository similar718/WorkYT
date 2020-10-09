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
import com.yt.bleandnfc.databinding.DialogScanResultBinding;

import androidx.databinding.DataBindingUtil;

public class ScanResultDialog extends Dialog {
    private Context context;
    private ScanResultClickListenerInterface clickListenerInterface;

    DialogScanResultBinding dataBinding;

    public ScanResultDialog(Context context) {
        super(context, R.style.CustomDialog);
        this.context = context;
        if (context == null) {
            return;
        }
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.dialog_scan_result,null,false);

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

    public void showDialog(String str1,String str2,String str3,String str4){
        String content = str1 + "\n" + str2 + "\n" + str3 + "\n" + str4 ;
        dataBinding.tvContent.setText(content);
        show();
    }

    public void setScanResultClicklistener(ScanResultClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    private class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.dialog_sure_btn: // 确定
                    if (clickListenerInterface != null) {
                        clickListenerInterface.doSure();
                    }
                    cancel();
                    break;
                case R.id.dialog_cancel_btn: // 取消
                    cancel();
                    break;
            }
        }
    }

    public interface ScanResultClickListenerInterface {
        void doSure();
    }
}
