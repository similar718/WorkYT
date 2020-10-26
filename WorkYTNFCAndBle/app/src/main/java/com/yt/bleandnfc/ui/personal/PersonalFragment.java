package com.yt.bleandnfc.ui.personal;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yt.base.mvvm.model.IBaseModelListener1;
import com.yt.base.mvvm.model.PagingResult;
import com.yt.bleandnfc.R;
import com.yt.bleandnfc.api.model.PersonalModel;
import com.yt.bleandnfc.base.fragment.YTBaseFragment;
import com.yt.bleandnfc.constant.Constants;
import com.yt.bleandnfc.databinding.FragmentPersonalBinding;
import com.yt.bleandnfc.mvvm.model.PersonalDataModel;
import com.yt.bleandnfc.ui.adapter.PersonalItemAdapter;
import com.yt.bleandnfc.ui.view.CommonTitleBarView;
import com.yt.bleandnfc.utils.TimeUtil;


import java.util.ArrayList;
import java.util.List;

public class PersonalFragment extends YTBaseFragment<PersonalViewModel, FragmentPersonalBinding> implements IBaseModelListener1<List<PersonalModel>> {

    PersonalItemAdapter mAdapter;
    PersonalDataModel mDataModel;

    @Override
    public void onLazyLoad() {

    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_personal;
    }

    @Override
    protected PersonalViewModel createViewModel() {
        viewModel = new PersonalViewModel();
        viewModel.setIView(this);
        return viewModel;
    }

    @Override
    public void initData() {
        dataBinding.tvTime.setText(TimeUtil.getTodayTimeYMD());

        // RecycleView
        mAdapter = new PersonalItemAdapter(getActivity());
        dataBinding.rvList.setHasFixedSize(true);
        dataBinding.rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        dataBinding.rvList.setAdapter(mAdapter);

        mDataModel = new PersonalDataModel(this);
        mDataModel.refresh();

        dataBinding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mDataModel.refresh();
            }
        });
        dataBinding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mDataModel.loadNextPage();
            }
        });

        initClick();
    }

    private void initClick(){
        // 返回
        dataBinding.titleView.setTitleLeftClick(new CommonTitleBarView.OnTitleLeftClick() {
            @Override
            public void onLeftClick() {
                // 返回到主界面
                Navigation.findNavController(dataBinding.titleView).navigateUp();
            }
        });
    }


    private List<PersonalModel> viewModels = new ArrayList<>();

    @Override
    public void onLoadSuccess(List<PersonalModel> baseCustomViewModels, PagingResult... results) {
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
}
