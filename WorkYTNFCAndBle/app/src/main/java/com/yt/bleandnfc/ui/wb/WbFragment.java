package com.yt.bleandnfc.ui.wb;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.yt.base.utils.LogUtlis;
import com.yt.bleandnfc.R;
import com.yt.bleandnfc.base.YTApplication;
import com.yt.bleandnfc.base.fragment.YTBaseFragment;
import com.yt.bleandnfc.constant.Constants;
import com.yt.bleandnfc.databinding.FragmentWbBinding;
import com.yt.bleandnfc.eventbus.ScanResult;
import com.yt.bleandnfc.manager.ImageShowManager;
import com.yt.bleandnfc.manager.IntentManager;
import com.yt.bleandnfc.ui.dialog.BLEAndGPSHintDialog;
import com.yt.bleandnfc.ui.view.CommonTitleBarView;
import com.yt.bleandnfc.utils.BLEAndGPSUtils;
import com.yt.bleandnfc.utils.GetImagePath;
import com.yt.bleandnfc.utils.ImageUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.navigation.Navigation;

import static android.app.Activity.RESULT_CANCELED;

public class WbFragment extends YTBaseFragment<WBViewModel, FragmentWbBinding> implements View.OnClickListener {

    @Override
    public void onLazyLoad() {

    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_wb;
    }

    @Override
    protected WBViewModel createViewModel() {
        viewModel = new WBViewModel();
        viewModel.setIView(this);
        return viewModel;
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);

