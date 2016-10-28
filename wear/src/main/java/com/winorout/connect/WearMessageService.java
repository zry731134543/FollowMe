package com.winorout.connect;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.Wearable;
import com.winorout.entity.SportData;
import com.winorout.followme.common.bean.BatteryInfo;
import com.winorout.followme.common.bean.DeviceInfo;
import com.winorout.followme.common.utils.TransformBytesAndObject;
import com.winorout.interfaces.OnStepChange;
import com.winorout.presenter.SensorPresenter;


/**
 * 手表端发送消息的服务
 *
 * @author ryzhang
 * @data 2016/10/20 17:35
 */
public class WearMessageService extends Service implements MobvoiApiClient.ConnectionCallbacks
        , MobvoiApiClient.OnConnectionFailedListener {
    private static final String TAG = "ryzhang";
    private final String SPORT_PATH = "/sport";
    private final String BARRAGE_PATH = "/barrage";
    private MobvoiApiClient mMobvoiApiClient;
    private SensorPresenter sensorPresenter;
    private static SportData msportData;
    private IntentFilter filter = new IntentFilter("com.aa.START");
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            sendMessageToPhone("wear", BARRAGE_PATH, message);
        }
    };

    //获取电池状态相关
    private BroadcastReceiver batteryLevelRcvr;
    private IntentFilter batteryLevelFilter;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        initVariables();
        Log.d(TAG, "WearMessageService.onCreate");
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

    private void initVariables() {
        mMobvoiApiClient = new MobvoiApiClient.Builder(WearMessageService.this)//跨进程通信
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        sensorPresenter = new SensorPresenter(this);//监听步数变化
        sensorPresenter.setOnStepChange(new OnStepChange() {
            @Override
            public void getStep(SportData sportData) {
                msportData = sportData;
                sendMessageToPhone("wear", SPORT_PATH, msportData.toJSON());
            }
        });
        registerReceiver(broadcastReceiver, filter);
        mMobvoiApiClient.connect();
        sensorPresenter.registerListener();
        getBatteryState();
    }

    /**
     * 获取设备信息
     */
    private void getDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo(Build.MODEL, Build.VERSION.RELEASE);
        byte[] data = TransformBytesAndObject.ObjectToByte(deviceInfo);
        sendMessageToPhone("wear", "DEVICE_INFO", data);
        Log.e(TAG, "设备信息: "
                + "\n\t设备名称：" + deviceInfo.getDeviceName()
                + "\n\t安卓版本：" + deviceInfo.getAndroidVersion());
    }

    /**
     * 获取设备电池状态
     */
    private void getBatteryState() {
        batteryLevelRcvr = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int batteryLevel;       //当前电量
                String batteryStatus;   //使用状态
                String hatteryHealth;    //健康状况

                String action = intent.getAction();
                /*
                 * 如果捕捉到的action是ACTION_BATTERY_CHANGED， 就运行onBatteryInfoReceiver()
                 */
                if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                    batteryLevel = intent.getIntExtra("level", 0);    //目前电量
                    switch (intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN)) {
                        case BatteryManager.BATTERY_STATUS_CHARGING:
                            batteryStatus = "充电状态";
                            break;
                        case BatteryManager.BATTERY_STATUS_DISCHARGING:
                            batteryStatus = "放电状态";
                            break;
                        case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                            batteryStatus = "未充电";
                            break;
                        case BatteryManager.BATTERY_STATUS_FULL:
                            batteryStatus = "充满电";
                            break;
                        case BatteryManager.BATTERY_STATUS_UNKNOWN:
                            batteryStatus = "未知道状态";
                            break;
                        default:
                            batteryStatus = "获取失败";
                            break;
                    }

                    switch (intent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
                        case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                            hatteryHealth = "未知错误";
                            break;
                        case BatteryManager.BATTERY_HEALTH_GOOD:
                            hatteryHealth = "状态良好";
                            break;
                        case BatteryManager.BATTERY_HEALTH_DEAD:
                            hatteryHealth = "电池没有电";
                            break;
                        case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                            hatteryHealth = "电池电压过高";
                            break;
                        case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                            hatteryHealth = "电池过热";
                            break;
                        default:
                            hatteryHealth = "获取失败";
                            break;
                    }
                    BatteryInfo batteryInfo = new BatteryInfo(batteryLevel, batteryStatus, hatteryHealth);
                    byte[] data = TransformBytesAndObject.ObjectToByte(batteryInfo);
                    sendMessageToPhone("wear", "BATTERY_INFO", data);
                    Log.e(TAG, "电池状态: "
                            + "\n\t当前电量:" + batteryInfo.getBatteryLevel()
                            + "\n\t使用状态:" + batteryInfo.getBatteryStatus()
                            + "\n\t健康状况:" + batteryInfo.getBatteryHealth());
                    //获取设备信息
                    getDeviceInfo();
                }
            }
        };
        //注册广播监听系统电池状态
        batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelRcvr, batteryLevelFilter);
    }

    /**
     * 发送消息到手机
     *
     * @param nodeId nodeID
     * @param path   发送路径
     * @param data   发送的数据
     */
    private void sendMessageToPhone(String nodeId, String path, String data) {
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

    private void sendMessageToPhone(String nodeId, String path, byte[] data) {
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
