package com.yt.bleandnfc.ui.login;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.yt.bleandnfc.R;
import com.yt.bleandnfc.api.model.LoginModel;
import com.yt.bleandnfc.base.activity.YTBaseActivity;
import com.yt.bleandnfc.databinding.ActivityLoginBinding;
import com.yt.bleandnfc.manager.IntentManager;
import com.yt.bleandnfc.manager.SPManager;
import com.yt.bleandnfc.mvvm.viewmodel.LoginViewModel;
import com.yt.common.interfaces.IPermissionListener;

import java.util.List;

import androidx.lifecycle.Observer;

public class LoginActivity extends YTBaseActivity<LoginViewModel, ActivityLoginBinding> {

    @Override
    protected int setLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected LoginViewModel createViewModel() {
        viewModel = new LoginViewModel((Activity) mContext);
        viewModel.setIView(this);

        viewModel.mLoginModel.observe(this, new Observer<LoginModel>() {
            @Override
            public void onChanged(LoginModel loginModel) {
                if (loginModel != null) {
                    if (loginModel.obj != null && loginModel.code == 200) {
                        showToastMsg("登录成功");
                        SPManager.getInstance().setUserId(loginModel.getObj().getId());
                        SPManager.getInstance().setDeptId(loginModel.getObj().getDeptId());
                        SPManager.getInstance().setDeptName(loginModel.getObj().getDeptName());
                        loginSuccess();
                    } else {
                        showToastMsg(loginModel.message);
                    }
                } else {
                    showToastMsg("请求异常");
                }
            }
        });
        return viewModel;
    }
    // APP主要权限 存储权限和位置权限 位置权限主要是在搜索心贴的时候有用到
    public static final String[] PERMISSIONPERMS_STORAGE_LOCATION = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA};

    @Override
    protected void initData() {
        String username = SPManager.getInstance().getUserName();
        String userpwd = SPManager.getInstance().getUserPwd();
        if (!TextUtils.isEmpty(username)){
            dataBinding.etUsername.setText(username);
        }
        if (!TextUtils.isEmpty(userpwd)){
            dataBinding.etPwd.setText(userpwd);
        }

        dataBinding.cbSaveInfo.setChecked(SPManager.getInstance().getSaveStatusLogin());

        // 开始申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestRunTimePermission(PERMISSIONPERMS_STORAGE_LOCATION,iPermissionListener);
        }

        dataBinding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = dataBinding.etUsername.getText().toString().trim();
                String userpwd = dataBinding.etPwd.getText().toString().trim();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(userpwd)){
                    showToastMsg("请输入工号/名称或者密码");
                    return;
                }
                viewModel.login(username,userpwd);
            }
        });
    }

    private void loginSuccess(){
        // 登录
        if (dataBinding.cbSaveInfo.isChecked()) {
            SPManager.getInstance().setUserName(dataBinding.etUsername.getText().toString().trim());
            SPManager.getInstance().setUserPwd(dataBinding.etPwd.getText().toString().trim());
            SPManager.getInstance().setSaveStatusLogin(true);
        } else {
            SPManager.getInstance().setUserName("");
            SPManager.getInstance().setUserPwd("");
            SPManager.getInstance().setSaveStatusLogin(false);
        }
        IntentManager.getInstance().goMainActivity(mContext);
        finish();
    }

    private IPermissionListener iPermissionListener = new IPermissionListener() {
        @Override
        public void onGranted() {
//            showToastMsg("已成功授权");
        }

        @Override
        public void onGranted(List<String> grantedPermission) {

        }

        @Override
        public void onDenied(List<String> deniedPermission) {

        }

        @Override
        public void onDenied() {

        }
    };
}
