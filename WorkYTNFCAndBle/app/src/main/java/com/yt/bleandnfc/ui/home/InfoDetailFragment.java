package com.yt.bleandnfc.ui.home;

import android.annotation.SuppressLint;
import android.view.View;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yt.base.mvvm.model.IBaseModelListener1;
import com.yt.base.mvvm.model.PagingResult;
import com.yt.bleandnfc.R;
import com.yt.bleandnfc.api.model.AlarmCountAlarmByStateModel;
import com.yt.bleandnfc.api.model.AlarmFindAlarmByStateModel;
import com.yt.bleandnfc.api.model.GetUserBindByUserId;
import com.yt.bleandnfc.base.YTApplication;
import com.yt.bleandnfc.base.fragment.YTBaseFragment;
import com.yt.bleandnfc.constant.Constants;
import com.yt.bleandnfc.databinding.FragmentInfoDetailBinding;
import com.yt.bleandnfc.eventbus.AlarmAddResult;
import com.yt.bleandnfc.manager.ImageShowManager;
import com.yt.bleandnfc.manager.IntentManager;
import com.yt.bleandnfc.manager.SPManager;
import com.yt.bleandnfc.mvvm.model.WarningRecordModel;
import com.yt.bleandnfc.ui.adapter.WarningRecordItemAdapter;
import com.yt.bleandnfc.ui.dialog.BLEAndGPSHintDialog;
import com.yt.bleandnfc.ui.dialog.WarningRecordDetailDialog;
import com.yt.bleandnfc.utils.BLEAndGPSUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

public class InfoDetailFragment extends YTBaseFragment<InfoDetailViewModel, FragmentInfoDetailBinding> implements IBaseModelListener1<List<AlarmFindAlarmByStateModel.ObjBean>>,View.OnClickListener {

    @Override
    public void onLazyLoad() {

    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_info_detail;
    }

