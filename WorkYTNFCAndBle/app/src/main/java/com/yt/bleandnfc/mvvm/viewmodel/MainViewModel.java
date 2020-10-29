package com.yt.bleandnfc.mvvm.viewmodel;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import com.yt.base.view.BaseViewModel;
import com.yt.bleandnfc.MainActivity;
import com.yt.bleandnfc.R;
import com.yt.bleandnfc.api.YTApiInterface;
import com.yt.bleandnfc.api.model.AlarmCountAlarmByStateModel;
import com.yt.bleandnfc.api.model.AlarmFindAlarmByStateModel;
import com.yt.bleandnfc.base.observer.BaseHttpObserver;
import com.yt.bleandnfc.constant.Constants;
import com.yt.bleandnfc.manager.SPManager;
import com.yt.bleandnfc.utils.NetworkUtil;
import com.yt.network.YTNetworkApi;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MainViewModel extends BaseViewModel {

    public MutableLiveData<AlarmCountAlarmByStateModel> mAlarmCountAlarmByStateModel;

    private Activity mContext;

    public MainViewModel(Activity context){
        mContext = context;
        mAlarmCountAlarmByStateModel = new MutableLiveData<>();
    }


    public void alarmList(){
        if (!NetworkUtil.isNetworkConnected()) {
            return;
        }
        YTNetworkApi.getService(YTApiInterface.class)
                .alarmCountAlarmByState(SPManager.getInstance().getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseHttpObserver<AlarmCountAlarmByStateModel>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void getData(AlarmCountAlarmByStateModel data) {
                        mAlarmCountAlarmByStateModel.setValue(data);
                    }

                    @Override
                    public void onErrorInfo(Throwable e) {
                        mAlarmCountAlarmByStateModel.setValue(null);
                    }
                });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getNotification() {
        //1.获取通知管理器
        NotificationManager manager=(NotificationManager)mContext.getSystemService(NOTIFICATION_SERVICE);
        //2.创建通知  8.0以后需要自建通知通道
        Notification notification = null;
        //创建通道
        String id="mchannel";//通道id
        String name="通道1";//通道名称
        //判断安卓版本 如果大于8.0则使用通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel= new NotificationChannel(id,name,NotificationManager.IMPORTANCE_HIGH);
            //创建通道
            manager.createNotificationChannel(channel);
            //创建通知
            notification = new Notification.Builder(mContext.getApplicationContext(),id)
                    //setLargeIcon 设置大图标
                    //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.wolf))
                    //setSmallIcon 设置小图标
                    .setSmallIcon(R.mipmap.ic_launcher_logo)
                    //setContentText 设置内容
                    .setContentText("有新报警信息")
                    //setStyle 设置样式
                    //.setStyle(new Notification.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.wolf)))
                    //setContentTitle 设置标题
                    //.setContentTitle("好消息")
                    //setAutoCancel点击过后取消显示
                    .setAutoCancel(true)
                    //帮对应的Activity
                    .setContentIntent(PendingIntent.getActivity(mContext.getApplicationContext(),
                            1,new Intent(mContext, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT)
                    ).build();
        }else{
            //安卓4.0-8.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                notification =new Notification.Builder(mContext.getApplicationContext()
                )//setSmallIcon 设置小图标
                        .setSmallIcon(R.mipmap.ic_launcher_logo)
                        //setContentText 设置内容
                        .setContentText("有新报警信息")
                        //setContentTitle 设置标题
                        //.setContentTitle("好消息")
                        //setAutoCancel点击过后取消显示
                        .setAutoCancel(true)
                        .setContentIntent(
                                PendingIntent.getActivity(
                                        mContext.getApplicationContext(),1,new
                                                Intent(
                                                mContext,
                                                MainActivity.class),
                                        PendingIntent.FLAG_CANCEL_CURRENT
                                )
                        ).build();
            }
        }
        //4.发出通知
        manager.notify(0,notification);
    }
}
