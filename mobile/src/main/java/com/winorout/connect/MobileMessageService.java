package com.winorout.connect;


import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Wearable;
import com.winorout.followme.common.bean.BatteryInfo;
import com.winorout.followme.common.bean.DeviceInfo;
import com.winorout.followme.common.utils.TransformBytesAndObject;
import com.winorout.followme.personalCenter.DeviceInfoActivity;
import com.winorout.tools.Logg;

/**
 * 手表端接收消息的Service
 * @author  ryzhang
 * @data 2016/10/20 16:34
 */

public class MobileMessageService extends Service implements MobvoiApiClient.ConnectionCallbacks
        ,MessageApi.MessageListener,MobvoiApiClient.OnConnectionFailedListener{

    private static final String TAG = "MobileMessageService";
    public static final int SEND_DEVICE_INFO_BROADCAST = 0x187;
    public static final int SEND_BATTERY_INFO_BROADCAST = 0x188;
    private MobvoiApiClient mMobvoiApiClient;
    private DeviceInfo mDeviceInfo;         //设备信息
    private BatteryInfo mBatteryInfo;       //电池信息
    private Handler mHandler;

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
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SEND_DEVICE_INFO_BROADCAST:
                        //通过广播将设备信息数据发送到我的设备界面
                        Intent mDeviceInfoIntent = new Intent();
                        mDeviceInfoIntent.setAction(DeviceInfoActivity.ACTION_DEVICE_INFO_CHANGED);
                        Bundle mDeviceInfoBundle = new Bundle();
                        mDeviceInfoBundle.putSerializable("DeviceInfo", mDeviceInfo);
                        mDeviceInfoIntent.putExtras(mDeviceInfoBundle);
                        sendBroadcast(mDeviceInfoIntent);
                        Log.e(TAG, "onMessageReceived: 发送 " + mDeviceInfo.getDeviceName());
                        break;
                    case SEND_BATTERY_INFO_BROADCAST:
                        //通过广播将电池信息数据发送到我的设备界面
                        Intent mBatteryInfoIntent = new Intent();
                        mBatteryInfoIntent.setAction(DeviceInfoActivity.ACTION_BATTERY_INFO_CHANGED);
                        Bundle mBatteryInfoBundle = new Bundle();
                        mBatteryInfoBundle.putSerializable("BatteryInfo", mBatteryInfo);
                        mBatteryInfoIntent.putExtras(mBatteryInfoBundle);
                        sendBroadcast(mBatteryInfoIntent);
                        Log.e(TAG, "onMessageReceived: 发送 " + mBatteryInfo.getBatteryHealth());
                        break;
                }
            }
        };
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

    //监听手表发过来的信息
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        new MessageEventFilter(messageEvent,this).sendMessage();
        switch (messageEvent.getPath()) {
            case "BATTERY_INFO":
                //获取电池信息
                mBatteryInfo = (BatteryInfo) TransformBytesAndObject.ByteToObject(messageEvent.getData());
                Log.e(TAG, "电池状态: "
                        + "\n\t当前电量:" + mBatteryInfo.getBatteryLevel()
                        + "\n\t使用状态:" + mBatteryInfo.getBatteryStatus()
                        + "\n\t健康状况:" + mBatteryInfo.getBatteryHealth());
                //通知发送广播
                Message msg1 = new Message();
                msg1.what = SEND_BATTERY_INFO_BROADCAST;
                mHandler.sendMessage(msg1);
                Log.e(TAG, "onMessageReceived: 发送通知1");
                break;
            case "DEVICE_INFO":
                //获取设备信息
                mDeviceInfo = (DeviceInfo) TransformBytesAndObject.ByteToObject(messageEvent.getData());
                Log.e(TAG, "设备信息: "
                        + "\n\t设备名称：" + mDeviceInfo.getDeviceName()
                        + "\n\t安卓版本：" + mDeviceInfo.getAndroidVersion());
                //通知发送广播
                Message msg2 = new Message();
                msg2.what = SEND_DEVICE_INFO_BROADCAST;
                mHandler.sendMessage(msg2);
                Log.e(TAG, "onMessageReceived: 发送通知2");
                break;
        }
    }

    /**
     * 发送消息到手表
     *
     * @param nodeId nodeID
     * @param path   发送路径
     * @param data   发送的数据
     */
    private void sendMessageToWear(String nodeId, String path, String data) {
        Log.d("ryzhang", "sendMessage:" + data);
        Wearable.MessageApi.sendMessage(
                mMobvoiApiClient, "", path, data.getBytes()).setResultCallback(
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

    private void sendMessageToWear(String nodeId, String path, byte[] data) {
        Log.d("ryzhang", "sendMessage:" + data);
        Wearable.MessageApi.sendMessage(
                mMobvoiApiClient, "", path, data).setResultCallback(
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
