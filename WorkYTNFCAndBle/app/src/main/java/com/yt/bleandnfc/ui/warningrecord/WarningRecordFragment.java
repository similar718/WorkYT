package com.yt.bleandnfc.ui.warningrecord;

import android.view.View;
import android.widget.RadioGroup;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yt.base.mvvm.model.IBaseModelListener1;
import com.yt.base.mvvm.model.PagingResult;
import com.yt.bleandnfc.R;
import com.yt.bleandnfc.api.model.AlarmFindAlarmByStateModel;
import com.yt.bleandnfc.base.fragment.YTBaseFragment;
import com.yt.bleandnfc.constant.Constants;
import com.yt.bleandnfc.databinding.FragmentWarningRecordBinding;
import com.yt.bleandnfc.eventbus.AlarmAddResult;
import com.yt.bleandnfc.mvvm.model.WarningRecordModel;
import com.yt.bleandnfc.ui.adapter.WarningRecordItemAdapter;
import com.yt.bleandnfc.ui.dialog.WarningRecordDatePickerDialog;
import com.yt.bleandnfc.ui.dialog.WarningRecordDetailDialog;
import com.yt.bleandnfc.ui.view.CommonTitleBarView;
import com.yt.bleandnfc.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

public class WarningRecordFragment extends YTBaseFragment<WarningRecordViewModel, FragmentWarningRecordBinding> implements IBaseModelListener1<List<AlarmFindAlarmByStateModel.ObjBean>> {

    @Override
    public void onLazyLoad() {

    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_warning_record;
    }

    @Override
    protected WarningRecordViewModel createViewModel() {
        viewModel = new WarningRecordViewModel();
        viewModel.setIView(this);
        return viewModel;
    }

    WarningRecordItemAdapter mAdapter;
    WarningRecordModel warningRecordModel;

    @Override
    public void initData() {
        EventBus.getDefault().register(this);

        // 月历时间
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH);
        dataBinding.tvYearMonth.setText(year + "年" + (month+1) + "月");
        dataBinding.tvRange.setText(TimeUtil.getDestinationMonth(year,month));

        // RecycleView
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

        initTimerPicker();
    }

    private int warning_type = 0;
    private int savecheckedId = R.id.tv_use_warning_type;

    private void initClick(){
        // 返回
        dataBinding.titleView.setTitleLeftClick(new CommonTitleBarView.OnTitleLeftClick() {
            @Override
            public void onLeftClick() {
                // 返回到主界面
                Navigation.findNavController(dataBinding.titleView).navigateUp();
            }
        });
        // 日历 年月
        dataBinding.rlCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示日历dialog
                mTimerPicker.show(TimeUtil.getTodayTime());
            }
        });
        // 类型点击事件处理
        dataBinding.bgWarningRecord.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (savecheckedId == checkedId) {
                    return;
                }
                switch (checkedId) {
                    case R.id.tv_use_warning_type: // 领用报警
                        warning_type = 0;
                        break;
                    case R.id.tv_timeout_warning_type: // 超时报警
                        warning_type = 1;
                        break;
                    case R.id.tv_stop_warning_type: // 停放报警
                        warning_type = 2;
                        break;
                    case R.id.tv_outofindex_warning_type: // 越界报警
                        warning_type = 3;
                        break;
                }
                // 更新列表
                warningRecordModel.refresh();
            }
        });

        mAdapter.setOnItemClickListener(new WarningRecordItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                StringBuilder stringBuilder = new StringBuilder();
                // 操作员“XXX（工号：11111）”于2020年9月24日09点25分使用工作梯（编号：xxxx）发生违规行为，违规内容“违规停放”。
                stringBuilder.append("操作员“")
                        .append(viewModels.get(position).getUserName())
                        .append("（工号：")
                        .append(viewModels.get(position).getUserId())
                        .append("）”于")
                        .append(viewModels.get(position).getCreateTime())
                        .append("使用工作梯（编号：")
                        .append(viewModels.get(position).getCarNumber())
                        .append("）发生违规行为，违规内容“违规停放”。");
                showWarningRecordDetail(stringBuilder.toString());
            }
        });
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
        Constants.mAlarmNum = viewModels.size();
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


    // setting time dialog
    private WarningRecordDatePickerDialog mTimerPicker;
    String mStartTime = "2019-08-23 19:47";
    String mEndTime = "2029-08-23 19:47";
    /**
     * init setting time dialog
     */
    private void initTimerPicker() {
        long times = System.currentTimeMillis(); //1566564746548 1565387466036
        mEndTime = TimeUtil.long2Str(times, true);
        long time = times - (1000L * 60L * 60L * 24L * 30L * 12L * 3L);
        mStartTime = TimeUtil.long2Str(time, true);

        mTimerPicker = new WarningRecordDatePickerDialog(getActivity(), new WarningRecordDatePickerDialog.Callback() {
            @Override
            public void onTimeSelected(int year, int month, long timestamp) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(year).append("年").append(month+1).append("月");
                dataBinding.tvYearMonth.setText(stringBuilder);

                dataBinding.tvRange.setText(TimeUtil.getDestinationMonth(year, month));
            }
        }, mStartTime, mEndTime);
        // click other return
        mTimerPicker.setCancelable(true);
        // show minutes and second
        mTimerPicker.setCanShowPreciseTime(false);
        mTimerPicker.setScrollLoop(true);
        mTimerPicker.setCanShowAnim(false);
        mTimerPicker.setShowText(true);
        mTimerPicker.setShowYear(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventScanData(AlarmAddResult result) {
        if (result.type == 1) {
            warningRecordModel.refresh();
        }
    }
}
