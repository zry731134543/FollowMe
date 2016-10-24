package com.winorout.connect;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.Wearable;
import com.winorout.interfaces.OnMessgaeChange;
import com.winorout.interfaces.OnStepChange;
import com.winorout.presenter.SensorPresenter;
import com.winorout.services.MyReceiver;


/**
 *手表端发送消息的服务
 * @author  ryzhang
 * @data 2016/10/20 17:35
 */
public class WearMessageService extends Service{
    private static final String TAG="ryzhang";
    private MobvoiApiClient mMobvoiApiClient;
    private SensorPresenter sensorPresenter;
    private final String stepPath="/step_count";
    private final String messagePath="/message";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        init();
        sensorPresenter.registerListener();
        mMobvoiApiClient.connect();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        sensorPresenter.unregisterListener();
        super.onDestroy();
    }

    private void init(){
        sensorPresenter=new SensorPresenter(this);
        sensorPresenter.setOnStepChange(new OnStepChange(){
            @Override
            public void getStep(int step) {
                sendMessage(step+"",stepPath);
            }
        });
        new MyReceiver().setOnMessageChang(new OnMessgaeChange() {
            @Override
            public void receiveMessage(String message) {
                sendMessage(message,messagePath);
            }
        });
        mMobvoiApiClient = new MobvoiApiClient.Builder(getApplication())
                .addApi(Wearable.API)
                .addConnectionCallbacks(new MobvoiApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        int step=sensorPresenter.fetchSteps();
                        sendMessage(step+"",stepPath);
                        Log.d(TAG, "连接成功---当前步数："+step);
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "连接中断: " + cause);
                    }
                }) .addOnConnectionFailedListener(new MobvoiApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "连接失败: ");
                    }
                })
                .build();
    }

    /**
     * 发送消息到手机
     * @param content 发送的数据
     * @param type 发送类别
     */
    private void sendMessage(String content,String type){
        Wearable.MessageApi.sendMessage(
                mMobvoiApiClient, "", type, content.getBytes()).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.d(TAG, "发送消息失败，返回码: " + sendMessageResult.getStatus().getStatusCode());
                        } else {
                            Log.d(TAG, "发送消息成功");
                        }
                    }
                }
        );
    }


}
