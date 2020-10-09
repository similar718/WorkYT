package com.yt.bleandnfc;

import android.content.Intent;
import android.os.Build;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.yt.base.utils.LogUtlis;
import com.yt.bleandnfc.base.activity.YTBaseActivity;
import com.yt.bleandnfc.databinding.ActivityMainBinding;
import com.yt.bleandnfc.mvvm.viewmodel.EmptyViewModel;
import com.yt.bleandnfc.service.KeepAppLifeService;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends YTBaseActivity<EmptyViewModel, ActivityMainBinding> {

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected EmptyViewModel createViewModel() {
        return new EmptyViewModel();
    }

    private int mEndMenuId = R.id.navigation_info_detail;

    private NavController mNavController;

    @Override
    protected void initData() {
        dataBinding.navView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_info_detail,
                R.id.navigation_warning_record,
                R.id.navigation_check_location,
                R.id.navigation_wb)
                .build();
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, mNavController, appBarConfiguration);
        NavigationUI.setupWithNavController(dataBinding.navView, mNavController);

        // 底部点击事件的处理 保存状态信息的处理
        //bottomNavigationView Item 选择监听
        dataBinding.navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean popBackStack = mNavController.popBackStack(item.getItemId(),false);
                if (popBackStack) {
                    return popBackStack;
                } else {
                    if (item.isChecked()) {
                        return false;
                    } else {
                        return NavigationUI.onNavDestinationSelected(item, mNavController);
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 保持前台进程 离开界面就开始服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, KeepAppLifeService.class));
        } else {
            startService(new Intent(this,KeepAppLifeService.class));
        }
    }

}
