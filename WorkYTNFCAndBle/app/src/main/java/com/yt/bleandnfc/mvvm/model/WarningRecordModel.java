package com.yt.bleandnfc.mvvm.model;

import com.yt.base.mvvm.model.IBaseModelListener;
import com.yt.base.mvvm.model.PagingResult;
import com.yt.bleandnfc.mvvm.viewmodel.WarningRecordItemViewModel;

import java.util.ArrayList;
import java.util.List;

public class WarningRecordModel {
    private IBaseModelListener<List<WarningRecordItemViewModel>> mListener;
    private int mPage = 1;

    public WarningRecordModel(IBaseModelListener listener){
        mListener = listener;
    }

    public void refresh(){
        mPage = 1;
        loadNextPage();
    }

    public void loadNextPage() {
        List<WarningRecordItemViewModel> list = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            WarningRecordItemViewModel viewModel = new WarningRecordItemViewModel("领用报警",i+".使用工作梯（编号201520）发生违法行为...","2020-09-28 11:35");
            list.add(viewModel);
        }
        mListener.onLoadSuccess(list, new PagingResult(mPage == 1, list.isEmpty(), list.size() >= 10));
        mPage ++;

//        TecentNetworkApi.getService(NewsApiInterface.class)
//                .getNewsList(mChannelId,
//                        mChannelName, String.valueOf(mPage))
//                .compose(TecentNetworkApi.getInstance().applySchedulers(new BaseObserver<NewsListBean>() {
//                    @Override
//                    public void onSuccess(NewsListBean newsChannelsBean) {
//                        List<BaseCustomViewModel> viewModels = new ArrayList<>();
//                        for(NewsListBean.Contentlist contentlist:newsChannelsBean.showapiResBody.pagebean.contentlist){
//                            if(contentlist.imageurls != null && contentlist.imageurls.size() > 0){
//                                PictureTitleViewModel pictureTitleViewModel = new PictureTitleViewModel();
//                                pictureTitleViewModel.pictureUrl = contentlist.imageurls.get(0).url;
//                                pictureTitleViewModel.jumpUri = contentlist.link;
//                                pictureTitleViewModel.title = contentlist.title;
//                                viewModels.add(pictureTitleViewModel);
//                            } else {
//                                TitleViewModel titleViewModel = new TitleViewModel();
//                                titleViewModel.jumpUri = contentlist.link;
//                                titleViewModel.title = contentlist.title;
//                                viewModels.add(titleViewModel);
//                            }
//                        }
//                        mListener.onLoadSuccess(viewModels, new PagingResult(mPage == 1, viewModels.isEmpty(), viewModels.size() >= 10));
//                        mPage ++;
//                    }
//
//                    @Override
//                    public void onFailure(Throwable e) {
//                        e.printStackTrace();
//                    }
//                }));
    }
}
