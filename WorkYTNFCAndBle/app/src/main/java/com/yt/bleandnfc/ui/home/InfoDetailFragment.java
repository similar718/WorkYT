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
        StringBuilder stringBuilder = new StringBuilder();
        if (Constants.mBindLists.size() > 0) {
            for (int i = 0; i < Constants.mBindLists.size(); i++){
                stringBuilder.append("已绑定车辆").append(Constants.mBindLists.get(i)).append("          ");
            }
        } else {
            stringBuilder.append("未绑定车辆");
        }
        dataBinding.tvStatusInfo.setText(stringBuilder);
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
                StringBuilder stringBuilder = new StringBuilder();
                // 操作员“XXX（工号：11111）”于2020年9月24日09点25分使用工作梯（编号：xxxx）发生违规行为，违规内容“违规停放”。
//                stringBuilder.append("操作员“")
//                        .append(viewModels.get(position).getUserName())
//                        .append("（工号：")
//                        .append(viewModels.get(position).getUserId())
//                        .append("）”于")
//                        .append(viewModels.get(position).getCreateTime())
//                        .append("在null机位")
//                        .append("使用工作梯（编号：")
//                        .append(viewModels.get(position).getCarNumber())
//                        .append("）发生违规行为，违规内容“违规停放”。");
                stringBuilder
                        .append(viewModels.get(position).getUserName())
                        .append("（")
                        .append(viewModels.get(position).getUserId())
                        .append("）\n")
                        .append(viewModels.get(position).getCreateTime())
                        .append("\nnull机位,工作梯（编号：")
                        .append(viewModels.get(position).getCarNumber())
                        .append("）\n发生违规行为，违规内容“违规停放”。");
                showWarningRecordDetail(stringBuilder.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_setting: // 跳转设置界面
                Navigation.findNavController(v).navigate(R.id.navigation_settings);
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

    private void showWarningRecordDetail(String content){
        if (mWarningRecordDetail == null) {
            mWarningRecordDetail = new WarningRecordDetailDialog(getActivity());
        }
        mWarningRecordDetail.showDialog(content);
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
