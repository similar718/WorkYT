/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yt.bleandnfc.ui.zxing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.yt.base.utils.LogUtlis;
import com.yt.base.utils.ToastUtils;
import com.yt.bleandnfc.R;
import com.yt.bleandnfc.api.model.BindModel;
import com.yt.bleandnfc.api.model.CarNumberInfoModel;
import com.yt.bleandnfc.base.activity.YTBaseActivity;
import com.yt.bleandnfc.constant.Constants;
import com.yt.bleandnfc.databinding.ZxingActivityCaptureBinding;
import com.yt.bleandnfc.eventbus.ScanResult;
import com.yt.bleandnfc.keyboard.SoftKeyBoardListener;
import com.yt.bleandnfc.manager.SPManager;
import com.yt.bleandnfc.mvvm.viewmodel.InputActivateCodeViewModel;
import com.yt.bleandnfc.nfcres.NfcHandler;
import com.yt.bleandnfc.nfcres.NfcView;
import com.yt.bleandnfc.ui.dialog.ScanResultDialog;
import com.yt.bleandnfc.ui.view.CommonTitleBarView;
import com.yt.bleandnfc.ui.view.QrcodeViewfinderView;
import com.yt.bleandnfc.ui.zxing.camera.CameraManager;
import com.yt.bleandnfc.ui.zxing.history.HistoryActivity;
import com.yt.bleandnfc.ui.zxing.history.HistoryItem;
import com.yt.bleandnfc.ui.zxing.history.HistoryManager;
import com.yt.bleandnfc.ui.zxing.share.ShareActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a
 * viewfinder to help the user place the barcode correctly, shows feedback as the image processing
 * is happening, and then overlays the results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends YTBaseActivity<InputActivateCodeViewModel, ZxingActivityCaptureBinding> implements SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;
    private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;

    private static final String[] ZXING_URLS = {"http://zxing.appspot.com/scan", "zxing://scan/"};

    private static final int HISTORY_REQUEST_CODE = 0x0000bacc;

    private static final Collection<ResultMetadataType> DISPLAYABLE_METADATA_TYPES =
            EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
                    ResultMetadataType.SUGGESTED_PRICE,
                    ResultMetadataType.ERROR_CORRECTION_LEVEL,
                    ResultMetadataType.POSSIBLE_COUNTRY);

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private Result savedResultToShow;
    private QrcodeViewfinderView viewfinderView;
    private Result lastResult;
    private boolean hasSurface;
    private boolean copyToClipboard;
    private IntentSource source;
    private String sourceUrl;
    private ScanFromWebPageManager scanFromWebPageManager;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;
    private HistoryManager historyManager;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private boolean mIsStop = false; // 界面是否没有关闭 只是回到了主界面 然后再返回的操作

    private String mCodeNumber = "";

    QrcodeViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // 保持Activity处于唤醒状态
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mIsStop = false;
    }

    private String mCarName = "";

    @Override
    protected int setLayoutId() {
        return R.layout.zxing_activity_capture;
    }

    @Override
    protected InputActivateCodeViewModel createViewModel() {
        viewModel = new InputActivateCodeViewModel((Activity) mContext);
        viewModel.setIView(this);
        // 扫描结果的数据请求
        viewModel.mCarNumberInfo.observe(this, new Observer<CarNumberInfoModel>() {
            @Override
            public void onChanged(CarNumberInfoModel carNumberInfoModel) {
                if (carNumberInfoModel != null){
                    if (carNumberInfoModel.getCode() == 200){
                        // 显示子框口
                        mCarName = carNumberInfoModel.getObj().getCarNumber();
                        showScanResultDialog(mCarName,
                                carNumberInfoModel.getObj().getDeptName(),
                                carNumberInfoModel.getObj().getDeviceBind() == null ? "未绑定" : "已绑定",
                                "正常",
                                carNumberInfoModel.getObj().getNumber());
                    } else {
                        showToastMsg(carNumberInfoModel.getMessage());
                    }
                } else {
                    showToastMsg("数据异常");
                }
            }
        });
        // 绑定和解绑
        viewModel.mBind.observe(this, new Observer<BindModel>() {
            @Override
            public void onChanged(BindModel bindModel) {
                if (bindModel != null){
                    if (bindModel.getCode() == 100 && mType == 1){ // 绑定成功
                        SPManager.getInstance().setCarNum(mCodeNumber);
                        Constants.mBindLists.add(mCarName);
                        showToastMsg("绑定成功");
                    } else if (bindModel.getCode() == 102 && mType == 3){ // 解绑成功
                        SPManager.getInstance().setCarNum("");
                        Constants.mBindLists.remove(mCarName);
                        showToastMsg("解绑成功");
                    } else {
                        if (bindModel.getCode() == 101) {

                        } else if (bindModel.getCode() == 103) {

                        }
                        showToastMsg(bindModel.getMessage());
                    }
                    finish();
                } else {
                    showToastMsg("数据异常");
                }
            }
        });
        return viewModel;
    }

    private int mType = 1;

    @Override
    protected void initData() {
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        mType = getIntent().getIntExtra("type",1);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        dataBinding.titleBar.setTitleLeftClick(new CommonTitleBarView.OnTitleLeftClick() {
            @Override
            public void onLeftClick() {
                finish();
            }
        });

        // 初始化NFC数据
        mNfcHandler = new NfcHandler(mNFCView);
        mNfcHandler.init(this);

        // 1 绑定  2 维修保养  3 解绑  4 维保归还
        if (mType == 1) {
            dataBinding.titleBar.setTitleText("扫码绑定/领用");
        } else if (mType == 2) {
            dataBinding.titleBar.setTitleText("扫码维修保养");
        } else if (mType == 3) {
            dataBinding.titleBar.setTitleText("扫码解绑/归还");
        } else if (mType == 4) {
            dataBinding.titleBar.setTitleText("扫码维保归还");
        }

        // 对输入框的监听
        dataBinding.etScanInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = s.toString().trim().length();
                if (count > 0) {
                    dataBinding.tvScanInputSure.setEnabled(true);
                } else {
                    dataBinding.tvScanInputSure.setEnabled(false);
                }
            }
        });

        dataBinding.tvScanInputSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = dataBinding.etScanInput.getText().toString().trim();
                if (!TextUtils.isEmpty(data)) {
                    // 扫码知道数据信息
                    // 扫码知道数据信息
                    mCodeNumber = data;
                    if (mType == 1 || mType == 3) { // 绑定  解绑
                        viewModel.getCarNumberInfo(mCodeNumber);
                    } else if (mType == 2) { // 维修保养
                        EventBus.getDefault().post(new ScanResult(mType, mCodeNumber));
                        finish();
                    } else if (mType == 4) { // 维保归还

                    }
                } else {
                    showToastMsg("请输入车辆编号");
                }
            }
        });

        dataBinding.rlLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasFlash()) {
                    cameraManager.openFlash();
                } else {
                    showToastMsg("当前设备不支持手电筒");
                }
            }
        });

        dataBinding.rlFrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsClickFRID = true;
                if (!TextUtils.isEmpty(mNFCContent)) {
                    dataBinding.etScanInput.setText(mNFCContent);
                } else {
                    if (mIsOpenNFC) { // 如果出现就开始获取nfc数据
                        getNFCInfo();
                    } else {
                        showToastMsg("使用当前功能，需要打开NFC功能");
                        finish();
                    }
                }
            }
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        // historyManager must be initialized here to update the history preference
        historyManager = new HistoryManager(this);
        historyManager.trimHistory();

        // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
        // want to open the camera driver and measure the screen size if we're going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());

        viewfinderView = (QrcodeViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);


        handler = null;

        beepManager.updatePrefs();
        inactivityTimer.onResume();


        source = IntentSource.NONE;
        decodeFormats = null;
        characterSet = null;

        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();