        // 对输入框的监听
        dataBinding.etCarTop.addTextChangedListener(new TextWatcher() {
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
                    dataBinding.tvSureCommit.setEnabled(true);
                } else {
                    dataBinding.tvSureCommit.setEnabled(false);
                }
            }
        });

        initClick();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    private void initClick() {
        // 返回
        dataBinding.titleView.setTitleLeftClick(new CommonTitleBarView.OnTitleLeftClick() {
            @Override
            public void onLeftClick() {
                // 返回到主界面
                Navigation.findNavController(dataBinding.titleView).navigateUp();
            }
        });
        // 扫码
        dataBinding.rlScan.setOnClickListener(this);
        // 类型选择
        dataBinding.rb1.setOnClickListener(this);
        dataBinding.rb2.setOnClickListener(this);
        dataBinding.rb3.setOnClickListener(this);
        dataBinding.rb4.setOnClickListener(this);
        // 确认提交
        dataBinding.tvSureCommit.setOnClickListener(this);
        // 添加图片
        dataBinding.ivImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_scan: // 扫码
                if (!BLEAndGPSUtils.isOpenBLE()) {
                    // 蓝牙没有打开
                    showBleAndGPSHintDialog("请打开蓝牙，可正常使用APP内的功能",false);
                } else if (!BLEAndGPSUtils.isOpenGPS(YTApplication.getInstance())){
                    showBleAndGPSHintDialog("请打开GPS，可正常使用APP内的功能",false);
                } else {
                    IntentManager.getInstance().goNextStepScanActivity(getActivity(), 2);
                }
                break;
            case R.id.tv_sure_commit: // 确认提交
                break;
            case R.id.rb_1:
                dataBinding.rb1.setChecked(true);
                dataBinding.rb2.setChecked(false);
                dataBinding.rb3.setChecked(false);
                dataBinding.rb4.setChecked(false);
                break;
            case R.id.rb_2:
                dataBinding.rb1.setChecked(false);
                dataBinding.rb2.setChecked(true);
                dataBinding.rb3.setChecked(false);
                dataBinding.rb4.setChecked(false);
                break;
            case R.id.rb_3:
                dataBinding.rb1.setChecked(false);
                dataBinding.rb2.setChecked(false);
                dataBinding.rb3.setChecked(true);
                dataBinding.rb4.setChecked(false);
                break;
            case R.id.rb_4:
                dataBinding.rb1.setChecked(false);
                dataBinding.rb2.setChecked(false);
                dataBinding.rb3.setChecked(false);
                dataBinding.rb4.setChecked(true);
                break;
            case R.id.iv_img: // 选择本地或者拍照图片
                requestPermission(CAMERA_PERMISSIONS, 0x0003);//请求拍照权限
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventScanData(ScanResult result) {
        if (result.type == 2) {
            dataBinding.etCarTop.setText(result.content);
        }
    }

    String[] CAMERA_PERMISSIONS = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    private int REQUEST_CODE_PERMISSION = 0x00099;
    /**
     * 请求权限
     *
     * @param permissions 请求的权限
     * @param requestCode 请求权限的请求码
     */
    public void requestPermission(String[] permissions, int requestCode) {
        // 当API大于 23 时，才动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.REQUEST_CODE_PERMISSION = requestCode;
            if (checkPermissions(permissions)) {
                permissionSuccess(REQUEST_CODE_PERMISSION);
            } else {
                List<String> needPermissions = getDeniedPermissions(permissions);
                ActivityCompat.requestPermissions(getActivity(), needPermissions.toArray(new String[needPermissions.size()]), REQUEST_CODE_PERMISSION);
            }
        }
    }
    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     */
    public List<String> getDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) !=
                    PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                needRequestPermissionList.add(permission);
            }
        }
        return needRequestPermissionList;
    }
    /**
     * 权限成功回调函数
     *
     * @param requestCode
     */
    public void permissionSuccess(int requestCode) {
        // 获取权限成功
        if (requestCode == 0x0003) {
            setHeadPortrait();
        }
    }
    /**
     * 检测所有的权限是否都已授权
     *
     * @param permissions
     * @return
     */
    public boolean checkPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    AlertDialog headPortraitDialog = null;
    /**
     * 设置头像dialog
     */
    public void setHeadPortrait() {

        headPortraitDialog = new AlertDialog.Builder(getActivity(), R.style.SetHeadPortraitDialog).create();//创建AlertDialog
        headPortraitDialog.show();
        headPortraitDialog.getWindow().setContentView(R.layout.dialog_set_head_portrait_layout);//设置AlertDialog的布局文件

        headPortraitDialog.getWindow().setGravity(Gravity.BOTTOM); //显示在底部
        WindowManager m = headPortraitDialog.getWindow().getWindowManager();//获取AlertDialog窗口
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = headPortraitDialog.getWindow().getAttributes();
        p.width = d.getWidth(); //设置dialog的宽度为当前手机屏幕的宽度
        headPortraitDialog.getWindow().setAttributes(p);
        headPortraitDialog.setCanceledOnTouchOutside(true);//设置点击空白处dialog消失
        headPortraitDialog.getWindow().findViewById(R.id.camera_txt).setOnClickListener(new WbFragment.HeadPortraitClickListener());
        headPortraitDialog.getWindow().findViewById(R.id.from_gallery_txt).setOnClickListener(new WbFragment.HeadPortraitClickListener());
        headPortraitDialog.getWindow().findViewById(R.id.cancel_txt).setOnClickListener(new WbFragment.HeadPortraitClickListener());
    }

    String headCropPath = Environment.getExternalStorageDirectory() + "/"+ Constants.FILE_NAME+"/head/head_crop.jpg";//裁剪后头像的路径
    String cameraPath = Environment.getExternalStorageDirectory() + "/"+Constants.FILE_NAME+"/head/head_camera.jpg";//保存拍照头像的路径

    private Uri imageUri;//拍照后照片地址
    private Uri imageCropUri = null;//裁剪后图片地址
    // 请求识别码
    public static final int GALLERY = 0xa1;//相册
    public static final int CAMERA = 0xa2;//相机

    class HeadPortraitClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.camera_txt://拍照
                    if (ImageUtil.hasSdcard()) {
                        ImageUtil.createSDCardDir();
                        File file = new File(cameraPath);
                        imageUri = Uri.fromFile(file);
                        File cropFile = new File(headCropPath);
                        imageCropUri = Uri.fromFile(cropFile);
                    }
                    choseHeadImageFromCameraCapture();
                    headPortraitDialog.dismiss();
                    break;
                case R.id.from_gallery_txt://图库
                    if (ImageUtil.hasSdcard()) {
                        ImageUtil.createSDCardDir();
                        File cropFile = new File(headCropPath);
                        imageCropUri = Uri.fromFile(cropFile);
                    }
                    choseHeadImageFromGallery();
                    headPortraitDialog.dismiss();
                    break;
                case R.id.cancel_txt://取消
                    headPortraitDialog.dismiss();
                    break;
            }
        }
    }

    // 启动手机相机拍摄照片作为头像
    private void choseHeadImageFromCameraCapture() {
        // 判断存储卡是否可用，存储照片文件
        if (ImageUtil.hasSdcard()) {
            ImageUtil.createSDCardDir();
            Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri = getUriForFile(getActivity(), new File(cameraPath));
            intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intentFromCapture, CAMERA);
        }
    }

    // 从本地相册选取图片作为头像
    private void choseHeadImageFromGallery() {
        Intent intentFromGallery = new Intent();
        // 设置文件类型
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_PICK);
        startActivityForResult(intentFromGallery, GALLERY);
    }

    private static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), YTApplication.getInstance().FILE_PROVIDER, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0x0003:
                //权限请求失败
                if (grantResults.length == CAMERA_PERMISSIONS.length) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            //弹出对话框引导用户去设置
                            showDialog();
                            showToastMsg("请求权限被拒绝");
                            break;
                        }
                    }
                }else{
                    showToastMsg("已授权");
                    permissionSuccess(0x0003);
                }
                break;
        }
    }

    //弹出提示框
    private void showDialog(){
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage("头像需要相机和读写权限，是否去设置？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        IntentManager.getInstance().goToAppSetting(mContext);
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private String TAG = WbFragment.class.getSimpleName();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            LogUtlis.d(TAG, "RESULT_CANCELED==========");
            return;
        }
        switch (requestCode) {
            case GALLERY://相册
                if (data == null) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    File imgUri = new File(GetImagePath.getPath(getActivity().getApplicationContext(), data.getData()));
                    Uri dataUri = null;
                    try {
                        dataUri = FileProvider.getUriForFile(YTApplication.getInstance(), YTApplication.getInstance().FILE_PROVIDER, imgUri);
                    } catch (IllegalArgumentException i) {
                        i.printStackTrace();
                    }
                    if (dataUri != null) {
                        ImageShowManager.showImageView(getActivity(),R.drawable.add_img,dataUri,dataBinding.ivImg);
                    }

                } else {
                    ImageShowManager.showImageView(getActivity(),R.drawable.add_img,data.getData(),dataBinding.ivImg);
                }
                break;

            case CAMERA://相机
                ImageShowManager.showImageView(getActivity(),
                        R.drawable.add_img,
                        FileProvider.getUriForFile(getActivity().getApplicationContext(), YTApplication.getInstance().FILE_PROVIDER, new File(cameraPath)),
                        dataBinding.ivImg);
                break;
        }
    }

    private BLEAndGPSHintDialog mBLEAndGPSHintDialog;

    private void showBleAndGPSHintDialog(String title,boolean isPermissionHint){
        if (mBLEAndGPSHintDialog == null) {
            mBLEAndGPSHintDialog = new BLEAndGPSHintDialog(getActivity());
            mBLEAndGPSHintDialog.setBLEAndGPSHintClicklistener(new BLEAndGPSHintDialog.BLEAndGPSHintClickListenerInterface() {
                @Override
                public void doSure() {
                    if (isPermissionHint) {
                        IntentManager.getInstance().goToAppSetting(getActivity());
                    } else {

                    }
                }
            });
        }
        mBLEAndGPSHintDialog.showDialog(title, isPermissionHint);
    }

}
