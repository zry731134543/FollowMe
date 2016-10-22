package com.winorout.connect;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
//    private static int i=0;
    private static final Uri STEP_URI = Uri.parse("content://com.mobvoi.ticwear.steps");
    private ContentResolver mResolver;
    private int mSteps;
    private ContentObserver mObserver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        init();
        mMobvoiApiClient.connect();
        mResolver.registerContentObserver(STEP_URI, true, mObserver);
        mSteps = fetchSteps();
        Log.d("ryzhang","当前步数"+mSteps);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mResolver.unregisterContentObserver(mObserver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void init(){
        onStepChange= new OnStepChange(){
            @Override
            public void getStep(String step) {
                sendMessage(step);
            }
        };
        mResolver = this.getContentResolver();
        mObserver = new ContentObserver(null) {
            @Override
            public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                mSteps = fetchSteps();
                Log.d("ryzhang","当前步数"+mSteps);
                onStepChange.getStep(mSteps+"");
            }
        };
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

    private int fetchSteps() {
        int steps = 0;
        Cursor cursor = mResolver.query(STEP_URI, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    steps = cursor.getInt(0);
                }
            } finally {
                cursor.close();
            }
        }
        return steps;
    }

}
