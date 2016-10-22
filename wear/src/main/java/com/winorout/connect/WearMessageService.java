package com.winorout.connect;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.Wearable;
import com.winorout.interfaces.OnStepChange;


/**
 *手表端发送消息的服务
 * @author  ryzhang
 * @data 2016/10/20 17:35
 */
public class WearMessageService extends Service{
    private static final String TAG="ryzhang";
    private MobvoiApiClient mMobvoiApiClient;
    private OnStepChange onStepChange;
    private boolean isConnect=false;
    private static int i=0;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        init();
        mMobvoiApiClient.connect();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isConnect){
            Log.d(TAG, "发送消息"+(++i));
            onStepChange.getStep(i+"");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void init(){
        mMobvoiApiClient = new MobvoiApiClient.Builder(getApplication())
                .addApi(Wearable.API)
                .addConnectionCallbacks(new MobvoiApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "连接成功：");
                        isConnect=true;
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
        onStepChange= new OnStepChange(){
            @Override
            public void getStep(String step) {
                sendMessage(step);
            }
        };
    }

    /**
     * 发送消息
     * @param content
     */
    private void sendMessage(String content){
        Wearable.MessageApi.sendMessage(
                mMobvoiApiClient, "", "/step_count", content.getBytes()).setResultCallback(
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
