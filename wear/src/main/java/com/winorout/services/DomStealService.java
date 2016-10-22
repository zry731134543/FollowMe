package com.winorout.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Wearable;
import com.winorout.util.VibratorUtil;

/**
 * Created by wangchaohu on 2016/10/21.
 */

public class DomStealService extends Service implements MobvoiApiClient.ConnectionCallbacks, MobvoiApiClient.OnConnectionFailedListener, MessageApi.MessageListener{

    private final IBinder binder = new DomStealBinder();
    private MobvoiApiClient mobvoiApiClient;

    //变量以及常量
    private long[] time = new long[]{1000, 2000, 1000, 2000, 1000, 2000};   //静止时长，震动时长
    private int SUCCESS = 1, FAIL = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("wch", "onCreate");

        mobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mobvoiApiClient.connect();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("wch", "onBind");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("wch", "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d("wch", "onDestroy");
        super.onDestroy();
    }

    public void excute(){
        Log.d("wch", "excute");
    }

    public class DomStealBinder extends Binder{

        public DomStealService getService(){
            return DomStealService.this;
        }
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
                            VibratorUtil.Vibrate(DomStealService.this, time, true);
                        } else {
                            Log.d("wch", "Success");
                            VibratorUtil.Vibrate(DomStealService.this, true);
                        }
                    }
                }
        );
    }
}
