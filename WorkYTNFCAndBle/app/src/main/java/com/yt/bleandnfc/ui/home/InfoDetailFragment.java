package com.yt.bleandnfc.ui.home;

import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yt.base.mvvm.model.IBaseModelListener;
import com.yt.base.mvvm.model.PagingResult;
import com.yt.bleandnfc.R;
import com.yt.bleandnfc.base.YTApplication;
import com.yt.bleandnfc.base.fragment.YTBaseFragment;
import com.yt.bleandnfc.databinding.FragmentInfoDetailBinding;
import com.yt.bleandnfc.manager.ImageShowManager;
import com.yt.bleandnfc.manager.IntentManager;
import com.yt.bleandnfc.manager.SPManager;
import com.yt.bleandnfc.mvvm.model.WarningRecordModel;
import com.yt.bleandnfc.mvvm.viewmodel.WarningRecordItemViewModel;
import com.yt.bleandnfc.ui.adapter.WarningRecordItemAdapter;
import com.yt.bleandnfc.ui.dialog.WarningRecordDetailDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

public class InfoDetailFragment extends YTBaseFragment<InfoDetailViewModel, FragmentInfoDetailBinding> implements IBaseModelListener<List<WarningRecordItemViewModel>>,View.OnClickListener {

    @Override
    public void onLazyLoad() {

    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_info_detail;
    }

    @Override
    protected InfoDetailViewModel createViewModel() {
        viewModel = new InfoDetailViewModel();
        viewModel.setIView(this);
        return viewModel;
    }

    private String imgUrl = "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1790987102,3279448956&fm=26&gp=0.jpg";

    WarningRecordItemAdapter mAdapter;
    WarningRecordModel warningRecordModel;

    @Override
    public void initData() {
        // 显示用户信息 头像 用户名 用户类型 用户编号
        ImageShowManager.showImageViewToCircle(
                YTApplication.getInstance(),
                R.drawable.tab_info_detail_select,
                imgUrl,
                dataBinding.ivHead
        );
        dataBinding.tvUsername.setText("用户名"+ SPManager.getInstance().getUserName());
        dataBinding.tvType.setText("机务部");
        dataBinding.tvUserId.setText("工号：AK-1999");

        // 报警number 领用报警 超时报警 停放报警
        dataBinding.tvUseWarningNum.setText("" + 10);
        dataBinding.tvTimeoutWarningNum.setText("" + 10);
        dataBinding.tvStopWarningNum.setText("" + 10);

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
        setTextSwitcher();
        setData();
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

        mAdapter.setOnItemClickListener(new WarningRecordItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showWarningRecordDetail(viewModels.get(position).contentStr);
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
                IntentManager.getInstance().goNextStepScanActivity(getActivity(),1);
                break;
            case R.id.rl_unbind: // 解绑 TODO
                IntentManager.getInstance().goNextStepScanActivity(getActivity(),3);
                break;
            case R.id.rl_wxby: // 维修保养
                Navigation.findNavController(v).navigate(R.id.navigation_wb);
                break;
            case R.id.rl_wbgh: // 维保归还 TODO
//                showWarningRecordDetail("操作员“XXX（工号：11111）”于2020年9月24日09点25分使用工作梯（编号：xxxx）发生违规行为，违规内容“违规停放”。\n 返回到主界面");
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

    private List<WarningRecordItemViewModel> viewModels = new ArrayList<>();

    @Override
    public void onLoadSuccess(List<WarningRecordItemViewModel> baseCustomViewModels, PagingResult... results) {
        if(results != null && results.length > 0 && results[0].isFirstPage) {
            viewModels.clear();
        }
        viewModels.addAll(baseCustomViewModels);
        mAdapter.setData(viewModels);
        dataBinding.refreshLayout.finishRefresh();
        dataBinding.refreshLayout.finishLoadMore();
    }

    @Override
    public void onLoadFail(String message) {
        showToastMsg(message);
    }

    private int index = 0;//textview上下滚动下标
    private Handler handler = new Handler();
    private boolean isFlipping = false; // 是否启用预警信息轮播
    private List<String> mWarningTextList = new ArrayList<>();

    private void setTextSwitcher() {
        dataBinding.textSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_bottom));
        dataBinding.textSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_top));
        dataBinding.textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(getActivity());
                textView.setTextSize(11);//字号
                textView.setTextColor(Color.parseColor("#333333"));
                textView.setGravity(Gravity.CENTER);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                textView.setLayoutParams(params);
                textView.setPadding(10, 0, 0, 10);
                return textView;
            }
        });
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isFlipping) {
                return;
            }
            index++;
            dataBinding.textSwitcher.setText(mWarningTextList.get(index % mWarningTextList.size()));
            if (index == mWarningTextList.size()) {
                index = 0;
            }
            startFlipping();
        }
    };

    //开启信息轮播
    public void startFlipping() {
        if (mWarningTextList.size() > 1) {
            handler.removeCallbacks(runnable);
            isFlipping = true;
            handler.postDelayed(runnable, 3000);
        }
    }

    //关闭信息轮播
    public void stopFlipping() {
        if (mWarningTextList.size() > 1) {
            isFlipping = false;
            handler.removeCallbacks(runnable);
        }
    }

    //设置数据
    private void setData() {
        mWarningTextList.add("XXXXXX绑定设备中");
        mWarningTextList.add("XXXXXX绑定设备成功");
        mWarningTextList.add("XXXXXX绑定设备失败");
        mWarningTextList.add("XXXXXX解绑设备成功");
        mWarningTextList.add("XXXXXX解绑设备失败");
        if (mWarningTextList.size() == 1) {
            dataBinding.textSwitcher.setText(mWarningTextList.get(0));
            index = 0;
        }
        if (mWarningTextList.size() > 1) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dataBinding.textSwitcher.setText(mWarningTextList.get(0));
                    index = 0;
                }
            }, 100);
            dataBinding.textSwitcher.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_bottom));
            dataBinding.textSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_top));
            startFlipping();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startFlipping();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopFlipping();
    }
}
