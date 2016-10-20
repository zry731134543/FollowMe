package com.winorout.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Wearable;
import com.winorout.interfaces.DomStealViewInterface;
import com.winorout.util.VibratorUtil;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangchaohu on 2016/10/20.
 */

public class DomStealPresenter implements MobvoiApiClient.ConnectionCallbacks, MobvoiApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    private Context mContext;

    private DomStealViewInterface domStealViewInterface;    //防盗界面接口

    private MobvoiApiClient mobvoiApiClient;

    //变量以及常量
    private long[] time = new long[]{100, 2000};   //震动时长，静止时长
    private int SUCCESS = 1, FAIL = -1;

    /**
     * Handler
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FAIL) {
                domStealViewInterface.disconnection();
                VibratorUtil.Vibrate(((Activity) mContext), time, true);
            }
            if (msg.what == SUCCESS) {
                domStealViewInterface.connection();
                VibratorUtil.Vibrate(((Activity) mContext), true);
            }
        }
    };

    /**
     * 构造器
     */
    public DomStealPresenter(Context context, DomStealViewInterface viewInterface) {
        this.mContext = context;
        this.domStealViewInterface = viewInterface;
                loading();
    }

    public void loading() {
        mobvoiApiClient = new MobvoiApiClient.Builder(mContext)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mobvoiApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mobvoiApiClient, this);
        sendMessagetoPhone();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("wch", "onConnectionFailed::" + connectionResult);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("wch", "onMessageReceived" + messageEvent);
    }

    /**
     * 向手机发送message
     * 如果发送成功，说明有连接
     * 如果发送失败，说明连接断开
     */
    private void sendMessagetoPhone() {
        byte[] data = String.valueOf("send").getBytes();

        Wearable.MessageApi.sendMessage(
                mobvoiApiClient, "default", "Steps", data).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.d("wch", "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                            mHandler.sendEmptyMessage(FAIL);
                        } else {
                            Log.d("wch", "Success");
                            mHandler.sendEmptyMessage(SUCCESS);
                        }
                    }
                }
        );
    }
}
