package com.yt.bleandnfc.ui.settings;

import android.view.View;

import com.yt.bleandnfc.R;
import com.yt.bleandnfc.base.YTApplication;
import com.yt.bleandnfc.base.fragment.YTBaseFragment;
import com.yt.bleandnfc.databinding.FragmentSettingsBinding;
import com.yt.bleandnfc.manager.IntentManager;
import com.yt.bleandnfc.manager.SPManager;
import com.yt.bleandnfc.ui.dialog.SettingsDialog;
import com.yt.bleandnfc.ui.view.CommonTitleBarView;

import androidx.navigation.Navigation;

public class SettingsFragment extends YTBaseFragment<SettingsViewModel, FragmentSettingsBinding> implements View.OnClickListener {

    @Override
    public void onLazyLoad() {

    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected SettingsViewModel createViewModel() {
        viewModel = new SettingsViewModel();
        viewModel.setIView(this);
        return viewModel;
    }

    @Override
    protected void initData() {
        // ip
        dataBinding.tvCurrentContent.setText("192.168.0.1");

        dataBinding.tvCenter.setText(""+SPManager.getInstance().getSaveFontScale());

        initClick();
    }

    private void initClick(){
        // 返回
        dataBinding.titleView.setTitleLeftClick(new CommonTitleBarView.OnTitleLeftClick() {
            @Override
            public void onLeftClick() {
                // 返回主界面 主Fragment
                Navigation.findNavController(dataBinding.titleView).navigateUp();
            }
        });
        // 设置IP
        dataBinding.setIpBg.setOnClickListener(this);
        // 设置端口
        dataBinding.setPortBg.setOnClickListener(this);
        // 退出登录
        dataBinding.tvLogout.setOnClickListener(this);

        // 字体变小
        dataBinding.tvSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float fontscale = (float) (SPManager.getInstance().getSaveFontScale() - 0.1);
                YTApplication.setAppFontSize(fontscale);
                dataBinding.tvCenter.setText(""+fontscale);
            }
        });

        // 字体变大
        dataBinding.tvBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float fontscale = (float) (SPManager.getInstance().getSaveFontScale() + 0.1);
                YTApplication.setAppFontSize(fontscale);
                dataBinding.tvCenter.setText(""+fontscale);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_ip_bg: // 设置IP 显示dialog
                showSettingsDialog(1);
                break;
            case R.id.set_port_bg: // 设置端口 显示dialog
                showSettingsDialog(2);
                break;
            case R.id.tv_logout: // 退出登录
                IntentManager.getInstance().goLoginActivity(getActivity());
                break;
        }
    }

    private SettingsDialog mSettingsDialog;

    /**
     * 显示设置界面
     * @param type
     */
    private void showSettingsDialog(int type){
        if (mSettingsDialog == null) {
            mSettingsDialog = new SettingsDialog(getActivity());
            mSettingsDialog.setSettingsClicklistener(new SettingsDialog.SettingsClickListenerInterface() {
                @Override
                public void doSure(int types) {
                    if (types == 1) {
                        showToastMsg("设置ip成功");
                    } else if (types == 2){
                        showToastMsg("设置端口成功");
                    }
                }
            });
        }
        mSettingsDialog.showDialog(type);
    }
}
