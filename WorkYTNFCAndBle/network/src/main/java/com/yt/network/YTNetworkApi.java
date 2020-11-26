package com.yt.network;

import com.yt.network.base.NetworkApi;
import com.yt.network.beans.TecentBaseResponse;
import com.yt.network.constant.NetConstants;
import com.yt.network.errorhandler.ExceptionHandle;
import com.yt.network.utils.TecentUtil;

import java.io.IOException;

import io.reactivex.functions.Function;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class YTNetworkApi extends NetworkApi {
    private static volatile YTNetworkApi sInstance;

    public static YTNetworkApi getInstance() {
        if (sInstance == null) {
            synchronized (YTNetworkApi.class) {
                if (sInstance == null) {
                    sInstance = new YTNetworkApi();
                }
            }
        }
        return sInstance;
    }

    public static  <T> T getService(Class<T> service) {
        return getInstance().getRetrofit(service).create(service);
    }

    @Override
    protected Interceptor getInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String timeStr = TecentUtil.getTimeStr();
                Request.Builder builder = chain.request().newBuilder();
                builder.addHeader("Source", "source");
                builder.addHeader("Authorization", TecentUtil.getAuthorization(timeStr));
                builder.addHeader("Date", timeStr);
                return chain.proceed(builder.build());
            }
        };
    }

    protected <T> Function<T, T> getAppErrorHandler() {
        return new Function<T, T>() {
            @Override
            public T apply(T response) throws Exception {
                //response中code码不会0 出现错误
                if (response instanceof TecentBaseResponse && ((TecentBaseResponse) response).showapiResCode != 0) {
                    ExceptionHandle.ServerException exception = new ExceptionHandle.ServerException();
                    exception.code = ((TecentBaseResponse) response).showapiResCode;
                    exception.message = ((TecentBaseResponse) response).showapiResError != null ? ((TecentBaseResponse) response).showapiResError : "";
                    throw exception;
                }
                return response;
            }
        };
    }

    @Override
    public String getFormal() {
        return NetConstants.HOSTIP_DATA;
    }

    @Override
    public String getTest() {
        return NetConstants.HOSTIP_DATA;
    }
}