    @Override
    protected InfoDetailViewModel createViewModel() {
        viewModel = new InfoDetailViewModel(getActivity());
        viewModel.setIView(this);
        viewModel.mAlarmCountAlarmByStateModel.observe(this, new Observer<AlarmCountAlarmByStateModel>() {
            @Override
            public void onChanged(AlarmCountAlarmByStateModel alarmCountAlarmByStateModel) {
                if (alarmCountAlarmByStateModel != null) {
                    if (alarmCountAlarmByStateModel.getCode() == 200) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Constants.mAlarmNum = alarmCountAlarmByStateModel.getObj();
                                dataBinding.tvUseWarningNum.setText("" + alarmCountAlarmByStateModel.getObj());
//                                dataBinding.tvTimeoutWarningNum.setText("0");
//                                dataBinding.tvStopWarningNum.setText("0");
                            }
                        });
                    } else {
                        showToastMsg(alarmCountAlarmByStateModel.getMessage());
                    }
                } else {
                    showToastMsg("服务器请求异常");
                }
            }
        });
        viewModel.mGetUserBindByUserId.observe(this, new Observer<GetUserBindByUserId>() {
            @Override
            public void onChanged(GetUserBindByUserId getUserBindByUserId) {
                if (getUserBindByUserId != null) {
                    if (getUserBindByUserId.getCode() == 200) {
                        StringBuilder stringBuilder = new StringBuilder();
                        if (getUserBindByUserId.getObj().isBindState() == false){
                            // 没有绑定设备
                            stringBuilder.append("未绑定车辆");
                        } else {
                            int len = getUserBindByUserId.getObj().getDevices().size();
                            for (int i = 0; i < len; i++){
                                // 有绑定设备
                                stringBuilder.append("已绑定车辆")
                                        .append(getUserBindByUserId.getObj().getDevices().get(i).getNumber())
                                        .append("          ");
                            }
                        }
                        dataBinding.tvStatusInfo.setText(stringBuilder);
                    } else {
                        showToastMsg(getUserBindByUserId.getMessage());
                    }
                } else {
                    showToastMsg("服务器请求异常");
                }
            }
        });
        return viewModel;
    }

    private String imgUrl = "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1790987102,3279448956&fm=26&gp=0.jpg";

    WarningRecordItemAdapter mAdapter;
    WarningRecordModel warningRecordModel;

    @SuppressLint("SetTextI18n")
    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        // 显示用户信息 头像 用户名 用户类型 用户编号
        ImageShowManager.showImageViewToCircle(
                YTApplication.getInstance(),
                R.drawable.tab_info_detail_select,
                imgUrl,
                dataBinding.ivHead
        );
        dataBinding.tvUsername.setText(SPManager.getInstance().getName());
        dataBinding.tvType.setText(SPManager.getInstance().getDeptName());
        dataBinding.tvUserId.setText("工号：" + SPManager.getInstance().getUserId());

        // 报警number 领用报警 超时报警 停放报警
        dataBinding.tvUseWarningNum.setText("" + 0);
        dataBinding.tvTimeoutWarningNum.setText("" + 0);
        dataBinding.tvStopWarningNum.setText("" + 0);

        // 报警列表
        mAdapter = new WarningRecordItemAdapter();
        dataBinding.rvList.setHasFixedSize(true);
        dataBinding.rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        dataBinding.rvList.setAdapter(mAdapter);

        warningRecordModel = new WarningRecordModel(this);
        warningRecordModel.refresh();

        dataBinding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                warningRecordModel.refresh();
            }
        });
        dataBinding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                warningRecordModel.loadNextPage();
            }
        });

        initClick();

        viewModel.getAlarmNum();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getUserBindByUserId();
    }

    private void initClick(){
        // 设置
        dataBinding.ivSetting.setOnClickListener(this);
        // 绑定
        dataBinding.rlBind.setOnClickListener(this);
        // 解绑
        dataBinding.rlUnbind.setOnClickListener(this);
        // 维修保养
        dataBinding.rlWxby.setOnClickListener(this);
        // 维保归还
        dataBinding.rlWbgh.setOnClickListener(this);
        // 个人信息
        dataBinding.ivPersonal.setOnClickListener(this);

        mAdapter.setOnItemClickListener(new WarningRecordItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showWarningRecordDetail(viewModels.get(position).getUserName()+"(" + viewModels.get(position).getUserId() + ")",
                        viewModels.get(position).getCreateTime(),
                        "null机位,工作梯(编号：" + viewModels.get(position).getCarNumber() + ")",
                        "发生违规行为，违规内容“违规停放”。"
                        );
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_setting: // 跳转设置界面
//                Navigation.findNavController(v).navigate(R.id.navigation_settings);
                IntentManager.getInstance().goLoginActivity(getActivity());
                break;
            case R.id.rl_bind: // 绑定
                if (!BLEAndGPSUtils.isOpenBLE()) {
                    // 蓝牙没有打开
                    showBleAndGPSHintDialog("请打开蓝牙，可正常使用APP内的功能",false);
                } else if (!BLEAndGPSUtils.isOpenGPS(YTApplication.getInstance())){
                    showBleAndGPSHintDialog("请打开GPS，可正常使用APP内的功能",false);
                } else {
                    IntentManager.getInstance().goNextStepScanActivity(getActivity(), 1);
                }
                break;
            case R.id.rl_unbind: // 解绑
                if (!BLEAndGPSUtils.isOpenBLE()) {
                    // 蓝牙没有打开
                    showBleAndGPSHintDialog("请打开蓝牙，可正常使用APP内的功能",false);
                } else if (!BLEAndGPSUtils.isOpenGPS(YTApplication.getInstance())){
                    showBleAndGPSHintDialog("请打开GPS，可正常使用APP内的功能",false);
                } else {
                    IntentManager.getInstance().goNextStepScanActivity(getActivity(), 3);
                }
                break;
            case R.id.rl_wxby: // 维修保养
                Navigation.findNavController(v).navigate(R.id.navigation_wb);
                break;
            case R.id.iv_personal: // 个人信息
                Navigation.findNavController(v).navigate(R.id.navigation_personal);
                break;
            case R.id.rl_wbgh: // 维保归还
                if (!BLEAndGPSUtils.isOpenBLE()) {
                    // 蓝牙没有打开
                    showBleAndGPSHintDialog("请打开蓝牙，可正常使用APP内的功能",false);
                } else if (!BLEAndGPSUtils.isOpenGPS(YTApplication.getInstance())){
                    showBleAndGPSHintDialog("请打开GPS，可正常使用APP内的功能",false);
                } else {
                    IntentManager.getInstance().goNextStepScanActivity(getActivity(), 4);
                }
                break;
        }
    }

    private WarningRecordDetailDialog mWarningRecordDetail;

    private void showWarningRecordDetail(String content,String content2,String content3,String content4){
        if (mWarningRecordDetail == null) {
            mWarningRecordDetail = new WarningRecordDetailDialog(getActivity());
        }
        mWarningRecordDetail.showDialog(content, content2, content3, content4);
    }

    private List<AlarmFindAlarmByStateModel.ObjBean> viewModels = new ArrayList<>();

    @Override
    public void onLoadSuccess(List<AlarmFindAlarmByStateModel.ObjBean> baseCustomViewModels, PagingResult... results) {
        if(results != null && results.length > 0) {
            if (results[0].isFirstPage) {
                viewModels.clear();
            }
            if (!results[0].hasNextPage) {
                dataBinding.refreshLayout.setNoMoreData(true);
            } else {
                dataBinding.refreshLayout.setNoMoreData(false);
            }
        }
        viewModels.addAll(baseCustomViewModels);
        mAdapter.setData(viewModels);
        dataBinding.refreshLayout.finishRefresh();
        dataBinding.refreshLayout.finishLoadMore();
    }

    @Override
    public void onLoadFail(String message) {
        showToastMsg(message);
        dataBinding.refreshLayout.finishRefresh();
        dataBinding.refreshLayout.finishLoadMore();
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

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventScanData(AlarmAddResult result) {
        if (result.type == 1) {
            warningRecordModel.refresh();
            viewModel.getAlarmNum();
        }
    }
}
