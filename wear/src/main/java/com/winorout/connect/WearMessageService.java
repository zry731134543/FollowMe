package com.winorout.connect;

import android.app.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.Wearable;
import com.winorout.interfaces.OnStepChange;
import com.winorout.presenter.SensorPresenter;


/**
 *手表端发送消息的服务
 * @author  ryzhang
 * @data 2016/10/20 17:35
 */
public class WearMessageService extends Service implements MobvoiApiClient.ConnectionCallbacks
    ,MobvoiApiClient.OnConnectionFailedListener{
    private static final String TAG="ryzhang";
    private final String SPORT_PATH="/sport";
    private final String BARRAGE_PATH="/barrage";
    private MobvoiApiClient mMobvoiApiClient;
    private SensorPresenter sensorPresenter;
    private IntentFilter filter=new IntentFilter("com.aa.START");
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message=intent.getStringExtra("message");
            sendMessage(message,BARRAGE_PATH);
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "WearMessageService.onCreate");
        initVariables();
        registerReceiver(broadcastReceiver, filter);
        mMobvoiApiClient.connect();
        sensorPresenter.registerListener();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "WearMessageService.onDestroy");
        sensorPresenter.unregisterListener();
        mMobvoiApiClient.disconnect();
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void initVariables(){
        mMobvoiApiClient = new MobvoiApiClient.Builder(WearMessageService.this)//跨进程通信
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        sensorPresenter=new SensorPresenter(this);//监听步数变化
        sensorPresenter.setOnStepChange(new OnStepChange(){
            @Override
            public void getStep(SportData sportData) {
                sendMessage(sportData.toJSON(),SPORT_PATH);
            }
        });

    }

    /**
     * 发送消息到手机
     * @param content 发送的数据
     * @param path 发送路径
     */
    private void sendMessage(String content,String path){
        Log.d("ryzhang","sendMessage:"+content);
        Wearable.MessageApi.sendMessage(
                mMobvoiApiClient, "", path, content.getBytes()).setResultCallback(
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

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "连接成功");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "连接中断: " + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "连接失败: ");
    }

}
