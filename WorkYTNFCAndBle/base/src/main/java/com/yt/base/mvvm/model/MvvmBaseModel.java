package com.yt.base.mvvm.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.yt.base.preference.BasicDataPreferenceUtil;
import com.yt.base.utils.GenericUtils;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.CallSuper;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class MvvmBaseModel<F, T> implements MvvmDataObserver<F> {
    private CompositeDisposable compositeDisposable;
    protected WeakReference<IBaseModelListener> mReferenceIBaseModelListener;
    private BaseCachedData<F> mData;
    protected int pageNumber = 0;
    private String mCachedPreferenceKey;
    private String mApkPredefinedData;
    private boolean mIsPaging;
    private final int INIT_PAGE_NUMBER;
    private boolean mIsLoading;

    public MvvmBaseModel(boolean isPaging, String cachePreferenceKey, String apkPredefinedData, int... initPageNumber) {
        this.mIsPaging = isPaging;
        this.INIT_PAGE_NUMBER = (initPageNumber != null && initPageNumber.length > 0) ? initPageNumber[0] : 0;
        if (initPageNumber != null && initPageNumber.length > 0) {
            this.pageNumber = initPageNumber[0];
        }
        this.mCachedPreferenceKey = cachePreferenceKey;
        this.mApkPredefinedData = apkPredefinedData;
        if (mCachedPreferenceKey != null) {
            mData = new BaseCachedData<F>();
        }
    }

    public boolean isPaging() {
        return mIsPaging;
    }

    /**
     * 注册监听
     *
     * @param listener
     */
    public void register(IBaseModelListener listener) {
        if (listener != null) {
            mReferenceIBaseModelListener = new WeakReference<>(listener);
        }
    }

    /**
     * 由于渠道处在App的首页，为了保证app打开的时候由于网络慢或者异常的情况下tablayout不为空，
     * 所以app对渠道数据进行了预制；
     * 加载完成以后会立即进行网络请求，同时缓存在本地，今后app打开都会从preference读取，而不在读取
     * apk预制数据，由于渠道数据变化没那么快，在app第二次打开的时候会生效，并且是一天请求一次。
     */
    protected void saveDataToPreference(F data) {
        if (data != null) {
            mData.data = data;
            mData.updateTimeInMills = System.currentTimeMillis();
            BasicDataPreferenceUtil.getInstance().setString(mCachedPreferenceKey, new Gson().toJson(mData));
        }
    }

    public void refresh() {
        if (!mIsLoading) {
            if (isPaging()) {
                pageNumber = INIT_PAGE_NUMBER;
            }
            mIsLoading = true;
            load();
        }
    }

    public void loadNextPage() {
        if (!mIsLoading) {
            mIsLoading = true;
            load();
        }
    }

    public abstract void load();

    /**
     * 是否更新数据，可以在这里设计策略，可以是一天一次，一月一次等等，
     * 默认是每次请求都更新
     */
    protected boolean isNeedToUpdate() {
        return true;
    }

    @CallSuper
    public void cancel() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }


    public void addDisposable(Disposable d) {
        if (d == null) {
            return;
        }

        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }

        compositeDisposable.add(d);
    }

    public void getCachedDataAndLoad() {
        mIsLoading = true;
        if (mCachedPreferenceKey != null) {
            String saveDataString = BasicDataPreferenceUtil.getInstance().getString(mCachedPreferenceKey);
            if (!TextUtils.isEmpty(saveDataString)) {
                try {
                    F savedData = new Gson().fromJson(new JSONObject(saveDataString).getString("data"), (Class<F>) GenericUtils.getGenericType(MvvmBaseModel.this));
                    if (savedData != null && savedData != null) {
                        onSuccess(savedData, true);
                        if (isNeedToUpdate()) {
                            load();
                        }
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (mApkPredefinedData != null) {
                try {
                    F savedData = new Gson().fromJson(mApkPredefinedData,  (Class<F>) GenericUtils.getGenericType(MvvmBaseModel.this));
                    if (savedData != null && savedData != null) {
                        onSuccess(savedData, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        load();
    }

    /**
     * 发消息给UI线程
     */
    protected void notifyResultToListeners(F networkResonseBean, T data, boolean isFromCache) {
        IBaseModelListener listenerItem = mReferenceIBaseModelListener.get();
        if (listenerItem != null) {
            if (isPaging()) {
                    listenerItem.onLoadFinish(this, data,
                            isFromCache ? new PagingResult(false, true, true) :
                                new PagingResult(data == null ? true : ((List) data).isEmpty(), pageNumber == INIT_PAGE_NUMBER, data == null ? false : ((List) data).size() > 0));
            } else {
                listenerItem.onLoadFinish(this, data);
            }
        }

        if (isPaging()) {
            if (mCachedPreferenceKey != null && pageNumber == INIT_PAGE_NUMBER && !isFromCache) {
                saveDataToPreference(networkResonseBean);
            }
            if (!isFromCache) {
                if(data != null && data instanceof List && ((List) data).size() > 0){
                    pageNumber++;
                }
            }
        } else {
            if (mCachedPreferenceKey != null && !isFromCache) {
                saveDataToPreference(networkResonseBean);
            }
        }
        if(!isFromCache) {
            mIsLoading = false;
        }
    }

    protected void loadFail(final String errorMessage) {
        IBaseModelListener listenerItem = mReferenceIBaseModelListener.get();
        if (listenerItem != null) {
            if (isPaging()) {
                listenerItem.onLoadFail(this, errorMessage, new PagingResult(true, pageNumber == INIT_PAGE_NUMBER, false));
            } else {
                listenerItem.onLoadFail(this, errorMessage);
            }
        }
        mIsLoading = false;
    }

    protected boolean isRefresh() {
        return pageNumber == INIT_PAGE_NUMBER;
    }

    public boolean isLoading() {
        return mIsLoading;
    }
}
