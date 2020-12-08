package com.example.ytdemo;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.ytdemo.keyboard.SoftKeyBoardListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();//去掉标题栏

        // 改变状态栏颜色
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        decorView = getWindow().getDecorView();
        contentView = findViewById(R.id.container);

        setSoftKeyBoardListener();
    }

    private View decorView;
    private View contentView;

    private SoftKeyBoardListener softKeyBoardListener;
    /**
     * 添加软键盘监听
     */
    private void setSoftKeyBoardListener() {
        softKeyBoardListener = new SoftKeyBoardListener(this);
        //软键盘状态监听
        softKeyBoardListener.setListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                Rect r = new Rect();
                decorView.getWindowVisibleDisplayFrame(r);
                if (contentView.getPaddingBottom() != height) {
                    contentView.setPadding(0, 0, 0, height);
                }
            }

            @Override
            public void keyBoardHide(int height) {
                if (contentView.getPaddingBottom() != 0) {
                    contentView.setPadding(0, 0, 0, 0);
                }
            }
        });
    }

}