//        if (!mIsStop) {
//            if (hasSurface) {
//                LogUtils.e(TAG, " hasSurface = " + hasSurface + " onResume _ing if");
//                // The activity was paused but not stopped, so the surface still exists. Therefore
//                // surfaceCreated() won't be called, so init the camera here.
//                initCamera(surfaceHolder);
//            } else {
//                LogUtils.e(TAG, " hasSurface = " + hasSurface + " onResume _ing else");
//                // TODO 添加的代码 打印log发现会调用两次onResume的生命周期，如果两次都是false
//                //  界面就没有初始化成功 所以 如果第一次是false的话 将标志改为true，测试 100 次没有出现创建不成功的情况
//                hasSurface = true;
//                // Install the callback and wait for surfaceCreated() to init the camera.
//                surfaceHolder.addCallback(this);
//            }
//        } else {
            if (hasSurface) {
                // The activity was paused but not stopped, so the surface still exists. Therefore
                // surfaceCreated() won't be called, so init the camera here.
                initCamera(surfaceHolder);
            } else {
                // Install the callback and wait for surfaceCreated() to init the camera.
                surfaceHolder.addCallback(this);
            }
//        }

        mNfcHandler.enableNfc((Activity) mContext);
    }

    SurfaceView surfaceView = null;
    SurfaceHolder surfaceHolder = null;


    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        //historyManager = null; // Keep for onActivityResult

        if (!hasSurface) {
            if (surfaceHolder != null) {
                LogUtlis.e(TAG," hasSurface = " + hasSurface + " onPause _ing if");
                surfaceHolder.removeCallback(this);
            }
        }
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mIsStop = true;
    }


    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (source == IntentSource.NATIVE_APP_INTENT) {
                    setResult(RESULT_CANCELED);
                    finish();
                    return true;
                }
                if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
                    restartPreviewAfterDelay(0L);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_FOCUS:
            case KeyEvent.KEYCODE_CAMERA:
                // Handle these events so they don't launch the Camera app
                return true;
            // Use volume up/down to turn on light
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                cameraManager.setTorch(false);
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                cameraManager.setTorch(true);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.zxing_capture, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intents.FLAG_NEW_DOC);
        switch (item.getItemId()) {
            case R.id.menu_share:
                intent.setClassName(this, ShareActivity.class.getName());
                startActivity(intent);
                break;
            case R.id.menu_history:
                intent.setClassName(this, HistoryActivity.class.getName());
                startActivityForResult(intent, HISTORY_REQUEST_CODE);
                break;
            case R.id.menu_settings:
                intent.setClassName(this, PreferencesActivity.class.getName());
                startActivity(intent);
                break;
            case R.id.menu_help:
                intent.setClassName(this, HelpActivity.class.getName());
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK && requestCode == HISTORY_REQUEST_CODE && historyManager != null) {
            int itemNumber = intent.getIntExtra(Intents.History.ITEM_NUMBER, -1);
            if (itemNumber >= 0) {
                HistoryItem historyItem = historyManager.buildHistoryItem(itemNumber);
                decodeOrStoreSavedBitmap(null, historyItem.getResult());
            }
        }
    }

    private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
        // Bitmap isn't used yet -- will be used soon
        if (handler == null) {
            savedResultToShow = result;
        } else {
            if (result != null) {
                savedResultToShow = result;
            }
            if (savedResultToShow != null) {
                Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
                handler.sendMessage(message);
            }
            savedResultToShow = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            LogUtlis.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // do nothing
    }

    /**
     * A valid barcode has been found, so give an indication of success and show the results.
     *
     * @param rawResult   The contents of the barcode.
     * @param scaleFactor amount by which thumbnail was scaled
     * @param barcode     A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();
        lastResult = rawResult;
        // ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);

        boolean fromLiveScan = barcode != null;
        if (fromLiveScan) {
            //historyManager.addHistoryItem(rawResult, resultHandler);
            // Then not from history, so beep/vibrate and we have an image to draw on
            beepManager.playBeepSoundAndVibrate();
            //drawResultPoints(barcode, scaleFactor, rawResult);
            CharSequence displayContents = rawResult.getText();
            LogUtlis.d(TAG, "解析结果 " + displayContents);

            mCodeNumber = (String) displayContents;
            //提交服务器
            if (mType == 1 || mType == 3) { // 绑定  解绑
                viewModel.getCarNumberInfo(mCodeNumber);
            } else if (mType == 2){ // 维修保养
                EventBus.getDefault().post(new ScanResult(mType,mCodeNumber));
                finish();
            } else if (mType == 4) { // 维保归还

            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            LogUtlis.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
            }
            //decodeOrStoreSavedBitmap(null, null);
        } catch (IOException ioe) {
            LogUtlis.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            LogUtlis.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
        }
        resetStatusView();
    }

    private void resetStatusView() {
        viewfinderView.setVisibility(View.VISIBLE);
        lastResult = null;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    private ScanResultDialog mScanResultDialog;

    /**
     * 显示扫描结果界面
     * @param str1
     * @param str2
     * @param str3
     * @param str4
     */
    private void showScanResultDialog(String str1,String str2,String str3,String str4,String number){
        if (mScanResultDialog == null) {
            mScanResultDialog = new ScanResultDialog(mContext);
            mScanResultDialog.setScanResultClicklistener(new ScanResultDialog.ScanResultClickListenerInterface() {
                @Override
                public void doSure() {
                    if (mType == 1) { // 绑定
                        viewModel.optionBindAndUnBind(
                                SPManager.getInstance().getUserId(),
                                number,
                                true,
                                true,
                                Constants.LOCATION_LNG,
                                Constants.LOCATION_LNG_TYPE,
                                Constants.LOCATION_LAT,
                                Constants.LOCATION_LAT_TYPE,
                                Constants.LOCATION_SATELLIE
                        );
                    } else if (mType == 3) { // 解绑
                        viewModel.optionBindAndUnBind(
                                SPManager.getInstance().getUserId(),
                                number,
                                false,
                                true,
                                Constants.LOCATION_LNG,
                                Constants.LOCATION_LNG_TYPE,
                                Constants.LOCATION_LAT,
                                Constants.LOCATION_LAT_TYPE,
                                Constants.LOCATION_SATELLIE
                        );
                    }
                }

                @Override
                public void cancel() {
                    restartCamera();
                }
            });
        }
        mScanResultDialog.showDialog(str1, str2, str3, str4, mType);
    }

    private boolean hasFlash(){
        return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }


    @Override
    protected void onNewIntent(Intent intent) { // TODO nfc必须要使用的
        Log.d(TAG, "onNewIntent()! action is:" + intent.getAction());
        super.onNewIntent(intent);
        setIntent(intent);
    }

    // NFC 硬件相关东西
    private NfcHandler mNfcHandler;
    private boolean mIsRequestNFCUid = false;
    private boolean mIsOpenNFC = true;

    private String mNFCContent = "";
    private boolean mIsClickFRID = false;


    private NfcView mNFCView = new NfcView() {
        @Override
        public void appendResponse(final String response) {
            Log.e(TAG, "appendResponse: data______________________________" + response);
            // NFC相关信息的回调事件
            if (TextUtils.isEmpty(response)){
                return;
            }
            mIsRequestNFCUid = true; // 从线程中读取到NFC的相关数据
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(1000); // 获取成功只有震动1秒的钟
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showText(mContext,response);
                    StringBuilder data = new StringBuilder();
                    data.append("NFC相关信息：").append(response);
                    mNFCContent = response;
                    if (mIsClickFRID) {
                        dataBinding.etScanInput.setText(mNFCContent);
                    }
                }
            });
        }

        @Override
        public void notNfcDevice() {
            Toast.makeText(mContext, "未找到NFC设备！", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void notOpenNFC() {
            mIsOpenNFC = false;
            mHandler.sendEmptyMessage(HANDLER_INIT_IMAGEVIEW_NFC);
            Toast.makeText(mContext, "请在设置中打开NFC开关！", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void getNFCStatusOk() {
            mIsOpenNFC = true;
            mHandler.sendEmptyMessage(HANDLER_INIT_IMAGEVIEW_NFC);
        }
    };

    // 开始查看NFC是否被读取
    private void getNFCInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!mIsRequestNFCUid) {
                    try {
                        Thread.sleep(1000);
                        // 循环读取数据
                        mNfcHandler.readCardId(getIntent());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private final int HANDLER_INIT_IMAGEVIEW_NFC = 0x0102;

    // 主线程的Handler用来刷新界面
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void handleMessage(@NonNull Message msg) {
            // 由主线程中的Looper不断的loop将handler里面的信息不断的轮询，将符合要求的数据dispatchMessage分发
            // 到主线程的handlerMessage进行更新界面的数据
            switch (msg.what){
                case HANDLER_INIT_IMAGEVIEW_NFC:
                    if (mIsOpenNFC) { // 如果出现就开始获取nfc数据
                        getNFCInfo();
                    }
                    break;
            }
        }
    };

    void restartCamera(){
        historyManager = new HistoryManager(this);
        historyManager.trimHistory();

        cameraManager = new CameraManager(getApplication());

        viewfinderView = (QrcodeViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);

        handler = null;

        beepManager.updatePrefs();
        inactivityTimer.onResume();

        source = IntentSource.NONE;
        decodeFormats = null;
        characterSet = null;

        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();

        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
        }
    }
}
