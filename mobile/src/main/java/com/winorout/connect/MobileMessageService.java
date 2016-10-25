package com.winorout.connect;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Wearable;
import com.winorout.followme.sports.DateTimeData;
import com.winorout.followme.sports.PedometerDB;
import com.winorout.followme.sports.StepListencer;
import com.winorout.tools.Logg;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 手表端接收消息的Service
 * @author  ryzhang
 * @data 2016/10/20 16:34
 */

public class MobileMessageService extends Service implements MobvoiApiClient.ConnectionCallbacks
        ,MessageApi.MessageListener,MobvoiApiClient.OnConnectionFailedListener{
    private MobvoiApiClient mMobvoiApiClient;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        mMobvoiApiClient = new MobvoiApiClient.Builder(MobileMessageService.this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mMobvoiApiClient.connect();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Wearable.MessageApi.removeListener(mMobvoiApiClient, this);
        mMobvoiApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mMobvoiApiClient, this);
        Logg.d("连接成功");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logg.d("连接中断");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Logg.d("连接失败");
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Logg.d("onMessageReceived");
        new MessageEventFilter(messageEvent,this).sendMessage();
    }

}
